package com.twotiger.stock.http;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by liuqing on 2014/12/24.
 */
@ThreadSafe
public abstract class AbstractAction<O> implements Action<O> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractAction.class);

    protected   byte[] getResponseBody(HttpResponse httpResponse){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            httpResponse.getEntity().writeTo(byteArrayOutputStream);
            return  byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("解析响应数据异常！",e);
        }
        return null;
    }
}
