/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.service.report;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.report.AnalyticsRequest;
import com.enerallies.vem.beans.report.Report;
import com.enerallies.vem.beans.report.ReportRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : ReportService 
 * 
 * ReportService: is used to declare all the dash board related service
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
public interface ReportService {

	/**
	 * getDashboardData: Is used to get reports data and alerts data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
   public Response getDashboardData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
   
   /**
    * getReportData:  Is used to get the reports data
    * 
    * @param reportRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public Response getReportData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;

   /**
    * getCustomerAnalyticsData: It gets customer analytics data
    * 
    * @param analyticsRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public Response getCustomerAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException;

   /**
    * getAnalyticParams: getting analytics params data
    * 
    * @return
    * @throws VEMAppException
    */
   public Response getAnalyticParams() throws VEMAppException;
   
   /**
    * getTempSetpointReport: method to get temperature vs setpoint report
    * 
    * @param reportRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public Response getTempSetpointReport(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
   
   /**
    * getHVACUsageReport : is to get HVAC usage report
    * 
    * @param reportRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public Response getHVACUsageReport(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
   
   /**
    * getGroupAnalyticsData: It gets group analytics data
    * 
    * @param analyticsRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public Response getGroupAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException;

   /**
    * getSiteAnalyticsData: It gets site analytics data
    * 
    * @param analyticsRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public Response getSiteAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException;

   /**
    * getTrendingAnalytics: method to get trending analytics details
    * 
    * @param analyticsRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public Response getTrendingAnalytics(AnalyticsRequest analyticsRequest, int userId, String timeZone) throws VEMAppException;

   /**
	 * getDegradedPerformData: Is used to get degraded performance data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
  public Response getDegradedPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
 
  /**
	 * getWithinSetpointPerformData: Is used to get within set points performance data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
  public Response getWithinSetpointPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;

  /**
	 * getHVACUsagePerformData: Is used to get HVACUsage performance Data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
  public Response getHVACUsagePerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;

  /**
	 * getManualChangesPerformData: Is used to get manual changes performance Data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
  public Response getManualChangesPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;

  /**
	 * getSitesForGroups service is used to list site for requested groups.
	 * 
	 * @param customerId
	 * @param groupIds
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getSitesForGroups(String customerId, String groupIds, GetUserResponse userDetails) throws VEMAppException;

}
