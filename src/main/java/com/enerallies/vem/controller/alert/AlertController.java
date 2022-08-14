package com.enerallies.vem.controller.alert;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.alert.AlertService;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : AlertController 
 * 
 * AlertController: is used to handle all the Role related Requests
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        16-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 16-09-2016		Raja		    File Created.
 * 
 */

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

	private static final Logger logger=Logger.getLogger(AlertController.class);
	@Autowired AlertService alertService;
	
	GetUserResponse userDetails = null;
	
	
	@RequestMapping(value="/customerAlertConfig/", method=RequestMethod.POST)
	private ResponseEntity<Response> customerAlertConfiguration(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [customerAlertConfiguration] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				response=alertService.alertList(alertRequest);
				status = HttpStatus.OK;
			}
			
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus() == null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode() == null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
			}
			logger.error("", e);
		}
		logger.info("[END] [customerAlertConfiguration] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
		
	/**
	 * getSite controller is used handle the request & response for 
	 * getting the data of existing site.
	 * 
	 * @param siteId
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getConfig", method=RequestMethod.POST)
	private ResponseEntity<Response> getConfig(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [getConfig] [Alert Controller Layer]");
		logger.info("Configid and customerId :" +alertRequest.getAlertId() +":::" + alertRequest.getCustomerId() );
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				
				alertRequest.setUserId(userDetails.getUserId());
				alertRequest.setIsSuper(userDetails.getIsSuper());
				alertRequest.setIsEai(userDetails.getIsEai());
				
				response = alertService.getConfig(alertRequest);
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus() == null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode() == null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_ALERT_GETCONFIG);
				
			}
			logger.error("", e);
		}
		logger.info("[END] [getConfig] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * addGroup controller is used handle the request & response for creating an new group.
	 * @param groupRequest
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/saveConfig", method=RequestMethod.POST)
	private ResponseEntity<Response> saveConfig(@RequestBody AlertRequest alertRequest,HttpSession session){
		logger.info("[BEGIN] [saveConfig] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		try{
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				alertRequest.setUserId(userDetails.getUserId());
				alertRequest.setIsEai(userDetails.getIsEai());
				alertRequest.setIsSuper(userDetails.getIsSuper());
				response = alertService.saveConfig(alertRequest);
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus() == null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode() == null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_ALERT_CONFIG_SAVE);
			}
			logger.error("", e);
		}
		logger.info("[END] [saveConfig] [Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * validateSession method is for validating session
	 * @param session; HttpSession
	 * @return boolean
	 */

	private boolean validateSession(HttpSession session){
		// Getting the session details
		userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		boolean sessionFlag=false;
		try {
			// checking for if session is valid or not
			if(userDetails != null){
				sessionFlag=true;
			}
					
		} catch (Exception e) {
			logger.error("" ,e);
			sessionFlag=false;
		}
		return sessionFlag;
	}
	
	/**
	 * alerts controller is used handle the request & response for 
	 * getting the data of existing customer.
	 *  
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/customeralerts", method=RequestMethod.POST)
	private ResponseEntity<Response> getCustomerAlerts(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [getCustomerAlerts] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				
				alertRequest.setIsEai(userDetails.getIsEai());
				alertRequest.setUserId(userDetails.getUserId());
				alertRequest.setCustomers(userDetails.getCustomers());
				alertRequest.setIsSuper(userDetails.getIsSuper());
				alertRequest.setTimeZone(userDetails.getTimeZone());
				response = alertService.getCustomerAlerts(alertRequest);
				
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus() == null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode() == null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_FETCH);
			}
			logger.error("", e);
		}
		logger.info("[END] [getCustomerAlerts] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	
	/**
	 * alerts controller is used handle the request & response for 
	 * getting the data of existing customer.
	 *  
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/dashboardAlerts", method=RequestMethod.POST)
	private ResponseEntity<Response> getDashboardAlerts(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [getDashboardAlerts] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				
				alertRequest.setIsEai(userDetails.getIsEai());
				alertRequest.setUserId(userDetails.getUserId());
				alertRequest.setIsSuper(userDetails.getIsSuper());
				alertRequest.setTimeZone(userDetails.getTimeZone());
				alertRequest.setPdfReportalertIds("");
				response = alertService.getDashboardAlerts(alertRequest);
				
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus() == null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode() == null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_FETCH);
			}
			logger.error("", e);
		}
		logger.info("[END] [getDashboardAlerts] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/*
	*//**
	 * alerts controller is used handle the request & response for 
	 * getting the data of existing customer.
	 *  
	 * @return ResponseEntity<Response>
	 *//*
	@RequestMapping(value="/storeDeviceState", method=RequestMethod.POST)
	private ResponseEntity<Response> storeDevicesState(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [storeDeviceState] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			response=alertService.storeDeviceStatus(alertRequest);
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus() == null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode() == null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_STORE_ALERTS);
			}
			logger.error("", e);
		}
		logger.info("[END] [getCustomerAlerts] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	*/
	
	/**
	 * getNewAlert controller is used handle the request & response for getting the new alert count
	 * getting the data of existing customer.
	 *  
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getNewAlert", method=RequestMethod.POST)
	private ResponseEntity<Response> getNewAlertCount(HttpSession session){
		
		logger.info("[BEGIN] [getNewAlertCount] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				AlertRequest alertRequest=new AlertRequest();
				alertRequest.setCustomers((userDetails.getCustomers()==null)?"0":userDetails.getCustomers());
				alertRequest.setUserId(userDetails.getUserId());
				alertRequest.setIsSuper(userDetails.getIsSuper());
				response=alertService.getNewAlertCount(alertRequest);
				status=HttpStatus.OK;
			}
			
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus() == null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode() == null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
			}
			logger.error("", e);
		}
		logger.info("[END] [getNewAlertCount] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * updateAlertStatus controller is used handle the request & response for updating the alert status
	 *@param  getting the data of existing customer.
	 *  
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/updateAlertStatus", method=RequestMethod.POST)
	private ResponseEntity<Response> updateAlertStatus(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [updateAlertStatus] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				alertRequest.setUserId(userDetails.getUserId());
				response=alertService.updateAlertStatus(alertRequest);
				status=HttpStatus.OK;
			}
			
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus() == null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode() == null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_ALERT_CONFIG_SAVE);
			}
			logger.error("", e);
		}
		logger.info("[END] [updateAlertStatus] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getAlertsByActionItems : It fetches the alerts by action items based on alert type 
	 *  
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getAlertsByActionItems", method=RequestMethod.POST)
	private ResponseEntity<Response> getAlertsByActionItems(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [getAlertsByActionItems] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				
				alertRequest.setIsEai(userDetails.getIsEai());
				alertRequest.setUserId(userDetails.getUserId());
				alertRequest.setCustomers(userDetails.getCustomers());
				alertRequest.setIsSuper(userDetails.getIsSuper());
				alertRequest.setTimeZone(userDetails.getTimeZone());
				response = alertService.getAlertsByActionItems(alertRequest);
				
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus() == null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode() == null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
			}
			logger.error("", e);
		}
		logger.info("[END] [getAlertsByActionItems] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 *  updateActionItems : It fetches the alerts by action items based on alert type 
	 *  @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/updateUserActionItems", method=RequestMethod.POST)
	private ResponseEntity<Response> updateActionItems(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [updateActionItems] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				alertRequest.setUserId(userDetails.getUserId());
				alertRequest.setIsEai(userDetails.getIsEai());
				alertRequest.setIsSuper(userDetails.getIsSuper());
				response = alertService.updateActionItems(alertRequest);
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			// Preparing failure response
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_ACTIONLIST_UPDATE);
			logger.error("[ERROR] [updateActionItems] [ Alert Controller Layer] ",e);
		}
		logger.info("[END] [updateActionItems] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	
	/**
	 *  getDeviceAlerts : It fetches the alerts based on devices
	 *  @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getDeviceAlerts", method=RequestMethod.POST)
	private ResponseEntity<Response> getDeviceAlerts(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [getDeviceAlerts] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				alertRequest.setUserId(userDetails.getUserId());
				alertRequest.setIsEai(userDetails.getIsEai());
				alertRequest.setIsSuper(userDetails.getIsSuper());
				response = alertService.getDeviceAlertsService(alertRequest);
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("", e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
			logger.error("[ERROR] [getDeviceAlerts] [ Alert Controller Layer] ",e);
		}
		logger.info("[END] [getDeviceAlerts] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 *  deleteCustomerConfig : To delete customer alert configuration
	 *  @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/deleteCustomerConfig", method=RequestMethod.POST)
	private ResponseEntity<Response> deleteCustomerConfig(@RequestBody AlertRequest alertRequest, HttpSession session){
		
		logger.info("[BEGIN] [deleteCustomerConfig] [Alert Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				alertRequest.setUserId(userDetails.getUserId());
				alertRequest.setIsEai(userDetails.getIsEai());
				alertRequest.setIsSuper(userDetails.getIsSuper());
				response = alertService.deleteCustomerConfigService(alertRequest);
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("", e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_CONFIG_DELETE);
			logger.error("[ERROR] [deleteCustomerConfig] [ Alert Controller Layer] ",e);
		}
		logger.info("[END] [deleteCustomerConfig] [ Alert Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	
}
