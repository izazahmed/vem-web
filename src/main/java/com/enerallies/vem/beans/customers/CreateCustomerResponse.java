/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.customers;

import com.enerallies.vem.beans.admin.GetUserResponse;

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
public class CreateCustomerResponse {
	
	/** this property holds the user id */
	private int customerID;
	
	/** this property holds the user name */
	private String companyName;
	private GetUserResponse getUserResponse;

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public GetUserResponse getGetUserResponse() {
		return getUserResponse;
	}

	public void setGetUserResponse(GetUserResponse getUserResponse) {
		this.getUserResponse = getUserResponse;
	}
	
}
