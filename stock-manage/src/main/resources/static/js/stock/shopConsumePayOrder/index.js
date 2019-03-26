layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopConsumePayOrderTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopConsumePayOrderTable)
            }
        },
        initTable:function(){
            shopConsumePayOrderTable = table.render({
                elem : '#shopConsumePayOrderTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopConsumePayOrder/list.json' // 数据接口
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
                        width : 60,
                },
                {

                    field : 'customerId', 
                        title : '客户ID',
                },
                {

                    field : 'payOrderId', 
                        title : '支付订单号',
                },
                {

                    field : 'exchangeId', 
                        title : '兑换ID',
                },
                {

                    field : 'amount', 
                        title : '支付金额',
                },
                {

                    field : 'status', 
                        title : '支付状态',
                },
                {

                    field : 'payTime', 
                        title : '支付时间',
                },
                {

                    field : 'retMsg', 
                        title : '支付结果描述',
                },
                {

                    field : 'createTime', 
                        title : '创建时间',
                },
                {

                    field : 'updateTime', 
                        title : '更新时间',
                },
                {

                    field : 'remark', 
                        title : '备注',
                }

        ] ]

        });

            table.on('checkbox(shopConsumePayOrderTable)', function(obj){
                var shopConsumePayOrder = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopConsumePayOrderTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopConsumePayOrder/add.do";
                    Common.openDlg(url,"payOrder管理>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopConsumePayOrderTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopConsumePayOrder/edit.do?id="+data.id;
                    Common.openDlg(url,"payOrder管理>"+data.id+">编辑");
                },
                del : function() {
                    layui.use(['del'], function(){
                        var delView = layui.del
                        delView.delBatch();
                    });
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