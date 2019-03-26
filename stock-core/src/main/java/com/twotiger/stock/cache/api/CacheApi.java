package com.twotiger.stock.cache.api;

import java.util.function.Supplier;

/**
 * 缓存接口
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/10
 * Time: 20:34
 * To change this template use File | Settings | File Templates.
 */
public interface CacheApi<V> {

//    /**
//     * 创建缓存对象
//     * @param key
//     * @param valueClass
//     * @param <V>
//     * @return
//     */
//    <V1> CacheObject<V1> getCacheObject(String key,Class<V1> valueClass);


    CacheObject<V> getCacheObject(String key);

//    <T> T get(String key, Class<T> type);

    /**
     * 判断是否存在指定的key
     * @param key
     * @return
     */
    boolean exists(String key);

    /**
     * 获取缓存对象
     * @param key
     * @return
     */
    V get(String key);

    /**
     * 获取缓存对象
     * @param key
     * @param init 如果不存在初始化的函数
     * @return
     */
    V get(String key, Supplier<V> init);

    /**
     * 获取以前的值并设置新值
     * @param key
     * @param newValue
     * @return 以前的对象 若不存在 返回null
     */
    V getAndPut(String key, V newValue);

    /**
     *获取并移除
     * @param key
     * @return
     */
    V getAndRemove(String key);

    /**
     * 获取并替换 (仅当key存在时才替换)
     * if (cache.containsKey(key)) {
     *   V oldValue = cache.get(key);
     *   cache.put(key, value);
     *   return oldValue;
     * } else {
     *   return null;
     * }
     * @param key
     * @param newValue
     * @return 旧对象或null
     */
    V getAndReplace(String key, V newValue);

    /**
     * 设置缓存对象
     * @param key
     * @param value
     */
    void put(String key, V value);

    /**
     * 当不存在时设置缓存对象
     * @param key
     * @param value
     * @return 设置成功true 失败false
     */
    boolean putIfAbsent(String key, V value);

    /**
     * 若匹配则替换
     * @param key
     * @param oldValue
     * @param newValue
     * @return 替换成功true 否则false
     */
    boolean replace(String key, V oldValue, V newValue);

    /**
     * 移除缓存对象
     * @param key
     * @return key存在时true 否则false
     */
    boolean remove(String key);

    /**
     * 存在且匹配才移除缓存 否则返回false
     * @param key
     * @param oldValue
     * @return 如果匹配返回true 否则false
     */
    boolean remove(String key, V oldValue);


    void removeAll();
}
