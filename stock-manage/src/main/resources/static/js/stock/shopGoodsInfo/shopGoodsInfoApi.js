/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
            updateShopGoodsInfo:function(form,callback){
                Lib.submitForm("/stock/shopGoodsInfo/updateSell.json",form,{},callback)
            },
            addShopGoodsInfo:function(form,callback){
                Lib.submitForm("/stock/shopGoodsInfo/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopGoodsInfo/delete.json",{"ids":ids},function(){
                    callback();
                })
            },
            grounding:function(ids,callback){
                Common.post("/stock/shopGoodsInfo/grounding.json",{"ids":ids},function(){
                    callback();
                })
            },
            undercarriage:function(ids,callback){
                Common.post("/stock/shopGoodsInfo/undercarriage.json",{"ids":ids},function(){
                    callback();
                })
            },
            out:function(ids,callback){
                Common.post("/stock/shopGoodsInfo/out.json",{"ids":ids},function(){
                    callback();
                })
            },
            stick:function(form,callback){
                Lib.submitForm("/stock/shopGoodsInfo/updateStick.json",form,{},callback)
            },
            
            exportExcel:function(form,callback){
                var formPara = form.serializeJson();
                Common.post("/stock/shopGoodsInfo/excel/export.json", formPara, function(fileId) {
                    callback(fileId);
                })
            }
		
    };
    exports('shopGoodsInfoApi',api);
});