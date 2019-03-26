package com.twotiger.stock.http;

import org.apache.http.HttpEntity;

/**
 * Created by liuqing on 2014/12/24.
 * @param <I>
 * @param <O>
 */
public abstract class PostAction<I,O> extends AbstractAction<O> {
    /**
     * 设置请求信息
     * @param content
     * @param in
     * @return
     */
    public abstract HttpEntity beforeRequest (DomainContent content,I in);
}
