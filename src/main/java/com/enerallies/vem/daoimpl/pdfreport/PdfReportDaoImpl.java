package com.enerallies.vem.daoimpl.pdfreport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.pdfreport.AddPDFReportDataRequest;
import com.enerallies.vem.dao.pdfreport.PdfReportDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.TableFieldConstants;

/**
 * File Name : PdfReportDaoImpl 
 * PdfReportDaoImpl: Its an implementation class for PdfReportDAO interface.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-02-2017
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 08-02-2017		Bhoomika Rabadiya   File Created.
 *
 */

@Component
public class PdfReportDaoImpl implements PdfReportDAO{

	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(PdfReportDaoImpl.class);
	
	/** Data source instance */
	private DataSource dataSource;
	
	/** JDBC Template instance */
	private JdbcTemplate jdbcTemplate;	
	
	private static final String DELETE_PDF_REPORT="update b_pdf_report_data set is_delete = 1 where id = ?";
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}

	@Override
	public JSONObject getReportList(int userId, int reportId) throws VEMAppException {
		
		logger.info("[BEGIN] [getReportList] [PDFReport DAO LAYER]");
		
		/*
		 * final return object.
		 */
		JSONArray reportData = new JSONArray();
		JSONObject reportFinalObject = new JSONObject();
		JSONObject reportFilterObject = new JSONObject();
		
		Set<String> customerFilterList = new HashSet<String>();
		Set<String> groupFilterList = new HashSet<String>();
		Set<String> siteFilterList = new HashSet<String>();
		
		try {
			
			jdbcTemplate.query("call sp_get_pdf_reports_list ("+userId+","+reportId+")", new RowCallbackHandler() {
				
				JSONObject reportObject = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {
					String[] customerNamesArray;
					String[] specificNamesArray;
					String specificNames = "";
					String customer = "";
					String customerLogo = "";
					reportObject = new JSONObject();
					
			    	customerNamesArray = rs.getString("specificNames").split("~~~");
			    	
			    	int reprtLevel = rs.getInt("reportLevel");
			    	
			    	if (customerNamesArray.length==1) {
			    		specificNamesArray = customerNamesArray[0].split("###");
			    		if (specificNamesArray.length == 2) {
			    			try {
			    				customerLogo = specificNamesArray[0].split("#~#")[1];
			    			} catch(ArrayIndexOutOfBoundsException e){
			    				customerLogo = "";
			    			}
			    			customer = specificNamesArray[0].split("#~#")[0];
			    			specificNames = specificNamesArray[1];
			    		}
			    		
			    	} else {
			    		for (int i = 0; i < customerNamesArray.length; i++) {
			    			if(specificNames.isEmpty()) {
			    				specificNames = customerNamesArray[i].split("###")[1];
			    			} else {
			    				specificNames = specificNames +"~#~"+ customerNamesArray[i].split("###")[1];
			    			}
						}
			    	}
			    	List<String> specificIdsArray = Arrays.asList(rs.getString("siteIds").split(","));
			    	List<Integer> intIDs = new ArrayList<Integer>();
			    	for (int i = 0; i < specificIdsArray.size(); i++) {	
			    		intIDs.add(Integer.parseInt(specificIdsArray.get(i).trim()));
			    	}
			    	Collections.sort(intIDs);
			    	List<String> tempArray = Arrays.asList(specificNames.split("~#~"));
			    	Set<String> uniqueTemp = new HashSet<String>(tempArray);
			    	for (int i = 0; i < tempArray.size(); i++) {	
				    	if (reprtLevel == 1) {			    		
		    				customerFilterList.add(tempArray.get(i).trim());
		    			} else if (reprtLevel == 2) {
		    				groupFilterList.add(tempArray.get(i).trim());
		    			} else {
		    				siteFilterList.add(tempArray.get(i).trim());
		    			}
			    	}
			    	reportObject.put("id", rs.getInt("id"));
			    	reportObject.put("userId", rs.getString("userId"));
			    	reportObject.put("userEmail", rs.getString("userEmail"));
			    	reportObject.put("userFName", rs.getString("userFName"));
			    	reportObject.put("userLName", rs.getString("userLName"));
			    	reportObject.put("reportLevel", reprtLevel);
			    	reportObject.put("reportStatus", rs.getInt("reportStatus"));
			    	reportObject.put("fileName", rs.getString("fileName"));
			    	reportObject.put("customer", customer);
			    	reportObject.put("companyLogo", customerLogo == null ? "" : customerLogo.trim());
			    	reportObject.put("comfortOpt", rs.getInt("comfortOpt"));
			    	reportObject.put("specificNames", StringUtils.join(uniqueTemp, ", "));
			    	reportObject.put("specificIds", StringUtils.join(intIDs, ","));
			    	reportObject.put("fromDate", rs.getString("fullFromDate"));
			    	reportObject.put("toDate", rs.getString("fullToDate"));		
			    	reportObject.put("mailFromDate", rs.getString("mailFromDate"));
			    	reportObject.put("mailToDate", rs.getString("mailToDate"));	
			    	reportObject.put("specificNamesArray", specificNames.split("~#~"));
			    	reportObject.put("specificNamesMail", specificNames);
			    	reportObject.put("reportDuration", rs.getString("fromDate")+" - "+rs.getString("toDate"));
			    	reportObject.put("reportLevelText", rs.getString("reportLevelText"));
			    	reportObject.put("reportPreferenceText", rs.getString("reportPreferenceText"));
			    	if (tempArray.size() > 1) {
			    		reportObject.put("reportName", (customer + " " + rs.getString("reportPreferenceText") + " " + rs.getString("reportLevelText")  + (reprtLevel != 1 ? "s" : "") + " Report : " + reportObject.get("reportDuration").toString()).trim());
				    	reportObject.put("reportPDFName", (customer + " " + rs.getString("reportPreferenceText") + " " + rs.getString("reportLevelText")  + (reprtLevel != 1 ? "s" : "") + " Report - " + (rs.getString("pdfFromDate")+" - "+rs.getString("pdfToDate")).toString()).trim());
				    	reportObject.put("resendHeader", (customer + " " + rs.getString("reportLevelText") + (reprtLevel != 1 ? "s " : "") + rs.getString("reportPreferenceText") + " Report").trim());
			    	} else {
						reportObject.put("reportName", tempArray.isEmpty() ? "" : tempArray.get(0) + " " + rs.getString("reportPreferenceText") + " Report : " + reportObject.get("reportDuration").toString().trim());
				    	reportObject.put("reportPDFName", tempArray.isEmpty() ? "" : tempArray.get(0).split("#")[0].toString().trim() + " " + rs.getString("reportPreferenceText") + " " + rs.getString("reportLevelText") + " Report - " + (rs.getString("pdfFromDate")+" - "+rs.getString("pdfToDate")).toString().trim());
				    	reportObject.put("resendHeader", tempArray.isEmpty() ? "" : tempArray.get(0) + " " + rs.getString("reportPreferenceText") + " Report");
			    	}
			    	
			    	reportData.add(reportObject);
				}
			});
			
			reportFinalObject.put("reportList", reportData);
			
			reportFilterObject.put("customerList", customerFilterList);
			reportFilterObject.put("groupList", groupFilterList);
			reportFilterObject.put("siteList", siteFilterList);
			
			reportFinalObject.put("filters", reportFilterObject);
			
			logger.debug("[DEBUG] PDF Report List reportData - "+reportData);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_GET_LIST_FAILED, logger, e);
		}
		
		logger.info("[END] [getReportList] [PDFReport DAO LAYER]");
		
		return reportFinalObject;
	}

	@Override
	public int addPDFReportData(AddPDFReportDataRequest addPDFReportDataRequest, int userId) throws VEMAppException {

		
		logger.info("[BEGIN] [addPDFReportData] [PDF Report DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
		
	    /*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_insert_pdf_reports_data");
		   
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
		    
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_report_level_text", CommonUtility.isNull(addPDFReportDataRequest.getReportLevelText()));
			inputParams.put(TableFieldConstants.IN_USER_ID, addPDFReportDataRequest.getUserId());
			inputParams.put("in_from_date", addPDFReportDataRequest.getFromDate());
			inputParams.put("in_to_date", addPDFReportDataRequest.getToDate());
			inputParams.put("in_actual_file_path", addPDFReportDataRequest.getActualFilePath());
			inputParams.put("in_specific_ids", StringUtils.join(addPDFReportDataRequest.getSpecificIds(), ","));
			inputParams.put("in_report_level", addPDFReportDataRequest.getReportLevel());
			inputParams.put("in_site_ids", addPDFReportDataRequest.getSiteIds());
			inputParams.put("in_report_preference", addPDFReportDataRequest.getReportPreference());
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_insert_pdf_reports_data");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the add Site request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get(TableFieldConstants.OUT_ERROR_MSG);
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get(TableFieldConstants.OUT_FLAG);
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_INSERT_FAILED, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_INSERT_FAILED, logger, e);
		}
		
		logger.info("[END] [addPDFReportData] [PDF Report DAO LAYER]");

		return statusFlag;
	}
	
	@Override
	public int deletePDFReport(String reportId) throws VEMAppException {
		logger.info("[BEGIN] [addPDFReportData] [PDF Report DAO LAYER]");
		int updatedFlag = 0;
		int count = 0;
		String[] reportIDs = reportId.split(",");
		try {
			for (int i = 0; i < reportIDs.length; i++) {
				updatedFlag = jdbcTemplate.update(DELETE_PDF_REPORT, reportIDs[i]);
				if (updatedFlag >= 1) {
					count++;
				}
			}
			if (reportIDs.length == count) {
				updatedFlag = 1;
			}
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_DELETE_FAILED, logger, e);
		}
		return updatedFlag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, ArrayList<HashMap<String, String>>> getPDFReportUsersData() throws VEMAppException {
		
		logger.info("[BEGIN] [getPDFReportUsersData] [Report DAO LAYER]");
		
		HashMap<Integer, ArrayList<HashMap<String, String>>> userMap=new LinkedHashMap<>();

		try {
			
			jdbcTemplate.query("call sp_get_reports_users ()", new RowCallbackHandler() {
				
				ArrayList<HashMap<String, String>> sitesList = null;
				HashMap<String,String> siteData = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {

			    	logger.debug("[DEbug] the user_id - "+rs.getString("user_id"));
			    	
			    	if(userMap.containsKey(rs.getInt("user_id"))){
			    		sitesList = userMap.get(rs.getInt("user_id"));
			    	}else{
			    		sitesList = new ArrayList<>();
			    	}
			    	
			    	siteData = new LinkedHashMap<>();
		    		
			    	siteData.put("userEmail", CommonUtility.isNull(rs.getString("user_email")));
			    	siteData.put("userFname", CommonUtility.isNull(rs.getString("user_fname")));
			    	siteData.put("userLname", CommonUtility.isNull(rs.getString("user_lname")));
			    	siteData.put("userReportPreference", CommonUtility.isNull(rs.getInt("user_report_preference"))+"");
			    	siteData.put("userReportLevel", CommonUtility.isNull(rs.getInt("user_report_level"))+"");
			    	siteData.put("userReportPreferenceText", CommonUtility.isNull(rs.getString("user_report_preference_text"))+"");
			    	siteData.put("userReportLevelText", CommonUtility.isNull(rs.getString("user_report_level_text"))+"");
			    	siteData.put("loggedUser", CommonUtility.isNull(rs.getInt("logged_user"))+"");
			    	
		    		siteData.put("siteId", CommonUtility.isNull(rs.getInt("site_id"))+"");
		    		siteData.put("siteName", CommonUtility.isNull(rs.getString("site_name")));
		    		siteData.put("siteCode", CommonUtility.isNull(rs.getString("site_internal_id")));
		    		siteData.put("deviceCount", CommonUtility.isNull(rs.getInt("device_count"))+"");
		    		
		    		siteData.put("groupIds", CommonUtility.isNull(rs.getString("group_ids")));
		    		
		    		siteData.put("customerId", CommonUtility.isNull(rs.getInt("customer_id"))+"");
		    		siteData.put("companyName", CommonUtility.isNull(rs.getString("company_name")));
		    		siteData.put("companyLogo", CommonUtility.isNull(rs.getString("company_logo")));
		    		siteData.put("comfortOpt", CommonUtility.isNull(rs.getInt("comfort_opt")) > 0 ? "1" : "0");
		    		siteData.put("siteAddress", CommonUtility.isNull(rs.getString("site_address")));
		    		
		    		sitesList.add(siteData);
		    		
			    	userMap.put(CommonUtility.isNull(rs.getInt("user_id")), sitesList);
			    	
				}
			});
			
			logger.debug("[DEBUG] userMap - "+userMap);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_SITE_FAILED, logger, e);
		}
		
		logger.info("[END] [getPDFReportUsersData] [Report DAO LAYER]");
		
		return userMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, ArrayList<HashMap<String, String>>> getPDFReportUsersCustomerData() throws VEMAppException {
		
		logger.info("[BEGIN] [getPDFReportUsersCustomerData] [Report DAO LAYER]");
		
		HashMap<Integer, ArrayList<HashMap<String, String>>> userMap=new LinkedHashMap<>();

		try {
			
			jdbcTemplate.query("call sp_get_reports_users_customers ()", new RowCallbackHandler() {
				
				ArrayList<HashMap<String, String>> customersList = null;
				HashMap<String,String> customerData = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {

			    	logger.debug("[DEbug] the user_id - "+rs.getString("user_id"));
			    	
			    	if(userMap.containsKey(rs.getInt("user_id"))){
			    		customersList = userMap.get(rs.getInt("user_id"));
			    	}else{
			    		customersList = new ArrayList<>();
			    	}
			    	
			    	customerData = new LinkedHashMap<>();
		    		
			    	customerData.put("userEmail", CommonUtility.isNull(rs.getString("user_email")));
			    	customerData.put("userFname", CommonUtility.isNull(rs.getString("user_fname")));
			    	customerData.put("userLname", CommonUtility.isNull(rs.getString("user_lname")));
			    	customerData.put("userReportPreference", CommonUtility.isNull(rs.getInt("user_report_preference"))+"");
			    	customerData.put("userReportLevel", CommonUtility.isNull(rs.getInt("user_report_level"))+"");
			    	customerData.put("userReportPreferenceText", CommonUtility.isNull(rs.getString("user_report_preference_text"))+"");
			    	customerData.put("userReportLevelText", CommonUtility.isNull(rs.getString("user_report_level_text"))+"");
			    	customerData.put("loggedUser", CommonUtility.isNull(rs.getInt("logged_user"))+"");
			    	
			    	customerData.put("deviceCount", CommonUtility.isNull(rs.getInt("device_count"))+"");
			    	customerData.put("customerId", CommonUtility.isNull(rs.getInt("customer_id"))+"");
			    	customerData.put("companyName", CommonUtility.isNull(rs.getString("company_name")));
			    	customerData.put("companyLogo", CommonUtility.isNull(rs.getString("company_logo")));
			    	customerData.put("comfortOpt", CommonUtility.isNull(rs.getInt("comfort_opt")) > 0 ? "1" : "0");
			    	customerData.put("siteIds", CommonUtility.isNull(rs.getString("site_ids"))+"");
		    		
			    	customersList.add(customerData);
		    		
			    	userMap.put(CommonUtility.isNull(rs.getInt("user_id")), customersList);
			    	
				}
			});
			
			logger.debug("[DEBUG] userMap - "+userMap);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_SITE_FAILED, logger, e);
		}
		
		logger.info("[END] [getPDFReportUsersCustomerData] [Report DAO LAYER]");
		
		return userMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, ArrayList<HashMap<String, String>>> getPDFReportUsersGroupData() throws VEMAppException {
		
		logger.info("[BEGIN] [getPDFReportUsersGroupData] [Report DAO LAYER]");
		
		HashMap<Integer, ArrayList<HashMap<String, String>>> userMap=new LinkedHashMap<>();

		try {
			
			jdbcTemplate.query("call sp_get_reports_users_groups ()", new RowCallbackHandler() {
				
				ArrayList<HashMap<String, String>> groupsList = null;
				HashMap<String,String> groupData = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {

			    	logger.debug("[DEbug] the user_id - "+rs.getString("user_id"));
			    	
			    	if(userMap.containsKey(rs.getInt("user_id"))){
			    		groupsList = userMap.get(rs.getInt("user_id"));
			    	}else{
			    		groupsList = new ArrayList<>();
			    	}
			    	
			    	groupData = new LinkedHashMap<>();
		    		
			    	groupData.put("userEmail", CommonUtility.isNull(rs.getString("user_email")));
			    	groupData.put("userFname", CommonUtility.isNull(rs.getString("user_fname")));
			    	groupData.put("userLname", CommonUtility.isNull(rs.getString("user_lname")));
			    	groupData.put("userReportPreference", CommonUtility.isNull(rs.getInt("user_report_preference"))+"");
			    	groupData.put("userReportLevel", CommonUtility.isNull(rs.getInt("user_report_level"))+"");
			    	groupData.put("userReportPreferenceText", CommonUtility.isNull(rs.getString("user_report_preference_text"))+"");
			    	groupData.put("userReportLevelText", CommonUtility.isNull(rs.getString("user_report_level_text"))+"");
			    	groupData.put("loggedUser", CommonUtility.isNull(rs.getInt("logged_user"))+"");
			    	
			    	groupData.put("groupId", CommonUtility.isNull(rs.getInt("group_id"))+"");
			    	groupData.put("groupName", CommonUtility.isNull(rs.getString("group_name")));
		    		groupData.put("deviceCount", CommonUtility.isNull(rs.getInt("device_count"))+"");
		    		
		    		groupData.put("customerId", CommonUtility.isNull(rs.getInt("customer_id"))+"");
		    		groupData.put("companyName", CommonUtility.isNull(rs.getString("company_name")));
		    		groupData.put("companyLogo", CommonUtility.isNull(rs.getString("company_logo")));
		    		groupData.put("comfortOpt", CommonUtility.isNull(rs.getInt("comfort_opt")) > 0 ? "1" : "0");
		    		groupData.put("siteIds", CommonUtility.isNull(rs.getString("site_ids"))+"");
		    		
		    		groupsList.add(groupData);
		    		
			    	userMap.put(CommonUtility.isNull(rs.getInt("user_id")), groupsList);
			    	
				}
			});
			
			logger.debug("[DEBUG] userMap - "+userMap);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_SITE_FAILED, logger, e);
		}
		
		logger.info("[END] [getPDFReportUsersGroupData] [Report DAO LAYER]");
		
		return userMap;
	}

}
