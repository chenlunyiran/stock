package com.twotiger.stock.cache.api;

/**
 * 锁相关API
 */
public interface LockApi {

    /**
     * 申请锁 直到成功 ， expiry 获得的锁时设置的存活时间 使用默认配置
     * @param lockName 锁标识
     */
    void acquire(String lockName);

    /**
     * 申请锁 直到成功
     * @param lockName 锁标识
     * @param expiryTime 获得的锁时设置的存活时间 单位 毫秒
     */
    void acquire(String lockName,long expiryTime);

    /**
     * 申请锁 直到成功
     * @param lockName  锁标识
     * @param requestNo 请求标识 用于判断锁资源占用情况
     */
    void acquire(String lockName,String requestNo);

    /**
     * 申请锁 直到成功
     * @param lockName 锁标识
     * @param requestNo 请求标识 用于判断锁资源占用情况
     * @param expiryTime  获得的锁时设置的存活时间 单位 毫秒
     */
    void acquire(String lockName,String requestNo,long expiryTime);

    /**
     * 指定时间内获取锁 获取成功返回true 失败返回false
     * @param lockName 锁标识
     * @param waitTime 尝试时间   单位 毫秒
     * @return 获取成功返回true 失败返回false
     */
    boolean tryAcquire(String lockName,long waitTime);

    /**
     *指定时间内获取锁 获取成功返回true 失败返回false
     * @param lockName 锁标识
     * @param waitTime 尝试时间 单位 毫秒
     * @param expiryTime 获得的锁时设置的存活时间 单位 毫秒
     * @return 获取成功返回true 失败返回false
     */
    boolean tryAcquire(String lockName,long waitTime,long expiryTime);
    /**
     *指定时间内获取锁 获取成功返回true 失败返回false
     * @param lockName 锁标识
     * @param waitTime 尝试时间 单位 毫秒
     * @param requestNo 请求标识 用于判断锁资源占用情况
     * @return 获取成功返回true 失败返回false
     */
    boolean tryAcquire(String lockName,String requestNo,long waitTime);
    /**
     *指定时间内获取锁 获取成功返回true 失败返回false
     * @param lockName 锁标识
     * @param waitTime 尝试时间 单位 毫秒
     * @param requestNo 请求标识 用于判断锁资源占用情况
     * @param expiryTime 获得的锁时设置的存活时间 单位 毫秒
     * @return 获取成功返回true 失败返回false
     */
    boolean tryAcquire(String lockName,String requestNo,long waitTime,long expiryTime);

    /**
     * 释放锁资源
     * @param lockName 锁标识
     */
    void release(String lockName);

    /**
     * 创建全局锁对象
     * @param lockName 锁标识
     * @param expiryTime 获得的锁时设置的存活时间 单位 毫秒
     * @return
     */
    LockObject createLockObject(String lockName,long expiryTime);

    /**
     * 创建全局锁对象
     * @param lockName 锁标识
     * @return
     */
    LockObject createLockObject(String lockName);

}
