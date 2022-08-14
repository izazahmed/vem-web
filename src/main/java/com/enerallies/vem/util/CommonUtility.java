/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.enerallies.vem.exceptions.VEMAppException;


/**
 * File Name : CommonUtility 
 * CommonUtility: Utility class for some common methods.
 *
 * @author (Goush Basha – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        19-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 19-09-2016		Goush Basha			File Created
 *
 */

public class CommonUtility {

	/*
	 * Adding the private constructor to avoid creation of object.
	 */
	private CommonUtility(){}; 
	
	/*
	 *  Getting logger
	 */
	private static final Logger logger = Logger.getLogger(CommonUtility.class);
	
	/**
	 * isNull(String) utility is used check the input param is null
	 * or not. If it is null then return empty otherwise same value. 
	 *  
	 * @param strValue
	 * @return String
	 */
	public static String isNull(String strValue) {
		
		return strValue == null ? "" : strValue;
	}
	
	/**
	 * isNull(Integer) utility is used check the input param is null
	 * or not. If it is null then return 0 otherwise same value. 
	 *  
	 * @param intValue
	 * @return Integer
	 */
	public static Integer isNull(Integer intValue) {
		
		return intValue == null ? 0 : intValue;
	}
	
	/**
	 * isNull(Object) utility is used check the input param is null
	 * or not. If it is null then return 0 otherwise same value. 
	 *  
	 * @param objValue
	 * @return Object
	 */
	public static Object isNullForString(Object objValue) {
		return objValue == null ? "" : objValue;
	}
	public static Object isNullForInteger(Object objValue) {
		return objValue == null ? 0 : objValue;
	}
	public static String phoneValidate(String field) throws Exception{
		logger.info("UploadUsersTemplate validate_phone start");
		String result="No";
		boolean b=false;
		Pattern p=null;
		Matcher m=null;
		try{
			p = Pattern.compile("^(?=.*[0-9])[- ()0-9]+$");
	        m = p.matcher(field);
	        b = m.find();
	        if (b == false)
	        	result="Yess";
	       
		}catch (Exception e) {
	    	logger.error("UploadUsersTemplate validate_phone Error:",e);
	    	throw new Exception(e);
		}
		logger.info("UploadUsersTemplate validate_phone end");
		return result;
	}
	
	public static String emailValidate(String field) throws Exception{
		logger.info("UploadUsersTemplate checkSpecialCharectorsMail start");
		String result="No",emailregex="";
		Boolean b=false;
		try{
	        emailregex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	        b = field.matches(emailregex);
	        if (b == false) {
	        	result="Yess";
	            }
		}catch (Exception e) {
			
	    	logger.error("UploadUsersTemplate checkSpecialCharectorsMail Error:",e);
	    	throw new Exception(e);
		}
		logger.info("UploadUsersTemplate checkSpecialCharectorsMail end");
		return result;
	}
	
	/**
	 * restClient(String) utility is used to execute the Given API call
	 * if success it will return JSON string else "FAILURE" string . 
	 *  
	 * @param address
	 * @return JSONObject
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public static JSONObject getLatitudeAndLangitute(String address){
		logger.info("CommonUtility getLatitudeAndLangitute Start");
		String output="";
		StringBuilder operationResponse = new StringBuilder();
		HttpURLConnection conn = null;
		BufferedReader br = null;
		JSONObject resultJson = null;
		try {
			String geocodingUrl = ConfigurationUtils.getConfig("geocoding.url");
			String apikey = ConfigurationUtils.getConfig("YOUR_API_KEY");
			String strUrl=geocodingUrl+apikey+"&address="+address;
			logger.debug("strUrl::"+strUrl);
    		URL url = new URL(strUrl);
    		conn = (HttpURLConnection) url.openConnection();
    		conn.setRequestMethod("GET");
    		conn.setRequestProperty("Accept", "application/json");

    		if (conn.getResponseCode() != 200) {
    			operationResponse.append("FAILURE");
    			logger.error("CommonUtility getLatitudeAndLangitute Failed : HTTP error code : "+ conn.getResponseCode());
    		}else{
    			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        		while ((output = br.readLine()) != null) {
        			operationResponse.append(new String(output.getBytes(), "UTF-8"));
        		}
        		logger.debug("The JSON is :"+operationResponse);
        		
        		if(!operationResponse.toString().isEmpty()){
        			JSONParser parser = new JSONParser();
        			JSONObject jsonObject = (JSONObject) parser.parse(operationResponse.toString());
        			if(jsonObject.get("status").toString().equalsIgnoreCase("OK")){
	        			JSONArray jsonArray = (JSONArray) jsonObject.get("results");
	        			if(!jsonArray.isEmpty()){
		        			jsonObject = (JSONObject) jsonArray.get(0);
		        			jsonObject = (JSONObject)jsonObject.get("geometry");
		        			resultJson = (JSONObject)jsonObject.get("location");
	        			}
        			}
        			logger.debug("CommonUtility getLatitudeAndLangitute resultJson:"+resultJson);
        			
        		}
    		}

    	  }catch (MalformedURLException e) {
    	      logger.error("CommonUtility getLatitudeAndLangitute Error:",e);
    	  }catch (IOException e) {
    		  logger.error("CommonUtility getLatitudeAndLangitute Error:",e);
    	  }catch (ParseException e) {
    		  logger.error("CommonUtility getLatitudeAndLangitute Error:",e);
    	  }finally{
    		  if(conn != null){
    			  conn.disconnect();
    		  }
    		  try{
    			  if(br != null){
        			  br.close();
        		  }  
    		  }catch (IOException e) {
        		  logger.error("CommonUtility getLatitudeAndLangitute Error:",e);
        	  }
    		  
    	  }
		logger.info("CommonUtility getLatitudeAndLangitute end");
		return resultJson;
		
	}
	
	/**
	 * This method is used to get the time zone details based on the address
	 * 
	 * @param address
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getTimeZone(String address) throws Exception {
		
		// Declaring and initializing the resultObj JSONObject
		JSONObject resultObj = null;
		try {
			// Instantiating the HttpRestClient 
			HttpRestClient httpRestClient = new HttpRestClient();
			
			// Constructing the geoCodeAPI String using the MessageFormat
			String geoCodeAPI = MessageFormat.format(ConfigurationUtils.getConfig("geocode.api.url"),
					URLEncoder.encode(address, "UTF-8"),
					URLEncoder.encode(ConfigurationUtils.getConfig("geocode.api.key"), "UTF-8"));
			
			// Calling the sendGet method of HttpRestClient to get the geoData
			String geoDataJsonString = httpRestClient.sendGet(geoCodeAPI);
			
			// Converting the geoDataJsonString String to JsonNode
			JsonNode geoDataJsonNode = new ObjectMapper().readValue(geoDataJsonString, JsonNode.class);
			
			// Reading the Status from geoDataJsonNode
			String geoDataStatus = geoDataJsonNode.get("status").asText();
			
			// Validating the geoDataStatus  
			if(geoDataStatus != null && "OK".equalsIgnoreCase(geoDataStatus)) {
				
				// Reading the location JsonNode from geoDataJsonNode
				JsonNode locationNode = geoDataJsonNode.get("results").get(0).get("geometry").get("location");
				
				// Reading the lat info form locationNode
				String lat = locationNode.get("lat").asText();
				// Reading the lng info form locationNode
				String lng = locationNode.get("lng").asText();
				// Getting the current Time Stamp
				long epoch = (new Date().getTime()) / 1000;
				
				// Constructing the timeZoneAPI String using the MessageFormat
				String timeZoneAPI = MessageFormat.format(ConfigurationUtils.getConfig("timezone.api.url"), lat, lng, String.valueOf(epoch),
						URLEncoder.encode(ConfigurationUtils.getConfig("timezone.api.key"), "UTF-8"));
				
				// Calling the sendGet method of HttpRestClient to get the timeZoneData 
				String timeZoneDataJsonString = httpRestClient.sendGet(timeZoneAPI);
				
				// Converting the timeZoneResultJsonString String to JsonNode  
				JsonNode timeZoneJsonNode = new ObjectMapper().readValue(timeZoneDataJsonString, JsonNode.class);
				
				// Reading the status from timeZoneJsonNode
				String timeZoneStatus = timeZoneJsonNode.get("status").asText();
				
				// Validating the timeZoneStatus
				if(timeZoneStatus != null && "OK".equalsIgnoreCase(timeZoneStatus)) {
					
					//Reading the timeZoneId form timeZoneJsonNode
					String timeZoneId = timeZoneJsonNode.get("timeZoneId").asText();
					
					// Validating the timeZoneId
					if(timeZoneId != null) {
						
						// Getting the timeZoneShort form using TimeZone.getTimeZone() method
						String timeZoneShort = TimeZone.getTimeZone(timeZoneId).getDisplayName(true, TimeZone.SHORT);
						
						// Instantiating the resultObj JSONObject 
						resultObj = new JSONObject();
						
						// Adding the timeZone to resultObj JSON
						resultObj.put("timeZone", timeZoneId);
						// Adding the timeZoneShort to resultObj JSON
						resultObj.put("timeZoneShort", timeZoneShort);					
					}	
				}
			}
		}catch(Exception exception) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_TIMEZONE_DETAILS_FAILED, logger, exception);
		}
		return resultObj;
	}
	
	public static boolean isValidDate(String inDate, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (java.text.ParseException pe) {
			return false;
		}
		return true;
	}
}
