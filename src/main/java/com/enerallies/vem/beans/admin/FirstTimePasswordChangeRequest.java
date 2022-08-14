/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.admin;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.validators.admin.PhoneNumber;

/**
 * File Name : FirstTimePasswordChangeRequest 
 * FirstTimePasswordChangeRequest: is used to create first time password
 *
 * @author Nagarjuna Eerla.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        04-08-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	06-09-2016			Nagarjuna Eerla		File Created
 *
 */
public class FirstTimePasswordChangeRequest {
	
	/** This property holds the phoneNumber **/
	@PhoneNumber(message=ErrorCodes.WARN_PHONE_INVALID)
	private String phoneNumber;
	
	/** This property holds the token **/
	@NotEmpty(message = ErrorCodes.WARN_AUTH_TOKEN_NOT_EMPTY)
	private String token;
	
	//*********************************** Getters *********************** //
	
	/**
	 * Gets the phone number
	 * @return
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Gets the token
	 * @return
	 */
	public String getToken() {
		return token;
	}
	
	
	//*********************************** Setters *********************** //
	
	/**
	 * Sets the phone number
	 * @param phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * Sets the token 
	 * @param token
	 */
	public void setToken(String token) {
		this.token = token;
	}
}
