layui.define([ 'form', 'table','userApi'], function(exports) {
	var form = layui.form;
	var userApi = layui.userApi;
	var index = layui.index;
	var view = {
			init:function(){
				Lib.initGenrealForm($("#changePasswordSelfForm"),form);
				this.initSubmit();
			},
			initSubmit:function(){
				$("#savePasswordSelf").click(function(){
                    var password  = $("#password").val().replace(/\s+/g,"");
                    var password2  = $("#password2").val().replace(/\s+/g,"");
                    if(password.length<=0 || password2.length<=0){
                        return;
                    }
                    $.post("/admin/user/changePasswordSelf.json",{password:password,password2:password2},function (data) {
                        if(data.code == '0') {
                            Common.success("密码更改成功");
                            setTimeout(function () {
                                $("#LAY_changePasswordSelf").parent().find(".layui-layer-btn1").click()
                            }, 1);
                        } else {
                            Common.error(data.msg);
                        }
                    },"json").error(function(){
                        Common.error("系统异常！");
                    });
				});
				
				$("#savePasswordSelf-cancel").click(function(){
                    $("#layui-layer1").hide();
                    $("#layui-layer-shade1").hide();
				});
			}
				
	}
	
	
	
	 exports('changePasswordSelf',view);
	
});