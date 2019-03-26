layui.define([ 'form', 'laydate', 'table','shopConsumeGoodsOrderApi'], function(exports) {
    var form = layui.form;
    var shopConsumeGoodsOrderApi = layui.shopConsumeGoodsOrderApi;
    var index = layui.index;
    var view = {
        init:function(){
	        Lib.initGenrealForm($("#cancelOrderForm"),form);
	        this.initSubmit();
        },
        initSubmit:function(){
            $("#updateButton").click(function(){

                var freightAmount = parseFloat($("#freightAmount").val());
                if(isNaN(freightAmount)){
                    Common.error("金额转换异常！");
                    return;
                }

                var reFreightAmount = 0;
                if($("#reFreightAmount").val() != ""){
                    reFreightAmount = parseFloat($("#reFreightAmount").val());
                }

                if(isNaN(reFreightAmount)){
                    Common.error("金额转换异常！");
                    return;
                }

                if (reFreightAmount <= freightAmount) {
                    form.on('submit(form)', function(){
                        shopConsumeGoodsOrderApi.cancelOrder($('#cancelOrderForm'),function(){
                            parent.window.dataReload();
                            Common.info("订单已取消");
                            Lib.closeFrame();
                        });
                    });
                }else{
                    Common.info("退还运费金额不能大于可退运费金额！");
                    return;
                }

            });
            $("#updateButton-cancel").click(function(){
                Lib.closeFrame();
            });
        }
            
    }
    exports('cancelOrder',view);
});