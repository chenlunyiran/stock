/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopTransportTemplate:function(form,callback){
                Lib.submitForm("/stock/shopTransportTemplate/update.json",form,{},callback)
            },
            addShopTransportTemplate:function(form,callback){
                Lib.submitForm("/stock/shopTransportTemplate/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopTransportTemplate/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
		
    };
    exports('shopTransportTemplateApi',api);
});