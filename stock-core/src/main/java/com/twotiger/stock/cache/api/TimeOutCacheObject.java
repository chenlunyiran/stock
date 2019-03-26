package com.twotiger.stock.cache.api;

/**
 * 有时限的缓存对象
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/10
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public interface TimeOutCacheObject {
    /**
     * 缓存对象并设置缓存时间
     * @param key
     * @param lifeTime 秒
     * @param value
     */
    void put(String key, long lifeTime, Object value);

    /**
     * 重新设置缓存对象的缓存时间
     * @param key
     * @param lifeTime 秒
     * @return 对象存在设置成功 true  对象已经失效 false
     */
    boolean resetTimeOut(String key, long lifeTime);

    /**
     * 当不存在时设置缓存对象并设置缓存时间
     * @param key
     * @param lifeTime 秒
     * @param value
     * @return 设置成功true 失败false
     */
    boolean putIfAbsent(String key, long lifeTime, Object value);
}
