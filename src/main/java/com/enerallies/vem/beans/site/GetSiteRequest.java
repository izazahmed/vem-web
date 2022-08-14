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
 * File Name : GetSiteRequest 
 * 
 * GetSiteRequest: is used to hold the Get site request and check site internal id
 * data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        15-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER              	COMMENTS
 * 15-09-2016		Goush Basha		    File Created(Sprint-3).
 * 
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class GetSiteRequest {
	
	/** This property is used to hold the siteId. */
	@NotNull(message = ErrorCodes.ERROR_SITE_ID_EMPTY)
	private Integer siteId;
	
	/** This property is used to hold the siteInternalId. */
	private String siteInternalId;
	
	/** This property is used to hold the customerId. */
	@NotNull(message = ErrorCodes.ERROR_SITE_CUSTOMER_ID_EMPTY)
	private Integer customerId;
	
	/*********************************** Getters ***********************/
	
	/**
	 * Gets the siteId value for this Site.
	 * @return siteId
	 */
	public Integer getSiteId() {
		return siteId;
	}
	
	/**
	 * Gets the siteInternalId value for this Site.
	 * @return siteInternalId
	 */
	public String getSiteInternalId() {
		return siteInternalId;
	}
	
	/**
	 * Gets the customerId value for this Site.
	 * @return customerId
	 */
	public Integer getCustomerId() {
		return customerId;
	}
	
	/*********************************** Setters ***********************/
	/**
	 * Sets the siteId value for this Site.
	 * @param siteId
	 */
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	
	/**
	 * Sets the siteInternalId value for this Site.
	 * @param siteInternalId
	 */
	public void setSiteInternalId(String siteInternalId) {
		this.siteInternalId = siteInternalId;
	}
	
	/**
	 * Sets the customerId value for this Site.
	 * @param customerId
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

}
