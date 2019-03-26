layui.define([ 'form', 'laydate', 'table','shopGoodsInfoApi'], function(exports) {
    var form = layui.form;
    var shopGoodsInfoApi = layui.shopGoodsInfoApi;
    var index = layui.index;
    var view = {
        init:function(){
	        Lib.initGenrealForm($("#updateForm"),form);
	        this.initSubmit();
        },
        initSubmit:function(){
            $("#updateButton").click(function(){
            	if ($("#sellPrice").val()>=$("#purchasePrice").val()) {
            		form.on('submit(form)', function(){
                        shopGoodsInfoApi.updateShopGoodsInfo($('#updateForm'),function(){
                           parent.window.dataReload();
                           Common.info("更新成功");
                           Lib.closeFrame();
                        });
                    });
				}else {
					Common.info("售价不能小于进价！！！");
            		return;
				}
                
            });
            $("#updateButton-cancel").click(function(){
                Lib.closeFrame();
            });
        }
            
    }
    exports('edit',view);
	
});