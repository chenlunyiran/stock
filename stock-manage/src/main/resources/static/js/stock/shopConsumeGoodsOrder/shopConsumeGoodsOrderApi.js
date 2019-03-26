/*访问后台的代码*/
layui.define([], function(exports) {
    var api={
           /* updateShopConsumeGoodsOrder:function(form,callback){
                Lib.submitForm("/stock/shopConsumeGoodsOrder/update.json",form,{},callback)
            },
            addShopConsumeGoodsOrder:function(form,callback){
                Lib.submitForm("/stock/shopConsumeGoodsOrder/add.json",form,{},callback)
            },
            del:function(ids,callback){
                Common.post("/stock/shopConsumeGoodsOrder/delete.json",{"ids":ids},function(){
                    callback();
                })
            }
            ,*/

            cancelOrder : function(form, callback) {
                Lib.submitForm("/stock/shopConsumeGoodsOrder/cancelOrder.json", form, {},
                    callback)
            },

            logisticInfo : function(form, callback) {
                Lib.submitForm("/stock/shopConsumeGoodsOrder/logisticInfo.json", form, {},
                    callback)
            },

            remark : function(form, callback) {
                    Lib.submitForm("/stock/shopConsumeGoodsOrder/remark.json", form, {},
                        callback)
            },

            exportExcel:function(form,callback){
                var formPara = form.serializeJson();
                Common.post("/stock/shopConsumeGoodsOrder/excel/export.json", formPara, function(fileId) {
                    callback(fileId);
                })
            }
		
    };
    exports('shopConsumeGoodsOrderApi',api);
});