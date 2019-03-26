package com.twotiger.stock.customer.controller;

import com.github.pagehelper.PageInfo;
import com.twotiger.shop.core.entity.ShopConsumeGoodsOrder;
import com.twotiger.shop.core.pojo.PagePOJO;
import com.twotiger.shop.core.service.ShopConsumeGoodsOrderService;
import com.twotiger.stock.customer.common.ReturnMessage;
import com.twotiger.stock.customer.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by alean on 2018/11/19.
 */
@Controller
@RequestMapping("/own/order")
public class OrderController {

    @Autowired
    private ShopConsumeGoodsOrderService shopConsumeGoodsOrderService;

    @PostMapping("/list")
    @ResponseBody
    public ReturnMessage<PagePOJO<ShopConsumeGoodsOrder,Map<String,Object>>> list(PagePOJO<ShopConsumeGoodsOrder,Map<String,Object>> pagePOJO){

        ReturnMessage<PagePOJO<ShopConsumeGoodsOrder,Map<String,Object>>> pagePOJOReturnMessage = new ReturnMessage<>();

        ShopConsumeGoodsOrder shopConsumeGoodsOrder = new ShopConsumeGoodsOrder();
        shopConsumeGoodsOrder.setCustomerId(SessionUtil.getCurrentCustomer().getId());

        pagePOJO.setT(shopConsumeGoodsOrder);

        PageInfo<Map<String,Object>> pageInfo = shopConsumeGoodsOrderService.query(pagePOJO);
        List<Map<String, Object>> list = pageInfo.getList();
        pagePOJO.setData(list);
        pagePOJO.setTotal(pageInfo.getPages());

        pagePOJOReturnMessage.setData(pagePOJO);
        ReturnMessage.wrapReturnMessageSuccess(pagePOJOReturnMessage);
        return pagePOJOReturnMessage;
    }

}
