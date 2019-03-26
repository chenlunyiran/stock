layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopConsConfigTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopConsConfigTable)
            }
        },
        initTable:function(){
            shopConsConfigTable = table.render({
                elem : '#shopConsConfigTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopConsConfig/list.json' // 数据接口
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
                    sort : true
                },
                {

                    field : 'nid', 
                        title : '标识',
                    width : 150,
                    sort : true
                },
                {

                    field : 'name', 
                        title : '名称',
                    width : 180,
                    sort : true
                },
                {

                    field : 'value', 
                        title : '值',
                    sort : true
                },
                {

                    field : 'description', 
                        title : '描述',
                    width : 180,
                    sort : true

                },
                {

                    field : 'statusText',
                        title : '状态',
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

                    field : 'remark', 
                        title : '备注',
                }
*/
        ] ]

        });

            table.on('checkbox(shopConsConfigTable)', function(obj){
                var shopConsConfig = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopConsConfigTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopConsConfig/add.do";
                    // Common.openDlg(url,"ShopConsConfig管理>新增");
                    layer.open({
                        title: '新增变量',
                        type: 2,
                        area: ['750px', '450px'],
                        content: url
                    });
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopConsConfigTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopConsConfig/edit.do?id="+data.id;
                    // Common.openDlg(url,"ShopConsConfig管理>"+data.id+">编辑");
                    layer.open({
                        title: '变量配置编辑',
                        type: 2,
                        area: ['750px', '450px'],
                        content: url
                    });
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