package com.enerallies.vem.controller.common;


import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.service.common.CommonService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : CommonController 
 * 
 * CommonController: is used to handle common Requests used in all modules of VEM2.0 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	08-11-2016		Rajashekharaiah Muniswamy		File Created
 */
@Controller
@RequestMapping("/api/comm")
public class CommonController {
	/** Getting logger*/	
	private static final Logger logger = Logger.getLogger(CommonController.class);
	
	/**Instatiating CommonService*/
	@Autowired
	private CommonService commonService;
	
	@RequestMapping(value="/validate-zip-code", method=RequestMethod.GET)
	private ResponseEntity<Response> validateZipCode(@RequestParam(required = true, value = "zipCode") int zipCode, @RequestParam(required = true, value = "stateId") int stateId, @RequestParam(required = true, value = "city") String city, HttpSession session){
	logger.info("[BEGIN] [validateZipCode] [CommonController Layer]");
		
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
				 * And if query parameters are not equal to null or empty or 0
				 */
				if(zipCode!=0 && stateId!=0 && city!=null && city!=""){
					response = commonService.validateZipCode(zipCode, stateId, city);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_COMMON_VALIDATE_ZIPCODE);
					response.setData(CommonConstants.ERROR_OCCURRED+" :zipcode or state id can not be empty or zero");
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
			response.setCode(ErrorCodes.ERROR_COMMON_ZIPCODE_NOTVALIDATE);
			response.setData(CommonConstants.ERROR_OCCURRED);
			logger.error("Error while validating zipcode "+e);
		}
		
		logger.info("[END] [validateZipCode] [CommonController Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getTimeZone controller is used handle the request & response for 
	 * getting the time zone for zipCode.
	 * 
	 * @param zipCode
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getTimeZone", method=RequestMethod.GET)
	private ResponseEntity<Response> getTimeZone(@RequestParam(value = "zipCode") String zipCode, 
			HttpSession session){
		
		logger.info("[BEGIN] [getTimeZone] [CommonController Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		logger.debug("[DEBUG] the zipCode::::::"+zipCode);
		
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
				 * And if the zipCode is '' means zipCode is invalid
				 * if the zipCode is not '' means zipCode is valid.
				 */
				if(!zipCode.isEmpty()){
					response = commonService.getTimeZone(zipCode);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_COMMON_TIMEZONE);
					response.setData(CommonConstants.ERROR_OCCURRED+": Zipcode should not be empty!");
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
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [getTimeZone] [CommonController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	/**
	 * storeTimeZone controller is used handle the request & response for 
	 * storing the time zone in session.
	 * 
	 * @param timeZone
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/storeTimeZone", method=RequestMethod.GET)
	private ResponseEntity<Response> storeTimeZone(@RequestParam(value = "timeZone") String timeZone, 
			HttpSession session){
		
		logger.info("[BEGIN] [storeTimeZone] [CommonController Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		logger.debug("[DEBUG] the timeZone::::::"+timeZone);
		
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
				 * If timeZone is not empty the set into userdetails object.
				 * else send the error response.
				 */
				if(!timeZone.isEmpty()){
					userDetails.setTimeZone(timeZone);
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.SUCCESS_COMMON_STORE_TIMEZONE);
					response.setData("success");
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_COMMON_STORE_TIMEZONE);
					response.setData(CommonConstants.ERROR_OCCURRED+": timeZone should not be empty!");
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
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("ERROR",e);
		}
		
		logger.info("[END] [storeTimeZone] [CommonController Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
}
