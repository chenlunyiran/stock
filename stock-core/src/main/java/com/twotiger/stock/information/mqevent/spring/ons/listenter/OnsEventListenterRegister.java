package com.twotiger.stock.information.mqevent.spring.ons.listenter;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.twotiger.stock.information.mqevent.spring.AbstractEventListenterRegister;
import com.twotiger.stock.information.mqevent.spring.ons.autoconfig.OnsProperties;
import com.twotiger.stock.util.JavaSerializerUtil;
import com.twotiger.stock.util.StringUtils;
import com.twotiger.stock.information.mqevent.spring.ons.autoconfig.OnsProperties;
import com.twotiger.stock.util.JavaSerializerUtil;
import com.twotiger.stock.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class OnsEventListenterRegister extends AbstractEventListenterRegister {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnsEventListenterRegister.class);

    @Autowired
    private ConsumerBean consumerbean;

    private OnsProperties onsProperties;

    private final MessageListener messageListener = new MessageListener(){

        @Override
        public Action consume(Message message, ConsumeContext consumeContext) {
            String tag = null;
            String id = null;
            Object eventData = null;
            try {
                tag = message.getTag();
                if(listeners==null){
                    LOGGER.warn("listeners is null tag="+tag);
                    return Action.ReconsumeLater;
                }
                if(!eventMolds.contains(tag)){
                    LOGGER.warn("no listeners tag="+tag);
                    return Action.ReconsumeLater;
                }
                id = message.getKey();
                byte[] body = message.getBody();
                eventData = JavaSerializerUtil.deserializer(body);
                processEvent(tag, id, eventData);
                return Action.CommitMessage;
            }catch (Exception e){
                LOGGER.warn("处理消息异常！消息已经消费 需要人工核对 tag="+tag+"id="+id+"-"+message.getMsgID()+"eventData="+JSON.toJSONString(eventData),e);
                return Action.CommitMessage;
            }
        }
    };


    @Override
    protected void childInit() {

    }

    @Override
    protected void subscribe(Set<String> eventMolds) {
        if(consumerbean==null){
            throw new RuntimeException("consumerbean is null");
        }
        try {
            consumerbean.subscribe(onsProperties.getTopic(),StringUtils.merge(eventMolds,"||"),messageListener);
        } catch (Exception e) {
            LOGGER.error("订阅启动失败！",e);
            throw new RuntimeException("订阅启动失败！",e);
        }
        LOGGER.info("OnsEventListenterRegister subscribe Properties=["+consumerbean.getProperties()+"]");
    }

    @Override
    protected void unsubscribe() {
        consumerbean.unsubscribe(onsProperties.getTopic());
    }

    public ConsumerBean getConsumerbean() {
        return consumerbean;
    }

    public void setConsumerbean(ConsumerBean consumerbean) {
        this.consumerbean = consumerbean;
    }

    public OnsProperties getOnsProperties() {
        return onsProperties;
    }

    public void setOnsProperties(OnsProperties onsProperties) {
        this.onsProperties = onsProperties;
    }
}
