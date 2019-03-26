package com.twotiger.stock.logger.util;

import java.lang.reflect.Method;

/**
 * 调用信息
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/10/30
 * Time: 16:20
 * To change this template use File | Settings | File Templates.
 */
public class InvokerInfo {
    //调用标识
    private final String invokerToken;
    //调用名称
    private final String invokerName;
    //调用class
    private final Class invokerClass;
    //调用方法
    private final Method invokerMethod;
    //调用方法参数
    private final Object[] args;

    public InvokerInfo(String invokerToken, String invokerName, Class invokerClass, Method invokerMethod, Object[] args) {
        this.invokerClass = invokerClass;
        this.invokerToken = invokerToken;
        this.invokerName = invokerName;
        this.invokerMethod = invokerMethod;
        this.args = args;
    }

    /**
     * Getter for property 'invokerClass'.
     *
     * @return Value for property 'invokerClass'.
     */
    public Class getInvokerClass() {
        return invokerClass;
    }

    /**
     * Getter for property 'invokerToken'.
     *
     * @return Value for property 'invokerToken'.
     */
    public String getInvokerToken() {
        return invokerToken;
    }

    /**
     * Getter for property 'invokerMethod'.
     *
     * @return Value for property 'invokerMethod'.
     */
    public Method getInvokerMethod() {
        return invokerMethod;
    }

    /**
     * Getter for property 'invokerName'.
     *
     * @return Value for property 'invokerName'.
     */
    public String getInvokerName() {
        return invokerName;
    }

    /**
     * Getter for property 'invokerClassName'.
     *
     * @return Value for property 'invokerClassName'.
     */
    public String getInvokerClassName() {
        return invokerClass == null ? "noClassName" : invokerClass.getSimpleName();
    }

    /**
     * Getter for property 'invokerMethodName'.
     *
     * @return Value for property 'invokerMethodName'.
     */
    public String getInvokerMethodName() {
        return invokerMethod == null ? "noMethodName" : invokerMethod.getName();
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "InvokerInfo{" +
            "invokerClass=" + invokerClass +
            ", invokerToken='" + invokerToken + '\'' +
            ", invokerName='" + invokerName + '\'' +
            ", invokerMethod=" + invokerMethod +
            '}';
    }
}
