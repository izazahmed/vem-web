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
 * File Name : ObservationLocation 
 * ObservationLocation Class is used hold Observation Location information
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
public class ObservationLocation {

	/**
	 * Declare String full, this property is used to hold the full
	 */
	private String full;

	/**
	 * Declare String city, this property is used to hold the city
	 */
	private String city;

	/**
	 * Declare String state, this property is used to hold the state
	 */
	private String state;

	/**
	 * Declare String country, this property is used to hold the country
	 */
	private String country;

	/**
	 * Declare String country_iso3166, this property is used to hold the
	 * country_iso3166
	 */
	private String country_iso3166;

	/**
	 * Declare String latitude, this property is used to hold the latitude
	 */
	private String latitude;

	/**
	 * Declare String longitude, this property is used to hold the longitude
	 */
	private String longitude;

	/**
	 * Declare String elevation, this property is used to hold the elevation
	 */
	private String elevation;

	/**
	 * @return the full
	 */
	public String getFull() {
		return full;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @return the country_iso3166
	 */
	public String getCountry_iso3166() {
		return country_iso3166;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @return the elevation
	 */
	public String getElevation() {
		return elevation;
	}

	/**
	 * @param full
	 *            the full to set
	 */
	public void setFull(String full) {
		this.full = full;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @param country_iso3166
	 *            the country_iso3166 to set
	 */
	public void setCountry_iso3166(String country_iso3166) {
		this.country_iso3166 = country_iso3166;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @param elevation
	 *            the elevation to set
	 */
	public void setElevation(String elevation) {
		this.elevation = elevation;
	}

}
