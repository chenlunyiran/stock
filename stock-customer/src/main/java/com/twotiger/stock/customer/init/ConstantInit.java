package com.twotiger.stock.customer.init;

import com.twotiger.shop.cache.jedis.JedisActuator;
import com.twotiger.shop.core.entity.ShopConsConfig;
import com.twotiger.shop.core.service.ShopConsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  固定变量参数初始化
 */
@Component
@Order(1)
public class ConstantInit implements CommandLineRunner {

    @Autowired
    private ShopConsConfigService shopConsConfigService;

    @Autowired
    private JedisActuator jedisActuator;

    @Override
    public void run(String... args) throws Exception {
        List<ShopConsConfig> configs = shopConsConfigService.queryByStatus(1);
        for (ShopConsConfig config : configs) {
            jedisActuator.execute(jedis -> jedis.set(config.getNid(),config.getValue()));
        }
    }
}
