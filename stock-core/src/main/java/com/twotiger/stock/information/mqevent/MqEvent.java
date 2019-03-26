package com.twotiger.stock.information.mqevent;



import com.twotiger.stock.information.event.Event;

import java.io.Serializable;

/**
 * Created by liuqing-notebook on 2016/2/4.
 */
public interface MqEvent<D extends Serializable> extends Event<D> {
}
