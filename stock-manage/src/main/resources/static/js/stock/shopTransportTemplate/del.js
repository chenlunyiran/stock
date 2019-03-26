layui.define(['table', 'shopTransportTemplateApi'], function(exports) {
    var shopTransportTemplateApi = layui.shopTransportTemplateApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        delBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopTransportTemplateTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要删除这些运费模板吗?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopTransportTemplateApi.del(ids,function(){
                Common.info("删除成功");
                    dataReload();
                })
            })
        }
    }
    exports('del',view);
	
});