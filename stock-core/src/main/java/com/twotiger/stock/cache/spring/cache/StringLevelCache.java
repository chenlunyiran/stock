package com.twotiger.stock.cache.spring.cache;


import com.twotiger.stock.cache.multilevel.LevelCache;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * 1 redis string 实现
 * 2 jcache
 */
public class StringLevelCache implements Cache {

    private final LevelCache levelCache;

    public StringLevelCache(LevelCache levelCache) {
        this.levelCache = levelCache;
    }

    private String buildKeyFromObj(Object key){
        if(key instanceof String){
            return buildKeyFromObj(key);
        }else {
            return String.valueOf(key.hashCode());
        }
    }

    @Override
    public String getName() {
        return levelCache.getLower().name();
    }

    @Override
    public Object getNativeCache() {
        return levelCache;
    }

    @Override
    public ValueWrapper get(Object key) {
        return ()->levelCache.get(buildKeyFromObj(key));
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T)levelCache.get(buildKeyFromObj(key));
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T)levelCache.get(buildKeyFromObj(key),()->{
            try {
                return valueLoader.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void put(Object key, Object value) {
        levelCache.put(buildKeyFromObj(key),value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return ()->levelCache.putIfAbsent(buildKeyFromObj(key),value);
    }

    @Override
    public void evict(Object key) {
        levelCache.remove(buildKeyFromObj(key));
    }


    @Override
    public void clear() {
        levelCache.removeAll();
    }
}
