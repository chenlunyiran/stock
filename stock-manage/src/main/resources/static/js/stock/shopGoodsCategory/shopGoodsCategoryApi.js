/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopGoodsCategory:function(form,callback){
                Lib.submitForm("/stock/shopGoodsCategory/update.json",form,{},callback)
            },
            addShopGoodsCategory:function(form,callback){
                Lib.submitForm("/stock/shopGoodsCategory/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopGoodsCategory/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
            ,
            exportExcel:function(form,callback){
                var formPara = form.serializeJson();
                Common.post("/stock/shopGoodsCategory/excel/export.json", formPara, function(fileId) {
                    callback(fileId);
                })
            }
		
    };
    exports('shopGoodsCategoryApi',api);
});