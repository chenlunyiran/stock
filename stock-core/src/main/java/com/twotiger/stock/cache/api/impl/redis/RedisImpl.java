package com.twotiger.stock.cache.api.impl.redis;

import com.google.common.collect.ImmutableList;
import com.twotiger.stock.cache.api.CacheApi;
import com.twotiger.stock.cache.api.abs.AbsCacheApi;
import com.twotiger.stock.cache.spring.redis.ObjectRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * redis 实现
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/12
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */
public class RedisImpl<V> extends AbsCacheApi<V> implements CacheApi<V> {
    private static final String SetNxOrGetObjectActionScript = "local v = redis.call('get',KEYS[1]);if v then return v;else redis.call('set',KEYS[1],ARGV[1]);return ARGV[1];end";
    private static final String GetAndDelObjectActionScript = "local v=redis.call('get',KEYS[1]);redis.call('del',KEYS[1]);return v;";
    private static final String GetAndReplaceObjectScript = "local v=redis.call('get',KEYS[1]);if v then redis.call('set',KEYS[1],ARGV[1]);end;return v;";
    //key 不存在返回并设置后返回2 ,cas 成功返回1 , 旧值不匹配返回0 .
    private static final String ReplaceNxSetObjectActionScript = "local v=redis.call('get',KEYS[1]);if not v then redis.call('set',KEYS[1],ARGV[2]);return 2;elseif (v==ARGV[1]) then redis.call('set', KEYS[1], ARGV[2]);return 1;else return 0;end";
    private static final String RemoveEqObjectActionScript = "local v=redis.call('get',KEYS[1]);if v and v==ARGV[1] then redis.call('del',KEYS[1]);return 1;else return 0;end";
    private static final RedisScript<Long> ReplaceNxSetObjectAction = RedisScript.of(ReplaceNxSetObjectActionScript, Long.class);
    private static final RedisScript<Long> RemoveEqObjectAction = RedisScript.of(RemoveEqObjectActionScript, Long.class);

    private final ObjectRedisTemplate objectRedisTemplate;
    private final ValueOperations<String, V> valueOperations;
    private final RedisScript<V> SetNxOrGetObjectAction;
    private final RedisScript<V> GetAndDelObjectAction;
    private final RedisScript<V> GetAndReplaceObjectAction;

    public RedisImpl(String cacheName, ObjectRedisTemplate objectRedisTemplate) {
        super(cacheName);
        this.objectRedisTemplate = objectRedisTemplate;
        this.valueOperations = (ValueOperations<String, V>) objectRedisTemplate.opsForValue();
        Class<Object> resultClass = Object.class;
        SetNxOrGetObjectAction = RedisScript.of(SetNxOrGetObjectActionScript, resultClass);
        GetAndDelObjectAction = RedisScript.of(GetAndDelObjectActionScript, resultClass);
        GetAndReplaceObjectAction = RedisScript.of(GetAndReplaceObjectScript, resultClass);
    }

    protected final String realKey(String key) {
        return new StringJoiner("_").add(cacheName).add(key).toString();
    }

    @Override
    public boolean exists(String key) {
        return objectRedisTemplate.hasKey(realKey(key));
    }

    @Override
    public V get(String key) {
        return valueOperations.get(realKey(key));
    }

    @Override
    public V get(String key, Supplier<V> init) {
        V value = get(key);
        if(value==null){
            value = init.get();
            if(value!=null){
                value = objectRedisTemplate.execute(SetNxOrGetObjectAction, ImmutableList.of(realKey(key)), value);
            }
        }
        return value;
    }

    @Override
    public V getAndPut(String key, V newValue) {
        return valueOperations.getAndSet(realKey(key), newValue);
    }

    @Override
    public V getAndRemove(String key) {
        V value = objectRedisTemplate.execute(GetAndDelObjectAction, ImmutableList.of(realKey(key)));
        return value;
    }

    @Override
    public V getAndReplace(String key, V newValue) {
        V value = objectRedisTemplate.execute(GetAndReplaceObjectAction, ImmutableList.of(realKey(key)), newValue);
        return value;
    }

    @Override
    public void put(String key, V value) {
        valueOperations.set(realKey(key), value);
    }

    @Override
    public boolean putIfAbsent(String key, V value) {
        return valueOperations.setIfAbsent(realKey(key), value);
    }

    /**
     * 匹配替换，若key不存在则设置为newValue并返回true，若存在则匹配替换，不匹配才返回false
     * @param key
     * @param oldValue
     * @param newValue
     * @return
     */
    @Override
    public boolean replace(String key, V oldValue, V newValue) {
        return objectRedisTemplate.execute(ReplaceNxSetObjectAction, ImmutableList.of(realKey(key)), oldValue, newValue) > 0;
    }

    @Override
    public boolean remove(String key) {
        return objectRedisTemplate.delete(realKey(key));
    }

    @Override
    public void removeAll() {
        Set<String> keySet = objectRedisTemplate.keys(cacheName + "*");
        if (keySet == null || keySet.isEmpty()) {
            return;
        }
        objectRedisTemplate.delete(keySet);
    }

    @Override
    public boolean remove(String key, V oldValue) {
        return objectRedisTemplate.execute(RemoveEqObjectAction, ImmutableList.of(realKey(key)), oldValue) > 0;
    }
}
