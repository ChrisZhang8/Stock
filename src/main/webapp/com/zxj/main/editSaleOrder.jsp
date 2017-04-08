<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="jquery,ui,easy,easyui,web">
    <meta name="description" content="easyui helps you build your web pages easily!">
    <title>销售订单-修改</title>
    <link rel="stylesheet" type="text/css" href="../css/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="../css/themes/icon.css">
    <link href="../css/jquery.fileupload.css" rel="stylesheet">
    <!-- <link rel="stylesheet" type="text/css" href="http://www.jeasyui.com/easyui/demo/demo.css"> -->
    
    <script type="text/javascript" src="../js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    
    <script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
    <script type="text/javascript" src="../js/jquery.fileupload.js"></script>
    <script type="text/javascript" src="../js/jquery.iframe-transport.js"></script>
    
    <script>
    
    	var root = "<%=request.getContextPath() %>";
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
			    url:root+'/com/zxj/dbm/LogicServlet?method=updateSaleOrder',
			    onSubmit: function(param){
			    	param.pdata=JSON.stringify(data.rows);
			        // return false to prevent submit;
			    },
			    success:function(data){
			    	$.messager.alert('info',data);
			        //alert(data)
			    }
			});
			
			
		}
		
	    
		$(function(){
			
			$('#saleorderform').form('load',{
				SALE_ORDER_NO:"<%=request.getParameter("ORDER_NO")%>"
			});
			
			$('#saleorderform').form('submit', {
				url: root+'/com/zxj/dbm/LogicServlet?method=viewSaleOrder',
				onSubmit: function(param){
					
				},
				success: function(data){
					
					var dt = JSON.parse(data);
					$('#saleorderform').form('load',{
						ORDER_NO:dt.order.ORDER_NO,
						CUSTOMER:dt.order.CUSTOMER,
						ORDER_DATE:dt.order.ORDER_DATE,
						DEPOSIT:dt.order.DEPOSIT,
						OPER_USER:dt.order.OPER_USER,
						INVOICE_TYPE:dt.order.INVOICE_TYPE,
						CURRENCY:dt.order.CURRENCY,
						PAYMENT_MODE:dt.order.PAYMENT_MODE
					});
					
					$('#detail').datagrid('loadData',dt.detail);

				}
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
					{field:'BRAND',title:'品牌',width:'5%',editor:'text'},
					{field:'BATCH_NO',title:'批号',width:'5%',editor:'text'},
					{field:'ORDER_QTY',title:'订单量',align:'right',width:'10%',editor:{type:'numberbox'}},
					{field:'STOCK_QTY',title:'库存量',align:'right',width:'10%',editor:'numberbox'},
					{field:'OUT_QTY',title:'已出货量',align:'right',width:'10%',editor:'numberbox'},
					{field:'NEED_BUY_QTY',title:'需采购量',align:'right',width:'10%',editor:'numberbox'},
					{field:'PRICE',title:'未税单价',align:'right',width:'5%',editor:{type:'numberbox',options:{precision:2}}},
					{field:'TAX_PRICE',title:'含税单价',align:'right',width:'5%',editor:{type:'numberbox',options:{precision:2}}},
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
			        var n1 = $(editors[3].target);
			        var n2 = $(editors[8].target);
			        var n3 = $(editors[9].target);
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
		        url:root+'/com/zxj/dbm/LogicServlet?method=importExcel',
		        add: function (e, data) {
		            //data.context = $('<p/>').text('Uploading...').appendTo(document.body);
		            data.submit();
		        },
		        done: function (e, data) {
		        	//alert(data.result);
		            $('#detail').datagrid('loadData',data.result);
		        }
		    });
			
			
		});
		function getRowIndex(target){
			var tr = $(target).closest('tr.datagrid-row');
			return parseInt(tr.attr('datagrid-row-index'));
		}
		function editrow(target){
			$('#detail').datagrid('beginEdit', getRowIndex(target));
		}
		function deleterow(target){
			$.messager.confirm('Confirm','Are you sure?',function(r){
				if (r){
					$('#detail').datagrid('deleteRow', getRowIndex(target));
				}
			});
		}
		function saverow(target){
			$('#detail').datagrid('endEdit', getRowIndex(target));
		}
		function cancelrow(target){
			$('#detail').datagrid('cancelEdit', getRowIndex(target));
		}
		
		
		
		
		
	</script>
    
</head>
<body>
	<h2 align="center">销售订单-修改</h2>
	<div>
		
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()">保存</a>
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-reload'" style="width:80px" onclick="reload()">刷新</a>
	</div>
	
	<div class="easyui-panel" >
		<form id="saleorderform" method="post">
			<table>
				<tr>
					<td><input class="easyui-textbox" readonly="readonly" name="CUSTOMER" style="width:100%" data-options="label:'客户名称:',required:true"></td>
					<td><input class="easyui-textbox" readonly="readonly" id="SALE_ORDER_NO" name="SALE_ORDER_NO"  style="width:100%" data-options="label:'销售订单号:',required:true"></td>
					<td><input class="easyui-datetimebox" name="ORDER_DATE" label="订单日期:" labelPosition="left" style="width:100%;"></td>
					<td><input class="easyui-numberbox" name="DEPOSIT" style="width:100%" data-options="label:'定金:',min:0,precision:2"></td>
				</tr>
				<tr>
					<td><input class="easyui-textbox" name="OPER_USER" style="width:100%" data-options="label:'业务员:',required:true"></td>
					<td><input class="easyui-combobox" style="width:100%" name="INVOICE_TYPE"  data-options="label:'开票类型：',valueField: 'value',textField: 'label',
																	data: [{
																		label: '不含税',
																		value: '0'
																	},{
																		label: '增值税-17',
																		value: '17'
																	},{
																		label: '普通税',
																		value: '3'
																	}]" />
					</td>
					<td><input class="easyui-combobox" style="width:100%" name="CURRENCY" data-options="label:'币种：',valueField: 'value',textField: 'label',
																	data: [{
																		label: 'RMB',
																		value: '0'
																	},{
																		label: 'dollar',
																		value: '1'
																	}]" />
					
					</td>
					<td><input class="easyui-textbox" name="PAYMENT_MODE" style="width:100%" data-options="label:'付款方式:'"></td>
				</tr>
			
			</table>
		
		
		
		</form>
	
	</div>

	<div style="margin:10px 0">
	</div>
	
	
	<div id="toolbar">
		 <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="insert()">添加明细</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="del()">删除</a>
        
         <span id="weixin_upload" class="btn btn-primary fileinput-button">  
			<span>excel导入</span>  
			<input type="file" id="fileupload" name="weixin_image" multiple>  
		</span>  
	</div>
	
	
	<table id="detail"></table>
	
	
</body>
</html>