package com.twotiger.stock.information.mqevent.spring.rocket.listenter;


import com.twotiger.stock.information.mqevent.spring.AbstractEventListenterRegister;
import com.twotiger.stock.information.mqevent.spring.rocket.config.PushConsumerBean;
import com.twotiger.stock.util.JavaSerializerUtil;
import com.twotiger.stock.util.StringUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Set;

/**
 * Created by liuqing-notebook on 2016/2/4.
 */
public class RocketPushEventListenterRegister extends AbstractEventListenterRegister {

    private PushConsumerBean pushConsumerBean;

    private final MessageListener messageListener = new MessageListenerConcurrently(){

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            list.forEach(messageExt -> {
                String tag = messageExt.getTags();
                String id = messageExt.getKeys();
                byte[] body = messageExt.getBody();
                Object eventData = JavaSerializerUtil.deserializer(body);
                processEvent(tag,id,eventData);
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    };

    @Override
    protected void childInit() {

    }

    @Override
    protected void subscribe(Set<String> eventMolds) {
        if(pushConsumerBean==null){
            throw new RuntimeException("pushConsumerBean is null");
        }
        try {
            pushConsumerBean.subscribeAndStart(StringUtils.merge(eventMolds,"||"),messageListener);
        } catch (Exception e) {
            LOGGER.error("订阅启动失败！",e);
            throw new RuntimeException("订阅启动失败！",e);
        }
        LOGGER.info("RocketPushEventListenterRegister subscribe topic=["+pushConsumerBean.getConsumerTopic()+"] group=["+pushConsumerBean.getConsumerGroup()+"]");
    }

    @Override
    protected void unsubscribe() {
        pushConsumerBean.unsubscribe();
    }

    public PushConsumerBean getPushConsumerBean() {
        return pushConsumerBean;
    }

    public void setPushConsumerBean(PushConsumerBean pushConsumerBean) {
        this.pushConsumerBean = pushConsumerBean;
    }
}
