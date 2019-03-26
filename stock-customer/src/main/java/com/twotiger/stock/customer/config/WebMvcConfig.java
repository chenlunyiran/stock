package com.twotiger.stock.customer.config;


import com.twotiger.stock.customer.intercept.LoginInterceptor;
import com.twotiger.stock.customer.intercept.SystemVariableInterceptor;
import com.twotiger.shop.logger.monitor.CostTimeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginInterceptor loginInterceptor;

    @Autowired
    SystemVariableInterceptor systemVariableInterceptor;

    // 页面js的版本控制，去除缓存
    private static final String  THYMELEAF_JS_VERSION = "version";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CostTimeInterceptor())
                .addPathPatterns("/**").excludePathPatterns("/static/**","/js/**","/templates/**","/**/*.html","/**/*.js","/**/*.gif","/**/*.jpg","/**/*.png","/**/*.js**","/**/*.woff","/**/*.woff**","/**/*.css","/**/*.css**","/**/index.do");

        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").
                excludePathPatterns("/login",
                        "/css/**",
                        "/img/**",
                        "/js/**",
                        "/error",
                        "/notice/errorPage",
                        "/data/**");

        registry.addInterceptor(systemVariableInterceptor);

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public FilterRegistrationBean contextFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CommonsRequestLoggingFilter commonsRequestLoggingFilter = new CommonsRequestLoggingFilter();
        commonsRequestLoggingFilter.setIncludeClientInfo(true);
        commonsRequestLoggingFilter.setIncludePayload(true);
        commonsRequestLoggingFilter.setIncludeHeaders(true);
        commonsRequestLoggingFilter.setIncludeQueryString(true);
        commonsRequestLoggingFilter.setMaxPayloadLength(2048);
        registrationBean.setFilter(commonsRequestLoggingFilter);
        registrationBean.setOrder(1);
        return registrationBean;
    }


}
