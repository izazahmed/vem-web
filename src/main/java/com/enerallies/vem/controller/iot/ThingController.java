/**
 * 
 */
package com.enerallies.vem.controller.iot;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.services.iot.model.InternalFailureException;
import com.amazonaws.services.iot.model.InvalidRequestException;
import com.amazonaws.services.iot.model.ResourceAlreadyExistsException;
import com.amazonaws.services.iot.model.ServiceUnavailableException;
import com.amazonaws.services.iot.model.ThrottlingException;
import com.amazonaws.services.iot.model.UnauthorizedException;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.iot.DeviceStatusRequest;
import com.enerallies.vem.beans.iot.DisconnectDeviceRequest;
import com.enerallies.vem.beans.iot.SetClockRequest;
import com.enerallies.vem.beans.iot.SetTStatDataRequest;
import com.enerallies.vem.beans.iot.SetTemperatureRequest;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.beans.iot.ThingUpdateRequest;
import com.enerallies.vem.beans.iot.Things;
import com.enerallies.vem.beans.iot.UpdateHeatPumpFieldReq;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.iot.ThingService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;




/**
 * File Name : ThingController 
 * 
 * ThingController: is used to handle all the thing related Requests like register and list things
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        31-08-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	31-08-2016		Rajashekharaiah Muniswamy		File Created
 * 02	31-08-2016		Rajashekharaiah Muniswamy		Added registerThing Method
 * 03   09-09-2016		Rajashekharaiah Muniswamy		Added session
 * 04   19-09-2016		Rajashekharaiah Muniswamy		Added changeDeviceStatus method
 * 05  	20-09-2016		Rajashekharaiah Muniswamy		Added disconnectDevice method
 * 06   26-09-2016		Rajashekharaiah Muniswamy       Added updateDevice method
 * 07   04-10-2016		Rajashekharaiah Muniswamy       Modified updateDevice method
 * 08   06-10-2016		Rajashekharaiah Muniswamy       Modified getThingListByCustomer method fixed permission related issues
 * 09	17-10-2016		Rajashekharaiah Muniswamy		Added setTemp method to set the thermostat temperature
 * 10	18-10-2016		Rajashekharaiah Muniswamy		Added setTStatData method to set the thermostat data
 * 11	19-10-2016		Rajashekharaiah Muniswamy		Added setClock method to set the thermostat clock
 */

@Controller
@RequestMapping("/api/things")
public class ThingController {

	/** Auto wiring instance of ThingService  */
	@Autowired
	private ThingService thingService;
	
	/** Getting logger*/	
	private static final Logger logger = Logger.getLogger(ThingController.class);
	
	/**
	 * @param thing
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/registerThing", method = RequestMethod.POST)
	public ResponseEntity<Response> registerThing(@RequestBody Thing thing, HttpSession session) {
		
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		
		if(userInfo!=null){

			thing.setCreatedBy(userInfo.getUserId());
			
			/** call to creating thing */
			response = thingService.registerThing(thing);
		}else{
			/** set status to unauthorized */
			status = HttpStatus.UNAUTHORIZED;
			
			/** Failure status and code*/
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.INVALID_SESSION);
		}

		return new ResponseEntity<>(response, status);
	}

	/**
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getThingList", method = RequestMethod.GET)
	public ResponseEntity<Response> getThingList(HttpSession session) {

		Response response = new Response(); 
		HttpStatus status;
		
		Things things;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

		try {
			if(userInfo!=null){
			/** call to get thing list service*/
			things = thingService.getThingList();
		
				if(things!=null){
					if(userInfo.getIsEai()==1 || userInfo.getIsSuper()==1){
						things.setPermission(1);
					}else{
						things.setPermission(0);
					}
				
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.SUCCESS_LISTING_DEVICES);
					response.setData(things);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.GENERAL_APP_ERROR);
					status = HttpStatus.OK;
					
				}
			
			}else{
				/** set status to unauthorized */
				status = HttpStatus.UNAUTHORIZED;
				
				/** Failure */
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (InternalFailureException | InvalidRequestException | ResourceAlreadyExistsException
				| ServiceUnavailableException | ThrottlingException | UnauthorizedException e){
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			status = HttpStatus.OK;
			logger.error("[ERROR] [ThingController] [getThingList]"+e);
		}
		

		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param customerId
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getThingListSort", method = RequestMethod.GET)
	public ResponseEntity<Response> getThingListSort(@RequestParam("sort-by") String sortBy, @RequestParam("value") int value, HttpSession session) {
		logger.info("[BEGIN] [getThingListByCustomer] [ThingController Layer]");
		Response response = new Response(); 
		HttpStatus status;
		
		Things things;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

		try {
			if(userInfo!=null){
				
				if(sortBy!=null && sortBy!="" && value!=0){
				
				Map<String , String> permissions = userInfo.getRolePermissions();
				logger.debug("Permissions for device module: "+userInfo.getRolePermissions());
				logger.debug("Timezone found for the user: "+userInfo.getTimeZone());
				
				if(permissions.containsKey("Device Management")){
					logger.debug("Permissions for device module: "+permissions.get("Device Management"));

					/** call to get thing list service*/
					things = thingService.getThingList(value, sortBy, userInfo);
				
						if(things!=null){
							if(permissions.get("Device Management").equals("2")){
								things.setPermission(1);
							}else{
								things.setPermission(0);
							}
						
							response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
							response.setCode(ErrorCodes.SUCCESS_LISTING_DEVICES);
							response.setData(things);
							status = HttpStatus.OK;
						}else{
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.GENERAL_APP_ERROR);
							status = HttpStatus.OK;
							
						}
				}else{
					things=new Things();
					things.setThings(new ArrayList<>());
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.SUCCESS_LISTING_DEVICES);
					response.setData(things);
					status = HttpStatus.OK;
				}
				
				
			}else {
				/** set status to unauthorized */
				status = HttpStatus.BAD_REQUEST;
				/** Failure */
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_INVALID_SORT_DETAILS);				
			}

			}else{
				/** set status to unauthorized */
				status = HttpStatus.UNAUTHORIZED;
				
				/** Failure */
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (InternalFailureException | InvalidRequestException | ResourceAlreadyExistsException
				| ServiceUnavailableException | ThrottlingException | UnauthorizedException e){
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			status = HttpStatus.OK;
			logger.error("[ERROR] [ThingController] [getThingListByCustomer]"+e);
		} catch (VEMAppException e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			status = HttpStatus.OK;
			logger.error("[ERROR] [ThingController] [getThingListByCustomer]"+e);
		}
		
		logger.info("[END] [getThingListByCustomer] [ThingController Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param deviceStatus
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/changeDeviceStatus", method = RequestMethod.POST)
	public ResponseEntity<Response> changeDeviceStatus(@RequestBody DeviceStatusRequest deviceStatus, HttpSession session) {
		
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

			try {
				if(userInfo!=null){

					/** call to creating thing */
				response = thingService.updateDeviceStatus(deviceStatus, userInfo.getUserId());
				
				}else{
					/** set status to unauthorized */
					status = HttpStatus.UNAUTHORIZED;
					
					/** Failure status and code*/
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.INVALID_SESSION);
				}
			} catch (VEMAppException e) {
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
				status = HttpStatus.OK;
				logger.error("[ERROR] [ThingController] [changeDeviceStatus]"+e);
			}


		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param disconnectDevice
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/disconnectDevice", method = RequestMethod.POST)
	public ResponseEntity<Response> disconnectDevice(@RequestBody DisconnectDeviceRequest disconnectDevice, HttpSession session) {
		
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

			try {
				if(userInfo!=null){

					/** call to creating thing */
				response = thingService.disconnectDevice(disconnectDevice, userInfo.getUserId());
				
				}else{
					/** set status to unauthorized */
					status = HttpStatus.UNAUTHORIZED;
					
					/** Failure status and code*/
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.INVALID_SESSION);
				}
			} catch (VEMAppException e) {
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
				status = HttpStatus.OK;
				logger.error("[ERROR] [ThingController] [disconnectDevice]"+e);
			}


		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param thing
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/updateDevice", method = RequestMethod.POST)
	public ResponseEntity<Response> updateDevice(@RequestBody ThingUpdateRequest thing, HttpSession session) {
		
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		
		try{
			if(userInfo!=null){
	
				thing.setUpdatedBy(userInfo.getUserId());
				
				/** call to creating thing */
				response = thingService.updateDevice(thing);
			}else{
				/** set status to unauthorized */
				status = HttpStatus.UNAUTHORIZED;
				
				/** Failure status and code*/
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
		}catch(VEMAppException e){
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			status = HttpStatus.OK;
			logger.error("[ERROR] [ThingController] [updateDevice]"+e);
		}
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * listSite controller is used handle the request & response for 
	 * Listing all the Sites for a customer.
	 *  
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/listSite", method=RequestMethod.GET)
	private ResponseEntity<Response> listSite(@RequestParam("sort-by") String sortBy, @RequestParam("value") int value, HttpSession session){
		
		logger.info("[BEGIN] [listSite] [ThingController Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the customerId is '0' means customerId is invalid
				 * if the customerId is not '0' means customerId is valid.
				 */
				if(sortBy!=null && value!=0){
					response = thingService.listSite(sortBy, value, userDetails.getUserId(), userDetails.getIsSuper());
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_SITE_CUSTOMER_ID_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":Customer id should not be empty!");
				}
				 
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch (Exception e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.LIST_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			status = HttpStatus.OK;
			logger.error("Error while fetching site list "+e);
		}
		
		logger.info("[END] [listSite] [ThingController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param deviceId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/getDevice/{deviceId}", method=RequestMethod.GET)
	private ResponseEntity<Response> getDevice(@PathVariable("deviceId") int deviceId, HttpSession session){
	logger.info("[BEGIN] [getDevice] [ThingController Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the deviceId is '0' means deviceId is invalid
				 * if the deviceId is not '0' means deviceId is valid.
				 */
				if(deviceId!=0){
					response = thingService.getDevice(deviceId, userDetails);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_DEVICE_DEVICEID_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":device id should not be empty!");
				}
				 
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch (Exception e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_GET_DEVICE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			logger.error("Error while fetching device info "+e);
		}
		
		logger.info("[END] [getDevice] [ThingController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param deviceId
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/deleteDevice/{deviceId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response> deleteDevice(@PathVariable("deviceId") int deviceId , HttpSession session) {
		logger.info("[BEGIN] [deleteDevice] [ThingController Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

			try {
				if(userInfo!=null){

					/** call to deleting thing */
				response = thingService.deleteDevice(deviceId, userInfo.getUserId());
				
				}else{
					/** set status to unauthorized */
					status = HttpStatus.UNAUTHORIZED;
					
					/** Failure status and code*/
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.INVALID_SESSION);
				}
			} catch (VEMAppException e) {
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_DELETE_DEVICE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED);
				status = HttpStatus.OK;
				logger.error("[ERROR] [ThingController] [deleteDevice]"+e);
			}

		logger.info("[END] [deleteDevice] [ThingController Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	
	/**
	 * listThermostatUnit controller is used handle the request & response for 
	 * Listing all the THermostat units for a site.
	 *  
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/listThermostatUnit/{siteId}", method=RequestMethod.GET)
	private ResponseEntity<Response> listThermostatUnit(@PathVariable("siteId") int siteId, HttpSession session){
		
		logger.info("[BEGIN] [listThermostatUnit] [ThingController Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the siteId is '0' means siteId is invalid
				 * if the siteId is not '0' means siteId is valid.
				 */
				if(siteId!=0){
					response = thingService.listThermostatUnit(siteId, userDetails);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_DEVICE_SITEID_EMPTY_TSTATUNITS);
					response.setData(CommonConstants.ERROR_OCCURRED+":Site id should not be empty!");
				}
				 
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch (Exception e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_LIST_THERMOSTAT_UNITS_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			status = HttpStatus.OK;
			logger.error("Error while fetching thermostat unit list "+e);
		}
		
		logger.info("[END] [listThermostatUnit] [ThingController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param siteId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/getTstatPref/{siteId}", method=RequestMethod.GET)
	private ResponseEntity<Response> getTstatPref(@PathVariable("siteId") int siteId, HttpSession session){
	logger.info("[BEGIN] [getTstatPref] [ThingController Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the siteId is '0' means siteId is invalid
				 * if the siteId is not '0' means siteId is valid.
				 */
				if(siteId!=0){
					response = thingService.getTstatPref(siteId);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_DEVICE_SITEID_EMPTY_TSTATUNITS);
					response.setData(CommonConstants.ERROR_OCCURRED+":site id should not be empty!");
				}
				 
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch (Exception e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_DEVICE_GET_TSTATPREF_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			logger.error("Error while fetching thermostat preference info "+e);
		}
		
		logger.info("[END] [getTstatPref] [ThingController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	

	/**
	 * @param xcspecDevId
	 * @param setTemperatureRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/{deviceId}/set-temperature", method = RequestMethod.POST)
	public ResponseEntity<Response> setTemp(@PathVariable("deviceId") int deviceId, @RequestBody SetTemperatureRequest setTemperatureRequest, HttpSession session) {
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

			try {
				
				/*
				 *  checking for if session is valid or not
				 */
				if(userInfo!=null){

					/*
					 *call to set temperature for thermostat on device id
					 */
				response = thingService.setTemp(deviceId, setTemperatureRequest, userInfo.getUserId());
				
				}else{
					/** set status to unauthorized */
					status = HttpStatus.UNAUTHORIZED;
					
					/** Failure status and code*/
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.INVALID_SESSION);
				}
			} catch (VEMAppException e) {
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
				status = HttpStatus.OK;
				logger.error("[ERROR] [ThingController] [disconnectDevice]"+e);
			}


		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param disconnectDevice
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/{deviceId}/set-tstat-data", method = RequestMethod.POST)
	public ResponseEntity<Response> setTStatData(@PathVariable("deviceId") int deviceId, @RequestBody SetTStatDataRequest setTStatDataRequest, HttpSession session) {
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

			try {
				
				/*
				 *  checking for if session is valid or not
				 */
				if(userInfo!=null){

					/*
					 *call to set hold for thermostat on device id
					 */
				response = thingService.setTStatData(deviceId, setTStatDataRequest,  userInfo.getUserId());
				
				}else{
					/** set status to unauthorized */
					status = HttpStatus.UNAUTHORIZED;
					
					/** Failure status and code*/
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.INVALID_SESSION);
				}
			} catch (VEMAppException e) {
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
				status = HttpStatus.OK;
				logger.error("[ERROR] [ThingController] [disconnectDevice]"+e);
			}


		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param xcspecDevId
	 * @param setTemperatureRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/{deviceId}/set-clock", method = RequestMethod.POST)
	public ResponseEntity<Response> setClock(@PathVariable("deviceId") int deviceId, @RequestBody SetClockRequest setClockRequest, HttpSession session) {
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

			try {
				
				/*
				 *  checking for if session is valid or not
				 */
				if(userInfo!=null){

					/*
					 *call to set thermostat clock on device id
					 */
				response = thingService.setClock(deviceId, setClockRequest,  userInfo.getUserId());
				
				}else{
					/** set status to unauthorized */
					status = HttpStatus.UNAUTHORIZED;
					
					/** Failure status and code*/
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.INVALID_SESSION);
				}
			} catch (VEMAppException e) {
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
				status = HttpStatus.OK;
				logger.error("[ERROR] [ThingController] [disconnectDevice]"+e);
			}


		return new ResponseEntity<>(response, status);
	}

	
	@RequestMapping(value="/list-dev-forecast", method=RequestMethod.GET)
	private ResponseEntity<Response> listDevForecast(@RequestParam("site-id") int siteId, HttpSession session){
		
		logger.info("[BEGIN] [listDevForecast] [ThingController Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the siteId is '0' means siteId is invalid
				 * if the siteId is not '0' means siteId is valid.
				 */
				if(siteId!=0){
					response = thingService.listDevForecast(siteId, userDetails);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_DEVICE_SITEID_EMPTY_TSTATUNITS);
					response.setData(CommonConstants.ERROR_OCCURRED+":Site id should not be empty!");
				}
				 
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch (Exception e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_LIST_THERMOSTAT_UNITS_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			status = HttpStatus.OK;
			logger.error("Error while fetching devicelist with forecast info "+e);
		}
		
		logger.info("[END] [listDevForecast] [ThingController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * @param deviceStatus
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/update-heatpump-field", method = RequestMethod.POST)
	public ResponseEntity<Response> updateHeatPumpFields(@RequestBody UpdateHeatPumpFieldReq heatPump, HttpSession session) {
		
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

			try {
				if(userInfo!=null){

					/** call to creating thing */
				response = thingService.updateHeatPumpFields(heatPump, userInfo.getUserId());
				
				}else{
					/** set status to unauthorized */
					status = HttpStatus.UNAUTHORIZED;
					
					/** Failure status and code*/
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.INVALID_SESSION);
				}
			} catch (VEMAppException e) {
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
				status = HttpStatus.OK;
				logger.error("[ERROR] [ThingController] [updateHeatPumpFields]"+e);
			}


		return new ResponseEntity<>(response, status);
	}
	
}
