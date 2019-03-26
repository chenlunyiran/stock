queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_transport_template t
    where del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopTransportTemplate.query")#
    @if(!isEmpty(name)){
      and  t.name like #'%'+name+'%'#
    @}
    order by update_time desc
    
    

batchDelShopTransportTemplateByIds
===

* 批量逻辑删除

    update shop_transport_template set del_flag = 1 where id  in( #join(ids)#)
    
queryAllNoCondition
===
    select id,name,transport_style from shop_transport_template where del_flag = 0

queryByGoodsId
===
    select  t.first_count,
    t.id,
    t.first_price,
    t.next_count ,
    t.next_price, r.goods_id 
    from shop_sell_rule r LEFT JOIN shop_transport_template t on r.transport_id = t.id where r.goods_id in( #join(goodsIds)#)
