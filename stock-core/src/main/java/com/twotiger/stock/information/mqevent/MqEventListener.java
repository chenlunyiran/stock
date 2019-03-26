package com.twotiger.stock.information.mqevent;


import com.twotiger.stock.information.event.EventListener;

import java.io.Serializable;

/**
 * Created by liuqing-notebook on 2016/2/4.
 */
public interface MqEventListener<D  extends Serializable,E extends MqEvent<D>> extends EventListener<D,E> {

    /**
     * 事件类型  MqEvent 的 fullClassName
     * @return
     */
    String getEventMold();

    /**
     * 监听事件优先级 越大优先级越高
     * @return
     */
    int getLevel();
}
