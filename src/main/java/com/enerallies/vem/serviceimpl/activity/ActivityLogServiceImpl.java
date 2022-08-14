/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.serviceimpl.activity;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enerallies.vem.beans.audit.AddManualLogRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.dao.activity.ActivityLogDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.activity.ActivityLogService;
import com.enerallies.vem.serviceimpl.site.SiteServiceImpl;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : ActivityLogServiceImpl 
 * 
 * ActivityLogServiceImpl:Its an implementation class for ActivityLogService service interface.
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

@Service("activityLogService")
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService{
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(ActivityLogServiceImpl.class);
	
	/**instantiating the ActivityLogDao dao for accessing the dao layer.*/
	@Autowired 
	@Qualifier("activityLogDaoImpl")
	ActivityLogDao activityLogDao;
	
	@Override
	public Response getActivityLogData(int serviceId, int specificId,String startDate, 
			String endDate, int userId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getActivityLogData] [ActivityLogServiceImpl SERVICE LAYER]");
		
		Response response = new Response();
		
		//Used to store list of Activity logs.
		JSONArray activityLogData;
		
		try {
			
			//Calling the DAO layer getActivityLogData() method.
			activityLogData = activityLogDao.getActivityLogData(serviceId, specificId, startDate, endDate, userId, timeZone);
			
			/* if activityLogData is not null means the getActivityLogData request is
			 *  success
			 *  else fail.
			 */
			if(activityLogData!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_ACTIVITY_LOG_DATA_SUCCESS);
				response.setData(activityLogData);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [getActivityLogData] [ActivityLogServiceImpl SERVICE LAYER]");
		
		return response;
	}

	
	@Override
	public Response getActivityLogPaginationData(int serviceId, int specificId,String startDate, 
			String endDate, int userId, String timeZone,int currentPage, int recordsPerPage,
			String action , String module, String description) throws VEMAppException {
		
		logger.info("[BEGIN] [getActivityLogPaginationData] [ActivityLogServiceImpl SERVICE LAYER]");
		
		Response response = new Response();
		
		//Used to store list of Activity logs.
		JSONArray activityLogData;
		
		try {
			
			//Calling the DAO layer getActivityLogData() method.
			activityLogData = activityLogDao.getActivityLogPaginationData(serviceId, specificId, startDate, endDate, userId, timeZone,currentPage,recordsPerPage,action,module,description);
			int totalRecords=activityLogDao.getActivityTotalRecords(serviceId, specificId, startDate, endDate, userId, timeZone,action,module,description);
			
			JSONObject jsonResponse=new JSONObject();
			
			/* if activityLogData is not null means the getActivityLogData request is
			 *  success
			 *  else fail.
			 */
			if(activityLogData!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_ACTIVITY_LOG_DATA_SUCCESS);
				jsonResponse.put("records", activityLogData);
				jsonResponse.put("totalRecords", totalRecords);
				response.setData(jsonResponse);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [getActivityLogPaginationData] [ActivityLogServiceImpl SERVICE LAYER]");
		
		return response;
	}

	
	@Override
	public Response createManualActivityLog(
			AddManualLogRequest addManualLogRequest, int userId)
			throws VEMAppException {
		
		logger.info("[BEGIN] [createManualActivityLog] [Activity Log SERVICE LAYER]");
		
		Response response = new Response();
		
		JSONObject activityLog = new JSONObject();
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(addManualLogRequest);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				//Catches when all the server or bean level validations are true.
				int status = activityLogDao.createManualActivityLog(addManualLogRequest, userId);
				
				/* if status is 1 or greater means the add site request is
				 *  success
				 *  else fail.
				 */
				if(status >= 1){
					activityLog.put("activityLogId", status);
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.CREATE_ACTIVITY_LOG_SUCCESS);
					response.setData(activityLog);
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.CREATE_ACTIVITY_LOG_FAILED);
					response.setData(CommonConstants.ERROR_OCCURRED+":Log has not created at DB Side.");
				}
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.CREATE_ACTIVITY_LOG_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CREATE_ACTIVITY_LOG_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [createManualActivityLog] [Activity Log service LAYER]");
		
		return response;
	
	}


	@Override
	public Response getFilterData(Integer userId, int isSuperUser)
			throws VEMAppException {
		
		logger.info("[BEGIN] [getFilterData] [ActivityLogServiceImpl SERVICE LAYER]");
		
		Response response = new Response();
		
		//Used to store list of Activity logs.
		JSONArray activityLogData;
		
		try {
			
			//Calling the DAO layer getFilterData() method.
			activityLogData = activityLogDao.getFilterData(userId,  isSuperUser);
			
			JSONObject jsonResponse=new JSONObject();
			
			/* if activityLogData is not null means the getFilterData request is
			 *  success
			 *  else fail.
			 */
			if(activityLogData!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_ACTIVITY_LOG_DATA_SUCCESS);
				jsonResponse.put("records", activityLogData);
				response.setData(jsonResponse);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ACTIVITY_LOG_DATA_FAILED, logger, e);
		}
		
		logger.info("[END] [getFilterData] [ActivityLogServiceImpl SERVICE LAYER]");
		
		return response;
	}
}
