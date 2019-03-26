package com.twotiger.shop.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.WebApplicationInitializer;
import tk.mybatis.spring.annotation.MapperScan;

;
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages= {"com.ibeetl.admin.core","com.twotiger.shop.core","com.twotiger.shop.config","com.twotiger.shop.job.config"})
@MapperScan(basePackages = {"com.twotiger.stock.core.mapper"})
public class ShopJobStart extends SpringBootServletInitializer implements WebApplicationInitializer {
	
    public static void main(String[] args) {
    	
    	SpringApplication.run(ShopJobStart.class, args);
    }
}	   