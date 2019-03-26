package com.twotiger.stock.information.mqevent;


import com.twotiger.stock.information.event.EventPublisher;

/**
 * Created by liuqing-notebook on 2016/2/4.
 */
public interface MqEventPublisher<E extends MqEvent> extends EventPublisher<E> {
}
