package com.twotiger.stock.http;


import javax.annotation.concurrent.ThreadSafe;

/**
 * Created by liuqing on 2014/12/24.
 * @param <O>
 */
@ThreadSafe
public abstract class GetAction<O> extends AbstractAction<O> {

    /**
     * 设置请求信息
     * @param content
     */
    public abstract  void beforeRequest (DomainContent content);

    /**
     * 取得get查询字段
     * @return
     */
    public abstract String getQuery();

}
