queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_supplier t
    where del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopSupplier.query")#
    @if(!isEmpty(name)){
        and  t.name like #'%'+name+'%'#
    @}
    order by update_time desc
    

batchDelShopSupplierByIds
===

* 批量逻辑删除

    update shop_supplier set del_flag = 1 where id  in( #join(ids)#)

queryAllNoCondition
===
    select id,name from shop_supplier where del_flag = 0

    
