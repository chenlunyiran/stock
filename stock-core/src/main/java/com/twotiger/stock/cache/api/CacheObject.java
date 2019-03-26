package com.twotiger.stock.cache.api;

import java.util.function.Supplier;

/**
 * 缓存的对象
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/10
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public interface CacheObject<V> {
    /**
     * 判断是否存在
     * @return
     */
    boolean exists();

    /**
     * 获取缓存对象的值
     * @return 从缓存中取得对象 不存在返回null
     */
    V get();

    /**
     * 获取缓存对象
     * @param init 如果不存在初始化的函数
     * @return 从缓存中取得对象 不存在尝试通过init来设置值
     */
    V get(Supplier<V> init);

    /**
     * 获取以前的值并设置新值
     * @param newValue
     * @return 以前的对象 若不存在 返回null
     */
    V getAndPut(V newValue);

    /**
     *获取并移除
     * @return
     */
    V getAndRemove();

    /**
     * 获取并替换
     * @param newValue
     * @return 旧对象或null
     */
    V getAndReplace(V newValue);

    /**
     * 设置缓存对象的值
     * @param value
     */
    void put(V value);

    /**
     * 当不存在时设置缓存对象
     * @param value
     * @return 设置成功true 失败false
     */
    boolean putIfAbsent(V value);

    /**
     * 若匹配则替换
     * @param oldValue
     * @param newValue
     * @return 替换成功true 否则false
     */
    boolean replace(V oldValue, V newValue);

    /**
     * 移除缓存的值
     * @return key存在时true 否则false
     */
    boolean remove();

    /**
     * 若匹配则移除
     * @param oldValue
     * @return 如果匹配返回true 否则false
     */
    boolean remove(Object oldValue);
}
