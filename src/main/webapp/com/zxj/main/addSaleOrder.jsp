<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="jquery,ui,easy,easyui,web">
    <meta name="description" content="easyui helps you build your web pages easily!">
    <title>销售订单-新增</title>
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
	</script>

	<script src="addSaleOrder.js"></script>
    
</head>
<body>
	<h2 align="center">销售订单-新增</h2>
	<div>
		
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()">保存</a>
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-reload'" style="width:80px" onclick="reload()">刷新</a>
	</div>
	
	<div class="easyui-panel" >
		<form id="saleorderform" method="post">
			<table>
				<tr>
					<td><input class="easyui-textbox" name="CUSTOMER" style="width:100%" data-options="label:'客户名称:',required:true"></td>
					<td><input class="easyui-textbox" id="SALE_ORDER_NO" name="SALE_ORDER_NO"  style="width:100%" data-options="label:'销售订单号:',required:true"></td>
					<td><input class="easyui-datetimebox" name="ORDER_DATE" label="订单日期:" labelPosition="left" style="width:100%;" required=true></td>
					<td><input class="easyui-datetimebox" name="DELIVERY_DATE" label="订单交期:" labelPosition="left" style="width:100%;" required=true></td>
				</tr>
				<tr>
					<td><input class="easyui-textbox" name="CREATE_USER" style="width:100%" data-options="label:'业务员:',required:true"></td>
					<td><input class="easyui-combobox" style="width:100%" name="TAX_POINT"  data-options="label:'开票类型：',required:true,valueField: 'value',textField: 'label',
																	data: [{
																		label: '不含税',
																		value: '0',
																		selected: true
																	},{
																		label: '增值税-17',
																		value: '17'
																	},{
																		label: '普通税',
																		value: '3'
																	}]" />
					</td>
					<td><input class="easyui-combobox" style="width:100%" name="CURRENCY" data-options="label:'币种：',required:true,valueField: 'value',textField: 'label',
																	data: [{
																		label: 'RMB',
																		value: '0',
																		selected: true
																	},{
																		label: 'dollar',
																		value: '1'
																	}]" />
					
					</td>
					<td><input class="easyui-numberbox" name="DEPOSIT" style="width:100%" data-options="label:'定金:',min:0,precision:2"></td>
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
        
         <span id="weixin_upload" class="btn btn-primary fileinput-button" style="display: inline-flex;border: groove;">  
			<span>excel导入</span>  
			<input type="file" id="fileupload" name="weixin_image" multiple>  
		</span>  
	</div>
	
	
	<table id="detail"></table>
	
	
</body>
</html>