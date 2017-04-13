package com.zxj.dbm.service.impl;

import com.zxj.dbm.dto.*;
import com.zxj.dbm.service.BuyOrderServiceInterface;
import com.zxj.dbm.service.LogicServiceInterface;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyOrderService implements BuyOrderServiceInterface {
	
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger LOGGER = Logger.getLogger(BuyOrderService.class);
	
	private LogicServiceInterface logicService;

	public void setLogicService(LogicServiceInterface logicService) {
		this.logicService = logicService;
	}

	@Override
	public List<Map> viewBuyOrder(Map param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map viewBuyOrderByNo(Map param) throws Exception {
		SqlQueryRequest request = new SqlQueryRequest();
		request.setParam(param);
		String sql = "SELECT * FROM buy_order where ORDER_NO=:ORDER_NO";
		request.setSql(sql);
		LogicQueryResponse resp = logicService.query(request);
		if(resp.getTotalRecords()==0){
			return new HashMap();
		}
		return resp.getRecords().get(0);
	}

	@Override
	public Map viewPaginatorOrders(Map param) throws Exception {
		try {
			SqlPaginatorRequest req = new SqlPaginatorRequest();
			Integer page = Integer.valueOf((String)param.get("page"));
			Integer pageSize = Integer.valueOf((String)param.get("rows"));
			String startIndex = String.valueOf((page-1)*pageSize);
			
			req.setPageSize(pageSize.toString());
			req.setStartIndex(startIndex);
			req.setParam(param);
			String sql = "SELECT * FROM buy_order where (ORDER_NO = :ORDER_NO OR :ORDER_NO ='')";
			req.setSql(sql);
			LogicQueryResponse resp = logicService.query(req);
			Map result = new HashMap();
			result.put("rows", resp.getRecords());
			result.put("total", resp.getTotalRecords());
			return result;
		} catch (Exception e) {
//			e.printStackTrace();
			LOGGER.error("查询采购订单失败！",e);
			throw e;
		}
		
	}

	@Override
	public Map viewPaginatorOrderDetail(Map param) throws Exception {
		SqlQueryRequest req = new SqlQueryRequest();
		req.setParam(param);
		String sql = "select * from BUY_ORDER_PRODUCT where BUY_order_no=:ORDER_NO";
		req.setSql(sql);
		LogicQueryResponse resp = logicService.query(req);
		Map result = new HashMap();
		result.put("rows", resp.getRecords());
		result.put("total", resp.getTotalRecords());
		return result;
	}

	@Override
	public void addBuyOrder(Map order) throws Exception {
		try{
			//add sale order
			SqlUpdateRequest req = new SqlUpdateRequest();
//		order.put("SID", UUID.randomUUID().toString());
			order.put("DELIVERY_DATE", df.format(new Date()));
			order.put("CREATE_DATE_TIME", df.format(new Date()));
			order.put("STATUS", "P");
			String sql = "INSERT INTO buy_order(ORDER_NO,ORDER_DATE,DELIVERY_DATE,STATUS,"
					+ "CREATE_USER,CREATE_DATE_TIME,MODIFY_USER,MODIFY_DATE_TIME)              "+
					"VALUES(:ORDER_NO ,:ORDER_DATE ,:DELIVERY_DATE ,:STATUS ,:CREATE_USER,:CREATE_DATE_TIME,:CREATE_USER,"+
					":CREATE_DATE_TIME) ";
			req.setSql(sql);
			req.setParam(order);

			logicService.execute(req);

			//add order product
			insertOrderProducts(order);
		}catch (Exception e){
			LOGGER.error("新增采购订单失败！",e);
			throw e;
		}


	}
	
	private void insertOrderProducts(Map param)throws Exception{
		try{
            SqlBatchUpdateRequest sr = new SqlBatchUpdateRequest();
            String ptSql = "insert INTO BUY_ORDER_PRODUCT                                         "+
                    "(BUY_ORDER_NO,PRODUCT,BATCH_NO,VENDOR,ORDER_QTY,                        "+
                    "RECEIVE_QTY,TAX_POINT,PRICE,TOTAL_AMOUNT,STATUS,REMARK,CREATE_USER,CREATE_DATE_TIME)         "+
                    "values(:ORDER_NO,:PRODUCT,:BATCH_NO,:VENDOR,:ORDER_QTY,:RECEIVE_QTY,"
                    + ":TAX_POINT,:PRICE,:TOTAL_AMOUNT,:STATUS,:REMARK,:CREATE_USER,:CREATE_DATE_TIME)           ";
            sr.setSql(ptSql);
            JSONArray array = JSONArray.fromObject(param.get("pdata"));
            if(array !=null && array.size()>0){
                List<Map> datas = array.subList(0, array.size());
                for(Map m :datas){

                    m.put("CREATE_DATE_TIME",df.format(new Date()));
                    m.put("CREATE_USER", param.get("CREATE_USER"));
                    m.put("ORDER_NO", param.get("ORDER_NO"));
                    m.put("STATUS", "P");
                    if(StringUtils.isEmpty(m.get("RECEIVE_QTY").toString())){
                        m.put("RECEIVE_QTY",0);
                    }
                    if(m.get("TAX_POINT")==null || StringUtils.isEmpty(m.get("TAX_POINT").toString())){
                        m.put("TAX_POINT",0);
                    }

                }
                sr.setParam(datas);
                logicService.execute(sr);
            }
        }catch(Exception e){
		    LOGGER.error("保存采购清单详细信息失败！",e);
		    throw e;
        }

		
	}

	@Override
	public void updateBuyOrder(Map order) throws Exception {
		try {
			//更新order
			SqlUpdateRequest req = new SqlUpdateRequest();
			order.put("MODIFY_DATE_TIME", df.format(new Date()));
			req.setParam(order);
			String sql = "update buy_order set ORDER_DATE=:ORDER_DATE,"
					+ "DELIVERY_DATE=:DELIVERY_DATE,"
					+ "MODIFY_USER=:CREATE_USER,MODIFY_DATE_TIME=:MODIFY_DATE_TIME"
					+ " where ORDER_NO=:ORDER_NO";
			req.setSql(sql);
			logicService.execute(req);
			//删除products
			SqlUpdateRequest delpt = new SqlUpdateRequest();
			String delProducts = "delete from buy_ORDER_PRODUCT where buy_ORDER_NO=:ORDER_NO";
			delpt.setSql(delProducts);
			delpt.setParam(order);
			logicService.execute(delpt);
			
			//新增products
			insertOrderProducts(order);
		} catch (Exception e) {
//			e.printStackTrace();
			LOGGER.error("修改采购订单失败！",e);
			throw e;
		}
		

	}

}
