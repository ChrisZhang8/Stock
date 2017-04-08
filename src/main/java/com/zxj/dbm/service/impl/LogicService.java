package com.zxj.dbm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zxj.dbm.dto.LogicQueryResponse;
import com.zxj.dbm.dto.SqlBatchUpdateRequest;
import com.zxj.dbm.dto.SqlPaginatorRequest;
import com.zxj.dbm.dto.SqlQueryRequest;
import com.zxj.dbm.dto.SqlUpdateRequest;
import com.zxj.dbm.service.LogicServiceInterface;
import com.zxj.dbm.util.RegUtils;
import com.zxj.dbm.util.RegUtils.FindCallback;

import net.sf.json.util.JSONUtils;

public class LogicService implements LogicServiceInterface {

	private static final String reg = "\\:\\s*[a-zA-Z\\.0-9\\_\\-\\?\\*\\/]+\\s*";
	private static final String mreg = "\\/\\*([\\S\\s]*?)\\*\\/"; 
	
	private DataSource dataSource;
	
	
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public LogicQueryResponse query(SqlQueryRequest request) throws Exception {
		LogicQueryResponse response = new LogicQueryResponse();
		if(StringUtils.isBlank(request.getSql())){
			throw new Exception("sql不能为空！");
		}
		
		Map paramMap = request.getParam();
		
		//对SQL进行解析
		Object psql = parseSql(paramMap,request.getSql()); 
		List<Map> list = new ArrayList<Map>();
		if(psql instanceof String){ 
			try{
				List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
				NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(dataSource); 
				result = npjt.queryForList((String)psql,paramMap); 
				list.addAll(result);
			}catch(Exception ex){
				throw new Exception("查询数据失败!\n>>>PARAMETER>>>\n"+request.getParam()+"\n>>>SQL>>>\n"+psql+"\n"+ExceptionUtils.getFullStackTrace(ex),ex);
			}
		}
		else if(psql instanceof List){
			list = (List<Map>) psql;
		}
		response.setRecords(list);
		response.setTotalRecords(list.size());
		return response; 	
	}

	protected Object parseSql(final Map<String, Object> queryParam,String sql) throws Exception{ 
		
		
		sql = StringUtils.trim(RegUtils.findAndReplace(mreg, sql, new FindCallback(){
				public String execute(String source) {
					return " ";
				}
			}));
		try{
			RegUtils.findAndReplace(reg, sql, new FindCallback(){
				public String execute(String source) {
					String s = StringUtils.trim(StringUtils.replace(source, ":", ""));
					if(JSONUtils.isNull(queryParam.get(s)))queryParam.put(s, "");
					return source;
				}
				
			});
			return sql;
		}catch(Exception ex){
			throw new Exception("初始化参数失败!\n>>> PARAM >>>:\n"+queryParam+sql,ex);
		}
	}
	
	@Override
	public LogicQueryResponse query(SqlPaginatorRequest request) throws Exception {
		
		if(StringUtils.isBlank(request.getSql())){
			throw new Exception("sql不能为空！");
		} 
		String pcSql = "select t.*,(select count(1) from ("+request.getSql()+")s)TOTAL_RECORDS from ("+request.getSql()+
				")t LIMIT " 
				+request.getStartIndex()+","+request.getPageSize()+"";
		
		SqlQueryRequest sqlReq = request;
		sqlReq.setParam(request.getParam());
		sqlReq.setSql(pcSql);
		LogicQueryResponse response = this.query(sqlReq);
		if(response.getTotalRecords()>0){
			response.setTotalRecords(Integer.valueOf(response.getRecords().get(0).get("TOTAL_RECORDS").toString()));
			
		}
		response.setStartIndex(Integer.valueOf(request.getStartIndex()));
		return response;
	}

	@Override
	public int execute(SqlUpdateRequest request) throws Exception {
		int row=0;
		if(StringUtils.isBlank(request.getSql())){
			throw new Exception("sql不能为空！");
		}
//		String sql = request.getSql();
		Map paramMap = request.getParam();
		String[] sqls = StringUtils.split(request.getSql(),";");
		for (int i = 0; i < sqls.length; i++) {
			String sql = (String)parseSql(request.getParam(),sqls[i]); 
			try {
				NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(dataSource);
				row = npjt.update(sql, paramMap);
				if(row == 0){
//					throw new ExtBusinessException("更新失败！影响行数为0。");
				}
			} catch(Exception ex){
				throw new Exception("更新数据失败!\n>>>PARAMETER>>>\n"+request.getParam()+"\n>>>SQL>>>\n"+sql+"\n"+ExceptionUtils.getFullStackTrace(ex),ex);
			}
		}
		return row;
	}

	@Override
	public void execute(SqlBatchUpdateRequest request) throws Exception {
		int row=0;
		if(StringUtils.isBlank(request.getSql())){
			throw new Exception("sql不能为空！");
		}
		
//		List<Map<String,Object>> paramList = request.getParam();
		List<Map> paramList = request.getParam();
		
		//对SQL进行解析
		 
//		String sql = (String)parseSql(request.getParam().get(0),request.getSql());;
		String sql = request.getSql();
		
		try {
			
			NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(dataSource);
			int[] rows = npjt.batchUpdate(sql, paramList.toArray(new Map[]{}));
			if(row == 0){
//					throw new ExtBusinessException("更新失败！影响行数为0。");
			}
		} catch(Exception ex){
			throw new Exception("更新数据失败!\n>>>PARAMETER>>>\n"+request.getParam()+"\n>>>SQL>>>\n"+sql+"\n"+ExceptionUtils.getFullStackTrace(ex),ex);
		}
	
		
	}

}
