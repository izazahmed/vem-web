/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enerallies.vem.beans.admin.CreateNewPasswordRequest;
import com.enerallies.vem.beans.admin.FirstTimePasswordChangeRequest;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.admin.MailBroadCast;
import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.controller.admin.AdminController;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.admin.AdminService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.publish.MailPublisher;
import com.enerallies.vem.util.template.ITemplateUtil;

/**
 * File Name : IndexController 
 * 
 * IndexController: This is the index controller to point index page
 *
 * @author Nagarjuna Eerla.
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
 * 02	02-09-2016		Nagarjuna Eerla		Added sign-in, forgot, logout requests
 */
@Controller
@RequestMapping("/")
public class IndexController {
	
	 // Getting logger instance
	 private static final Logger logger = Logger.getLogger(AdminController.class);
	
	 /** Auto wiring instance of IotService  */
	 @Autowired
	 AdminService adminService;
	 
	 @Autowired
	 private AuditDAO auditDao;
	 
	 @Autowired
	 private ITemplateUtil itemplateUtil;
	 
	  /**
	   * getIndexPage : this is for welcome page
	   * @param response
	   * @return
	   */
	  @RequestMapping(method = RequestMethod.GET)
	  public String getIndexPage(HttpServletResponse response, HttpSession session) {
		  logger.info("[BEGIN] [getIndexPage] [Index Controller Layer]");
		  String pageName = CommonConstants.DASHBOARD_REDIRECT;
		  try{
			  GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
			  response.setHeader("Pragma", "no-cache");
			  response.setHeader("Cache-Control", "no-cache");
			  response.setDateHeader("Expires", 0);
			  if(userDetails == null)
				  pageName = CommonConstants.LOGIN;
			  logger.info("page name:::::::::::::::"+pageName);
		  }catch (Exception e) {
			logger.error("",e);
		  }
	      return pageName;
	  }
	  
	  /**
	   * getLoginPage : this is for welcome page, this method is used to redirect in interceptor
	   * @param response
	   * @return
	   */
	  @RequestMapping(value = "/login", method = RequestMethod.GET)
	  public String getLoginPage(HttpServletResponse response) {
		  	response.setHeader("Pragma", "no-cache");
		    response.setHeader("Cache-Control", "no-cache");
		    response.setDateHeader("Expires", 0);
	       return CommonConstants.LOGIN;
	  }
	  
	  /**
	   * sign-in : Implementing EAI sign in
	   * 
	   * @param userName
	   * @param password
	   * @param model
	   * @param session
	   * @param httpResponse
	   * @return
	   */
		@RequestMapping(value = "/signin", method = RequestMethod.POST)
		public String signin(@RequestParam String userName, @RequestParam String password,@RequestParam String timezone,Model model, HttpServletResponse httpResponse,HttpServletRequest request) {
			
			logger.info("[BEGIN] [signin] [Controller Layer]");
			
			Response response = new Response();
			
			 HttpSession oldSession = request.getSession();
			 oldSession.invalidate();
			 HttpSession session = request.getSession(true);
			
			// status code instantiation
			HttpStatus status = HttpStatus.OK;
		
			// Creating userDetails object to store user details
			GetUserResponse userDetails = null;;
			
			// Preparing application error message
			String errorMessage = "";
			
			// Handling page to re-direct for successful / unsuccessful login 
			String pageToRedirect = "";
			
			try {
				
				// Checking is email empty or not
				if(StringUtils.isNotEmpty(userName)){
					
					// Checking is password is empty or not
					if(StringUtils.isNotEmpty(password)){

						// setting session attribute and storing those into userDetails object
						userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
						
						if(userDetails == null){
							
							//checking is email exist in our records in or not
							if(adminService.isEmailExist(userName)){
								
								// Get the user details
								GetUserResponse user = adminService.getUserDetailsByMail(userName);
								
								/*
								 *  Checking for the user is type of customer only or not
								 *  if isCSO is type of 1 then he is a customer type only
								 */
								if(user.getIsCSO() != 1){

									/*
									 * If user.getIsFirstTimeUser() = 1 means first time user
									 * 0 means not a first time user
									 * 
									 * If user.getIsTokenExpired() = 1 means token still active else 
									 * 2 means token has expired
									 */
									if(user.getIsFirstTimeUser() != 1){
										
										/*
										 *  checking if the user is locked or unlocked
										 *  if is_active is 
										 *  0 --> in-active
										 *  1 --> active
										 *  2 --> active and lock
										 *  3 --> logical delete
										 *  
										 */
										if(user.getIsActive() != 2){
											
											//checking for whether the user is active or not 
											if(user.getIsActive() != 0){
												// validating the user details
												response = adminService.validateUserDetails(userName, password, user.getIsSuper(), user.getUserId(), timezone);
												userDetails = (GetUserResponse) response.getData();
												userDetails.setTimeZone(timezone);
												
												if(StringUtils.equals(response.getCode(), ErrorCodes.USER_INFO_USER_VALIDATE_SUCCESS)){
													session.setAttribute("eaiUserDetails", userDetails);
													pageToRedirect  = CommonConstants.DASHBOARD_REDIRECT;
												}else if(StringUtils.equals(response.getCode(), ErrorCodes.WARN_INCREMENT_FAIL_COUNT_FAILURE)){
													// Need to give info to users like how many attempts he has	
													int failCount = userDetails.getFailureCount();
													if(failCount<3){
														errorMessage = CommonConstants.INVALID_CREDENTIALS;
													}else if(failCount==3){
														errorMessage = CommonConstants.YOU_HAVE_TWO_MORE_ATTEMPTS;
													}else if(failCount==4){
														errorMessage = CommonConstants.THIS_IS_YOUR_LAST_ATTEMPT;
													}else if(failCount==5){
														//Sending user locking email notification 
														sendUserLockEmail(user.getFirstName(), user.getEmailId(), request, user.getUserId());
														errorMessage = CommonConstants.YOUR_ACCOUNT_IS_LOCKED_NEXT_24;
													}
													pageToRedirect  = CommonConstants.LOGIN;
												}else if(StringUtils.equals(response.getCode(), ErrorCodes.WARN_USER_GOT_LOCKED_FAILURE)){
													//Sending user locking email notification 
													sendUserLockEmail(user.getFirstName(), user.getEmailId(), request, user.getUserId());
													// Need to give info to users like how many attempts he has											
													errorMessage = CommonConstants.YOUR_ACCOUNT_IS_LOCKED;
													pageToRedirect  = CommonConstants.LOGIN;
												}else{
													// Need to give info to users like how many attempts he has											
													errorMessage = CommonConstants.FAIL_TO_VALIDATE_USER;
													pageToRedirect  = CommonConstants.LOGIN;
												}
												
											}else{
												errorMessage = CommonConstants.USER_IS_INACTIVE;
												pageToRedirect  = CommonConstants.LOGIN;
											}
										}else if(user.getIsActive() == 2){ // user is locked
											
											// check if the user is eligible to release lock
											if(adminService.isUserEligibleToReleaseLock(userName)){
													
													// validating the user details
													response = adminService.validateUserDetails(userName, password, user.getIsSuper(), user.getUserId(), timezone);
													userDetails = (GetUserResponse) response.getData();
													userDetails.setTimeZone(timezone);
													if(StringUtils.equals(response.getCode(), ErrorCodes.USER_INFO_USER_VALIDATE_SUCCESS)){
														session.setAttribute("eaiUserDetails", userDetails);
														pageToRedirect  = CommonConstants.DASHBOARD_REDIRECT;
													}else if(StringUtils.equals(response.getCode(), ErrorCodes.WARN_INCREMENT_FAIL_COUNT_FAILURE)){
														// Need to give info to users like how many attempts he has	
														int failCount = userDetails.getFailureCount();
														if(failCount<3){
															errorMessage = CommonConstants.INVALID_CREDENTIALS;
														}else if(failCount==3){
															errorMessage = CommonConstants.YOU_HAVE_TWO_MORE_ATTEMPTS;
														}else if(failCount==4){
															errorMessage = CommonConstants.THIS_IS_YOUR_LAST_ATTEMPT;
														}else if(failCount==5){
															//Sending user locking email notification 
															sendUserLockEmail(user.getFirstName(), user.getEmailId(), request, user.getUserId());
															errorMessage = CommonConstants.YOUR_ACCOUNT_IS_LOCKED_NEXT_24;
														}
														pageToRedirect  = CommonConstants.LOGIN;
													}else if(StringUtils.equals(response.getCode(), ErrorCodes.WARN_USER_GOT_LOCKED_FAILURE)){
														// Need to give info to users like how many attempts he has											
														errorMessage = CommonConstants.USER_LOCKED;
														pageToRedirect  = CommonConstants.LOGIN;
													}else{
														// Need to give info to users like how many attempts he has											
														errorMessage = CommonConstants.FAIL_TO_VALIDATE_USER;
														pageToRedirect  = CommonConstants.LOGIN;
													}
											}else{
												errorMessage = CommonConstants.YOUR_ACCOUNT_IS_LOCKED;
												pageToRedirect  = CommonConstants.LOGIN;
											}
										}
										
									}else{
										if(user.getIsTokenExpired() == 2){
											errorMessage = CommonConstants.EXPIRED_MAIL_FOR_INSTRUCTIONS;
										}else{
											errorMessage = CommonConstants.CHECK_MAIL_FOR_INSTRUCTIONS;
										}
										pageToRedirect  = CommonConstants.LOGIN;
									}
								}else {
									errorMessage = CommonConstants.CSO_USER_TYPE;
									pageToRedirect  = CommonConstants.LOGIN;
								}
								
							}else{
								errorMessage = CommonConstants.INVALID_USERNAME;
								pageToRedirect  = CommonConstants.LOGIN;
							}
							
						}else{
							// already the user is logged into the application
							pageToRedirect  = CommonConstants.DASHBOARD_REDIRECT;
						}
						
					}else{
						errorMessage = CommonConstants.PSWD_NOT_EMPTY;
						pageToRedirect  = CommonConstants.LOGIN;
					}
					
				}else{
					errorMessage = CommonConstants.USER_NAME_NOT_EMPTY;
					pageToRedirect  = CommonConstants.LOGIN;
				}
				
				httpResponse.setHeader("Pragma", "no-cache");
				httpResponse.setHeader("Cache-Control", "no-cache");
				httpResponse.setDateHeader("Expires", 0);
			    
			} catch (Exception e) {
				logger.error("[ERROR] [signin] [Index Controller Layer]"+e);
				errorMessage = "Error occured while validating user details";
				pageToRedirect  = CommonConstants.LOGIN;
			}

			// Adding model attribute only when error occurred into the application
			if(StringUtils.equals(pageToRedirect, CommonConstants.LOGIN)){
				// adding model attributes
				model.addAttribute("eaiMessage", errorMessage);
				model.addAttribute("status", "error");
				model.addAttribute("userName", userName);
				model.addAttribute("password", password);

				// Auditing logged-in user details
				AuditRequest auditRequest = new AuditRequest();
				auditRequest.setUserId(0);
				auditRequest.setUserAction("Failed");
				auditRequest.setLocation("");
				auditRequest.setServiceId("8"); // for user login module
				auditRequest.setDescription(userName);
				auditRequest.setServiceSpecificId(0);
				auditRequest.setOutFlag("");
				auditRequest.setOutErrMsg("");
				auditDao.insertAuditLog(auditRequest);
				
			}else{
				
				// Auditing logged-in user details
				AuditRequest auditRequest = new AuditRequest();
				auditRequest.setUserId(userDetails.getUserId());
				auditRequest.setUserAction("Success");
				auditRequest.setLocation("");
				auditRequest.setServiceId("8"); // for user login module
				auditRequest.setDescription("User login");
				auditRequest.setServiceSpecificId(userDetails.getUserId());
				auditRequest.setOutFlag("");
				auditRequest.setOutErrMsg("");
				auditDao.insertAuditLog(auditRequest);
			}
			
			
			session.setAttribute("pdfFlag", request.getParameter("pdfFlag") != null ? request.getParameter("pdfFlag") : "");
			
			session.setAttribute("pdfValue", request.getParameter("pdfValue") != null ? request.getParameter("pdfValue") : "");
			
			logger.info(StringUtils.isEmpty(errorMessage)?"No error occured while validating user details" : errorMessage );
			
			logger.info("[END] [signin] [Controller Layer] session id");
			
			return pageToRedirect;
		}

		private void sendUserLockEmail(String userFname, String emailId, HttpServletRequest request, int userId) throws VEMAppException, MalformedURLException{
			
			// Creating broadcast object to send mail related details to publisher
			MailBroadCast broadCast = new MailBroadCast();
			
			// setting the template variable as map key
			Map<String,String> templateMap = new HashMap<>();
			templateMap.put(CommonConstants.USER_FNAME_C, userFname);

			URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
			
			/*"http://"+
			ConfigurationUtils.getConfig("server.address")+
			"/"+ConfigurationUtils.getConfig("app.context")*/
			
			String imgUrl = url+
					"/fileUpload/loadApplicationImage?imageName=uploaded_logo.png";
			
			templateMap.put("imgUrl", imgUrl);
			
			// Getting subject from properties and adding to broadcast object
			broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.LOCK_MAIL_SUBJECT));
			
			// Preparing mail content from free marker template 
			broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.LOCK_MAIL_CONTENT),templateMap));
			broadCast.setToEmail(emailId);
			
			// Instantiating mail publisher
			MailPublisher publisher = new MailPublisher();
			
			publisher.publishEmail(broadCast);
			
			auditActivityLog(0, userId, "Emailed", "Locked email has been sent");
			
		}

		/**
		 * forgotPassword: will resets the password by mail ID
		 * 
		 * @param emailId
		 * @param model
		 * @return
		 */
		@RequestMapping(value = "forgotPassword", method = RequestMethod.POST)
		public String forgotPassword(@RequestParam String emailId, Model model) {
			
			logger.info("[BEGIN] [forgotPassword] [Controller Layer]");
			
			// Instantiating Response object
			Response response = new Response();
			
			// Preparing application error message
			String errorMessage = "";
			
			// Flag to decide whether is this request is success or fail
			boolean isSuccess = false;
			
			// status code instantiation
			HttpStatus status = HttpStatus.OK;
			
			try {
				// Checking if the user is empty or not
				if(StringUtils.isNotEmpty(emailId)){
					if(adminService.isEmailExist(emailId)){
						
						// Getting user details
						GetUserResponse user = adminService.getUserDetailsByMail(emailId);
						
						/*
						 *  Checking for the user is type of customer only or not
						 *  if isCSO is type of 1 then he is a customer type only
						 */
						if(user.getIsCSO() != 1){
						
							// Checking is token expired or not.
							if(user.getIsTokenExpired() != 1){
								
								/*
								 * If user.getIsFirstTimeUser() = 1 means first time user
								 * 0 means not a first time user
								 * 
								 * If user.getIsTokenExpired() = 1 means token still active else 
								 * 2 means token has expired
								 */
								if(user.getIsFirstTimeUser() != 1){
									
									/*
									 *  checking if the user is locked or unlocked
									 *  if is_active is 
									 *  0 --> in-active
									 *  1 --> active
									 *  2 --> active and lock
									 *  3 --> logical delete
									 *  
									 */
									if(user.getIsActive() != 2){
										
										//checking for whether the user is active or not 
										if(user.getIsActive() != 0){
											
											// reseting the password by mail id
											response = adminService.forgotPassword(emailId, user.getFirstName(), user.getUserId());
											
											// Checking whether the forgot password process is success or not
											if(StringUtils.equals(response.getStatus(), CommonConstants.AppStatus.SUCCESS.toString())){
												isSuccess  = true; // forgot success page
												errorMessage = CommonConstants.RESENT_PSWD_DETAILS_SENT_MAIL;
											}else{
												errorMessage = CommonConstants.ERROR_WHILE_DOING_REQUEST;
											}
											
										}else{
											errorMessage = CommonConstants.USER_IS_INACTIVE;
										}
										
									}else{
										
										// check if the user is eligible to release lock
										if(adminService.isUserEligibleToReleaseLock(emailId)){
											
											// reseting the password by mail id
											response = adminService.forgotPassword(emailId, user.getFirstName(), user.getUserId());
											
											// Checking whether the forgot password process is success or not
											if(StringUtils.equals(response.getStatus(), CommonConstants.AppStatus.SUCCESS.toString())){
												isSuccess  = true; // forgot success page
												errorMessage = CommonConstants.REQUEST_COMPLETED;
											}else{
												errorMessage = CommonConstants.ERROR_WHILE_DOING_REQUEST;
											}
										}else{
											errorMessage = CommonConstants.YOUR_ACCOUNT_IS_LOCKED;
										}
									}
								}else{
									if(user.getIsTokenExpired() == 2){
										errorMessage = CommonConstants.EXPIRED_MAIL_FOR_INSTRUCTIONS;
									}else{
										errorMessage = CommonConstants.CHECK_MAIL_FOR_INSTRUCTIONS;
									}
								}
							}else{
								errorMessage = CommonConstants.LINK_ALREADY_IN_USE;
							}
						}else {
							errorMessage = CommonConstants.CSO_USER_TYPE;
						}
					}else{
						errorMessage = CommonConstants.INVALID_USERNAME;
					}
				}else{
					errorMessage = CommonConstants.USER_NAME_NOT_EMPTY;
				}
				
			} catch (Exception e) {
				logger.error("[ERROR] [forgotPassword] [Index Controller Layer] "+e);
				errorMessage = "Error occured while doing your forgot password request please contact admin";
			}
			
			if(isSuccess)
				model.addAttribute("forgotStatus", "success");
			else
				model.addAttribute("forgotStatus", "error");
		
			model.addAttribute("forgotMessage", errorMessage);
			
			logger.info("[END] [forgotPassword] [Index Controller Layer]");
			
			return "forgotPassword";
		}
		
		/**
		 * createNewPassword: Creates New password
		 * 
		 * @param createNewPasswordRequest
		 * @return
		 */
		@RequestMapping(value = "/createNewPassword", method = RequestMethod.POST)
		public ResponseEntity<Response> createNewPassword(@RequestBody CreateNewPasswordRequest createNewPasswordRequest) {
			
			logger.info("[BEGIN] [createNewPassword] [Controller Layer]");
			
			// Instantiating Response object
			Response response = new Response();
			
			// status code instantiation
			HttpStatus status = HttpStatus.OK;
			
			// new password error message
			String newPwdErrorMsg = "";
			
			// flag to decide the request is success or failure
			boolean isSuccess = false;
			
			try {
				
				/* Instantiating the ValidatorBean and validating the createNewPasswordRequest bean.*/
				ValidatorBean validatorBean = ConfigurationUtils.validateBeans(createNewPasswordRequest);
				
				// If any of the property of createUserRequest is not a valid property then sending corresponding error message 
				if(validatorBean.isNotValid()){
					if(StringUtils.equals(validatorBean.getMessage(), "ERR_USER_2103"))
						newPwdErrorMsg = "Auth Token should not be empty";
					else if(StringUtils.equals(validatorBean.getMessage(), "ERR_USER_2104"))
						newPwdErrorMsg = "Choose password should not be empty";
					else if(StringUtils.equals(validatorBean.getMessage(), "ERR_USER_2105"))
						newPwdErrorMsg = "Repeated password should not be empty";
					else if(StringUtils.equals(validatorBean.getMessage(), "ERR_USER_2107"))
						newPwdErrorMsg = "Password should contain atleast one alphabet and number";
				}else if(ConfigurationUtils.comparePasswords(createNewPasswordRequest.getChoosePwd(), createNewPasswordRequest.getRepeatPwd())){
						
						// Creating new password
						response = adminService.createNewPassword(createNewPasswordRequest);
						
						// Checking whether the new password process is success or not
						if(StringUtils.equals(response.getStatus(), CommonConstants.AppStatus.SUCCESS.toString())){
							isSuccess  = true; // new password success page
							newPwdErrorMsg = "Thank You. Your password is saved successfully";
						}else{
							newPwdErrorMsg = CommonConstants.ERROR_WHILE_DOING_REQUEST;
						}
				}else{
					newPwdErrorMsg = "Choose password and repeate passwords doesn't match";
				}
				
			} catch (Exception e) {
				// Preparing failure response
				newPwdErrorMsg = CommonConstants.ERROR_WHILE_DOING_REQUEST;
				logger.error("[ERROR] [createNewPassword] [Controller Layer]"+e);
			}
			
			if(isSuccess)
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			else
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
		
			response.setCode(newPwdErrorMsg);
			
			logger.info("[END] [createNewPassword] [Controller Layer]");
			
			return new ResponseEntity<>(response, status);
		}
		
		/**
		 * 
		 * createFirstTimePassword: Creates first time password
		 * 
		 * @param passwordChangeRequest
		 * @return
		 */
		@RequestMapping(value = "/createFirstTimePassword", method = RequestMethod.POST)
		public ResponseEntity<Response> createFirstTimePassword(@RequestBody FirstTimePasswordChangeRequest passwordChangeRequest) {
			
			logger.info("[BEGIN] [createFirstTimePassword] [Controller Layer]");
			
			// Instantiating Response object
			Response response = new Response();
			
			// status code instantiation
			HttpStatus status = HttpStatus.OK;
			
			// new first time password error message
			String firstTimePwdErrorMsg = "";
			
			// flag to decide the request is success or failure
			boolean isSuccess = false;

			try {
				
				/* Instantiating the ValidatorBean and validating the createNewPasswordRequest bean.*/
				ValidatorBean validatorBean = ConfigurationUtils.validateBeans(passwordChangeRequest);
				
				// if any of the property of createUserRequest is not a valid property then sending corresponding error message 
				if(validatorBean.isNotValid()){
					
					if(StringUtils.equals(validatorBean.getMessage(), "ERR_USER_2103"))
						firstTimePwdErrorMsg = "Auth Token should not be empty";
					else if(StringUtils.equals(validatorBean.getMessage(), "ERR_USER_2032"))
						firstTimePwdErrorMsg = "Invalid phone Number";
					
				}else{
				
					// validate securityQuestions
					response = adminService.validateSecurityQuestions(passwordChangeRequest.getPhoneNumber(), passwordChangeRequest.getToken());
					
					if(StringUtils.equals(response.getCode(), ErrorCodes.USER_INFO_USER_VALIDATE_SUCCESS)){
					
						isSuccess  = true; // first time password success page
						firstTimePwdErrorMsg = "Thank You. Your security question has been validated successfully";
						
						/*
						
						// comparing if the both passwords are matched or not
						if(ConfigurationUtils.comparePasswords(passwordChangeRequest.getChoosePassword(), passwordChangeRequest.getRepeatedPassword())){
							
							// Getting user details from Database by token
							User user = adminDao.getUserDetails(CommonConstants.FetchUserDetailsByType.TOKEN.toString(), passwordChangeRequest.getToken());
							
							// checking if the token is expired or not
							if(user.getIsTokenExpired() != 2){
								
								// Creating new password
								response = adminService.createNewFirstTimePassword(passwordChangeRequest);
								
								// Checking whether the first time user password process is success or not
								if(StringUtils.equals(response.getStatus(), CommonConstants.AppStatus.SUCCESS.toString())){
									isSuccess  = true; // first time password success page
									firstTimePwdErrorMsg = "Thank You. Your password is saved successfully";
								}else{
									firstTimePwdErrorMsg = CommonConstants.ERROR_WHILE_DOING_REQUEST;
								}
							}else{
								firstTimePwdErrorMsg = "Your token has been expired";
							}
							
						}else{
							firstTimePwdErrorMsg = "Choose password and repeate passwords doesn't match";
						}*/
						
					}else if(StringUtils.equals(response.getCode(), ErrorCodes.WARN_INCREMENT_FAIL_COUNT_FAILURE)){
						firstTimePwdErrorMsg = "Invalid answer for security question";
					}else if(StringUtils.equals(response.getCode(), ErrorCodes.WARN_USER_MAX_ATTEMPTS_OVER_FOR_SECURITY_QUESTIONS_FAILURE)){
						firstTimePwdErrorMsg = "Please contact the EnerAllies Support at 1-888-770-3009.";
					}else if(StringUtils.equals(response.getCode(), ErrorCodes.WARN_INVALID_TOKEN)){
						firstTimePwdErrorMsg = "Invalid token";
					}else{
						firstTimePwdErrorMsg = CommonConstants.CONTACT_EAI_HELP;
					}
				}
				
			} catch (Exception e) {
				
				// Preparing failure response
				firstTimePwdErrorMsg = CommonConstants.ERROR_WHILE_DOING_REQUEST;
				logger.error("[ERROR] [createFirstTimePassword] [Controller Layer]"+e);
				
			}
			
			if(isSuccess){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
		
			response.setCode(firstTimePwdErrorMsg);
			
			logger.info("[END] [createFirstTimePassword] [Controller Layer]");
			
			return new ResponseEntity<>(response, status);
		}
		
	  /**
	   * getDashBoardPage: this is for dash board page
	   * @param response
	   * @return
	   */
	  @RequestMapping(value = "dashboard",method = RequestMethod.GET)
	  public String getDashBoardPage(HttpServletResponse response) {
		  	
		  	response.setHeader("Pragma", "no-cache");
		    response.setHeader("Cache-Control", "no-cache");
		    response.setDateHeader("Expires", 0);

		    return "dashboard";
	  }
	  
	  /**
	   * getForgotPage : is for forgot page
	   * @return
	   */
	  @RequestMapping(value = "forgot",method = RequestMethod.GET)
	  public String getForgotPage() {
	       return "forgotPassword";
	  }

	  /**
	   * logout : this is for logout request
	   * 
	   * @param session
	   * @param response
	   * @return
	   */
	  @RequestMapping(value = "logout",method = RequestMethod.GET)
	  public String logout(HttpSession session, HttpServletResponse response) {
		  
		  	try {
				  
			  	// setting session attribute and storing those into userDetails object
			  	GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		  		
		  		// Auditing logged-in user details
				AuditRequest auditRequest = new AuditRequest();
				auditRequest.setUserId(userDetails.getUserId());
				auditRequest.setUserAction("Success");
				auditRequest.setLocation("");
				auditRequest.setServiceId("11"); // for user logout module
				auditRequest.setDescription("User logout");
				auditRequest.setServiceSpecificId(userDetails.getUserId());
				auditRequest.setOutFlag("");
				auditRequest.setOutErrMsg("");
				auditDao.insertAuditLog(auditRequest);
			  
			  	// Removing the session attribute
			  	session.removeAttribute("eaiUserDetails");
			  	
			  	// invalidating the session
			  	session.invalidate();
			  	
			  	response.setHeader("Pragma", "no-cache");
			    response.setHeader("Cache-Control", "no-cache");
			    response.setDateHeader("Expires", 0);
			    
			} catch (Exception e) {
				logger.error("[ERROR] [logout] [Controller Layer]"+e);
			}
		    
		  	// redirecting to home page 
	       return "redirect:/";
	  }
	  
	 /**
	  * newPassword: this page is for to change password with forgot password
	  * 
	  * @param token
	  * @param model
	  * @return
	  */
	  @RequestMapping(value = "newPassword",method = RequestMethod.GET)
	  public String newPassword(@RequestParam String token, Model model) {
		  
		   // Checking is token still active or expired
		   String pageToRedirect = "newPassword";
		   
			try {
				if(adminService.isTokenExpired(token, "newPwd")){
					pageToRedirect = "commonInfo";
					model.addAttribute("expiryType", "resetPassword");
				}
			} catch (VEMAppException e) {
				
				logger.error("[ERROR] [newPassword] [Index Controller Layer]"+e);
			}
		   
	       return pageToRedirect;
	  }
	 
	  /**
	   * firstTimePassword : this is for first time password
	   * 
	   * @param token
	   * @param model
	   * @return
	   */
	  @RequestMapping(value = "firstTimePassword",method = RequestMethod.GET)
	  public String firstTimePassword(@RequestParam String token, Model model) {
		  
		  String pageToRedirect = "firstTimePassword";
		  // Checking is token still active or expired
		  
		   try {
				if(adminService.isTokenExpired(token, "firstTimePwd")){
					pageToRedirect = "commonInfo";
					model.addAttribute("expiryType", "cretePassword");
				}
			} catch (VEMAppException e) {
				
				logger.error("[ERROR] [newPassword] [Index Controller Layer]"+e);
			}
	       return pageToRedirect;
	  }
	  
	  
	  /**
	   * invalidSession : Redirects to login page when the session is expired.
	   * @param response
	   * @param model
	   * @return String
	   */
	  @RequestMapping(value = "/invalidSession", method = RequestMethod.GET)
	  public String invalidSession(HttpServletResponse response,Model model) {
		  logger.info("invalidSession @@@@@@@@@@@@@@@@@@@@@@@@ entered");
		  	response.setHeader("Pragma", "no-cache");
		    response.setHeader("Cache-Control", "no-cache");
		    response.setDateHeader("Expires", 0);
		    model.addAttribute("sessionCheck", "true");
		    logger.info("invalidSession @@@@@@@@@@@@@@@@@@@@@@@@ end");
		    return CommonConstants.LOGIN;
	  
	  }
	  
	  /**
		 * auditActivityLogForEmail: common method to audit activity log for email
		 * 
		 * @param loggedInuserId
		 * @param serviceSpecificId
		 * @param userAction
		 * @param desc
		 */
		public void auditActivityLog(int loggedInuserId, int serviceSpecificId, String userAction, String desc){
			logger.info("[START][auditActivityLog]["+desc+"][IndexController]");
			AuditRequest auditRequest = new AuditRequest();
			auditRequest.setUserId(loggedInuserId);
			auditRequest.setUserAction(userAction);
			auditRequest.setLocation("");
			auditRequest.setServiceId("6"); // for user module
			auditRequest.setDescription(desc);
			auditRequest.setServiceSpecificId(serviceSpecificId);
			auditRequest.setOutFlag("");
			auditRequest.setOutErrMsg("");
			auditDao.insertAuditLog(auditRequest);
			logger.info("[END][auditActivityLog]["+desc+"][IndexController]");
		}
	
}