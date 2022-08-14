/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.common;
/**
 * File Name : Response 
 * 
 * Response: This is the common response for all requests
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 03-08-2016		Nagarjuna Eerla		File Created
 *
 */
public class Response {
	/** This property will holds the status either success or fail. */
	private String status;
	
	/** This property will holds the data */
	private Object data;
	
	/** This property will holds the error code or success code. */
	private String code;

	/* ----------------------------- Getters -------------------- */

	/**
	 * Gets the status of request
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * gets the data
	 * @return
	 */
	public Object getData() {
		return data;
	}
	
	/**
	 * Gets the Error code or success code 
	 * @return
	 */
	public String getCode() {
		return code;
	}
	
	/* ----------------------------- Setters -------------------- */
	
	/**
	 * Sets the status of request
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Sets the data
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * Sets the Error code or success code
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
}
