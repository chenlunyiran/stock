package com.twotiger.stock.customer.util;

import com.twotiger.stock.customer.common.ReturnMessage;
import com.twotiger.stock.customer.enums.ResultCode;

import java.util.HashMap;

/**
 * Created by lei on 2018/11/19.
 */
public class ResultUtil {


    public static ReturnMessage SUCCESS(ResultCode code, Object data) {
        return new ReturnMessage(code, data);
    }

    public static ReturnMessage SUCCESS(Object data) {
        return new ReturnMessage(ResultCode.OK, data);
    }

    public static ReturnMessage SUCCESS() {
        return new ReturnMessage(ResultCode.OK, null);
    }

    public static ReturnMessage ERROR(ResultCode code, Object data) {
        return new ReturnMessage(code, data);
    }

    public static ReturnMessage ERROR(ResultCode code) {
        return new ReturnMessage(code, new HashMap<>());
    }

}
