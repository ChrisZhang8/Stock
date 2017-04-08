package com.zxj.dbm.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicQueryResponse {
	private int totalRecords;
	private List<Map> records;
	private int startIndex;
	
	
	public int getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public List<Map> getRecords() {
		return records;
	}
	public void setRecords(List<Map> records) {
		this.records = records;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getStartIndex() {
		return startIndex;
	}
	
	
	
}
