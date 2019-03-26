layui.define(['table', 'shopSupplierApi'], function(exports) {
    var shopSupplierApi = layui.shopSupplierApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopSupplierTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些供应商吗?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopSupplierApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});