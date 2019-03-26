package com.twotiger.stock.customer.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.twotiger.shop.core.entity.ShopExchangeOrder;
import com.twotiger.shop.core.pojo.PagePOJO;
import com.twotiger.shop.core.service.ShopCustomerAccountService;
import com.twotiger.shop.core.service.ShopExchangeOrderService;
import com.twotiger.stock.customer.common.ReturnMessage;
import com.twotiger.stock.customer.enums.ResultCode;
import com.twotiger.stock.customer.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alean on 2018/11/22.
 */
@Controller
@RequestMapping("/own")
public class ChargeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChargeController.class);

    @Autowired
    private ShopCustomerAccountService shopCustomerAccountService;

    @Autowired
    private ShopExchangeOrderService shopExchangeOrderService;


    /**
     * 兑换消费币
     * @param amount
     * @param response
     */
    @PostMapping("charge")
    @ResponseBody
    public ReturnMessage charge(String amount, HttpServletResponse response) {
        ReturnMessage returnMessage = new ReturnMessage<>();
        HashMap<String, Boolean> map = new HashMap<>();

        Long id = SessionUtil.getCurrentCustomer().getId();
        boolean result = false;
        try {
            result = shopCustomerAccountService.exChange(id, amount);
            map.put("result",result);
            returnMessage.setData(map);
            ReturnMessage.wrapReturnMessageSuccess(returnMessage);
        } catch (Exception e) {
            map.put("result",result);
            returnMessage.setData(map);
            ReturnMessage.wrapReturnMessage(returnMessage, ResultCode.SYSTEM_EXCEPTION);
            LOGGER.error("兑换消费币",e);
        } finally {
            return returnMessage;
        }
    }


    /**
     * 兑换记录View
     * @param request
     * @return
     */
    @GetMapping("chargeLogsView")
    public String chargeLogsView(HttpServletRequest request,HttpServletResponse response){

        Long id = SessionUtil.getCurrentCustomer().getId();
        ReturnMessage<PagePOJO<ShopExchangeOrder,ShopExchangeOrder>> pagePOJOReturnMessage = new ReturnMessage<>();

        PagePOJO<ShopExchangeOrder,ShopExchangeOrder> pagePOJO = new PagePOJO<>();
        ShopExchangeOrder shopExchangeOrder = new ShopExchangeOrder();
        shopExchangeOrder.setCustomerId(id);

        pagePOJO.setT(shopExchangeOrder);
        pagePOJO.setCurrentPage(1);
        pagePOJO.setPageSize(10);

        PageInfo<ShopExchangeOrder> pageInfo = shopExchangeOrderService.query(pagePOJO);
        List<ShopExchangeOrder> list = pageInfo.getList();
        pagePOJO.setData(list);
        pagePOJO.setTotal(pageInfo.getPages());

        pagePOJOReturnMessage.setData(pagePOJO);
        ReturnMessage.wrapReturnMessageSuccess(pagePOJOReturnMessage);
        request.setAttribute("data", JSON.toJSON(pagePOJOReturnMessage));

        //cookie管理
        Cookie[] cookies = request.getCookies();
        for (Cookie ck:cookies) {
            String name = ck.getName();
            if("JSESSIONID".equals(name)){
                ck.setMaxAge(60*10);
                response.addCookie(ck);
            }
        }

        return "/own/chargeLogsView.html";
    }

    /**
     * 兑换记录
     * @param request
     * @param pagePOJO
     * @return
     */
    @PostMapping("chargeLogs")
    @ResponseBody
    public ReturnMessage<PagePOJO<ShopExchangeOrder,ShopExchangeOrder>> chargeLogs(HttpServletRequest request,PagePOJO<ShopExchangeOrder,ShopExchangeOrder> pagePOJO){

        Long id = SessionUtil.getCurrentCustomer().getId();
        ReturnMessage<PagePOJO<ShopExchangeOrder,ShopExchangeOrder>> pagePOJOReturnMessage = new ReturnMessage<>();

        ShopExchangeOrder shopExchangeOrder = new ShopExchangeOrder();
        shopExchangeOrder.setCustomerId(id);
        pagePOJO.setT(shopExchangeOrder);

        PageInfo<ShopExchangeOrder> pageInfo = shopExchangeOrderService.query(pagePOJO);
        List<ShopExchangeOrder> list = pageInfo.getList();
        pagePOJO.setData(list);
        pagePOJO.setTotal(pageInfo.getPages());

        pagePOJOReturnMessage.setData(pagePOJO);
        ReturnMessage.wrapReturnMessageSuccess(pagePOJOReturnMessage);

        return pagePOJOReturnMessage;
    }
}
