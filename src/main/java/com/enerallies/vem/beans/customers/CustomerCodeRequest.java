
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
 * 10-08-2016		Madhu Bantu		File Created
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CustomerCodeRequest {
	/** This property is used to hold the company name. */
	//@NotEmpty(message = ErrorCodes.ERROR_COMPANY_NAME_EMPTY)
	//@Size(min = 6, max = 100, message = ErrorCodes.ERROR_COMPANY_NAME_MAX_SIZE)
	private String companyName;
	
	/** This property is used to hold the customer id.*/
	//@NotNull(message = ErrorCodes.ERROR_CUSTOMER_ID_EMPTY)
	private String customerCode;


	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

}