package com.twotiger.stock.information.mqevent.spring.rocket.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * 生产者
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/2/1
 * Time: 21:18
 * To change this template use File | Settings | File Templates.
 */
public class ProductFactoryBean extends AbsRocketConfig implements FactoryBean<DefaultMQProducer>,InitializingBean, DisposableBean {

    private static final String producerGroup_Key = "rocketmq.producerGroup";
    private static final String createTopicKey_Key = "rocketmq.createTopicKey";
    private static final String defaultTopicQueueNums_Key = "rocketmq.defaultTopicQueueNums";
    private static final String sendMsgTimeout_Key = "rocketmq.sendMsgTimeout";
    private static final String compressMsgBodyOverHowmuch_Key = "rocketmq.compressMsgBodyOverHowmuch";
    private static final String retryAnotherBrokerWhenNotStoreOK_Key = "rocketmq.retryAnotherBrokerWhenNotStoreOK";
    private static final String maxMessageSize_Key = "rocketmq.maxMessageSize";

    /**
     * 主题
     */
    private String createTopicKey;

    /**
     * 生产消息组
     */
    private String producerGroup;


    /**
     * 配置文件路径
     */
    private String configLocation ;

    private DefaultMQProducer mqProducer;

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Override
    public DefaultMQProducer getObject() throws Exception {
        return mqProducer;
    }

    @Override
    public Class<?> getObjectType() {
        return DefaultMQProducer.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        mqProducer.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource resource =resourcePatternResolver.getResource(configLocation);
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        mqProducer = new DefaultMQProducer();
        configClient(mqProducer,properties);
        if(!StringUtils.isEmpty(producerGroup)){
            mqProducer.setProducerGroup(producerGroup);
        }else{
            producerGroup = properties.getProperty(producerGroup_Key);
            if(!StringUtils.isEmpty(producerGroup)){
                mqProducer.setProducerGroup(producerGroup);
            }
        }
        if(!StringUtils.isEmpty(createTopicKey)){
            mqProducer.setCreateTopicKey(createTopicKey);
        }else{
            createTopicKey = properties.getProperty(createTopicKey_Key);
            if(!StringUtils.isEmpty(createTopicKey)){
                mqProducer.setCreateTopicKey(createTopicKey);
            }else{
                throw new RuntimeException("createTopicKey isEmpty !");
            }
        }
        String defaultTopicQueueNums = properties.getProperty(defaultTopicQueueNums_Key);
        if(!StringUtils.isEmpty(defaultTopicQueueNums)){
            mqProducer.setDefaultTopicQueueNums(Integer.parseInt(defaultTopicQueueNums));
        }
        String sendMsgTimeout = properties.getProperty(sendMsgTimeout_Key);
        if(!StringUtils.isEmpty(sendMsgTimeout)){
            mqProducer.setSendMsgTimeout(Integer.parseInt(sendMsgTimeout));
        }
        String compressMsgBodyOverHowmuch = properties.getProperty(compressMsgBodyOverHowmuch_Key);
        if(!StringUtils.isEmpty(compressMsgBodyOverHowmuch)){
            mqProducer.setCompressMsgBodyOverHowmuch(Integer.parseInt(compressMsgBodyOverHowmuch));
        }
        String retryAnotherBrokerWhenNotStoreOK = properties.getProperty(retryAnotherBrokerWhenNotStoreOK_Key);
        if(!StringUtils.isEmpty(retryAnotherBrokerWhenNotStoreOK)){
            mqProducer.setRetryAnotherBrokerWhenNotStoreOK(retryAnotherBrokerWhenNotStoreOK.equalsIgnoreCase("true"));
        }
        String maxMessageSize = properties.getProperty(maxMessageSize_Key);
        if(!StringUtils.isEmpty(maxMessageSize)){
            mqProducer.setMaxMessageSize(Integer.parseInt(maxMessageSize));
        }
        logger.info("rocket Product properties config=["+properties+"]");
        logger.info("rocket Product bean config=["+toString()+"]");
        //start
        mqProducer.start();
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }


    /**
     * Getter for property 'configLocation'.
     *
     * @return Value for property 'configLocation'.
     */
    public String getConfigLocation() {
        return configLocation;
    }

    /**
     * Setter for property 'configLocation'.
     *
     * @param configLocation Value to set for property 'configLocation'.
     */
    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * Getter for property 'mqProducer'.
     *
     * @return Value for property 'mqProducer'.
     */
    public DefaultMQProducer getMqProducer() {
        return mqProducer;
    }

    /**
     * Setter for property 'mqProducer'.
     *
     * @param mqProducer Value to set for property 'mqProducer'.
     */
    public void setMqProducer(DefaultMQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }

    /**
     * Getter for property 'resourcePatternResolver'.
     *
     * @return Value for property 'resourcePatternResolver'.
     */
    public ResourcePatternResolver getResourcePatternResolver() {
        return resourcePatternResolver;
    }

    /**
     * Setter for property 'resourcePatternResolver'.
     *
     * @param resourcePatternResolver Value to set for property 'resourcePatternResolver'.
     */
    public void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public String getCreateTopicKey() {
        return createTopicKey;
    }

    public void setCreateTopicKey(String createTopicKey) {
        this.createTopicKey = createTopicKey;
    }

    @Override
    public String toString() {
        return "ProductFactoryBean{" +
                "createTopicKey='" + createTopicKey + '\'' +
                ", producerGroup='" + producerGroup + '\'' +
                '}';
    }
}
