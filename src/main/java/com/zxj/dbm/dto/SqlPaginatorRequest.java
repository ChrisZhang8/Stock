package com.zxj.dbm.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SqlPaginatorRequest extends SqlQueryRequest implements Serializable{

	private String startIndex;
	private String endIndex;
	private String pageSize;
	
	
	
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(String startIndex) {
		this.startIndex = startIndex;
	}
	public String getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(String endIndex) {
		this.endIndex = endIndex;
	}
	
	
	
	
}
