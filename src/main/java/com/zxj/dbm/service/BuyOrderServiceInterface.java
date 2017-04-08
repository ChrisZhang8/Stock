package com.zxj.dbm.service;

import java.util.List;
import java.util.Map;

public interface BuyOrderServiceInterface {
	
	
	
	public List<Map> viewBuyOrder(Map param)throws Exception;
	
	public Map viewBuyOrderByNo(Map param)throws Exception;
	
	public Map viewPaginatorOrders(Map param)throws Exception;
	
	public Map viewPaginatorOrderDetail(Map param)throws Exception;
	
	
	public void addBuyOrder(Map order)throws Exception;
	
	public void updateBuyOrder(Map order)throws Exception;
	
}
