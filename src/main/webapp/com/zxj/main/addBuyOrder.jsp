<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!doctype html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="jquery,ui,easy,easyui,web">
    <meta name="description" content="easyui helps you build your web pages easily!">
    <title>采购订单-新增</title>
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
				row:{'PRODUCT':'','BRAND':'','VENDOR':'','SHAPE':'','BATCH_NO':'','ORDER_QTY':0,'RECEIVE_QTY':0,'PRICE':0.00,'TOTAL_AMOUNT':'0.00'}
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
			//var fmdata = $("#buyorderform").serialize();
			
			$('#buyorderform').form('submit', {
			    url:root+'/com/zxj/dbm/BuyOrderServlet?method=addBuyOrder',
			    onSubmit: function(param){
			    	param.pdata=JSON.stringify(data.rows);
			    	//alert(JSON.stringify(param)); 
			    },
			    success:function(data){
			        var dt = JSON.parse(data);
			        if (dt.status == 'N'){
                        $.messager.alert('error',dt.message);
					}else {
                        $.messager.alert('info',dt.result);

                        $('#buyorderform').form('clear');
                        $('#buyorderform').form('load',{
                            ORDER_NO:uuid(8, 16)
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
			
			$('#buyorderform').form('load',{
				ORDER_NO:uuid(8, 16)
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
					{field:'BRAND',title:'品牌',width:'10%',editor:'text'},
					{field:'BATCH_NO',title:'批号',width:'10%',editor:'text'},
					{field:'ORDER_QTY',title:'订货量',align:'right',width:'10%',editor:{type:'numberbox'}},
					{field:'VENDOR',title:'供应商',width:'10%',editor:'text'},
					{field:'SHAPE',title:'封装',width:'10%',editor:'text'},
					{field:'RECEIVE_QTY',title:'到货量',align:'right',width:'8%',editor:{type:'numberbox',value:0}},
                    {field:'TAX_POINT',title:'税点',width:'8%',
                        formatter:function(value,row){
                            return row.IS_TAX_NAME || value;
                        },
                        editor:{
                            type:'combobox',
                            options:{
                                valueField:'TAX_POINT',
                                textField:'TAX_POINT_NAME',
                                data:[{"TAX_POINT_NAME":"增值税-17%","TAX_POINT":"17"},
									{"TAX_POINT_NAME":"普通税-5%","TAX_POINT":"5"},
                                    {"TAX_POINT_NAME":"不含税","TAX_POINT":"0"}],
                                required:true
                            }
                        }
                    },
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
		        url:root+'/com/zxj/dbm/BuyOrderServlet?method=importExcel',
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
		
	</script>
    
</head>
<body>
	<h2 align="center">采购订单-新增</h2>
	<div>
		
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="save()">保存</a>
	<a href="javascript:void(0)" class="easyui-linkbutton" data-options="iconCls:'icon-reload'" style="width:80px" onclick="reload()">刷新</a>
	</div>
	
	<div class="easyui-panel" >
		<form id="buyorderform" method="post">
			<table>
				<tr>
					
					<td><input class="easyui-textbox" id="PUR_ORDER_NO" name="ORDER_NO"  style="width:100%" data-options="label:'采购订单号:',required:true"></td>
					<td><input class="easyui-datetimebox" name="ORDER_DATE" label="订单日期:" labelPosition="left" style="width:100%;"></td>

					<td><input class="easyui-textbox" id="CREATE_USER" name="CREATE_USER"  style="width:100%" data-options="label:'操作人:'"></td>
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