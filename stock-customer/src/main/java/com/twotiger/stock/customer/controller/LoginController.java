package com.twotiger.stock.customer.controller;

import com.twotiger.shop.core.entity.ShopCustomer;
import com.twotiger.shop.core.service.ShopCustomerService;
import com.twotiger.stock.customer.util.SessionUtil;
import com.twotiger.shop.util.httpclient.ThirdRetMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lei on 2018/11/16.
 */
@RestController
public class LoginController {

    @Autowired
    private ShopCustomerService shopCustomerService;

    @RequestMapping("/login")
    public void firstLogin(String token, String key, String platform, HttpServletRequest request, HttpServletResponse response) throws Exception{
        ThirdRetMsg thirdRetMsg = shopCustomerService.queryThirdCustomerInfo(token, key, platform);
        if(thirdRetMsg==null){
            return;
        }
        if(!thirdRetMsg.getCode().equals("OK")){
            return;
        }
        String thirdUserId = thirdRetMsg.getData().get("customerId");
        ShopCustomer shopCustomer = shopCustomerService.findShopCustomer(Long.valueOf(thirdUserId), platform);
        if(shopCustomer==null){
            shopCustomer = shopCustomerService.regist(thirdRetMsg.getData(), platform, request.getRemoteAddr());
        }
        SessionUtil.setCurrentUser(shopCustomer);
        response.sendRedirect("/");
    }


}
