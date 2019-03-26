
layui.define([ 'form', 'laydate','layedit','upload', 'table','shopGoodsInfoMainApi'], function(exports) {
    var form = layui.form;
    var shopGoodsInfoMainApi = layui.shopGoodsInfoMainApi;
    var index = layui.index;
    var layedit = layui.layedit;
    var upload = layui.upload;

    var view = {
        init:function(){
            Lib.initGenrealForm($("#addForm"),form);
            this.initSelectList();
            this.initTextArea();
            this.initSubmit();
            this.intiUpload();
        },
        initSubmit:function(area){
            $("#addButton").click(function(){
                 form.on('submit(form)', function(){
                     $("#shopGoodsExtendArea").val(layedit.getContent(1));
                        shopGoodsInfoMainApi.addShopGoodsInfo($('#addForm'),function(){
                         parent.window.dataReload();
                         Common.info("添加成功");
                         Lib.closeFrame();
                     });
                });
            });
        
            $("#addButton-cancel").click(function(){
                Lib.closeFrame();
            });
        },
        initSelectList:function(){
            $.each(JSON.parse(shopSuppliers), function (index,item) {
                $("#supplySelect").append('<option value="'+item.id+'" >'+item.name+'</option>')
            });

            $.each(JSON.parse(shopGoodsCategories), function (index,item) {
                $("#categorySelect").append('<option value="'+item.id+'" >'+item.name+'</option>')
            });

            $.each(JSON.parse(shopTransportTemplates), function (index,item) {
                $("#shopTransportTemplatesSelect").append('<option value="'+item.id+'" >'+item.name+'('+ item.transportStyle + ')'+ '</option>')
            });

            form.render('select');
        },
        initTextArea:function(){
            layedit.set({
                uploadImage: {
                    url: '/stock/shopGoodsInfoMain/uploadImg.json?remark='+uuid+'&type=1'
                    ,type: 'post' //默认post
                },
                height: 1000
            });
            var term = layedit.build('shopGoodsExtendArea'); //建立编辑器
            form.verify({
                content: function() {
                    return layedit.sync(term);
                }
            });
        },
        intiUpload:function () {
            //多文件列表示例
            var demoListView = $('#demoList')
                ,demo2 =  $('#demo2')
                ,uploadListIns = upload.render({
                elem: '#testList'
                ,url: '/stock/shopGoodsInfoMain/uploadImg.json'
                ,accept: 'file'
                ,multiple: true
                ,data:{
                    "remark" : uuid,
                    "type":0
                }
                ,auto: false
                ,bindAction: '#testListAction'
                ,choose: function(obj){
                    var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                    //读取本地文件
                    obj.preview(function(index, file, result){

                        var img = $('<img src="' + result + '" id="upload-img-'+ index +'" alt="'+ file.name +'" style="margin-left:20px; width: 120px; height: 100px;" class="layui-upload-img">');

                        var tr = $(['<tr id="upload-'+ index +'">'
                            ,'<td>'+ file.name +'</td>'
                            ,'<td>等待上传</td>'
                            ,'<td>'
                            ,'<button type="button" class="layui-btn layui-btn-xs demo-reload layui-hide">重传</button>'
                            ,'<button class="layui-btn layui-btn-xs layui-btn-danger demo-delete">删除</button>'
                            ,'</td>'
                            ,'</tr>'].join(''));

                        //单个重传
                        tr.find('.demo-reload').on('click', function(){
                            obj.upload(index, file);
                        });

                        //删除
                        tr.find('.demo-delete').on('click', function(){
                            delete files[index]; //删除对应的文件
                            tr.remove();
                            img.remove();
                            uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                        });

                        demo2.append(img);
                        demoListView.append(tr);
                    });
                }
                ,done: function(res, index, upload){
                    if(res.code == 0){ //上传成功
                        var tr = demoListView.find('tr#upload-'+ index)
                            ,tds = tr.children();
                        tds.eq(1).html('<span style="color: #5FB878;">上传成功</span>');
                        tds.eq(2).html(''); //清空操作
                        return delete this.files[index]; //删除文件队列已经上传成功的文件
                    }
                    this.error(index, upload);
                }
                ,error: function(index, upload){
                    var tr = demoListView.find('tr#upload-'+ index)
                        ,tds = tr.children();
                    tds.eq(1).html('<span style="color: #FF5722;">上传失败</span>');
                    tds.eq(2).find('.demo-reload').removeClass('layui-hide'); //显示重传
                }
            });
        }
    }
    exports('add',view);

    form.verify({
        checkMoreZero : function(value) {
            if (Number(value)<0){
                return '请输入一个正数';
            }
        },
        checkSellPrice : function(value) {
            var sellPrice = $("#purchasePrice").val();
            if (Number(value) < Number(sellPrice)){
                return '售出价小于进价';
            }
        }
    });
});

function setFrozenQuantity() {
    var totalQuantity = $("#totalQuantity").val();
    var avaliableQuantity = $("#avaliableQuantity").val();

    if (totalQuantity == null){
        totalQuantity = 0;
        avaliableQuantity = 0;
    }else if (avaliableQuantity == null){
        avaliableQuantity = 0;
    }
    if (parseInt(totalQuantity) < parseInt(avaliableQuantity)) {
        Common.info("可用数量应小于总数量！");
        $("#avaliableQuantity").val(totalQuantity);
        $("#frozenQuantity").val(0);
    } else {
        $("#frozenQuantity").val(totalQuantity - avaliableQuantity);
    }

}

//进价金额改变， 联动修改
function setSellPriceWithPurchasePrice() {
    var purchasePrice = $("#purchasePrice").val();
    var sellPrice = $("#sellPrice").val();

    if (purchasePrice == null){
        purchasePrice = 0;
        sellPrice = 0;
    }

    var minSellPrice = Number(purchasePrice)*minRatio;
    $("#minRatioSpan").text(' 最小：'+minSellPrice);

    if (minSellPrice > Number(sellPrice)) {
        $("#sellPrice").val(null);
    }
}

//售价金额改变，联动判断
function setSellPriceWithSellPrice() {
    var purchasePrice = $("#purchasePrice").val();
    var sellPrice = $("#sellPrice").val();

    if (purchasePrice == null){
        purchasePrice = 0;
    }
    var minSellPrice = Number(purchasePrice)*minRatio;
    $("#minRatioSpan").text(' 最小：'+minSellPrice);

    if (minSellPrice > Number(sellPrice)) {
        Common.info("单价小于最低售价！"+minSellPrice);
        $("#sellPrice").val(null);
    }
}

    $("#totalQuantity").change(function() {
        setFrozenQuantity();
    });
    $("#avaliableQuantity").change(function() {
        setFrozenQuantity();
    });
    $("#purchasePrice").change(function() {
        setSellPriceWithPurchasePrice();
    });
    $("#sellPrice").change(function() {
        setSellPriceWithSellPrice();
    });
