package com.twotiger.shop.job.handler;

import com.twotiger.shop.core.entity.ShopSellRule;
import com.twotiger.shop.core.service.ShopGoodsInfoService;
import com.twotiger.shop.core.service.impl.ShopSellRuleServiceImpl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.glue.GlueTypeEnum;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 上架任务
 */


@JobHandler(value="launchHandler")
@Component
public class LaunchHandler extends IJobHandler {
    @Autowired
    ShopSellRuleServiceImpl shopSellRuleService;

    @Autowired
    ShopGoodsInfoService shopGoodsInfoService;


    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("定时上架任务开始执行[LaunchHandler.start]");
        //需要上架
        List<ShopSellRule> needLaunchGoodsByTimer = shopSellRuleService.findNeedLaunchGoodsByTimer();


        //需要下架
        List<ShopSellRule> needNoLaunchGoodsByTimer = shopSellRuleService.findNeedNoLaunchGoodsByTimer();


        //上架
        needLaunchGoodsByTimer.forEach( s -> {
            XxlJobLogger.log("上架[LaunchHandler],上架商品id：{},数据库上下架时间为：{}，{}",s.getId(),s.getLaunchStartTime(),s.getLaunchEndTime());
            shopGoodsInfoService.launchGoods(s.getGoodsId());
        });

        //下架
        needNoLaunchGoodsByTimer.forEach( s -> {
            XxlJobLogger.log("下架[LaunchHandler],下架商品id：{},数据库上下架时间为：{}，{}",s.getId(),s.getLaunchStartTime(),s.getLaunchEndTime());
            ;
            shopGoodsInfoService.unLaunchGoods(s.getGoodsId());
        });


        XxlJobLogger.log("定时上架任务开始执行完毕[LaunchHandler.end]");
        return IJobHandler.SUCCESS;

    }
}
