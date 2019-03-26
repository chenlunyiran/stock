/*访问后台的代码*/
layui.define([], function(exports) {
    var api = {
        updateUser : function(form, callback) {
            Lib.submitForm("/admin/user/update.json", form, {}, callback)
        },
        addUser : function(form, callback) {
            Lib.submitForm("/admin/user/add.json", form, {}, callback)
        },
        del : function(ids, callback) {
            Common.post("/admin/user/delete.json", {
                "ids" : ids
            }, function() {
                callback();
            })
        },
        changePassword : function(form, callback) {
            var password  = $("#password").val().replace(/\s+/g,"");
            var password2  = $("#password2").val().replace(/\s+/g,"");
            if(password.length<=0 || password2.length<=0){
                return;
            }
            Lib.submitForm("/admin/user/changePassword.json", form, {},
                    callback)
        },
        changePasswordSelf : function(form, callback) {
            var password  = $("#password").val().replace(/\s+/g,"");
            var password2  = $("#password2").val().replace(/\s+/g,"");
            if(password.length<=0 || password2.length<=0){
                return;
            }
            Lib.submitForm("/admin/user/changePasswordSelf.json", form, {},
                callback)
        },
        addUserRole : function(form, callback) {
            Lib.submitForm("/admin/user/role/add.json", form, {}, callback)
        },
        delUserRole : function(ids, callback) {
            Common.post("/admin/user/role/delete.json", {
                "ids" : ids
            }, function() {
                callback();
            })
        },
        exportUsers:function(form,callback){
            var formPara = form.serializeJson();
            Common.post("/admin/user/excel/export.json", formPara, function(fileId) {
                callback(fileId);
            })
        }

    };

    exports('userApi', api);

});