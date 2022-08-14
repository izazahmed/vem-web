package com.enerallies.vem.controller.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.schedule.AddScheduleDetails;
import com.enerallies.vem.service.schedule.ScheduleService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

@Controller
@RequestMapping("/api/scheduleinfo")
public class ScheduleInfoController {

	private static final Logger logger = Logger
			.getLogger(ScheduleInfoController.class);
	@Autowired
	private ScheduleService scheduleService;
	private static final String USER_DETAILS_OBJECT = "eaiUserDetails";

	/**
	 * 
	 * 
	 * addSite controller is used handle the request & response for creating an
	 * new Site.
	 * 
	 * @param roleRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value = "/addSchedule", method = RequestMethod.POST)
	private ResponseEntity<Response> addSchedule(
			@RequestBody AddScheduleDetails addScheduleDetails,
			HttpSession session) {
		
		logger.debug("[DEBUG] addSchedule - Started " );

		ArrayList<JSONObject> tplist = new ArrayList<JSONObject>();

		Iterator<JSONObject> iteratordet;
		AddScheduleDetails scheduleDetails;
		HashMap map = new HashMap();
		ArrayList<AddScheduleDetails> scheduleOnjList = new ArrayList<AddScheduleDetails>();

		if (addScheduleDetails.getTimepointsmap() != null) {

			for (Iterator<String> iterator = addScheduleDetails
					.getTimepointsmap().keySet().iterator(); iterator.hasNext();) {

				String key = iterator.next();
				logger.debug("[DEBUG] Key - " + key);
				tplist = (ArrayList<JSONObject>) addScheduleDetails
						.getTimepointsmap().get(key);

				iteratordet = tplist.iterator();
				int dow = Integer.parseInt(key);
				
				while (iteratordet.hasNext()) {
					scheduleDetails = new AddScheduleDetails();
					map = iteratordet.next();

					scheduleDetails.setTime(map.get("time").toString());
					scheduleDetails.setAm(map.get("am").toString());
					scheduleDetails.setHtpoint(map.get("htpoint").toString());
					scheduleDetails.setHtunit(map.get("htunit").toString());
					scheduleDetails.setClpoint(map.get("clpoint").toString());
					scheduleDetails.setClunit(map.get("clunit").toString());
					scheduleDetails.setDayId(dow + "");

					scheduleOnjList.add(scheduleDetails);

				}

			}
		}

		logger.debug("[DEBUG] scheduleOnjList - " + scheduleOnjList);

		addScheduleDetails.setSchdlObjList(scheduleOnjList);

		/*
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;

		/*
		 * Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session
				.getAttribute(USER_DETAILS_OBJECT);

		try {

			/*
			 * checking for if session is valid or not
			 */
			if (userDetails != null) {
				/*
				 * Catches when the session is valid.
				 */
				if (addScheduleDetails != null) {
					response = scheduleService.addSchedule(addScheduleDetails,
							userDetails.getUserId());
					status = HttpStatus.OK;
				} else {
					throw new NullPointerException();
				}

			} else {

				/*
				 * Catches when the request is unauthorized and Preparing
				 * failure response
				 */
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);

			}

		} catch (NullPointerException ne) {
			status = HttpStatus.BAD_REQUEST;
			logger.error("", ne);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("", e);
		}

		logger.info("[END] [addSite] [Site Controller Layer]");

		return new ResponseEntity<>(response, status);
	}

	/**
	 * 
	 * 
	 * addSite controller is used handle the request & response for creating an
	 * new Site.
	 * 
	 * @param roleRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value = "/getScheduleDetails", method = RequestMethod.POST)
	private ResponseEntity<Response> getScheduleDetails(
			@RequestBody AddScheduleDetails addScheduleDetails,
			HttpSession session) {
 			Response response = new Response();
		   HttpStatus status = HttpStatus.OK;

		   logger.info("[Start] [getScheduleDetails] [Site Controller Layer ]"+addScheduleDetails.getScheduleId());
		   
		try {
			
			/*
			 * Getting the session details
			 */
			GetUserResponse userDetails = (GetUserResponse) session
					.getAttribute(USER_DETAILS_OBJECT);
			
			/*
			 * checking for if session is valid or not
			 */
			if (userDetails != null) {
				
				response = scheduleService.getScheduleDetails(addScheduleDetails,userDetails);
				
				ObjectMapper mapper = new ObjectMapper(); try {		
						  logger.info("JSON Object::::::::::::"
					  +mapper.writeValueAsString(response)); }
					 catch (IOException e1)
					  { // TODO Auto-generated catch block e1.printStackTrace(); 
						 
					  }
				
			}else {

				/*
				 * Catches when the request is unauthorized and Preparing
				 * failure response
				 */
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);

			}
			
				 
									
		} catch (NullPointerException ne) {
			status = HttpStatus.BAD_REQUEST;
			logger.error("", ne);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("", e);
		}

		logger.info("[END] [getScheduleDetails] [Site Controller Layer]"+response);

		
		
		return new ResponseEntity<>(response, status);
	}
	
	

	/**
	 * 
	 * 
	 * addSite controller is used handle the request & response for creating an
	 * new Site.
	 * 
	 * @param roleRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value = "/updateSchedule", method = RequestMethod.POST)
	private ResponseEntity<Response> updateSchedule(
			@RequestBody AddScheduleDetails addScheduleDetails,
			HttpSession session) {

		ArrayList<JSONObject> tplist = new ArrayList<JSONObject>();
		
		 logger.info("[Start] [updateSchedule] [Schedule Info Controller Layer ]");
		   

		Iterator<JSONObject> iteratordet;
		AddScheduleDetails scheduleDetails;
		HashMap map = new HashMap();
		ArrayList<AddScheduleDetails> scheduleOnjList = new ArrayList<AddScheduleDetails>();

		if (addScheduleDetails.getTimepointsmap() != null) {

			for (Iterator<String> iterator = addScheduleDetails
					.getTimepointsmap().keySet().iterator(); iterator.hasNext();) {

				String key = iterator.next();
				logger.debug("[DEBUG] Key - " + key);
				tplist = (ArrayList<JSONObject>) addScheduleDetails
						.getTimepointsmap().get(key);

				iteratordet = tplist.iterator();
				int dow = Integer.parseInt(key);
				
				while (iteratordet.hasNext()) {
					scheduleDetails = new AddScheduleDetails();
					map = iteratordet.next();

					scheduleDetails.setTime(map.get("time").toString());
					scheduleDetails.setAm(map.get("am").toString());
					scheduleDetails.setHtpoint(map.get("htpoint").toString());
					scheduleDetails.setHtunit(map.get("htunit").toString());
					scheduleDetails.setClpoint(map.get("clpoint").toString());
					scheduleDetails.setClunit(map.get("clunit").toString());
					scheduleDetails.setDayId(dow + "");

					scheduleOnjList.add(scheduleDetails);

				}

			}
		}

		logger.debug("[DEBUG] scheduleOnjList - " + scheduleOnjList);

		addScheduleDetails.setSchdlObjList(scheduleOnjList);

		/*
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;

		/*
		 * Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session
				.getAttribute(USER_DETAILS_OBJECT);

		try {

			/*
			 * checking for if session is valid or not
			 */
			if (userDetails != null) {
				/*
				 * Catches when the session is valid.
				 */
				if (addScheduleDetails != null) {
					response = scheduleService.updateSchedule(addScheduleDetails,
							userDetails.getUserId());
					status = HttpStatus.OK;
				} else {
					throw new NullPointerException();
				}

			} else {

				/*
				 * Catches when the request is unauthorized and Preparing
				 * failure response
				 */
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);

			}

		} catch (NullPointerException ne) {
			status = HttpStatus.BAD_REQUEST;
			logger.error("", ne);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("", e);
		}

		logger.info("[END] [updateSchedule ] [Scheduler Controller Layer]");

		return new ResponseEntity<>(response, status);
	}

	/**
	 * 
	 * 
	 * addSite controller is used handle the request & response for creating an
	 * new Site.
	 * 
	 * @param roleRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value = "/deleteSchedule", method = RequestMethod.POST)
	private ResponseEntity<Response> deleteSchedule(
			@RequestBody AddScheduleDetails addScheduleDetails,
			HttpSession session) {
 			Response response = new Response();
		   HttpStatus status = HttpStatus.OK;

		   logger.info("[Start] [deleteSchedule] [Site Controller Layer ]"+addScheduleDetails.getScheduleId());
		   
		try {
			GetUserResponse userDetails = (GetUserResponse) session
					.getAttribute(USER_DETAILS_OBJECT);
			
			response = scheduleService.deleteSchedule(addScheduleDetails,userDetails.getUserId());
			
			ObjectMapper mapper = new ObjectMapper(); try {		
					  logger.info("JSON Object::::::::::::"
				  +mapper.writeValueAsString(response)); }
				 catch (IOException e1)
				  { // TODO Auto-generated catch block e1.printStackTrace(); 
					 
				  }
				 
									
		} catch (NullPointerException ne) {
			status = HttpStatus.BAD_REQUEST;
			logger.error("", ne);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("", e);
		}

		logger.info("[END] [deleteSchedule] [Site Controller Layer]"+response);

		
		
		return new ResponseEntity<>(response, status);
	}
	
	
	/**
	 * 
	 * 
	 * addSite controller is used handle the request & response for creating an
	 * new Site.
	 * 
	 * @param roleRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value = "/addDeviceSchedule", method = RequestMethod.POST)
	private ResponseEntity<Response> addDeviceSchedule(
			@RequestBody AddScheduleDetails addScheduleDetails,
			HttpSession session) {
		
		logger.debug("[DEBUG] addDeviceSchedule - Started " );

		ArrayList<JSONObject> tplist = new ArrayList<JSONObject>();

		Iterator<JSONObject> iteratordet;
		AddScheduleDetails scheduleDetails;
		HashMap map = new HashMap();
		ArrayList<AddScheduleDetails> scheduleOnjList = new ArrayList<AddScheduleDetails>();

		if (addScheduleDetails.getTimepointsmap() != null) {

			for (Iterator<String> iterator = addScheduleDetails
					.getTimepointsmap().keySet().iterator(); iterator.hasNext();) {

				String key = iterator.next();
				logger.debug("[DEBUG] Key - " + key);
				tplist = (ArrayList<JSONObject>) addScheduleDetails
						.getTimepointsmap().get(key);

				iteratordet = tplist.iterator();
				int dow = Integer.parseInt(key);
				
				while (iteratordet.hasNext()) {
					scheduleDetails = new AddScheduleDetails();
					map = iteratordet.next();

					scheduleDetails.setTime(map.get("time").toString());
					scheduleDetails.setAm(map.get("am").toString());
					scheduleDetails.setHtpoint(map.get("htpoint").toString());
					scheduleDetails.setHtunit(map.get("htunit").toString());
					scheduleDetails.setClpoint(map.get("clpoint").toString());
					scheduleDetails.setClunit(map.get("clunit").toString());
					scheduleDetails.setDayId(dow + "");

					scheduleOnjList.add(scheduleDetails);

				}

			}
		}

		logger.debug("[DEBUG] scheduleOnjList - " + scheduleOnjList);

		addScheduleDetails.setSchdlObjList(scheduleOnjList);

		/*
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;

		/*
		 * Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session
				.getAttribute(USER_DETAILS_OBJECT);

		try {

			/*
			 * checking for if session is valid or not
			 */
			if (userDetails != null) {
				/*
				 * Catches when the session is valid.
				 */
				if (addScheduleDetails != null) {
					response = scheduleService.addDeviceSchedule(addScheduleDetails,
							userDetails.getUserId());
					status = HttpStatus.OK;
				} else {
					throw new NullPointerException();
				}

			} else {

				/*
				 * Catches when the request is unauthorized and Preparing
				 * failure response
				 */
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);

			}

		} catch (NullPointerException ne) {
			status = HttpStatus.BAD_REQUEST;
			logger.error("", ne);
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("", e);
		}

		logger.info("[END] [addDeviceSchedule] [Schedule Controller Layer]");

		return new ResponseEntity<>(response, status);
	}

	


	
}
