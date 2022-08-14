

/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.business;

import java.util.ResourceBundle;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.DatesUtil;
import com.enerallies.vem.util.HttpRestClient;
import com.enerallies.vem.util.ResourceBundleObject;
import com.enerallies.vem.weather.beans.WundergroundResponse;




/**
 * File Name : WundergroundBusiness 
 * WundergroundBusiness Class is used call the WeatherUnderground API to get weather details
 *
 * @author (Y Chenna Reddy � CTE).
 * 
 * Contact (Umang)
 * 
 * @version     1.0
 * @date        29-07-2016
 *
 * MOD HISTORY
 * 
 * DATE				USER             		COMMENTS
 * 
 * 29-07-2016		Y Chenna Reddy			File Created
 * 22-11-2016		Rajashekharaiah M 		Added getForecastData and httpGetRequest methods
 *
 */
@Component
public class WundergroundBusiness {
	
	// Getting logger	
	final static Logger logger = Logger.getLogger(WundergroundBusiness.class);
	
	/**
	 * Method Name: processWundergroundAPI() 
	 * This method is used to call the WeatherUnderground API to get  
	 * the weather information based on the country and location.
	 *  
	 * @throws Exception
	 */
	public WundergroundResponse processWundergroundAPI(String zipcodeOrCity) throws Exception {
		
		// Instantiate HttpRestClient
		HttpRestClient http = new HttpRestClient();
		
		// Call buildRequest method to get the URL
		String url = buildRequest(zipcodeOrCity);
		logger.info("URL: "+ url);
		
		// Call sendGet method of HttpRestClient
		String resonse = http.sendGet(url);
		logger.info("Response: "+ resonse);
		
		// Parsing Response JSON String to JSON Object
		JsonNode responseJson = new ObjectMapper().readValue(resonse, JsonNode.class);
		
		// Parsing JSON Object to Java Object
		WundergroundResponse wundergroundResponse = new ObjectMapper().readValue(responseJson.toString(),WundergroundResponse.class);
		
		return wundergroundResponse;

	}

	/**
	 * Method name: buildRequest()
	 * This method is used to build the URL.
	 *  
	 * @param zipcode
	 * @return urlString
	 * @throws Exception
	 */
	private String buildRequest(String zipcodeOrCity) throws Exception {
		ResourceBundle resourceBundle = ResourceBundleObject.getInstance("Wungerground");
		StringBuilder urlBuilder = new StringBuilder();

		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url1"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url2.key"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url3"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url5.country"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url4"));
		urlBuilder.append(zipcodeOrCity);
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url7"));

		logger.info(urlBuilder.toString());
		return urlBuilder.toString();

	}
	
	public String getForeCastData(String zipcodeOrCity) throws Exception{
		
		ResourceBundle resourceBundle = ResourceBundleObject.getInstance("Wungerground");
		StringBuilder urlBuilder = new StringBuilder();

		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url1"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url2.key"));
		urlBuilder.append(resourceBundle.getString("wunderground.request.forecast.url"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url5.country"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url4"));
		urlBuilder.append(zipcodeOrCity);
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url7"));

		logger.info("URL to fetch forecast data :"+urlBuilder.toString());
		
		String url = urlBuilder.toString();
		
		
		return httpGetRequest(url);
	}
	
	private String httpGetRequest(String url) throws Exception{
		// Instantiate HttpRestClient
		HttpRestClient http = new HttpRestClient();
		
		// Call sendGet method of HttpRestClient
		String response = http.sendGet(url);
		logger.info("Response: "+ response);
		
		return response;
	}
	
	public String getHourlyForecastData(String zipcodeOrCity) throws Exception{
		
		ResourceBundle resourceBundle = ResourceBundleObject.getInstance("Wungerground");
		StringBuilder urlBuilder = new StringBuilder();

		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url1"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url2.key"));
		urlBuilder.append(resourceBundle.getString("wunderground.request.hourlyforecast.url"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url5.country"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url4"));
		urlBuilder.append(zipcodeOrCity);
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url7"));

		logger.info("URL to fetch forecast data :"+urlBuilder.toString());
		
		String url = urlBuilder.toString();
		
		
		return httpGetRequest(url);
	}

	
	public String getHourlyHistoryData(String zipcodeOrCity) throws Exception{
		
		ResourceBundle resourceBundle = ResourceBundleObject.getInstance("Wungerground");
		StringBuilder urlBuilder = new StringBuilder();
	
		DateTime dt = new DateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Los_Angeles")));
		
		StringBuilder historyDate = new StringBuilder();
		String month= ""+dt.getMonthOfYear();
		String day = ""+dt.getDayOfMonth();
		
		if(dt.getMonthOfYear()<10)
			month = "0"+month;

		if(dt.getDayOfMonth()<10)
			day = "0"+day;

		historyDate.append(dt.getYear());
		historyDate.append(month);
		historyDate.append(day);
		
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url1"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url2.key"));
		urlBuilder.append(resourceBundle.getString("wunderground.request.hourlyhistory24.url"));
		urlBuilder.append(historyDate+"/q/");
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url5.country"));
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url4"));
		urlBuilder.append(zipcodeOrCity);
		urlBuilder.append(resourceBundle.getString("wunderground.ruquest.url7"));

		logger.info("URL to fetch history  data for the day or 24 hours :"+urlBuilder.toString());
		
		String url = urlBuilder.toString();
		
		
		return httpGetRequest(url);
	}

public static void main(String[] args) {
	DateTime dt = new DateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Los_Angeles")));
System.out.println(dt);
System.out.println(dt.getYear()+" "+dt.getMonthOfYear()+" "+dt.getDayOfMonth());
	StringBuilder historyDate = new StringBuilder();
	String month= ""+dt.getMonthOfYear();
	String day = ""+dt.getDayOfMonth();
	
	if(dt.getMonthOfYear()<10)
		month = "0"+month;

	if(dt.getDayOfMonth()<10)
		day = "0"+day;

	historyDate.append(dt.getYear());
	historyDate.append(month);
	historyDate.append(day);
	
	System.out.println(historyDate);

}

}
