/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.service.site;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.site.ActivateOrDeActivateSiteRequest;
import com.enerallies.vem.beans.site.AddSiteRequest;
import com.enerallies.vem.beans.site.GetSiteRequest;
import com.enerallies.vem.beans.site.UpdateSiteRequest;
import com.enerallies.vem.beans.upload.FileUploadResponse;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : SiteService 
 * 
 * RoleService service is used to serve the all role related operations.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
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
 * 16-09-2016		Goush Basha		    Added getCities() method(Sprint-3).
 * 19-09-2016		Goush Basha		    Added deleteSite() method(Sprint-3).
 * 12-10-2016		Goush Basha			Changed the listSite() method to accept the group id.
 * 
 */

@Component
public interface SiteService {

	/**
	 * addSite service is used to create the new Site.
	 * 
	 * @param addSiteRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response addSite(AddSiteRequest addSiteRequest,int userId) throws VEMAppException;
	
	/**
	 * updateSite service is used to update the existing Site.
	 * 
	 * @param updateSiteRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response updateSite(UpdateSiteRequest updateSiteRequest, int userId) throws VEMAppException;
	
	/**
	 * listSite service is used to list all existing Sites.
	 * 
	 * @param customerId
	 * @param groupId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response listSite(String page,Integer id,GetUserResponse userDetails) throws VEMAppException;
	
	/**
	 * loadAddSite service is used to fill the add site form drop downs with data.
	 * 
	 * @param customerId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response loadAddSite(String page,Integer id,GetUserResponse userDetails) throws VEMAppException;
	
	/**
	 * activateOrDeActivateSite service is used to update the status of Site.
	 * 
	 * @param activateOrDeActivateSiteRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response activateOrDeActivateSite(ActivateOrDeActivateSiteRequest activateOrDeActivateSiteRequest,int userId) throws VEMAppException;
	
	/**
	 * getSite service is used to get the data of existing Site.
	 * 
	 * @param siteId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getSite(Integer siteId, String page,Integer id,GetUserResponse userDetails) throws VEMAppException;
	
	/**
	 * checkSiteInternalId service is used to check the site internal id.
	 * 
	 * @param getSiteRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response checkSiteInternalId(GetSiteRequest getSiteRequest) throws VEMAppException;
	
	/**
	 * getCities service is used to get all the cities for the requested
	 * state.
	 * 
	 * @param stateId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getCities(int stateId) throws VEMAppException;
	/**
	 * getCitiesSearch service is used to get all the cities for the requested
	 * state.
	 * 
	 * @param stateId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getCitiesSearch(String searchText) throws VEMAppException;
	/**
	 * deleteSite service is used to delete the requested site.
	 * 
	 * @param siteId
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response deleteSite(int siteId, int userId) throws VEMAppException;
	
	/**
	 * getSitesForGroups service is used to list site for requested groups.
	 * 
	 * @param groupIds
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getSitesForGroups(String groupIds, GetUserResponse userDetails) throws VEMAppException;
	
	public Response uploadSiteTemplate(FileUploadResponse fileUploadResponse) throws VEMAppException;
	
	public Response getGeoCodeData(String zipCode) throws VEMAppException;

}
