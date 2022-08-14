/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.serviceimpl.iot;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyResult;
import com.amazonaws.services.iot.model.AttachThingPrincipalRequest;
import com.amazonaws.services.iot.model.AttachThingPrincipalResult;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.amazonaws.services.iot.model.CreatePolicyRequest;
import com.amazonaws.services.iot.model.CreatePolicyResult;
import com.amazonaws.services.iot.model.CreateThingRequest;
import com.amazonaws.services.iot.model.CreateThingResult;
import com.amazonaws.services.iot.model.CreateTopicRuleRequest;
import com.amazonaws.services.iot.model.DeleteThingRequest;
import com.amazonaws.services.iot.model.DescribeEndpointRequest;
import com.amazonaws.services.iot.model.DescribeEndpointResult;
import com.amazonaws.services.iot.model.InternalFailureException;
import com.amazonaws.services.iot.model.InvalidRequestException;
import com.amazonaws.services.iot.model.LimitExceededException;
import com.amazonaws.services.iot.model.ResourceAlreadyExistsException;
import com.amazonaws.services.iot.model.ResourceNotFoundException;
import com.amazonaws.services.iot.model.ServiceUnavailableException;
import com.amazonaws.services.iot.model.ThrottlingException;
import com.amazonaws.services.iot.model.TopicRulePayload;
import com.amazonaws.services.iot.model.UnauthorizedException;
import com.enerallies.vem.beans.iot.DeviceData;
import com.enerallies.vem.controller.iot.IoTController;
import com.enerallies.vem.listeners.iot.MyIoTDevice;
import com.enerallies.vem.listeners.iot.TopicListener;
import com.enerallies.vem.service.iot.IoTService;
import com.enerallies.vem.util.ConfigurationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * File Name : IoTServiceImpl 
 * 
 * IoTServiceImpl: is the implementation file for IoT operation methods
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2.1.0
 * @date        21-07-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	21-07-2016			Nagarjuna Eerla		File Created
 * 02	19-08-2016			Nagarjuna Eerla		Removing sonar qube issues
 */

@Service("iotService")
@Transactional
public class IoTServiceImpl implements IoTService, InitializingBean {
	
	// Getting logger
	private static final Logger logger = Logger.getLogger(IoTController.class);
		
	/** creating the credentials object to authenticate AWS Services */
	BasicAWSCredentials credentials = new BasicAWSCredentials(ConfigurationUtils.getConfig("AWS_ACCESS_KEY_ID"),
			ConfigurationUtils.getConfig("AWS_SECRET_ACCESS_KEY"));

	/** MQTT Client */
	AWSIotMqttClient awsIotMqttClient = null;

	
	/** Creating iotClient */
	AWSIotClient iotClient = null;

	/** Creating region */
	Region region = Region.getRegion(Regions.US_EAST_1);

	/**
	 * Method is for to initialize MQTT and AWS IoT clients.
	 */
	//@PostConstruct
	public void init(){

			/** Creating AWS IoT client */
			iotClient = new AWSIotClient(credentials);
			/** Creating AWS MQTT client */
			//awsIotMqttClient = new AWSIotMqttClient(ConfigurationUtils.getConfig("AWS_IOT_MQTT_HOST"), ConfigurationUtils.getConfig("AWS_IOT_MQTT_CLIENT_ID"), ConfigurationUtils.getConfig("AWS_ACCESS_KEY_ID"),
			//		ConfigurationUtils.getConfig("AWS_SECRET_ACCESS_KEY"));
			/** Connecting to MQTT client */
			//awsIotMqttClient.connect();

			/** setting region for device thing */
			iotClient.setRegion(region);

	}
	
	@Override
	public CreateThingResult createThing(CreateThingRequest thingRequest) throws Exception {

		/** thing result object */
		CreateThingResult thingResult = null;
		try {

			/** Creates a thing in the Thing Registry. */
			thingResult = iotClient.createThing(thingRequest);

		} catch (InternalFailureException | InvalidRequestException | ResourceAlreadyExistsException
				| ServiceUnavailableException | ThrottlingException | UnauthorizedException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return thingResult;
	}

	@Override
	public void deleteThing(DeleteThingRequest deleteThingRequest) throws Exception {
		
		try {
			
			/** Deletes the specified thing from the Thing Registry. */
			iotClient.deleteThing(deleteThingRequest);
			
		} catch (InternalFailureException | InvalidRequestException | ResourceAlreadyExistsException
				| ServiceUnavailableException | ThrottlingException | UnauthorizedException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		
	}

	@Override
	public DescribeEndpointResult describeEndpoint(DescribeEndpointRequest describeEndpointRequest) throws Exception {
		/** describe end point response object */
		DescribeEndpointResult describeEndpointResult = null;
		try {
			
			describeEndpointRequest.setRequestCredentials(credentials);
			
			/** Creates a thing in the Thing Registry. */
			describeEndpointResult = iotClient.describeEndpoint(describeEndpointRequest);

		} catch (InternalFailureException | InvalidRequestException | ResourceAlreadyExistsException
				| ServiceUnavailableException | ThrottlingException | UnauthorizedException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return describeEndpointResult;
	}

	@Override
	public void publishMessages(DeviceData deviceData) throws Exception {
		String payload = null;
        try {
        	
        	//attaching device to MQTT Client
        	AWSIotDevice device = new AWSIotDevice(deviceData.getName());
        	awsIotMqttClient.attach(device);
        	
        	/** Payload to publish into IoT */
        	payload = new ObjectMapper().writeValueAsString(deviceData);
        	/** Publishing message / paylod to IoT */
            awsIotMqttClient.publish(ConfigurationUtils.getConfig("TOPIC_NAME"), payload, 3000);
                       
        } catch (AWSIotException e) {
            throw e;
        } catch (Exception e) {
        	 throw e;
        }
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		/**
		 * Currently we are not using this overridden method.
		 */
		
	}

	@Override
	public void subscribeMessages(String topicName) throws Exception {
		
        try {
        	/** creating topic  */        	
        	AWSIotTopic topic = new TopicListener(topicName, AWSIotQos.QOS0);
        	
        	/** subscribing for messages on topic */
        	awsIotMqttClient.subscribe(topic, true);
                       
        } catch (AWSIotException e) {
            throw e;
        } catch (Exception e) {
        	 throw e;
        }
		
	}

	@Override
	public String getDeviceShadowState(String thingName) throws Exception {
		String deviceState = null;
		MyIoTDevice myDevice = null;
        try {
        	
        	myDevice = new MyIoTDevice(thingName);
        	awsIotMqttClient.attach(myDevice);
        	
        	// Get the entire shadow document
        	deviceState = myDevice.get();
                       
        } catch (AWSIotException e) {
        	throw e;
        } catch (Exception e) {
        	 throw e;
        }

        return deviceState;
	}

	@Override
	public void updateDeviceShadow(String thingName) throws Exception {
        try {

        	
        	MyIoTDevice myDevice = new MyIoTDevice(thingName);
        	awsIotMqttClient.attach(myDevice);
        	
        	// Delete existing shadow document
        	myDevice.delete();
        	
        	myDevice.setReportInterval(5000);
        	
        	
        } catch (AWSIotException e) {
            throw e;
        } catch (Exception e) {
        	 throw e;
        }		
	}

	@Override
	public void deleteDeviceShadow(String thingName) throws Exception {
		try {

			MyIoTDevice myDevice = new MyIoTDevice(thingName);
        	awsIotMqttClient.attach(myDevice);
        	
        	// Delete existing shadow document
        	myDevice.delete();
        	
        } catch (AWSIotException e) {
            throw e;
        } catch (Exception e) {
        	 throw e;
        }
	}

	@Override
	public CreatePolicyResult createPolicy(CreatePolicyRequest createPolicyRequest) throws Exception {

		/** fetching the policy result */
		CreatePolicyResult createPolicyResult = null;
		
		try {

			//Policy result
			createPolicyResult = iotClient.createPolicy(createPolicyRequest);
			
        } catch (Exception e) {
        	 throw e;
        }
		return createPolicyResult;
	}

	@Override
	public void createTopicRule(CreateTopicRuleRequest createTopicRuleRequest) throws Exception {
		try {

			//Policy result
			iotClient.createTopicRule(createTopicRuleRequest);
			
        } catch (Exception e) {
        	 throw e;
        }
	}

	@Override
	public CreateTopicRuleRequest createConfigurableRuleRequest(String ruleName, TopicRulePayload payloadType) {
		
		// Initialization of topic rule request instance
		CreateTopicRuleRequest createTopicRuleRequest = null;
		
		try {
			//creating an instance
			createTopicRuleRequest = new CreateTopicRuleRequest();
			//setting rule pay load
			createTopicRuleRequest.setTopicRulePayload(payloadType);
			//setting rule name
			createTopicRuleRequest.setRuleName(ruleName);
			
		} catch (Exception e) {
			throw e;
		}
		
		return createTopicRuleRequest;
	}
	
	@Override
	public CreateKeysAndCertificateResult generateRSAKeyPairAndCertificate(CreateKeysAndCertificateRequest createKeysAndCertificateRequest) throws Exception {
		/** fetching the 2048-bit RSA key pair and issues an X.509 certificate **/
		CreateKeysAndCertificateResult createKeysAndCertificateResult = null;
		
		try {

			/**
			 * This method Generates the 2048-bit RSA key pair and issues an X.509 certificate
			 */
			createKeysAndCertificateResult = iotClient.createKeysAndCertificate(createKeysAndCertificateRequest);
			
        }catch (InvalidRequestException | ThrottlingException | UnauthorizedException
				| ServiceUnavailableException | InternalFailureException e) {
			throw e;
		}catch (Exception e) {
        	 throw e;
        }
		return createKeysAndCertificateResult;
	}
	
	@Override
	public AttachPrincipalPolicyResult attachingPolicyToCertificate(AttachPrincipalPolicyRequest attachPrincipalPolicyRequest) throws Exception {
		/** fetching the result data after attaching the policy to certificate **/
		AttachPrincipalPolicyResult attachPrincipalPolicyResult = new AttachPrincipalPolicyResult();
		
		try {

			/**
			 * This method attachingPolicyToCertificate will attach the policy to certificate
			 */
			iotClient.attachPrincipalPolicy(attachPrincipalPolicyRequest);
			
        }catch (ResourceNotFoundException  | InvalidRequestException | ThrottlingException
				| UnauthorizedException  | ServiceUnavailableException | InternalFailureException | LimitExceededException e) {
			throw e;
		}catch (Exception e) {
        	 throw e;
        }
		return attachPrincipalPolicyResult;
	}
	
	@Override
	public AttachThingPrincipalResult attachingThingToCertificate(AttachThingPrincipalRequest attachThingPrincipalRequest) throws Exception {
		/** fetching the result data after attaching the Thing to certificate **/
		AttachThingPrincipalResult attachThingPrincipalResult = null;
		
		try {

			/**
			 * This method attachingThingToCertificate will attach the Thing to certificate
			 */
			attachThingPrincipalResult = iotClient.attachThingPrincipal(attachThingPrincipalRequest);
			
        }catch (ResourceNotFoundException  | InvalidRequestException | ThrottlingException
				| UnauthorizedException  | ServiceUnavailableException | InternalFailureException e) {
			throw e;
		}catch (Exception e) {
        	 throw e;
        }
		return attachThingPrincipalResult;
	}

}
