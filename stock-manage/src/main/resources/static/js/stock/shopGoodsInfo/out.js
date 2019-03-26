layui.define(['table', 'shopGoodsInfoApi'], function(exports) {
    var shopGoodsInfoApi = layui.shopGoodsInfoApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        outBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopGoodsInfoTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要售罄这些商品吗?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopGoodsInfoApi.out(ids,function(){
                Common.info("售罄成功");
                    dataReload();
                })
            })
        }
    }
    exports('out',view);
	
});