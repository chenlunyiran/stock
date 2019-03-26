/**
 * Twotiger.com Llc.
 * Copyright (c) 2013-2016 All Rights Reserved.
 */
package com.twotiger.stock.util.httpclient;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: TODO(这里描述这个类的作用) 
 * @Author douxy
 * @Date 2016年6月1日 下午3:28:33 
 */
public class TTHttpRequestRetryHandler implements HttpRequestRetryHandler {

    private int retryCount = 3;

    private boolean canRetry = false;

    private Set<Class<? extends IOException>> igrone = new HashSet<Class<? extends IOException>>();

    /**
     * 
     */
    public TTHttpRequestRetryHandler(int retryCount, boolean canRetry) {
        this.retryCount = retryCount;
        this.canRetry = canRetry;
        //遇到此类异常不进行重试
        //igrone.add(SocketTimeoutException.class);
    }

    /** 
     * @see org.apache.http.client.HttpRequestRetryHandler#retryRequest(java.io.IOException, int, org.apache.http.protocol.HttpContext)
     */
    @Override
    public boolean retryRequest(IOException paramIOException, int paramInt,
                                HttpContext paramHttpContext) {
        if (!canRetry) {
            return false;
        }
        if (paramInt > retryCount) {
            return false;
        }
        if (igrone.contains(paramIOException.getClass())) {
            return false;
        }
        for (Class<? extends IOException> clazz : igrone) {
            if (clazz.isInstance(paramIOException)) {
                return false;
            }
        }

        return true;
    }

}
