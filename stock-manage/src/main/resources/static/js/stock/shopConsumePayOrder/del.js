layui.define(['table', 'shopConsumePayOrderApi'], function(exports) {
    var shopConsumePayOrderApi = layui.shopConsumePayOrderApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopConsumePayOrderTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些payOrder?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopConsumePayOrderApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});