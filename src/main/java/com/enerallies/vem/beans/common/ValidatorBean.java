/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.common;
/**
 * File Name : ValidatorBean 
 * ValidatorBean: is used to validate the bean
 *
 * @author (Nagarjuna Eerla – CTE).
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
public class ValidatorBean {
	
	/** This property holds the validation message */
	private String message;
	
	/** This property holds whether the property is valid or not */
	private boolean isNotValid;
	
	/*********************************** Getters ***********************/
	
	/** Gets the validation message */
	public String getMessage() {
		return message;
	}
	
	/** Gets the value of whether the property is valid or not */
	public boolean isNotValid() {
		return isNotValid;
	}
	
	/*********************************** Setters ***********************/
	
	/** Sets the validation message */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/** Sets whether the property is valid or not */
	public void setNotValid(boolean isNotValid) {
		this.isNotValid = isNotValid;
	}
}
