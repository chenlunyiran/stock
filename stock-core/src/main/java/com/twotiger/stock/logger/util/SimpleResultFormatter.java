package com.twotiger.stock.logger.util;

import com.alibaba.fastjson.JSON;

/**
 * Created by liuqing-notebook on 2017/2/13.
 */
public class SimpleResultFormatter implements ResultFormatter {
    @Override
    public String format(Object result) {
        return JSON.toJSONString(result);
    }
}
