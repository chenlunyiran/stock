layui.define([ 'form', 'laydate', 'table' ], function(exports) {
    var form = layui.form;
    var laydate = layui.laydate;
    var table = layui.table;
    var shopGoodsExtendTable = null;
    var view ={
        init:function(){
            this.initTable();
            this.initSearchForm();
            this.initToolBar();
            window.dataReload = function(){
                Lib.doSearchForm($("#searchForm"),shopGoodsExtendTable)
            }
        },
        initTable:function(){
            shopGoodsExtendTable = table.render({
                elem : '#shopGoodsExtendTable',
                height : Lib.getTableHeight(1),
                cellMinWidth: 100,
                method : 'post',
                url : Common.ctxPath + '/stock/shopGoodsExtend/list.json' // 数据接口
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
                },
                {

                    field : 'goodsId', 
                        title : '商品id',
                },
                {

                    field : 'desc', 
                        title : '详细信息',
                },
                {

                    field : 'createTime', 
                        title : '创建时间',
                },
                {

                    field : 'updateTime', 
                        title : '更新时间',
                }

        ] ]

        });

            table.on('checkbox(shopGoodsExtendTable)', function(obj){
                var shopGoodsExtend = obj.data;
                if(obj.checked){
                    //按钮逻辑Lib.buttonEnable()
                }else{

                }
            })
        },

        initSearchForm:function(){
            Lib.initSearchForm( $("#searchForm"),shopGoodsExtendTable,form);
        },
        initToolBar:function(){
            toolbar = {
                add : function() { // 获取选中数据
                    var url = "/stock/shopGoodsExtend/add.do";
                    Common.openDlg(url,"ShopGoodsExtend管理>新增");
                },
                edit : function() { // 获取选中数目
                    var data = Common.getOneFromTable(table,"shopGoodsExtendTable");
                    if(data==null){
                        return ;
                    }
                    var url = "/stock/shopGoodsExtend/edit.do?id="+data.id;
                    Common.openDlg(url,"ShopGoodsExtend管理>"+data.id+">编辑");
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