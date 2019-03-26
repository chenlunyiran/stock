package com.twotiger.stock.information.mqevent.spring;

import com.twotiger.stock.information.mqevent.MqEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 事件发布器FactoryBean
 * Created by liuqing-notebook on 2016/2/3.
 */
public abstract class AbstractEventPublisherFactoryBean implements FactoryBean<MqEventPublisher>,InitializingBean, DisposableBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Class<?> getObjectType() {
        return MqEventPublisher.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
