<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="jquery,ui,easy,easyui,web">
    <meta name="description" content="easyui helps you build your web pages easily!">
    <title>采购订单</title>
    <link rel="stylesheet" type="text/css" href="../css/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="../css/themes/icon.css">
    <!-- <link rel="stylesheet" type="text/css" href="http://www.jeasyui.com/easyui/demo/demo.css"> -->
    
    <script type="text/javascript" src="../js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
</head>
<body>
	<h2 align="center">采购订单</h2>
	<div class="demo-info">
		
		<div>销售订单.</div>
	</div>
	
	<div style="margin:10px 0">
	</div>
	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="insert()">新增</a>
	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="del()">删除</a>
	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="save()">保存</a>
	<table id="tt"></table>
	
	<script>
		
		$(function(){
			$('#tt').datagrid({
				title:'销售明细',
				//iconCls:'icon-edit',
				//width:660,
				//height:250,
				singleSelect:true,
				idField:'PRODUCT',
				//url:'../data/datagrid_data.json',
				//fitColumns:true,
				columns:[[
					{field:'PRODUCT',title:'产品型号',editor:'text'},
					{field:'BRAND',title:'品牌',editor:'text'},
					{field:'ORDER_QTY',title:'订单量',align:'right',editor:{type:'numberbox',options:{precision:1}}},
					{field:'STOCK_QTY',title:'库存量',align:'right',editor:'numberbox'},
					{field:'OUT_QTY',title:'已出货量',align:'right',editor:'numberbox'},
					{field:'NEED_BUY_QTY',title:'需采购量',align:'right',editor:'numberbox'},
					{field:'PRICE',title:'未税单价',align:'right',editor:{type:'numberbox',options:{precision:2}}},
					{field:'TAX_PRICE',title:'含税单价',align:'right',editor:{type:'numberbox',options:{precision:2}}},
					{field:'TOTAL_AMOUNT',title:'总金额',align:'right',editor:{type:'numberbox',options:{precision:2}}},
					{field:'REMARK',title:'备注',editor:'text'},
					{field:'action',title:'Action',align:'center',
						formatter:function(value,row,index){
							if (row.editing){
								var s = '<a href="javascript:void(0)" onclick="saverow(this)">Save</a> ';
								var c = '<a href="javascript:void(0)" onclick="cancelrow(this)">Cancel</a>';
								return s+c;
							} else {
								var e = '<a href="javascript:void(0)" onclick="editrow(this)">Edit</a> ';
								var d = '<a href="javascript:void(0)" onclick="deleterow(this)">Delete</a>';
								return e+d;
							}
						}
					}
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
				onAfterEdit:function(index,row){
					row.editing = false;
					$(this).datagrid('refreshRow', index);
				},
				onCancelEdit:function(index,row){
					row.editing = false;
					$(this).datagrid('refreshRow', index);
				}
			});
		});
		function getRowIndex(target){
			var tr = $(target).closest('tr.datagrid-row');
			return parseInt(tr.attr('datagrid-row-index'));
		}
		function editrow(target){
			$('#tt').datagrid('beginEdit', getRowIndex(target));
		}
		function deleterow(target){
			$.messager.confirm('Confirm','Are you sure?',function(r){
				if (r){
					$('#tt').datagrid('deleteRow', getRowIndex(target));
				}
			});
		}
		function saverow(target){
			$('#tt').datagrid('endEdit', getRowIndex(target));
		}
		function cancelrow(target){
			$('#tt').datagrid('cancelEdit', getRowIndex(target));
		}
		function insert(){
			var row = $('#tt').datagrid('getSelected');
			if (row){
				var index = $('#tt').datagrid('getRowIndex', row);
			} else {
				index = 0;
			}
			$('#tt').datagrid('insertRow', {
				index: index,
				row:{
					status:'P'
				}
			});
			$('#tt').datagrid('selectRow',index);
			$('#tt').datagrid('beginEdit',index);
		}
		
		function del(){
			var row = $('#tt').datagrid('getSelected');
			if(row){
				var index = $('#tt').datagrid('getRowIndex', row);
				$.messager.confirm('Confirm','Are you sure?',function(r){
					if (r){
						$('#tt').datagrid('deleteRow', index);
					}
				});
			}else{
				alert("请选择要删除的数据行！");
			}
			
		}
		function save(){
			var data = $('#tt').datagrid('getData');
			alert(JSON.stringify(data));
		}
	</script>
	
</body>
</html>