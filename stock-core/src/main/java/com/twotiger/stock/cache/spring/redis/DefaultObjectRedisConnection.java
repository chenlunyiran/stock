package com.twotiger.stock.cache.spring.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.*;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.convert.Converters;
import org.springframework.data.redis.connection.convert.ListConverter;
import org.springframework.data.redis.connection.convert.MapConverter;
import org.springframework.data.redis.connection.convert.SetConverter;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DefaultObjectRedisConnection implements ObjectRedisConnection, DecoratedRedisConnection {

    private static final byte[][] EMPTY_2D_BYTE_ARRAY = new byte[0][];

    private final Log log = LogFactory.getLog(DefaultObjectRedisConnection.class);
    private final RedisConnection delegate;
    private final RedisSerializer<String> keySerializer;
    private final RedisSerializer<Object> valueSerializer;
    private Converter<byte[], Object> bytesToObject = new DefaultObjectRedisConnection.DeserializingConverter();
    private Converter<byte[], String> bytesToString = new DefaultObjectRedisConnection.StringConverter();
    private SetConverter<RedisZSetCommands.Tuple, ObjectRedisConnection.ObjectTuple> tupleToObjectTuple = new SetConverter<>(new DefaultObjectRedisConnection.TupleConverter());
    private SetConverter<ObjectRedisConnection.ObjectTuple, RedisZSetCommands.Tuple> ObjectTupleToTuple = new SetConverter<>(new DefaultObjectRedisConnection.ObjectTupleConverter());
    private ListConverter<byte[], Object> byteListToObjectList = new ListConverter<>(bytesToObject);
    private MapConverter<byte[], Object> byteMapToObjectMap = new MapConverter<>(bytesToObject);
    private SetConverter<byte[], Object> byteSetToObjectSet = new SetConverter<>(bytesToObject);
    private SetConverter<byte[], String> byteSetToStringSet = new SetConverter<>(bytesToString);
    private Converter<GeoResults<RedisGeoCommands.GeoLocation<byte[]>>, GeoResults<RedisGeoCommands.GeoLocation<String>>> byteGeoResultsToStringGeoResults;

    @SuppressWarnings("rawtypes")
    private Queue<Converter> pipelineConverters = new LinkedList<>();
    @SuppressWarnings("rawtypes")
    private Queue<Converter> txConverters = new LinkedList<>();
    private boolean deserializePipelineAndTxResults = false;
    private DefaultObjectRedisConnection.IdentityConverter<Object, ?> identityConverter = new DefaultObjectRedisConnection.IdentityConverter<>();

    private class DeserializingConverter implements Converter<byte[], Object> {
        public Object convert(byte[] source) {
            return valueSerializer.deserialize(source);
        }
    }

    private class StringConverter implements Converter<byte[], String> {
        public String convert(byte[] source) {
            return keySerializer.deserialize(source);
        }
    }

    private class TupleConverter implements Converter<RedisZSetCommands.Tuple, ObjectRedisConnection.ObjectTuple> {
        public ObjectRedisConnection.ObjectTuple convert(RedisZSetCommands.Tuple source) {
            return new DefaultObjectTuple(source, valueSerializer.deserialize(source.getValue()));
        }
    }

    private class ObjectTupleConverter implements Converter<ObjectRedisConnection.ObjectTuple, RedisZSetCommands.Tuple> {
        public RedisZSetCommands.Tuple convert(ObjectRedisConnection.ObjectTuple source) {
            return new DefaultTuple(source.getValue(), source.getScore());
        }
    }

    private class IdentityConverter<S, T> implements Converter<S, T> {
        public Object convert(Object source) {
            return source;
        }
    }

    @SuppressWarnings("rawtypes")
    private class TransactionResultConverter implements Converter<List<Object>, List<Object>> {
        private Queue<Converter> txConverters;

        public TransactionResultConverter(Queue<Converter> txConverters) {
            this.txConverters = txConverters;
        }

        public List<Object> convert(List<Object> execResults) {
            return convertResults(execResults, txConverters);
        }
    }

    /**
     * Constructs a new <code>DefaultObjectRedisConnection</code> instance. Uses {@link StringRedisSerializer} as
     * underlying serializer.
     *
     * @param connection Redis connection
     */
    public DefaultObjectRedisConnection(RedisConnection connection) {
        this(connection, new StringRedisSerializer(), new JdkSerializationRedisSerializer());
    }

    /**
     * Constructs a new <code>DefaultObjectRedisConnection</code> instance.
     *
     * @param connection Redis connection
     * @param keySerializer String serializer
     * @param valueSerializer Object serializer
     */
    public DefaultObjectRedisConnection(RedisConnection connection, RedisSerializer<String> keySerializer, RedisSerializer<Object> valueSerializer) {

        Assert.notNull(connection, "connection is required");
        Assert.notNull(keySerializer, "keySerializer is required");
        Assert.notNull(valueSerializer, "valueSerializer is required");

        this.delegate = connection;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.byteGeoResultsToStringGeoResults = Converters.deserializingGeoResultsConverter(keySerializer);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#append(byte[], byte[])
     */
    @Override
    public Long append(byte[] key, byte[] value) {
        return convertAndReturn(delegate.append(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#bgSave()
     */
    @Override
    public void bgSave() {
        delegate.bgSave();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#bgReWriteAof()
     */
    @Override
    public void bgReWriteAof() {
        delegate.bgReWriteAof();
    }

    /**
     * @deprecated As of 1.3, use {@link #bgReWriteAof}.
     */
    @Deprecated
    public void bgWriteAof() {
        bgReWriteAof();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#bLPop(int, byte[][])
     */
    @Override
    public List<byte[]> bLPop(int timeout, byte[]... keys) {
        return convertAndReturn(delegate.bLPop(timeout, keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#bRPop(int, byte[][])
     */
    @Override
    public List<byte[]> bRPop(int timeout, byte[]... keys) {
        return convertAndReturn(delegate.bRPop(timeout, keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#bRPopLPush(int, byte[], byte[])
     */
    @Override
    public byte[] bRPopLPush(int timeout, byte[] srcKey, byte[] dstKey) {
        return convertAndReturn(delegate.bRPopLPush(timeout, srcKey, dstKey), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnection#close()
     */
    @Override
    public void close() throws RedisSystemException {
        delegate.close();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#dbSize()
     */
    @Override
    public Long dbSize() {
        return convertAndReturn(delegate.dbSize(), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#decr(byte[])
     */
    @Override
    public Long decr(byte[] key) {
        return convertAndReturn(delegate.decr(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#decrBy(byte[], long)
     */
    @Override
    public Long decrBy(byte[] key, long value) {
        return convertAndReturn(delegate.decrBy(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#del(byte[][])
     */
    @Override
    public Long del(byte[]... keys) {
        return convertAndReturn(delegate.del(keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisTxCommands#discard()
     */
    @Override
    public void discard() {
        try {
            delegate.discard();
        } finally {
            txConverters.clear();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnectionCommands#echo(byte[])
     */
    @Override
    public byte[] echo(byte[] message) {
        return convertAndReturn(delegate.echo(message), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisTxCommands#exec()
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List<Object> exec() {

        try {
            List<Object> results = delegate.exec();
            if (isPipelined()) {
                pipelineConverters.add(new DefaultObjectRedisConnection.TransactionResultConverter(new LinkedList<>(txConverters)));
                return results;
            }
            return convertResults(results, txConverters);
        } finally {
            txConverters.clear();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#exists(byte[])
     */
    @Override
    public Boolean exists(byte[] key) {
        return convertAndReturn(delegate.exists(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#expire(byte[], long)
     */
    @Override
    public Boolean expire(byte[] key, long seconds) {
        return convertAndReturn(delegate.expire(key, seconds), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#expireAt(byte[], long)
     */
    @Override
    public Boolean expireAt(byte[] key, long unixTime) {
        return convertAndReturn(delegate.expireAt(key, unixTime), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#flushAll()
     */
    @Override
    public void flushAll() {
        delegate.flushAll();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#flushDb()
     */
    @Override
    public void flushDb() {
        delegate.flushDb();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#get(byte[])
     */
    @Override
    public byte[] get(byte[] key) {
        return convertAndReturn(delegate.get(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#getBit(byte[], long)
     */
    @Override
    public Boolean getBit(byte[] key, long offset) {
        return convertAndReturn(delegate.getBit(key, offset), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#getConfig(java.lang.String)
     */
    @Override
    public Properties getConfig(String pattern) {
        return convertAndReturn(delegate.getConfig(pattern), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnection#getNativeConnection()
     */
    @Override
    public Object getNativeConnection() {
        return convertAndReturn(delegate.getNativeConnection(), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#getRange(byte[], long, long)
     */
    @Override
    public byte[] getRange(byte[] key, long start, long end) {
        return convertAndReturn(delegate.getRange(key, start, end), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#getSet(byte[], byte[])
     */
    @Override
    public byte[] getSet(byte[] key, byte[] value) {
        return convertAndReturn(delegate.getSet(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisPubSubCommands#getSubscription()
     */
    @Override
    public Subscription getSubscription() {
        return delegate.getSubscription();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hDel(byte[], byte[][])
     */
    @Override
    public Long hDel(byte[] key, byte[]... fields) {
        return convertAndReturn(delegate.hDel(key, fields), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hExists(byte[], byte[])
     */
    @Override
    public Boolean hExists(byte[] key, byte[] field) {
        return convertAndReturn(delegate.hExists(key, field), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hGet(byte[], byte[])
     */
    @Override
    public byte[] hGet(byte[] key, byte[] field) {
        return convertAndReturn(delegate.hGet(key, field), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hGetAll(byte[])
     */
    @Override
    public Map<byte[], byte[]> hGetAll(byte[] key) {
        return convertAndReturn(delegate.hGetAll(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hIncrBy(byte[], byte[], long)
     */
    @Override
    public Long hIncrBy(byte[] key, byte[] field, long delta) {
        return convertAndReturn(delegate.hIncrBy(key, field, delta), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hIncrBy(byte[], byte[], double)
     */
    @Override
    public Double hIncrBy(byte[] key, byte[] field, double delta) {
        return convertAndReturn(delegate.hIncrBy(key, field, delta), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hKeys(byte[])
     */
    @Override
    public Set<byte[]> hKeys(byte[] key) {
        return convertAndReturn(delegate.hKeys(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hLen(byte[])
     */
    @Override
    public Long hLen(byte[] key) {
        return convertAndReturn(delegate.hLen(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hMGet(byte[], byte[][])
     */
    @Override
    public List<byte[]> hMGet(byte[] key, byte[]... fields) {
        return convertAndReturn(delegate.hMGet(key, fields), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hMSet(byte[], java.util.Map)
     */
    @Override
    public void hMSet(byte[] key, Map<byte[], byte[]> hashes) {
        delegate.hMSet(key, hashes);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hSet(byte[], byte[], byte[])
     */
    @Override
    public Boolean hSet(byte[] key, byte[] field, byte[] value) {
        return convertAndReturn(delegate.hSet(key, field, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hSetNX(byte[], byte[], byte[])
     */
    @Override
    public Boolean hSetNX(byte[] key, byte[] field, byte[] value) {
        return convertAndReturn(delegate.hSetNX(key, field, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hVals(byte[])
     */
    @Override
    public List<byte[]> hVals(byte[] key) {
        return convertAndReturn(delegate.hVals(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#incr(byte[])
     */
    @Override
    public Long incr(byte[] key) {
        return convertAndReturn(delegate.incr(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#incrBy(byte[], long)
     */
    @Override
    public Long incrBy(byte[] key, long value) {

        return convertAndReturn(delegate.incrBy(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#incrBy(byte[], double)
     */
    @Override
    public Double incrBy(byte[] key, double value) {
        return convertAndReturn(delegate.incrBy(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#info()
     */
    @Override
    public Properties info() {
        return convertAndReturn(delegate.info(), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#info(java.lang.String)
     */
    @Override
    public Properties info(String section) {
        return convertAndReturn(delegate.info(section), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnection#isClosed()
     */
    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnection#isQueueing()
     */
    @Override
    public boolean isQueueing() {
        return delegate.isQueueing();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisPubSubCommands#isSubscribed()
     */
    @Override
    public boolean isSubscribed() {
        return delegate.isSubscribed();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#keys(byte[])
     */
    @Override
    public Set<byte[]> keys(byte[] pattern) {
        return convertAndReturn(delegate.keys(pattern), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#lastSave()
     */
    @Override
    public Long lastSave() {
        return convertAndReturn(delegate.lastSave(), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lIndex(byte[], long)
     */
    @Override
    public byte[] lIndex(byte[] key, long index) {
        return convertAndReturn(delegate.lIndex(key, index), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lInsert(byte[], org.springframework.data.redis.connection.RedisListCommands.Position, byte[], byte[])
     */
    @Override
    public Long lInsert(byte[] key, RedisListCommands.Position where, byte[] pivot, byte[] value) {
        return convertAndReturn(delegate.lInsert(key, where, pivot, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lLen(byte[])
     */
    @Override
    public Long lLen(byte[] key) {
        return convertAndReturn(delegate.lLen(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lPop(byte[])
     */
    @Override
    public byte[] lPop(byte[] key) {
        return convertAndReturn(delegate.lPop(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lPush(byte[], byte[][])
     */
    @Override
    public Long lPush(byte[] key, byte[]... values) {
        return convertAndReturn(delegate.lPush(key, values), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lPushX(byte[], byte[])
     */
    @Override
    public Long lPushX(byte[] key, byte[] value) {
        return convertAndReturn(delegate.lPushX(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lRange(byte[], long, long)
     */
    @Override
    public List<byte[]> lRange(byte[] key, long start, long end) {
        return convertAndReturn(delegate.lRange(key, start, end), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lRem(byte[], long, byte[])
     */
    @Override
    public Long lRem(byte[] key, long count, byte[] value) {

        return convertAndReturn(delegate.lRem(key, count, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lSet(byte[], long, byte[])
     */
    @Override
    public void lSet(byte[] key, long index, byte[] value) {
        delegate.lSet(key, index, value);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#lTrim(byte[], long, long)
     */
    @Override
    public void lTrim(byte[] key, long start, long end) {
        delegate.lTrim(key, start, end);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#mGet(byte[][])
     */
    @Override
    public List<byte[]> mGet(byte[]... keys) {
        return convertAndReturn(delegate.mGet(keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#mSet(java.util.Map)
     */
    @Override
    public Boolean mSet(Map<byte[], byte[]> tuple) {
        return convertAndReturn(delegate.mSet(tuple), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#mSetNX(java.util.Map)
     */
    @Override
    public Boolean mSetNX(Map<byte[], byte[]> tuple) {
        return convertAndReturn(delegate.mSetNX(tuple), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisTxCommands#multi()
     */
    @Override
    public void multi() {
        delegate.multi();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#persist(byte[])
     */
    @Override
    public Boolean persist(byte[] key) {
        return convertAndReturn(delegate.persist(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#move(byte[], int)
     */
    @Override
    public Boolean move(byte[] key, int dbIndex) {
        return convertAndReturn(delegate.move(key, dbIndex), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnectionCommands#ping()
     */
    @Override
    public String ping() {
        return convertAndReturn(delegate.ping(), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisPubSubCommands#pSubscribe(org.springframework.data.redis.connection.MessageListener, byte[][])
     */
    @Override
    public void pSubscribe(MessageListener listener, byte[]... patterns) {
        delegate.pSubscribe(listener, patterns);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisPubSubCommands#publish(byte[], byte[])
     */
    @Override
    public Long publish(byte[] channel, byte[] message) {
        return convertAndReturn(delegate.publish(channel, message), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#randomKey()
     */
    @Override
    public byte[] randomKey() {
        return convertAndReturn(delegate.randomKey(), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#rename(byte[], byte[])
     */
    @Override
    public void rename(byte[] sourceKey, byte[] targetKey) {
        delegate.rename(sourceKey, targetKey);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#renameNX(byte[], byte[])
     */
    @Override
    public Boolean renameNX(byte[] sourceKey, byte[] targetKey) {
        return convertAndReturn(delegate.renameNX(sourceKey, targetKey), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#resetConfigStats()
     */
    @Override
    public void resetConfigStats() {
        delegate.resetConfigStats();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#rPop(byte[])
     */
    @Override
    public byte[] rPop(byte[] key) {
        return convertAndReturn(delegate.rPop(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#rPopLPush(byte[], byte[])
     */
    @Override
    public byte[] rPopLPush(byte[] srcKey, byte[] dstKey) {
        return convertAndReturn(delegate.rPopLPush(srcKey, dstKey), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#rPush(byte[], byte[][])
     */
    @Override
    public Long rPush(byte[] key, byte[]... values) {
        return convertAndReturn(delegate.rPush(key, values), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisListCommands#rPushX(byte[], byte[])
     */
    @Override
    public Long rPushX(byte[] key, byte[] value) {
        return convertAndReturn(delegate.rPushX(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sAdd(byte[], byte[][])
     */
    @Override
    public Long sAdd(byte[] key, byte[]... values) {
        return convertAndReturn(delegate.sAdd(key, values), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#save()
     */
    @Override
    public void save() {
        delegate.save();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sCard(byte[])
     */
    @Override
    public Long sCard(byte[] key) {
        return convertAndReturn(delegate.sCard(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sDiff(byte[][])
     */
    @Override
    public Set<byte[]> sDiff(byte[]... keys) {
        return convertAndReturn(delegate.sDiff(keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sDiffStore(byte[], byte[][])
     */
    @Override
    public Long sDiffStore(byte[] destKey, byte[]... keys) {
        return convertAndReturn(delegate.sDiffStore(destKey, keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnectionCommands#select(int)
     */
    @Override
    public void select(int dbIndex) {
        delegate.select(dbIndex);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#set(byte[], byte[])
     */
    @Override
    public Boolean set(byte[] key, byte[] value) {
        return convertAndReturn(delegate.set(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#set(byte[], byte[], org.springframework.data.redis.core.types.Expiration, org.springframework.data.redis.connection.RedisStringCommands.SetOptions)
     */
    @Override
    public Boolean set(byte[] key, byte[] value, Expiration expiration, RedisStringCommands.SetOption option) {
        return convertAndReturn(delegate.set(key, value, expiration, option), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#setBit(byte[], long, boolean)
     */
    @Override
    public Boolean setBit(byte[] key, long offset, boolean value) {
        return convertAndReturn(delegate.setBit(key, offset, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#setConfig(java.lang.String, java.lang.String)
     */
    @Override
    public void setConfig(String param, String value) {
        delegate.setConfig(param, value);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#setEx(byte[], long, byte[])
     */
    @Override
    public Boolean setEx(byte[] key, long seconds, byte[] value) {
        return convertAndReturn(delegate.setEx(key, seconds, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#pSetEx(byte[], long, byte[])
     */
    @Override
    public Boolean pSetEx(byte[] key, long milliseconds, byte[] value) {
        return convertAndReturn(delegate.pSetEx(key, milliseconds, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#setNX(byte[], byte[])
     */
    @Override
    public Boolean setNX(byte[] key, byte[] value) {
        return convertAndReturn(delegate.setNX(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#setRange(byte[], byte[], long)
     */
    @Override
    public void setRange(byte[] key, byte[] value, long start) {
        delegate.setRange(key, value, start);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#shutdown()
     */
    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#shutdown(org.springframework.data.redis.connection.RedisServerCommands.ShutdownOption)
     */
    @Override
    public void shutdown(RedisServerCommands.ShutdownOption option) {
        delegate.shutdown(option);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sInter(byte[][])
     */
    @Override
    public Set<byte[]> sInter(byte[]... keys) {
        return convertAndReturn(delegate.sInter(keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sInterStore(byte[], byte[][])
     */
    @Override
    public Long sInterStore(byte[] destKey, byte[]... keys) {
        return convertAndReturn(delegate.sInterStore(destKey, keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sIsMember(byte[], byte[])
     */
    @Override
    public Boolean sIsMember(byte[] key, byte[] value) {
        return convertAndReturn(delegate.sIsMember(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sMembers(byte[])
     */
    @Override
    public Set<byte[]> sMembers(byte[] key) {
        return convertAndReturn(delegate.sMembers(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sMove(byte[], byte[], byte[])
     */
    @Override
    public Boolean sMove(byte[] srcKey, byte[] destKey, byte[] value) {
        return convertAndReturn(delegate.sMove(srcKey, destKey, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#sort(byte[], org.springframework.data.redis.connection.SortParameters, byte[])
     */
    @Override
    public Long sort(byte[] key, SortParameters params, byte[] storeKey) {
        return convertAndReturn(delegate.sort(key, params, storeKey), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#sort(byte[], org.springframework.data.redis.connection.SortParameters)
     */
    @Override
    public List<byte[]> sort(byte[] key, SortParameters params) {
        return convertAndReturn(delegate.sort(key, params), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sPop(byte[])
     */
    @Override
    public byte[] sPop(byte[] key) {
        return convertAndReturn(delegate.sPop(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sPop(byte[], long)
     */
    @Override
    public List<byte[]> sPop(byte[] key, long count) {
        return convertAndReturn(delegate.sPop(key, count), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sRandMember(byte[])
     */
    @Override
    public byte[] sRandMember(byte[] key) {
        return convertAndReturn(delegate.sRandMember(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sRandMember(byte[], long)
     */
    @Override
    public List<byte[]> sRandMember(byte[] key, long count) {
        return convertAndReturn(delegate.sRandMember(key, count), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sRem(byte[], byte[][])
     */
    @Override
    public Long sRem(byte[] key, byte[]... values) {
        return convertAndReturn(delegate.sRem(key, values), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#strLen(byte[])
     */
    @Override
    public Long strLen(byte[] key) {
        return convertAndReturn(delegate.strLen(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#bitCount(byte[])
     */
    @Override
    public Long bitCount(byte[] key) {
        return convertAndReturn(delegate.bitCount(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#bitCount(byte[], long, long)
     */
    @Override
    public Long bitCount(byte[] key, long start, long end) {
        return convertAndReturn(delegate.bitCount(key, start, end), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisStringCommands#bitOp(org.springframework.data.redis.connection.RedisStringCommands.BitOperation, byte[], byte[][])
     */
    @Override
    public Long bitOp(RedisStringCommands.BitOperation op, byte[] destination, byte[]... keys) {
        return convertAndReturn(delegate.bitOp(op, destination, keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisPubSubCommands#subscribe(org.springframework.data.redis.connection.MessageListener, byte[][])
     */
    @Override
    public void subscribe(MessageListener listener, byte[]... channels) {
        delegate.subscribe(listener, channels);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sUnion(byte[][])
     */
    @Override
    public Set<byte[]> sUnion(byte[]... keys) {
        return convertAndReturn(delegate.sUnion(keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#sUnionStore(byte[], byte[][])
     */
    @Override
    public Long sUnionStore(byte[] destKey, byte[]... keys) {
        return convertAndReturn(delegate.sUnionStore(destKey, keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#ttl(byte[])
     */
    @Override
    public Long ttl(byte[] key) {
        return convertAndReturn(delegate.ttl(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#ttl(byte[], java.util.concurrent.TimeUnit)
     */
    @Override
    public Long ttl(byte[] key, TimeUnit timeUnit) {
        return convertAndReturn(delegate.ttl(key, timeUnit), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#type(byte[])
     */
    @Override
    public DataType type(byte[] key) {
        return convertAndReturn(delegate.type(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisTxCommands#unwatch()
     */
    @Override
    public void unwatch() {
        delegate.unwatch();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisTxCommands#watch(byte[][])
     */
    @Override
    public void watch(byte[]... keys) {
        delegate.watch(keys);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zAdd(byte[], double, byte[])
     */
    @Override
    public Boolean zAdd(byte[] key, double score, byte[] value) {
        return convertAndReturn(delegate.zAdd(key, score, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zAdd(byte[], java.util.Set)
     */
    @Override
    public Long zAdd(byte[] key, Set<RedisZSetCommands.Tuple> tuples) {
        return convertAndReturn(delegate.zAdd(key, tuples), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zCard(byte[])
     */
    @Override
    public Long zCard(byte[] key) {
        return convertAndReturn(delegate.zCard(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zCount(byte[], double, double)
     */
    @Override
    public Long zCount(byte[] key, double min, double max) {
        return convertAndReturn(delegate.zCount(key, min, max), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zCount(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Long zCount(byte[] key, RedisZSetCommands.Range range) {
        return convertAndReturn(delegate.zCount(key, range), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zIncrBy(byte[], double, byte[])
     */
    @Override
    public Double zIncrBy(byte[] key, double increment, byte[] value) {
        return convertAndReturn(delegate.zIncrBy(key, increment, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zInterStore(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Aggregate, int[], byte[][])
     */
    @Override
    public Long zInterStore(byte[] destKey, RedisZSetCommands.Aggregate aggregate, int[] weights, byte[]... sets) {
        return convertAndReturn(delegate.zInterStore(destKey, aggregate, weights, sets), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zInterStore(byte[], byte[][])
     */
    @Override
    public Long zInterStore(byte[] destKey, byte[]... sets) {
        return convertAndReturn(delegate.zInterStore(destKey, sets), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRange(byte[], long, long)
     */
    @Override
    public Set<byte[]> zRange(byte[] key, long start, long end) {
        return convertAndReturn(delegate.zRange(key, start, end), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScore(byte[], double, double, long, long)
     */
    @Override
    public Set<byte[]> zRangeByScore(byte[] key, double min, double max, long offset, long count) {
        return convertAndReturn(delegate.zRangeByScore(key, min, max, offset, count), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScore(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<byte[]> zRangeByScore(byte[] key, RedisZSetCommands.Range range) {
        return convertAndReturn(delegate.zRangeByScore(key, range), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScore(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range, org.springframework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<byte[]> zRangeByScore(byte[] key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return convertAndReturn(delegate.zRangeByScore(key, range, limit), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScoreWithScores(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRangeByScoreWithScores(byte[] key, RedisZSetCommands.Range range) {
        return convertAndReturn(delegate.zRangeByScoreWithScores(key, range), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScore(byte[], double, double)
     */
    @Override
    public Set<byte[]> zRangeByScore(byte[] key, double min, double max) {
        return convertAndReturn(delegate.zRangeByScore(key, min, max), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScoreWithScores(byte[], double, double, long, long)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRangeByScoreWithScores(byte[] key, double min, double max, long offset, long count) {
        return convertAndReturn(delegate.zRangeByScoreWithScores(key, min, max, offset, count), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScoreWithScores(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range, org.springframework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRangeByScoreWithScores(byte[] key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return convertAndReturn(delegate.zRangeByScoreWithScores(key, range, limit), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScoreWithScores(byte[], double, double)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRangeByScoreWithScores(byte[] key, double min, double max) {
        return convertAndReturn(delegate.zRangeByScoreWithScores(key, min, max), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeWithScores(byte[], long, long)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRangeWithScores(byte[] key, long start, long end) {
        return convertAndReturn(delegate.zRangeWithScores(key, start, end), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRangeByScore(byte[], double, double, long, long)
     */
    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, double min, double max, long offset, long count) {
        return convertAndReturn(delegate.zRevRangeByScore(key, min, max, offset, count), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRangeByScore(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, RedisZSetCommands.Range range) {
        return convertAndReturn(delegate.zRevRangeByScore(key, range), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRangeByScore(byte[], double, double)
     */
    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, double min, double max) {
        return convertAndReturn(delegate.zRevRangeByScore(key, min, max), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRangeByScore(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range, org.springframework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return convertAndReturn(delegate.zRevRangeByScore(key, range, limit), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRangeByScoreWithScores(byte[], double, double, long, long)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRevRangeByScoreWithScores(byte[] key, double min, double max, long offset, long count) {
        return convertAndReturn(delegate.zRevRangeByScoreWithScores(key, min, max, offset, count), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRangeByScoreWithScores(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRevRangeByScoreWithScores(byte[] key, RedisZSetCommands.Range range) {
        return convertAndReturn(delegate.zRevRangeByScoreWithScores(key, range), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRangeByScoreWithScores(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range, org.springframework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRevRangeByScoreWithScores(byte[] key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return convertAndReturn(delegate.zRevRangeByScoreWithScores(key, range, limit), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRangeByScoreWithScores(byte[], double, double)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRevRangeByScoreWithScores(byte[] key, double min, double max) {
        return convertAndReturn(delegate.zRevRangeByScoreWithScores(key, min, max), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRank(byte[], byte[])
     */
    @Override
    public Long zRank(byte[] key, byte[] value) {
        return convertAndReturn(delegate.zRank(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRem(byte[], byte[][])
     */
    @Override
    public Long zRem(byte[] key, byte[]... values) {
        return convertAndReturn(delegate.zRem(key, values), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRemRange(byte[], long, long)
     */
    @Override
    public Long zRemRange(byte[] key, long start, long end) {
        return convertAndReturn(delegate.zRemRange(key, start, end), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRemRangeByScore(byte[], double, double)
     */
    @Override
    public Long zRemRangeByScore(byte[] key, double min, double max) {
        return convertAndReturn(delegate.zRemRangeByScore(key, min, max), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRemRangeByScore(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Long zRemRangeByScore(byte[] key, RedisZSetCommands.Range range) {
        return convertAndReturn(delegate.zRemRangeByScore(key, range), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRange(byte[], long, long)
     */
    @Override
    public Set<byte[]> zRevRange(byte[] key, long start, long end) {
        return convertAndReturn(delegate.zRevRange(key, start, end), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRangeWithScores(byte[], long, long)
     */
    @Override
    public Set<RedisZSetCommands.Tuple> zRevRangeWithScores(byte[] key, long start, long end) {
        return convertAndReturn(delegate.zRevRangeWithScores(key, start, end), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRevRank(byte[], byte[])
     */
    @Override
    public Long zRevRank(byte[] key, byte[] value) {
        return convertAndReturn(delegate.zRevRank(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zScore(byte[], byte[])
     */
    @Override
    public Double zScore(byte[] key, byte[] value) {
        return convertAndReturn(delegate.zScore(key, value), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zUnionStore(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Aggregate, int[], byte[][])
     */
    public Long zUnionStore(byte[] destKey, RedisZSetCommands.Aggregate aggregate, int[] weights, byte[]... sets) {
        return convertAndReturn(delegate.zUnionStore(destKey, aggregate, weights, sets), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zUnionStore(byte[], byte[][])
     */
    public Long zUnionStore(byte[] destKey, byte[]... sets) {
        return convertAndReturn(delegate.zUnionStore(destKey, sets), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#pExpire(byte[], long)
     */
    @Override
    public Boolean pExpire(byte[] key, long millis) {
        return convertAndReturn(delegate.pExpire(key, millis), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#pExpireAt(byte[], long)
     */
    @Override
    public Boolean pExpireAt(byte[] key, long unixTimeInMillis) {
        return convertAndReturn(delegate.pExpireAt(key, unixTimeInMillis), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#pTtl(byte[])
     */
    @Override
    public Long pTtl(byte[] key) {
        return convertAndReturn(delegate.pTtl(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#pTtl(byte[], java.util.concurrent.TimeUnit)
     */
    @Override
    public Long pTtl(byte[] key, TimeUnit timeUnit) {
        return convertAndReturn(delegate.pTtl(key, timeUnit), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#dump(byte[])
     */
    @Override
    public byte[] dump(byte[] key) {
        return convertAndReturn(delegate.dump(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#restore(byte[], long, byte[])
     */
    @Override
    public void restore(byte[] key, long ttlInMillis, byte[] serializedValue) {
        delegate.restore(key, ttlInMillis, serializedValue);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisScriptingCommands#scriptFlush()
     */
    @Override
    public void scriptFlush() {
        delegate.scriptFlush();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisScriptingCommands#scriptKill()
     */
    @Override
    public void scriptKill() {
        delegate.scriptKill();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisScriptingCommands#scriptLoad(byte[])
     */
    @Override
    public String scriptLoad(byte[] script) {
        return convertAndReturn(delegate.scriptLoad(script), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisScriptingCommands#scriptExists(java.lang.String[])
     */
    @Override
    public List<Boolean> scriptExists(String... scriptSha1) {
        return convertAndReturn(delegate.scriptExists(scriptSha1), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisScriptingCommands#eval(byte[], org.springframework.data.redis.connection.ReturnType, int, byte[][])
     */
    @Override
    public <T> T eval(byte[] script, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return convertAndReturn(delegate.eval(script, returnType, numKeys, keysAndArgs), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisScriptingCommands#evalSha(java.lang.String, org.springframework.data.redis.connection.ReturnType, int, byte[][])
     */
    @Override
    public <T> T evalSha(String scriptSha1, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return convertAndReturn(delegate.evalSha(scriptSha1, returnType, numKeys, keysAndArgs), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisScriptingCommands#evalSha(byte[], org.springframework.data.redis.connection.ReturnType, int, byte[][])
     */
    @Override
    public <T> T evalSha(byte[] scriptSha1, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        return convertAndReturn(delegate.evalSha(scriptSha1, returnType, numKeys, keysAndArgs), identityConverter);
    }

    //
    // String methods
    //

    private byte[][] keySerializeMulti(String... keys) {

        if (keys == null) {
            return EMPTY_2D_BYTE_ARRAY;
        }

        byte[][] ret = new byte[keys.length][];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = keySerializer.serialize(keys[i]);
        }

        return ret;
    }

    private byte[][] valueSerializeMulti(Object... keys) {

        if (keys == null) {
            return EMPTY_2D_BYTE_ARRAY;
        }

        byte[][] ret = new byte[keys.length][];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = valueSerializer.serialize(keys[i]);
        }

        return ret;
    }


    private Map<byte[], byte[]> serialize(Map<String, Object> hashes) {
        Map<byte[], byte[]> ret = new LinkedHashMap<>(hashes.size());

        for (Map.Entry<String, Object> entry : hashes.entrySet()) {
            ret.put(keySerializer.serialize(entry.getKey()), valueSerializer.serialize(entry.getValue()));
        }

        return ret;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#append(java.lang.String, java.lang.String)
     */
    @Override
    public Long append(String key, String value) {
        return append(keySerializer.serialize(key), keySerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#bLPop(int, java.lang.String[])
     */
    @Override
    public List<Object> bLPop(int timeout, String... keys) {
        return convertAndReturn(delegate.bLPop(timeout, keySerializeMulti(keys)), byteListToObjectList);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#bRPop(int, java.lang.String[])
     */
    @Override
    public List<Object> bRPop(int timeout, String... keys) {
        return convertAndReturn(delegate.bRPop(timeout, keySerializeMulti(keys)), byteListToObjectList);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#bRPopLPush(int, java.lang.String, java.lang.String)
     */
    @Override
    public String bRPopLPush(int timeout, String srcKey, String dstKey) {
        return convertAndReturn(delegate.bRPopLPush(timeout, keySerializer.serialize(srcKey), keySerializer.serialize(dstKey)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#decr(java.lang.String)
     */
    @Override
    public Long decr(String key) {
        return decr(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#decrBy(java.lang.String, long)
     */
    @Override
    public Long decrBy(String key, long value) {
        return decrBy(keySerializer.serialize(key), value);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#del(java.lang.String[])
     */
    @Override
    public Long del(String... keys) {
        return del(keySerializeMulti(keys));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#echo(java.lang.String)
     */
    @Override
    public String echo(String message) {
        return convertAndReturn(delegate.echo(keySerializer.serialize(message)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#exists(java.lang.String)
     */
    @Override
    public Boolean exists(String key) {
        return exists(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#expire(java.lang.String, long)
     */
    @Override
    public Boolean expire(String key, long seconds) {
        return expire(keySerializer.serialize(key), seconds);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#expireAt(java.lang.String, long)
     */
    @Override
    public Boolean expireAt(String key, long unixTime) {
        return expireAt(keySerializer.serialize(key), unixTime);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#get(java.lang.String)
     */
    @Override
    public Object get(String key) {
        return convertAndReturn(delegate.get(keySerializer.serialize(key)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#getBit(java.lang.String, long)
     */
    @Override
    public Boolean getBit(String key, long offset) {
        return getBit(keySerializer.serialize(key), offset);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#getRange(java.lang.String, long, long)
     */
    @Override
    public String getRange(String key, long start, long end) {
        return convertAndReturn(delegate.getRange(keySerializer.serialize(key), start, end), bytesToString);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#getSet(java.lang.String, java.lang.String)
     */
    @Override
    public Object getSet(String key, Object value) {
        return convertAndReturn(delegate.getSet(keySerializer.serialize(key), valueSerializer.serialize(value)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hDel(java.lang.String, java.lang.String[])
     */
    @Override
    public Long hDel(String key, String... fields) {
        return hDel(keySerializer.serialize(key), keySerializeMulti(fields));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hExists(java.lang.String, java.lang.String)
     */
    @Override
    public Boolean hExists(String key, String field) {
        return hExists(keySerializer.serialize(key), keySerializer.serialize(field));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hGet(java.lang.String, java.lang.String)
     */
    @Override
    public Object hGet(String key, String field) {
        return convertAndReturn(delegate.hGet(keySerializer.serialize(key), keySerializer.serialize(field)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hGetAll(java.lang.String)
     */
    @Override
    public Map<String, Object> hGetAll(String key) {
        return convertAndReturn(delegate.hGetAll(keySerializer.serialize(key)), byteMapToObjectMap);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hIncrBy(java.lang.String, java.lang.String, long)
     */
    @Override
    public Long hIncrBy(String key, String field, long delta) {
        return hIncrBy(keySerializer.serialize(key), keySerializer.serialize(field), delta);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hIncrBy(java.lang.String, java.lang.String, double)
     */
    @Override
    public Double hIncrBy(String key, String field, double delta) {
        return hIncrBy(keySerializer.serialize(key), keySerializer.serialize(field), delta);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hKeys(java.lang.String)
     */
    @Override
    public Set<Object> hKeys(String key) {
        return convertAndReturn(delegate.hKeys(keySerializer.serialize(key)), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hLen(java.lang.String)
     */
    @Override
    public Long hLen(String key) {
        return hLen(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hMGet(java.lang.String, java.lang.String[])
     */
    @Override
    public List<Object> hMGet(String key, String... fields) {
        return convertAndReturn(delegate.hMGet(keySerializer.serialize(key), keySerializeMulti(fields)), byteListToObjectList);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hMSet(java.lang.String, java.util.Map)
     */
    @Override
    public void hMSet(String key, Map<String, Object> hashes) {
        delegate.hMSet(keySerializer.serialize(key), serialize(hashes));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hSet(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Boolean hSet(String key, String field, Object value) {
        return hSet(keySerializer.serialize(key), keySerializer.serialize(field), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hSetNX(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Boolean hSetNX(String key, String field, Object value) {
        return hSetNX(keySerializer.serialize(key), keySerializer.serialize(field), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hVals(java.lang.String)
     */
    @Override
    public List<Object> hVals(String key) {
        return convertAndReturn(delegate.hVals(keySerializer.serialize(key)), byteListToObjectList);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#incr(java.lang.String)
     */
    @Override
    public Long incr(String key) {
        return incr(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#incrBy(java.lang.String, long)
     */
    @Override
    public Long incrBy(String key, long value) {
        return incrBy(keySerializer.serialize(key), value);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#incrBy(java.lang.String, double)
     */
    @Override
    public Double incrBy(String key, double value) {
        return incrBy(keySerializer.serialize(key), value);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#keys(java.lang.String)
     */
    @Override
    public Collection<String> keys(String pattern) {
        return convertAndReturn(delegate.keys(keySerializer.serialize(pattern)), byteSetToStringSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lIndex(java.lang.String, long)
     */
    @Override
    public Object lIndex(String key, long index) {
        return convertAndReturn(delegate.lIndex(keySerializer.serialize(key), index), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lInsert(java.lang.String, org.springframework.data.redis.connection.RedisListCommands.Position, java.lang.String, java.lang.String)
     */
    @Override
    public Long lInsert(String key, RedisListCommands.Position where, Object pivot, Object value) {
        return lInsert(keySerializer.serialize(key), where, valueSerializer.serialize(pivot), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lLen(java.lang.String)
     */
    @Override
    public Long lLen(String key) {
        return lLen(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lPop(java.lang.String)
     */
    @Override
    public Object lPop(String key) {
        return convertAndReturn(delegate.lPop(keySerializer.serialize(key)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lPush(java.lang.String, java.lang.String[])
     */
    @Override
    public Long lPush(String key, Object... values) {
        return lPush(keySerializer.serialize(key), valueSerializeMulti(values));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lPushX(java.lang.String, java.lang.String)
     */
    @Override
    public Long lPushX(String key, Object value) {
        return lPushX(keySerializer.serialize(key), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lRange(java.lang.String, long, long)
     */
    @Override
    public List<Object> lRange(String key, long start, long end) {
        return convertAndReturn(delegate.lRange(keySerializer.serialize(key), start, end), byteListToObjectList);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lRem(java.lang.String, long, java.lang.String)
     */
    @Override
    public Long lRem(String key, long count, Object value) {
        return lRem(keySerializer.serialize(key), count, valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lSet(java.lang.String, long, java.lang.String)
     */
    @Override
    public void lSet(String key, long index, Object value) {
        delegate.lSet(keySerializer.serialize(key), index, valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#lTrim(java.lang.String, long, long)
     */
    @Override
    public void lTrim(String key, long start, long end) {
        delegate.lTrim(keySerializer.serialize(key), start, end);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#mGet(java.lang.String[])
     */
    @Override
    public List<Object> mGet(String... keys) {
        return convertAndReturn(delegate.mGet(keySerializeMulti(keys)), byteListToObjectList);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#mSetNXString(java.util.Map)
     */
    @Override
    public Boolean mSetNXString(Map<String, Object> tuple) {
        return mSetNX(serialize(tuple));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#mSetString(java.util.Map)
     */
    @Override
    public Boolean mSetString(Map<String, Object> tuple) {
        return mSet(serialize(tuple));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#persist(java.lang.String)
     */
    @Override
    public Boolean persist(String key) {
        return persist(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#move(java.lang.String, int)
     */
    @Override
    public Boolean move(String key, int dbIndex) {
        return move(keySerializer.serialize(key), dbIndex);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#pSubscribe(org.springframework.data.redis.connection.MessageListener, java.lang.String[])
     */
    @Override
    public void pSubscribe(MessageListener listener, String... patterns) {
        delegate.pSubscribe(listener, keySerializeMulti(patterns));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#publish(java.lang.String, java.lang.String)
     */
    @Override
    public Long publish(String channel, String message) {
        return publish(keySerializer.serialize(channel), keySerializer.serialize(message));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#rename(java.lang.String, java.lang.String)
     */
    @Override
    public void rename(String oldName, String newName) {
        delegate.rename(keySerializer.serialize(oldName), keySerializer.serialize(newName));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#renameNX(java.lang.String, java.lang.String)
     */
    @Override
    public Boolean renameNX(String oldName, String newName) {
        return renameNX(keySerializer.serialize(oldName), keySerializer.serialize(newName));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#rPop(java.lang.String)
     */
    @Override
    public Object rPop(String key) {
        return convertAndReturn(delegate.rPop(keySerializer.serialize(key)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#rPopLPush(java.lang.String, java.lang.String)
     */
    @Override
    public Object rPopLPush(String srcKey, String dstKey) {
        return convertAndReturn(delegate.rPopLPush(keySerializer.serialize(srcKey), keySerializer.serialize(dstKey)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#rPush(java.lang.String, java.lang.String[])
     */
    @Override
    public Long rPush(String key, Object... values) {
        return rPush(keySerializer.serialize(key), valueSerializeMulti(values));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#rPushX(java.lang.String, java.lang.String)
     */
    @Override
    public Long rPushX(String key, Object value) {
        return rPushX(keySerializer.serialize(key), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sAdd(java.lang.String, java.lang.String[])
     */
    @Override
    public Long sAdd(String key, Object... values) {
        return sAdd(keySerializer.serialize(key), valueSerializeMulti(values));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sCard(java.lang.String)
     */
    @Override
    public Long sCard(String key) {
        return sCard(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sDiff(java.lang.String[])
     */
    @Override
    public Set<Object> sDiff(String... keys) {
        return convertAndReturn(delegate.sDiff(keySerializeMulti(keys)), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sDiffStore(java.lang.String, java.lang.String[])
     */
    @Override
    public Long sDiffStore(String destKey, String... keys) {
        return sDiffStore(keySerializer.serialize(destKey), keySerializeMulti(keys));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#set(java.lang.String, java.lang.String)
     */
    @Override
    public Boolean set(String key, Object value) {
        return set(keySerializer.serialize(key), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#set(java.lang.String, java.lang.String, org.springframework.data.redis.core.types.Expiration, org.springframework.data.redis.connection.RedisStringCommands.SetOptions)
     */
    @Override
    public Boolean set(String key, Object value, Expiration expiration, RedisStringCommands.SetOption option) {
        return set(keySerializer.serialize(key), valueSerializer.serialize(value), expiration, option);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#setBit(java.lang.String, long, boolean)
     */
    @Override
    public Boolean setBit(String key, long offset, boolean value) {
        return setBit(keySerializer.serialize(key), offset, value);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#setEx(java.lang.String, long, java.lang.String)
     */
    @Override
    public Boolean setEx(String key, long seconds, Object value) {
        return setEx(keySerializer.serialize(key), seconds, valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#pSetEx(java.lang.String, long, java.lang.String)
     */
    @Override
    public Boolean pSetEx(String key, long seconds, Object value) {
        return pSetEx(keySerializer.serialize(key), seconds, valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#setNX(java.lang.String, java.lang.String)
     */
    @Override
    public Boolean setNX(String key, Object value) {
        return setNX(keySerializer.serialize(key), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#setRange(java.lang.String, java.lang.String, long)
     */
    @Override
    public void setRange(String key, String value, long start) {
        delegate.setRange(keySerializer.serialize(key), keySerializer.serialize(value), start);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sInter(java.lang.String[])
     */
    @Override
    public Set<Object> sInter(String... keys) {
        return convertAndReturn(delegate.sInter(keySerializeMulti(keys)), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sInterStore(java.lang.String, java.lang.String[])
     */
    @Override
    public Long sInterStore(String destKey, String... keys) {
        return sInterStore(keySerializer.serialize(destKey), keySerializeMulti(keys));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sIsMember(java.lang.String, java.lang.String)
     */
    @Override
    public Boolean sIsMember(String key, Object value) {
        return sIsMember(keySerializer.serialize(key), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sMembers(java.lang.String)
     */
    @Override
    public Set<Object> sMembers(String key) {
        return convertAndReturn(delegate.sMembers(keySerializer.serialize(key)), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sMove(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Boolean sMove(String srcKey, String destKey, Object value) {
        return sMove(keySerializer.serialize(srcKey), keySerializer.serialize(destKey), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sort(java.lang.String, org.springframework.data.redis.connection.SortParameters, java.lang.String)
     */
    @Override
    public Long sort(String key, SortParameters params, String storeKey) {
        return sort(keySerializer.serialize(key), params, keySerializer.serialize(storeKey));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sort(java.lang.String, org.springframework.data.redis.connection.SortParameters)
     */
    @Override
    public List<Object> sort(String key, SortParameters params) {
        return convertAndReturn(delegate.sort(keySerializer.serialize(key), params), byteListToObjectList);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sPop(java.lang.String)
     */
    @Override
    public String sPop(String key) {
        return convertAndReturn(delegate.sPop(keySerializer.serialize(key)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sPop(java.lang.String, long)
     */
    @Override
    public List<Object> sPop(String key, long count) {
        return convertAndReturn(delegate.sPop(keySerializer.serialize(key), count), byteListToObjectList);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sRandMember(java.lang.String)
     */
    @Override
    public String sRandMember(String key) {
        return convertAndReturn(delegate.sRandMember(keySerializer.serialize(key)), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sRandMember(java.lang.String, long)
     */
    @Override
    public List<Object> sRandMember(String key, long count) {
        return convertAndReturn(delegate.sRandMember(keySerializer.serialize(key), count), byteListToObjectList);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sRem(java.lang.String, java.lang.String[])
     */
    @Override
    public Long sRem(String key, Object... values) {
        return sRem(keySerializer.serialize(key), valueSerializeMulti(values));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#strLen(java.lang.String)
     */
    @Override
    public Long strLen(String key) {
        return strLen(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#bitCount(java.lang.String)
     */
    @Override
    public Long bitCount(String key) {
        return bitCount(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#bitCount(java.lang.String, long, long)
     */
    @Override
    public Long bitCount(String key, long start, long end) {
        return bitCount(keySerializer.serialize(key), start, end);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#bitOp(org.springframework.data.redis.connection.RedisStringCommands.BitOperation, java.lang.String, java.lang.String[])
     */
    @Override
    public Long bitOp(RedisStringCommands.BitOperation op, String destination, String... keys) {
        return bitOp(op, keySerializer.serialize(destination), keySerializeMulti(keys));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#subscribe(org.springframework.data.redis.connection.MessageListener, java.lang.String[])
     */
    @Override
    public void subscribe(MessageListener listener, String... channels) {
        delegate.subscribe(listener, keySerializeMulti(channels));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sUnion(java.lang.String[])
     */
    @Override
    public Set<Object> sUnion(String... keys) {
        return convertAndReturn(delegate.sUnion(keySerializeMulti(keys)), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sUnionStore(java.lang.String, java.lang.String[])
     */
    @Override
    public Long sUnionStore(String destKey, String... keys) {
        return sUnionStore(keySerializer.serialize(destKey), keySerializeMulti(keys));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#ttl(java.lang.String)
     */
    @Override
    public Long ttl(String key) {
        return ttl(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#ttl(java.lang.String, java.util.concurrent.TimeUnit)
     */
    @Override
    public Long ttl(String key, TimeUnit timeUnit) {
        return ttl(keySerializer.serialize(key), timeUnit);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#type(java.lang.String)
     */
    @Override
    public DataType type(String key) {
        return type(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zAdd(java.lang.String, double, java.lang.String)
     */
    @Override
    public Boolean zAdd(String key, double score, Object value) {
        return zAdd(keySerializer.serialize(key), score, valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zAdd(java.lang.String, java.util.Set)
     */
    @Override
    public Long zAdd(String key, Set<ObjectRedisConnection.ObjectTuple> tuples) {
        return zAdd(keySerializer.serialize(key), ObjectTupleToTuple.convert(tuples));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zCard(java.lang.String)
     */
    @Override
    public Long zCard(String key) {
        return zCard(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zCount(java.lang.String, double, double)
     */
    @Override
    public Long zCount(String key, double min, double max) {
        return zCount(keySerializer.serialize(key), min, max);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zIncrBy(java.lang.String, double, java.lang.String)
     */
    @Override
    public Double zIncrBy(String key, double increment, Object value) {
        return zIncrBy(keySerializer.serialize(key), increment, valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zInterStore(java.lang.String, org.springframework.data.redis.connection.RedisZSetCommands.Aggregate, int[], java.lang.String[])
     */
    @Override
    public Long zInterStore(String destKey, RedisZSetCommands.Aggregate aggregate, int[] weights, Object... sets) {
        return zInterStore(keySerializer.serialize(destKey), aggregate, weights, valueSerializeMulti(sets));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zInterStore(java.lang.String, java.lang.String[])
     */
    @Override
    public Long zInterStore(String destKey, Object... sets) {
        return zInterStore(keySerializer.serialize(destKey), valueSerializeMulti(sets));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRange(java.lang.String, long, long)
     */
    @Override
    public Set<Object> zRange(String key, long start, long end) {
        return convertAndReturn(delegate.zRange(keySerializer.serialize(key), start, end), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeByScore(java.lang.String, double, double, long, long)
     */
    @Override
    public Set<Object> zRangeByScore(String key, double min, double max, long offset, long count) {
        return convertAndReturn(delegate.zRangeByScore(keySerializer.serialize(key), min, max, offset, count), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeByScore(java.lang.String, double, double)
     */
    @Override
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return convertAndReturn(delegate.zRangeByScore(keySerializer.serialize(key), min, max), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeByScoreWithScores(java.lang.String, double, double, long, long)
     */
    @Override
    public Set<ObjectRedisConnection.ObjectTuple> zRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        return convertAndReturn(delegate.zRangeByScoreWithScores(keySerializer.serialize(key), min, max, offset, count),
            tupleToObjectTuple);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeByScoreWithScores(java.lang.String, double, double)
     */
    @Override
    public Set<ObjectRedisConnection.ObjectTuple> zRangeByScoreWithScores(String key, double min, double max) {
        return convertAndReturn(delegate.zRangeByScoreWithScores(keySerializer.serialize(key), min, max), tupleToObjectTuple);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeWithScores(java.lang.String, long, long)
     */
    @Override
    public Set<ObjectRedisConnection.ObjectTuple> zRangeWithScores(String key, long start, long end) {
        return convertAndReturn(delegate.zRangeWithScores(keySerializer.serialize(key), start, end), tupleToObjectTuple);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRank(java.lang.String, java.lang.String)
     */
    @Override
    public Long zRank(String key, Object value) {
        return zRank(keySerializer.serialize(key), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRem(java.lang.String, java.lang.String[])
     */
    @Override
    public Long zRem(String key, Object... values) {
        return zRem(keySerializer.serialize(key), valueSerializeMulti(values));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRemRange(java.lang.String, long, long)
     */
    @Override
    public Long zRemRange(String key, long start, long end) {
        return zRemRange(keySerializer.serialize(key), start, end);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRemRangeByScore(java.lang.String, double, double)
     */
    @Override
    public Long zRemRangeByScore(String key, double min, double max) {
        return zRemRangeByScore(keySerializer.serialize(key), min, max);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRevRange(java.lang.String, long, long)
     */
    @Override
    public Set<Object> zRevRange(String key, long start, long end) {
        return convertAndReturn(delegate.zRevRange(keySerializer.serialize(key), start, end), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRevRangeWithScores(java.lang.String, long, long)
     */
    @Override
    public Set<ObjectRedisConnection.ObjectTuple> zRevRangeWithScores(String key, long start, long end) {
        return convertAndReturn(delegate.zRevRangeWithScores(keySerializer.serialize(key), start, end), tupleToObjectTuple);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRevRangeByScore(java.lang.String, double, double)
     */
    @Override
    public Set<Object> zRevRangeByScore(String key, double min, double max) {
        return convertAndReturn(delegate.zRevRangeByScore(keySerializer.serialize(key), min, max), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRevRangeByScoreWithScores(java.lang.String, double, double)
     */
    @Override
    public Set<ObjectRedisConnection.ObjectTuple> zRevRangeByScoreWithScores(String key, double min, double max) {
        return convertAndReturn(delegate.zRevRangeByScoreWithScores(keySerializer.serialize(key), min, max), tupleToObjectTuple);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRevRangeByScore(java.lang.String, double, double, long, long)
     */
    @Override
    public Set<Object> zRevRangeByScore(String key, double min, double max, long offset, long count) {
        return convertAndReturn(delegate.zRevRangeByScore(keySerializer.serialize(key), min, max, offset, count), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRevRangeByScoreWithScores(java.lang.String, double, double, long, long)
     */
    @Override
    public Set<ObjectRedisConnection.ObjectTuple> zRevRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        return convertAndReturn(delegate.zRevRangeByScoreWithScores(keySerializer.serialize(key), min, max, offset, count),
            tupleToObjectTuple);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRevRank(java.lang.String, java.lang.String)
     */
    @Override
    public Long zRevRank(String key, Object value) {
        return zRevRank(keySerializer.serialize(key), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zScore(java.lang.String, java.lang.String)
     */
    @Override
    public Double zScore(String key, Object value) {
        return zScore(keySerializer.serialize(key), valueSerializer.serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zUnionStore(java.lang.String, org.springframework.data.redis.connection.RedisZSetCommands.Aggregate, int[], java.lang.String[])
     */
    @Override
    public Long zUnionStore(String destKey, RedisZSetCommands.Aggregate aggregate, int[] weights, Object... sets) {
        return zUnionStore(keySerializer.serialize(destKey), aggregate, weights, valueSerializeMulti(sets));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zUnionStore(java.lang.String, java.lang.String[])
     */
    @Override
    public Long zUnionStore(String destKey, Object... sets) {
        return zUnionStore(keySerializer.serialize(destKey), valueSerializeMulti(sets));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoAdd(byte[], org.springframework.data.geo.Point, byte[])
     */
    @Override
    public Long geoAdd(byte[] key, Point point, byte[] member) {

        return convertAndReturn(delegate.geoAdd(key, point, member), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoAdd(byte[], org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation)
     */
    public Long geoAdd(byte[] key, RedisGeoCommands.GeoLocation<byte[]> location) {
        return convertAndReturn(delegate.geoAdd(key, location), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoAdd(java.lang.String, org.springframework.data.geo.Point, java.lang.String)
     */
    @Override
    public Long geoAdd(String key, Point point, String member) {
        return geoAdd(keySerializer.serialize(key), point, keySerializer.serialize(member));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoAdd(java.lang.String, org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation)
     */
    @Override
    public Long geoAdd(String key, RedisGeoCommands.GeoLocation<String> location) {

        Assert.notNull(location, "Location must not be null!");
        return geoAdd(key, location.getPoint(), location.getName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoAdd(byte[], java.util.Map)
     */
    @Override
    public Long geoAdd(byte[] key, Map<byte[], Point> memberCoordinateMap) {
        return convertAndReturn(delegate.geoAdd(key, memberCoordinateMap), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoAdd(byte[], java.lang.Iterable)
     */
    @Override
    public Long geoAdd(byte[] key, Iterable<RedisGeoCommands.GeoLocation<byte[]>> locations) {
        return convertAndReturn(delegate.geoAdd(key, locations), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoAdd(java.lang.String, java.util.Map)
     */
    @Override
    public Long geoAdd(String key, Map<String, Point> memberCoordinateMap) {

        Assert.notNull(memberCoordinateMap, "MemberCoordinateMap must not be null!");

        Map<byte[], Point> byteMap = new HashMap<>();
        for (Map.Entry<String, Point> entry : memberCoordinateMap.entrySet()) {
            byteMap.put(keySerializer.serialize(entry.getKey()), entry.getValue());
        }

        return geoAdd(keySerializer.serialize(key), byteMap);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoAdd(java.lang.String, java.lang.Iterable)
     */
    @Override
    public Long geoAdd(String key, Iterable<RedisGeoCommands.GeoLocation<String>> locations) {

        Assert.notNull(locations, "Locations must not be null!");

        Map<byte[], Point> byteMap = new HashMap<>();
        for (RedisGeoCommands.GeoLocation<String> location : locations) {
            byteMap.put(keySerializer.serialize(location.getName()), location.getPoint());
        }

        return geoAdd(keySerializer.serialize(key), byteMap);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoDist(byte[], byte[], byte[])
     */
    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2) {
        return convertAndReturn(delegate.geoDist(key, member1, member2), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoDist(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Distance geoDist(String key, Object member1, Object member2) {
        return geoDist(keySerializer.serialize(key), valueSerializer.serialize(member1), valueSerializer.serialize(member2));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoDist(byte[], byte[], byte[], org.springframework.data.geo.Metric)
     */
    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2, Metric metric) {
        return convertAndReturn(delegate.geoDist(key, member1, member2, metric), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoDist(java.lang.String, java.lang.String, java.lang.String, org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit)
     */
    @Override
    public Distance geoDist(String key, Object member1, Object member2, Metric metric) {
        return geoDist(keySerializer.serialize(key), valueSerializer.serialize(member1), valueSerializer.serialize(member2), metric);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoHash(byte[], byte[][])
     */
    @Override
    public List<String> geoHash(byte[] key, byte[]... members) {
        return convertAndReturn(delegate.geoHash(key, members), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoHash(java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> geoHash(String key, Object... members) {
        return convertAndReturn(delegate.geoHash(keySerializer.serialize(key), valueSerializeMulti(members)), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoPos(byte[], byte[][])
     */
    @Override
    public List<Point> geoPos(byte[] key, byte[]... members) {
        return convertAndReturn(delegate.geoPos(key, members), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoPos(java.lang.String, java.lang.String[])
     */
    @Override
    public List<Point> geoPos(String key, Object... members) {
        return geoPos(keySerializer.serialize(key), valueSerializeMulti(members));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoRadius(java.lang.String, org.springframework.data.geo.Circle)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius(String key, Circle within) {
        return convertAndReturn(delegate.geoRadius(keySerializer.serialize(key), within), byteGeoResultsToStringGeoResults);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoRadius(java.lang.String, org.springframework.data.geo.Circle, org.springframework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius(String key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args) {
        return convertAndReturn(delegate.geoRadius(keySerializer.serialize(key), within, args), byteGeoResultsToStringGeoResults);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoRadiusByMember(java.lang.String, java.lang.String, double)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadiusByMember(String key, String member, double radius) {
        return geoRadiusByMember(key, member, new Distance(radius, RedisGeoCommands.DistanceUnit.METERS));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoRadiusByMember(java.lang.String, java.lang.String, org.springframework.data.geo.Distance)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadiusByMember(String key, Object member, Distance radius) {

        return convertAndReturn(delegate.geoRadiusByMember(keySerializer.serialize(key), valueSerializer.serialize(member), radius),
            byteGeoResultsToStringGeoResults);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoRadiusByMember(java.lang.String, java.lang.String, org.springframework.data.geo.Distance, org.springframework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadiusByMember(String key, Object member, Distance radius,
                                                                              RedisGeoCommands.GeoRadiusCommandArgs args) {

        return convertAndReturn(delegate.geoRadiusByMember(keySerializer.serialize(key), valueSerializer.serialize(member), radius, args),
            byteGeoResultsToStringGeoResults);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoRadius(byte[], org.springframework.data.geo.Circle)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoRadius(byte[] key, Circle within) {
        return convertAndReturn(delegate.geoRadius(key, within), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoRadius(byte[], org.springframework.data.geo.Circle, org.springframework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoRadius(byte[] key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args) {
        return convertAndReturn(delegate.geoRadius(key, within, args), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoRadiusByMember(byte[], byte[], double)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, double radius) {
        return geoRadiusByMember(key, member, new Distance(radius, RedisGeoCommands.DistanceUnit.METERS));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoRadiusByMember(byte[], byte[], org.springframework.data.geo.Distance)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius) {
        return convertAndReturn(delegate.geoRadiusByMember(key, member, radius), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoRadiusByMember(byte[], byte[], org.springframework.data.geo.Distance, org.springframework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius,
                                                                              RedisGeoCommands.GeoRadiusCommandArgs args) {

        return convertAndReturn(delegate.geoRadiusByMember(key, member, radius, args), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisGeoCommands#geoRemove(byte[], byte[][])
     */
    @Override
    public Long geoRemove(byte[] key, byte[]... members) {
        return zRem(key, members);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#geoRemove(java.lang.String, java.lang.String[])
     */
    @Override
    public Long geoRemove(String key, Object... members) {
        return geoRemove(keySerializer.serialize(key), valueSerializeMulti(members));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnection#closePipeline()
     */
    @Override
    public List<Object> closePipeline() {

        try {
            return convertResults(delegate.closePipeline(), pipelineConverters);
        } finally {
            pipelineConverters.clear();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnection#isPipelined()
     */
    @Override
    public boolean isPipelined() {
        return delegate.isPipelined();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnection#openPipeline()
     */
    @Override
    public void openPipeline() {
        delegate.openPipeline();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#execute(java.lang.String)
     */
    @Override
    public Object execute(String command) {
        return execute(command, (byte[][]) null);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisCommands#execute(java.lang.String, byte[][])
     */
    @Override
    public Object execute(String command, byte[]... args) {
        return convertAndReturn(delegate.execute(command, args), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#execute(java.lang.String, java.lang.String[])
     */
    @Override
    public Object execute(String command, String... args) {
        return execute(command, keySerializeMulti(args));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#pExpire(java.lang.String, long)
     */
    @Override
    public Boolean pExpire(String key, long millis) {
        return pExpire(keySerializer.serialize(key), millis);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#pExpireAt(java.lang.String, long)
     */
    @Override
    public Boolean pExpireAt(String key, long unixTimeInMillis) {
        return pExpireAt(keySerializer.serialize(key), unixTimeInMillis);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#pTtl(java.lang.String)
     */
    @Override
    public Long pTtl(String key) {
        return pTtl(keySerializer.serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#pTtl(java.lang.String, java.util.concurrent.TimeUnit)
     */
    @Override
    public Long pTtl(String key, TimeUnit timeUnit) {
        return pTtl(keySerializer.serialize(key), timeUnit);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#scriptLoad(java.lang.String)
     */
    @Override
    public String scriptLoad(String script) {
        return scriptLoad(keySerializer.serialize(script));
    }

    /**
     * NOTE: This method will not deserialize Strings returned by Lua scripts, as they may not be encoded with the same
     * serializer used here. They will be returned as byte[]s
     */
    public <T> T eval(String script, ReturnType returnType, int numKeys, String... keysAndArgs) {
        return eval(keySerializer.serialize(script), returnType, numKeys, keySerializeMulti(keysAndArgs));
    }

    /**
     * NOTE: This method will not deserialize Strings returned by Lua scripts, as they may not be encoded with the same
     * serializer used here. They will be returned as byte[]s
     */
    public <T> T evalSha(String scriptSha1, ReturnType returnType, int numKeys, String... keysAndArgs) {
        return evalSha(scriptSha1, returnType, numKeys, keySerializeMulti(keysAndArgs));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#time()
     */
    @Override
    public Long time() {
        return convertAndReturn(this.delegate.time(), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#getClientList()
     */
    @Override
    public List<RedisClientInfo> getClientList() {
        return convertAndReturn(this.delegate.getClientList(), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#slaveOf(java.lang.String, int)
     */
    @Override
    public void slaveOf(String host, int port) {
        this.delegate.slaveOf(host, port);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#slaveOfNoOne()
     */
    @Override
    public void slaveOfNoOne() {
        this.delegate.slaveOfNoOne();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisKeyCommands#scan(org.springframework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<byte[]> scan(ScanOptions options) {
        return this.delegate.scan(options);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zScan(byte[], org.springframework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<RedisZSetCommands.Tuple> zScan(byte[] key, ScanOptions options) {
        return this.delegate.zScan(key, options);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisSetCommands#scan(byte[], org.springframework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<byte[]> sScan(byte[] key, ScanOptions options) {
        return this.delegate.sScan(key, options);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHashCommands#hscan(byte[], org.springframework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<Map.Entry<byte[], byte[]>> hScan(byte[] key, ScanOptions options) {
        return this.delegate.hScan(key, options);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#setClientName(java.lang.String)
     */
    @Override
    public void setClientName(byte[] name) {
        this.delegate.setClientName(name);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#setClientName(java.lang.String)
     */
    @Override
    public void setClientName(String name) {
        setClientName(keySerializer.serialize(name));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#killClient(byte[])
     */
    @Override
    public void killClient(String host, int port) {
        this.delegate.killClient(host, port);
    }

    /*
     * @see org.springframework.data.redis.connection.RedisServerCommands#getClientName()
     */
    @Override
    public String getClientName() {
        return convertAndReturn(this.delegate.getClientName(), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#hScan(java.lang.String, org.springframework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<Map.Entry<String, Object>> hScan(String key, ScanOptions options) {

        return new ConvertingCursor<>(this.delegate.hScan(keySerializer.serialize(key), options),
            source -> new Map.Entry<String, Object>() {

                @Override
                public String getKey() {
                    return bytesToString.convert(source.getKey());
                }

                @Override
                public Object getValue() {
                    return bytesToObject.convert(source.getValue());
                }

                @Override
                public String setValue(Object value) {
                    throw new UnsupportedOperationException("Cannot set value for entry in cursor");
                }
            });
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#sScan(java.lang.String, org.springframework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<Object> sScan(String key, ScanOptions options) {
        return new ConvertingCursor<>(this.delegate.sScan(keySerializer.serialize(key), options), bytesToObject);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zScan(java.lang.String, org.springframework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<ObjectRedisConnection.ObjectTuple> zScan(String key, ScanOptions options) {
        return new ConvertingCursor<>(delegate.zScan(keySerializer.serialize(key), options), new DefaultObjectRedisConnection.TupleConverter());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisConnection#getSentinelConnection()
     */
    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return delegate.getSentinelConnection();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeByScore(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Set<Object> zRangeByScore(String key, String min, String max) {
        return convertAndReturn(delegate.zRangeByScore(keySerializer.serialize(key), min, max), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeByScore(java.lang.String, java.lang.String, java.lang.String, long, long)
     */
    @Override
    public Set<Object> zRangeByScore(String key, String min, String max, long offset, long count) {
        return convertAndReturn(delegate.zRangeByScore(keySerializer.serialize(key), min, max, offset, count), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScore(byte[], java.lang.String, java.lang.String)
     */
    @Override
    public Set<byte[]> zRangeByScore(byte[] key, String min, String max) {
        return convertAndReturn(delegate.zRangeByScore(key, min, max), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByScore(byte[], java.lang.String, java.lang.String, long, long)
     */
    @Override
    public Set<byte[]> zRangeByScore(byte[] key, String min, String max, long offset, long count) {
        return convertAndReturn(delegate.zRangeByScore(key, min, max, offset, count), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHyperLogLogCommands#pfAdd(byte[], byte[][])
     */
    @Override
    public Long pfAdd(byte[] key, byte[]... values) {
        return convertAndReturn(delegate.pfAdd(key, values), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#pfAdd(java.lang.String, java.lang.String[])
     */
    @Override
    public Long pfAdd(String key, Object... values) {
        return pfAdd(keySerializer.serialize(key), valueSerializeMulti(values));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHyperLogLogCommands#pfCount(byte[][])
     */
    @Override
    public Long pfCount(byte[]... keys) {
        return convertAndReturn(delegate.pfCount(keys), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#pfCount(java.lang.String[])
     */
    @Override
    public Long pfCount(String... keys) {
        return pfCount(keySerializeMulti(keys));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisHyperLogLogCommands#pfMerge(byte[], byte[][])
     */
    @Override
    public void pfMerge(byte[] destinationKey, byte[]... sourceKeys) {
        delegate.pfMerge(destinationKey, sourceKeys);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#pfMerge(java.lang.String, java.lang.String[][])
     */
    @Override
    public void pfMerge(String destinationKey, String... sourceKeys) {
        this.pfMerge(keySerializer.serialize(destinationKey), keySerializeMulti(sourceKeys));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByLex(byte[])
     */
    @Override
    public Set<byte[]> zRangeByLex(byte[] key) {
        return convertAndReturn(delegate.zRangeByLex(key), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByLex(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<byte[]> zRangeByLex(byte[] key, RedisZSetCommands.Range range) {
        return convertAndReturn(delegate.zRangeByLex(key, range), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisZSetCommands#zRangeByLex(byte[], org.springframework.data.redis.connection.RedisZSetCommands.Range, org.springframework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<byte[]> zRangeByLex(byte[] key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return convertAndReturn(delegate.zRangeByLex(key, range, limit), identityConverter);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeByLex(java.lang.String)
     */
    @Override
    public Set<Object> zRangeByLex(String key) {
        return zRangeByLex(key, RedisZSetCommands.Range.unbounded());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeByLex(java.lang.String, org.springframework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<Object> zRangeByLex(String key, RedisZSetCommands.Range range) {
        return zRangeByLex(key, range, null);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.ObjectRedisConnection#zRangeByLex(java.lang.String, org.springframework.data.redis.connection.RedisZSetCommands.Range, org.springframework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<Object> zRangeByLex(String key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
        return convertAndReturn(delegate.zRangeByLex(keySerializer.serialize(key), range), byteSetToObjectSet);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#migrate(byte[], org.springframework.data.redis.connection.RedisNode, int, org.springframework.data.redis.connection.RedisServerCommands.MigrateOption)
     */
    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, @Nullable RedisServerCommands.MigrateOption option) {
        delegate.migrate(key, target, dbIndex, option);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.RedisServerCommands#migrate(byte[], org.springframework.data.redis.connection.RedisNode, int, org.springframework.data.redis.connection.RedisServerCommands.MigrateOption, long)
     */
    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, @Nullable RedisServerCommands.MigrateOption option, long timeout) {
        delegate.migrate(key, target, dbIndex, option, timeout);
    }

    /**
     * Specifies if pipelined and tx results should be deserialized to Strings. If false, results of
     * {@link #closePipeline()} and {@link #exec()} will be of the type returned by the underlying connection
     *
     * @param deserializePipelineAndTxResults Whether or not to deserialize pipeline and tx results
     */
    public void setDeserializePipelineAndTxResults(boolean deserializePipelineAndTxResults) {
        this.deserializePipelineAndTxResults = deserializePipelineAndTxResults;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T> T convertAndReturn(@Nullable Object value, Converter converter) {

        if (isFutureConversion()) {

            addResultConverter(converter);
            return null;
        }


        return value == null ? null : ObjectUtils.nullSafeEquals(converter, identityConverter) ? (T) value : (T) converter.convert(value);
    }

    private void addResultConverter(Converter<?, ?> converter) {
        if (isQueueing()) {
            txConverters.add(converter);
        } else {
            pipelineConverters.add(converter);
        }
    }

    private boolean isFutureConversion() {
        return isPipelined() || isQueueing();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<Object> convertResults(@Nullable List<Object> results, Queue<Converter> converters) {
        if (!deserializePipelineAndTxResults || results == null) {
            return results;
        }
        if (results.size() != converters.size()) {
            // Some of the commands were done directly on the delegate, don't attempt to convert
            log.warn("Delegate returned an unexpected number of results. Abandoning type conversion.");
            return results;
        }
        List<Object> convertedResults = new ArrayList<>(results.size());
        for (Object result : results) {

            Converter converter = converters.remove();
            convertedResults.add(result == null ? null : converter.convert(result));
        }
        return convertedResults;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.redis.connection.DecoratedRedisConnection#getDelegate()
     */
    @Override
    public RedisConnection getDelegate() {
        return delegate;
    }


    //TODO

    @Override
    public Long hStrLen(byte[] bytes, byte[] bytes1) {
        return null;
    }

    @Override
    public Long exists(byte[]... bytes) {
        return null;
    }

    @Override
    public Long unlink(byte[]... bytes) {
        return null;
    }

    @Override
    public Long touch(byte[]... bytes) {
        return null;
    }

    @Override
    public void restore(byte[] bytes, long l, byte[] bytes1, boolean b) {

    }

    @Override
    public ValueEncoding encodingOf(byte[] bytes) {
        return null;
    }

    @Override
    public Duration idletime(byte[] bytes) {
        return null;
    }

    @Override
    public Long refcount(byte[] bytes) {
        return null;
    }

    @Override
    public List<Long> bitField(byte[] bytes, BitFieldSubCommands bitFieldSubCommands) {
        return null;
    }

    @Override
    public Long bitPos(byte[] bytes, boolean b, org.springframework.data.domain.Range<Long> range) {
        return null;
    }

    @Override
    public Long zUnionStore(byte[] bytes, Aggregate aggregate, Weights weights, byte[]... bytes1) {
        return null;
    }

    @Override
    public Long zInterStore(byte[] bytes, Aggregate aggregate, Weights weights, byte[]... bytes1) {
        return null;
    }
}
