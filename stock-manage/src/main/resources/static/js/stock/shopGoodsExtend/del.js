layui.define(['table', 'shopGoodsExtendApi'], function(exports) {
    var shopGoodsExtendApi = layui.shopGoodsExtendApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopGoodsExtendTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些ShopGoodsExtend?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopGoodsExtendApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});