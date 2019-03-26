package com.twotiger.stock.information.mqevent.spring.ons.autoconfig;

import com.alibaba.fastjson.JSON;
import com.twotiger.stock.information.mqevent.MqEventListener;
import com.twotiger.stock.information.mqevent.spring.ons.listenter.OnsEventListenterRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 监听器(消费者)自动订阅
 */
public class ListenterRegisterAutoRegiest implements BeanPostProcessor,ApplicationListener<ApplicationStartedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListenterRegisterAutoRegiest.class);
    private List<MqEventListener> listeners = new ArrayList<>();
    private final OnsEventListenterRegister onsEventListenterRegister;

    public ListenterRegisterAutoRegiest(OnsEventListenterRegister onsEventListenterRegister) {
        this.onsEventListenterRegister = onsEventListenterRegister;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MqEventListener){
            listeners.add((MqEventListener)bean);
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        LOGGER.info("onApplicationEvent----"+JSON.toJSONString(listeners));
        if(!listeners.isEmpty()){
            onsEventListenterRegister.setListeners(listeners);
        }
    }
}
