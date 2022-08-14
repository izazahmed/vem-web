/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.beans.site;

import javax.validation.constraints.NotNull;

import com.enerallies.vem.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : ActivateOrDeActivateSiteRequest 
 * 
 * ActivateOrDeActivateSiteRequest: is used to hold the status of site from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        09-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER              	COMMENTS
 * 09-09-2016		Goush Basha		    File Created(Sprint-3).
 * 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActivateOrDeActivateSiteRequest {
	
	/** This property is used to hold the siteId. */
	@NotNull(message = ErrorCodes.ERROR_SITE_ID_EMPTY)
	private Integer siteId;
	
	/** This property is used to hold the siteStatus. */
	@NotNull(message = ErrorCodes.ERROR_SITE_STATUS_EMPTY)
	private Integer siteStatus;
	
	//------------------------Getters------------------------
	/**
	 * Gets the siteId value for this Site.
	 * @return siteId
	 */
	public Integer getSiteId() {
		return siteId;
	}
	
	/**
	 * Gets the siteStatus value for this Site.
	 * @return siteStatus
	 */
	public Integer getSiteStatus() {
		return siteStatus;
	}
	
	//-----------------------Setters-------------------------
	/**
	 * Sets the siteId value for this Site.
	 * @param siteId
	 */
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	
	/**
	 * Sets the siteStatus value for this Site.
	 * @param siteStatus
	 */
	public void setSiteStatus(Integer siteStatus) {
		this.siteStatus = siteStatus;
	}

}
