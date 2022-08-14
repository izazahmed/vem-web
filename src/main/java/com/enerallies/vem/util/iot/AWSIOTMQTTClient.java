/**
 * 
 */
package com.enerallies.vem.util.iot;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.enerallies.vem.util.ConfigurationUtils;


/**
 * File Name : AWSIOTMQTTClient 
 * 
 * AWSIOTMQTTClient: is a singleton class to provide single MQTT over websocket connection  
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
 */

@Component(value="mqttClient")
public class AWSIOTMQTTClient implements InitializingBean{
	
	// Getting logger
	private static final Logger logger 	= Logger.getLogger(AWSIOTMQTTClient.class);

	/**Instance of  AWSIOTMQTTClient*/
	private static AWSIOTMQTTClient awsiotmqttClient;
	
	/**Instance of  AWSIotMqttClient*/
	public static AWSIotMqttClient mqttClient;
	
	/**AWS IOT Host*/
	static String clientEndpoint 		= ConfigurationUtils.getConfig("AWS_IOT_MQTT_HOST");      

	// replace with your own client ID. Use unique client IDs for concurrent connections.
	String clientId 				= UUID.randomUUID().toString().split("-")[0]+"mqttclient";                              

	/**AWS IAM User access key Id*/
	static String keyId 				= ConfigurationUtils.getConfig("AWS_ACCESS_KEY_ID");
	
	/**AWS IAM User secret access key*/
	static String keyToken 				= ConfigurationUtils.getConfig("AWS_SECRET_ACCESS_KEY");

	

	
	/**
	 * private constructor
	 */
	private AWSIOTMQTTClient(){
		mqttClient = new AWSIotMqttClient(clientEndpoint, clientId, keyId, keyToken);
		try {
			mqttClient.connect();
			logger.info("MQTT client connected successfully");
		} catch (AWSIotException e) {
			logger.error("Error while connecting MQTT client and the message is : ", e);
		}
	}
	
	
	public void initializeMQTTClient(){
		new AWSIOTMQTTClient();
	}
	
	/**
	 * @return AWSIotMqttClient
	 */
	public static AWSIotMqttClient getMqttClient() {
		
			if(mqttClient==null){
				mqttClient = new AWSIotMqttClient(clientEndpoint, new AWSIOTMQTTClient().clientId, keyId, keyToken);
				try {
					mqttClient.connect();
					logger.info("MQTT client connected successfully");
				} catch (AWSIotException e) {
					logger.error("Error while connecting MQTT client and the message is : " ,e);
				}
			}
		
		
			logger.info("MQTT client connection details "+clientEndpoint);
			
			logger.info("MQTT client connection Status :"+mqttClient.getConnectionStatus());
			
			if(mqttClient.getConnectionStatus().name().equals("DISCONNECTED") || mqttClient.getConnectionStatus().name().equals("RECONNECTING") ){
				
					try {
						logger.info("Disconnected and creating new mqtt client object : "+mqttClient);
						
						mqttClient = new AWSIotMqttClient(clientEndpoint, new AWSIOTMQTTClient().clientId, keyId, keyToken);
						
						logger.info("Created new mqtt client object : "+mqttClient);
					
						mqttClient.connect();
						logger.info("MQTT client connected successfully after new object creation");
					} catch (AWSIotException e) {
						logger.error("Error while connecting MQTT client and the message is : ",e);
					}
			}
			return mqttClient;
		
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
