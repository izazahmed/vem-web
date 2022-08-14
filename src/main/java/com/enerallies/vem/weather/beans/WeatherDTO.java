/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.weather.beans;

/**
 * File Name : WeatherDTO 
 * WeatherDTO is used to hold the data related to weather
 *
 * @author (Goush Basha – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        21-07-2016
 *
 * MOD HISTORY
 * DATE				USER             	COMMENTS
 * 21-07-2016		Goush Basha			File Created
 *
 */

public class WeatherDTO {
	
	/**
	 * This property is used to hold the weather id.
	 */
	private int weatherId;
	
	/**
	 * This property is used to hold the fullName.
	 */
	private String fullName;
	
	/**
	 * This property is used to hold the city.
	 */
	private String city;
	
	/**
	 * This property is used to hold the stateCode.
	 */
	private String stateCode;
	
	/**
	 * This property is used to hold the stateName.
	 */
	private String stateName;
	
	/**
	 * This property is used to hold the country.
	 */
	private String country;
	
	/**
	 * This property is used to hold the countryCode.
	 */
	private String countryCode;
	
	/**
	 * This property is used to hold the zip.
	 */
	private String zip;
	
	/**
	 * This property is used to hold the latitude.
	 */
	private String latitude;
	
	/**
	 * This property is used to hold the longitude.
	 */
	private String longitude;
	
	/**
	 * This property is used to hold the observedTimeStamp.
	 */
	private String observedTimeStamp;
	
	/**
	 * This property is used to hold the weather.
	 */
	private String weather;
	
	/**
	 * This property is used to hold the temperature.
	 */
	private String temperature;
	
	/**
	 * This property is used to hold the humidity.
	 */
	private String humidity;
	
	/**
	 * This property is used to hold the windSpeed.
	 */
	private String windSpeed;
	
	/**
	 * This property is used to hold the feelLikeTemperature.
	 */
	private String feelLikeTemperature;
	
	/**
	 * This property is used to hold the precipitation.
	 */
	private String precipitation;
	
	/**
	 * This property is used to hold the maxTemperature.
	 */
	private String maxTemperature;
	
	/**
	 * This property is used to hold the minTemperature.
	 */
	private String minTemperature;
	
	/**
	 * This property is used to hold the rainFall.
	 */
	private String rainFall;
	
	/**
	 * This property is used to hold the snowDepth.
	 */
	private String snowDepth;
	
	//****************************getters******************************
	/**
	 * Gets the weatherId value for this WeatherDTO.
	 * @return weatherId
	 */
	public int getWeatherId() {
		return weatherId;
	}
	
	/**
	 * Gets the fullName value for this WeatherDTO.
	 * @return fullName
	 */
	public String getFullName() {
		return fullName;
	}
	
	/**
	 * Gets the city value for this WeatherDTO.
	 * @return city
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Gets the stateCode value for this WeatherDTO.
	 * @return stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}
	
	/**
	 * Gets the stateName value for this WeatherDTO.
	 * @return stateName
	 */
	public String getStateName() {
		return stateName;
	}
	
	/**
	 * Gets the country value for this WeatherDTO.
	 * @return country
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * Gets the countryCode value for this WeatherDTO.
	 * @return countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	
	/**
	 * Gets the zip value for this WeatherDTO.
	 * @return zip
	 */
	public String getZip() {
		return zip;
	}
	
	/**
	 * Gets the latitude value for this WeatherDTO.
	 * @return latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	
	/**
	 * Gets the longitude value for this WeatherDTO.
	 * @return longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	
	/**
	 * Gets the observedTimeStamp value for this WeatherDTO.
	 * @return observedTimeStamp
	 */
	public String getObservedTimeStamp() {
		return observedTimeStamp;
	}

	/**
	 * Gets the weather value for this WeatherDTO.
	 * @return weather
	 */
	public String getWeather() {
		return weather;
	}
	
	/**
	 * Gets the temperature value for this WeatherDTO.
	 * @return temperature
	 */
	public String getTemperature() {
		return temperature;
	}
	
	/**
	 * Gets the humidity value for this WeatherDTO.
	 * @return humidity
	 */
	public String getHumidity() {
		return humidity;
	}
	
	/**
	 * Gets the windSpeed value for this WeatherDTO.
	 * @return windSpeed
	 */
	public String getWindSpeed() {
		return windSpeed;
	}
	
	/**
	 * Gets the feelLikeTemperature value for this WeatherDTO.
	 * @return feelLikeTemperature
	 */
	public String getFeelLikeTemperature() {
		return feelLikeTemperature;
	}
	
	/**
	 * Gets the precipitation value for this WeatherDTO.
	 * @return precipitation
	 */
	public String getPrecipitation() {
		return precipitation;
	}
	
	/**
	 * Gets the maxTemperature value for this WeatherDTO.
	 * @return maxTemperature
	 */
	public String getMaxTemperature() {
		return maxTemperature;
	}
	
	/**
	 * Gets the minTemperature value for this WeatherDTO.
	 * @return minTemperature
	 */
	public String getMinTemperature() {
		return minTemperature;
	}
	
	/**
	 * Gets the rainFall value for this WeatherDTO.
	 * @return rainFall
	 */
	public String getRainFall() {
		return rainFall;
	}
	
	/**
	 * Gets the snowDepth value for this WeatherDTO.
	 * @return snowDepth
	 */
	public String getSnowDepth() {
		return snowDepth;
	}
	
	//******************setters**************************
	/**
	 * Sets the weatherId value for this WeatherDTO.
	 * @param weatherId
	 */
	public void setWeatherId(int weatherId) {
		this.weatherId = weatherId;
	}
	
	/**
	 * Sets the fullName value for this WeatherDTO.
	 * @param fullName
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	/**
	 * Sets the city value for this WeatherDTO.
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}
	
	/**
	 * Sets the stateCode value for this WeatherDTO.
	 * @param stateCode
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	
	/**
	 * Sets the stateName value for this WeatherDTO.
	 * @param stateName
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	
	/**
	 * Sets the country value for this WeatherDTO.
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	
	/**
	 * Sets the countryCode value for this WeatherDTO.
	 * @param countryCode
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	/**
	 * Sets the zip value for this WeatherDTO.
	 * @param zip
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	/**
	 * Sets the latitude value for this WeatherDTO.
	 * @param latitude
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Sets the longitude value for this WeatherDTO.
	 * @param longitude
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Sets the observedTimeStamp value for this WeatherDTO.
	 * @param observedTimeStamp
	 */
	public void setObservedTimeStamp(String observedTimeStamp) {
		this.observedTimeStamp = observedTimeStamp;
	}
	
	/**
	 * Sets the weather value for this WeatherDTO.
	 * @param weather
	 */
	public void setWeather(String weather) {
		this.weather = weather;
	}
	
	/**
	 * Sets the temperature value for this WeatherDTO.
	 * @param temperature
	 */
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	
	/**
	 * Sets the humidity value for this WeatherDTO.
	 * @param humidity
	 */
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	
	/**
	 * Sets the windSpeed value for this WeatherDTO.
	 * @param windSpeed
	 */
	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}
	
	/**
	 * Sets the feelLikeTemperature value for this WeatherDTO.
	 * @param feelLikeTemperature
	 */
	public void setFeelLikeTemperature(String feelLikeTemperature) {
		this.feelLikeTemperature = feelLikeTemperature;
	}
	
	/**
	 * Sets the precipitation value for this WeatherDTO.
	 * @param precipitation
	 */
	public void setPrecipitation(String precipitation) {
		this.precipitation = precipitation;
	}
	
	/**
	 * Sets the maxTemperature value for this WeatherDTO.
	 * @param maxTemperature
	 */
	public void setMaxTemperature(String maxTemperature) {
		this.maxTemperature = maxTemperature;
	}
	
	/**
	 * Sets the minTemperature value for this WeatherDTO.
	 * @param minTemperature
	 */
	public void setMinTemperature(String minTemperature) {
		this.minTemperature = minTemperature;
	}
	
	/**
	 * Sets the rainFall value for this WeatherDTO.
	 * @param rainFall
	 */
	public void setRainFall(String rainFall) {
		this.rainFall = rainFall;
	}
	
	/**
	 * Sets the snowDepth value for this WeatherDTO.
	 * @param snowDepth
	 */
	public void setSnowDepth(String snowDepth) {
		this.snowDepth = snowDepth;
	}
	
}
