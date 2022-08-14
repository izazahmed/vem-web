/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.serviceimpl.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enerallies.vem.beans.admin.BulkUploadResponse;
import com.enerallies.vem.beans.admin.BulkUploadUserRequest;
import com.enerallies.vem.beans.admin.ChangePassword;
import com.enerallies.vem.beans.admin.ChangePasswordRequest;
import com.enerallies.vem.beans.admin.CreateNewPasswordRequest;
import com.enerallies.vem.beans.admin.CreateUserRequest;
import com.enerallies.vem.beans.admin.CreateUserResponse;
import com.enerallies.vem.beans.admin.DeleteUserRequest;
import com.enerallies.vem.beans.admin.DeleteUserResponse;
import com.enerallies.vem.beans.admin.GetUserRequest;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.admin.MailBroadCast;
import com.enerallies.vem.beans.admin.UpdateEmailIdRequest;
import com.enerallies.vem.beans.admin.UpdateMyProfileRequest;
import com.enerallies.vem.beans.admin.UpdateUserRequest;
import com.enerallies.vem.beans.admin.UpdateUserResponse;
import com.enerallies.vem.beans.admin.User;
import com.enerallies.vem.beans.admin.UserActivityRequest;
import com.enerallies.vem.beans.admin.UserActivityResponse;
import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.common.ConvertJsonBeanToDAOBean;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.UploadUserData;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.beans.customers.CustomersRequestBean;
import com.enerallies.vem.dao.admin.AdminDao;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.dao.customers.CustomersDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.admin.AdminService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.PasswordEncryptionProviderUsingSHA;
import com.enerallies.vem.util.UploadUserTemplateThread;
import com.enerallies.vem.util.publish.MailPublisher;
import com.enerallies.vem.util.template.ITemplateUtil;
/**
 * File Name : AdminServiceImpl 
 * 
 * AdminServiceImpl: is the implementation file for admin operation methods
 *
 * @author Cambridge Technologies.
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
@Service("adminService")
@Transactional
public class AdminServiceImpl implements AdminService{
	
	// Getting logger instance
	private static final Logger logger = Logger.getLogger(AdminServiceImpl.class);
	private static final String USER_FAILED="FAILED,This email address is already in use."; 
	
	@Autowired
	private AdminDao adminDao;
	
	@Autowired
	private CustomersDAO customerDao;

	@Autowired
	private ITemplateUtil itemplateUtil;
	
	@Autowired
	private AuditDAO auditDao;
	
	@Override
	public Response saveUser(CreateUserRequest createUserRequest, int loggedInUser) throws VEMAppException {
		
		logger.info("[BEGIN] [saveUser] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		// Initializing flag to send mail or not
		boolean mailFlag = false;
		boolean mailFlagForActivityLog = false;
		try {
			
			/* Instantiating the ValidatorBean and validating the CreateUserRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(createUserRequest);
			
			// if any of the property of createUserRequest is not a valid property then sending corresponding error message 
			if(validatorBean.isNotValid()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else if(adminDao.isEmailExist(createUserRequest.getEmailId()) <= 0) { // checking whether the email is exist or not
			
				// converting client request to DAO bean
				User user = ConvertJsonBeanToDAOBean.saveUserJsonToDAOBean(createUserRequest);
				
				// Checking for the user object is opted to send mail creating user or not
				if (user.getIsOptedToSendMail() == 1) {
					
					// Creating broadcast object to send mail related details to publisher
					MailBroadCast broadCast = new MailBroadCast();
					
					// setting the template variable as map key
					Map<String,String> templateMap = new HashMap<>();
					String userName = user.getUname();
					String userFname = user.getFirstName();
					String userLname = user.getLastName();

					HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
					  
					URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
					
					String imgUrl = url+"/fileUpload/loadApplicationImage?imageName=uploaded_logo.png";
					
					templateMap.put("imgUrl", imgUrl);
					templateMap.put(CommonConstants.USER_FNAME_C, userFname);
					templateMap.put(CommonConstants.USER_LNAME_C, userLname);
					templateMap.put(CommonConstants.USER_NAME, userName);

					// Generating random key / auth token
					String randomKey = RandomStringUtils.randomAlphanumeric(30);

					// Preparing URL to create new password
					String resetLink = url+"/firstTimePassword/?token="+randomKey;
				
					// Adding link to forgot password template
					templateMap.put("resetLink", resetLink);
					
					// Getting subject from properties and adding to broadcast object
					broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.NEW_MAIL_SUBJECT));
					
					// Preparing mail content from free marker template 
					broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.NEW_MAIL_CONTENT),templateMap));
					
					broadCast.setToEmail(user.getEmailId());
					
					// Instantiating mail publisher
					MailPublisher publisher = new MailPublisher();
					
					// Broad casting mail
					mailFlag = publisher.publishEmail(broadCast);
					
					if(mailFlag){
					
						mailFlagForActivityLog = true;
						/*
						 *  Setting token expire flag to 1 means 
						 *  Token has generated and the token is active. 
						 */
						user.setIsTokenExpired(1);
						
						// setting flag to 1 means user received the mail
						user.setIsEmailSent(1);

						// Setting random key
						user.setAuthToken(randomKey);
					}
					
				}else{
					
					mailFlag = true;
					
					/*
					 *  Setting token expire flag to 0 means 
					 *  No Token has generated and the token is a default token. 
					 */
					user.setIsTokenExpired(0);
				}
				
				// Publishing the broadcast object to send mail
				if(mailFlag){
					
					// Calling to saveUserDetails to save user details
					int userId = adminDao.saveUserDetails(user, loggedInUser);
					
					/*
					 * if userId is >=1 means success 
					 * else request is fail
					 */
					if(userId >= 1){
						
						// Creating response instance
						CreateUserResponse createUserResponse = new CreateUserResponse();
						createUserResponse.setUserId(userId);
						createUserResponse.setUserName(user.getUname());
		
						// Activity log
						// Auditing user creation details
						if(mailFlagForActivityLog){
							auditActivityLog(loggedInUser, userId, "Emailed","New account creation email has been sent", "6");
						}
						
						// Adding status code and response data to the response object
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.USER_INFO_SAVE_SUCCESS);
						response.setData(createUserResponse);
						
					}else{
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.USER_ERROR_SAVE_FAILED);
					}
				} else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.USER_ERROR_INVALID_EMAIL_ADDRESS_FAILED);
				}
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_EMAIL_ALREADY_EXIST);
			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_SAVE_FAILED, logger, e);
		}
		
		logger.info("[END] [saveUser] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response createNewPassword(CreateNewPasswordRequest createNewPasswordRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [createNewPassword] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			// converting client request to DAO bean
			ChangePassword changePassword = ConvertJsonBeanToDAOBean.convertCreateNewPwdRequestToDAOBean(createNewPasswordRequest);

			// Getting user details from Database by token
			User user = adminDao.getUserDetails(CommonConstants.FetchUserDetailsByType.TOKEN.toString(), changePassword.getAuthToken(),0,0,null);
			
			// Doing password encryption by calling common method generateEncryptedPassword
			changePassword.setPassword(ConfigurationUtils.generateEncryptedPassword(user.getEmailId(), changePassword.getPassword()));
			
			// Setting user id 
			changePassword.setUserId(user.getUserId());
			
			// calling to resetPassword to reset password
			int status = adminDao.changePassword(changePassword);
			
			/*
			 *  if status is > 0 means createNewPasswordRequest is success 
			 *  else createNewPasswordRequest is fail
			 */
			if(status > 0){

				// Creating broadcast object to send mail related details to publisher
				MailBroadCast broadCast = new MailBroadCast();
				
				// setting the template variable as map key
				Map<String,String> templateMap = new HashMap<>();
				String userFname = user.getFirstName();
				templateMap.put(CommonConstants.USER_FNAME_C, userFname);

				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
				
				URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
				
				// Preparing URL to create new password
				String loginLink = url+"/";
				
				// Adding link to forgot password template
				templateMap.put("loginLink", loginLink);
				
				String imgUrl = url+"/fileUpload/loadApplicationImage?imageName=uploaded_logo.png";
				
				templateMap.put("imgUrl", imgUrl);
				
				// Getting subject from properties and adding to broadcast object
				broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.UPDATE_PASSWORD_MAIL_SUBJECT));
				
				// Preparing mail content from free marker template 
				broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.UPDATE_PASSWORD_MAIL_CONTENT),templateMap));
				broadCast.setToEmail(user.getEmailId());
				
				// Broad casting mail only if the first time password change
				if(!createNewPasswordRequest.getIsFirstTimeChange()){
					
					// Instantiating mail publisher
					MailPublisher publisher = new MailPublisher();
					
					// Broad casting mail
					boolean mailFlag = publisher.publishEmail(broadCast);
					
					if(mailFlag){
						// Activity log
						// Auditing change password details
						auditActivityLog(0, user.getUserId(), "Emailed", "Change password email has been sent", "15");
						
					}
				}
				
				// Adding status code and response data to the response object
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.USER_INFO_CHANGE_PWD_SUCCESS);
				response.setData(status);
				
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED);
			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED, logger, e);
		}
		
		logger.info("[END] [createNewPassword] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response forgotPassword(String emailId, String firstName, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [forgotPassword] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
				// Instantiating the mail broadCast instance
				MailBroadCast broadCast = new MailBroadCast();
				
				// setting the template variable as map key
				Map<String,String> templateMap = new HashMap<>();
				templateMap.put(CommonConstants.USER_FNAME_C, firstName);
				
				// Generating random key / auth token
				String randomKey = RandomStringUtils.randomAlphanumeric(30);

				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
				
				URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
				
				// Preparing url to create new password
				String resetLink =  url+"/newPassword/?token="+randomKey;
				
				// Adding link to forgot password template
				templateMap.put("resetLink", resetLink);
				String imgUrl = url+"/fileUpload/loadApplicationImage?imageName=uploaded_logo.png";
				
				templateMap.put("imgUrl", imgUrl);
				broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.RESET_MAIL_SUBJECT));
				broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.RESET_MAIL_CONTENT),templateMap));
				broadCast.setToEmail(emailId); 
			
				// Instantiating the mail publisher instance
				MailPublisher publisher = new MailPublisher();
				
				// publishing the mail
				boolean mailFlag = publisher.publishEmail(broadCast);
				
				if(mailFlag){
					
					// Activity log
					// Auditing forgot password details
					auditActivityLog(0, userId, "Emailed", "Forgot password email has been sent", "6");
										
					// resetting password
					int status = adminDao.resetPasswordByMail(emailId, ConfigurationUtils.generateRandomPassword(emailId), randomKey);
					
					/*
					 *  if status is > 0 means forgotPassword is success 
					 *  else forgotPassword is fail
					 */
					if(status > 0){
						// Preparing success response object
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.USER_INFO_FORGOT_PWD_SUCCESS);
						
					}else{
						// Preparing failure response object
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.USER_ERROR_INVALID_FAILED);
					}
				}else{
					// Preparing failure response object
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.USER_ERROR_INVALID_EMAIL_ADDRESS_FAILED);
				}
				
				
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_FORGOT_PWD_FAILED, logger, e);
		}
		
		logger.info("[END] [forgotPassword] [SERVICE LAYER]");
		
		return response;
	}


	@Override
	public Response changePassword(ChangePasswordRequest changePasswordRequest, int isSuper, int loggedInUserId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [changePassword] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			/* Instantiating the ValidatorBean and validating the CreateUserRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(changePasswordRequest);
			
			// if any of the property of createUserRequest is not a valid property then sending corresponding error message 
			if(validatorBean.isNotValid()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else {
				// converting client request to DAO bean
				ChangePassword changePassword = ConvertJsonBeanToDAOBean.convertChangePwdRequestToDAOBean(changePasswordRequest);
				
				// Getting user details from DAO Layer
				User user = adminDao.getUserDetails(CommonConstants.FetchUserDetailsByType.USER_ID.toString(), changePasswordRequest.getUserId()+"",0,0,timeZone);
				
				// Comparing is current password matches with existing database password or not
				if(StringUtils.equals(user.getPassword(), ConfigurationUtils.generateEncryptedPassword(user.getEmailId(), changePasswordRequest.getCurrentPwd()))){
	
					// Doing password encryption
					changePassword.setPassword(ConfigurationUtils.generateEncryptedPassword(user.getEmailId(), changePasswordRequest.getNewPwd()));
					
					// calling to changePassword to change password
					int status = adminDao.changePassword(changePassword);
					
					/*
					 *  if status is grater than 0 means success 
					 *  else request is fail
					 */
					if(status > 0){
						
						// Creating broadcast object to send mail related details to publisher
						MailBroadCast broadCast = new MailBroadCast();
						
						// setting the template variable as map key
						Map<String,String> templateMap = new HashMap<>();
						String userFname = user.getFirstName();
						templateMap.put(CommonConstants.USER_FNAME_C, userFname);

						HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
						
						URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
						
						// Preparing URL to create new password
						String loginLink = url+"/";
						
						// Adding link to forgot password template
						templateMap.put("loginLink", loginLink);
						
						String imgUrl = url+"/fileUpload/loadApplicationImage?imageName=uploaded_logo.png";
						
						templateMap.put("imgUrl", imgUrl);
						
						// Getting subject from properties and adding to broadcast object
						broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.UPDATE_PASSWORD_MAIL_SUBJECT));
						
						// Preparing mail content from free marker template 
						broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.UPDATE_PASSWORD_MAIL_CONTENT),templateMap));
						broadCast.setToEmail(user.getEmailId());
						
						// Instantiating mail publisher
						MailPublisher publisher = new MailPublisher();
						
						// Broad casting mail
						boolean mailFlag = publisher.publishEmail(broadCast);
						
						if(mailFlag){
							// Activity log
							// Auditing change password details
							auditActivityLog(loggedInUserId, changePasswordRequest.getUserId(), "Emailed", "Change password email has been sent", "15");
							
						}
						
						// Preparing success response object
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.USER_INFO_CHANGE_PWD_SUCCESS);
						response.setData(status);
						
					}else{
						// Preparing failure response object
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED);
					}
				}else{
					// Preparing failure response object
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.USER_ERROR_CURRENT_PWD_DOES_NOT_MATCH_FAILED);
				}
			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED, logger, e);
		}
		
		logger.info("[END] [changePassword] [SERVICE LAYER]");
		
		return response;
	}


	@Override
	public Response getUserDetails(GetUserRequest getUserRequest, int isSuper, int loggedInUserId, String timezone) throws VEMAppException {
		
		logger.info("[BEGIN] [getUserDetails] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			/* Instantiating the ValidatorBean and validating the GetUserRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(getUserRequest);
			
			// if any property is not a valid property in GetUserRequest then sending corresponding error message 
			if(validatorBean.isNotValid()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				// Getting user details from Database
				User user = adminDao.getUserDetails(CommonConstants.FetchUserDetailsByType.USER_ID.toString(), getUserRequest.getUserId()+"",isSuper,loggedInUserId, timezone);
				
				// Converting dao user object to UI respect object
				GetUserResponse userDetails = ConvertJsonBeanToDAOBean.convertUserDetailsDaoToUserResponse(user);
				
				if(userDetails.getSiteCount() == 1){
					// getting customer and site id if site count is 1
					userDetails.setCustSiteIds(adminDao.getCustomerSiteIds(userDetails.getUserId(), isSuper, loggedInUserId));
				}
				
				// Preparing success response object
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.USER_INFO_GET_USER_SUCCESS);
				response.setData(userDetails);
			}
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_GET_FAILED, logger, e);
		}
		
		logger.info("[END] [getUserDetails] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response updateUserDetails(UpdateUserRequest updateUserRequest, int loggedInUser) throws VEMAppException {
		
		logger.info("[BEGIN] [updateUserDetails] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();

		// Initializing flag to send mail or not
		boolean mailFlag = false;
		boolean mailFlagForActivityLog = false;
		
		try {
		
			/* Instantiating the ValidatorBean and validating the UpdateUserRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(updateUserRequest);
			
			// if any property is not a valid property in GetUserRequest then sending corresponding error message
			if(validatorBean.isNotValid()){ 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			} else {
				
					// converting client request to DAO bean
					User user = ConvertJsonBeanToDAOBean.updateUserJsonToDAOBean(updateUserRequest);
					
					// Checking for the user object is opted to send mail creating user or not
					if (user.getIsOptedToSendMail() == 1) {
						
						// Creating broadcast object to send mail related details to publisher
						MailBroadCast broadCast = new MailBroadCast();
						
						// setting the template variable as map key
						Map<String,String> templateMap = new HashMap<>();
						String userName = user.getEmailId();
						String userFname = user.getFirstName();
						String userLname = user.getLastName();

						HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
						
						URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
						
						String imgUrl = url+"/fileUpload/loadApplicationImage?imageName=uploaded_logo.png";
						
						templateMap.put("imgUrl", imgUrl);
						templateMap.put(CommonConstants.USER_FNAME_C, userFname);
						templateMap.put(CommonConstants.USER_LNAME_C, userLname);
						templateMap.put(CommonConstants.USER_NAME, userName);
						
						// Generating random key / auth token
						String randomKey = RandomStringUtils.randomAlphanumeric(30);

						// Preparing URL to create new password
						String resetLink = url+"/firstTimePassword/?token="+randomKey;
						
						// Adding link to forgot password template
						templateMap.put("resetLink", resetLink);
						
						// Getting subject from properties and adding to broadcast object
						broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.NEW_MAIL_SUBJECT));
						
						// Preparing mail content from free marker template 
						broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.NEW_MAIL_CONTENT),templateMap));
						broadCast.setToEmail(user.getEmailId());
						
						// Instantiating mail publisher
						MailPublisher publisher = new MailPublisher();
						
						// Broad casting mail
						mailFlag = publisher.publishEmail(broadCast);
						
						/*
						 * True means mail has been sent successfully 
						 */
						if(mailFlag){
							
							mailFlagForActivityLog = true;
							/*
							 *  Setting token expire flag to 1 means 
							 *  Token has generated and the token is active. 
							 */
							user.setIsTokenExpired(1);
							
							// setting flag to 1 means user received the mail
							user.setIsEmailSent(1);

							// Setting random key
							user.setAuthToken(randomKey);
						}
						
					}else{
						mailFlag = true;
					}
					
					/*
					 * Saving user details into db
					 */
					if (mailFlag) {
						
						// calling to updateUserDetails to update user details
						int userId = adminDao.updateUserDetails(user, loggedInUser);
						
						/*
						 * if status is greater than equal to 1 means success 
						 * else request is fail
						 */
						if(userId >= 1){
							
							// Creating updateUserResponse instance
							UpdateUserResponse updateUserResponse = new UpdateUserResponse();
							updateUserResponse.setUserId(user.getUserId());
			
							// Activity log
							// Auditing user creation details
							if(mailFlagForActivityLog){
								auditActivityLog(loggedInUser, userId, "Emailed","New account creation email has been sent", "6");
							}
							
							// Preparing success response object
							response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
							response.setCode(ErrorCodes.USER_INFO_UPDATE_SUCCESS);
							response.setData(updateUserResponse);
							
						} else{
							// Preparing failure response object
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.USER_ERROR_UPDATE_FAILED);
						}
					} else{
						// Preparing failure response object
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.USER_ERROR_INVALID_EMAIL_ADDRESS_FAILED);
					}
					
					
			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_UPDATE_FAILED, logger, e);
		}
		
		logger.info("[END] [updateUserDetails] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getAllUsersDetails(String type, String value, int isSuper, int loggedInUserId, String timezone) throws VEMAppException {
		
		logger.info("[BEGIN] [getAllUsersDetails] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
				// Getting user details from Database
				List<User> usersList = adminDao.getAllUsersDetails(type, value, isSuper, loggedInUserId, timezone);
				
				// Converting DAO layer result to user friendly response 
				List<GetUserResponse> usersDetails = ConvertJsonBeanToDAOBean.convertUsersDetailsDaoToUserResponse(usersList, isSuper, loggedInUserId);
				
				/*
				String customerIds = "";
				String groupIds = "";
				String siteIds = "";
				
				for (GetUserResponse getUserResponse : usersDetails) {
					customerIds = customerIds+","+getUserResponse.getCustomers();
					groupIds = groupIds+","+getUserResponse.getGroupId();
					siteIds = siteIds+","+getUserResponse.getLocationId();
				}
				
				List<Customer> customers = adminDao.getCustomers(customerIds);
				List<Group> groups = adminDao.getGroups(groupIds);
				List<Site> sites = adminDao.getSites(siteIds);
				
				JSONObject object = new JSONObject();
				object.put("usersList", usersDetails);
				object.put("customersList", customers);
				object.put("groupsList", groups);
				object.put("sitesList", sites);
				*/

				// Preparing success response object
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.USER_INFO_GET_USER_SUCCESS);
				response.setData(usersDetails);
				
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_GET_FAILED, logger, e);
		}
		
		logger.info("[END] [getAllUsersDetails] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response deleteUser(DeleteUserRequest deleteUserRequest, int isSuper, int loggedInUserId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [deleteUser] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
		
			/* Instantiating the ValidatorBean and validating the DeleteUserRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(deleteUserRequest);
			
			// if any property is not a valid property in DeleteUserRequest then sending corresponding error message  
			if(validatorBean.isNotValid()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.USER_ERROR_DELETE_FAILED);
				response.setData(validatorBean.getMessage());
			}else{
				
				// calling to deleteUserDetails to delete user details
				int userId = adminDao.deleteUserDetails(deleteUserRequest.getUserId(), loggedInUserId);
				
				/*
				 *  if status is >=1 means success 
				 *  else request is fail
				 */
				if(userId >= 1){
					
					// Delete response instance
					DeleteUserResponse deleteUserResponse = new DeleteUserResponse();
					deleteUserResponse.setUserId(userId);
					
					// Getting user details from Database
					User user = adminDao.getUserDetails(CommonConstants.FetchUserDetailsByType.USER_ID.toString(), deleteUserRequest.getUserId()+"", isSuper, loggedInUserId, timeZone);
					
					
					// Creating broadcast object to send mail related details to publisher
					MailBroadCast broadCast = new MailBroadCast();
					
					// setting the template variable as map key
					Map<String,String> templateMap = new HashMap<>();
					templateMap.put(CommonConstants.USER_FNAME_C, user.getFirstName());
					templateMap.put(CommonConstants.USER_LNAME_C, user.getLastName());
					
					HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
					
					URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
					
					/*"http://"+
					ConfigurationUtils.getConfig("server.address")+
					"/"+ConfigurationUtils.getConfig("app.context")*/
					
					String imgUrl = url+
							"/fileUpload/loadApplicationImage?imageName=uploaded_logo.png";
					
					templateMap.put("imgUrl", imgUrl);
					
					// Getting subject from properties and adding to broadcast object
					broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.DELETE_MAIL_SUBJECT));
					
					// Preparing mail content from free marker template 
					broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.DELETE_MAIL_CONTENT),templateMap));
					broadCast.setToEmail(user.getEmailId());
					
					// Instantiating mail publisher
					MailPublisher publisher = new MailPublisher();
					
					publisher.publishEmail(broadCast);
					
					// Preparing success response object
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.USER_INFO_DELETE_SUCCESS);
					response.setData(deleteUserResponse);
					
				}else{
					// Preparing failure response object
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.USER_ERROR_DELETE_FAILED);
				}
			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_DELETE_FAILED, logger, e);
		}
		
		logger.info("[END] [deleteUser] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response userAcitivity(UserActivityRequest userActivityRequest, int loggedInUser) throws VEMAppException {
		
		logger.info("[BEGIN] [userAcitivity] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			/* Instantiating the ValidatorBean and validating the UserActivityRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(userActivityRequest);
			
			// if any property is not a valid property in UserActivityRequest then sending corresponding error message 
			if(validatorBean.isNotValid()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				// calling to inActivateUser to in-activate user
				int userId = adminDao.userAcitivity(userActivityRequest.getUserId(), userActivityRequest.getStatus(), loggedInUser);
				
				/*
				 *  if status is >=1 means success 
				 *  else request is fail
				 */
				if(userId >= 1){
					
					// in-activate user response instance
					UserActivityResponse userActivityResponse = new UserActivityResponse();
					userActivityResponse.setUserId(userId);
	
					// Preparing success response object
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.USER_INFO_ACTIVITY_SUCCESS);
					response.setData(userActivityResponse);
					
				}else{
					// Preparing failure response object
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.USER_ERROR_ACTIVITY_FAILED);
				}
			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_ACTIVITY_FAILED, logger, e);
		}
		
		logger.info("[END] [userAcitivity] [SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response updateMyProfile(UpdateMyProfileRequest updateMyProfileRequest, int isSuper, int loggedInUserId, String timezone) throws VEMAppException {
		
		logger.info("[BEGIN] [updateMyProfile] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
		
			// Instantiating the ValidatorBean and validating the UpdateUserRequest bean.
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(updateMyProfileRequest);
			
			// if any property is not a valid property in UpdateUserRequest then sending corresponding error message
			if(validatorBean.isNotValid()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.USER_ERROR_UPDATE_PROFILE_FAILED);
				response.setData(validatorBean.getMessage());
			}else{
				
				// converting client request to DAO bean
				User user = ConvertJsonBeanToDAOBean.updateUserJsonToDAOBean(updateMyProfileRequest);
				
				// calling to  updateMyProfile to update user profile.
				int userId = adminDao.updateMyProfile(user);
				
				/*
				 *  if status is >=1 means success 
				 *  else request is fail
				 */
				if(userId >= 1){
					
					// Instantiating getUserRequest instance
					GetUserRequest getUserRequest = new GetUserRequest();
					getUserRequest.setUserId(user.getUserId());
					
					// Creating broadcast object to send mail related details to publisher
					MailBroadCast broadCast = new MailBroadCast();
					
					// setting the template variable as map key
					Map<String,String> templateMap = new HashMap<>();
					String userFname = user.getFirstName();
					templateMap.put(CommonConstants.USER_FNAME_C, userFname);

					HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
					
					URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
					
					// Preparing URL to create new password
					String loginLink = url+"/";
					
					// Adding link to forgot password template
					templateMap.put("loginLink", loginLink);
					
					String imgUrl = url+"/fileUpload/loadApplicationImage?imageName=uploaded_logo.png";
					
					templateMap.put("imgUrl", imgUrl);
					
					// Getting subject from properties and adding to broadcast object
					broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.UPDATE_PROFILE_MAIL_SUBJECT));
					
					// Preparing mail content from free marker template 
					broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.UPDATE_PROFILE_MAIL_CONTENT),templateMap));
					broadCast.setToEmail(updateMyProfileRequest.getEmailId());
					
					// Instantiating mail publisher
					MailPublisher publisher = new MailPublisher();
					
					// Broad casting mail
					boolean mailFlag = publisher.publishEmail(broadCast);
					
					if(mailFlag){
						// Activity log
						// Auditing user creation details
						auditActivityLog(loggedInUserId, loggedInUserId, "Emailed", "User profile update email has been sent", "6");
					}
					
					auditActivityLog(loggedInUserId, loggedInUserId, "Updated", "Updated user profile", "6");
					
					// Preparing success response object
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.USER_INFO_UPDATE_PROFILE_SUCCESS);
					response.setData(getUserDetails(getUserRequest, isSuper, loggedInUserId, timezone).getData());
					
				}else{
					// Preparing failure response object
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.USER_ERROR_UPDATE_PROFILE_FAILED);
				}
			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_UPDATE_PROFILE_FAILED, logger, e);
		}
		
		logger.info("[END] [updateMyProfile] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response filterByCustomer(int customerId) throws VEMAppException {
		/**
		 * Need to implement it by the time of customer implementation
		 */
		return null;
	}

	@Override
	public Response validateUserDetails(String userName, String password, int isSuper, int loggedInUserId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [validateUserDetails] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		// Initialization of userId
		String key = "";
		
		// Initialization of user id
		int userId = 0;
		
		try {
			
			// Calling DAO Method to validate user details
			key = adminDao.validateUserDetails(userName, ConfigurationUtils.generateEncryptedPassword(userName, password));
			
			// Splitting the key with separator '~'
			userId = Integer.parseInt(key.split("~")[0]);
			
			// Instantiating the get user response object
			GetUserResponse userResponse = null;
			
			/*
			 *  Getting user details object by user id 
			 *  if user id is grater than 0
			 */
			if (userId > 0){
				
				// Preparing get user request to get user details by user id
				GetUserRequest getUserRequest = new GetUserRequest();
				getUserRequest.setUserId(userId);
				
				// Getting user details
				userResponse = (GetUserResponse) getUserDetails(getUserRequest, isSuper, loggedInUserId, timeZone).getData();
				
			}
			
			// validating the key value and assigning corresponding error codes to it
			if(StringUtils.equals(key.split("~")[1], CommonConstants.DbFlags.LOGIN_SUCCESS.toString())){
				
				// Preparing success response
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.USER_INFO_USER_VALIDATE_SUCCESS);
				response.setData(userResponse);
				
			}else if(StringUtils.equals(key.split("~")[1], CommonConstants.DbFlags.INCREMENTED_FAIL_COUNT.toString())){
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_INCREMENT_FAIL_COUNT_FAILURE);
				response.setData(userResponse);
				
			}else if(StringUtils.equals(key.split("~")[1], CommonConstants.DbFlags.USER_GOT_LOCKED.toString())){
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_USER_GOT_LOCKED_FAILURE);
				response.setData(userResponse);
			}else if(StringUtils.equals(key.split("~")[1], CommonConstants.DbFlags.ERROR.toString())){
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			}
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_USER_VALIDATE_FAILED, logger, e);
		}
		
		logger.info("[END] [validateUserDetails] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public boolean isUserLocked(String email) throws VEMAppException {
		
		// flag initialization
		boolean flag = false;
		
		try {
			
			/*
			 * Calling DAO method isUserLocked to 
			 * Check whether the user is locked or unlocked
			 */
			int status = adminDao.isUserLocked(email);
			
			/*
			 * status > 0 means user is locked
			 */
			if (status > 0)	
				flag = true;
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHECK_IS_LOCKED_FAILED, logger, e);
		}
		return flag;
	}

	@Override
	public boolean isUserEligibleToReleaseLock(String email) throws VEMAppException {

		// flag initialization
		boolean flag = false;
		
		try {
			
			/*
			 * Calling DAO method isUserEligibleToReleaseLock to 
			 * check whether the user is eligible to release lock or not
			 */
			int status = adminDao.isUserEligibleToReleaseLock(email);
			
			/*
			 * status > 0 means user lock has been released
			 */
			if (status > 0)	
				flag = true;
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_RELEASE_LOCK_FAILED, logger, e);
		}
		return flag;
	}

	@Override
	public boolean isEmailExist(String email) throws VEMAppException {
		
		// flag initialization
		boolean flag = false;
		
		try {
			
			/*
			 * Calling DAO method isEmailExist to 
			 * check whether the user is exist in our records or not
			 */
			int status = adminDao.isEmailExist(email);
			
			/*
			 * status > 0 means user found
			 * else not found
			 */
			if (status > 0)	
				flag = true;
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_RELEASE_LOCK_FAILED, logger, e);
		}
		return flag;
	}
	
	@Override
	public GetUserResponse getUserDetailsByMail(String email) throws VEMAppException {

		logger.info("[BEGIN] [getUserDetailsByMail] [SERVICE LAYER]");
		
		// Declaration of user 
		User user;
		
		// Declaration userDetails instances
		GetUserResponse userDetails;
		
		try {
			
			// Getting user details from Database  
			user = adminDao.getUserDetailsByEmail(email);
			
			// Converting DAO user object to UI respect object
			userDetails = ConvertJsonBeanToDAOBean.convertUserDetailsDaoToUserResponse(user);
		
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_GET_FAILED, logger, e);
		}
		
		logger.info("[END] [getUserDetailsByMail] [SERVICE LAYER]");
		
		return userDetails;
	}

	@Override
	public Response validateSecurityQuestions(String phoneNumber, String token)
			throws VEMAppException {

		logger.info("[BEGIN] [validateSecurityQuestions] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		// Initialization of userId
		String key = "";
		
		// Initialization of user id
		int userId = 0;
		
		try {
			
			// Calling DAO Method to validate user details
			key = adminDao.validateSecurityQuestions(phoneNumber, token);
			
			// Splitting the key with separator '~'
			userId = Integer.parseInt(key.split("~")[0]);
			
			// Instantiating the get user response object
			GetUserResponse userResponse = null;
			
			/*
			 *  Getting user details object by user id 
			 *  if user id is grater than 0
			 */
			if (userId > 0){
				
				// Preparing get user request to get user details by user id
				GetUserRequest getUserRequest = new GetUserRequest();
				getUserRequest.setUserId(userId);
				
			}
			
			// validating the key value and assigning corresponding error codes to it
			if(StringUtils.equals(key.split("~")[1], CommonConstants.DbFlags.VALIED_SECURITY_QUESTIONS.toString())){
				
				// Preparing success response
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.USER_INFO_USER_VALIDATE_SUCCESS);
				response.setData(userResponse);
				
			}else if(StringUtils.equals(key.split("~")[1], CommonConstants.DbFlags.INCREMENTED_FAIL_COUNT.toString())){
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_INCREMENT_FAIL_COUNT_FAILURE);
				response.setData(userResponse);
				
			}else if(StringUtils.equals(key.split("~")[1], CommonConstants.DbFlags.MAX_ATTEMPTS_OVER_FOR_SECURITY_QUESTIONS.toString())){
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_USER_MAX_ATTEMPTS_OVER_FOR_SECURITY_QUESTIONS_FAILURE);
				response.setData(userResponse);
			}else if(StringUtils.equals(key.split("~")[1], CommonConstants.DbFlags.INVALID_TOKEN.toString())){
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_INVALID_TOKEN);
				response.setData(userResponse);
			}else if(StringUtils.equals(key.split("~")[1], CommonConstants.DbFlags.ERROR.toString())){
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			}
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_USER_VALIDATE_FAILED, logger, e);
		}
		
		logger.info("[END] [validateSecurityQuestions] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getUserProfileInfo(GetUserRequest getUserRequest, int isSuper, int loggedInUserId, String timeZone) throws VEMAppException {
		
		logger.info("[BEGIN] [getUserProfileInfo] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		try {
			
			/* Instantiating the ValidatorBean and validating the GetUserRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(getUserRequest);
			
			// if any property is not a valid property in GetUserRequest then sending corresponding error message 
			if(validatorBean.isNotValid()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				// Getting user details from Database
				User user = adminDao.getUserDetails(CommonConstants.FetchUserDetailsByType.USER_ID.toString(), getUserRequest.getUserId()+"", isSuper, loggedInUserId,timeZone);
				
				// Converting dao user object to UI respect object
				GetUserResponse userDetails = ConvertJsonBeanToDAOBean.convertUserDetailsDaoToUserResponse(user);
				
				// Creating request bean
				CustomersRequestBean request = new CustomersRequestBean();
				
				// Setting userId
				request.setUserId(getUserRequest.getUserId()+"");
				
				// Fetching customers list by user id
				Object customersList = customerDao.getUserCustomersList(request).getData();
				
				// Setting customers list			
				userDetails.setCustomersList(customersList);
					
				// Preparing success response object
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.USER_INFO_GET_USER_SUCCESS);
				response.setData(userDetails);
			}
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_GET_FAILED, logger, e);
		}
		
		logger.info("[END] [getUserProfileInfo] [SERVICE LAYER]");
		
		return response;
	}

	@Override
	public boolean isTokenExpired(String token, String tokenType) throws VEMAppException {
		
		logger.info("[END] [isTokenExpired] [SERVICE LAYER]");
		
		// flag initialization
		boolean flag = false;
		
		try {
			
			/*
			 * Calling DAO method isTokenExpired to 
			 * check whether the token is expired or not
			 */
			int status = adminDao.isTokenExpired(token, tokenType);
			
			/*
			 * status = 1 means token active
			 * else status = 2 means token expired
			 */
			if (status == 1)	
				flag = false;
			else if(status == 2) 
				flag = true;
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.TOKEN_EXPIRY_CHECK_FAILED, logger, e);
		}
		
		logger.info("[END] [isTokenExpired] [SERVICE LAYER]");
		
		return flag;
	}

	@Override
	public Response getSitesGroupsByCustomers(int selectedUserId, int loggedInUserId, int isSuper, String customerIds) throws VEMAppException {
		logger.info("[BEGIN] [getSitesGroupsByCustomers] [UserService SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store list of groups.
		JSONArray groupSites;
		
		try {
			
			//Calling the DAO layer getSitesGroupsByCustomers() method.
			groupSites = adminDao.getSitesGroupsByCustomers(selectedUserId, loggedInUserId, isSuper, customerIds);
			
			/* if groupSites is not null means the get groups and sites list request is
			 *  success
			 *  else fail.
			 */
			if(groupSites!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_SITES_BY_GROUP_IDS_SUCCESS);
				response.setData(groupSites);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_SITES_BY_GROUP_IDS_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GET_SITES_BY_GROUP_IDS_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_SITES_BY_GROUP_IDS_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [getSitesGroupsByCustomers] [UserService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response userUploadTemplate(String fileName,HttpSession session) {
		
		logger.info("AdminServiceImpl UploadUsersTemplate start");
	    Map<Integer,String> excelHeaderValues=new HashMap<Integer,String>();
	    BulkUploadResponse bulkUploadResponse = new BulkUploadResponse();
		Response response = new Response();
		// Creating userDetails object to store user details
		Object userDetails ;
	    int rowCounter=0;
	    int failedCounter=0;
	    int successCounter=0,totalCount=0;
	    String cellValue="",result="",errors="",failedFlag="";
	    int maxNumOfCells=0;
		int errorFlag=0,cellType=0,rowEmptyCounter=0;
		String firstName="",lastName="",title="",email="",role="",phone="",reportPrefer="",assginGroup="",assignSites="",assignCustomers="",checkRowEmptyFlag="No";
		int reportPref=0 ,alertPrefe=0,isOptedSendEmail=0;
	    File file=null;
	    
	    BulkUploadUserRequest bulkUploadUserRequest = new BulkUploadUserRequest();
	    try {
		file=new File(fileName);	
			excelHeaderValues.put(0,"First Name*");
			excelHeaderValues.put(1,"Last Name*");
			excelHeaderValues.put(2,"Title");
			excelHeaderValues.put(3,"Email*");
			excelHeaderValues.put(4,"Phone*");
			excelHeaderValues.put(5,"Role*");
			excelHeaderValues.put(6,"Report Preference*");
			excelHeaderValues.put(7,"Alert Preference*");
			excelHeaderValues.put(8,"Assign Customers");
			excelHeaderValues.put(9,"Assign Group");
			excelHeaderValues.put(10,"Assign Locations");
			excelHeaderValues.put(11,"Send Account Creation Email");
			excelHeaderValues.put(12,"Result");
			
			FileInputStream fis = new FileInputStream(file);
			Workbook workbook = null;
			if(fileName.toLowerCase().endsWith("xlsx")){
			workbook = new XSSFWorkbook(fis);
			}else if(fileName.toLowerCase().endsWith("xls")){
			workbook = new HSSFWorkbook(fis);
			}
			CellStyle cellStyle = workbook.createCellStyle();        
			Font font = workbook.createFont();
			font.setFontHeightInPoints((short)13);
			font.setColor(IndexedColors.RED.getIndex());
			cellStyle.setFont(font);
			cellStyle.setWrapText(true);      
			CellStyle cellStyleForSuccess = workbook.createCellStyle();        
			Font fontForSuccess = workbook.createFont();
			fontForSuccess.setFontHeightInPoints((short)13);
			fontForSuccess.setColor(IndexedColors.GREEN.getIndex());
			cellStyleForSuccess.setFont(fontForSuccess);         
			int numberOfSheets = workbook.getNumberOfSheets(); 
			FileOutputStream output_file;
			for(int i=0; i < numberOfSheets; i++){
			Sheet sheet = workbook.getSheetAt(i);
			sheet.setColumnWidth(13, 20000);
			Iterator<Row> rowIterator = sheet.iterator();
			rowCounter=0;
			rowEmptyCounter=0;
			 Row row = null;
			 List<User> usersList = new ArrayList<>();
		         while (i <= sheet.getLastRowNum()) {
		        	 rowCounter=rowCounter+1;
						row = sheet.getRow(i++);
						Cell cellForResult=null;
						 if(rowCounter==1){
				       			if( row.getCell(12) == null ){
				       				cellForResult = row.createCell(12);
				 	                 } else {
				 	                	cellForResult = row.getCell(12);
				 	                 }
				       			cellForResult.setCellValue("Result");
				 		     }
						
						if(rowCounter>1){
				        	 checkRowEmptyFlag=checkRowEmpty(row, "UploadUsersTemplate");
				         }
						if(checkRowEmptyFlag.equalsIgnoreCase("No")){
				        	 errors="";
					         errorFlag=0;
					         if(rowCounter==1)
					        	 maxNumOfCells= row.getLastCellNum();
					         for( int cellCounter = 0; cellCounter < maxNumOfCells; cellCounter ++){
					        	 Cell cell=null;
					        	 if( row.getCell(cellCounter) == null ){
				                     cell = row.createCell(cellCounter);
				                 } else {
				                     cell = row.getCell(cellCounter);
				                 }
					        	 cellType = cell.getCellType();
				                 if (cellType == XSSFCell.CELL_TYPE_STRING) {
				                	 cellValue=cell.getStringCellValue().trim();
				                 } else if (cellType == XSSFCell.CELL_TYPE_NUMERIC) {
				                	 cell.setCellType(Cell.CELL_TYPE_STRING);
				 	 				cellValue=cell.getStringCellValue();
				                 } else if (cellType == XSSFCell.CELL_TYPE_BOOLEAN) {
				                	 cellValue=cell.getBooleanCellValue()+"".trim();
				                 } else if (cellType == XSSFCell.CELL_TYPE_BLANK) {
				                	 cellValue="";
				                 }
					        	if(rowCounter==1 && !cellValue.trim().equalsIgnoreCase(excelHeaderValues.get(cellCounter))){
					        		 response.setStatus("wrong format");
					        		 response.setCode("500");
					        		 response.setData(bulkUploadResponse);
					        		 return response;
				                 }
					        	 if(rowCounter>1){
					        		 CellStyle style = workbook.createCellStyle();
					        		 style.setFillForegroundColor(IndexedColors.RED.getIndex());
					        		 style.setTopBorderColor(IndexedColors.RED.getIndex());
					        		 style.setBottomBorderColor(IndexedColors.RED.getIndex());
					        		 style.setBorderBottom(IndexedColors.RED.getIndex());
					        		 style.setLeftBorderColor(IndexedColors.RED.getIndex());
				        		     style.setBorderLeft(IndexedColors.RED.getIndex());
				        		     style.setRightBorderColor(IndexedColors.RED.getIndex());
					        		 style.setBorderRight(IndexedColors.RED.getIndex()); 
					        		 style.setBorderTop(IndexedColors.RED.getIndex());
					                 if(cellCounter==0){ 
					                	 totalCount = totalCount+1;
					                	 bulkUploadResponse.setTotalCount(totalCount);
				                         if(cellValue.trim().length()==0){
							                  errors=errors+",Please provide first name";
							                  cell.setCellStyle(cellStyle);
							                  row.getCell(0).setCellStyle(style);
							                  errorFlag=1;
							                  failedFlag = "Not a valid record";
							                  bulkUploadResponse.setInsertRecord(failedFlag);
                                              response.setData(bulkUploadResponse);
							                  firstName="";
							              }
				                         else if(cellValue.length()>32){
				                             errors=errors+",First name should not exceed 32 characters.";
				                             cell.setCellStyle(cellStyle);
				                             row.getCell(0).setCellStyle(style);
				                             errorFlag=1;
				                             failedFlag = "Not a valid record";
				                             bulkUploadResponse.setInsertRecord(failedFlag);
				                             response.setData(bulkUploadResponse);
				                             firstName="";
				                         }
					                 }
					                 if(cellCounter==1){                             
				                         if(cellValue.trim().length()==0){
							                  errors=errors+",Please provide last name";
							                  cell.setCellStyle(cellStyle);
							                  row.getCell(1).setCellStyle(style);
							                  errorFlag=1;
							                  failedFlag = "Not a valid record";
							                  bulkUploadResponse.setInsertRecord(failedFlag);
							                  response.setData(bulkUploadResponse);
							                  lastName="";
							              }
				                         else if(cellValue.length()>32){
				                             errors=errors+",Last name should not exceed 32 characters.";
				                             cell.setCellStyle(cellStyle);
				                             row.getCell(1).setCellStyle(style);
				                             errorFlag=1;
				                             failedFlag = "Not a valid record";
				                             bulkUploadResponse.setInsertRecord(failedFlag);
				                             response.setData(bulkUploadResponse);
				                             title="";
				                         }
					                 }if(cellCounter==2){                             
				                         if(cellValue.length()>20){
							                  errors=errors+",Title should not exceed 20 characters.";
							                  cell.setCellStyle(cellStyle);
							                  row.getCell(2).setCellStyle(style);
							                  errorFlag=1;
							                  failedFlag = "Not a valid record";
							                  bulkUploadResponse.setInsertRecord(failedFlag);
							                  response.setData(bulkUploadResponse);
							                  title="";
							              }
					                 }
					                 if(cellCounter==3){                             
				                         if(cellValue.trim().length()==0){
							                  errors=errors+",Please provide email id";
							                  cell.setCellStyle(cellStyle);
							                  row.getCell(3).setCellStyle(style);
							                  errorFlag=1;
							                  failedFlag = "Not a valid record";
							                  bulkUploadResponse.setInsertRecord(failedFlag);
							                  response.setData(bulkUploadResponse);
							                  email="";
							              }
				                         else if(CommonUtility.emailValidate(cellValue.trim()).equals("Yes")){//special Charectors check
							                  errors=errors+",Please provide valid email id";
							                  cell.setCellStyle(cellStyle);
							                  row.getCell(3).setCellStyle(style);
							                  errorFlag=1;
							                  failedFlag = "Not a valid record";
							                  bulkUploadResponse.setInsertRecord(failedFlag);
							                  response.setData(bulkUploadResponse);
							                  email="";
							              }
					                 }
					                 if(cellCounter==4){                             
				                         if(cellValue.trim().length()==0){
							                  errors=errors+",Please provide phone number";
							                  cell.setCellStyle(cellStyle);
							                  row.getCell(4).setCellStyle(style);
							                  errorFlag=1;
							                  failedFlag = "Not a valid record";
							                  bulkUploadResponse.setInsertRecord(failedFlag);
							                  response.setData(bulkUploadResponse);
							                  phone="";
							              }
				                         else if(CommonUtility.phoneValidate(cellValue).equals("Yes")){
				 								errors=errors+",Please provide valid phone number";
				 								cell.setCellStyle(cellStyle);
				 								row.getCell(4).setCellStyle(style);
				 								errorFlag=1;
				 								failedFlag = "Not a valid record";
				 								bulkUploadResponse.setInsertRecord(failedFlag);
				 								response.setData(bulkUploadResponse);
				 								phone="";
				 							}
					                 }
					                 if(cellCounter==5){                             
				                         if(cellValue.trim().length()==0){
							                  errors=errors+",Please provide a role name";
							                  cell.setCellStyle(cellStyle);
							                  row.getCell(5).setCellStyle(style);
							                  errorFlag=1;
							                  failedFlag = "Not a valid record";
							                  bulkUploadResponse.setInsertRecord(failedFlag);
							                  response.setData(bulkUploadResponse);
							                  role="";
							              }
					                 }
					                 if(cellCounter==6){                             
				                         if(cellValue.trim().length()==0){
							                  errors=errors+",Please provide report preference";
							                  cell.setCellStyle(cellStyle);
							                  row.getCell(6).setCellStyle(style);
							                  errorFlag=1;
							                  failedFlag = "Not a valid record";
							                  bulkUploadResponse.setInsertRecord(failedFlag);
							                  response.setData(bulkUploadResponse);
							                  reportPrefer="";
							              }
					                 }
					                 else if(cellCounter==12){
				                         if(errors.trim().length()>0 && errorFlag==1){
				                        	errors=errors.substring(1)+".";
					                        cell.setCellValue(errors.replaceAll(",", ",\n"));
					                        cell.setCellStyle(cellStyle);
					                        row.setHeight((short) ((short)row.getHeight()*(short)errors.substring(1).split(",").length));
					                     }else{
					                    	 row.getCell(12).setCellValue(USER_FAILED);
							       			 row.getCell(12).setCellStyle(cellStyle);
					                     }

					                 }
				                 }
				             } 
				         
						if(row.getRowNum()==0){
						      continue; //just skip the rows if row number is 0 or 1
						     }
						output_file =new FileOutputStream(new File(fileName));  //Open FileOutputStream to write updates
						workbook.write(output_file); //write changes
						output_file.close();
						if(errors.trim().length()==0 && errorFlag==0 && rowCounter>1){
							User user = new User();
							user.setFirstName(row.getCell(0).getStringCellValue());
							user.setLastName(row.getCell(1).getStringCellValue());
							user.setTitle(row.getCell(2).getStringCellValue());
							user.setEmailId(row.getCell(3).getStringCellValue());
							DataFormatter formatter = new DataFormatter();
							user.setPhoneNumber(formatter.formatCellValue(row.getCell(4)));
							String roleName = row.getCell(5).getStringCellValue();
							String assignedCustomers = formatter.formatCellValue(row.getCell(8));
							String assignedGroups = formatter.formatCellValue(row.getCell(9));
							String assginedSites = formatter.formatCellValue(row.getCell(10));
							String reportPreference =formatter.formatCellValue(row.getCell(6));
							if(reportPreference == CommonConstants.ReportPreferences.None.toString()){
								reportPreference = "0";							
							}
							if(StringUtils.equalsIgnoreCase(reportPreference, CommonConstants.ReportPreferences.Weekly.toString())){
								reportPreference = "1";							
							}
							if(StringUtils.equalsIgnoreCase(reportPreference, CommonConstants.ReportPreferences.Monthly.toString())){
								reportPreference = "2";							
							}
							if(StringUtils.equalsIgnoreCase(reportPreference, CommonConstants.ReportPreferences.Quarterly.toString())){
								reportPreference = "3";							
							}
							if(StringUtils.equalsIgnoreCase(reportPreference, CommonConstants.ReportPreferences.Yearly.toString())){
								reportPreference = "4";							
							}
							user.setReportPreference(Integer.parseInt(reportPreference));
							
							String alertPreference = (formatter.formatCellValue(row.getCell(7)));
							if(StringUtils.equalsIgnoreCase(alertPreference,CommonConstants.AlertPreferences.NONE.toString())){
								alertPreference = "0";							
							}
							if(StringUtils.equalsIgnoreCase(alertPreference,CommonConstants.AlertPreferences.Email.toString())){
								alertPreference = "1";							
							}
							if(StringUtils.equalsIgnoreCase(alertPreference,CommonConstants.AlertPreferences.SMS.toString())){
								alertPreference = "2";							
							}
							user.setAlertPreference(Integer.parseInt(alertPreference.trim()));
							String isOptedToSendMail = (formatter.formatCellValue(row.getCell(11)));
							if(StringUtils.equalsIgnoreCase(isOptedToSendMail.toUpperCase(), "FALSE")){
								isOptedToSendMail = "0";							
							}
							else{
								isOptedToSendMail = "1";							
							}
							user.setIsOptedToSendMail(Integer.parseInt(isOptedToSendMail));
							UploadUserData excelUserData = new UploadUserData(); 
							String userData = excelUserData.getUploadUserData(roleName,assignedCustomers, assignedGroups, assginedSites,adminDao);
							int roleId =Integer.parseInt(userData.split("~")[0]);
							String customerId = userData.split("~")[1];
							String groupId = userData.split("~")[2];
							String siteId =userData.split("~")[3];
							user.setRoleId(roleId);
							user.setCustomers(customerId);
							user.setGroupId(groupId);
							user.setLocationId(siteId);
							user.setIsActive(1);
							if(adminDao.isEmailExist(row.getCell(3).getStringCellValue()) <= 0) {
								String randomKey = RandomStringUtils.randomAlphanumeric(30);
								user.setIsTokenExpired(1);
								user.setSecurityFailCount(1);
								user.setAuthToken(randomKey);
							}
								else{
									failedFlag = "Not a valid record";
					                bulkUploadResponse.setInsertRecord(failedFlag);
									row.getCell(12).setCellValue(USER_FAILED);
				       			    row.getCell(12).setCellStyle(cellStyle);
				       				continue;
								}
							String password = ConfigurationUtils.generateRandomPassword(row.getCell(3).getStringCellValue());
							String rest_username = PasswordEncryptionProviderUsingSHA.get_SHA_1_HashedString(
									ConfigurationUtils.generateRandomString(), user.getEmailId());
							String rest_password = PasswordEncryptionProviderUsingSHA.
							get_SHA_1_HashedString(
									ConfigurationUtils.generateRandomString(), user.getEmailId());
							user.setPassword(password);
							user.setRestUserName(rest_username);
							user.setRestPassword(rest_password);
			       			if( row.getCell(12) != null ){
			       				row.getCell(12).setCellValue(USER_FAILED);
			       			    row.getCell(12).setCellStyle(cellStyle);
			 	                 } else {
			 	                	row.getCell(12).setCellValue(USER_FAILED);
			 	                	row.getCell(12).setCellStyle(cellStyle);
			 	                 }
							try{    
							
									 if(user != null){
										 row.getCell(12).setCellValue("SUCCESS");
										 row.getCell(12).setCellStyle(cellStyleForSuccess);
								         usersList.add(user);
									   }
								}
								catch(Exception e){
									continue;
								}
						}
						}
						else{
				        	 rowEmptyCounter=rowEmptyCounter+1;
				         }
						
					}
		      // Getting the session details
		 		GetUserResponse userDetail = (GetUserResponse) session.getAttribute("eaiUserDetails");
		            if(usersList != null)
		            {
		            	long startTime = System.currentTimeMillis();
		            	successCounter = usersList.size();
		            	bulkUploadResponse.setSucessCount(successCounter);
		            	for (User user : usersList) {
							bulkUploadResponse.setRecordResponse("Record Inserted");
		            		int usersInserted = adminDao.saveUserDetails(user,userDetail.getUserId());
		            		System.out.println("@@@@usersInserted@@@@" + usersInserted);
		        		}
		            	long stopTime = System.currentTimeMillis();
					    long elapsedTime = stopTime - startTime;
					    System.out.println("@@@@BatchUpdate elapsedTime@@@@" + elapsedTime);
		            }
		            
		            
		            response.setData(bulkUploadResponse);
					output_file =new FileOutputStream(new File(fileName));  //Open FileOutputStream to write updates
					workbook.write(output_file); //write changes
					output_file.close();
					if(usersList != null){
					UploadUserTemplateThread mailUtility= new UploadUserTemplateThread(usersList);
				    Thread mailThread= new Thread(mailUtility);
				    mailThread.start();
					}
				}
			
		}catch (Exception e) {
			logger.error("AdminServiceImpl UploadUsersTemplate Error:",e);
			result="F";
		}
		logger.info("AdminServiceImpl UploadUsersTemplate end");
		return response;
	}

	public String checkRowEmpty(Row row,String template) throws Exception{
	    logger.info("AdminServiceImpl checkRowEmpty start");
	    String result="No";
	    boolean b=false;
	    ArrayList<String> rowData_al=null;
	    String cellValue="";
	    int lastCellIndex=0;
	    try{
	    	rowData_al=new ArrayList<String>();
	    	 if(template.equalsIgnoreCase("UploadUsersTemplate")){
	    		lastCellIndex=12;
	    	}
    		for(int i=0;i<lastCellIndex;i++){
    			Cell cell=null;
	        	 if( row.getCell(i) == null ){
                     cell = row.createCell(i);
                 } else {
                     cell = row.getCell(i);
                 }
	        	 //getting the cell data
	        	 int cellType = cell.getCellType();
                 if (cellType == XSSFCell.CELL_TYPE_STRING) {
                	 cellValue=cell.getStringCellValue().trim();
                 } else if (cellType == XSSFCell.CELL_TYPE_NUMERIC) {
 	 				cell.setCellType(Cell.CELL_TYPE_STRING);
	 				cellValue=cell.getStringCellValue();
                 } else if (cellType == XSSFCell.CELL_TYPE_BOOLEAN) {
                	 cellValue=cell.getBooleanCellValue()+"".trim();
                 } else if (cellType == XSSFCell.CELL_TYPE_BLANK) {
                	 cellValue="";
                 }
                 if(cellValue.trim().length()>0)
                	 rowData_al.add(cellValue);
    		}
	    	if(rowData_al.size()==0)
	    		result="Yes";
	    }catch (Exception e) {
	        logger.error("AdminServiceImpl checkRowEmpty Error:",e);
	        throw new Exception(e);
	    }
	    logger.info("AdminServiceImpl checkRowEmpty end");
	    return result;
	}
	
	/**
	 * auditActivityLogForEmail: common method to audit activity log for email
	 * 
	 * @param loggedInuserId
	 * @param serviceSpecificId
	 * @param userAction
	 * @param desc
	 */
	public void auditActivityLog(int loggedInuserId, int serviceSpecificId, String userAction, String desc, String serviceId){
		AuditRequest auditRequest = new AuditRequest();
		auditRequest.setUserId(loggedInuserId);
		auditRequest.setUserAction(userAction);
		auditRequest.setLocation("");
		auditRequest.setServiceId(serviceId); // for user module
		auditRequest.setDescription(desc);
		auditRequest.setServiceSpecificId(serviceSpecificId);
		auditRequest.setOutFlag("");
		auditRequest.setOutErrMsg("");
		auditDao.insertAuditLog(auditRequest);
	}
	
	@Override
	public String getCompanyLogo(int userId) throws VEMAppException{
		String companyLogo = "";
		try {
			companyLogo = adminDao.getCompanyLogo(userId);
		} catch (Exception e) {
			throw new VEMAppException("Error in getCompanyLogo",e);
		}
		return companyLogo;
	}

	@Override
	public Response updateEmailId(UpdateEmailIdRequest updateEmailIdRequest, String loggedInUserName) throws VEMAppException {
		logger.info("[BEGIN] [updateEmailId] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();

		// Initializing flag to send mail or not
		boolean mailFlag = false;
		boolean mailFlagForActivityLog = false;
		
		try {
		
			/* Instantiating the ValidatorBean and validating the UpdateUserRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(updateEmailIdRequest);
			
			// if any property is not a valid property in GetUserRequest then sending corresponding error message
			if(validatorBean.isNotValid()){ 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			} else if(adminDao.isEmailExist(updateEmailIdRequest.getToChangeEmailId()) <= 0) {
					// Checking the logged in user password is correct or not
					if((adminDao.getCurrentPassword(updateEmailIdRequest.getLoggedInUserId())).equals(ConfigurationUtils.generateEncryptedPassword(loggedInUserName, updateEmailIdRequest.getLoggedInUserPassword()))){
						// Setting 1, always it is 1 while updating user email id
						updateEmailIdRequest.setIsOptedToSendMail(1);
						
						// Checking for the user object is opted to send mail creating user or not
						if (updateEmailIdRequest.getIsOptedToSendMail() == 1) {
							
							// Creating broadcast object to send mail related details to publisher
							MailBroadCast broadCast = new MailBroadCast();
							
							HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
							
							URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
							
							String imgUrl = url+
									"/fileUpload/loadApplicationImage?imageName=uploaded_logo.png";
							
							// Creating broadcast object to send mail related details to publisher
							MailBroadCast deactivateUserBroadcast = new MailBroadCast();
							
							// setting the template variable as map key
							Map<String,String> templateMapDeactivate = new HashMap<>();
							templateMapDeactivate.put(CommonConstants.USER_FNAME_C, updateEmailIdRequest.getFirstName());
							templateMapDeactivate.put(CommonConstants.USER_LNAME_C, updateEmailIdRequest.getLastName());
							
							templateMapDeactivate.put("imgUrl", imgUrl);
							
							// Getting subject from properties and adding to broadcast object
							deactivateUserBroadcast.setSubject(ConfigurationUtils.getConfig(CommonConstants.DELETE_MAIL_SUBJECT));
							
							// Preparing mail content from free marker template 
							deactivateUserBroadcast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.DELETE_MAIL_CONTENT),templateMapDeactivate));
							deactivateUserBroadcast.setToEmail(updateEmailIdRequest.getFromEmailId());
							
							// Instantiating mail publisher
							MailPublisher publisherDeactivateUser = new MailPublisher();
							
							//Publishing Deactivation Email
							publisherDeactivateUser.publishEmail(deactivateUserBroadcast);
							
							// setting the template variable as map key
							Map<String,String> templateMap = new HashMap<>();
							String userName = updateEmailIdRequest.getToChangeEmailId();
							String userFname = updateEmailIdRequest.getFirstName();
							String userLname = updateEmailIdRequest.getLastName();
							
							templateMap.put("imgUrl", imgUrl);
							templateMap.put(CommonConstants.USER_FNAME_C, userFname);
							templateMap.put(CommonConstants.USER_LNAME_C, userLname);
							templateMap.put(CommonConstants.USER_NAME, userName);
							
							// Generating random key / auth token
							String randomKey = RandomStringUtils.randomAlphanumeric(30);
	
							// Preparing URL to create new password
							String resetLink = url+"/firstTimePassword/?token="+randomKey;
							
							// Adding link to forgot password template
							templateMap.put("resetLink", resetLink);
							
							// Getting subject from properties and adding to broadcast object
							broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.NEW_MAIL_SUBJECT));
							
							// Preparing mail content from free marker template 
							broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.NEW_MAIL_CONTENT),templateMap));
							broadCast.setToEmail(updateEmailIdRequest.getToChangeEmailId());
							
							// Instantiating mail publisher
							MailPublisher publisher = new MailPublisher();
							
							// Broad casting mail
							mailFlag = publisher.publishEmail(broadCast);
							
							/*
							 * True means mail has been sent successfully 
							 */
							if(mailFlag){
								
								mailFlagForActivityLog = true;
	
								// Setting random key
								updateEmailIdRequest.setAuthToken(randomKey);
							}
							
						}else{
							mailFlag = true;
						}
						
						/*
						 * Saving user details into db
						 */
						if (mailFlag) {
							
							// calling to updateUserDetails to update user details
							int userId = adminDao.updateEmailId(updateEmailIdRequest);
							
							/*
							 * if status is greater than equal to 1 means success 
							 * else request is fail
							 */
							if(userId >= 1){
								
								// Creating updateUserResponse instance
								UpdateUserResponse updateUserResponse = new UpdateUserResponse();
								updateUserResponse.setUserId(userId);
				
								// Activity log
								// Auditing user creation details
								if(mailFlagForActivityLog){
									auditActivityLog(updateEmailIdRequest.getLoggedInUserId(), userId, "Emailed","Account deactivation email has been sent", "6");
									auditActivityLog(updateEmailIdRequest.getLoggedInUserId(), userId, "Emailed","New account creation email has been sent", "6");
								}
								
								// Preparing success response object
								response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
								response.setCode(ErrorCodes.USER_INFO_UPDATE_EMAIL_SUCCESS);
								response.setData(updateUserResponse);
								
							} else{
								// Preparing failure response object
								response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
								response.setCode(ErrorCodes.USER_ERROR_UPDATE_EMAIL_FAILED);
							}
						} else{
							// Preparing failure response object
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.USER_ERROR_INVALID_EMAIL_ADDRESS_FAILED);
						}
					}else{
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.INCORRECT_PASSWORD_DETAILS);
					}
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.WARN_EMAIL_ALREADY_EXIST);
				}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_UPDATE_EMAIL_FAILED, logger, e);
		}
		
		logger.info("[END] [updateEmailId] [SERVICE LAYER]");
		
		return response;
	}
}
	
