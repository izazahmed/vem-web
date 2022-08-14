/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.daoimpl.activity;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.audit.AddManualLogRequest;
import com.enerallies.vem.dao.activity.ActivityLogDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.TableFieldConstants;

/**
 * File Name : ActivityLogDaoImpl 
 * 
 * ActivityLogDaoImpl an implementation class for ActivityLogDao interface.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        07-11-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 07-11-2016		Goush Basha		    File Created & getActivityLogData() method.
 *
 */

@Component("activityLogDaoImpl")
public class ActivityLogDaoImpl implements ActivityLogDao{
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(ActivityLogDaoImpl.class);
	
	/** Data source instance */
	private DataSource dataSource;
	
	/** JDBC Template instance */
	private JdbcTemplate jdbcTemplate;	
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	@Override
	public JSONArray getActivityLogData(int serviceId, int specificId, String startDate,
			String endDate, int userId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getActivityLogData] [ActivityLog DAO LAYER]");
		
		/*
		 * Used to hold Activity log data.
		 */
		JSONArray activityLogData= new JSONArray();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_select_activity_log procedure.");
			logger.debug("[DEBUG] Input param serviceId-"+serviceId);
			logger.debug("[DEBUG] Input param specificId-"+specificId);
			logger.debug("[DEBUG] Input param startDate-"+startDate);
			logger.debug("[DEBUG] Input param endDate-"+endDate);
			logger.debug("[DEBUG] Input param timeZone-"+timeZone);
			
			serviceId = (serviceId < 0) ? 0 :serviceId;
			specificId = (specificId < 0) ? 0 :specificId;
			
			jdbcTemplate.query("call sp_select_activity_log "
					+ ""
					+ "("+serviceId+","+specificId+","+userId+",'"+startDate+"','"+endDate+"','"+CommonUtility.isNull(timeZone)+"')", new RowCallbackHandler() {
				/*
				 * Used to hold the each record of Activity log.
				 */
				JSONObject obj = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {
					/*
					 * Getting the metadata from result set.
					 * and looping among each record and creating the activity log object
					 * for each record and adding the object to array to make list of
					 * activity log data.
					 */
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					obj=new JSONObject();
					for(int i=1;i<=columnCount;i++){
						if("alNumber".equalsIgnoreCase(rsmd.getColumnLabel(i)))
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getInt(rsmd.getColumnLabel(i))));
						else if(rsmd.getColumnType(i) == Types.INTEGER)
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getInt(rsmd.getColumnLabel(i))));
						else
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getString(rsmd.getColumnLabel(i))));
					}
						
				   activityLogData.add(obj);
				
				}
			});
			
			logger.debug("[DEBUG] ActivityLog activityLogData List - "+activityLogData);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED, logger, e);
		}
		
		logger.info("[END] [getActivityLogData] [ActivityLog DAO LAYER]");
		
		return activityLogData;
	}

	
	@Override
	public JSONArray getActivityLogPaginationData(int serviceId, int specificId, String startDate,
			String endDate, int userId, String timeZone,int currentPage, int recordsPerPage,
			String action, String module, String description) throws VEMAppException {
		
		logger.info("[BEGIN] [getActivityLogPaginationData] [ActivityLog DAO LAYER]");
		
		/*
		 * Used to hold Activity log data.
		 */
		JSONArray activityLogData= new JSONArray();
		
		String searchFilter = "";
		
		try {
			
			logger.debug("[DEBUG] Executing sp_select_activity_log_pagination procedure.");
			logger.debug("[DEBUG] Input param serviceId-"+serviceId);
			logger.debug("[DEBUG] Input param specificId-"+specificId);
			logger.debug("[DEBUG] Input param startDate-"+startDate);
			logger.debug("[DEBUG] Input param endDate-"+endDate);
			logger.debug("[DEBUG] Input param timeZone-"+timeZone);
			logger.debug("[DEBUG] Input param currentPage-"+currentPage);
			logger.debug("[DEBUG] Input param recordsPerpage-"+recordsPerPage);
			logger.debug("[DEBUG] Input param action-"+action);
			logger.debug("[DEBUG] Input param module-"+module);
			logger.debug("[DEBUG] Input param description -"+description);
			
			serviceId = (serviceId < 0) ? 0 :serviceId;
			specificId = (specificId < 0) ? 0 :specificId;
			action	= (action.equalsIgnoreCase("undefined"))?"" : action;
			module	= (module.equalsIgnoreCase("undefined"))?"" : module;
			description	= (description.equalsIgnoreCase("undefined"))?"" : description;
			
			jdbcTemplate.query("call sp_select_activity_log_pagination "
					+ ""
					+ "("+serviceId+","+specificId+","+userId+",'"+startDate+"','"+endDate+"','"+CommonUtility.isNull(timeZone)+"',"+currentPage+","+recordsPerPage+",'"+searchFilter+"','"+action+"','"+module+"','"+description+"')", new RowCallbackHandler() {
				/*
				 * Used to hold the each record of Activity log.
				 */
				JSONObject obj = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {
					/*
					 * Getting the metadata from result set.
					 * and looping among each record and creating the activity log object
					 * for each record and adding the object to array to make list of
					 * activity log data.
					 */
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					obj=new JSONObject();
					for(int i=1;i<=columnCount;i++){
						if("alNumber".equalsIgnoreCase(rsmd.getColumnLabel(i)))
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getInt(rsmd.getColumnLabel(i))));
						else if(rsmd.getColumnType(i) == Types.INTEGER)
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getInt(rsmd.getColumnLabel(i))));
						else
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getString(rsmd.getColumnLabel(i))));
					}
						
				   activityLogData.add(obj);
				
				}
			});
			
			logger.debug("[DEBUG] ActivityLog activityLogData List - "+activityLogData);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED, logger, e);
		}
		
		logger.info("[END] [getActivityLogPaginationData] [ActivityLog DAO LAYER]");
		
		return activityLogData;
	}

	
	@Override
	public int getActivityTotalRecords(int serviceId, int specificId, String startDate, String endDate, int userId,
			String timeZone,String action, String module, String description) throws VEMAppException {
		
		
		logger.info("[BEGIN] [getActivityTotalRecords] [ActivityLog DAO LAYER]");
		
		/*
		 * Used to hold Activity log data.
		 */
		int logCount=0;
		SimpleJdbcCall simpleJdbcCall;
		try{
			logger.debug("[DEBUG] Executing getActivityTotalRecords procedure.");
			logger.debug("[DEBUG] Input param serviceId-"+serviceId);
			logger.debug("[DEBUG] Input param specificId-"+specificId);
			logger.debug("[DEBUG] Input param startDate-"+startDate);
			logger.debug("[DEBUG] Input param endDate-"+endDate);
			logger.debug("[DEBUG] Input param timeZone-"+timeZone);
			
			serviceId = (serviceId < 0) ? 0 :serviceId;
			specificId = (specificId < 0) ? 0 :specificId;
			action	= (action.equalsIgnoreCase("undefined"))?"" : action;
			module	= (module.equalsIgnoreCase("undefined"))?"" : module;
			description	= (description.equalsIgnoreCase("undefined"))?"" : description;
			
			
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
			String sqlProc="sp_select_activity_log_pagination_count";
						  
		    Map<String,Object> inputParams=new HashMap<>();
		    Map<String,Object> outParams=new HashMap<>();
		    inputParams.put("in_service_id", serviceId);
			inputParams.put("in_specific_id", specificId);
			inputParams.put("in_user_id", userId);
			inputParams.put("in_start_date", startDate);
			inputParams.put("in_end_date", endDate);
			inputParams.put("in_time_zone", timeZone);
			inputParams.put("in_action", action);
			inputParams.put("in_module", module);
			inputParams.put("in_description", description);
			
			logger.debug("[DEBUG] Executing "+sqlProc+"procedure with input params "+inputParams);
			simpleJdbcCall.withProcedureName(sqlProc);
			outParams=simpleJdbcCall.execute(inputParams);
			Iterator<Entry<String, Object>> itr = outParams.entrySet().iterator();
			
			while (itr.hasNext()) {
		        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
		        String key = entry.getKey();
		        if(key.equalsIgnoreCase("total_rec_count")){
		        	logger.info("entry.getValue()"+entry.getValue());
		        	logCount=(int) entry.getValue();
		        }
			}
			
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED, logger, e);
		}
		
		logger.info("[END] [getActivityLogPaginationData] [ActivityLog DAO LAYER]");
		return logCount;
	}

	@Override
	public int createManualActivityLog(AddManualLogRequest addManualLogRequest,
			int userId) throws VEMAppException {

		
		logger.info("[BEGIN] [createManualActivityLog] [Activity Log DAO LAYER]");
		
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
		    simpleJdbcCall.withProcedureName("sp_insert_manual_active_log");
		    
		    int serviceId = 0;
		    int specificId = 0;
		    String action = "";
		    StringBuilder desc = new StringBuilder();
		    
		    String manualLogSub = "";
		    String manualLogContact = "";
		    String manualLogDescription = "";
		    String manualLogContactNumber = "";
		    String manualLogTimetamp = "";
		    
		    int isPdfReport = 0;
			int reportPreference = 0;
			int reportLevel = 0;
			String reportLevelIds = "";
			String reportComponent = "";
		    
		    if (addManualLogRequest.getIsPdfReport() == 1) {
		    	action = "Emailed";
		    	desc = new StringBuilder(addManualLogRequest.getDescription());
		    	if (addManualLogRequest.getReportLevel() == 1) {
		    		serviceId = 1;
		    	} else if (addManualLogRequest.getReportLevel() == 2) {
		    		serviceId = 2;
		    	} else if (addManualLogRequest.getReportLevel() == 3) {
		    		serviceId = 3;
		    	}
		    	specificId = CommonUtility.isNull(addManualLogRequest.getSpecificId());
		    	isPdfReport = CommonUtility.isNull(addManualLogRequest.getIsPdfReport());
		    	reportPreference = CommonUtility.isNull(addManualLogRequest.getReportPreference());
		    	reportLevel = CommonUtility.isNull(addManualLogRequest.getReportLevel());
		    	reportLevelIds = CommonUtility.isNull(addManualLogRequest.getReportLevelIds());
		    	reportComponent = CommonUtility.isNull(addManualLogRequest.getReportComponent());
					
		    } else {
		    	List<String> description = new ArrayList<String>();

				if (StringUtils.isNotEmpty(addManualLogRequest.getTimeStamp())) {
					description.add(addManualLogRequest.getTimeStamp());
				}
				if (StringUtils.isNotEmpty(addManualLogRequest.getSubject())) {
					description.add(addManualLogRequest.getSubject());
				}
				if (StringUtils.isNotEmpty(addManualLogRequest.getContact())) {
					description.add(addManualLogRequest.getContact());
				}
				if (StringUtils.isNotEmpty(addManualLogRequest.getContactNumber())) {
					description.add(addManualLogRequest.getContactNumber());
				}
				if (StringUtils.isNotEmpty(addManualLogRequest.getDescription())) {
					description.add(addManualLogRequest.getDescription());
				}
				desc.append(StringUtils.join(description, ", "));
			    
			    if (addManualLogRequest.getDeviceId() != 0) {
			    	serviceId = 4;
			    	specificId = addManualLogRequest.getDeviceId();
			    } else if (addManualLogRequest.getSiteId() != 0) {
			    	serviceId = 3;
			    	specificId = addManualLogRequest.getSiteId();
			    } else if (addManualLogRequest.getGroupId() != 0) {
			    	serviceId = 2;
			    	specificId = addManualLogRequest.getGroupId();
			    } else if (addManualLogRequest.getCustomerId() != 0) {
			    	serviceId = 1;
			    	specificId = addManualLogRequest.getCustomerId();
			    }
			    if (addManualLogRequest.getType() == 1) {
					action = "Called";
			    } else if(addManualLogRequest.getType() == 2) {
			    	action = "Emailed";
			    } else {
			    	action = "Texted";
			    }
			    manualLogSub = CommonUtility.isNull(addManualLogRequest.getSubject());
			    manualLogContact = CommonUtility.isNull(addManualLogRequest.getContact());
			    manualLogDescription = CommonUtility.isNull(addManualLogRequest.getDescription());
			    manualLogContactNumber = CommonUtility.isNull(addManualLogRequest.getContactNumber());
			    manualLogTimetamp = CommonUtility.isNull(addManualLogRequest.getTimeStamp());
			    
		    }
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams = new HashMap<>();
			inputParams.put("al_action", action);
			inputParams.put("al_user_id", CommonUtility.isNull(userId));
			inputParams.put("al_location", "");
			inputParams.put("al_service_id", serviceId);
			inputParams.put("al_discription", desc);
			inputParams.put("al_service_specific_id", specificId);
			inputParams.put("in_al_manual_log_sub", manualLogSub);
			inputParams.put("in_al_manual_log_contact", manualLogContact);
			inputParams.put("in_al_manual_log_description", manualLogDescription);
			inputParams.put("in_al_manual_log_contact_number", manualLogContactNumber);
			inputParams.put("in_al_manual_log_time_stamp", manualLogTimetamp);
			
			inputParams.put("in_al_is_pdf_report", isPdfReport);
			inputParams.put("in_al_report_preference", reportPreference);
			inputParams.put("in_al_report_level", reportLevel);
			inputParams.put("in_al_report_level_ids", reportLevelIds);
			inputParams.put("in_al_report_component", reportComponent);
			
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_insert_manual_active_log");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the create Activity log request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get(TableFieldConstants.OUT_ERROR_MSG);
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get(TableFieldConstants.OUT_FLAG);
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CREATE_ACTIVITY_LOG_FAILED, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CREATE_ACTIVITY_LOG_FAILED, logger, e);
		}
		
		logger.info("[END] [createManualActivityLog] [Activity Log DAO LAYER]");

		return statusFlag;
		
	}
	
	@Override
	public JSONArray getManualActivityLogData(int serviceId, int specificId, int days,
			String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getManualActivityLogData] [ActivityLog DAO LAYER]");
		
		/*
		 * Used to hold Manual Activity log data.
		 */
		JSONArray activityLogData= new JSONArray();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_select_activity_log procedure.");
			logger.debug("[DEBUG] Input param serviceId-"+serviceId);
			logger.debug("[DEBUG] Input param specificId-"+specificId);
			logger.debug("[DEBUG] Input param days-"+days);
			logger.debug("[DEBUG] Input param timeZone-"+timeZone);
			
			serviceId = (serviceId < 0) ? 0 :serviceId;
			specificId = (specificId < 0) ? 0 :specificId;
			
			jdbcTemplate.query("call sp_select_manual_activity_log "
					+ ""
					+ "("+serviceId+","+specificId+","+days+",'"+CommonUtility.isNull(timeZone)+"')", new RowCallbackHandler() {
				/*
				 * Used to hold the each record of manual Activity log.
				 */
				JSONObject obj = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {
					/*
					 * Getting the metadata from result set.
					 * and looping among each record and creating the manual activity log object
					 * for each record and adding the object to array to make list of
					 * manual activity log data.
					 */
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					obj=new JSONObject();
					for(int i=1;i<=columnCount;i++){
						if("alNumber".equalsIgnoreCase(rsmd.getColumnLabel(i)))
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getInt(rsmd.getColumnLabel(i))));
						else if(rsmd.getColumnType(i) == Types.INTEGER)
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getInt(rsmd.getColumnLabel(i))));
						else
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getString(rsmd.getColumnLabel(i))));
					}
						
				   activityLogData.add(obj);
				
				}
			});
			
			logger.debug("[DEBUG] ManualActivityLog manual activityLogData List - "+activityLogData);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED, logger, e);
		}
		
		logger.info("[END] [getManualActivityLogData] [ActivityLog DAO LAYER]");
		
		return activityLogData;
	}

	@Override
	public JSONArray getFilterData(int userId, 
			int isSuperUser) throws VEMAppException {
		
		logger.info("[BEGIN] [getFilterData] [ActivityLog DAO LAYER]");
		
		/*
		 * Used to hold Activity log data.
		 */
		JSONArray activityLogData= new JSONArray();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_select_activity_log_pagination procedure.");
			logger.debug("[DEBUG] Input param userId-"+userId);
			logger.debug("[DEBUG] Input param isSuperUser-"+isSuperUser);
			
			
			jdbcTemplate.query("call sp_select_activity_log_filter_data "
					+ ""
					+ "("+userId+",'"+isSuperUser+"')", new RowCallbackHandler() {
				/*
				 * Used to hold the each record of Activity log.
				 */
				JSONObject obj = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {
					/*
					 * Getting the metadata from result set.
					 * and looping among each record and creating the activity log filter object
					 * for each record and adding the object to array to make list of
					 * activity log filter data.
					 */
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					obj=new JSONObject();
					for(int i=1;i<=columnCount;i++){
						if(rsmd.getColumnType(i) == Types.INTEGER)
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getInt(rsmd.getColumnLabel(i))));
						else
							obj.put(rsmd.getColumnLabel(i), CommonUtility.isNull(rs.getString(rsmd.getColumnLabel(i))));
					}
						
				   activityLogData.add(obj);
				
				}
			});
			
			logger.debug("[DEBUG] ActivityLog getFilterData List - "+activityLogData);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED, logger, e);
		}
		
		logger.info("[END] [getFilterData] [ActivityLog DAO LAYER]");
		
		return activityLogData;
	}
}
