/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util.template;

import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : ITemplateUtil 
 * ITemplateUtil: is the template interface
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2.1.0
 * @date        04-08-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	04-08-2016			Nagarjuna Eerla		File Created
 *
 */
public interface ITemplateUtil {

	/**
	 *  Method to convert template string to string content
	 * 
	 * @param templateStr
	 * @param model
	 * @return String
	 */
	public String prepareContentFromTemplateString(String templateStr, Object model) throws VEMAppException;
}
