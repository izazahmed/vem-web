package com.enerallies.vem.beans.customers;
/**
 * File Name : CreateCustomerRequest 
 * CreateCustomerRequest: is used to transfer CustomerRequest Data from client side
 *
 * @author (Madhu Bantu – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        01-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 01-09-2016		Madhu Bantu 		File Created
 *
 */
public class CustomerCodeResponse {
	
/** This property is used to hold the company name. */
	//@NotEmpty(message = ErrorCodes.ERROR_COMPANY_NAME_EMPTY)
	//@Size(min = 6, max = 100, message = ErrorCodes.ERROR_COMPANY_NAME_MAX_SIZE)
	private String companyName;

	/** This property is used to hold the customer id */
	//@NotEmpty(message = ErrorCodes.ERROR_COMPANY_CODE_EMPTY)
	//@Size(max = 100, message = ErrorCodes.ERROR_COMPANY_CODE_MAX_SIZE)
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