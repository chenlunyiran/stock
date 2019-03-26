/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopSupplier:function(form,callback){
                Lib.submitForm("/stock/shopSupplier/update.json",form,{},callback)
            },
            addShopSupplier:function(form,callback){
                Lib.submitForm("/stock/shopSupplier/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopSupplier/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
            ,
            exportExcel:function(form,callback){
                var formPara = form.serializeJson();
                Common.post("/stock/shopSupplier/excel/export.json", formPara, function(fileId) {
                    callback(fileId);
                })
            }
		
    };
    exports('shopSupplierApi',api);
});