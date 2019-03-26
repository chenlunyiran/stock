layui.define(['table', 'shopGoodsInfoApi'], function(exports) {
    var shopGoodsInfoApi = layui.shopGoodsInfoApi;
    var table=layui.table;
    var view = {
        init:function(){
        },
        undercarriageBatch:function(){
            var data = Common.getMoreDataFromTable(table,"shopGoodsInfoTable");
            if(data==null){
                return ;
            }
            Common.openConfirm("确认要下架这些商品吗?",function(){
            var ids =Common.concatBatchId(data,"id");
            shopGoodsInfoApi.undercarriage(ids,function(){
                Common.info("下架成功");
                    dataReload();
                })
            })
        }
    }
    exports('undercarriage',view);
	
});