/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopGoodsExtend:function(form,callback){
                Lib.submitForm("/stock/shopGoodsExtend/update.json",form,{},callback)
            },
            addShopGoodsExtend:function(form,callback){
                Lib.submitForm("/stock/shopGoodsExtend/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopGoodsExtend/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
		
    };
    exports('shopGoodsExtendApi',api);
});