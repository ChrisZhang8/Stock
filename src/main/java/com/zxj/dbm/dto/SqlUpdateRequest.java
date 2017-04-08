package com.zxj.dbm.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SqlUpdateRequest implements Serializable{

	private String site;
	private String user;
	private Map<String,Object> param = new HashMap<String,Object>();
	private String sql;
	private String dtSource;
	
	
	
	
	public String getDtSource() {
		return dtSource;
	}
	public void setDtSource(String dtSource) {
		this.dtSource = dtSource;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Map<String, Object> getParam() {
		return param;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	
	
	
}
