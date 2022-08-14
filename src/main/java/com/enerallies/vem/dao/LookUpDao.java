/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.dao;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : LookUpDao 
 * 
 * LookUpDao dao is used to get the data from lookup tables(City and State).
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

@Repository
public interface LookUpDao {
	
	/**
	  * This is the method to be used to initialize database resources i.e. connection.
	  * 
	  * @param dataSource
	  */
	public void setDataSource(DataSource dataSource);
	
	/**
	  * getCities dao is used to get the data from city table.
	  * 
	  * @param stateId
	  * @return JSONArray
	  * @throws VEMAppException
	  */
	public JSONArray getCities(int stateId) throws VEMAppException;
	
	/**
	  * getStates dao is used to get the data from states table.
	  * 
	  * @return JSONArray
	  * @throws VEMAppException
	  */
	
public JSONArray getCitiesSearch(String searchText) throws VEMAppException;
	
	/**
	  * getStates dao is used to get the data from states table.
	  * 
	  * @return JSONArray
	  * @throws VEMAppException
	  */
	public JSONArray getStates() throws VEMAppException;
	
	/**
	  * loadRestUserByUsername dao is used to get the Rest call related digest values
	  * from DB for specified user.
	  * 
	  * @param restUserName
	  * @return JSONObject
	  * @throws VEMAppException
	  */
	public JSONObject loadRestUserByUsername(String restUserName) throws VEMAppException;
	
	/**
	  * loadRestUserByUsername dao is used to get the Rest call related digest values
	  * from DB for specified user.
	  * 
	  * @param restUserName
	  * @return JSONObject
	  * @throws VEMAppException
	  */
	public JSONObject getTimeZone(String zipCode) throws VEMAppException;

}
