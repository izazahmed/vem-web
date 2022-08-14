/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.service.iot;

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
import com.amazonaws.services.iot.model.TopicRulePayload;
import com.enerallies.vem.beans.iot.DeviceData;

/**
 * File Name : IoTService 
 * 
 * IoTService: is used declare all the IoT operation methods
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
 *
 */

public interface IoTService {

	/**
	 * createThing: Creates a thing in the Thing Registry.
	 * 
	 * @param thingRequest
	 * @return CreateThingResult
	 * @throws Exception
	 */
	public CreateThingResult createThing(CreateThingRequest thingRequest) throws Exception;

	/**
	 * deleteThing: Deletes the specified thing from the Thing Registry.
	 * @param deleteThingRequest
	 * @throws Exception
	 */
	public void deleteThing(DeleteThingRequest deleteThingRequest) throws Exception;
	
	/**
	 * Returns a unique end point specific to the AWS account making the call.
	 * 
	 * @param describeEndpointRequest
	 * @return DescribeEndpointResult
	 * @throws Exception
	 */
	public DescribeEndpointResult describeEndpoint(DescribeEndpointRequest describeEndpointRequest) throws Exception;
	
	/**
	 * Publish the device data to IoT.
	 * 
	 * @param deviceData
	 * @throws Exception
	 */
	public void publishMessages(DeviceData deviceData) throws Exception;
	
	/**
	 * Subscribes for messages.
	 * 
	 * @param topicName
	 * @throws Exception
	 */
	public void subscribeMessages(String topicName) throws Exception;
	
	/**
	 * Get the entire shadow document
	 * 
	 * @param thingName
	 * @return String
	 * @throws Exception
	 */
	public String getDeviceShadowState(String thingName) throws Exception;
	
	/**
	 * Delete existing shadow document
	 * 
	 * @param thingName
	 * @throws Exception
	 */
	public void updateDeviceShadow(String thingName) throws Exception;
	
	/**
	 * Update shadow document
	 * 
	 * @param thingName
	 * @throws Exception
	 */
	public void deleteDeviceShadow(String thingName) throws Exception;
	
	/**
	 * Creates an AWS IoT policy.
	 * 
	 * @param createPolicyRequest
	 * @return
	 * @throws Exception
	 */
	public CreatePolicyResult createPolicy(CreatePolicyRequest createPolicyRequest) throws Exception;
	
	/**
	 * Creates a rule. If the action is successful, the service sends back an HTTP 200 response with an empty HTTP body.
	 *
	 * @param createTopicRuleRequest
	 * @throws Exception
	 */
	public void createTopicRule(CreateTopicRuleRequest createTopicRuleRequest) throws Exception;
	
	/**
	 * Creates the configurable topic rule
	 * 
	 * @param ruleName
	 * @param payloadType
	 * @return
	 * @throws Exception
	 */
	public CreateTopicRuleRequest createConfigurableRuleRequest(String ruleName, TopicRulePayload payloadType) throws Exception;
	
	/**
	 * This method will generate a 2048-bit RSA key pair and issues an X.509 certificate
	 * 
	 * @param CreateKeysAndCertificateRequest
	 * @return CreateKeysAndCertificateResult
	 * @throws Exception
	 */
	public CreateKeysAndCertificateResult generateRSAKeyPairAndCertificate(CreateKeysAndCertificateRequest createKeysAndCertificateRequest) throws Exception;
	
	/**
	 * This method attachingPolicyToCertificate will attach the policy to certificate
	 * 
	 * @param AttachPrincipalPolicyRequest
	 * @return AttachPrincipalPolicyResult
	 * @throws Exception
	 */
	public AttachPrincipalPolicyResult attachingPolicyToCertificate(AttachPrincipalPolicyRequest attachPrincipalPolicyRequest) throws Exception;
	
	/**
	 * This method attachingThingToCertificate will attach the Thing to certificate
	 * 
	 * @param AttachThingPrincipalRequest
	 * @return AttachThingPrincipalResult
	 * @throws Exception
	 */
	public AttachThingPrincipalResult attachingThingToCertificate(AttachThingPrincipalRequest attachThingPrincipalRequest) throws Exception;
	
}

