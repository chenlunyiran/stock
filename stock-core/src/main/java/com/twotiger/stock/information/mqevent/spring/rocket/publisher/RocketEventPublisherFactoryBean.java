package com.twotiger.stock.information.mqevent.spring.rocket.publisher;



import com.twotiger.stock.information.mqevent.MqEventPublisher;
import com.twotiger.stock.information.mqevent.spring.AbstractEventPublisherFactoryBean;
import com.twotiger.stock.util.JavaSerializerUtil;
import com.twotiger.stock.util.Reflect;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;


/**
 * Created by liuqing-notebook on 2016/2/4.
 */
public class RocketEventPublisherFactoryBean extends AbstractEventPublisherFactoryBean {
    /**
     * 发布的主题
     */
    private final String topic;

    private DefaultMQProducer mqProducer;

    private MqEventPublisher mqEventPublisher;

    public RocketEventPublisherFactoryBean(String topic) {
        this.topic = topic;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public MqEventPublisher getObject() throws Exception {
        return mqEventPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(mqProducer==null){
            throw new RuntimeException("mqProducer is null");
        }
        mqEventPublisher = event -> {
            String id = event.getId();
            String mold = Reflect.getFullClassName(event.getClass());//TODO mold method in event
            Message message = new Message(topic,mold,id, JavaSerializerUtil.serializer(event.getData()));
            try {
                logger.debug("send message topic={} mold={} id={}",topic,mold,id);
                mqProducer.send(message);
            } catch (Exception e) {
                logger.error("send message error!",e);
            }
        };
        logger.info("RocketEventPublisher use topic=["+topic+"]");
    }

    public void setMqProducer(DefaultMQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }
}
