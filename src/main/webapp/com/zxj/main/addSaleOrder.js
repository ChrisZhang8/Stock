
function insert(){
    var row = $('#detail').datagrid('getSelected');
    if (row){
        var index = $('#detail').datagrid('getRowIndex', row);
        index=index+1;
    } else {
        index = 0;
    }

    $('#detail').datagrid('insertRow', {
        index: index,
        row:{}
    });
    $('#detail').datagrid('selectRow',index);
    $('#detail').datagrid('beginEdit',index);
}

function delEach(){
    var row = $('#detail').datagrid('getSelections');
    if(row.length>0){
        var index = $('#detail').datagrid('getRowIndex', row[0]);
        $('#detail').datagrid('deleteRow',index);
        delEach();
    }

}

function del(){

    $.messager.confirm('Confirm','Are you sure?',function(r){
        if (r){
            delEach();
        }
    });


}
function save(){
    var data = $('#detail').datagrid('getData');
    //alert(JSON.stringify(data));
    //var fmdata = $("#saleorderform").serialize();

    $('#saleorderform').form('submit', {
        url:root+'/com/zxj/dbm/SaleOrderServlet?method=addSaleOrder',
        onSubmit: function(param){
            param.pdata=JSON.stringify(data.rows);
            if (data.total == 0){
                $.messager.alert('error','没有订单明细信息！','error');
                return false;
            }
            var isValid = $(this).form('validate');
            if (!isValid){
                $.messager.progress('close');	// hide progress bar while the form is invalid
            }
            return isValid;	// return false will stop the form submission
        },
        success:function(data){
            var rs = JSON.parse(data);
            if(rs.status == 'N'){
                $.messager.alert('error',rs.message,'error');
            }else{
                $.messager.alert('info',rs.result,'info');
                $('#saleorderform').form('clear');
                $('#saleorderform').form('load',{
                    SALE_ORDER_NO:uuid(8, 16)
                });
                $('#detail').datagrid('loadData',{"total":0,"rows":[]});
            }

            //alert(data)
        }
    });


}

function reject(){
    $('#detail').datagrid('rejectChanges');
    editIndex = undefined;
}

$(function(){


    $('#saleorderform').form('load',{
        SALE_ORDER_NO:uuid(8, 16)
    });

    $('#detail').datagrid({
        //title:'销售明细',
        //iconCls:'icon-edit',
        toolbar: '#toolbar',
        rownumbers:true,
        singleSelect:false,
        idField:'PRODUCT',
        //url:'../data/datagrid_data.json',
        //fitColumns:true,
        columns:[[
            //{field:"ck",checkbox:"true"},
            {field:'PRODUCT',title:'产品型号',width:'10%',editor:'text'},
            {field:'PRODUCT_NAME',title:'名称',width:'5%',editor:'text'},
            {field:'BRAND',title:'品牌',width:'5%',editor:'text'},
            {field:'PACKAGE',title:'封装',width:'5%',editor:'text'},
            {field:'BATCH_NO',title:'批号',width:'5%',editor:'text'},
            {field:'ORDER_QTY',title:'订单量',align:'right',width:'10%',editor:{type:'numberbox'}},
            {field:'PRICE',title:'单价',align:'right',width:'5%',editor:{type:'numberbox',options:{precision:2}}},
            {field:'TOTAL_AMOUNT',title:'总金额',align:'right',width:'5%',editor:{type:'numberbox',options:{precision:2}}},

            {field:'REMARK',title:'备注',width:'20%',editor:'text'}

        ]],

        onDblClickRow:function(index,row){

            $(this).datagrid('beginEdit', index);
        },
        onClickRow:function(index,row){
            $(this).datagrid('endEdit', index);
        },

        onEndEdit:function(index,row){

            row.editing = false;
            $(this).datagrid('refreshRow', index);
        },
        onBeforeEdit:function(index,row){
            row.editing = true;
            $(this).datagrid('refreshRow', index);
        },
        onBeginEdit:function(rowIndex){
            var editors = $(this).datagrid('getEditors', rowIndex);
            var n1 = $(editors[5].target);
            var n2 = $(editors[6].target);
            var n3 = $(editors[7].target);
            n1.add(n2).numberbox({
                onChange:function(){
                    var cost = n1.numberbox('getValue')*n2.numberbox('getValue');
                    n3.numberbox('setValue',cost.toFixed(2));
                }
            })
        },
        onAfterEdit:function(index,row){
            row.editing = false;
            $(this).datagrid('refreshRow', index);
        },
        onCancelEdit:function(index,row){
            row.editing = false;
            $(this).datagrid('refreshRow', index);
        }
    });


    $('#fileupload').fileupload({
        dataType: 'json',
        url:root+'/com/zxj/dbm/SaleOrderServlet?method=importExcel',
        add: function (e, data) {
            //data.context = $('<p/>').text('Uploading...').appendTo(document.body);
            data.submit();
        },
        done: function (e, data) {
            if (data.status=='N'){
                $.messager.alert('error',data.message);
            }else {
                //alert(JSON.stringify(data.result));
                $('#detail').datagrid('loadData',data.result);
            }

        }
    });


});


function uuid(len, radix) {
    var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
    var uuid = [], i;
    radix = radix || chars.length;

    if (len) {
        // Compact form
        for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
    } else {
        // rfc4122, version 4 form
        var r;

        // rfc4122 requires these characters
        uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
        uuid[14] = '4';

        // Fill in random data. At i==19 set the high bits of clock sequence as
        // per rfc4122, sec. 4.1.5
        for (i = 0; i < 36; i++) {
            if (!uuid[i]) {
                r = 0 | Math.random()*16;
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
            }
        }
    }

    return uuid.join('');
}