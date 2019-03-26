package com.twotiger.stock.information.mqevent.spring.rabbit.listenter;


import com.twotiger.stock.information.Event;
import com.twotiger.stock.information.mqevent.spring.AbstractEventListenterRegister;
import com.twotiger.stock.util.Reflect;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * 事件监听注册器
 * Created by liuqing-notebook on 2017/2/22.
 */
public class RabbitEventListenterRegister extends AbstractEventListenterRegister implements RabbitListenerConfigurer {

    @Autowired
    private MessageConverter messageConverter;
    @Autowired
    private RabbitAdmin rabbitAdmin;
    private volatile Set<String> eventMolds;


    private MessageListener messageListener = new MessageListener(){
        @Override
        public void onMessage(Message message) {
            Event event = (Event)messageConverter.fromMessage(message);
            String className = Reflect.getFullClassName(event.getClass());
            processEvent(className,event.getId(),event.data());
        }
    };

    public void childInit(){
    }


    @Override
    protected void subscribe(Set<String> eventMolds) {
        if(eventMolds.size()>0) {
            this.eventMolds = eventMolds;
            for(String className:eventMolds) {
                Queue queue = new Queue(className);
                rabbitAdmin.declareQueue(queue);
                Binding binding = new Binding(className, Binding.DestinationType.QUEUE, "directExchange", className, null);
                rabbitAdmin.declareBinding(binding);
            }
        }
    }

    @Override
    protected void unsubscribe() {

    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        if(eventMolds.size()>0) {
            String[] array = eventMolds.toArray(new String[eventMolds.size()]);
            SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
            endpoint.setId("event-endpoint");
            endpoint.setQueueNames(array);
            endpoint.setMessageListener(messageListener);
            rabbitListenerEndpointRegistrar.registerEndpoint(endpoint);
        }
    }
}
