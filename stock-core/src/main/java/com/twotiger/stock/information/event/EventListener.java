package com.twotiger.stock.information.event;


/**
 * Created by liuqing on 2015/4/2.
 */
public interface EventListener <D ,E extends Event<D>>{

        default void onEvent(E event){
                onEvent(event.getId(),event.getData());
        }

        void onEvent(String id, D eventData);
}
