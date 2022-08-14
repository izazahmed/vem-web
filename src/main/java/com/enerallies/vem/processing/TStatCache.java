package com.enerallies.vem.processing;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.listeners.iot.awsiot.ClientTopicSubscriber;
import com.enerallies.vem.util.iot.AWSIOTMQTTClient;

/**
 * File Name : TStatCache 
 * 
 * TStatCache: will maintain the runtime DB i.e. Thermostat list 
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

@Component(value="tStatCache")
public class TStatCache implements InitializingBean{
	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(TStatCache.class);

	@Autowired
	private IoTDao iotDao;

	public static final Hashtable<Integer, Thing> tStatCache = new Hashtable<>();

	@PostConstruct
	public void buildTStatCache(){
		logger.info("BEGIN: Building Thermostat Cache");
		List<Thing> things;
		try {
			things = iotDao.getThingList();


			if(!tStatCache.isEmpty()){
				tStatCache.clear();
			}

			if(things!=null){
			for (int i = 0; i < things.size(); i++) {
				
				if(things.get(i).getAwsCompatible()==1 && (things.get(i).getModel()).contains("Pro1")){
					tStatCache.put(things.get(i).getDeviceId(), things.get(i));
				}
			}
			}

		} catch (SQLException e) {
			logger.error("Error found while fetching device list at the start of server", e);
		}
		logger.info("Thermostat Cache size:"+tStatCache.size());
		logger.info("END: Building Thermostat Cache");
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	public static void addThermostatToCache(Thing thing){
		try {
			logger.info("Cache count before adding:"+tStatCache.size());
			tStatCache.put(thing.getDeviceId(), thing);
			logger.info("Device has been added to Cache Dev id:"+thing.getDeviceId()+" and MAC id:"+thing.getMacId());

			AWSIotMqttClient mqttClient = AWSIOTMQTTClient.getMqttClient();

			ClientTopicSubscriber updateAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+thing.getMacId()+"/shadow/update/accepted", AWSIotQos.QOS0);
			ClientTopicSubscriber getAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+thing.getMacId()+"/shadow/get/accepted", AWSIotQos.QOS0);

			mqttClient.subscribe(updateAcceptedTopic);
			mqttClient.subscribe(getAcceptedTopic);

			logger.info("Subscription is successfull for added Dev id:"+thing.getDeviceId()+" and MAC id:"+thing.getMacId());
			logger.info("Cache count after adding:"+tStatCache.size());
		} catch (AWSIotException e) {
			logger.error("Error found while subscribing for newly added device", e);
		}
	}
	
	public static void deleteThermostatFromCache(int deviceId){
		try {
			logger.info("Cache count before deleting:"+tStatCache.size());

			if(tStatCache.containsKey(deviceId)){
				Thing thing = tStatCache.get(deviceId);
				
				AWSIotMqttClient mqttClient = AWSIOTMQTTClient.getMqttClient();
	
				ClientTopicSubscriber updateAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+thing.getMacId()+"/shadow/update/accepted", AWSIotQos.QOS0);
				ClientTopicSubscriber getAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+thing.getMacId()+"/shadow/get/accepted", AWSIotQos.QOS0);
				mqttClient.unsubscribe(updateAcceptedTopic);
				mqttClient.unsubscribe(getAcceptedTopic);
				logger.info("UnSubscription is successfull for added Dev id:"+thing.getDeviceId()+" and MAC id:"+thing.getMacId());
	
				tStatCache.remove(thing.getDeviceId());
				logger.info("Device has been deleted from Cache Dev id:"+thing.getDeviceId()+" and MAC id:"+thing.getMacId());
				
				logger.info("Cache count after deleting:"+tStatCache.size());
			}
		} catch (AWSIotException e) {
			logger.error("Error found while unsubscribing for deleted device", e);
		}
	}
	
	public static void updateThermostatToCache(Thing thing){
		AWSIotMqttClient mqttClient = AWSIOTMQTTClient.getMqttClient();
		
		try {
			if(thing.getRegisterType()==0){
				if(tStatCache.containsKey(thing.getDeviceId())){
					Thing cacheThing = tStatCache.get(thing.getDeviceId());
					
					ClientTopicSubscriber updateAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+cacheThing.getMacId()+"/shadow/update/accepted", AWSIotQos.QOS0);
					ClientTopicSubscriber getAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+cacheThing.getMacId()+"/shadow/get/accepted", AWSIotQos.QOS0);
					mqttClient.unsubscribe(updateAcceptedTopic);
					mqttClient.unsubscribe(getAcceptedTopic);
					logger.info("UnSubscription is successfull for added Dev id:"+cacheThing.getDeviceId()+" and MAC id:"+cacheThing.getMacId());
	
					tStatCache.remove(cacheThing.getDeviceId());
					logger.info("Device has been deleted from Cache Dev id:"+cacheThing.getDeviceId()+" and MAC id:"+cacheThing.getMacId());
					
					logger.info("Cache count after deleting:"+tStatCache.size());
				}
			}else{
				if(tStatCache.containsKey(thing.getDeviceId())){
					Thing cacheThing = tStatCache.get(thing.getDeviceId());
					if(!(thing.getMacId()).equalsIgnoreCase(cacheThing.getMacId())){
						ClientTopicSubscriber updateAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+cacheThing.getMacId()+"/shadow/update/accepted", AWSIotQos.QOS0);
						ClientTopicSubscriber getAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+cacheThing.getMacId()+"/shadow/get/accepted", AWSIotQos.QOS0);
						mqttClient.unsubscribe(updateAcceptedTopic);
						mqttClient.unsubscribe(getAcceptedTopic);
						logger.info("UnSubscription is successfull for updated Dev id:"+cacheThing.getDeviceId()+" and MAC id:"+cacheThing.getMacId());
		
						tStatCache.remove(cacheThing.getDeviceId());
						logger.info("Device has been updated and deleted from Cache Dev id:"+cacheThing.getDeviceId()+" and MAC id:"+cacheThing.getMacId());
						
						logger.info("Cache count after update and deleting:"+tStatCache.size());
						
						//subscription part for the updated device
						logger.info("Cache count before updating:"+tStatCache.size());
						tStatCache.put(thing.getDeviceId(), thing);
						logger.info("Device has been updated to Cache Dev id:"+thing.getDeviceId()+" and MAC id:"+thing.getMacId());

						ClientTopicSubscriber nupdateAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+thing.getMacId()+"/shadow/update/accepted", AWSIotQos.QOS0);
						ClientTopicSubscriber ngetAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+thing.getMacId()+"/shadow/get/accepted", AWSIotQos.QOS0);

						mqttClient.subscribe(nupdateAcceptedTopic);
						mqttClient.subscribe(ngetAcceptedTopic);

						logger.info("Subscription is successfull for updated Dev id:"+thing.getDeviceId()+" and MAC id:"+thing.getMacId());
						logger.info("Cache count after updating:"+tStatCache.size());
						
					}
					
				}else{
					logger.info("Cache count before updating:"+tStatCache.size());
					tStatCache.put(thing.getDeviceId(), thing);
					logger.info("Device has been updated to Cache Dev id:"+thing.getDeviceId()+" and MAC id:"+thing.getMacId());

					ClientTopicSubscriber updateAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+thing.getMacId()+"/shadow/update/accepted", AWSIotQos.QOS0);
					ClientTopicSubscriber getAcceptedTopic = new ClientTopicSubscriber("$aws/things/"+thing.getMacId()+"/shadow/get/accepted", AWSIotQos.QOS0);

					mqttClient.subscribe(updateAcceptedTopic);
					mqttClient.subscribe(getAcceptedTopic);

					logger.info("Subscription is successfull for updated device Dev id:"+thing.getDeviceId()+" and MAC id:"+thing.getMacId());
					logger.info("Cache count after updating:"+tStatCache.size());
				}
			}
		} catch (AWSIotException e) {
			logger.error("Error found while unsubscribing for updating and deleting device", e);
		}
	}
}
