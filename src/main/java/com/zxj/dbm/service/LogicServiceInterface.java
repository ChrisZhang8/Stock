package com.zxj.dbm.service;


import com.zxj.dbm.dto.LogicQueryResponse;
import com.zxj.dbm.dto.SqlBatchUpdateRequest;
import com.zxj.dbm.dto.SqlPaginatorRequest;
import com.zxj.dbm.dto.SqlQueryRequest;
import com.zxj.dbm.dto.SqlUpdateRequest;

public interface LogicServiceInterface {
	
	public LogicQueryResponse query(SqlQueryRequest request)throws Exception;
	public LogicQueryResponse query(SqlPaginatorRequest request)throws Exception;
	
	public int execute(SqlUpdateRequest request)throws Exception;
	public void execute(SqlBatchUpdateRequest request)throws Exception;

}
