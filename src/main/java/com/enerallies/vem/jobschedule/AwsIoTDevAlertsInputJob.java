package com.enerallies.vem.jobschedule;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.daoimpl.alert.AlertDaoImpl;
import com.enerallies.vem.daoimpl.schedule.ScheduleDaoImpl;
import com.enerallies.vem.processing.TStatCache;

@Component(value="awsIoTDevAlertsInputJob")
public class AwsIoTDevAlertsInputJob{

	@Autowired
	ScheduleDaoImpl scheduleDaoImpl;
	
	@Autowired
	AlertDaoImpl alertDao;
	
	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(AwsIoTDevAlertsInputJob.class);

	protected void iterateCacheNPublish(){
		logger.info("AWS IOT device input to alerts module job has been started and cache size"+TStatCache.tStatCache.size());
		try {
			
			for(Map.Entry entry: TStatCache.tStatCache.entrySet()){
								
				Thing thing = (Thing)entry.getValue();
				
				if(thing.getShadowState()!=null){
					
					JSONParser parser = new JSONParser();
					
					JSONObject objObj = new JSONObject();
					JSONObject stateBody = (JSONObject)thing.getShadowState();

					org.json.simple.JSONObject reported = (org.json.simple.JSONObject)stateBody.get("reported");
					
					objObj.put("reported", reported);
					
					logger.info("Sending data to convert JSON reported :"+reported);
					
					String respStr = scheduleDaoImpl.getDeviceJsonResponse(objObj.toJSONString(), thing.getMacId());
					org.json.simple.JSONObject convertedObj = (org.json.simple.JSONObject)parser.parse(respStr);
					
					convertedObj.put("macId", thing.getMacId());
					convertedObj.put("success", false);
					convertedObj.put("message", "");
					convertedObj.put("xcspec_device_id", "");
					
					AlertRequest alertRequest = new AlertRequest();
					alertRequest.setDeviceJSON(convertedObj.toJSONString());
					
					logger.info("Sending data to alert module :"+convertedObj);
					alertDao.storeDeviceStatus(alertRequest);
					logger.info("Sent data to alert module :"+convertedObj);
				}
			}
	
		}catch (Exception e) {
			logger.error("Error while publising data to alerts module aws iot device :"+e);
		}

	}

}
