/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopCustomer:function(form,callback){
                Lib.submitForm("/stock/shopCustomer/update.json",form,{},callback)
            },
            addShopCustomer:function(form,callback){
                Lib.submitForm("/stock/shopCustomer/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopCustomer/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
		
    };
    exports('shopCustomerApi',api);
});