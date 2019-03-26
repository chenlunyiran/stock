/**
 * Twotiger.com Llc.
 * Copyright (c) 2013-2015 All Rights Reserved.
 */
package com.twotiger.stock.customer.enums;

/**
 * @Description: 状态码 
 * @Author hansc
 * @Date 2015年7月9日 上午11:44:25 
 */
public enum ResultCode {
    OK("交易完成"),
    CHANGE("金额变化"),
    INVALID_PARAM("非法参数"),
    SYSTEM_EXCEPTION("系统异常");

    private String desc;
    private ResultCode(String desc) {
        this.desc = desc;
    }

    public ResultCode setDesc(String desc){
        this.desc = desc;
        return this;
    }

    public String desc() {
        return this.desc;
    }
}
