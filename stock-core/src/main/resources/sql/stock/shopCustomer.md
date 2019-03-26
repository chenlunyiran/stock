queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_customer t
    where t.del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopCustomer.query")#
    @if(!isEmpty(customerId)){
        and  t.id =#customerId#
    @}
    @if(!isEmpty(userName)){
        and  t.user_name =#userName#
    @}
    @if(!isEmpty(name)){
        and  t.name =#name#
    @}
    @if(!isEmpty(phone)){
        and  t.phone =#phone#
    @}
    @if(!isEmpty(createDateMin)){
        and  t.create_time>= #createDateMin#
    @}
    @if(!isEmpty(createDateMax)){
        and  t.create_time< #nextDay(createDateMax)#
    @}
    @if(!isEmpty(updateDateMin)){
        and  t.update_time>= #updateDateMin#
    @}
    @if(!isEmpty(updateDateMax)){
        and  t.update_time< #nextDay(updateDateMax)#
    @}
    order by t.update_time desc
    
    
batchDelShopCustomerByIds
===

* 批量逻辑删除

    update shop_customer set del_flag = 1 where id  in( #join(ids)#)
    
