/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
           /* updateShopExchangeOrder:function(form,callback){
                Lib.submitForm("/stock/shopExchangeOrder/update.json",form,{},callback)
            },
            addShopExchangeOrder:function(form,callback){
                Lib.submitForm("/stock/shopExchangeOrder/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopExchangeOrder/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
            ,*/
            exportExcel:function(form,callback){
                var formPara = form.serializeJson();
                Common.post("/stock/shopExchangeOrder/excel/export.json", formPara, function(fileId) {
                    callback(fileId);
                })
            }
		
    };
    exports('shopExchangeOrderApi',api);
});