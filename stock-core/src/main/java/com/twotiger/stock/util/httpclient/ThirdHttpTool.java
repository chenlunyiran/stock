package com.twotiger.stock.util.httpclient;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by lei on 2018/11/22.
 */
@Component
public class ThirdHttpTool {

    public static ThirdRetMsg httpEncrypt(Map<String,String> data, String exUrl) throws Exception{
        String thirdRetData = HttpClientUtil.doPost(data,exUrl);
        ThirdRetMsg thirdRetMsg = JSON.parseObject(thirdRetData, ThirdRetMsg.class);
        return thirdRetMsg;
    }

}
