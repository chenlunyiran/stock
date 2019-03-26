package com.twotiger.stock.information;

import java.util.function.Consumer;

/**
 * Created by liuqing on 2015/4/1.
 */
public interface InformationManager {


    /**
     * 执行命令 一对一 有回调
     * @param command
     * @return object 执行结果
     */
    Object process(Command command);

    /**
     * 发送消息 一对一 无回调
     * @param message
     * @param callBack
     */
    void send(Message message, Consumer<Message> callBack);

    /**
     * 发布事件 一对多
     * @param event
     */
    void publish(Event event);
}
