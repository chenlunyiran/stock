queryByCondition
===


    select 
    @pageTag(){
    t.*, c.user_name, c.name, c.phone, c.regist_from
    @}
    from shop_customer_account_log t
    left join shop_customer c on c.id = t.customer_id
    where t.del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopCustomerAccountLog.query")#
    @if(!isEmpty(userName)){
        and  c.user_name =#userName#
    @}
    
    @if(!isEmpty(name)){
        and  c.name =#name#
    @}
    
    @if(!isEmpty(phone)){
        and  c.phone =#phone#
    @}
    
    @if(!isEmpty(registFrom)){
        and  c.regist_from =#registFrom#
    @}
    
    @if(!isEmpty(type)){
        and  t.`type` =#type#
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
    
    

batchDelShopCustomerAccountLogByIds
===

* 批量逻辑删除

    update shop_customer_account_log set del_flag = 1 where id  in( #join(ids)#)
    
