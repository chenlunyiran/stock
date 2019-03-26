layui.define([ 'form', 'laydate', 'table','shopConsumePayOrderApi'], function(exports) {
    var form = layui.form;
    var shopConsumePayOrderApi = layui.shopConsumePayOrderApi;
    var index = layui.index;
    var view = {
        init:function(){
            Lib.initGenrealForm($("#addForm"),form);
            this.initSubmit();
        },
        initSubmit:function(){
            $("#addButton").click(function(){
                 form.on('submit(form)', function(){
                     shopConsumePayOrderApi.addShopConsumePayOrder($('#addForm'),function(){
                         parent.window.dataReload();
                         Common.info("添加成功");
                         Lib.closeFrame();
                     });
                });
            });
        
            $("#addButton-cancel").click(function(){
                Lib.closeFrame();
            });
        }
    			
    }
    exports('add',view);
});