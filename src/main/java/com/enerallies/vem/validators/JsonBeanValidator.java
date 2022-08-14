/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.validators;

import java.lang.reflect.Field;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.serviceimpl.admin.AdminServiceImpl;

/**
 * File Name : JsonBeanValidator 
 * JsonBeanValidator: will validate the java Bean based on annotations used on variables and it will generate the Violated Messages
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
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

@Component
public class JsonBeanValidator {
	// Getting logger
	private static final Logger logger = Logger.getLogger(AdminServiceImpl.class);
	private Validator validator;
	
	private Validator getValidator() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
		return validator;
	}
	
	/**
	 * Gets  the violated messages
	 * @param pojoClass
	 * @return
	 */
	public <T> ValidatorBean getViolatedMessages(T pojoClass){
		if (logger.isDebugEnabled()){
			logger.debug("[BEGIN] [getViolatedMessages] [Validation Layer]");
		}
		validator = getValidator();
		ValidatorBean validatorBean = new ValidatorBean();
		validatorBean.setNotValid(false);
		
		Set<ConstraintViolation <T>> constraintViolations = validator.validate(pojoClass);
		if(CollectionUtils.isNotEmpty(constraintViolations)) {
			
			validatorBean.setMessage(constraintViolations.iterator().next().getMessage());
			validatorBean.setNotValid(true);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[END] [getViolatedMessages] [Validation Layer]");
		}
		return validatorBean;
	}
	
	/**
	 * Gets  the violated messages
	 * @param object
	 * @param clazz
	 * @return
	 */
	public <T> ValidatorBean getViolatedMessages(T object, Class<T> clazz){
		
		validator = getValidator();
		ValidatorBean validatorBean = new ValidatorBean();
		validatorBean.setNotValid(false);
		Set<ConstraintViolation <T>> constraintViolations;
		
		for(Field field:clazz.getDeclaredFields()) {
			
			constraintViolations = validator.validateProperty(object, field.getName());
			
			if(CollectionUtils.isNotEmpty(constraintViolations)) {
				
				validatorBean.setMessage(constraintViolations.iterator().next().getMessage());
				validatorBean.setNotValid(true);
				break;
			}
		} 
		
		return validatorBean;
	}
}
