queryByCondition
===


    select 
    @pageTag(){
        se.*,sc.`name`,sc.phone
    @}
    from shop_exchange_order se left join shop_customer sc on se.customer_id=sc.id
    where se.del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopExchangeOrder.query")#
    
     @if(!isEmpty(exchangeOrderId)){
            and  se.exchange_order_id =#exchangeOrderId#
     @}
        
    @if(!isEmpty(name)){
        and  sc.name =#name#
    @}
    
    @if(!isEmpty(phone)){
        and  sc.phone =#phone#
    @}
    
    @if(!isEmpty(status)){
            and  se.status =#status#
    @}
        
     @if(!isEmpty(createDateMin)){
            and  se.update_time>= #createDateMin#
     @}
     
     @if(!isEmpty(createDateMax)){
            and  se.update_time< #nextDay(createDateMax)#
     @}
    
    
    

batchDelShopExchangeOrderByIds
===

* 批量逻辑删除

    update shop_exchange_order set del_flag = 1 where id  in( #join(ids)#)
    
