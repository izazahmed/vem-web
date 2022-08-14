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
 * File Name : CreateUserRequest 
 * CreateUserRequest: is used to transfer UserRequest Data from client side
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
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

public class CreateUserRequest {
	
	/** This property is used to hold the user id. */
	@NotEmpty(message = ErrorCodes.WARN_FIRST_NAME_NOT_EMPTY)
	@Size(max=32, message=ErrorCodes.WARN_FIRST_NAME_SIZE)
	private String firstName;
	
	/** This property is used to hold the  last name */
	@NotEmpty(message = ErrorCodes.WARN_LAST_NAME_NOT_EMPTY)
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
	//@Size(max=42, message=ErrorCodes.WARN_EMAIL_MAX_EXCEDED)
	private String emailId;

	/** This property is used to hold the title of the user. */
	@Size(max=20, message=ErrorCodes.WARN_TITLE_SIZE)
	private String title;
	
	/** This property is used to hold the role of the user */
	@NotNull(message = ErrorCodes.WARN_ROLE_ID_NOT_EMPTY)
	private Integer roleId;
	
	/** this property holds the customers list */
	private String customers;
	
	/** This property is used to hold the group id */
	private String groupId;
	
	/** This property is used to hold the location id */
	private String locationId;
	
	/** This property is used to hold the alert preferences */
	private int alertPreference;
	
	/** This property is used to hold the value of is opted to send mail while creating the user */
	private int isOptedToSendMail;
	
	/** This property is used to hold the value of report preference for the user  */
	private int reportPreference;
	
	/** This property is used to hold the value of report level for the user  */
	private int reportLevel;
	
	/** This property is used to hold the createdBy for the user  */
	private int createdBy;

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
	public Integer getRoleId() {
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
	 * Gets the customers
	 * @return
	 */
	public String getCustomers() {
		return customers;
	}
	

	/**
	 * Gets the createdBy
	 * @return
	 */
	public int getCreatedBy() {
		return createdBy;
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
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	/**
	 * Sets the alert preference information
	 * 
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
	 * Sets the customers
	 * @param customers
	 */
	public void setCustomers(String customers) {
		this.customers = customers;
	}

	/**
	 * Sets the createdBy
	 * @param createdBy
	 */
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
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
