package com.twotiger.stock.information.mqevent;


import com.twotiger.stock.information.event.AbsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by liuqing-notebook on 2017/2/23.
 */
public class AbsMqEvent<D extends Serializable> extends AbsEvent<D> implements MqEvent<D> {
    public AbsMqEvent(String id, D data) {
        super(id, data);
    }
    public AbsMqEvent(D data) {
        super(UUID.randomUUID().toString().replace("-",""), data);
    }
}
