layui.define(['table', 'shopGoodsInfoApi'], function(exports) {
    var shopGoodsInfoApi = layui.shopGoodsInfoApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        groundingBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopGoodsInfoTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要上架这些商品吗?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopGoodsInfoApi.grounding(ids,function(){
                Common.info("上架成功");
                    dataReload();
                })
            })
        }
    }
    exports('grounding',view);
	
});