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
 * File Name : DeleteUserRequest 
 * DeleteUserRequest: is the bean to delete user 
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
public class DeleteUserRequest {
	
	/** This property is used to hold the user id.*/
	@NotNull(message = ErrorCodes.WARN_USER_ID_NOT_NULL)
	private Integer userId;
	
	/** This property is used to hold the user who is updated the user. */
	private int updateBy;

	//*********************************** Getters ***********************//
	
	/**
	 * Gets the user id
	 * @return
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * gets the updated by
	 * @return
	 */
	public int getUpdateBy() {
		return updateBy;
	}
	//*********************************** Setters ***********************//
	
	/**
	 * Sets the user id
	 * @param userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * sets the user updated by
	 * @param updateBy
	 */
	public void setUpdateBy(int updateBy) {
		this.updateBy = updateBy;
	}
}
