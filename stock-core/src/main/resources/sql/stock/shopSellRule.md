queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_sell_rule t
    where del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopSellRule.query")#
    
    
    

batchDelShopSellRuleByIds
===

* 批量逻辑删除

    update shop_sell_rule set del_flag = 1 where id  in( #join(ids)#)
    
findNeedLaunchGoodsByTimer
===
    SELECT r.* from shop_sell_rule r LEFT JOIN shop_goods_info i on r.goods_id = i.id
    where r.del_flag = 0 and i.del_flag=0
    and r.launch_type = 2
    and i.launch_status = 0
    and r.launch_start_time <= NOW()
    and r.launch_end_time >NOW()

findNeedNoLaunchGoodsByTimer
===
    SELECT r.* from shop_sell_rule r LEFT JOIN shop_goods_info i on r.goods_id = i.id
    where r.del_flag = 0 and i.del_flag=0
    and r.launch_type = 2
    and i.launch_status = 1
    and r.launch_end_time <=NOW()