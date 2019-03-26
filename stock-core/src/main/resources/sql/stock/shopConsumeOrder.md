queryByCondition
===
    select 
    @pageTag(){
        sg.id,
    	sp.customer_id,
    	sc.`name`,
    	sc.phone,
    	sp.pay_order_id,
    	sg.consume_goods_order_id,
    	si.`name` as good_name,
    	si.sell_price,
    	sg.count,
    	si.sell_price*sg.count amount,
    	sp.`status`,
    	sp.pay_time,
    	concat(
    		sg.logistics_company,
    		sg.logistics_num
    	) as logistics,
    	sg.update_time
    @}
    from shop_consume_pay_order sp
    LEFT JOIN shop_consume_goods_order sg ON sp.pay_order_id = sg.pay_order_id
    LEFT JOIN shop_customer sc ON sc.id = sp.customer_id
    LEFT JOIN shop_goods_info si ON sg.goods_id = si.id
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
       and  sg.consume_goods_orderId = #consumeGoodOrderId#
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
    
    
    

batchDelShopConsumePayOrderByIds
===

* 批量逻辑删除

    update shop_consume_pay_order set del_flag = 1 where id  in( #join(ids)#)
    
