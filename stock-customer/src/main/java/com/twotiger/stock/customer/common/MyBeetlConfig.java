package com.twotiger.stock.customer.common;

import com.ibeetl.starter.BeetlTemplateCustomize;
import org.beetl.core.GroupTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by alean on 2018/11/30.
 */

@Configuration
public class MyBeetlConfig {

    @Bean
    public BeetlTemplateCustomize beetlTemplateCustomize() {
        return new BeetlTemplateCustomize() {
            public void customize(GroupTemplate groupTemplate) {

            }
        };
    }
}
