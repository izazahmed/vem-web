/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.admin;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : ChangePasswordRequest 
 * ChangePasswordRequest: is used to transfer ChangePasswordRequest Data
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        04-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 04-08-2016		Nagarjuna Eerla		File Created
 *
 */
public class ChangePasswordRequest {

	/** This property is used to hold the user id. */
	@NotNull(message = ErrorCodes.WARN_USER_ID_NOT_NULL)
	private Integer userId;
	
	/** This property is used to hold the user current password. */
	@NotEmpty(message = ErrorCodes.WARN_CURRENT_PWD_NOT_EMPTY)
	private String currentPwd;
	
	/** This property is used to hold the user new password. */
	@NotEmpty(message = ErrorCodes.WARN_CHOOSE_PWD_NOT_EMPTY)
	private String newPwd;
	
	/** This property is used to hold the user repeated password. */
	@NotEmpty(message = ErrorCodes.WARN_REPEAT_PWD_NOT_EMPTY)
	private String repeatPwd;
	
	/** This property is used to hold the user who is updated the user. */
	private int updateBy;
	
	//*********************************** Getters *********************** //
	
	/**
	 * gets the updated by
	 * @return
	 */
	public int getUpdateBy() {
		return updateBy;
	}
	
	/**
	 * Gets the current password
	 * @return
	 */
	public String getCurrentPwd() {
		return currentPwd;
	}
	
	/**
	 * Gets the current user id
	 * @return
	 */
	public int getUserId() {
		return userId;
	}
	
	/**
	 * Gets the current new password
	 * @return
	 */
	public String getNewPwd() {
		return newPwd;
	}
	
	/**
	 * Gets the repeated password
	 * @return
	 */
	public String getRepeatPwd() {
		return repeatPwd;
	}
	//*********************************** Setters *********************** //
	/**
	 * sets the current user id
	 * @param userId
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	/**
	 * sets the current user password
	 * @param currentPwd
	 */
	public void setCurrentPwd(String currentPwd) {
		this.currentPwd = currentPwd;
	}
	
	/**
	 * sets the current user new password
	 * @param newPwd
	 */
	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
	
	/**
	 * sets the repeated password
	 * @param repeatPwd
	 */
	public void setRepeatPwd(String repeatPwd) {
		this.repeatPwd = repeatPwd;
	}
	
	/**
	 * sets the user updated by
	 * @param updateBy
	 */
	public void setUpdateBy(int updateBy) {
		this.updateBy = updateBy;
	}
}
