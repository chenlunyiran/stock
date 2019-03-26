package com.twotiger.stock.cache.api.impl.jcache;

import com.twotiger.stock.cache.api.CacheApi;
import com.twotiger.stock.cache.api.abs.AbsCacheApi;
import org.springframework.beans.factory.DisposableBean;

import javax.cache.Cache;
import java.util.function.Supplier;

/**
 * JCache impl
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/12
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */
public class JCacheImpl<V> extends AbsCacheApi<V> implements CacheApi<V>, DisposableBean {
    private final Cache<String, V> cache;

    public JCacheImpl(Cache<String, V> cache) {
        super(cache.getName());
        this.cache = cache;
    }

    @Override
    public boolean exists(String key) {
        return cache.containsKey(key);
    }

    @Override
    public V get(String key) {
        return cache.get(key);
    }

    @Override
    public V get(String key, Supplier<V> init) {
        V value = get(key);
        if(value==null){
            value = init.get();
            if(value!=null&&!cache.putIfAbsent(key,value)){
                value=get(key);
            }
        }
        return value;
    }

    @Override
    public V getAndPut(String key, V newValue) {
        return cache.getAndPut(key,newValue);
    }

    @Override
    public V getAndRemove(String key) {
        return cache.getAndRemove(key);
    }

    @Override
    public V getAndReplace(String key, V newValue) {
        return cache.getAndReplace(key,newValue);
    }

    @Override
    public void put(String key, V value) {
        cache.put(key,value);
    }

    @Override
    public boolean putIfAbsent(String key, V value) {
        return cache.putIfAbsent(key,value);
    }

    @Override
    public boolean replace(String key, V oldValue, V newValue) {
        return cache.replace(key,oldValue,newValue);
    }

    @Override
    public boolean remove(String key) {
        return cache.remove(key);
    }

    @Override
    public boolean remove(String key, V oldValue) {
        return cache.remove(key,oldValue);
    }

    @Override
    public void removeAll() {
        cache.removeAll();
    }

    @Override
    public void destroy() throws Exception {
        cache.clear();
    }
}
