package com.enerallies.vem.beans.weather;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : AddForecastRequest 
 * 
 * AddForecastRequest: is for transfer forecast related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        28-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	28-11-2016		Rajashekharaiah Muniswamy		File Created
 */
public class AddForecastRequest {

	/**used to hold mode 0:OFF, 1:ON*/
	@NotNull(message=ErrorCodes.ERROR_FORECAST_MODE_NULL)
	private Integer mode;
	
	/**used to hold type 0:siteId, 1:deviceId*/
	@NotNull(message=ErrorCodes.ERROR_FORECAST_TYPE_NULL)
	private Integer type;

	/**used to hold type id*/
	@NotNull(message=ErrorCodes.ERROR_FORECAST_TYPE_ID_NULL)
	private Integer typeId;

	/**used to hold forecast name*/
	@NotEmpty(message=ErrorCodes.ERROR_FORECAST_FORECASTNAME_EMPTY)
	private String forecastName;

	/**used to hold from date*/
	@NotEmpty(message=ErrorCodes.ERROR_FORECAST_FROMDATE_EMPTY)
	private String fromDate;
	
	/**used to hold todate*/
	@NotEmpty(message=ErrorCodes.ERROR_FORECAST_TODATE_EMPTY)
	private String toDate;
	
	/**used to hold created by username*/
	private int createdBy;
	
	private String addType;
	
	
	private List<ForecastTemp> forecastTempList;


	/**
	 * @return the addType
	 */
	public String getAddType() {
		return addType;
	}


	/**
	 * @param addType the addType to set
	 */
	public void setAddType(String addType) {
		this.addType = addType;
	}


	/**
	 * @return the createdBy
	 */
	public int getCreatedBy() {
		return createdBy;
	}


	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}


	/**
	 * @return the mode
	 */
	public Integer getMode() {
		return mode;
	}


	/**
	 * @param mode the mode to set
	 */
	public void setMode(Integer mode) {
		this.mode = mode;
	}


	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}


	/**
	 * @return the typeId
	 */
	public Integer getTypeId() {
		return typeId;
	}


	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}


	/**
	 * @return the forecastName
	 */
	public String getForecastName() {
		return forecastName;
	}


	/**
	 * @param forecastName the forecastName to set
	 */
	public void setForecastName(String forecastName) {
		this.forecastName = forecastName;
	}


	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}


	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}


	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}


	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}


	/**
	 * @return the forcastTempList
	 */
	public List<ForecastTemp> getForecastTempList() {
		return forecastTempList;
	}


	/**
	 * @param forcastTempList the forcastTempList to set
	 */
	public void setForecastTempList(List<ForecastTemp> forecastTempList) {
		this.forecastTempList = forecastTempList;
	}
	
	
	
}
