package com.twotiger.stock.information.mqevent.spring.rabbit.publisher;


import com.twotiger.stock.information.event.Event;
import com.twotiger.stock.information.mqevent.MqEventPublisher;
import com.twotiger.stock.util.Reflect;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 事件消息发送器
 * Created by liuqing-notebook on 2017/2/22.
 */
public class RabbitMqEventPublisher implements MqEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageConverter messageConverter;
    @Autowired
    private RabbitAdmin rabbitAdmin;

    //已经绑定的queue
    private final Set<String>  alreadyBinding = new CopyOnWriteArraySet();

    private MessageProperties messageProperties = new MessageProperties();

    @Override
    public void publish(Event event) {
        final String className = Reflect.getFullClassName(event.getClass());
        if(!alreadyBinding.contains(className)) {
            String exchange = rabbitTemplate.getExchange();//使用 rabbitTemplate 配置的交换中心
            Queue queue = new Queue(className);
            rabbitAdmin.declareQueue(queue);
            Binding binding = new Binding(className, Binding.DestinationType.QUEUE, exchange, className, null);
            rabbitAdmin.declareBinding(binding);
            alreadyBinding.add(className);
        }
        rabbitTemplate.send(className, messageConverter.toMessage(event,messageProperties));
    }
}
