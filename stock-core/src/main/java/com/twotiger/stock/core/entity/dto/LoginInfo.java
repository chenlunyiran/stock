package com.twotiger.stock.core.entity.dto;

/**
 * Created by lei on 2018/11/16.
 */
public class LoginInfo {

    private String token;

    private String randomStr;

    private Integer systemType;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRandomStr() {
        return randomStr;
    }

    public void setRandomStr(String randomStr) {
        this.randomStr = randomStr;
    }

    public Integer getSystemType() {
        return systemType;
    }

    public void setSystemType(Integer systemType) {
        this.systemType = systemType;
    }
}
