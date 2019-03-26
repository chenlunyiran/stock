package com.twotiger.stock.cache.api;

/**
 * 锁对象
 */
public interface LockObject {
    /**
     * 申请锁 直到成功
     */
    void lock();

    /**
     * 申请锁 直到成功
     * @param requestNo 请求标识 用于判断锁资源占用情况
     */
    void lock(String requestNo);

    /**
     *指定时间内尝试获取锁 获取成功返回true 失败返回false
     * @param waitTime 尝试时间 单位 毫秒
     * @return 获取成功返回true 失败返回false
     */
    boolean tryLock(long waitTime);

    /**
     * 指定时间内尝试获取锁 获取成功返回true 失败返回false
     * @param waitTime 尝试时间 单位 毫秒
     * @param requestNo 请求标识 用于判断锁资源占用情况
     */
    boolean tryLock(long waitTime, String requestNo);

    /**
     * 释放锁
     */
    void unLock();
}
