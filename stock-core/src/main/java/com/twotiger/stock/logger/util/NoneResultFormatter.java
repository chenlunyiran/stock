package com.twotiger.stock.logger.util;

/**
 * Created by liuqing-notebook on 2017/2/13.
 */
public class NoneResultFormatter implements ResultFormatter {
    private static final String NONE_RESULT = "不显示";

    @Override
    public String format(Object result) {
        return NONE_RESULT;
    }
}
