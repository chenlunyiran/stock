package com.twotiger.stock.information.event.spi;


import com.twotiger.stock.information.event.EventPublisher;

/**
 * Created by liuqing-notebook on 2016/2/3.
 */
public interface EventPublisherServiceProvider {
    /**
     *初始化事件发布器
     * @return
     */
    EventPublisher initPublisher();
}
