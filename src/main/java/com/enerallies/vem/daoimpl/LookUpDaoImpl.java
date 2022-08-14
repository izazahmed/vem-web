/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.daoimpl;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import com.enerallies.vem.dao.LookUpDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : LookUpDaoImpl 
 * 
 * LookUpDaoImpl an implementation class for LookUpDao interface.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 08-09-2016		Goush Basha		    File Created & getCities(), getStates() methods are added(Sprint-3).
 * 19-09-2016		Goush Basha		    Added one param state id for getCities() and remove the from eh cache(Sprint-3).
 * 
 */

@Component
public class LookUpDaoImpl implements LookUpDao{
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(LookUpDaoImpl.class);
	
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
	public JSONArray getCities(int stateId) throws VEMAppException {

		logger.info("[BEGIN] [getCities] [LookUp DAO LAYER]");
		
		/*
		 * Used to hold all the list of cities.
		 */
		JSONArray cityArray= new JSONArray();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_look_up_cities procedure.");
			
			jdbcTemplate.query("call sp_look_up_cities ("+stateId+")", new RowCallbackHandler() {
				/*
				 * Used to hold the each record of city.
				 */
				JSONObject cityObj = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {

			    	logger.debug("[DEbug] the city_id - "+rs.getString("city_id"));
			    	
			    	/*
			    	 * Instantiating and setting all the details of
			    	 * each city from list. 
			    	 */
			    	cityObj = new JSONObject();
			    	cityObj.put("cityId", CommonUtility.isNull(rs.getInt("city_id")));
			    	cityObj.put("stateId", CommonUtility.isNull(rs.getString("city_frn_state_id")));
			    	cityObj.put("city", "");
			    	cityObj.put("cityName", CommonUtility.isNull(rs.getString("city_name")));
			    	cityObj.put("zipCode", CommonUtility.isNull(rs.getString("zip_code")));
			    	/*
			    	 * Adding the each site object to the cities list.
			    	 */
			    	cityArray.add(cityObj);
				
				}
			});
			
			logger.debug("[DEBUG] City JSONArray - "+cityArray);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOOKUP_CITIES_FAILED, logger, e);
		}
		
		logger.info("[END] [getCities] [LookUp DAO LAYER]");
		
		return cityArray;
	
	}
	@Override
	public JSONArray getCitiesSearch(String searchText) throws VEMAppException {

		logger.info("[BEGIN] [getCities] [LookUp DAO LAYER]");
		
		/*
		 * Used to hold all the list of cities.
		 */
		JSONArray cityArray= new JSONArray();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_look_up_cities procedure.");
			
			jdbcTemplate.query("call sp_look_up_cities_search_text ('"+searchText+"')", new RowCallbackHandler() {
				/*
				 * Used to hold the each record of city.
				 */
				JSONObject cityObj = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {

			    	logger.debug("[DEbug] the city_id - "+rs.getString("city_id"));
			    	
			    	/*
			    	 * Instantiating and setting all the details of
			    	 * each city from list. 
			    	 */
			    	cityObj = new JSONObject();
			    	cityObj.put("cityId", CommonUtility.isNull(rs.getInt("city_id")));
			    	cityObj.put("stateId", CommonUtility.isNull(rs.getString("city_frn_state_id")));
			    	cityObj.put("city", "");
			    	cityObj.put("cityName", CommonUtility.isNull(rs.getString("city_name")));
			    	cityObj.put("zipCode", CommonUtility.isNull(rs.getString("zip_code")));
			    	/*
			    	 * Adding the each site object to the cities list.
			    	 */
			    	cityArray.add(cityObj);
				
				}
			});
			
			logger.debug("[DEBUG] City JSONArray - "+cityArray);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOOKUP_CITIES_FAILED, logger, e);
		}
		
		logger.info("[END] [getCities] [LookUp DAO LAYER]");
		
		return cityArray;
	
	}
	@Override
	@Cacheable("states")
	public JSONArray getStates() throws VEMAppException {

		logger.info("[BEGIN] [getStates] [LookUp DAO LAYER]");
		
		/*
		 * Used to hold all the list of states.
		 */
		JSONArray stateArray= new JSONArray();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_look_up_states procedure.");
			
			jdbcTemplate.query("call sp_look_up_states ()", new RowCallbackHandler() {
				/*
				 * Used to hold the each record of state.
				 */
				JSONObject stateObj = null;
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {

			    	logger.debug("[DEbug] the state_id - "+rs.getString("state_id"));
			    	
			    	/*
			    	 * Instantiating and setting all the details of
			    	 * each state from list. 
			    	 */
			    	stateObj = new JSONObject();
			    	stateObj.put("stateId", CommonUtility.isNull(rs.getInt("state_id")));
			    	stateObj.put("countryId", CommonUtility.isNull(rs.getInt("state_frn_country_id")));
			    	stateObj.put("state", CommonUtility.isNull(rs.getString("state")));
			    	stateObj.put("stateName", CommonUtility.isNull(rs.getString("state_name")));

			    	/*
			    	 * Adding the each site object to the states list.
			    	 */
			    	stateArray.add(stateObj);
				
				}
			});
			
			logger.debug("[DEBUG] getStates JSONArray - "+stateArray);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOOKUP_STATES_FAILED, logger, e);
		}
		
		logger.info("[END] [getStates] [LookUp DAO LAYER]");
		
		return stateArray;
	
	}
	@Override
	@Cacheable("loadRestUserByUsername")
	public JSONObject loadRestUserByUsername(String restUserName) throws VEMAppException {

		logger.info("[BEGIN] [loadRestUserByUsername] [LookUp DAO LAYER]");
		
		/*
		 * Used to hold all the Rest user digest values.
		 */
		JSONObject restUserObject= new JSONObject();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_select_rest_user procedure.");
			
			jdbcTemplate.query("call sp_select_rest_user ('"+restUserName+"')", new RowCallbackHandler() {
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {
			    	/*
			    	 * Getting the rest user details and filling them in restobject. 
			    	 */
			    	restUserObject.put("userName", CommonUtility.isNull(rs.getString("rest_user_name")));
			    	restUserObject.put("password", CommonUtility.isNull(rs.getString("rest_password")));

				}
			});
			
			logger.debug("[DEBUG] load rest user JSONObject - "+restUserObject);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOOKUP_REST_USER_FAILED, logger, e);
		}
		
		logger.info("[END] [loadRestUserByUsername] [LookUp DAO LAYER]");
		
		return restUserObject;
	
	}
	
	@Override
	@Cacheable("getTimeZone")
	public JSONObject getTimeZone(String zipCode) throws VEMAppException {

		logger.info("[BEGIN] [getTimeZone] [LookUp DAO LAYER]");
		
		JSONObject timeZone = new JSONObject();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_select_timezone procedure.");
			
		    int zipCodeLength = zipCode.length();
		    if(zipCodeLength < 5){
		    	int remainingZeros= 5 - zipCodeLength;
		    	String zeros="";
		    	for (int i = 1; i <= remainingZeros; i++) {
		    		zeros = zeros+"0";
				}
		    	zipCode =zeros + zipCode;
		    }
		    logger.debug("[DEbug] the zipCode-"+zipCode);
			jdbcTemplate.query("call sp_select_timezone ('"+zipCode+"')", new RowCallbackHandler() {
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {

			    	logger.debug("[DEbug] the timezone - "+rs.getString("timezone"));
			    	timeZone.put("timeZone", CommonUtility.isNull(rs.getString("timezone")));
				
				}
			});
			
			
			logger.debug("[DEBUG] getTimezone timezone - "+timeZone);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOOKUP_GET_TIMEZONE_FAILED, logger, e);
		}
		
		logger.info("[END] [getTimezone] [LookUp DAO LAYER]");
		
		return timeZone;
	
	}

}
