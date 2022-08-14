/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.admin;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : CreateNewPasswordRequest 
 * CreateNewPasswordRequest: is used to transfer CreateNewPasswordRequest Data
 *
 * @author Cambridge Technologies.
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
 * 01	04-08-2016			Nagarjuna Eerla		File Created
 *
 */
public class CreateNewPasswordRequest {
	
	/** This property is used to hold the user id. */
	private int userId;
	
	/** This property is used to hold the authToken. */
	@NotEmpty(message = ErrorCodes.WARN_AUTH_TOKEN_NOT_EMPTY)
	private String authToken;

	/** This property is used to hold the choose password. */
	@NotEmpty(message = ErrorCodes.WARN_CHOOSE_PWD_NOT_EMPTY)
	@Pattern(regexp="[^\\w\\d]*(([0-9]+.*[A-Za-z]+.*)|[A-Za-z]+.*([0-9]+.*))", message = ErrorCodes.WARN_PASSWORD_SHOULD_BE_ALPHA_NUMERIC)
	private String choosePwd;
	
	/** This property is used to hold the repeated password. */
	@NotEmpty(message = ErrorCodes.WARN_REPEAT_PWD_NOT_EMPTY)
	@Pattern(regexp="[^\\w\\d]*(([0-9]+.*[A-Za-z]+.*)|[A-Za-z]+.*([0-9]+.*))", message = ErrorCodes.WARN_PASSWORD_SHOULD_BE_ALPHA_NUMERIC)
	private String repeatPwd;
	
	/** This property is used to hold the user who is updated the user. */
	private int updateBy;
	
	private boolean isFirstTimeChange;
	
	//*********************************** Getters *********************** //
	
	/**
	 * Gets the authToken
	 * @return
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * Gets the user id
	 * @return
	 */
	public int getUserId() {
		return userId;
	}
	
	/**
	 * Gets the user choose password
	 * @return
	 */
	public String getChoosePwd() {
		return choosePwd;
	}
	
	/**
	 * Gets the user repeated password
	 * @return
	 */
	public String getRepeatPwd() {
		return repeatPwd;
	}

	/**
	 * gets the updated by
	 * @return
	 */
	public int getUpdateBy() {
		return updateBy;
	}
	
	
	//*********************************** Getters *********************** //
	
	/**
	 * Sets the authToken
	 * @param authToken
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
	/**
	 * Sets the user id
	 * @param userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	/**
	 * Sets the user choose password
	 * @param choosePwd
	 */
	public void setChoosePwd(String choosePwd) {
		this.choosePwd = choosePwd;
	}
	
	/**
	 * Sets the user repeated password
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

	public boolean getIsFirstTimeChange() {
		return isFirstTimeChange;
	}

	public void setIsFirstTimeChange(boolean isFirstTimeChange) {
		this.isFirstTimeChange = isFirstTimeChange;
	}
}
