package com.enerallies.vem.jobschedule;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.iot.DeviceMoreDetails;
import com.enerallies.vem.beans.iot.SetClockRequest;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.weather.ForecastResponse;
import com.enerallies.vem.business.WundergroundBusiness;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.dao.weather.WeatherDao;
import com.enerallies.vem.daoimpl.schedule.ScheduleDaoImpl;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.listeners.xcspec.RestClient;
import com.enerallies.vem.service.iot.ThingService;
import com.enerallies.vem.service.weather.WeatherService;
import com.enerallies.vem.serviceimpl.iot.ThingServiceDataUpdater;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.iot.DeviceDataPublisher;


@Component(value="hourlyForecastJob")
public class HourlyForecastJob {
	/** Getting logger*/	
	private static final Logger logger = Logger.getLogger(HourlyForecastJob.class);

	@Autowired
	IoTDao iotDao;

	@Autowired
	WeatherDao weatherDao;

	@Autowired
	WundergroundBusiness wunder;
	@Autowired
	ScheduleDaoImpl scheduleDaoImpl;

	@Autowired
	WeatherService weatherService;

	@Autowired
	ThingService thingService;
	
	@Autowired
	DeviceDataPublisher publisher;
	
	/** Auto wiring instance of AuditDao  */
	@Autowired
	private AuditDAO auditDao;
	
	@Autowired
	ThingServiceDataUpdater dataUpdater;

	private RestClient restClient = new RestClient();

	public static final String action = "Updated";

	private void hourlyForecastJobMethod(){
		try {

			logger.info("Hourly Forecast job has been started");
			List<ThingResponse> thingResponse = iotDao.getThingListWithSiteName();

			logger.info("Found the device list in hourly forecast job :"+thingResponse.size());
			ArrayList deviceList = new ArrayList();
			for (int i = 0; i < thingResponse.size(); i++) {

				if(thingResponse.get(i).getRegisterType()==1){
					/*String hourlyForecastResp = wunder.getHourlyForecastData(thingResponse.get(i).getSiteZipcode());
					logger.info("Hourly Forecast :"+hourlyForecastResp);*/
					
					if(thingResponse.get(i).getDeviceStatus()==1){
					
					String hourlyForecastResp = wunder.getHourlyHistoryData(thingResponse.get(i).getSiteZipcode());
					logger.info("Hourly History data :"+hourlyForecastResp);
					weatherService.parseAndInsertHourlyForecastData(hourlyForecastResp, thingResponse.get(i));

					if(thingResponse.get(i).getAwsCompatible()==0){
						preferences(thingResponse.get(i));
					}else{
						applyPreferencesAWSIOTDev(thingResponse.get(i));
					}
						
					List<ForecastResponse> forecastList;

					//Calling the DAO layer getForeList() method.
					forecastList = weatherDao.getForecastList(1, thingResponse.get(i).getDeviceId(), 2);
					/* if forecastList is not null means the get forecastList request is
					 *  success
					 *  else fail.
					 */
					
					logger.info("=====Data =======:"+forecastList.size()+" mode :"+" night schedule:"+thingResponse.get(i).getNightSchedule()+" device Id:"+ thingResponse.get(i).getDeviceId());
					
					if(thingResponse.get(i).getNightSchedule()==1){
					if(forecastList!=null && forecastList.size()>0){
						if(forecastList.get(0).getMode()==0){
								//download and schedule method
								HashMap devicemap= new HashMap();
/*								devicemap.put("xcspec_device_id",thingResponse.get(i).getXcspecDeviceId());
								devicemap.put("device_id",thingResponse.get(i).getDeviceId());
								deviceList.add(devicemap);*/
								
								DeviceMoreDetails devMoreDetails = iotDao.getDeviceMoreDetails(thingResponse.get(i).getDeviceId(), thingResponse.get(i).getCreatedBy(), 1);
							
								logger.info("======Applying schedule for device Id:"+thingResponse.get(i).getDeviceId()+" and schedule Id:"+devMoreDetails.getDevScheduleId());
								GetUserResponse userDetails = new GetUserResponse();
								userDetails.setUserId(1);
								Schedule schedule = new Schedule();
								schedule.setDeviceId(""+thingResponse.get(i).getDeviceId());
								schedule.setScheduleId(""+devMoreDetails.getDevScheduleId());
								schedule.setCustomerId("");
								schedule.setGroupId("");
								schedule.setSiteId("");
								applySchedule(userDetails, schedule);
							}

						}else{

								DeviceMoreDetails devMoreDetails = iotDao.getDeviceMoreDetails(thingResponse.get(i).getDeviceId(), thingResponse.get(i).getCreatedBy(), 1);
							
								logger.info("======Applying schedule for device Id:"+thingResponse.get(i).getDeviceId()+" and schedule Id:"+devMoreDetails.getDevScheduleId());
								GetUserResponse userDetails = new GetUserResponse();
								userDetails.setUserId(1);
								Schedule schedule = new Schedule();
								schedule.setDeviceId(""+thingResponse.get(i).getDeviceId());
								schedule.setScheduleId(""+devMoreDetails.getDevScheduleId());
								schedule.setCustomerId("");
								schedule.setGroupId("");
								schedule.setSiteId("");
								applySchedule(userDetails, schedule);
							
						}
					}

					}//end if device status
				}//end if register type
			}
			logger.info("deviceList in hourly forecast data job************************ :"+deviceList.size());
			if(deviceList.size()>0){
				//scheduleDaoImpl.applyDeviceJSON(null,deviceList,0);
			}
		} catch (SQLException e) {
			logger.error("Error found while fetching device list to fetch hourly forecast data"+e);
		} catch (Exception e) {
			logger.error("Error found while fetching hourly forecast data", e);
		}
	}

	private void applyPreferencesAWSIOTDev(ThingResponse thingResponse) {
		String dbFanState;
		String targetValueFan;
		String dbLockState;
		String targetValueLock;
		String hold;
		String holdLog;
		String hvacMode;
		String hvacModeLog;
		String fanConfigValue = "ON";
		
		org.json.simple.JSONObject target = new org.json.simple.JSONObject();
		logger.info("===$$$ applyPreferencesAWSIOTDev $$$===");
		if(thingResponse.getFanPref()==1){
			dbFanState = "ON";
			targetValueFan="on";
		}else{
			dbFanState = "AUTO";
			targetValueFan = "a";
			fanConfigValue = "IDLE";
		}

		target.put("fm", targetValueFan);
		
		// Auto update of time(Day light savings)
		
		TimeZone timeZone=TimeZone.getTimeZone(thingResponse.getSiteTimezone());
		Calendar calendar = Calendar.getInstance(timeZone);
		
		String[] weekDays = {"su","mo","tu","we","th","fr","sa"};
		
		String weekday = weekDays[calendar.get(Calendar.DAY_OF_WEEK)];
		String time = calendar.get(Calendar.HOUR_OF_DAY) +":"+calendar.get(Calendar.MINUTE);
		SetClockRequest setClockRequest = new SetClockRequest();
		setClockRequest.setCurrentDay(weekday);
		setClockRequest.setCurrentTime(time);
		try
		{
			thingService.setClock(thingResponse.getDeviceId(), setClockRequest,0 );
		}
		catch(Exception e)
		{
			logger.error("Unable to set clock due to ::",e);
		}
		
		//end
		
//		insertActivityLog(thingResponse, 0, action, "Tstat preferences fan mode has been set to "+dbFanState);
		insertIntoActivityLog(thingResponse, 0, action, "Tstat preferences fan mode has been set to "+dbFanState,"4");
		logger.info("===$$$ Calling insert device config $$$===");
		dataUpdater.insertDeviceConfig(thingResponse, 0, 5, fanConfigValue);

		if(thingResponse.getLockPref()==1){
			dbLockState = "partial";
			targetValueLock = "p1";
		}else if(thingResponse.getLockPref()==2){
				dbLockState = "full";
				targetValueLock = "p2";
			}else{
			dbLockState = "unlock";
			targetValueLock = "off";
		}
		target.put("lk", targetValueLock);

		String activityDesc = "Tstat preferences keypad lockout has been set to "+dbLockState;
//		insertActivityLog(thingResponse, 0, action, activityDesc);
		insertIntoActivityLog(thingResponse, 0, action, activityDesc,"4");
			

		if(thingResponse.getHoldToAuto()==1){
			hold = "d";
			holdLog = "AUTO"; 

				target.put("h", hold);
//				insertActivityLog(thingResponse, 0, action, "Tstat preferences hold has been set to "+holdLog);
				insertIntoActivityLog(thingResponse, 0, action, "Tstat preferences hold has been set to "+holdLog,"4");
				logger.info("===$$$ Calling insert device config $$$===");
				dataUpdater.insertDeviceConfig(thingResponse, 0, 3, "ENABLE");
		}					
			

		if(thingResponse.getHvacToAuto()==1){
			hvacModeLog = "AUTO"; 
			String value = "a";
			target.put("om", value);
							
			/*JSONObject omeValue = new JSONObject();
			omeValue.put("h", "on");
			omeValue.put("c", "on");
			omeValue.put("a", "on");
			omeValue.put("eh", "off");
			
			target.put("ome", omeValue);*/
					
//			insertActivityLog(thingResponse, 0, action, "Tstat preferences hvac mode has been set to "+hvacModeLog);
			insertIntoActivityLog(thingResponse, 0, action, "Tstat preferences hvac mode has been set to "+hvacModeLog,"4");
			dataUpdater.insertDeviceConfig(thingResponse, 0, 4, hvacModeLog);
		}

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				publisher.publish(thingResponse.getMacId(), target.toJSONString());
			}
		}).start();
	}

	public void applySchedule(GetUserResponse userDetails,Schedule schedule){
		new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Apply schedule thread started for device"+schedule.getDeviceId());
				if(userDetails!=null && schedule!=null){
					try {
						//scheduleDaoImpl.applyDeviceJSON(null,deviceListForecast,0);
						scheduleDaoImpl.applySchedule(userDetails, schedule);
					} catch (VEMAppException e) {
						logger.error("Error found while applying schedule");
					}
				}
			}
		}).start();
	}
	
	private void preferences(ThingResponse thingResponse) {
		
		logger.info("===$$$ preferences $$$===");
		try{
			String liveData = restClient.getDeviceLiveData(thingResponse.getXcspecDeviceId());

			JSONObject liveDataObj = new JSONObject(liveData);
			if("Must login or provide credentials.".equals((String)liveDataObj.get("message"))){
				restClient.loginToGetauthStringEnc();
				liveData = restClient.getDeviceLiveData(thingResponse.getXcspecDeviceId());
			}

			JSONObject obj = new JSONObject(liveData);
			if((Integer)obj.get("code") == 200){


				JSONObject dataObj = (JSONObject)obj.get("data");

				String liveFanState = dataObj.getString("fan_state");
				String liveHVACMode = dataObj.getString("tstat_mode");
				String liveHoldMode = dataObj.getString("temp_hold");
				String dbFanState;
				String dbLockState;

				if(liveFanState.equalsIgnoreCase("IDLE"))
					liveFanState = "AUTO";


				if(thingResponse.getFanPref()==1){
					dbFanState = "ON";
				}else{
					dbFanState = "AUTO";
				}

				if(!liveFanState.equalsIgnoreCase(dbFanState)){
					JSONObject target = new JSONObject();
					String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_FAN_API");
					target.put("data", dbFanState);
					restClient.setTStatData(url, thingResponse.getXcspecDeviceId(), target.toString());
					thingService.insertIntoActivityLog(thingResponse.getXcspecDeviceId(), 0, action, "Tstat preferences fan mode has been set to "+dbFanState,"4");
				}

				if(thingResponse.getLockPref()==1){
					dbLockState = "PARTIAL1";
				}else{
					dbLockState = "DISABLE";
				}

					JSONObject targetLock = new JSONObject();
					String url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_LOCKOUT_API");
					targetLock.put("data", dbLockState);
					String respFXCSPEC  = restClient.setTStatData(url, thingResponse.getXcspecDeviceId(), targetLock.toString());
					
					
					String s;
					if(dbLockState.equalsIgnoreCase("PARTIAL1"))
						s="partial";
					else
						s="unlock";
					String activityDesc = "Tstat keypad lockout has been set to "+s;
					JSONObject respFXCSPECObj = new JSONObject(respFXCSPEC);
					if((Integer)respFXCSPECObj.get("code") == 200){
						thingService.insertActivityLog(thingResponse.getXcspecDeviceId(), 0, action, activityDesc);
					}
					
					
					
					String hold;
					String holdLog;
					if(thingResponse.getHoldToAuto()==1){
						hold = "ENABLE";
						holdLog = "AUTO"; 

						if(!liveHoldMode.equalsIgnoreCase(hold)){
							JSONObject holdTarget = new JSONObject();
							String holdUrl = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_HOLD_API");
							holdTarget.put("data", hold);
							String respHXCSPEC = restClient.setTStatData(holdUrl, thingResponse.getXcspecDeviceId(), holdTarget.toString());
							JSONObject respHXCSPECObj = new JSONObject(respHXCSPEC);
							if((Integer)respHXCSPECObj.get("code") == 200){
								thingService.insertIntoActivityLog(thingResponse.getXcspecDeviceId(), 0, action, "Tstat preferences hold has been set to "+holdLog,"4");
							}
						}
					}					
					
					String hvacMode;
					String hvacModeLog;
					if(thingResponse.getHvacToAuto()==1){
						hvacMode = "AUTO";
						hvacModeLog = "AUTO"; 

						if(!liveHVACMode.equalsIgnoreCase(hvacMode)){
							JSONObject hvacModeTarget = new JSONObject();
							String hvacModeUrl = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_HVACMODE_API");
							hvacModeTarget.put("data", hvacMode);
							String respFXCSPECHVAC = restClient.setTStatData(hvacModeUrl, thingResponse.getXcspecDeviceId(), hvacModeTarget.toString());
							JSONObject respFXCSPECObjHVAC = new JSONObject(respFXCSPECHVAC);
							if((Integer)respFXCSPECObjHVAC.get("code") == 200){
								thingService.insertIntoActivityLog(thingResponse.getXcspecDeviceId(), 0, action, "Tstat preferences hvac mode has been set to "+hvacModeLog,"4");
							}
						}

					
					}
					

			}//end of checking live data 200 ok response


		}catch (Exception e) {
			logger.error("Error while applying preferences");
		}
	}
	
	public void insertIntoActivityLog(ThingResponse thingResponse, int userId, String action, String descr, String serviceId)
	{
		try
		{
			// Activity log 
			AuditRequest auditRequest = new AuditRequest();
			auditRequest.setUserId(userId);
			auditRequest.setUserAction(action);
			auditRequest.setLocation(""+thingResponse.getLocation());
			auditRequest.setServiceId(serviceId); // for user module
			auditRequest.setDescription(descr);
			auditRequest.setServiceSpecificId(thingResponse.getDeviceId());
			auditRequest.setOutFlag("");
			auditRequest.setOutErrMsg("");
			auditDao.insertAuditLog(auditRequest);

		}
		catch(Exception e)
		{
			logger.error("Unable to insert into activity log due to :: ",e);
		}
	}
	
	public void insertActivityLog(ThingResponse thingResponse, int userId, String action, String descr){

		// Activity log 
		AuditRequest auditRequest = new AuditRequest();
		auditRequest.setUserId(userId);
		auditRequest.setUserAction(action);
		auditRequest.setLocation(""+thingResponse.getLocation());
		auditRequest.setServiceId("4"); // for user module
		auditRequest.setDescription(descr);
		auditRequest.setServiceSpecificId(thingResponse.getDeviceId());
		auditRequest.setOutFlag("");
		auditRequest.setOutErrMsg("");
		auditDao.insertAuditLog(auditRequest);
}
}
