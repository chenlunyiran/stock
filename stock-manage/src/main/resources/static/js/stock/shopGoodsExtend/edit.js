layui.define([ 'form', 'laydate', 'table','shopGoodsExtendApi'], function(exports) {
    var form = layui.form;
    var shopGoodsExtendApi = layui.shopGoodsExtendApi;
    var index = layui.index;
    var view = {
        init:function(){
	        Lib.initGenrealForm($("#updateForm"),form);
	        this.initSubmit();
        },
        initSubmit:function(){
            $("#updateButton").click(function(){
                form.on('submit(form)', function(){
                    shopGoodsExtendApi.updateShopGoodsExtend($('#updateForm'),function(){
                       parent.window.dataReload();
                       Common.info("更新成功");
                       Lib.closeFrame();
                    });
                });
            });
            $("#updateButton-cancel").click(function(){
                Lib.closeFrame();
            });
        }
            
    }
    exports('edit',view);
	
});