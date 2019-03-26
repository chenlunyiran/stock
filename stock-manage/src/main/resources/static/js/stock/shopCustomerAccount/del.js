layui.define(['table', 'shopCustomerAccountApi'], function(exports) {
    var shopCustomerAccountApi = layui.shopCustomerAccountApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopCustomerAccountTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些ShopCustomerAccount?",function(){
            var ids =Common.concatBatchId(data,"customerId");
            shopCustomerAccountApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});