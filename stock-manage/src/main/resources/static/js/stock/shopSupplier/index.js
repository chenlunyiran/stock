layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopSupplierTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopSupplierTable)
            }
        },
        initTable:function(){
            shopSupplierTable = table.render({
                elem : '#shopSupplierTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopSupplier/list.json' // 数据接口
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
                        title : '供应商名称',
                        sort : true,
                },
                {

                    field : 'remark', 
                        title : '备注',
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

        ] ]

        });

            table.on('checkbox(shopSupplierTable)', function(obj){
                var shopSupplier = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopSupplierTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopSupplier/add.do";
                    Common.openDlg(url,"供应商管理>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopSupplierTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopSupplier/edit.do?id="+data.id;
                    Common.openDlg(url,"供应商管理管理>编辑");
                },
                del : function() {
                    layui.use(['del'], function(){
                        var delView = layui.del
                        delView.delBatch();
                    });
                }
            ,
                exportDocument : function() {
                    layui.use([ 'shopSupplierApi' ], function() {
                        var shopSupplierApi = layui.shopSupplierApi
                        Common.openConfirm("确认要导出这些供应商管理数据?", function() {
                            shopSupplierApi.exportExcel($("#searchForm"), function(fileId) {
                                Lib.download(fileId);
                            })
                        })
                    });
                },
                importDocument:function(){
                    var uploadUrl = Common.ctxPath+"/stock/shopSupplier/excel/import.do";
                    //模板,
                    var templatePath= "/stock/shopSupplier/shopSupplier_upload_template.xls";
                    //公共的简单上传文件处理
                    var url = "/core/file/simpleUpload.do?uploadUrl="+uploadUrl+"&templatePath="+templatePath;
                    Common.openDlg(url, "供应商管理管理>上传");
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