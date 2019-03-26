/**
 * Created by alean on 2018/11/22.
 */
$(function () {
    $("#next").on('click',function(){
        var name = $("#name").val();
        var idCard = $("#idCard").val();

        $.post("/common/auth", {'name': name,'idCard':idCard}, function(res) {
            if(res.code=='OK'){
                if(whitchFrom=="choose"){
                    window.location.href="/common/passwordView?whitchFrom=choose&goodsId="+goodsId+"&shopperId="+shopperId+"&addressId="+addressId;
                }else if(whitchFrom=="wangji") {
                    window.location.href="/common/passwordView?whitchFrom=wangji";
                }else{
                    window.location.href="/common/passwordView";
                }
            }else{
                $('body').toast({
                    content:"验证失败，请确认身份信息",
                    duration:1000
                });
            }
        }, 'json');
    })
})
