package com.twotiger.stock.information.mqevent.spring.rocket.config;

import org.apache.rocketmq.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * rocketmq 客户端通用配置
 * Created by liuqing-notebook on 2016/2/4.
 */
public abstract class AbsRocketConfig {
    protected  final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String namesrvAddr_Key = "rocketmq.namesrvAddr";
    private static final String clientIP_Key = "rocketmq.clientIP";
    private static final String instanceName_Key = "rocketmq.instanceName";
    private static final String clientCallbackExecutorThreads_Key = "rocketmq.clientCallbackExecutorThreads";
    private static final String pollNameServerInteval_Key = "rocketmq.pollNameServerInteval";
    private static final String heartbeatBrokerInterval_Key = "rocketmq.heartbeatBrokerInterval";
    private static final String persistConsumerOffsetInterval_Key = "rocketmq.persistConsumerOffsetInterval";
    /**
     * namesrvAddr
     */
    private String namesrvAddr;

    protected void configClient(ClientConfig clientConfig, Properties properties){
        if(!StringUtils.isEmpty(namesrvAddr)){
            clientConfig.setNamesrvAddr(namesrvAddr);
        }else{
            namesrvAddr = properties.getProperty(namesrvAddr_Key);
            if(!StringUtils.isEmpty(namesrvAddr)){
                clientConfig.setNamesrvAddr(namesrvAddr);
            }else{
                throw new RuntimeException("namesrvAddr isEmpty !");
            }
        }
        String clientIP = properties.getProperty(clientIP_Key);
        if(!StringUtils.isEmpty(clientIP)){
            clientConfig.setClientIP(clientIP);
        }
        String instanceName = properties.getProperty(instanceName_Key);
        if(!StringUtils.isEmpty(instanceName)){
            clientConfig.setInstanceName(instanceName);
        }
        String clientCallbackExecutorThreads = properties.getProperty(clientCallbackExecutorThreads_Key);
        if(!StringUtils.isEmpty(clientCallbackExecutorThreads)){
            clientConfig.setClientCallbackExecutorThreads(Integer.parseInt(clientCallbackExecutorThreads));
        }
        String pollNameServerInteval = properties.getProperty(pollNameServerInteval_Key);
        if(!StringUtils.isEmpty(pollNameServerInteval)){
            clientConfig.setPollNameServerInterval(Integer.parseInt(pollNameServerInteval));
        }
        String heartbeatBrokerInterval = properties.getProperty(heartbeatBrokerInterval_Key);
        if(!StringUtils.isEmpty(heartbeatBrokerInterval)){
            clientConfig.setHeartbeatBrokerInterval(Integer.parseInt(heartbeatBrokerInterval));
        }
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }
}
