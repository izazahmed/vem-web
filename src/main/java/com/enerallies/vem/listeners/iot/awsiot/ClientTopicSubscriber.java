package com.enerallies.vem.listeners.iot.awsiot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.daoimpl.alert.AlertDaoImpl;
import com.enerallies.vem.daoimpl.schedule.ScheduleDaoImpl;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.processing.TStatCache;
import com.enerallies.vem.util.iot.AWSIOTMQTTClient;
import com.enerallies.vem.util.iot.XCSPECDataParser;

public class ClientTopicSubscriber extends AWSIotTopic{

	private static final Logger logger = Logger.getLogger(ClientTopicSubscriber.class);
	private AlertDaoImpl alertDao = (AlertDaoImpl)ApplicationContextProvider.getApplicationContext().getBean("alertDaoImpl");

	XCSPECDataParser xcspecDataParser = (XCSPECDataParser)ApplicationContextProvider.getApplicationContext().getBean("xcspecDataParser");
	
	ScheduleDaoImpl scheduleDaoImpl = (ScheduleDaoImpl)ApplicationContextProvider.getApplicationContext().getBean("scheduleDaoImpl");
	
	public ClientTopicSubscriber(String topic, AWSIotQos qos) {
		super(topic, qos);
	}

	@Override
	public void onMessage(AWSIotMessage message) {
		super.onMessage(message);

		String topic = message.getTopic();
		String s = message.getStringPayload();

		logger.info("Topic Received :"+topic);
		logger.info("Payload Received :"+s);
		String topicArr[] = topic.split("/");
		try {
			JSONParser jsonParser=new JSONParser();
			org.json.simple.JSONObject json =(org.json.simple.JSONObject)jsonParser.parse(s);
			org.json.simple.JSONObject stateBody = (org.json.simple.JSONObject)json.get("state");

			if(topic.contains("update/accepted")){

				if(stateBody.containsKey("reported")){
					org.json.simple.JSONObject reported = (org.json.simple.JSONObject)stateBody.get("reported");
					logger.info("Update accepted and reported data :"+reported);
					logger.info("Thing name :"+topicArr[2]);
					
					scheduleDaoImpl.insertDeviceLastUpdate(topicArr[2]);
					
					publishMessage(topicArr[2]);
					
					if(reported.containsKey("r")){
						processResetRollback(topicArr[2], reported);
					}
					
				}/*else if(stateBody.containsKey("desired")){
					org.json.simple.JSONObject desired = (org.json.simple.JSONObject)stateBody.get("desired");
					
					if(desired!=null){
						
						if(desired.containsKey("prU")){
							
							try {
								Thread.sleep(600000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}


						}else{
							try {
								Thread.sleep(60000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							
						}
						clearDesired(topicArr[2]);			
					}
				}
*/
			}else if (topic.contains("get/accepted")) {
				
				JSONParser parser = new JSONParser();
				org.json.simple.JSONObject reported = (org.json.simple.JSONObject)stateBody.get("reported");
				logger.info("Get shadow accepted and reported data :"+reported);
				
				/* commented to do some
				//org.json.simple.JSONObject convertedObj = xcspecDataParser.parse(reported);
				String respStr = scheduleDaoImpl.getDeviceJsonResponse(reported.toJSONString());
				org.json.simple.JSONObject convertedObj = (org.json.simple.JSONObject)parser.parse(respStr);
				
				convertedObj.put("macId", topicArr[2]);
				convertedObj.put("success", false);
				convertedObj.put("message", "");
				convertedObj.put("xcspec_device_id", "");
				
				AlertRequest alertRequest = new AlertRequest();
				alertRequest.setDeviceJSON(convertedObj.toJSONString());
				
				logger.info("Sending data to alert module :"+convertedObj);
				alertDao.storeDeviceStatus(alertRequest);
				logger.info("Sent data to alert module :"+convertedObj);
				
				*/
				
				pushToCache(topicArr[2], stateBody);
				
				if(stateBody.containsKey("delta")){
					JSONObject  delta = (JSONObject)stateBody.get("delta");
					
					if(delta.containsKey("cl")){
						
						if(stateBody.containsKey("desired")){
						clearDesired(topicArr[2]);
						logger.info("===$$$ Found clock and clearing desired  $$$==="+topicArr[2]);
						}
					}
					logger.info("===$$$ There are delta value so not clearing desired for mac $$$==="+topicArr[2]);
				}else{
					if(stateBody.containsKey("desired")){
					clearDesired(topicArr[2]);
					logger.info("===$$$ No delta found So clearing desired  $$$==="+topicArr[2]);
					}
				}
			}
		} catch (ParseException | VEMAppException e) {
			logger.error("Error occured while parsing recieved message",e);
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
			

/*		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
				AWSIotMqttClient client = AWSIOTMQTTClient.getMqttClient();
				JSONObject desiredData=null;
				String dataPayload = "{\"state\":{\"desired\":"+desiredData+"}}";
				
				client.publish("$aws/things/"+macId+"/shadow/update", AWSIotQos.QOS0, dataPayload);
				} catch (AWSIotException e) {
				logger.error("Error occured while publishing clear desired -->null",e);			
				}
			}
		}).start();	
*/		
/*		ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(new Runnable() {
            public void run() {
            	try{
    				AWSIotMqttClient client = AWSIOTMQTTClient.getMqttClient();
    				JSONObject desiredData=null;
    				String dataPayload = "{\"state\":{\"desired\":"+desiredData+"}}";
    				
    				client.publish("$aws/things/"+macId+"/shadow/update", AWSIotQos.QOS0, dataPayload);
    				} catch (AWSIotException e) {
    				logger.error("Error occured while publishing clear desired -->null",e);			
    				}
            }
        }, 30, TimeUnit.SECONDS);
        
        exec.shutdown();*/

	}


	private void pushToCache(String macId, JSONObject stateBody) {
		
		for(Map.Entry entry: TStatCache.tStatCache.entrySet()){
			Thing thing = (Thing)entry.getValue();
            if(macId.equalsIgnoreCase(thing.getMacId())){
                thing.setShadowState(stateBody);
                
                TStatCache.tStatCache.put(thing.getDeviceId(), thing);
                break; //breaking because its one to one map
            }
        }

	}
	
	private void processResetRollback(String macId, JSONObject reported) {
		String resetStatus = (String)reported.get("r");
		
		logger.info("===$$$ Publishing reset data : "+reported.get("r"));
		if(resetStatus.equals("1")){
			logger.info("===$$$ Publishing reset data found reported: "+reported.get("r"));
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try{
					Thread.sleep(120000);	
					AWSIotMqttClient client = AWSIOTMQTTClient.getMqttClient();
					JSONObject desiredData = new JSONObject();
					desiredData.put("r", "0");
					String dataPayload = "{\"state\":{\"desired\":"+desiredData+"}}";
					
					logger.info("===$$$ Publishing reset data: "+dataPayload);
					client.publish("$aws/things/"+macId+"/shadow/update", AWSIotQos.QOS0, dataPayload);
					} catch (AWSIotException e) {
					logger.error("Error occured while publishing reset thermostat command roll back value",e);			
					} catch (InterruptedException e) {
						logger.error("Error occured while publishing reset thermostat command roll back value thread sleep",e);	
					}
				}
			}).start();	
		}
	}

	public void publishMessage(String thing){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
				AWSIotMqttClient client = AWSIOTMQTTClient.getMqttClient();
				client.publish("$aws/things/"+thing+"/shadow/get", AWSIotQos.QOS0, "");
				} catch (AWSIotException e) {
				logger.error("Error occured while publishing get shadow message to aws iot",e);			
				}
			}
		}).start();	

	}
}
