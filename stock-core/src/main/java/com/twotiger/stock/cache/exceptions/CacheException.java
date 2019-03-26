package com.twotiger.stock.cache.exceptions;

/**
 * 缓存运行时异常
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/11/22
 * Time: 10:04
 * To change this template use File | Settings | File Templates.
 */
public class CacheException extends RuntimeException {
    public CacheException() {
        super();
    }

    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    protected CacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
