queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_goods_info t
    where del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopGoodsInfo.query")#
    @if(!isEmpty(sku)){
        and  t.sku =#sku#
    @}
    @if(!isEmpty(name)){
        and  t.name =#name#
    @}
    @if(!isEmpty(launchStatus)){
        and  t.launch_status =#launchStatus#
    @}
    
    
    

batchDelShopGoodsInfoByIds
===

* 批量逻辑删除

    update shop_goods_info set del_flag = 1 where id  in( #join(ids)#)
    
batchGroundingShopGoodsInfoByIds
===

* 批量上架逻辑

    update shop_goods_info set launch_status = 1 where id  in( #join(ids)#) 

batchunDercarRiageShopGoodsInfoByIds
===

* 批量下架逻辑

	update shop_goods_info set launch_status = 0 where id  in( #join(ids)#)


    
    
    
queryGoodsInfoMainByCondition
===
* 商品列表查询

    select 
    @pageTag(){
        i.*, c.name category_name 
    @}
    from shop_goods_info i LEFT JOIN shop_goods_category c on i.category_id = c.id
    where i.del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopGoodsInfo.query")#
    @if(!isEmpty(sku)){
        and  i.sku =#sku#
    @}
    @if(!isEmpty(name)){
        and  i.name like #'%'+name+'%'#
    @}
    @if(!isEmpty(avalFlagStatus)&& avalFlagStatus>0){
        and i.avaliable_quantity>0 
    @}
    @if(!isEmpty(avalFlagStatus)&&avalFlagStatus == 0){
        and  i.avaliable_quantity = 0
    @}
     order by i.update_time desc
    
queryGoodsInfoRuleByCondition
===
* 商品列表查询

    select 
    @pageTag(){
        i.*, c.sell_type ,c.launch_type,i.name,i.sku,
      CASE when i.avaliable_quantity>0 THEN '有货'
		 WHEN i.avaliable_quantity = 0 THEN '无货'
     ELSE '异常' END as stock_status
    @}
    from shop_goods_info i LEFT JOIN shop_sell_rule c on i.id = c.goods_id
    where i.del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopGoodsInfo.query")#
    @if(!isEmpty(avalFlagStatus)&& avalFlagStatus>0){
        and i.avaliable_quantity>0 
    @}
    @if(!isEmpty(avalFlagStatus)&&avalFlagStatus == 0){
        and  i.avaliable_quantity = 0
    @}
    @if(!isEmpty(launchType)){
        and c.launch_type =#launchType#
    @}
    @if(!isEmpty(sellType)){
        and  c.sell_type =#sellType#
    @}
     @if(!isEmpty(sku)){
        and  i.sku =#sku#
    @}
    @if(!isEmpty(name)){
        and  i.name like #'%'+name+'%'#
    @}
    @if(!isEmpty(launchStatus)){
        and  i.launch_status =#launchStatus#
    @}
    order by i.sort asc,i.update_time desc
    
batOutShopGoodsInfoByIds
===
* 售罄操作

    update shop_goods_info set avaliable_quantity = 0 where id  in( #join(ids)#)


updateStickShopGoodsInfoById
===

* 置顶位置更新

    update shop_goods_info set sort = #sort# where id  = #id# 
    
updateUnStickShopGoodsInfoById
===

* 原来的置顶位更新为100

    update shop_goods_info set sort = 100 where id  = #id#

queryStickShopGoodsInfoBySort
===
* 查询出置顶的数据

	select * from shop_goods_info t where del_flag=0 and t.sort = #sort#

queryShopGoodsMainById
===
    SELECT i.*,e.id as extend_id, e.goods_desc,r.id as rule_id, r.transport_id from 
    shop_goods_info i LEFT JOIN shop_goods_extend e on i.id = e.goods_id 
    LEFT JOIN shop_sell_rule r on i.id = r.goods_id where i.id=#id# and i.del_flag = 0 LIMIT 1
       
queryOneByIdForEditRule
===
* 编辑查询出所有字段

	 select 
     i.*, r.id as rule_id, r.launch_type,r.transport_id,  DATE_FORMAT(r.launch_start_time,'%Y-%m-%d  %h:%i:%s') as launch_start_time,DATE_FORMAT(r.launch_end_time,'%Y-%m-%d  %h:%i:%s') as launch_end_time,e.name as ename,c.name vname
     from shop_goods_info i LEFT JOIN shop_sell_rule r on i.id = r.goods_id
     LEFT JOIN shop_supplier e on i.supplier_id = e.id
     LEFT JOIN shop_goods_category c on i.category_id = c.id
        where i.del_flag=0 and i.id = #id#
   
updateSellShopGoodsInfoById
===   
* 售卖规则更新操作

	update shop_goods_info set name = #name#,sell_price=#sellPrice#,avaliable_quantity=#avaliableQuantity# where id  = #id#
	
queryEffectiveRecord
===
    select * from shop_goods_info s where s.avaliable_quantity >0 or s.frozen_quantity > 0 and s.launch_status=1 and s.del_flag=0
