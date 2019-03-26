package com.twotiger.stock.customer.init;

import com.twotiger.shop.core.service.ShopGoodsCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @description: 库存初始化
 * @author: NYE
 * @create: 2018-11-27 18:10
 **/
@Component
@Order(2)
public class ShopStockInit implements CommandLineRunner {

    @Autowired
    private ShopGoodsCacheService shopGoodsCacheService;

    @Override
    public void run(String... args) throws Exception {
        shopGoodsCacheService.updateCacheStock();
    }
}
