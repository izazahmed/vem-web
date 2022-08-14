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
 * File Name : RTURequest 
 * 
 * RTURequest: is used to hold the RTU or HVAC request
 * data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        30-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER              	COMMENTS
 * 30-09-2016		Goush Basha		    File Created(Sprint-3).
 * 
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class RTURequest {
	
	/** This property is used to hold the model. */
	private String model;
	
	/** This property is used to hold the unit. */
	private String unit;
	
	/** This property is used to hold the location. */
	private Integer location;
	
	/** This property is used to hold the heating. */
	private String heating;
	
	/** This property is used to hold the cooling. */
	private String cooling;
	
	//------------------------Getters------------------------
	/**
	 * Gets the model value for this HVAC or RTU.
	 * @return model
	 */
	public String getModel() {
		return model;
	}
	
	/**
	 * Gets the location value for this HVAC or RTU.
	 * @return location
	 */
	public Integer getLocation() {
		return location;
	}
	
	/**
	 * Gets the unit value for this HVAC or RTU.
	 * @return unit
	 */
	public String getUnit() {
		return unit;
	}
	
	/**
	 * Gets the heating value for this HVAC or RTU.
	 * @return heating
	 */
	public String getHeating() {
		return heating;
	}
	
	/**
	 * Gets the cooling value for this HVAC or RTU.
	 * @return cooling
	 */
	public String getCooling() {
		return cooling;
	}
	
	//-----------------------Setters-------------------------
	/**
	 * Sets the model value for this HVAC or RTU.
	 * @param model
	 */
	public void setModel(String model) {
		this.model = model;
	}
	
	/**
	 * Sets the location value for this HVAC or RTU.
	 * @param location
	 */
	public void setLocation(Integer location) {
		this.location = location;
	}
	
	/**
	 * Sets the unit value for this HVAC or RTU.
	 * @param unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	/**
	 * Sets the heating value for this HVAC or RTU.
	 * @param heating
	 */
	public void setHeating(String heating) {
		this.heating = heating;
	}
	
	/**
	 * Sets the cooling value for this HVAC or RTU.
	 * @param cooling
	 */
	public void setCooling(String cooling) {
		this.cooling = cooling;
	}
	
}
