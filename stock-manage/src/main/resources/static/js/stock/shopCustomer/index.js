layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopCustomerTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopCustomerTable)
            }
        },
        initTable:function(){
            shopCustomerTable = table.render({
                elem : '#shopCustomerTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopCustomer/list.json' // 数据接口
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
                            width : 70,
                        sort : true,
                    },
                    {

                        field : 'userName',
                        title : '用户名',
                        width : 240,
                        sort : true
                    },
                    {

                        field : 'name',
                        title : '姓名',
                        width : 120,
                        sort : true
                    },
                    {

                        field : 'phone',
                        title : '手机号',
                        width : 150,
                        sort : true
                    },
                    {

                        field : 'idCard',
                        title : '身份证号',
                        width : 200,
                        sort : true
                    },
                    {

                        field : 'registFromText',
                        title : '用户来源',
                        width : 150,
                        sort : true
                    },
                    {

                        field : 'originalCustomerId',
                        title : '原债权用户ID',
                        width : 120,
                        sort : true,
                    },
                    {

                        field : 'createTime',
                        title : '开户时间',
                        width : 200,
                        sort : true
                    },
                    {

                        field : 'updateTime',
                        title : '更新时间',
                        width : 200,
                        sort : true
                    }

                ] ]

            });

            table.on('checkbox(shopCustomerTable)', function(obj){
                var shopCustomer = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopCustomerTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopCustomer/add.do";
                    Common.openDlg(url,"用户列表>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopCustomerTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopCustomer/edit.do?id="+data.id;
                    Common.openDlg(url,"用户列表>编辑");
                },
                detail : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopCustomerTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopCustomer/detail.do?id="+data.id;
                    Common.openDlg(url,"用户列表>详情");
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