layui.define(['table', 'shopConsConfigApi'], function(exports) {
    var shopConsConfigApi = layui.shopConsConfigApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopConsConfigTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除变量吗?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopConsConfigApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});