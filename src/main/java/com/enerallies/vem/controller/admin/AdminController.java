/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.controller.admin;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enerallies.vem.beans.admin.ChangePasswordRequest;
import com.enerallies.vem.beans.admin.CreateUserRequest;
import com.enerallies.vem.beans.admin.DeleteUserRequest;
import com.enerallies.vem.beans.admin.GetUserRequest;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.admin.UpdateEmailIdRequest;
import com.enerallies.vem.beans.admin.UpdateMyProfileRequest;
import com.enerallies.vem.beans.admin.UpdateUserRequest;
import com.enerallies.vem.beans.admin.UserActivityRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.dao.admin.AdminDao;
import com.enerallies.vem.service.admin.AdminService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : AdminController 
 * 
 * AdminController: is used to handle all the admin related Requests
 *
 * @author Naagrjuna Eerla
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	02-08-2016		Nagarjuna Eerla		File Created
 * 02	03-08-2016		Nagarjuna Eerla		Added saveUser Method
 * 03	04-08-2016		Nagarjuna Eerla		Added createNewPassword Method
 * 04	04-08-2016		Nagarjuna Eerla		Added forgotPassword Method
 * 05	05-08-2016		Nagarjuna Eerla		Added changePassword Method
 * 06	08-08-2016		Nagarjuna Eerla		Added getUserDetails Method
 * 07	09-08-2016		Nagarjuna Eerla		Added updateUserDetails Method
 * 08	10-08-2016		Nagarjuna Eerla		Added deleteUser Method
 * 09	11-08-2016		Nagarjuna Eerla		Added activateUser Method
 * 10	13-08-2016		Nagarjuna Eerla		Added inActivateUser Method
 * 11	18-08-2016		Nagarjuna Eerla		Added filterByStatus Method
 * 12	18-08-2016		Nagarjuna Eerla		Added filterByRole Method
 * 13	19-08-2016		Nagarjuna Eerla		Removing sonar qube issues
 */
@Controller
@RequestMapping("/api/admin")
public class AdminController {
	
	// Getting logger instance
	private static final Logger logger = Logger.getLogger(AdminController.class);
	
	/** Auto wiring instance of IotService  */
	@Autowired
	AdminService adminService;
	
	/**
	 * saveUser: Creates new user
	 * @param userRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/saveUser", method = RequestMethod.POST)
	public ResponseEntity<Response> saveUser(@RequestBody CreateUserRequest userRequest, HttpSession session) {
		
		logger.info("[BEGIN] [saveUser] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			// checking for if session is valid or not
			if(userDetails != null){

				// Adding user id to the request
				userRequest.setCreatedBy(userDetails.getUserId());
				
				// Saving user
				response = adminService.saveUser(userRequest,userDetails.getUserId());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_SAVE_FAILED);
			logger.error("[ERROR] [saveUser] [Controller Layer]"+e);
		}
		
		logger.info("[END] [saveUser] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * changePassword: will resets or changes the password
	 * 
	 * @param changePasswordRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ResponseEntity<Response> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpSession session) {
		
		logger.info("[BEGIN] [changePassword] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
	
		try {
			
			// checking for if session is valid or not
			if(userDetails != null){
				
				// Adding user id to the request
				changePasswordRequest.setUpdateBy(userDetails.getUserId());
				
				// Changing the password
				response = adminService.changePassword(changePasswordRequest,userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED);
			logger.error("[ERROR] [changePassword] [Controller Layer]"+e);
			
		}
		
		logger.info("[END] [changePassword] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getUserDetails: will gets the user details by user id
	 * @param getUserRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getUserDetails", method = RequestMethod.POST)
	public ResponseEntity<Response> getUserDetails(@RequestBody GetUserRequest getUserRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getUserDetails] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
			
		try {
					
			// checking for if session is valid or not
			if(userDetails != null){
				
				// getting user details
				response = adminService.getUserDetails(getUserRequest, userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_SAVE_FAILED);
			logger.error("[END] [getUserDetails] [Controller Layer]: "+e);
			
		}
		
		logger.info("[END] [getUserDetails] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * updateUserDetails: updates user details
	 * @param updateUserRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/updateUserDetails", method = RequestMethod.POST)
	public ResponseEntity<Response> updateUserDetails(@RequestBody UpdateUserRequest updateUserRequest, HttpSession session) {
		
		logger.info("[BEGIN] [updateUserDetails] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			
			// checking for if session is valid or not
			if(userDetails != null){
				
				// Adding user id to the request
				updateUserRequest.setUpdateBy(userDetails.getUserId());
				
				// updating user details
				response = adminService.updateUserDetails(updateUserRequest, userDetails.getUserId());
				
				/*
				 * If logged in user and selected user is same then updating session details
				 */
				if((userDetails.getUserId() == updateUserRequest.getUserId()) && response.getStatus().equals(CommonConstants.AppStatus.SUCCESS.toString())){
					// Updating session
					
					GetUserRequest getUserRequest = new GetUserRequest();
					getUserRequest.setUserId(updateUserRequest.getUserId());
					GetUserResponse usersDetails = (GetUserResponse) adminService.getUserDetails(getUserRequest, userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone()).getData();
					session.setAttribute(CommonConstants.SESSION_USER, usersDetails);
					response.setData(usersDetails);
				}
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_UPDATE_FAILED);
			logger.error("[END] [updateUserDetails] [Controller Layer] : "+e);
			
		}
		
		logger.info("[END] [updateUserDetails] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getAllUsersDetails: Gets all user details
	 * 
	 * @param type
	 * @param value
	 * @param access_token
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "getAllUsersDetails", method = RequestMethod.GET)
	public ResponseEntity<Response> getAllUsersDetails(@RequestParam String type, @RequestParam String value, HttpSession session) {
		
		logger.info("[BEGIN] [getAllUsersDetails] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {

			// checking for if session is valid or not
			if(userDetails != null){
				
					if(StringUtils.equals(type, CommonConstants.CUSTOMERS)){
						// Getting all users details
						response = adminService.getAllUsersDetails(CommonConstants.CUSTOMERS, value , userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone());
					}else if(StringUtils.equals(type, "USERS") && (userDetails.getIsSuper() != 1)){
						// Getting all users details
						response = adminService.getAllUsersDetails(CommonConstants.CUSTOMERS, userDetails.getCustomers(), userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone());
					}else{
						// Getting all users details of type SITES and GROUPS (and only belongs to logged in user customers)
						response = adminService.getAllUsersDetails(type, value, userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone());
					}
				
			}else{
				
				// This request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_GET_FAILED);
			logger.error("[ERROR] [getAllUsersDetails] [Controller Layer] : "+e);
			
		}
		
		logger.info("[END] [getAllUsersDetails] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * deleteUserDetails : Deletes user details
	 * @param deleteUserRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/deleteUserDetails", method = RequestMethod.POST)
	public ResponseEntity<Response> deleteUserDetails(@RequestBody DeleteUserRequest deleteUserRequest, HttpSession session) {
		
		logger.info("[BEGIN] [deleteUserDetails] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
		
			// checking for if session is valid or not
			if(userDetails != null){

				// Adding user id to the request
				deleteUserRequest.setUpdateBy(userDetails.getUserId());
				
				// deleting user details
				response = adminService.deleteUser(deleteUserRequest, userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_DELETE_FAILED);
			logger.error("[ERROR] [deleteUserDetails] [Controller Layer] : "+e);
			
		}
		
		logger.info("[END] [deleteUserDetails] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * userAcitivity: It activates in activates user details
	 * 
	 * @param userActivityRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/userAcitivity", method = RequestMethod.POST)
	public ResponseEntity<Response> userAcitivity(@RequestBody UserActivityRequest userActivityRequest, HttpSession session) {

		logger.info("[BEGIN] [userAcitivity] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			
			// checking for if session is valid or not
			if(userDetails != null){

				// Adding user id to the request
				userActivityRequest.setUpdateBy(userDetails.getUserId());
				
				// in-activating user
				response = adminService.userAcitivity(userActivityRequest, userDetails.getUserId());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_ACTIVITY_FAILED);
			logger.error("[ERROR] [userAcitivity] [Controller Layer] : "+e);
			
		}
		
		logger.info("[END] [userAcitivity] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getUserProfileInfo : It gets the user profile info with customer list
	 *  
	 * @param getUserRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getUserProfileInfo", method = RequestMethod.POST)
	public ResponseEntity<Response> getUserProfileInfo(@RequestBody GetUserRequest getUserRequest, HttpSession session) {
		
		logger.info("[BEGIN] [getUserProfileInfo] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
	
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			
			// checking for if session is valid or not
			if(userDetails != null){
				
				// getting user details
				response = adminService.getUserProfileInfo(getUserRequest, userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_GET_FAILED);
			logger.error("[END] [getUserProfileInfo] [Controller Layer]: "+e);
			
		}
		
		logger.info("[END] [getUserProfileInfo] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * updateUserDetails: updates user details
	 * @param updateMyProfileRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/updateMyProfile", method = RequestMethod.POST)
	public ResponseEntity<Response> updateMyProfile(@RequestBody UpdateMyProfileRequest updateMyProfileRequest, HttpSession session) {
		
		logger.info("[BEGIN] [updateMyProfile] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			
			// checking for if session is valid or not
			if(userDetails != null){
				
				// updating user details
				response = adminService.updateMyProfile(updateMyProfileRequest, userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone());
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_UPDATE_PROFILE_FAILED);
			logger.error("[END] [updateMyProfile] [Controller Layer] : "+e);
			
		}
		
		logger.info("[END] [updateMyProfile] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getSitesGroupsByCustomers: is used to handle the request & response for 
	 * Listing all the Sites and groups based on customers.
	 * 
	 * @param groupIds
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getGroupsSites", method=RequestMethod.GET)
	private ResponseEntity<Response> getSitesGroupsByCustomers(@RequestParam(value = "userId") int userId, @RequestParam(value = "customerIds") String customerIds, HttpSession session){
		
		logger.info("[BEGIN] [getSitesGroupsByCustomers] [USER Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		logger.debug("[DEBUG] the customerIds::::::"+customerIds);
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the groupIds is '' means groupIds is invalid
				 * if the groupIds is not '' means groupIds is valid.
				 */
				if(!customerIds.isEmpty()){
					response = adminService.getSitesGroupsByCustomers(userId, userDetails.getUserId(), userDetails.getIsSuper(), customerIds);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_SITE_GROUP_IDS_EMPTY);
					response.setData(CommonConstants.ERROR_OCCURRED+":Group ids should not be empty!");
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
			logger.error("[END] [updateMyProfile] [Controller Layer] : "+e);
		}
		
		logger.info("[END] [getSitesGroupsByCustomers] [USER Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * setTimeZone: It sets time zone to the session
	 * 
	 * @param timeZone
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "setTimeZone", method = RequestMethod.GET)
	public ResponseEntity<Response> setTimeZone(@RequestParam String timeZone, HttpSession session) {
		
		logger.info("[BEGIN] [setTimeZone] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		try {

			// checking for if session is valid or not
			if(userDetails != null){
				userDetails.setTimeZone(timeZone);
				session.setAttribute("eaiUserDetails", userDetails);
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.SESSION_INFO_TIME_ZONE_SUCCESS);
				response.setData(userDetails);
			}else{
				
				// This request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.SESSION_ERROR_TIME_ZONE_FAILED);
			logger.error("[ERROR] [setTimeZone] [Controller Layer] : "+e);
			
		}
		
		logger.info("[END] [setTimeZone] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * updateEmailId: updates user email details
	 * @param updateEmailIdRequest
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/updateEmailId", method = RequestMethod.POST)
	public ResponseEntity<Response> updateEmailId(@RequestBody UpdateEmailIdRequest updateEmailIdRequest, HttpSession session) {
		
		logger.info("[BEGIN] [updateEmailId] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(CommonConstants.SESSION_USER);
		
		try {
			
			// checking for if session is valid or not
			if(userDetails != null){
				
				updateEmailIdRequest.setLoggedInUserId(userDetails.getUserId());
				
				// updating Email id details
				response = adminService.updateEmailId(updateEmailIdRequest, userDetails.getEmailId());
				
				/*
				 * If logged in user and selected user is same then updating session details
				 */
				if((userDetails.getUserId() == updateEmailIdRequest.getUserId()) && response.getStatus().equals(CommonConstants.AppStatus.SUCCESS.toString())){
					// Updating session
					GetUserRequest getUserRequest = new GetUserRequest();
					getUserRequest.setUserId(updateEmailIdRequest.getUserId());
					GetUserResponse usersDetails = (GetUserResponse) adminService.getUserDetails(getUserRequest, userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone()).getData();
					session.setAttribute(CommonConstants.SESSION_USER, usersDetails);
					response.setData(usersDetails);
				}
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.USER_ERROR_UPDATE_EMAIL_FAILED);
			logger.error("[END] [updateEmailId] [Controller Layer] : "+e);
			
		}
		
		logger.info("[END] [updateEmailId] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
}
