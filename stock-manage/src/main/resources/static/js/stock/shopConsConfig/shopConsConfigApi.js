/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopConsConfig:function(form,callback){
                Lib.submitForm("/stock/shopConsConfig/update.json",form,{},callback)
            },
            addShopConsConfig:function(form,callback){
                Lib.submitForm("/stock/shopConsConfig/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopConsConfig/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
		
    };
    exports('shopConsConfigApi',api);
});