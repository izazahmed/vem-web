/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.admin;
/**
 * File Name : CreateUserResponse 
 * CreateUserResponse: is used to transfer creation of user response to client side
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
public class CreateUserResponse {
	
	/** this property holds the user id */
	private int userId;
	
	/** this property holds the user name */
	private String userName;
	
	/*********************************** Getters ***********************/
	
	/**
	 * Gets the user id
	 * @return
	 */
	public int getUserId() {
		return userId;
	}
	
	/**
	 * gets the user name
	 * @return
	 */
	public String getUserName() {
		return userName;
	}
	
	/*********************************** Setters ***********************/
	
	/**
	 * Sets the user name
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Sets the user id
	 * @param userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
