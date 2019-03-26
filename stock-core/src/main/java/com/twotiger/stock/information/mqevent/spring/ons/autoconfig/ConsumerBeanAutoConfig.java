package com.twotiger.stock.information.mqevent.spring.ons.autoconfig;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.twotiger.stock.information.mqevent.spring.ons.listenter.OnsEventListenterRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Properties;

/**
 *消费者自动配置
 */
@Configuration
@EnableConfigurationProperties(OnsProperties.class)
@ConditionalOnClass(com.aliyun.openservices.ons.api.bean.ConsumerBean.class)
@ConditionalOnProperty(prefix = "aliyun.ons", name = "consumer",havingValue = "true")
public class ConsumerBeanAutoConfig {
    @Autowired
    private OnsProperties onsProperties;
    @Bean(initMethod="start" ,destroyMethod="shutdown")
    public ConsumerBean consumerBean(){
        ConsumerBean consumerBean = new ConsumerBean();
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey,onsProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey,onsProperties.getSecretKey());
        properties.put(PropertyKeyConst.ConsumerId,onsProperties.getConsumerId());
        consumerBean.setProperties(properties);
        consumerBean.setSubscriptionTable(new HashMap<>());
        return consumerBean;
    }
    @Bean
    public OnsEventListenterRegister OnsEventListenterRegister(ConsumerBean consumerBean){
        OnsEventListenterRegister onsEventListenterRegister = new OnsEventListenterRegister();
        onsEventListenterRegister.setOnsProperties(onsProperties);
        onsEventListenterRegister.setConsumerbean(consumerBean);
        return onsEventListenterRegister;
    }
    @Bean
    public ListenterRegisterAutoRegiest listenterRegisterAutoRegiest(OnsEventListenterRegister onsEventListenterRegister){
        return new ListenterRegisterAutoRegiest(onsEventListenterRegister);
    }

}
