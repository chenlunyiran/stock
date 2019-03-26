package com.twotiger.stock.cache.api;

/**
 * 可过期的缓存对象
 */
public interface ExpireAble {
    /**
     * 获取失效时间
     *
     * @return
     */
    long expiretime();
}
