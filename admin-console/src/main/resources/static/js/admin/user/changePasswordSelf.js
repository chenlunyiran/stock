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
					userApi.changePasswordSelf($('#changePasswordSelfForm'),function(){
						Common.info("密码更改成功");
					});
					
				});
				
				$("#savePasswordSelf-cancel").click(function(){
					window.close();
				});
			}
				
	}
	
	
	
	 exports('changePasswordSelf',view);
	
});