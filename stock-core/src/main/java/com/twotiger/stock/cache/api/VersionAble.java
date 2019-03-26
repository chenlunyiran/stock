package com.twotiger.stock.cache.api;

/**
 * 可版本控制的缓存对象
 */
public interface VersionAble {
    /**
     * 获取当前版本号
     *
     * @return
     */
    long currentVersion();
}
