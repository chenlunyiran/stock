/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopConsumePayOrder:function(form,callback){
                Lib.submitForm("/stock/shopConsumePayOrder/update.json",form,{},callback)
            },
            addShopConsumePayOrder:function(form,callback){
                Lib.submitForm("/stock/shopConsumePayOrder/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopConsumePayOrder/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
		
    };
    exports('shopConsumePayOrderApi',api);
});