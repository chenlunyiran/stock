package com.twotiger.stock.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.WebApplicationInitializer;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages= {"com.twotiger.stock.core","com.twotiger.stock.config","com.twotiger.stock.util","com.twotiger.stock.manage","com.ibeetl.admin","com.twotiger.stock.mq.listener"})
@MapperScan(basePackages = {"com.twotiger.stock.core.mapper"})
public class StockManageStart extends SpringBootServletInitializer implements WebApplicationInitializer {
	
    public static void main(String[] args) {
    	
    	SpringApplication.run(StockManageStart.class, args);
    }
}	   