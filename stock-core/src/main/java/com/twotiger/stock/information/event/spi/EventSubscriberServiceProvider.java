package com.twotiger.stock.information.event.spi;


import com.twotiger.stock.information.event.EventSubscriber;

/**
 * Created by liuqing-notebook on 2016/2/3.
 */
public interface EventSubscriberServiceProvider {
    /**
     * 初始化事件订阅器
     * @return
     */
    EventSubscriber initSubscriber();
}
