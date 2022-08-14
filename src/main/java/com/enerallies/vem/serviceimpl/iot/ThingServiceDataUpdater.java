package com.enerallies.vem.serviceimpl.iot;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.iot.DeviceConfigInsert;
import com.enerallies.vem.beans.iot.SetClockRequest;
import com.enerallies.vem.beans.iot.SetTStatDataRequest;
import com.enerallies.vem.beans.iot.SetTemperatureRequest;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.processing.TStatCache;
import com.enerallies.vem.service.iot.ThingService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.iot.AWSIOTMQTTClient;
import com.enerallies.vem.util.iot.DeviceDataPublisher;

@Component(value="thingServiceDataUpdater")
public class ThingServiceDataUpdater {

	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(ThingServiceDataUpdater.class);
	
	@Autowired
	DeviceDataPublisher publisher;
	
	@Autowired
	ThingService thingService;

	/** Auto wiring instance of AuditDao  */
	@Autowired
	private AuditDAO auditDao;

	/** Auto wiring instance of IoTDao  */
	@Autowired
	private IoTDao ioTDao;
	
	@Autowired
	ThingServiceUtilHelper utilHelper;
	
	public Response setTemp(Response response, ThingResponse thingResponse, SetTemperatureRequest setTemperatureRequest,
			int userId) {
		JSONObject desiredObjSetTemp = new JSONObject();
		
		checkDeltaAndClearDesired(thingResponse);

		/*
		 *  Convert the data to aws iot format and then publish as shown below	
		 * 	    cool set point
		 *	    {
		 *	    "cs":"75"
		 *	    }		
		 *
		 *	    heat set point
		 *
		 *	    {
		 *	    "hs":"75"
		 *	    }		
 		 *	
		 * 	
		 */		
		if(setTemperatureRequest.getMode().equalsIgnoreCase("COOL")){
			desiredObjSetTemp.put("cs", setTemperatureRequest.getTemp());
		}else{
			desiredObjSetTemp.put("hs", setTemperatureRequest.getTemp());
		}
		
		logger.info("===$$$ Publishing device temperature set point for mac id:"+thingResponse.getMacId()+" and data: "+desiredObjSetTemp+" $$$===");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				publisher.publish(thingResponse.getMacId(), desiredObjSetTemp.toJSONString());
			}
		}).start();

		
		if("COOL".equalsIgnoreCase(setTemperatureRequest.getMode())){
			insertActivityLog(thingResponse, userId, "Updated", "Tstat Cool SP has been set to "+setTemperatureRequest.getTemp());
			insertDeviceConfig(thingResponse, userId, 1, setTemperatureRequest.getTemp());
		}else{
			insertActivityLog(thingResponse, userId, "Updated", "Tstat Heat SP has been set to "+setTemperatureRequest.getTemp());
			insertDeviceConfig(thingResponse, userId, 2, setTemperatureRequest.getTemp());
		}
		
		logger.info("===$$$ Published and inserted logs device temperature set point for mac id:"+thingResponse.getMacId()+" and data: "+desiredObjSetTemp+" $$$===");
		response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
		response.setCode(ErrorCodes.SUCCESS_DEVICE_SET_TEMP);
		response.setData(setTemperatureRequest);

		return response;
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

	public void insertDeviceConfig(ThingResponse thingResponse, int userId, int action, String value){
		try {
			// Insert device configuration 
			DeviceConfigInsert deviceConfig = new DeviceConfigInsert();
			deviceConfig.setAction(action);
			deviceConfig.setValue(value);
			deviceConfig.setCreatedBy(userId);
			deviceConfig.setUpdatedFlag(0);
			deviceConfig.setDeviceId(thingResponse.getDeviceId());


			ioTDao.insertDeviceConfig(deviceConfig);
			
			} catch (VEMAppException e) {
				logger.error("Error found while inserting device configuration", e);
			}

	}


	public Response setClock(Response response, ThingResponse thingResponse, SetClockRequest setClockRequest,
			int userId) {
		JSONObject desiredObjSetClock = new JSONObject();

		checkDeltaAndClearDesired(thingResponse);
		/*
		 *  Convert the data to aws iot format and then publish as shown below	
		 * 	    set clock
		 *	    {"cl":{"h":"00:51","d":2}}
		 *
 		 *	
		 * 	
		 */		
		int day = utilHelper.dayOfWeekClock(setClockRequest.getCurrentDay());
		
		JSONObject clock = new JSONObject();
		clock.put("h", setClockRequest.getCurrentTime());
		clock.put("d", day);
		
		desiredObjSetClock.put("cl", clock);
		
		logger.info("===$$$ Publishing device clock set for mac id:"+thingResponse.getMacId()+" and data: "+desiredObjSetClock+" $$$===");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				publisher.publish(thingResponse.getMacId(), desiredObjSetClock.toJSONString());
			}
		}).start();
		
		
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
		
		insertActivityLog(thingResponse, userId, "Updated", "Tstat clock has been set to "+currentDay+" "+formattedTime);
		logger.info("===$$$ Published and inserted logs device temperature set point for mac id:"+thingResponse.getMacId()+" and data: "+desiredObjSetClock+" $$$===");
		return response;
	}


	public Response setTstatData(Response response, ThingResponse thingResponse,
			SetTStatDataRequest setTStatDataRequest, int userId) {
		String 	activityAction		= "";
		String 	activityDesc		= "";
		int 	configAction		= 0;
		String  configValue			= "";
		
		checkDeltaAndClearDesired(thingResponse);
		
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
			String value;
			
			if(configValue.equalsIgnoreCase("ENABLE"))
				value = "e";
			else
				value = "d";	
			
			target.put("h", value);
			activityDesc = "Tstat hold has been set to "+configValue;
			configAction = 3;
		}else if("2".equals(setTStatDataRequest.getType())){
			String var = setTStatDataRequest.getData();
			String value;
			if(var.equalsIgnoreCase("IDLE")){
				var = "AUTO";
				value = "a";
			}else{
				value = "on";
			}

			target.put("fm", value);
			
			activityDesc = "Tstat fan mode has been set to "+var;
			configAction = 5;

		}else if("3".equals(setTStatDataRequest.getType())){
			target.put("tc", setTStatDataRequest.getData());

			activityDesc = "Tstat calibration has been set to "+configValue;
			//configAction = 6;

		}/*else if("4".equals(setTStatDataRequest.getType())){
			url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_UNIT_API");
			target.put("data", setTStatDataRequest.getData());
			respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());

			activityDesc = "Tstat display unit has been set to "+configValue;
			//configAction = 11;
		}*/else if("5".equals(setTStatDataRequest.getType())){
			
			String s;
			String value;
			if(configValue.equalsIgnoreCase("PARTIAL1")){
				s="partial";
				value = "p1";
			}else if(configValue.equalsIgnoreCase("FULL")){
				s="full";
				value = "p2";
			}else{
				s="unlocked";
				value = "off";
			}

			target.put("lk", value);

			activityDesc = "Tstat keypad lockout has been set to "+s;
			//configAction = 7;

		}else if("6".equals(setTStatDataRequest.getType())){
			target.put("mt", setTStatDataRequest.getData());
			target.put("me", "on");

			activityDesc = "Tstat message has been set to "+configValue;
			configAction = 8;

		}else if("8".equals(setTStatDataRequest.getType())){

			target.put("r", "1");
			activityDesc = "Tstat has been reset";
		}else if("9".equals(setTStatDataRequest.getType())){

			String value;
			String logField;
			if(configValue.equalsIgnoreCase("COOL")){
				value = "c";
				target.put("om", value);
				
				/*JSONObject omeValue = new JSONObject();
				omeValue.put("h", "on");
				omeValue.put("c", "on");
				omeValue.put("a", "off");
				omeValue.put("eh", "off");
				target.put("ome", omeValue);*/
				
				logField = "COOL";

			}else if(configValue.equalsIgnoreCase("HEAT")){
				value = "h";
				target.put("om", value);
				
				/*JSONObject omeValue = new JSONObject();
				omeValue.put("h", "on");
				omeValue.put("c", "on");
				omeValue.put("a", "off");
				omeValue.put("eh", "off");
				target.put("ome", omeValue);*/
				logField = "HEAT";
				
			}else if(configValue.equalsIgnoreCase("OFF")){
				value = "off";
				target.put("om", value);
				
				/*JSONObject omeValue = new JSONObject();
				omeValue.put("h", "off");
				omeValue.put("c", "off");
				omeValue.put("a", "off");
				omeValue.put("eh", "off");
				target.put("ome", omeValue);*/
				
				logField = "OFF";
			}else{
				value = "a";
				target.put("om", value);
								
				/*JSONObject omeValue = new JSONObject();
				omeValue.put("h", "on");
				omeValue.put("c", "on");
				omeValue.put("a", "on");
				omeValue.put("eh", "off");
				
				target.put("ome", omeValue);*/
				logField = "AUTO";
			}
			
			activityDesc = "Tstat HVAC mode has been set to "+logField;
			configAction = 4;

		}else{
			/*url = ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_SET_ETT_API");
			target.put("data", setTStatDataRequest.getData());
			respFXCSPEC = restClient.setTStatData(url, xcspecDevId, target.toString());

			activityDesc = "Tstat ETT has been set to "+configValue;
			//configAction = 12;
			 * */	
			activityDesc = "";
			configAction = 0;
		}

		
		
		if(!activityDesc.isEmpty()){
			logger.info("===$$$ Publishing device data set for mac id:"+thingResponse.getMacId()+" and data: "+target+" $$$===");
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					publisher.publish(thingResponse.getMacId(), target.toJSONString());
				}
			}).start();
			
						
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.SUCCESS_DEVICE_SET_TSTAT_DATA);
			response.setData(target);

			logger.info("===$$$ Published and inserted logs device set data for mac id:"+thingResponse.getMacId()+" and data: "+target+" $$$===");

			insertActivityLog(thingResponse, userId, activityAction, activityDesc);
			
			if(configAction!=0)
				insertDeviceConfig(thingResponse, userId, configAction, configValue);
		}else{
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode("Command not supported");

		}
		return response;
	}
	
	public void checkDeltaAndClearDesired(ThingResponse thingResponse){
		Thing thing = TStatCache.tStatCache.get(thingResponse.getDeviceId());
		
		if(thing!=null){
			JSONObject stateBody = (JSONObject)thing.getShadowState();

			if(stateBody!=null){
				
				if(stateBody.containsKey("delta")){
					JSONObject  delta = (JSONObject)stateBody.get("delta");
					
					if(delta.containsKey("cl")){
						if(stateBody.containsKey("desired")){
						clearDesired(thingResponse.getMacId());
						logger.info("===$$$ Found clock and clearing desired  $$$==="+thingResponse.getMacId());
					}
					}
					logger.info("===$$$ There are delta value so not clearing desired for mac $$$==="+thingResponse.getMacId());
				}else{
					if(stateBody.containsKey("desired")){
					clearDesired(thingResponse.getMacId());
					logger.info("===$$$ No delta found So clearing desired  $$$==="+thingResponse.getMacId());
					}
				}
			}
		}
	}
	
	private void clearDesired(String macId) {

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
				AWSIotMqttClient client = AWSIOTMQTTClient.getMqttClient();
				JSONObject desiredData=null;
				String dataPayload = "{\"state\":{\"desired\":"+desiredData+"}}";
				logger.info("============Publishing cleared message ");
				client.publish("$aws/things/"+macId+"/shadow/update", AWSIotQos.QOS0, dataPayload);
				logger.info("============Published cleared message ");
				
				} catch (AWSIotException e) {
				logger.error("Error occured while publishing clear desired -->null",e);			
				}
				
			}
		}).start();
	}
}
