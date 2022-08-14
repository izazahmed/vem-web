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
 * File Name : WundergroundResponse 
 * WundergroundResponse Class is used hold WundergroundResponse information
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
public class WundergroundResponse {

	/**
	 * Declare WugResponse response this property is used to hold the response
	 */
	private WugResponse response;

	/**
	 * Declare CurrentObservation current_observation this property is used to
	 * hold the current_observation
	 */
	private CurrentObservation current_observation;

	/**
	 * @return the response
	 */
	public WugResponse getResponse() {
		return response;
	}

	/**
	 * @return the current_observation
	 */
	public CurrentObservation getCurrent_observation() {
		return current_observation;
	}

	/**
	 * @param response
	 *            the response to set
	 */
	public void setResponse(WugResponse response) {
		this.response = response;
	}

	/**
	 * @param current_observation
	 *            the current_observation to set
	 */
	public void setCurrent_observation(CurrentObservation current_observation) {
		this.current_observation = current_observation;
	}

}
