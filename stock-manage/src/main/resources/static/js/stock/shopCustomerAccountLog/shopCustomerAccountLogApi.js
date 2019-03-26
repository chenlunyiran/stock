/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopCustomerAccountLog:function(form,callback){
                Lib.submitForm("/stock/shopCustomerAccountLog/update.json",form,{},callback)
            },
            addShopCustomerAccountLog:function(form,callback){
                Lib.submitForm("/stock/shopCustomerAccountLog/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopCustomerAccountLog/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
		
    };
    exports('shopCustomerAccountLogApi',api);
});