package com.twotiger.stock.information.event.spi;


import com.twotiger.stock.information.event.EventPublisher;
import com.twotiger.stock.information.event.EventSubscriber;

/**
 * Created by liuqing on 2015/4/2.
 */
public interface EventServiceProvider {
    /**
     *初始化事件发布器
     * @return
     */
    EventPublisher initPublisher();

    /**
     * 初始化事件订阅器
     * @return
     */
    EventSubscriber initSubscriber();

    /**
     * 事件响应级别  通知响应时越大越靠前
     * @return
     */
    int level();

}
