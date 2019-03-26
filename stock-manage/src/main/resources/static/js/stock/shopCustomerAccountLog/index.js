layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopCustomerAccountLogTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopCustomerAccountLogTable)
            }
        },
        initTable:function(){
            shopCustomerAccountLogTable = table.render({
                elem : '#shopCustomerAccountLogTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 80,
                method : 'post',
                url : Common.ctxPath + '/stock/shopCustomerAccountLog/list.json' // 数据接口
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
                        fixed:'left',
                            width : 70,
                        sort : true
                    },
                    {

                        field : 'customerId',
                        title : '用户ID',
                        sort : true
                    },
                    {

                        field : 'userName',
                        title : '用户名',
                        width : 220 ,
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
                        width : 120,
                        sort : true
                    },
                    {

                        field : 'registFromText',
                        title : '用户渠道',
                        width : 100,
                        sort : true
                    },
                    {

                        field : 'typeText',
                        title : '操作类型',
                        width : 100,
                        sort : true
                    },
                    {

                        field : 'amount',
                        title : '操作金额(消费金)',
                        width : 150,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.amount)
                        }
                    },
                    {

                        field : 'total',
                        title : '总资产(消费金)',
                        width : 150,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.total)
                        }
                    },
                    {

                        field : 'frozen',
                        title : '冻结金额(消费金)',
                        width : 150,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.frozen)
                        }
                    },
                    {

                        field : 'avaliable',
                        title : '可用余额(消费金)',
                        width : 150,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.avaliable)
                        }
                    },
                    {

                        field : 'totalExchange',
                        title : '累计兑换金额(消费金)',
                        width : 180,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.totalExchange)
                        }
                    },
                    {

                        field : 'totalConsume',
                        title : '累计消费金额(消费金)',
                        width : 180,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.totalConsume)
                        }
                    },
                    {

                        field : 'bussKey',
                        title : '业务Id',
                        width : 120,
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
                    },
                    {

                        field : 'remark',
                        title : '备注',

                    }

                ] ]

            });

            table.on('checkbox(shopCustomerAccountLogTable)', function(obj){
                var shopCustomerAccountLog = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopCustomerAccountLogTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopCustomerAccountLog/add.do";
                    Common.openDlg(url,"账户资金流水>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopCustomerAccountLogTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopCustomerAccountLog/edit.do?id="+data.id;
                    Common.openDlg(url,"账户资金流水>编辑");
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