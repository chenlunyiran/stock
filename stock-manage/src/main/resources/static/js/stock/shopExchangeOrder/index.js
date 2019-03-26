layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopExchangeOrderTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopExchangeOrderTable)
            }
        },
        initTable:function(){
            shopExchangeOrderTable = table.render({
                elem : '#shopExchangeOrderTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopExchangeOrder/list.json' // 数据接口
                ,page : Lib.tablePage // 开启分页
                ,limit : 30,
                cols : [ [ // 表头
                    /*{
                        type : 'checkbox',
                        fixed:'left',
                    },*/
                {
                    field : 'id', 
                        title : 'ID',
                    fixed:'center',
                        width : 60,
                    sort : true
                },
                {
                    field : 'customerId', 
                    title : '用户ID',
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
                        width : 150,
                        sort : true
                    },
                    {
                        field : 'exchangeOrderId',
                        title : '订单编号',
                        width : 170,
                        sort : true
                    },
                    {
                        field : 'exchangeChannel',
                        title : '兑换渠道',
                        sort : true
                    },
                    {
                        field : 'exchangeRule',
                        title : '兑换规则',
                        sort : true
                    },
                    {
                        field : 'assetAmount',
                        title : '资产（元）',
                        width : 150,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.assetAmount, 2)
                        }
                    },
                    {
                        field : 'consumeAmount',
                        title : '消费金',
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.consumeAmount, 2)
                        }
                    },
                    {
                        field : 'statusText',
                        title : '订单状态',
                        sort : true
                    },
                    {
                        field : 'createTime',
                        title : '创建时间',
                        width : 170,
                        sort : true
                    },
                    {
                        field : 'updateTime',
                        title : '更新时间',
                        width : 170,
                        sort : true
                    }
        ] ]

        });

            /*table.on('checkbox(shopExchangeOrderTable)', function(obj){
                var shopExchangeOrder = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })*/
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopExchangeOrderTable,form);
        },
        initToolBar:function(){
            toolbar = {
               /* add : function() { // 获取选中数据
                    var url = "/stock/shopExchangeOrder/add.do";
                    Common.openDlg(url,"ShopExchangeOrder管理>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopExchangeOrderTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopExchangeOrder/edit.do?id="+data.id;
                    Common.openDlg(url,"ShopExchangeOrder管理>"+data.id+">编辑");
                },
                del : function() {
                    layui.use(['del'], function(){
                        var delView = layui.del
                        delView.delBatch();
                    });
                }
            ,
                exportDocument : function() {
                    layui.use([ 'shopExchangeOrderApi' ], function() {
                        var shopExchangeOrderApi = layui.shopExchangeOrderApi
                        Common.openConfirm("确认要导出这些ShopExchangeOrder数据?", function() {
                            shopExchangeOrderApi.exportExcel($("#searchForm"), function(fileId) {
                                Lib.download(fileId);
                            })
                        })
                    });
                },
                importDocument:function(){
                    var uploadUrl = Common.ctxPath+"/stock/shopExchangeOrder/excel/import.do";
                    //模板,
                    var templatePath= "/stock/shopExchangeOrder/shopExchangeOrder_upload_template.xls";
                    //公共的简单上传文件处理
                    var url = "/core/file/simpleUpload.do?uploadUrl="+uploadUrl+"&templatePath="+templatePath;
                    Common.openDlg(url, "ShopExchangeOrder管理>上传");
                }*/
        };
            $('.ext-toolbar').on('click', function() {
                var type = $(this).data('type');
                toolbar[type] ? toolbar[type].call(this) : '';
            });
        }
    }
    exports('index',view);

});