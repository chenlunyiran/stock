layui.define([ 'form', 'laydate', 'table','shopGoodsInfoApi'], function(exports) {
    var form = layui.form;
    var shopGoodsInfoApi = layui.shopGoodsInfoApi;
    var index = layui.index;
    var view = {
        init:function(){
	        Lib.initGenrealForm($("#updateStickForm"),form);
	        this.initSubmit();
        },
        initSubmit:function(){
            $("#updateButton").click(function(){
            	if ($("#launchStatus").val()==0) {
            		Common.info("未上架商品不能置顶");
            		return;
				}
            	if ($("#sort").val()==null||$("#sort").val()=="") {
            		Common.info("请选择置顶位");
            		return;
				}
                form.on('submit(form)', function(){
                    shopGoodsInfoApi.stick($('#updateStickForm'),function(){
                       parent.window.dataReload();
                       Common.info("置顶成功");
                       Lib.closeFrame();
                    });
                });
            });
            $("#updateButton-cancel").click(function(){
                Lib.closeFrame();
            });
        }
            
    }
    exports('stick',view);
	
});