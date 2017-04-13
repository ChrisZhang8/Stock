package com.zxj.dbm.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class BuyOrderServlet
 */
public class BuyOrderServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private static final String[] keys = new String[]{"PRODUCT","BRAND",
			"BATCH_NO","ORDER_QTY","VENDOR","SHAPE","RECEIVE_QTY","PRICE","TOTAL_AMOUNT","REMARK"};
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BuyOrderServlet() {
        super();
        /*key.put(0, "PRODUCT");
		key.put(1, "BRAND");
        key.put(2, "BATCH_NO");
        key.put(3, "ORDER_QTY");
        key.put(4, "VENDOR");
        key.put(5, "SHAPE");
        key.put(6, "RECEIVE_QTY");
        key.put(7, "PRICE");
        key.put(8, "TOTAL_AMOUNT");
        key.put(9, "REMARK");*/
    }
    
    public void init() throws ServletException {
    	super.init();
    }
	

	@Override
	public String execute(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 新增采购订单
	 * @param HttpServletRequest
	 * @param HttpServletResponse
	 */
	public void addBuyOrder(HttpServletRequest request, HttpServletResponse response){
		try {
			Map param = getReqContext(request,response);	
			buyOrderService.addBuyOrder(param);
			this.outPrintJsonText(request, response, "新增采购订单成功！");
		} catch (Exception e) {
			this.outPrintJsonException(request, response, e.getMessage());
		}
		
		
	}
	
	/**
	 * 修改采购订单
	 * @param request
	 * @param response
	 */
	public void updateBuyOrder(HttpServletRequest request,
			HttpServletResponse response){
		try {
			Map param = getReqContext(request,response);
			buyOrderService.updateBuyOrder(param);
			
			this.outPrintJsonText(request, response, "修改成功！");
		} catch (Exception e) {
			this.outPrintJsonException(request, response, e.getMessage());
		}
	}
	
	public void viewBuyOrder(HttpServletRequest request, HttpServletResponse response){
		try {
			
			Map param = getReqContext(request,response);	
			
			Map order = buyOrderService.viewBuyOrderByNo(param);
			Map detail = buyOrderService.viewPaginatorOrderDetail(param);
			
			Map result = new HashMap();
			result.put("order", order);
			result.put("detail", detail);
			
			this.outPrintText(request, response, JSONObject.fromObject(result).toString());
		} catch (Exception e) {
			e.printStackTrace();
			this.outPrintJsonException(request, response, e.getMessage());
		}
	}
	
	
	public void viewPaginatorOrders(HttpServletRequest request, HttpServletResponse response){
		try {
			
			Map param = getReqContext(request,response);	
			
			Map result = buyOrderService.viewPaginatorOrders(param);
			
			this.outPrintText(request, response, JSONObject.fromObject(result).toString());
			
		} catch (Exception e) {
			this.outPrintJsonException(request, response, e.getMessage());
		}
	}

	public void viewPaginatorOrderDetail(HttpServletRequest request, HttpServletResponse response){
		try {
			Map param = getReqContext(request,response);	
			
			Map result = buyOrderService.viewPaginatorOrderDetail(param);
			
			this.outPrintText(request, response, JSONObject.fromObject(result).toString());
		} catch (Exception e) {
			this.outPrintJsonException(request, response, e.getMessage());
		}
	}
	
	
	public void importExcel(HttpServletRequest request,
			HttpServletResponse response){
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory(); 
			ServletFileUpload upload = new ServletFileUpload(factory); 
			upload.setHeaderEncoding("UTF-8");
			//设置上传文件大小的上限，-1表示无上限 
			upload.setSizeMax(Long.valueOf(-1));
			FileItem fi = null;
			List<FileItem> fileItems = upload.parseRequest(request);
			Iterator it = fileItems.iterator();
			while (it.hasNext()) {
				FileItem fitem = (FileItem) it.next();
				if(!fitem.isFormField()){
					fi = fitem;
				}
			}
			List<Map> list = new ArrayList<Map>();
			if(fi.getName().toUpperCase().endsWith(".XLS")){
				list = parseExcel(fi.getInputStream());
			}else if(fi.getName().toUpperCase().endsWith(".XLSX")){
				list = parseXlsx(fi.getInputStream());
			}else{
				throw new Exception("导入文件 必须为EXCEL文件！");
			}
			Map result = new HashMap();
			result.put("rows", list);
			result.put("total", list.size());
			
			this.outPrintText(request, response, JSONObject.fromObject(result).toString());
			
		} catch (Exception e) {
			this.outPrintJsonException(request, response, e.getMessage());
		}
		
	}
	
	
	private   List parseExcel(InputStream inputStream)throws Exception{
		try {
			HSSFWorkbook workbook=new HSSFWorkbook(inputStream);
			
				HSSFRow row=null;
				HSSFSheet sheet=workbook.getSheetAt(0);  
				int rowNum=sheet.getLastRowNum();//获得总行数
				int fr = sheet.getFirstRowNum();
//				Object[][] arrays = new Object[rowNum][coloumNum];
				List list = new ArrayList();
				HSSFCell cell=null;
				for (int i = fr+1; i < rowNum+1; i++) {
					row= sheet.getRow(i); 
					if(row==null)continue;
					Map m = new HashMap();
					int coloumNum=row.getPhysicalNumberOfCells();//获得总列数
					int fc = row.getFirstCellNum();
					for (int k = fc; k < keys.length; k++) {
						if(row.getCell(k)==null)continue;
						
						cell = row.getCell(k);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						if(StringUtils.isNotBlank(keys[k]))m.put(keys[k], cell.getStringCellValue());
					}
					if(m.isEmpty())continue;
					list.add(m);
				}
			
			return list;
		} catch (Exception e) {
			
			throw new Exception("解析EXCEl文件失败!",e);
		}
	}
	
	private  List parseXlsx(InputStream inputStream)throws Exception{
		try {
			
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			
				XSSFRow row=null;
				XSSFSheet sheet=workbook.getSheetAt(0);  
				
				int rowNum=sheet.getLastRowNum();//获得总行数
				int fr = sheet.getFirstRowNum();
				
//				Object[][] arrays = new Object[rowNum][coloumNum];
				List list = new ArrayList();
				XSSFCell cell=null;
				for (int i = fr+1; i < rowNum+1; i++) {
					row= sheet.getRow(i); 
					if(row==null)continue;
					Map m = new HashMap();
					int coloumNum=row.getPhysicalNumberOfCells();//获得总列数
					int fc = row.getFirstCellNum();
					for (int k = fc; k < keys.length; k++) {
						if(row.getCell(k)==null)continue;
						
						cell = row.getCell(k);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						if(StringUtils.isNotBlank(keys[k]))m.put(keys[k], cell.getStringCellValue());
						
					}
					if(m.isEmpty())continue;
					list.add(m);
				}
				
			return list;
		} catch (Exception e) {
			throw new Exception("解析EXCEl文件失败!",e);
		}
	}
}
