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
 * File Name : InactivateUserRequest 
 * InactivateUserRequest: is used to transfer in-active User request data from client to server to in-activate user
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
public class UserActivityRequest {

	/** This property is used to hold the user id.*/
	@NotNull(message = ErrorCodes.WARN_USER_ID_NOT_NULL)
	private Integer userId;

	@NotNull(message = ErrorCodes.WARN_STATUS_NOT_NULL)
	private Integer status;
	
	/** This property is used to hold the user who is updated the user. */
	private int updateBy;
		
	//*********************************** Getters ***********************//
	/**
	 * Gets status
	 * @return
	 */
	public Integer getStatus() {
		return status;
	}

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
	 * Sets status
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	
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
