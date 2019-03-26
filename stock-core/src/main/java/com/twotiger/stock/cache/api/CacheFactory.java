package com.twotiger.stock.cache.api;


public interface CacheFactory {

    <V> CacheApi<V> createCache(String cacheName, Class<V> valueType);

    CacheApi<Object> createCache(String cacheName);

    Iterable<String> cacheNames();
}
