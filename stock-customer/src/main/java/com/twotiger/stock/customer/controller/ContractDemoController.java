package com.twotiger.stock.customer.controller;

import com.twotiger.shop.core.entity.ShopCustomer;
import com.twotiger.shop.core.entity.ThirdPlatform;
import com.twotiger.shop.core.mapper.ThirdPlatformMapper;
import com.twotiger.stock.customer.common.ReturnMessage;
import com.twotiger.stock.customer.enums.ResultCode;
import com.twotiger.stock.customer.util.SessionUtil;
import com.twotiger.shop.util.httpclient.ThirdHttpTool;
import com.twotiger.shop.util.httpclient.ThirdRetMsg;
import com.twotiger.shop.util.httpclient.ThirdTxCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alean on 2018/12/4.
 */
@Controller
@RequestMapping("/own")
public class ContractDemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractDemoController.class);

    @Autowired
    private ThirdPlatformMapper thirdPlatformMapper;

    /**
     * 兑换消费金静态页面
     * @return
     */
    @GetMapping("/exchangeContractDemo")
    public String exchangeContractDemo(){
        return "/contractDemo/exchangeContractTwotiger.html";
    }


    /**
     * 查看兑换合同
     */
    @PostMapping("exchangeContract")
    @ResponseBody
    public ReturnMessage<Map<String,String>> exchangeContract(HttpServletRequest request){

        ReturnMessage<Map<String,String>> returnMessage = new ReturnMessage<>();
        HashMap<String, String> data = new HashMap<>();

        ShopCustomer currentCustomer = SessionUtil.getCurrentCustomer();
        String registFrom = currentCustomer.getRegistFrom();
        Long originalCustomerId = currentCustomer.getOriginalCustomerId();
        String payOrderId = request.getParameter("exchangeOrderId");
        try {
            ThirdPlatform thirdPlatform = thirdPlatformMapper.selectByNid(registFrom);
            String authUrl = thirdPlatform.getAuthUrl();

            Map<String,String> requestParam = new HashMap<>();
            requestParam.put("exchangeOrderId",payOrderId);
            requestParam.put("customerId",originalCustomerId.toString());
            requestParam.put("type","bondStockExchange");

            ThirdRetMsg thirdRetMsg = ThirdHttpTool.httpEncrypt(requestParam, authUrl + ThirdTxCode.查询合同.url());
            String code = thirdRetMsg.getCode();
            String codeDesc = thirdRetMsg.getCodeDesc();
            Map<String, String> data_ = thirdRetMsg.getData();
            if("OK".equals(code)){
                data.put("contractPath",data_.get("contractPath"));
                returnMessage.setData(data);
                ReturnMessage.wrapReturnMessageSuccess(returnMessage);
            }else {
                ReturnMessage.wrapReturnMessage(returnMessage, ResultCode.SYSTEM_EXCEPTION.setDesc(codeDesc));
            }
        } catch (Exception e) {
            ReturnMessage.wrapReturnMessage(returnMessage, ResultCode.SYSTEM_EXCEPTION);
            LOGGER.error("查看兑换合同",e);
        } finally {
            return returnMessage;
        }
    }
}
