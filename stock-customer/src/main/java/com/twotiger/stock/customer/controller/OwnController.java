package com.twotiger.stock.customer.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.twotiger.shop.core.entity.*;
import com.twotiger.shop.core.pojo.PagePOJO;
import com.twotiger.shop.core.service.*;
import com.twotiger.shop.core.service.impl.HomeServiceImpl;
import com.twotiger.stock.customer.common.ReturnMessage;
import com.twotiger.stock.customer.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by alean on 2018/11/15.
 */
@Controller
@RequestMapping("/own")
public class OwnController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnController.class);

    @Autowired
    private ShopCustomerAddressService shopCustomerAddressService;

    @Autowired
    private ShopCustomerAccountLogService shopCustomerAccountLogService;

    @Autowired
    private ShopCustomerAccountService shopCustomerAccountService;

    @Autowired
    private ShopConsumeGoodsOrderService shopConsumeGoodsOrderService;

    @Autowired
    private ThirdPlatformService thirdPlatformService;
    @Autowired
    private HomeServiceImpl homeService;


    /**
     * 我的页
     * @return
     */
    @GetMapping("home")
    public String home(HttpServletRequest request){
        ShopCustomerAccount shopCustomerAccount = shopCustomerAccountService.selectByCustomerId(SessionUtil.getCurrentCustomer().getId());
        BigDecimal total = shopCustomerAccount.getTotal();
        request.setAttribute("total",total);
        //购物车有效商品种类数
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        Integer cartNum = homeService.queryCartNum(customerId);
        request.setAttribute("cartNum",cartNum);
        return "/own/home.html";
    }


    /**
     * 兑换消费币页面
     * @return
     */

    @GetMapping("chargeView")
    public String chargeView(HttpServletRequest request){
        ShopCustomer shopCustomer = SessionUtil.getCurrentCustomer();
        BigDecimal remainAmount;
        ThirdPlatform twotiger;
        try {
            remainAmount = shopCustomerAccountService.preExchange(shopCustomer.getId());
            twotiger = thirdPlatformService.query("twotiger");
        } catch (Exception e) {
            LOGGER.info("兑换消费币页面",e);
            return "/notice/errorPage";
        }
        request.setAttribute("remainAmount",remainAmount);
        request.setAttribute("exchangeRation",twotiger.getExchangeRatio());
        return "/own/chargeView.html";
    }

    /**
     * 消费金流水
     * @return
     */
    @GetMapping("expenseLogView")
    public String expenseLog(HttpServletRequest request){

        ReturnMessage<PagePOJO<ShopCustomerAccountLog,ShopCustomerAccountLog>> pagePOJOReturnMessage = new ReturnMessage<>();

        PagePOJO<ShopCustomerAccountLog,ShopCustomerAccountLog> pagePOJO  = new PagePOJO<>();
        ShopCustomerAccountLog shopCustomerAccountLog = new ShopCustomerAccountLog();
        shopCustomerAccountLog.setCustomerId(SessionUtil.getCurrentCustomer().getId());

        pagePOJO.setT(shopCustomerAccountLog);
        pagePOJO.setCurrentPage(1);
        pagePOJO.setPageSize(10);

        PageInfo<ShopCustomerAccountLog> pageInfo = shopCustomerAccountLogService.query(pagePOJO);
        List<ShopCustomerAccountLog> list = pageInfo.getList();
        pagePOJO.setData(list);
        pagePOJO.setTotal(pageInfo.getPages());

        pagePOJOReturnMessage.setData(pagePOJO);
        ReturnMessage.wrapReturnMessageSuccess(pagePOJOReturnMessage);
        request.setAttribute("data",JSON.toJSON(pagePOJOReturnMessage));


        ShopCustomerAccount shopCustomerAccount = shopCustomerAccountService.selectByCustomerId(SessionUtil.getCurrentCustomer().getId());
        BigDecimal total = shopCustomerAccount.getTotal();
        request.setAttribute("total",total);

        return "/own/expenseLogView.html";
    }

    /**
     * 订单列表
     * @return
     */
    @GetMapping("orderList")
    public String orderList(HttpServletRequest request){

        PagePOJO<ShopConsumeGoodsOrder,Map<String,Object>> pagePOJO = new PagePOJO<>();
        ReturnMessage<PagePOJO<ShopConsumeGoodsOrder,Map<String,Object>>> pagePOJOReturnMessage = new ReturnMessage<>();

        ShopConsumeGoodsOrder shopConsumeGoodsOrder = new ShopConsumeGoodsOrder();
        shopConsumeGoodsOrder.setCustomerId(SessionUtil.getCurrentCustomer().getId());

        pagePOJO.setT(shopConsumeGoodsOrder);
        pagePOJO.setCurrentPage(1);
        pagePOJO.setPageSize(10);

        PageInfo<Map<String,Object>> pageInfo = shopConsumeGoodsOrderService.query(pagePOJO);
        List<Map<String,Object>> list = pageInfo.getList();
        pagePOJO.setData(list);
        pagePOJO.setTotal(pageInfo.getPages());

        pagePOJOReturnMessage.setData(pagePOJO);
        ReturnMessage.wrapReturnMessageSuccess(pagePOJOReturnMessage);
        request.setAttribute("data", JSON.toJSON(pagePOJOReturnMessage));
        return "own/orderList.html";
    }

    /**
     * 地址管理
     * @return
     */
    @GetMapping("addressManager")
    public String addressManager(HttpServletRequest request,String whitchFrom,String shopperId,Long goodsId,Long addressId){
        Long id = SessionUtil.getCurrentCustomer().getId();
        List<ShopCustomerAddress> addresses = shopCustomerAddressService.queryByStatus(id);
        request.setAttribute("addresses",addresses);
        request.setAttribute("whitchFrom",whitchFrom);
        request.setAttribute("shopperId",shopperId);
        request.setAttribute("goodsId",goodsId);
        request.setAttribute("addressId",addressId);
        return "own/addressManager.html";
    }


    /**
     * 修改交易密码
     * @return
     */
    @GetMapping("modifyPassword")
    public String modifyPassword(HttpServletRequest request,String whitchFrom,String shopperId,Long goodsId,Long addressId){
        String name = SessionUtil.getCurrentCustomer().getName();
        request.setAttribute("name",name);
        request.setAttribute("whitchFrom",whitchFrom);
        request.setAttribute("shopperId",shopperId);
        request.setAttribute("goodsId",goodsId);
        request.setAttribute("addressId",addressId);
        return "/common/authentication.html";
    }
}
