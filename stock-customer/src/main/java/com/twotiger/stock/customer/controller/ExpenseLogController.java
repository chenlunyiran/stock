package com.twotiger.stock.customer.controller;

import com.github.pagehelper.PageInfo;
import com.twotiger.shop.core.entity.ShopConsumeGoodsOrder;
import com.twotiger.shop.core.entity.ShopConsumePayOrder;
import com.twotiger.shop.core.entity.ShopCustomerAccountLog;
import com.twotiger.shop.core.entity.ShopExchangeOrder;
import com.twotiger.shop.core.pojo.PagePOJO;
import com.twotiger.shop.core.service.ShopConsumeGoodsOrderService;
import com.twotiger.shop.core.service.ShopConsumePayOrderService;
import com.twotiger.shop.core.service.ShopCustomerAccountLogService;
import com.twotiger.shop.core.service.impl.ShopExchangeOrderServiceImpl;
import com.twotiger.stock.customer.common.ReturnMessage;
import com.twotiger.stock.customer.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by alean on 2018/11/19.
 */
@Controller
@RequestMapping("/own/expenseLog")
public class ExpenseLogController {

    @Autowired
    ShopExchangeOrderServiceImpl shopExchangeOrderService;

    @Autowired
    private ShopCustomerAccountLogService shopCustomerAccountLogService;

    @Autowired
    private ShopConsumeGoodsOrderService shopConsumeGoodsOrderService;

    @Autowired
    private ShopConsumePayOrderService shopConsumePayOrderService;


    @PostMapping("list")
    @ResponseBody
    public ReturnMessage<PagePOJO<ShopCustomerAccountLog,ShopCustomerAccountLog>> list(PagePOJO<ShopCustomerAccountLog,ShopCustomerAccountLog> pagePOJO){
        ReturnMessage<PagePOJO<ShopCustomerAccountLog,ShopCustomerAccountLog>> pagePOJOReturnMessage = new ReturnMessage<>();

        ShopCustomerAccountLog shopCustomerAccountLog = new ShopCustomerAccountLog();
        shopCustomerAccountLog.setCustomerId(SessionUtil.getCurrentCustomer().getId());

        pagePOJO.setT(shopCustomerAccountLog);

        PageInfo<ShopCustomerAccountLog> pageInfo = shopCustomerAccountLogService.query(pagePOJO);
        List<ShopCustomerAccountLog> list = pageInfo.getList();
        pagePOJO.setData(list);
        pagePOJO.setTotal(pageInfo.getPages());

        pagePOJOReturnMessage.setData(pagePOJO);
        ReturnMessage.wrapReturnMessageSuccess(pagePOJOReturnMessage);
        return pagePOJOReturnMessage;
    }

    @GetMapping("detail")
    public String detail(Long id, Integer type,HttpServletRequest request){
        request.setAttribute("type",type);

        switch (type){
            case 1:
                ShopExchangeOrder shopExchangeOrder = shopExchangeOrderService.queryById(id);
                request.setAttribute("shopExchangeOrder",shopExchangeOrder);
                break;
            case 2:
                ShopConsumePayOrder shopConsumePayOrder = shopConsumePayOrderService.queryById(id);
                List<ShopConsumeGoodsOrder> shopConsumeGoodsOrders = shopConsumeGoodsOrderService.queryByPayOrderId(shopConsumePayOrder.getPayOrderId());
                request.setAttribute("shopConsumeGoodsOrders",shopConsumeGoodsOrders);
                break;
            case 3:
                ShopConsumeGoodsOrder shopConsumeGoodsOrder = shopConsumeGoodsOrderService.queryById(id);
                request.setAttribute("shopConsumeGoodsOrder",shopConsumeGoodsOrder);
                break;
        }
        return "/own/expenseLogDetail.html";
    }
}
