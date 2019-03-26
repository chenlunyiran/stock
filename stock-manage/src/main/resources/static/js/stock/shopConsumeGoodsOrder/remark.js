layui.define([ 'form', 'laydate', 'table','shopConsumeGoodsOrderApi'], function(exports) {
    var form = layui.form;
    var shopConsumeGoodsOrderApi = layui.shopConsumeGoodsOrderApi;
    var index = layui.index;
    var view = {
        init:function(){
	        Lib.initGenrealForm($("#remarkForm"),form);
	        this.initSubmit();
        },
        initSubmit:function(){
            $("#updateButton").click(function(){
                form.on('submit(form)', function(){
                    shopConsumeGoodsOrderApi.remark($('#remarkForm'),function(){
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
    exports('remark',view);
});