/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.service.activity;

import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.audit.AddManualLogRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.site.AddSiteRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : ActivityLogService 
 * 
 * ActivityLogService service is used to serve the all Activity related operations.
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

@Component
public interface ActivityLogService {
	
	/**
	 * getActivityLogData service is used to get the activity log data.
	 * 
	 * @param serviceId
	 * @param specificId
	 * @param userId
	 * @param timezone
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getActivityLogData(int serviceId, int specificId, String startDate,
			String endDate, int userId, String timezone) throws VEMAppException;

	
	
	/**
	 * getActivityLogData service is used to get the activity log data.
	 * 
	 * @param serviceId
	 * @param specificId
	 * @param userId
	 * @param timezone
	 * @param recordsPerPage
	 * @param currentPage
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getActivityLogPaginationData(int serviceId, int specificId, String startDate,
			String endDate, int userId, String timezone, int currentPage, int recordsPerPage,
			String action,String module, String description) throws VEMAppException;
	
	/**
	 * addSite service is used to create the new Site.
	 * 
	 * @param addManualLogRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response createManualActivityLog(AddManualLogRequest addManualLogRequest, int userId) throws VEMAppException;
	
	/**
	 * getFilterData service is used to get data for drop filters.
	 * 
	 * @param userId
	 * @param isSuperUser
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getFilterData(Integer userId,int isSuperUser) throws VEMAppException;

}
