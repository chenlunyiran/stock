/**
 * Created by alean on 2018/12/3.
 */
/**
 * Created by alean on 2018/11/19.
 */
var pno = 1, psize = 10;
var dataUrl = "/own/chargeLogs";
var moreBtn = "moreBtn";
var check = true;

function lookContract(exchangeOrderId){
    url = '/own/exchangeContract';
    data = {exchangeOrderId:exchangeOrderId};
    $.ajax({
        url:url,
        type: 'POST',
        async: false,
        data: data,
        dataType: 'json',
        success:function(d){
            if(d.code=="OK"){
                LZLH().toPDFViewer({"url" : d.data.contractPath});
            }else {
                $('body').toast({
                    content: d.msg,
                    duration:1000
                });
            }
        },
        error:function(data){

        }
    });
}


var tmpl=function (item){

        return [
            '<div class="xfjls_info bottom_bor">',
            '   <div class="div1 clearfix">',
            '   <p class="fl">协议</p>',
            '   <a class="fr " href="javascript:;" onclick="lookContract('+item.exchangeOrderId+')">查看</a>',
            '</div>',
            '<div class="xfjls_info">',
            '   <div class="div2 clearfix">',
            '       <p class="fl">兑换时间</p>',
            '       <a class="fr ">'+moment(item.createTime).format('YYYY-MM-DD HH:mm:ss')+'</a>',
            '   </div>',
            '   <div class="div2 clearfix">',
            '       <p class="fl">兑换金额</p>',
            '       <a class="fr "><span class="ora">'+TT.formatNumberRgx(item.assetAmount)+'</span> 元</a>',
            '   </div>',
            '   <div class="div2 clearfix">',
            '       <p class="fl">兑换消费金</p>',
            '       <a class="fr "><span class="ora">'+TT.formatNumberRgx(item.consumeAmount)+'</span> 消费金</a>',
            '   </div>',
            '</div>',
        ].join('');
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
                '<p class="tc">您还没兑付记录哦！</p>'+
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
