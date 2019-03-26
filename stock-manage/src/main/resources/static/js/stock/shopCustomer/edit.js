layui.define([ 'form', 'laydate', 'table','shopCustomerApi'], function(exports) {
    var form = layui.form;
    var shopCustomerApi = layui.shopCustomerApi;
    var index = layui.index;
    var view = {
        init:function(){
	        Lib.initGenrealForm($("#updateForm"),form);
	        this.initSubmit();

            $("#close").click(function(){
                Lib.closeFrame();
            });
        },

        initSubmit:function(){
            $("#updateButton").click(function(){
                form.on('submit(form)', function(){
                    shopCustomerApi.updateShopCustomer($('#updateForm'),function(){
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