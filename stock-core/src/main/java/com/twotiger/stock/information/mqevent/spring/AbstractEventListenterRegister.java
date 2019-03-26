package com.twotiger.stock.information.mqevent.spring;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.twotiger.stock.information.mqevent.MqEventListener;
import com.twotiger.stock.util.Reflect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 事件监听注册器
 * Created by liuqing-notebook on 2016/2/3.
 */
public abstract class AbstractEventListenterRegister implements InitializingBean,DisposableBean {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEventListenterRegister.class);

    private boolean isSubscribe = false;

    private static final Comparator<MqEventListener> MQ_EVENT_LISTENER_LEVEL_COMPARATOR = (el1, el2)->{
        int cp = el1.getLevel()-el1.getLevel();
        return cp==0?1:cp;
    };

    //订阅过滤
    private static final Comparator<MqEventListener> MQ_EVENT_LISTENER_COMPARATOR = (el1,el2)->{
        if(Reflect.isSameClass(el1.getClass(),el2.getClass())){
            return 0;
        }
        return 1;
    };

    /**
     * 注册的事件监听器
     */
    protected List<MqEventListener> listeners;

    private final Set<Class<?>> listenerClass = new HashSet<>();

//    /**
//     * 事件监听器列表 过滤重复
//     */
//    private ConcurrentSkipListSet<MqEventListener> eventListenters = new ConcurrentSkipListSet<>(MQ_EVENT_LISTENER_COMPARATOR);

    /**
     * 需要订阅的事件
     */
    protected  Set<String> eventMolds = new HashSet<>();

    /**
     * 事件对应的监听器
     */
    private  Map<String,ArrayList<MqEventListener>> eventListenterMap = new HashMap<>();


    @Override
    public final void afterPropertiesSet() throws Exception {
        subscribe();
    }

    private void subscribe(){
        if(listeners!=null&&!listeners.isEmpty()&&this.isSubscribe==false) {
            childInit();
            for (MqEventListener eventListener : listeners) {
                if (listenerClass.add(Reflect.getRealClass(eventListener.getClass()))) {
                    String mold = eventListener.getEventMold();
                    eventMolds.add(mold);
                    if (!eventListenterMap.containsKey(mold)) {
                        eventListenterMap.put(mold, new ArrayList<>());
                    }
                    ArrayList<MqEventListener> moldListeners = eventListenterMap.get(mold);
                    moldListeners.add(eventListener);
                } else {
                    LOGGER.error("eventListener repetition！" + eventListener);
                }
            }
            eventListenterMap.values().forEach(moldListeners -> {
                Collections.sort(moldListeners, MQ_EVENT_LISTENER_LEVEL_COMPARATOR);
            });
            eventMolds = new ImmutableSet.Builder<String>().addAll(eventMolds).build();
            eventListenterMap = new ImmutableMap.Builder<String, ArrayList<MqEventListener>>().putAll(eventListenterMap).build();
            subscribe(eventMolds);
            LOGGER.info("subscribe Listener Events=[" + JSON.toJSON(eventMolds) + "]");
            LOGGER.info("subscribe Event Listeners=[" + listenerClass + "]");
            this.isSubscribe=true;
        }else {
            LOGGER.info("subscribe Listener isEmpty");
        }
    }

    protected abstract void childInit();

    /**
     * 订阅事件
     * @param eventMolds
     */
    protected abstract void subscribe(Set<String> eventMolds);

    /**
     * 取消订阅
     * @param
     */
    protected abstract void unsubscribe();

    /**
     * 处理事件
     * @param mold
     * @param eventId
     * @param eventData
     */
    protected final void processEvent(String mold,String eventId,Object eventData){
        List<MqEventListener> eventListeners = eventListenterMap.get(mold);
        if(eventListeners==null||eventListeners.isEmpty()){
            LOGGER.error("mold= ["+mold+"] no register eventListeners");
            return;
        }
        for(MqEventListener mqEventListener:eventListeners){
            try {
                LOGGER.debug("{}.onEvent=({},{})",mqEventListener,eventId,eventData);
                mqEventListener.onEvent(eventId, eventData);
            }catch (Exception e){
                LOGGER.error("eventId="+eventId,e);
            }
        }
    }


    @Override
    public final void destroy() throws Exception {
        unsubscribe();
    }

    public List<MqEventListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<MqEventListener> listeners) {
        if(this.listeners!=null&&!this.listeners.isEmpty()){
            throw new RuntimeException("监听器不能覆盖");
        }
        this.listeners = ImmutableList.copyOf(listeners);
        this.subscribe();
    }
}
