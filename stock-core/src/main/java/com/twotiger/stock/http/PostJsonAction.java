package com.twotiger.stock.http;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * Created by liuqing on 2014/12/26.
 * @param <I>
 * @param <O>
 */
public abstract class PostJsonAction<I,O>  extends PostAction<I,O> {
    private static final ContentType contentType = ContentType.APPLICATION_JSON;

    protected abstract void initRequestHeader(DomainContent content,I in);

    protected abstract String convertToJsonString(I in);

    @Override
    public final HttpEntity beforeRequest(DomainContent content, I in) {
        initRequestHeader(content,in);
        return new StringEntity(convertToJsonString(in),contentType);
    }

}
