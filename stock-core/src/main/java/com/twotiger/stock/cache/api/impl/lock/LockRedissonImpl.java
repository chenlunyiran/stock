package com.twotiger.stock.cache.api.impl.lock;

import com.twotiger.stock.cache.api.LockApi;
import com.twotiger.stock.cache.api.LockObject;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁 Redisson 的实现
 */
public class LockRedissonImpl implements LockApi {

    private final RedissonClient redisson;

    //默认超时时间 单位毫秒
    static final long DEFAULT_EXPIRY_TIME=30000;//30秒

    public LockRedissonImpl(String host,String port,String password){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port).setPassword(password);
        this.redisson = Redisson.create(config);
    }

    @Override
    public void acquire(String lockName) {
        RLock lock = redisson.getLock(lockName);
        lock.lock(DEFAULT_EXPIRY_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public void acquire(String lockName, long expiryTime) {
        RLock lock = redisson.getLock(lockName);
        lock.lock(expiryTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void acquire(String lockName, String requestNo) {
        acquire(lockName);
    }

    @Override
    public void acquire(String lockName, String requestNo, long expiryTime) {
        acquire(lockName,expiryTime);
    }

    @Override
    public boolean tryAcquire(String lockName, long waitTime) {
        RLock lock = redisson.getLock(lockName);
        try {
            return  lock.tryLock(waitTime,DEFAULT_EXPIRY_TIME, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryAcquire(String lockName, long waitTime, long expiryTime) {
        RLock lock = redisson.getLock(lockName);
        try {
            return  lock.tryLock(waitTime,expiryTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryAcquire(String lockName, String requestNo, long waitTime) {
        return tryAcquire(lockName,waitTime);
    }

    @Override
    public boolean tryAcquire(String lockName, String requestNo, long waitTime, long expiryTime) {
        return tryAcquire(lockName,waitTime,expiryTime);
    }

    @Override
    public void release(String lockName) {
        RLock lock = redisson.getLock(lockName);
        lock.unlock();
    }

    @Override
    public LockObject createLockObject(String lockName, long expiryTime) {
        return new SimpleLockObject(lockName,expiryTime);
    }

    @Override
    public LockObject createLockObject(String lockName) {
        return new SimpleLockObject(lockName);
    }

    final class SimpleLockObject implements LockObject{
        private final String lockName;

        private final long expiryTime;

        SimpleLockObject(String lockName){
            this.lockName=lockName;
            this.expiryTime=DEFAULT_EXPIRY_TIME;
        }

        SimpleLockObject(String lockName, long expiryTime){
            this.lockName=lockName;
            this.expiryTime=expiryTime;
        }


        @Override
        public void lock() {
            acquire(lockName,expiryTime);
        }

        @Override
        public void lock(String requestNo) {
            acquire(lockName,requestNo,expiryTime);
        }

        @Override
        public boolean tryLock(long waitTime) {
            return tryAcquire(lockName,waitTime,expiryTime);
        }

        @Override
        public boolean tryLock(long waitTime, String requestNo) {
            return tryAcquire(lockName,requestNo,waitTime,expiryTime);
        }

        @Override
        public void unLock() {
            release(lockName);
        }
    }
}
