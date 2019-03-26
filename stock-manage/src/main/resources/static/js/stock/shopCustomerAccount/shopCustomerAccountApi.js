/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopCustomerAccount:function(form,callback){
                Lib.submitForm("/stock/shopCustomerAccount/update.json",form,{},callback)
            },
            addShopCustomerAccount:function(form,callback){
                Lib.submitForm("/stock/shopCustomerAccount/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopCustomerAccount/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
		
    };
    exports('shopCustomerAccountApi',api);
});