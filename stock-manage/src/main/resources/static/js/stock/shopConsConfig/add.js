layui.define([ 'form', 'laydate', 'table','shopConsConfigApi'], function(exports) {
    var form = layui.form;
    var shopConsConfigApi = layui.shopConsConfigApi;
    var index = layui.index;
    var view = {
        init:function(){
            Lib.initGenrealForm($("#addForm"),form);
            this.initSubmit();
        },
        initSubmit:function(){
            $("#addButton").click(function(){
                 form.on('submit(form)', function(){
                     shopConsConfigApi.addShopConsConfig($('#addForm'),function(){
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