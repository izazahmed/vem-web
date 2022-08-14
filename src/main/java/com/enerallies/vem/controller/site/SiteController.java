/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.controller.site;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.site.ActivateOrDeActivateSiteRequest;
import com.enerallies.vem.beans.site.AddSiteRequest;
import com.enerallies.vem.beans.site.GetSiteRequest;
import com.enerallies.vem.beans.site.UpdateSiteRequest;
import com.enerallies.vem.service.site.SiteService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : SiteController 
 * 
 * SiteController: is used to handle all the Site related Requests
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

@RestController
@RequestMapping("/api/site")
public class SiteController {
	
	/* Get the logger object*/
	private static final Logger logger=Logger.getLogger(SiteController.class);
	
	/** Constant for user details object **/
	private static final String USER_DETAILS_OBJECT="eaiUserDetails";
	
	/**Instantiated the site service for accessing the service layer.*/
	@Autowired SiteService siteService;
	
	/**
	 * addSite controller is used handle the request & response for 
	 * creating an new Site.
	 *  
	 * @param roleRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	private ResponseEntity<Response> addSite(@RequestBody AddSiteRequest addSiteRequest,HttpSession session){
		
		logger.info("[BEGIN] [addSite] [Site Controller Layer]");
		
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
				if(addSiteRequest != null){
					response = siteService.addSite(addSiteRequest, userDetails.getUserId());
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
		
		logger.info("[END] [addSite] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * updateSite controller is used handle the request & response for 
	 * updating an existing Site.
	 *  
	 * @param updateSiteRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/update", method=RequestMethod.POST)
	private ResponseEntity<Response> updateSite(@RequestBody UpdateSiteRequest updateSiteRequest, HttpSession session){
		
		logger.info("[BEGIN] [updateSite] [Site Controller Layer]");
		
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
				if(updateSiteRequest != null){
					response = siteService.updateSite(updateSiteRequest, userDetails.getUserId());
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
		
		logger.info("[END] [updateSite] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * listSite controller is used handle the request & response for 
	 * Listing all the Sites.
	 * 
	 * @param page
	 * @param id
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/list", method=RequestMethod.GET)
	private ResponseEntity<Response> listSite(@RequestParam(value = "moduleName") String page, @RequestParam(value = "moduleId") Integer id,HttpSession session){
		
		logger.info("[BEGIN] [listSite] [Site Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		logger.debug("[DEBUG] the from Page::::::"+page);
		logger.debug("[DEBUG] the id::::::"+id);
		
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
				 * Catches when the session is valid
				 * And if the customerId is '0' means customerId is invalid
				 * if the customerId is not '0' means customerId is valid.
				 */
				if(!page.isEmpty() && id != 0){
					response = siteService.listSite(page,id,userDetails);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_SITE_CUSTOMER_ID_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":Customer id should not be empty!");
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
		
		logger.info("[END] [listSite] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * loadNewSiteForm controller is used handle the request & response for 
	 * loading the new site form.
	 *  
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/loadForm", method=RequestMethod.GET)
	private ResponseEntity<Response> loadAddSite(@RequestParam(value = "moduleName") String page, @RequestParam(value = "moduleId") Integer id, HttpSession session){
		
		logger.info("[BEGIN] [loadAddSite] [Site Controller Layer]");
		
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
				 * Catches when the session is valid
				 * And if the customerId is '0' means customerId is invalid
				 * if the customerId is not '0' means customerId is valid.
				 */
				if(!page.isEmpty() && id != 0){
					response = siteService.loadAddSite(page, id, userDetails);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_SITE_CUSTOMER_ID_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":State id should not be empty!");
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
		
		logger.info("[END] [loadAddSite] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * activateOrDeActivateSite controller is used handle the request & response for 
	 * updating an status of Site.
	 *  
	 * @param activateOrDeActivateSiteRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/activateOrDeActivateSite", method=RequestMethod.POST)
	private ResponseEntity<Response> activateOrDeActivateSite(@RequestBody ActivateOrDeActivateSiteRequest activateOrDeActivateSiteRequest, HttpSession session){
		
		logger.info("[BEGIN] [activateOrDeActivateSite] [Site Controller Layer]");
		
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
				if(activateOrDeActivateSiteRequest != null){
					response = siteService.activateOrDeActivateSite(activateOrDeActivateSiteRequest, userDetails.getUserId());
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
		
		logger.info("[END] [activateOrDeActivateSite] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getSite controller is used handle the request & response for 
	 * getting the data of existing site.
	 * 
	 * @param siteId
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/get/{siteId}", method=RequestMethod.GET)
	private ResponseEntity<Response> getSite(@PathVariable("siteId") int siteId, @RequestParam(value = "moduleName") String page, @RequestParam(value = "moduleId") Integer id, HttpSession session){
		
		logger.info("[BEGIN] [getSite] [Site Controller Layer]");
		
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
				 * Catches when the session is valid
				 * And if the siteId is '0' means siteId is invalid
				 * if the siteId is not '0' means siteId is valid.
				 */
				if(!page.isEmpty() && id != 0 && siteId != 0){
					response = siteService.getSite(siteId, page, id, userDetails);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_SITE_ID_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":Site id should not be empty!");
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
		
		logger.info("[END] [getSite] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * checkSiteInternalId controller is used handle the request & response for 
	 * checking the site internal id.
	 *  
	 * @param getSiteRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/checkInternalId", method=RequestMethod.POST)
	private ResponseEntity<Response> checkSiteInternalId(@RequestBody GetSiteRequest getSiteRequest, HttpSession session){
		
		logger.info("[BEGIN] [checkSiteInternalId] [Site Controller Layer]");
		
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
				if(getSiteRequest != null){
					response = siteService.checkSiteInternalId(getSiteRequest);
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
		
		logger.info("[END] [checkSiteInternalId] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getCities controller is used handle the request & response for 
	 * getting the all the cities for requested state.
	 *  
	 * @param stateId
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getCities/{stateId}", method=RequestMethod.GET)
	private ResponseEntity<Response> getCities(@PathVariable("stateId") int stateId, HttpSession session){

		logger.info("[BEGIN] [getCities] [Site Controller Layer]");
		
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
				 * Catches when the session is valid
				 * And if the stateId is '0' means stateId is invalid
				 * if the stateId is not '0' means stateId is valid.
				 */
				if(stateId!=0){
					response = siteService.getCities(stateId);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_STATE_ID_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":State id should not be empty!");
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
		
		logger.info("[END] [getCities] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	
	}
	/**
	 * getCitiesSearchText controller is used handle the request & response for 
	 * getting the cities for searched text value.
	 *  
	 * @param citySearchText
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getCitiesSearch/{citySearchText}", method=RequestMethod.GET)
	private ResponseEntity<Response> getCitiesSearchText(@PathVariable("citySearchText") String citySearchText, HttpSession session){

		logger.info("[BEGIN] [getCitiesSearchText] [Site Controller Layer]");
		
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
				 * Catches when the session is valid
				 * And if the stateId is '0' means stateId is invalid
				 * if the stateId is not '0' means stateId is valid.
				 */
				if(citySearchText.length()!=0){
					response = siteService.getCitiesSearch(citySearchText);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_STATE_ID_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":State id should not be empty!");
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
		
		logger.info("[END] [getCities] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	
	}
	/**
	 * deleteSite controller is used handle the request & response for 
	 * deleting the existing site.
	 *  
	 * @param siteId
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/delete/{siteId}", method=RequestMethod.GET)
	private ResponseEntity<Response> deleteSite(@PathVariable("siteId") int siteId, HttpSession session){

		logger.info("[BEGIN] [deleteSite] [Site Controller Layer]");
		
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
				 * Catches when the session is valid
				 * And if the siteId is '0' means siteId is invalid
				 * if the siteId is not '0' means siteId is valid.
				 */
				if(siteId!=0){
					response = siteService.deleteSite(siteId, userDetails.getUserId());
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_SITE_ID_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":Site id should not be empty!");
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
		
		logger.info("[END] [deleteSite] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	
	}
	
	/**
	 * getCustomerId controller is used handle the request & response for 
	 * getting the customer id of the logged in user.
	 *  
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getCustomerId", method=RequestMethod.GET)
	private ResponseEntity<Response> getCustomerId(HttpSession session){

		logger.info("[BEGIN] [getCustomerId] [Site Controller Layer]");
		
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
			String customerId = "0";
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid.
				 * And getting the customer ids from session for the 
				 * logged in user.
				 */
				String customers=userDetails.getCustomers();
				logger.debug("[DEBUG] :: the customerIds are::"+customers);
				if(customers!=null && !customers.isEmpty()){
					String[] customerids=customers.split(",");
					if(customerids.length>0){
						customerId = customerids[0];
					}
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
			logger.debug("[DEBUG] :: the customerId is::"+customerId);
			if("0".equals(customerId)){
				
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_CUSTOMER_ID_FAILED);
				JSONObject obj=new JSONObject();
				obj.put("customerId", customerId);
				response.setData(obj);
				
			}else{
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_CUSTOMER_ID_SUCCESS);
				JSONObject obj=new JSONObject();
				obj.put("customerId", customerId);
				response.setData(obj);
			}
			
			
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [getCustomerId] [Site Controller Layer]");
		
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
	private ResponseEntity<Response> getSitesForGroups(@RequestParam(value = "groupIds") String groupIds, HttpSession session){
		
		logger.info("[BEGIN] [getSitesForGroups] [Site Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		logger.debug("[DEBUG] the groupIds::::::"+groupIds);
		
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
				 * Catches when the session is valid
				 * And if the groupIds is '' means groupIds is invalid
				 * if the groupIds is not '' means groupIds is valid.
				 */
				if(!groupIds.isEmpty()){
					response = siteService.getSitesForGroups(groupIds, userDetails);
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
		
		logger.info("[END] [getSitesForGroups] [Site Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getTimeZone controller is used handle the request & response for 
	 * getting the time zone details based on the address
	 * 
	 * @param address
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getTimeZone", method=RequestMethod.GET)
	private ResponseEntity<Response> getTimeZone(@RequestParam(value = "address") String address, HttpSession session){
		
		logger.info("[BEGIN] [getTimeZone] [Site Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		// Declaring and initializing the status HttpStatus
		HttpStatus status = HttpStatus.OK;
		
		logger.debug("[DEBUG] the Address::::::"+address);
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		JSONObject timeZoneJson  = null;
		try {			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){				
				/*
				 * Catches when the session is valid
				 * And if the address is '' means address is invalid
				 * if the address is not '' means address is valid.
				 */
				if(!address.isEmpty()){
					// Calling CommonUtility getTimeZone method to get TimeZone details
					timeZoneJson = CommonUtility.getTimeZone(address);
					// Setting the status to response
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					// Setting the code to response
					response.setCode(ErrorCodes.GET_TIMEZONE_DETAILS_SUCCESS);
					// Setting the Data to response
					response.setData(timeZoneJson);
					status = HttpStatus.OK;
				}
				if(address.isEmpty() || timeZoneJson == null || timeZoneJson.size() == 0){
					// Setting the status to response
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					// Setting the code to response
					response.setCode(ErrorCodes.ERROR_TIMEZONE_EMPTY);
					// Setting the Data to response
					response.setData(CommonConstants.ERROR_OCCURRED+":Address should not be empty!");
				}				 
			}else{				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				// Setting the status to response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				// Setting the code to response
				response.setCode(ErrorCodes.INVALID_SESSION);				
			}			
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}		
		logger.info("[END] [getSitesForGroups] [Site Controller Layer]");		
		return new ResponseEntity<>(response, status);
	}	
	
	/**
	 * getGeoCodeData controller is used handle the request & response for 
	 * getting the Geo Code data details based on the address
	 * 
	 * @param address
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value = "/getGeoCodeData", method = RequestMethod.GET)
	private ResponseEntity<Response> getGeoCodeData(@RequestParam(value = "zipCode") String zipCode,
			HttpSession session) {

		logger.info("[BEGIN] [getGeoCodeData] [Site Controller Layer]");

		/*
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		// Declaring and initializing the status HttpStatus
		HttpStatus status = HttpStatus.OK;

		logger.debug("[DEBUG] the Address::::::" + zipCode);

		/*
		 * Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		try {
			/*
			 * checking for if session is valid or not
			 */
			if (userDetails != null) {
				/*
				 * Catches when the session is valid And if the address is ''
				 * means address is invalid if the address is not '' means
				 * address is valid.
				 */
				if (!zipCode.isEmpty()) {
					// Calling Site Service Impl getTimeZone method to get
					// TimeZone details
					response = siteService.getGeoCodeData(zipCode);
				}
				if (zipCode.isEmpty()) {
					// Setting the status to response
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					// Setting the code to response
					response.setCode(ErrorCodes.GET_GEOCODEDATA_DETAILS_FAILED);
					// Setting the Data to response
					response.setData(CommonConstants.ERROR_OCCURRED + ":ZipCode should not be empty!");
				}
			} else {
				/*
				 * Catches when the request is unauthorized and Preparing
				 * failure response
				 */
				status = HttpStatus.UNAUTHORIZED;
				// Setting the status to response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				// Setting the code to response
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("", e);
		}
		logger.info("[END] [getGeoCodeData] [Site Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
}
