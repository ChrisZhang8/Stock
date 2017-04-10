package com.zxj.dbm.service.impl;

import com.zxj.dbm.dto.*;
import com.zxj.dbm.service.LogicServiceInterface;
import com.zxj.dbm.service.ShopOrderServiceInterface;
import com.zxj.dbm.service.StorageServiceInterface;
import net.sf.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShopOrderService implements ShopOrderServiceInterface {
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private LogicServiceInterface logicService;
	
	private StorageServiceInterface storageService;
	
	
	public void setStorageService(StorageServiceInterface storageService) {
		this.storageService = storageService;
	}

	public void setLogicService(LogicServiceInterface logicService) {
		this.logicService = logicService;
	}

	@Override
	public void addSaleOrder(Map order) throws Exception {
		try {
			//add sale order
			SqlUpdateRequest req = new SqlUpdateRequest();
			order.put("SID", UUID.randomUUID().toString());
			order.put("DELIVERY_DATE", df.format(new Date()));
			order.put("CREATE_DATE_TIME", df.format(new Date()));
			order.put("STATUS", "P");
			if(StringUtils.isEmpty((String)order.get("DEPOSIT"))){
				order.put("DEPOSIT", 0.00);
			}
			
			String sql = "INSERT INTO sale_order(SID,ORDER_NO,CUSTOMER,ORDER_DATE,INVOICE_TYPE,CURRENCY,"
					+ "DEPOSIT,PAYMENT_MODE,STATUS,DELIVERY_DATE,"
					+ "CREATE_USER,CREATE_DATE_TIME,MODIFY_USER,MODIFY_DATE_TIME)                                                          "+
					"VALUES (:SID ,:SALE_ORDER_NO ,:CUSTOMER ,:ORDER_DATE ,:INVOICE_TYPE ,:CURRENCY,:DEPOSIT,:PAYMENT_MODE,"+
					":STATUS ,:DELIVERY_DATE ,:CREATE_USER ,:CREATE_DATE_TIME,:CREATE_USER,:CREATE_DATE_TIME      "+
					"	)                                                                              ";
			req.setSql(sql);
			req.setParam(order);
			
			logicService.execute(req);
			
			//add order product
			insertOrderProducts(order);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		
	}
	
	private void insertOrderProducts(Map param)throws Exception{
		//新增products
		SqlBatchUpdateRequest sr = new SqlBatchUpdateRequest();
		String ptSql = "insert INTO SALE_ORDER_PRODUCT                                                  "+
				"(SID,SALE_ORDER_NO,PRODUCT,BRAND,BATCH_NO,ORDER_QTY,                        "+
				"OUT_QTY,PRICE,TAX_PRICE,TOTAL_AMOUNT,STATUS,REMARK,CREATE_DATE_TIME,CREATE_USER)         "+
				"values(:SID,:ORDER_NO,:PRODUCT,:BRAND,:BATCH_NO,:ORDER_QTY,:OUT_QTY,   "+
				":PRICE,:TAX_PRICE,:TOTAL_AMOUNT,:STATUS,:REMARK,:CREATE_DATE_TIME,:CREATE_USER)           ";
		sr.setSql(ptSql);
		JSONArray array = JSONArray.fromObject(param.get("pdata"));
		List<Map> datas = array.subList(0, array.size());
		for(Map m :datas){
			m.put("SID", UUID.randomUUID().toString());
			m.put("CREATE_DATE_TIME",df.format(new Date()));
			m.put("CREATE_USER", param.get("CREATE_USER"));
			m.put("ORDER_NO", param.get("SALE_ORDER_NO"));
			m.put("STATUS", "P");
			if(StringUtils.isEmpty((String)m.get("OUT_QTY"))){
				m.put("OUT_QTY",0.00);
			}
		}
		sr.setParam(datas);
		logicService.execute(sr);
				
	}

	@Override
	public void updateSaleOrder(Map order) throws Exception {
		//更新order
		SqlUpdateRequest req = new SqlUpdateRequest();
		order.put("UPDATE_DATE_TIME", df.format(new Date()));
		req.setParam(order);
		String sql = "update sale_order set CUSTOMER=:CUSTOMER,ORDER_DATE=:ORDER_DATE,"
				+ "INVOICE_TYPE=:INVOICE_TYPE,CURRENCY=:CURRENCY,DEPOSIT=:DEPOSIT,PAYMENT_MODE=:PAYMENT_MODE,DELIVERY_DATE=:DELIVERY_DATE,"
				+ "CREATE_USER=:CREATE_USER,UPDATE_DATE_TIME=:UPDATE_DATE_TIME"
				+ " where ORDER_NO=:ORDER_NO";
		req.setSql(sql);
		logicService.execute(req);
		//删除products
		SqlUpdateRequest delpt = new SqlUpdateRequest();
		String delProducts = "delete from SALE_ORDER_PRODUCT where SALE_ORDER_NO=:ORDER_NO";
		delpt.setSql(delProducts);
		delpt.setParam(order);
		logicService.execute(delpt);
		
		//新增products
		insertOrderProducts(order);
		
	}

	@Override
	public void deleteSaleOrder() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Map> viewSaleOrder(Map param) throws Exception {
		
		SqlQueryRequest request = new SqlQueryRequest();
		request.setParam(param);
		String sql = "SELECT * FROM sale_order where ORDER_NO=:ORDER_NO";
		request.setSql(sql);
		LogicQueryResponse resp = logicService.query(request);
		return resp.getRecords();
	}
	
	@Override
	public Map viewSaleOrderByNo(Map param) throws Exception {
		
		SqlQueryRequest request = new SqlQueryRequest();
		request.setParam(param);
		String sql = "SELECT * FROM sale_order where ORDER_NO=:ORDER_NO";
		request.setSql(sql);
		LogicQueryResponse resp = logicService.query(request);
		if(resp.getTotalRecords()==0){
			return new HashMap();
		}
		return resp.getRecords().get(0);
	}
	
	@Override
	public Map viewPaginatorOrders(Map param)throws Exception{
		SqlPaginatorRequest req = new SqlPaginatorRequest();
		Integer page = Integer.valueOf((String)param.get("page"));
		Integer pageSize = Integer.valueOf((String)param.get("rows"));
		String startIndex = String.valueOf((page-1)*pageSize);
		
		req.setPageSize(pageSize.toString());
		req.setStartIndex(startIndex);
		req.setParam(param);
		String sql = "SELECT * FROM sale_order where (ORDER_NO = :ORDER_NO OR :ORDER_NO ='')                       "+
				"AND (CUSTOMER=:CUSTOMER OR :CUSTOMER='') AND (CREATE_USER=:CREATE_USER OR :CREATE_USER='')   "+
				"ORDER BY MODIFY_DATE_TIME DESC                                                                ";
		req.setSql(sql);
		LogicQueryResponse resp = logicService.query(req);
		Map result = new HashMap();
		result.put("rows", resp.getRecords());
		result.put("total", resp.getTotalRecords());
		return result;
		
	}

	@Override
	public Map viewPaginatorOrderDetail(Map param) throws Exception {
		SqlQueryRequest req = new SqlQueryRequest();
		req.setParam(param);
		String fileUrl = this.getClass().getResource("/sql/saleOrderDetail.sql").getPath();
		
		String sql = FileUtils.readFileToString(new File(fileUrl));
		req.setSql(sql);
		LogicQueryResponse resp = logicService.query(req);
		Map result = new HashMap();
		result.put("rows", resp.getRecords());
		result.put("total", resp.getTotalRecords());
		return result;
	}

	@Override
	public void orderShipment(Map param) throws Exception {
		
		try {
			List<Map> products = (List<Map>)param.get("PRODUCTS");
			
			if(products!=null && products.size()>0){
				for(Map m:products){
					m.put("ORDER_NO", param.get("ORDER_NO"));
					//订单量 = 已出货量+出货量 status C 完成
					Integer orderQty = (Integer)m.get("ORDER_QTY"); 
					Integer outQty = (Integer)m.get("OUT_QTY");
					Integer qty = Integer.valueOf((String)m.get("QTY"));
					if(qty <= 0)throw new Exception("出货量必须大于0！");
					if(orderQty== outQty+qty){
						m.put("STATUS", "C");
					}else{
						m.put("STATUS", "H");
					}
				}
				SqlBatchUpdateRequest req = new SqlBatchUpdateRequest();
				req.setParam(products);
				String sql = "update SALE_ORDER_PRODUCT set status=:STATUS,OUT_QTY=OUT_QTY+:QTY where sale_order_no=:ORDER_NO AND PRODUCT = :PRODUCT";
				req.setSql(sql);
				logicService.execute(req);
			}
			
			//扣减库存
			storageService.consume(param);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		
	}

}
