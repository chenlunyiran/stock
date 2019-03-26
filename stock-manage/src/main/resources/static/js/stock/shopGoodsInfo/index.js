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
                url : Common.ctxPath + '/stock/shopGoodsInfo/list.json' // 数据接口
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
                    width : 130,
                    templet: function(d){
                        return stickLabel(d.sku,d.sort)
                    }
                },
                {

                    field : 'name', 
                        title : '商品名称',
                        sort : true,
                },
              /*  {

                    field : 'supplierId', 
                        title : '供应商id',
                },*/
               /* {

                    field : 'categoryId', 
                        title : '类别id',
                },*/
                /*{

                    field : 'purchasePrice', 
                        title : '进价',
                },*/
                {

                    field : 'sellPrice', 
                        title : '单价(消费金)',
                        sort : true,
                        templet: function(d){
                            return Common.formatMoney(d.sellPrice,2)
                        }
                },
                {

                    field : 'stockStatus', 
                        title : '库存状态',
                        sort : true,
                },
                {

                    field : 'avaliableQuantity', 
                        title : '可销库存',
                        sort : true,
                },
                {

                    field : 'frozenQuantity', 
                        title : '冻结库存',
                        sort : true,
                },
               
                {

                    field : 'sellTypeText', 
                        title : '售卖方式',
                        sort : true,
                },
                {

                    field : 'launchTypeText', 
                        title : '上架方式',
                        sort : true,
                },
                {

                    field : 'launchStatusText', 
                        title : '上架状态',
                        sort : true,
                },
               /* {

                    field : 'state', 
                        title : '有效性， 0有效 1无效',
                },*/
               /* {

                    field : 'sort', 
                        title : '置顶与排序:0不置顶、1-5为置顶顺序',
                },*/
               {

                    field : 'createTime', 
                        title : '创建时间',
                        sort : true,
                        width : 170,
                },
               {

                    field : 'updateTime', 
                        title : '更新时间',
                        sort : true,
                        width : 170,
                },

        ] ]

        });

            table.on('checkbox(shopGoodsInfoTable)', function(obj){
                var shopGoodsInfo = obj.data;
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
                    var url = "/stock/shopGoodsInfo/add.do";
                    Common.openDlg(url,"ShopGoodsInfo管理>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopGoodsInfoTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopGoodsInfo/edit.do?id="+data.id;
                    Common.openDlg(url,"售卖规则编辑");
                },
                del : function() {
                    layui.use(['del'], function(){
                        var delView = layui.del
                        delView.delBatch();
                    });
                },
                grounding : function() {
                    layui.use(['grounding'], function(){
                        var groundingView = layui.grounding
                        groundingView.groundingBatch();
                    });
                },
                undercarriage : function() {
                    layui.use(['undercarriage'], function(){
                        var undercarriageView = layui.undercarriage
                        undercarriageView.undercarriageBatch();
                    });
                },
                out : function() {
                    layui.use(['out'], function(){
                        var outView = layui.out
                        outView.outBatch();
                    });
                },
                stick : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopGoodsInfoTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopGoodsInfo/stick.do?id="+data.id;

                    //跳页面
                    // Common.openDlg(url,"将此商品置顶至");

                    //弹框
                    layer.open({
                        title: '将此商品置顶至',
                        type: 2,
                        area: ['330px', '250px'],
                        content: [url, 'no']
                    });
                },
           
                exportDocument : function() {
                    layui.use([ 'shopGoodsInfoApi' ], function() {
                        var shopGoodsInfoApi = layui.shopGoodsInfoApi
                        Common.openConfirm("确认要导出这些ShopGoodsInfo数据?", function() {
                            shopGoodsInfoApi.exportExcel($("#searchForm"), function(fileId) {
                                Lib.download(fileId);
                            })
                        })
                    });
                },
                importDocument:function(){
                    var uploadUrl = Common.ctxPath+"/stock/shopGoodsInfo/excel/import.do";
                    //模板,
                    var templatePath= "/stock/shopGoodsInfo/shopGoodsInfo_upload_template.xls";
                    //公共的简单上传文件处理
                    var url = "/core/file/simpleUpload.do?uploadUrl="+uploadUrl+"&templatePath="+templatePath;
                    Common.openDlg(url, "ShopGoodsInfo管理>上传");
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


function stickLabel(sku,sort) {
    if(sort <100){
        return sku+" <span class='layui-badge' style='width: 30px;'>"+sort+"</span>";
    }else {
        return sku;
    }
}