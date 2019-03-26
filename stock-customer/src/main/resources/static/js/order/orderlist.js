/**
 * Created by alean on 2018/11/19.
 */
var pno = 1, psize = 10;
var dataUrl = "/own/order/list";
var moreBtn = "moreBtn";
var check = true;
var orderId=-1;
var totalPrice=-1;

function logisticsInfo(logisticsNum,logisticsCompany){
    $("#logisticsNum").html("快递单号："+logisticsNum);
    $("#logisticsCompany").html(" 快递公司："+logisticsCompany);
    $("#wuliu-popup").show()

}
//显示详情
function toPay(id,amount){
    orderId=id;
    totalPrice=amount.toFixed(2);
    document.getElementById("toPayMoney").innerHTML = amount.toFixed(2);
    $("#yPassWord").show();
}

var tmpl=function (item){
    var logisticsCompany = "'"+item.logisticsCompany+"'";
    // 0、待支付 1、支付成功 2、支付失败 3、已取消 4、待发货 5、已发货 6、退货中 7、已退货
    var logisticsStatus ="";
    switch (item.status){
        case 0:
            var id = item.id;
            var amount = item.count*item.price+item.freight_amount;
            logisticsStatus = '<a class="db fr tc" href="javascript:;" onclick="toPay('+id+','+amount+')" >待支付</a>';
            break;
        case 1:
            logisticsStatus = '<span class="db fr">待发放</span>';
            break;
        case 2:break;
        case 3:
            logisticsStatus = '<span class="db fr">已取消</span>';
            break;
        case 4:
            logisticsStatus = '<span class="db fr">待发放</span>';
            break;
        case 5:
            logisticsStatus = '<a class="db fr tc" href="javascript:;" onclick="logisticsInfo('+item.logisticsNum+','+logisticsCompany+')" >查看物流</a>';
            break;
        case 6:break;
        case 7:break;
    }

    var freightAmount = item.freight_amount==0?'包邮':TT.formatNumberRgx(item.freight_amount)+' 消费金';

    return [
        '<div class="myorder_list">',
        '    <div class="list_top">',
        '          <p class="fl">订单号：'+item.consumeGoodsOrderId+'</p>',
        logisticsStatus,
        '    </div>',
        '    <div class="list_center">',
        '       <dl class="clearfix">',
        '           <dt class="fl">',
        '               <img src="'+item.imagePath+'"/>',
        '           </dt>',
        '           <dd class="fl">',
        '               <p>'+item.name+'</p>',
        '               <span class="db"><strong>'+TT.formatNumberRgx(item.price)+'</strong>消费金</span>',
        '               <p class="clearfix"><em class="fl">数量 x'+item.count+'</em><em class="fr">运费：'+freightAmount+'</em></p>',
        '           </dd>',
        '       </dl>',
        '   </div>',
        '   <div class="list_bottom top_bor clearfix">',
        '       <p class="fl">共支付 '+TT.formatNumberRgx(item.count*item.price+item.freight_amount)+' 消费金</p>',
        '       <span class="db fr">'+moment(item.createTime).format('YYYY-MM-DD HH:mm:ss')+'</span>',
        '   </div>',
        '</div>'
    ].join('');
}

//显示详情
function showDetail(articleId){
    window.location.href=PAGE.baseURL+"touch3/platNoticeDetail?id="+articleId;
}

makeHtml();

function loadData() {
    check=false;
    $("#"+moreBtn).html('正在加载……');

    var param = {currentPage:pno,pageSize:psize};
    $.ajax({
        url : dataUrl,
        data : param,
        type : 'POST',
        dataType : 'json',
        success : function(d) {
            if(d.code == 'OK') {
                data=d;
                makeHtml();
            } else {

            }
        },
        error : function(data) {

        }
    });
    $("#"+moreBtn).html('加载更多');
}

function makeHtml(){
    var mark = false;
    for(var i=0;i<data.data.data.length;i++){
        mark = true;
        $("#dataDiv").append(tmpl(data.data.data[i]));
    }
    if(data.data.currentPage<data.data.total){
        check=true;
        $("#"+moreBtn).html('加载更多');
        pno++;
    }else{
        check=false;
        if(!mark){
            <!-- 您还没有订单哦！ -->
            var div = '<div class="noadress">'+
                        '<img class="db" src="/img/public/nodata.png" alt="">'+
                        '<p class="tc">您还没有订单哦！</p>'+
                      '</div>';
            $("#"+moreBtn).html(div);
        }else {
            $("#"+moreBtn).html('已无更多数据');
        }
    }
}

$(".go-top").on('click',function(){
    window.scroll(0, 0);
})
$("#"+moreBtn).on('click',function(){
    if(check){
        loadData();
    }
})

window.onscroll = function(e) {
    if (window.pageYOffset > window.innerHeight){
        $('.go-top').show();
    }else{
        $('.go-top').hide();
    }
    if (moreBtn == null || moreBtn == ''){
        return;
    }
    var obj = document.getElementById(moreBtn);
    if (obj && check&& (window.pageYOffset + window.innerHeight) >= obj.offsetTop) {
        loadData();
    }
};