package com.twotiger.stock.information.mqevent.spring.rocket.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * Created by liuqing-notebook on 2016/2/4.
 */
public class PushConsumerBean extends AbsRocketConfig implements InitializingBean, DisposableBean {

    private static final String consumerTopic_Key ="rocketmq.consumerTopic";
//    #Consumer组名，多个Consumer如果属于一个应用，订阅同样的消息，且消费逻辑一致，则应该将它们归为同一组
//    #默认值 DEFAULT_CONSUMER
    private static final String consumerGroup_Key ="rocketmq.consumerGroup";
//    # 消息模型，支持以下两种 1、集群消费 2、广播消费
//    #默认值 CLUSTERING
    private static final String messageModel_Key ="rocketmq.messageModel";
//    # Consumer启动后，默认从什么位置开始消费
//    #默认值 CONSUME_FROM_LAST_OFFSET
    private static final String consumeFromWhere_Key ="rocketmq.consumeFromWhere";
//    # Rebalance算法实现策略
//    #默认值 AllocateMessageQueueAveragely
    private static final String allocateMessageQueueStrategy_Key ="rocketmq.allocateMessageQueueStrategy";
//    #消息监听器
//    #默认值
    private static final String messageListener_Key ="rocketmq.messageListener";
//    #消费进度存储
//    #默认值
    private static final String offsetStore_Key ="rocketmq.offsetStore";
//    #消费线程池数量 min
//    #默认值 10
    private static final String consumeThreadMin_Key ="rocketmq.consumeThreadMin";
//    #消费线程池数量 max
//    #默认值 20
    private static final String consumeThreadMax_Key ="rocketmq.consumeThreadMax";
//    #单队列并行消费允许的最大跨度
//    #默认值 2000
    private static final String consumeConcurrentlyMaxSpan_Key ="rocketmq.consumeConcurrentlyMaxSpan";
//    #拉消息间隔，由于是长轮询，所以为0，但是如果应用为了流控，也可以设置大于0的值，单位毫秒
//    #默认值 0
    private static final String pullInterval_Key ="rocketmq.pullInterval";
//    #批量消费，一次消费多少条消息
//    #默认值 1
    private static final String consumeMessageBatchMaxSize_Key ="rocketmq.consumeMessageBatchMaxSize";
//    #批量拉消息，一次最多拉多少条
//    #默认值 32
    private static final String pullBatchSize_Key ="rocketmq.pullBatchSize";



    /**
     * 消费的主题
     */
    private String consumerTopic;

    /**
     * 消费消息组
     */
    private String consumerGroup;

    /**
     * 配置文件路径
     */
    private String configLocation ;
    /**
     * 消费者
     */
    private DefaultMQPushConsumer mqPushConsumer;

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource resource =resourcePatternResolver.getResource(configLocation);
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        mqPushConsumer = new DefaultMQPushConsumer();
        configClient(mqPushConsumer,properties);
        if(!StringUtils.isEmpty(consumerGroup)){
            mqPushConsumer.setConsumerGroup(consumerGroup);
        }else{
            consumerGroup = properties.getProperty(consumerGroup_Key);
            if(!StringUtils.isEmpty(consumerGroup)){
                mqPushConsumer.setConsumerGroup(consumerGroup);
            }
        }
        if(StringUtils.isEmpty(consumerTopic)){
            consumerTopic = properties.getProperty(consumerTopic_Key);
            if(StringUtils.isEmpty(consumerTopic)){
                throw new RuntimeException("topic isEmpty !");
            }
        }
        logger.info("rocket Consumer properties config=["+properties+"]");
        logger.info("rocket Consumer bean config=["+toString()+"]");
    }

    @Override
    public void destroy() throws Exception {
        mqPushConsumer.shutdown();
    }

    /**
     * 订阅事件并启动
     * @param subExpression
     * @param messageListener
     * @throws Exception
     */
    public void subscribeAndStart(String subExpression, MessageListener messageListener) throws Exception{
        logger.info("subscribe topic=["+consumerTopic+"] subExpression=["+subExpression+"]");
        mqPushConsumer.subscribe(consumerTopic,subExpression);
        mqPushConsumer.setMessageListener(messageListener);
        mqPushConsumer.start();
    }

    /**
     * 取消订阅
     */
    public void unsubscribe(){
        try {
            mqPushConsumer.unsubscribe(consumerTopic);
        }catch (Exception e){
            logger.error("unsubscribe error Topic="+consumerTopic,e);
        }
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getConsumerTopic(){
        return consumerTopic;
    }

    public void setConsumerTopic(String consumerTopic) {
        this.consumerTopic = consumerTopic;
    }

    @Override
    public String toString() {
        return "PushConsumerBean{" +
                "consumerTopic='" + consumerTopic + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                '}';
    }
}
