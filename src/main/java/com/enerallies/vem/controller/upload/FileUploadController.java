package com.enerallies.vem.controller.upload;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.enerallies.vem.beans.admin.BulkUploadResponse;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.upload.FileUploadResponse;
import com.enerallies.vem.service.admin.AdminService;
import com.enerallies.vem.service.pdfreport.PdfReportService;
import com.enerallies.vem.service.site.SiteService;
import com.enerallies.vem.serviceimpl.site.BulkUploaderThread;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : FileUploadController 
 * 
 * IndexController: This is the index controller to point index page
 *
 * @author Madhu Bantu (mbantu@ctepl.com).
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        30-09-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	30-09-2016		Madhu Bantu		     File Created
 */

@Controller
@RequestMapping("/fileUpload")
public class FileUploadController {
     
	// Getting logger
	private static final Logger logger = Logger.getLogger(FileUploadController.class);
	
	//Images location
	private static final String IMAGES_LOCATION = "vem.images";
	
	/** Auto wiring instance of IotService  */
	@Autowired
	AdminService adminService;
	
	@Autowired
	SiteService siteService;
	
	@Autowired PdfReportService pdfReportService;
	
	private String getExcelPath() {
		String path = "vem.excel";
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		if ("/vemqa".equals(request.getContextPath())) {
			path = "vemqa.excel";
		}
		logger.info("[END] [getExcelPath] path is "+path);
		return path;
	}
	/**
	 * Upload single file using uploadFileHandler
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ResponseEntity<Response> uploadFileHandler(MultipartHttpServletRequest request,@RequestParam("file") MultipartFile file) {
		        logger.info("[END] [uploadFileHandler] [Controller Layer]");
        		FileUploadResponse fileUploadResponse= new FileUploadResponse();
        		String fileName ="";
		        // Instantiating Response object
				Response response = new Response();
				// status code instantiation
				HttpStatus status = HttpStatus.OK;
		        if (!file.isEmpty()) {
		    	try {
			    String fileNames = file.getOriginalFilename();
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = System.getProperty(IMAGES_LOCATION);
				//File dir = new File(rootPath + File.separator + "EnerAlliesImages");
				File dir = new File(rootPath);
				if (!dir.exists())
					dir.mkdirs();
				 DateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
			     String timeStamp = format.format(new Date());
			     fileName = "Image_"+timeStamp+"."+fileNames.split("\\.(?=[^\\.]+$)")[1];
			     File path=new File(dir.getAbsolutePath()+File.separator+fileName);
				 BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
				 stream.write(bytes);
				 stream.close();
				 fileUploadResponse.setFileLocation(path.getAbsolutePath());
				 fileUploadResponse.setFileName(fileName);
				 response.setData(fileUploadResponse);
				 response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				logger.info("Server File Location="	+ path.getAbsolutePath());
			    } catch (Exception e) {
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.UPLOAD_ERROR_SAVE_FAILED);
				logger.error("[ERROR] [uploadFileHandler] [Controller Layer]"+e);
			    }

			    }
		       logger.info("[END] [uploadFileHandler] [Controller Layer]");
		       return new ResponseEntity<>(response, status);
	        }
	
	@RequestMapping(value="/loadImage", method = RequestMethod.GET)
	public void loadImage(HttpServletRequest request, HttpServletResponse response
			,HttpSession session) throws IOException{

		logger.info("Starting of loadImage doGet()");
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		String rootPath = System.getProperty(IMAGES_LOCATION);
		FileInputStream inputStream = null;
		OutputStream outputStream = null;
		String imageFilePath = null;
		if(request.getParameter("imageName") !=null && request.getParameter("imageName")!="" ) {
			imageFilePath=rootPath + File.separator + request.getParameter("imageName");
		}
		logger.info("FILE PATH :: " + imageFilePath);
		response.reset();
		response.setHeader("Content-Disposition", "inline");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Expires", "0");
		response.setContentType("image/png");
		/*
		 *  checking for if session is valid or not
		 */
		if(userDetails != null){
			
			/*
			 * Catches when the session is valid.
			 */
			if(imageFilePath !=null) {
				byte[] image = null;
				File file = new File(imageFilePath);
				image = new byte[(int) file.length()];
				try {
					inputStream = new FileInputStream(imageFilePath);
					inputStream.read(image);
					outputStream = response.getOutputStream();
					outputStream.write(image);
				} catch (Exception ex) {
					response.reset();
					response.setStatus(500);
					logger.error("Error in loadImage", ex);
				} finally {
					if (outputStream != null) {
						outputStream.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				}
			}
		}else{
			response.reset();
			response.setStatus(401);
		}
		
	}
	
	/**
	 * loadApplicationImage: It loads application image from different path
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping(value="/loadApplicationImage", method = RequestMethod.GET)
	public void loadApplicationImage(HttpServletRequest request, HttpServletResponse response
			,HttpSession session) throws IOException{

		logger.info("Starting of loadImage doGet()");
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		String rootPath = System.getProperty(IMAGES_LOCATION);
		FileInputStream inputStream = null;
		OutputStream outputStream = null;
		String imageFilePath = null;
		if(request.getParameter("imageName") !=null && request.getParameter("imageName")!="" ) {
			imageFilePath=rootPath + File.separator + "ApplicationImages" + File.separator + request.getParameter("imageName");
		}
		logger.info("FILE PATH :: " + imageFilePath);
		response.reset();
		response.setHeader("Content-Disposition", "inline");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Expires", "0");
		response.setContentType("image/png");
		/*
		 *  checking for if session is valid or not
		 */
		//if(userDetails != null){
			
			/*
			 * Catches when the session is valid.
			 */
			if(imageFilePath !=null) {
				byte[] image = null;
				File file = new File(imageFilePath);
				image = new byte[(int) file.length()];
				try {
					inputStream = new FileInputStream(imageFilePath);
					inputStream.read(image);
					outputStream = response.getOutputStream();
					outputStream.write(image);
				} catch (Exception ex) {
					response.reset();
					response.setStatus(500);
					logger.error("Error in loadImage", ex);
				} finally {
					if (outputStream != null) {
						outputStream.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				}
			}
		/*}else{
			response.reset();
			response.setStatus(401);
		}*/
		
	}
	
	@RequestMapping(value="/loadExcel", method = RequestMethod.GET)
	public void loadExcel(HttpServletRequest request, HttpServletResponse response
			,HttpSession session) throws IOException{

		logger.info("Starting of loadExcel doGet()");
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		String rootPath = System.getProperty(getExcelPath());
		FileInputStream inputStream = null;
		OutputStream outputStream = null;
		String imageFilePath = null;
		if(request.getParameter("excelName") !=null && request.getParameter("excelName")!="" ) {
			imageFilePath=rootPath + File.separator + request.getParameter("excelName");
		}
		logger.info("FILE PATH :: " + imageFilePath);
		response.reset();
		response.setHeader("Content-Disposition", "inline");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Expires", "0");
		String headerValue = String.format("attachment; filename=\"%s\"", request.getParameter("excelName"));
		response.setHeader("Content-Disposition", headerValue);
	    response.setContentType("application/vnd.ms-excel");
		/*
		 *  checking for if session is valid or not
		 */
		if(userDetails != null){
			
			/*
			 * Catches when the session is valid.
			 */
			if(imageFilePath !=null) {
				byte[] excel = null;
				File file = new File(imageFilePath);
				excel = new byte[(int) file.length()];
				try {
					inputStream = new FileInputStream(imageFilePath);
					inputStream.read(excel);
					outputStream = response.getOutputStream();
					outputStream.write(excel);
					logger.info("loadExcel success :: " + imageFilePath);
				} catch (Exception ex) {
					response.reset();
					response.setStatus(500);
					logger.error("Error in loadExcel", ex);
				} finally {
					if (outputStream != null) {
						outputStream.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				}
			}
		}else{
			logger.info("loadExcel invalid session");
			response.reset();
			response.setStatus(401);
		}
		
	}
	
	/**
	 * uploadSettingsLogo : Uploading settings logo
	 * 
	 * @param request
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/uploadSettingsLogo", method = RequestMethod.POST)
	public ResponseEntity<Response> uploadSettingsLogo(@RequestParam("file") MultipartFile file) {
		        logger.info("[START] [uploadSettingsLogo] [File Upload Controller Layer]");
        		FileUploadResponse fileUploadResponse= new FileUploadResponse();
        		String fileName ="";
		        // Instantiating Response object
				Response response = new Response();
				// status code instantiation
				HttpStatus status = HttpStatus.OK;
		        if (!file.isEmpty()) {
			    	try {
					    String fileNames = file.getOriginalFilename();
						byte[] bytes = file.getBytes();
		
						// Creating the directory to store file
						String rootPath = System.getProperty(IMAGES_LOCATION);
						File dir = new File(rootPath + File.separator+"ApplicationImages");
						 dir.mkdirs();
					     fileName = "uploaded_logo"+"."+fileNames.split("\\.(?=[^\\.]+$)")[1];
					     File path=new File(dir.getAbsolutePath()+File.separator+fileName);
						 BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
						 stream.write(bytes);
						 stream.close();
						 fileUploadResponse.setFileLocation(path.getAbsolutePath());
						 fileUploadResponse.setFileName(fileName);
						 response.setData(fileUploadResponse);
						 response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						 response.setCode(ErrorCodes.UPLOAD_INFO_APP_IMAGE_SAVE_SUCCESS);
						logger.info("Server File Location="	+ path.getAbsolutePath());
				    } catch (Exception e) {
						// Preparing failure response
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.UPLOAD_ERROR_SAVE_FAILED);
						logger.error("[ERROR] [uploadSettingsLogo] [Controller Layer]"+e);
				    }

			    }else{
			    	// Preparing failure response
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.USER_ERROR_UPLOAD_IMAGE_EMPTY);
			    }
		       logger.info("[END] [uploadSettingsLogo] [Controller Layer]");
		       return new ResponseEntity<>(response, status);
	 }
	
	/**
	 * loadAppLogo: It loads the application logo
	 * @param request
	 * @param response
	 * @param session
	 * @throws IOException
	 */
	@RequestMapping(value="/loadAppLogo", method = RequestMethod.GET)
	public void loadAppLogo(HttpServletRequest request, HttpServletResponse response
			,HttpSession session) throws IOException{

		logger.info("Starting of loadImage doGet()");
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		String rootPath = System.getProperty(IMAGES_LOCATION);
		FileInputStream inputStream = null;
		OutputStream outputStream = null;
		String imageFilePath = null;
		if(request.getParameter("imageName") !=null && request.getParameter("imageName")!="" ) {
			imageFilePath=rootPath + File.separator + "ApplicationImages" + File.separator + request.getParameter("imageName");
		}
		logger.info("FILE PATH :: " + imageFilePath);
		response.reset();
		response.setHeader("Content-Disposition", "inline");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Expires", "0");
		response.setContentType("image/png");
		/*
		 *  checking for if session is valid or not
		 */
		if(userDetails != null){
			
			/*
			 * Catches when the session is valid.
			 */
			if(imageFilePath !=null) {
				byte[] image = null;
				File file = new File(imageFilePath);
				image = new byte[(int) file.length()];
				try{
					inputStream = new FileInputStream(imageFilePath);
					inputStream.read(image);
					outputStream = response.getOutputStream();
					outputStream.write(image);
				} catch (Exception ex) {
					response.reset();
					response.setStatus(500);
					logger.error("Error in loadImage", ex);
				} finally {
					if (outputStream != null) {
						outputStream.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				}
			}
		}else{
			response.reset();
			response.setStatus(401);
		}
		
	}
	/**
	 * uploadUsersTemplate : It will improt list of users and save it in users table.
	 * @author (Madhu Bantu � CTE).
	 * @param MultipartHttpServletRequest
	 * @param session
	 * @return
	 */
	 @RequestMapping(value="/usersUpload",method=RequestMethod.POST)
	  public ResponseEntity<Response> usersUpload(MultipartHttpServletRequest request,HttpSession session,@RequestParam("file") MultipartFile file) throws Exception{
		  logger.info("UploadUsersControler uploadUsersTemplate start");
		            // Instantiating Response object
			        Response response = new Response();
			        
			        // status code instantiation
			        HttpStatus status = HttpStatus.OK;
			        FileUploadResponse fileUploadResponse= new FileUploadResponse();
    		        String fileName ="";
    		        String  responseStatus="",recordInserted="";
 		            int failedRecords =0,succesRecords =0,totalRecords=0;
	                if (!file.isEmpty()) {
	    	        try {
		            String fileNames = file.getOriginalFilename();
			        byte[] bytes = file.getBytes();
			          // Creating the directory to store file
			         String rootPath = System.getProperty(IMAGES_LOCATION);
			         File dir = new File(rootPath);
			         if (!dir.exists())
				     dir.mkdirs();
			         DateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
		             String timeStamp = format.format(new Date());
		             fileName = "UploadUsersTemplate_"+timeStamp+"."+fileNames.split("\\.(?=[^\\.]+$)")[1];
		             File path=new File(dir.getAbsolutePath()+File.separator+fileName);
			         BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
			         stream.write(bytes);
			         stream.close();
			         File template=new File(dir.getAbsolutePath()+System.getProperty("file.separator")+"UploadUsersTemplate_"+timeStamp+".xlsx");
		             response =adminService.userUploadTemplate(dir.getAbsolutePath()+System.getProperty("file.separator")+"UploadUsersTemplate_"+timeStamp+".xlsx",session);
		             BulkUploadResponse bulkUploadResponse= (BulkUploadResponse) response.getData();
		             bulkUploadResponse.setFileName(fileName);
		             failedRecords = bulkUploadResponse.getTotalCount()-bulkUploadResponse.getSucessCount();
		             bulkUploadResponse.setFailedCount(failedRecords);
		             responseStatus = bulkUploadResponse.getRecordResponse();
		             recordInserted = bulkUploadResponse.getInsertRecord();
		     } catch (Exception e) {
		             logger.error("UploadUsers Template Error:",e);
		             response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				     response.setCode(ErrorCodes.USER_UPLOAD_FAILED);
				     return new ResponseEntity<>(response, status);
		     }
		     if(response.getStatus() == "wrong format"){
		    	    response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				    response.setCode(ErrorCodes.USER_UPLOAD_WRONG_FORMAT_FAILED);
				    return new ResponseEntity<>(response, status);
		     }
		     else if((recordInserted == "Not a valid record") && (responseStatus =="Record Inserted")){
		    	    response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				    response.setCode(ErrorCodes.USER_UPLOAD_RECORD_FAILED);
				    return new ResponseEntity<>(response, status);
		     } else if(recordInserted == "Not a valid record"){
		    	    response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				    response.setCode(ErrorCodes.USER_UPLOAD_RECORDS_FAILED);
				    return new ResponseEntity<>(response, status);
		     }
		     logger.info("UploadUsers AddUsersTemplate end");
		        // Preparing success response object
				   response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				   response.setCode(ErrorCodes.USER_UPLOAD_SUCCESS);
				   return new ResponseEntity<>(response, status);
		 }
	               return new ResponseEntity<>(response, status);
	  }
	 
	 /**
		 * uploadSiteTemplate : It will import list of users and save it in users table.
		 * @author (Madhu Bantu � CTE).
		 * @param MultipartHttpServletRequest
		 * @param session
		 * @return
		 */
		 @RequestMapping(value="/sitesUpload",method=RequestMethod.POST)
		  public ResponseEntity<Response> sitesUpload(MultipartHttpServletRequest request,HttpSession session,@RequestParam(value = "customerId") int customerId,@RequestParam("file") MultipartFile file) throws Exception{
			  logger.info("Sites BulkUpload Template start");
			            // Instantiating Response object
				        Response response = new Response();
				        
				        // status code instantiation
				        HttpStatus status = HttpStatus.OK;
				        FileUploadResponse fileUploadResponse= new FileUploadResponse();
	    		        String fileName ="";
	    		        String  responseStatus="",recordInserted="";
	 		            int failedRecords =0,succesRecords =0,totalRecords=0;
	 		           BulkUploadResponse bulkUploadResponse;
		                if (!file.isEmpty()) {
		    	        try {
			            String fileNames = file.getOriginalFilename();
			            String[] nameExt = fileNames.split("\\.(?=[^\\.]+$)");
			            
			            
			            // 	check for file format
			            logger.info("before chekcing the file "+fileNames);
			            
			            if(fileNames == null || fileNames.isEmpty() || (!fileNames.endsWith("xlsx") && !fileNames.endsWith("xls"))) {
							logger.error("Invalid file format");			            	
			            	response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.SITE_UPLOAD_WRONG_FORMAT_FAILED);
							return new ResponseEntity<>(response,status);							
						}
			            
			            String fileExtension="."+nameExt[1];
			            fileName = nameExt[0];
			            logger.info("File format is valid ");
			            
				        byte[] bytes = file.getBytes();
				          // Creating the directory to store file
				         String rootPath = System.getProperty(getExcelPath());
				         File dir = new File(rootPath);
				         if (!dir.exists())
					     dir.mkdirs();
				         /*DateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
			             String timeStamp = format.format(new Date());*/
				         long random = System.currentTimeMillis();
			             fileName = fileName+"_results_"+random+fileExtension;
			             File path=new File(dir.getAbsolutePath()+File.separator+fileName);
				         BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
				         stream.write(bytes);
				         stream.close();
				         //File template=new File(dir.getAbsolutePath()+System.getProperty("file.separator")+"Sites_Bulk_Import_Template_"+timeStamp+".xlsx");
				         
				         GetUserResponse userDetail = (GetUserResponse) session.getAttribute("eaiUserDetails");
				         fileUploadResponse.setUserId(userDetail.getUserId());
				         fileUploadResponse.setIsSuper(userDetail.getIsSuper());
				         fileUploadResponse.setFileLocation(dir.getAbsolutePath()+System.getProperty("file.separator")+fileName);
				         fileUploadResponse.setCustomerId(customerId);
				         fileUploadResponse.setFileName(fileName);
				         
				         boolean isSheetValid = checkWhetherSheetIsValidOrNot(fileUploadResponse);
						if (!isSheetValid) {
							logger.error("Invalid file format");
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.SITE_UPLOAD_WRONG_FORMAT_FAILED);
							return new ResponseEntity<>(response, status);
						}
				         
				         BulkUploaderThread bulkUploaderThread = new BulkUploaderThread();
				         bulkUploaderThread.siteBulkUploadMethod(fileUploadResponse); //uncomment this line to enable thread code and comment below line
				         //response = siteService.uploadSiteTemplate(fileUploadResponse); 
				         
				         //uncomment this line to enable thread code
			            /* bulkUploadResponse= (BulkUploadResponse) response.getData();
			             System.out.println("==> fileName :: "+fileName);
			             bulkUploadResponse.setFileName(fileName);
			             failedRecords = bulkUploadResponse.getTotalCount()-bulkUploadResponse.getSucessCount();
			             bulkUploadResponse.setFailedCount(failedRecords);
			             responseStatus = bulkUploadResponse.getRecordResponse();
			             recordInserted = bulkUploadResponse.getInsertRecord();*/
			     } catch (Exception e) {
			             logger.error("Sites_Bulk_Import_Template Error:",e);
			             response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					     response.setCode(ErrorCodes.SITE_UPLOAD_FAILED);
					     return new ResponseEntity<>(response, status);
			     }
			     /*if(response.getStatus() == "wrong format"){
			    	    response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					    response.setCode(ErrorCodes.SITE_UPLOAD_WRONG_FORMAT_FAILED);
					    return new ResponseEntity<>(response, status);
			     }
			     else if((recordInserted == "Not a valid record") && (responseStatus =="Record Inserted")){
			    	    response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					    response.setCode(ErrorCodes.SITE_UPLOAD_RECORD_FAILED);
					    return new ResponseEntity<>(response, status);
			     } else if(recordInserted == "Not a valid record"){
			    	    response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					    response.setCode(ErrorCodes.SITE_UPLOAD_RECORDS_FAILED);
					    return new ResponseEntity<>(response, status);
			     } else if("null".equals(recordInserted) || StringUtils.equalsIgnoreCase(recordInserted, "null") || bulkUploadResponse.getSucessCount() == 0){
			    	    response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					    response.setCode(ErrorCodes.SITE_UPLOAD_RECORDS_FAILED);
					    return new ResponseEntity<>(response, status);
			     }*/ 
			     
			     logger.info("Sites BulkUpload Template end");
			        // Preparing success response object
			     		response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					    response.setCode(ErrorCodes.SITE_UPLOAD_SUCCESS);
					   return new ResponseEntity<>(response, status);
			 }   else{  
				        response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				        response.setCode(ErrorCodes.SITE_UPLOAD_WRONG_FORMAT_FAILED);
		               return new ResponseEntity<>(response, status);
			 }
		  }
		 
		private boolean checkWhetherSheetIsValidOrNot(FileUploadResponse fileUploadResponse) {
			File file = new File(fileUploadResponse.getFileLocation());
			try (FileInputStream fis = new FileInputStream(file)) {
	
				Workbook workbook = null;
				if (fileUploadResponse.getFileLocation().toLowerCase().endsWith("xlsx")) {
					workbook = new XSSFWorkbook(fis);
				} else if (fileUploadResponse.getFileLocation().toLowerCase().endsWith("xls")) {
					workbook = new HSSFWorkbook(fis);
				}
	
				int numberOfSheets = workbook.getNumberOfSheets();
	
				for (int i = 0; i < numberOfSheets; i++) {
					Sheet sheet = workbook.getSheetAt(i);
					int rowCounter = 0;
					Row row = null;
					while (i <= sheet.getLastRowNum()) {
						rowCounter = rowCounter + 1;
						row = sheet.getRow(i++);
						if (row != null && row.getRowNum() == 0) {
							Map<Integer,String> excelHeaderValues = new HashMap<>();
							int cellNum = 0;
							int cellType = 0;
							for (cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
								Cell cellVal = row.getCell(cellNum);
								if ((cellType == XSSFCell.CELL_TYPE_STRING) || (cellType == XSSFCell.CELL_TYPE_NUMERIC)
										|| (cellType == XSSFCell.CELL_TYPE_BOOLEAN)) {
									if (cellVal == null || cellVal.getCellType() == Cell.CELL_TYPE_BLANK) {
										break;
									} else {
										excelHeaderValues.put(cellNum, row.getCell(cellNum).getStringCellValue().toUpperCase());
									}
								}
							}
							
							if (!excelHeaderValues.containsValue("SITE NAME")) {
								return false;
							}
							if (!excelHeaderValues.containsValue("STATE")) {
								return false;
							}
							if (!excelHeaderValues.containsValue("CITY")) {
								return false;
							}
							if (!excelHeaderValues.containsValue("ZIP CODE")) {
								return false;
							}
							if (!excelHeaderValues.containsValue("SITE PHONE")) {
								return false;
							}
							if (!excelHeaderValues.containsValue("ADDRESS")) {
								return false;
							}
						}	
					}
				}
	
			} catch (Exception e) {
				logger.error("SiteServiceImpl UploadSiteTemplate Error:", e);
			}
			return true;
		}
		
		 /**
			 * loadPDFReportImages: It loads the PDF report images logo
			 * @param request
			 * @param response
			 * @throws Exception
			 */
			 @RequestMapping(value="/loadPDFReportImages", method = RequestMethod.GET)
			 public void loadPDFReportImages(HttpServletRequest request, HttpServletResponse response) throws Exception{

				logger.info("Starting of loadPDFReportImages doGet()");
				
				// Prepare streams.
				BufferedInputStream input;
				BufferedOutputStream output;
				String rootPath = System.getProperty(IMAGES_LOCATION);
				String imageFilePath = null;
				if(request.getParameter("imageName") !=null && request.getParameter("imageName")!="" ) {
					imageFilePath=rootPath + File.separator + request.getParameter("imageName");
				}
				logger.debug("loadPDFReportImages FILE PATH :: " + imageFilePath);
				
				int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
		        File image = new File(imageFilePath);
		        
		         // Get content type by filename.
		         String contentType = "png";
		         contentType="image/"+contentType;
		
		         // Init servlet response.
		         response.reset();
		         response.setBufferSize(DEFAULT_BUFFER_SIZE);
		         response.setContentType(contentType);
		         response.setHeader("Content-Length", String.valueOf(image.length()));
		         response.setHeader("Content-Disposition", "inline; filename=\"" + image.getName() + "\"");
		
		        
		         input = new BufferedInputStream(new FileInputStream(image), DEFAULT_BUFFER_SIZE);
		           
		         output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
		         // Write file contents to response.
		         byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		         int length;
		         while ((length = input.read(buffer)) > 0) {
		             output.write(buffer, 0, length);
		        }
		        output.close();
		        input.close();
		        
		        logger.info("Starting of loadPDFReportImages doGet()");
			}
			 
			 @RequestMapping(value="/loadPDFReportChart", method = RequestMethod.GET)
			 public void loadPDFReportChart(HttpServletRequest request, HttpServletResponse response) throws Exception{

				logger.info("Starting of loadPDFReportImages doGet()");
				
				// Prepare streams.
				BufferedInputStream input;
				BufferedOutputStream output;
				String rootPath = System.getProperty(IMAGES_LOCATION);
				String imageFilePath = null;
				if(request.getParameter("imageName") != null && request.getParameter("imageName") != "" ) {
					imageFilePath = rootPath + File.separator + request.getParameter("folderName") + File.separator + request.getParameter("imageName");
				}
				logger.debug("loadPDFReportImages FILE PATH :: " + imageFilePath);
				
				int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
		        File image = new File(imageFilePath);
		        
		         // Get content type by filename.
		         String contentType = "png";
		         contentType="image/"+contentType;
		
		         // Init servlet response.
		         response.reset();
		         response.setBufferSize(DEFAULT_BUFFER_SIZE);
		         response.setContentType(contentType);
		         response.setHeader("Content-Length", String.valueOf(image.length()));
		         response.setHeader("Content-Disposition", "inline; filename=\"" + image.getName() + "\"");
		
		        
		         input = new BufferedInputStream(new FileInputStream(image), DEFAULT_BUFFER_SIZE);
		           
		         output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
		         // Write file contents to response.
		         byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		         int length;
		         while ((length = input.read(buffer)) > 0) {
		             output.write(buffer, 0, length);
		        }
		        output.close();
		        input.close();
		        
		        logger.info("Starting of loadPDFReportImages doGet()");
			}
		
			 
			@RequestMapping(value = "/getDownloadPDF", method = RequestMethod.GET)
			public void getDownloadPDF(@RequestParam String fileName, @RequestParam String reportName, HttpServletResponse response, HttpServletRequest request) throws UnsupportedOperationException, IOException {
				try {
					String rootPath = System.getProperty(IMAGES_LOCATION);
					String imageFilePath = rootPath + File.separator + "PDFImages" + File.separator + fileName;       		
					File pdfFile = new File(imageFilePath);
					InputStream is = new FileInputStream(pdfFile);

					String userAgent = request.getHeader("user-agent");
					boolean isInternetExplorer = (userAgent.indexOf("MSIE") > -1);

				    byte[] fileNameBytes = reportName.getBytes((isInternetExplorer) ? ("windows-1250") : ("utf-8"));
				    String dispositionFileName = "";
				    for (byte b: fileNameBytes) dispositionFileName += (char)(b & 0xff);
				    int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
			        response.reset();
			        response.setCharacterEncoding("UTF-8");
			        response.setContentType("application/pdf");
				    String disposition = "attachment; filename=\"" + dispositionFileName + ".pdf\"";
				    response.setHeader("Content-disposition", disposition);
					response.setContentLength((int) pdfFile.length());
					BufferedInputStream input = null;
				    BufferedOutputStream output = null;
		
			        input = new BufferedInputStream(is, DEFAULT_BUFFER_SIZE);
			        output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
			        
			        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			        int length;
			           while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			           }
			        output.close();
			        input.close();
					
					} catch(UnsupportedEncodingException ence) {
						logger.error("", ence);
					}
			        /*int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
			        response.reset();
			        response.setCharacterEncoding("UTF-8");
			        response.setContentType("application/pdf");
					response.addHeader("Content-Disposition", "attachment; filename=" + reportName + ".pdf");
					response.setContentLength((int) pdfFile.length());
					
			        BufferedInputStream input = null;
			        BufferedOutputStream output = null;
	
			        input = new BufferedInputStream(is, DEFAULT_BUFFER_SIZE);
			        output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
			        
			        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			        int length;
			           while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			           }
			        output.close();
			        input.close();*/
			}			
			
			@RequestMapping(value = "/getHighChartData", method = RequestMethod.GET)
			public void getHighChartData(@RequestParam("data") String data, HttpServletResponse response, HttpServletRequest request) throws UnsupportedOperationException, IOException {
				try {
					HttpPost post = new HttpPost("http://export.highcharts.com");
					HttpClient httpClient = HttpClientBuilder.create().build();
					  
					ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
					postParameters.add(new BasicNameValuePair("content", "options"));
					postParameters.add(new BasicNameValuePair("options", data));
					postParameters.add(new BasicNameValuePair("constr", "Chart"));
					postParameters.add(new BasicNameValuePair("type", "png"));
				    post.setEntity(new UrlEncodedFormEntity(postParameters));
				    
				    HttpResponse apacheResponse = httpClient.execute(post);
				    
			        InputStream is = apacheResponse.getEntity().getContent();
			        
			        int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
			        String contentType = "png";
			        contentType="image/"+contentType;
			
			        response.reset();
			        response.setBufferSize(DEFAULT_BUFFER_SIZE);
			        response.setContentType(contentType);
			        response.setHeader("Content-Disposition", "inline; filename=\"" + new Date().getTime() + ".PNG" + "\"");
			
			        BufferedInputStream input = null;
			        BufferedOutputStream output = null;
			
			        input = new BufferedInputStream(is, DEFAULT_BUFFER_SIZE);
			        output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
			        
			        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			        int length;
			           while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			           }
			        output.close();
			        input.close();
				} catch (Exception e) {
					logger.error("",e);
				}
			}
}