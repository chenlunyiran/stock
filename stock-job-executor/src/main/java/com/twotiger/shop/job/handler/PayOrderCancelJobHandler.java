package com.twotiger.shop.job.handler;

import com.twotiger.shop.core.service.ShopConsumeOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定时取消支付订单
 */
@JobHandler(value = "payOrderCancelJobHandler")
@Component
public class PayOrderCancelJobHandler extends IJobHandler {

    @Autowired
    private ShopConsumeOrderService shopConsumeOrderService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        shopConsumeOrderService.payOrderCancel();
        return SUCCESS;
    }
}
