layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopConsumeGoodsOrderTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopConsumeGoodsOrderTable)
            }
        },
        initTable:function(){
            shopConsumeGoodsOrderTable = table.render({
                elem : '#shopConsumeGoodsOrderTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopConsumeGoodsOrder/list.json' // 数据接口
                ,page : Lib.tablePage // 开启分页
                ,limit : 30,
                cols : [ [ // 表头
                    {
                        type : 'checkbox',
                        fixed:'left',
                    },
                {

                    field : 'id', 
                        title : 'ID',
                    fixed:'left',
                        width : 100,
                    sort : true
                },
                {

                    field : 'customerId', 
                        title : '客户ID',
                    sort : true
                },
                    {

                        field : 'name',
                        title : '姓名',
                        sort : true
                    },
                    {

                        field : 'phone',
                        title : '手机号',
                        width : 130,
                        sort : true
                    },
                    {

                        field : 'payOrderId',
                        title : '支付订单号',
                        width : 190,
                        sort : true
                    },
                    {

                        field : 'consumeGoodsOrderId',
                        title : '订单编号',
                        width : 190,
                        sort : true
                    },
                    {

                        field : 'goodsName',
                        title : '商品名称',
                        width : 170,
                        sort : true
                    },
                    {

                        field : 'price',
                        title : '单价（消费金）',
                        width : 150,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.price, 2)
                        }
                    },
                    {

                        field : 'count',
                        title : '数量',
                        sort : true
                    },
                    {

                        field : 'freightAmount',
                        title : '运费（消费金）',
                        width : 150,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.freightAmount, 2)
                        }
                    },
                    {
                        field : 'amount',
                        title : '实付款（消费金）',
                        width : 150,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.amount, 2)
                        }
                    },
                    {
                        field : 'statusText',
                        title : '订单状态',
                        sort : true
                    },
                    {

                        field : 'payTime',
                        title : '支付时间',
                        width : 160,
                        sort : true
                    },
                    {

                        field : 'logistics',
                        title : '物流信息',
                        width : 170,
                        sort : true
                    },
                    {
                        field : 'createTime',
                        title : '创建时间',
                        width : 160,
                        sort : true
                    },
                    {
                        field : 'updateTime',
                        title : '更新时间',
                        width : 160,
                        sort : true
                    }/*,
                    {
                        title : '操作',
                        toolbar:'#barDemo',
                        fixed:'right',
                        width:278,
                    }*/

        ] ]

        });

            table.on('checkbox(shopConsumeGoodsOrderTable)', function(obj){
                var shopConsumeGoodsOrder = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            });
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopConsumeGoodsOrderTable,form);
        },
        initToolBar:function(){
            toolbar = {
                /*add : function() { // 获取选中数据
                    var url = "/stock/shopConsumeGoodsOrder/add.do";
                    Common.openDlg(url,"ShopConsumeGoodsOrder管理>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopConsumeGoodsOrderTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopConsumeGoodsOrder/edit.do?id="+data.id;
                    Common.openDlg(url,"ShopConsumeGoodsOrder管理>"+data.id+">编辑");
                },
                del : function() {
                    layui.use(['del'], function(){
                        var delView = layui.del
                        delView.delBatch();
                    });
                }
            ,*/
                detail : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopConsumeGoodsOrderTable");
                    if(data==null){
                        return ;
                    }

                    var url = "/stock/shopConsumeGoodsOrder/detail.do?id="+data.id;
                    Common.openDlg(url,"消费订单>订单详情");
                },

                cancelOrder : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopConsumeGoodsOrderTable");
                    if(data==null){
                        return ;
                    }

                    if(data.status == 3){
                        layer.alert("此订单已被取消关闭！");
                        return;
                    }

                    if(data.status == 0 || data.status == 1 || data.status){
                        if(data.status == 0){
                            layer.confirm("此订单还未支付，您确认要取消关闭此订单吗？",{btn: ['确定', '取消'],title:"提示"},function (index) {
                                $.ajax({
                                    type: "post",
                                    url: "/stock/shopConsumeGoodsOrder/cancelOrder.json?id="+data.id,
                                    dataType: "json",
                                    async:false,
                                    success:function(ret) {
                                        if(ret.code == 0){
                                            Common.info("订单成功取消！");
                                        }else{
                                            Common.info("订单取消失败！");
                                        }
                                        Lib.doSearchForm($("#searchForm"),shopConsumeGoodsOrderTable)
                                        layer.close(index);
                                    },
                                    error:function () {
                                        Common.error("服务器错误，请联系管理员！");
                                    }});
                            });

                        }else{
                            var url = "/stock/shopConsumeGoodsOrder/cancelOrder.do?id="+data.id;
                            Common.openDlg(url,"消费订单>取消订单");
                        }
                    }else{
                        layer.alert("此订单不能执行取消操作！");
                        return;
                    }

                },
                logisticInfo : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopConsumeGoodsOrderTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopConsumeGoodsOrder/logisticInfo.do?id="+data.id;
                    //Common.openDlg(url,"消费订单>物流信息");
                    layer.open({
                        title: '物流信息',
                        type: 2,
                        area: ['550px', '350px'],
                        content: url
                    });
                },
                remark : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopConsumeGoodsOrderTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopConsumeGoodsOrder/remark.do?id="+data.id;
                    //Common.openDlg(url,"消费订单>备注");
                    layer.open({
                        title: '备注',
                        type: 2,
                        area: ['550px', '350px'],
                        content: url
                    });
                },

                exportDocument : function() {
                    layui.use([ 'shopConsumeGoodsOrderApi' ], function() {
                        var shopConsumeGoodsOrderApi = layui.shopConsumeGoodsOrderApi
                        Common.openConfirm("确认要导出订单数据?", function() {
                            shopConsumeGoodsOrderApi.exportExcel($("#searchForm"), function(fileId) {
                                Lib.download(fileId);
                            })
                        })
                    });
                },
                importDocument:function(){
                    var uploadUrl = Common.ctxPath+"/stock/shopConsumeGoodsOrder/excel/import.do";
                    //模板,
                    var templatePath= "/stock/shopConsumeGoodsOrder/shopConsumeGoodsOrder_upload_template.xls";
                    //公共的简单上传文件处理
                    var url = "/core/file/simpleUpload.do?uploadUrl="+uploadUrl+"&templatePath="+templatePath;
                    Common.openDlg(url, "ShopConsumeGoodsOrder管理>上传");
                }
        };
            $('.ext-toolbar').on('click', function() {
                var type = $(this).data('type');
                toolbar[type] ? toolbar[type].call(this) : '';
            });
        }
    }
    exports('index',view);

});