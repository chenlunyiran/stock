package com.twotiger.stock.customer.controller;

import com.twotiger.shop.core.entity.ShopCustomerAddress;
import com.twotiger.shop.core.service.ShopCustomerAddressService;
import com.twotiger.stock.customer.common.ReturnMessage;
import com.twotiger.stock.customer.enums.ResultCode;
import com.twotiger.stock.customer.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by alean on 2018/11/15.
 */
@Controller
@RequestMapping("/own/addressManager")
public class AddressController {

    @Autowired
    private ShopCustomerAddressService shopCustomerAddressService;

    /**
     * 增加地址视图
     * @return
     */
    @GetMapping("addAddressView")
    public String addAddressView(HttpServletRequest request,String whitchFrom,String shopperId,Long goodsId,Long addressId){
        request.setAttribute("whitchFrom",whitchFrom);
        request.setAttribute("shopperId",shopperId);
        request.setAttribute("goodsId",goodsId);
        request.setAttribute("addressId",addressId);
        return "own/addAddressView.html";
    }


    /**
     * 编辑地址视图
     * @return
     */
    @GetMapping("modifyAddressView")
    public String modifyAddressView(String id,HttpServletRequest request,String whitchFrom,String shopperId,Long goodsId,Long addressId){
        ShopCustomerAddress shopCustomerAddress = shopCustomerAddressService.queryByPrimaryKeyAndStatus(Long.valueOf(id));
        request.setAttribute("shopCustomerAddress",shopCustomerAddress);
        request.setAttribute("whitchFrom",whitchFrom);
        request.setAttribute("shopperId",shopperId);
        request.setAttribute("goodsId",goodsId);
        request.setAttribute("addressId",addressId);
        return "own/modifyAddressView.html";
    }

    /**
     * 编辑地址
     * @return
     */
    @PostMapping("modifyAddress")
    public void modifyAddress(ShopCustomerAddress shopCustomerAddress, HttpServletRequest request, HttpServletResponse response){
        String type = request.getParameter("type");
        Long id = SessionUtil.getCurrentCustomer().getId();
        switch (type){
            case "insert":
                shopCustomerAddress.setCustomerId(id);
                shopCustomerAddressService.insert(shopCustomerAddress);
                break;
            case "update":
                ShopCustomerAddress shopCustomerAddress_ = shopCustomerAddressService.queryByPrimaryKeyAndStatus(shopCustomerAddress.getId());
                Long customerId = shopCustomerAddress_.getCustomerId();
                shopCustomerAddress.setCustomerId(id);
                /**
                 * 判断是否本人操作
                 */
                if(!customerId.equals(id)){
                    break;
                }
                shopCustomerAddressService.updateSelective(shopCustomerAddress);
                break;
        }
        try {
            String whitchFrom = request.getParameter("whitchFrom");
            if("choose".equals(whitchFrom) && "insert".equals(type)){
                Long addressId = shopCustomerAddress.getId();
                String goodsId = request.getParameter("goodsId");
                String shopperId = request.getParameter("shopperId");
                response.sendRedirect("/rushBuy?id="+goodsId+"&shopperId="+shopperId+"&addressId="+addressId);
            }else if("choose".equals(whitchFrom) && "update".equals(type)){
                String addressId = request.getParameter("addressId");
                String goodsId = request.getParameter("goodsId");
                String shopperId = request.getParameter("shopperId");
                response.sendRedirect("/own/addressManager?whitchFrom=choose&goodsId="+goodsId+"&shopperId="+shopperId+"&addressId="+addressId);
            }else{
                response.sendRedirect("/own/addressManager");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 编辑地址
     * @return
     */
    @ResponseBody
    @PostMapping("deleteAddress")
    public ReturnMessage deleteAddress(ShopCustomerAddress shopCustomerAddress){
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        Long id = shopCustomerAddress.getId();

        shopCustomerAddress = shopCustomerAddressService.queryByPrimaryKeyAndStatus(id);
        Long customerId_ = shopCustomerAddress.getCustomerId();
        /***
         * 判断是否本人操作
         */
        if(!customerId_.equals(customerId)){
            ReturnMessage<Object> rm = new ReturnMessage<>();
            ReturnMessage.wrapReturnMessage(rm,ResultCode.INVALID_PARAM);
            return rm;
        }
        ShopCustomerAddress sa = new ShopCustomerAddress();
        sa.setId(shopCustomerAddress.getId());
        sa.setStatus((byte)0);
        shopCustomerAddressService.updateSelective(sa);
        ReturnMessage<Object> rm = new ReturnMessage<>();
        ReturnMessage.wrapReturnMessageSuccess(rm);
        return rm;
    }
}
