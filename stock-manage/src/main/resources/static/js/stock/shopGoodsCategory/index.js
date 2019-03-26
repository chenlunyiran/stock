layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopGoodsCategoryTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopGoodsCategoryTable)
            }
        },
        initTable:function(){
            shopGoodsCategoryTable = table.render({
                elem : '#shopGoodsCategoryTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopGoodsCategory/list.json' // 数据接口
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

                    field : 'nid', 
                        title : '类别编号',
                        sort : true,
                },
                // {
                //
                //     field : 'parentId',
                //         title : '父类别id',
                // },
                {

                    field : 'name', 
                        title : '类别名称',
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

            table.on('checkbox(shopGoodsCategoryTable)', function(obj){
                var shopGoodsCategory = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopGoodsCategoryTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopGoodsCategory/add.do";
                    Common.openDlg(url,"商品类别管理>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopGoodsCategoryTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopGoodsCategory/edit.do?id="+data.id;
                    Common.openDlg(url,"商品类别管理>编辑");
                },
                del : function() {
                    layui.use(['del'], function(){
                        var delView = layui.del
                        delView.delBatch();
                    });
                }
            ,
                exportDocument : function() {
                    layui.use([ 'shopGoodsCategoryApi' ], function() {
                        var shopGoodsCategoryApi = layui.shopGoodsCategoryApi
                        Common.openConfirm("确认要导出这些商品类别数据?", function() {
                            shopGoodsCategoryApi.exportExcel($("#searchForm"), function(fileId) {
                                Lib.download(fileId);
                            })
                        })
                    });
                },
                importDocument:function(){
                    var uploadUrl = Common.ctxPath+"/stock/shopGoodsCategory/excel/import.do";
                    //模板,
                    var templatePath= "/stock/shopGoodsCategory/shopGoodsCategory_upload_template.xls";
                    //公共的简单上传文件处理
                    var url = "/core/file/simpleUpload.do?uploadUrl="+uploadUrl+"&templatePath="+templatePath;
                    Common.openDlg(url, "商品类别管理>上传");
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