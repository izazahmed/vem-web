/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.validators.admin;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * File Name : PhoneNumberConstraintValidator 
 * PhoneNumberConstraintValidator: it will contains the actual logic to validate phone number
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2.1.0
 * @date        10-08-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	10-08-2016			Nagarjuna Eerla		File Created
 *
 */
public class PhoneNumberConstraintValidator implements ConstraintValidator<PhoneNumber, String> {

	@Override
	public void initialize(PhoneNumber phoneNumber) {
		// The “initialize” method is empty here, but it can be used to save data from the annotation
	}

	@Override
	public boolean isValid(String phoneNumber, ConstraintValidatorContext arg1) {
		if(phoneNumber == null) {
            return false;
        }
        return phoneNumber.matches("[0-9()\\-+/.]*");
	}

}
