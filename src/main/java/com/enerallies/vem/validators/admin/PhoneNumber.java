/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.validators.admin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * File Name : PhoneNumber 
 * PhoneNumber: Is the annotation to validate phone number
 *
 * @author (Nagarjuna Eerla – CTE).
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2.1.0
 * @date        21-07-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	21-07-2016			Nagarjuna Eerla		File Created
 *
 */
@Documented
@Constraint(validatedBy = PhoneNumberConstraintValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
	
	/**
	 *  message(): method defines how the message is resolved.
	 * @return
	 */
	String message() default "{PhoneNumber}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
