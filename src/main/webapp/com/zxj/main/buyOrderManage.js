$(function(){
    $('#buyOrders').datagrid({
        view: detailview,
        detailFormatter:function(index,row){
            return '<div style="padding:2px"><table class="ddv"></table></div>';
        },
        onExpandRow: function(index,row){
            var ddv = $(this).datagrid('getRowDetail',index).find('table.ddv');
            ddv.datagrid({
                url:root+'/com/zxj/dbm/BuyOrderServlet?method=viewPaginatorOrderDetail',
                queryParams: {ORDER_NO:row.ORDER_NO},
                fitColumns:true,
                rownumbers:true,
                pagination:true,
                singleSelect:false,
                loadMsg:'',
                height:'auto',
                columns:[[
                    {field:"ck",checkbox:"true"},
                    {field:'SID',hidden:true},
                    {field:'PRODUCT',title:'产品型号',width:'10%'},
                    {field:'BRAND',title:'品牌',width:'5%'},
                    {field:'VENDOR',title:'供应商',width:'10%'},
                    {field:'SHAPE',title:'封装',width:'10%'},
                    {field:'BATCH_NO',title:'批号',width:'10%'},
                    {field:'ORDER_QTY',title:'订货量',align:'right',width:'8%'},
                    {field:'RECEIVE_QTY',title:'已到货量',align:'right',width:'8%'},
                    {field:'QTY',title:'此次到货量',align:'right',width:'8%',editor:'numberbox'},
                    {field:'TAX_POINT',title:'税点',align:'right',width:'10%'},
                    {field:'PRICE',title:'单价',align:'right',width:'10%'},
                    {field:'TOTAL_AMOUNT',title:'总金额',align:'right',width:'10%'},
                    {field:'REMARK',title:'备注',width:'20%',editor:'text'}

                ]],

                onDblClickCell: function(index,field,value){
                    $(this).datagrid('beginEdit', index);
                    var ed = $(this).datagrid('getEditor', {index:index,field:field});
                    $(ed.target).focus();
                },


                onClickCell:function(index,field,value){
                    $(this).datagrid('endEdit', index);
                },

                onEndEdit:function(index,row){
                    row.editing = false;
                    $(this).datagrid('refreshRow', index);
                },

                onResize:function(){
                    $('#buyOrders').datagrid('fixDetailRowHeight',index);
                },
                onLoadSuccess:function(data){
                    setTimeout(function(){
                        $('#buyOrders').datagrid('fixDetailRowHeight',index);
                    },0);
                }
            });
            var pager =ddv.datagrid('getPager');

            pager.pagination({
                showPageList:true,
                buttons:[{
                    iconCls:'icon-ok',
                    handler:function(){
                        storage(ddv,row);
                    }
                }
                ]
            });


            $('#buyOrders').datagrid('fixDetailRowHeight',index);
        },
        rownumbers:true,
        pagination:true,
        url:root+'/com/zxj/dbm/BuyOrderServlet?method=viewPaginatorOrders',
        /* queryParams: {
         name: 'easyui',
         subject: 'datagrid'
         } */
        columns:[[
            {field:'ORDER_NO',title:'采购订单号',align:'center',width:'20%'},
            {field:'ORDER_DATE',title:'订单日期',align:'center',width:'20%'},
            {field:'DELIVERY_DATE',title:'交期',align:'center',width:'20%'},
            {field:'CREATE_USER',title:'业务员',align:'center',width:'10%'},
            {field:'REMARK',title:'备注',align:'center',width:'20%'},
            {field:'STATUS',title:'状态',align:'center',width:'5%'},
            {field:'ACTION',title:'操作',width:80,align:'center',width:'5%',
                formatter:function(value,row,index){
                    var s = '<a href="'+root+'/com/zxj/main/editBuyOrder.jsp?ORDER_NO='+row.ORDER_NO+'" class="easyui-linkbutton" target="_blank">修改</a> ';
                    return s;

                }
            }

        ]]

    });



});


function query(){

    $('#buyOrders').datagrid('load',{
        ORDER_NO:$('#ORDER_NO').val()
    });
}
//入库
function storage(ddv,row){
    var rows = ddv.datagrid('getChecked');
    //alert(JSON.stringify(rows));
    var param = new Object();
    param.ORDER_NO = row.ORDER_NO;
    param.CREATE_USER = row.CREATE_USER;
    param.PRODUCTS = JSON.stringify(rows);
    $.ajax({
        type:"post",
        url:root+'/com/zxj/dbm/StorageServlet?method=storage',
        data:param,
        async:false,
        success:function(data) {
            if(data.status == 'N'){
                $.messager.alert('error',data.message);
            }else{
                $.messager.alert('info',data.result);
                ddv.datagrid('reload');
            }

        },
        error:function(data){
            alert(data.responseText);
        }
    });


}

function freshTable(){
    $('#buyOrders').datagrid('reload');

}