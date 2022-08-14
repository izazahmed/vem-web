/**
 * 
 */
package com.enerallies.vem.listeners.xcspec;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


import com.enerallies.vem.util.ConfigurationUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


/**
 * File Name : RestClient 
 * 
 * RestClient: is to make HTTP REST calls to xcspec service 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        06-09-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	06-09-2016		Rajashekharaiah Muniswamy		File Created
 * 02	06-09-2016		Rajashekharaiah Muniswamy		Added loginToGetauthStringEnc Method
 * 03	06-09-2016		Rajashekharaiah Muniswamy		Added getDeviceLiveData Method
 * 04	09-09-2016		Rajashekharaiah Muniswamy		Added createThermostat Method
 * 05 	15-09-2016		Rajashekharaiah Muniswamy		Added deleteDevice method
 * 06	14-10-2016		Rajashekharaiah Muniswamy		Added getTempUnits method
 * 07   14-10-2016		Rajashekharaiah Muniswamy		Added getKeyBLockout method
 * 08 	14-10-2016 		Rajashekharaiah Muniswamy 		Added getETInterval method
 * 09	18-10-2016		Rajashekharaiah Muniswamy		Added setTHoldXCSPEC method to set the thermostat temperature hold
 * 10	26-10-2016		Rajashekharaiah Muniswamy		Added setTStatData method to set thermostat data
 * 11 	27-10-2016		Rajashekharaiah Muniswamy		Added getTStataData	method to get thermostat data
 * 12	03-11-2016		Rajashekharaiah Muniswamy		Added setSchedule method
 * 13	04-11-2016		Rajashekharaiah Muniswamy		Added getSchedule method
 * 14	04-11-2016		Rajashekharaiah Muniswamy		Added getScheduleCH method
 */

public class RestClient {

	// Getting logger
	private static final Logger logger = Logger.getLogger(RestClient.class);

	//Declare constant content type
	public static final String ACCEPT_TYPE = "application/json";

	//Declare constant Authorization
	public static final String AUTHORIZATION = "Authorization";

	/*
	 * 
	 * Authorization token and it is valid only for 1 hour
	 * 
	 * */
	public static String authStringEnc;


	/** Authorization token for user login -XCSPEC service*/
	public static String authToken = ConfigurationUtils.getConfig("XCSPEC_LOGINAPI_ACCEESS_TOKEN_"+ConfigurationUtils.getConfig("build.env").toUpperCase());



	/**
	 * @param authToken
	 * @return String
	 */
	public String loginToGetauthStringEnc(){
		/*
		 * 
		 * XSPEC server URL to user login and get Authorization token
		 * */
		String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_LOGIN_API");


		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("Auth token to login API " + authToken);
		logger.debug("URL: " + url);

		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.type(ACCEPT_TYPE)
				.header(AUTHORIZATION, authToken)
				.post(ClientResponse.class);
		String output = resp.getEntity(String.class);
		logger.debug("Response code got as:" + resp.getStatus()+ " and response data"+output);

		if(resp.getStatus() != 200){
			return null;
		}else{

			JSONObject json = new JSONObject(output);

			JSONObject data = (JSONObject) json.get("data");

			authStringEnc = data.get("token").toString();
			return data.get("token").toString();
		}

	}



	/**
	 * @param deviceId
	 * @return
	 */
	public String getDeviceLiveData(String deviceId) {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();				
		/*
		 * 
		 * XSPEC server URL to get the live data from Pro1 device
		 * */
		String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_TLIVE_DATA_API");
		url = url.replace("$deviceId", deviceId);
		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("Auth token to get Live data: " + authStringEnc);
		logger.debug("URL: " + url);


		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.header("Content-Type", ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.get(ClientResponse.class);
		String output = resp.getEntity(String.class);
		logger.info("XCSPEC server response code while getting live data for device id :"+deviceId+" and respose code"+resp.getStatus()+ " and response data"+output);
		return output;
	}


	public String setTemperatureTXSPEC(String deviceId,String target) {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();		
		/*
		 * 
		 * XSPEC server URL to set the temperature value
		 * */
		String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_TEMP_API");
		url = url.replace("$deviceId", deviceId);

		/*
		 * Get the client
		 * */	   
		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("Auth token for set temperature: " + authStringEnc);
		logger.debug("URL: " + url);

		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.type(ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.post(ClientResponse.class, target);
		String output = resp.getEntity(String.class);
		logger.info("Response code got for set temperature:" + resp.getStatus()+ " and response data"+output);
		return output;
	}

	public String createThermostat(String requestBody) {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();		
		/*
		 * 
		 * XSPEC server URL to create Thermostat or register Thermostat
		 * */
		String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_REG_THERM_API");


		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("auth token, auth string for create thermostat in XCSPEC: " + authStringEnc);
		logger.debug("URL: " + url);

		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.type(ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.post(ClientResponse.class, requestBody);
		String output = resp.getEntity(String.class);
		logger.info("Response code got for create thermostat:" + resp.getStatus()+ " and response data"+output);

		if(resp.getStatus()==201){
			String locationUrl = resp.getMetadata().getFirst("location");
			logger.info("Location url found "+locationUrl);
			String xcspecDevId = locationUrl.substring(locationUrl.lastIndexOf("/")+1);
			logger.info("XCSPEC device id found "+xcspecDevId);
			JSONObject createdObj = new JSONObject(output);
			createdObj.put("xcspecDevId", xcspecDevId);
			
			output = createdObj.toString();
		}
		return output;
	}


	public String getThermostats() {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();		
		/*
		 * 
		 * XSPEC server URL to get the list of Thermostats for a user
		 * */
		String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_LIST_THERM_API");

		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("auth token, auth string for get list of thermostats: " + authStringEnc);
		logger.debug("URL: " + url);

		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.header("Content-Type", ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.get(ClientResponse.class);
		String output = resp.getEntity(String.class);
		logger.info("XCSPEC server response code while getting list of thermostats and respose code"+resp.getStatus()+ " and response data"+output);
		return output;
	}


	/**
	 * @param args
	 */
	public String deleteDevice(String deviceId) {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();				
		/*
		 * 
		 * XSPEC server URL to delete device from xcspec deregister
		 * */
		String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_DEREG_API");
		url = url+deviceId;

		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);

		logger.debug("URL to delete device from xcspec"+url);
		logger.debug("auth token, auth string for delete device: " + authStringEnc);
		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.header("Content-Type", ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.delete(ClientResponse.class);
		String output = resp.getEntity(String.class);
		logger.debug("response for delete device from xcspec : deviceId :"+deviceId +" and response :"+ output);
		return output;

	}

	/**
	 * @param deviceId
	 * @return
	 */
	public String getTempUnits(String deviceId) {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();				
		/*
		 * 
		 * XSPEC server URL to get the temperature units from Pro1 device
		 * */
		String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_GET_TUNITS_API");
		url = url.replace("$deviceId", deviceId);

		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("Auth Token to get Temp Units: " + authStringEnc);
		logger.debug("URL: " + url);

		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.header("Content-Type", ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.get(ClientResponse.class);
		String output = resp.getEntity(String.class);

		logger.info("XCSPEC server response while getting temperature units data for device id :"+deviceId+" and respose code"+resp.getStatus()+ " and response data"+output);

		return output;

	}

	/**
	 * @param deviceId
	 * @return
	 */
	public String getKeyBLockout(String deviceId) {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();				
		/*
		 * 
		 * XSPEC server URL to get the keyboard lockout status from Pro1 device
		 * */
		String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_GET_LOCKOUT_API");
		url = url.replace("$deviceId", deviceId);

		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("Auth Token to get keyboard lockout: " + authStringEnc);
		logger.debug("URL: " + url);

		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.header("Content-Type", ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.get(ClientResponse.class);
		String output = resp.getEntity(String.class);

		logger.info("XCSPEC server respnse while getting keyboard lockout data for device id :"+deviceId+" and respose code"+resp.getStatus()+ " and response data"+output);

		return output;
	}

	/**
	 * @param deviceId
	 * @return
	 */
	public String getETInterval(String deviceId) {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();				
		/*
		 * 
		 * XSPEC server URL to get the engaged transaction interval data from Pro1 device
		 * */
		String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_GET_ETINTERVAL_API");
		url = url.replace("$deviceId", deviceId);

		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("Auth Token to get engaged transaction interval data : " + authStringEnc);
		logger.debug("URL: " + url);

		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.header("Content-Type", ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.get(ClientResponse.class);
		String output = resp.getEntity(String.class);

		logger.info("XCSPEC server response while getting engaged transaction interval for device id :"+deviceId+" and respose code"+resp.getStatus()+ " and response data"+output);

			return output;
	}

	/**
	 * @param url
	 * @param deviceId
	 * @param target
	 * @return
	 */
	public String setTStatData(String url, String deviceId,String target) {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();		

		url = url.replace("$deviceId", deviceId);

		/*
		 * Get the client
		 * */	   
		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("Auth token for set data: " + authStringEnc);
		logger.debug("URL: " + url + "and data"+target);
		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.type(ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.post(ClientResponse.class, target);
		String output = resp.getEntity(String.class);
		logger.info("Response code got for set data:" + resp.getStatus()+ " and response data"+output);
		return output;
	}

	/**
	 * @param deviceId
	 * @param url
	 * @return
	 */
	public String getTStatData(String deviceId, String url) {

		/**Get the latest authorization token for the user**/
		//authStringEnc = loginToGetauthStringEnc();				
		/*
		 * 
		 * XSPEC server URL to get the device data from Pro1 device
		 * */
		url = url.replace("$deviceId", deviceId);

		Client restClient = Client.create();
		WebResource webResource = restClient.resource(url);


		logger.debug("Auth Token to get device data : " + authStringEnc);
		logger.debug("URL: " + url);

		ClientResponse resp = webResource.accept(ACCEPT_TYPE)
				.header("Content-Type", ACCEPT_TYPE)
				.header(AUTHORIZATION, authStringEnc)
				.get(ClientResponse.class);
		String output = resp.getEntity(String.class);

		logger.info("XCSPEC server response while getting engaged transaction interval for device id :"+deviceId+" and respose code"+resp.getStatus()+ " and response data"+output);

			return output;
	}

	/**
	 * @param deviceId
	 * @param type
	 * @return
	 */
	public String getSchedule(String deviceId, String type){
		JSONObject targetGetSC = new JSONObject();
		String output;

		//prepare json body to get schedule on parameters
		targetGetSC.put("schedule_config", "7");
		targetGetSC.put("program", type.toUpperCase());
		targetGetSC.put("periods_per_day", "4");

		/**Call to xcspec api to get schedule*/
		String schedule = setTStatData(ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_GET_SCDL_API"), deviceId, targetGetSC.toString());

		JSONObject scheduleObj1 = new JSONObject(schedule);
		if("Must login or provide credentials.".equals((String)scheduleObj1.get("message"))){
			loginToGetauthStringEnc();
			schedule = setTStatData(ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_GET_SCDL_API"), deviceId, targetGetSC.toString());
		}

		JSONObject respFXCSPECObj = new JSONObject(schedule);

		/*check if response is ok*/
		if((Integer)respFXCSPECObj.get("code") == 200){
			output=respFXCSPECObj.get("data").toString();
		}else{
			output=null;
		}

		return output;
	}

	/**
	 * @param deviceId
	 * @param setScheduleCool
	 * @param setScheduleHeat
	 * @return
	 */
	public String setSchedule(String deviceId, String setScheduleCool, String setScheduleHeat){
		String schedule = null;
		//check if device id is null
		if(deviceId!=null){
			//check if schedule cool is null
			if(setScheduleCool!=null){
				/**Call to xcspec api to set schedule*/
				schedule = setTStatData(ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_SCDL_API"), deviceId, setScheduleCool);

				JSONObject scheduleObj1 = new JSONObject(schedule);
				if("Must login or provide credentials.".equals((String)scheduleObj1.get("message"))){
					loginToGetauthStringEnc();
					schedule = setTStatData(ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_SCDL_API"), deviceId, setScheduleCool);
				}
			}
			//check if schedule heat is null
			if(setScheduleHeat!=null){
				/**Call to xcspec api to set schedule*/
				schedule = setTStatData(ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_SCDL_API"), deviceId, setScheduleHeat);
				JSONObject scheduleObj1 = new JSONObject(schedule);
				if("Must login or provide credentials.".equals((String)scheduleObj1.get("message"))){
					loginToGetauthStringEnc();
					schedule = setTStatData(ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_SCDL_API"), deviceId, setScheduleHeat);
				}
				
			}
		}
		return schedule;
	}

	/**
	 * @param deviceId
	 * @return
	 */
	public String getScheduleCH(String deviceId){
		/**Call to xcspec api to get schedule cool*/
		String scheduleCool = getSchedule(deviceId, "C");
		JSONObject scheduleCoolObj = new JSONObject();
		if(scheduleCool!=null){
			scheduleCoolObj = new JSONObject(scheduleCool);
		}

		/**Call to xcspec api to get schedule heat*/
		String scheduleHeat = getSchedule(deviceId, "H");
		JSONObject scheduleHeatObj = new JSONObject();
		if(scheduleHeat!=null){
			scheduleHeatObj = new JSONObject(scheduleHeat);
		}

		JSONArray arraySchedule = new JSONArray();
		arraySchedule.put(scheduleCoolObj);
		arraySchedule.put(scheduleHeatObj);
		return arraySchedule.toString();
	}

}
