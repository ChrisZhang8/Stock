<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="jquery,ui,easy,easyui,web">
    <meta name="description" content="easyui helps you build your web pages easily!">
    <title>采购订单-管理</title>
    <link rel="stylesheet" type="text/css" href="../css/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="../css/themes/icon.css">
    <!-- <link rel="stylesheet" type="text/css" href="http://www.jeasyui.com/easyui/demo/demo.css"> -->
    
    <script type="text/javascript" src="../js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/datagrid-detailview.js"></script>
    

    <script>    	var root = "<%=request.getContextPath() %>";	</script>

	<script src="buyOrderManage.js"></script>
    
</head>
<body>
	<h2 align="center">采购订单-管理</h2>
	<div>
		
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="query()">查询</a>
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-reload'" style="width:80px" onclick="freshTable()">刷新</a>
	<a href="<%=request.getContextPath()%>/com/zxj/main/addBuyOrder.jsp" target="_blank">新建采购订单</a>
	</div>
	
	<div class="easyui-panel" >
		<form id="buyorderform" method="post">
			<table>
				<tr>
					<td><input class="easyui-textbox" id="CUSTOMER" name="CUSTOMER" style="width:100%" data-options="label:'客户名称:'"></td>
					<td><input class="easyui-textbox" id="ORDER_NO" name="ORDER_NO" style="width:100%" data-options="label:'销售订单号:'"></td>
					<td><input class="easyui-datetimebox" id="ORDER_DATE" name="ORDER_DATE" label="订单日期:" labelPosition="left" style="width:100%;"></td>
				</tr>

			</table>

		</form>
	
	</div>

	<div style="margin:10px 0">
	</div>
	<table id="buyOrders"></table>
	

</body>
</html>