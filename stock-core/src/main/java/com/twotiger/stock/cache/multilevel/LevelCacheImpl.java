package com.twotiger.stock.cache.multilevel;

import com.twotiger.stock.cache.api.CacheApi;
import com.twotiger.stock.cache.api.abs.AbsCacheApi;

/**
 * 层级缓存
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/22
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */
public class LevelCacheImpl extends AbsCacheApi implements LevelCache{
    private final CacheApi upperCache;

    private final LowerCache lowerCache;

    public LevelCacheImpl(String cacheName, CacheApi upperCache, LowerCache lowerCache) {
        super(cacheName);
        this.upperCache = upperCache;
        this.lowerCache = lowerCache;
    }

    @Override
    public CacheApi getUpper() {
        return upperCache;
    }

    @Override
    public LowerCache getLower() {
        return lowerCache;
    }

}
