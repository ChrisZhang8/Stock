<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="jquery,ui,easy,easyui,web">
    <meta name="description" content="easyui helps you build your web pages easily!">
    <title>采购订单-报表</title>
    <link rel="stylesheet" type="text/css" href="../css/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="../css/themes/icon.css">
    <!-- <link rel="stylesheet" type="text/css" href="http://www.jeasyui.com/easyui/demo/demo.css"> -->
    
    <script type="text/javascript" src="../js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/datagrid-detailview.js"></script>
    
    
    <script>
	    
    	var root = "<%=request.getContextPath() %>";
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
                        	{field:'PRODUCT',title:'产品型号',width:'20%'},
        					{field:'BRAND',title:'品牌',width:'10%'},
        					{field:'VENDOR',title:'供应商',width:'20%'},
        					{field:'SHAPE',title:'封装',width:'10%'},
        					{field:'BATCH_NO',title:'批号',width:'20%'},
        					{field:'ORDER_QTY',title:'订货量',align:'right',width:'10%'},
        					{field:'RECEIVE_QTY',title:'到货量',align:'right',width:'10%'},
        					{field:'PRICE',title:'单价',align:'right',width:'10%'},
        					{field:'TOTAL_AMOUNT',title:'总金额',align:'right',width:'10%'},
        					{field:'REMARK',title:'备注',width:'20%',editor:'text'}
        					
                        ]],
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
                            iconCls:'icon-cut',
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
					{field:'STATUS',title:'状态',align:'center',width:'5%'}

					
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
        	param.PRODUCTS = JSON.stringify(rows);
        	$.ajax({
        		 type:"post",
       		     url:root+'/com/zxj/dbm/StorageServlet?method=storage',
       		     data:param,
       		     async:false,
       		     success:function(data) {
       		    	 ddv.datagrid('reload');
       		     },
       		     error:function(data){
       		       alert(data.responseText);
       		  }      
        	});

			
		}
		
		function freshTable(){
			$('#buyOrders').datagrid('reload');
			
		}
		
	</script>
    
</head>
<body>
	<h2 align="center">采购订单-报表</h2>
	<div>
		
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-search'" onclick="query()">查询</a>
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-reload'" style="width:80px" onclick="freshTable()">刷新</a>
	</div>
	
	<div class="easyui-panel" >
		<form id="saleorderform" method="post">
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