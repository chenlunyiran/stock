package com.twotiger.consume;

import com.twotiger.shop.cache.jedis.JedisActuator;
import com.twotiger.shop.core.entity.ShopConsumeOrder;
import com.twotiger.shop.core.executor.AsyncService;
import com.twotiger.shop.core.service.ShopConsumeOrderService;
import com.twotiger.shop.core.service.ShopGoodsInfoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class ConsumeOrderTest extends BaseCase{

    @Autowired
    private ShopConsumeOrderService shopConsumeOrderService;

    @Autowired
    private JedisActuator jedisActuator;

    @Test
    public void testPayOrderCancel(){
        try {
            shopConsumeOrderService.payOrderCancel();
            /*while (true){

            }*/
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Test
    public void testSubmitOrder(){

        ShopConsumeOrder order1 = new ShopConsumeOrder();
        order1.setCount(2);
        order1.setFreight(new BigDecimal("12"));
        order1.setPrice(new BigDecimal("20"));
        order1.setGoodsId(45);

        ShopConsumeOrder order2 = new ShopConsumeOrder();
        order2.setCount(3);
        order2.setFreight(new BigDecimal("12"));
        order2.setPrice(new BigDecimal("30"));
        order2.setGoodsId(46);

        ShopConsumeOrder order3 = new ShopConsumeOrder();
        order3.setCount(2);
        order3.setFreight(new BigDecimal("12"));
        order3.setPrice(new BigDecimal("20"));
        order3.setGoodsId(20);

        ShopConsumeOrder order4 = new ShopConsumeOrder();
        order4.setCount(2);
        order4.setFreight(new BigDecimal("12"));
        order4.setPrice(new BigDecimal("30"));
        order4.setGoodsId(29);

        List<ShopConsumeOrder> goodsList = new ArrayList<>();
        goodsList.add(order1);
        goodsList.add(order2);

        shopConsumeOrderService.submitConsumeOrder(goodsList,51,new BigDecimal("20"),8);
    }

    @Test
    public void testPayConsumeOrder(){
        shopConsumeOrderService.payConsumeOrder("123456",1,491);
    }

    @Test
    public void test1(){
        String[] arrays = {"totalQuantity","avaliableQuantity","frozenQuantity"};
        List<String> execute = jedisActuator.execute(jedis -> jedis.hmget(String.valueOf(16),arrays));
        for (String str : execute){
            System.out.println(str);
        }
    }


    class MyThread extends Thread{
        long orderId;
        CountDownLatch begin;
        CountDownLatch end;

        public MyThread(long orderId,CountDownLatch begin, CountDownLatch end) {
            this.orderId=orderId;
            this.begin=begin;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                begin.await();

                System.out.println(Thread.currentThread().getName()+" 开始支付");

                shopConsumeOrderService.payConsumeOrder("123456",51,orderId);
                //System.out.println(Thread.currentThread().getName()+"orderId="+orderId);

                System.out.println(Thread.currentThread().getName()+" 支付结束");

                end.countDown();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    class MyThread1 extends Thread{
        long orderId;
        CountDownLatch begin;
        CountDownLatch end;

        public MyThread1(long orderId,CountDownLatch begin, CountDownLatch end) {
            this.orderId=orderId;
            this.begin=begin;
            this.end = end;
        }

        @Override
        public void run() {
            try {
                begin.await();

                System.out.println(Thread.currentThread().getName()+" 开始下单");

                Random ran = new Random();
                ShopConsumeOrder order1 = new ShopConsumeOrder();
                order1.setCount(ran.nextInt(3)+1);
                order1.setFreight(new BigDecimal("12"));
                order1.setPrice(new BigDecimal("120"));
                order1.setGoodsId(45);

                ShopConsumeOrder order2 = new ShopConsumeOrder();
                order2.setCount(ran.nextInt(3)+1);
                order2.setFreight(new BigDecimal("12"));
                order2.setPrice(new BigDecimal("100"));
                order2.setGoodsId(46);

                List<ShopConsumeOrder> goodsList = new ArrayList<>();
                goodsList.add(order1);
                goodsList.add(order2);

                shopConsumeOrderService.submitConsumeOrder(goodsList,51,new BigDecimal("40"),8);

                System.out.println(Thread.currentThread().getName()+" 下单结束");

                end.countDown();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private ShopGoodsInfoService shopGoodsInfoService;

    @Test
    public void testUpdate(){

        shopGoodsInfoService.updateGoodsInventory(76,2,1);

        shopGoodsInfoService.updateGoodsInventory(76,2,2);

        shopGoodsInfoService.updateGoodsInventory(76,2,3);

        shopGoodsInfoService.updateGoodsInventory(76,2,4);


    }

    @Test
    public void testBatch(){

        for(int i = 1322 ; i <= 10 ; i++){
            shopConsumeOrderService.payConsumeOrder("123456",51,i);
        }

    }

    @Autowired
    private AsyncService asyncService;

    @Test
    public void test000(){
        for(int i = 0 ; i < 10 ; i++){
            asyncService.executeAsync(i);
        }
    }

    @Test
    public void testBatchPayConsumeOrder(){

        /*long orderId = 657;
        for(int i = 657 ; i <= 805 ; i++){
            shopConsumeOrderService.payConsumeOrder("123456",27,orderId);
            orderId++;
        }*/

        CountDownLatch begin = new CountDownLatch(1);

        CountDownLatch end = new CountDownLatch(30);

        long orderId = 2288;

        for(int i = 1 ; i <= 30 ; i++){

            Thread thread = new MyThread(orderId,begin,end);
            thread.start();

            orderId++;
        }

        try{

            System.out.println("1 秒后统一开始");

            Thread.sleep(1000);

            begin.countDown();

            end.await();

            System.out.println("停止比赛");

        }catch (Exception e){
            e.printStackTrace();
        }

        while (true){

        }
    }
}
