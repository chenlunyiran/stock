layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopCustomerAccountTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopCustomerAccountTable)
            }
        },
        initTable:function(){
            shopCustomerAccountTable = table.render({
                elem : '#shopCustomerAccountTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 80,
                method : 'post',
                url : Common.ctxPath + '/stock/shopCustomerAccount/list.json' // 数据接口
                ,page : Lib.tablePage // 开启分页
                ,limit : 30,
                cols : [ [ // 表头
                    // {
                    //     type : 'checkbox',
                    //     fixed:'left',
                    // },
                    {

                        field : 'id',
                        title : 'ID',
                        fixed: 'left',
                        width : 70,
                        sort : true
                    },
                    {

                        field : 'customerId',
                        title : '用户ID',
                        fixed: 'left',
                        sort : true
                    },

                    {

                        field : 'userName',
                        title : '用户名',
                        width : 220,
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

                        field : 'total',
                        title : '总资产(消费金)',
                        width : 160,
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
                        title : '可用金额(消费金)',
                        width : 150,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.avaliable)
                        }
                    },
                    {
                        field : 'totalExchange',
                        title : '累计兑换额(消费金)',
                        width : 170,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.totalExchange)
                        }
                    },
                    {
                        field : 'totalConsume',
                        title : '累计消费额(消费金)',
                        width : 170,
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.totalConsume)
                        }
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

            table.on('checkbox(shopCustomerAccountTable)', function(obj){
                var shopCustomerAccount = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopCustomerAccountTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopCustomerAccount/add.do";
                    Common.openDlg(url,"账户列表>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopCustomerAccountTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopCustomerAccount/edit.do?customerId="+data.customerId;
                    Common.openDlg(url,"账户列表>编辑");
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
