/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.beans.site;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : SiteHoursRequest 
 * 
 * SiteHoursRequest: is used to hold the site hours request data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        31-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER              	COMMENTS
 * 31-08-2016		Goush Basha		    File Created(Sprint-3).
 * 
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class SiteHoursRequest {

	/** This property is used to hold the dayOfWeek. */
	private Integer dayOfWeek;
	
	/** This property is used to hold the openHrs. */
	private String openHrs;
	
	/** This property is used to hold the closeHrs. */
	private String closeHrs;
	
	/*********************************** Getters ***********************/
	/**
	 * Gets the dayOfWeek value for this Site Hour.
	 * @return dayOfWeek
	 */
	public Integer getDayOfWeek() {
		return dayOfWeek;
	}
	
	/**
	 * Gets the openHrs value for this Site Hour.
	 * @return openHrs
	 */
	public String getOpenHrs() {
		return openHrs;
	}
	
	/**
	 * Gets the closeHrs value for this Site Hour.
	 * @return closeHrs
	 */
	public String getCloseHrs() {
		return closeHrs;
	}
	
	/*********************************** Setters ***********************/
	/**
	 * Sets the dayOfWeek value for this Site Hour.
	 * @param dayOfWeek
	 */
	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	/**
	 * Sets the openHrs value for this Site Hour.
	 * @param openHrs
	 */
	public void setOpenHrs(String openHrs) {
		this.openHrs = openHrs;
	}
	
	/**
	 * Sets the closeHrs value for this Site Hour.
	 * @param closeHrs
	 */
	public void setCloseHrs(String closeHrs) {
		this.closeHrs = closeHrs;
	}	
}
