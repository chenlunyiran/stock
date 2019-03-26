package com.twotiger.stock.logger.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/10/30
 * Time: 17:48
 * To change this template use File | Settings | File Templates.
 */
public class SimpleInvokerInfoBuilder implements InvokerInfoBuilder {

    public static final SimpleInvokerInfoBuilder INSTANCE = new SimpleInvokerInfoBuilder();

    @Override
    public InvokerInfo buildFrom(ProceedingJoinPoint pjp) {
        String token = "token";
        String taskName;
        Class clazz = null;
        Method method = null;
        Object[] args = null;
        try {
            Object target = pjp.getTarget();
            clazz = target.getClass();
            if (Proxy.isProxyClass(clazz)) {
                clazz = clazz.getInterfaces()[0];
            }
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            method = signature.getMethod();
            args = pjp.getArgs();
            taskName = String.format("%s.%s", clazz.getSimpleName(), method.getName());
        } catch (Exception e) {
            taskName = e.getMessage();
        }
        return new InvokerInfo(token, taskName, clazz, method, args);//todo 延迟输出入参args可能会发生变化 不准确
    }

    @Override
    public InvokerInfo buildFrom(HttpServletRequest httpServletRequest, Object handler) {
        InvokerInfo invokerInfo = new InvokerInfo("", httpServletRequest.getRequestURL().toString(), null, null, null);
        return invokerInfo;
    }

    @Override
    public InvokerInfo buildFrom(ServletRequest request) {
        InvokerInfo invokerInfo = new InvokerInfo("", request.getServerName().toString(), null, null, null);
        return invokerInfo;
    }
}
