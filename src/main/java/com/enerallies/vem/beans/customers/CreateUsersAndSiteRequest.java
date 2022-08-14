/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.customers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
 * 06-09-2016		Madhu Bantu		File Created
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CreateUsersAndSiteRequest {
	
	/** This property is used to hold the customerId. */
	private String customerId;
	
	/** This property is used to hold the locationsList. */
	private String locationId;
	
	/** This property is used to hold the usersList. */
	private String userId;

	public String getCustomerId() {
		return customerId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

}
