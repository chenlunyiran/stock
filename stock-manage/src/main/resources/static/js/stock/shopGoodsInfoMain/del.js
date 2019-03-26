layui.define(['table', 'shopGoodsInfoMainApi'], function(exports) {
    var shopGoodsInfoMainApi = layui.shopGoodsInfoMainApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopGoodsInfoTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些商品吗?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopGoodsInfoMainApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});