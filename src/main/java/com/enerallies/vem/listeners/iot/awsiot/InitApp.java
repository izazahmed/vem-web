/**
 * 
 */
package com.enerallies.vem.listeners.iot.awsiot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.listeners.xcspec.RestClient;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.iot.AWSIOTMQTTClient;

/**
 * File Name : InitApp 
 * 
 * InitApp: is to start the xcspec to awsiot integration timers 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
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
 * 02	06-09-2016		Rajashekharaiah Muniswamy		Added initialize Method
 * 03	07-09-2016		Rajashekharaiah Muniswamy		Added initializeLoginTimer Method
 */

public class InitApp {

	// Getting logger
	private static final Logger logger = Logger.getLogger(InitApp.class);


	/** Create synchronized device list to mantain device id's*/
	public static List<Thing> deviceIdList = new ArrayList<Thing>();

	/** RestClient instance*/
	public static RestClient restClient = new RestClient();


	public static void initialize(){
		try {
			AWSIotMqttClient mqttClient = AWSIOTMQTTClient.getMqttClient();


			logger.debug("Device list size found to get data from xcspec: "+deviceIdList.size());
			for (int i = 0; i < deviceIdList.size(); i++) {
				String macId = deviceIdList.get(i).getMacId();
				String xcspecDeviceId = deviceIdList.get(i).getXcspecDeviceId();

				if(deviceIdList.get(i).getRegisterType()==1 && deviceIdList.get(i).getAwsCompatible()==0 && (deviceIdList.get(i).getModel()).contains("Pro1")){
					AwsIotTopicSubscriber topic = new AwsIotTopicSubscriber("$aws/things/"+macId+"/shadow/update/accepted", AWSIotQos.QOS1);
					mqttClient.unsubscribe("$aws/things/"+macId+"/shadow/update/accepted");
					mqttClient.subscribe(topic);
					queryAndPublishData(xcspecDeviceId, macId);
				}
			}


		} catch (AWSIotException e1) {

			logger.error("Failed to get live data and publish :error: " + e1);
		}
	}

	public static void initializeLoginTimer(){
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				RestClient.authStringEnc = restClient.loginToGetauthStringEnc();
			}
		}, Integer.parseInt(ConfigurationUtils.getConfig("XCSPEC_LOGIN_API_DELAY")), Integer.parseInt(ConfigurationUtils.getConfig("XCSPEC_LOGIN_API_INTERVAL")));

	}

	public static void  publishMessage(String topic,String toSendPayload){

		try {
		// optional parameters can be set before connect()
		AWSIotMqttClient mqttClient = AWSIOTMQTTClient.getMqttClient();
		
		if(topic.contains("delete")){
			String topicArr[] = topic.split("/");
			mqttClient.unsubscribe("$aws/things/"+topicArr[0]+"/shadow/update/accepted");
		}

			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try{
					mqttClient.publish(topic, AWSIotQos.QOS0, toSendPayload);
					} catch (AWSIotException e) {
						logger.error("Failed to publish message for topic and payload: Topic : "+topic+" and Payload : "+toSendPayload);
						logger.error("Error :"+e);
					}
				}
			}).start();
			

			logger.debug("====Message published successfully===: Topic : "+topic+" and Payload : "+toSendPayload);
		} catch (AWSIotException e) {
			logger.error("Failed to publish message for topic and payload: Topic : "+topic+" and Payload : "+toSendPayload);
			logger.error("Error :"+e);
		}
	}

	public static void  queryAndPublishData(String xcspecDeviceId,String macId){
		
		logger.info("XCSPEC device id to get live data"+xcspecDeviceId);
		
		try{
		String liveData = restClient.getDeviceLiveData(xcspecDeviceId);

		JSONObject liveDataObj = new JSONObject(liveData);
		if("Must login or provide credentials.".equals((String)liveDataObj.get("message"))){
			restClient.loginToGetauthStringEnc();
			liveData = restClient.getDeviceLiveData(xcspecDeviceId);
		}
		
		JSONObject obj = new JSONObject(liveData);
		if((Integer)obj.get("code") == 200){

	
			obj.put("xcspec_device_id", xcspecDeviceId);
			obj.put("macId", macId);

			JSONObject dataObj = (JSONObject)obj.get("data");

			if("".equals(dataObj.get("tstat_msg"))){
				logger.debug("Checking for tstat_msg null or not : "+dataObj.get("tstat_msg"));
				dataObj.put("tstat_msg", "null");
			}

			obj.put("data", dataObj);
			liveData = obj.toString();

			/** Prepare AWS IOT message to send and it is standard format to publish device state*/
			String toSendPayload = "{\"state\":{\"reported\":"+liveData+"}}";

			/** Topic to publish device status*/
			String topicPublish = "$aws/things/"+macId+"/shadow/update";


			publishMessage(topicPublish, toSendPayload);
		}
		
		} catch (Exception e) {
			logger.error("[ERROR] [InitApp] [queryAndPublishData]"+e);
		}
	}
}
