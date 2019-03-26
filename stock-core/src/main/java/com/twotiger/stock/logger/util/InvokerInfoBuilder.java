package com.twotiger.stock.logger.util;

import org.aspectj.lang.ProceedingJoinPoint;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * 调用信息创建器
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/10/30
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */
public interface InvokerInfoBuilder {
    InvokerInfo buildFrom(ProceedingJoinPoint pjp);

    InvokerInfo buildFrom(HttpServletRequest httpServletRequest, Object handler);

    InvokerInfo buildFrom(ServletRequest request);
}
