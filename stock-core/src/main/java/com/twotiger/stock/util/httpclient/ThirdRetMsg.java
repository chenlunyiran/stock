package com.twotiger.stock.util.httpclient;

import java.util.Map;

/**
 * Created by lei on 2018/11/19.
 */
public class ThirdRetMsg {

    private String code;

    private String codeDesc;

    private Map<String,String> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
