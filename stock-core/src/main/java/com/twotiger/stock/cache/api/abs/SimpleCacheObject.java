package com.twotiger.stock.cache.api.abs;

import com.twotiger.stock.cache.api.CacheApi;
import com.twotiger.stock.cache.api.CacheObject;

import java.util.function.Supplier;

/**
 * 缓存对象实现
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/12
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
class SimpleCacheObject<V> implements CacheObject<V>{

    private final String key;

    private final CacheApi cacheApi;

    SimpleCacheObject(CacheApi cacheApi,String key){
        this.cacheApi=cacheApi;
        this.key = key;
    }

    @Override
    public boolean exists() {
        return cacheApi.exists(key);
    }

    @Override
    public V get() {
        return (V)cacheApi.get(key);
    }

    @Override
    public V get(Supplier<V> init) {
        return (V)cacheApi.get(key,init);
    }

    @Override
    public V getAndPut(V newValue) {
        return (V)cacheApi.getAndPut(key,newValue);
    }

    @Override
    public V getAndRemove() {
        return (V)cacheApi.getAndRemove(key);
    }

    @Override
    public V getAndReplace(V newValue) {
        return (V)cacheApi.getAndReplace(key,newValue);
    }

    @Override
    public void put(V value) {
        cacheApi.put(key,value);
    }

    @Override
    public boolean putIfAbsent(V value) {
        return cacheApi.putIfAbsent(key,value);
    }

    @Override
    public boolean replace(V oldValue, V newValue) {
        return cacheApi.replace(key,oldValue,newValue);
    }

    @Override
    public boolean remove() {
        return cacheApi.remove(key);
    }

    @Override
    public boolean remove(Object oldValue) {
        return cacheApi.remove(key,oldValue);
    }
}
