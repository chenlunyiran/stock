/**
 * Created by alean on 2018/11/21.
 */
var TT = TT || {};
/**
 * 格式化数字千分位逗号分割
 */
TT.formatNumberRgx = function(num) {
    var parts = num.toString().split(".");
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    return parts.join(".");
};

//校验是否是数字,保留两位小数
TT.validate = function(amount){
    if(!(/^(?!0+(?:\.0+)?$)(?:[1-9]\d*|0)(?:\.\d{1,2})?$/.test(amount))){
        return false;
    }
    return true;
}
//判断var1 是否大于  var2
TT.compareNum = function(var1,var2){
    return Number(var1)>=Number(var2);
}
//var1-var2
TT.substract = function(var1,var2){
    return Number(var1)-Number(var2);
}
//是否被100整除
TT.divisionBy100 = function(var1){
    return Number(var1)%100==0;
}

/**
 * 常用正则
 */
TT.Regex = {
    name : [ /^[a-zA-Z]([a-zA-Z0-9_]){5,19}$/, "6-20位，可使用字母、数字、下划线，需以字母开头" ],
    pwd : [ /^[A-Za-z0-9\^$\.\+\*_@!#%~=-]{8,32}$/,
        "密码只能为 8 - 32 位数字，字母及常用符号组成" ],
    phone : [ /^(13[0-9]|15[0-9]|16[0-9]|17[0-9]|18[0-9]|19[0-9]|14[57])[0-9]{8}$/,
        "请输入正确手机号" ],
    email : [ /^([-_A-Za-z0-9\.]+)@([_A-Za-z0-9]+\.)+[A-Za-z0-9]{2,3}$/,
        "请输入正确的邮箱" ],
    bankCard : [ /^[0-9]{15,19}$/, "请输入正确的银行卡号" ],
    idCard : [ /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, "请输入正确的身份证号" ],
    cmsCode : [ /^[0-9]{6}$/, "请输入正确的验证码" ],
    unionBankCode:[/^[0-9]{12}$/, "请输入正确的开户行号" ],
    payPwd : [ /^\d{6}$/,"提现密码只能为6 位数字" ],
    postcode : [ /^\d{6}$/,"邮政编码只能为6 位数字" ]
}

TT.isBlank = function(var1){
    return var1 =='' || var1 =='undefined';
}

//判断是否最长100
TT.maxLength = function (var1,length) {
    return var1.length>length
}

//获取长度
TT.strlen = function(str){
    var len = 0;
    for (var i=0; i<str.length; i++) {
        var c = str.charCodeAt(i);
        //单字节加1
        if ((c >= 0x0001 && c <= 0x007e) || (0xff60<=c && c<=0xff9f)) {
            len++;
        }
        else {
            len+=2;
        }
    }
    return len;
}

TT.lengthBetween = function(var1,minL,maxL){
    return var1.length>=minL && var1.length<=maxL;
}
//剔除空格
TT.tichukongge = function(var1){
    return var1.replace(/\s*/g,"");
}

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

TT.format = function (date) { //author: meizz
    date = new Date(date);
    var o = {
        "Y": date.getYear(),//年
        "M": date.getMonth() + 1, //月份
        "d": date.getDate(), //日
        "h": date.getHours(), //小时
        "m": date.getMinutes(), //分
        "s": date.getSeconds() //秒
    };

    return o.Y+"-"+ o.M+"-"+ o.d+" "+ o.h+":"+ o.m+":"+ o.s;
}

/**
 * 浏览器识别
 */
TT.sys = {
    versions : function() {
        var u = navigator.userAgent, app = navigator.appVersion;
        return {
            trident : u.indexOf('Trident') > -1, // IE内核
            presto : u.indexOf('Presto') > -1, // opera内核
            webKit : u.indexOf('AppleWebKit') > -1, // 苹果、谷歌内核
            gecko : u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,// 火狐内核
            mobile : !!u.match(/AppleWebKit.*Mobile.*/), // 是否为移动终端
            ios : u.indexOf('iOS') > -1, // ios终端
            android : u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, // android终端或者uc浏览器
            iPhone : u.indexOf('iPhone') > -1, // 是否为iPhone或者QQHD浏览器
            iPad : u.indexOf('iPad') > -1, // 是否iPad
            webApp : u.indexOf('Safari') == -1, // 是否web应该程序，没有头部与底部
            weixin : u.indexOf('MicroMessenger') > -1, // 是否微信 （2015-01-22新增）
            qq : u.match(/\sQQ/i) == " qq" // 是否QQ
        };
    }(),
    language : (navigator.browserLanguage || navigator.language).toLowerCase()
}