/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.dao.admin;

import java.util.List;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.springframework.stereotype.Service;

import com.enerallies.vem.beans.admin.ChangePassword;
import com.enerallies.vem.beans.admin.Customer;
import com.enerallies.vem.beans.admin.Group;
import com.enerallies.vem.beans.admin.Site;
import com.enerallies.vem.beans.admin.UpdateEmailIdRequest;
import com.enerallies.vem.beans.admin.User;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : AdminDao 
 * 
 * AdminDao: is the Admin interface contains the prototypes of Admin Methods
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        02-08-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	02-08-2016			Nagarjuna Eerla		File Created
 *
 *
 */

@Service
public interface AdminDao {
	
	
   /**
    * setDataSource: This is the method to be used to initialize database resources i.e. connection.
    * @param dataSource
    */
   public void setDataSource(DataSource dataSource);

   /**
    * saveUserDetails: Creates an user
    * 
    * @param userRequest
    * @param loggedInUser
    * @return
    * @throws Exception
    */
   public int saveUserDetails(User userRequest, int loggedInUser) throws VEMAppException;
   
   /**
    * updateEmailId: it updates email ID
    * 
    * @param updateEmailIdRequest
    * @return
    * @throws VEMAppException
    */
   public int updateEmailId(UpdateEmailIdRequest updateEmailIdRequest) throws VEMAppException;
   
   /**
    * isEmailExist: it get resets the existing password and sends the reset email.
    * @param emailId
    * @return
    * @throws Exception
    */
   public int isEmailExist(String emailId) throws VEMAppException;

   
   /**
    * changePassword: it changes the password
    * @param changePassword
    * @return
    * @throws Exception
    */
   public int changePassword(ChangePassword changePassword) throws VEMAppException;
   
   /**
    * getCurrentPassword: gets current password
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public String getCurrentPassword(int userId) throws VEMAppException;

  /**
   * resetPasswordByMail: resets the password by email id.
   * 
   * @param emailId
   * @param password
   * @param randomKey
   * @return
   * @throws VEMAppException
   */
   public int resetPasswordByMail(String emailId, String password, String randomKey) throws VEMAppException;
   
   /**
    * getUserDetails(int userId): Method fetches the user details by type either EMAIL, USER_ID, TOKEN
    * 
    * @param type
    * @param value
    * @param isSuper
    * @param loggedInUserId
    * @return
    * @throws VEMAppException
    */
   public User getUserDetails(String type, String value, int isSuper, int loggedInUserId, String timezone) throws VEMAppException;
   
   /**
    * updateUserDetails(User user): Updates VEM User Details
    * @param user
    * @return
    * @throws VEMAppException
    */
   public int updateUserDetails(User user, int loggedInUser) throws VEMAppException;
   
   /**
    * getAllUsersDetails(): Gets all the VEM user details
    * 
    * @param type
    * @param value
    * @param isSuper
    * @param loggedInUserId
    * @return
    * @throws VEMAppException
    */
   public List<User> getAllUsersDetails(String type, String value, int isSuper, int loggedInUserId, String timezone) throws VEMAppException;
   
   /**
    * deleteUserDetails : Deletes VEM user details
    * @param userId
    * @return
    * @throws VEMAppException
    */
   public int deleteUserDetails(int userId, int loggedInUser) throws VEMAppException;
   
   /**
    * userAcitivity : Will activates and in-activates an user
    * @param userId
    * @param status
    * @return
    * @throws VEMAppException
    */
   public int userAcitivity(int userId, int status, int loggedInUser) throws VEMAppException;
   
   /**
    * updateMyProfile(User user): Updates VEM User profile
    * @param user
    * @return
    * @throws VEMAppException
    */
   public int updateMyProfile(User user) throws VEMAppException;
   
   /**
    * 
    * filterByCustomer : It filters the users by customer 
    * 
    * @param customerId
    * @return
    * @throws VEMAppException
    */
   public List<User> filterByCustomer(int customerId) throws VEMAppException;
 
   /**
    * validateUserDetails : it validates the user details
    * 
    * @param userName
    * @param password
    * @return
    * @throws VEMAppException
    */
   public String validateUserDetails(String userName, String password) throws VEMAppException;
   
   /**
    * isUserLocked : checks is user locked / unlocked
    * 
    * @param email
    * @return
    * @throws VEMAppException
    */
   public int isUserLocked(String email) throws VEMAppException;
   
   /**
    * isUserEligibleToReleaseLock : checking for user is eligible to release lock or not
    * 
    * @param email
    * @return
    * @throws VEMAppException
    */
   public int isUserEligibleToReleaseLock(String email) throws VEMAppException;

   /**
    * 
    * validateSecurityQuestions : validates the user details
    * 
    * @param userName
    * @param phoneNumber
    * @param token
    * @return
    * @throws VEMAppException
    */
   public String validateSecurityQuestions(String phoneNumber, String token) throws VEMAppException;
   
   /**
    * isTokenExpired: is used to check whether the token is expired or not
    * 
    * @param token
    * @param tokenType
    * @return
    * @throws VEMAppException
    */
   public int isTokenExpired(String token, String tokenType) throws VEMAppException;
   
	 /**
	  * getSitesGroupsByCustomers: is used to get all Sites and groups by customers from database.
	  * 
	  * @param selectedUserId
	  * @param loggedInUserId
	  * @param isSuper
	  * @param customerIds
	  * @return JSONArray
	  * @throws VEMAppException
	  */
	 public JSONArray getSitesGroupsByCustomers(int selectedUserId, int loggedInUserId, int isSuper, String customerIds) throws VEMAppException;
	
	 /***
	  * Uploading users Data through bulk import
	  * @param role
	  * @param assignedCustomers
	  * @param assginedGroup
	  * @param assignedSite
	  * @return
	  * @throws VEMAppException
	  */
	 public String uploadUserData(String role, String assignedCustomers,String assginedGroup, String assignedSite) 	throws VEMAppException;
	
	 /**
	  * getUserDetailsByEmail : it get the user details by User Email
	  * 
	  * @param emailId
	  * @return
	  * @throws VEMAppException
	  */
	public User getUserDetailsByEmail(String emailId) throws VEMAppException;
	
	/**
	 * getCustomerSiteIds: method is used to get customer id and site id when logged in user has the single site 
	 * 
	 * @param userId
	 * @param isSuper
	 * @param loggedInUserId
	 * @return
	 * @throws VEMAppException
	 */
	public String getCustomerSiteIds(int userId, int isSuper, int loggedInUserId) throws VEMAppException;
	
	/**
	 * getCustomers: gets customers by customer id's
	 * 
	 * @param customerIds
	 * @return
	 * @throws VEMAppException
	 */
	public List<Customer> getCustomers(String customerIds) throws VEMAppException;
	
	/**
	 * 
	 * getGroups: gets groups by group id's
	 * 
	 * @param groupIds
	 * @return
	 * @throws VEMAppException
	 */
	public List<Group> getGroups(String groupIds) throws VEMAppException;
	
	/**
	 * 
	 * getSites : gets sites by site id's
	 * 
	 * @param siteIds
	 * @return
	 * @throws VEMAppException
	 */
	public List<Site> getSites(String siteIds) throws VEMAppException;

	/**
	 * Getting company logo info
	 * 
	 * @param userId
	 * @return
	 * @throws VEMAppException
	 */
	public String getCompanyLogo(int userId) throws VEMAppException;
}
