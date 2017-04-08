package com.zxj.dbm.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zxj.dbm.dto.LogicQueryResponse;
import com.zxj.dbm.dto.SqlBatchUpdateRequest;
import com.zxj.dbm.dto.SqlQueryRequest;
import com.zxj.dbm.service.LogicServiceInterface;
import com.zxj.dbm.service.StorageServiceInterface;

public class StorageService implements StorageServiceInterface {

	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private LogicServiceInterface logicService;
	
	
	
	public void setLogicService(LogicServiceInterface logicService) {
		this.logicService = logicService;
	}

	@Override
	public void storage(Map param) throws Exception {
		try {
			//每次入库插入新的记录 生成新的inventoryID
			
			SqlBatchUpdateRequest req = new SqlBatchUpdateRequest();
			List<Map> pm = (List<Map>)param.get("PRODUCTS");
			for(Map m : pm){
				m.put("CREATE_USER", param.get("CREATE_USER"));
				m.put("CREATE_DATE_TIME", sf.format(new Date()));
				m.put("ORDER_NO", param.get("ORDER_NO"));
				m.put("INVENTORY_ID", param.get("ORDER_NO")+"-"+ String.valueOf(new Date().getTime()));
			}
			req.setParam(pm);
			String sql = "INSERT INTO INVENTORY (                                  "+
					"	SID,                                                    "+
					"	INVENTORY_ID,                                           "+
					"	BUY_ORDER_NO,                                           "+
					"	PRODUCT,                                                "+
					"	VENDOR,                                                 "+
					"	STORE_QTY,												"+
					"	QTY,                                                    "+
					"	BATCH_NO,                                               "+
					"	PRICE,                                                  "+
					"	CREATE_USER,                                            "+
					"	CREATE_DATE_TIME,                                       "+
					"	MODIFY_USER,                                            "+
					"	MODIFY_DATE_TIME                                        "+
					")                                                        "+
					"VALUES                                                   "+
					"	(                                                       "+
					"		UUID(),:INVENTORY_ID ,:ORDER_NO ,:PRODUCT,           "+
					"		:VENDOR,:RECEIVE_QTY,:RECEIVE_QTY,:BATCH_NO,          "+
					"		:PRICE,:CREATE_USER,:CREATE_DATE_TIME,                "+
					"		:CREATE_USER,:CREATE_DATE_TIME                        "+
					"	)                                                       ";
			req.setSql(sql);
			
			logicService.execute(req);
			
			//更新buyorderproducts信息
			SqlBatchUpdateRequest request = new SqlBatchUpdateRequest();
			List<Map> lm = (List<Map>)param.get("PRODUCTS");
			request.setParam(lm);
			String updatePrdSql = "update buy_order_product set RECEIVE_QTY=:RECEIVE_QTY,REMARK=:REMARK where SID=:SID ";
			request.setSql(updatePrdSql);
			logicService.execute(request);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public List<Map> consume(Map param) throws Exception {
		try {
			List<Map> products = (List<Map>)param.get("PRODUCTS");
			for (Map map : products) {
				//1. 查看库存是否足够
				if(!isEnough(map))throw new Exception("产品型号【"+map.get("PRODUCT")+"】库存不足!");
				
				//2. 遍历扣减库存
				consumeInv(map);
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	private boolean isEnough(Map param)throws Exception{
		SqlQueryRequest req = new SqlQueryRequest();
		String sql = "select * from (select sum(qty)PROD_QTY FROM INVENTORY where PRODUCT=:PRODUCT  )t where t.PROD_QTY >= :QTY";
		req.setSql(sql);
		req.setParam(param);
		LogicQueryResponse resp = logicService.query(req);
		if(resp.getTotalRecords()>0)return true;
		return false;
	}
	
	private void consumeInv(Map param)throws Exception{
		SqlQueryRequest req = new SqlQueryRequest();
		String sql = "select * FROM INVENTORY where PRODUCT=:PRODUCT ";
		req.setSql(sql);
		req.setParam(param);
		LogicQueryResponse resp = logicService.query(req);
		BigDecimal outQty = new BigDecimal((String)param.get("QTY"));
		List<Map> rs = new ArrayList<Map>();
		for(Map m : resp.getRecords()){
			BigDecimal qty = (BigDecimal)m.get("QTY");
			rs.add(m);
			if((outQty.subtract(qty)).compareTo(BigDecimal.ZERO)==-1){
				m.put("QTY", outQty);
				break;
			}else{
				outQty = outQty.subtract(qty);
			}
		}
		//批量更新库存
		SqlBatchUpdateRequest sr = new SqlBatchUpdateRequest();
		sr.setParam(rs);
		String upSql = "UPDATE INVENTORY                                              "+
				"SET QTY = QTY -:QTY,                                     "+
				" MODIFY_DATE_TIME = date_format(now(), '%Y-%m-%d %h:%i:%s')   "+
				"WHERE                                                         "+
				"	INVENTORY_ID =:INVENTORY_ID                                 ";
		sr.setSql(upSql);
		logicService.execute(sr);
	}
	

	@Override
	public List<Map> viewInventory(Map param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map viewPaginatorInventory(Map param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
