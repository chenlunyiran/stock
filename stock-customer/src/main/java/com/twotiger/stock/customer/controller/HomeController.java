package com.twotiger.stock.customer.controller;

import com.ibeetl.admin.core.util.PlatformException;
import com.twotiger.shop.cache.api.LockApi;
import com.twotiger.shop.core.entity.*;
import com.twotiger.shop.core.entity.entityvo.ShopGoodsInfoVo;
import com.twotiger.shop.core.service.ShopConsumeOrderService;
import com.twotiger.shop.core.service.impl.HomeServiceImpl;
import com.twotiger.stock.customer.common.ReturnMessage;
import com.twotiger.stock.customer.enums.ResultCode;
import com.twotiger.stock.customer.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wxming.
 * @date 2018/11/16.
 */
@RestController
@RequestMapping("/")
public class HomeController {

    @Autowired
    private HomeServiceImpl homeService;
    @Autowired
    private ShopConsumeOrderService shopConsumeOrderService;
    @Autowired
    private LockApi lockApi;
    /**
     * 商城首页
     * @return
     */
    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mView = new ModelAndView("/index.html");
        List<ShopGoodsInfoVo> goodsList = homeService.queryGoodsList();
        mView.addObject("goodsList",goodsList);
        List<ShopConsumeGoodsOrder> goodsOrderList = homeService.queryGoodsOrderList();
        if(goodsOrderList.size()>=10){
            mView.addObject("goodsOrderList",goodsOrderList);
        }
        //购物车有效商品种类数
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        Integer cartNum = homeService.queryCartNum(customerId);
        mView.addObject("cartNum",cartNum);
        return mView;
    }
    /**
     * 购物车页
     * @return
     */
    @RequestMapping("/myShopCart")
    public ModelAndView myShopCart() {
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        ModelAndView mView = new ModelAndView("/goods/shoptrolley.html");
        //购物车有效商品种类数
        Integer cartNum = homeService.queryCartNum(customerId);
        List<ShopGoodsInfoVo> goodsListYes = homeService.queryCartList(customerId,true);
        List<ShopGoodsInfoVo> goodsListNo = homeService.queryCartList(customerId,false);
        mView.addObject("cartNum",cartNum);
        mView.addObject("goodsListYes",goodsListYes);
        mView.addObject("goodsListNo",goodsListNo);
        return mView;
    }
    /**
     * 商品详情页
     * @return
     */
    @RequestMapping("/goodsDetail")
    public ModelAndView goodsDetail(Long id,String whichFrom) {
        ShopGoodsInfoVo thisGoods = homeService.queryThisGoods(id);
        if(thisGoods==null){
            ModelAndView mView4 = new ModelAndView("/notice/errorPage");
            return mView4;
        }
        ModelAndView mView = new ModelAndView("/goods/detail.html");
        //查询处理商品简介
        ShopGoodsExtend goodsExtend = homeService.queryGoodsExtend(id);
        //查询商品图片列表
        List<ShopImg> imgList = homeService.queryImgList(id);
        mView.addObject("thisGoods",thisGoods!=null?thisGoods:new ShopGoodsInfoVo());
        mView.addObject("goodsExtend",goodsExtend);
        mView.addObject("imgList",imgList);
        mView.addObject("whichFrom",whichFrom);
        return mView;
    }

    /**
     * 商品详情页添加购物车
     * @param goodsId
     * @return
     */
    @ResponseBody
    @PostMapping("/addCart")
    public ReturnMessage addCart(Long goodsId){
        ReturnMessage<Object> rm = new ReturnMessage<>();
        ShopGoodsInfoVo thisGoods = homeService.checkQuantity(goodsId);
        if(thisGoods!=null){
            Long customerId = SessionUtil.getCurrentCustomer().getId();
            //给同一个用户加锁
            String myLock = "addCart-"+customerId+"-"+goodsId;
            try {
                lockApi.acquire(myLock);
                ShopShoppingCart cart = homeService.queryCartByGoods(goodsId,customerId);
                if(cart!=null){
                    if(thisGoods.getAvaliableQuantity()>cart.getCount()){
                        int num = homeService.addCartNum(cart.getId(),goodsId,customerId);
                        ReturnMessage.wrapReturnMessageSuccess(rm);
                    }else if(thisGoods.getAvaliableQuantity()<cart.getCount()){
                        //清购物车，重新从1添加
                        homeService.quChuCartReAdd(goodsId,customerId);
                        int num = homeService.addCartNum(cart.getId(),goodsId,customerId);
                        ReturnMessage.wrapReturnMessageSuccess(rm);
                    }else{
                        ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
                    }
                }else{
                    //插入一条新的数据
                    int num = homeService.InsertOneCart(goodsId,customerId,thisGoods.getSellPrice());
                    ReturnMessage.wrapReturnMessageSuccess(rm);
                }
            } finally {
                lockApi.release(myLock);
            }
        }else{
            ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
        }
        return rm;
    }

    /**
     * 马上抢或购物车结算，进入确认订单页
     * @return
     */
    @RequestMapping("/rushBuy")
    public ModelAndView rushBuy(Long id,Long addressId,String shopperId) {
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        ModelAndView mView = new ModelAndView("/goods/sureOder.html");

        //可用余额
        ShopCustomerAccount customerAccount = homeService.queryCustomerAccount(customerId);
        BigDecimal avaliable = BigDecimal.ZERO;
        if(customerAccount != null && customerAccount.getAvaliable()!=null){
            avaliable = customerAccount.getAvaliable();
        }
        //查用户信息是否有支付密码
        ShopCustomer customer = homeService.queryCustomer(customerId);
        if(customer != null && customer.getTradePassword()!=null && customer.getTradePassword().trim().length()>0){
            mView.addObject("havePwd","yes");
        }else{
            mView.addObject("havePwd","no");
        }
        //收货地址
        ShopCustomerAddress address;
        if(addressId==null){
            address = homeService.queryOneAddress(customerId);
            if(address!=null){
                addressId = address.getId();
            }
        }else{
            address = homeService.queryAddressById(addressId);
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        //
        String kuCun="have";

        List<ShopGoodsInfoVo> goodsList = new ArrayList<>();
        //id!=null是马上抢页面过来的
        if(id != null){
            //跳转回哪个页面用。
            kuCun="FromDetail";
            ShopGoodsInfoVo thisGoods = homeService.queryThisGoods(id);
            if(thisGoods!=null){
                thisGoods.setCount(1);
                totalPrice=thisGoods.getSellPrice().add(thisGoods.getFirstPrice());
                goodsList.add(thisGoods);
            }
            //购物车结算页过来
        }else if(shopperId != null && shopperId.indexOf(",") > -1 && shopperId.length()>1){
            List<Long> shopperIdList = new ArrayList<>();
            String[] split = shopperId.substring(1).split(",");
            for (String aId : split) {
                try{
                    shopperIdList.add(new Long(aId));
                }catch (Exception e){
                    //防止非法操作输入id不是数字。
                }
            }
            if(shopperIdList.size()>0){
                goodsList = homeService.queryOrderGoodsList(shopperIdList,customerId);
                for (ShopGoodsInfoVo goods:goodsList) {
                    BigDecimal thisPrice = goods.getSellPrice().multiply(new BigDecimal(String.valueOf(goods.getCount()))).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal transMoney = goods.getFirstPrice().add(goods.getNextPrice().multiply(new BigDecimal(String.valueOf(goods.getCount()-1))).setScale(2, BigDecimal.ROUND_HALF_UP));
                    goods.setFirstPrice(transMoney);
                    totalPrice = totalPrice.add(thisPrice).add(transMoney);
                }
            }
            if(goodsList.size()<=0){
                kuCun="no";
            }else if(goodsList.size()<shopperIdList.size()){
                kuCun="buFen";
            }
        }
        //算运费
        /*if(goodsList!=null && goodsList.size()>0){
            BigDecimal transMoney = shopGoodsInfoService.calculateTransportAmount(goodsList);
            totalPrice = totalPrice.add(transMoney);
        }*/

        mView.addObject("goodsList",goodsList);
        mView.addObject("totalPrice",totalPrice);
        mView.addObject("avaliable",avaliable);
        mView.addObject("address",address);
        mView.addObject("id",id);
        mView.addObject("addressId",addressId);
        mView.addObject("shopperId",shopperId);
        mView.addObject("kuCun",kuCun);
        return mView;
    }

    /**
     * 实时校验库存（一件）
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/checkQuantity")
    public ReturnMessage checkQuantity(Long id){
        ReturnMessage<Object> rm = new ReturnMessage<>();
        ShopGoodsInfoVo thisGoods = homeService.checkQuantity(id);
        if(thisGoods!=null){
            ReturnMessage.wrapReturnMessageSuccess(rm);
        }else{
            ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
        }
        return rm;
    }
    /**
     * 添加，实时校验库存（与本人购物车数量比）
     * @param goodsId
     * @return
     */
    @ResponseBody
    @PostMapping("/checkQuantityByCart")
    public ReturnMessage checkQuantityByCart(Long goodsId){
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        ReturnMessage<Object> rm = new ReturnMessage<>();
        /*ShopGoodsInfoVo thisGoods = homeService.checkQuantityByCart(goodsId,customerId);
        if(thisGoods!=null){
            ReturnMessage.wrapReturnMessageSuccess(rm);
        }else{
            ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
        }*/
        return rm;
    }
    /**
     * 购物车一个一个添加
     * @param cartId
     * @param goodsId
     * @return
     */
    @ResponseBody
    @PostMapping("/addCartNum")
    public ReturnMessage addCartNum(Long cartId,Long goodsId){
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        ReturnMessage<Object> rm = new ReturnMessage<>();
        ShopGoodsInfoVo thisGoods = homeService.checkQuantityByCart(cartId,goodsId,customerId);
        if(thisGoods!=null){
            int num = homeService.addCartNum(cartId,goodsId,customerId);
            ReturnMessage.wrapReturnMessageSuccess(rm);
        }else{
            ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
        }
        return rm;
    }
    /**
     * 购物车一个一个减
     * @param cartId
     * @return
     */
    @ResponseBody
    @PostMapping("/jianCartNum")
    public ReturnMessage jianCartNum(Long cartId){
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        ReturnMessage<Object> rm = new ReturnMessage<>();
        int num = homeService.jianCartNum(cartId,customerId);
        ReturnMessage.wrapReturnMessageSuccess(rm);
        return rm;
    }
    /**
     * 购物车删除
     * @param cartId
     * @return
     */
    @ResponseBody
    @PostMapping("/deleteOneCart")
    public ReturnMessage deleteOneCart(Long cartId){
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        ReturnMessage<Object> rm = new ReturnMessage<>();
        int num = homeService.deleteOneCart(cartId,customerId);
        ReturnMessage.wrapReturnMessageSuccess(rm);
        return rm;
    }

    /**
     * 确认订单生产订单
     * @param goodsId
     * @param addressId
     * @param shopperId
     * @return
     */
    @ResponseBody
    @PostMapping("/parseOrder")
    public ReturnMessage parseOrder(Long goodsId,Long addressId,String shopperId,BigDecimal totalPrice){
        ReturnMessage<Object> rm = new ReturnMessage<>();
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        BigDecimal totalPriceNew = BigDecimal.ZERO;

        List<ShopGoodsInfoVo> goodsList = new ArrayList<>();
        if(goodsId != null){
            ShopGoodsInfoVo thisGoods = homeService.queryThisGoods(goodsId);
            if(thisGoods!=null){
                thisGoods.setCount(1);
                totalPriceNew=thisGoods.getSellPrice().add(thisGoods.getFirstPrice());
                goodsList.add(thisGoods);
            }
        }else if(shopperId != null && shopperId.indexOf(",") > -1 && shopperId.length()>1){
            List<Long> shopperIdList = new ArrayList<>();
            String[] split = shopperId.substring(1).split(",");
            for (String aId : split) {
                try{
                    shopperIdList.add(new Long(aId));
                }catch (Exception e){
                    //防止非法操作输入id不是数字。
                }
            }
            if(shopperIdList.size()>0){
                goodsList = homeService.queryOrderGoodsList(shopperIdList,customerId);
                for (ShopGoodsInfoVo goods:goodsList) {
                    BigDecimal thisPrice = goods.getSellPrice().multiply(new BigDecimal(String.valueOf(goods.getCount()))).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal transMoney = goods.getFirstPrice().add(goods.getNextPrice().multiply(new BigDecimal(String.valueOf(goods.getCount()-1))).setScale(2, BigDecimal.ROUND_HALF_UP));
                    goods.setFirstPrice(transMoney);
                    totalPriceNew = totalPriceNew.add(thisPrice).add(transMoney);
                }
            }
        }
        //检验最新金额
        if(totalPrice.compareTo(totalPriceNew)!=0){
            ReturnMessage.wrapReturnMessage(rm, ResultCode.CHANGE);
        }else{
            BigDecimal totalFreight = BigDecimal.ZERO;
            List<ShopConsumeOrder> orderGoodsList = new ArrayList<>();
            for (ShopGoodsInfoVo goods:goodsList) {
                ShopConsumeOrder order = new ShopConsumeOrder();
                order.setGoodsId(goods.getId());
                order.setCount(goods.getCount());
                order.setFreight(goods.getFirstPrice());
                order.setPrice(goods.getSellPrice());
                orderGoodsList.add(order);
                totalFreight = totalFreight.add(goods.getFirstPrice());
            }
            try{
                Long orderId = shopConsumeOrderService.submitConsumeOrder(orderGoodsList,customerId,totalFreight,addressId);
                //下单成功减去购物车数量
                if(goodsId == null){
                    try{
                        homeService.quChuCart(orderGoodsList,customerId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                ReturnMessage.wrapReturnMessageSuccess(rm);
                rm.setData(orderId);
            }catch (Exception e){
                ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
            }
        }
        return rm;
    }

    /**
     * 订单支付
     * @param orderId
     * @param pwd
     * @return
     */
    @ResponseBody
    @PostMapping("/orderToPay")
    public ReturnMessage orderToPay(Long orderId,String pwd){
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        ReturnMessage<Object> rm = new ReturnMessage<>();
        try{
            Boolean result = shopConsumeOrderService.payConsumeOrder(pwd,customerId,orderId);
            if(result){
                ReturnMessage.wrapReturnMessageSuccess(rm);
            }else{
                ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
            }
        }catch (PlatformException e){
            rm.setCode("EX");
            rm.setMsg(e.getMessage());
        }catch (Exception e){
            ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
        }
        return rm;
    }
    /**
     * 订单支付一笔一笔
     * @param orderId
     * @param pwd
     * @return
     */
    @ResponseBody
    @PostMapping("/orderToPayOneByOne")
    public ReturnMessage orderToPayOneByOne(Long orderId,String pwd){
        Long customerId = SessionUtil.getCurrentCustomer().getId();
        ReturnMessage<Object> rm = new ReturnMessage<>();
        try{
            Boolean result = shopConsumeOrderService.payConsumeOrderOneByOne(pwd,customerId,orderId);
            if(result){
                ReturnMessage.wrapReturnMessageSuccess(rm);
            }else{
                ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
            }
        }catch (PlatformException e){
            rm.setCode("EX");
            rm.setMsg(e.getMessage());
        }catch (Exception e){
            ReturnMessage.wrapReturnMessage(rm, ResultCode.INVALID_PARAM);
        }
        return rm;
    }
    /**
     * 支付成功页
     * @return
     */
    @RequestMapping("/paySuccess")
    public ModelAndView paySuccess(BigDecimal totalPrice) {
        ModelAndView mView = new ModelAndView("/goods/paymentSucess.html");
        mView.addObject("totalPrice",totalPrice);
        return mView;
    }
}
