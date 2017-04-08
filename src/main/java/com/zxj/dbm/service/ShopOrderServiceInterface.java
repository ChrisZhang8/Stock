package com.zxj.dbm.service;

import java.util.List;
import java.util.Map;

public interface ShopOrderServiceInterface {
	
	public void addSaleOrder(Map order)throws Exception;

	public void updateSaleOrder(Map order)throws Exception;
	
	public void orderShipment(Map m)throws Exception;
	
	public void deleteSaleOrder()throws Exception;
	
	public List<Map> viewSaleOrder(Map param)throws Exception;
	
	public Map viewSaleOrderByNo(Map param)throws Exception;
	
	public Map viewPaginatorOrders(Map param)throws Exception;
	
	public Map viewPaginatorOrderDetail(Map param)throws Exception;
	
	
	
}
