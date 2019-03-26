package com.twotiger.stock.logger.util;

/**
 * 方法调用参数格式化器
 * Created by liuqing-notebook on 2017/2/13.
 */
public interface ParamFormatter {
    /**
     * @param args 方法调用的参数
     * @return 格式化的字符串
     */
    String format(Object[] args);
}
