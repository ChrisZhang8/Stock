<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>进销存导航页面</title>
</head>
<body>


<a href="<%=request.getContextPath()%>/com/zxj/main/buyOrderManage.jsp" target="_blank">采购订单管理</a><br>

<a href="<%=request.getContextPath()%>/com/zxj/main/saleOrderManage.jsp" target="_blank">销售订单管理</a><br>


<a href="<%=request.getContextPath()%>/com/zxj/main/viewSaleOrder.jsp" target="_blank">销售订单报表</a><br>

<a href="<%=request.getContextPath()%>/com/zxj/main/viewBuyOrder.jsp" target="_blank">采购订单报表</a><br>



</body>
</html>