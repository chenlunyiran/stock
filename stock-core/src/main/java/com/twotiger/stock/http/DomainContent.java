package com.twotiger.stock.http;

/**
 * Created by liuqing on 2014/12/24.
 */
public interface DomainContent {
    /**
     * 设置请求头信息
     * @param key
     * @param value
     */
    void addRequestHeader(String key, String value);
}
