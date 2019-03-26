package com.twotiger.stock.information.mqevent.spring.rocket.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by liuqing-notebook on 2016/2/4.
 */
public class PullConsumerBean extends AbsRocketConfig implements InitializingBean, DisposableBean {


    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }
}
