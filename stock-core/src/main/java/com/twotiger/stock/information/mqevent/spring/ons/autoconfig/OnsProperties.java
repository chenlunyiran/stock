package com.twotiger.stock.information.mqevent.spring.ons.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "aliyun.ons")
@Component
public class OnsProperties {
    //公共
    private String onsAddr;
    private String accessKey;
    private String secretKey;
    private String topic;
    //消费端
    private String consumerId;
    private Long consumeThreadNums;
    private Long consumeTimeout;
    private Integer consumeMessageBatchMaxSize;
    //生产端
    private String producerId;
    private Long SendMsgTimeoutMillis;

    public String getOnsAddr() {
        return onsAddr;
    }

    public void setOnsAddr(String onsAddr) {
        this.onsAddr = onsAddr;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public Long getConsumeThreadNums() {
        return consumeThreadNums;
    }

    public void setConsumeThreadNums(Long consumeThreadNums) {
        this.consumeThreadNums = consumeThreadNums;
    }

    public Long getConsumeTimeout() {
        return consumeTimeout;
    }

    public void setConsumeTimeout(Long consumeTimeout) {
        this.consumeTimeout = consumeTimeout;
    }

    public Integer getConsumeMessageBatchMaxSize() {
        return consumeMessageBatchMaxSize;
    }

    public void setConsumeMessageBatchMaxSize(Integer consumeMessageBatchMaxSize) {
        this.consumeMessageBatchMaxSize = consumeMessageBatchMaxSize;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public Long getSendMsgTimeoutMillis() {
        return SendMsgTimeoutMillis;
    }

    public void setSendMsgTimeoutMillis(Long sendMsgTimeoutMillis) {
        SendMsgTimeoutMillis = sendMsgTimeoutMillis;
    }
}
