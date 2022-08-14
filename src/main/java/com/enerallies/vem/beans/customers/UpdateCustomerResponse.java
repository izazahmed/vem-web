/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.customers;

import javax.validation.constraints.NotNull;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : CreateCustomerResponse 
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
 * 13-09-2016		Madhu Bantu		File Created
 *
 */
public class UpdateCustomerResponse {
	
	/** This property is used to hold the customer id */
	@NotNull(message = ErrorCodes.WARN_USER_ID_NOT_NULL)
	private Integer customerId;
	
	/** this property holds the user id */
	private int customerCode;
	
	/** this property holds the user name */
	private String companyName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public int getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(int customerCode) {
		this.customerCode = customerCode;
	}
	
}
