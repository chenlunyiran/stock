/**
 * Created by alean on 2018/11/17.
 */
var deleteAddress =  function(id){
    var url = "/own/addressManager/deleteAddress";
    var param = {id:id,type:"delete"};
    doRequest(url,param);
}
var doRequest = function(url,param){
    param = param || {};
    $.ajax({
        url : url,
        data : param,
        type : 'POST',
        dataType : 'json',
        success : function(data) {
            if(data.code == 'OK') {
                $('body').toast({
                    content:'删除成功',
                    duration:1000
                });
                setTimeout(function(){
                    window.location.href='/own/addressManager';
                },1000)
            } else {
                $('body').toast({
                    content:data.msg,
                    duration:1000
                });
            }
        },
        error : function(data) {

        }
    });
}