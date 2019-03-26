layui.define(['table', 'shopCustomerApi'], function(exports) {
    var shopCustomerApi = layui.shopCustomerApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopCustomerTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些ShopCustomer?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopCustomerApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});