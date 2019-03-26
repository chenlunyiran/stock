package com.twotiger.stock.http;

/**
 * Created by liuqing on 2014/12/24.
 */
public interface ClientDomain extends  WebSocketConnect{
    public <I, O> O doAction(PostAction<I, O> action, I in);

    public <O> O doAction(GetAction<O> action);

    public void addCookie(String key, String value);

    public String getCookie(String key);
}
