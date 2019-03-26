package com.twotiger.stock.cache.api.abs;

import com.twotiger.stock.cache.api.CacheApi;
import com.twotiger.stock.cache.api.CacheObject;

/**
 * 抽象CacheApi  简单实现create方法
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/12
 * Time: 21:48
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbsCacheApi<V> implements CacheApi<V> {
    public AbsCacheApi(String cacheName) {
        this.cacheName = cacheName;
    }

    protected final String cacheName;

    @Override
    public CacheObject<V> getCacheObject(String key) {
        return new SimpleCacheObject<V>(this,key);
    }
}
