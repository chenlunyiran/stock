queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_consume_pay_order t
    where del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopConsumePayOrder.query")#
    @if(!isEmpty(id)){
        and  t.id =#id#
    @}
    @if(!isEmpty(customerId)){
        and  t.customer_id =#customerId#
    @}
    
    
    

batchDelShopConsumePayOrderByIds
===

* 批量逻辑删除

    update shop_consume_pay_order set del_flag = 1 where id  in( #join(ids)#)
    
findByPayOrderId
===
    select * from shop_consume_pay_order t where t.pay_order_id = #payOrderId#
    
findByStatusAndPayEndTime
===
    select * from shop_consume_pay_order t where t.status = #status# and t.pay_end_time <= #payEndTime#
    
updateStatusByPayOrderId
===
    update shop_consume_pay_order set status=#status# where pay_order_id = #payOrderId#
    
