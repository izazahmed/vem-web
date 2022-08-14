/**
 * 
 */
package com.enerallies.vem.processing;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.enerallies.vem.listeners.iot.awsiot.ClientTopicSubscriber;
import com.enerallies.vem.util.iot.AWSIOTMQTTClient;

/**
 * File Name : TStatDataProcessing 
 * 
 * TStatDataProcessing: is to process the thermostat data by getting thermostat list from DB and subscribing for thermostat data 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        09-02-2017
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	09-02-2017		Rajashekharaiah Muniswamy		File Created
 */
@Component(value="tStatDataProcessorJob")
public class TStatDataProcessorJob {
	
	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(TStatDataProcessorJob.class);

	public void initializeDataProcessing(){
		AWSIotMqttClient mqttClient = AWSIOTMQTTClient.getMqttClient();
		
		try {
/*		if(mqttClient.getConnectionStatus().name().equals("CONNECTED")){
			logger.info("24 hours-Disconnecting the client to reconnect after 24 hours");
			mqttClient.disconnect();
			logger.info("24 hours-Disconnected successfully");
			
			mqttClient = AWSIOTMQTTClient.getMqttClient();
			
			logger.info("24 hours-Re-connected successfully");
		}
*/		
		Set<Integer> keys = TStatCache.tStatCache.keySet();
		
		Iterator<Integer> itr= keys.iterator();
		logger.info("Iterating over Thermostat Cache:"+TStatCache.tStatCache.size());
		while (itr.hasNext()) {
			Integer key = itr.next();
			
			logger.info("Device Id:"+key+" MAC Id:"+TStatCache.tStatCache.get(key).getMacId());
			
			ClientTopicSubscriber updateAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+TStatCache.tStatCache.get(key).getMacId()+"/shadow/update/accepted", AWSIotQos.QOS0);
			ClientTopicSubscriber getAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+TStatCache.tStatCache.get(key).getMacId()+"/shadow/get/accepted", AWSIotQos.QOS0);
		
			mqttClient.subscribe(updateAcceptedTopic);
			mqttClient.subscribe(getAcceptedTopic);
		}
		} catch (AWSIotException e) {
			logger.error("24 hours-Error found while disconnecting after 24 hours", e);
		}
	}
	
}
