package com.twotiger.stock.information.mqevent;



import com.twotiger.stock.util.Reflect;

import java.io.Serializable;

/**
 * Created by liuqing-notebook on 2016/2/5.
 */
public abstract class AbsMqEventListener<D  extends Serializable,E extends MqEvent<D>> implements  MqEventListener<D,E>{

    protected final  Class<E> eventClass = Reflect.Generic.getGenericSupertype(getClass(),1);

    private final String eventMold = Reflect.getFullClassName(eventClass);

    @Override
    public final String getEventMold() {
        return eventMold;
    }
}
