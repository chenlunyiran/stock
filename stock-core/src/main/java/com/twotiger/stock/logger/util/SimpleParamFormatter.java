package com.twotiger.stock.logger.util;

import com.alibaba.fastjson.JSON;

/**
 * Created by liuqing-notebook on 2017/2/13.
 */
public class SimpleParamFormatter implements ParamFormatter {
    @Override
    public String format(Object[] args) {
        String res = "";
        try {
            res = JSON.toJSONString(args);
        } catch (Exception e) {

        }
        return res;
    }
}
