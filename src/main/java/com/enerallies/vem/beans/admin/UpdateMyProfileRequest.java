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

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.validators.admin.PhoneNumber;

/**
 * File Name : UpdateMyProfileRequest 
 * UpdateUserRequest: is the bean to update user 
 *
 * @author Nagarjuna Eerla(neerla@ctepl.com).
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        10-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 19-10-2016		Nagarjuna Eerla		File Created
 *
 */
public class UpdateMyProfileRequest {
	
	/** This property is used to hold the user id */
	@NotNull(message = ErrorCodes.WARN_USER_ID_NOT_NULL)
	private Integer userId;
	
	/** This property is used to hold the user id. */
	@NotEmpty(message = ErrorCodes.WARN_FIRST_NAME_NOT_EMPTY)
	@Size(max=32, message=ErrorCodes.WARN_FIRST_NAME_SIZE)
	private String firstName;
	
	/** This property is used to hold the email id. */
	private String emailId;
	
	/** This property is used to hold the  last name */
	@Size(max=32, message=ErrorCodes.WARN_LAST_NAME_SIZE)
	private String lastName;

	/** This property is used to hold the contact number of the user. */
	@PhoneNumber(message=ErrorCodes.WARN_PHONE_INVALID)
	private String phoneNumber;
	
	/** This property is used to hold the alert preferences */
	private int alertPreference;
	
	/** This property is used to hold the value of report preference for the user  */
	private int reportPreference;
	
	/** This property is used to hold the value of report level for the user  */
	private int reportLevel;
	
/*********************************** Getters ***********************/

	/**
	 * gets contact number
	 * @return String
	 */
	public String getPhoneNumber() {
		return phoneNumber;
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
	 * Gets the alert preference information
	 * @return
	 */
	public int getAlertPreference() {
		return alertPreference;
	}
	
	/**
	 * Gets the user id
	 * @return
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Gets emailId
	 * @return
	 */
	public String getEmailId() {
		return emailId;
	}
	

	/**
	 * Gets the report preference
	 * @return
	 */
	public int getReportPreference() {
		return reportPreference;
	}

	//*********************************** Setters ***********************
	
	/**
	 * sets contact number
	 * @param contactNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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
	 * Sets the alert preference information
	 * @param alertPreference
	 */
	public void setAlertPreference(int alertPreference) {
		this.alertPreference = alertPreference;
	}
	
	/**
	 * Sets the user id
	 * @param userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Sets user id
	 * @param userId
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * Sets email email id
	 * @param emailId
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	/**
	 * Sets report preferences of the user
	 * @param reportPreference
	 */
	public void setReportPreference(int reportPreference) {
		this.reportPreference = reportPreference;
	}

	public int getReportLevel() {
		return reportLevel;
	}

	public void setReportLevel(int reportLevel) {
		this.reportLevel = reportLevel;
	}
	
}
