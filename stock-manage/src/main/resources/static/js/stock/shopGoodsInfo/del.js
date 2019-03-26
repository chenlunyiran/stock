layui.define(['table', 'shopGoodsInfoApi'], function(exports) {
    var shopGoodsInfoApi = layui.shopGoodsInfoApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopGoodsInfoTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些ShopGoodsInfo?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopGoodsInfoApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});