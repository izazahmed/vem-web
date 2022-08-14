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
 * File Name : ThermostatRequest 
 * 
 * ThermostatRequest: is used to hold the Thermostat request
 * data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
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
public class ThermostatRequest {
	
	/** This property is used to hold the unit. */
	private String unit;
	
	/** This property is used to hold the locationImage. */
	private String locationImage;
	
	/** This property is used to hold the spaceEnough. */
	private Integer spaceEnough;
	
	/** This property is used to hold the make. */
	private String make;
	
	/** This property is used to hold the model. */
	private String model;
	
	/** This property is used to hold the hVACUnit. */
	private String hVACUnit;
	
	/** This property is used to hold the wiringConfigThermostat. */
	private String wiringConfigThermostat;
	
	/** This property is used to hold the wiringThermostatImage. */
	private String wiringThermostatImage;
	
	/** This property is used to hold the rCAndCPower. */
	private Integer rCAndCPower;
	
	/** This property is used to hold the cWireAttached. */
	private Integer cWireAttached;
	
	/** This property is used to hold the noCWireAttached. */
	private Integer noCWireAttached;
	
	/** This property is used to hold the automatedSchedule. */
	private Integer automatedSchedule;
	
	/** This property is used to hold the automatedScheduleNote. */
	private String  automatedScheduleNote;
	
	/** This property is used to hold the locationOfRemoteSensor. */
	private String locationOfRemoteSensor;
	
	/** This property is used to hold the validateSensor. */
	private String validateSensor;
	
	/** This property is used to hold the wiringConfigSensor. */
	private String wiringConfigSensor;
	
	/** This property is used to hold the wiringSensorImage. */
	private String wiringSensorImage;
	
	/** This property is used to hold the locationType. */
	private Integer locationType;
	
	/** This property is used to hold the locationType. */
	private String otherLocation;
	
	/*********************************** Getters ***********************/
	/**
	 * Gets the locationType value for this Thermostat.
	 * @return locationType
	 */
	public Integer getLocationType() {
		return locationType;
	}
	
	/**
	 * Gets the automatedScheduleNote value for this Thermostat.
	 * @return automatedScheduleNote
	 */
	public String getAutomatedScheduleNote() {
		return automatedScheduleNote;
	}
	
	/**
	 * Gets the rCAndCPower value for this Thermostat.
	 * @return rCAndCPower
	 */
	public Integer getrCAndCPower() {
		return rCAndCPower;
	}
	
	/**
	 * Gets the cWireAttached value for this Thermostat.
	 * @return cWireAttached
	 */
	public Integer getcWireAttached() {
		return cWireAttached;
	}
	
	/**
	 * Gets the unit value for this Thermostat.
	 * @return unit
	 */
	public String getUnit() {
		return unit;
	}
	
	/**
	 * Gets the hVACUnit value for this Thermostat.
	 * @return hVACUnit
	 */
	public String gethVACUnit() {
		return hVACUnit;
	}
	
	/**
	 * Gets the locationImage value for this Thermostat.
	 * @return locationImage
	 */
	public String getLocationImage() {
		return locationImage;
	}
	
	/**
	 * Gets the spaceEnough value for this Thermostat.
	 * @return spaceEnough
	 */
	public Integer getSpaceEnough() {
		return spaceEnough;
	}
	
	/**
	 * Gets the make value for this Thermostat.
	 * @return make
	 */
	public String getMake() {
		return make;
	}
	
	/**
	 * Gets the model value for this Thermostat.
	 * @return model
	 */
	public String getModel() {
		return model;
	}
	
	/**
	 * Gets the wiringConfigThermostat value for this Thermostat.
	 * @return wiringConfigThermostat
	 */
	public String getWiringConfigThermostat() {
		return wiringConfigThermostat;
	}
	
	/**
	 * Gets the wiringThermostatImage value for this Thermostat.
	 * @return wiringThermostatImage
	 */
	public String getWiringThermostatImage() {
		return wiringThermostatImage;
	}
	
	/**
	 * Gets the noCWireAttached value for this Thermostat.
	 * @return noCWireAttached
	 */
	public Integer getNoCWireAttached() {
		return noCWireAttached;
	}
	
	/**
	 * Gets the automatedSchedule value for this Thermostat.
	 * @return automatedSchedule
	 */
	public Integer getAutomatedSchedule() {
		return automatedSchedule;
	}
	
	/**
	 * Gets the locationOfRemoteSensor value for this Thermostat.
	 * @return locationOfRemoteSensor
	 */
	public String getLocationOfRemoteSensor() {
		return locationOfRemoteSensor;
	}
	
	/**
	 * Gets the validateSensor value for this Thermostat.
	 * @return validateSensor
	 */
	public String getValidateSensor() {
		return validateSensor;
	}
	
	/**
	 * Gets the wiringConfigSensor value for this Thermostat.
	 * @return wiringConfigSensor
	 */
	public String getWiringConfigSensor() {
		return wiringConfigSensor;
	}
	
	/**
	 * Gets the wiringSensorImage value for this Thermostat.
	 * @return wiringSensorImage
	 */
	public String getWiringSensorImage() {
		return wiringSensorImage;
	}
	
	/*********************************** Setters ***********************/
	/**
	 * Sets the locationType value for this Thermostat.
	 * @param locationType
	 */
	public void setLocationType(Integer locationType) {
		this.locationType = locationType;
	}
	
	/**
	 * Sets the automatedScheduleNote value for this Thermostat.
	 * @param automatedScheduleNote
	 */
	public void setAutomatedScheduleNote(String automatedScheduleNote) {
		this.automatedScheduleNote = automatedScheduleNote;
	}
	
	/**
	 * Sets the rCAndCPower value for this Thermostat.
	 * @param rCAndCPower
	 */
	public void setrCAndCPower(Integer rCAndCPower) {
		this.rCAndCPower = rCAndCPower;
	}
	
	/**
	 * Sets the cWireAttached value for this Thermostat.
	 * @param cWireAttached
	 */
	public void setcWireAttached(Integer cWireAttached) {
		this.cWireAttached = cWireAttached;
	}
	
	/**
	 * Sets the unit value for this Thermostat.
	 * @param unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	/**
	 * Sets the hVACUnit value for this Thermostat.
	 * @param hVACUnit
	 */
	public void sethVACUnit(String hVACUnit) {
		this.hVACUnit = hVACUnit;
	}
	
	/**
	 * Sets the locationImage value for this Thermostat.
	 * @param locationImage
	 */
	public void setLocationImage(String locationImage) {
		this.locationImage = locationImage;
	}
	
	/**
	 * Sets the spaceEnough value for this Thermostat.
	 * @param spaceEnough
	 */
	public void setSpaceEnough(Integer spaceEnough) {
		this.spaceEnough = spaceEnough;
	}
	
	/**
	 * Sets the make value for this Thermostat.
	 * @param make
	 */
	public void setMake(String make) {
		this.make = make;
	}
	
	/**
	 * Sets the model value for this Thermostat.
	 * @param model
	 */
	public void setModel(String model) {
		this.model = model;
	}
	
	/**
	 * Sets the wiringConfigThermostat value for this Thermostat.
	 * @param wiringConfigThermostat
	 */
	public void setWiringConfigThermostat(String wiringConfigThermostat) {
		this.wiringConfigThermostat = wiringConfigThermostat;
	}
	
	/**
	 * Sets the wiringThermostatImage value for this Thermostat.
	 * @param wiringThermostatImage
	 */
	public void setWiringThermostatImage(String wiringThermostatImage) {
		this.wiringThermostatImage = wiringThermostatImage;
	}
	
	/**
	 * Sets the noCWireAttached value for this Thermostat.
	 * @param noCWireAttached
	 */
	public void setNoCWireAttached(Integer noCWireAttached) {
		this.noCWireAttached = noCWireAttached;
	}
	
	/**
	 * Sets the automatedSchedule value for this Thermostat.
	 * @param automatedSchedule
	 */
	public void setAutomatedSchedule(Integer automatedSchedule) {
		this.automatedSchedule = automatedSchedule;
	}
	
	/**
	 * Sets the locationOfRemoteSensor value for this Thermostat.
	 * @param locationOfRemoteSensor
	 */
	public void setLocationOfRemoteSensor(String locationOfRemoteSensor) {
		this.locationOfRemoteSensor = locationOfRemoteSensor;
	}
	
	/**
	 * Sets the validateSensor value for this Thermostat.
	 * @param validateSensor
	 */
	public void setValidateSensor(String validateSensor) {
		this.validateSensor = validateSensor;
	}
	
	/**
	 * Sets the wiringConfigSensor value for this Thermostat.
	 * @param wiringConfigSensor
	 */
	public void setWiringConfigSensor(String wiringConfigSensor) {
		this.wiringConfigSensor = wiringConfigSensor;
	}
	
	/**
	 * Sets the wiringSensorImage value for this Thermostat.
	 * @param wiringSensorImage
	 */
	public void setWiringSensorImage(String wiringSensorImage) {
		this.wiringSensorImage = wiringSensorImage;
	}

	public String getOtherLocation() {
		return otherLocation;
	}

	public void setOtherLocation(String otherLocation) {
		this.otherLocation = otherLocation;
	}	
	
}
