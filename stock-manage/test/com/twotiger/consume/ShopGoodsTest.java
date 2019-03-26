package com.twotiger.consume;

import com.twotiger.shop.core.entity.entityvo.ShopGoodsInfoVo;
import com.twotiger.shop.core.service.ShopGoodsInfoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShopGoodsTest extends BaseCase {
    @Autowired
    ShopGoodsInfoService shopGoodsInfoService;


    @Test
    public void  test(){
        List<ShopGoodsInfoVo> shopGoodsInfos = new ArrayList<>();
        ShopGoodsInfoVo s1 = new ShopGoodsInfoVo();
        s1.setId(44L);
        s1.setCount(1);
        shopGoodsInfos.add(s1);

        ShopGoodsInfoVo s2 = new ShopGoodsInfoVo();
        s2.setId(43L);
        s2.setCount(3);
        shopGoodsInfos.add(s2);

        ShopGoodsInfoVo s3 = new ShopGoodsInfoVo();
        s3.setId(42L);
        s3.setCount(1);
        shopGoodsInfos.add(s3);

        BigDecimal bigDecimal = shopGoodsInfoService.calculateTransportAmount(shopGoodsInfos);
        System.out.println(bigDecimal);
    }
}
