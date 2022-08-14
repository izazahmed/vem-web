package com.enerallies.vem.serviceimpl.iot;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.iot.ConfigChange;
import com.enerallies.vem.beans.iot.DeviceConfigInsert;
import com.enerallies.vem.beans.iot.ProcessSetAt;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.listeners.xcspec.RestClient;
import com.enerallies.vem.util.ConfigurationUtils;

@Component(value="thingServiceUtilHelper")
public class ThingServiceUtilHelper {

	/** Auto wiring instance of IoTDao  */
	@Autowired
	IoTDao ioTDao;

	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(ThingServiceUtilHelper.class);

	/**Rest client class reference to call XCSPEC specific REST API's*/
	RestClient restClient = new RestClient();


	public ThingResponse processSetAt(ThingResponse thingResponse) {
		try {
			//ThingState dataObj = (ThingState)thingResponse.getThingState();
			org.json.simple.JSONObject dataObj = (org.json.simple.JSONObject)thingResponse.getThingState();
			if(dataObj!=null){
				//String coolSet = dataObj.getCool_set();
				String coolSet = (String)dataObj.get("cool_set");
				coolSet = coolSet.substring(0, 2);	

				//String heatSet = dataObj.getHeat_set();
				String heatSet = (String)dataObj.get("heat_set");
				heatSet = heatSet.substring(0, 2);

				/*TStatClock clock = dataObj.getTstat_clock();
				String day = clock.getCurrent_day();
				String time = clock.getCurrent_time();*/

				org.json.simple.JSONObject clock = (org.json.simple.JSONObject)dataObj.get("tstat_clock");
				String time = (String)clock.get("current_time");
				String day = (String)clock.get("current_day");

				int intDay = dayOfWeek(day);

				ProcessSetAt processSetAt = ioTDao.processSetAt(thingResponse.getDeviceId(), coolSet, heatSet, intDay, time);

				logger.info("Device set at details found as getCoolPoint : "+processSetAt.getCoolPoint());
				logger.info("Device set at details found as coolSet : "+coolSet);
				logger.info("Device set at details found as getHeatPoint : "+processSetAt.getHeatPoint());
				logger.info("Device set at details found as heatSet : "+heatSet);
			
				
				
				if(processSetAt!=null && processSetAt.getCoolPoint()!=null && processSetAt.getHeatPoint()!=null){
					logger.info("Device set at details found as Conition ");
					if(processSetAt.getCoolPoint().equals(coolSet)){
						logger.info("Device set at details found as coolSet equal");
						thingResponse.setCoolSetAt(1);
					}else{
						thingResponse.setCoolSetAt(0);
					}
					
					if(processSetAt.getHeatPoint().equals(heatSet)){
						logger.info("Device set at details found as heatset equal");
						thingResponse.setHeatSetAt(1);
					}else{
						thingResponse.setHeatSetAt(0);
					}
				}
			}
		} catch (VEMAppException e) {
			logger.error("Error found while fetching set at info", e);
		}

		return thingResponse;
	}

	public int dayOfWeek(String day) {
		int i;

		switch (day) {
		case "mo":
			i=1;
			break;
		case "tu":
			i=2;
			break;
		case "we":
			i=3;
			break;
		case "th":
			i=4;
			break;
		case "fr":
			i=5;
			break;
		case "sa":
			i=6;
			break;
		case "su":
			i=7;
			break;
		default:
			i=1;
			break;
		}
		return i;
	}
	
	
	public int dayOfWeekClock(String day) {
		int i;

		switch (day) {
		case "mo":
			i=0;
			break;
		case "tu":
			i=1;
			break;
		case "we":
			i=2;
			break;
		case "th":
			i=3;
			break;
		case "fr":
			i=4;
			break;
		case "sa":
			i=5;
			break;
		case "su":
			i=6;
			break;
		default:
			i=0;
			break;
		}
		return i;
	}

	public List<ConfigChange> processConfigChanges(int deviceId) {
		List<ConfigChange> configChanges = new ArrayList<>();

		try {
			List<DeviceConfigInsert> devConfig= ioTDao.getConfigChanges(deviceId);

			for (int i = 0; i < devConfig.size(); i++) {
				String name;
				String value = devConfig.get(i).getValue();

				ConfigChange configChange = new ConfigChange();
				//'1 - Target setpoint cool\n2-   Target setpoint Heat\n3 -Set point \n4- Havc mode \n5-Fan mode \n6 - calibration\n7- lock \n8- message\n9-clock day\n10- clock time\n11-(clock min)temp unit\n12-ET\n13- Reset\n'

				switch (devConfig.get(i).getAction()) {
				case 1:{
					name = "cool_set";
					break;
				}
				case 2:{
					name = "heat_set";
					break;
				}
				case 3:{
					name = "temp_hold";
					break;
				}
				case 4:{
					name = "tstat_mode";
					break;
				}
				case 5:{
					name = "fan_state";
					break;
				}
				case 6:{
					name = "calibration";
					break;
				}
				case 7:{
					name = "keyBLockout";
					break;
				}
				case 8:{
					name = "tstat_msg";
					break;
				}
				case 9:{
					name = "current_day";
					break;
				}
				case 10:{
					name = "current_time";
					break;
				}
				case 11:{
					name = "tempUnits";
					break;
				}
				case 12:{
					name = "eTInterval";
					break;
				}
				case 13:{
					name = "reset";
					break;
				}

				default:
					name = "NA";
					break;
				}

				configChange.setConfigName(name);
				configChange.setConfigValue(value);
				configChanges.add(configChange);
			}

		} catch (VEMAppException e) {
			logger.error("Error found while fetching device configuration"+e);
		}
		return configChanges;		
	}

	public ThingResponse getThermostatFields(ThingResponse thingResponse) throws Exception{

		/**Call to xcspec api to get temp units*/
		String resTempUnits = restClient.getTempUnits(thingResponse.getXcspecDeviceId());

		JSONObject liveDataObj = new JSONObject(resTempUnits);
		if("Must login or provide credentials.".equals((String)liveDataObj.get("message"))){
			restClient.loginToGetauthStringEnc();
			resTempUnits = restClient.getTempUnits(thingResponse.getXcspecDeviceId());
		}

		JSONObject resTempUnitsObj = new JSONObject(resTempUnits);
		/**Check for the success response*/
		if((Integer)resTempUnitsObj.get("code") == 200){
			thingResponse.setTempUnits(resTempUnitsObj.getJSONObject("data").getString("t_units"));
		}

		/**Call to xcspec api to get keyboard lockout*/
		String resKeyBLockout = restClient.getKeyBLockout(thingResponse.getXcspecDeviceId());

		JSONObject resKeyBLockoutObj1 = new JSONObject(resKeyBLockout);
		if("Must login or provide credentials.".equals((String)resKeyBLockoutObj1.get("message"))){
			restClient.loginToGetauthStringEnc();
			resKeyBLockout = restClient.getKeyBLockout(thingResponse.getXcspecDeviceId());
		}

		JSONObject resKeyBLockoutObj = new JSONObject(resKeyBLockout);

		/**Check for the success response*/
		if((Integer)resKeyBLockoutObj.get("code") == 200){
			thingResponse.setKeyBLockout(resKeyBLockoutObj.getJSONObject("data").getString("lockout"));
		}


		/**Call to xcspec api to get engaged transaction intevral*/
		String resETInterval = restClient.getETInterval(thingResponse.getXcspecDeviceId());

		JSONObject resETIntervalObj1 = new JSONObject(resETInterval);
		if("Must login or provide credentials.".equals((String)resETIntervalObj1.get("message"))){
			restClient.loginToGetauthStringEnc();
			resETInterval = restClient.getETInterval(thingResponse.getXcspecDeviceId());
		}
		JSONObject resETIntervalObj = new JSONObject(resETInterval);

		/**check for the success response*/
		if((Integer)resETIntervalObj.get("code") == 200){
			thingResponse.seteTInterval(resETIntervalObj.getJSONObject("data").getString("ET"));
		}

		/**Call to xcspec api to get relay status*/
		String relayStatus = restClient.getTStatData(thingResponse.getXcspecDeviceId(), ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_GET_RELAYSTATUS_API"));

		JSONObject relayStatusObj1 = new JSONObject(relayStatus);
		if("Must login or provide credentials.".equals((String)relayStatusObj1.get("message"))){
			restClient.loginToGetauthStringEnc();
			relayStatus = restClient.getTStatData(thingResponse.getXcspecDeviceId(), ConfigurationUtils.getConfig("XCSPEC_BASE_PATH")+ConfigurationUtils.getConfig("XCSPEC_GET_RELAYSTATUS_API"));
		}
		JSONObject relayStatusObj = new JSONObject(relayStatus);

		if((Integer)relayStatusObj.get("code") == 200){
			thingResponse.setRelayStatus(relayStatusObj.getJSONObject("data").toString());
		}

		/*		*//**Call to xcspec api to get schedule cool*//*
		String scheduleCool =restClient.getSchedule(thingResponse.getXcspecDeviceId(), "C");
		if(scheduleCool!=null){
			thingResponse.setScheduleCool(scheduleCool);
		}

		 *//**Call to xcspec api to get schedule heat*//*
		String scheduleHeat =restClient.getSchedule(thingResponse.getXcspecDeviceId(), "H");
		if(scheduleHeat!=null){
			thingResponse.setScheduleHeat(scheduleHeat);
		}*/

		return thingResponse;
	}

	public String getCurrentDaySet(String currentDay) {
		String curr;
		switch (currentDay) {
		case "mo":
			curr = "mon";
			break;
		case "tu":
			curr = "tue";
			break;
		case "we":
			curr = "wed";
			break;
		case "th":
			curr = "thu";
			break;
		case "fr":
			curr = "fri";
			break;
		case "sa":
			curr = "sat";
			break;

		default:
			curr="sun";
			break;
		}

		return curr;
	}
}
