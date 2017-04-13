package com.zxj.dbm.web;

import net.sf.json.JSONArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class StorageServlet
 */
public class StorageServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see BaseServlet#BaseServlet()
     */
    public StorageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 入库存
	 * @param request
	 * @param response
	 */
	public void storage(HttpServletRequest request,HttpServletResponse response){
		try {
			Map param = getReqContext(request,response);	
			
    		JSONArray array = JSONArray.fromObject(param.get("PRODUCTS"));
    		List<Map> products = array.subList(0, array.size());
    		
    		param.put("PRODUCTS", products);
			storageService.storage(param);
			
			this.outPrintJsonText(request, response, "入库成功！");
			
		} catch (Exception e) {
			this.outPrintJsonException(request, response, e.getMessage());
		}
	}
	
	/**
	 * 库存消耗
	 * @param request
	 * @param response
	 */
	public void consume(HttpServletRequest request,HttpServletResponse response){
		try {
			
			
			
		} catch (Exception e) {
			this.outPrintJsonException(request, response, e.getMessage());
		}
	}

}
