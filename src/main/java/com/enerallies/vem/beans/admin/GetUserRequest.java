/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.admin;

import javax.validation.constraints.NotNull;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : GetUserRequest 
 * GetUserRequest: is used to transfer User data from client to server to get the user details
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        09-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 09-08-2016		Nagarjuna Eerla		File Created
 *
 */
public class GetUserRequest {
	
	/** This property is used to hold the user id.*/
	@NotNull(message = ErrorCodes.WARN_USER_ID_NOT_EMPTY)
	private Integer userId;

	//*********************************** Getters ***********************//
	
	/**
	 * Gets the user id
	 * @return
	 */
	public int getUserId() {
		return userId;
	}

	//*********************************** Setters ***********************//
	
	/**
	 * Sets the user id
	 * @param userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
