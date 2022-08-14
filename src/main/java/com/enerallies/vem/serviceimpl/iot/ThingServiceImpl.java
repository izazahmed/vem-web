/**
 * 
 */
package com.enerallies.vem.serviceimpl.iot;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.iot.model.DeleteThingRequest;
import com.amazonaws.services.iot.model.DeleteThingResult;
import com.amazonaws.services.iot.model.ResourceAlreadyExistsException;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.beans.iot.ConfigChange;
import com.enerallies.vem.beans.iot.DevForecastResponse;
import com.enerallies.vem.beans.iot.DeviceConfigInsert;
import com.enerallies.vem.beans.iot.DeviceMoreDetails;
import com.enerallies.vem.beans.iot.DeviceStatusRequest;
import com.enerallies.vem.beans.iot.DisconnectDeviceRequest;
import com.enerallies.vem.beans.iot.GroupInfo;
import com.enerallies.vem.beans.iot.OccupyHours;
import com.enerallies.vem.beans.iot.ScheduleData;
import com.enerallies.vem.beans.iot.SetClockRequest;
import com.enerallies.vem.beans.iot.SetTStatDataRequest;
import com.enerallies.vem.beans.iot.SetTemperatureRequest;
import com.enerallies.vem.beans.iot.SiteDevice;
import com.enerallies.vem.beans.iot.TSTATPreference;
import com.enerallies.vem.beans.iot.ThermostatUnit;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.iot.ThingState;
import com.enerallies.vem.beans.iot.ThingUpdateRequest;
import com.enerallies.vem.beans.iot.Things;
import com.enerallies.vem.beans.iot.UpdateHeatPumpFieldReq;
import com.enerallies.vem.beans.iot.Weather;
import com.enerallies.vem.beans.weather.ForecastResponse;
import com.enerallies.vem.business.WundergroundBusiness;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.dao.weather.WeatherDao;
import com.enerallies.vem.daoimpl.schedule.ScheduleDaoImpl;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.listeners.iot.awsiot.InitApp;
import com.enerallies.vem.listeners.xcspec.RestClient;
import com.enerallies.vem.service.iot.ThingService;
import com.enerallies.vem.service.weather.WeatherService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.DatesUtil;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.iot.XCSPECDataParser;
import com.enerallies.vem.weather.beans.WundergroundResponse;


/**
 * File Name : ThingServiceImpl 
 * 
 * ThingServiceImpl: is implementation of ThingService methods 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        31-08-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	31-08-2016		Rajashekharaiah Muniswamy		File Created
 * 02	31-08-2016		Rajashekharaiah Muniswamy		Added registerThing Method
 * 03   06-09-2016		Rajashekharaiah Muniswamy		Modified init method to add timers
 * 04   07-09-2016		Rajashekharaiah Muniswamy		Added getDeviceShadowState method
 * 05  	08-09-2016		Rajashekharaiah Muniswamy		Added getThingList method
 * 06   19-09-2016		Rajashekharaiah Muniswamy		Added changeDeviceStatus method
 * 07	20-09-2016		Rajashekharaiah Muniswamy		Added disconnectDevice method
 * 08   26-09-2016		Rajashekharaiah Muniswamy       Added updateDevice method
 * 09   27-09-2016		Rajashekharaiah Muniswamy		Added  listThermostatUnit method
 * 10   28-09-2016      Rajashekharaiah Muniswamy		Added getTstatPref method
 * 11	30-09-2016		Rajashekharaiah Muniswamy		Modified location name to location type (Integer)
 * 12	04-10-2016		Rajashekharaiah Muniswamy		Modified update device method
 * 13	13-10-2016		Rajashekharaiah Muniswamy		Modified getDevice method added zip code and weather info
 * 14	14-10-2016		Rajashekharaiah Muniswamy		Modified getDevice method to add tempUnits, keyBLockout, eTInterval
 * 15	17-10-2016		Rajashekharaiah Muniswamy		Added setTemp method to set the thermostat temperature
 * 16	18-10-2016		Rajashekharaiah Muniswamy		Added set thermostat data method to set the thermostat data like hold, lockout, fan, calibration
 * 17	19-10-2016		Rajashekharaiah Muniswamy		Added setClock method to set the thermostat clock
 * 17	21-10-2016		Rajashekharaiah Muniswamy		Modified getDevice and getThingListByCustomer added current UTC time
 * 18	02-11-2016		Rajashekharaiah Muniswamy 		Modified update device method with new user story reviews
 * 19	03-11-2016		Rajashekharaiah Muniswamy 		Modified delete device method with new user story reviews
 * 20	04-11-2016 		Rajashekharaiah Muniswamy		Added insertActivityLog method to insert device configuration audit(active log)
 * 21	15-11-2016		Rajashekharaiah Muniswamy		Modified getThingList by id sort by customer id, site id, schedule id, and group id  
 */

@Service(value="thingService")
public class ThingServiceImpl implements ThingService, InitializingBean{


	/** Auto wiring instance of IoTDao  */
	@Autowired
	private IoTDao ioTDao;

	/** Auto wiring instance of AuditDao  */
	@Autowired
	private AuditDAO auditDao;


	/** Auto wiring instance of WundergroundBuisness  */
	@Autowired
	private WundergroundBusiness wundergroundBusiness;

	@Autowired
	WeatherDao weatherDao;

	@Autowired
	ScheduleDaoImpl scheduleDaoImpl;
	
	@Autowired
	XCSPECDataParser xcspecDataParser;
	
	@Autowired
	ThingServiceIoTDataHelper ioTDataHelper;
	
	@Autowired
	ThingServiceCRUDHelper crudHelper;

	@Autowired
	ThingServiceUtilHelper utilHelper;
	
	@Autowired
	ThingServiceDataUpdater dataUpdater;
	
	@Autowired
	WeatherService weatherService;

	/** Scheduler to get the device from DB  */
	Timer queryDBTimer;

	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(ThingServiceImpl.class);

	/**Rest client class reference to call XCSPEC specific REST API's*/
	RestClient restClient = new RestClient();

	//@Scheduled(cron="0 0/10 * * * ?") //cron="0 0/10 * * * ?"
	private void deviceListTask(){

		try {
			/** Get thing list from DB */
			List<Thing> thingList = ioTDao.getThingList();
			if(thingList!=null){
				if(InitApp.deviceIdList!=null){
					InitApp.deviceIdList.clear();
				}

				InitApp.deviceIdList = thingList;

			}

		} catch (SQLException e) {
			logger.error("Error while fetching device list :"+e);
		}catch (Exception e) {
			logger.error("Error while starting the timer of fetching device list :"+e);
		}

		/**scheduler for subscribe and Start querying data from xcspec and publish to aws iot */
		InitApp.initialize();

	}

	//@Scheduled(cron="0 0 0/1 * * ?") //cron="0 0 0/1 * * ?"
	private void loginScheduler(){
		RestClient.authStringEnc = restClient.loginToGetauthStringEnc();
	}

	/** Register thing that includes registration with XCSPEC and AWS IOT */
	@Override
	public Response registerThing(Thing thing) throws ResourceAlreadyExistsException {
		logger.info("[BEGIN] [ThingServiceImpl] [registerThing]");

		/**Creating reference of Response object*/
		Response resp = new Response();

		try{
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(thing);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				resp.setCode(validatorBean.getMessage());
			}else{

				if((thing.getModel()).contains("Pro1")){

					if(thing.getAwsCompatible() == 0){
						resp = crudHelper.registerDevPro1NonAWSComp(thing, resp);
					}else{
						
						if(thing.getRegisterType()==1){
							int result = ioTDataHelper.validateDevRegAWSIOT(thing.getMacId());
							if(result>0){
								resp = crudHelper.registerDevPro1AWSComp(thing, resp);
							}else{
								resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
								resp.setCode(ErrorCodes.DEVICE_REG_VALIDATE_AWS_IOT_REG_FAILED);
							}
							
						}else{
							resp = crudHelper.registerDevPro1AWSComp(thing, resp);
						}
					}

				}else{
					resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					resp.setCode(ErrorCodes.DEVICE_MODEL_NOT_SUPPORTED);
				}
			}

		} catch (Exception e) {
			resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			resp.setCode(ErrorCodes.GENERAL_APP_ERROR);
			logger.error("[ERROR] [ThingServiceImpl] [registerThing]"+e);
		}

		if(resp.getStatus().equals("SUCCESS")){
			ThingResponse sresp = (ThingResponse) resp.getData();
			if(sresp.getXcspecDeviceId()!=null){
				applyScheduleForRegdDevice(sresp);
			}
		}

		logger.info("[END] [ThingServiceImpl] [registerThing]");
		return resp;
	}


	private void applyScheduleForRegdDevice(ThingResponse resp){
		logger.info("Entered to create new thread to apply schedule for registered device by site id");
		ArrayList deviceListToApplySch = new ArrayList();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					List<ScheduleData> scheduleDataList = weatherDao.getScheduleList(resp.getSiteId(), 2);
					logger.info("Schedule list found and its size: "+scheduleDataList.size());
					if(scheduleDataList!=null && scheduleDataList.size()>0){
						ScheduleData data = scheduleDataList.get(0);

						int scheduleId = data.getId();

						logger.info("Schedule id found to apply schedule for registered device. schedulId:"+scheduleId+" and deviceId:"+resp.getDeviceId());

						HashMap devicemapApplySch= new HashMap();
						devicemapApplySch.put("xcspec_device_id",resp.getXcspecDeviceId());
						devicemapApplySch.put("device_id",resp.getDeviceId());
						devicemapApplySch.put("schedule_id",scheduleId);
						deviceListToApplySch.add(devicemapApplySch);
						logger.info("Calling apply schedule method for registered device. schedulId:"+scheduleId+" and deviceId:"+resp.getDeviceId());
						//scheduleDaoImpl.applyDeviceJSON(null,deviceListToApplySch,0);
					}
				} catch (VEMAppException e) {
					logger.error("Error found while getting schedule list on site id");
				}
			}
		}).start();
	}


	/** To get the device id of Registered device with mac id */
	private String getDeviceId(String macId) {
		RestClient restClient = new RestClient();

		logger.info("[ThingServiceImpl] [getDeviceId] Going to get thermostat device id for mac id "+ macId);
		String responseJSONString = restClient.getThermostats();

		JSONObject liveDataObj = new JSONObject(responseJSONString);
		if("Must login or provide credentials.".equals((String)liveDataObj.get("message"))){
			restClient.loginToGetauthStringEnc();
			responseJSONString = restClient.getThermostats();
		}

		JSONObject pObj = new JSONObject(responseJSONString);
		String deviceId = null;
		if((Integer)pObj.get("code") == 200){

			JSONArray thermostats = pObj.getJSONArray("data");
			for (int i = 0; i < thermostats.length(); i++) {
				JSONObject thermostat = thermostats.getJSONObject(i);
				if(macId.equalsIgnoreCase(thermostat.getString("mac_address"))){
					deviceId = thermostat.getString("id");

					logger.info("[ThingServiceImpl] [getDeviceId] Found the device id for mac:"+thermostat.getString("mac_address")+" and id :"+thermostat.getString("id"));
				}
			}
		}

		return deviceId;
	}

	@Override
	public Response disconnectDevice(DisconnectDeviceRequest disconnectDevice, Integer userId) throws VEMAppException {
		logger.info("[BEGIN] [updateDeviceStatus] [ThingServiceImpl SERVICE LAYER]");

		Response response = new Response();

		try {

			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(disconnectDevice);

			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{

				String responseStringJSON = restClient.deleteDevice(disconnectDevice.getXcspecDeviceId());

				JSONObject liveDataObj = new JSONObject(responseStringJSON);
				if("Must login or provide credentials.".equals((String)liveDataObj.get("message"))){
					restClient.loginToGetauthStringEnc();
					responseStringJSON = restClient.deleteDevice(disconnectDevice.getXcspecDeviceId());
				}

				JSONObject responseJSON = new JSONObject(responseStringJSON);


				if(responseJSON.getBoolean("success")){

					DeleteThingRequest deleteThingRequest = new DeleteThingRequest();
					deleteThingRequest.setThingName(disconnectDevice.getMacId());

					DeleteThingResult result = ioTDataHelper.iotClient.deleteThing(deleteThingRequest);

					if(result!=null){

						//Catches when all the server or bean level validations are true.
						int status = ioTDao.disconnectDevice(disconnectDevice, userId);

						/*if status is 1 or greater means the device disconnect updation  request is
						 *  success
						 *  else fail.
						 */
						if(status >= 1){
							response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
							response.setCode(ErrorCodes.SUCCESS_UPDATING_DEVICE_DISCONNECT);
						}else{
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.ERROR_UPDATE_DEVICE_DISCONNECT);
							response.setData(CommonConstants.ERROR_OCCURRED+":Device disconnect update failed in db.");
						}
					}else{
						response.setCode(ErrorCodes.ERROR_AWS_IOT_DELETE_DEVICE);
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					}

				}else{
					if("Must login or provide credentials.".equals(responseJSON.getString("message"))){
						response.setCode(ErrorCodes.ERROR_XCSPEC_UNAUTHORISED);
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					}else if("Thermostat not found.".equals(responseJSON.getString("message"))){
						response.setCode(ErrorCodes.ERROR_XCSPEC_DELETE_DEVICE_NOT_FOUND);
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						logger.error("Error found while deleting device from xcspec");
					}
				}
			}

		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_UPDATE_DISCONNECT_DEVICE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DISCONNECT_DEVICE_FAILED, logger, e);
		}

		logger.info("[BEGIN] [updateDeviceStatus] [ThingServiceImpl SERVICE LAYER]");

		return response;
	}
	
	@Override
	public Response updateDeviceStatus(DeviceStatusRequest deviceStatus, Integer userId) throws VEMAppException {
		logger.info("[BEGIN] [updateDeviceStatus] [ThingServiceImpl SERVICE LAYER]");

		Response response = new Response();

		try {

			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(deviceStatus);

			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{

				//Catches when all the server or bean level validations are true.
				int status = ioTDao.updateDeviceStatus(deviceStatus, userId);

				/* if status is 1 or greater means the device status updation  request is
				 *  success
				 *  else fail.
				 */
				if(status >= 1){
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.SUCCESS_UPDATING_DEVICE_STATUS_DEVICES);
					response.setData(status);
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED);
					response.setData(CommonConstants.ERROR_OCCURRED+":Device status update failed in db.");
				}
			}

		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED, logger, e);
		}

		logger.info("[BEGIN] [updateDeviceStatus] [ThingServiceImpl SERVICE LAYER]");

		return response;
	}


	
	/** To get list of things or devices */
	@Override
	public Things getThingList() throws RuntimeException {
		Things things;
		List<ThingResponse> thingResponseList;
		try {
			/** Get thing list from DB */
			thingResponseList = ioTDao.getThingListWithSiteName();

			for (ThingResponse thing : thingResponseList) {

				/** To get the device shadow for each device */
				String jsonStringState;
				if(thing.getRegisterType()==1){
					jsonStringState = ioTDataHelper.getDeviceShadowState(thing.getMacId());
				}else{
					jsonStringState = null;
				}

				ThingState obj;
				if(jsonStringState!=null){
					JSONObject objPar = new JSONObject(jsonStringState);
					JSONObject stateObj = (JSONObject)objPar.get("state");
					JSONObject reportedObj = (JSONObject)stateObj.get("reported");

					if(reportedObj.has("data")){
						JSONObject dataObj = (JSONObject)reportedObj.get("data");

						logger.info("dataObj state String json : "+dataObj);

						ObjectMapper mapper = new ObjectMapper();


						String s = dataObj.toString();

						s = s.replaceAll("\\\\","");
						obj= mapper.readValue(s, ThingState.class);
					}else{

						obj = null;
					}

				}else{
					//String testData = "{\"op_state\":\"OFF\",\"heat_set\":\"63.0\",\"cool_set\":\"75.0\",\"zone_temp\":\"74.8\",\"fan_state\":\"IDLE\",\"temp_hold\":\"DISABLE\",\"tstat_mode\":\"HEAT\",\"tstat_msg\":null,\"co2_1\":\"0\",\"co2_2\":\"0\",\"co2_3\":\"0\",\"co2_4\":\"0\",\"co2_1_RSSI\":\"0\",\"co2_2_RSSI\":\"0\",\"co2_3_RSSI\":\"0\",\"co2_4_RSSI\":\"0\",\"button_pressed\":\"FALSE\",\"datetime\":\"09/08/2016 10:42:55 +0000\",\"relay_state\":{\"relay1\":\"ON\",\"relay2\":\"OFF\",\"relay3\":\"OFF\",\"relay4\":\"OFF\",\"relay5\":\"OFF\",\"relay6\":\"OFF\",\"relay7\":\"OFF\"},\"tstat_clock\":{\"current_time\":\"16:11\",\"current_day\":\"th\"}}";
					//ObjectMapper mapper = new ObjectMapper();
					//obj= mapper.readValue(testData, ThingState.class);
					obj=null;
				}


				thing.setThingState(obj);

			}

			things = new Things();
			things.setThings(thingResponseList);
		} catch (Exception e) {
			things = null;

			logger.error("Exception while Iterating for the thing state "+e.getMessage());
		}

		return things;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Not using this method");

	}


	@Override
	public Things getThingList(int value, String sortBy, GetUserResponse userInfo) throws VEMAppException {
		Things things;
		List<ThingResponse> thingResponseList;
		try {

			if(sortBy.equals("customerId")){
				/** Get thing list from DB by customerId */
				thingResponseList = ioTDao.getThingList(value, 1, userInfo);
			}else if(sortBy.equals("siteId")){
				/** Get thing list from DB by siteId*/
				thingResponseList = ioTDao.getThingList(value, 2, userInfo);
			}else if(sortBy.equals("scheduleId")){
				/** Get thing list from DB by scheduleId*/
				thingResponseList = ioTDao.getThingList(value, 3, userInfo);
			}else if(sortBy.equals("groupId")){
				/** Get thing list from DB by groupId*/
				thingResponseList = ioTDao.getThingList(value, 4, userInfo);
			}else if(sortBy.equals("userId")){
				/** Get thing list from DB by userId*/
				thingResponseList = ioTDao.getThingList(value, 5, userInfo);
			}else{
				thingResponseList = new ArrayList<>();
			}
			for (ThingResponse thing : thingResponseList) {

				/** To get the device shadow for each device */
				String jsonStringState;
				if(thing.getRegisterType()==1){
					jsonStringState = ioTDataHelper.getDeviceShadowState(thing.getMacId());
				}else{
					jsonStringState = null;
				}

				Object obj;
				if(jsonStringState!=null){
					JSONParser parser = new JSONParser();
					org.json.simple.JSONObject objPar = (org.json.simple.JSONObject)parser.parse(jsonStringState);
					org.json.simple.JSONObject stateObj = (org.json.simple.JSONObject)objPar.get("state");
					org.json.simple.JSONObject reportedObj = (org.json.simple.JSONObject)stateObj.get("reported");
					
					if(thing.getAwsCompatible()==0){
						if(reportedObj.containsKey("data")){
							org.json.simple.JSONObject dataObj = (org.json.simple.JSONObject)reportedObj.get("data");
	
							logger.info("dataObj state String json : "+dataObj);
	
							/*ObjectMapper mapper = new ObjectMapper();
							String s = dataObj.toString();
							s = s.replaceAll("\\\\","");
							obj= (ThingState)mapper.readValue(s, ThingState.class);*/
							obj = dataObj;
						}else{
	
							obj = null;
						}
					}else{
						//org.json.simple.JSONObject convertedObj = xcspecDataParser.parse(reportedObj);
						String respStr = scheduleDaoImpl.getDeviceJsonResponse(stateObj.toJSONString(), thing.getMacId());
						
						logger.info("===$$$ converted JSON $$$====: "+respStr);
						
						if(respStr!=null){
							org.json.simple.JSONObject convertedObj = (org.json.simple.JSONObject)parser.parse(respStr);
							if(convertedObj.containsKey("data"))
								obj = convertedObj.get("data");
							else
								obj = null;
						}else{
							obj = null;
						}
					}
				}else{
					//String testData = "{\"op_state\":\"OFF\",\"heat_set\":\"63.0\",\"cool_set\":\"75.0\",\"zone_temp\":\"74.8\",\"fan_state\":\"IDLE\",\"temp_hold\":\"DISABLE\",\"tstat_mode\":\"HEAT\",\"tstat_msg\":null,\"co2_1\":\"0\",\"co2_2\":\"0\",\"co2_3\":\"0\",\"co2_4\":\"0\",\"co2_1_RSSI\":\"0\",\"co2_2_RSSI\":\"0\",\"co2_3_RSSI\":\"0\",\"co2_4_RSSI\":\"0\",\"button_pressed\":\"FALSE\",\"datetime\":\"09/08/2016 10:42:55 +0000\",\"relay_state\":{\"relay1\":\"ON\",\"relay2\":\"OFF\",\"relay3\":\"OFF\",\"relay4\":\"OFF\",\"relay5\":\"OFF\",\"relay6\":\"OFF\",\"relay7\":\"OFF\"},\"tstat_clock\":{\"current_time\":\"16:11\",\"current_day\":\"th\"}}";
					//ObjectMapper mapper = new ObjectMapper();
					//obj= mapper.readValue(testData, ThingState.class);
					obj=null;
				}


				thing.setThingState(obj);
				thing.setCurrentUTCDateTime(DatesUtil.getCurrentUTCDateTime());

				DeviceMoreDetails devMoreDetails = ioTDao.getDeviceMoreDetails(thing.getDeviceId(), userInfo.getUserId(), userInfo.getIsSuper());

				if(devMoreDetails!=null){
					thing.setScheduleName(devMoreDetails.getDevScheduleName());
					thing.setScheduleStatus(devMoreDetails.getDevScheduleStatus());
					thing.setActivityCount(devMoreDetails.getDevActivityCount());
					thing.setAlertCount(devMoreDetails.getDevAlertCount());
					thing.setScheduleId(devMoreDetails.getDevScheduleId());
					thing.setDevCommFailConfigTime(devMoreDetails.getDevCommFailConfigTime());
				}

				List<GroupInfo> group = ioTDao.getGroupInfo(thing.getSiteId(), userInfo.getUserId(), userInfo.getIsSuper());

				if (group!=null && group.size()>0) {
					StringBuilder groupName = new StringBuilder();
					StringBuilder groupIds = new StringBuilder();
					for (int i = 0; i < group.size(); i++) {

						if(i==group.size()-1){
							groupName.append(group.get(i).getGroupName());
							groupIds.append(group.get(i).getGroupId());
						}else{
							groupName.append(group.get(i).getGroupName());
							groupName.append(", ");
							groupIds.append(group.get(i).getGroupId());
							groupIds.append(", ");

						}
					}
					thing.setGroupId(groupIds.toString());
					thing.setGroupName(groupName.toString());
				}

				thing = utilHelper.processSetAt(thing);
			}

			things = new Things();
			things.setThings(thingResponseList);
		} catch (Exception e) {
			things = null;

			logger.error("Exception while Iterating for the thing state ",e);
		}

		return things;
	}



	@Override
	public Response updateDevice(ThingUpdateRequest thing) {
		logger.info("[BEGIN] [ThingServiceImpl] [updateDevice]");

		/**Creating reference of Response object*/
		Response resp = new Response();

		try{

			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(thing);

			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				resp.setCode(validatorBean.getMessage());
			}else{
				if((thing.getModel()).contains("Pro1")){

					if(thing.getAwsCompatible() == 0){
						resp = crudHelper.updateDevPro1NonAWSComp(thing, resp);
					}else{
						
						if(thing.getRegisterType()==1){
							int result = ioTDataHelper.validateDevRegAWSIOT(thing.getMacId());
							if(result>0){
								resp = crudHelper.updateDevPro1AWSComp(thing, resp);
							}else{
								resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
								resp.setCode(ErrorCodes.DEVICE_REG_VALIDATE_AWS_IOT_REG_FAILED);
							}
						}else{
							resp = crudHelper.updateDevPro1AWSComp(thing, resp);
						}
						
						
					}
				}else{
					resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					resp.setCode(ErrorCodes.DEVICE_MODEL_NOT_SUPPORTED);
				}
			}//end of else validation

		} catch (Exception e) {
			resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			resp.setCode(ErrorCodes.GENERAL_APP_ERROR);
			logger.error("[ERROR] [ThingServiceImpl] [updateDevice]"+e);
		}


		logger.info("[END] [ThingServiceImpl] [updateDevice]");
		return resp;
	}




	@Override
	public Response listSite(String sortBy, int value, int userId, int isSuper) throws VEMAppException{
		logger.info("[BEGIN] [listSite] [ThingServiceImpl SERVICE LAYER]");

		Response response = new Response();
		//Used to store list of sites.
		List<SiteDevice> sites;

		try {

			//Calling the DAO layer listSite() method.
			// if sort on customer id  - 1
			if(sortBy.equalsIgnoreCase("customerId")){
				sites = ioTDao.listSite(1, value,  userId, isSuper);

			}else if(sortBy.equalsIgnoreCase("groupId")){
				sites = ioTDao.listSite(2, value,  userId, isSuper);

			}else{
				sites = null;
			}

			/* if sites is not null means the get sites list request is
			 *  success
			 *  else fail.
			 */
			logger.info("Sites list object "+sites);
			if(sites!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.LIST_SITE_SUCCESS);
				response.setData(sites);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.LIST_SITE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}

		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.LIST_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_SITE_FAILED, logger, e);
		}

		logger.info("[END] [listSite] [ThingServiceImpl SERVICE LAYER]");

		return response;
	}


	@Override
	public Response getDevice(int deviceId, GetUserResponse userInfo) throws VEMAppException {
		logger.info("[BEGIN] [getDevice] [ThingServiceImpl SERVICE LAYER]");

		Response response = new Response();
		//Used to store thingResponse.
		ThingResponse thingResponse;

		try {

			//Calling the DAO layer listSite() method.
			thingResponse = ioTDao.getThingInfo(1,deviceId, null);

			/* if thingResponse is not null means the get device request is
			 *  success
			 *  else fail.
			 */

			if(thingResponse!=null){

				/** To get the device shadow for each device */
				String jsonStringState;
				if(thingResponse.getRegisterType()==1){
					/*if(thingResponse.getXcspecDeviceId()!=null)
						InitApp.queryAndPublishData(thingResponse.getXcspecDeviceId(), thingResponse.getMacId());*/
					
					jsonStringState = ioTDataHelper.getDeviceShadowState(thingResponse.getMacId());
				}else{
					jsonStringState = null;
				}

				weatherService.fetchAndStoreHistoryData(thingResponse.getDeviceId(), thingResponse.getSiteZipcode());
				//ThingState obj;
				Object obj;
				if(jsonStringState!=null){
					/*JSONObject objPar = new JSONObject(jsonStringState);
					JSONObject stateObj = (JSONObject)objPar.get("state");
					JSONObject reportedObj = (JSONObject)stateObj.get("reported");*/
					JSONParser parser = new JSONParser();
					org.json.simple.JSONObject objPar = (org.json.simple.JSONObject)parser.parse(jsonStringState);
					org.json.simple.JSONObject stateObj = (org.json.simple.JSONObject)objPar.get("state");
					org.json.simple.JSONObject reportedObj = (org.json.simple.JSONObject)stateObj.get("reported");

					if(thingResponse.getAwsCompatible()==0){					
						if(reportedObj.containsKey("data")){
							//JSONObject dataObj = (JSONObject)reportedObj.get("data");
							org.json.simple.JSONObject dataObj = (org.json.simple.JSONObject)reportedObj.get("data");
							logger.info("dataObj state String json : "+dataObj);
	
							/*ObjectMapper mapper = new ObjectMapper();
							String s = dataObj.toString();
							s = s.replaceAll("\\\\","");
							obj= mapper.readValue(s, ThingState.class);*/
							obj = dataObj;
						}else{
	
							obj = null;
						}
					}else{
						//org.json.simple.JSONObject convertedObj = xcspecDataParser.parse(reportedObj);
						String respStr = scheduleDaoImpl.getDeviceJsonResponse(stateObj.toJSONString(), thingResponse.getMacId());
						org.json.simple.JSONObject convertedObj = (org.json.simple.JSONObject)parser.parse(respStr);
						if(convertedObj.containsKey("data"))
							obj = convertedObj.get("data");
						else
							obj = null;
					}
				}else{
					//String testData = "{\"op_state\":\"OFF\",\"heat_set\":\"63.0\",\"cool_set\":\"75.0\",\"zone_temp\":\"74.8\",\"fan_state\":\"IDLE\",\"temp_hold\":\"DISABLE\",\"tstat_mode\":\"HEAT\",\"tstat_msg\":null,\"co2_1\":\"0\",\"co2_2\":\"0\",\"co2_3\":\"0\",\"co2_4\":\"0\",\"co2_1_RSSI\":\"0\",\"co2_2_RSSI\":\"0\",\"co2_3_RSSI\":\"0\",\"co2_4_RSSI\":\"0\",\"button_pressed\":\"FALSE\",\"datetime\":\"09/08/2016 10:42:55 +0000\",\"relay_state\":{\"relay1\":\"ON\",\"relay2\":\"OFF\",\"relay3\":\"OFF\",\"relay4\":\"OFF\",\"relay5\":\"OFF\",\"relay6\":\"OFF\",\"relay7\":\"OFF\"},\"tstat_clock\":{\"current_time\":\"16:11\",\"current_day\":\"th\"}}";
					//ObjectMapper mapper = new ObjectMapper();
					//obj= mapper.readValue(testData, ThingState.class);
					obj=null;
				}

				thingResponse.setThingState(obj);

				DeviceMoreDetails devMoreDetails = ioTDao.getDeviceMoreDetails(deviceId, userInfo.getUserId(), userInfo.getIsSuper());

				if(devMoreDetails!=null){
					thingResponse.setScheduleName(devMoreDetails.getDevScheduleName());
					thingResponse.setScheduleStatus(devMoreDetails.getDevScheduleStatus());
					thingResponse.setActivityCount(devMoreDetails.getDevActivityCount());
					thingResponse.setAlertCount(devMoreDetails.getDevAlertCount());
					thingResponse.setScheduleId(devMoreDetails.getDevScheduleId());
					thingResponse.setDevCommFailConfigTime(devMoreDetails.getDevCommFailConfigTime());
				}

				List<GroupInfo> group = ioTDao.getGroupInfo(thingResponse.getSiteId(), userInfo.getUserId(), userInfo.getIsSuper());

				if (group!=null && group.size()>0) {
					StringBuilder groupName = new StringBuilder();
					StringBuilder groupIds = new StringBuilder();
					for (int i = 0; i < group.size(); i++) {

						if(i==group.size()-1){
							groupName.append(group.get(i).getGroupName());
							groupIds.append(group.get(i).getGroupId());
						}else{
							groupName.append(group.get(i).getGroupName());
							groupName.append(", ");
							groupIds.append(group.get(i).getGroupId());
							groupIds.append(", ");

						}
					}
					thingResponse.setGroupId(groupIds.toString());
					thingResponse.setGroupName(groupName.toString());
				}

				thingResponse.setCurrentUTCDateTime(DatesUtil.getCurrentUTCDateTime());

				WundergroundResponse wResp = wundergroundBusiness.processWundergroundAPI(thingResponse.getSiteZipcode());
				Weather weather = null;
				if(wResp.getCurrent_observation() != null){

					weather = new Weather();

					weather.setTemp_f(wResp.getCurrent_observation().getTemp_f());
					weather.setIcon_url(wResp.getCurrent_observation().getIcon_url());
					weather.setWeather(wResp.getCurrent_observation().getWeather());
					weather.setFeelslike_f(wResp.getCurrent_observation().getFeelslike_f());
					weather.setRelative_humidity(wResp.getCurrent_observation().getRelative_humidity());

					String forecastResp = wundergroundBusiness.getForeCastData(thingResponse.getSiteZipcode());

					if(forecastResp!=null){
						try{
							JSONObject forecastObj = new JSONObject(forecastResp);

							JSONObject nForecastObj = (JSONObject)forecastObj.get("forecast");

							JSONObject simpleForecastObj = (JSONObject)nForecastObj.get("simpleforecast");

							JSONArray forecastdayArray = (JSONArray)simpleForecastObj.get("forecastday");

							JSONObject today = (JSONObject)forecastdayArray.get(0);

							JSONObject todayMax = (JSONObject)today.get("high");

							int maxTempVal = todayMax.getInt("fahrenheit");

							JSONObject todayMin = (JSONObject)today.get("low");

							int minTempVal = todayMin.getInt("fahrenheit");

							weather.setHigh_temp(""+maxTempVal);
							weather.setLow_temp(""+minTempVal);
						}catch(Exception e){
							logger.error("error found while parsing 3 day forecast data to get min and max temperature value"+e);
						}

					}
				}

				thingResponse.setWeather(weather);

				if(thingResponse.getXcspecDeviceId()!=null)
					thingResponse = utilHelper.getThermostatFields(thingResponse);

				List<ConfigChange> configChnages = utilHelper.processConfigChanges(thingResponse.getDeviceId());

				thingResponse.setConfigChanges(configChnages);


				List<OccupyHours> ohs = ioTDao.getSiteOccupyHours(thingResponse.getSiteId());

				if(ohs!=null && ohs.size()>0){

					if(ohs.get(0).getOpenTime().contains("AM") || ohs.get(0).getOpenTime().contains("PM") || ohs.get(0).getOpenTime().contains("pm") || ohs.get(0).getOpenTime().contains("am")){
					}else{
						for (int i = 0; i < ohs.size(); i++) {
							SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
							SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
							Date _24HourDtopen = _24HourSDF.parse(ohs.get(i).getOpenTime());
							Date _24HourDtclose = _24HourSDF.parse(ohs.get(i).getCloseTime());
							ohs.get(i).setOpenTime(_12HourSDF.format(_24HourDtopen));
							ohs.get(i).setCloseTime(_12HourSDF.format(_24HourDtclose));
						}
					}
					thingResponse.setOccHours(ohs);
				}


				thingResponse = utilHelper.processSetAt(thingResponse);


				if(thingResponse.getScheduleId()!=0){
					org.json.simple.JSONObject schedule = weatherDao.readSchedule(deviceId);
					thingResponse.setDbSchedule(schedule);
				}

				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.SUCCES_GET_DEVICE);
				response.setData(thingResponse);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_GET_DEVICE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}

		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_GET_DEVICE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_GET_DEVICE_FAILED, logger, e);
		}

		logger.info("[END] [getDevice] [ThingServiceImpl SERVICE LAYER]");

		return response;
	}



	/* (non-Javadoc)
	 * @see com.enerallies.vem.service.iot.ThingService#deleteDevice(int)
	 */
	@Override
	public Response deleteDevice(int deviceId, int userId) throws VEMAppException {
		logger.info("[BEGIN] [deleteDevice] [ThingServiceImpl SERVICE LAYER]");
		/**Declaring Respnse object*/
		Response response = new Response();

		try {
			ThingResponse thingResponse = ioTDao.getThingInfo(1,deviceId, null);

			if(thingResponse!=null){
				if(thingResponse.getModel().contains("Pro1")){
					if(thingResponse.getAwsCompatible()==0){
					 response = crudHelper.deleteDevPro1NonAWSComp(deviceId, userId, response, thingResponse);
					}else{
						response = crudHelper.deleteDevPro1AWSComp(deviceId, userId, response);
						JSONObject desiredData=null;
						InitApp.publishMessage("$aws/things/"+thingResponse.getMacId()+"/shadow/update", "{\"state\":{\"desired\":"+desiredData+"}}");
					}
						
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.DEVICE_MODEL_NOT_SUPPORTED);
				}
			}else{
				response.setCode(ErrorCodes.ERROR_DEVICE_NOT_FOUND);
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
		} catch (SQLException e1) {
			logger.error("Error found while checking device info in DB "+e1.getMessage());
			response.setCode(ErrorCodes.ERROR_SQL_FETCH_DEVICE_MACID);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());

		}

		logger.info("[BEGIN] [deleteDevice] [ThingServiceImpl SERVICE LAYER]");

		return response;
	}



	@Override
	public Response listThermostatUnit(int siteId,GetUserResponse userInfo) throws VEMAppException {
		List<ThingResponse> thingResponseList;
		logger.info("[BEGIN] [listThermostatUnit] [ThingServiceImpl SERVICE LAYER]");

		Response response = new Response();
		//Used to store list of thermostat units.
		List<ThermostatUnit> tstatUnits;

		try {

			//Calling the DAO layer listThermostatUnit() method.
			tstatUnits = ioTDao.listThermostatUnit(siteId);

			thingResponseList = ioTDao.getThingList(siteId, 2, userInfo);

			if (thingResponseList != null) {
				for (int i = 0; i < thingResponseList.size(); i++) {
					for (int j = 0; j < tstatUnits.size(); j++) {
						if(thingResponseList.get(i).getUnit().equalsIgnoreCase(tstatUnits.get(j).getTstatUnit())){
							tstatUnits.remove(j);
						}
					}
				}
			}

			/* if tstatUnits is not null means the get listThermostatUnit list request is
			 *  success
			 *  else fail.
			 */
			logger.info("Sites list object "+tstatUnits);
			if(tstatUnits!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.SUCCESS_LIST_TSTATUNIT);
				response.setData(tstatUnits);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_LIST_THERMOSTAT_UNITS_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}

		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_LIST_THERMOSTAT_UNITS_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_LIST_THERMOSTAT_UNITS_FAILED, logger, e);
		}

		logger.info("[END] [listThermostatUnit] [ThingServiceImpl SERVICE LAYER]");

		return response;
	}


	@Override
	public Response getTstatPref(int siteId) throws VEMAppException {

		/**Instantiate Response object*/
		Response response = new Response();

		//Used to store TSTATPreference.
		TSTATPreference tstatPreference;


		tstatPreference = ioTDao.getTstatPref(siteId);

		/**
		 * if tstatPreference is not null means getTstatPref request is success
		 * */

		if(tstatPreference.getFanPref()==2 || tstatPreference.getFanPref()==0){
			tstatPreference.setFanPref(0);
		}else{
			tstatPreference.setFanPref(1);
		}


		if (tstatPreference.getLock() == 0) {
			tstatPreference.setLock(0);
		} else if (tstatPreference.getLock() == 1) {
			tstatPreference.setLock(1);
		} else if (tstatPreference.getLock() == 2) {
			tstatPreference.setLock(2);
		} else {
			tstatPreference.setLock(1);
		}
		
		response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
		response.setCode(ErrorCodes.SUCCESS_DEVICE_TSTATPREF);
		response.setData(tstatPreference);

		return response;
	}


	@Override
	public Response setTemp(int deviceId, SetTemperatureRequest setTemperatureRequest, int userId) throws VEMAppException {

		/*Instantiate Response object*/
		Response response = new Response();
		ThingResponse thingResponse = null;
		String xcspecDevId = null;

		try{
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(setTemperatureRequest);

			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				thingResponse = ioTDao.getThingInfo(1,deviceId, null);
				xcspecDevId = thingResponse.getXcspecDeviceId();
				if(thingResponse.getAwsCompatible()==1 && thingResponse.getModel().contains("Pro1")){
					response = dataUpdater.setTemp(response, thingResponse, setTemperatureRequest, userId);
				}else{
					JSONObject targetTemp = new JSONObject();
					targetTemp.put("mode", setTemperatureRequest.getMode());
					targetTemp.put("temp", setTemperatureRequest.getTemp());

					/*client request to set temperature*/
					String respFXCSPEC = restClient.setTemperatureTXSPEC(xcspecDevId, targetTemp.toString());

					JSONObject respFXCSPECObj1 = new JSONObject(respFXCSPEC);
					if("Must login or provide credentials.".equals((String)respFXCSPECObj1.get("message"))){
						restClient.loginToGetauthStringEnc();
						respFXCSPEC = restClient.setTemperatureTXSPEC(xcspecDevId, targetTemp.toString());
					}


					JSONObject respFXCSPECObj = new JSONObject(respFXCSPEC);

					/*check if response is ok*/
					if((Integer)respFXCSPECObj.get("code") == 200){
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.SUCCESS_DEVICE_SET_TEMP);
						response.setData(setTemperatureRequest);

						if("COOL".equalsIgnoreCase(setTemperatureRequest.getMode())){
							insertActivityLog(xcspecDevId, userId, "Updated", "Tstat Cool SP has been set to "+setTemperatureRequest.getTemp());
							insertDeviceConfig(xcspecDevId, userId, 1, setTemperatureRequest.getTemp());
						}else{
							insertActivityLog(xcspecDevId, userId, "Updated", "Tstat Heat SP has been set to "+setTemperatureRequest.getTemp());
							insertDeviceConfig(xcspecDevId, userId, 2, setTemperatureRequest.getTemp());
						}
					}else{
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(respFXCSPECObj.getString("message"));
					}
				
				}

}
		} catch (Exception e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			logger.error("[ERROR] [ThingServiceImpl] [setTemp]"+e);
		}		
		return response;
	}


	@Override
	public Response setTStatData(int deviceId, SetTStatDataRequest setTStatDataRequest, int userId) throws VEMAppException {
		/*Instantiate Response object*/
		Response response = new Response();
		ThingResponse thingResponse = null;
		String xcspecDevId = null;

		
		String 	activityAction		= "";
		String 	activityDesc		= "";
		int 	configAction		= 0;
		String  configValue			= "";
		try{
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(setTStatDataRequest);

			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				thingResponse = ioTDao.getThingInfo(1,deviceId, null);
				xcspecDevId = thingResponse.getXcspecDeviceId();
				if(thingResponse.getAwsCompatible()==1 && thingResponse.getModel().contains("Pro1")){
					response = dataUpdater.setTstatData(response, thingResponse, setTStatDataRequest, userId);
				}else{
					String url;
					JSONObject target = new JSONObject();
					/*client request to set hold*/
					String respFXCSPEC;
					activityAction = "Updated";
					configValue = setTStatDataRequest.getData();

					/*Check for the data set type
					 * 1: 	set hold
					 * 2:	set fan mode
					 * 3: 	set calibration
					 * 4:	set temp display unit
					 * 5:	set keyboard lockout
					 * 6: 	set message area
					 * 7: 	set engaged transaction time
					 * 8:   set reset thermostat
					 * 9:   set hvac operating mode
					 * 
					 * */
					if("1".equals(setTStatDataRequest.getType())){
						url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_HOLD_API");
						target.put("data", setTStatDataRequest.getData());
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());
						activityDesc = "Tstat hold has been set to "+configValue;
						configAction = 3;
					}else if("2".equals(setTStatDataRequest.getType())){
						url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_FAN_API");
						String var = setTStatDataRequest.getData();
						if(var.equalsIgnoreCase("IDLE")){
							var = "AUTO";
						}
						target.put("data", var);
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());
						activityDesc = "Tstat fan mode has been set to "+var;
						configAction = 5;

					}else if("3".equals(setTStatDataRequest.getType())){
						url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_CAL_API");
						target.put("data", setTStatDataRequest.getData());
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());

						activityDesc = "Tstat calibration has been set to "+configValue;
						//configAction = 6;

					}else if("4".equals(setTStatDataRequest.getType())){
						url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_UNIT_API");
						target.put("data", setTStatDataRequest.getData());
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());

						activityDesc = "Tstat display unit has been set to "+configValue;
						//configAction = 11;
					}else if("5".equals(setTStatDataRequest.getType())){
						url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_LOCKOUT_API");
						target.put("data", setTStatDataRequest.getData());
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());

						String s;
						if(configValue.equalsIgnoreCase("PARTIAL1"))
							s="partial";
						else
							s="unlock";
						activityDesc = "Tstat keypad lockout has been set to "+s;
						//configAction = 7;

					}else if("6".equals(setTStatDataRequest.getType())){
						url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_MESSAGE_API");
						target.put("tstat_msg", setTStatDataRequest.getData());
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());

						activityDesc = "Tstat message has been set to "+configValue;
						configAction = 8;

					}else if("8".equals(setTStatDataRequest.getType())){
						url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_RESTART_API");
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, "");
						JSONObject respFXCSPECObj = new JSONObject(respFXCSPEC);
						activityDesc = "Tstat has been set to "+configValue;
						//insertDeviceConfig(xcspecDevId, userId, 13, "reset");	
					}else if("9".equals(setTStatDataRequest.getType())){
						url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_HVACMODE_API");
						target.put("data", setTStatDataRequest.getData());
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());

						String hvacModeLog;
						
						if(configValue.equalsIgnoreCase("HEAT"))
							hvacModeLog="HEAT";
						else if(configValue.equalsIgnoreCase("COOL"))
							hvacModeLog="COOL";
						else if(configValue.equalsIgnoreCase("AUTO"))
							hvacModeLog="AUTO";
						else
							hvacModeLog="OFF";
						
						activityDesc = "Tstat HVAC mode has been set to "+hvacModeLog;
						configAction = 4;

					}else{
						url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_ETT_API");
						target.put("data", setTStatDataRequest.getData());
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());

						activityDesc = "Tstat Engaged Transactional Interval has been set to "+configValue;
						//configAction = 12;
					}

					JSONObject respFXCSPECObj1 = new JSONObject(respFXCSPEC);
					if("Must login or provide credentials.".equals((String)respFXCSPECObj1.get("message"))){
						restClient.loginToGetauthStringEnc();
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());
					}

					JSONObject respFXCSPECObj = new JSONObject(respFXCSPEC);

					/*check if response is ok*/
					if((Integer)respFXCSPECObj.get("code") == 200){
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.SUCCESS_DEVICE_SET_TSTAT_DATA);
						response.setData(setTStatDataRequest);

						insertActivityLog(xcspecDevId, userId, activityAction, activityDesc);

						if(configAction!=0)
							insertDeviceConfig(xcspecDevId, userId, configAction, configValue);
					}else{
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(respFXCSPECObj.getString("message"));
					}
					
				}
				
			}
		} catch (Exception e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			logger.error("[ERROR] [ThingServiceImpl] [setTstatData]"+e);
		}
		return response;
	}


	@Override
	public Response setClock(int deviceId, SetClockRequest setClockRequest, int userId) throws VEMAppException {
		/*Instantiate Response object*/
		Response response = new Response();

		ThingResponse thingResponse = null;
		String xcspecDevId = null;
		try{
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(setClockRequest);

			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				thingResponse = ioTDao.getThingInfo(1,deviceId, null);
				xcspecDevId = thingResponse.getXcspecDeviceId();
				if(thingResponse.getAwsCompatible()==1 && thingResponse.getModel().contains("Pro1")){
					response  = dataUpdater.setClock(response, thingResponse, setClockRequest, userId);
				}else{
					JSONObject data = new JSONObject();
					JSONObject targetClock = new JSONObject();
					targetClock.put("current_time", setClockRequest.getCurrentTime());
					targetClock.put("current_day", setClockRequest.getCurrentDay());

					data.put("data", targetClock);

					String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_CLOCK_API");
					/*client request to set clock*/
					String respFXCSPEC = restClient.setTStatData(url, xcspecDevId, data.toString());

					JSONObject respFXCSPECObj1 = new JSONObject(respFXCSPEC);
					if("Must login or provide credentials.".equals((String)respFXCSPECObj1.get("message"))){
						restClient.loginToGetauthStringEnc();
						respFXCSPEC = restClient.setTStatData(url, xcspecDevId, data.toString());
					}

					JSONObject respFXCSPECObj = new JSONObject(respFXCSPEC);

					/*check if response is ok*/
					if((Integer)respFXCSPECObj.get("code") == 200){
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.SUCCESS_DEVICE_SET_CLOCK);
						response.setData(setClockRequest);

						String currentDay = utilHelper.getCurrentDaySet(setClockRequest.getCurrentDay());
						
						currentDay = currentDay.substring(0, 1).toUpperCase() + currentDay.substring(1);
						String formattedTime = "";
						try
						{
							SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
							Date d1 = sdf1.parse(setClockRequest.getCurrentTime());
							SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
							formattedTime = sdf.format(d1);
						}
						catch(Exception e)
						{
							logger.error("Unable to covert date format to log into activity log due to : ",e);
						}
						
						insertActivityLog(xcspecDevId, userId, "Updated", "Tstat clock has been set to "+currentDay+" "+formattedTime);

						//insertDeviceConfig(xcspecDevId, userId, 9, setClockRequest.getCurrentDay());
						//insertDeviceConfig(xcspecDevId, userId, 10, setClockRequest.getCurrentTime());
					}else{
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(respFXCSPECObj.getString("message"));
					}
					
				}
					
					
			}
		} catch (Exception e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			logger.error("[ERROR] [ThingServiceImpl] [setClock]"+e);
		}

		return response;
	}

	public void insertIntoActivityLog(String xcspecId, int userId, String action, String descr,String serviceId)
	{
		try {
			//fetch device info by xcspec device id
			ThingResponse devInfo = ioTDao.getThingInfo(3, 0, xcspecId);

			// Activity log 
			AuditRequest auditRequest = new AuditRequest();
			auditRequest.setUserId(userId);
			auditRequest.setUserAction(action);
			auditRequest.setLocation(""+devInfo.getLocation());
			auditRequest.setServiceId(serviceId); // for user module
			auditRequest.setDescription(descr);
			auditRequest.setServiceSpecificId(devInfo.getDeviceId());
			auditRequest.setOutFlag("");
			auditRequest.setOutErrMsg("");
			auditDao.insertAuditLog(auditRequest);

		} catch (SQLException e) {
			logger.error("Error found while auditing"+e);
		}
	
	}

	public void insertActivityLog(String xcspecId, int userId, String action, String descr){

		try {
			//fetch device info by xcspec device id
			ThingResponse devInfo = ioTDao.getThingInfo(3, 0, xcspecId);

			// Activity log 
			AuditRequest auditRequest = new AuditRequest();
			auditRequest.setUserId(userId);
			auditRequest.setUserAction(action);
			auditRequest.setLocation(""+devInfo.getLocation());
			auditRequest.setServiceId("4"); // for user module
			auditRequest.setDescription(descr);
			auditRequest.setServiceSpecificId(devInfo.getDeviceId());
			auditRequest.setOutFlag("");
			auditRequest.setOutErrMsg("");
			auditDao.insertAuditLog(auditRequest);

		} catch (SQLException e) {
			logger.error("Error found while auditing"+e);
		}
	}

	public void insertDeviceConfig(String xcspecId, int userId, int action, String value){

		try {
			//fetch device info by xcspec device id
			ThingResponse devInfo = ioTDao.getThingInfo(3, 0, xcspecId);

			// Insert device configuration 
			DeviceConfigInsert deviceConfig = new DeviceConfigInsert();
			deviceConfig.setAction(action);
			deviceConfig.setValue(value);
			deviceConfig.setCreatedBy(userId);
			deviceConfig.setUpdatedFlag(0);
			deviceConfig.setDeviceId(devInfo.getDeviceId());

			ioTDao.insertDeviceConfig(deviceConfig);

		} catch (SQLException e) {
			logger.error("Error found while inserting device configuration", e);
		} catch (VEMAppException e) {
			logger.error("Error found while inserting device configuration" ,e);
		}
	}

	@Override
	public Response listDevForecast(int siteId, GetUserResponse userInfo) throws VEMAppException {
		Response resp = new Response();
		Things things;
		List<ThingResponse> thingResponseList;
		List<DevForecastResponse> devForecastRespList = new ArrayList<>();
		/** Get thing list from DB by siteId*/
		thingResponseList = ioTDao.getThingList(siteId, 2, userInfo);

		if(thingResponseList!=null && thingResponseList.size()>0){

			for (int i = 0; i < thingResponseList.size(); i++) {
				DevForecastResponse fore = new DevForecastResponse();

				fore.setDeviceId(thingResponseList.get(i).getDeviceId());
				fore.setDeviceName(thingResponseList.get(i).getName());

				List<ForecastResponse> forecastList;

				//Calling the DAO layer getForeList() method.
				forecastList = weatherDao.getForecastList(1, thingResponseList.get(i).getDeviceId(), 0);
				/* if forecastList is not null means the get forecastList request is
				 *  success
				 *  else fail.
				 */
				if(forecastList!=null && forecastList.size()!=0){
					fore.setForecastFlag(1);
				}
				devForecastRespList.add(fore);	

			}
			resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			resp.setCode(ErrorCodes.SUCCESS_DEVICE_LIST_FORECAST_DATA);
			resp.setData(devForecastRespList);

		}

		return resp;
	}

	@Override
	public Response updateHeatPumpFields(UpdateHeatPumpFieldReq heatPump, Integer userId) throws VEMAppException {
		logger.info("[BEGIN] [updateHeatPumpFields] [ThingServiceImpl SERVICE LAYER]");

		Response response = new Response();

		try {

			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(heatPump);

			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{

				//Catches when all the server or bean level validations are true.
				int status = ioTDao.updateHeatPumpFields(heatPump, userId);

				/* if status is 1 or greater means the device status updation  request is
				 *  success
				 *  else fail.
				 */
				if(status >= 1){
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.SUCCESS_UPDATING_DEVICE_STATUS_DEVICES);
					response.setData(status);
					
					
					ThingResponse thingResponse = ioTDao.getThingInfo(1,heatPump.getDeviceId(), null);
					
					String action = "Updated";
					String descr = "";
					
					if(heatPump.getHeatPumpUpdateType()==1){
						if(heatPump.getHeatPumpUpdateTypeValue()==0){
							descr = "Tstat HVAC–Heat pump unit has been set to OFF";
						}else{
							descr = "Tstat HVAC–Heat pump unit has been set to ON";
						}
					}else if(heatPump.getHeatPumpUpdateType()==2){
						descr = "Tstat stages of heat has been set to "+heatPump.getHeatPumpUpdateTypeValue();
					}else if(heatPump.getHeatPumpUpdateType()==3){
						descr = "Tstat stages of cool has been set to "+heatPump.getHeatPumpUpdateTypeValue();
						
					}else{
						if(heatPump.getHeatPumpUpdateTypeValue()==0){
							descr = "Tstat gas auxilary has been set to OFF";
						}else{
							descr = "Tstat gas auxilary has been set to ON";
						}
					}
						dataUpdater.insertActivityLog(thingResponse, userId, action, descr);
					
					
					
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED);
					response.setData(CommonConstants.ERROR_OCCURRED+":Device status update failed in db.");
				}
			}

		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED, logger, e);
		}

		logger.info("[BEGIN] [updateHeatPumpFields] [ThingServiceImpl SERVICE LAYER]");

		return response;
	}
	
}
