package com.twotiger.stock.cache.spring.redis;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface ObjectRedisConnection extends RedisConnection {

    /**
     * Object-friendly ZSet tuple.
     */
    interface ObjectTuple extends RedisZSetCommands.Tuple {
        Object getValueAsObject();
    }

    /**
     * 'Native' or 'raw' execution of the given command along-side the given arguments. The command is executed as is,
     * with as little 'interpretation' as possible - it is up to the caller to take care of any processing of arguments or
     * the result.
     *
     * @param command Command to execute
     * @param args    Possible command arguments (may be null)
     * @return execution result.
     * @see RedisCommands#execute(String, byte[]...)
     */
    Object execute(String command, String... args);

    /**
     * 'Native' or 'raw' execution of the given command along-side the given arguments. The command is executed as is,
     * with as little 'interpretation' as possible - it is up to the caller to take care of any processing of arguments or
     * the result.
     *
     * @param command Command to execute
     * @return execution result.
     * @see RedisCommands#execute(String, byte[]...)
     */
    Object execute(String command);

    /**
     * Determine if given {@code key} exists.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/exists">Redis Documentation: EXISTS</a>
     * @see RedisKeyCommands#exists(byte[])
     */
    Boolean exists(String key);

    /**
     * Delete given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return The number of keys that were removed.
     * @see <a href="http://redis.io/commands/del">Redis Documentation: DEL</a>
     * @see RedisKeyCommands#del(byte[]...)
     */
    Long del(String... keys);

    /**
     * Determine the type stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/type">Redis Documentation: TYPE</a>
     * @see RedisKeyCommands#type(byte[])
     */
    DataType type(String key);

    /**
     * Find all keys matching the given {@code pattern}.
     *
     * @param pattern must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/keys">Redis Documentation: KEYS</a>
     * @see RedisKeyCommands#keys(byte[])
     */
    Collection<String> keys(String pattern);

    /**
     * Rename key {@code oleName} to {@code newName}.
     *
     * @param oldName must not be {@literal null}.
     * @param newName must not be {@literal null}.
     * @see <a href="http://redis.io/commands/rename">Redis Documentation: RENAME</a>
     * @see RedisKeyCommands#rename(byte[], byte[])
     */
    void rename(String oldName, String newName);

    /**
     * Rename key {@code oleName} to {@code newName} only if {@code newName} does not exist.
     *
     * @param oldName must not be {@literal null}.
     * @param newName must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/renamenx">Redis Documentation: RENAMENX</a>
     * @see RedisKeyCommands#renameNX(byte[], byte[])
     */
    Boolean renameNX(String oldName, String newName);

    /**
     * Set time to live for given {@code key} in seconds.
     *
     * @param key     must not be {@literal null}.
     * @param seconds
     * @return
     * @see <a href="http://redis.io/commands/expire">Redis Documentation: EXPIRE</a>
     * @see RedisKeyCommands#expire(byte[], long)
     */
    Boolean expire(String key, long seconds);

    /**
     * Set time to live for given {@code key} in milliseconds.
     *
     * @param key    must not be {@literal null}.
     * @param millis
     * @return
     * @see <a href="http://redis.io/commands/pexpire">Redis Documentation: PEXPIRE</a>
     * @see RedisKeyCommands#pExpire(byte[], long)
     */
    Boolean pExpire(String key, long millis);

    /**
     * Set the expiration for given {@code key} as a {@literal UNIX} timestamp.
     *
     * @param key      must not be {@literal null}.
     * @param unixTime
     * @return
     * @see <a href="http://redis.io/commands/expireat">Redis Documentation: EXPIREAT</a>
     * @see RedisKeyCommands#expireAt(byte[], long)
     */
    Boolean expireAt(String key, long unixTime);

    /**
     * Set the expiration for given {@code key} as a {@literal UNIX} timestamp in milliseconds.
     *
     * @param key              must not be {@literal null}.
     * @param unixTimeInMillis
     * @return
     * @see <a href="http://redis.io/commands/pexpireat">Redis Documentation: PEXPIREAT</a>
     * @see RedisKeyCommands#pExpireAt(byte[], long)
     */
    Boolean pExpireAt(String key, long unixTimeInMillis);

    /**
     * Remove the expiration from given {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/persist">Redis Documentation: PERSIST</a>
     * @see RedisKeyCommands#persist(byte[])
     */
    Boolean persist(String key);

    /**
     * Move given {@code key} to database with {@code index}.
     *
     * @param key     must not be {@literal null}.
     * @param dbIndex
     * @return
     * @see <a href="http://redis.io/commands/move">Redis Documentation: MOVE</a>
     * @see RedisKeyCommands#move(byte[], int)
     */
    Boolean move(String key, int dbIndex);

    /**
     * Get the time to live for {@code key} in seconds.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/ttl">Redis Documentation: TTL</a>
     * @see RedisKeyCommands#ttl(byte[])
     */
    Long ttl(String key);

    /**
     * Get the time to live for {@code key} in and convert it to the given {@link TimeUnit}.
     *
     * @param key      must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/ttl">Redis Documentation: TTL</a>
     * @see RedisKeyCommands#ttl(byte[], TimeUnit)
     * @since 1.8
     */
    Long ttl(String key, TimeUnit timeUnit);

    /**
     * Get the precise time to live for {@code key} in milliseconds.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/pttl">Redis Documentation: PTTL</a>
     * @see RedisKeyCommands#pTtl(byte[])
     */
    Long pTtl(String key);

    /**
     * Get the precise time to live for {@code key} in and convert it to the given {@link TimeUnit}.
     *
     * @param key      must not be {@literal null}.
     * @param timeUnit must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/pttl">Redis Documentation: PTTL</a>
     * @see RedisKeyCommands#pTtl(byte[], TimeUnit)
     * @since 1.8
     */
    Long pTtl(String key, TimeUnit timeUnit);

    /**
     * Returns {@code message} via server roundtrip.
     *
     * @param message the message to echo.
     * @return
     * @see <a href="http://redis.io/commands/echo">Redis Documentation: ECHO</a>
     * @see RedisConnectionCommands#echo(byte[])
     */
    String echo(String message);

    /**
     * Sort the elements for {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param params must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    List<Object> sort(String key, SortParameters params);

    /**
     * Sort the elements for {@code key} and store result in {@code storeKey}.
     *
     * @param key      must not be {@literal null}.
     * @param params   must not be {@literal null}.
     * @param storeKey must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sort">Redis Documentation: SORT</a>
     */
    Long sort(String key, SortParameters params, String storeKey);

    // -------------------------------------------------------------------------
    // Methods dealing with values/Redis strings
    // -------------------------------------------------------------------------

    /**
     * Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/get">Redis Documentation: GET</a>
     * @see RedisStringCommands#get(byte[])
     */
    Object get(String key);

    /**
     * Set {@code value} of {@code key} and return its old value.
     *
     * @param key   must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/getset">Redis Documentation: GETSET</a>
     * @see RedisStringCommands#getSet(byte[], byte[])
     */
    Object getSet(String key, Object value);

    /**
     * Get multiple {@code keys}. Values are returned in the order of the requested keys.
     *
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/mget">Redis Documentation: MGET</a>
     * @see RedisStringCommands#mGet(byte[]...)
     */
    List<Object> mGet(String... keys);

    /**
     * Set {@code value} for {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @see RedisStringCommands#set(byte[], byte[])
     */
    @Nullable
    Boolean set(String key, Object value);

    /**
     * Set {@code value} for {@code key} applying timeouts from {@code expiration} if set and inserting/updating values
     * depending on {@code option}.
     *
     * @param key        must not be {@literal null}.
     * @param value      must not be {@literal null}.
     * @param expiration can be {@literal null}. Defaulted to {@link Expiration#persistent()}.
     * @param option     can be {@literal null}. Defaulted to {@link RedisStringCommands.SetOption#UPSERT}.
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     * @see RedisStringCommands#set(byte[], byte[], Expiration, RedisStringCommands.SetOption)
     * @since 1.7
     */
    @Nullable
    Boolean set(String key, Object value, Expiration expiration, RedisStringCommands.SetOption option);

    /**
     * Set {@code value} for {@code key}, only if {@code key} does not exist.
     *
     * @param key   must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     * @see RedisStringCommands#setNX(byte[], byte[])
     */
    @Nullable
    Boolean setNX(String key, Object value);

    /**
     * Set the {@code value} and expiration in {@code seconds} for {@code key}.
     *
     * @param key     must not be {@literal null}.
     * @param seconds
     * @param value   must not be {@literal null}.
     * @see <a href="http://redis.io/commands/setex">Redis Documentation: SETEX</a>
     * @see RedisStringCommands#setEx(byte[], long, byte[])
     */
    @Nullable
    Boolean setEx(String key, long seconds, Object value);

    /**
     * Set the {@code value} and expiration in {@code milliseconds} for {@code key}.
     *
     * @param key          must not be {@literal null}.
     * @param milliseconds
     * @param value        must not be {@literal null}.
     * @see <a href="http://redis.io/commands/psetex">Redis Documentation: PSETEX</a>
     * @see RedisStringCommands#pSetEx(byte[], long, byte[])
     * @since 1.3
     */
    @Nullable
    Boolean pSetEx(String key, long milliseconds, Object value);

    /**
     * Set multiple keys to multiple values using key-value pairs provided in {@code tuple}.
     *
     * @param tuple must not be {@literal null}.
     * @see <a href="http://redis.io/commands/mset">Redis Documentation: MSET</a>
     * @see RedisStringCommands#mSet(Map)
     */
    @Nullable
    Boolean mSetString(Map<String, Object> tuple);

    /**
     * Set multiple keys to multiple values using key-value pairs provided in {@code tuple} only if the provided key does
     * not exist.
     *
     * @param tuple must not be {@literal null}.
     * @see <a href="http://redis.io/commands/msetnx">Redis Documentation: MSETNX</a>
     * @see RedisStringCommands#mSetNX(Map)
     */
    Boolean mSetNXString(Map<String, Object> tuple);

    /**
     * Increment an integer value stored as string value of {@code key} by 1.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/incr">Redis Documentation: INCR</a>
     * @see RedisStringCommands#incr(byte[])
     */
    Long incr(String key);

    /**
     * Increment an integer value stored of {@code key} by {@code delta}.
     *
     * @param key   must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/incrby">Redis Documentation: INCRBY</a>
     * @see RedisStringCommands#incrBy(byte[], long)
     */
    Long incrBy(String key, long value);

    /**
     * Increment a floating point number value of {@code key} by {@code delta}.
     *
     * @param key   must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/incrbyfloat">Redis Documentation: INCRBYFLOAT</a>
     * @see RedisStringCommands#incrBy(byte[], double)
     */
    Double incrBy(String key, double value);

    /**
     * Decrement an integer value stored as string value of {@code key} by 1.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/decr">Redis Documentation: DECR</a>
     * @see RedisStringCommands#decr(byte[])
     */
    Long decr(String key);

    /**
     * Decrement an integer value stored as string value of {@code key} by {@code value}.
     *
     * @param key   must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/decrby">Redis Documentation: DECRBY</a>
     * @see RedisStringCommands#decrBy(byte[], long)
     */
    Long decrBy(String key, long value);

    /**
     * Append a {@code value} to {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/append">Redis Documentation: APPEND</a>
     * @see RedisStringCommands#append(byte[], byte[])
     */
    Long append(String key, String value);

    /**
     * Get a substring of value of {@code key} between {@code start} and {@code end}.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/getrange">Redis Documentation: GETRANGE</a>
     * @see RedisStringCommands#getRange(byte[], long, long)
     */
    String getRange(String key, long start, long end);

    /**
     * Overwrite parts of {@code key} starting at the specified {@code offset} with given {@code value}.
     *
     * @param key    must not be {@literal null}.
     * @param value
     * @param offset
     * @see <a href="http://redis.io/commands/setrange">Redis Documentation: SETRANGE</a>
     * @see RedisStringCommands#setRange(byte[], byte[], long)
     */
    void setRange(String key, String value, long offset);

    /**
     * Get the bit value at {@code offset} of value at {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param offset
     * @return
     * @see <a href="http://redis.io/commands/getbit">Redis Documentation: GETBIT</a>
     * @see RedisStringCommands#getBit(byte[], long)
     */
    Boolean getBit(String key, long offset);

    /**
     * Sets the bit at {@code offset} in value stored at {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param offset
     * @param value
     * @return the original bit value stored at {@code offset}.
     * @see <a href="http://redis.io/commands/setbit">Redis Documentation: SETBIT</a>
     * @see RedisStringCommands#setBit(byte[], long, boolean)
     */
    Boolean setBit(String key, long offset, boolean value);

    /**
     * Count the number of set bits (population counting) in value stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/bitcount">Redis Documentation: BITCOUNT</a>
     * @see RedisStringCommands#bitCount(byte[])
     */
    Long bitCount(String key);

    /**
     * Count the number of set bits (population counting) of value stored at {@code key} between {@code start} and
     * {@code end}.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/bitcount">Redis Documentation: BITCOUNT</a>
     * @see RedisStringCommands#bitCount(byte[], long, long)
     */
    Long bitCount(String key, long start, long end);

    /**
     * Perform bitwise operations between strings.
     *
     * @param op          must not be {@literal null}.
     * @param destination must not be {@literal null}.
     * @param keys        must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/bitop">Redis Documentation: BITOP</a>
     * @see RedisStringCommands#bitOp(RedisStringCommands.BitOperation, byte[], byte[]...)
     */
    Long bitOp(RedisStringCommands.BitOperation op, String destination, String... keys);

    /**
     * Get the length of the value stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/strlen">Redis Documentation: STRLEN</a>
     * @see RedisStringCommands#strLen(byte[])
     */
    Long strLen(String key);

    // -------------------------------------------------------------------------
    // Methods dealing with Redis Lists
    // -------------------------------------------------------------------------

    /**
     * Append {@code values} to {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     * @see RedisListCommands#rPush(byte[], byte[]...)
     */
    Long rPush(String key, Object... values);

    /**
     * Prepend {@code values} to {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     * @see RedisListCommands#lPush(byte[], byte[]...)
     */
    Long lPush(String key, Object... values);

    /**
     * Append {@code values} to {@code key} only if the list exists.
     *
     * @param key   must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/rpushx">Redis Documentation: RPUSHX</a>
     * @see RedisListCommands#rPushX(byte[], byte[])
     */
    Long rPushX(String key, Object value);

    /**
     * Prepend {@code values} to {@code key} only if the list exists.
     *
     * @param key   must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lpushx">Redis Documentation: LPUSHX</a>
     * @see RedisListCommands#lPushX(byte[], byte[])
     */
    Long lPushX(String key, Object value);

    /**
     * Get the size of list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/llen">Redis Documentation: LLEN</a>
     * @see RedisListCommands#lLen(byte[])
     */
    Long lLen(String key);

    /**
     * Get elements between {@code start} and {@code end} from list at {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
     * @see RedisListCommands#lRange(byte[], long, long)
     */
    List<Object> lRange(String key, long start, long end);

    /**
     * Trim list at {@code key} to elements between {@code start} and {@code end}.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @see <a href="http://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
     * @see RedisListCommands#lTrim(byte[], long, long)
     */
    void lTrim(String key, long start, long end);

    /**
     * Get element at {@code index} form list at {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param index
     * @return
     * @see <a href="http://redis.io/commands/lindex">Redis Documentation: LINDEX</a>
     * @see RedisListCommands#lIndex(byte[], long)
     */
    Object lIndex(String key, long index);

    /**
     * Insert {@code value} {@link RedisListCommands.Position#BEFORE} or {@link RedisListCommands.Position#AFTER} existing {@code pivot} for {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param where must not be {@literal null}.
     * @param pivot
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/linsert">Redis Documentation: LINSERT</a>
     * @see RedisListCommands#lIndex(byte[], long)
     */
    Long lInsert(String key, RedisListCommands.Position where, Object pivot, Object value);

    /**
     * Set the {@code value} list element at {@code index}.
     *
     * @param key   must not be {@literal null}.
     * @param index
     * @param value
     * @see <a href="http://redis.io/commands/lset">Redis Documentation: LSET</a>
     * @see RedisListCommands#lSet(byte[], long, byte[])
     */
    void lSet(String key, long index, Object value);

    /**
     * Removes the first {@code count} occurrences of {@code value} from the list stored at {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param count
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/lrem">Redis Documentation: LREM</a>
     * @see RedisListCommands#lRem(byte[], long, byte[])
     */
    Long lRem(String key, long count, Object value);

    /**
     * Removes and returns first element in list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/lpop">Redis Documentation: LPOP</a>
     * @see RedisListCommands#lPop(byte[])
     */
    Object lPop(String key);

    /**
     * Removes and returns last element in list stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/rpop">Redis Documentation: RPOP</a>
     * @see RedisListCommands#rPop(byte[])
     */
    Object rPop(String key);

    /**
     * Removes and returns first element from lists stored at {@code keys} (see: {@link #lPop(byte[])}). <br>
     * <b>Blocks connection</b> until element available or {@code timeout} reached.
     *
     * @param timeout
     * @param keys    must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/blpop">Redis Documentation: BLPOP</a>
     * @see RedisListCommands#bLPop(int, byte[]...)
     */
    List<Object> bLPop(int timeout, String... keys);

    /**
     * Removes and returns last element from lists stored at {@code keys} (see: {@link #rPop(byte[])}). <br>
     * <b>Blocks connection</b> until element available or {@code timeout} reached.
     *
     * @param timeout
     * @param keys    must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/brpop">Redis Documentation: BRPOP</a>
     * @see RedisListCommands#bRPop(int, byte[]...)
     */
    List<Object> bRPop(int timeout, String... keys);

    /**
     * Remove the last element from list at {@code srcKey}, append it to {@code dstKey} and return its value.
     *
     * @param srcKey must not be {@literal null}.
     * @param dstKey must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/rpoplpush">Redis Documentation: RPOPLPUSH</a>
     * @see RedisListCommands#rPopLPush(byte[], byte[])
     */
    Object rPopLPush(String srcKey, String dstKey);

    /**
     * Remove the last element from list at {@code srcKey}, append it to {@code dstKey} and return its value (see
     * {@link #rPopLPush(byte[], byte[])}). <br>
     * <b>Blocks connection</b> until element available or {@code timeout} reached.
     *
     * @param timeout
     * @param srcKey  must not be {@literal null}.
     * @param dstKey  must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/brpoplpush">Redis Documentation: BRPOPLPUSH</a>
     * @see RedisListCommands#bRPopLPush(int, byte[], byte[])
     */
    Object bRPopLPush(int timeout, String srcKey, String dstKey);

    // -------------------------------------------------------------------------
    // Methods dealing with Redis Sets
    // -------------------------------------------------------------------------

    /**
     * Add given {@code values} to set at {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/sadd">Redis Documentation: SADD</a>
     * @see RedisSetCommands#sAdd(byte[], byte[]...)
     */
    Long sAdd(String key, Object... values);

    /**
     * Remove given {@code values} from set at {@code key} and return the number of removed elements.
     *
     * @param key    must not be {@literal null}.
     * @param values
     * @return
     * @see <a href="http://redis.io/commands/srem">Redis Documentation: SREM</a>
     * @see RedisSetCommands#sRem(byte[], byte[]...)
     */
    Long sRem(String key, Object... values);

    /**
     * Remove and return a random member from set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/spop">Redis Documentation: SPOP</a>
     * @see RedisSetCommands#sPop(byte[])
     */
    Object sPop(String key);

    /**
     * Remove and return {@code count} random members from set at {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param count the number of random members to return.
     * @return empty {@link List} if {@literal key} does not exist.
     * @see <a href="http://redis.io/commands/spop">Redis Documentation: SPOP</a>
     * @see RedisSetCommands#sPop(byte[], long)
     * @since 2.0
     */
    List<Object> sPop(String key, long count);

    /**
     * Move {@code value} from {@code srcKey} to {@code destKey}
     *
     * @param srcKey  must not be {@literal null}.
     * @param destKey must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/smove">Redis Documentation: SMOVE</a>
     * @see RedisSetCommands#sMove(byte[], byte[], byte[])
     */
    Boolean sMove(String srcKey, String destKey, Object value);

    /**
     * Get size of set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/scard">Redis Documentation: SCARD</a>
     * @see RedisSetCommands#sCard(byte[])
     */
    Long sCard(String key);

    /**
     * Check if set at {@code key} contains {@code value}.
     *
     * @param key   must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/sismember">Redis Documentation: SISMEMBER</a>
     * @see RedisSetCommands#sIsMember(byte[], byte[])
     */
    Boolean sIsMember(String key, Object value);

    /**
     * Returns the members intersecting all given sets at {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sinter">Redis Documentation: SINTER</a>
     * @see RedisSetCommands#sInter(byte[]...)
     */
    Set<Object> sInter(String... keys);

    /**
     * Intersect all given sets at {@code keys} and store result in {@code destKey}.
     *
     * @param destKey must not be {@literal null}.
     * @param keys    must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sinterstore">Redis Documentation: SINTERSTORE</a>
     * @see RedisSetCommands#sInterStore(byte[], byte[]...)
     */
    Long sInterStore(String destKey, String... keys);

    /**
     * Union all sets at given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sunion">Redis Documentation: SUNION</a>
     * @see RedisSetCommands#sUnion(byte[]...)
     */
    Set<Object> sUnion(String... keys);

    /**
     * Union all sets at given {@code keys} and store result in {@code destKey}.
     *
     * @param destKey must not be {@literal null}.
     * @param keys    must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sunionstore">Redis Documentation: SUNIONSTORE</a>
     * @see RedisSetCommands#sUnionStore(byte[], byte[]...)
     */
    Long sUnionStore(String destKey, String... keys);

    /**
     * Diff all sets for given {@code keys}.
     *
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sdiff">Redis Documentation: SDIFF</a>
     * @see RedisSetCommands#sDiff(byte[]...)
     */
    Set<Object> sDiff(String... keys);

    /**
     * Diff all sets for given {@code keys} and store result in {@code destKey}.
     *
     * @param destKey must not be {@literal null}.
     * @param keys    must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/sdiffstore">Redis Documentation: SDIFFSTORE</a>
     * @see RedisSetCommands#sDiffStore(byte[], byte[]...)
     */
    Long sDiffStore(String destKey, String... keys);

    /**
     * Get all elements of set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/smembers">Redis Documentation: SMEMBERS</a>
     * @see RedisSetCommands#sMembers(byte[])
     */
    Set<Object> sMembers(String key);

    /**
     * Get random element from set at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
     * @see RedisSetCommands#sRandMember(byte[])
     */
    Object sRandMember(String key);

    /**
     * Get {@code count} random elements from set at {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param count
     * @return
     * @see <a href="http://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
     * @see RedisSetCommands#sRem(byte[], byte[]...)
     */
    List<Object> sRandMember(String key, long count);

    /**
     * Use a {@link Cursor} to iterate over elements in set at {@code key}.
     *
     * @param key     must not be {@literal null}.
     * @param options must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/scan">Redis Documentation: SCAN</a>
     * @see RedisSetCommands#sScan(byte[], ScanOptions)
     * @since 1.4
     */
    Cursor<Object> sScan(String key, ScanOptions options);

    // -------------------------------------------------------------------------
    // Methods dealing with Redis Sorted Sets
    // -------------------------------------------------------------------------

    /**
     * Add {@code value} to a sorted set at {@code key}, or update its {@code score} if it already exists.
     *
     * @param key   must not be {@literal null}.
     * @param score the score.
     * @param value the value.
     * @return
     * @see <a href="http://redis.io/commands/zadd">Redis Documentation: ZADD</a>
     * @see RedisZSetCommands#zAdd(byte[], double, byte[])
     */
    Boolean zAdd(String key, double score, Object value);

    /**
     * Add {@code tuples} to a sorted set at {@code key}, or update its {@code score} if it already exists.
     *
     * @param key    must not be {@literal null}.
     * @param tuples the tuples.
     * @return
     * @see <a href="http://redis.io/commands/zadd">Redis Documentation: ZADD</a>
     * @see RedisZSetCommands#zAdd(byte[], Set)
     */
    Long zAdd(String key, Set<ObjectTuple> tuples);

    /**
     * Remove {@code values} from sorted set. Return number of removed elements.
     *
     * @param key    must not be {@literal null}.
     * @param values must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zrem">Redis Documentation: ZREM</a>
     * @see RedisZSetCommands#zRem(byte[], byte[]...)
     */
    Long zRem(String key, Object... values);

    /**
     * Increment the score of element with {@code value} in sorted set by {@code increment}.
     *
     * @param key       must not be {@literal null}.
     * @param increment
     * @param value     the value.
     * @return
     * @see <a href="http://redis.io/commands/zincrby">Redis Documentation: ZINCRBY</a>
     * @see RedisZSetCommands#zIncrBy(byte[], double, byte[])
     */
    Double zIncrBy(String key, double increment, Object value);

    /**
     * Determine the index of element with {@code value} in a sorted set.
     *
     * @param key   must not be {@literal null}.
     * @param value the value.
     * @return
     * @see <a href="http://redis.io/commands/zrank">Redis Documentation: ZRANK</a>
     * @see RedisZSetCommands#zRank(byte[], byte[])
     */
    Long zRank(String key, Object value);

    /**
     * Determine the index of element with {@code value} in a sorted set when scored high to low.
     *
     * @param key   must not be {@literal null}.
     * @param value the value.
     * @return
     * @see <a href="http://redis.io/commands/zrevrank">Redis Documentation: ZREVRANK</a>
     * @see RedisZSetCommands#zRevRank(byte[], byte[])
     */
    Long zRevRank(String key, Object value);

    /**
     * Get elements between {@code start} and {@code end} from sorted set.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
     * @see RedisZSetCommands#zRange(byte[], long, long)
     */
    Set<Object> zRange(String key, long start, long end);

    /**
     * Get set of {@link RedisZSetCommands.Tuple}s between {@code start} and {@code end} from sorted set.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
     * @see RedisZSetCommands#zRangeWithScores(byte[], long, long)
     */
    Set<ObjectTuple> zRangeWithScores(String key, long start, long end);

    /**
     * Get elements where score is between {@code min} and {@code max} from sorted set.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return
     * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRangeByScore(byte[], double, double)
     */
    Set<Object> zRangeByScore(String key, double min, double max);

    /**
     * Get set of {@link RedisZSetCommands.Tuple}s where score is between {@code min} and {@code max} from sorted set.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return
     * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRangeByScoreWithScores(byte[], double, double)
     */
    Set<ObjectTuple> zRangeByScoreWithScores(String key, double min, double max);

    /**
     * Get elements in range from {@code start} to {@code end} where score is between {@code min} and {@code max} from
     * sorted set.
     *
     * @param key    must not be {@literal null}.
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRangeByScore(byte[], double, double, long, long)
     */
    Set<Object> zRangeByScore(String key, double min, double max, long offset, long count);

    /**
     * Get set of {@link RedisZSetCommands.Tuple}s in range from {@code start} to {@code end} where score is between {@code min} and
     * {@code max} from sorted set.
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRangeByScoreWithScores(byte[], double, double, long, long)
     */
    Set<ObjectTuple> zRangeByScoreWithScores(String key, double min, double max, long offset, long count);

    /**
     * Get elements in range from {@code start} to {@code end} from sorted set ordered from high to low.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
     * @see RedisZSetCommands#zRevRange(byte[], long, long)
     */
    Set<Object> zRevRange(String key, long start, long end);

    /**
     * Get set of {@link RedisZSetCommands.Tuple}s in range from {@code start} to {@code end} from sorted set ordered from high to low.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
     * @see RedisZSetCommands#zRevRangeWithScores(byte[], long, long)
     */
    Set<ObjectTuple> zRevRangeWithScores(String key, long start, long end);

    /**
     * Get elements where score is between {@code min} and {@code max} from sorted set ordered from high to low.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return
     * @see <a href="http://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
     * @see RedisZSetCommands#zRevRangeByScore(byte[], double, double)
     */
    Set<Object> zRevRangeByScore(String key, double min, double max);

    /**
     * Get set of {@link RedisZSetCommands.Tuple} where score is between {@code min} and {@code max} from sorted set ordered from high to
     * low.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return
     * @see <a href="http://redis.io/commands/zrevrangebyscore">Redis Documentation: ZREVRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRevRangeByScoreWithScores(byte[], double, double)
     */
    Set<ObjectTuple> zRevRangeByScoreWithScores(String key, double min, double max);

    /**
     * Get elements in range from {@code start} to {@code end} where score is between {@code min} and {@code max} from
     * sorted set ordered high -> low.
     *
     * @param key    must not be {@literal null}.
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     * @see <a href="http://redis.io/commands/zrevrangebyscore">Redis Documentation: ZREVRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRevRangeByScore(byte[], double, double, long, long)
     */
    Set<Object> zRevRangeByScore(String key, double min, double max, long offset, long count);

    /**
     * Get set of {@link RedisZSetCommands.Tuple} in range from {@code start} to {@code end} where score is between {@code min} and
     * {@code max} from sorted set ordered high -> low.
     *
     * @param key    must not be {@literal null}.
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     * @see <a href="http://redis.io/commands/zrevrangebyscore">Redis Documentation: ZREVRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRevRangeByScoreWithScores(byte[], double, double, long, long)
     */
    Set<ObjectTuple> zRevRangeByScoreWithScores(String key, double min, double max, long offset, long count);

    /**
     * Count number of elements within sorted set with scores between {@code min} and {@code max}.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return
     * @see <a href="http://redis.io/commands/zcount">Redis Documentation: ZCOUNT</a>
     * @see RedisZSetCommands#zCount(byte[], double, double)
     */
    Long zCount(String key, double min, double max);

    /**
     * Get the size of sorted set with {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zcard">Redis Documentation: ZCARD</a>
     * @see RedisZSetCommands#zCard(byte[])
     */
    Long zCard(String key);

    /**
     * Get the score of element with {@code value} from sorted set with key {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param value the value.
     * @return
     * @see <a href="http://redis.io/commands/zscore">Redis Documentation: ZSCORE</a>
     * @see RedisZSetCommands#zScore(byte[], byte[])
     */
    Double zScore(String key, Object value);

    /**
     * Remove elements in range between {@code start} and {@code end} from sorted set with {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param start
     * @param end
     * @return
     * @see <a href="http://redis.io/commands/zremrangebyrank">Redis Documentation: ZREMRANGEBYRANK</a>
     * @see RedisZSetCommands#zRemRange(byte[], long, long)
     */
    Long zRemRange(String key, long start, long end);

    /**
     * Remove elements with scores between {@code min} and {@code max} from sorted set with {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return
     * @see <a href="http://redis.io/commands/zremrangebyscore">Redis Documentation: ZREMRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRemRangeByScore(byte[], double, double)
     */
    Long zRemRangeByScore(String key, double min, double max);

    /**
     * Union sorted {@code sets} and store result in destination {@code key}.
     *
     * @param destKey must not be {@literal null}.
     * @param sets    must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
     * @see RedisZSetCommands#zUnionStore(byte[], byte[]...)
     */
    Long zUnionStore(String destKey, Object... sets);

    /**
     * Union sorted {@code sets} and store result in destination {@code key}.
     *
     * @param destKey   must not be {@literal null}.
     * @param aggregate must not be {@literal null}.
     * @param weights
     * @param sets      must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zunionstore">Redis Documentation: ZUNIONSTORE</a>
     * @see RedisZSetCommands#zUnionStore(byte[], RedisZSetCommands.Aggregate, int[], byte[]...)
     */
    Long zUnionStore(String destKey, RedisZSetCommands.Aggregate aggregate, int[] weights, Object... sets);

    /**
     * Intersect sorted {@code sets} and store result in destination {@code key}.
     *
     * @param destKey must not be {@literal null}.
     * @param sets    must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
     * @see RedisZSetCommands#zInterStore(byte[], byte[]...)
     */
    Long zInterStore(String destKey, Object... sets);

    /**
     * Intersect sorted {@code sets} and store result in destination {@code key}.
     *
     * @param destKey   must not be {@literal null}.
     * @param aggregate must not be {@literal null}.
     * @param weights
     * @param sets      must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zinterstore">Redis Documentation: ZINTERSTORE</a>
     * @see RedisZSetCommands#zInterStore(byte[], RedisZSetCommands.Aggregate, int[], byte[]...)
     */
    Long zInterStore(String destKey, RedisZSetCommands.Aggregate aggregate, int[] weights, Object... sets);

    /**
     * Use a {@link Cursor} to iterate over elements in sorted set at {@code key}.
     *
     * @param key     must not be {@literal null}.
     * @param options must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zscan">Redis Documentation: ZSCAN</a>
     * @see RedisZSetCommands#zScan(byte[], ScanOptions)
     * @since 1.4
     */
    Cursor<ObjectTuple> zScan(String key, ScanOptions options);

    /**
     * Get elements where score is between {@code min} and {@code max} from sorted set.
     *
     * @param key must not be {@literal null}.
     * @param min must not be {@literal null}.
     * @param max must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRangeByScore(byte[], String, String)
     * @since 1.5
     */
    Set<Object> zRangeByScore(String key, String min, String max);

    /**
     * Get elements in range from {@code start} to {@code end} where score is between {@code min} and {@code max} from
     * sorted set.
     *
     * @param key    must not be {@literal null}.
     * @param min    must not be {@literal null}.
     * @param max    must not be {@literal null}.
     * @param offset
     * @param count
     * @return
     * @see <a href="http://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     * @see RedisZSetCommands#zRangeByScore(byte[], double, double, long, long)
     * @since 1.5
     */
    Set<Object> zRangeByScore(String key, String min, String max, long offset, long count);

    /**
     * Get all the elements in the sorted set at {@literal key} in lexicographical ordering.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zrangebylex">Redis Documentation: ZRANGEBYLEX</a>
     * @see RedisZSetCommands#zRangeByLex(byte[])
     * @since 1.6
     */
    Set<Object> zRangeByLex(String key);

    /**
     * Get all the elements in {@link RedisZSetCommands.Range} from the sorted set at {@literal key} in lexicographical ordering.
     *
     * @param key   must not be {@literal null}.
     * @param range must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zrangebylex">Redis Documentation: ZRANGEBYLEX</a>
     * @see RedisZSetCommands#zRangeByLex(byte[], RedisZSetCommands.Range)
     * @since 1.6
     */
    Set<Object> zRangeByLex(String key, RedisZSetCommands.Range range);

    /**
     * Get all the elements in {@link RedisZSetCommands.Range} from the sorted set at {@literal key} in lexicographical ordering. Result is
     * limited via {@link RedisZSetCommands.Limit}.
     *
     * @param key   must not be {@literal null}.
     * @param range must not be {@literal null}.
     * @param range can be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/zrangebylex">Redis Documentation: ZRANGEBYLEX</a>
     * @see RedisZSetCommands#zRangeByLex(byte[], RedisZSetCommands.Range, RedisZSetCommands.Limit)
     * @since 1.6
     */
    Set<Object> zRangeByLex(String key, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit);

    // -------------------------------------------------------------------------
    // Methods dealing with Redis Hashes
    // -------------------------------------------------------------------------

    /**
     * Set the {@code value} of a hash {@code field}.
     *
     * @param key   must not be {@literal null}.
     * @param field must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/hset">Redis Documentation: HSET</a>
     * @see RedisHashCommands#hSet(byte[], byte[], byte[])
     */
    Boolean hSet(String key, String field, Object value);

    /**
     * Set the {@code value} of a hash {@code field} only if {@code field} does not exist.
     *
     * @param key   must not be {@literal null}.
     * @param field must not be {@literal null}.
     * @param value
     * @return
     * @see <a href="http://redis.io/commands/hsetnx">Redis Documentation: HSETNX</a>
     * @see RedisHashCommands#hSetNX(byte[], byte[], byte[])
     */
    Boolean hSetNX(String key, String field, Object value);

    /**
     * Get value for given {@code field} from hash at {@code key}.
     *
     * @param key   must not be {@literal null}.
     * @param field must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/hget">Redis Documentation: HGET</a>
     * @see RedisHashCommands#hGet(byte[], byte[])
     */
    Object hGet(String key, String field);

    /**
     * Get values for given {@code fields} from hash at {@code key}.
     *
     * @param key    must not be {@literal null}.
     * @param fields must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/hmget">Redis Documentation: HMGET</a>
     * @see RedisHashCommands#hMGet(byte[], byte[]...)
     */
    List<Object> hMGet(String key, String... fields);

    /**
     * Set multiple hash fields to multiple values using data provided in {@code hashes}
     *
     * @param key    must not be {@literal null}.
     * @param hashes must not be {@literal null}.
     * @see <a href="http://redis.io/commands/hmset">Redis Documentation: HMSET</a>
     * @see RedisHashCommands#hMGet(byte[], byte[]...)
     */
    void hMSet(String key, Map<String, Object> hashes);

    /**
     * Increment {@code value} of a hash {@code field} by the given {@code delta}.
     *
     * @param key   must not be {@literal null}.
     * @param field must not be {@literal null}.
     * @param delta
     * @return
     * @see <a href="http://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     * @see RedisHashCommands#hIncrBy(byte[], byte[], long)
     */
    Long hIncrBy(String key, String field, long delta);

    /**
     * Increment {@code value} of a hash {@code field} by the given {@code delta}.
     *
     * @param key   must not be {@literal null}.
     * @param field
     * @param delta
     * @return
     * @see <a href="http://redis.io/commands/hincrbyfloat">Redis Documentation: HINCRBYFLOAT</a>
     * @see RedisHashCommands#hIncrBy(byte[], byte[], double)
     */
    Double hIncrBy(String key, String field, double delta);

    /**
     * Determine if given hash {@code field} exists.
     *
     * @param key   must not be {@literal null}.
     * @param field must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/hexits">Redis Documentation: HEXISTS</a>
     * @see RedisHashCommands#hExists(byte[], byte[])
     */
    Boolean hExists(String key, String field);

    /**
     * Delete given hash {@code fields}.
     *
     * @param key    must not be {@literal null}.
     * @param fields must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/hdel">Redis Documentation: HDEL</a>
     * @see RedisHashCommands#hDel(byte[], byte[]...)
     */
    Long hDel(String key, String... fields);

    /**
     * Get size of hash at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/hlen">Redis Documentation: HLEN</a>
     * @see RedisHashCommands#hLen(byte[])
     */
    Long hLen(String key);

    /**
     * Get key set (fields) of hash at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/hkeys">Redis Documentation: HKEYS</a>?
     * @see RedisHashCommands#hKeys(byte[])
     */
    Set<Object> hKeys(String key);

    /**
     * Get entry set (values) of hash at {@code field}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/hvals">Redis Documentation: HVALS</a>
     * @see RedisHashCommands#hVals(byte[])
     */
    List<Object> hVals(String key);

    /**
     * Get entire hash stored at {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/hgetall">Redis Documentation: HGETALL</a>
     * @see RedisHashCommands#hGetAll(byte[])
     */
    Map<String, Object> hGetAll(String key);

    /**
     * Use a {@link Cursor} to iterate over entries in hash at {@code key}.
     *
     * @param key     must not be {@literal null}.
     * @param options must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/hscan">Redis Documentation: HSCAN</a>
     * @see RedisHashCommands#hScan(byte[], ScanOptions)
     * @since 1.4
     */
    Cursor<Map.Entry<String, Object>> hScan(String key, ScanOptions options);

    // -------------------------------------------------------------------------
    // Methods dealing with HyperLogLog
    // -------------------------------------------------------------------------

    /**
     * Adds given {@literal values} to the HyperLogLog stored at given {@literal key}.
     *
     * @param key    must not be {@literal null}.
     * @param values must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/pfadd">Redis Documentation: PFADD</a>
     * @see RedisHyperLogLogCommands#pfAdd(byte[], byte[]...)
     * @since 1.5
     */
    Long pfAdd(String key, Object... values);

    /**
     * Return the approximated cardinality of the structures observed by the HyperLogLog at {@literal key(s)}.
     *
     * @param keys must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/pfcount">Redis Documentation: PFCOUNT</a>
     * @see RedisHyperLogLogCommands#pfCount(byte[]...)
     */
    Long pfCount(String... keys);

    /**
     * Merge N different HyperLogLogs at {@literal sourceKeys} into a single {@literal destinationKey}.
     *
     * @param destinationKey must not be {@literal null}.
     * @param sourceKeys     must not be {@literal null}.
     * @see <a href="http://redis.io/commands/pfmerge">Redis Documentation: PFMERGE</a>
     * @see RedisHyperLogLogCommands#pfMerge(byte[], byte[]...)
     */
    void pfMerge(String destinationKey, String... sourceKeys);

    // -------------------------------------------------------------------------
    // Methods dealing with Redis Geo-Indexes
    // -------------------------------------------------------------------------

    /**
     * Add {@link Point} with given member {@literal name} to {@literal key}.
     *
     * @param key    must not be {@literal null}.
     * @param point  must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @return Number of elements added.
     * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     * @see RedisGeoCommands#geoAdd(byte[], Point, byte[])
     * @since 1.8
     */
    Long geoAdd(String key, Point point, String member);

    /**
     * Add {@link RedisGeoCommands.GeoLocation} to {@literal key}.
     *
     * @param key      must not be {@literal null}.
     * @param location must not be {@literal null}.
     * @return Number of elements added.
     * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     * @see RedisGeoCommands#geoAdd(byte[], RedisGeoCommands.GeoLocation)
     * @since 1.8
     */
    Long geoAdd(String key, RedisGeoCommands.GeoLocation<String> location);

    /**
     * Add {@link Map} of member / {@link Point} pairs to {@literal key}.
     *
     * @param key                 must not be {@literal null}.
     * @param memberCoordinateMap must not be {@literal null}.
     * @return Number of elements added.
     * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     * @see RedisGeoCommands#geoAdd(byte[], Map)
     * @since 1.8
     */
    Long geoAdd(String key, Map<String, Point> memberCoordinateMap);

    /**
     * Add {@link RedisGeoCommands.GeoLocation}s to {@literal key}
     *
     * @param key       must not be {@literal null}.
     * @param locations must not be {@literal null}.
     * @return Number of elements added.
     * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     * @see RedisGeoCommands#geoAdd(byte[], Iterable)
     * @since 1.8
     */
    Long geoAdd(String key, Iterable<GeoLocation<String>> locations);

    /**
     * Get the {@link Distance} between {@literal member1} and {@literal member2}.
     *
     * @param key     must not be {@literal null}.
     * @param member1 must not be {@literal null}.
     * @param member2 must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="http://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
     * @see RedisGeoCommands#geoDist(byte[], byte[], byte[])
     * @since 1.8
     */
    Distance geoDist(String key, Object member1, Object member2);

    /**
     * Get the {@link Distance} between {@literal member1} and {@literal member2} in the given {@link Metric}.
     *
     * @param key     must not be {@literal null}.
     * @param member1 must not be {@literal null}.
     * @param member2 must not be {@literal null}.
     * @param metric  must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="http://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
     * @see RedisGeoCommands#geoDist(byte[], byte[], byte[], Metric)
     * @since 1.8
     */
    Distance geoDist(String key, Object member1, Object member2, Metric metric);

    /**
     * Get geohash representation of the position for one or more {@literal member}s.
     *
     * @param key     must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/geohash">Redis Documentation: GEOHASH</a>
     * @see RedisGeoCommands#geoHash(byte[], byte[]...)
     * @since 1.8
     */
    List<String> geoHash(String key, Object... members);

    /**
     * Get the {@link Point} representation of positions for one or more {@literal member}s.
     *
     * @param key     must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/geopos">Redis Documentation: GEOPOS</a>
     * @see RedisGeoCommands#geoPos(byte[], byte[]...)
     * @since 1.8
     */
    List<Point> geoPos(String key, Object... members);

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle}.
     *
     * @param key    must not be {@literal null}.
     * @param within must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     * @see RedisGeoCommands#geoRadius(byte[], Circle)
     * @since 1.8
     */
    GeoResults<GeoLocation<String>> geoRadius(String key, Circle within);

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle} applying {@link RedisGeoCommands.GeoRadiusCommandArgs}.
     *
     * @param key    must not be {@literal null}.
     * @param within must not be {@literal null}.
     * @param args   must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     * @see RedisGeoCommands#geoRadius(byte[], Circle, RedisGeoCommands.GeoRadiusCommandArgs)
     * @since 1.8
     */
    GeoResults<GeoLocation<String>> geoRadius(String key, Circle within, RedisGeoCommands.GeoRadiusCommandArgs args);

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius}.
     *
     * @param key    must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @param radius
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     * @see RedisGeoCommands#geoRadiusByMember(byte[], byte[], double)
     * @since 1.8
     */
    GeoResults<GeoLocation<String>> geoRadiusByMember(String key, String member, double radius);

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@link Distance}.
     *
     * @param key    must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @param radius must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     * @see RedisGeoCommands#geoRadiusByMember(byte[], byte[], Distance)
     * @since 1.8
     */
    GeoResults<GeoLocation<String>> geoRadiusByMember(String key, Object member, Distance radius);

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@link Distance} and {@link RedisGeoCommands.GeoRadiusCommandArgs}.
     *
     * @param key    must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @param radius must not be {@literal null}.
     * @param args   must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     * @see RedisGeoCommands#geoRadiusByMember(byte[], byte[], Distance, RedisGeoCommands.GeoRadiusCommandArgs)
     * @since 1.8
     */
    GeoResults<GeoLocation<String>> geoRadiusByMember(String key, Object member, Distance radius,
                                                      RedisGeoCommands.GeoRadiusCommandArgs args);

    /**
     * Remove the {@literal member}s.
     *
     * @param key     must not be {@literal null}.
     * @param members must not be {@literal null}.
     * @return Number of members elements removed.
     * @see <a href="http://redis.io/commands/zrem">Redis Documentation: ZREM</a>
     * @see RedisGeoCommands#geoRemove(byte[], byte[]...)
     * @since 1.8
     */
    Long geoRemove(String key, Object... members);

    // -------------------------------------------------------------------------
    // Methods dealing with Redis Pub/Sub
    // -------------------------------------------------------------------------

    /**
     * Publishes the given message to the given channel.
     *
     * @param channel the channel to publish to, must not be {@literal null}.
     * @param message message to publish
     * @return the number of clients that received the message
     * @see <a href="http://redis.io/commands/publish">Redis Documentation: PUBLISH</a>
     * @see RedisPubSubCommands#publish(byte[], byte[])
     */
    Long publish(String channel, String message);

    /**
     * Subscribes the connection to the given channels. Once subscribed, a connection enters listening mode and can only
     * subscribe to other channels or unsubscribe. No other commands are accepted until the connection is unsubscribed.
     * <p>
     * Note that this operation is blocking and the current thread starts waiting for new messages immediately.
     *
     * @param listener message listener, must not be {@literal null}.
     * @param channels channel names, must not be {@literal null}.
     * @see <a href="http://redis.io/commands/subscribe">Redis Documentation: SUBSCRIBE</a>
     * @see RedisPubSubCommands#subscribe(MessageListener, byte[]...)
     */
    void subscribe(MessageListener listener, String... channels);

    /**
     * Subscribes the connection to all channels matching the given patterns. Once subscribed, a connection enters
     * listening mode and can only subscribe to other channels or unsubscribe. No other commands are accepted until the
     * connection is unsubscribed.
     * <p>
     * Note that this operation is blocking and the current thread starts waiting for new messages immediately.
     *
     * @param listener message listener, must not be {@literal null}.
     * @param patterns channel name patterns, must not be {@literal null}.
     * @see <a href="http://redis.io/commands/psubscribe">Redis Documentation: PSUBSCRIBE</a>
     * @see RedisPubSubCommands#pSubscribe(MessageListener, byte[]...)
     */
    void pSubscribe(MessageListener listener, String... patterns);

    // -------------------------------------------------------------------------
    // Methods dealing with Redis Lua Scripting
    // -------------------------------------------------------------------------

    /**
     * Load lua script into scripts cache, without executing it.<br>
     * Execute the script by calling {@link #evalSha(byte[], ReturnType, int, byte[]...)}.
     *
     * @param script must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/script-load">Redis Documentation: SCRIPT LOAD</a>
     * @see RedisScriptingCommands#scriptLoad(byte[])
     */
    String scriptLoad(String script);

    /**
     * Evaluate given {@code script}.
     *
     * @param script      must not be {@literal null}.
     * @param returnType  must not be {@literal null}.
     * @param numKeys
     * @param keysAndArgs must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/eval">Redis Documentation: EVAL</a>
     * @see RedisScriptingCommands#eval(byte[], ReturnType, int, byte[]...)
     */
    <T> T eval(String script, ReturnType returnType, int numKeys, String... keysAndArgs);

    /**
     * Evaluate given {@code scriptSha}.
     *
     * @param scriptSha   must not be {@literal null}.
     * @param returnType  must not be {@literal null}.
     * @param numKeys
     * @param keysAndArgs must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/evalsha">Redis Documentation: EVALSHA</a>
     * @see RedisScriptingCommands#evalSha(String, ReturnType, int, byte[]...)
     */
    <T> T evalSha(String scriptSha, ReturnType returnType, int numKeys, String... keysAndArgs);

    /**
     * Assign given name to current connection.
     *
     * @param name
     * @see <a href="http://redis.io/commands/client-setname">Redis Documentation: CLIENT SETNAME</a>
     * @see RedisServerCommands#setClientName(byte[])
     * @since 1.3
     */
    void setClientName(String name);

    /**
     * Request information and statistics about connected clients.
     *
     * @return {@link List} of {@link RedisClientInfo} objects.
     * @see <a href="http://redis.io/commands/client-list">Redis Documentation: CLIENT LIST</a>
     * @see RedisServerCommands#getClientList()
     * @since 1.3
     */
    List<RedisClientInfo> getClientList();

}
