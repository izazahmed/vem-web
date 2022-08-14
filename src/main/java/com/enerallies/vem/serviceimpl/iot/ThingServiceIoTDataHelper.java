package com.enerallies.vem.serviceimpl.iot;

import java.nio.ByteBuffer;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
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
import com.amazonaws.services.iot.model.DeleteThingRequest;
import com.amazonaws.services.iot.model.InternalFailureException;
import com.amazonaws.services.iot.model.InvalidRequestException;
import com.amazonaws.services.iot.model.LimitExceededException;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.ListThingsResult;
import com.amazonaws.services.iot.model.ResourceAlreadyExistsException;
import com.amazonaws.services.iot.model.ResourceNotFoundException;
import com.amazonaws.services.iot.model.ServiceUnavailableException;
import com.amazonaws.services.iot.model.ThingAttribute;
import com.amazonaws.services.iot.model.ThrottlingException;
import com.amazonaws.services.iot.model.UnauthorizedException;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.GetThingShadowRequest;
import com.amazonaws.services.iotdata.model.GetThingShadowResult;
import com.enerallies.vem.util.ConfigurationUtils;

@Component(value="thingServiceIoTDataHelper")
public class ThingServiceIoTDataHelper implements InitializingBean{

	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(ThingServiceIoTDataHelper.class);
	/** creating the credentials object to authenticate AWS Services */
	BasicAWSCredentials credentials = new BasicAWSCredentials(ConfigurationUtils.getConfig("AWS_ACCESS_KEY_ID"),
			ConfigurationUtils.getConfig("AWS_SECRET_ACCESS_KEY"));

	/** Creating iotClient */
	AWSIotClient iotClient = null;
	

	/** Creating region */
	Region region = Region.getRegion(Regions.US_EAST_1);


	/**
	 * Method is for to initialize MQTT and AWS IoT clients.
	 */
	@PostConstruct
	private void initApp(){

		try {
			/** Creating AWS IoT client */
			iotClient = new AWSIotClient(credentials);

			/** setting region for device thing */
			iotClient.setRegion(region);

			logger.debug("AWS IOT client loaded successfully to register thing");

			//InitApp.initializeLoginTimer();
			/** Scheduler for fetching new devices list and query for live data and publish to aws iot */
		} catch (Exception e) {
			logger.error("Error while initializing :"+e);
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	/** Get the device shadow of device using thing name or device id */

	public String getDeviceShadowState(String thingName) throws Exception {

		/** Set credentials */
		AWSCredentials cred = new AWSCredentials() {

			public String getAWSSecretKey() {
				return ConfigurationUtils.getConfig("AWS_SECRET_ACCESS_KEY");
			}

			public String getAWSAccessKeyId() {
				return ConfigurationUtils.getConfig("AWS_ACCESS_KEY_ID");
			}
		};

		String s = "";
		try{
			AWSIotDataClient client = new AWSIotDataClient(cred);

			GetThingShadowRequest req = new GetThingShadowRequest();
			client.setEndpoint(ConfigurationUtils.getConfig("AWS_IOT_MQTT_HOST"));

			client.setRegion(Region.getRegion(Regions.US_EAST_1));
			req.setRequestCredentials(cred);

			GetThingShadowResult res = client.getThingShadow(req.withThingName(thingName));

			ByteBuffer b = res.getPayload();

			while(b.hasRemaining()){
				char ch = (char) b.get();
				s = s+""+ch;
			}

		}catch (Exception e) {
			logger.info("Thing state failed for the thing name:"+thingName+" and state :"+s+" exception :"+e.getMessage());
			s = null;
		}
		logger.info("Thing state received for the thing name:"+thingName+" and state :"+s);
		return s;
	}

	/**
	 * This we are not implementing as of now
	 *  (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */

	/** Method to created thing using AWS IOT sdk method */
	public CreateThingResult createThing(CreateThingRequest thingRequest) throws ResourceAlreadyExistsException {
		/** thing result object */
		CreateThingResult thingResult = null;
		try {

			/** Creates a thing in the Thing Registry. */
			thingResult = iotClient.createThing(thingRequest);

		} catch (InternalFailureException | InvalidRequestException | ResourceAlreadyExistsException
				| ServiceUnavailableException | ThrottlingException | UnauthorizedException e) {
			logger.error("Failed thing creation "+e);
			throw e;
		} catch (Exception e) {
			throw e;
		}

		logger.debug("Thing Created successfully");
		return thingResult;
	}



	public CreatePolicyResult createPolicy(CreatePolicyRequest createPolicyRequest) throws ResourceAlreadyExistsException {
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



	public CreateKeysAndCertificateResult generateRSAKeyPairAndCertificate(
			CreateKeysAndCertificateRequest createKeysAndCertificateRequest) throws ResourceAlreadyExistsException {
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
		}
		return createKeysAndCertificateResult;
	}



	public AttachPrincipalPolicyResult attachingPolicyToCertificate(
			AttachPrincipalPolicyRequest attachPrincipalPolicyRequest) throws ResourceAlreadyExistsException {
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
		}
		return attachPrincipalPolicyResult;
	}


	public AttachThingPrincipalResult attachingThingToCertificate(
			AttachThingPrincipalRequest attachThingPrincipalRequest) throws ResourceAlreadyExistsException {
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
		}
		return attachThingPrincipalResult;
	}

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


	public int validateDevRegAWSIOT(String macId){

		int result = 0;
		logger.info("===$$$ Validating device registered in AWS IOT");
		ListThingsRequest things = new ListThingsRequest();

		things.clone();
		things.setAttributeName("macId");
		things.setAttributeValue(macId);
		ListThingsResult thingResult = iotClient.listThings(things);

		List<ThingAttribute> list = thingResult.getThings();
		logger.info("test"+list.size());
		
		if(!list.isEmpty()){
			result = 1;
			logger.info("===$$$ Device is registered in AWS IOT : Validation is completed and it is success. mac:"+macId);
		}else{
			logger.info("===$$$ Device is not registered in AWS IOT : Validation is completed and it is failed. mac:"+macId);
		}
		return result;
	}
	/*	public static void main(String[] args) {
	 ThingServiceIoTDataHelper data = new ThingServiceIoTDataHelper();
	 data.iotClient = new AWSIotClient(data.credentials);
	 data.iotClient.setRegion(data.region);

	 ListThingsRequest things = new ListThingsRequest();

	 things.clone();
	 things.setAttributeName("name");
	 things.setAttributeValue("XCSpecTstat");
	 ListThingsResult result = data.iotClient.listThings(things);

	 List<ThingAttribute> list = result.getThings();
	 System.out.println("test"+data.iotClient.getServiceName());
	 System.out.println("test"+list.size());
	 for (int i = 0; i < list.size(); i++) {

		System.out.println(list.get(i).getThingName());
	}
	}*/

}
