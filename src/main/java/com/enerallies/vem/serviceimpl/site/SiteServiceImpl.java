/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.serviceimpl.site;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enerallies.vem.beans.admin.BulkUploadResponse;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.beans.customers.CustomersRequestBean;
import com.enerallies.vem.beans.group.GroupRequest;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.site.ActivateOrDeActivateSiteRequest;
import com.enerallies.vem.beans.site.AddSiteRequest;
import com.enerallies.vem.beans.site.GetSiteRequest;
import com.enerallies.vem.beans.site.RTURequest;
import com.enerallies.vem.beans.site.ThermostatRequest;
import com.enerallies.vem.beans.site.UpdateSiteRequest;
import com.enerallies.vem.beans.upload.FileUploadResponse;
import com.enerallies.vem.dao.LookUpDao;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.dao.common.CommonDao;
import com.enerallies.vem.dao.customers.CustomersDAO;
import com.enerallies.vem.dao.group.GroupDAO;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.dao.site.SiteDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.iot.ThingService;
import com.enerallies.vem.service.site.SiteService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.HttpRestClient;
import com.enerallies.vem.util.TimeFormatValidator;

/**
 * File Name : SiteServiceImpl 
 * 
 * SiteServiceImpl:Its an implementation class for SiteService service interface.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies ? Umang Gupta (ugupta@ctepl.com)
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

@Service("siteService")
@Transactional
public class SiteServiceImpl implements SiteService{

	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(SiteServiceImpl.class);
	
	/**instantiating the site dao for accessing the dao layer.*/
	@Autowired SiteDao siteDao;
	@Autowired GroupDAO groupDAO;
	
	@Autowired CommonDao commonDao;
	@Autowired CustomersDAO customerDao;
	@Autowired AuditDAO auditDAO;
	
	/**instantiating the lookUp dao for accessing the dao layer.*/
	@Autowired LookUpDao lookUpDao;
	
	@Autowired ThingService thingService;
	
	@Autowired IoTDao ioTDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public Response addSite(AddSiteRequest addSiteRequest, int userId)
			throws VEMAppException {
		
		logger.info("[BEGIN] [addSite] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		
		JSONObject siteObject = new JSONObject();
		
		/*
		 * Variables for lat and lang.
		 */
		String lat = "0";
		String lang = "0";
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(addSiteRequest);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				if(siteDao.validateSiteNameIsAlreadyExists("Insert", addSiteRequest.getSiteName(), Integer.toString(addSiteRequest.getCustomerId()), null) == 0) {
					  /*
				     * Get the longitute and latitude values based on 
				     * Address
				     */
				    
				    StringBuilder address = new StringBuilder(addSiteRequest.getSiteAddLine1().trim());
				    if(!addSiteRequest.getSiteAddLine2().isEmpty()){
				    	address.append(", "+addSiteRequest.getSiteAddLine2().trim());
				    }
				    
				    address.append(", "+addSiteRequest.getSiteCityName());
				    address.append(", "+addSiteRequest.getSiteStateName());
				    address.append(" "+addSiteRequest.getSiteZipCode()+", USA");
				    
				    logger.debug("address::::"+address+"  URLEncoder:"+URLEncoder.encode(address.toString(), "UTF-8"));
				    
				    JSONObject latLanObject = CommonUtility.getLatitudeAndLangitute(URLEncoder.encode(address.toString(), "UTF-8"));
				    
				    if(latLanObject != null && latLanObject.get("lat") != null){
				    	lat = latLanObject.get("lat").toString();
				    	lang = latLanObject.get("lng").toString();
				    } else {
				    	response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.ADD_SITE_LAT_LON_FAILED);
						response.setData(CommonConstants.ERROR_OCCURRED+":Invalid address");
				    }
				    addSiteRequest.setLangitude(lang);
				    addSiteRequest.setLatitude(lat);
				    if (addSiteRequest.getLangitude().equalsIgnoreCase("0") && addSiteRequest.getLatitude().equalsIgnoreCase("0")) {
				    	response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.ADD_SITE_LAT_LON_FAILED);
						response.setData(CommonConstants.ERROR_OCCURRED+":Invalid address");
				    } else {
						//Catches when all the server or bean level validations are true.
						int status = siteDao.addSite(addSiteRequest, userId);
						
						/* if status is 1 or greater means the add site request is
						 *  success
						 *  else fail.
						 */
						if(status >= 1){
							siteObject.put("siteId", status);
							response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
							response.setCode(ErrorCodes.ADD_SITE_SUCCESS);
							response.setData(siteObject);
						}else{
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.ADD_SITE_FAILED);
							response.setData(CommonConstants.ERROR_OCCURRED+":Site has not created at DB Side.");
						}
				    }
				} else {
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.SITE_VALIDATE_SITE_NAME_FAILED);
					response.setData(CommonConstants.ERROR_OCCURRED+":Site Name Already Exists");
				}
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ADD_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [addSite] [SiteService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response updateSite(UpdateSiteRequest updateSiteRequest, int userId)
			throws VEMAppException {
		
		logger.info("[BEGIN] [updateSite] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		
		/*
		 * Variables for lat and lang.
		 */
		String lat = "0";
		String lang = "0";
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(updateSiteRequest);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{				
				if(siteDao.validateSiteNameIsAlreadyExists("Update", updateSiteRequest.getSiteName(), Integer.toString(updateSiteRequest.getCustomerId()), Integer.toString(updateSiteRequest.getSiteId())) == 0) {
					
					/*
				     * Get the longitute and latitude values based on 
				     * Address
				     */
				    
				    StringBuilder address = new StringBuilder(updateSiteRequest.getSiteAddLine1().trim());
				    if(!updateSiteRequest.getSiteAddLine2().isEmpty()){
				    	address.append(", "+updateSiteRequest.getSiteAddLine2().trim());
				    }
				    
				    address.append(", "+updateSiteRequest.getSiteCityName());
				    address.append(", "+updateSiteRequest.getSiteStateName());
				    address.append(" "+updateSiteRequest.getSiteZipCode()+", USA");
				    
				    logger.debug("address::::"+address+"  URLEncoder:"+URLEncoder.encode(address.toString(), "UTF-8"));
				    
				    JSONObject latLanObject = CommonUtility.getLatitudeAndLangitute(URLEncoder.encode(address.toString(), "UTF-8"));
				    
				    if(latLanObject != null && latLanObject.get("lat") != null){
				    	lat = latLanObject.get("lat").toString();
				    	lang = latLanObject.get("lng").toString();
				    }
				    updateSiteRequest.setLangitude(lang);
				    updateSiteRequest.setLatitude(lat);
				    
				    if (updateSiteRequest.getLangitude().equalsIgnoreCase("0") && updateSiteRequest.getLatitude().equalsIgnoreCase("0")) {
				    	response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.ADD_SITE_LAT_LON_FAILED);
						response.setData(CommonConstants.ERROR_OCCURRED+":Invalid address");
				    } else {
				    	//Catches when all the server or bean level validations are true.
						int status = siteDao.updateSite(updateSiteRequest, userId);
						
						/* if status is 1 or greater means the update site request is
						 *  success
						 *  else fail.
						 */
						if(status >= 1){
							response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
							response.setCode(ErrorCodes.UPDATE_SITE_SUCCESS);
							response.setData(status);
						}else{
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.UPDATE_SITE_FAILED);
							response.setData(CommonConstants.ERROR_OCCURRED+":Site has not updated at DB Side successfully.");
						}
				    }
					
				} else {
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.SITE_VALIDATE_SITE_NAME_FAILED);
					response.setData(CommonConstants.ERROR_OCCURRED+":Site Name Already Exists");
				}
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.UPDATE_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [updateSite] [SiteService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response listSite(String page, Integer id, GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [listSite] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store list of sites.
		JSONObject sites;
		
		try {
			
			//Calling the DAO layer listSite() method.
			sites = siteDao.listSite(page, id, userDetails);
			
			/* if sites is not null means the get sites list request is
			 *  success
			 *  else fail.
			 */
			if(sites!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.LIST_SITE_SUCCESS);
				response.setData(sites);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.LIST_SITE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+": No Records Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.LIST_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [listSite] [SiteService SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response loadAddSite(String page,Integer id,GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [loadAddSite] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store list of all the drop down values.
		JSONObject loadObject;
		
		try {
			
			//Calling the DAO layer loadAddSite() method.
			loadObject = siteDao.loadAddSite(page, id, userDetails);
			
			/* if loadObject is not null means the get all the drop down values
			 * request is success else fail.
			 */
			if(loadObject!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.LOAD_ADD_SITE_SUCCESS);
				response.setData(loadObject);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.LOAD_ADD_SITE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.LOAD_ADD_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOAD_ADD_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [loadAddSite] [SiteService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response activateOrDeActivateSite(ActivateOrDeActivateSiteRequest activateOrDeActivateSiteRequest,int userId)
			throws VEMAppException {
		
		logger.info("[BEGIN] [activateOrDeActivateSite] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(activateOrDeActivateSiteRequest);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				//Catches when all the server or bean level validations are true.
				int status = siteDao.activateOrDeActivateSite(activateOrDeActivateSiteRequest, userId);
				
				/* if status is 1 or greater means the site status updation  request is
				 *  success
				 *  else fail.
				 */
				if(status >= 1){
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.UPDATE_SITE_STATUS_SUCCESS);
					response.setData(status);
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.UPDATE_SITE_STATUS_FAILED);
					response.setData(CommonConstants.ERROR_OCCURRED+":Site updation has not done at DB Side.");
				}
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.UPDATE_SITE_STATUS_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_SITE_STATUS_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [activateOrDeActivateSite] [SiteService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response getSite(Integer siteId, String page,Integer id,GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [getSite] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store the site data.
		JSONObject siteObject;
		
		try {
			
			//Calling the DAO layer getSite() method.
			siteObject = siteDao.getSite(siteId, page, id, userDetails);
			
			/* if siteObject is not null means the get site data
			 * request is success else fail.
			 */
			if(siteObject!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_SITE_SUCCESS);
				response.setData(siteObject);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_SITE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No data found for given site!");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GET_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [getSite] [SiteService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response checkSiteInternalId(GetSiteRequest getSiteRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [checkSiteInternalId] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(getSiteRequest);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				//Catches when all the server or bean level validations are true.
				int status = siteDao.checkSiteInternalId(getSiteRequest);
				
				/*
				 * forming the success response with return data.
				 * If status is 0 means duplicate id not found
				 * else there is a duplicate id found.
				 */
				if(status == 0){
					response.setCode(ErrorCodes.CHECK_SITE_INTERNAL_ID_SUCCESS);
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setData(status);
				}else{
					response.setCode(ErrorCodes.ERROR_DUPLICATE_SITE_INTERNAL_ID);
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setData(status);
				}
				
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.CHECK_SITE_INTERNAL_ID_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CHECK_SITE_INTERNAL_ID_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [checkSiteInternalId] [SiteService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response getCities(int stateId) throws VEMAppException {
		
		logger.info("[BEGIN] [getCities] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store the cities data.
		JSONArray citiesArray;
		
		try {
			
			//Calling the DAO layer getCities() method.
			citiesArray = lookUpDao.getCities(stateId);
			
			/* if citiesArray is not null means the get cities
			 * request is success else fail.
			 */
			if(citiesArray!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_CITIES_SUCCESS);
				response.setData(citiesArray);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_CITIES_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No data found for given state!");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GET_CITIES_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_CITIES_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [getCities] [SiteService SERVICE LAYER]");
		
		return response;
	}
	@Override
	public Response getCitiesSearch(String searchText) throws VEMAppException {
		
		logger.info("[BEGIN] [getCitiesSearch] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store the cities data.
		JSONArray citiesArray;
		
		try {
			
			//Calling the DAO layer getCities() method.
			citiesArray = lookUpDao.getCitiesSearch(searchText);
			
			/* if citiesArray is not null means the get cities
			 * request is success else fail.
			 */
			if(citiesArray!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_CITIES_SUCCESS);
				response.setData(citiesArray);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_CITIES_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No data found for given state!");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GET_CITIES_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_CITIES_FAILED, logger, e);
		}
		
		logger.info("[END] [getCitiesSearch] [SiteService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response deleteSite(int siteId, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [deleteSite] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		
		/* 
		 * updateflag used to capture the updated row count.
		 */
		int updateFlag = 0;
		
		try {
			/*
			 * calling the deleteSite method for logical deletion
			 */
			updateFlag = siteDao.deleteSite(siteId, userId);
			
			/*
			 * If the updateFlag > 0 then the delete site requested succeeded
			 * Otherwise failed.
			 */
			if(updateFlag > 0){
				
				// Calling deleteDevice 
				List<ThingResponse> thingList = ioTDao.getThingListBySite(siteId);
				for(ThingResponse thingResponse : thingList) {
					Response deleteDeviceResponse = thingService.deleteDevice(thingResponse.getDeviceId(),userId);
					logger.info("[deleteSite] [SERVICE LAYER] [DeleteDeviceResponse] " +deleteDeviceResponse);
				}
				
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.DELETE_SITE_SUCCESS);
				response.setData("1");
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.DELETE_SITE_FAILED);
				response.setData("0");
			}
			
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.DELETE_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DELETE_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [deleteSite] [SiteService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response getSitesForGroups(String groupIds, GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [getSitesForGroups] [SiteService SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store list of groups.
		JSONObject groups;
		
		try {
			
			//Calling the DAO layer getSitesForGroups() method.
			groups = siteDao.getSitesForGroups(groupIds, userDetails);
			
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
		
		logger.info("[BEGIN] [getSitesForGroups] [SiteService SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response uploadSiteTemplate(FileUploadResponse fileUploadResponse) throws VEMAppException {
		
		logger.info("SiteServiceImpl UploadSiteTemplate start");

		Map<Integer,String> excelHeaderValues = new HashMap<>();
	    BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
		Response response = new Response();
		
		// Creating userDetails object to store user details
	    File file = new File(fileUploadResponse.getFileLocation());
	    try(FileInputStream fis = new FileInputStream(file)) {	  
	    	
			Workbook workbook = null;
			if(fileUploadResponse.getFileLocation().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(fis);
			}else if(fileUploadResponse.getFileLocation().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(fis);
			}
			
			CellStyle cellStyleForFailed = createCellStyleForFailed(workbook);
			CellStyle cellStyleForSuccess = createCellStyleForSuccess(workbook);  
			
			int numberOfSheets = workbook.getNumberOfSheets(); 
			int totalRows = 0;
			int failedCount = 0;
			for (int i = 0; i < numberOfSheets; i++) {
				workbook.setActiveSheet(0);
				Sheet sheet = workbook.getSheetAt(i);
				sheet.setColumnWidth(21, 20000);
				int rowCounter = 0;
				int rowEmptyCounter = 0;
				Row row = null;
				Map<String,Long> siteIds = new HashMap<>();
				int cellType = 0;
				
				row = sheet.getRow(i);
				int maxNumOfCells = getExcelHeaderNames(excelHeaderValues, i, sheet, row, cellType);
				
				totalRows = getTotalRowNotEmpty(sheet, excelHeaderValues, maxNumOfCells);
				siteDao.updateBulkUploadProgress(fileUploadResponse.getFileName(), 0, totalRows, 0, fileUploadResponse.getUserId(), "inprogress");
				
				AuditRequest auditRequest = new AuditRequest();
				auditRequest.setUserId(0);
				auditRequest.setUserAction("Started");
				auditRequest.setLocation("");
				auditRequest.setServiceId("13");
				auditRequest.setDescription("Site bulk import started");
				auditRequest.setServiceSpecificId(fileUploadResponse.getCustomerId());
				
				auditDAO.insertAuditLog(auditRequest);
				
				CustomersRequestBean customerBean = new CustomersRequestBean();
				customerBean.setCustomerId(String.valueOf(fileUploadResponse.getCustomerId()));
				customerBean.setUserId(String.valueOf(fileUploadResponse.getUserId()));
				customerBean.setIsSuperAdmin(fileUploadResponse.getIsSuper());
				Response customerDetails = customerDao.getCustomerProfile(customerBean);
				
				CellStyle style = createCellStyle(workbook);
				while (i <= sheet.getLastRowNum()) {
					rowCounter = rowCounter + 1;
					row = sheet.getRow(i++);
					
					if (row != null && rowCounter > 1) {
						clearResutCell(excelHeaderValues, row, maxNumOfCells);
						HashMap<String, String> routeMap = checkRouteUploadTemplate(row,excelHeaderValues,maxNumOfCells,style,cellStyleForFailed);
						String checkRowEmptyFlag = checkRowEmpty(row, "Sites_Bulk_Import_Template");
						
						if (checkRowEmptyFlag.equalsIgnoreCase("No") && routeMap.containsKey("route")) {
							int presentRow = rowCounter-1;
							String route = routeMap.get("route");
							logger.info(" [UploadSiteTemplate] route is "+route);
							if (!route.equalsIgnoreCase("noread")) {
								bulkUploadResponse.setTotalCount(bulkUploadResponse.getTotalCount() + 1);
								String rowType = routeMap.get("rowType");
								logger.info(" [UploadSiteTemplate] Row Type is "+rowType);
								
								if (route.equalsIgnoreCase("newrecord") && rowType.equalsIgnoreCase("site")) {
									try {
										long siteId = siteUpload(fileUploadResponse, excelHeaderValues, bulkUploadResponse, response, maxNumOfCells, 
												style, cellStyleForFailed, cellStyleForSuccess, row, customerDetails);
										siteIds.put(routeMap.get("rowId"), siteId);
										if (siteId > 0) {
											updateBulkUploadProgress(fileUploadResponse, totalRows, 0, presentRow);
										} else {
											failedCount++;
											updateBulkUploadProgress(fileUploadResponse, totalRows, 1, presentRow);
										}
									} catch (Exception e) {
										failedCount++;
										updateBulkUploadProgress(fileUploadResponse, totalRows, 1, presentRow);
										logger.error("SiteServiceImpl UploadSiteTemplate Error:",e);
									}
								} else if (siteIds.containsKey(routeMap.get("parentId")) && siteIds.get(routeMap.get("parentId")) > 0
											&& (rowType.equalsIgnoreCase("hvac") || rowType.equalsIgnoreCase("thermostat"))) {
									try {
										long siteId = siteIds.get(routeMap.get("parentId"));
										long insertId = siteUpdate(fileUploadResponse.getCustomerId(), excelHeaderValues, bulkUploadResponse, maxNumOfCells, workbook, 
												cellStyleForFailed, row, fileUploadResponse.getUserId(), cellStyleForSuccess, siteId, rowType);
										response.setData(bulkUploadResponse);
										if (insertId > 0) {
											updateBulkUploadProgress(fileUploadResponse, totalRows, 0, presentRow);
										} else {
											failedCount++;
											updateBulkUploadProgress(fileUploadResponse, totalRows, 1, presentRow);
										}
									} catch (Exception e) {
										failedCount++;
										updateBulkUploadProgress(fileUploadResponse, totalRows, 1, presentRow);
										logger.error("SiteServiceImpl UploadSiteTemplate Error:",e);
									}
								} else {
									failedCount++;
									updateBulkUploadProgress(fileUploadResponse, totalRows, 1, presentRow);
								}
							} 
						} else {
							rowEmptyCounter += 1; 
						}
					}
				}
				siteDao.updateBulkUploadProgress(fileUploadResponse.getFileName(), 0, totalRows, 0, fileUploadResponse.getUserId(), "completed");
				response.setData(bulkUploadResponse);
				sheet.showInPane(0, 0);
				FileOutputStream outPutFile = new FileOutputStream(new File(fileUploadResponse.getFileLocation())); 
				// Open FileOutputStream to write updates
				workbook.write(outPutFile); // write changes
				outPutFile.close();
				logSuccessInformation(totalRows, failedCount, fileUploadResponse);
			}
			
		} catch (Exception e) {
			logger.error("SiteServiceImpl UploadSiteTemplate Error:",e);
		} 
		logger.info("SiteServiceImpl UploadSiteTemplate end");
		return response;
	}

	private void clearResutCell(Map<Integer, String> excelHeaderValues, Row row, int maxNumOfCells) {
		for (int cellNumber = 0; cellNumber < maxNumOfCells; cellNumber++) {
			Cell result = null;
			if (excelHeaderValues.get(cellNumber).equalsIgnoreCase(CommonConstants.RESULT)) {
				if (row.getCell(cellNumber) == null) {
					result = row.createCell(cellNumber);
				} else {
					result = row.getCell(cellNumber);
				}
				String resultCellValue = row.getCell(cellNumber).getStringCellValue();
				if (resultCellValue.length() != 0) {
					result.setCellValue("");
				}
			}
		}
	}

	private int getExcelHeaderNames(Map<Integer, String> excelHeaderValues, int i, Sheet sheet, Row row, int cellType) {
		int maxNumOfCells = 0;
		Cell cellForResult = null;
		i++;
		try {
			if (row != null && row.getRowNum() == 0) {
				// continue; //just skip the rows if row number is 0 or
				int cellNum = 0;
				for (cellNum = 0; i <= row.getLastCellNum(); cellNum++) {
					Cell cellVal = row.getCell(cellNum);
					if (cellVal != null && CommonConstants.RESULT.equalsIgnoreCase(cellVal.toString())) {
						excelHeaderValues.remove(cellNum, CommonConstants.RESULT);
					}
					if ((cellType == XSSFCell.CELL_TYPE_STRING) || (cellType == XSSFCell.CELL_TYPE_NUMERIC)
							|| (cellType == XSSFCell.CELL_TYPE_BOOLEAN)) {
						if (cellVal != null && CommonConstants.RESULT.equalsIgnoreCase(cellVal.toString())) {
							row.removeCell(cellVal);
							cellNum = cellNum - 1;
							continue;
						}
						if (cellVal == null || cellVal.getCellType() == Cell.CELL_TYPE_BLANK) {
							excelHeaderValues.put(cellNum, CommonConstants.RESULT);
							if (row.getCell(cellNum) == null) {
								cellForResult = row.createCell(cellNum);
							} else {
								cellForResult = row.getCell(cellNum);
							}
							cellForResult.setCellValue(CommonConstants.RESULT);
							break;
						} else {
							excelHeaderValues.put(cellNum, row.getCell(cellNum).getStringCellValue());
						}
					}
				}
				maxNumOfCells = row.getLastCellNum();
				for (int cellNumber = 0; cellNumber < maxNumOfCells; cellNumber++) {
					if (excelHeaderValues.get(cellNumber).equalsIgnoreCase(CommonConstants.RESULT)) {
						sheet.autoSizeColumn(cellNumber,true);
					}
				}
			}
		} catch (Exception e) {
			logger.error("SiteServiceImpl getTotalRowNotEmpty Error:",e);
			throw e;
		}
		return maxNumOfCells;
	}
	
	private int getTotalRowNotEmpty(Sheet sheet, Map<Integer, String> excelHeaderValues, int maxNumOfCells) throws VEMAppException {
		int totalRows = 0;
		int rowCounter = 0;
		int i = 0;
		try {
			DataFormatter formatter = new DataFormatter();
			while (i <= sheet.getLastRowNum()) {
				rowCounter = rowCounter + 1;
				Row row = sheet.getRow(i++);
				if (row != null && rowCounter > 1) {
					String checkRowEmptyFlag = checkRowEmpty(row, "Sites_Bulk_Import_Template");
					if (checkRowEmptyFlag.equalsIgnoreCase("No")) {
						String ignore = "";
						for (int cellCounters = 0; cellCounters < maxNumOfCells; cellCounters++) {				
							String headerName = excelHeaderValues.get(cellCounters).toUpperCase();
							
							switch (headerName) {
							case "IGNORE":
								ignore = formatter.formatCellValue(row.getCell(cellCounters)).trim();
								cellCounters = maxNumOfCells - 1;
								break;
								default:
							}
						}
						if (!ignore.equalsIgnoreCase("Yes")) {
							totalRows++;
						}
					}
				}
			}
		} catch (VEMAppException e) {
			logger.error("SiteServiceImpl getTotalRowNotEmpty Error:",e);
			throw e;
		}
		return totalRows;
	}
	
	private void logSuccessInformation(int totalRows, int failedCount, FileUploadResponse fileUploadResponse) {
		try {
			AuditRequest auditRequest = new AuditRequest();
			auditRequest.setUserId(0);
			if (failedCount > 0) {
				auditRequest.setUserAction("Failed");
			} else {
				auditRequest.setUserAction("Success");
			}
			auditRequest.setLocation("");
			auditRequest.setServiceId("13");
			auditRequest.setDescription("Bulk import completed "+(totalRows-failedCount)+"/"+totalRows+" added, "+failedCount+" failed");
			auditRequest.setServiceSpecificId(fileUploadResponse.getCustomerId());
			auditDAO.insertAuditLog(auditRequest);
		} catch (Exception e) {
			logger.error("SiteServiceImpl logSuccessInformation Error:",e);
		}
	}

	private void updateBulkUploadProgress(FileUploadResponse fileUploadResponse, int totalRows, int failedCount, int presentRow) throws VEMAppException {
		try {
			if (presentRow == totalRows)
				siteDao.updateBulkUploadProgress(fileUploadResponse.getFileName(), 1, totalRows, failedCount, fileUploadResponse.getUserId(), "completed");
			else
				siteDao.updateBulkUploadProgress(fileUploadResponse.getFileName(), 1, totalRows, failedCount, fileUploadResponse.getUserId(), "inprogress");
		} catch (Exception e) {
			logger.error("SiteServiceImpl updateBulkUploadProgress Error:",e);
		}
	}
	
	private CellStyle createCellStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.RED.getIndex());
		style.setTopBorderColor(IndexedColors.RED.getIndex());
		style.setBottomBorderColor(IndexedColors.RED.getIndex());
		style.setBorderBottom(IndexedColors.RED.getIndex());
		style.setLeftBorderColor(IndexedColors.RED.getIndex());
		style.setBorderLeft(IndexedColors.RED.getIndex());
		style.setRightBorderColor(IndexedColors.RED.getIndex());
		style.setBorderRight(IndexedColors.RED.getIndex());
		style.setBorderTop(IndexedColors.RED.getIndex());
		return style;
	}
	
	private CellStyle createCellStyleForSuccess(Workbook workbook) {
		CellStyle cellStyleForSuccess = workbook.createCellStyle();        
		Font fontForSuccess = workbook.createFont();
		fontForSuccess.setFontHeightInPoints((short)12);
		fontForSuccess.setColor(IndexedColors.GREEN.getIndex());
		cellStyleForSuccess.setFont(fontForSuccess);
		return cellStyleForSuccess;
	}

	private CellStyle createCellStyleForFailed(Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();        
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short)12);
		font.setColor(IndexedColors.RED.getIndex());
		cellStyle.setFont(font);
		cellStyle.setWrapText(true);
		return cellStyle;
	}
	
	private HashMap<String, String> checkRouteUploadTemplate(Row row, Map<Integer, String> excelHeaderValues, int maxNumOfCells, CellStyle style, CellStyle cellStyleForFailed) throws Exception {
		logger.info("check the route for upload");
		HashMap<String, String> resultMap = new HashMap<>();
		try {
			DataFormatter formatter = new DataFormatter();
			
			String ignore = "";
			String rowId = "";
			String parentId = "";
			String rowType = "";

			String routeString = "error";
			StringBuilder errors = new StringBuilder();
			
			for (int cellCounters = 0; cellCounters < maxNumOfCells; cellCounters++) {				
				String headerName = excelHeaderValues.get(cellCounters).toUpperCase();
				
				switch (headerName) {
				case "IGNORE":
					ignore = formatter.formatCellValue(row.getCell(cellCounters)).trim();
					break;
				case "ROWID":
					rowId = formatter.formatCellValue(row.getCell(cellCounters)).trim();
					break;
				case "PARENTID":
					parentId = formatter.formatCellValue(row.getCell(cellCounters)).trim();
					break;
				case "ROW TYPE":
					rowType = formatter.formatCellValue(row.getCell(cellCounters)).trim();
					break;
					default:
				}
			}
			if (ignore.isEmpty()){
				ignore = "No";
			}
			if (rowId.isEmpty()){
				rowId = String.valueOf(row.getRowNum()+1);
			}
			if (rowType.isEmpty()){
				rowType = "Site";
			}
			if (!"yes".equalsIgnoreCase(ignore)) {
				if (!rowType.equalsIgnoreCase("site")) {
					routeString = "updateRecord";
				}else if (rowType.equalsIgnoreCase("site")) {
					routeString = "newRecord";
				} else {
					errors.append(",Row Id is a mandatory field");
				}
				if (rowType.isEmpty()) {
					errors.append(",Please select Row Type value");
				}
				if (!rowType.equalsIgnoreCase("site") && parentId.isEmpty()) {
					errors.append(",For Row Type (Hvac) or (Thermostat) Parent Id is a mandatory field");
				}
				if (errors.length() > 0) {
					appendErrorsToExcel(excelHeaderValues, maxNumOfCells, cellStyleForFailed, row, errors);
					routeString = "error";
				}
			} else {
				routeString = "noread";
			}
			
			if (!rowType.isEmpty()) {
				rowType = rowType.trim().toLowerCase();
			}

			resultMap.put("route", routeString);
			resultMap.put("rowId", rowId);
			resultMap.put("parentId", parentId);
			resultMap.put("rowType", rowType);

		} catch (Exception e) {
			resultMap.put("result", "error");
			logger.error("", e);
			throw e;
		}
		return resultMap;
	}

	private long siteUpload(FileUploadResponse fileUploadResponse, Map<Integer, String> excelHeaderValues,
			BulkUploadResponse bulkUploadResponse, Response response, int maxNumOfCells,  CellStyle style,
			CellStyle cellStyleForFailed, CellStyle cellStyleForSuccess, Row row, Response customerDetails) throws Exception {
		long siteId = 0;
		StringBuilder errors = new StringBuilder();
		try {
			UpdateSiteRequest addSiteRequest = new UpdateSiteRequest();
			addSiteRequest.setCustomerId(fileUploadResponse.getCustomerId());
			DataFormatter formatter = new DataFormatter();
			
			setSiteRequestDetailsForUpload(excelHeaderValues, maxNumOfCells, row, addSiteRequest, formatter, style, cellStyleForFailed, fileUploadResponse, errors, customerDetails);
			
			if (errors.toString().trim().length() > 0) {
				bulkUploadResponse.setInsertRecord(CommonConstants.NOT_VALID_RECORD);
				response.setData(bulkUploadResponse);			
				appendErrorsToExcel(excelHeaderValues, maxNumOfCells, cellStyleForFailed, row, errors);			
			}
				
			if(errors.toString().trim().length()==0) {
				
				List<String> invalidGroupNameArr = new ArrayList<>();
				List<String> validGroupIdsArr = addSiteRequest.getSiteGroups();
				
				// create a group and add to valid group name array

				if (addSiteRequest.getNewGroups() != null) {
					if (validGroupIdsArr == null) {
						validGroupIdsArr = new ArrayList<>();
					}
					for (String groupName : addSiteRequest.getNewGroups()) {
					// Initialing the group request
					GroupRequest groupRequest = new GroupRequest();
					groupRequest.setCustomerId(addSiteRequest.getCustomerId());
					groupRequest.setGroupName(groupName);
					groupRequest.setSelectedLocations(new ArrayList<>());
					groupRequest.setUserId(fileUploadResponse.getUserId());
					groupRequest.setUpdateMode(0);
	
					// creating a new group
					try {
						Response groupResponse = groupDAO.addGroup(groupRequest);
						if (groupResponse.getStatus().equalsIgnoreCase("success")) {
							validGroupIdsArr.add(String.valueOf((Integer) groupResponse.getData()));
						} else {
							invalidGroupNameArr.add(groupName);
						}
					} catch (Exception e) {
						logger.error("", e);
						invalidGroupNameArr.add(groupName);
					}
					
					if (!invalidGroupNameArr.isEmpty())
						for (int siteSuccess = 0; siteSuccess < maxNumOfCells; siteSuccess++) {
							if (excelHeaderValues.get(siteSuccess).equalsIgnoreCase("Groups")) {
								String invalidGroupNames = invalidGroupNameArr.toString().replace("[", "").replace("]", "");
								row.getCell(siteSuccess).setCellValue("These are the invalid groups: "+invalidGroupNames);
								row.getCell(siteSuccess).setCellStyle(cellStyleForFailed);
							}
						}
					}
					addSiteRequest.setSiteGroups(validGroupIdsArr);
				}
				
				try {
					JSONObject siteInserted = siteDao.addBulkUploadSites(addSiteRequest, fileUploadResponse.getUserId());
					String errorMessage = (String) siteInserted.get("errorMsg");
					siteId = (Integer) siteInserted.get("insertId");
					if (siteId > 0) {
						bulkUploadResponse.setRecordResponse("Record Inserted");
						bulkUploadResponse.setSucessCount(bulkUploadResponse.getSucessCount() + 1);
						for (int siteSucces = 0; siteSucces < maxNumOfCells; siteSucces++) {
							if (excelHeaderValues.get(siteSucces).equalsIgnoreCase(CommonConstants.RESULT)) {
								row.getCell(siteSucces).setCellValue("SUCCESS");
								row.getCell(siteSucces).setCellStyle(cellStyleForSuccess);
							}
						}
					} else {
						for (int siteSucces = 0; siteSucces < maxNumOfCells; siteSucces++) {
							if (excelHeaderValues.get(siteSucces).equalsIgnoreCase(CommonConstants.RESULT)) {
								if (errorMessage.split(":")[1].contains("Data truncated for column 'stt")) {
									row.getCell(siteSucces).setCellValue("FAILED, Incorrect store hours format.");
									row.getCell(siteSucces).setCellStyle(cellStyleForFailed);
								} else if (errorMessage.split(":")[1].contains("Cannot add or update a child row")) {
									row.getCell(siteSucces).setCellValue("FAILED, Invalid address entered.");
									row.getCell(siteSucces).setCellStyle(cellStyleForFailed);
								} else {
									row.getCell(siteSucces).setCellValue("FAILED," + errorMessage.split(":")[1]);
									row.getCell(siteSucces).setCellStyle(cellStyleForFailed);
								}
							}
						}
					}
					System.out.println("@@@@SiteCreated***siteId***" + siteInserted);
				} catch (Exception e) {
					logger.error("", e);
					return siteId;
				}
			}
		} catch (Exception e) {
			if (errors.toString().trim().length() > 0) {
				bulkUploadResponse.setInsertRecord(CommonConstants.NOT_VALID_RECORD);
				response.setData(bulkUploadResponse);			
				appendErrorsToExcel(excelHeaderValues, maxNumOfCells, cellStyleForFailed, row, errors);			
			}
			logger.error("SiteServiceImpl siteUpload Error:",e);
			throw e;
		}
		return siteId;
	}

	private Cell appendErrorsToExcel(Map<Integer, String> excelHeaderValues, int maxNumOfCells,
			CellStyle cellStyleForFailed, Row row, StringBuilder errors) {
		Cell cell = null;
		for (int cellNumber = 0; cellNumber < maxNumOfCells; cellNumber++) {
			if (excelHeaderValues.get(cellNumber).equalsIgnoreCase(CommonConstants.RESULT)) {
				if (row.getCell(cellNumber) == null) {
					cell = row.createCell(cellNumber);
				} else {
					cell = row.getCell(cellNumber);
				}
				String resultCellValue = row.getCell(cellNumber).getStringCellValue();
				if (resultCellValue.length() != 0) {
					cell.setCellValue("");
				}
				if (errors.toString().trim().length() > 0) {
					errors.insert(0, "Failed");
					cell.setCellValue(errors.toString().replaceAll(",", ",\n"));
					cell.setCellStyle(cellStyleForFailed);
					row.setHeight((short) (row.getHeight() * (short) errors.substring(1).split(",").length));
				}
			}
		}
		return cell;
	}
	
	private void setSiteRequestDetailsForUpload(Map<Integer, String> excelHeaderValues, int maxNumOfCells, Row row, UpdateSiteRequest addSiteRequest, DataFormatter formatter, 
			CellStyle style, CellStyle cellStyleForFailed,FileUploadResponse fileUploadResponse, StringBuilder errors, Response customerDetails) throws Exception {
		int siteCell = 0;
		Cell cell;
		String cityName = null;
		String storeHours = null;
		RTURequest rtuRequest = null;
		ThermostatRequest thermoRequest = null;
		System.out.println("setSiteRequestDetailsForUpload start");
		logger.info(" [setSiteRequestDetailsForUpload] is started ");
		
		for (int cellCounters = 0; cellCounters < maxNumOfCells; cellCounters++) {
			
			if (row.getCell(siteCell) == null) {
				cell = row.createCell(siteCell);
			} else {
				cell = row.getCell(siteCell);
			}
			
			String headerName = excelHeaderValues.get(siteCell).toUpperCase().trim();
			
			switch (headerName) {
				
			case "SITE NAME":
				String siteName = row.getCell(siteCell).getStringCellValue();
				if (siteName == null || siteName.trim().length() == 0 || siteName == "" || siteName.isEmpty()) {
					errors.append(",Site name is a mandatory field");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else if (siteName.length() > 50) {
					errors.append(",Site name should not exceed 50 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else if(siteDao.validateSiteNameIsAlreadyExists("Insert", siteName, Integer.toString(fileUploadResponse.getCustomerId()), null) > 0) {
				     errors.append(",Site name already exists");
				     cell.setCellStyle(cellStyleForFailed);
				     row.getCell(siteCell).setCellStyle(style);
				} else {
					addSiteRequest.setSiteName(siteName.trim());				
				}
				break;
				
			case "STORE #":				
				String siteStoreId = row.getCell(siteCell).getStringCellValue();
				if ((siteStoreId.trim().length() != 0) && CommonUtility.phoneValidate(siteStoreId).equals("Yess")) {
					errors.append(", Store Id should accept only numeric values.");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else if (siteStoreId.trim().length() > 20) {
					errors.append(",Site Id should not exceed 20 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				GetSiteRequest getSiteRequest = new GetSiteRequest();
				getSiteRequest.setSiteId(0);
				getSiteRequest.setSiteInternalId(siteStoreId.trim());
				getSiteRequest.setCustomerId(fileUploadResponse.getCustomerId());
				int validStoreId = siteDao.checkSiteInternalId(getSiteRequest);
				if (validStoreId > 0) {
					errors.append(",This store number is already used. Please enter a unique store number");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else
					addSiteRequest.setSiteInternalId(siteStoreId.trim());			
				break;
				
			case "TYPE":
				String storeType = formatter.formatCellValue(row.getCell(siteCell));
				int siteType = 0;
				boolean isNumeric = storeType.chars().allMatch(Character::isDigit);
				if (StringUtils.isNotEmpty(storeType.trim()) && isNumeric)
					siteType = Integer.parseInt(storeType.trim());
				if (siteType > 0 && siteType < 10) {
					addSiteRequest.setSiteType(siteType);
				} else {
					addSiteRequest.setSiteType(0);
					addSiteRequest.setSiteTypeNew(storeType.trim());
				}
				break;
				
			case "ADDRESS":
				String siteAddress = row.getCell(siteCell).getStringCellValue();
				if (siteAddress == null || siteAddress.trim().length() == 0 || siteAddress == "" || siteAddress.isEmpty()) {
					errors.append(",Address is a mandatory field");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else if (siteAddress.length() > 50) {
					errors.append(",Address should not exceed 50 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else
					addSiteRequest.setSiteAddLine1(siteAddress.trim());
				break;
				
			case "ADDRESS LINE2":
				String siteAddressLine2 = row.getCell(siteCell).getStringCellValue();
				if (siteAddressLine2.length() != 1 && siteAddressLine2.length() > 50) {
					errors.append(",Address Line 2 should not exceed 50 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else 
					addSiteRequest.setSiteAddLine2(siteAddressLine2.trim());
				break;
				
			case "STATE":
				String stateName = row.getCell(siteCell).getStringCellValue();
				if (stateName == null || stateName.trim().length() == 0 || stateName == "" || stateName.isEmpty()) {
					errors.append(",State is a mandatory field");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else 
					addSiteRequest.setSiteStateName(stateName.trim());
				break;
				
			case "CITY":
				cityName = row.getCell(siteCell).getStringCellValue();
				if (cityName == null || cityName.trim().length() == 0 || cityName == "" || cityName.isEmpty()) {
					errors.append(",City is a mandatory field");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else 
					addSiteRequest.setSiteCityName(cityName.trim());
				break;
				
			case "ZIP CODE":
				validateAndSetZipcode(excelHeaderValues, maxNumOfCells, row, addSiteRequest, formatter, cellStyleForFailed, siteCell, cell, addSiteRequest.getSiteCityName(), errors, style);
				break;
				
			case "SITE PHONE":
				String sitePhone = row.getCell(siteCell).getStringCellValue();
				if (sitePhone != null) {
					sitePhone = sitePhone.replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\-","").replaceAll("\\+","").replaceAll("\\s+","");
				}
				boolean flag = validatePhoneNo(row, style, cellStyleForFailed, errors, siteCell, cell, sitePhone,"Phone number");
				if (flag)
					addSiteRequest.setSitePhNo(sitePhone.replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\-","").replaceAll("\\+","").replaceAll("\\s+",""));
				break;	
				
			case "DISTRICT":
				String district = row.getCell(siteCell).getStringCellValue();
				if (district.length() > 50) {
					errors.append(",District field should not exceed 50 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else 
					addSiteRequest.setSiteDistrict(district.trim());				
				break;
				
			case "REGION":
				String region = row.getCell(siteCell).getStringCellValue();
				if (region.length() > 50) {
					errors.append(",Region field should not exceed 50 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else
					addSiteRequest.setSiteRegion(region.trim());
				break;
				
			case "AREA":
				String area = row.getCell(siteCell).getStringCellValue();
				if (area.length() > 50) {
					errors.append(",Area field should not exceed 50 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else 
					addSiteRequest.setSiteArea(area.trim());
				break;
				
			case "STORE HOURS":
				storeHours = formatter.formatCellValue(row.getCell(siteCell));
				if (storeHours != null && (storeHours.toLowerCase().contains("am") || storeHours.toLowerCase().contains("pm"))) {
					addSiteRequest.setSiteHrsFormate(1);
				} else {
					addSiteRequest.setSiteHrsFormate(0);
				}
				StringBuilder siteHoursListStr = validateAndSetStoreHours(excelHeaderValues, maxNumOfCells, row, cellStyleForFailed, cell, style, errors, storeHours, "store", siteCell);
				addSiteRequest.setSiteStoreHours(siteHoursListStr);
				break;
			case "GROUPS":
				String groupNames = row.getCell(siteCell).getStringCellValue();
				validateGroups(fileUploadResponse.getCustomerId(), excelHeaderValues, maxNumOfCells, cellStyleForFailed, row, addSiteRequest, groupNames, fileUploadResponse.getUserId());
				break;
			case "FAN PREFERENCES":
				String fanOn = formatter.formatCellValue(row.getCell(siteCell));
				setFanPreferences(addSiteRequest, fanOn);
				break;
			case "LOCK PREFERENCES":
				String lockPreference = formatter.formatCellValue(row.getCell(siteCell));
				setLockPreferences(addSiteRequest, lockPreference);
				break;
			case "RESET HVAC TO AUTO":
				String hvacMode = formatter.formatCellValue(row.getCell(siteCell));
				if (hvacMode.isEmpty()) 
					addSiteRequest.setIsHVACModeToAuto(1);
				else {
					int siteResetHVACModeAuto = getModeBasedOnFlag(hvacMode);
					addSiteRequest.setIsHVACModeToAuto(siteResetHVACModeAuto);
				}
				break;
			case "RESET HOLD TO OFF":
				String holdMode = formatter.formatCellValue(row.getCell(siteCell));
				if (holdMode.isEmpty())
					addSiteRequest.setResetHoldMode(1);
				else {
					int siteResetHoldMode = getModeBasedOnFlag(holdMode);
					addSiteRequest.setResetHoldMode(siteResetHoldMode);
				}
				break;
			case "MIN SP":
				String minSP = formatter.formatCellValue(row.getCell(siteCell));
				if ((minSP.trim().length() != 0) && CommonUtility.phoneValidate(minSP).equals("Yess")) {
					errors.append(",Min SP field should accept only numeric values");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else if ((minSP.trim().length() != 0) && CommonUtility.phoneValidate(minSP).equals("No")
						&& (((Integer.parseInt(minSP)) < 45 || (Integer.parseInt(minSP)) > 90))) {
					errors.append(",Min SP range should be 45 to 90");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else {
					addSiteRequest.setMinSP(minSP);
				}
				break;
				
			case "MAX SP":
				String maxSP = formatter.formatCellValue(row.getCell(siteCell));
				if ((maxSP.trim().length() != 0) && CommonUtility.phoneValidate(maxSP).equals("Yess")) {
					errors.append(",Max SP field should accept only numeric values");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else if (maxSP.trim().length() != 0 && CommonUtility.phoneValidate(maxSP).equals("No")
						&& (addSiteRequest.getMinSP() != null && addSiteRequest.getMinSP().length() != 0 && Integer.parseInt(maxSP) <= Integer.parseInt(addSiteRequest.getMinSP()))) {
					errors.append(",Max SP has to be greater than Min SP");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else if ((maxSP.trim().length() != 0) && CommonUtility.phoneValidate(maxSP).equals("No")
						&& (((Integer.parseInt(maxSP)) > 90))) {
					errors.append(",Max SP range should be 45 to 90");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else 
					addSiteRequest.setMaxSP(maxSP);
				break;
				
			case "NIGHTLY SCHEDULE DOWNLOAD":
				String scheduleDownload = formatter.formatCellValue(row.getCell(siteCell));
				int siteNightlyScheduleDownload = getModeBasedOnFlag(scheduleDownload);
				addSiteRequest.setNightlyScheduleDownload(siteNightlyScheduleDownload);
				break;
				
				
				//Hvac Information block
			/*case "MODEL OF THE HVAC":
				String modelOfRTU = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(modelOfRTU) && rtuRequest == null)
					rtuRequest = new RTURequest();
				if (StringUtils.isNotEmpty(modelOfRTU) && rtuRequest != null) {
					if (modelOfRTU.trim().length() > 48) {
						errors.append(",Model of the HVAC should not exceed 48 characters");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
					rtuRequest.setModel(modelOfRTU);
				}
				break;
				
			case "UNIT #":
				String hVACUnitNumber = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(hVACUnitNumber) && rtuRequest == null)
					rtuRequest = new RTURequest();
				if (StringUtils.isNotEmpty(hVACUnitNumber) && rtuRequest != null) {
					if (hVACUnitNumber.trim().length() > 48) {
						errors.append(",Unit should not exceed 48 characters");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
					rtuRequest.setUnit(hVACUnitNumber);
				}
				break;
				
			case "SELECT WHERE IT’S LOCATED":
				String hvacLocation = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(hvacLocation) && rtuRequest == null)
					rtuRequest = new RTURequest();
				if (StringUtils.isNotEmpty(hvacLocation) && rtuRequest != null) {
					if ("Roof Top".equals(hvacLocation)) {
						rtuRequest.setLocation(1);
					} else if ("Closet".equals(hvacLocation)) {
						rtuRequest.setLocation(2);
					} else if ("Back of the building".equals(hvacLocation)) {
						rtuRequest.setLocation(3);
					}
				}
				break;
			case "HEATING TEMPERATURE":
				String heatTemp = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(heatTemp) && rtuRequest == null)
					rtuRequest = new RTURequest();
				if (StringUtils.isNotEmpty(heatTemp) && rtuRequest != null) {
					rtuRequest.setHeating(heatTemp);
				}
				break;
				
			case "COOLING TEMPERATURE":
				String coolTemp = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(coolTemp) && rtuRequest == null)
					rtuRequest = new RTURequest();
				if (StringUtils.isNotEmpty(coolTemp) && rtuRequest != null) {
					rtuRequest.setCooling(coolTemp);
				}
				break;*/
			case "HOW DO EMPLOYEES BELIEVE THE SYSTEM HAS BEEN RUNNING?":
				String empBelieveSystem = formatter.formatCellValue(row.getCell(siteCell));
				if (empBelieveSystem.trim().length() > 1024) {
					errors.append(",How do employees believe the system has been running should not exceed 1024 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setEmpBelieveSystem(empBelieveSystem);
				break;
				
				
				//Thermostat Information block
			/*case "HVAC UNIT #":
				String hvacUnit = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(hvacUnit) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(hvacUnit) && thermoRequest != null) {
					thermoRequest.sethVACUnit(hvacUnit);
				}
				break;
			case "THERMOSTAT UNIT #":
				String hUnit = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(hUnit) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(hUnit) && thermoRequest != null) {
					if (hUnit.trim().length() > 48) {
						errors.append(",Thermostat Unit should not exceed 48 characters");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
					thermoRequest.setUnit(hUnit);
				}
				break;
			case "LOCATION OF THERMOSTAT":
				String thermoLocation = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(thermoLocation) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(thermoLocation) && thermoRequest != null) {
					setThermoLocation(thermoRequest, thermoLocation);
				}
				break;
			case "IS THE MOUNTING SPACE LARGE ENOUGH?":
				String mountSpaceEnough = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(mountSpaceEnough) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(mountSpaceEnough) && thermoRequest != null) {
					if ("Yes".equalsIgnoreCase(mountSpaceEnough)) {
						thermoRequest.setSpaceEnough(1);
					} else if ("No".equalsIgnoreCase(mountSpaceEnough)) {
						thermoRequest.setSpaceEnough(0);
					}
				}
				break;
			case "MAKE OF THERMOSTAT":
				String makeThermo = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(makeThermo) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(makeThermo) && thermoRequest != null) {
					if (makeThermo.trim().length() > 48) {
						errors.append(",Make of Thermostat should not exceed 48 characters");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
					thermoRequest.setMake(makeThermo);
				}
				break;
			case "MODEL OF THERMOSTAT":
				String modelThermo = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(modelThermo) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(modelThermo) && thermoRequest != null) {
					if (modelThermo.trim().length() > 48) {
						errors.append(",Model of Thermostat should not exceed 48 characters");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
					thermoRequest.setModel(modelThermo);
				}
				break;
			case "PLEASE DESCRIBE CURRENT WIRING CONFIGURATION OF THERMOSTATS":
				String wiringConfigure = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(wiringConfigure) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(wiringConfigure) && thermoRequest != null) {
					if (wiringConfigure.trim().length() > 48) {
						errors.append(",Wiring Configuration Description should not exceed 48 characters");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
					thermoRequest.setWiringConfigThermostat(wiringConfigure);
				}
				break;
			case "CONFIRM 24VDC BETWEEN THE RC/R AND C":
				String confirm24VDC = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(confirm24VDC) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(confirm24VDC) && thermoRequest != null) {
					if ("Yes".equalsIgnoreCase(confirm24VDC)) {
						thermoRequest.setrCAndCPower(1);
					} else if ("No".equalsIgnoreCase(confirm24VDC)) {
						thermoRequest.setrCAndCPower(0);
					}
				}
				break;
			case "IF THERE IS 'C' WIRE ATTACHED":
				String cWireAttach = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(cWireAttach) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(cWireAttach) && thermoRequest != null) {
					if ("Yes".equalsIgnoreCase(cWireAttach)) {
						thermoRequest.setcWireAttached(1);
					} else if ("No".equalsIgnoreCase(cWireAttach)) {
						thermoRequest.setcWireAttached(0);
					}
				}
				break;
			case "IF THERE IS NO 'C' ATTACHED, CHECK IF THERE IS A SPARE UNUSED WIRE IN THE BUNDLE OF HVAC WIRING":
				String noCAttached = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(noCAttached) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(noCAttached) && thermoRequest != null) {
					if ("Yes".equalsIgnoreCase(noCAttached)) {
						thermoRequest.setNoCWireAttached(1);
					} else if ("No".equalsIgnoreCase(noCAttached)) {
						thermoRequest.setNoCWireAttached(0);
					}
				}
				break;
			case "DOES THERMOSTAT HAVE AN AUTOMATED SCHEDULE ?":
				String autoSchedudle = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(autoSchedudle) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(autoSchedudle) && thermoRequest != null) {
					if ("Yes".equalsIgnoreCase(autoSchedudle)) {
						thermoRequest.setAutomatedSchedule(1);
					} else if ("No".equalsIgnoreCase(autoSchedudle)) {
						thermoRequest.setAutomatedSchedule(0);
					}
				}
			case "SCHEDULE DETAILS":
				String schedudle = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(schedudle) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(schedudle) && thermoRequest != null) {
					if (schedudle.trim().length() > 48) {
						errors.append(",Schedule Details should not exceed 48 characters");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
					thermoRequest.setAutomatedScheduleNote(schedudle);
				}
				break;
			case "LOCATION OF REMOTE SENSORS FOR THIS THERMOSTAT":
				String remoteSensors = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(remoteSensors) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(remoteSensors) && thermoRequest != null) {
					thermoRequest.setLocationOfRemoteSensor(remoteSensors);
				}
				break;
			case "IF SENSORS ARE PRESENT, VALIDATE WHICH SENSOR IS CONNECTED TO WHICH THERMOSTAT":
				String validateSensor = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(validateSensor) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(validateSensor) && thermoRequest != null) {
					if (validateSensor.trim().length() > 48) {
						errors.append(",Validate Sensor should not exceed 48 characters");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
					thermoRequest.setValidateSensor(validateSensor);
				}
				break;
			case "PLEASE DESCRIBE WIRING TO CURRENT SENSOR IF APPLICABLE":
				String currentSensor = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(currentSensor) && thermoRequest == null)
					thermoRequest = new ThermostatRequest();
				if (StringUtils.isNotEmpty(currentSensor) && thermoRequest != null) {
					if (currentSensor.trim().length() > 1024) {
						errors.append(",Current Wiring Sensor should not exceed 1024 characters");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
					thermoRequest.setWiringConfigSensor(currentSensor);
				}
				break;*/
				
				// Site Information block
			case "ACTUAL SITE SURVEY DATE":
				String surveyDate = null;
				Date javaDate = null;
				if (row.getCell(siteCell).getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
					javaDate = DateUtil.getJavaDate((double) row.getCell(siteCell).getNumericCellValue());
				} else {
					String s = formatter.formatCellValue(row.getCell(siteCell));
					boolean isNumericDate = s.chars().allMatch(Character::isDigit);
					if (!s.trim().isEmpty() && isNumericDate) {
						javaDate = DateUtil.getJavaDate((double) Double.parseDouble(s));
					} else {
						surveyDate = s;
					}
				}
				if (javaDate != null) {
					surveyDate = new SimpleDateFormat("MM/dd/yyyy").format(javaDate);
				}
				if (StringUtils.isNotEmpty(surveyDate)) {
					if (CommonUtility.isValidDate(surveyDate, "MM/dd/yyyy")) {
						addSiteRequest.setSurveyDate(surveyDate);
					} else {
						errors.append(",Invalid site survey date");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
					}
				}
				break;
			case "OCCUPY HOURS":
				String occupyHours = formatter.formatCellValue(row.getCell(siteCell));
				if (occupyHours == null || occupyHours.trim().length() == 0)
					occupyHours = storeHours;
				if (occupyHours != null && (occupyHours.toLowerCase().contains("am") || occupyHours.toLowerCase().contains("pm"))) {
					addSiteRequest.setSiteOccupyHrsFormate(1);
				} else {
					addSiteRequest.setSiteOccupyHrsFormate(0);
				}
				StringBuilder siteOccupyHoursListStr = validateAndSetStoreHours(excelHeaderValues, maxNumOfCells, row, cellStyleForFailed, cell, style, errors, occupyHours, "occupy", siteCell);
				addSiteRequest.setSiteOccupyHours(siteOccupyHoursListStr);
				break;
			case "SQUARE FOOTAGE":
				String squareFootage = formatter.formatCellValue(row.getCell(siteCell));
				if ((squareFootage.trim().length() != 0) && CommonUtility.phoneValidate(squareFootage).equals("Yess")) {
					errors.append(",Square Footage field should accept only numeric values");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else if (squareFootage.trim().length() > 48) {
					errors.append(",Square Footage should not exceed 48 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else {
					addSiteRequest.setSquareFootage(squareFootage);
				}
				break;
			case "BUILDING LAYOUT AND LOCATION OF THERMOSTATS AND SENSORS, ALSO INCLUDE SQUARE FOOTAGE":
				String buildingLayout = formatter.formatCellValue(row.getCell(siteCell));
				addSiteRequest.setBuildingLayout(buildingLayout);
				break;
			case "IS THERE A FLOOR PLAN ON THE WALL? IF SO, PLEASE TAKE A PICTURE AND MARK LOCATION OF THERMOSTATS AND REMOTE SENSORS":
				String floorPlan = formatter.formatCellValue(row.getCell(siteCell));
				if (floorPlan.trim().length() > 48) {
					errors.append(",Floor Plan should not exceed 48 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setFloorPlan(floorPlan);
				break;
				
				//Site Contact Block
			case "LOCAL SITE CONTACT":
				String localSiteContact = formatter.formatCellValue(row.getCell(siteCell));
				if (localSiteContact.trim().length() > 48) {
					errors.append(",Local Site Contact should not exceed 48 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setLocalSiteContact(localSiteContact);
				break;
			case "LOCAL CONTACT PHONE":
				String localContactPhone = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(localContactPhone)) {
					localContactPhone = localContactPhone.replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\-","").replaceAll("\\+","").replaceAll("\\s+","");
					boolean localPhone = validatePhoneNo(row, style, cellStyleForFailed, errors, siteCell, cell, localContactPhone,"Local contact number");
					if (localPhone)
						addSiteRequest.setLocalContactPhone(localContactPhone);
				}
				break;
			case "LOCAL CONTACT EMAIL":
				String localContactEmail = formatter.formatCellValue(row.getCell(siteCell));
				if ((localContactEmail.trim().length() != 0) && CommonUtility.emailValidate(localContactEmail).equals("Yess")) {
					errors.append(",Please enter valid Local Contact email");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else if (localContactEmail.trim().length() > 30) {
					errors.append(",Local contact email should not exceed 30 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else {
					addSiteRequest.setLocalContactEmail(localContactEmail);
				}
				break;
			case "LOCAL CONTACT MOBILE":
				String localContactMobile = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(localContactMobile)) {
					localContactMobile = localContactMobile.replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\-","").replaceAll("\\+","").replaceAll("\\s+","");
					boolean localMobile = validatePhoneNo(row, style, cellStyleForFailed, errors, siteCell, cell, localContactMobile,"Local contact mobile number");
					if (localMobile)
						addSiteRequest.setLocalContactMobile(localContactMobile);
				}
				break;
			case "ALTERNATE SITE CONTACT":
				String alternateSiteContact = formatter.formatCellValue(row.getCell(siteCell));
				if (alternateSiteContact.trim().length() > 48) {
					errors.append(",Alternate Site Contact should not exceed 48 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setAlternateSiteContact(alternateSiteContact);
				break;
			case "ALTERNATE CONTACT PHONE":
				String alternateContactPhone = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(alternateContactPhone)) {
					alternateContactPhone = alternateContactPhone.replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\-","").replaceAll("\\+","").replaceAll("\\s+","");
					boolean altPhone = validatePhoneNo(row, style, cellStyleForFailed, errors, siteCell, cell, alternateContactPhone,"Alternate contact number");
					if (altPhone)
						addSiteRequest.setAlternateContactPhone(alternateContactPhone);
				}
				break;
			case "ESCORT CONTACT NAME":
				String escortContactName = formatter.formatCellValue(row.getCell(siteCell));
				if (escortContactName.trim().length() > 48) {
					errors.append(",Escort Site Contact should not exceed 48 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setEscortContactName(escortContactName);
				break;
			case "ESCORT CONTACT NUMBER":
				String escortContactNumber = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(escortContactNumber)) {
					escortContactNumber = escortContactNumber.replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\-","").replaceAll("\\+","").replaceAll("\\s+","");
					boolean escPhone = validatePhoneNo(row, style, cellStyleForFailed, errors, siteCell, cell, escortContactNumber,"Escort contact number");
					if (escPhone)
						addSiteRequest.setEscortContactNumber(escortContactNumber);
				}
				break;
				
				//Site Access Block
			case "LIST ANY ACCESS RESTRICTIONS DIFFERENT FROM NORMAL BUSINESS HOURS":
				String listaAccessRestrictionsFromFormalHours = formatter.formatCellValue(row.getCell(siteCell));
				if (listaAccessRestrictionsFromFormalHours.trim().length() > 105) {
					errors.append(",Access Restictions should not exceed 105 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setListaAccessRestrictionsFromFormalHours(listaAccessRestrictionsFromFormalHours);
				break;
			case "SPECIAL ROOM ACCESS INFORMATION ":
				String specialRoomAccessInformation = formatter.formatCellValue(row.getCell(siteCell));
				if (specialRoomAccessInformation.trim().length() > 104) {
					errors.append(",Special Room Access Information should not exceed 104 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setSpecialRoomAccessInformation(specialRoomAccessInformation);
				break;
			case "IS THERE A LOCK BOX ON THERMOSTAT, IF SO WHO HAS ACCESS?":
				String lockBoxOnThermostat = formatter.formatCellValue(row.getCell(siteCell));
				if (lockBoxOnThermostat.trim().length() > 104) {
					errors.append(",Lock Box Access Information should not exceed 104 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setLockBoxOnThermostat(lockBoxOnThermostat);
				break;
			case "WHO HAS ACCESS OR WHO MANAGES THE THERMOSTAT?":
				String accessOrManagesThermostat = formatter.formatCellValue(row.getCell(siteCell));
				if (accessOrManagesThermostat.trim().length() > 104) {
					errors.append(",Who has Access or Who Manages the Thermostat should not exceed 104 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setAccessOrManagesThermostat(accessOrManagesThermostat);
				break;
				
				//Building Environment Block
			case "BUILDING ENVIRONMENT":
				String buildingLocation = formatter.formatCellValue(row.getCell(siteCell));
				addSiteRequest.setBuildingType(getBuildingType(buildingLocation));
				break;
			case "NOTES":
				String buildingNotes = formatter.formatCellValue(row.getCell(siteCell));
				if (buildingNotes.trim().length() > 1024) {
					errors.append(",Notes should not exceed 1024 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setBuildingNotes(buildingNotes);
				break;
				
				//Survey Technician Information Block
			case "SURVEY TECHNICIAN NAME":
				String technicianName = formatter.formatCellValue(row.getCell(siteCell));
				if (technicianName.trim().length() > 48) {
					errors.append(",Technician Name should not exceed 48 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setTechnicianName(technicianName);
				break;
			case "SURVEY TECHNICIAN PHONE NUMBER":
				String technicianPhone = formatter.formatCellValue(row.getCell(siteCell));
				if (StringUtils.isNotEmpty(technicianPhone)) {
					technicianPhone = technicianPhone.replaceAll("\\(","").replaceAll("\\)","").replaceAll("\\-","").replaceAll("\\+","").replaceAll("\\s+","");
					boolean localPhone = validatePhoneNo(row, style, cellStyleForFailed, errors, siteCell, cell, technicianPhone,"Technician phone number");
					if (localPhone)
						addSiteRequest.setTechnicianPhone(technicianPhone);
				}
				break;
			case "SURVEY NOTES":
				String technicianNotes = formatter.formatCellValue(row.getCell(siteCell));
				if (technicianNotes.trim().length() > 1024) {
					errors.append(",Technician Notes should not exceed 1024 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				addSiteRequest.setTechnicianNotes(technicianNotes);
				break;
				
			default:
				break;
			}			
			
			//validateAndSetThermostatDetails(excelHeaderValues, row, formatter, siteCell, thermoRequest);
			
			siteCell++;
		}
		addSiteRequest.setCustomerId(fileUploadResponse.getCustomerId());
		addSiteRequest.setUserId(fileUploadResponse.getUserId());
		
		setDefaultValuesOfCustomer(addSiteRequest, customerDetails, errors);
		validateSiteAddressForLatLang(addSiteRequest, errors, excelHeaderValues, maxNumOfCells, row, style, cellStyleForFailed);
		
		String stHours = addSiteRequest.getSiteStoreHours() != null  ?  addSiteRequest.getSiteStoreHours().toString() : "";
		String ocHours = addSiteRequest.getSiteStoreHours() != null  ?  addSiteRequest.getSiteOccupyHours().toString() : "";
		if (stHours.equalsIgnoreCase(ocHours)) {
			addSiteRequest.setSameAsStore(1);
		} else {
			addSiteRequest.setSameAsStore(0);
		}
		
		makeRTURequestAsString(addSiteRequest, rtuRequest);
		makeThermoRequestAsString(addSiteRequest, thermoRequest);
	}

	private void validateSiteAddressForLatLang(UpdateSiteRequest addSiteRequest, StringBuilder errors, Map<Integer, String> excelHeaderValues, int maxNumOfCells, Row row, CellStyle style, CellStyle cellStyleForFailed) {
		try {
			/*
		     * Get the longitude and latitude values based on 
		     * Address
		     */
		    StringBuilder address = new StringBuilder(addSiteRequest.getSiteAddLine1().trim());
		    String[] latAddress = address.toString().trim().split(",");
		    String cityname = addSiteRequest.getSiteCityName();
		    String statename = addSiteRequest.getSiteStateName();
		    String zip = addSiteRequest.getSiteZipCode();
		    
		    int start = address.indexOf(cityname);
		    if(start >= 0)
		    	address = address.delete(start, start + cityname.length());
		    
		    int end = address.indexOf(statename);
		    if(end >= 0)
		    	 address = address.delete(end, end + statename.length());
		    
		    int next = address.indexOf(zip);		    
		    if(next >= 0)
		    	address= address.delete(next, next + zip.length());
		    
		    int city =Arrays.asList(latAddress).indexOf(cityname);
		    int state =Arrays.asList(latAddress).indexOf(statename);
		    int zipcode =Arrays.asList(latAddress).indexOf(zip);
		    
		     if(city == -1)
		    	 address.append(","+addSiteRequest.getSiteCityName()); 
		     if(state == -1)
		    	  address.append(","+addSiteRequest.getSiteStateName()); 
		     if(zipcode == -1)
		    	  address.append(","+addSiteRequest.getSiteZipCode());
		     
			while (address.toString().endsWith(",")) {
				if (address.toString().endsWith(",")) {
					address = address.deleteCharAt(address.length() - 1);
					addSiteRequest.setSiteAddLine1(address.toString());
				}
			}
		    logger.debug("address::::"+address+"  URLEncoder:"+URLEncoder.encode(address.toString(), "UTF-8"));
		    
		    JSONObject latLanObject = CommonUtility.getLatitudeAndLangitute(URLEncoder.encode(address.toString(), "UTF-8"));
		    
		    if(latLanObject != null && latLanObject.get("lat") != null) {
		    	addSiteRequest.setLatitude(latLanObject.get("lat").toString());
		    	addSiteRequest.setLangitude(latLanObject.get("lng").toString());
		    } else {
		    	errors.append(",Address is invalid.");
		    	for (int cellNumber = 0; cellNumber < maxNumOfCells; cellNumber++) {
		    		Cell cell;
					if (("Address").equalsIgnoreCase(excelHeaderValues.get(cellNumber))) {
						if (row.getCell(cellNumber) == null) {
							cell = row.createCell(cellNumber);
						} else {
							cell = row.getCell(cellNumber);
						}
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(cellNumber).setCellStyle(style);
					}
		    	}
		    }
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void setDefaultValuesOfCustomer(UpdateSiteRequest addSiteRequest, Response customerDetails, StringBuilder errors) {
		try {
			addSiteRequest.setDegreePreference(0);
			if (addSiteRequest.getSiteOccupyHours() == null) {
				addSiteRequest.setSiteOccupyHours(addSiteRequest.getSiteStoreHours());
			}
			
			JSONArray customerData = (JSONArray) customerDetails.getData();
			Object data = customerData.get(0);
			JSONObject jsonObject = (JSONObject) data;
			String maxSP = "";
			
			if (addSiteRequest.getFanAuto() == null || addSiteRequest.getFanOn() == null) {
				String fanAuto = (String) jsonObject.get("fanOn");
				if ("Fan Auto".equalsIgnoreCase(fanAuto)) {
					setFanPreferences(addSiteRequest, fanAuto);
				}
			}
			
			if (addSiteRequest.getIsHVACModeToAuto() == null) {
				String havcAuto = (String) jsonObject.get("havcAuto");
				int flag = getModeBasedOnFlag(havcAuto);
				addSiteRequest.setIsHVACModeToAuto(flag);
			}
			
			if (addSiteRequest.getResetHoldMode() == null) {
				String resetHold = (String) jsonObject.get("resetHold");
				int flag = getModeBasedOnFlag(resetHold);
				addSiteRequest.setResetHoldMode(flag);
			}
			
			if (addSiteRequest.getNightlyScheduleDownload() == null) {
				String nightSchedule = (String) jsonObject.get("nightSchedule");
				int flag = getModeBasedOnFlag(nightSchedule);
				addSiteRequest.setNightlyScheduleDownload(flag);
			}
			
			if (addSiteRequest.getLock() == null) {
				if (jsonObject.get("lockPref") != null && jsonObject.get("lockPref").toString().length() > 0 ) {
					int lockPref = Integer.parseInt(jsonObject.get("lockPref").toString());
					addSiteRequest.setLock(lockPref);
				} 
			}

			if (addSiteRequest.getMinSP() == null || addSiteRequest.getMinSP().trim().length() == 0
				|| addSiteRequest.getMaxSP() == null || addSiteRequest.getMaxSP().trim().length() == 0) {
				maxSP = (String) jsonObject.get("thermostateMaxSetPoint");
				String minSP = (String) jsonObject.get("thermostateMinSetPoint");
				if (minSP != null && minSP.trim().length() > 0) {
					addSiteRequest.setMinSP(minSP);
				} else {
					errors.append(",Failed. Min SP is a mandatory field");
				}
			}
			if (addSiteRequest.getMaxSP() == null || addSiteRequest.getMaxSP().trim().length() == 0) {
				if (maxSP != null && maxSP.trim().length() > 0) {
					addSiteRequest.setMaxSP(maxSP);
				} else {
					errors.append(",Failed. Max SP is a mandatory field");
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private boolean validatePhoneNo(Row row, CellStyle style, CellStyle cellStyleForFailed, StringBuilder errors,
			int siteCell, Cell cell, String sitePhone, String numtype) throws Exception {
		if (sitePhone == null || sitePhone.trim().length() == 0 || sitePhone == "" || sitePhone.isEmpty()) {
			errors.append(","+numtype+" field is mandatory.");
			cell.setCellStyle(cellStyleForFailed);
			row.getCell(siteCell).setCellStyle(style);
			return false;
		} else if (sitePhone.trim().length() > 10 || sitePhone.trim().length() < 10 || CommonUtility.phoneValidate(sitePhone).equals("Yess")) {
			errors.append(",Incorrect "+numtype+" format");
			cell.setCellStyle(cellStyleForFailed);
			row.getCell(siteCell).setCellStyle(style);
			return false;
		}  
		return true;
	}

	private int getModeBasedOnFlag(String stringValue) {
		int flag;
		if (StringUtils.equalsIgnoreCase(stringValue.toUpperCase(), "Yes")) {
			flag = 1;
		} else {
			flag = 0;
		}
		return flag;
	}

	private void setFanPreferences(UpdateSiteRequest addSiteRequest, String fanOn) {
		int siteFanOn;
		int siteFanAuto;
		if (StringUtils.equalsIgnoreCase(fanOn.toUpperCase(), "Fan On")) {
			siteFanOn = 1;
			siteFanAuto = 0;
		} else if (StringUtils.equalsIgnoreCase(fanOn.toUpperCase(), "Fan Auto")) {
			siteFanAuto = 1;
			siteFanOn = 0;
		} else {
			siteFanOn = 0;
			siteFanAuto = 1;
		}
		addSiteRequest.setFanOn(siteFanOn);
		addSiteRequest.setFanAuto(siteFanAuto);
	}
	
	private void setLockPreferences(UpdateSiteRequest addSiteRequest, String fanOn) {
		int lockStatus = 1;
		if (StringUtils.equalsIgnoreCase(fanOn.toUpperCase(), "LOCK")) {
			lockStatus = 1;
		} else if (StringUtils.equalsIgnoreCase(fanOn.toUpperCase(), "UNLOCK")) {
			lockStatus = 0;
		} else if (StringUtils.equalsIgnoreCase(fanOn.toUpperCase(), "FULL")) {
			lockStatus = 2;
		}
		addSiteRequest.setLock(lockStatus);
	}

	private StringBuilder validateAndSetStoreHours(Map<Integer, String> excelHeaderValues, int maxNumOfCells, Row row,
			CellStyle cellStyleForFailed, Cell cell, CellStyle style, StringBuilder errors, String storeHours, String field, int siteCell) {
		StringBuilder siteHoursListStr = new StringBuilder();
		try {
			if (storeHours != null && !storeHours.trim().isEmpty()) {
				String seperatedHours = storeHours.replaceAll("\\r\\n|\\r|\\n", "& ");
				String[] strArray = seperatedHours.trim().split("&");
				for (int hour = 0; hour <= strArray.length - 1; hour++) {
					String siteStoreHoursFormat = strArray[hour];
					siteStoreHoursFormat = siteStoreHoursFormat.replaceAll("\\s+","");
					boolean validStoreHours = TimeFormatValidator.validateTimeFormat(siteStoreHoursFormat.trim().toLowerCase());
					if (validStoreHours) {
						validateStoreHours(siteHoursListStr, siteStoreHoursFormat);
					} else {
						errors.append(", Invalid "+field+" hours format");
						cell.setCellStyle(cellStyleForFailed);
						row.getCell(siteCell).setCellStyle(style);
						break;
					}
				}
				if (siteHoursListStr.length() > 0) {
					siteHoursListStr.replace(siteHoursListStr.length() - 1, siteHoursListStr.length(), "");
				}
			} 
		} catch (Exception e) {
			logger.error("", e);
			populateErrorMessage(excelHeaderValues, maxNumOfCells, row, cellStyleForFailed, cell, style, "FAILED. Please provide valid store hours format");
		}
		return siteHoursListStr;
	}

	private void validateAndSetZipcode(Map<Integer, String> excelHeaderValues, int maxNumOfCells, Row row,
			UpdateSiteRequest addSiteRequest, DataFormatter formatter, CellStyle cellStyleForFailed, int siteCell,
			Cell cell, String cityName, StringBuilder errors, CellStyle style) throws NumberFormatException, Exception {
		
		String siteMandatoryZipCode = formatter.formatCellValue(row.getCell(siteCell)).trim();
		if (siteMandatoryZipCode.trim().length() == 0) {
			errors.append(",Zip Code is a mandatory field");
			cell.setCellStyle(cellStyleForFailed);
			row.getCell(siteCell).setCellStyle(style);
		} else if ("0".equals(siteMandatoryZipCode)) {
			errors.append(",Zip code should not be zero");
			cell.setCellStyle(cellStyleForFailed);
			row.getCell(siteCell).setCellStyle(style);
		} else if (siteMandatoryZipCode.length() > 6) {
			errors.append(",Zip Code should not be more than 6 digits");
			cell.setCellStyle(cellStyleForFailed);
			row.getCell(siteCell).setCellStyle(style);
		} else if (!siteMandatoryZipCode.chars().allMatch(Character::isDigit)) {
			errors.append(",Zip Code should be numeric");
			cell.setCellStyle(cellStyleForFailed);
			row.getCell(siteCell).setCellStyle(style);
		} else {
			Response response = getGeoCodeData(siteMandatoryZipCode);
			
			JSONArray jsonArray = (JSONArray)response.getData();
			
			if (!jsonArray.isEmpty()) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(0);
				long stateID = Long.parseLong(jsonObject.get("state_id").toString());
				String stateName = jsonObject.get("state_name").toString().trim();
				String stateShortCode = (String) jsonObject.get("state_short_code");
				String timeZone = (String) jsonObject.get("timezone");
				
				if (stateName.equalsIgnoreCase(addSiteRequest.getSiteStateName()) || stateShortCode.equalsIgnoreCase(addSiteRequest.getSiteStateName())) {
					addSiteRequest.setSiteState((int)stateID);
				} else {
					errors.append(",Invalid State. Suggested state:").append(stateName);
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				
				addSiteRequest.setSiteZipCode(siteMandatoryZipCode);
				addSiteRequest.setSiteTimeZone(timeZone);
				
				String timeZoneShortStd = TimeZone.getTimeZone(timeZone).getDisplayName(false, TimeZone.SHORT);
				addSiteRequest.setSiteTimeZoneStd(timeZoneShortStd);
				String timeZoneShortDls = TimeZone.getTimeZone(timeZone).getDisplayName(true, TimeZone.SHORT);
				addSiteRequest.setSiteTimeZoneDls(timeZoneShortDls);
			} else {
				errors.append(",Invalid Zip code");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			}
			
			boolean isValidCity = false;
			StringBuilder cities = new StringBuilder();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				String jsonCityName = jsonObject.get("city_name").toString().trim();
				long cityID = Long.parseLong(jsonObject.get("city_id").toString());
				
				cities.append(jsonCityName).append(";");
				if (jsonCityName != null && jsonCityName.equalsIgnoreCase(cityName)) {
					addSiteRequest.setSiteCity((int)cityID);
					isValidCity = true;
				}
			}
			
			if (!jsonArray.isEmpty() && !isValidCity) {
				errors.append(",Invalid City. Suggested cities:").append(cities.toString());
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			}
		}
	}

	private void populateErrorMessage(Map<Integer, String> excelHeaderValues, int maxNumOfCells, Row row,
			CellStyle cellStyleForFailed, Cell cell, CellStyle style, String error) {
		for (int siteSuccess = 0; siteSuccess < maxNumOfCells; siteSuccess++) {
			if (excelHeaderValues.get(siteSuccess).equalsIgnoreCase(CommonConstants.RESULT)) {
				if (row.getCell(siteSuccess) == null) {
					row.createCell(siteSuccess).setCellValue(error);
				} else {
					row.getCell(siteSuccess).setCellValue(error);
				}
				if (cell != null) {
					cell.setCellStyle(cellStyleForFailed);
				}
				row.getCell(siteSuccess).setCellStyle(style);
			}
		}
	}

	private void validateGroups(int customerId, Map<Integer, String> excelHeaderValues, int maxNumOfCells,
			CellStyle cellStyleForFailed, Row row, UpdateSiteRequest addSiteRequest, String groupNames,int userId)
			throws VEMAppException {
		String dbGroups = siteDao.getGroupsByCustomer(customerId);
		List<String> validGroupIdsArr = new ArrayList<>();
		String[] dbIdNameGroupsArr = dbGroups.trim().split(",");
		List<String> newGroups = new ArrayList<>();
		String[] excelGroupList = groupNames.trim().split(",");
		List<String> dbGroupIdsList = new ArrayList<>();
		List<String> dbGroupNamesList = new ArrayList<>();
		Map<String,String> groupIdNameMap = new HashMap<>();
		
		if(StringUtils.isNotBlank(dbGroups)){
			for (String groupIdName : dbIdNameGroupsArr) {
				dbGroupNamesList.add(groupIdName.split("~")[0].trim().toLowerCase());
				dbGroupIdsList.add(groupIdName.split("~")[1].trim());
				groupIdNameMap.put(groupIdName.split("~")[0].trim().toLowerCase(), groupIdName.split("~")[1].trim());
			}
		}
		
		for (String groupName : excelGroupList) {

			logger.info("Checking the group with name " + groupName);

			if (dbGroupNamesList.contains(groupName.trim().toLowerCase())) {
				validGroupIdsArr.add(groupIdNameMap.get(groupName.trim().toLowerCase()));
			} else if (!groupName.isEmpty() && groupName.length() > 0) {
				logger.info("Creating a new group with group name " + groupName);
				newGroups.add(groupName.trim());
			}
		}
		addSiteRequest.setSiteGroups(validGroupIdsArr);
		addSiteRequest.setNewGroups(newGroups);
	}

	private void validateStoreHours(StringBuilder siteHoursListStr, String siteStoreHoursFormat) {
		String siteDays = siteStoreHoursFormat.split(":")[0];
		String[] siteDay = siteDays.split(",");
		String siteHours = siteStoreHoursFormat.split(":")[1];
		String openHour;
		String closedHour;
		int hoursCount = StringUtils.countMatches(siteStoreHoursFormat, ":");
		if ((siteStoreHoursFormat.toLowerCase().contains("am") || siteStoreHoursFormat.toLowerCase().contains("pm")) && hoursCount > 1) {
			int pos = siteStoreHoursFormat.indexOf(':');
			String amWith12Hour = siteStoreHoursFormat.substring(pos + 1, siteStoreHoursFormat.length());
			openHour = amWith12Hour.trim().split("-")[0].trim();
			closedHour = amWith12Hour.trim().split("-")[1].trim();
		} else if (siteStoreHoursFormat.toLowerCase().contains("am") || siteStoreHoursFormat.toLowerCase().contains("pm")) {
			openHour = siteHours.split("-")[0].trim();
			closedHour = siteHours.split("-")[1].trim();
		} else {
			openHour = siteHours.trim().split("-")[0].trim();
			closedHour = siteHours.trim().split("-")[1].trim();
		}
		for (String selectedDay : siteDay) {
			String day = selectedDay.toUpperCase().trim();
			openHour = openHour.toUpperCase().trim();
			closedHour = closedHour.toUpperCase().trim();
			switch (day) {
			case "MON":
				siteHoursListStr.append(1 + "~" + openHour + "~" + closedHour + ",");				
				break;
			case "TUE":
				siteHoursListStr.append(2 + "~" + openHour + "~" + closedHour + ",");				
				break;
			case "WED":
				siteHoursListStr.append(3 + "~" + openHour + "~" + closedHour + ",");				
				break;
			case "THU":
				siteHoursListStr.append(4 + "~" + openHour + "~" + closedHour + ",");				
				break;
			case "FRI":
				siteHoursListStr.append(5 + "~" + openHour + "~" + closedHour + ",");				
				break;
			case "SAT":
				siteHoursListStr.append(6 + "~" + openHour + "~" + closedHour + ",");				
				break;
			case "SUN":
				siteHoursListStr.append(7 + "~" + openHour + "~" + closedHour + ",");				
				break;
			default:
				break;			
			}
		}
	}
	
	private long siteUpdate(int customerId, Map<Integer, String> excelHeaderValues, BulkUploadResponse bulkUploadResponse,
			 int maxNumOfCells, Workbook workbook, CellStyle cellStyleForFailed, Row row, int userId, CellStyle cellStyleForSuccess,
			 long siteId, String componentValue) throws Exception {
		long insertId = 0;
		StringBuilder errors = new StringBuilder();
		try {
			CellStyle style = createCellStyle(workbook);
			
			UpdateSiteRequest addSiteRequest = new UpdateSiteRequest();
			DataFormatter formatter = new DataFormatter();
			int siteCell = 0;
			RTURequest rtuRequest = new RTURequest();
			ThermostatRequest thermoRequest = new ThermostatRequest();
			Cell cell;
			for( int cellCounters = 0; cellCounters < maxNumOfCells; cellCounters ++) {
				
				if (row.getCell(siteCell) == null) {
					cell = row.createCell(siteCell);
				} else {
					cell = row.getCell(siteCell);
				}
				
				if (componentValue.equalsIgnoreCase("hvac")) {				
					//getting hvac details started
					validateAndSetHvacDetails(excelHeaderValues, cellStyleForFailed, row, style, errors, formatter, siteCell, rtuRequest, cell, siteId);				
					if (excelHeaderValues.get(siteCell).equalsIgnoreCase("How do employees believe the system has been running?")) {
						String empBelieveSystem = formatter.formatCellValue(row.getCell(siteCell));
						addSiteRequest.setEmpBelieveSystem(empBelieveSystem);
					}
				} else if (componentValue.equalsIgnoreCase("thermostat")) {
					//getting Thermostat details started
					validateAndSetThermostatDetails(excelHeaderValues, row, formatter, siteCell, thermoRequest, errors, cell, cellStyleForFailed, style);
				}
				
				siteCell ++;
			}
			
			addSiteRequest.setCustomerId(customerId);
			addSiteRequest.setUserId(userId);
			addSiteRequest.setSiteId((int)siteId);
			
			if (componentValue.equalsIgnoreCase("hvac")) {
				makeRTURequestAsString(addSiteRequest, rtuRequest);
				addSiteRequest.setThermostatDetails("");
			} else if (componentValue.equalsIgnoreCase("thermostat")) {
				makeThermoRequestAsString(addSiteRequest, thermoRequest);
				addSiteRequest.setHvacDetails("");
			}
			
			if (errors.toString().trim().length() > 0) {
				bulkUploadResponse.setInsertRecord(CommonConstants.NOT_VALID_RECORD);
				appendErrorsToExcel(excelHeaderValues, maxNumOfCells, cellStyleForFailed, row, errors);			
			}
			
			if(errors.toString().trim().length()==0) {
				JSONObject json = siteDao.updateBulkUploadSites(addSiteRequest, userId);
				insertId = (Integer)json.get("insertId");
				String errorMessage =(String)json.get("errorMsg");
				if (insertId > 0) {
					bulkUploadResponse.setRecordResponse("Record Inserted");
					bulkUploadResponse.setSucessCount(bulkUploadResponse.getSucessCount() + 1);
					for (int siteSucces = 0; siteSucces < maxNumOfCells; siteSucces++) {
						if (excelHeaderValues.get(siteSucces).equalsIgnoreCase(CommonConstants.RESULT)) {
							row.getCell(siteSucces).setCellValue("SUCCESS");
							row.getCell(siteSucces).setCellStyle(cellStyleForSuccess);
						}
					}				
				} else {
					for (int siteSucces = 0; siteSucces < maxNumOfCells; siteSucces++) {
						if (excelHeaderValues.get(siteSucces).equalsIgnoreCase(CommonConstants.RESULT)) {
							if (errorMessage.split(":")[1].contains("Data truncated for column 'stt")) {
								row.getCell(siteSucces).setCellValue("FAILED, Incorrect store hours format.");
								row.getCell(siteSucces).setCellStyle(cellStyleForFailed);
							} else if (errorMessage.split(":")[1].contains("Cannot add or update a child row")) {
								row.getCell(siteSucces).setCellValue("FAILED, Invalid address entered.");
								row.getCell(siteSucces).setCellStyle(cellStyleForFailed);
							} else {
								row.getCell(siteSucces).setCellValue("FAILED," + errorMessage.split(":")[1]);
								row.getCell(siteSucces).setCellStyle(cellStyleForFailed);
							}
						}
					}
				}			
				System.out.println("@@@@SiteUpdate***siteId***" + siteId);
			}
		} catch (Exception e) {
			if (errors.toString().trim().length() > 0) {
				bulkUploadResponse.setInsertRecord(CommonConstants.NOT_VALID_RECORD);
				appendErrorsToExcel(excelHeaderValues, maxNumOfCells, cellStyleForFailed, row, errors);			
			}
			logger.error("SiteServiceImpl siteUpload Error:",e);
			throw e;
		}
		return insertId;
	}

	private void validateAndSetThermostatDetails(Map<Integer, String> excelHeaderValues, Row row,  DataFormatter formatter, 
			int siteCell, ThermostatRequest thermoRequest, StringBuilder errors, Cell cell,CellStyle cellStyleForFailed,CellStyle style) throws Exception {

		String headerName = excelHeaderValues.get(siteCell).toUpperCase();
		
		switch (headerName) {
		
		case "HVAC UNIT #":
			String hvacUnit = formatter.formatCellValue(row.getCell(siteCell));
			thermoRequest.sethVACUnit(hvacUnit);
			break;
		case "THERMOSTAT UNIT #":
			String hUnit = formatter.formatCellValue(row.getCell(siteCell));
			if (hUnit.trim().length() != 0) {
				if (hUnit.trim().length() > 48) {
					errors.append(",Thermostat Unit should not exceed 48 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				thermoRequest.setUnit(hUnit);
			}
			break;
		case "OTHER LOCATION":
			String otherLocation = formatter.formatCellValue(row.getCell(siteCell));
			if (thermoRequest.getLocationType() != null && thermoRequest.getLocationType() == 23) {
				if (otherLocation.trim().length() > 48) {
					errors.append(",Other Location should not exceed 50 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				thermoRequest.setOtherLocation(otherLocation);
			}
			break;
		case "LOCATION OF THERMOSTAT":
			String thermoLocation = formatter.formatCellValue(row.getCell(siteCell));
			setThermoLocation(thermoRequest, thermoLocation);
			if (thermoRequest.getLocationType() == null || thermoRequest.getLocationType() <= 0) {
				errors.append(",Thermostat Location is invalid");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			}
			break;
		case "IS THE MOUNTING SPACE LARGE ENOUGH?":
			String mountSpaceEnough = formatter.formatCellValue(row.getCell(siteCell));
			if ("Yes".equalsIgnoreCase(mountSpaceEnough)) {
				thermoRequest.setSpaceEnough(1);
			} else if ("No".equalsIgnoreCase(mountSpaceEnough)) {
				thermoRequest.setSpaceEnough(0);
			}
			break;
		case "MAKE OF THERMOSTAT":
			String makeThermo = formatter.formatCellValue(row.getCell(siteCell));
			if (makeThermo.trim().length() > 48) {
				errors.append(",Make of Thermostat should not exceed 48 characters");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			}
			thermoRequest.setMake(makeThermo);
			break;
		case "MODEL OF THERMOSTAT":
			String modelThermo = formatter.formatCellValue(row.getCell(siteCell));
			if (modelThermo.trim().length() > 48) {
				errors.append(",Model of Thermostat should not exceed 48 characters");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			}
			thermoRequest.setModel(modelThermo);
			break;
		case "PLEASE DESCRIBE CURRENT WIRING CONFIGURATION OF THERMOSTATS":
			String wiringConfigure = formatter.formatCellValue(row.getCell(siteCell));
			if (wiringConfigure.trim().length() > 48) {
				errors.append(",Wiring Configuration Description should not exceed 48 characters");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			}
			thermoRequest.setWiringConfigThermostat(wiringConfigure);
			break;
		case "CONFIRM 24VDC BETWEEN THE RC/R AND C":
			String confirm24VDC = formatter.formatCellValue(row.getCell(siteCell));
			if ("Yes".equalsIgnoreCase(confirm24VDC)) {
				thermoRequest.setrCAndCPower(1);
			} else if ("No".equalsIgnoreCase(confirm24VDC)) {
				thermoRequest.setrCAndCPower(0);
			}
			break;
		case "IF THERE IS 'C' WIRE ATTACHED":
			String cWireAttach = formatter.formatCellValue(row.getCell(siteCell));
			if ("Yes".equalsIgnoreCase(cWireAttach)) {
				thermoRequest.setcWireAttached(1);
			} else if ("No".equalsIgnoreCase(cWireAttach)) {
				thermoRequest.setcWireAttached(0);
			}
			break;
		case "IF THERE IS NO 'C' ATTACHED, CHECK IF THERE IS A SPARE UNUSED WIRE IN THE BUNDLE OF HVAC WIRING":
			String noCAttached = formatter.formatCellValue(row.getCell(siteCell));
			if ("Yes".equalsIgnoreCase(noCAttached)) {
				thermoRequest.setNoCWireAttached(1);
			} else if ("No".equalsIgnoreCase(noCAttached)) {
				thermoRequest.setNoCWireAttached(0);
			}
			break;
		case "DOES THERMOSTAT HAVE AN AUTOMATED SCHEDULE ?":
			String autoSchedudle = formatter.formatCellValue(row.getCell(siteCell));
			if (autoSchedudle == null || autoSchedudle.isEmpty() || "No".equalsIgnoreCase(autoSchedudle)) {
				thermoRequest.setAutomatedSchedule(0);
			} else {
				thermoRequest.setAutomatedSchedule(1);
			}
			break;
		case "SCHEDULE DETAILS":
			String schedudle = formatter.formatCellValue(row.getCell(siteCell));
			if (schedudle.trim().length() > 500) {
				errors.append(",Schedule Details should not exceed 500 characters");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			}
			thermoRequest.setAutomatedScheduleNote(schedudle);
			break;
		case "LOCATION OF REMOTE SENSORS FOR THIS THERMOSTAT":
			String remoteSensors = formatter.formatCellValue(row.getCell(siteCell));
			thermoRequest.setLocationOfRemoteSensor(remoteSensors);
			break;
		case "IF SENSORS ARE PRESENT, VALIDATE WHICH SENSOR IS CONNECTED TO WHICH THERMOSTAT":
			String validateSensor = formatter.formatCellValue(row.getCell(siteCell));
			if (validateSensor.trim().length() > 48) {
				errors.append(",Validate Sensor should not exceed 48 characters");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			}
			thermoRequest.setValidateSensor(validateSensor);
			break;
		case "PLEASE DESCRIBE WIRING TO CURRENT SENSOR IF APPLICABLE":
			String currentSensor = formatter.formatCellValue(row.getCell(siteCell));
			if (StringUtils.isNotEmpty(currentSensor)) {
				if (currentSensor.trim().length() > 1024) {
					errors.append(",Current Wiring Sensor should not exceed 1024 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				thermoRequest.setWiringConfigSensor(currentSensor);
			}
			break;
		default:
			break;
			
		}
	}

	private void validateAndSetHvacDetails(Map<Integer, String> excelHeaderValues, CellStyle cellStyleForFailed,  Row row, 
			CellStyle style, StringBuilder errors, DataFormatter formatter, int siteCell, RTURequest rtuRequest, Cell cell, long siteId) throws Exception {

		String headerName = excelHeaderValues.get(siteCell).toUpperCase();

		switch (headerName) {
		
		case "MODEL OF THE HVAC":
			String modelOfRTU = formatter.formatCellValue(row.getCell(siteCell));
			if (modelOfRTU.trim().length() > 48) {
				errors.append(",Model of The HVAC should not exceed 48 characters");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			}
			rtuRequest.setModel(modelOfRTU);
			break;

		case "UNIT #":
			String hVACUnitNumber = formatter.formatCellValue(row.getCell(siteCell));
			if (hVACUnitNumber.trim().length() != 0) {
				if (hVACUnitNumber.trim().length() > 48) {
					errors.append(",Unit should not exceed 48 characters");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				}
				boolean isDuplicate = siteDao.isHvacUnitDuplicate(siteId, hVACUnitNumber);
				if (isDuplicate) {
					errors.append(",#Unit number is duplicate.");
					cell.setCellStyle(cellStyleForFailed);
					row.getCell(siteCell).setCellStyle(style);
				} else {
					rtuRequest.setUnit(hVACUnitNumber);
				}
			}
			break;
		case "SELECT WHERE IT’S LOCATED":
			String hvacLocation = formatter.formatCellValue(row.getCell(siteCell));
			if (StringUtils.isNotEmpty(hvacLocation) && rtuRequest != null) {
				if ("Roof Top".equals(hvacLocation)) {
					rtuRequest.setLocation(1);
				} else if ("Closet".equals(hvacLocation)) {
					rtuRequest.setLocation(2);
				} else if ("Back of the building".equals(hvacLocation)) {
					rtuRequest.setLocation(3);
				}
			}
			break;
			
		case "HEATING TEMPERATURE":
			String heatTemp = formatter.formatCellValue(row.getCell(siteCell));
			if ((heatTemp.trim().length() != 0) && !heatTemp.matches("[0-9.]*")) {
				errors.append(",Heating Temperature field should accept only numeric values");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			} else {
				rtuRequest.setHeating(heatTemp);
			}
			break;

		case "COOLING TEMPERATURE":
			String coolTemp = formatter.formatCellValue(row.getCell(siteCell));
			if ((coolTemp.trim().length() != 0) && !coolTemp.matches("[0-9.]*")) {
				errors.append(",Cooling Temperature field should accept only numeric values");
				cell.setCellStyle(cellStyleForFailed);
				row.getCell(siteCell).setCellStyle(style);
			} else {
				rtuRequest.setCooling(coolTemp);
			}
			break;
			
		default:
			break;
			
		}
	}

	/*
	 * Making the Thermostat List as String using '~#~' and '~!~'
	 * separators if the user selected any HVAC.
	 */
	private void makeThermoRequestAsString(UpdateSiteRequest addSiteRequest, ThermostatRequest thermoRequest) {
		StringBuilder thermostatDetails = new StringBuilder();

		if (thermoRequest != null) {
			thermostatDetails.append(CommonUtility.isNull(thermoRequest.getUnit()) + "~!~" + 
					CommonUtility.isNull("") + "~!~" + CommonUtility.isNull(thermoRequest.getSpaceEnough()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getMake()) + "~!~" 
					+ CommonUtility.isNull(thermoRequest.getModel()) + "~!~" 
					+ CommonUtility.isNull(thermoRequest.gethVACUnit()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getWiringConfigThermostat()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getWiringThermostatImage()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getrCAndCPower()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getcWireAttached()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getNoCWireAttached()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getAutomatedSchedule()) + "~!~"
					+ ((thermoRequest.getAutomatedSchedule() != null && thermoRequest.getAutomatedSchedule() == 1) ?
							CommonUtility.isNull(thermoRequest.getAutomatedScheduleNote()) : "") + "~!~"
					+ CommonUtility.isNull(thermoRequest.getLocationOfRemoteSensor()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getValidateSensor()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getWiringConfigSensor()) + "~!~" + CommonUtility.isNull("") + "~!~"
					+ CommonUtility.isNull(thermoRequest.getLocationType()) + "~!~"
					+ CommonUtility.isNull(thermoRequest.getOtherLocation()) + "~!~" + "~#~");
	
			/*
			 * Trimming the last unnecessary '~#~'.
			 */
			thermostatDetails.replace(thermostatDetails.length() - 3, thermostatDetails.length(), "");
		}
		addSiteRequest.setThermostatDetails(thermostatDetails.toString());
	}

	/*
	 * Making the HVAC List as String using '~#~' and '~!~'
	 * separators if the user selected any HVAC.
	 */
	private void makeRTURequestAsString(UpdateSiteRequest addSiteRequest, RTURequest rtuRequest) {
		StringBuilder hvacDetails = new StringBuilder();
		if (rtuRequest != null) {
			hvacDetails.append(CommonUtility.isNull(rtuRequest.getModel()) + "~!~"
					+ CommonUtility.isNull(rtuRequest.getUnit()) + "~!~" + CommonUtility.isNull(rtuRequest.getLocation())
					+ "~!~" + CommonUtility.isNull(rtuRequest.getHeating()) + "~!~"
					+ CommonUtility.isNull(rtuRequest.getCooling()) + "~#~");
	
			/*
			 * Trimming the last unnecessary '~#~'.
			 */
			hvacDetails.replace(hvacDetails.length() - 3, hvacDetails.length(), "");
		}
		addSiteRequest.setHvacDetails(hvacDetails.toString());
	}

	private int getBuildingType(String buildingLocation) {
		String buildingLocation1 = buildingLocation.toUpperCase();
		
		switch (buildingLocation1) {
		case "STANDALONE":
			return 1;
		case "CAMPUS COMPLEX":
			return 2;
		case "MULTI-STORY":
			return 3;
		case "MALL/STRIP MALL":
			return 4;

		default:
			break;
		}
		return 0;		
	}
	
	private void setThermoLocation(ThermostatRequest thermoRequest, String thermoLocation) {
		String thermoLocation1 = thermoLocation.toUpperCase();
		
		switch (thermoLocation1) {
		case "BANQUET ROOM":
			thermoRequest.setLocationType(1);
			break;
		case "CALL CENTER":
			thermoRequest.setLocationType(2);
			break;
		case "COUNTER":
			thermoRequest.setLocationType(3);
			break;
		case "DINING ROOM":
			thermoRequest.setLocationType(4);
			break;
		case "DINING ROOM LEFT":
			thermoRequest.setLocationType(5);
			break;
		case "DINING ROOM RIGHT":
			thermoRequest.setLocationType(6);
			break;
		case "DINING ROOM 1":
			thermoRequest.setLocationType(7);
			break;
		case "DINING ROOM 2":
			thermoRequest.setLocationType(8);
			break;
		case "KITCHEN":
			thermoRequest.setLocationType(9);
			break;
		case "KITCHEN FRONT":
			thermoRequest.setLocationType(10);
			break;
		case "KITCHEN BACK":
			thermoRequest.setLocationType(11);
			break;
		case "LOBBY":
			thermoRequest.setLocationType(12);
			break;
		case "OFFICE":
			thermoRequest.setLocationType(13);
			break;
		case "STORAGE AREA":
			thermoRequest.setLocationType(14);
			break;
		case "BACKROOM":
			thermoRequest.setLocationType(15);
			break;
		case "FRONT":
			thermoRequest.setLocationType(16);
			break;
		case "BACK":
			thermoRequest.setLocationType(17);
			break;
		case "LEFT":
			thermoRequest.setLocationType(18);
			break;
		case "RIGHT":
			thermoRequest.setLocationType(19);
			break;
		case "RESTROOM":
			thermoRequest.setLocationType(20);
			break;
		case "ENTRY":
			thermoRequest.setLocationType(21);
			break;
		case "MAIN":
			thermoRequest.setLocationType(22);
			break;
		case "OTHER":
			thermoRequest.setLocationType(23);
			break;

		default:
			break;
		}
		
	}


	public String checkRowEmpty(Row row, String template) throws VEMAppException {
		logger.info("SiteServiceImpl checkRowEmpty start");
		String result = "No";
		List<String> rowDataList = null;
		String cellValue = "";
		int lastCellIndex = 0;
		try {
			rowDataList = new ArrayList<>();
			if ("Sites_Bulk_Import_Template".equalsIgnoreCase(template)) {
				lastCellIndex = 20;
			}
			for (int i = 0; i < lastCellIndex; i++) {
				Cell cell;
				if (row.getCell(i) == null) {
					cell = row.createCell(i);
				} else {
					cell = row.getCell(i);
				}
				// getting the cell data
				int cellType = cell.getCellType();
				if (cellType == XSSFCell.CELL_TYPE_STRING) {
					cellValue = cell.getStringCellValue().trim();
				} else if (cellType == XSSFCell.CELL_TYPE_NUMERIC) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cellValue = cell.getStringCellValue();
				} else if (cellType == XSSFCell.CELL_TYPE_BOOLEAN) {
					cellValue = cell.getBooleanCellValue() + "".trim();
				} else if (cellType == XSSFCell.CELL_TYPE_BLANK) {
					cellValue = "";
				}
				if (cellValue.trim().length() > 0)
					rowDataList.add(cellValue);
			}
			if (rowDataList.isEmpty())
				result = "Yes";
		} catch (Exception e) {
			logger.error("SiteServiceImpl checkRowEmpty Error:", e);
			throw new VEMAppException("checkRowEmpty", e);
		}
		logger.info("SiteServiceImpl checkRowEmpty end");
		return result;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Response getGeoCodeData(String zipCode) throws VEMAppException {
		Response response = new Response();
		JSONArray resultArray = new JSONArray();
		try {
			JSONArray geoCodeDataArray = siteDao.getGeoCodeData(zipCode);
			if(geoCodeDataArray != null && !geoCodeDataArray.isEmpty()) {
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.SITE_GETGEOCODEDATA_SUCCESS);
				response.setData(geoCodeDataArray);
			} else {
				String geoDataJsonString = getGeoCodeDataByZipCode(zipCode);
				System.out.println(geoDataJsonString);

				// Converting the geoDataJsonString String to JsonNode
				JsonNode geoDataJsonNode = new ObjectMapper().readValue(geoDataJsonString, JsonNode.class);

				// Reading the Status from geoDataJsonNode
				String geoDataStatus = geoDataJsonNode.get("status").asText();

				// Validating the geoDataStatus
				if (geoDataStatus == null || !"OK".equalsIgnoreCase(geoDataStatus)) {
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.GET_GEOCODEDATA_DETAILS_FAILED);
					response.setData(resultArray);
				} else if (geoDataStatus != null && "OK".equalsIgnoreCase(geoDataStatus)) {

					String state = null;
					String stateShorCode = null;
					String post_Code = null;
					String city = null;
					ArrayList cities = null;

					JsonNode postcodeLocalitiesNode = geoDataJsonNode.get("results").get(0).get("postcode_localities");
					if (postcodeLocalitiesNode != null) {
						cities = new ObjectMapper().readValue(postcodeLocalitiesNode, ArrayList.class);
					}

					JsonNode addressComponentsNode = geoDataJsonNode.get("results").get(0).get("address_components");

					List<String> cityList = null;
					if (addressComponentsNode.isArray()) {
						cityList = new ArrayList<>();
						for (final JsonNode addressComponentNode : addressComponentsNode) {
							System.out.println(addressComponentNode);
							ArrayList typesArrayList = new ObjectMapper().readValue(addressComponentNode.get("types"),
									ArrayList.class);

							if (typesArrayList.contains("administrative_area_level_1")) {
								state = addressComponentNode.get("long_name").asText();
								stateShorCode = addressComponentNode.get("short_name").asText();
							}

							if (typesArrayList.contains("locality")) {
								cityList.add(addressComponentNode.get("long_name").asText());
							}

							if (typesArrayList.contains("neighborhood")) {
								cityList.add(addressComponentNode.get("long_name").asText());
							}

							if (typesArrayList.contains("postal_code")) {
								post_Code = addressComponentNode.get("long_name").asText();
							}
						}
					}
					
					// Reading the location JsonNode from geoDataJsonNode
					JsonNode locationNode = geoDataJsonNode.get("results").get(0).get("geometry").get("location");
					
					// Reading the lat info form locationNode
					String latitude = locationNode.get("lat").asText();
					// Reading the lng info form locationNode
					String longitude = locationNode.get("lng").asText();
					// Getting the current Time Stamp
					long epoch = (new Date().getTime()) / 1000;
					
					logger.info("state : " + state);
					logger.info("stateShorCode :  " + stateShorCode);
					logger.info("post_Code  : " + post_Code);
					logger.info("city  : " + city);
					logger.info("cities  : " + cities);
					
					String timeZoneLongName = getTimeZone(latitude, longitude, epoch);
					
					if(timeZoneLongName != null) {
						if (cities != null && !cities.isEmpty()) {
							for(final Object cityString : cities) {
								JSONObject resultJSon = siteDao.insertGeoCodeData(state, stateShorCode, (String)cityString, zipCode, latitude, longitude, timeZoneLongName);
								resultArray.add(resultJSon);
							}
						} else if(cityList  != null && !cityList.isEmpty()) {
							for(final String cityString : cityList) {
								JSONObject resultJSon = siteDao.insertGeoCodeData(state, stateShorCode, (String)cityString, zipCode, latitude, longitude, timeZoneLongName);
								resultArray.add(resultJSon);
							}
						} else {
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.GET_GEOCODEDATA_DETAILS_FAILED);
							response.setData(resultArray);
						}
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.SITE_GETGEOCODEDATA_SUCCESS);
						response.setData(resultArray);
					} else {
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.GET_GEOCODEDATA_DETAILS_FAILED);
						response.setData(resultArray);
					}				
				}				
			}
		} catch (Exception e) {
			logger.error("SiteServiceImpl getGeoCodeData Error:", e);
			throw new VEMAppException("getGeoCodeData", e);
		}		
		return response;
	}
	
	/**
	 * 
	 * @param zipCode
	 * @return
	 * @throws Exception
	 */
	private String getGeoCodeDataByZipCode(String zipCode)  throws Exception {
		String geoDataJsonString = null;
		try {
			// Instantiating the HttpRestClient 
			HttpRestClient httpRestClient = new HttpRestClient();
			
			// Constructing the geoCodeAPI String using the MessageFormat
			String geoCodeAPI = MessageFormat.format(ConfigurationUtils.getConfig("geocode.api.zipcode.url"),
					URLEncoder.encode(zipCode, "UTF-8"),
					URLEncoder.encode(ConfigurationUtils.getConfig("geocode.api.key"), "UTF-8"));
			
			// Calling the sendGet method of HttpRestClient to get the geoData
			geoDataJsonString = httpRestClient.sendGet(geoCodeAPI);
		} catch(Exception exception) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_GEOCODEDATA_DETAILS_FAILED, logger, exception);
		}
		return geoDataJsonString;
	}
	
	/**
	 * 
	 * @param lat
	 * @param lng
	 * @param epoch
	 * @return
	 * @throws Exception
	 */
	private String getTimeZone(String lat, String lng, long epoch) throws Exception {
		String timeZoneLongName = null;
		try {
			// Instantiating the HttpRestClient
			HttpRestClient httpRestClient = new HttpRestClient();
			// Constructing the timeZoneAPI String using the MessageFormat
			String timeZoneAPI = MessageFormat.format(ConfigurationUtils.getConfig("timezone.api.url"), lat, lng, String.valueOf(epoch),
					URLEncoder.encode(ConfigurationUtils.getConfig("timezone.api.key"), "UTF-8"));

			// Calling the sendGet method of HttpRestClient to get the
			// timeZoneData
			String timeZoneDataJsonString = httpRestClient.sendGet(timeZoneAPI);
			System.out.println("timeZoneDataJsonString   "+timeZoneDataJsonString);
			// Converting the timeZoneResultJsonString String to JsonNode
			JsonNode timeZoneJsonNode = new ObjectMapper().readValue(timeZoneDataJsonString, JsonNode.class);

			// Reading the status from timeZoneJsonNode
			String timeZoneStatus = timeZoneJsonNode.get("status").asText();

			// Validating the timeZoneStatus
			if (timeZoneStatus != null && "OK".equalsIgnoreCase(timeZoneStatus)) {
				// Reading the timeZoneId form timeZoneJsonNode
				timeZoneLongName = timeZoneJsonNode.get("timeZoneId").asText();				
			}			
			System.out.println("timeZoneLongName   "+timeZoneLongName);
		} catch (Exception exception) {

		}
		return timeZoneLongName;
	}
}
