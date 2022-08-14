package com.enerallies.vem.serviceimpl.weather;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.beans.iot.ScheduleData;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.iot.UpdateForecastRequest;
import com.enerallies.vem.beans.iot.UpdateForecastRequestList;
import com.enerallies.vem.beans.weather.AddForecastRequest;
import com.enerallies.vem.beans.weather.AddForecastRequestList;
import com.enerallies.vem.beans.weather.ForecastData;
import com.enerallies.vem.beans.weather.ForecastResponse;
import com.enerallies.vem.beans.weather.ForecastTempResponse;
import com.enerallies.vem.business.WundergroundBusiness;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.dao.weather.WeatherDao;
import com.enerallies.vem.daoimpl.schedule.ScheduleDaoImpl;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.listeners.xcspec.RestClient;
import com.enerallies.vem.service.weather.WeatherService;
import com.enerallies.vem.serviceimpl.iot.ThingServiceIoTDataHelper;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;


/**
 * File Name : WeatherServiceImpl 
 * 
 * WeatherServiceImpl: is implementation of WeatherService methods 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        22-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	22-11-2016		Rajashekharaiah Muniswamy		File Created
 */

@Service(value="weatherService")
public class WeatherServiceImpl implements WeatherService{

	/** Getting logger*/	
	private static final Logger logger = Logger.getLogger(WeatherServiceImpl.class);
	
	@Autowired
	IoTDao iotDao;
	
	@Autowired
	WeatherDao weatherDao;
	
	@Autowired
	WundergroundBusiness wunder;
	@Autowired
	ScheduleDaoImpl scheduleDaoImpl;
	
	/** Auto wiring instance of AuditDao  */
	@Autowired
	private AuditDAO auditDao;
	
	private Map<Integer,Date> startTimeMap = new LinkedHashMap<>();
	
	@Autowired
	ThingServiceIoTDataHelper iotDataHelper;
	/**Rest client class reference to call XCSPEC specific REST API's*/
	RestClient restClient = new RestClient();
	
	//@Scheduled(cron="0 45 23 * * ?") //*/50 * * * * ? //0 0 12 * * ?
	private void forecastJob(){
		try {
			
			logger.info("Forecast job has been started");
			List<ThingResponse> thingResponse = iotDao.getThingListWithSiteName();
			
			logger.info("Found the device list in forecast job :"+thingResponse.size());
			ArrayList deviceList = new ArrayList();
			for (int i = 0; i < thingResponse.size(); i++) {
				
				if(thingResponse.get(i).getRegisterType()==1){
					String forecastResp = wunder.getForeCastData(thingResponse.get(i).getSiteZipcode());
					logger.info("Forecast :"+forecastResp);
					insertForecastData(forecastResp, thingResponse.get(i));
					
					String hourlyForecastResp = wunder.getHourlyForecastData(thingResponse.get(i).getSiteZipcode());
					logger.info("Hourly Forecast :"+hourlyForecastResp);
					parseAndInsertHourlyForecastData(hourlyForecastResp, thingResponse.get(i));
					
					if(thingResponse.get(i).getNightSchedule()==1){
						//download and schedule method
						HashMap devicemap= new HashMap();
						devicemap.put("xcspec_device_id",thingResponse.get(i).getXcspecDeviceId());
						devicemap.put("device_id",thingResponse.get(i).getDeviceId());
						deviceList.add(devicemap);
					}
				}
			}
			logger.info("deviceList************************ :"+deviceList.size());
			if(deviceList.size()>0){
				//scheduleDaoImpl.applyDeviceJSON(null,deviceList,0);
			}
			
		} catch (SQLException e) {
			logger.error("Error found while fetching device list to fetch forecast data");
		} catch (Exception e) {
			logger.error("Error found while fetching forecast data");
		}
	}
	
	public void parseAndInsertHourlyForecastData(String hourlyForecastResp, ThingResponse thingResponse) {
		
		try{
			JSONParser parser = new JSONParser();
			org.json.simple.JSONObject respObj = (org.json.simple.JSONObject)parser.parse(hourlyForecastResp);
			 			
			/*if(respObj.containsKey("hourly_forecast")){
				org.json.simple.JSONArray hourlyForecastArry = (org.json.simple.JSONArray)respObj.get("hourly_forecast");
				
				weatherDao.insertHourlyForecastData(hourlyForecastArry, thingResponse);
			}*/
			
			if(respObj.containsKey("history")){
				
				org.json.simple.JSONObject history = (org.json.simple.JSONObject)respObj.get("history");
				org.json.simple.JSONArray hourlyHistoryArry = (org.json.simple.JSONArray)history.get("observations");
				
				weatherDao.insertHourlyHistoryData(hourlyHistoryArry, thingResponse);
			}
			
		}catch (Exception e) {
			logger.error("Error found while parsing and inserting hourly forecast data into DB", e);
		}
		
	}

	public void insertForecastData(String forecast, ThingResponse thingResponse){
		
		ForecastData forecastData = new ForecastData();
		try{
		JSONObject forecastObj = new JSONObject(forecast);
		
		JSONObject nForecastObj = (JSONObject)forecastObj.get("forecast");
		
		JSONObject simpleForecastObj = (JSONObject)nForecastObj.get("simpleforecast");
		
		JSONArray forecastdayArray = (JSONArray)simpleForecastObj.get("forecastday");
		
		JSONObject today = (JSONObject)forecastdayArray.get(0);
		JSONObject tomorrow = (JSONObject)forecastdayArray.get(1);
		
		JSONObject todayMax = (JSONObject)today.get("high");
		JSONObject todayMin = (JSONObject)today.get("low");
		
		JSONObject tomorrowMax = (JSONObject)tomorrow.get("high");
		JSONObject tomorrowMin = (JSONObject)tomorrow.get("low");
		
		
		todayMax.getString("fahrenheit");
		
		forecastData.setDeviceId(thingResponse.getDeviceId());
		forecastData.setZipcode(thingResponse.getSiteZipcode());
		forecastData.setTodayMinTemp(todayMin.getString("fahrenheit"));
		forecastData.setTodayMaxTemp(todayMax.getString("fahrenheit"));	
		forecastData.setTomoMinTemp(tomorrowMin.getString("fahrenheit"));
		forecastData.setTomoMaxTemp(tomorrowMax.getString("fahrenheit"));
		
		weatherDao.insertForecastData(forecastData);
		
		}catch (Exception e) {
			logger.error("Error found while parsing and inserting forecast data into DB", e);
		}
	}

	@Override
	public Response addForecast(AddForecastRequestList forecastReq, int userId) throws VEMAppException {
		logger.info("[BEGIN] [WeatherServiceImpl] [addForecast]");
		/**sql store results*/
		int storeResult = 0;

		/**Creating reference of Response object*/
		Response resp = new Response();
		List<Integer> savedForecastIds = new ArrayList<>();

		/* Instantiating the bean validator and validating the request bean.*/
		ValidatorBean validatorBean = ConfigurationUtils.validateBeans(forecastReq);
		if(validatorBean.isNotValid()){
			//Catches when bean validations failed.
			resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			resp.setCode(validatorBean.getMessage());
		}else{
			List<AddForecastRequest> forecastList = forecastReq.getForecastConfig();
			if("add".equalsIgnoreCase(forecastList.get(0).getAddType())){

				for (int i = 0; i < forecastList.size(); i++) {
					
					forecastList.get(i).setCreatedBy(userId);
					storeResult = weatherDao.addForecast(forecastList.get(i));
					savedForecastIds.add(storeResult);
				}
				resp.setCode(ErrorCodes.SUCCESS_ADD_FORECAST_CONFIG);

			}else{
			//Used to store list of forecast data.
				for (int i = 0; i < forecastList.size(); i++) {
				List<ForecastResponse> forecastListToChk;
					//Calling the DAO layer getForeList() method.
				forecastListToChk = weatherDao.getForecastList(forecastList.get(i).getType(), forecastList.get(i).getTypeId(), 0);
					/* if forecastList is not null means the get forecastList request is
					 *  success
					 *  else fail.
					 */
					if(forecastListToChk!=null && forecastListToChk.size()>0){
						for (int j = 0; j < forecastListToChk.size(); j++) {
							weatherDao.deleteForecast(forecastListToChk.get(j).getForecastId(), userId);
							
						}
					}
					
				}
					
					for (int i = 0; i < forecastList.size(); i++) {
						
						forecastList.get(i).setCreatedBy(userId);
						storeResult = weatherDao.addForecast(forecastList.get(i));
						savedForecastIds.add(storeResult);
					}
					
					resp.setCode(ErrorCodes.SUCCESS_APPLY_FORECAST_CONFIG);
			}
			
			if(!savedForecastIds.isEmpty()){
				
				List<ForecastResponse> data = getStoredForecastInfo(savedForecastIds);
				
				insertForecastActivityLog(data.get(0).getTypeId(), userId, "Added", data.get(0).getForecastName()+" forecast schedule added ", data.get(0).getType());
				resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				resp.setData(data);
			}else{
				resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				resp.setCode(ErrorCodes.ERROR_ADD_FORECAST_CONFIG);
				resp.setData(storeResult);
			}
		}
		logger.info("[END] [WeatherServiceImpl] [addForecast]");
		return resp;
	}
	
	private List<ForecastResponse> getStoredForecastInfo(List<Integer> savedForecastIds) throws VEMAppException{
		List<ForecastResponse> forecastListResponse = new ArrayList<>();
		try {
		for (int i = 0; i < savedForecastIds.size(); i++) {
			//Calling the DAO layer getForeList() method.
			List<ForecastResponse> forecastListDB = weatherDao.getForecastList(0, savedForecastIds.get(i), 3);
			/* if forecastList is not null means the get forecastList request is
			 *  success
			 *  else fail.
			 */
			if(forecastListDB!=null){
				
				List<ForecastTempResponse> forecastTempList;
				
				forecastTempList = weatherDao.getForecastTempList(forecastListDB.get(0).getForecastId());
				forecastListDB.get(0).setForecastTempList(forecastTempList);

				forecastListResponse.add(forecastListDB.get(0));
			}
		}
		} catch (VEMAppException e) {
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_LIST_FORECAST_FAILED, logger, e);
		}
		
		return forecastListResponse;
	}

	@Override
	public Response getScheduleList(String sortBy, int value) throws VEMAppException {
		logger.info("[BEGIN] [getScheduleList] [WeatherServiceImpl SERVICE LAYER]");

		Response response = new Response();
		//Used to store list of schedule list.
		List<ScheduleData> scheduleDataList;

		try {

			//Calling the DAO layer getScheduleList() method.
			if(sortBy.equalsIgnoreCase("deviceId"))
				scheduleDataList = weatherDao.getScheduleList(value, 1);
			else if(sortBy.equalsIgnoreCase("siteId"))
				scheduleDataList = weatherDao.getScheduleList(value, 2);
			else
				scheduleDataList = weatherDao.getScheduleList(0, 3);
			/* if scheduleDataList is not null means the get scheduleDataList list request is
			 *  success
			 *  else fail.
			 */
			logger.info("Schedule list object "+scheduleDataList);
			if(scheduleDataList!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.SUCCESS_LISTING_SCHEDULE);
				response.setData(scheduleDataList);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_LIST_SCHEDULE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}

			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_LIST_SCHEDULE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_LIST_SCHEDULE_FAILED, logger, e);
		}

		logger.info("[END] [getScheduleList] [WeatherServiceImpl SERVICE LAYER]");

		return response;
	}

	@Override
	public Response getForecastList(int type, int typeId) throws VEMAppException {
		logger.info("[BEGIN] [getForecastList] [WeatherServiceImpl SERVICE LAYER]");

		Response response = new Response();
		//Used to store list of forecast data.
		List<ForecastResponse> forecastList;

		try {

			//Calling the DAO layer getForeList() method.
			forecastList = weatherDao.getForecastList(type, typeId, 0);
			/* if forecastList is not null means the get forecastList request is
			 *  success
			 *  else fail.
			 */
			if(forecastList!=null){
				
				for (int i = 0; i < forecastList.size(); i++) {
					List<ForecastTempResponse> forecastTempList = weatherDao.getForecastTempList(forecastList.get(i).getForecastId());
					
					forecastList.get(i).setForecastTempList(forecastTempList);
				}
				
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.SUCCESS_LISTING_FORECAST);
				response.setData(forecastList);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_LIST_FORECAST_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}

			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_LIST_FORECAST_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_LIST_FORECAST_FAILED, logger, e);
		}

		logger.info("[END] [getForecastList] [WeatherServiceImpl SERVICE LAYER]");

		return response;
	}

	@Override
	public Response readSchedule(int deviceId) throws VEMAppException {
		logger.info("[BEGIN] [readSchedule] [WeatherServiceImpl SERVICE LAYER]");

		Response response = new Response();
		
		ThingResponse thingResponse;

		org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
		org.json.simple.JSONObject schedule;
		try {

			//Calling the DAO layer readSchedule() method.
			schedule = weatherDao.readSchedule(deviceId);
			/* if forecastList is not null means the get readSchedule request is
			 *  success
			 *  else fail.
			 */
			obj.put("dbSchedule", schedule);

			org.json.simple.JSONObject devSchedule = new org.json.simple.JSONObject();

			//Calling the DAO layer listSite() method.
				thingResponse = iotDao.getThingInfo(1,deviceId, null);
				
			logger.info("===$$$ Getting the schedule for the device:"+thingResponse.getDeviceId()+" and mac id:"+thingResponse.getMacId()+" aws compatibility:"+thingResponse.getAwsCompatible());	
			if(thingResponse.getAwsCompatible()==1 && thingResponse.getModel().contains("Pro1")){
				String shadow = iotDataHelper.getDeviceShadowState(thingResponse.getMacId());
				
				JSONParser parser = new JSONParser();
				org.json.simple.JSONObject objPar = (org.json.simple.JSONObject)parser.parse(shadow);
				org.json.simple.JSONObject stateObj = (org.json.simple.JSONObject)objPar.get("state");
				org.json.simple.JSONObject reportedObj = (org.json.simple.JSONObject)stateObj.get("reported");
				
				logger.info("===$$$ Sending reported data to convertee=r:"+reportedObj.toJSONString());
				Map  formatedSchedule = scheduleDaoImpl.generateXpacJson(stateObj.toJSONString());
				
				logger.info("===$$$ Recieved converted schedule data :"+formatedSchedule);
				if(formatedSchedule!=null){
					logger.info("===$$$ Recieved converted schedule data COOL:"+formatedSchedule.get("cool"));
	
					devSchedule.put("scheduleCool", formatedSchedule.get("cool"));
	
					logger.info("===$$$ Recieved converted schedule data HEAT:"+formatedSchedule.get("heat"));
					
					devSchedule.put("scheduleHeat", formatedSchedule.get("heat"));
				}
			}else{
				/**Call to xcspec api to get schedule cool*/
				String scheduleCool =restClient.getSchedule(thingResponse.getXcspecDeviceId(), "C");
				if(scheduleCool!=null){
					devSchedule.put("scheduleCool", scheduleCool);
				}

				/**Call to xcspec api to get schedule heat*/
				String scheduleHeat =restClient.getSchedule(thingResponse.getXcspecDeviceId(), "H");
				if(scheduleHeat!=null){
					devSchedule.put("scheduleHeat", scheduleHeat);
				}
			}
				


			obj.put("devSchedule", devSchedule);
				
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.SUCCESS_READ_SCHEDULE);
				response.setData(obj);
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_READ_SCHEDULE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_READ_SCHEDULE_FAILED, logger, e);
		}

		logger.info("[END] [readSchedule] [WeatherServiceImpl SERVICE LAYER]");

		return response;
	}

	@Override
	public Response updateForecast(UpdateForecastRequestList updateForecastReq, int userId) throws VEMAppException {
		logger.info("[BEGIN] [WeatherServiceImpl] [updateForecast]");
		/**sql store results*/
		int storeResult = 0;

		/**Creating reference of Response object*/
		Response resp = new Response();
		List<Integer> savedForecastIds = new ArrayList<>();

		/* Instantiating the bean validator and validating the request bean.*/
		ValidatorBean validatorBean = ConfigurationUtils.validateBeans(updateForecastReq);
		if(validatorBean.isNotValid()){
			//Catches when bean validations failed.
			resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			resp.setCode(validatorBean.getMessage());
		}else{
			List<UpdateForecastRequest> forecastList = updateForecastReq.getForecastConfig();
			for (int i = 0; i < forecastList.size(); i++) {
				forecastList.get(i).setCreatedBy(userId);
				storeResult = weatherDao.updateForecast(forecastList.get(i));
				if (storeResult>0) {
					savedForecastIds.add(forecastList.get(i).getForecastId());
				}

			}
			
			if(!savedForecastIds.isEmpty()){
				
				List<ForecastResponse> data = getStoredForecastInfo(savedForecastIds);
				
				insertForecastActivityLog(data.get(0).getTypeId(), userId, "Updated", data.get(0).getForecastName()+" forecast schedule updated", data.get(0).getType());
				
				resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				resp.setCode(ErrorCodes.SUCCESS_UPDATE_FORECAST_CONFIG);
				resp.setData(data);
			}else{
				resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				resp.setCode(ErrorCodes.ERROR_UPDATE_FORECAST_CONFIG);
				resp.setData(storeResult);
			}
		}
		logger.info("[END] [WeatherServiceImpl] [updateForecast]");
		return resp;
	}

	@Override
	public Response deleteForecast(int forecastId, int userId) throws VEMAppException {
		Response response = new Response();
		
		List<ForecastResponse> forecastListDB = weatherDao.getForecastList(0, forecastId, 3);
		
		int result = weatherDao.deleteForecast(forecastId, userId);
		if(result >= 1){
			
			
			if(forecastListDB!=null){
				insertForecastActivityLog(forecastListDB.get(0).getTypeId(), userId, "Deleted", forecastListDB.get(0).getForecastName()+" forecast schedule deleted", forecastListDB.get(0).getType());
			}
			//delete thing shadow. or remove cache or remove last recorded data
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.SUCCESS_DELETE_FORECAST);
			response.setData(result);
		}else{
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_DELETE_FORECAST_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED+":Device delete failed in db.");
		}
		return response;
	}

	@Override
	public Response runSchedule(int deviceId, int scheduleId, String xcspecId, GetUserResponse userInfo) throws VEMAppException {
		ArrayList deviceListForecast = new ArrayList();
		HashMap devicemapForecast= new HashMap();
		Response response = new Response();
		
/*		devicemapForecast.put("xcspec_device_id",xcspecId);
		devicemapForecast.put("device_id",deviceId);
		devicemapForecast.put("schedule_id",scheduleId);
		deviceListForecast.add(devicemapForecast);*/
		
		GetUserResponse userDetails = new GetUserResponse();
		userDetails.setUserId(userInfo.getUserId());
		
		Schedule schedule = new Schedule();
		schedule.setDeviceId(""+deviceId);
		schedule.setScheduleId(""+scheduleId);
		schedule.setCustomerId("");
		schedule.setGroupId("");
		schedule.setSiteId("");

		
		new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Apply schedule thread started for device"+schedule.getDeviceId());
					try {
						//scheduleDaoImpl.applyDeviceJSON(null,deviceListForecast,0);
						scheduleDaoImpl.applySchedule(userDetails, schedule);
					} catch (VEMAppException e) {
						logger.error("Error found while applying schedule");
					}
			}
		}).start();

		
		
		response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
		response.setCode(ErrorCodes.SUCCESS_RUN_SCHEDULE);
		return response;
	}
	
	
	public void insertForecastActivityLog(int forecastId, int userId, String action, String descr, int addForecastInDevOrSite){

		try {
			String serviceId="4";
			
			//type id 0:siteid, 1: deviceid 
			//service id 
			if(addForecastInDevOrSite==0)
				serviceId="3";
			
			// Activity log 
			AuditRequest auditRequest = new AuditRequest();
			auditRequest.setUserId(userId);
			auditRequest.setUserAction(action);
			auditRequest.setLocation("");
			auditRequest.setServiceId(serviceId); // for forecast module
			auditRequest.setDescription(descr);
			auditRequest.setServiceSpecificId(forecastId);
			auditRequest.setOutFlag("");
			auditRequest.setOutErrMsg("");
			auditDao.insertAuditLog(auditRequest);

		} catch (Exception e) {
			logger.error("Error found while auditing forecast"+e);
		}
	}
	
	@Override
	public Response updateForecastMode(String deviceId, String forecastMode,String forcastType, int userId) throws VEMAppException {
		
		Response response = new Response();
		
		int result = weatherDao.updateForecastMode(deviceId, forecastMode,forcastType, userId);
		if(result == 1){
			//delete thing shadow. or remove cache or remove last recorded data
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.SUCCESS_UPDATE_FORECAST_CONFIG);
			response.setData(result);
		}else if(result == 2){
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.DEVICE_FORECAST_NULL);
			response.setData(result);
		}else{
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_UPDATE_FORECAST_CONFIG);
			response.setData(CommonConstants.ERROR_OCCURRED+":Forecast update failed in db.");
		}
		return response;
	}
	
	@Override
	public void fetchAndStoreHistoryData(int deviceId, String zipcode){

		try{
			Date endTime = new Date();
			logger.error("[fetchAndStoreHistoryData] "+endTime);
			long diffInMinutes;
			if (startTimeMap.containsKey(deviceId)) {
				Date startTime = startTimeMap.get(deviceId);
				long duration  = endTime.getTime() - startTime.getTime();
				diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
			} else {
				diffInMinutes = 60;
			}
			logger.error("[fetchAndStoreHistoryData] diffInMinutes "+diffInMinutes);
			if (diffInMinutes > 59) {
				logger.error("[fetchAndStoreHistoryData] inside if");
				String hourlyForecastResp = wunder.getHourlyHistoryData(zipcode);
				logger.info("Hourly History data in device profile:"+hourlyForecastResp);
				
				ThingResponse thingResponse = iotDao.getThingInfo(1,deviceId, null);
	
				parseAndInsertHourlyForecastData(hourlyForecastResp, thingResponse);
				startTimeMap.put(deviceId, endTime);
			}
		}catch (Exception e) {
			logger.error("Error found while fetching hourly forecast data", e);
		}
	}
}
