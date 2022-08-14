/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.admin;

import java.util.Date;

/**
 * File Name : ChangePassword 
 * ChangePassword: is used to transfer ChangePassword Data
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
public class ChangePassword {

	/** This property is used to hold the user id. */
	private int userId;
	
	/** This property is used to hold the user password */
	private String password;
	
	/** This property is used to hold the user who is updated the user. */
	private int updateBy;
	
	/** This property is used to hold the updated date on which the user got updated. */
	private Date updateDate;
	
	/** This property is used to hold the updated date on which the user got updated. */
	private String authToken;
	
	
	//*********************************** Getters ***********************//
	/**
	 * gets the user id
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

	/**
	 * gets the user updated date
	 * @return
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * gets the user password
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Gets auth token
	 * @param authToken
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
	//*********************************** Setters ***********************//
	
	/**
	 * sets the user id
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
	
	/**
	 * sets the user updated date
	 * @param updateDate
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	/**
	 * sets the user password
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets auth token
	 * @return
	 */
	public String getAuthToken() {
		return authToken;
	}
}
