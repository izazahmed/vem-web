package com.enerallies.vem.beans.iot;

import javax.validation.constraints.NotNull;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : UpdateForecastTemp 
 * 
 * UpdateForecastTemp: is for transfer forecast temperature related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        03-01-2017
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	03-01-2017		Rajashekharaiah Muniswamy		File Created
 */
public class UpdateForecastTemp {

	/**used to hold forecast temp id*/
	@NotNull(message=ErrorCodes.ERROR_FORECAST_TEMP_ID_NULL)
	private Integer forecastTempId;
	
	/**used to hold forecast id*/
	@NotNull(message=ErrorCodes.ERROR_FORECAST_ID_NULL)
	private Integer forecastId;
	
	/**used to hold minimum temperature*/
	@NotNull(message=ErrorCodes.ERROR_FORECAST_MINTEMP_NULL)
	private Integer minTemp;
	
	/**used to hold maximum temperature*/
	@NotNull(message=ErrorCodes.ERROR_FORECAST_MAXTEMP_NULL)
	private Integer maxTemp;
	
	/**used to hold scheduleId*/
	@NotNull(message=ErrorCodes.ERROR_FORECAST_SCHEDULEID_NULL)
	private Integer scheduleId;

	/**
	 * @return the forecastTempId
	 */
	public Integer getForecastTempId() {
		return forecastTempId;
	}

	/**
	 * @param forecastTempId the forecastTempId to set
	 */
	public void setForecastTempId(Integer forecastTempId) {
		this.forecastTempId = forecastTempId;
	}

	/**
	 * @return the forecastId
	 */
	public Integer getForecastId() {
		return forecastId;
	}

	/**
	 * @param forecastId the forecastId to set
	 */
	public void setForecastId(Integer forecastId) {
		this.forecastId = forecastId;
	}

	/**
	 * @return the minTemp
	 */
	public Integer getMinTemp() {
		return minTemp;
	}

	/**
	 * @param minTemp the minTemp to set
	 */
	public void setMinTemp(Integer minTemp) {
		this.minTemp = minTemp;
	}

	/**
	 * @return the maxTemp
	 */
	public Integer getMaxTemp() {
		return maxTemp;
	}

	/**
	 * @param maxTemp the maxTemp to set
	 */
	public void setMaxTemp(Integer maxTemp) {
		this.maxTemp = maxTemp;
	}

	/**
	 * @return the scheduleId
	 */
	public Integer getScheduleId() {
		return scheduleId;
	}

	/**
	 * @param scheduleId the scheduleId to set
	 */
	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	
}
