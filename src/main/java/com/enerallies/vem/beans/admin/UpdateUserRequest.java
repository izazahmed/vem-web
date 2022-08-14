/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.admin;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.validators.admin.PhoneNumber;
/**
 * File Name : UpdateUserRequest 
 * UpdateUserRequest: is the bean to update user 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        10-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 10-08-2016		Nagarjuna Eerla		File Created
 *
 */
public class UpdateUserRequest {

	/** This property is used to hold the user id */
	@NotNull(message = ErrorCodes.WARN_USER_ID_NOT_NULL)
	private Integer userId;
	
	/** This property is used to hold the user id. */
	@NotEmpty(message = ErrorCodes.WARN_FIRST_NAME_NOT_EMPTY)
	@Size(max=32, message=ErrorCodes.WARN_FIRST_NAME_SIZE)
	private String firstName;
	
	/** This property is used to hold the  last name */
	@Size(max=32, message=ErrorCodes.WARN_LAST_NAME_SIZE)
	private String lastName;
	
	/** This property is used to hold the first name */
	private int isActive;

	/** This property is used to hold the contact number of the user. */
	@PhoneNumber(message=ErrorCodes.WARN_PHONE_INVALID)
	private String phoneNumber;
	
	/** This property is used to hold the email id of the user. */
	//@NotEmpty(message = ErrorCodes.WARN_EMAIL_NOT_EMPTY)
	//@Email(message = ErrorCodes.WARN_EMAIL_INVALID)
	private String emailId;

	/** This property is used to hold the title of the user. */
	@Size(max=20, message=ErrorCodes.WARN_TITLE_SIZE)
	private String title;
	
	/** This property is used to hold the role of the user */
	@NotNull(message = ErrorCodes.WARN_ROLE_ID_NOT_EMPTY)
	private Integer roleId;
	
	/** This property is used to hold the customers */
	private String customers;
	
	/** This property is used to hold the group id */
	private String groupId;
	
	/** This property is used to hold the location id */
	private String locationId;
	
	/** This property is used to hold the alert preferences */
	private int alertPreference;
	
	/** This property holds the current password **/
	private String currentPassword;
	
	/** This property holds the new password **/
	private String newPassword;
	
	/** This property holds the repeat password **/
	private String repeatPassword;
	
	/** This property is used to hold the user name */
	private String uname;
	
	/** This property is used to hold the value of is opted to send mail while creating the user */
	private int isOptedToSendMail;
	
	/** This property is used to hold the value of report preference for the user  */
	private int reportPreference;
	
	/** This property is used to hold the value of report level for the user  */
	private int reportLevel;
	
	/** This property is used to hold the user who is updated the user. */
	private int updateBy;
	
	/** This property is used to hold the property to say the user is opted for all the groups or not  */
	private int isUserOptedForAllGroups;
	
	/*********************************** Getters ***********************/
	
	/**
	 * Gets the value of isOptedToSendMail property
	 * @return
	 */
	public int getIsOptedToSendMail() {
		return isOptedToSendMail;
	}

	/**
	 * Gets the value of reportPreference property
	 * @return
	 */
	public int getReportPreference() {
		return reportPreference;
	}
	
	/**
	 * Gets the current password
	 * @return
	 */
	public String getCurrentPassword() {
		return currentPassword;
	}

	/**
	 * Gets new password
	 * @return
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * Gets repeated password
	 * @return
	 */
	public String getRepeatPassword() {
		return repeatPassword;
	}

	/**
	 * gets contact number
	 * @return String
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	/**
	 * gets email id
	 * @return String
	 */
	public String getEmailId() {
		return emailId;
	}
	
	
	/**
	 * Gets the first name of the user
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Gets the last name of the user
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Gets the isActive status
	 * @return
	 */
	public int getIsActive() {
		return isActive;
	}

	/**
	 * Gets the role of the user
	 * @return
	 */
	public int getRoleId() {
		return roleId;
	}

	/**
	 * Gets the alert preference information
	 * @return
	 */
	public int getAlertPreference() {
		return alertPreference;
	}

	/**
	 * Gets the group id
	 * @return
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Gets the location id
	 * @return
	 */
	public String getLocationId() {
		return locationId;
	}
	
	/**
	 * Gets the location of the id
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Gets the user id
	 * @return
	 */
	public int getUserId() {
		return userId;
	}
	
	/**
	 * gets the customers
	 * @return
	 */
	public String getCustomers() {
		return customers;
	}
	
	/**
	 * Gets the unique user name
	 * @return
	 */
	public String getUname() {
		return uname;
	}
	
	/**
	 * gets the updated by
	 * @return
	 */
	public int getUpdateBy() {
		return updateBy;
	}
	
	//*********************************** Setters ***********************

	/**
	 * Sets the value of isOptedToSendMail property
	 * @param isOptedToSendMail
	 */
	public void setIsOptedToSendMail(int isOptedToSendMail) {
		this.isOptedToSendMail = isOptedToSendMail;
	}

	/**
	 * Sets the value of reportPreference property
	 * @param reportPreference
	 */
	public void setReportPreference(int reportPreference) {
		this.reportPreference = reportPreference;
	}
	
	/**
	 * sets contact number
	 * @param contactNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * sets email id
	 * @param emailId
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * Sets the first name of the user
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the first name of the user
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * Sets the isActive status 
	 * @param isActive
	 */
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	/**
	 * Sets the role of the user
	 * @param role
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	/**
	 * Sets the alert preference information
	 * @param alertPreference
	 */
	public void setAlertPreference(int alertPreference) {
		this.alertPreference = alertPreference;
	}

	/**
	 * Sets the group id
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * Sets the Location id
	 * @param locationId
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * sets the title of the user
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Sets the user id
	 * @param userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Sets the customers
	 * @param customers
	 */
	public void setCustomers(String customers) {
		this.customers = customers;
	}

	/**
	 * Sets the current password
	 * @param currentPassword
	 */
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	/**
	 * Sets new password
	 * @param newPassword
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * Sets repeated password
	 * @param repeatPassword
	 */
	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}

	/**
	 * Sets user id
	 * @param userId
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	/**
	 * Sets the unique user name
	 * @param uname
	 */
	public void setUname(String uname) {
		this.uname = uname;
	}
	
	/**
	 * sets the user updated by
	 * @param updateBy
	 */
	public void setUpdateBy(int updateBy) {
		this.updateBy = updateBy;
	}

	public int getReportLevel() {
		return reportLevel;
	}

	public void setReportLevel(int reportLevel) {
		this.reportLevel = reportLevel;
	}

	public int getIsUserOptedForAllGroups() {
		return isUserOptedForAllGroups;
	}

	public void setIsUserOptedForAllGroups(int isUserOptedForAllGroups) {
		this.isUserOptedForAllGroups = isUserOptedForAllGroups;
	}
}
