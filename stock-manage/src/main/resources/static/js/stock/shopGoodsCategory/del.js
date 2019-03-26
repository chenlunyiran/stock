layui.define(['table', 'shopGoodsCategoryApi'], function(exports) {
    var shopGoodsCategoryApi = layui.shopGoodsCategoryApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopGoodsCategoryTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些类目吗?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopGoodsCategoryApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});