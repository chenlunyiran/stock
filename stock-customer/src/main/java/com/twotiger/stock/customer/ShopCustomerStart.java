package com.twotiger.stock.customer;


import com.twotiger.stock.customer.common.MyFilterType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.WebApplicationInitializer;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages= {"com.twotiger.shop.customer","com.twotiger.shop.core","com.ibeetl.admin","com.twotiger.shop.config","com.twotiger.shop.mq.listener"},
        excludeFilters={@ComponentScan.Filter(type = FilterType.CUSTOM,value = {MyFilterType.class})
        })
@MapperScan(basePackages = {"com.twotiger.stock.core.mapper"})
public class ShopCustomerStart extends SpringBootServletInitializer implements WebApplicationInitializer {
    public static void main(String[] args) {
    	SpringApplication.run(ShopCustomerStart.class, args);
    }

}	