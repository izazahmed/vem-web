package com.enerallies.vem.util.iot;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;


/**
 * File Name : DeviceDataPublisher 
 * 
 * DeviceDataPublisher: is to update device data or give command to device through aws iot thing shadow
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        02-03-2017
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	02-03-2017		Rajashekharaiah Muniswamy		File Created
 */

@Component(value="deviceDataPublisher")
public class DeviceDataPublisher {
	// Getting logger
	private static final Logger logger = Logger.getLogger(DeviceDataPublisher.class);

	public void publish(String macId, String desiredData){
		//Get the mqtt client
		AWSIotMqttClient client = AWSIOTMQTTClient.getMqttClient();
		
		//topic to publish device data
		String topic = "$aws/things/"+macId+"/shadow/update";
		
		//desired data payload
		String dataPayload = "{\"state\":{\"desired\":"+desiredData+"}}";
		
		try {
			logger.info("Publishing or updating device data of device mac id:"+macId+" and data :"+desiredData);
			
			client.publish(topic, dataPayload);
			
			logger.info("Published device data of device mac id:"+macId+" and data :"+desiredData);
		} catch (AWSIotException e) {
			logger.error("Failed to publish message for topic and payload: Topic : "+topic+" and Payload : "+dataPayload);
			logger.error("Error :", e);
		}
	}
}
