/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.serviceimpl.report;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.report.AnalyticParams;
import com.enerallies.vem.beans.report.AnalyticsRequest;
import com.enerallies.vem.beans.report.CategoryAnalytics;
import com.enerallies.vem.beans.report.DataName;
import com.enerallies.vem.beans.report.HVACUsage;
import com.enerallies.vem.beans.report.MapData;
import com.enerallies.vem.beans.report.Report;
import com.enerallies.vem.beans.report.ReportRequest;
import com.enerallies.vem.beans.report.ReportResponse;
import com.enerallies.vem.dao.report.ReportDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.report.ReportService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : ReportServiceImpl 
 * 
 * ReportServiceImpl: is used to implement all the dash borad related service
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

@Service("reportService")
@Transactional
public class ReportServiceImpl implements ReportService{

	// Getting logger instance
	private static final Logger logger = Logger.getLogger(ReportServiceImpl.class);

	@Autowired
	ReportDao reportDao;	
	
	@Override
	public Response getDashboardData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {

		logger.info("[BEGIN] [getDashboardData] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			ReportResponse reportResponse = new ReportResponse(); 
			
			List<Report> reports = new LinkedList<>();
			Report degradedData = reportDao.getDegradedPerformData(reportRequest, userId, timeZone);
			Report withinSetpointdata = reportDao.getWithinSetpointPerformData(reportRequest, userId, timeZone);
			Report hvacUsageData = reportDao.getHVACUsagePerformData(reportRequest, userId, timeZone);
			Report manualData = reportDao.getManualChangesPerformData(reportRequest, userId, timeZone);
			reports.add(degradedData);
			reports.add(withinSetpointdata);
			reports.add(hvacUsageData);
			reports.add(manualData);
			List<DataName> criticalIssues = reportDao.getCriticalIssues(reportRequest, userId, timeZone);
			List<MapData> mapData = reportDao.getMapData(reportRequest, userId, timeZone);
			
			//Adding critical Issues data
			reportResponse.setReports(reports);
			reportResponse.setAlerts(criticalIssues);
			reportResponse.setMapData(mapData);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.DASH_INFO_FETCH_SUCCESS);
			response.setData(reportResponse);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASH_ERROR_FETCH_FAILED, logger, e);
		}
		
		logger.info("[END] [getDashboardData] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getReportData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {

		logger.info("[BEGIN] [getReportData] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			List<Report> reportsData = reportDao.getReportData(reportRequest, userId, timeZone);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.REPORT_INFO_FETCH_SUCCESS);
			response.setData(reportsData);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.REPORT_ERROR_FETCH_FAILED, logger, e);
		}
		
		logger.info("[END] [getReportData] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getCustomerAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [getCustomerAnalyticsData] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			List<DataName> analyticsData = reportDao.getCustomerAnalyticsData(analyticsRequest, userId);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.CUSTOMER_ANALYTICS_FETCH_SUCCESS);
			response.setData(analyticsData);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ANALYTICS_ERROR_FETCH_FAILED, logger, e);
		}
		
		logger.info("[END] [getCustomerAnalyticsData] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getAnalyticParams() throws VEMAppException {
		logger.info("[BEGIN] [getAnalyticParams] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			List<AnalyticParams> analyticParams = reportDao.getAnalyticParams();
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.ANALYTICS_INFO_FETCH_SUCCESS);
			response.setData(analyticParams);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ANALYTICS_ERROR_FETCH_FAILED, logger, e);
		}
		
		logger.info("[END] [getAnalyticParams] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getTempSetpointReport(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getTempSetpointReport] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			JSONObject temperatureReport = reportDao.getTempSetpointReport(reportRequest, userId, timeZone);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.REPORT_INFO_FETCH_SUCCESS);
			response.setData(temperatureReport);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.REPORT_ERROR_FETCH_FAILED, logger, e);
		}
		
		logger.info("[END] [getTempSetpointReport] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getHVACUsageReport(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getHVACUsageReport] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			HVACUsage hvacUsageReport = reportDao.getHVACUsageReport(reportRequest, userId, timeZone);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.REPORT_INFO_FETCH_SUCCESS);
			response.setData(hvacUsageReport);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.REPORT_ERROR_FETCH_FAILED, logger, e);
		}
		
		logger.info("[END] [getHVACUsageReport] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getGroupAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [getGroupAnalyticsData] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			CategoryAnalytics analyticsData = reportDao.getGroupAnalyticsData(analyticsRequest, userId);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.GROUP_ANALYTICS_FETCH_SUCCESS);
			response.setData(analyticsData);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GROUP_ANALYTICS_ERROR_FETCH_FAILED, logger, e);
		}
		
		logger.info("[END] [getGroupAnalyticsData] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getSiteAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [getSiteAnalyticsData] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			CategoryAnalytics analyticsData = reportDao.getSiteAnalyticsData(analyticsRequest, userId);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.SITE_ANALYTICS_FETCH_SUCCESS);
			response.setData(analyticsData);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.SITE_ANALYTICS_ERROR_FETCH_FAILED, logger, e);
		}
		
		logger.info("[END] [getSiteAnalyticsData] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getTrendingAnalytics(AnalyticsRequest analyticsRequest, int userId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getTrendingAnalytics] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		String deviceIds = "";
		
		try {
			
			JSONObject temperatureReport = new JSONObject();
			
			if(!"0".equals(analyticsRequest.getDeviceIds())){

				// checking devices time zones
				if(reportDao.isDevicesInSameTimezone(analyticsRequest.getDeviceIds())){
					temperatureReport = reportDao.getTrendingAnalytics(analyticsRequest, userId, timeZone);
					
					// Preparing success response object
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.ANALYTICS_INFO_FETCH_SUCCESS);
				}else{
					// Preparing success response object
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ANALYTICS_ERR_DIFF_TIME_ZONE_FAILURE);
				}
				
			}else{
				
				if(StringUtils.equals(analyticsRequest.getType(), CommonConstants.GROUP)){
					// Calling sp to get devices by groups
					deviceIds = reportDao.getDeviceIds(analyticsRequest.getType(), analyticsRequest.getGroupIds(), userId);
				}else if(StringUtils.equals(analyticsRequest.getType(), CommonConstants.SITE)){
					// Calling sp to get devices by sites
					deviceIds = reportDao.getDeviceIds(analyticsRequest.getType(), analyticsRequest.getSiteIds(), userId);
				}
				
				String[] devicesArr = (!("null".equalsIgnoreCase(deviceIds) || StringUtils.isEmpty(deviceIds)))?deviceIds.split(","):new String[]{};
				
				/*
				 * Checking wether the devices are there for current selection
				 * then only allowing user to see the report
				 */
				if(devicesArr.length != 0){
					analyticsRequest.setDeviceIds(deviceIds);
				
						// checking devices time zones
						if(reportDao.isDevicesInSameTimezone(deviceIds)){
							temperatureReport = reportDao.getTrendingAnalytics(analyticsRequest, userId, timeZone);
							// Preparing success response object
							response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
							response.setCode(ErrorCodes.ANALYTICS_INFO_FETCH_SUCCESS);
						}else{
							// Preparing success response object
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.ANALYTICS_ERR_DIFF_TIME_ZONE_FAILURE);
						}
					}else{
					// Preparing failure response object for no devices found
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.NO_DEVICES_FAILURE);
				}
			}
			
			response.setData(temperatureReport);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ANALYTICS_ERROR_FETCH_FAILED, logger, e);
		}
		
		logger.info("[END] [getTrendingAnalytics] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getDegradedPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getDegradedPerformData] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			Report degradedData = reportDao.getDegradedPerformData(reportRequest, userId, timeZone);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.DASHBOARD_DEGRADED_PERFORMANCE_FETCH_SUCCESS);
			response.setData(degradedData);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASHBOARD_DEGRADED_PERFORMANCE_FETCH_FAIL, logger, e);
		}
		
		logger.info("[END] [getDegradedPerformData] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getWithinSetpointPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getWithinSetpointPerformData] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			Report withinSetpointData = reportDao.getWithinSetpointPerformData(reportRequest, userId, timeZone);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.DASHBOARD_WITHIN_SETPOINT_PERFORMANCE_FETCH_SUCCESS);
			response.setData(withinSetpointData);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASHBOARD_WITHIN_SETPOINT_PERFORMANCE_FETCH_FAIL, logger, e);
		}
		
		logger.info("[END] [getWithinSetpointPerformData] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getHVACUsagePerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getHVACUsagePerformData] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			Report hhvacPerformanceData = reportDao.getHVACUsagePerformData(reportRequest, userId, timeZone);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.DASHBOARD_HVACUSAGE_PERFORMANCE_FETCH_SUCCESS);
			response.setData(hhvacPerformanceData);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASHBOARD_HVACUSAGE_PERFORMANCE_FETCH_FAIL, logger, e);
		}
		
		logger.info("[END] [getHVACUsagePerformData] [REPORT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getManualChangesPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getManualChangesPerformData] [REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			Report manualData = reportDao.getManualChangesPerformData(reportRequest, userId, timeZone);
			
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.DASHBOARD_MANUAL_PERFORMANCE_FETCH_SUCCESS);
			response.setData(manualData);
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASHBOARD_MANUAL_PERFORMANCE_FETCH_FAIL, logger, e);
		}
		
		logger.info("[END] [getManualChangesPerformData] [REPORT SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response getSitesForGroups(String customerId, String groupIds, GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [getSitesForGroups] [Reports SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store list of groups.
		JSONObject groups;
		
		try {
			
			//Calling the DAO layer getSitesForGroups() method.
			groups = reportDao.getSitesForGroups(customerId, groupIds, userDetails);
			
			/* if groups is not null means the get groups list request is
			 *  success
			 *  else fail.
			 */
			if(groups!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_SITES_BY_GROUP_IDS_SUCCESS);
				response.setData(groups);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_SITES_BY_GROUP_IDS_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GET_SITES_BY_GROUP_IDS_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_SITES_BY_GROUP_IDS_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [getSitesForGroups] [Reports SERVICE LAYER]");
		
		return response;
	}

}
