/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.weather.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;



/**
 * File Name : WugResponse 
 * WugResponse Class is used hold response information
 *
 * @author (Y Chenna Reddy – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     1.0
 * @date        28-07-2016
 *
 * MOD HISTORY
 * 
 * DATE				USER             		COMMENTS
 * 
 * 28-07-2016		Y Chenna Reddy			File Created
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WugResponse {

	/**
	 * Declare String version this property is used to hold the version
	 */
	private String version;
	
	/**
	 * Declare String termsofService this property is used to hold the
	 * termsofService
	 */
	private String termsofService;

	/**
	 * Declare WugResponseFeatures features this property is used to hold the
	 * features
	 */
	private WugResponseFeatures features;

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the termsofService
	 */
	public String getTermsofService() {
		return termsofService;
	}

	/**
	 * @return the features
	 */
	public WugResponseFeatures getFeatures() {
		return features;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @param termsofService
	 *            the termsofService to set
	 */
	public void setTermsofService(String termsofService) {
		this.termsofService = termsofService;
	}

	/**
	 * @param features
	 *            the features to set
	 */
	public void setFeatures(WugResponseFeatures features) {
		this.features = features;
	}

}
