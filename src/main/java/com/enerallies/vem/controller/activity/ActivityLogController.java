/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.controller.activity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.audit.AddManualLogRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.site.AddSiteRequest;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.activity.ActivityLogService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : ActivityLogController 
 * 
 * ActivityLogController: is used to handle all the Activity related Requests
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

@RestController
@RequestMapping("/api/activityLog")
public class ActivityLogController {

	/* Get the logger object*/
	private static final Logger logger=Logger.getLogger(ActivityLogController.class);
	
	@Autowired ActivityLogService activityLogService;
	
	/** Constant for user details object **/
	private static final String USER_DETAILS_OBJECT="eaiUserDetails";
	
	
	/**
	 * getActivityLogData controller is used handle the request & response for 
	 * Getting the Activity Log Data.
	 *  
	 * @param serviceId
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/get", method=RequestMethod.GET)
	private ResponseEntity<Response> getActivityLogData(@RequestParam(value = "serviceId") int serviceId,
			@RequestParam(value = "specificId") int specificId,
			@RequestParam(value = "startDate") String startDate,
			@RequestParam(value = "endDate") String endDate,
			HttpSession session){
		
		logger.info("[BEGIN] [getActivityLogData] [ActivityLogController Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		logger.debug("[DEBUG] the serviceId::::::"+serviceId);
		logger.debug("[DEBUG] the specificId::::::"+specificId);

		logger.debug("[DEBUG] the startDate::::::"+startDate);
		logger.debug("[DEBUG] the endDate::::::"+endDate);
		/*
		 *  Getting the session user details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the serviceId is less than or equal to '-1' means serviceId is invalid
				 * if the serviceId is greater than '0' means serviceId is valid.
				 */
				if(serviceId > -1){
					response = activityLogService.getActivityLogData(serviceId, specificId, startDate, endDate, userDetails.getUserId(),userDetails.getTimeZone());
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_ACTIVITY_LOG_SERVICE_ID_INVALID);
					response.setData(CommonConstants.ERROR_OCCURRED+":Service id should not be valid!");
				}
				 
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [getActivityLogData] [ActivityLogController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	
	/**
	 * addSite controller is used handle the request & response for 
	 * creating an new Site.
	 *  
	 * @param roleRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	private ResponseEntity<Response> createManualActivityLog(@RequestBody AddManualLogRequest addManualLogRequest,HttpSession session){
		
		logger.info("[BEGIN] [getActivityLogData] [ActivityLogController Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid.
				 */
				if(addManualLogRequest != null){
					addManualLogRequest.setIsPdfReport(0);
					response = activityLogService.createManualActivityLog(addManualLogRequest, userDetails.getUserId());
					status = HttpStatus.OK;
				}else{
					throw new NullPointerException();
				}

			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch(NullPointerException ne){
			status=HttpStatus.BAD_REQUEST;
			logger.error("",ne);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [getActivityLogData] [ActivityLogController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	

	/**
	 * getActivityLogData controller is used handle the request & response for 
	 * Getting the Activity Log Data.
	 *  
	 * @param serviceId
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getActivityLogPagination", method=RequestMethod.GET)
	private ResponseEntity<Response> getActivityLogPaginationData(@RequestParam(value = "serviceId") int serviceId,
			@RequestParam(value = "specificId") int specificId,
			@RequestParam(value = "startDate") String startDate,
			@RequestParam(value = "endDate") String endDate,
			@RequestParam(value="currentPage") int currentPage,
			@RequestParam(value="recordsPerPage") int recordsPerPage, 
			@RequestParam(value="filterByAction") String action,
			@RequestParam(value="filterByModule") String module,
			@RequestParam(value="description") String description,
			HttpSession session){
		
		logger.info("[BEGIN] [getActivityLogPaginationData] [ActivityLogController Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		logger.debug("[DEBUG] the serviceId::::::"+serviceId);
		logger.debug("[DEBUG] the specificId::::::"+specificId);
		logger.debug("[DEBUG] the startDate::::::"+startDate);
		logger.debug("[DEBUG] the endDate::::::"+endDate);
		logger.debug("[DEBUG] the currentPage::::::"+currentPage);
		logger.debug("[DEBUG] the records per page::::::"+recordsPerPage);
		logger.debug("[DEBUG] the action ::::::"+action);
		logger.debug("[DEBUG] the module ::::::"+module);
		logger.debug("[DEBUG] the description ::::::"+ description);
		
		/*
		 *  Getting the session user details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the serviceId is less than or equal to '-1' means serviceId is invalid
				 * if the serviceId is greater than '0' means serviceId is valid.
				 */
				if(serviceId > -1){
					response = activityLogService.getActivityLogPaginationData(serviceId, specificId, startDate, endDate, userDetails.getUserId(),userDetails.getTimeZone(),currentPage,recordsPerPage,action,module,description);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_ACTIVITY_LOG_SERVICE_ID_INVALID);
					response.setData(CommonConstants.ERROR_OCCURRED+":Service id should not be valid!");
				}
				 
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [getActivityLogPaginationData] [ActivityLogController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}

	/**
	 * getActivityLogData controller is used handle the request & response for 
	 * Getting list of groups based on filter
	 *  
	 * @param json request with filter type
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getFilterData", method=RequestMethod.GET)
	private ResponseEntity<Response> getFilterData(HttpServletRequest request,HttpSession session){
		
		Response response = new Response();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		try{
			if(!validateSession(session)){
				logger.error("[ERROR][ActivityLogController][getFilterData]");
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);	
			}
			
			GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
			
			if(userDetails!=null)
			{
				response = activityLogService.getFilterData(userDetails.getUserId(), userDetails.getIsSuper());
				status = HttpStatus.OK;
			}
			
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
			
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
				
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMERS_FETCH);
			}
			
		}	
			
		logger.info("status **************"+status);
		
		return new ResponseEntity<>(response, status);
	}
	
	private boolean validateSession(HttpSession session){
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		boolean sessionFlag=false;
		try {
			// checking for if session is valid or not
			if(userDetails != null){
				sessionFlag=true;
			}
					
		} catch (Exception e) {
			logger.error("" ,e);
			sessionFlag=false;
		}
		return sessionFlag;

	}
	
}
