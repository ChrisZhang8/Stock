package com.zxj.dbm.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.zxj.dbm.service.BuyOrderServiceInterface;
import com.zxj.dbm.service.LogicServiceInterface;
import com.zxj.dbm.service.ShopOrderServiceInterface;
import com.zxj.dbm.service.StorageServiceInterface;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class BaseServlet
 */
public abstract class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String STATUS_KEY = "status";
	public SimpleDateFormat sdm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String RESULT_KEY = "result";

	public static final String JSON_STATUS_KEY = "status";
	public static final String JSON_RESULT_KEY = "result";
	public static final String JSON_MESSAGE_KEY = "message";
	public static final String JSON_MESSAGE_DETAIL_KEY = "messageDetail";
	public static final String JSON_STATUS_SUCCESS = "Y";
	public static final String JSON_STATUS_FAILURE = "N";

	@Autowired
	protected LogicServiceInterface logicService;
	
	@Autowired
	protected ShopOrderServiceInterface shopOrderService;
	
	@Autowired
	protected BuyOrderServiceInterface buyOrderService;
	
	@Autowired
	protected StorageServiceInterface storageService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BaseServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		super.init();
		// SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
		// config.getServletContext());
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//		ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String method = request.getParameter("method");
		if (StringUtils.isEmpty(method))
			method = "execute";
		try {
			Object path = MethodUtils.invokeMethod(this, method, new Object[] { request, response });
			if (path != null) {

				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path.toString());
				dispatcher.forward(request, response);
			}
		} catch (Exception e) {
			throw new RuntimeException((new StringBuilder()).append("execute method:'").append(method)
					.append("' fail !").append(e.getMessage()).toString(), e);
		}
	}

	public static Map getReqContext(HttpServletRequest request, HttpServletResponse response){
		Map p = new HashMap();
		Enumeration   en =request.getParameterNames();   
        while(en.hasMoreElements()){   
          String   paramName=(String)en.nextElement();                       
          String[]   values=request.getParameterValues(paramName);   
          if(values.length == 1){
        	  p.put(paramName, values[0]);
          }
          else if(values.length > 1){
        	  p.put(paramName, values);
          }

        } 
		return p;
	}
	
	public abstract String execute(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse);

	/**
	 * 错误处理
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doError(HttpServletRequest request, HttpServletResponse response, Exception e)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/com/sap/me/system/common/client/ErrorPage.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * 页面跳转处理
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doJump(HttpServletRequest request, HttpServletResponse response, String urls)
			throws ServletException, IOException {
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(urls);
		requestDispatcher.forward(request, response);

		// RequestDispatcher dispatcher = request.getRequestDispatcher(urls);
		// dispatcher.forward(request, response);
	}

	
	/**
	 * 简单的输出文本格式内容，适用于Ajax请求。
	 * 
	 * @param content
	 * @throws @throws
	 *             IOException
	 */
	public void simpleResponse(String content, HttpServletResponse response) {

		response.setContentType("text/html; charset=UTF-8");

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(content);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	public String getJsonText(Object obj) {
		JSONObject res = new JSONObject();
		res.put(STATUS_KEY, true);
		if (obj instanceof Exception) {
			res.put(RESULT_KEY, ((Exception) obj).getMessage());
		} else {
			res.put(RESULT_KEY, obj);
		}
		return res.toString();
	}

	public void outPrintJsonException(HttpServletRequest request, HttpServletResponse response, String ex) {
		Map m = new HashMap();
		m.put("status", "N");
		m.put("message", ex);
		outPrintText(request, response, JSONObject.fromObject(m).toString());
	}

	public void outPrintJsonText(HttpServletRequest request, HttpServletResponse response, Object obj) {
		Map m = new HashMap();
		m.put(this.JSON_STATUS_KEY, this.JSON_STATUS_SUCCESS);
		m.put(this.JSON_RESULT_KEY, obj);
		this.outPrintText(request, response, JSONObject.fromObject(m).toString());
	}

	/**
	 * 打印错误信息
	 * 
	 * @param obj
	 * @return
	 */
	public void outPrintJsonException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		boolean isDebug = StringUtils.equalsIgnoreCase(request.getParameter("_DEBUG"), "true");
		Map m = new HashMap();
		m.put(JSON_STATUS_KEY, JSON_STATUS_FAILURE);
		m.put(JSON_MESSAGE_KEY, ExceptionUtils.getStackTrace(ex));
		if (isDebug)
			m.put(JSON_MESSAGE_DETAIL_KEY, ExceptionUtils.getStackTrace(ex));
		outPrintText(request, response, JSONObject.fromObject(m).toString());
	}

	public void outPrintText(HttpServletRequest request, HttpServletResponse response, String text) {
		response.setContentType("text/json;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(text);
		} catch (IOException e) {
			if (out != null)
				try {
					out.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public List<FileItem> uploadFile(HttpServletRequest request, String path) throws Exception {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			// 创建磁盘工厂，利用构造器实现内存数据储存量和临时储存路径
			DiskFileItemFactory factory = new DiskFileItemFactory(1024 * 4, new File(path));

			// 产生一新的文件上传处理程式
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 设置路径、文件名的字符集
			upload.setHeaderEncoding("UTF-8");
			// 设置允许用户上传文件大小,单位:字节
			upload.setSizeMax(1024 * 1024 * 100);
			// 解析请求，开始读取数据
			// Iterator<FileItem> iter = (Iterator<FileItem>)
			// upload.getItemIterator(request);
			// 得到所有的表单域，它们目前都被当作FileItem
			List<FileItem> fileItems = upload.parseRequest(request);
			return fileItems;
		}
		return null;

	}

	/**
	 * 下载
	 * 
	 * @param response
	 * @throws FileNotFoundException
	 */
	public void downloadLocal(HttpServletResponse response, String fileName, String filePath) throws Exception {
		// 下载本地文件
		// 文件的默认保存名 fileName
		// 读到流中
		BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(filePath));// 文件的存放路径
		// 设置输出的格式
		response.reset();
		response.setContentType("text/html");
		response.addHeader("Content-Disposition", "attachment; filename=" + fileName.toString() + "");
		// 循环取出流中的数据
		byte[] b = new byte[1024];
		int len;
		try {
			while ((len = inStream.read(b)) > 0)
				response.getOutputStream().write(b, 0, len);
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

}
