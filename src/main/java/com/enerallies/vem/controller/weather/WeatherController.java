package com.enerallies.vem.controller.weather;

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

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.iot.UpdateForecastRequestList;
import com.enerallies.vem.beans.weather.AddForecastRequestList;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.weather.WeatherService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : WeatherController 
 * 
 * WeatherController: is an entry point to get the weather related information
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        22-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	22-11-2016		Rajashekharaiah Muniswamy		File Created
 */

@Controller
@RequestMapping("/api/weather")
public class WeatherController {
	
	/** Getting logger*/	
	private static final Logger logger = Logger.getLogger(WeatherController.class);

	@Autowired
	WeatherService weatherService;
	
	@RequestMapping(value = "/add-forecast", method = RequestMethod.POST)
	public ResponseEntity<Response> addForecast(@RequestBody AddForecastRequestList forecastReq, HttpSession session){
		logger.info("[BEGIN] [addForecast] [WeatherController Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		if(userInfo!=null){
			/** call to creating thing */
			try {
				response = weatherService.addForecast(forecastReq, userInfo.getUserId());
			} catch (VEMAppException e) {
				logger.error("Error found while storing forecast data"+e);
				/** Failure status and code*/
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			}
		}else{
			/** set status to unauthorized */
			status = HttpStatus.UNAUTHORIZED;
			/** Failure status and code*/
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.INVALID_SESSION);
		}
		logger.info("[END] [addForecast] [WeatherController Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	@RequestMapping(value = "/updateforecast-mode/{deviceId}/{forecastMode}/{forcastType}", method = RequestMethod.POST)
	public ResponseEntity<Response> updateForecastMode(@PathVariable("deviceId") String deviceId, @PathVariable("forecastMode") String forecastMode, @PathVariable("forcastType") String forcastType, HttpSession session){
		logger.info("[BEGIN] [updateForecastMode] [WeatherController Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		
		
		if(userInfo!=null){
		
			/** call to creating thing */
			try {
				response = weatherService.updateForecastMode(deviceId, forecastMode,forcastType, userInfo.getUserId());
			} catch (VEMAppException e) {
				logger.error("Error found while storing forecast data"+e);
				/** Failure status and code*/
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			}
		}else{
			/** set status to unauthorized */
			status = HttpStatus.UNAUTHORIZED;
			
			/** Failure status and code*/
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.INVALID_SESSION);
		}
		logger.info("[END] [addForecast] [WeatherController Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	
	
	/**
	 * @param sortBy
	 * @param value
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/get-schedule-list", method = RequestMethod.GET)
	public ResponseEntity<Response> getScheduleList(@RequestParam("sort-by") String sortBy, @RequestParam("value") int value, HttpSession session) {
		logger.info("[BEGIN] [getScheduleList] [WeatherController Layer]");
		Response response = new Response(); 
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

		try {
			if(userInfo!=null){
				
				if(sortBy!=null && sortBy!="" && value!=0){
				
					/** call to get schedule service*/
					response = weatherService.getScheduleList(sortBy, value);
				
	
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
			
		}catch (VEMAppException e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			status = HttpStatus.OK;
			logger.error("[ERROR] [ThingController] [getScheduleList]"+e);
		}
		
		logger.info("[END] [getScheduleList] [WeatherController Layer]");
		return new ResponseEntity<>(response, status);
	}


	@RequestMapping(value = "/forecast-list", method = RequestMethod.GET)
	public ResponseEntity<Response> getForecastList(@RequestParam("type") int type, @RequestParam("type-id") int typeId, HttpSession session) {
		logger.info("[BEGIN] [getForecastList] [WeatherController Layer]");
		Response response = new Response(); 
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

		try {
			if(userInfo!=null){
				
				if(typeId!=0){
				
					/** call to get forecast list service*/
					response = weatherService.getForecastList(type, typeId);
				
	
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
			
		}catch (VEMAppException e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			status = HttpStatus.OK;
			logger.error("[ERROR] [WeatherController] [getForecastList]"+e);
		}
		
		logger.info("[END] [getForecastList] [WeatherController Layer]");
		return new ResponseEntity<>(response, status);
	}

	@RequestMapping(value = "/read-schedule", method = RequestMethod.GET)
	public ResponseEntity<Response> readSchedule(@RequestParam("device-id") int deviceId, HttpSession session) {
		logger.info("[BEGIN] [readSchedule] [WeatherController Layer]");
		Response response = new Response(); 
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

		try {
			if(userInfo!=null){
				
				if(deviceId!=0){
				
					/** call to get forecast list service*/
					response = weatherService.readSchedule(deviceId);
				
	
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
			
		}catch (VEMAppException e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			status = HttpStatus.OK;
			logger.error("[ERROR] [WeatherController] [readSchedule]"+e);
		}
		
		logger.info("[END] [readSchedule] [WeatherController Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	@RequestMapping(value = "/update-forecast", method = RequestMethod.POST)
	public ResponseEntity<Response> updateForecast(@RequestBody UpdateForecastRequestList updateForecastReq, HttpSession session){
		logger.info("[BEGIN] [updateForecast] [WeatherController Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		
		
		if(userInfo!=null){

		
			/** call to creating thing */
			try {
				response = weatherService.updateForecast(updateForecastReq, userInfo.getUserId());
			} catch (VEMAppException e) {
				logger.error("Error found while updating forecast data"+e);
				/** Failure status and code*/
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			}
		}else{
			/** set status to unauthorized */
			status = HttpStatus.UNAUTHORIZED;
			
			/** Failure status and code*/
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.INVALID_SESSION);
		}
		logger.info("[END] [updateForecast] [WeatherController Layer]");
		return new ResponseEntity<>(response, status);
	}

	/**
	 * @param forecastId
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/delete-forecast/{forecastId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response> deleteForecast(@PathVariable("forecastId") int forecastId , HttpSession session) {
		logger.info("[BEGIN] [deleteForecast] [WeatherController Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

			try {
				if(userInfo!=null){

					/** call to deleting forecast */
				response = weatherService.deleteForecast(forecastId, userInfo.getUserId());
				
				}else{
					/** set status to unauthorized */
					status = HttpStatus.UNAUTHORIZED;
					
					/** Failure status and code*/
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.INVALID_SESSION);
				}
			} catch (VEMAppException e) {
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_DELETE_FORECAST_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED);
				status = HttpStatus.OK;
				logger.error("[ERROR] [WeatherController] [deleteForecast]"+e);
			}

		logger.info("[END] [deleteForecast] [WeatherController Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	@RequestMapping(value = "/run-schedule", method = RequestMethod.GET)
	public ResponseEntity<Response> runSchedule(@RequestParam("device-id") int deviceId, @RequestParam("schedule-id") int scheduleId, @RequestParam("xcspec-id") String xcspecId, HttpSession session) {
		logger.info("[BEGIN] [runSchedule] [WeatherController Layer]");
		Response response = new Response(); 
		HttpStatus status = HttpStatus.OK;
		
		/** Get session object */
		GetUserResponse userInfo = (GetUserResponse)session.getAttribute("eaiUserDetails");
		

		try {
			if(userInfo!=null){
				
				if(deviceId!=0 && scheduleId!=0 && xcspecId!=null && xcspecId!=""){
				
					/** call to get forecast list service*/
					response = weatherService.runSchedule(deviceId,scheduleId,xcspecId,userInfo);
				
	
				}else {
					/** set status to unauthorized */
					status = HttpStatus.BAD_REQUEST;
					/** Failure */
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_INVALID_RUN_SCHEDULE_INFO);				
				}

			}else{
				/** set status to unauthorized */
				status = HttpStatus.UNAUTHORIZED;
				
				/** Failure */
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		}catch (VEMAppException e) {
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			status = HttpStatus.OK;
			logger.error("[ERROR] [WeatherController] [runSchedule]"+e);
		}
		
		logger.info("[END] [runSchedule] [WeatherController Layer]");
		return new ResponseEntity<>(response, status);
	}

}
