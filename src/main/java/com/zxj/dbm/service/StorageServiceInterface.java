package com.zxj.dbm.service;

import java.util.List;
import java.util.Map;

public interface StorageServiceInterface {
	
	
	/**
	 * 入库
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public void storage(Map param)throws Exception;
	
	/**
	 * 库存消耗
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<Map> consume(Map param)throws Exception;
	
	
	public List<Map> viewInventory(Map param)throws Exception;
	
	public Map viewPaginatorInventory(Map param)throws Exception;
	
	
	
}
