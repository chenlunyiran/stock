layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopTransportTemplateTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopTransportTemplateTable)
            }
        },
        initTable:function(){
            shopTransportTemplateTable = table.render({
                elem : '#shopTransportTemplateTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopTransportTemplate/list.json' // 数据接口
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
                        sort : true,
                },
                {

                    field : 'name',
                        title : '模板名称',
                        sort : true,
                },
                {

                    field : 'transportStyle', 
                        title : '运送方式',
                        sort : true,
                },
                {

                    field : 'firstCount', 
                        title : '首件（个）',
                        sort : true,
                },
                {

                    field : 'firstPrice', 
                        title : '运费（元）',
                        sort : true,
                },
                {

                    field : 'nextCount', 
                        title : '续件（个）',
                        sort : true,
                },
                {

                    field : 'nextPrice', 
                        title : '运费（元）',
                        sort : true,
                },
                {

                    field : 'createTime',
                        title : '创建时间',
                        sort : true,
                },
                {

                    field : 'updateTime',
                        title : '更新时间',
                        sort : true,
                },
                {

                    field : 'remark', 
                        title : '备注',
                        sort : true,
                }

        ] ]

        });

            table.on('checkbox(shopTransportTemplateTable)', function(obj){
                var shopTransportTemplate = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopTransportTemplateTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopTransportTemplate/add.do";
                    Common.openDlg(url,"运费管理>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopTransportTemplateTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopTransportTemplate/edit.do?id="+data.id;
                    Common.openDlg(url,"运费管理>编辑");
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