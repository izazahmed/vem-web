/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.daoimpl.site;

import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.iot.Weather;
import com.enerallies.vem.beans.site.ActivateOrDeActivateSiteRequest;
import com.enerallies.vem.beans.site.AddSiteRequest;
import com.enerallies.vem.beans.site.GetSiteRequest;
import com.enerallies.vem.beans.site.RTURequest;
import com.enerallies.vem.beans.site.SiteHoursRequest;
import com.enerallies.vem.beans.site.ThermostatRequest;
import com.enerallies.vem.beans.site.UpdateSiteRequest;
import com.enerallies.vem.business.WundergroundBusiness;
import com.enerallies.vem.dao.LookUpDao;
import com.enerallies.vem.dao.site.SiteDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.TableFieldConstants;
import com.enerallies.vem.weather.beans.WundergroundResponse;

/**
 * File Name : SiteDaoImpl 
 * 
 * SiteDaoImpl:Its an implementation class for SiteDao interface.
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
 *
 */

@Component
public class SiteDaoImpl implements SiteDao{
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(SiteDaoImpl.class);
	
	/*Constant variable declaration*/
	private static final String SITE_GROUPS = "siteGroups";
	private static final String RESULT_SET_1 = "#result-set-1";
	private static final String RESULT_SET_2 = "#result-set-2";
	private static final String RESULT_SET_3 = "#result-set-3";
	private static final String RESULT_SET_4 = "#result-set-4";
	private static final String RESULT_SET_5 = "#result-set-5";
	private static final String RESULT_SET_6 = "#result-set-6";
	private static final String RESULT_SET_7 = "#result-set-7";
	private static final String RESULT_SET_8 = "#result-set-8";
	private static final String VALUE = "value";
	private static final String LABLE = "lable";
	private static final String SITE_LIST="sitesList";
	private static final String CUSTOMER_ID="customer_id";
	
	@Autowired
	LookUpDao lookUpDao;
	
	@Autowired
	WundergroundBusiness wundergroundBusiness;
	
	/** Data source instance */
	private DataSource dataSource;
	
	/** JDBC Template instance */
	private JdbcTemplate jdbcTemplate;	
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	@Override
	public int addSite(AddSiteRequest addSiteRequest, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [addSite] [Site DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
		
		/*
		 * These two variables are used to make the newly added site group ids 
		 * into string bcz the MYSQL procedure do not accept the List,
		 * So separating the element using the ','.
		 */
		List<String> newSiteGroupIdsList = null;
		StringBuilder newSiteGroupIdsListStr = new StringBuilder();
		
		/*
		 * These Three variables are used to make the selected site hours 
		 * into string bcz the MYSQL procedure do not accept the List,
		 * So separating the element using the ','.
		 * Here each element is an object, So we are making all the object data
		 * into String using '~'.
		 */
		List<SiteHoursRequest> siteHoursList = null;
	    StringBuilder siteHoursListStr = new StringBuilder();
	    SiteHoursRequest siteHoursRequestObj = null;
		
	    /*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		/*
		 * Temporary variable for holding the fan status.
		 */
		int fanStatus = 0;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_insert_site");
		    
		    /*
		     * Making the Site Hours as String using ',' and '~' separators 
		     * if the user selected any siteHours.
		     */
		    siteHoursList = addSiteRequest.getSiteHours();
		    if(!siteHoursList.isEmpty()){
		    	for (int i = 0; i < siteHoursList.size(); i++) {
			    	siteHoursRequestObj = siteHoursList.get(i);
			    	siteHoursListStr.append(siteHoursRequestObj.getDayOfWeek()+"~"
			    	+siteHoursRequestObj.getOpenHrs()+"~"+siteHoursRequestObj.getCloseHrs()+",");
				}
			    
			    /*
			     * Trimming the last unnecessary ','.
			     */
			    siteHoursListStr.replace(siteHoursListStr.length()-1, siteHoursListStr.length(), "");
			    
		    }
		    
		    /*
		     * Making the newly added site groupids into a string and appending the each
		     * element using ','.
		     */
		    newSiteGroupIdsList = addSiteRequest.getSiteGroups();
		    if(!newSiteGroupIdsList.isEmpty()){
			    for (int i = 0; i < newSiteGroupIdsList.size(); i++) {
			    	newSiteGroupIdsListStr.append(newSiteGroupIdsList.get(i)+",");
				}
			    
			    /*
			     * Trimming the last unnecessary ','.
			     */
			    newSiteGroupIdsListStr.replace(newSiteGroupIdsListStr.length()-1, newSiteGroupIdsListStr.length(), "");
		    }
		    
		    /*
		     * if the fan on is 1 then fanStatus is 1
		     * else if fan auto is 1 then fanStatus is 1
		     * else fanStatus is 0
		     */
		    if(CommonUtility.isNull(addSiteRequest.getFanOn()) == 1)
		    	fanStatus = 1;
		    else if(CommonUtility.isNull(addSiteRequest.getFanAuto()) == 1)
		    	fanStatus = 2;
		    
		    String zipCode = addSiteRequest.getSiteZipCode();
		    int zipCodeLength = zipCode.length();
		    if(zipCodeLength < 5){
		    	int remainingZeros= 5 - zipCodeLength;
		    	String zeros="";
		    	for (int i = 1; i <= remainingZeros; i++) {
		    		zeros = zeros+"0";
				}
		    	zipCode =zeros + zipCode;
		    }
		    
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_site_name", CommonUtility.isNull(addSiteRequest.getSiteName()));
			inputParams.put(TableFieldConstants.IN_SITE_INTERNAL_ID, CommonUtility.isNull(addSiteRequest.getSiteInternalId()));
			inputParams.put("in_site_type_id", CommonUtility.isNull(addSiteRequest.getSiteType()));
			inputParams.put("in_site_type_other", CommonUtility.isNull(addSiteRequest.getSiteTypeNew()));
			inputParams.put("in_site_phone", CommonUtility.isNull(addSiteRequest.getSitePhNo()));
			inputParams.put("in_site_district", CommonUtility.isNull(addSiteRequest.getSiteDistrict()));
			inputParams.put("in_site_area", CommonUtility.isNull(addSiteRequest.getSiteArea()));
			inputParams.put("in_site_region", CommonUtility.isNull(addSiteRequest.getSiteRegion()));
			inputParams.put("in_site_grp_ids_str", CommonUtility.isNull(newSiteGroupIdsListStr.toString()));
			inputParams.put("in_site_degree_pref", CommonUtility.isNull(addSiteRequest.getDegreePreference()));
			inputParams.put("in_site_add1", CommonUtility.isNull(addSiteRequest.getSiteAddLine1()));
			inputParams.put("in_site_add2", CommonUtility.isNull(addSiteRequest.getSiteAddLine2()));
			inputParams.put("in_site_lat", CommonUtility.isNull(addSiteRequest.getLatitude()));
			inputParams.put("in_site_lang", CommonUtility.isNull(addSiteRequest.getLangitude()));
			inputParams.put("in_site_city_id", CommonUtility.isNull(addSiteRequest.getSiteCity()));
			inputParams.put("in_site_zip_code", CommonUtility.isNull(zipCode));
			inputParams.put("in_site_hours_str", siteHoursListStr);
			inputParams.put("in_fan_status", CommonUtility.isNull(fanStatus));
			inputParams.put("in_lock_status", CommonUtility.isNull(addSiteRequest.getLock()));
			inputParams.put("in_same_as_store", CommonUtility.isNull(addSiteRequest.getSameAsStore()));
			inputParams.put("in_rst_HVAC_to_auto", CommonUtility.isNull(addSiteRequest.getIsHVACModeToAuto()));
			inputParams.put("in_rst_hold_mode", CommonUtility.isNull(addSiteRequest.getResetHoldMode()));
			inputParams.put("in_nightly_download", CommonUtility.isNull(addSiteRequest.getNightlyScheduleDownload()));
			inputParams.put(TableFieldConstants.IN_USER_ID, userId);
			inputParams.put(TableFieldConstants.IN_CUSTOMER_ID, addSiteRequest.getCustomerId());
			inputParams.put("in_site_hrs_formate", addSiteRequest.getSiteHrsFormate());
			inputParams.put("in_site_frn_time_zone_id", addSiteRequest.getSiteTimeZone());
			inputParams.put("in_site_frn_time_zone_std", TimeZone.getTimeZone(addSiteRequest.getSiteTimeZone()).getDisplayName(Boolean.FALSE, 0));
			inputParams.put("in_site_frn_time_zone_dls", TimeZone.getTimeZone(addSiteRequest.getSiteTimeZone()).getDisplayName(Boolean.TRUE, 0));
			inputParams.put("in_selected_user_id", 
					CommonUtility.isNull(addSiteRequest.getUserId()) == 0 ? userId 
							: CommonUtility.isNull(addSiteRequest.getUserId()));
			inputParams.put("in_max_set_point", CommonUtility.isNull(addSiteRequest.getMaxSP()).isEmpty()? 0 :addSiteRequest.getMaxSP());
			inputParams.put("in_min_set_point", CommonUtility.isNull(addSiteRequest.getMinSP()).isEmpty()? 0 :addSiteRequest.getMinSP());
			logger.debug("[DEBUG] Executing the stored  procedure - sp_insert_site");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the add Site request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get(TableFieldConstants.OUT_ERROR_MSG);
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get(TableFieldConstants.OUT_FLAG);
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_SITE_FAILED, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_SITE_FAILED, logger, e);
		}
		
		logger.info("[END] [addSite] [Site DAO LAYER]");

		return statusFlag;
	}
	
	@Override
	public int updateSite(UpdateSiteRequest updateSiteRequest,int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [updateSite] [Site DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
				
		/*
		 * These two variables are used to make the newly added site group ids 
		 * into string bcz the MYSQL procedure do not accept the List,
		 * So separating the element using the ','.
		 */
		List<String> updatedSiteGroupIdsList = null;
		StringBuilder updatedSiteGroupIdsListStr = new StringBuilder();
		
		List<String> updatedDeletedSiteGroupIdsList = null;
		StringBuilder updatedDeletedSiteGroupIdsListStr = new StringBuilder();
		
		/*
		 * These Three variables are used to make the selected site hours 
		 * into string bcz the MYSQL procedure do not accept the List,
		 * So separating the element using the ','.
		 * Here each element is an object, So we are making all the object data
		 * into String using '~'.
		 */
		List<SiteHoursRequest> siteHoursList = null;
	    StringBuilder siteHoursListStr = new StringBuilder();
	    SiteHoursRequest siteHoursRequestObj = null;
	    
	    /*
	     * These below variables are used to make the 
	     * occupy hours list into a suitable string.
	     */
	    List<SiteHoursRequest> siteOccupyHoursList = null;
	    StringBuilder siteOccupyHoursListStr = new StringBuilder();
	    
	    /*
	     * These below variables are used to make the 
	     * HVAC list into a suitable string.
	     */
	    List<RTURequest> hvacList = null;
	    StringBuilder hvacListStr = new StringBuilder();
	    RTURequest rtuRequest = null;
	    
	    /*
	     * These below variables are used to make the 
	     * Thermostat list into a suitable string.
	     */
	    List<ThermostatRequest> thermostatList = null;
	    StringBuilder thermostatListStr = new StringBuilder();
	    ThermostatRequest thermostatRequest = null;
		
	    /*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		/*
		 * Temporary variable to holding the fan status.
		 */
		int fanStatus=0;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_update_site_more_info");
		    
		    /*
		     * Making the newly added site groupids into a string and appending the each
		     * element using ','.
		     */
		    updatedSiteGroupIdsList = updateSiteRequest.getSiteGroups();
		    if(!updatedSiteGroupIdsList.isEmpty()){
			    for (int i = 0; i < updatedSiteGroupIdsList.size(); i++) {
			    	updatedSiteGroupIdsListStr.append(updatedSiteGroupIdsList.get(i)+",");
				}
			    
			    /*
			     * Trimming the last unnecessary ','.
			     */
			    updatedSiteGroupIdsListStr.replace(updatedSiteGroupIdsListStr.length()-1, updatedSiteGroupIdsListStr.length(), "");
		    }
		    
		    updatedDeletedSiteGroupIdsList = updateSiteRequest.getDeleteSiteGroups();
		    if(!updatedDeletedSiteGroupIdsList.isEmpty()){
			    for (int i = 0; i < updatedDeletedSiteGroupIdsList.size(); i++) {
			    	updatedDeletedSiteGroupIdsListStr.append(updatedDeletedSiteGroupIdsList.get(i)+",");
				}
			    
			    /*
			     * Trimming the last unnecessary ','.
			     */
			    updatedDeletedSiteGroupIdsListStr.replace(updatedDeletedSiteGroupIdsListStr.length()-1, updatedDeletedSiteGroupIdsListStr.length(), "");
		    }
		    
		    /*
		     * Making the Site Hours as String using ',' and '~' separators
		     * if the user selected any siteHours.
		     */
		    siteHoursList = updateSiteRequest.getSiteHours();
		    if(!siteHoursList.isEmpty()){
		    	for (int i = 0; i < siteHoursList.size(); i++) {
			    	siteHoursRequestObj = siteHoursList.get(i);
			    	siteHoursListStr.append(siteHoursRequestObj.getDayOfWeek()+"~"
			    	+siteHoursRequestObj.getOpenHrs()+"~"+siteHoursRequestObj.getCloseHrs()+",");
				}
			    
			    /*
			     * Trimming the last unnecessary ','.
			     */
			    siteHoursListStr.replace(siteHoursListStr.length()-1, siteHoursListStr.length(), "");
		    }
		    
		    /*
		     * Making the Site Hours as String using ',' and '~' separators
		     * if the user selected any siteHours.
		     */
		    siteOccupyHoursList = updateSiteRequest.getOccupyHours();
		    if(!siteOccupyHoursList.isEmpty()){
		    	for (int i = 0; i < siteOccupyHoursList.size(); i++) {
			    	siteHoursRequestObj = siteOccupyHoursList.get(i);
			    	siteOccupyHoursListStr.append(siteHoursRequestObj.getDayOfWeek()+"~"
			    	+siteHoursRequestObj.getOpenHrs()+"~"+siteHoursRequestObj.getCloseHrs()+",");
				}
			    
			    /*
			     * Trimming the last unnecessary ','.
			     */
			    siteOccupyHoursListStr.replace(siteOccupyHoursListStr.length()-1, siteOccupyHoursListStr.length(), "");
		    }
		    
		    /*
		     * Making the HVAC List as String using '~#~' and '~!~' separators
		     * if the user selected any HVAC.
		     */
		    hvacList = updateSiteRequest.getrTUList();
		    if(!hvacList.isEmpty()){
		    	for (int i = 0; i < hvacList.size(); i++) {
		    		rtuRequest = hvacList.get(i);
	    			hvacListStr.append(CommonUtility.isNull(rtuRequest.getModel())+"~!~"
			    	+CommonUtility.isNull(rtuRequest.getUnit())+"~!~"
			    	+CommonUtility.isNull(rtuRequest.getLocation())+"~!~"
			    	+CommonUtility.isNull(rtuRequest.getHeating())+"~!~"
			    	+CommonUtility.isNull(rtuRequest.getCooling())+"~#~");
		    		
				}
			    
			    /*
			     * Trimming the last unnecessary '~#~'.
			     */
		    	hvacListStr.replace(hvacListStr.length()-3, hvacListStr.length(), "");
		    }
		    
		    /*
		     * Making the Thermostat List as String using '~#~' and '~!~' separators
		     * if the user selected any Thermostat.
		     */
		    thermostatList = updateSiteRequest.getThermostatList();
		    if(!thermostatList.isEmpty()){
		    	for (int i = 0; i < thermostatList.size(); i++) {
		    		
		    		thermostatRequest = thermostatList.get(i);
		    		String otherLocation = "";
		    		if (thermostatRequest.getLocationType() == 23) {
		    			otherLocation = CommonUtility.isNull(thermostatRequest.getOtherLocation());
		    		}
		    		thermostatListStr.append(CommonUtility.isNull(thermostatRequest.getUnit())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getLocationImage())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getSpaceEnough())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getMake())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getModel())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.gethVACUnit())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getWiringConfigThermostat())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getWiringThermostatImage())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getrCAndCPower())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getcWireAttached())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getNoCWireAttached())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getAutomatedSchedule())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getAutomatedScheduleNote())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getLocationOfRemoteSensor())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getValidateSensor())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getWiringConfigSensor())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getWiringSensorImage())+"~!~"
			    	+CommonUtility.isNull(thermostatRequest.getLocationType())+"~!~"
			    	+otherLocation+"~!~"	
			    	+"~#~");
				}
			    
			    /*
			     * Trimming the last unnecessary '~#~'.
			     */
		    	thermostatListStr.replace(thermostatListStr.length()-3, thermostatListStr.length(), "");
		    }
		    
		    /*
		     * Setting up the fan status with proper value.
		     */
		    if(CommonUtility.isNull(updateSiteRequest.getFanOn()) ==1)
		    	fanStatus = 1;
		    else if(CommonUtility.isNull(updateSiteRequest.getFanAuto()) ==1)
		    	fanStatus = 2;
		    
		    String zipCode = updateSiteRequest.getSiteZipCode();
		    int zipCodeLength = zipCode.length();
		    if(zipCodeLength < 5){
		    	int remainingZeros= 5 - zipCodeLength;
		    	String zeros="";
		    	for (int i = 1; i <= remainingZeros; i++) {
		    		zeros = zeros+"0";
				}
		    	zipCode =zeros + zipCode;
		    }
		    
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put(TableFieldConstants.IN_SITE_ID, CommonUtility.isNull(updateSiteRequest.getSiteId()));
			inputParams.put("in_site_name", CommonUtility.isNull(updateSiteRequest.getSiteName()));
			inputParams.put("in_site_internal_id", CommonUtility.isNull(updateSiteRequest.getSiteInternalId()));
			inputParams.put("in_site_type_id", CommonUtility.isNull(updateSiteRequest.getSiteType()));
			inputParams.put("in_site_type_other", CommonUtility.isNull(updateSiteRequest.getSiteTypeNew()));
			inputParams.put("in_site_phone", CommonUtility.isNull(updateSiteRequest.getSitePhNo()));
			inputParams.put("in_site_district", CommonUtility.isNull(updateSiteRequest.getSiteDistrict()));
			inputParams.put("in_site_area", CommonUtility.isNull(updateSiteRequest.getSiteArea()));
			inputParams.put("in_site_region", CommonUtility.isNull(updateSiteRequest.getSiteRegion()));
			inputParams.put("in_site_grp_ids_str", CommonUtility.isNull(updatedSiteGroupIdsListStr.toString()));
			inputParams.put("in_site_degree_pref", CommonUtility.isNull(updateSiteRequest.getDegreePreference()));
			inputParams.put("in_site_add1", CommonUtility.isNull(updateSiteRequest.getSiteAddLine1()));
			inputParams.put("in_site_add2", CommonUtility.isNull(updateSiteRequest.getSiteAddLine2()));
			inputParams.put("in_site_lat", CommonUtility.isNull(updateSiteRequest.getLatitude()));
			inputParams.put("in_site_lang", CommonUtility.isNull(updateSiteRequest.getLangitude()));
			inputParams.put("in_site_city_id", CommonUtility.isNull(updateSiteRequest.getSiteCity()));
			inputParams.put("in_site_zip_code", CommonUtility.isNull(zipCode));
			inputParams.put("in_site_hours_str", CommonUtility.isNull(siteHoursListStr.toString()));
			inputParams.put("in_fan_status", CommonUtility.isNull(fanStatus));
			inputParams.put("in_lock_status", CommonUtility.isNull(updateSiteRequest.getLock()));
			inputParams.put("in_rst_HVAC_to_auto", CommonUtility.isNull(updateSiteRequest.getIsHVACModeToAuto()));
			inputParams.put("in_rst_hold_mode", CommonUtility.isNull(updateSiteRequest.getResetHoldMode()));
			inputParams.put("in_nightly_download", CommonUtility.isNull(updateSiteRequest.getNightlyScheduleDownload()));
			
			inputParams.put("in_survey_date", CommonUtility.isNull(updateSiteRequest.getSurveyDate()));
			inputParams.put("in_square_footage", CommonUtility.isNull(updateSiteRequest.getSquareFootage()));
			inputParams.put("in_building_layout", CommonUtility.isNull(updateSiteRequest.getBuildingLayout()));
			inputParams.put("in_building_layout_image", CommonUtility.isNull(updateSiteRequest.getBuildingLayoutImage()));
			inputParams.put("in_floor_plan", CommonUtility.isNull(updateSiteRequest.getFloorPlan()));
			inputParams.put("in_floor_image", CommonUtility.isNull(updateSiteRequest.getFloorImage()));
			inputParams.put("in_occupy_hrs_str", CommonUtility.isNullForString(siteOccupyHoursListStr));
			inputParams.put("in_local_site_contact", CommonUtility.isNull(updateSiteRequest.getLocalSiteContact()));
			inputParams.put("in_local_contact_phone", CommonUtility.isNull(updateSiteRequest.getLocalContactPhone()));
			inputParams.put("in_local_contact_email", CommonUtility.isNull(updateSiteRequest.getLocalContactEmail()));
			inputParams.put("in_local_contact_mobile", CommonUtility.isNull(updateSiteRequest.getLocalContactMobile()));
			inputParams.put("in_altr_site_contact", CommonUtility.isNull(updateSiteRequest.getAlternateSiteContact()));
			inputParams.put("in_altr_contact_phone", CommonUtility.isNull(updateSiteRequest.getAlternateContactPhone()));
			inputParams.put("in_escort_contact", CommonUtility.isNull(updateSiteRequest.getEscortContactName()));
			inputParams.put("in_escort_phone", CommonUtility.isNull(updateSiteRequest.getEscortContactNumber()));
			inputParams.put("in_access_restr_formal_hrs", CommonUtility.isNull(updateSiteRequest.getListaAccessRestrictionsFromFormalHours()));
			inputParams.put("in_special_room_access_info", CommonUtility.isNull(updateSiteRequest.getSpecialRoomAccessInformation()));
			inputParams.put("in_lock_box_thermostat", CommonUtility.isNull(updateSiteRequest.getLockBoxOnThermostat()));
			inputParams.put("in_access_manage_thermostat", CommonUtility.isNull(updateSiteRequest.getAccessOrManagesThermostat()));
			inputParams.put("in_building_type", CommonUtility.isNull(updateSiteRequest.getBuildingType()));
			inputParams.put("in_building_notes", CommonUtility.isNull(updateSiteRequest.getBuildingNotes()));
			inputParams.put("in_rtu_str", CommonUtility.isNullForString(hvacListStr));
			inputParams.put("in_thermostat_str", CommonUtility.isNullForString(thermostatListStr));
			inputParams.put("in_technician_name", CommonUtility.isNull(updateSiteRequest.getTechnicianName()));
			inputParams.put("in_technician_phone", CommonUtility.isNull(updateSiteRequest.getTechnicianPhone()));
			inputParams.put("in_technician_notes", CommonUtility.isNull(updateSiteRequest.getTechnicianNotes()));
			inputParams.put("in_cellular_mode", CommonUtility.isNull(updateSiteRequest.getCellularMode()));
			inputParams.put("in_cellular_coverage", CommonUtility.isNull(updateSiteRequest.getCellularCoverage()));
			inputParams.put("in_cellular_provide", CommonUtility.isNull(updateSiteRequest.getCellularProvide()));
			inputParams.put("in_cellular_wifi", CommonUtility.isNull(updateSiteRequest.getCellularWIFI()));
			inputParams.put("in_cellular_wifi_pwd", CommonUtility.isNull(updateSiteRequest.getCellularWIFIPWD()));
			inputParams.put("in_modem_type", CommonUtility.isNull(updateSiteRequest.getModemType()));
			inputParams.put("in_simcard_number", CommonUtility.isNull(updateSiteRequest.getSimCardNumber()));
			inputParams.put("in_modem_serial_number", CommonUtility.isNull(updateSiteRequest.getModemSerialNumber()));
			inputParams.put("in_modem_ip_adress", CommonUtility.isNull(updateSiteRequest.getModemIPAddress()));
			inputParams.put("in_modem_phone_number", CommonUtility.isNull(updateSiteRequest.getModemPhoneNumber()));
			inputParams.put("in_is_hvac_running", CommonUtility.isNull(updateSiteRequest.getIsHvacRunning()));
			inputParams.put("in_site_hrs_formate", CommonUtility.isNull(updateSiteRequest.getSiteHrsFormate()));
			inputParams.put("in_site_occupy_hrs_formate", CommonUtility.isNull(updateSiteRequest.getSiteOccupyHrsFormate()));
			inputParams.put("in_site_frn_time_zone_id", CommonUtility.isNull(updateSiteRequest.getSiteTimeZone()));
			inputParams.put("in_site_frn_time_zone_std", TimeZone.getTimeZone(updateSiteRequest.getSiteTimeZone()).getDisplayName(Boolean.FALSE, 0));
			inputParams.put("in_site_frn_time_zone_dls", TimeZone.getTimeZone(updateSiteRequest.getSiteTimeZone()).getDisplayName(Boolean.TRUE, 0));
			
			inputParams.put("in_user_id", userId);
			inputParams.put("in_customer_id", updateSiteRequest.getCustomerId());
			inputParams.put("in_site_delete_grp_ids_str", updatedDeletedSiteGroupIdsListStr);
			inputParams.put("in_site_status", CommonUtility.isNull(updateSiteRequest.getSiteStatus()));
			inputParams.put("in_same_as_store", CommonUtility.isNull(updateSiteRequest.getSameAsStore()));
			
			inputParams.put("in_max_set_point", CommonUtility.isNull(updateSiteRequest.getMaxSP()).isEmpty()? 0 :updateSiteRequest.getMaxSP());
			inputParams.put("in_min_set_point", CommonUtility.isNull(updateSiteRequest.getMinSP()).isEmpty()? 0 :updateSiteRequest.getMinSP());
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_update_site_more_info");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the add Site request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get(TableFieldConstants.OUT_ERROR_MSG);
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get(TableFieldConstants.OUT_FLAG);
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_SITE_FAILED, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_SITE_FAILED, logger, e);
		}
		
		logger.info("[END] [updateSite] [Site DAO LAYER]");

		return statusFlag;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject listSite(String page, Integer id, GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [listSite] [Site DAO LAYER]");
		
		/*
		 * Used to hold sites,cities,states and groups List.
		 */
		JSONArray siteArray= new JSONArray();
		JSONArray cityArrayList= new JSONArray();
		JSONArray stateArrayList= new JSONArray();
		JSONArray groupsArrayList= new JSONArray();
		JSONArray scheduleArrayList= new JSONArray();
		JSONArray customerArrayList= new JSONArray();
		
		
		/*
		 * Used to store the Sites Data,City Data,State Data,Groups Data
		 * and for data forming.
		 */
		HashMap<Integer, JSONObject> siteMap=new LinkedHashMap<>();
		HashMap<Integer, JSONObject> cities=new LinkedHashMap<>();
		HashMap<Integer, JSONObject> states=new LinkedHashMap<>();
		HashMap<Integer, JSONObject> groups=new LinkedHashMap<>();
		HashMap<String, JSONObject> schedules=new LinkedHashMap<>();
		HashMap<Integer, JSONObject> customers=new LinkedHashMap<>();
		
		/*
		 * final return object.
		 */
		JSONObject siteObjectFinal = new JSONObject();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_list_site procedure.");
			logger.debug("[DEBUG] Input param page-"+page);
			logger.debug("[DEBUG] Input param id-"+id);
			
			jdbcTemplate.query("call sp_list_site ('"+page+"',"+id+","+userDetails.getIsSuper()+","+userDetails.getUserId()+")", new RowCallbackHandler() {
				/*
				 * Used to hold the each record of site.
				 */
				JSONObject siteObj = null;
				
				/*
				 * Used to hold the groups array
				 * and group object
				 */
				JSONArray groupsArray = null;
				JSONObject groupObj = null;
				
				/*
				 * Used to hold the city and state objects.
				 */
				JSONObject cityObj = null;
				JSONObject stateObj = null;
				JSONObject scheduleObj = null;
				JSONObject customerObj = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {
			    	
			    	if(siteMap.containsKey(rs.getInt(TableFieldConstants.SITE_ID))){
			    		
			    		if(CommonUtility.isNull(rs.getInt(TableFieldConstants.GROUP_ID)) != 0){
			    			/*
				    		 * Getting the Site Object from SiteMap
				    		 * and from Site Object getting Site Array.
				    		 */
				    		siteObj = siteMap.get(rs.getInt(TableFieldConstants.SITE_ID));
				    		groupsArray = (JSONArray) siteObj.get(SITE_GROUPS);
				    		
				    		/*
				    		 * Instantiating the group object and assigning 
				    		 * the group id and group value
				    		 * and adding the groupObj to groupsArray 
				    		 */
				    		
			    			groupObj = new JSONObject();
				    		groupObj.put(VALUE, CommonUtility.isNull(rs.getInt(TableFieldConstants.GROUP_ID)));
				    		groupObj.put(LABLE, CommonUtility.isNull(rs.getString(TableFieldConstants.GROUP_NAME)));
				    		groupsArray.add(groupObj);
				    		
				    		/*
				    		 * Adding the groups data into Groups Map
				    		 */
				    		if(!groups.containsKey(CommonUtility.isNull(rs.getInt(TableFieldConstants.GROUP_ID))))
				    			groups.put(CommonUtility.isNull(rs.getInt(TableFieldConstants.GROUP_ID)),groupObj);
				    		
				    		siteObj.put(SITE_GROUPS, groupsArray);
			    		
				    		/*
					    	 * Add them in sitehash map
					    	 */
					    	siteMap.put(rs.getInt(TableFieldConstants.SITE_ID), siteObj);
			    		}
			    		
			    	}else{
			    		
			    		/*
			    		 * Instantiating the group Array and Adding 
			    		 * the group object
			    		 */
			    		groupsArray = new JSONArray();
			    		
			    		if(CommonUtility.isNull(rs.getInt(TableFieldConstants.GROUP_ID)) != 0){
			    			/*
				    		 * Instantiating the group object and assigning 
				    		 * the group id and group value
				    		 */
				    		groupObj = new JSONObject();
				    		groupObj.put(VALUE, CommonUtility.isNull(rs.getInt(TableFieldConstants.GROUP_ID)));
				    		groupObj.put(LABLE, CommonUtility.isNull(rs.getString(TableFieldConstants.GROUP_NAME)));
				    		groupsArray.add(groupObj);
				    		
				    		if(!groups.containsKey(CommonUtility.isNull(rs.getInt(TableFieldConstants.GROUP_ID))))
				    			groups.put(CommonUtility.isNull(rs.getInt(TableFieldConstants.GROUP_ID)),groupObj);
			    		}
			    		
			    		/*
			    		 * Forming the Schedule Object
			    		 * and adding to Schedule map.
			    		 */
			    		scheduleObj = new JSONObject();
			    		if(!CommonUtility.isNull(rs.getString("schedule_data")).isEmpty()){
			    			
			    			scheduleObj.put("scheduleId", CommonUtility.isNull(Integer.parseInt(
			    					rs.getString("schedule_data").split("###~###")[0])));
			    			scheduleObj.put("scheduleName", CommonUtility.isNull(
			    					rs.getString("schedule_data").split("###~###")[1]));
			    			if(!schedules.containsKey(CommonUtility.isNull(rs.getString("schedule_data"))))
			    				schedules.put(CommonUtility.isNull(rs.getString("schedule_data")),scheduleObj);
			    		}
			    		
			    		/*
			    		 * Forming the City Object
			    		 * and adding to cities map.
			    		 */
			    		cityObj = new JSONObject();
			    		cityObj.put("cityId",CommonUtility.isNull(rs.getInt(TableFieldConstants.CITY_ID)));
			    		cityObj.put("cityName", CommonUtility.isNull(rs.getString(TableFieldConstants.CITY_NAME)));
			    		
			    		if(!cities.containsKey(CommonUtility.isNull(rs.getInt(TableFieldConstants.CITY_ID))))
			    			cities.put(CommonUtility.isNull(rs.getInt(TableFieldConstants.CITY_ID)),cityObj);
			    		
			    		/*
			    		 * Forming the State Object
			    		 * and adding to States map.
			    		 */
			    		stateObj = new JSONObject();
			    		stateObj.put("stateName", CommonUtility.isNull(rs.getString(TableFieldConstants.STATE_NAME)));
			    		stateObj.put("stateId",CommonUtility.isNull(rs.getInt(TableFieldConstants.STATE_ID)));
			    		stateObj.put("stateCode",CommonUtility.isNull(rs.getString(TableFieldConstants.STATE_CODE)));
			    		
			    		if(!states.containsKey(CommonUtility.isNull(rs.getInt(TableFieldConstants.STATE_ID))))
			    			states.put(CommonUtility.isNull(rs.getInt(TableFieldConstants.STATE_ID)),stateObj);
			    		
			    		/*
			    		 * Forming the Customer Object
			    		 * and adding to Customers map.
			    		 */
			    		
			    		if(CommonUtility.isNull(rs.getInt(SiteDaoImpl.CUSTOMER_ID)) != 0){
			    			
			    			customerObj = new JSONObject();
				    		customerObj.put("customerName", CommonUtility.isNull(rs.getString("company_name")));
				    		customerObj.put("customerId",CommonUtility.isNull(rs.getInt(SiteDaoImpl.CUSTOMER_ID)));
				    		
				    		if(!customers.containsKey(CommonUtility.isNull(rs.getInt(SiteDaoImpl.CUSTOMER_ID))))
				    			customers.put(CommonUtility.isNull(rs.getInt(SiteDaoImpl.CUSTOMER_ID)),customerObj);
				    		
			    		}
			    		
			    		/*
				    	 * Instantiating and setting all the details of
				    	 * each site from list. 
				    	 */
				    	siteObj = new JSONObject();
				    	siteObj.put("siteId", CommonUtility.isNull(rs.getInt(TableFieldConstants.SITE_ID)));
				    	siteObj.put("siteName", CommonUtility.isNull(rs.getString("site_name")));
				    	siteObj.put("siteCode", CommonUtility.isNull(rs.getString("site_code")));
				    	siteObj.put("siteInternalId", CommonUtility.isNull(rs.getString("site_internal_id")));
				    	siteObj.put("siteAddLine1", CommonUtility.isNull(rs.getString("address_line1")));
				    	siteObj.put("siteAddLine2", CommonUtility.isNull(rs.getString("address_line2")));
				    	siteObj.put("isActive", CommonUtility.isNull(rs.getInt("is_active")));
				    	siteObj.put("siteZipCode", CommonUtility.isNull(rs.getString("zip_code")));
				    	siteObj.put("siteState", stateObj);
				    	siteObj.put("siteCity", cityObj);
				    	siteObj.put(SITE_GROUPS, groupsArray);
				    	siteObj.put("customerCode", CommonUtility.isNull(rs.getString("customer_code")));
				    	siteObj.put("schedule", scheduleObj);
				    	siteObj.put("deviceCount", CommonUtility.isNull(rs.getInt("device_count")));
				    	siteObj.put("alertsCount", CommonUtility.isNull(rs.getInt("alerts_count")));
				    	siteObj.put("customerId", CommonUtility.isNull(rs.getInt("customer_id")));
				    	siteObj.put("siteStatus", CommonUtility.isNull(rs.getInt("is_active")));
				    	siteObj.put("sitePhNo", CommonUtility.isNull(rs.getString("site_phone")));
				    	siteObj.put("customerName", CommonUtility.isNull(rs.getString("company_name")));
				    	
				    	String deviceLocationTemp = CommonUtility.isNull(rs.getString("device_location_temp"));
				    	JSONObject locationTempObj;
				    	JSONArray locationTempArray = new JSONArray();
				    	if(!deviceLocationTemp.isEmpty()){
				    		
				    		String [] locationArray = deviceLocationTemp.split("~~");
				    		
				    		for (int i = 0; i < locationArray.length; i++) {
								
				    			if(!locationArray[i].isEmpty() && locationArray[i].contains("##") && locationArray[i].split("##").length>=9){
				    				
				    				String strLocation = CommonUtility.isNull(locationArray[i].split("##")[0]).isEmpty() ?
					    						"" : locationArray[i].split("##")[0];
				    				String temp = CommonUtility.isNull(locationArray[i].split("##")[1]).isEmpty() ?
				    						"0" : locationArray[i].split("##")[1];
				    				String opState = CommonUtility.isNull(locationArray[i].split("##")[2]).isEmpty() ?
				    						"OFF" : locationArray[i].split("##")[2];
				    				String currentUTCDateTime = CommonUtility.isNull(locationArray[i].split("##")[3]).isEmpty() ?
				    						"" : locationArray[i].split("##")[3];
				    				String dateTime = CommonUtility.isNull(locationArray[i].split("##")[4]).isEmpty() ?
				    						"" : locationArray[i].split("##")[4];
				    				String tstatMode = CommonUtility.isNull(locationArray[i].split("##")[5]).isEmpty() ?
				    						"" : locationArray[i].split("##")[5];
				    				int isColored = CommonUtility.isNull(locationArray[i].split("##")[6]).isEmpty() ?
				    						0 : Integer.parseInt(locationArray[i].split("##")[6]);
				    				int regType = CommonUtility.isNull(locationArray[i].split("##")[7]).isEmpty() ?
				    						0 : Integer.parseInt(locationArray[i].split("##")[7]);
				    				int deviceId = CommonUtility.isNull(locationArray[i].split("##")[8]).isEmpty() ?
				    						0 : Integer.parseInt(locationArray[i].split("##")[8]);
				    				
				    				locationTempObj = new JSONObject();
			    					
			    					locationTempObj.put("location", strLocation);
				    				locationTempObj.put("temp", temp);
				    				locationTempObj.put("isColored", isColored > 0 ? true : false);
				    				locationTempObj.put("op_state", opState);
				    				locationTempObj.put("tstat_mode", tstatMode);
				    				locationTempObj.put("currentUTCDateTime", currentUTCDateTime);
				    				locationTempObj.put("dateTime", dateTime);
				    				locationTempObj.put("regType", regType);
				    				locationTempObj.put("deviceId", deviceId);
				    				
				    				locationTempArray.add(locationTempObj);
				    				
				    			}
				    			
							}
				    	}
				    	siteObj.put("deviceLocationTemp", locationTempArray);
				    	
				    	/*
				    	 * Add them in sitehash map
				    	 */
				    	siteMap.put(CommonUtility.isNull(rs.getInt("site_id")), siteObj);
				    	
			    	}
			    	
				}
			});
			
			/*
	    	 * Looping the HashMap and
	    	 * Adding the each site object to the sites list.
	    	 */
	    	for (Map.Entry<Integer, JSONObject> entry : siteMap.entrySet()) {
		    	siteArray.add((JSONObject)entry.getValue());
	    	}
	    	
	    	/*
	    	 * Looping the HashMap and
	    	 * Adding the each City object to the Cities list.
	    	 */
	    	for (Map.Entry<Integer, JSONObject> entry : cities.entrySet()) {
		    	cityArrayList.add((JSONObject)entry.getValue());
	    	}
	    	
	    	/*
	    	 * Looping the HashMap and
	    	 * Adding the each State object to the States list.
	    	 */
	    	for (Map.Entry<Integer, JSONObject> entry : states.entrySet()) {
		    	stateArrayList.add((JSONObject)entry.getValue());
	    	}
	    	
	    	/*
	    	 * Looping the HashMap and
	    	 * Adding the each Group object to the Groups list.
	    	 */
	    	for (Map.Entry<Integer, JSONObject> entry : groups.entrySet()) {
		    	groupsArrayList.add((JSONObject)entry.getValue());
	    	}
	    	
	    	/*
	    	 * Looping the HashMap and
	    	 * Adding the each Schedule object to the Schedule list.
	    	 */
	    	for (Map.Entry<String, JSONObject> entry : schedules.entrySet()) {
	    		scheduleArrayList.add((JSONObject)entry.getValue());
	    	}
	    	
	    	/*
	    	 * Looping the HashMap and
	    	 * Adding the each Customer object to the Customer list.
	    	 */
	    	for (Map.Entry<Integer, JSONObject> entry : customers.entrySet()) {
	    		customerArrayList.add((JSONObject)entry.getValue());
	    	}
	    	
	    	/*
	    	 * Forming the final return object by
	    	 * adding the siteList,CityList,StateList and GroupsList.
	    	 */
	    	siteObjectFinal = new JSONObject();
	    	siteObjectFinal.put("siteList", siteArray);
	    	siteObjectFinal.put("citiesList", cityArrayList);
	    	siteObjectFinal.put("statesList", stateArrayList);
	    	siteObjectFinal.put("groupsList", groupsArrayList);
	    	siteObjectFinal.put("scheduleList", scheduleArrayList);
	    	siteObjectFinal.put("customerList", customerArrayList);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_SITE_FAILED, logger, e);
		}
		
		logger.info("[END] [listSite] [Site DAO LAYER]");
		
		return siteObjectFinal;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject loadAddSite(String page,Integer id,GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [loadAddSite] [Site DAO LAYER]");
		
		/*
		 * Used to hold all the all the dropdown values.
		 */
		JSONObject loadObject = new JSONObject();
		JSONArray siteType = new JSONArray();
		JSONArray siteHrs = new JSONArray();
		JSONArray siteGroup = new JSONArray();
		JSONObject custThermostatInfo = new JSONObject();
		
		/*
		 * Temperary variables.
		 */
		JSONObject obj ;
		Map.Entry<String, Object> entry;
		String key;
		List<HashMap<String, Object>> tempList;
		
		/*
		 * Declaring the simpleJdbcCall and  simpleJdbcCallResult
		 */
		SimpleJdbcCall simpleJdbcCall;
		Map<String, Object> simpleJdbcCallResult;
		
		/*
		 * This iterator is used to loop the results. 
		 */
		Iterator<Entry<String, Object>> it;
		
		try {
			logger.debug("[DEBUG] Executing sp_list_site procedure.");
			/*
			 * Instantiating simpleJdbcCall, Appending the procedure
			 * And executing the procedure.
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_load_add_site_form");
			/*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_page", page);
			inputParams.put("in_id", id);
			inputParams.put("in_is_super", userDetails.getIsSuper());
			inputParams.put("in_user_id", userDetails.getUserId());
			
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
			
			/*
			 * Executing the procedure
			 */
			simpleJdbcCallResult = simpleJdbcCall.execute(inputParams);
			
			/*
			 * looping the resulsets.
			 */
			it = simpleJdbcCallResult.entrySet().iterator();
			    while (it.hasNext()) {
			        /*
			         * Getting the each result set as entry 
			         * and Getting the key from entry. 
			         */
			    	entry = (Map.Entry<String, Object>) it.next();
			        key = entry.getKey().toString();
			        
			        logger.debug("[DEBUG] Key - "+key);
			        
			        /*
			         * Forming the Site Type List from the site type table. 
			         */
			        if(key.equals(RESULT_SET_1)){
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        for (int i = 0; i < tempList.size(); i++) {
				        	obj = new JSONObject();
				        	obj.put(VALUE, CommonUtility.isNullForInteger(tempList.get(i).get(TableFieldConstants.ST_ID)));
				        	obj.put(LABLE, CommonUtility.isNullForString(tempList.get(i).get("st_name")));
				        	siteType.add(obj);
						}
				        logger.debug("[DEBUG] siteType---ARRAYLIST-- "+siteType);
				        
			        }else if(key.equals(RESULT_SET_2)){
			        	/*
				         * Forming the Site Hours List from the Site Hours table. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        for (int i = 0; i < tempList.size(); i++) {
				        	obj = new JSONObject();
				        	obj.put(VALUE, CommonUtility.isNullForInteger(tempList.get(i).get("dow_id")));
				        	obj.put(LABLE, CommonUtility.isNullForString(tempList.get(i).get(TableFieldConstants.DOW_DISPLAY_NAME)));
				        	siteHrs.add(obj);
						}
				        logger.debug("[DEBUG] siteHrs---ARRAYLIST-- "+siteHrs);
			        
			        }else if(key.equals(RESULT_SET_3)){
			        	/*
				         * Forming the Group List from the Group table. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        for (int i = 0; i < tempList.size(); i++) {
				        	obj = new JSONObject();
				        	obj.put(VALUE, CommonUtility.isNullForInteger(tempList.get(i).get(TableFieldConstants.GROUP_ID)));
				        	obj.put(LABLE, CommonUtility.isNullForString(tempList.get(i).get(TableFieldConstants.GROUP_NAME)));
				        	siteGroup.add(obj);
						}
				        logger.debug("[DEBUG] siteGroup---ARRAYLIST-- "+siteGroup);
			        
			        }else if(key.equals(RESULT_SET_4)){
			        	/*
				         * Forming the Customer Thermostat info from the customer_to_thermostat table. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
			        	custThermostatInfo = new JSONObject();
			        	if(tempList.size() == 1){
			        		int fanOn = 0;
				        	int fanAuto = 0;
				        	
				        	if("1".equals(CommonUtility.isNullForInteger(tempList.get(0).get("ctt_fan_status")).toString()))
				        		fanOn = 1;
				        	else if("2".equals(CommonUtility.isNullForInteger(tempList.get(0).get("ctt_fan_status")).toString()))
				        		fanAuto = 1;
				        					        	
				        	custThermostatInfo.put("nightlyScheduleDownload", CommonUtility.isNullForInteger(tempList.get(0).get("ctt_night_schedule")));
				        	custThermostatInfo.put("resetHoldMode", CommonUtility.isNullForInteger(tempList.get(0).get("ctt_reset_hold_mode")));
				        	custThermostatInfo.put("isHVACModeToAuto", CommonUtility.isNullForInteger(tempList.get(0).get("ctt_HVAC_to_auto")));
				        	custThermostatInfo.put("fanAuto", fanAuto);
				        	custThermostatInfo.put("fanOn", fanOn);
				        	custThermostatInfo.put("lock", CommonUtility.isNullForInteger(tempList.get(0).get("ctt_lock_status")));
				        	custThermostatInfo.put("degreePreference", CommonUtility.isNullForInteger(tempList.get(0).get("degree_preference")));
				        	custThermostatInfo.put("thermostateMinSetPoint", Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("ctt_min_sp")).toString()));
				        	custThermostatInfo.put("thermostateMaxSetPoint", Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("ctt_max_sp")).toString()));
			        	}
			        	
				        logger.debug("[DEBUG] Customer Thermostate info---OBJECT-- "+custThermostatInfo);
			        
			        }
			        
			    }
			    
			    /*
			     *	Attaching Site Type List,
			     *	Site Hours List,
			     * 	Site GroupList,
			     * 	States List
			     * 	to loadObject.
			     */
			    loadObject.put("siteType", siteType);
			    loadObject.put("siteHrs", siteHrs);
			    loadObject.put("siteGroup", siteGroup);
			    loadObject.put("states", lookUpDao.getStates());
			    loadObject.put("customerPreferences", custThermostatInfo);
			    
			    logger.debug("[DEBUG] loadObject---OBJECT-- "+loadObject);
			    
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOAD_ADD_SITE_FAILED, logger, e);
		}
		
		logger.info("[END] [loadAddSite] [Site DAO LAYER]");
		
		return loadObject;
	}
	
	@Override
	public int activateOrDeActivateSite(ActivateOrDeActivateSiteRequest activateOrDeActivateSiteRequest,int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [activateOrDeActivateSite] [Site DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
		
	    /*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_activate_deactivate_site");
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put(TableFieldConstants.IN_SITE_ID, activateOrDeActivateSiteRequest.getSiteId());
			inputParams.put("in_is_active", activateOrDeActivateSiteRequest.getSiteStatus());
			inputParams.put("in_user_id", userId);
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_activate_deactivate_site");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the update Site request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get("out_error_msg");
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get("out_flag");
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_SITE_STATUS_FAILED, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_SITE_STATUS_FAILED, logger, e);
		}
		
		logger.info("[END] [activateOrDeActivateSite] [Site DAO LAYER]");

		return statusFlag;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getSite(Integer siteId, String page,Integer id,GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [getSite] [Site DAO LAYER]");
		
		/*
		 * Used to hold site data,site hours data.
		 */
		JSONObject siteObject = new JSONObject();
		JSONArray siteHrs = new JSONArray();
		JSONArray siteOccupyHrs = new JSONArray();
		
		/*
		 * Temperary variables.
		 */
		JSONObject obj ;
		Map.Entry<String, Object> entry;
		String key;
		List<HashMap<String, Object>> tempList;
		JSONObject groupObj;
		JSONArray groupArray = new JSONArray();
		JSONArray hvacArray = new JSONArray();
		JSONArray thermostatArray = new JSONArray();
		
		/*
		 * Declaring the simpleJdbcCall and  simpleJdbcCallResult
		 */
		SimpleJdbcCall simpleJdbcCall;
		Map<String, Object> simpleJdbcCallResult;
		
		/*
		 * This iterator is used to loop the results. 
		 */
		Iterator<Entry<String, Object>> it;
		
		/*
		 * Used for holding the city and state and 
		 * final return objects. 
		 */
		JSONObject cityObj = null;
		JSONObject stateObj = null;
		JSONObject siteFinalObject = new JSONObject();
		
		JSONObject tabsCount = new JSONObject();
		
		String zipCode="";
		
		try {
			logger.debug("[DEBUG] Executing sp_select_site procedure.");
			/*
			 * Instantiating simpleJdbcCall, Appending the procedure,
			 * And executing the procedure.
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_select_site");
			
			/*
		     * Adding all the input parameter values to a hash map.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_site_id", siteId);
			inputParams.put("in_page", page);
			inputParams.put("in_id", id);
			inputParams.put("in_is_super", userDetails.getIsSuper());
			inputParams.put("in_user_id", userDetails.getUserId());
			inputParams.put("in_customer_ids", userDetails.getCustomers());
			
			/*
			 * Executing the procedure.
			 */
			simpleJdbcCallResult = simpleJdbcCall.execute(inputParams);
			
			/*
			 * looping the result sets.
			 */
			it = simpleJdbcCallResult.entrySet().iterator();
			
			    while (it.hasNext()) {
			        /*
			         * Getting the each result set as entry 
			         * and Getting the key from entry. 
			         */
			    	entry = (Map.Entry<String, Object>) it.next();
			        key = entry.getKey().toString();
			        
			        logger.debug("[DEBUG] Key - "+key);
			        
			        /*
			         * Forming the Site data from the site table. 
			         */
			        if(key.equals(RESULT_SET_1)){
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
			        	
				        if(!tempList.isEmpty()) {
				        	
				        	obj = new JSONObject();
				        	if("0".equals(CommonUtility.isNullForInteger(tempList.get(0).get(TableFieldConstants.ST_ID)).toString()))
				        		obj.put(LABLE, "Others");
				        	else
				        		obj.put(LABLE, CommonUtility.isNullForString(tempList.get(0).get("st_name")));
				        	obj.put(VALUE, CommonUtility.isNullForInteger(tempList.get(0).get(TableFieldConstants.ST_ID)));
				        	
				        	/*
				        	 * Preparing the city object.
				        	 */
				        	cityObj = new JSONObject();
				    		cityObj.put("cityId",CommonUtility.isNullForInteger(tempList.get(0).get("address_frn_city_id")));
				    		cityObj.put("cityName", CommonUtility.isNullForString(tempList.get(0).get(TableFieldConstants.CITY_NAME)));
				    		
				    		/*
				        	 * Preparing the State object.
				        	 */
				    		stateObj = new JSONObject();
				    		stateObj.put("stateName", CommonUtility.isNullForString(tempList.get(0).get(TableFieldConstants.STATE_NAME)));
				    		stateObj.put("stateId",CommonUtility.isNullForInteger(tempList.get(0).get("city_frn_state_id")));
				    		stateObj.put("stateCode",CommonUtility.isNullForString(tempList.get(0).get(TableFieldConstants.STATE_CODE)));
				    		/*
				    		 * Site Object preparation.
				    		 */
				        	siteObject.put("siteId", CommonUtility.isNullForInteger(tempList.get(0).get(TableFieldConstants.SITE_ID)));
				        	siteObject.put("siteName", CommonUtility.isNullForString(tempList.get(0).get("site_name")));
				        	siteObject.put("siteInternalId", CommonUtility.isNullForString(tempList.get(0).get("site_internal_id")));
				        	siteObject.put("siteCode", CommonUtility.isNullForString(tempList.get(0).get("site_code")));
				        	siteObject.put("siteType", obj);
				        	siteObject.put("siteAddLine1", CommonUtility.isNullForString(tempList.get(0).get("address_line1")));
				        	siteObject.put("siteAddLine2", CommonUtility.isNullForString(tempList.get(0).get("address_line2")));
				        	siteObject.put("siteZipCode", CommonUtility.isNullForString(tempList.get(0).get("zip_code")));
				        	zipCode = CommonUtility.isNullForString(tempList.get(0).get("zip_code")).toString();
				        	siteObject.put("sitePhNo", CommonUtility.isNullForString(tempList.get(0).get("site_phone")));
				        	siteObject.put("siteDistrict", CommonUtility.isNullForString(tempList.get(0).get("site_district")));
				        	siteObject.put("siteRegion", CommonUtility.isNullForString(tempList.get(0).get("site_region")));
				        	siteObject.put("siteArea", CommonUtility.isNullForString(tempList.get(0).get("site_area")));
				        	siteObject.put("degreePreference", CommonUtility.isNullForInteger(tempList.get(0).get("site_degree_pref")));
				        	siteObject.put("siteCity", cityObj);
				        	siteObject.put("siteState", stateObj);
				        	siteObject.put("customerId", CommonUtility.isNullForInteger(tempList.get(0).get("site_frn_customer_id")));
				        	
				        	siteObject.put("localSiteContact", CommonUtility.isNullForString(tempList.get(0).get("sc_local_contact")));
				        	siteObject.put("localContactPhone", CommonUtility.isNullForString(tempList.get(0).get("sc_local_phone")));
				        	siteObject.put("localContactEmail", CommonUtility.isNullForString(tempList.get(0).get("sc_local_email")));
				        	siteObject.put("alternateSiteContact", CommonUtility.isNullForString(tempList.get(0).get("sc_alternate_contac")));
				        	siteObject.put("alternateContactPhone", CommonUtility.isNullForString(tempList.get(0).get("sc_alternate_phone")));
				        	siteObject.put("escortContactName", CommonUtility.isNullForString(tempList.get(0).get("sc_escort_contact")));
				        	siteObject.put("escortContactNumber", CommonUtility.isNullForString(tempList.get(0).get("sc_escort_phone")));
				        	siteObject.put("localContactMobile", CommonUtility.isNullForString(tempList.get(0).get("sc_local_mobile")));
				        	siteObject.put("surveyDate", CommonUtility.isNullForString(tempList.get(0).get("site_survey_dt")).equals("00/00/0000") ? "" : CommonUtility.isNullForString(tempList.get(0).get("site_survey_dt")));
				        	siteObject.put("squareFootage", CommonUtility.isNullForString(tempList.get(0).get("site_square_footage")));
				        	siteObject.put("buildingType", CommonUtility.isNullForInteger(tempList.get(0).get("site_building_type")));
				        	siteObject.put("buildingNotes", CommonUtility.isNullForString(tempList.get(0).get("site_bilding_notes")));
				        	siteObject.put("technicianName", CommonUtility.isNullForString(tempList.get(0).get("site_survey_tecnician")));
				        	siteObject.put("technicianPhone", CommonUtility.isNullForString(tempList.get(0).get("site_technician_phone")));
				        	siteObject.put("technicianNotes", CommonUtility.isNullForString(tempList.get(0).get("site_technician_notes")));
				        	siteObject.put("cellularMode", CommonUtility.isNullForString(tempList.get(0).get("site_install_cellular_mode")));
				        	siteObject.put("cellularCoverage", CommonUtility.isNullForString(tempList.get(0).get("site_install_cellular_coverage")));
				        	
				        	siteObject.put("cellularProvide", CommonUtility.isNullForString(tempList.get(0).get("site_install_cellular_provider")));
				        	siteObject.put("cellularWIFI", CommonUtility.isNullForString(tempList.get(0).get("site_install_cellular_wifi")));
				        	siteObject.put("cellularWIFIPWD", CommonUtility.isNullForString(tempList.get(0).get("site_install_cellular_pw")));
				        	siteObject.put("modemType", CommonUtility.isNullForInteger(tempList.get(0).get("site_install_cellular_modem_type")));
				        	siteObject.put("simCardNumber", CommonUtility.isNullForString(tempList.get(0).get("site_install_cellular_sim_no")));
				        	siteObject.put("modemSerialNumber", CommonUtility.isNullForString(tempList.get(0).get("site_install_cellular_modern_serial_no")));
				        	siteObject.put("modemIPAddress", CommonUtility.isNullForString(tempList.get(0).get("site_install_cellular_modern_static_ip")));
				        	siteObject.put("modemPhoneNumber", CommonUtility.isNullForString(tempList.get(0).get("site_install_cellular_modern_phone_no")));
				        	
				        	siteObject.put("floorPlan", CommonUtility.isNullForString(tempList.get(0).get("site_thermostat_floor_plan")));
				        	siteObject.put("floorImage", CommonUtility.isNullForString(tempList.get(0).get("site_thermostat_floor_plan_image")));
				        	siteObject.put("buildingLayout", CommonUtility.isNullForString(tempList.get(0).get("site_thermostat_building_layout")));
				        	siteObject.put("buildingLayoutImage", CommonUtility.isNullForString(tempList.get(0).get("site_thermostat_building_layout_image")));
				        	siteObject.put("listaAccessRestrictionsFromFormalHours", CommonUtility.isNullForString(tempList.get(0).get("sa_restriction_hours")));
				        	siteObject.put("specialRoomAccessInformation", CommonUtility.isNullForString(tempList.get(0).get("sc_sp_room_info")));
				        	siteObject.put("lockBoxOnThermostat", CommonUtility.isNullForString(tempList.get(0).get("sa_lockbox_access")));
				        	siteObject.put("accessOrManagesThermostat", CommonUtility.isNullForString(tempList.get(0).get("sa_thermostat_access")));
				        	
				        	siteObject.put("siteTypeNew", CommonUtility.isNullForString(tempList.get(0).get("site_site_type_other")));
				        	siteObject.put("isHvacRunning", CommonUtility.isNullForString(tempList.get(0).get("site_hvac_is_running")));
				        	
				        	siteObject.put("siteHrsFormate", CommonUtility.isNullForInteger(tempList.get(0).get("site_hrs_formate")));
				        	siteObject.put("siteOccupyHrsFormate", CommonUtility.isNullForInteger(tempList.get(0).get("site_occupy_hrs_formate")));
				        	siteObject.put("siteTimeZone", CommonUtility.isNullForString(tempList.get(0).get("site_frn_time_zone_id")));
				        	siteObject.put("customerCode", CommonUtility.isNullForInteger(tempList.get(0).get("customer_code")));
				        	siteObject.put("siteStatus", CommonUtility.isNullForInteger(tempList.get(0).get("is_active")));
				        	siteObject.put("sameAsStore", CommonUtility.isNullForInteger(tempList.get(0).get("same_as_store")));
				        	siteObject.put("customerName", CommonUtility.isNullForInteger(tempList.get(0).get("company_name")));
				        	
				        	String siteStatusName = "";
				        	
				        	if(Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("is_active")).toString()) == 0)
				        		siteStatusName= "Inactive";
				        	else if(Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("is_active")).toString()) == 1)
				        		siteStatusName= "Active";
				        	else if(Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("is_active")).toString()) == 2)
				        		siteStatusName= "New";
				        	else if(Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("is_active")).toString()) == 3)
					        	siteStatusName= "Survey";
				        	else if(Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("is_active")).toString()) == 4)
					        	siteStatusName= "Install";
				        	else if(Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("is_active")).toString()) == 5)
					        	siteStatusName= "Waiting";
				        	else if(Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("is_active")).toString()) == 6)
					        	siteStatusName= "New ownership";
				        	else
				        		siteStatusName= "Closed";
				        	
				        	siteObject.put("siteStatusName", siteStatusName);
				        }
				        logger.debug("[DEBUG] siteObject---OBJECT-- "+siteObject);
				        
			        }else if(key.equals(RESULT_SET_2)){
			        	/*
				         * Forming the Site Hours data from the Site Hours table. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        for (int i = 0; i < tempList.size(); i++) {
				        	obj = new JSONObject();
				        	obj.put("id", CommonUtility.isNullForInteger(tempList.get(i).get("stt_id")));
				        	obj.put("dayName",CommonUtility.isNullForString(tempList.get(i).get("dow_display_name")));
				        	obj.put("dayOfWeek", CommonUtility.isNullForInteger(tempList.get(i).get("stt_frn_dow_id")));
				        	obj.put("openHrs", CommonUtility.isNullForString(tempList.get(i).get("stt_open_time")));
				        	obj.put("closeHrs", CommonUtility.isNullForString(tempList.get(i).get("stt_close_time")));
				        	siteHrs.add(obj);
						
				        }
				        logger.debug("[DEBUG] siteHrs---ARRAYLIST-- "+siteHrs);
			        
			        }else if(key.equals(RESULT_SET_3)){
			        	/*
				         * Forming the Occupy Hours data from the Site Hours table. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        for (int i = 0; i < tempList.size(); i++) {
				        	obj = new JSONObject();
				        	obj.put("id", CommonUtility.isNullForInteger(tempList.get(i).get("sto_id")));
				        	obj.put("dayName",CommonUtility.isNullForString(tempList.get(i).get("dow_display_name")));
				        	obj.put("dayOfWeek", CommonUtility.isNullForInteger(tempList.get(i).get("sto_frn_dow_id")));
				        	obj.put("openHrs", CommonUtility.isNullForString(tempList.get(i).get("sto_open_time")));
				        	obj.put("closeHrs", CommonUtility.isNullForString(tempList.get(i).get("sto_close_time")));
				        	siteOccupyHrs.add(obj);
						
				        }
				        logger.debug("[DEBUG] siteOccupyHrs---ARRAYLIST-- "+siteOccupyHrs);
			        
			        }else if(key.equals(RESULT_SET_4)){
			        	/*
				         * Forming the Groups data from the Group table. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        for (int i = 0; i < tempList.size(); i++) {
				        	
				        	groupObj = new JSONObject();
				        	groupObj.put(VALUE, CommonUtility.isNullForInteger(tempList.get(i).get("group_id")));
				        	groupObj.put(LABLE, CommonUtility.isNullForString(tempList.get(i).get("group_name")));
				        	
				        	groupArray.add(groupObj);
				        }
				        logger.debug("[DEBUG] groupArray---ARRAYLIST-- "+groupArray);
			        
			        }else if(key.equals(RESULT_SET_5)){
			        	/*
				         * Forming the HVAC data from the Site HVAC table. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        for (int i = 0; i < tempList.size(); i++) {
				        	
				        	obj = new JSONObject();
				        	obj.put("id", CommonUtility.isNullForInteger(tempList.get(i).get("sh_id")));
				        	obj.put("model", CommonUtility.isNullForString(tempList.get(i).get("sh_model")));
				        	obj.put("unit", CommonUtility.isNullForString(tempList.get(i).get("sh_unit")));
				        	obj.put("location", CommonUtility.isNullForInteger(tempList.get(i).get("sh_location")));
				        	
				        	obj.put("heating", CommonUtility.isNullForString(tempList.get(i).get("sh_heating_temp")));
				        	obj.put("cooling", CommonUtility.isNullForString(tempList.get(i).get("sh_cooling_temp")));
				        	obj.put("hVACUnit", CommonUtility.isNullForString(tempList.get(i).get("sh_unit_id")));
				        	
				        	hvacArray.add(obj);
				        }
				        logger.debug("[DEBUG] hvacArray---ARRAYLIST-- "+hvacArray);
			        
			        }else if(key.equals(RESULT_SET_6)){
			        	/*
				         * Forming the Site Thermostat data from the Site Thermostat table. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        for (int i = 0; i < tempList.size(); i++) {
				        	
				        	obj = new JSONObject();
				        	obj.put("id", CommonUtility.isNullForInteger(tempList.get(i).get("st_id")));
				        	obj.put("unit", CommonUtility.isNullForString(tempList.get(i).get("st_unit")));
				        	obj.put("locationImage", CommonUtility.isNullForString(tempList.get(i).get("st_site_photo_path")));
				        	obj.put("spaceEnough", CommonUtility.isNullForInteger(tempList.get(i).get("st_enough_space")));
				        	obj.put("make", CommonUtility.isNullForString(tempList.get(i).get("st_make")));
				        	obj.put("model", CommonUtility.isNullForString(tempList.get(i).get("st_model")));
				        	obj.put("wiringConfigThermostat", CommonUtility.isNullForString(tempList.get(i).get("st_wiring_config")));
				        	obj.put("wiringThermostatImage", CommonUtility.isNullForString(tempList.get(i).get("st_wiring_config_photo_path")));
				        	obj.put("rCAndCPower", CommonUtility.isNullForInteger(tempList.get(i).get("st_24dc_between_rc_c")));
				        	obj.put("cWireAttached", CommonUtility.isNullForInteger(tempList.get(i).get("st_c_wire_attached")));
				        	obj.put("noCWireAttached", CommonUtility.isNullForInteger(tempList.get(i).get("st_spare_unused_wire")));
				        	obj.put("automatedSchedule", CommonUtility.isNullForInteger(tempList.get(i).get("st_automated_schedule")));
				        	obj.put("locationOfRemoteSensor", CommonUtility.isNullForString(tempList.get(i).get("st_remote_sensor_location")));
				        	obj.put("validateSensor", CommonUtility.isNullForString(tempList.get(i).get("st_which_sensor_connected")));
				        	obj.put("wiringConfigSensor", CommonUtility.isNullForString(tempList.get(i).get("st_sensor_wiring")));
				        	obj.put("wiringSensorImage", CommonUtility.isNullForString(tempList.get(i).get("st_sensor_wiring_photo_path")));
				        	obj.put("automatedScheduleNote", CommonUtility.isNullForString(tempList.get(i).get("st_automated_schedule_note")));
				        	obj.put("locationType", CommonUtility.isNullForInteger(tempList.get(i).get("st_location_type")));
				        	obj.put("hVACUnit", CommonUtility.isNullForString(tempList.get(i).get("sh_unit")));
				        	obj.put("otherLocation", CommonUtility.isNullForString(tempList.get(i).get("st_other_location")));
				        	
				        	thermostatArray.add(obj);
				        }
				        logger.debug("[DEBUG] thermostatArray---ARRAYLIST-- "+thermostatArray);
			        
			        }else if(key.equals(RESULT_SET_7)){
			        	/*
				         * Forming the thermostate preferences data from the site_to_thermostat table. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        if(!tempList.isEmpty()) {
				        	/*
				        	 * Setting up the fanOn and fanAuto with proper values.
				        	 */
				        	int fanOn = 0;
				        	int fanAuto = 0;
				        	
				        	if("1".equals(CommonUtility.isNullForInteger(tempList.get(0).get("stt_fan_status")).toString()))
				        		fanOn = 1;
				        	else if("2".equals(CommonUtility.isNullForInteger(tempList.get(0).get("stt_fan_status")).toString()))
				        		fanAuto = 1;
				        	
				        	siteObject.put("fanOn", fanOn);
				        	siteObject.put("fanAuto", fanAuto);
				        	siteObject.put("lock", CommonUtility.isNullForInteger(tempList.get(0).get("stt_lock_status")));
				        	siteObject.put("isHVACModeToAuto", CommonUtility.isNullForInteger(tempList.get(0).get("stt_HVAC_to_auto")));
				        	siteObject.put("resetHoldMode", CommonUtility.isNullForInteger(tempList.get(0).get("stt_reset_hold_mode")));
				        	siteObject.put("nightlyScheduleDownload", CommonUtility.isNullForInteger(tempList.get(0).get("stt_night_schedule")));
				        	siteObject.put("maxSP", Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("stt_max_sp")).toString()));
				        	siteObject.put("minSP", Integer.parseInt(CommonUtility.isNullForInteger(tempList.get(0).get("stt_min_sp")).toString()));
						
				        }
				        logger.debug("[DEBUG] siteObject---OBJECT-- "+siteObject);
			        
			        }else if(key.equals(RESULT_SET_8)){
			        	/*
				         * Forming the tabs count data. 
				         */
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        if(!tempList.isEmpty()) {
				        					        	
				        	tabsCount.put("deviceCount", CommonUtility.isNullForInteger(tempList.get(0).get("device")));
				        	tabsCount.put("scheduleCount", CommonUtility.isNullForInteger(tempList.get(0).get("scheduled")));
				        	tabsCount.put("alertsCount", CommonUtility.isNullForInteger(tempList.get(0).get("alerts")));
				        	tabsCount.put("activityLogCount", CommonUtility.isNullForInteger(tempList.get(0).get("activity_log")));
				        	tabsCount.put("usersCount", CommonUtility.isNullForInteger(tempList.get(0).get("users")));
						
				        }
				        logger.debug("[DEBUG] siteTabsCountObject---OBJECT-- "+tabsCount);
			        
			        }
			        
			    }
			    
			    /*
			     * Attaching Site Hours data,
			     * Occupy Hours data,
			     * Site Groups data,
			     * HVAC list data and
			     * Thermostat List data
			     * to siteObject.
			     */
			    siteObject.put("siteHours", siteHrs);
			    siteObject.put("occupyHours", siteOccupyHrs);
			    siteObject.put(SITE_GROUPS, groupArray);
			    siteObject.put("rTUList", hvacArray);
			    siteObject.put("thermostatList", thermostatArray);
			    
			    WundergroundResponse wResp = wundergroundBusiness.processWundergroundAPI(zipCode);
				Weather weather = null;
				if(wResp.getCurrent_observation() != null){

					weather = new Weather();

					weather.setTemp_f(wResp.getCurrent_observation().getTemp_f());
					weather.setIcon_url(wResp.getCurrent_observation().getIcon_url());
					weather.setWeather(wResp.getCurrent_observation().getWeather());
					weather.setFeelslike_f(wResp.getCurrent_observation().getFeelslike_f());
					weather.setRelative_humidity(wResp.getCurrent_observation().getRelative_humidity());

					String forecastResp = wundergroundBusiness.getForeCastData(zipCode);
					
					if(forecastResp!=null){
						try{
								org.json.JSONObject forecastObj = new org.json.JSONObject(forecastResp);
								
								org.json.JSONObject nForecastObj = (org.json.JSONObject)forecastObj.get("forecast");
								
								org.json.JSONObject simpleForecastObj = (org.json.JSONObject)nForecastObj.get("simpleforecast");
								
								org.json.JSONArray forecastdayArray = (org.json.JSONArray)simpleForecastObj.get("forecastday");
								
								org.json.JSONObject today = (org.json.JSONObject)forecastdayArray.get(0);
								
								org.json.JSONObject todayMax = (org.json.JSONObject)today.get("high");
								
								int maxTempVal = todayMax.getInt("fahrenheit");
	
								org.json.JSONObject todayMin = (org.json.JSONObject)today.get("low");
								
								int minTempVal = todayMin.getInt("fahrenheit");
	
								weather.setHigh_temp(""+maxTempVal);
								weather.setLow_temp(""+minTempVal);
							
							}catch(Exception e){
								logger.error("error found while parsing 3 day forecast data to get min and max temperature value:",e);
							}
							
					}
				}
			    
			    
			    /*
			     * Preparing the final return object
			     */
			    siteFinalObject.put("siteData", siteObject);
			    siteFinalObject.put("prepopulateData", loadAddSite(page, id, userDetails));
			    siteFinalObject.put("weather", weather);
			    siteFinalObject.put("tabCounts", tabsCount);
			    
			    logger.debug("[DEBUG] siteFinalObject---OBJECT-- "+siteFinalObject);
			    
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_SITE_FAILED, logger, e);
		}
		
		logger.info("[END] [getSite] [Site DAO LAYER]");
		
		return siteFinalObject;
	}
	
	@Override
	public int checkSiteInternalId(GetSiteRequest getSiteRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [checkSiteInternalId] [Site DAO LAYER]");
		
		/*
		 * This variable is used to hold out param of stored procedure.
		 */
		int statusFlag = 0;
		
	    /*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_check_site_internal_id");

		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_site_id", CommonUtility.isNull(getSiteRequest.getSiteId()));
			inputParams.put("in_site_internal_id", getSiteRequest.getSiteInternalId());
			inputParams.put("in_customer_id", getSiteRequest.getCustomerId());
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_check_site_internal_id");
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        //getting the out flag value
        	statusFlag = (int) outParameters.get("out_flag");
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CHECK_SITE_INTERNAL_ID_FAILED, logger, e);
		}
		
		logger.info("[END] [checkSiteInternalId] [Site DAO LAYER]");

		return statusFlag;
	}
	
	@Override
	public int deleteSite(int siteId, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [deleteSite] [Site DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
		
		/*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		try {
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_delete_site");
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
		    Map<String,Object> inputParams=new HashMap<>();
			inputParams.put(TableFieldConstants.IN_SITE_ID, CommonUtility.isNull(siteId));
			inputParams.put(TableFieldConstants.IN_USER_ID, CommonUtility.isNull(userId));
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_delete_site");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
			
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the delete Site request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get(TableFieldConstants.OUT_ERROR_MSG);
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get(TableFieldConstants.OUT_FLAG);
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DELETE_SITE_FAILED, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DELETE_SITE_FAILED, logger, e);
		}
		
		logger.info("[END] [deleteSite] [Site DAO LAYER]");

		return statusFlag;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getSitesForGroups(String groupIds, GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [getSitesForGroups] [Site DAO LAYER]");
		Response response =new Response();
		
		JSONParser parser=new JSONParser();
		JSONArray siteArray=new JSONArray();
		JSONArray deviceArray = new JSONArray();
		JSONObject resultObj = new JSONObject();
		

		
		try {
			
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_get_sites_by_groups");
			Map<String, Object> inParamMap = new HashMap<String, Object>();
			inParamMap.put("in_group_ids", groupIds);
			inParamMap.put("in_super_admin", userDetails.getIsSuper());
			inParamMap.put("in_eai_admin", userDetails.getIsEai());
			inParamMap.put("in_user_id", userDetails.getUserId());
			
			logger.info("executing proc sp_get_sites_by_groups params "+inParamMap);
			
			SqlParameterSource in = new MapSqlParameterSource(inParamMap);
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			Iterator<Entry<String, Object>> itr = simpleJdbcCallResult.entrySet().iterator();

			while (itr.hasNext()) {
			        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
			        String key = entry.getKey();
			        if(key.equals(RESULT_SET_1)){
			        	
			        	Object value = entry.getValue();
			        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
			        	siteArray=(JSONArray)parser.parse(tempAry.toString());	
			        }else if(key.equals(RESULT_SET_2)){
			        	Object value = entry.getValue();
			        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
			        	deviceArray=(JSONArray)parser.parse(tempAry.toString());	
			        }
			    }
			
			resultObj.put("siteList", siteArray);
			resultObj.put("deviceList", deviceArray);
			   
			   
		} catch (Exception e) {
			logger.error("",e);
			resultObj = null;
			throw new VEMAppException("Internal Error occured at DAO layer");
			
		}
			    	
		logger.info("[END] [getSitesForGroups] [Site DAO LAYER]");
		
		return resultObj;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getGroupsByCustomer(int customerId) throws VEMAppException {

        logger.info("[BEGIN] [getGroupsByCustomer] [Site DAO LAYER]");
		
		// Initialization for key
		String key = "";
		
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_customer_groups");
			
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("in_customer_id",customerId);
			
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			// Fetching the created db flag
			key = simpleJdbcCallResult.get("dbFlag").toString();
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_EMAIL_EXIST_FAILED, logger, e);
		}
		logger.info("[END] [getGroupsByCustomer] [DAO LAYER]");
		return key;
	
	}

	@Override
	public String getStateCityId(String siteStateName, String siteCityName) throws VEMAppException {
        
		logger.info("[BEGIN] [getStateCityId] [Site DAO LAYER]");
		
		// Initialization for key
		String key;
		
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_site_state");
			
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("stateName",siteStateName);
			parameters.put("cityName",siteCityName);
			
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created db flag
			key = simpleJdbcCallResult.get("dbFlag").toString();
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_EMAIL_EXIST_FAILED, logger, e);
		}
		
		logger.info("[END] [getStateCityId] [DAO LAYER]");
		
		return key;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject addBulkUploadSites(UpdateSiteRequest addSiteRequest, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [addBulkUploadSites] [Site DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
		
		/*
		 * These two variables are used to make the newly added site group ids 
		 * into string bcz the MYSQL procedure do not accept the List,
		 * So separating the element using the ','.
		 */
		StringBuilder newSiteGroupIdsListStr = new StringBuilder();
		JSONObject result = new JSONObject();
		
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		/*
		 * Variables for lat and lang.
		 */
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_bulk_import_sites");
		    
		    /*
		     * Making the newly added site groupids into a string and appending the each
		     * element using ','.
		     */
		    List<String> newSiteGroupIdsList = addSiteRequest.getSiteGroups();
		    if(newSiteGroupIdsList != null && (newSiteGroupIdsList.size()) !=0){
			    for (int i = 0; i < newSiteGroupIdsList.size(); i++) {
			    	newSiteGroupIdsListStr.append(newSiteGroupIdsList.get(i)+",");
				}
			    
			    /*
			     * Trimming the last unnecessary ','.
			     */
			    newSiteGroupIdsListStr.replace(newSiteGroupIdsListStr.length()-1, newSiteGroupIdsListStr.length(), "");
		    }
		    
		    /*
		     * if the fan on is 1 then fanStatus is 1
		     * else if fan auto is 1 then fanStatus is 1
		     * else fanStatus is 0
		     */
		    int fanStatus = 0;
		    if(CommonUtility.isNull(addSiteRequest.getFanOn()) == 1)
		    	fanStatus = 1;
		    else if(CommonUtility.isNull(addSiteRequest.getFanAuto()) == 1)
		    	fanStatus = 2;
		    
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_site_name", CommonUtility.isNull(addSiteRequest.getSiteName()));
			inputParams.put(TableFieldConstants.IN_SITE_INTERNAL_ID, CommonUtility.isNull(addSiteRequest.getSiteInternalId()));
			inputParams.put("in_site_type_id", CommonUtility.isNull(addSiteRequest.getSiteType()));
			inputParams.put("in_site_type_other", CommonUtility.isNull(addSiteRequest.getSiteTypeNew()));
			inputParams.put("in_site_phone", CommonUtility.isNull(addSiteRequest.getSitePhNo()));
			inputParams.put("in_site_district", CommonUtility.isNull(addSiteRequest.getSiteDistrict()));
			inputParams.put("in_site_area", CommonUtility.isNull(addSiteRequest.getSiteArea()));
			inputParams.put("in_site_region", CommonUtility.isNull(addSiteRequest.getSiteRegion()));
			inputParams.put("in_site_grp_ids_str", CommonUtility.isNull(newSiteGroupIdsListStr.toString()));
			inputParams.put("in_site_degree_pref", CommonUtility.isNull(addSiteRequest.getDegreePreference()));
			inputParams.put("in_site_add1", CommonUtility.isNull(addSiteRequest.getSiteAddLine1()));
			inputParams.put("in_site_add2", CommonUtility.isNull(addSiteRequest.getSiteAddLine2()));
			inputParams.put("in_site_lat", CommonUtility.isNull(addSiteRequest.getLatitude()));
			inputParams.put("in_site_lang", CommonUtility.isNull(addSiteRequest.getLangitude()));
			inputParams.put("in_site_city_id", CommonUtility.isNull(addSiteRequest.getSiteCity()));
			inputParams.put("in_site_zip_code", CommonUtility.isNull(addSiteRequest.getSiteZipCode()));
			inputParams.put("in_site_hours_str", addSiteRequest.getSiteStoreHours());
			inputParams.put("in_fan_status", CommonUtility.isNull(fanStatus));
			inputParams.put("in_lock_status", CommonUtility.isNull(addSiteRequest.getLock()));
			inputParams.put("in_same_as_store", CommonUtility.isNull(addSiteRequest.getSameAsStore()));
			inputParams.put("in_rst_HVAC_to_auto", CommonUtility.isNull(addSiteRequest.getIsHVACModeToAuto()));
			inputParams.put("in_rst_hold_mode", CommonUtility.isNull(addSiteRequest.getResetHoldMode()));
			inputParams.put("in_nightly_download", CommonUtility.isNull(addSiteRequest.getNightlyScheduleDownload()));
			inputParams.put(TableFieldConstants.IN_USER_ID, CommonUtility.isNull(userId));
			inputParams.put(TableFieldConstants.IN_CUSTOMER_ID, CommonUtility.isNull(addSiteRequest.getCustomerId()));
			inputParams.put("in_site_hrs_formate", CommonUtility.isNull(addSiteRequest.getSiteHrsFormate()));
			inputParams.put("in_site_frn_time_zone_id", CommonUtility.isNull(addSiteRequest.getSiteTimeZone()));
			inputParams.put("in_site_frn_time_zone_std", CommonUtility.isNull(addSiteRequest.getSiteTimeZoneStd()));
			inputParams.put("in_site_frn_time_zone_dls", CommonUtility.isNull(addSiteRequest.getSiteTimeZoneDls()));
            inputParams.put("in_max_set_point",CommonUtility.isNull(addSiteRequest.getMaxSP()));
			inputParams.put("in_min_set_point",CommonUtility.isNull(addSiteRequest.getMinSP()));
			inputParams.put("in_selected_user_id",CommonUtility.isNull(userId));
			inputParams.put("in_rtu_str",CommonUtility.isNull(addSiteRequest.getHvacDetails()));
			inputParams.put("in_thermostat_str",CommonUtility.isNull(addSiteRequest.getThermostatDetails()));
			inputParams.put("in_is_hvac_running",CommonUtility.isNull(addSiteRequest.getEmpBelieveSystem()));
			
			inputParams.put("in_survey_date",CommonUtility.isNull(addSiteRequest.getSurveyDate()));
			inputParams.put("in_square_footage",CommonUtility.isNull(addSiteRequest.getSquareFootage()));
			inputParams.put("in_site_occupy_hrs_formate",CommonUtility.isNull(addSiteRequest.getSiteOccupyHrsFormate()));
			inputParams.put("in_occupy_hrs_str",addSiteRequest.getSiteOccupyHours());
			inputParams.put("in_building_layout",CommonUtility.isNull(addSiteRequest.getBuildingLayout()));
			inputParams.put("in_floor_plan",CommonUtility.isNull(addSiteRequest.getFloorPlan()));
			
			inputParams.put("in_building_type",CommonUtility.isNull(addSiteRequest.getBuildingType()));
			inputParams.put("in_building_notes",CommonUtility.isNull(addSiteRequest.getBuildingNotes()));
			
			inputParams.put("in_technician_name",CommonUtility.isNull(addSiteRequest.getTechnicianName()));
			inputParams.put("in_technician_phone",CommonUtility.isNull(addSiteRequest.getTechnicianPhone()));
			inputParams.put("in_technician_notes",CommonUtility.isNull(addSiteRequest.getTechnicianNotes()));
			
			inputParams.put("in_local_site_contact",CommonUtility.isNull(addSiteRequest.getLocalSiteContact()));
			inputParams.put("in_local_contact_phone",CommonUtility.isNull(addSiteRequest.getLocalContactPhone()));
			inputParams.put("in_local_contact_email",CommonUtility.isNull(addSiteRequest.getLocalContactEmail()));
			inputParams.put("in_local_contact_mobile",CommonUtility.isNull(addSiteRequest.getLocalContactMobile()));
			inputParams.put("in_altr_site_contact",CommonUtility.isNull(addSiteRequest.getAlternateSiteContact()));
			inputParams.put("in_altr_contact_phone",CommonUtility.isNull(addSiteRequest.getAlternateContactPhone()));
			inputParams.put("in_escort_contact",CommonUtility.isNull(addSiteRequest.getEscortContactName()));
			inputParams.put("in_escort_phone",CommonUtility.isNull(addSiteRequest.getEscortContactNumber()));
			
			inputParams.put("in_access_restr_formal_hrs",CommonUtility.isNull(addSiteRequest.getListaAccessRestrictionsFromFormalHours()));
			inputParams.put("in_special_room_access_info",CommonUtility.isNull(addSiteRequest.getSpecialRoomAccessInformation()));
			inputParams.put("in_lock_box_thermostat",CommonUtility.isNull(addSiteRequest.getLockBoxOnThermostat()));
			inputParams.put("in_access_manage_thermostat",CommonUtility.isNull(addSiteRequest.getAccessOrManagesThermostat()));
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_bulk_import_sites");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the add Site request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get(TableFieldConstants.OUT_ERROR_MSG);
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get(TableFieldConstants.OUT_FLAG);
	        }
	        
	        result.put("errorMsg", errorMsg);
	        result.put("insertId", statusFlag);
			
		}catch (Exception e) {
			
			logger.error("Code : "+ErrorCodes.GENERAL_APP_ERROR + "  Msg Code : "+ErrorCodes.ADD_SITE_FAILED, e);
			
		}
		
		logger.info("[END] [addBulkUploadSites] [Site DAO LAYER]");

		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject updateBulkUploadSites(UpdateSiteRequest addSiteRequest, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [updateBulkUploadSites] [Site DAO LAYER]");		
		int statusFlag = 0;
		JSONObject result = new JSONObject();
		
		try {
		    
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_bulk_import_sites_additional_info");
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("var_site_id", addSiteRequest.getSiteId());
			inputParams.put("in_user_id", userId);
			inputParams.put("in_customer_id", addSiteRequest.getCustomerId());
			inputParams.put("in_rtu_str", addSiteRequest.getHvacDetails());
			inputParams.put("in_thermostat_str", addSiteRequest.getThermostatDetails());
			
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_bulk_import_sites_additional_info");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
			Map<String,Object> outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the add Site request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        String errorMsg = (String) outParameters.get(TableFieldConstants.OUT_ERROR_MSG);
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get(TableFieldConstants.OUT_FLAG);
	        }
	        
	        result.put("errorMsg", errorMsg);
	        result.put("insertId", statusFlag);
			
		} catch (Exception e) {			
			logger.error("Code : "+ErrorCodes.GENERAL_APP_ERROR + "  Msg Code : "+ErrorCodes.ADD_SITE_FAILED, e);			
		}		
		logger.info("[END] [addBulkUploadSites] [Site DAO LAYER]");
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject updateBulkUploadProgress(String sheetName, int currentCounter, int tatalRecords, int failedCount, long userId, String status) throws VEMAppException {
		
		logger.info("[BEGIN] [updateBulkUploadProgress] [Site DAO LAYER]");		
		int statusFlag = 0;
		JSONObject result = new JSONObject();
		
		try {
		    
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_update_bulk_upload_progress");
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("upload_sheet_name", sheetName);
			inputParams.put("current_counter", currentCounter);
			inputParams.put("total_number_records", tatalRecords);
			inputParams.put("number_of_failed_records", failedCount);
			inputParams.put("user_id", userId);
			inputParams.put("upload_progress_status", status);
			
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_update_bulk_upload_progress");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
			Map<String,Object> outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the add Site request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
			String errorMsg = (String) outParameters.get(TableFieldConstants.OUT_ERROR_MSG);
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get(TableFieldConstants.OUT_FLAG);
	        }
	        
	        result.put("errorMsg", errorMsg);
	        result.put("insertId", statusFlag);
			
		} catch (Exception e) {			
			logger.error("Code : "+ErrorCodes.GENERAL_APP_ERROR + "  Msg Code : "+ErrorCodes.ADD_SITE_FAILED, e);			
		}		
		logger.info("[END] [addBulkUploadSites] [Site DAO LAYER]");
		return result;
	}
	
	@Override
	public int validateSiteNameIsAlreadyExists( String action, String siteName,  String customerId, String siteId) throws VEMAppException {		
		logger.info("[BEGIN] [SiteDaoImpl] [validateSiteNameIsAlreadyExists] Inside the SiteDaoImpl : validateSiteNameIsAlreadyExists() =====>");
		int count = 0;
		if("Insert".equalsIgnoreCase(action)) {
			String sql = "select count(site_id) as COUNT from site where upper(site_name) = upper(?) and site_frn_customer_id = ? and is_deleted = 0";
			count = jdbcTemplate.queryForObject(sql, new Object[] { siteName, customerId }, Integer.class);	
		} else {
			String sql = "select count(site_id) as COUNT from site where upper(site_name) = upper(?) and site_frn_customer_id = ? and site_id not in (?) and is_deleted = 0";
			count = jdbcTemplate.queryForObject(sql, new Object[] { siteName, customerId , siteId}, Integer.class);	
		}
		return count;
	}
	
	@Override
	public boolean isHvacUnitDuplicate(long siteId, String unit) throws VEMAppException {		
		logger.info("[BEGIN] [SiteDaoImpl] [isHvacUnitDuplicate] Inside the SiteDaoImpl : =====>");
		int count = 0;
		try {
			String sql = "SELECT count(sh_id) as count FROM site_hvac where sh_frn_site_id = ? and trim(upper(sh_unit)) = trim(upper(?)) and is_deleted = 0";
			count = jdbcTemplate.queryForObject(sql, new Object[] { siteId, unit }, Integer.class);
			if (count > 0) {
				return true;
			}
		} catch (DataAccessException e) {
			logger.error("isHvacUnitDuplicate ", e);
		}	
		return false;
	}
	
	/**
	 * 
	 */
	public JSONArray getGeoCodeData(String zipCode) throws VEMAppException {
		logger.debug("[BEGIN] [getGeoCodeData] [SITE DAO LAYER]");
		Map<String,Object> outParameters = null;
		JSONArray resultArray=new JSONArray();
		JSONParser parser=new JSONParser();		
		try {
			
			SimpleJdbcCall simpleJdbcCall=new SimpleJdbcCall(dataSource);
			simpleJdbcCall.withProcedureName("sp_get_geo_codes");
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_zip_code", zipCode);
						
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+" sp_get_geo_codes with input params "+inputParams);
			outParameters =simpleJdbcCall.execute(inputParams);
			
			Iterator<Entry<String, Object>> itr = outParameters.entrySet().iterator();				
			
			while (itr.hasNext()) {
		        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
		        String key = entry.getKey();
		        
		        if(key.equals(CommonConstants.RESULT_SET_1))
		        {
		        	Object value = (Object) entry.getValue();
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	resultArray=(JSONArray)parser.parse(tempAry.toString());					
				}
			}			
		} catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, "getGeoCodeData", logger, e);
		}
		logger.debug("[END] [getGeoCodeData] [SITE DAO LAYER]");
		return resultArray;
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public JSONObject insertGeoCodeData(String stateName, String stateCode, String cityName, String zipCode,
			String latitude, String longitude, String timezone) throws VEMAppException {
		/*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		JSONObject resultJson = new JSONObject();
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_insert_geo_codes");
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
		    Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_state_name", CommonUtility.isNull(stateName));
			inputParams.put("in_state_code", CommonUtility.isNull(stateCode));
			inputParams.put("in_city_name", CommonUtility.isNull(cityName));
			inputParams.put("in_zip_code", CommonUtility.isNull(zipCode));
			inputParams.put("in_latitude", CommonUtility.isNull(latitude));
			inputParams.put("in_longitude", CommonUtility.isNull(longitude));
			inputParams.put("in_timezone", CommonUtility.isNull(timezone));
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_insert_geo_codes");
			logger.debug("[DEBUG] "+CommonConstants.INPUT+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the add Site request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        String errorMessage = (String) outParameters.get(TableFieldConstants.OUT_ERROR_MSG);
	        //int errorCode = Integer.valueOf((String) outParameters.get(TableFieldConstants.OUT_FLAG));
			if (errorMessage.isEmpty()) {
				int state_id = (Integer) outParameters.get(TableFieldConstants.OUT_STATE_ID);
				int city_id = (Integer) outParameters.get(TableFieldConstants.OUT_CITY_ID);
				
				resultJson.put("state_id", state_id);
				resultJson.put("state_name", stateName);
				resultJson.put("state_short_code", stateCode);
				resultJson.put("city_id", city_id);
				resultJson.put("city_name", cityName);
				resultJson.put("zip_code", zipCode);
				resultJson.put("latitude", latitude);
				resultJson.put("longitude", longitude);
				resultJson.put("timezone", timezone);
				
			} else {
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_GEOCODESDATA_FAILED, logger,
						new Exception(errorMessage));
			}			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_GEOCODESDATA_FAILED, logger, e);
		}
		return resultJson;
	}
}
