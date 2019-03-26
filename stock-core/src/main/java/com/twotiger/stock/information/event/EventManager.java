package com.twotiger.stock.information.event;


import com.twotiger.stock.information.event.spi.EventServiceProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 事件管理器
 * Created by liuqing on 2015/4/2.
 */
public final class EventManager {
    private EventManager(){}

    /**
     * 事件发布器列表
     */
    private static final List<EventPublisher> eventPublisherList = new ArrayList<>();

    /**
     * 事件订阅器列表
     */
    private static final List<EventSubscriber> eventSubscriberList = new ArrayList<>();

    static{
        ServiceLoader<EventServiceProvider> serviceLoader = ServiceLoader.load(EventServiceProvider.class);
        Iterator<EventServiceProvider> eventProviders = serviceLoader.iterator();
        List<EventServiceProvider> temp = new ArrayList<>();
        if(eventProviders.hasNext()){
            temp.add(eventProviders.next());
        }
        temp.sort((a,b)->{return a.level()-b.level();});
        for(EventServiceProvider provider:temp){
            eventPublisherList.add(provider.initPublisher());
            eventSubscriberList.add(provider.initSubscriber());
        }
    }

    /**
     * 发布事件
     * @param event 事件
     */
    public static void publish(Event event){
        for(EventPublisher publisher:eventPublisherList){
            publisher.publish(event);
        }
    }

    /**
     * 订阅事件
     * @param eventClass 事件类型
     * @param eventListener 事件监听器
     * @param <E> 事件
     */
    public static <D,E extends Event<D>>  void subscriber(Class<E> eventClass,EventListener<D,E> eventListener){
        for (EventSubscriber subscriber:eventSubscriberList){
            subscriber.subscriber(eventClass,eventListener);
        }
    }

}
