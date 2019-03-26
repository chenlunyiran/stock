package com.twotiger.stock.information.mqevent.spring.ons.publisher;

import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.twotiger.stock.information.mqevent.MqEventPublisher;
import com.twotiger.stock.information.mqevent.spring.AbstractEventPublisherFactoryBean;
import com.twotiger.stock.util.JavaSerializerUtil;
import com.twotiger.stock.util.Reflect;
import com.twotiger.stock.util.JavaSerializerUtil;
import com.twotiger.stock.util.Reflect;
import org.springframework.beans.factory.annotation.Autowired;

public class OnsEventPublisherFactoryBean  extends AbstractEventPublisherFactoryBean {

    @Autowired
    private ProducerBean producerBean;

    private String topic;

    private MqEventPublisher mqEventPublisher;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(producerBean==null){
            throw new RuntimeException("mqProducer is null");
        }
        mqEventPublisher = event -> {
            String id = event.getId();
            String mold = Reflect.getFullClassName(event.getClass());//TODO mold method in event
            com.aliyun.openservices.ons.api.Message message = new com.aliyun.openservices.ons.api.Message(topic,mold,id, JavaSerializerUtil.serializer(event.getData()));
            try {
                logger.debug("send message topic={} mold={} id={}",topic,mold,id);
                producerBean.send(message);
            } catch (Exception e) {
                logger.error("send message error!",e);
            }
        };
        logger.info("RocketEventPublisher use topic=["+topic+"]");
    }

    @Override
    public MqEventPublisher getObject() throws Exception {
        return mqEventPublisher;
    }

    @Override
    public void destroy() throws Exception {
        producerBean.shutdown();
    }

    public ProducerBean getProducerBean() {
        return producerBean;
    }

    public void setProducerBean(ProducerBean producerBean) {
        this.producerBean = producerBean;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
