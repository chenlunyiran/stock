package com.twotiger.stock.information.event;


/**
 * 事件发布器
 * Created by liuqing on 2015/4/1.
 */
public interface EventPublisher<E extends Event>{

    /**
     * 发布事件
     * @param event
     */
    void publish(E event);

}
