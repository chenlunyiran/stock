package com.twotiger.stock.customer.controller;

import com.twotiger.shop.cache.api.CacheApi;
import com.twotiger.shop.core.entity.ShopCustomer;
import com.twotiger.shop.core.service.ShopCustomerService;
import com.twotiger.stock.customer.common.ReturnMessage;
import com.twotiger.stock.customer.enums.ResultCode;
import com.twotiger.stock.customer.util.SessionUtil;
import com.twotiger.shop.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by alean on 2018/11/22.
 */
@Controller
@RequestMapping("/common")
public class AuthController {

    private static final String AUTH_PREFIX = "AUTH_PREFIX_";

    @Autowired
    private CacheApi<String> cacheApi;

    @Autowired
    private ShopCustomerService shopCustomerService;

    @PostMapping("auth")
    @ResponseBody
    public ReturnMessage auth(String idCard, String name){

        Long id = SessionUtil.getCurrentCustomer().getId();

        ReturnMessage retMsg = new ReturnMessage();

        ShopCustomer currentCustomer = SessionUtil.getCurrentCustomer();
        String name1 = currentCustomer.getName();
        String idCard1 = currentCustomer.getIdCard();

        if(name1.equals(name) && idCard1.equals(idCard)){
            ReturnMessage.wrapReturnMessageSuccess(retMsg);
            boolean exists = cacheApi.exists(AUTH_PREFIX + id);
            if(exists){
                String s = cacheApi.get(AUTH_PREFIX + id);
                if(!s.equals(idCard)){
                    cacheApi.replace(AUTH_PREFIX + id,s,idCard);
                }
            }else {
                cacheApi.put(AUTH_PREFIX + id,idCard);
            }
        }else {
            ReturnMessage.wrapReturnMessage(retMsg, ResultCode.INVALID_PARAM);
        }
        return retMsg;
    }
    @GetMapping("passwordView")
    public String passwordView(HttpServletRequest request, String whitchFrom, String shopperId, Long goodsId, Long addressId){
        request.setAttribute("whitchFrom",whitchFrom);
        request.setAttribute("shopperId",shopperId);
        request.setAttribute("goodsId",goodsId);
        request.setAttribute("addressId",addressId);
        return "/common/passwordView.html";
    }


    @PostMapping("resetPayPw")
    @ResponseBody
    public ReturnMessage resetPayPw(String oldPw,String newPw){
        ReturnMessage retMsg = new ReturnMessage();
        ShopCustomer currentCustomer = SessionUtil.getCurrentCustomer();
        Long id = currentCustomer.getId();
        try {
            boolean num = StringUtil.isNum(oldPw);
            boolean num1 = StringUtil.isNum(newPw);
            if(!oldPw.equals(newPw) || !num || !num1){
                ReturnMessage.wrapReturnMessage(retMsg,ResultCode.INVALID_PARAM);
                return  retMsg;
            }
            boolean exists = cacheApi.exists(AUTH_PREFIX + id);
            if(!exists){
                ReturnMessage.wrapReturnMessage(retMsg,ResultCode.INVALID_PARAM.setDesc("请进行身份验证"));
                return retMsg;
            }
            String andRemove = cacheApi.get(AUTH_PREFIX + id);
            shopCustomerService.setTradePassword(id,andRemove,newPw);
            ReturnMessage.wrapReturnMessageSuccess(retMsg);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cacheApi.remove(AUTH_PREFIX + id);
        }
        return retMsg;
    }
}
