package com.enerallies.vem.beans.iot;

/**
 * File Name : Weather 
 * 
 * Weather: is for transfer outside weather related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        13-10-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	13-10-2016		Rajashekharaiah Muniswamy		File Created
 */
public class Weather {

	/**This property is used to hold weather outside temperature at the site*/
	private String 		temp_f;

	/**This property is used to hold weather icon url*/
	private String 		icon_url;

	/**This property is used to hold weather*/
	private String 		weather;
	
	/**This property is used to hold high temperature*/
	private String 		high_temp;

	/**This property is used to hold low temperature*/
	private String 		low_temp;

	/**This property is used to hold feels like temperature*/
	private String 		feelslike_f;

	/**This property is used to hold humidity*/
	private String 		relative_humidity;

	/**
	 * @return the temp_f
	 */
	public String getTemp_f() {
		return temp_f;
	}

	/**
	 * @param temp_f the temp_f to set
	 */
	public void setTemp_f(String temp_f) {
		this.temp_f = temp_f;
	}

	/**
	 * @return the icon_url
	 */
	public String getIcon_url() {
		return icon_url;
	}

	/**
	 * @param icon_url the icon_url to set
	 */
	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	/**
	 * @return the weather
	 */
	public String getWeather() {
		return weather;
	}

	/**
	 * @param weather the weather to set
	 */
	public void setWeather(String weather) {
		this.weather = weather;
	}

	/**
	 * @return the high_temp
	 */
	public String getHigh_temp() {
		return high_temp;
	}

	/**
	 * @param high_temp the high_temp to set
	 */
	public void setHigh_temp(String high_temp) {
		this.high_temp = high_temp;
	}

	/**
	 * @return the low_temp
	 */
	public String getLow_temp() {
		return low_temp;
	}

	/**
	 * @param low_temp the low_temp to set
	 */
	public void setLow_temp(String low_temp) {
		this.low_temp = low_temp;
	}

	/**
	 * @return the feelslike_f
	 */
	public String getFeelslike_f() {
		return feelslike_f;
	}

	/**
	 * @param feelslike_f the feelslike_f to set
	 */
	public void setFeelslike_f(String feelslike_f) {
		this.feelslike_f = feelslike_f;
	}

	/**
	 * @return the relative_humidity
	 */
	public String getRelative_humidity() {
		return relative_humidity;
	}

	/**
	 * @param relative_humidity the relative_humidity to set
	 */
	public void setRelative_humidity(String relative_humidity) {
		this.relative_humidity = relative_humidity;
	}
	
	
	

}
