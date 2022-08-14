package com.enerallies.vem.serviceimpl.iot;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.model.CreateThingRequest;
import com.amazonaws.services.iot.model.CreateThingResult;
import com.amazonaws.services.iot.model.DeleteThingRequest;
import com.amazonaws.services.iot.model.DeleteThingResult;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.beans.iot.DeviceStatusRequest;
import com.enerallies.vem.beans.iot.DisconnectDeviceRequest;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.iot.ThingUpdateRequest;
import com.enerallies.vem.business.WundergroundBusiness;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.dao.weather.WeatherDao;
import com.enerallies.vem.daoimpl.alert.AlertDaoImpl;
import com.enerallies.vem.daoimpl.schedule.ScheduleDaoImpl;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.listeners.iot.awsiot.InitApp;
import com.enerallies.vem.listeners.xcspec.RestClient;
import com.enerallies.vem.processing.TStatCache;
import com.enerallies.vem.service.iot.ThingService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.iot.XCSPECDataParser;

@Component(value="thingServiceCRUDHelper")
public class ThingServiceCRUDHelper {

	/** Auto wiring instance of IoTDao  */
	@Autowired
	private IoTDao ioTDao;

	/** Auto wiring instance of AuditDao  */
	@Autowired
	private AuditDAO auditDao;


	/** Auto wiring instance of WundergroundBuisness  */
	@Autowired
	private WundergroundBusiness wundergroundBusiness;

	@Autowired
	WeatherDao weatherDao;

	@Autowired
	ScheduleDaoImpl scheduleDaoImpl;

	@Autowired
	XCSPECDataParser xcspecDataParser;

	@Autowired
	ThingServiceIoTDataHelper ioTDataHelper;
	
	@Autowired
	AlertDaoImpl alertDao;

	@Autowired
	private ThingService thingService;
	/** Scheduler to get the device from DB  */
	Timer queryDBTimer;

	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(ThingServiceCRUDHelper.class);

	/**Rest client class reference to call XCSPEC specific REST API's*/
	RestClient restClient = new RestClient();


	public Response registerDevPro1AWSComp(Thing thing, Response resp) {
		int storeResult;
		logger.info("Registering AWS IOT compatible device");
		try{
			ThingResponse thingRespCheck = ioTDao.getThingInfo(2,0,thing.getMacId());

			if(thingRespCheck==null){

				java.util.Date date = new Date();
				Timestamp timestamp = new Timestamp(date.getTime());

				thing.setCreatedOn(timestamp.toString());
				thing.setIsActive(1);
				thing.setUpdatedBy(thing.getCreatedBy());
				storeResult = ioTDao.saveThingInfo(thing);
				if(storeResult<=0){
					ThingResponse thingResp = ioTDao.getThingInfo(2,0,thing.getMacId());

					resp.setCode(ErrorCodes.ERROR_SQL_SAVE_DEVICE);
					resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());

					resp.setData(thingResp);
				}else{

					ThingResponse savedData = ioTDao.getThingInfo(2, 0, thing.getMacId());

					resp = formAndUpdateEaiDeviceId(savedData);

					if(thing.getRegisterType()==1){
						Thing thingToCache = new Thing();
						thingToCache.setDeviceId(savedData.getDeviceId());
						thingToCache.setCustomerId(savedData.getCustomerId());
						thingToCache.setRegisterType(savedData.getRegisterType());
						thingToCache.setMacId(savedData.getMacId());
						thingToCache.setSiteId(savedData.getSiteId());

						TStatCache.addThermostatToCache(thingToCache);
					}

					/*resp.setCode(ErrorCodes.SUCCESS_REGISTER_DEVICE);
					resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					resp.setData(savedData);*/
					
					
					String jsonStringState = ioTDataHelper.getDeviceShadowState(savedData.getMacId());
					
					JSONParser parser = new JSONParser();
					
					org.json.simple.JSONObject json =(org.json.simple.JSONObject)parser.parse(jsonStringState);
					org.json.simple.JSONObject stateBody = (org.json.simple.JSONObject)json.get("state");
					org.json.simple.JSONObject objObj = new org.json.simple.JSONObject();
					//org.json.simple.JSONObject stateBody = (org.json.simple.JSONObject)thing.getShadowState();

					org.json.simple.JSONObject reported = (org.json.simple.JSONObject)stateBody.get("reported");
					
					objObj.put("reported", reported);
					
					logger.info("Sending data to convert JSON reported :"+reported);
					
					String respStr = scheduleDaoImpl.getDeviceJsonResponse(objObj.toJSONString(), savedData.getMacId());
					// to insert custom schedule
				    scheduleDaoImpl.addCustomSchedule(objObj.toJSONString(), savedData.getDeviceId());
					org.json.simple.JSONObject convertedObj = (org.json.simple.JSONObject)parser.parse(respStr);
					
					convertedObj.put("macId", savedData.getMacId());
					convertedObj.put("success", false);
					convertedObj.put("message", "");
					convertedObj.put("xcspec_device_id", "");
					
					AlertRequest alertRequest = new AlertRequest();
					alertRequest.setDeviceJSON(convertedObj.toJSONString());
					
					logger.info("Sending data to alert module :"+convertedObj);
					alertDao.storeDeviceStatus(alertRequest);
					logger.info("Sent data to alert module :"+convertedObj);
				}
			}else{
				resp.setCode(ErrorCodes.ERROR_SQL_SAVE_DEVICE);
				resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				resp.setData(thingRespCheck);
			}
		} catch (VEMAppException | SQLException e) {

			logger.error("Error found while storing the Thing "+e.getMessage());
		} catch (Exception e) {
			logger.error("Exception while Iterating for the thing state "+e.getMessage());
		}

		return resp;
	}

	public Response registerDevPro1NonAWSComp(Thing thing, Response resp){
		/**sql store results*/
		int storeResult;
		logger.info("Registering NON AWS IOT compatible device");
		/**Check if registration type is to connect or simply register with vem2.0*/
		if(thing.getRegisterType()==1){

			CreateThingResult result;

			/**creating JSON object to register with xcspec*/
			JSONObject requestBodyJSON = new JSONObject();
			requestBodyJSON.put("mac_address", thing.getMacId());
			requestBodyJSON.put("model", thing.getModel());
			requestBodyJSON.put("version",thing.getVersion());
			requestBodyJSON.put("name", thing.getName());
			requestBodyJSON.put("sit_id", ""+thing.getSiteId());
			requestBodyJSON.put("user_id", ""+thing.getCreatedBy());

			/** Call to register with XCSPEC */
			String responseStringJSON = restClient.createThermostat(requestBodyJSON.toString());

			JSONObject liveDataObj = new JSONObject(responseStringJSON);
			if("Must login or provide credentials.".equals((String)liveDataObj.get("message"))){
				restClient.loginToGetauthStringEnc();
				responseStringJSON = restClient.createThermostat(requestBodyJSON.toString());
			}

			JSONObject responseJSON = new JSONObject(responseStringJSON);

			try{
				if(responseJSON.getBoolean("success")){

					String deviceId = responseJSON.getString("xcspecDevId");

					if(deviceId!=null){

						thing.setXcspecDeviceId(deviceId);

						CreateThingRequest req = new CreateThingRequest();
						req.setThingName(thing.getMacId());


						/** Register with AWS IOT */
						result = ioTDataHelper.createThing(req);

						if(result!=null){
							thing.setThingARN(result.getThingArn());

							try {
								java.util.Date date = new Date();
								Timestamp timestamp = new Timestamp(date.getTime());

								thing.setCreatedOn(timestamp.toString());
								thing.setIsActive(1);

								thing.setUpdatedBy(thing.getCreatedBy());
								thing.setUpdatedOn(thing.getCreatedOn());
								storeResult = ioTDao.saveThingInfo(thing);
								if(storeResult<=0){
									resp.setCode(ErrorCodes.ERROR_SQL_SAVE_DEVICE);
									resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
								}else{

									ThingResponse savedData = ioTDao.getThingInfo(2, 0, thing.getMacId());

									resp = formAndUpdateEaiDeviceId(savedData);
									InitApp.queryAndPublishData(savedData.getXcspecDeviceId(), savedData.getMacId());

								}
							} catch (VEMAppException e) {

								logger.error("Error found while storing the Thing "+e.getMessage());
							}
						}else{
							resp.setCode(ErrorCodes.ERROR_AWS_IOT_REGISTER_DEVICE);
							resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						}

					}else{
						resp.setCode(ErrorCodes.ERROR_XCSPEC_UNAUTHORISED);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					}


				}else{
					if("Must login or provide credentials.".equals(responseJSON.getString("message"))){
						resp.setCode(ErrorCodes.ERROR_XCSPEC_UNAUTHORISED);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					}else if("Device with this mac address already exist".equals(responseJSON.getString("message"))){

						ThingResponse thingResp;
						try {
							thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());
							resp.setCode(ErrorCodes.ERROR_XCSPEC_ALREADY_REG);
							resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							resp.setData(thingResp);
						} catch (SQLException e) {
							logger.error("Error found while fetching the Thing info "+e.getMessage());
						}

					}

				}

			}catch(Exception e){
				logger.error("Error found while registering the Thing info "+e.getMessage());
				resp.setCode(ErrorCodes.ERROR_AWS_IOT_REGISTER_DEVICE);
				resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
		}else{		//register type if ends here
			try {

				ThingResponse thingRespCheck = ioTDao.getThingInfo(2,0,thing.getMacId());

				if(thingRespCheck==null){
					thing.setXcspecDeviceId(null);
					thing.setThingARN(null);


					java.util.Date date = new Date();
					Timestamp timestamp = new Timestamp(date.getTime());

					thing.setCreatedOn(timestamp.toString());
					thing.setIsActive(0);
					thing.setUpdatedBy(thing.getCreatedBy());
					storeResult = ioTDao.saveThingInfo(thing);
					if(storeResult<=0){
						ThingResponse thingResp = ioTDao.getThingInfo(2,0,thing.getMacId());

						resp.setCode(ErrorCodes.ERROR_SQL_SAVE_DEVICE);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());

						resp.setData(thingResp);
					}else{
						ThingResponse savedData = ioTDao.getThingInfo(2, 0, thing.getMacId());
						resp = formAndUpdateEaiDeviceId(savedData);
					}
				}else{
					resp.setCode(ErrorCodes.ERROR_SQL_SAVE_DEVICE);
					resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					resp.setData(thingRespCheck);
				}
			} catch (VEMAppException | SQLException e) {

				logger.error("Error found while storing the Thing "+e.getMessage());
			}
		}
		return resp;
	}

	public Response formAndUpdateEaiDeviceId(ThingResponse savedData) {
		Response resp = new Response();

		//String eai_device_id = savedData.getSiteCode()+"-0"+savedData.getDeviceType()+"-"+savedData.getDeviceId();
		String eai_device_id = savedData.getCustomerCode()+"-"+savedData.getSiteId()+"-0"+savedData.getDeviceType()+"-"+savedData.getDeviceId();
		savedData.setEaiDeviceId(eai_device_id);
		Thing thing = new Thing();
		thing.setDeviceId(savedData.getDeviceId());
		thing.setEaiDeviceId(eai_device_id);
		thing.setUpdatedBy(savedData.getUpdatedBy());

		try {
			//passing 1 for updating device eai device id in device table 
			int result =ioTDao.updateEaiDeviceIdDevice(thing);
			if(result>0){
				resp.setCode(ErrorCodes.SUCCESS_REGISTER_DEVICE);
				resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				resp.setData(savedData);
			}else{
				resp.setCode(ErrorCodes.SUCCESS_REGISTER_DEVICE_BUT_NOT_UPDATED_EAI_ID);
				resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				resp.setData(savedData);
			}


		} catch (VEMAppException e) {
			resp.setCode(ErrorCodes.ERROR_UPDATING_EAI_DEV_ID_DEVICE);
			resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			resp.setData(savedData);
		}

		return resp;
	}

	public Response updateDevPro1AWSComp(ThingUpdateRequest thing, Response resp) {

		logger.info("Updating device AWS IOT compatible");
		/**sql store results*/
		int storeResult;

		//thing response object on mac id
		ThingResponse thingRespToCheckReg;
		try{

			thingRespToCheckReg=  ioTDao.getThingInfo(2, 0, thing.getMacId());

			/**Check if registration type is to connect or simply register with vem2.0*/

			//checking if the device is there in db or not 
			if(thingRespToCheckReg==null){
				try{
					storeResult = ioTDao.updateDevice(thing);
					ThingResponse thingResp;
					if(storeResult<=0){
						thingResp = ioTDao.getThingInfo(2, 0 , thing.getMacId());

						resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());

						resp.setData(thingResp);
					}else{
						thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());

						Thing thingToCache = new Thing();
						thingToCache.setDeviceId(thingResp.getDeviceId());
						thingToCache.setCustomerId(thingResp.getCustomerId());
						thingToCache.setRegisterType(thingResp.getRegisterType());
						thingToCache.setMacId(thingResp.getMacId());
						thingToCache.setSiteId(thingResp.getSiteId());
						TStatCache.updateThermostatToCache(thingToCache);

						formAndUpdateEaiDeviceId(thingResp);
						resp.setCode(ErrorCodes.SUCCESS_UPDATE_DEVICE);
						resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						resp.setData(thingResp);
					}
				} catch (SQLException | VEMAppException e) {
					resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
					resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					logger.error("Error found while updating the device "+e.getMessage());
				}

			}else{ // end of checking if the device is there in db or not null or not

				if(thing.getDeviceId()==thingRespToCheckReg.getDeviceId()){
					//check again whether registered with xcspec and aws
					try {

						storeResult = ioTDao.updateDevice(thing);
						ThingResponse thingResp;
						if(storeResult<=0){
							thingResp = ioTDao.getThingInfo(2, 0 , thing.getMacId());


							resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
							resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());

							resp.setData(thingResp);
						}else{
							thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());

							Thing thingToCache = new Thing();
							thingToCache.setDeviceId(thingResp.getDeviceId());
							thingToCache.setCustomerId(thingResp.getCustomerId());
							thingToCache.setRegisterType(thingResp.getRegisterType());
							thingToCache.setMacId(thingResp.getMacId());
							thingToCache.setSiteId(thingResp.getSiteId());
							TStatCache.updateThermostatToCache(thingToCache);

							formAndUpdateEaiDeviceId(thingResp);
							resp.setCode(ErrorCodes.SUCCESS_UPDATE_DEVICE);
							resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
							resp.setData(thingResp);
						}
					} catch (SQLException | VEMAppException e) {
						resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						logger.error("Error found while updating the device "+e.getMessage());
					}

				}else{
					resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE_ALREADY_INVEM);
					resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					resp.setData(thingRespToCheckReg);
					logger.error("Device already registered in VEM ");

				}
			}

		} catch (Exception e) {
			resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			resp.setCode(ErrorCodes.GENERAL_APP_ERROR);
			logger.error("[ERROR] [ThingServiceImpl] [updateDevice]"+e);
		}
		return resp;
	}

	public Response updateDevPro1NonAWSComp(ThingUpdateRequest thing, Response resp) {

		logger.info("Updating device Non AWS IOT compatible Pro1");
		/**sql store results*/
		int storeResult;

		try {
			//thing response object on mac id
			ThingResponse thingRespToCheckReg;
			thingRespToCheckReg=  ioTDao.getThingInfo(2, 0, thing.getMacId());

			/**Check if registration type is to connect or simply register with vem2.0*/
			if(thing.getRegisterType()==1){

				//checking if the device is there in db or not 
				if(thingRespToCheckReg==null){

					resp = updateDevRegXCSPECNAWS(thing);

				}else{ // end of checking if the device is there in db or not null or not

					if(thing.getDeviceId()==thingRespToCheckReg.getDeviceId()){
						//check again whether registered with xcspec and aws
						if(thingRespToCheckReg.getRegisterType()==1){
							try {

								thing.setXcspecDeviceId(thingRespToCheckReg.getXcspecDeviceId());
								thing.setThingARN(thingRespToCheckReg.getThingARN());
								//thing.setIsActive(1);
								//passing 2 for updating device info complete 
								storeResult = ioTDao.updateDevice(thing);
								ThingResponse thingResp;
								if(storeResult<=0){
									thingResp = ioTDao.getThingInfo(2, 0 , thing.getMacId());

									resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
									resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());

									resp.setData(thingResp);
								}else{
									thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());

									resp =formAndUpdateEaiDeviceId(thingResp);
									resp.setCode(ErrorCodes.SUCCESS_UPDATE_DEVICE);
									resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
									InitApp.queryAndPublishData(thingResp.getXcspecDeviceId(), thingResp.getMacId());
								}
							} catch (SQLException | VEMAppException e) {
								resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
								resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
								logger.error("Error found while updating the device "+e.getMessage());
							}
						}else{
							resp = updateDevRegXCSPECNAWS(thing);
						}
					}else{
						resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE_ALREADY_INVEM);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						resp.setData(thingRespToCheckReg);
						logger.error("Device already registered in VEM ");

					}


				}// end of checking if else the device is there in db or not null or not

			}else{		//register type if ends here

				if(thingRespToCheckReg==null){
					try {

						thing.setXcspecDeviceId(null);
						thing.setThingARN(null);
						//thing.setIsActive(0);
						//passing 2 for updating device info complete 
						storeResult = ioTDao.updateDevice(thing);
						ThingResponse thingResp;
						if(storeResult<=0){
							thingResp = ioTDao.getThingInfo(2, 0 , thing.getMacId());

							resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
							resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());

							resp.setData(thingResp);
						}else{
							thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());

							resp =formAndUpdateEaiDeviceId(thingResp);
							resp.setCode(ErrorCodes.SUCCESS_UPDATE_DEVICE);
							resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						}
					} catch (SQLException | VEMAppException e) {
						resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						logger.error("Error found while updating the device "+e.getMessage());
					}
				}else{
					if(thing.getDeviceId()==thingRespToCheckReg.getDeviceId()){
						if(thingRespToCheckReg.getRegisterType()==1){
							DisconnectDeviceRequest disconnectDevice = new DisconnectDeviceRequest();
							disconnectDevice.setDeviceId(thingRespToCheckReg.getDeviceId());
							disconnectDevice.setMacId(thingRespToCheckReg.getMacId());
							disconnectDevice.setXcspecDeviceId(thingRespToCheckReg.getXcspecDeviceId());

							try {
								resp = thingService.disconnectDevice(disconnectDevice, thing.getUpdatedBy());

								if(resp.getStatus().equals("SUCCESS")){
									try {

										thing.setXcspecDeviceId(null);
										thing.setThingARN(null);
										//thing.setIsActive(0);
										//passing 2 for updating device info complete 
										storeResult = ioTDao.updateDevice(thing);
										ThingResponse thingResp;
										if(storeResult<=0){
											thingResp = ioTDao.getThingInfo(2, 0 , thing.getMacId());

											resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
											resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());

											resp.setData(thingResp);
										}else{
											thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());

											resp =formAndUpdateEaiDeviceId(thingResp);
											resp.setCode(ErrorCodes.SUCCESS_UPDATE_DEVICE);
											resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
										}
									} catch (SQLException | VEMAppException e) {
										resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
										resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
										logger.error("Error found while updating the device "+e.getMessage());
									}
								}
							} catch (VEMAppException e) {
								resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
								resp.setCode(ErrorCodes.GENERAL_APP_ERROR);
							}
						}else{
							try {

								thing.setXcspecDeviceId(null);
								thing.setThingARN(null);
								//thing.setIsActive(0);
								//passing 2 for updating device info complete 
								storeResult = ioTDao.updateDevice(thing);
								ThingResponse thingResp;
								if(storeResult<=0){
									thingResp = ioTDao.getThingInfo(2, 0 , thing.getMacId());

									resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
									resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());

									resp.setData(thingResp);
								}else{
									thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());

									resp =formAndUpdateEaiDeviceId(thingResp);
									resp.setCode(ErrorCodes.SUCCESS_UPDATE_DEVICE);
									resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
								}
							} catch (SQLException | VEMAppException e) {
								resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
								resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
								logger.error("Error found while updating the device "+e.getMessage());
							}
						}					
					}else{
						resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE_ALREADY_INVEM);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						resp.setData(thingRespToCheckReg);
						logger.error("Device already registered in VEM ");

					}

				}



			}//register type else ends here

		} catch (SQLException e1) {
			logger.error("Error found while checking device info in DB "+e1.getMessage());
			resp.setCode(ErrorCodes.ERROR_SQL_FETCH_DEVICE_MACID);
			resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
		}
		return resp;
	}

	public Response updateDevRegXCSPECNAWS(ThingUpdateRequest thing){
		Response resp = new Response();
		int storeResult;
		CreateThingResult result;

		/**creating JSON object to register with xcspec*/
		JSONObject requestBodyJSON = new JSONObject();
		requestBodyJSON.put("mac_address", thing.getMacId());
		requestBodyJSON.put("model", thing.getModel());
		requestBodyJSON.put("version",thing.getVersion());
		requestBodyJSON.put("name", thing.getName());
		requestBodyJSON.put("sit_id", ""+thing.getSiteId());
		requestBodyJSON.put("user_id", ""+thing.getUpdatedBy());

		try{
			/** Call to register with XCSPEC */
			String responseStringJSON = restClient.createThermostat(requestBodyJSON.toString());

			JSONObject liveDataObj = new JSONObject(responseStringJSON);
			if("Must login or provide credentials.".equals((String)liveDataObj.get("message"))){
				restClient.loginToGetauthStringEnc();
				responseStringJSON = restClient.createThermostat(requestBodyJSON.toString());
			}


			JSONObject responseJSON = new JSONObject(responseStringJSON);

			try{
				if(responseJSON.getBoolean("success")){

					String deviceId = responseJSON.getString("xcspecDevId");

					if(deviceId!=null){

						thing.setXcspecDeviceId(deviceId);

						/**creating thingrequest object to register with AWS IOT*/
						CreateThingRequest req = new CreateThingRequest();
						req.setThingName(thing.getMacId());


						/** Register with AWS IOT */
						result = ioTDataHelper.createThing(req);

						if(result!=null){
							thing.setThingARN(result.getThingArn());

							try {
								thing.setIsActive(1);
								//passing 2 for updating device info complete 
								storeResult = ioTDao.updateDevice(thing);
								ThingResponse thingResp;
								if(storeResult<=0){
									resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);					
									thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());
									resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
									resp.setData(thingResp);
								}else{

									thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());

									resp =formAndUpdateEaiDeviceId(thingResp);

									resp.setCode(ErrorCodes.SUCCESS_UPDATE_DEVICE);
									resp.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
								}
							} catch (SQLException e) {
								resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
								resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
								logger.error("Error found while updating the device "+e.getMessage());
							}
						}else{
							resp.setCode(ErrorCodes.ERROR_AWS_IOT_REGISTER_DEVICE);
							resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						}

					}else{
						resp.setCode(ErrorCodes.ERROR_XCSPEC_UNAUTHORISED);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					}


				}else{
					if("Must login or provide credentials.".equals(responseJSON.getString("message"))){
						resp.setCode(ErrorCodes.ERROR_XCSPEC_UNAUTHORISED);
						resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					}else if("Device with this mac address already exist".equals(responseJSON.getString("message"))){

						ThingResponse thingResp = null;
						try {
							thingResp = ioTDao.getThingInfo(2, 0, thing.getMacId());
							resp.setCode(ErrorCodes.ERROR_XCSPEC_ALREADY_REG);
							resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							resp.setData(thingResp);
						} catch (SQLException e) {
							resp.setCode(ErrorCodes.ERROR_SQL_UPDATE_DEVICE);
							resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							resp.setData(thingResp);
							logger.error("Error found while fetching the Thing info "+e.getMessage());
						}

					}

				}
			}catch(Exception e){
				logger.error("Error found while registering the Thing info "+e.getMessage());
				resp.setCode(ErrorCodes.ERROR_AWS_IOT_REGISTER_DEVICE);
				resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
		} catch (Exception e) {
			resp.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			resp.setCode(ErrorCodes.GENERAL_APP_ERROR);
			logger.error("[ERROR] [ThingServiceImpl] [updateDevRegXCSPECNAWS]"+e);
		}

		return resp;

	}

	public Response deleteDevPro1AWSComp(int deviceId, int userId, Response response) throws VEMAppException{
		logger.info("Deleting device AWS compatilble");
		try{
			DisconnectDeviceRequest disconnectDevice = new DisconnectDeviceRequest();

			disconnectDevice.setDeviceId(deviceId);

			TStatCache.deleteThermostatFromCache(deviceId);

			int regTypeStatus = ioTDao.disconnectDevice(disconnectDevice, userId);

			if(regTypeStatus >= 1){
				int status = ioTDao.deleteDevice(deviceId, userId);

				/* if status is 1 or greater means the device delete  request is
				 *  success
				 *  else fail.
				 */
				if(status >= 1){

					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.SUCCESS_DELETE_DEVICE);
					response.setData(status);
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_DELETE_DEVICE_FAILED);
					response.setData(CommonConstants.ERROR_OCCURRED+":Device delete failed in db.");
				}
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_UPDATE_DEVICE_DISCONNECT);
				response.setData(CommonConstants.ERROR_OCCURRED+":Device disconnect update failed in db.");
				//Catches when all the server or bean level validations are true.
			}
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_DELETE_DEVICE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_DELETE_DEVICE_FAILED, logger, e);
		}
		return response;
	}

	public Response deleteDevPro1NonAWSComp(int deviceId, int userId, Response response, ThingResponse thingResponse) throws VEMAppException{

		logger.info("Deleting device NON AWS compatilble");
		try {
		//check if device is registered. if registered then disconnect
		if(thingResponse.getRegisterType()==1){
			DisconnectDeviceRequest disconnectDevice = new DisconnectDeviceRequest();
			disconnectDevice.setDeviceId(thingResponse.getDeviceId());
			disconnectDevice.setMacId(thingResponse.getMacId());
			disconnectDevice.setXcspecDeviceId(thingResponse.getXcspecDeviceId());


			response = thingService.disconnectDevice(disconnectDevice, thingResponse.getUpdatedBy());
			if(response!=null){
			if(response.getStatus().equals("SUCCESS")){
				
					//Catches when all the server or bean level validations are true.
					int status = ioTDao.deleteDevice(deviceId, userId);

					/* if status is 1 or greater means the device delete  request is
					 *  success
					 *  else fail.
					 */
					if(status >= 1){
						//delete thing shadow. or remove cache or remove last recorded data
						InitApp.publishMessage("$aws/things/"+thingResponse.getMacId()+"/shadow/delete", "");

						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.SUCCESS_DELETE_DEVICE);
						response.setData(status);
					}else{
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.ERROR_DELETE_DEVICE_FAILED);
						response.setData(CommonConstants.ERROR_OCCURRED+":Device delete failed in db.");
					}
				

			}
			
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_DELETE_DEVICE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED);
			}
			
		}else{
			try {
				//Catches when all the server or bean level validations are true.
				int status = ioTDao.deleteDevice(deviceId, userId);

				/* if status is 1 or greater means the device delete  request is
				 *  success
				 *  else fail.
				 */
				if(status >= 1){
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.SUCCESS_DELETE_DEVICE);
					response.setData(status);
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_DELETE_DEVICE_FAILED);
					response.setData(CommonConstants.ERROR_OCCURRED+":Device delete failed in db.");
				}
			}catch (Exception e) {
				//Creating and throwing the customized exception.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_DELETE_DEVICE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED);
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_DELETE_DEVICE_FAILED, logger, e);
			}

		}

		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_DELETE_DEVICE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_DELETE_DEVICE_FAILED, logger, e);
		}
		return response;
	}


}
