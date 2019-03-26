queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_cons_config t
    where del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopConsConfig.query")#
    @if(!isEmpty(nid)){
        and  t.nid =#nid#
    @}
    @if(!isEmpty(name)){
        and  t.name =#name#
    @}
    @if(!isEmpty(status)){
        and  t.status =#status#
    @}
    order by t.update_time desc
    
    

batchDelShopConsConfigByIds
===

* 批量逻辑删除

    delete from shop_cons_config where id  in( #join(ids)#)
    
    
queryByStatus
===
    select * from shop_cons_config s where s.status=#status#
    
findByNid
===
    select * from shop_cons_config s where s.nid=#nid#

queryByNids
===
     select * from shop_cons_config s where s.nid in( #join(nids)#)
