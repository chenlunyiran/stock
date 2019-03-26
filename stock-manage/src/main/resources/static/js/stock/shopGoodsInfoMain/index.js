layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopGoodsInfoTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopGoodsInfoTable)
            }
        },
        initTable:function(){
            shopGoodsInfoTable = table.render({
                elem : '#shopGoodsInfoTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopGoodsInfoMain/list.json' // 数据接口
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

                    field : 'sku', 
                        title : 'SKU',
                        sort : true,
                },
                {

                    field : 'name', 
                        title : '商品名称',
                        sort : true,
                },
                    // {
                    //     field : 'thirdNo',
                    //     title : '供应商商品编号',
                    //     sort : true,
                    // },
               /* {

                    field : 'supplierId',
                        title : '供应商id',
                },*/
                {

                    field : 'categoryName',
                        title : '类别名称',
                        sort : true,
                },
                {

                    field : 'purchasePrice', 
                        title : '进价（元）',
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.purchasePrice, 2)
                        }
                },
                {

                    field : 'sellPrice', 
                        title : '单价（消费金）',
                        sort : true,
                        width: 150,
                },
                {

                    field : 'totalQuantity', 
                        title : '库存量',
                        sort : true,
                },
                {

                    field : 'avaliableQuantity', 
                        title : '可用库存量',
                        sort : true,
                },
                {

                    field : 'frozenQuantity', 
                        title : '冻结库存量',
                        sort : true,
                },
                // {
                //
                //     field : 'sort',
                //         title : '置顶与排序',
                // },
                {

                    field : 'createTime', 
                        title : '创建时间',
                        sort : true,
                        width: 170
                },
                {

                    field : 'updateTime', 
                        title : '更新时间',
                        sort : true,
                        width: 170
                },
                {

                    field : 'remark', 
                        title : '备注',
                        sort : true,
                }

        ] ]

        });

            table.on('checkbox(shopGoodsInfoTable)', function(obj){
                var shopGoodsInfoMain = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },
        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopGoodsInfoTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopGoodsInfoMain/add.do";
                    Common.openDlg(url,"商品管理>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopGoodsInfoTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopGoodsInfoMain/edit.do?id="+data.id;
                    Common.openDlg(url,"商品管理>编辑");
                },
                refresh : function() { // 获取选中数目
                    $.ajax({
                        type: "post",
                        url: "/stock/shopGoodsInfoMain/refresh.json",
                        dataType: "json",
                        async:false,
                        success:function(ret) {
                            if(ret.code == 0){
                                Common.info("缓存刷新成功！");
                            }else{
                                Common.info("缓存刷新失败！");
                            }
                        }});
                },
                del : function() {
                    layui.use(['del'], function(){
                        var delView = layui.del
                        delView.delBatch();
                    });
                }

            ,
                exportDocument : function() {
                    layui.use([ 'shopGoodsInfoApi' ], function() {
                        var shopGoodsInfoApi = layui.shopGoodsInfoApi
                        Common.openConfirm("确认要导出这些商品数据?", function() {
                            shopGoodsInfoApi.exportExcel($("#searchForm"), function(fileId) {
                                Lib.download(fileId);
                            })
                        })
                    });
                },
                importDocument:function(){
                    var uploadUrl = Common.ctxPath+"/stock/shopGoodsInfoMain/excel/import.do";
                    //模板,
                    var templatePath= "/stock/shopGoodsInfoMain/shopGoodsInfo_upload_template.xls";
                    //公共的简单上传文件处理
                    var url = "/core/file/simpleUpload.do?uploadUrl="+uploadUrl+"&templatePath="+templatePath;
                    Common.openDlg(url, "商品管理>上传");
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
