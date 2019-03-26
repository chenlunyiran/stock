layui.define(['table', 'shopCustomerAccountLogApi'], function(exports) {
    var shopCustomerAccountLogApi = layui.shopCustomerAccountLogApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopCustomerAccountLogTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些ShopCustomerAccountLog?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopCustomerAccountLogApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});