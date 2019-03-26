package com.twotiger.stock.http;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.Future;

/**
 * WebSocket连接器
 * Created by liuqing on 2014/12/30.
 */
public interface WebSocketConnect {
    /**
     * 建立WebSocket连接
     * @param socketHander WebSocket 处理器
     * @param path
     * @return 连接session
     */
    Future<Session> connect(Object socketHander, String path);
}
