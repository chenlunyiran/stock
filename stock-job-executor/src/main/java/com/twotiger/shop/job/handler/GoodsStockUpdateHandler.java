package com.twotiger.shop.job.handler;

import com.twotiger.shop.core.service.ShopGoodsCacheService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 更新缓存中库存
 * @author: NYE
 * @create: 2018-11-28 17:03
 **/
@JobHandler(value = "goodsStockUpdateHandler")
@Component
public class GoodsStockUpdateHandler extends IJobHandler {

    @Autowired
    private ShopGoodsCacheService shopGoodsCacheService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        shopGoodsCacheService.updateCacheStock();
        return SUCCESS;
    }
}
