/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.dao.activity;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.springframework.stereotype.Repository;

import com.enerallies.vem.beans.audit.AddManualLogRequest;
import com.enerallies.vem.beans.site.AddSiteRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : ActivityLogDao 
 * 
 * ActivityLogDao dao is used to serve all the database level operations related to Activity log.
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

@Repository
public interface ActivityLogDao {
	
	/**
	  * This is the method to be used to initialize database resources i.e. connection.
	  * 
	  * @param dataSource
	  */
	 public void setDataSource(DataSource dataSource);
	 
	/**
	  * getActivityLogData dao is used to get The Activity Log data from database.
	  * 
	  * @param serviceId
	  * @param specificId
	  * @param userId
	  * @return JSONArray
	  * @throws VEMAppException
	  */
	 public JSONArray getActivityLogData(int serviceId, int specificId,String startDate,
			 String endDate,int userId, String timeZone) throws VEMAppException;
	 

		/**
	  * getActivityLogPaginationData dao is used to get The Activity Log data from database with pagination.
	  * 
	  * @param serviceId
	  * @param specificId
	  * @param userId
	  * @return JSONArray
	  * @throws VEMAppException
	  */
	 public JSONArray getActivityLogPaginationData(int serviceId, int specificId,String startDate,
			 String endDate,int userId, String timeZone,int currentPage, int recordsPerPage,
			 String action, String module, String description) throws VEMAppException;
	 
	 /**
	 * getFilterData dao is used to get filter data for drop down component in activity log.
	 * @param userId
	 * @param isSuperUser
	 * @return JSONArray
	 * @throws VEMAppException
	 */
	public JSONArray getFilterData(int userId,int isSuperUser) throws VEMAppException;
	
		/**
	  * getActivityTotalRecords dao is used to get The Activity Log data from database.
	  * 
	  * @param serviceId
	  * @param specificId
	  * @param userId
	  * @return int
	  * @throws VEMAppException
	  */
	 public int getActivityTotalRecords(int serviceId, int specificId,String startDate,
			 String endDate,int userId, String timeZone,String action, String module, String description) throws VEMAppException;
	 
	 
	 /**
	  * addSite dao is used to Create a new Site in database.
	  * 
	  * @param addManualLogRequest
	  * @param userId
	  * @return int
	  * @throws VEMAppException
	  */
	 public int createManualActivityLog(AddManualLogRequest addManualLogRequest, int userId) throws VEMAppException;
	 
	 /**
	  * getManualActivityLogData dao is used to get The Manual Activity Log data from database.
	  * 
	  * @param serviceId
	  * @param specificId
	  * @param days
	  * @param timeZone
	  * @return JSONArray
	  * @throws VEMAppException
	  */
	 public JSONArray getManualActivityLogData(int serviceId, int specificId,int days,
			 String timeZone) throws VEMAppException;
}
