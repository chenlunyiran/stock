package com.twotiger.stock.http;

import org.apache.http.HttpResponse;

/**
 * http 请求操作
 * Created by liuqing on 2014/12/24.
 * @param <O>
 */
public interface Action<O> {


    /**
     * 设置响应信息
     * @param httpResponse
     * @return
     */
    O afterResponse(HttpResponse httpResponse);

    /**
     * 取得请求路径
     * @return 请求路径
     */
    String getPath();
}
