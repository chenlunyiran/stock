queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_goods_extend t
    where del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopGoodsExtend.query")#
    
    
    

batchDelShopGoodsExtendByIds
===

* 批量逻辑删除

    update shop_goods_extend set del_flag = 1 where id  in( #join(ids)#)
    
