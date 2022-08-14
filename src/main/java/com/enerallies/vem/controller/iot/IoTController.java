/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.controller.iot;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
import com.amazonaws.services.iot.model.ResourceAlreadyExistsException;
import com.amazonaws.services.iot.model.ServiceUnavailableException;
import com.amazonaws.services.iot.model.ThrottlingException;
import com.amazonaws.services.iot.model.UnauthorizedException;
import com.enerallies.vem.beans.iot.DeviceData;
import com.enerallies.vem.service.iot.IoTService;

/**
 * File Name : IoTController 
 * IoTController: is used to handle all the AWS IoT Requests
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

@Controller
@RequestMapping("/iot")
public class IoTController {

	// Getting logger
	private static final Logger logger = Logger.getLogger(IoTController.class);
	
	/** Auto wiring instance of IotService  */
	@Autowired
	IoTService iotService;

	/**
	 * Creates a thing in the Thing Registry.
	 * 
	 * @param thingRequest
	 * @return CreateThingResult
	 */
	@RequestMapping(value = "/createThing/", method = RequestMethod.POST)
	public ResponseEntity<CreateThingResult> createThing(@RequestBody CreateThingRequest thingRequest) {
		/** thingResult object holds the result after creating thing */
		@SuppressWarnings("unused")
		CreateThingResult thingResult = null;
		
		HttpStatus status = HttpStatus.OK;
		try {
			
			/** calling creating thing */
			thingResult = iotService.createThing(thingRequest);
			
			status = HttpStatus.OK;
			
		} catch (InternalFailureException | InvalidRequestException | ResourceAlreadyExistsException
				| ServiceUnavailableException | ThrottlingException | UnauthorizedException e){
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [createThing] [Controller Layer]"+e);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [createThing] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(thingResult, status);
	}
	
	/**
	 * Deletes the specified thing from the Thing Registry.
	 * 
	 * @param deleteThingRequest
	 * @return If the action is successful, the service sends back an HTTP 200 response with an empty HTTP body.
	 */
	@RequestMapping(value = "/deleteThing/", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteThing(@RequestBody DeleteThingRequest deleteThingRequest) {
		
		HttpStatus status = HttpStatus.OK;
		try {
			
			/** Deletes the thing from AWS IoT */
			iotService.deleteThing(deleteThingRequest);
			
			status = HttpStatus.OK;
			
		} catch (InternalFailureException | InvalidRequestException | ResourceAlreadyExistsException
				| ServiceUnavailableException | ThrottlingException | UnauthorizedException e){
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [deleteThing] [Controller Layer]"+e);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [deleteThing] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(status);
	}

	/**
	 * Returns a unique end point specific to the AWS account making the call.
	 * 
	 * @param describeEndpointRequest
	 * @return DescribeEndpointResult
	 */
	@RequestMapping(value = "/describeEndpoint/", method = RequestMethod.POST)
	public ResponseEntity<DescribeEndpointResult> describeEndpoint(@RequestBody DescribeEndpointRequest describeEndpointRequest) {
		
		/** describeEndpointResult object holds the result after describing thing */
		@SuppressWarnings("unused")
		DescribeEndpointResult describeEndpointResult = null;
		
		HttpStatus status = HttpStatus.OK;
		try {
			
			/** calling for describing end point */
			describeEndpointResult = iotService.describeEndpoint(describeEndpointRequest);
			
			status = HttpStatus.OK;
			
		} catch (InternalFailureException | InvalidRequestException | ResourceAlreadyExistsException
				| ServiceUnavailableException | ThrottlingException | UnauthorizedException e){
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [describeEndpoint] [Controller Layer]"+e);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [describeEndpoint] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(describeEndpointResult, status);
	}
	
	/**
	 * Publish the device data to IoT.
	 * 
	 * @param deviceData
	 * @return
	 */
	@RequestMapping(value = "/publishMessage/", method = RequestMethod.POST)
	public ResponseEntity<Void> publishMessages(@RequestBody DeviceData deviceData) {
		
		HttpStatus status = HttpStatus.OK;
		
		try {
			/** publishing the device data */
			iotService.publishMessages(deviceData);
			
			status = HttpStatus.OK;
			
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [publishMessages] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(status);
	}
	
	/**
	 * Subscribes for messages
	 * 
	 * @param topicName
	 * @return
	 */
	@RequestMapping(value = "/subscribeMessage/", method = RequestMethod.POST)
	public ResponseEntity<Void> subscribeMessages(@RequestParam String topicName) {
		HttpStatus status = HttpStatus.OK;
		try {
			/** Subscribing for messages */
			iotService.subscribeMessages(topicName);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [subscribeMessages] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(status);
	}
	
	/**
	 * Get the entire shadow document
	 * 
	 * @param thingName
	 * @return
	 */
	@RequestMapping(value = "/getDeviceState/", method = RequestMethod.GET)
	public ResponseEntity<String> getDeviceState(@RequestParam String thingName) {
		// Initializing shadow document
		String shadowDocument = "";
		HttpStatus status = HttpStatus.OK;
		try {
			/** Subscribing for messages */
			shadowDocument = iotService.getDeviceShadowState(thingName);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [getDeviceState] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(shadowDocument, status);
	}
	
	/**
	 * It fetches the data from thermostat and send to IoT
	 * 
	 * @param thingName
	 * @return
	 */
	@RequestMapping(value = "/updateDevice/", method = RequestMethod.POST)
	public ResponseEntity<Void> updateDeviceState(@RequestParam String thingName) {
		HttpStatus status = HttpStatus.OK;
		try {
			/** Subscribing for messages */
			iotService.updateDeviceShadow(thingName);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [updateDeviceState] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(status);
	}
	
	/**
	 * Deletes the thing shadow
	 * 
	 * @param thingName
	 * @return
	 */
	@RequestMapping(value = "/deleteDeviceState/", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteDeviceState(@RequestParam String thingName) {
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [deleteDeviceState] [Controller Layer]");
		}
		HttpStatus status = HttpStatus.OK;
		try {
			/** Subscribing for messages */
			iotService.deleteDeviceShadow(thingName);
			status = HttpStatus.OK;
		} catch (Exception e) {

			status = HttpStatus.INTERNAL_SERVER_ERROR;
			
			if (logger.isDebugEnabled()){
				logger.debug("[ERROR] [deleteDeviceState] [Controller Layer]"+e);
			}
		}
		if (logger.isDebugEnabled()){
			logger.debug("[END] [deleteDeviceState] [Controller Layer]");
		}
		return new ResponseEntity<>(status);
	}
	
	/**
	 * Creates IoT Policy
	 * 
	 * @param createPolicyRequest
	 * @return
	 */
	@RequestMapping(value = "/createPolicy/", method = RequestMethod.POST)
	public ResponseEntity<CreatePolicyResult> createPolicy(@RequestBody CreatePolicyRequest createPolicyRequest) {
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [createPolicy] [Controller Layer]");
		}
		CreatePolicyResult createPolicyResult = null;
		HttpStatus status = HttpStatus.OK;
		try {
			
			/** Creating policy */
			createPolicyResult = iotService.createPolicy(createPolicyRequest);
			
			status = HttpStatus.OK;
		} catch (Exception e) {
			
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if (logger.isDebugEnabled()){
				logger.debug("[ERROR] [createPolicy] [Controller Layer]"+e);
			}
		}
		if (logger.isDebugEnabled()){
			logger.debug("[END] [createPolicy] [Controller Layer]");
		}

		return new ResponseEntity<>(createPolicyResult, status);
	}
	
	/**
	 * Creates a rule. If the action is successful, the service sends back an HTTP 200 response with an empty HTTP body.
	 * 
	 * @param createTopicRuleRequest
	 * @return
	 */
	@RequestMapping(value = "/createTopicRule/", method = RequestMethod.POST)
	public ResponseEntity<Void> createTopicRule(@RequestBody CreateTopicRuleRequest createTopicRuleRequest) {
		HttpStatus status = HttpStatus.OK;
		try {
			
			/*
			 
			//Configurable topic rule creation
			
			Collection<Action> actions = new ArrayList<>();
			Action action = new Action();
			
			TopicRulePayload topicRulePayload = new TopicRulePayload();
			
			DynamoDBAction dynamoDBAction = new DynamoDBAction();
			dynamoDBAction.setTableName("");
			dynamoDBAction.setHashKeyField("");
			dynamoDBAction.setHashKeyValue("");
			dynamoDBAction.setPayloadField("");
			dynamoDBAction.setRangeKeyField("");
			dynamoDBAction.setRangeKeyValue("");
			dynamoDBAction.setRoleArn("");
			
			action.setDynamoDB(dynamoDBAction);
			action.setKinesis(null);
			
			actions.add(action);
			
			action.setDynamoDB(dynamoDBAction);
			topicRulePayload.setActions(actions);
			topicRulePayload.setAwsIotSqlVersion("");
			topicRulePayload.setDescription("");
			topicRulePayload.setRuleDisabled(false);
			topicRulePayload.setSql("");
			
			iotService.createTopicRule(iotService.createConfigurableRuleRequest("ruleName", topicRulePayload));*/
			
			/** Create topic rule */
			iotService.createTopicRule(createTopicRuleRequest);
			
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [createTopicRule] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(status);
	}
	
	/**
	 * This method will generate a 2048-bit RSA key pair and issues an X.509 certificate
	 * 
	 * @param isActive
	 * @return 
	 */
	@RequestMapping(value = "/generateCertificates/", method = RequestMethod.POST)
	public ResponseEntity<CreateKeysAndCertificateResult> generateRSAKeyPairAndCertificate(@RequestParam Boolean isActive) {
		CreateKeysAndCertificateResult createKeysAndCertificateResult = null;
		HttpStatus status = HttpStatus.OK;
		try {
			
			/** Creating object for CreateKeysAndCertificateRequest and setting the Attribute setAsActive with  isActive parameter*/
			CreateKeysAndCertificateRequest createKeysAndCertificateRequest=new CreateKeysAndCertificateRequest();
			createKeysAndCertificateRequest.setSetAsActive(isActive);
			
			/** generating  2048-bit RSA key pair and an X.509 certificate */
			createKeysAndCertificateResult = iotService.generateRSAKeyPairAndCertificate(createKeysAndCertificateRequest);
			
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("[ERROR] [generateRSAKeyPairAndCertificate] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(createKeysAndCertificateResult, status);
	}
	
	/**
	 * This method attachingPolicyToCertificate will attach the policy to certificate
	 * 
	 * @param policyName
	 * @param certificateARN
	 * @return ResponseEntity
	 */
	@RequestMapping(value = "/attachPolicyToCertificate/", method = RequestMethod.POST)
	public ResponseEntity<String> attachingPolicyToCertificate(@RequestParam String policyName,@RequestParam String certificateARN) {
		AttachPrincipalPolicyResult attachPrincipalPolicyResult = null;
		HttpStatus status = HttpStatus.OK;
		String msg = "Success";
		try {
			
			/** Creating object for AttachPrincipalPolicyRequest and setting the Attribute PolicyName with  policyName parameter
			 * and setting the Attribute Principal with certificateARN parameter
			 * */
			AttachPrincipalPolicyRequest attachPrincipalPolicyRequest=new  AttachPrincipalPolicyRequest();
			attachPrincipalPolicyRequest.setPolicyName(policyName);
			attachPrincipalPolicyRequest.setPrincipal(certificateARN);
			
			/** Attaching the policy to certificate */
			attachPrincipalPolicyResult = iotService.attachingPolicyToCertificate(attachPrincipalPolicyRequest);
			
			logger.debug("======Attach policy result ========="+attachPrincipalPolicyResult);
			
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			msg = "Error";
			logger.error("[ERROR] [attachingPolicyToCertificate] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(msg, status);
	}
	
	/**
	 * This method attachingThingToCertificate will attach the Thing to certificate
	 * 
	 * @param thingName
	 * @param certificateARN
	 * @return ResponseEntity
	 */
	@RequestMapping(value = "/attachThingToCertificate/", method = RequestMethod.POST)
	public ResponseEntity<String> attachingThingToCertificate(@RequestParam String thingName,@RequestParam String certificateARN) {
		AttachThingPrincipalResult attachThingPrincipalResult = null;
		HttpStatus status = HttpStatus.OK;
		String msg = "Success";
		try {
			
			/** Creating object for AttachThingPrincipalRequest and setting the Attribute ThingName with  thingName parameter
			 * and setting the Attribute Principal with certificateARN parameter
			 * */
			AttachThingPrincipalRequest attachThingPrincipalRequest=new AttachThingPrincipalRequest();
			attachThingPrincipalRequest.setPrincipal(certificateARN);
			attachThingPrincipalRequest.setThingName(thingName);
			
			/** Attaching the Thing to certificate */
			attachThingPrincipalResult = iotService.attachingThingToCertificate(attachThingPrincipalRequest);
			
			logger.debug("======Attach Thing result ========="+attachThingPrincipalResult);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			msg = "Error";
			logger.error("[ERROR] [attachingThingToCertificate] [Controller Layer]"+e);
		}

		return new ResponseEntity<>(msg, status);
	}

}
