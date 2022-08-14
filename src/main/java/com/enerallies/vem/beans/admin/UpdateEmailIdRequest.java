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

/**
 * File Name : UpdateEmailIdRequest 
 * UpdateEmailIdRequest: is used to update the user email ID Address
 *
 * @author Nagarjuna Eerla (neerla@ctepl.com).
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        15-05-2017
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 15-05-2017		Nagarjuna Eerla		File Created
 *
 */
public class UpdateEmailIdRequest {
	
	/** This property is used to hold the user id */
	@NotNull(message = ErrorCodes.WARN_USER_ID_NOT_NULL)
	private Integer userId;
	
	/** This property is having the previous email id */
	//@NotEmpty(message = ErrorCodes.WARN_EMAIL_NOT_EMPTY)
	//@Email(message = ErrorCodes.WARN_EMAIL_INVALID)
	private String fromEmailId;
	
	/** This property is used to hold the new email id. */
	//@NotEmpty(message = ErrorCodes.WARN_EMAIL_NOT_EMPTY)
	//@Email(message = ErrorCodes.WARN_EMAIL_INVALID)
	private String toChangeEmailId;
	
	/** This property holds the current password **/
	private String loggedInUserPassword;
	
	/** This property holds the logged In UserId **/
	private int loggedInUserId;
	
	/** This property is used to hold the value of is opted to send mail while creating the user */
	private int isOptedToSendMail;

	/** This property is used to hold the user id. */
	private String firstName;
	
	/** This property is used to hold the  last name */
	private String lastName;
	
	/** This property is used to hold the authToken */
	private String authToken;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFromEmailId() {
		return fromEmailId;
	}

	public void setFromEmailId(String fromEmailId) {
		this.fromEmailId = fromEmailId;
	}

	public String getToChangeEmailId() {
		return toChangeEmailId;
	}

	public void setToChangeEmailId(String toChangeEmailId) {
		this.toChangeEmailId = toChangeEmailId;
	}

	public String getLoggedInUserPassword() {
		return loggedInUserPassword;
	}

	public void setLoggedInUserPassword(String loggedInUserPassword) {
		this.loggedInUserPassword = loggedInUserPassword;
	}

	public int getIsOptedToSendMail() {
		return isOptedToSendMail;
	}

	public void setIsOptedToSendMail(int isOptedToSendMail) {
		this.isOptedToSendMail = isOptedToSendMail;
	}

	public int getLoggedInUserId() {
		return loggedInUserId;
	}

	public void setLoggedInUserId(int loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
}
