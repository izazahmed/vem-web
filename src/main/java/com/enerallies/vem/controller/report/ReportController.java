/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.controller.report;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.report.AnalyticsRequest;
import com.enerallies.vem.beans.report.ReportRequest;
import com.enerallies.vem.service.report.ReportService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : ReportController 
 * 
 * ReportController: is used to handle all the dash board related Requests
 *
 * @author Naagrjuna Eerla
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        22-11-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	22-11-2016			Nagarjuna Eerla		File Created
 */
@Controller
@RequestMapping("/api/report")
public class ReportController {

	// Getting logger instance
	private static final Logger logger = Logger.getLogger(ReportController.class);

	@Autowired
	ReportService reportService;

	/**
	 * getReportData: Is used to get reports data
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getDashboardData", method = RequestMethod.POST)
	public ResponseEntity<Response> getDashboardData(@RequestBody ReportRequest reportRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getDashboardData] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting reports data
				 response = reportService.getDashboardData(reportRequest, userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.DASH_ERROR_FETCH_FAILED);
			logger.error("[ERROR] [getDashboardData] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getDashboardData] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}

	/**
	 * getReportData: is used to get report data 
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getReportData", method = RequestMethod.POST)
	public ResponseEntity<Response> getReportData(@RequestBody ReportRequest reportRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getReportData] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting reports data
				 response = reportService.getReportData(reportRequest, userDetails.getUserId(),userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.REPORT_ERROR_FETCH_FAILED);
			logger.error("[ERROR] [getReportData] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getReportData] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getCustomerAnalyticsData: It gets customer analytics data
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getCustomerAnalyticsData", method = RequestMethod.POST)
	public ResponseEntity<Response> getCustomerAnalyticsData(@RequestBody AnalyticsRequest analyticsRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getCustomerAnalyticsData] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting analytics data
				 response = reportService.getCustomerAnalyticsData(analyticsRequest, userDetails.getUserId());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ANALYTICS_ERROR_FETCH_FAILED);
			logger.error("[ERROR] [getCustomerAnalyticsData] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getCustomerAnalyticsData] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getAnalyticParams: It gets analytic params for different data types 
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getAnalyticParams", method = RequestMethod.GET)
	public ResponseEntity<Response> getAnalyticParams(HttpSession session) {
		
		logger.info("[BEGIN] [getAnalyticParams] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting analytics data
				 response = reportService.getAnalyticParams();
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ANALYTICS_ERROR_FETCH_FAILED);
			logger.error("[ERROR] [getAnalyticParams] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getAnalyticParams] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getTempSetpointReport: is used to get temperature setpoint report data 
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getTempSetpointReport", method = RequestMethod.POST)
	public ResponseEntity<Response> getTempSetpointReport(@RequestBody ReportRequest reportRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getTempSetpointReport] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting reports data
				response = reportService.getTempSetpointReport(reportRequest, userDetails.getUserId(),userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.REPORT_ERROR_FETCH_FAILED);
			logger.error("[ERROR] [getTempSetpointReport] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getTempSetpointReport] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getHVACUsageReport: is used to get HVAC Usage report data 
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getHVACUsageReport", method = RequestMethod.POST)
	public ResponseEntity<Response> getHVACUsageReport(@RequestBody ReportRequest reportRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getHVACUsageReport] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting reports data
				response = reportService.getHVACUsageReport(reportRequest, userDetails.getUserId(),userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.REPORT_ERROR_FETCH_FAILED);
			logger.error("[ERROR] [getHVACUsageReport] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getHVACUsageReport] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getCustomerAnalyticsData: It gets customer analytics data
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getGroupAnalyticsData", method = RequestMethod.POST)
	public ResponseEntity<Response> getGroupAnalyticsData(@RequestBody AnalyticsRequest analyticsRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getGroupAnalyticsData] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting analytics data
				 response = reportService.getGroupAnalyticsData(analyticsRequest, userDetails.getUserId());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GROUP_ANALYTICS_ERROR_FETCH_FAILED);
			logger.error("[ERROR] [getGroupAnalyticsData] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getGroupAnalyticsData] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getCustomerAnalyticsData: It gets customer analytics data
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getSiteAnalyticsData", method = RequestMethod.POST)
	public ResponseEntity<Response> getSiteAnalyticsData(@RequestBody AnalyticsRequest analyticsRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getSiteAnalyticsData] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting analytics data
				 response = reportService.getSiteAnalyticsData(analyticsRequest, userDetails.getUserId());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.SITE_ANALYTICS_ERROR_FETCH_FAILED);
			logger.error("[ERROR] [getSiteAnalyticsData] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getSiteAnalyticsData] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getTempSetpointReport: is used to get temperature setpoint report data 
	 * 
	 * @param analyticsRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getTrendingAnalytics", method = RequestMethod.POST)
	public ResponseEntity<Response> getTrendingAnalytics(@RequestBody AnalyticsRequest analyticsRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getTrendingAnalytics] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting Trending analytic data
				response = reportService.getTrendingAnalytics(analyticsRequest, userDetails.getUserId(),userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ANALYTICS_ERROR_FETCH_FAILED);
			logger.error("[ERROR] [getTrendingAnalytics] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getTrendingAnalytics] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getDegradedPerformaceData: Is used to get dashboard degraded performance
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getDegradedPerformaceData", method = RequestMethod.POST)
	public ResponseEntity<Response> getDegradedPerformaceData(@RequestBody ReportRequest reportRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getDegradedPerformaceData] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting reports data
				 response = reportService.getDegradedPerformData(reportRequest, userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.DASHBOARD_DEGRADED_PERFORMANCE_FETCH_FAIL);
			logger.error("[ERROR] [getDegradedPerformaceData] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getDegradedPerformaceData] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}

	/**
	 * getWithinPerformaceData: Is used to get dashboard within performance data 
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getWithinPerformaceData", method = RequestMethod.POST)
	public ResponseEntity<Response> getWithinPerformaceData(@RequestBody ReportRequest reportRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getWithinPerformaceData] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting reports data
				 response = reportService.getWithinSetpointPerformData(reportRequest, userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.DASHBOARD_WITHIN_SETPOINT_PERFORMANCE_FETCH_FAIL);
			logger.error("[ERROR] [getWithinPerformaceData] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getWithinPerformaceData] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}

	/**
	 * getHvacPerformaceData: Is used to get dashboard HAVC performance data
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getHvacPerformaceData", method = RequestMethod.POST)
	public ResponseEntity<Response> getHvacPerformaceData(@RequestBody ReportRequest reportRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getHvacPerformaceData] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting reports data
				 response = reportService.getHVACUsagePerformData(reportRequest, userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.DASHBOARD_HVACUSAGE_PERFORMANCE_FETCH_FAIL);
			logger.error("[ERROR] [getHvacPerformaceData] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getHvacPerformaceData] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}

	/**
	 * getManualPerformaceData: Is used to get dashboard manual performance data
	 * 
	 * @param reportRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getManualPerformaceData", method = RequestMethod.POST)
	public ResponseEntity<Response> getManualPerformaceData(@RequestBody ReportRequest reportRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getManualPerformaceData] [Report Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Getting reports data
				 response = reportService.getManualChangesPerformData(reportRequest, userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.DASHBOARD_MANUAL_PERFORMANCE_FETCH_FAIL);
			logger.error("[ERROR] [getManualPerformaceData] [Report Controller Layer]"+e);
		}
		
		logger.info("[END] [getManualPerformaceData] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}

	/**
	 * getSitesForGroups controller is used handle the request & response for 
	 * Listing all the Sites based on Groups.
	 * 
	 * @param groupIds
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/list/groups", method=RequestMethod.GET)
	private ResponseEntity<Response> getSitesForGroupsInReports(@RequestParam(value = "customerId") String customerId, @RequestParam(value = "groupIds") String groupIds, HttpSession session){
		
		logger.info("[BEGIN] [getSitesForGroupsInReports] [Report Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		logger.debug("[DEBUG] the groupIds::::::"+groupIds);
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the groupIds is '' means groupIds is invalid
				 * if the groupIds is not '' means groupIds is valid.
				 */
				if(!groupIds.isEmpty()){
					response = reportService.getSitesForGroups(customerId, groupIds, userDetails);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_SITE_GROUP_IDS_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":Group ids should not be empty!");
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
		
		logger.info("[END] [getSitesForGroupsInReports] [Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
}
