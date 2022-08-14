/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.dao.site;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.site.ActivateOrDeActivateSiteRequest;
import com.enerallies.vem.beans.site.AddSiteRequest;
import com.enerallies.vem.beans.site.GetSiteRequest;
import com.enerallies.vem.beans.site.UpdateSiteRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : SiteDao 
 * 
 * SiteDao dao is used to serve all the database level operations related to site.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        31-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 31-08-2016		Goush Basha		    File Created & addSite() method(Sprint-3).
 * 06-09-2016		Goush Basha		    Added updateSite() method(Sprint-3).
 * 07-09-2016		Goush Basha		    Added listSite() method(Sprint-3).
 * 07-09-2016		Goush Basha		    Added loadAddSite() method(Sprint-3).
 * 07-09-2016		Goush Basha		    Added activateOrDeActivateSite() method(Sprint-3).
 * 14-09-2016		Goush Basha		    Added getSite() method(Sprint-3).
 * 15-09-2016		Goush Basha		    Added checkSiteInternalId() method(Sprint-3).
 * 19-09-2016		Goush Basha		    Added deleteSite() method(Sprint-3).
 * 12-10-2016		Goush Basha			Changed the listSite() method to accept the group id.
 */

@Repository
public interface SiteDao {
	
	 /**
	  * This is the method to be used to initialize database resources i.e. connection.
	  * 
	  * @param dataSource
	  */
	 public void setDataSource(DataSource dataSource);
	 
	 /**
	  * addSite dao is used to Create a new Site in database.
	  * 
	  * @param addSiteRequest
	  * @param userId
	  * @return int
	  * @throws VEMAppException
	  */
	 public int addSite(AddSiteRequest addSiteRequest, int userId) throws VEMAppException;
	 
	 /**
	  * updateSite dao is used to update an existing Site in database.
	  * 
	  * @param updateSiteRequest
	  * @param userId
	  * @return int
	  * @throws VEMAppException
	  */
	 public int updateSite(UpdateSiteRequest updateSiteRequest, int userId) throws VEMAppException;

	 /**
	  * listSite dao is used to get all the existing Sites from database.
	  * 
	  * @param customerId
	  * @param groupId
	  * @return JSONObject
	  * @throws VEMAppException
	  */
	 public JSONObject listSite(String page, Integer id, GetUserResponse userDetails) throws VEMAppException;

	 /**
	  * loadAddSite dao is used to get all drop down values from database.
	  * 
	  * @param customerId
	  * @return JSONObject
	  * @throws VEMAppException
	  */
	 public JSONObject loadAddSite(String page,Integer id,GetUserResponse userDetails) throws VEMAppException;
	 
	 /**
	  * activateOrDeActivateSite dao is used to update Site status in database.
	  * 
	  * @param activateOrDeActivateSiteRequest
	  * @param userId
	  * @return int
	  * @throws VEMAppException
	  */
	 public int activateOrDeActivateSite(ActivateOrDeActivateSiteRequest activateOrDeActivateSiteRequest,int userId) throws VEMAppException;
	 
	 /**
	  * getSite dao is used to get data of the requested site from database.
	  * 
	  * @param siteId
	  * @return JSONObject
	  * @throws VEMAppException
	  */
	 public JSONObject getSite(Integer siteId, String page,Integer id,GetUserResponse userDetails) throws VEMAppException;
	 
	 /**
	  * checkSiteInternalId dao is used to check the site internal id
	  * for an existing Site in database.
	  * 
	  * @param getSiteRequest
	  * @return int
	  * @throws VEMAppException
	  */
	 public int checkSiteInternalId(GetSiteRequest getSiteRequest) throws VEMAppException;
	 
	 /**
	  * deleteSite dao is used to delete the requested site
	  * from database.
	  * 
	  * @param siteId
	  * @param userId
	  * @return int
	  * @throws VEMAppException
	  */
	 public int deleteSite(int siteId, int userId) throws VEMAppException;
	 
	 /**
	  * getSitesForGroups dao is used to get all Sites for requested Groups from database.
	  * 
	  * @param groupIds
	  * @return JSONArray
	  * @throws VEMAppException
	  */
	 public JSONObject getSitesForGroups(String groupIds, GetUserResponse userDetails) throws VEMAppException;
	 
	public String getGroupsByCustomer(int customerId) throws VEMAppException;

	public JSONObject addBulkUploadSites(UpdateSiteRequest addSiteRequest, int userId) throws VEMAppException;

	public String getStateCityId(String siteStateName, String siteCityName) throws VEMAppException;

	public JSONObject updateBulkUploadSites(UpdateSiteRequest addSiteRequest, int userId) throws VEMAppException;

	public JSONObject updateBulkUploadProgress(String sheetName, int currentCounter, int tatalRecords, int failedCount, long userId, String status) throws VEMAppException;
		
	public int validateSiteNameIsAlreadyExists(String action, String siteName,  String customerId, String siteId) throws VEMAppException;
	
	public JSONArray getGeoCodeData(String zipCode)  throws VEMAppException;
	
	public JSONObject insertGeoCodeData(String stateName, String stateCode, String cityName, String zipCode, String latitude, String longitude, String timezone) throws VEMAppException;

	public boolean isHvacUnitDuplicate(long siteId, String unit) throws VEMAppException;

}
