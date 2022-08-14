/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.service.admin;

import javax.servlet.http.HttpSession;

import com.enerallies.vem.beans.admin.ChangePasswordRequest;
import com.enerallies.vem.beans.admin.CreateNewPasswordRequest;
import com.enerallies.vem.beans.admin.CreateUserRequest;
import com.enerallies.vem.beans.admin.DeleteUserRequest;
import com.enerallies.vem.beans.admin.FirstTimePasswordChangeRequest;
import com.enerallies.vem.beans.admin.GetUserRequest;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.admin.UpdateEmailIdRequest;
import com.enerallies.vem.beans.admin.UpdateMyProfileRequest;
import com.enerallies.vem.beans.admin.UpdateUserRequest;
import com.enerallies.vem.beans.admin.UserActivityRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : AdminService 
 * 
 * AdminService: is used declare all the admin operation methods
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 03-08-2016		Nagarjuna Eerla		File Created
 *
 */
public interface AdminService {

   /**
	 * saveUser : Creates an user in Database by using user request object
	 * 
	 * @param userRequest
	 * @param loggedInUser
	 * @return
	 * @throws VEMAppException
	 */
   public Response saveUser(CreateUserRequest userRequest, int loggedInUser) throws VEMAppException;

   /**
    * updateEmailId: it updates email ID
    * 
    * @param updateEmailIdRequest
    * @return
    * @throws VEMAppException
    */
   public Response updateEmailId(UpdateEmailIdRequest updateEmailIdRequest, String loggedInUserName) throws VEMAppException;
   
   /**
    * createNewPassword : Creates new Password by UserId
    * @param createNewPasswordRequest
    * @return
    * @throws Exception
    */
   public Response createNewPassword(CreateNewPasswordRequest createNewPasswordRequest) throws VEMAppException;
   
   /**
    * forgotPassword: it get resets the existing password and sends the reset email.
    * @param emailId
    * @param firstName
    * @return
    * @throws Exception
    */
   public Response forgotPassword(String emailId, String firstName, int userId) throws VEMAppException;
   
   /**
    * changePassword: it changes the password
    * @param changePasswordRequest
    * @return
    * @throws VEMAppException
    */
   public Response changePassword(ChangePasswordRequest changePasswordRequest, int isSuper, int loggedInUserId, String timeZone) throws VEMAppException;

   /**
    * getUserDetails(GetUserRequest getUserRequest) : Gets the VEM user details
    * @param getUserRequest
    * @param isSuper
    * @param loggedInUserId
    * @return
    * @throws VEMAppException
    */
   public Response getUserDetails(GetUserRequest getUserRequest, int isSuper, int loggedInUserId, String timezone) throws VEMAppException;
   
   /**
    * updateUserDetails : update VEM user details
    * @param updateUserRequest
    * @return
    * @throws VEMAppException
    */
   public Response updateUserDetails(UpdateUserRequest updateUserRequest, int loggedInUser) throws VEMAppException;
   
   /**
    * getAllUsersDetails() : Gets all the available users
    * 
    * @param type
    * @param value
    * @param isSuper
    * @param loggedInUserId
    * @return
    * @throws VEMAppException
    */
   public Response getAllUsersDetails(String type, String value, int isSuper, int loggedInUserId, String timezone) throws VEMAppException;
   
   /**
    * deleteUser: Delete the VEM user
    * @param deleteUserRequest
    * @return
    * @throws VEMAppException
    */
   public Response deleteUser(DeleteUserRequest deleteUserRequest, int isSuper, int loggedInUserId, String timeZone) throws VEMAppException;
   
   /**
    * userAcitivity: in-activates an user
    * @param userActivityRequest
    * @return
    * @throws VEMAppException
    */
   public Response userAcitivity(UserActivityRequest userActivityRequest, int loggedInUser) throws VEMAppException;
   
   /**
    * updateMyProfile : updates VEM user profile
    * @param updateMyProfileRequest
    * @param isSuper
    * @param loggedInUserId
    * @return
    * @throws VEMAppException
    */
   public Response updateMyProfile(UpdateMyProfileRequest updateMyProfileRequest, int isSuper, int loggedInUserId, String timezone) throws VEMAppException;

   /**
    * 
    * filterByCustomer : It filters the users by customer 
    * 
    * @param customerId
    * @return
    * @throws VEMAppException
    */
   public Response filterByCustomer(int customerId) throws VEMAppException;
   
   /**
    * validateUserDetails : it validates the user details
    * 
    * @param userName
    * @param password
    * @return
    * @throws VEMAppException
    */
   public Response validateUserDetails(String userName, String password, int isSuper, int loggedInUserId, String timeZone) throws VEMAppException;
   
   /**
    * isUserLocked : checks is user locked / unlocked
    * 
    * @param email
    * @return
    * @throws VEMAppException
    */
   public boolean isUserLocked(String email) throws VEMAppException;
   
   /**
    * isUserEligibleToReleaseLock : checking for is user eligible to release lock
    * 
    * @param email
    * @return
    * @throws VEMAppException
    */
   public boolean isUserEligibleToReleaseLock(String email) throws VEMAppException;
   
   /**
    * 
    * getUserDetailsByMail : Gets the user details by email id(user name)
    * 
    * @param email
    * @return
    * @throws VEMAppException
    */
   public GetUserResponse getUserDetailsByMail(String email) throws VEMAppException;
   
   /**
    * isEmailExist : it checks whether the user exist in records or not 
    * 
    * @param email
    * @return
    * @throws VEMAppException
    */
   public boolean isEmailExist(String email) throws VEMAppException;

   /**
    * validateSecurityQuestions : validates the user details
    * 
    * @param userName
    * @param phoneNumber
    * @param token
    * @return
    * @throws VEMAppException
    */
   public Response validateSecurityQuestions(String phoneNumber, String token) throws VEMAppException;
   
   /**
    * getUserProfileInfo : gets user profile info with customers
    * 
    * @param getUserRequest
    * @param isSuper
    * @param loggedInUserId
    * @return
    * @throws VEMAppException
    */
   public Response getUserProfileInfo(GetUserRequest getUserRequest, int isSuper, int loggedInUserId, String timeZone) throws VEMAppException;
   
   /**
    * isTokenExpired: is used to check whether the token is expired or not
    * 
    * @param token
    * @param tokenType
    * @return
    * @throws VEMAppException
    */
   public boolean isTokenExpired(String token, String tokenType) throws VEMAppException;
   
   /**
	 * getSitesGroupsByCustomers: is used to list groups and sites by customers.
	 * 
	 * @param selectedUserId
	 * @param loggedInUserId
	 * @param isSuper
	 * @param customerIds
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getSitesGroupsByCustomers(int selectedUserId, int loggedInUserId, int isSuper, String customerIds) throws VEMAppException;
	
   public Response userUploadTemplate(String fileName,HttpSession session);

   public String getCompanyLogo(int userId) throws VEMAppException;

}
