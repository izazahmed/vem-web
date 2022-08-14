/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

/**
 * File Name : ResourceBundleObject 
 * ResourceBundleObject Class is used get ResourceBundle Object
 *
 * @author (Y Chenna Reddy – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     1.0
 * @date        29-07-2016
 *
 * MOD HISTORY
 * 
 * DATE				USER             		COMMENTS
 * 
 * 29-07-2016		Y Chenna Reddy			File Created
 * 24-08-2016		Nagarjuna Eerla			Resolved the Major sonar Qube Issues
 */
public class ResourceBundleObject {
	
	private ResourceBundleObject(){}
	
	// Getting logger	
	 public static final Logger logger = Logger.getLogger(ResourceBundleObject.class);
	
	// Declare and initialize ResuourceBundle
	private static ResourceBundle resourceBundle = null;

	/**
	 * Method Name: getInstance()
	 * gets the instance of ResourceBundle Object
	 * 
	 * @param propertyfiletype
	 * @return
	 * @throws Exception
	 */
	public static ResourceBundle getInstance(String propertyfiletype){
		try {
			if (propertyfiletype.equalsIgnoreCase("DB")) {
				// Getting DB Properties ResourceBundle Object
				resourceBundle = ResourceBundle.getBundle("db", Locale.getDefault());
			} else if (propertyfiletype.equalsIgnoreCase("Logger")) {
				// Getting Logger Properties ResourceBundle Object
				resourceBundle = ResourceBundle.getBundle("log4j", Locale.getDefault());
			} else if (propertyfiletype.equalsIgnoreCase("Wungerground")) {
				// Getting WeatherUnderground Properties ResourceBundle Object
				resourceBundle = ResourceBundle.getBundle("wunderground",
						Locale.getDefault());
			}
		} catch (Exception exception) {			
			logger.error("Exception in getting the ResourceBundle object" + exception);
		}
		return resourceBundle;
	}
}
