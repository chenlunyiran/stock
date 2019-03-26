/**
 * Created by alean on 2018/11/19.
 */
var pno = 1, psize = 10;
var dataUrl = "/own/expenseLog/list";
var moreBtn = "moreBtn";
var check = true;



var tmpl=function (item){
    var type = item.type;
    //1:兑付、2:冻结、3:解冻、4:消费、5:退回
    if(1==type){
        return [
            '<a href="/own/expenseLog/detail?id='+item.bussKey+'&type='+item.keyFlag+'">',
            '   <div class="xfjls_info bottom_bor">',
            '       <div class="clearfix">',
            '           <p class="fl">兑换</p>',
            '           <strong class="fr font_gree">+'+TT.formatNumberRgx(item.amount)+'</strong>',
            '       </div>',
            '       <p class="clearfix">',
            '           <em class="db fl">'+moment(item.createTime).format('YYYY-MM-DD HH:mm:ss')+'</em>',
            '           <em class="db fr">余额：'+TT.formatNumberRgx(item.total)+'</em>',
            '       </p>',
            '    </div>',
            '</a>',
        ].join('');
    }else if(4==type){
        return [
            '<a href="/own/expenseLog/detail?id='+item.bussKey+'&type='+item.keyFlag+'">',
            '   <div class="xfjls_info bottom_bor">',
            '       <div class="clearfix">',
            '           <p class="fl">消费</p>',
            '           <strong class="fr font_orange">-'+TT.formatNumberRgx(item.amount)+'</strong>',
            '       </div>',
            '       <p class="clearfix">',
            '           <em class="db fl">'+moment(item.createTime).format('YYYY-MM-DD HH:mm:ss')+'</em>',
            '           <em class="db fr">余额：'+TT.formatNumberRgx(item.total)+'</em>',
            '       </p>',
            '    </div>',
            '</a>',
        ].join('');
    }else  if(3==type){
        return [
            '<a href="/own/expenseLog/detail?id='+item.bussKey+'&type='+item.keyFlag+'">',
            '   <div class="xfjls_info bottom_bor">',
            '       <div class="clearfix">',
            '           <p class="fl">消费</p>',
            '           <strong class="fr font_orange">-'+TT.formatNumberRgx(item.amount)+'</strong>',
            '       </div>',
            '       <p class="clearfix">',
            '           <em class="db fl">'+moment(item.createTime).format('YYYY-MM-DD HH:mm:ss')+'</em>',
            '           <em class="db fr">余额：'+TT.formatNumberRgx(item.total)+'</em>',
            '       </p>',
            '    </div>',
            '</a>',
        ].join('');
    }else  if(5==type){
        return [
            '<a href="/own/expenseLog/detail?id='+item.bussKey+'&type='+item.keyFlag+'">',
            '   <div class="xfjls_info bottom_bor">',
            '       <div class="clearfix">',
            '           <p class="fl">退回</p>',
            '           <strong class="fr font_orange">+'+TT.formatNumberRgx(item.amount)+'</strong>',
            '       </div>',
            '       <p class="clearfix">',
            '           <em class="db fl">'+moment(item.createTime).format('YYYY-MM-DD HH:mm:ss')+'</em>',
            '           <em class="db fr">余额：'+TT.formatNumberRgx(item.total)+'</em>',
            '       </p>',
            '    </div>',
            '</a>',
        ].join('');
    }
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
                        '<p class="tc">您还没消费金流水哦！</p>'+
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