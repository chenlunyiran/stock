package com.twotiger.stock.customer.common;

import com.twotiger.stock.customer.enums.ResultCode;

/**
 * Created by alean on 2018/11/17.
 */
public class ReturnMessage <T>{
    String code;
    String msg;
    T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ReturnMessage(ResultCode code, T t) {
        this.code = code.name();
        this.msg = code.desc();
        this.data = t;
    }

    public ReturnMessage(ResultCode code) {
        this.code = code.name();
        this.msg = code.desc();
        this.data = null;
    }

    public ReturnMessage() {
    }

    public static void wrapReturnMessageSuccess(ReturnMessage returnMessage){
        returnMessage.setCode(ResultCode.OK.name());
        returnMessage.setMsg(ResultCode.OK.desc());
    }
    public static void wrapReturnMessage(ReturnMessage returnMessage, ResultCode resultCode){
        returnMessage.setCode(resultCode.name());
        returnMessage.setMsg(resultCode.desc());
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
