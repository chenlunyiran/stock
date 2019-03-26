package com.twotiger.stock.information.event;

/**
 * 事件订阅器
 * Created by liuqing on 2015/4/2.
 */
public interface EventSubscriber {
    /**
     * 订阅事件
     * @param clazz 事件类型
     * @param eventListener 事件监听器
     * @param <E> 事件
     * @param <D> 事件数据
     */
    <D,E extends Event<D>>  void subscriber(Class<E> clazz, EventListener<D, E> eventListener);
}
