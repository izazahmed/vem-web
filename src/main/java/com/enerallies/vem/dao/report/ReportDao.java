/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.dao.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.report.AnalyticParams;
import com.enerallies.vem.beans.report.AnalyticsRequest;
import com.enerallies.vem.beans.report.CategoryAnalytics;
import com.enerallies.vem.beans.report.DataName;
import com.enerallies.vem.beans.report.HVACUsage;
import com.enerallies.vem.beans.report.MapData;
import com.enerallies.vem.beans.report.Report;
import com.enerallies.vem.beans.report.ReportRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : ReportDao 
 * 
 * ReportDao: is used to declare all the dao related methods
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

@Service
public interface ReportDao {
    /**
     * setDataSourceRead: This is the method to be used to initialize database resources i.e. connection.
     * @param dataSource
     */
     public void setDataSource(DataSource dataSource);

    /**
	 * getReportData: Is used to get reports data and alerts data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
   public List<Report> getReportData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
   
   /**
	 * getDegradedPerformData: Is used to get degraded performance data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
   public Report getDegradedPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
  
   /**
	 * getWithinSetpointPerformData: Is used to get within set points performance data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
   public Report getWithinSetpointPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
 
   /**
	 * getHVACUsagePerformData: Is used to get HVACUsage performance Data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
   public Report getHVACUsagePerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;

   /**
	 * getManualChangesPerformData: Is used to get manual changes performance Data
	 * 
	 * @param reportRequest
	 * @param userId
	 * @param timeZone
	 * @return
	 * @throws VEMAppException
	 */
   public Report getManualChangesPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;

   /**
    * getCustomerAnalyticsData: It gets customer analytics data
    * 
    * @param analyticsRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public List<DataName> getCustomerAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException;
   
   /**
    * getCriticalIssues: will gets the critical issues
    * 
    * @param reportRequest
    * @param userId
    * @param timeZone
    * @return
    * @throws VEMAppException
    */
   public List<DataName> getCriticalIssues(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
   
   /**
    * getMapData: Gets the map data
    * 
    * @param reportRequest
    * @param userId
    * @param timeZone
    * @return
    * @throws VEMAppException
    */
   public List<MapData> getMapData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
   
   /**
    * getPerformanceData: getting performance data
    * 
    * @param reportRequest
    * @param userId
    * @param timeZone
    * @return
    * @throws VEMAppException
    */
   public List<Report> getPerformanceData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
   
   /**
    * getAnalyticParams: getting analytics params data
    * 
    * @return
    * @throws VEMAppException
    */
   public List<AnalyticParams> getAnalyticParams() throws VEMAppException;
   
   /**
    * getTempSetpointReport: method to get temperature vs setpoint report
    * 
    * @param reportRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public JSONObject getTempSetpointReport(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;
   
   /**
    * getHVACUsageReport : is to get HVAC usage report
    * 
    * @param reportRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public HVACUsage getHVACUsageReport(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException;

   /**
    * getGroupAnalyticsData: It gets group analytics data
    * 
    * @param analyticsRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public CategoryAnalytics getGroupAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException;

   /**
    * getSiteAnalyticsData: It gets site analytics data
    * 
    * @param analyticsRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public CategoryAnalytics getSiteAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException;

   /**
    * getTrendingAnalytics: method to get trending analytics details
    * 
    * @param analyticsRequest
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public JSONObject getTrendingAnalytics(AnalyticsRequest analyticsRequest, int userId, String timeZone) throws VEMAppException;
   
   /**
    * getDeviceIds: is used to get device id's by group id's or site id's
    * 
    * @param typeIds
    * @return
    * @throws VEMAppException
    */
   public String getDeviceIds(String type, String typeIds, int userId) throws VEMAppException;
   
   /**
	  * getSitesForGroups dao is used to get all Sites for requested Groups from database.
	  * 
	  * @param groupIds
	  * @return JSONArray
	  * @throws VEMAppException
	  */
	 public JSONObject getSitesForGroups(String customerId, String groupIds, GetUserResponse userDetails) throws VEMAppException;
	 
	 public Map<Integer, ArrayList<HashMap<String, String>>> getPDFReportUsersData() throws VEMAppException;
   
	 /**
	  * 
	  * isDevicesInSameTimezone : is a method to comparing devices timezones
	  * 
	  * @param deviceIds
	  * @return
	  * @throws VEMAppException
	  */
	 public boolean isDevicesInSameTimezone(String deviceIds) throws VEMAppException;
	   
	 
}
