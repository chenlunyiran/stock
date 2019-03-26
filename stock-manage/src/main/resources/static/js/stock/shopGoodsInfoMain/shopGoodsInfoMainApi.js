/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopGoodsInfo:function(form,callback){
                Lib.submitForm("/stock/shopGoodsInfoMain/update.json",form,{},callback)
            },
            addShopGoodsInfo:function(form,callback){
                Lib.submitForm("/stock/shopGoodsInfoMain/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopGoodsInfoMain/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
            ,
            exportExcel:function(form,callback){
                var formPara = form.serializeJson();
                Common.post("/stock/shopGoodsInfoMain/excel/export.json", formPara, function(fileId) {
                    callback(fileId);
                })
            }
		
    };
    exports('shopGoodsInfoMainApi',api);
});