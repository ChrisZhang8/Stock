<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="jquery,ui,easy,easyui,web">
    <meta name="description" content="easyui helps you build your web pages easily!">
    <title>销售订单-管理</title>
    <link rel="stylesheet" type="text/css" href="../css/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="../css/themes/icon.css">
    <!-- <link rel="stylesheet" type="text/css" href="http://www.jeasyui.com/easyui/demo/demo.css"> -->
    
    <script type="text/javascript" src="../js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../js/datagrid-detailview.js"></script>
    
    
    <script>
	    
    	var root = "<%=request.getContextPath() %>";
		$(function(){
			$('#saleOrders').datagrid({
				view: detailview,
				detailFormatter:function(index,row){
                    return '<div style="padding:2px"><table class="ddv"></table></div>';
                },
                onExpandRow: function(index,row){
                    var ddv = $(this).datagrid('getRowDetail',index).find('table.ddv');
                    ddv.datagrid({
                        url:root+'/com/zxj/dbm/LogicServlet?method=viewPaginatorOrderDetail',
                        queryParams: {ORDER_NO:row.ORDER_NO},
                        fitColumns:true,
                        rownumbers:true,
                        pagination:true,
                        singleSelect:false,
                        loadMsg:'',
                        height:'auto',
                        columns:[[
                        	{field:"ck",checkbox:"true"},
                        	{field:'PRODUCT',title:'产品型号', align:'center',width:'10%'},
        					{field:'BRAND',title:'品牌',align:'center',width:'10%'},
        					{field:'BATCH_NO',title:'批号',align:'center',width:'10%'},
        					{field:'ORDER_QTY',title:'订单量',align:'center',width:'5%'},
        					{field:'STOCK_QTY',title:'库存量',align:'center',width:'5%'},
        					{field:'OUT_QTY',title:'已出货量',align:'center',width:'5%'},
        					{field:'QTY',title:'出货量',align:'center',editor:'numberbox',width:'5%'},
        					{field:'PRICE',title:'未税单价',align:'center',width:'5%'},
        					{field:'TAX_PRICE',title:'含税单价',align:'center',width:'5%'},
        					{field:'TOTAL_AMOUNT',title:'总金额',align:'center',width:'5%',type:'numberbox',options:{precision:2}},
        					{field:'STATUS',title:'状态',align:'center',width:'10%'},
        					{field:'REMARK',title:'备注',align:'center',width:'20%',editor:'text'}
        					
                        ]],
                        
                        onDblClickRow:function(index,row){
        					//if(row.ORDER_QTY>row.OUT_QTY){
        						$(this).datagrid('beginEdit', index);
        					//}
        				},
        				onClickRow:function(index,row){
        					$(this).datagrid('endEdit', index);
        				},
                        
                        /* onDblClickCell: function(index,field,value){
                       		$(this).datagrid('beginEdit', index);
                       		var ed = $(this).datagrid('getEditor', {index:index,field:field});
                       		$(ed.target).focus();
                       	},
                        
 						
        				onClickCell:function(index,field,value){
        					$(this).datagrid('endEdit', index);
        				}, */
        				
                        
                        onResize:function(){
                            $('#saleOrders').datagrid('fixDetailRowHeight',index);
                        },
                        onLoadSuccess:function(data){
                            setTimeout(function(){
                                $('#saleOrders').datagrid('fixDetailRowHeight',index);
                            },0);
                        }
                    });
                    var pager =ddv.datagrid('getPager'); 
                    
                    pager.pagination({
                        showPageList:true,
                        buttons:[{
                            iconCls:'icon-cut',
                            handler:function(){
                            	deliverGoods(ddv,row);
                            }
                        }
                       ]
                     });
                    
                    
                    $('#saleOrders').datagrid('fixDetailRowHeight',index);
                },
				rownumbers:true,
				pagination:true,
				url:root+'/com/zxj/dbm/LogicServlet?method=viewPaginatorOrders',
				/* queryParams: {
					name: 'easyui',
					subject: 'datagrid'
				} */
				columns:[[
					{field:'ORDER_NO',title:'销售订单号',align:'center',width:'10%'},
					{field:'ORDER_DATE',title:'订单日期',align:'center',width:'10%'},
					{field:'CUSTOMER',title:'客户',align:'center',width:'10%'},
					{field:'CURRENCY',title:'货币',align:'center',width:'5%'},
					{field:'AMOUNT',title:'未税金额',align:'center',width:'5%'},
					{field:'TAX_RATE',title:'税额',align:'center',width:'5%'},
					{field:'TAX_POINT',title:'税点',align:'center',width:'5%'},
					{field:'TAX_AMOUNT',title:'含税金额',align:'center',width:'5%'},
					{field:'DEPOSIT',title:'预收【定金】',align:'center',width:'5%'},
					{field:'OPER_USER',title:'业务员',align:'center',width:'5%'},
					{field:'PAYMENT_MODE',title:'付款方式',align:'center',width:'10%'},
					{field:'REMARK',title:'备注',align:'center',width:'15%'},
					{field:'STATUS',title:'状态',align:'center',width:'5%'},
					{field:'ACTION',title:'操作',width:80,align:'center',width:'5%',
		                formatter:function(value,row,index){
	                        var s = '<a href="'+root+'/com/zxj/main/editSaleOrder.jsp?ORDER_NO='+row.ORDER_NO+'" class="easyui-linkbutton" onclick="deliverGoods(this)">修改</a> ';
	                        return s;
		                    
		                }
		            }
					
				]]
				
			});
			
		});
		
		
		function query(){
			
			$('#saleOrders').datagrid('load',{
				ORDER_NO:$('#ORDER_NO').val()
			});
		}
		//出货
		function deliverGoods(ddv,row){
			var rows = ddv.datagrid('getChecked');
			if(rows.length==0){
				$.messager.alert('error','请选择出货型号','error');
				return;
			}
			
			$.messager.confirm('Confirm','确定要出货吗?',function(r){
				if (r){
					//alert(JSON.stringify(rows));
		        	var param = new Object();
		        	param.ORDER_NO = row.ORDER_NO;
		        	param.PRODUCTS = JSON.stringify(rows);
		        	$.ajax({
		        		 type:"post",
		       		     url:root+'/com/zxj/dbm/LogicServlet?method=orderShipment',
		       		     data:param,
		       		     async:false,
		       		     success:function(data) {
		       		    	 //异常
		       		    	 if(data.status=='N'){
		       		    		$.messager.alert('error',data.message,'error');
		       		    	 }else{
		       		    		$.messager.alert('info',data.result,'info');
		          		    	 ddv.datagrid('reload');
		       		    	 }
		       		    	
		       		     },
		       		     error:function(data){
		       		       //alert(data.message);
		       		       $.messager.alert('error',data.responseText);
		       		  }      
		        	});
				}
			});
			
        	

			
		}
		
		function freshTable(){
			$('#saleOrders').datagrid('reload');
			
		}
		
	</script>
    
</head>
<body>
	<h2 align="center">销售订单-管理</h2>
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
				<tr>
					<td><input class="easyui-textbox" id="OPER_USER" name="OPER_USER" style="width:100%" data-options="label:'业务员:'"></td>
					<td><input class="easyui-combobox" name="INVOICE_TYPE" style="width:100%" data-options="label:'开票类型：',valueField: 'value',textField: 'label',
																	data: [{
																		label: '不含税',
																		value: '0'
																	},{
																		label: '增值税',
																		value: '1'
																	},{
																		label: '普通税',
																		value: '2'
																	}]" />
					</td>
					<td><input class="easyui-combobox" name="CURRENCY" style="width:100%" data-options="label:'币种：',valueField: 'value',textField: 'label',
																	data: [{
																		label: 'RMB',
																		value: '0'
																	},{
																		label: 'dollar',
																		value: '1'
																	}]" />
					
					</td>
				</tr>
			
			</table>
		
		
		
		</form>
	
	</div>

	<div style="margin:10px 0">
	</div>
	<table id="saleOrders"></table>
	
	
	
	
	
</body>
</html>