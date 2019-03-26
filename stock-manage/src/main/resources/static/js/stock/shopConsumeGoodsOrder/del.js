layui.define(['table', 'shopConsumeGoodsOrderApi'], function(exports) {
    var shopConsumeGoodsOrderApi = layui.shopConsumeGoodsOrderApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopConsumeGoodsOrderTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些ShopConsumeGoodsOrder?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopConsumeGoodsOrderApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});