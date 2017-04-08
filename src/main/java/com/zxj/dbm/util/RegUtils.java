package com.zxj.dbm.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

public class RegUtils {
	public interface FindCallback {
		public String execute(String source);
	} 

	public static String findAndReplaceProp(String targetStr,Object obj) {
		String regEx = "\\{\\s*[a-zA-Z\\.0-9\\_\\-\\?\\*\\/\\[\\]\\(\\)]+\\s*\\}";
		Pattern p = Pattern.compile(regEx); // 正则表达式去匹配所有匹配的字符串
		Matcher m = p.matcher(targetStr);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String key = StringUtils.trim(StringUtils.substringBetween(m.group(),"{","}")); 
			String newStr = "";
			try { 
				newStr = PropertyUtils.getProperty(obj, key)+""; 
			} catch (Exception e) { 
				e.printStackTrace();
			}
			m.appendReplacement(buf, newStr);
		}
		m.appendTail(buf);
		return buf.toString();
	}
	
	public static String findAndReplace(String regEx, String targetStr,
			RegUtils.FindCallback callback) {
		Pattern p = Pattern.compile(regEx); // 正则表达式去匹配所有匹配的字符串
		Matcher m = p.matcher(targetStr);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String newStr = callback.execute(m.group());
			m.appendReplacement(buf, newStr);
		}
		m.appendTail(buf);
		return buf.toString();
	}
	public static String tranProperty(String source){
		 String reg = "\\_[a-zA-Z\\.0-9\\_\\-\\?\\*\\/\\(\\)]";
		source = StringUtils.lowerCase(source);
		source = RegUtils.findAndReplace(reg, source, new RegUtils.FindCallback(){

			@Override
			public String execute(String source) { 
				source = StringUtils.replace(source, "_", "");
				System.out.println(source);
				return StringUtils.upperCase(source);
			}});
		return source;
	}
}
