package com.twotiger.stock.information.mqevent.spring.ons.autoconfig;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.twotiger.stock.information.mqevent.spring.ons.publisher.OnsEventPublisherFactoryBean;
import com.twotiger.stock.information.mqevent.spring.ons.publisher.OnsEventPublisherFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 生产者自动配置
 */
@Configuration
@EnableConfigurationProperties(OnsProperties.class)
@ConditionalOnClass(com.aliyun.openservices.ons.api.bean.ProducerBean.class)
@ConditionalOnProperty(prefix = "aliyun.ons", name = "producer",havingValue = "true")
public class ProducerBeanAutoConfig {
    @Autowired
    private OnsProperties onsProperties;

    @Bean(initMethod="start" ,destroyMethod="shutdown")
    public ProducerBean producerBean(){
        ProducerBean producerBean = new ProducerBean();
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey,onsProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey,onsProperties.getSecretKey());
        properties.put(PropertyKeyConst.ProducerId,onsProperties.getProducerId());
        producerBean.setProperties(properties);
        return producerBean;
    }
    @Bean
    public OnsEventPublisherFactoryBean mqEventPublisher(ProducerBean producerBean){
        OnsEventPublisherFactoryBean onsEventPublisherFactoryBean = new OnsEventPublisherFactoryBean();
        onsEventPublisherFactoryBean.setProducerBean(producerBean);
        onsEventPublisherFactoryBean.setTopic(onsProperties.getTopic());
        return onsEventPublisherFactoryBean;
    }
}
