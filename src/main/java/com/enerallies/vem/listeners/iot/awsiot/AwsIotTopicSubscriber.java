/**
 * 
 */
package com.enerallies.vem.listeners.iot.awsiot;

import org.apache.log4j.Logger;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.daoimpl.alert.AlertDaoImpl;
import com.enerallies.vem.exceptions.VEMAppException;


/**
 * File Name : AwsIotTopicSubscriber 
 * 
 * AwsIotTopicSubscriber: is to subscribe for aws iot topics  
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
 * 02	07-11-2016		Rajashekharaiah Muniswamy		Modified onMessage method implement Alerts module integration
 */

public class AwsIotTopicSubscriber extends AWSIotTopic{
	
	// Getting logger
	private static final Logger logger = Logger.getLogger(AwsIotTopicSubscriber.class);
	private AlertDaoImpl alertDao;
	

	public AwsIotTopicSubscriber(String topic, AWSIotQos qos) {
		super(topic, qos);
		alertDao = (AlertDaoImpl)ApplicationContextProvider.getApplicationContext().getBean("alertDaoImpl");
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
			
			if(stateBody.containsKey("reported")){
				org.json.simple.JSONObject reported = (org.json.simple.JSONObject)stateBody.get("reported");
				logger.info("Sending reported data to Alerts module"+reported.toJSONString());
				logger.info("Test DAO"+alertDao);
				AlertRequest alertRequest = new AlertRequest();
					alertRequest.setDeviceJSON(reported.toJSONString());
					alertDao.storeDeviceStatus(alertRequest);
			}

			} catch (VEMAppException|ParseException e) {
				logger.error("Error occured while giving device data to Alerts module"+e.getMessage());
			}
			
	}

}
