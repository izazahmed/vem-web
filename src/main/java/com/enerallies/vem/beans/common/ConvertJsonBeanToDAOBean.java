/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.enerallies.vem.beans.admin.ChangePassword;
import com.enerallies.vem.beans.admin.ChangePasswordRequest;
import com.enerallies.vem.beans.admin.CreateNewPasswordRequest;
import com.enerallies.vem.beans.admin.CreateUserRequest;
import com.enerallies.vem.beans.admin.FirstTimePasswordChangeRequest;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.admin.UpdateMyProfileRequest;
import com.enerallies.vem.beans.admin.UpdateUserRequest;
import com.enerallies.vem.beans.admin.User;
import com.enerallies.vem.beans.customers.CreateCustomerRequest;
import com.enerallies.vem.beans.customers.Customer;
import com.enerallies.vem.beans.customers.UpdateCustomerRequest;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.DatesUtil;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : ConvertJsonBeanToDAOBean 
 * 
 * ConvertJsonBeanToDAOBean: It converts the client request json data to DAO Beans
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
 * 01	03-08-2016			Nagarjuna Eerla		File Created
 * 02   10-08-2016		    Goush Basha		    copyRoleRequestToRole() method has added.
 */
public class ConvertJsonBeanToDAOBean {
	// Adding private constructor to hide implicit public one
	private ConvertJsonBeanToDAOBean(){
		
	}
	
	// Getting logger
	private static final Logger logger = Logger.getLogger(ConvertJsonBeanToDAOBean.class);
	
	/**
	 * saveUserJsonToDAOBean: It converts user JSON data to DAO bean
	 * @param userRequest
	 * @return
	 * @throws VEMAppException 
	 */
	public static User saveUserJsonToDAOBean(CreateUserRequest userRequest) throws VEMAppException{
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [saveUserJsonToDAOBean] [Bean Conversion Layer]");
		}
		User user = new User();
		try {
			user.setFirstName(userRequest.getFirstName());
			user.setLastName(userRequest.getLastName());
			user.setTitle(userRequest.getTitle());
			user.setUname(userRequest.getEmailId());
			user.setEmailId(userRequest.getEmailId());
			if(CommonUtility.isNull(userRequest.getEmailId()).isEmpty())
				user.setPassword("CSOPassword");
			else
				user.setPassword(ConfigurationUtils.generateRandomPassword(userRequest.getEmailId()));
			user.setPhoneNumber(userRequest.getPhoneNumber());
			user.setIsActive(userRequest.getIsActive());
			user.setRoleId(userRequest.getRoleId());
			user.setCustomers(userRequest.getCustomers());
			user.setLocationId(userRequest.getLocationId());
			user.setGroupId(userRequest.getGroupId());
			user.setCreateBy(userRequest.getCreatedBy());
			user.setCreateDate(DatesUtil.getCurrentDateAndTime());
			user.setIsOptedToSendMail(userRequest.getIsOptedToSendMail());
			user.setReportPreference(userRequest.getReportPreference());
			user.setAlertPreference(userRequest.getAlertPreference());
			user.setReportLevel(userRequest.getReportLevel());
			user.setIsUserOptedForAllGroups(userRequest.getIsUserOptedForAllGroups());
		} catch (Exception e) {
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_SAVE_FAILED, logger, e);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [saveUserJsonToDAOBean] [Bean Conversion Layer]");
		}
		return user;
	}
	
	/**
	 * changePasswordRequest : Method converts the changePasswordRequest to DAO bean
	 * @param changePasswordRequest
	 * @return
	 * @throws VEMAppException
	 */
	public static ChangePassword convertChangePwdRequestToDAOBean(ChangePasswordRequest changePasswordRequest) throws VEMAppException {
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [convertChangePwdRequestToDAOBean] [Bean Conversion Layer]");
		}
		ChangePassword changePassword = new ChangePassword();
		try {
			if(ConfigurationUtils.comparePasswords(changePasswordRequest.getNewPwd(), changePasswordRequest.getRepeatPwd())){
				
				changePassword.setPassword(changePasswordRequest.getNewPwd()); // Encryption is doing at service layer
				changePassword.setUserId(changePasswordRequest.getUserId());
				changePassword.setUpdateBy(1);
				changePassword.setUpdateDate(DatesUtil.getCurrentDateAndTime()); 
			
			}else{
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED);
			}
			
		} catch (Exception e) {
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED, logger, e);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [convertChangePwdRequestToDAOBean] [Bean Conversion Layer]");
		}
		return changePassword;
	}

	/**
	 * convertCreateNewPwdRequestToDAOBean : Method converts the convertCreateNewPwdRequestToDAOBean to DAO bean
	 * 
	 * @param createNewPasswordRequest
	 * @return
	 * @throws VEMAppException
	 */
	public static ChangePassword convertCreateNewPwdRequestToDAOBean(
			CreateNewPasswordRequest createNewPasswordRequest) throws VEMAppException {
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [convertCreateNewPwdRequestToDAOBean] [Bean Conversion Layer]");
		}
		ChangePassword changePassword = new ChangePassword();
		try {
			
			if(ConfigurationUtils.comparePasswords(createNewPasswordRequest.getChoosePwd(), createNewPasswordRequest.getRepeatPwd())){
				changePassword.setPassword(createNewPasswordRequest.getRepeatPwd()); 
				changePassword.setAuthToken(createNewPasswordRequest.getAuthToken());
				changePassword.setUpdateBy(1);
				changePassword.setUpdateDate(DatesUtil.getCurrentDateAndTime());
			}else{
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED);
			}
		
		} catch (Exception e) {
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED, logger, e);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [convertCreateNewPwdRequestToDAOBean] [Bean Conversion Layer]");
		}
		return changePassword;
	}

	/**
	 * Method Name : ConvertUserDetailsDaoToUserResponse
	 * it converts DAO bean to client format bean response
	 * @param user
	 * @return
	 */
	public static GetUserResponse convertUserDetailsDaoToUserResponse(User user) {
		GetUserResponse userResponse = new GetUserResponse();
		userResponse.setUserId(user.getUserId());
		userResponse.setEmailId(user.getEmailId());
		userResponse.setUname(user.getUname());
		userResponse.setFirstName(user.getFirstName());
		userResponse.setLastName(user.getLastName());
		userResponse.setAlertPreference(user.getAlertPreference());
		userResponse.setIsActive(user.getIsActive());
		userResponse.setCustomers((user.getCustomers()==null)?"":user.getCustomers());
		userResponse.setGroupId(user.getGroupId());
		userResponse.setLocationId(user.getLocationId());
		userResponse.setRoleId(user.getRoleId());
		userResponse.setRoleName(user.getRoleName());
		userResponse.setPhoneNumber(user.getPhoneNumber());
		userResponse.setCreateBy(user.getCreateBy());
		userResponse.setCreateDate(user.getCreateDate());
		userResponse.setUpdateBy(user.getUpdateBy());
		userResponse.setUpdateDate(user.getUpdateDate());
		userResponse.setTitle(user.getTitle());
		userResponse.setIsEai(user.getIsEai());
		userResponse.setIsCSO(user.getIsCSO());
		userResponse.setIsSuper(user.getIsSuper());
		userResponse.setIsFirstTimeUser(user.getIsFirstTimeUser());
		userResponse.setLastLoginInfo(DatesUtil.convertTimestampToString(
					(user.getLastLoginInfo() == null)?null:user.getLastLoginInfo().toString(), 
					CommonConstants.DATE_DEFAULT_FORMAT)
				);
		userResponse.setFailureCount(user.getFailureCount());
		userResponse.setIsOptedToSendMail(user.getIsOptedToSendMail());
		userResponse.setIsTokenExpired(user.getIsTokenExpired());
		userResponse.setReportPreference(user.getReportPreference());
		userResponse.setIsEmailSent(user.getIsEmailSent());
		userResponse.setCreateActivityLogFlag(user.getCreateActivityLogFlag());
		
		// splitting role permissions and assigning to map
		Map<String, String> rolePermissions = new HashMap<>();
		
		// Checking if the user has the permissions or not
		if(StringUtils.isNotEmpty(user.getRolePermissions()))
		for (String permission : user.getRolePermissions().split(",")) {
			if(StringUtils.isNotEmpty(permission))
			rolePermissions.put(permission.split("~")[0], "Read-Only".equals(permission.split("~")[1]) ? "1" : 
														  "Read-Write".equals(permission.split("~")[1])?"2":
														  "Performance".equals(permission.split("~")[1])?"3": "4");
		}
		userResponse.setRolePermissions(rolePermissions);
		userResponse.setRestUserName(user.getRestUserName());
		userResponse.setRestPassword(user.getRestPassword());
		userResponse.setActivityLogCount(user.getActivityLogCount());
		userResponse.setSiteCount(user.getSiteCount());
		userResponse.setGroupCount(user.getGroupCount());
		userResponse.setCompanyLogo(user.getCompanyLogo());
		userResponse.setReportLevel(user.getReportLevel());
		userResponse.setIsUserOptedForAllGroups(user.getIsUserOptedForAllGroups());
		
		// Empty the group id's and site id's if the user selects all groups and all sites
		if(userResponse.getIsUserOptedForAllGroups() == 1){
			userResponse.setGroupId("");
			userResponse.setLocationId("");
		}
		
		return userResponse;
	}
	
	/**
	 * saveUserJsonToDAOBean: It converts customer JSON data to DAO bean
	 * @param CreeateCustomerRequest
	 * @return
	 * @throws VEMAppException 
	 */
	public static Customer saveCustomerJsonToDAOBean(CreateCustomerRequest createCustomerRequest) throws VEMAppException{
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [saveCustomerJsonToDAOBean] [Bean Conversion Layer]");
		}
		Customer customer = new Customer();
		try {
			customer.setCustomerCode(createCustomerRequest.getCustomerCode());
			customer.setCompanyName(createCustomerRequest.getCompanyName());
			customer.setAddressLine1(createCustomerRequest.getAddressLine1());
			customer.setAddressLine2(createCustomerRequest.getAddressLine2());
			customer.setCity(createCustomerRequest.getCity());
			customer.setState(createCustomerRequest.getState());
			customer.setPostalCode(createCustomerRequest.getPostalCode());
			customer.setCompanyLogo(createCustomerRequest.getCompanyLogo());
			customer.setDegreePrefereces(createCustomerRequest.getDegreePrefereces());
			customer.setThermostatePreferenceFanOn(createCustomerRequest.getThermostatePreferenceFanOn());
			customer.setThermostatePreferenceFanAuto(createCustomerRequest.getThermostatePreferenceFanAuto());
			customer.setThermostatePreferenceHvacAuto(createCustomerRequest.getThermostatePreferenceHvacAuto());
			customer.setThermostatePreferenceResetHold(createCustomerRequest.getThermostatePreferenceResetHold());
			customer.setNightlyScheduleDownload(createCustomerRequest.getNightlyScheduleDownload());
			customer.setThermostateMaxSetPoint(createCustomerRequest.getThermostateMaxSetPoint());
			customer.setThermostateMinSetPoint(createCustomerRequest.getThermostateMinSetPoint());
			customer.setLockPref(createCustomerRequest.getLockPref());
			customer.setCustomerStatus(createCustomerRequest.getCustomerStatus());
			customer.setFileLocation(createCustomerRequest.getFileLocation());
			customer.setFileName(createCustomerRequest.getFileName());
			customer.setCreateBy(1); //TODO: We have to assign here with logged in userID
			customer.setCreateDate(DatesUtil.getCurrentDateAndTime());
		} catch (Exception e) {
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_SAVE_FAILED, logger, e);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [saveCustomerJsonToDAOBean] [Bean Conversion Layer]");
		}
		return customer;
	}
	
	/**
	 * updateCustomerJsonToDAOBean : Converts updateCustomerRequest to User DAO bean
	 * @param updateUserRequest
	 * @return
	 * @throws VEMAppException
	 */
	public static Customer updateCustomerJsonToDAOBean(UpdateCustomerRequest updateCustomerRequest) throws VEMAppException{
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [updateUserJsonToDAOBean] [Bean Conversion Layer]");
		}
		Customer customer = new Customer();
		try {
			    customer.setCustomerId(updateCustomerRequest.getCustomerId());
				customer.setCustomerCode(updateCustomerRequest.getCustomerCode());
				customer.setCompanyName(updateCustomerRequest.getCompanyName());
				customer.setAddressLine1(updateCustomerRequest.getAddressLine1());
				customer.setAddressLine2(updateCustomerRequest.getAddressLine2());
				customer.setCity(updateCustomerRequest.getCity());
				customer.setState(updateCustomerRequest.getState());
				customer.setPostalCode(updateCustomerRequest.getPostalCode());
				customer.setCompanyLogo(updateCustomerRequest.getCompanyLogo());
				customer.setDegreePrefereces(updateCustomerRequest.getDegreePrefereces());
				customer.setThermostatePreferenceFanOn(updateCustomerRequest.getThermostatePreferenceFanOn());
				customer.setThermostatePreferenceFanAuto(updateCustomerRequest.getThermostatePreferenceFanAuto());
				customer.setThermostatePreferenceHvacAuto(updateCustomerRequest.getThermostatePreferenceHvacAuto());
				customer.setThermostatePreferenceResetHold(updateCustomerRequest.getThermostatePreferenceResetHold());
				customer.setNightlyScheduleDownload(updateCustomerRequest.getNightlyScheduleDownload());
				customer.setThermostateMaxSetPoint(updateCustomerRequest.getThermostateMaxSetPoint());
				customer.setThermostateMinSetPoint(updateCustomerRequest.getThermostateMinSetPoint());
				customer.setLockPref(updateCustomerRequest.getLockPref());
				customer.setCustomerStatus(updateCustomerRequest.getCustomerStatus());
				customer.setIsActive(updateCustomerRequest.getIsActive());
				customer.setCreateBy(1); //TODO: We have to assign here with logged in userID
				customer.setCreateDate(DatesUtil.getCurrentDateAndTime());
				customer.setFileLocation(updateCustomerRequest.getFileLocation());
				customer.setFileName(updateCustomerRequest.getFileName());
		} catch (Exception e) {
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ERROR_UPDATE_FAILED, logger, e);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[END] [updateCustomerJsonToDAOBean] [Bean Conversion Layer]");
		}
		return customer;
	}
	
	/**
	 * Method Name : ConvertUsersDetailsDaoToUserResponse
	 * it converts DAO bean to client format bean response
	 * @param usersList
	 * @return
	 */
	public static List<GetUserResponse> convertUsersDetailsDaoToUserResponse(List<User> usersList, int isSuper, int loggedInUserId) {
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [ConvertUsersDetailsDaoToUserResponse] [Bean Conversion Layer]");
		}
		// Creating list instance
		List<GetUserResponse> usersDetails = new ArrayList<>();
		
		// looping the users list
		for (User user : usersList) {
			
			// UserResponse object
			GetUserResponse userResponse = new GetUserResponse();
			
			// Setting all the properties
			userResponse.setUserId(user.getUserId());
			userResponse.setEmailId(user.getEmailId());
			userResponse.setUname(user.getUname());
			userResponse.setFirstName(user.getFirstName());
			userResponse.setLastName(user.getLastName());
			userResponse.setAlertPreference(user.getAlertPreference());
			userResponse.setIsActive(user.getIsActive());
			userResponse.setCustomers((user.getCustomers()==null)?"":user.getCustomers());
			userResponse.setGroupId(user.getGroupId());
			userResponse.setLocationId(user.getLocationId());
			userResponse.setRoleId(user.getRoleId());
			userResponse.setRoleName(user.getRoleName());
			userResponse.setPhoneNumber(user.getPhoneNumber());
			userResponse.setCreateBy(user.getCreateBy());
			userResponse.setCreateDate(user.getCreateDate());
			userResponse.setUpdateBy(user.getUpdateBy());
			userResponse.setUpdateDate(user.getUpdateDate());
			userResponse.setTitle(user.getTitle());
			userResponse.setIsEai(user.getIsEai());
			userResponse.setIsCSO(user.getIsCSO());
			userResponse.setIsSuper(user.getIsSuper());
			userResponse.setIsFirstTimeUser(user.getIsFirstTimeUser());
			userResponse.setLastLoginInfo(DatesUtil.convertTimestampToString(
						(user.getLastLoginInfo() == null)?null:user.getLastLoginInfo().toString(), 
						CommonConstants.DATE_DEFAULT_FORMAT));
			userResponse.setIsOptedToSendMail(user.getIsOptedToSendMail());
			userResponse.setReportPreference(user.getReportPreference());
			userResponse.setIsEmailSent(user.getIsEmailSent());
			userResponse.setCreateActivityLogFlag(user.getCreateActivityLogFlag());
			userResponse.setSiteCount(user.getSiteCount());
			userResponse.setGroupCount(user.getGroupCount());
			userResponse.setReportLevel(user.getReportLevel());
			// Adding user details to list
			usersDetails.add(userResponse);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[END] [ConvertUsersDetailsDaoToUserResponse] [Bean Conversion Layer]");
		}
		return usersDetails;
	}

	/**
	 * updateUserJsonToDAOBean : Converts updateUserRequest to User DAO bean
	 * @param updateUserRequest
	 * @return
	 * @throws VEMAppException
	 */
	public static User updateUserJsonToDAOBean(UpdateUserRequest updateUserRequest) throws VEMAppException{
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [updateUserJsonToDAOBean] [Bean Conversion Layer]");
		}
		User user = new User();
		try {
				user.setUserId(updateUserRequest.getUserId());
				user.setFirstName(updateUserRequest.getFirstName());
				user.setLastName(updateUserRequest.getLastName());
				user.setTitle(updateUserRequest.getTitle());
				user.setUname(updateUserRequest.getEmailId());
				user.setEmailId(updateUserRequest.getEmailId());
				user.setPhoneNumber(updateUserRequest.getPhoneNumber());
				user.setIsActive(updateUserRequest.getIsActive());
				user.setRoleId(updateUserRequest.getRoleId());
				user.setCustomers(updateUserRequest.getCustomers());
				user.setLocationId(updateUserRequest.getLocationId());
				user.setGroupId(updateUserRequest.getGroupId());
				user.setUpdateBy(updateUserRequest.getUpdateBy());
				user.setUpdateDate(DatesUtil.getCurrentDateAndTime());
				user.setAlertPreference(updateUserRequest.getAlertPreference());
				user.setIsOptedToSendMail(updateUserRequest.getIsOptedToSendMail());
				user.setReportPreference(updateUserRequest.getReportPreference());
				user.setReportLevel(updateUserRequest.getReportLevel());
				user.setIsUserOptedForAllGroups(updateUserRequest.getIsUserOptedForAllGroups());
		} catch (Exception e) {
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_SAVE_FAILED, logger, e);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[END] [updateUserJsonToDAOBean] [Bean Conversion Layer]");
		}
		return user;
	}

	/**
	 * convertNewFirstPwdRequestToDAOBean : it converts first time password request to change password format
	 * 
	 * @param firstTimePasswordChangeRequest
	 * @return
	 * @throws VEMAppException
	 */
	public static ChangePassword convertNewFirstPwdRequestToDAOBean(
			FirstTimePasswordChangeRequest firstTimePasswordChangeRequest) throws VEMAppException {
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [convertNewFirstPwdRequestToDAOBean] [Bean Conversion Layer]");
		}
		ChangePassword changePassword = new ChangePassword();
		try {
			
				changePassword.setAuthToken(firstTimePasswordChangeRequest.getToken());
				changePassword.setUpdateBy(1);
				changePassword.setUpdateDate(DatesUtil.getCurrentDateAndTime());
		
		} catch (Exception e) {
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED, logger, e);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [convertNewFirstPwdRequestToDAOBean] [Bean Conversion Layer]");
		}
		return changePassword;
	}
	
	/**
	 * updateUserJsonToDAOBean : Converts updateUserRequest to User DAO bean
	 * @param updateMyProfileRequest
	 * @return
	 * @throws VEMAppException
	 */
	public static User updateUserJsonToDAOBean(UpdateMyProfileRequest updateMyProfileRequest) throws VEMAppException{
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [updateUserJsonToDAOBean] [Bean Conversion Layer]");
		}
		User user = new User();
		try {
				user.setUserId(updateMyProfileRequest.getUserId());
				user.setFirstName(updateMyProfileRequest.getFirstName());
				user.setLastName(updateMyProfileRequest.getLastName());
				user.setPhoneNumber(updateMyProfileRequest.getPhoneNumber());
				user.setAlertPreference(updateMyProfileRequest.getAlertPreference());
				user.setReportPreference(updateMyProfileRequest.getReportPreference());
				user.setReportLevel(updateMyProfileRequest.getReportLevel());
				
		} catch (Exception e) {
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_SAVE_FAILED, logger, e);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[END] [updateUserJsonToDAOBean] [Bean Conversion Layer]");
		}
		return user;
	}
}
