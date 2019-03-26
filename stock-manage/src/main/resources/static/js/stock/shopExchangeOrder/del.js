layui.define(['table', 'shopExchangeOrderApi'], function(exports) {
    var shopExchangeOrderApi = layui.shopExchangeOrderApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopExchangeOrderTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些ShopExchangeOrder?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopExchangeOrderApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});