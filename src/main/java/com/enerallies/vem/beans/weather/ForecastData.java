package com.enerallies.vem.beans.weather;

/**
 * File Name : ForecastData 
 * 
 * ForecastData: is for transfer forecast data between different modules
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        23-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	23-11-2016		Rajashekharaiah Muniswamy		File Created
 */
public class ForecastData {

	/**used to hold forecast id*/
	private int forecastId;

	/**used to hold device id*/
	private int deviceId;
	
	/**used to hold zipcode*/
	private String zipcode;
	
	/**used to hold today's minimum temperature*/
	private String todayMinTemp;

	/**used to hold today's maximum temperature*/
	private String todayMaxTemp;

	/**used to hold tomorrow's minimum temperature*/
	private String tomoMinTemp;

	/**used to hold tomorrow's maximum temperature*/
	private String tomoMaxTemp;

	/**used to hold created on date time*/
	private String createdOn;
	
	
	
	/**
	 * @return the createdOn
	 */
	public String getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the forecastId
	 */
	public int getForecastId() {
		return forecastId;
	}

	/**
	 * @param forecastId the forecastId to set
	 */
	public void setForecastId(int forecastId) {
		this.forecastId = forecastId;
	}

	/**
	 * @return the deviceId
	 */
	public int getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * @param zipcode the zipcode to set
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @return the todayMinTemp
	 */
	public String getTodayMinTemp() {
		return todayMinTemp;
	}

	/**
	 * @param todayMinTemp the todayMinTemp to set
	 */
	public void setTodayMinTemp(String todayMinTemp) {
		this.todayMinTemp = todayMinTemp;
	}

	/**
	 * @return the todayMaxTemp
	 */
	public String getTodayMaxTemp() {
		return todayMaxTemp;
	}

	/**
	 * @param todayMaxTemp the todayMaxTemp to set
	 */
	public void setTodayMaxTemp(String todayMaxTemp) {
		this.todayMaxTemp = todayMaxTemp;
	}

	/**
	 * @return the tomoMinTemp
	 */
	public String getTomoMinTemp() {
		return tomoMinTemp;
	}

	/**
	 * @param tomoMinTemp the tomoMinTemp to set
	 */
	public void setTomoMinTemp(String tomoMinTemp) {
		this.tomoMinTemp = tomoMinTemp;
	}

	/**
	 * @return the tomoMaxTemp
	 */
	public String getTomoMaxTemp() {
		return tomoMaxTemp;
	}

	/**
	 * @param tomoMaxTemp the tomoMaxTemp to set
	 */
	public void setTomoMaxTemp(String tomoMaxTemp) {
		this.tomoMaxTemp = tomoMaxTemp;
	}

}
