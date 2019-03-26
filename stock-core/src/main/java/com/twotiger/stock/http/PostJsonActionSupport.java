package com.twotiger.stock.http;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by liuqing on 2014/12/29.
 * @param <I>
 * @param <O>
 */
public abstract class PostJsonActionSupport<I,O> extends PostJsonAction<I,O>{

    private static final Logger LOGGER = LoggerFactory.getLogger(PostJsonActionSupport.class);

    //输出参数类型
    private final Class<O> clazz;

    public PostJsonActionSupport(){
        Type[] types = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        clazz = (Class<O>) types[1];
    }

    @Override
    protected abstract void initRequestHeader(DomainContent content, I in);

    @Override
    protected String convertToJsonString(I in) {
        return JSON.toJSONString(in);
    }

    @Override
    public O afterResponse(HttpResponse httpResponse) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            httpResponse.getEntity().writeTo(byteArrayOutputStream);
        } catch (IOException e) {
            LOGGER.error("读取httpResponse IO 异常!",e);
        }
        return JSON.parseObject(byteArrayOutputStream.toByteArray(),clazz);
    }
}
