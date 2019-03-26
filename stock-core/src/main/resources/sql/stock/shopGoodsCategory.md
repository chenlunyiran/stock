queryByCondition
===


    select 
    @pageTag(){
    t.*
    @}
    from shop_goods_category t
    where del_flag=0 
    @//数据权限，该sql语句功能点  
    and #function("shopGoodsCategory.query")#
    @if(!isEmpty(nid)){
        and  t.nid =#nid#
    @}
    @if(!isEmpty(name)){
         and  t.name like #'%'+name+'%'#
    @}
    order by t.update_time desc

batchDelShopGoodsCategoryByIds
===
    update shop_goods_category set del_flag = 1 where id  in( #join(ids)#)
    
queryAllNoCondition
===
    select id,name from  shop_goods_category where del_flag = 0 order by update_time desc
  
insertCategory   
===
    insert into shop_goods_category(nid,name,remark) select
    IFNULL((SELECT LPAD(MAX(CAST(nid AS SIGNED INTEGER)) + 1,3,'0')FROM shop_goods_category),'001'),#name#,#remark# from dual