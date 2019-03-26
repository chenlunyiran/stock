queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_consume_goods_order t
    where del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopConsumeGoodsOrder.query")#
    @if(!isEmpty(customerId)){
        and  t.customer_id =#customerId#
    @}
    
  
queryConsumeOrderByCondition
===
       select 
          @pageTag(){
          	    sg.id,
                sp.customer_id,
                sc.`name`,
                sc.phone,
                sp.pay_order_id,
                sg.consume_goods_order_id,
                si.sku,
                si.`name` as goods_name,
                ss.`name` as supplier_name,
                sg.price,
                sg.count,
                sg.price*sg.count+sg.freight_amount amount,
                sg.freight_amount,
                sg.`status`,
                sp.pay_time,
                sg.logistics_company,
                sg.logistics_num,
                concat(
                    sg.logistics_company,
                    sg.logistics_num
                ) as logistics,
                sg.update_time,
                sg.create_time,
                addr.name as contact_name,
                addr.phone as contact_phone,
                concat(
                    addr.area,
                    addr.address
                ) as contact_address,
                sg.remark
          @}
          from shop_consume_goods_order sg
          LEFT JOIN shop_consume_pay_order sp ON sp.pay_order_id = sg.pay_order_id
          LEFT JOIN shop_customer sc ON sc.id = sp.customer_id
          LEFT JOIN shop_goods_info si ON sg.goods_id = si.id
          LEFT JOIN shop_supplier ss ON ss.id = si.supplier_id
          LEFT JOIN shop_customer_address addr on addr.id = sg.collect_good_address_id
          where sp.del_flag=0 
          @//数据权限，该sql语句功能点  
          and #function("shopConsumeOrder.query")#
          @if(!isEmpty(name)){
              and  sc.`name` = #name#
          @}
          
          @if(!isEmpty(phone)){
              and  sc.phone = #phone#
          @}
              
          @if(!isEmpty(payOrderId)){
             and  sp.pay_order_id = #payOrderId#
          @}
                  
          @if(!isEmpty(consumeGoodOrderId)){
             and  sg.consume_goods_order_id = #consumeGoodOrderId#
          @}
          
          @if(!isEmpty(status)){
              and  sg.`status` = #status#
          @}
          
          @if(!isEmpty(createDateMin)){
              and  sg.update_time>= #createDateMin#
          @}
          @if(!isEmpty(createDateMax)){
              and  sg.update_time< #nextDay(createDateMax)#
          @}
          order by sg.update_time desc
    
queryConsumeOrderById
===
    select 
          @pageTag(){
              sg.id,
          	sp.customer_id,
          	sc.`name`,
          	sc.phone,
          	sp.pay_order_id,
          	sg.consume_goods_order_id,
          	si.sku,
          	si.`name` as goods_name,
          	ss.`name` as supplier_name,
          	sg.price,
          	sg.count,
          	sg.price*sg.count+sg.freight_amount amount,
          	sg.freight_amount,
          	sg.`status`,
          	sp.pay_time,
          	sg.logistics_company,
            sg.logistics_num,
          	sg.update_time,
          	sg.create_time,
          	addr.name as contact_name,
          	addr.phone as contact_phone,
          	concat(
                addr.area,
                addr.address
            ) as contact_address,
            sg.remark,
            sg.back_reason
          @}
          from shop_consume_goods_order sg
          LEFT JOIN shop_consume_pay_order sp ON sp.pay_order_id = sg.pay_order_id
          LEFT JOIN shop_customer sc ON sc.id = sp.customer_id
          LEFT JOIN shop_goods_info si ON sg.goods_id = si.id
          LEFT JOIN shop_supplier ss ON ss.id = si.supplier_id
          LEFT JOIN shop_customer_address addr on addr.id = sg.collect_good_address_id
          where sp.del_flag=0 and sg.id=#id#



batchDelShopConsumeGoodsOrderByIds
===

* 批量逻辑删除

    update shop_consume_goods_order set del_flag = 1 where id  in( #join(ids)#)
    
    
queryFreightAmountByPayOrderId
===
    select ifnull(sum(freight_amount),0) as total_freight_amount,ifnull(sum(re_freight_amount),0) as total_re_freight_amount from shop_consume_goods_order where pay_order_id=#payOrderId#
    
updateOrderStatusByPayOrderId
===
    update shop_consume_goods_order set `status`=#status# where pay_order_id=#payOrderId#
    
queryByPayOrderId
===
    select * from shop_consume_goods_order where pay_order_id=#payOrderId#
    
queryByPayOrderIdAndStatus
===
    select * from shop_consume_goods_order where pay_order_id=#payOrderId# and status=#status#
    
