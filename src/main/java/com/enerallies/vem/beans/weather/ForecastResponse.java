package com.enerallies.vem.beans.weather;

import java.util.List;


/**
 * File Name : ForecastResponse 
 * 
 * ForecastResponse: is for transfer forecast related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        29-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	29-11-2016		Rajashekharaiah Muniswamy		File Created
 */
public class ForecastResponse {

	/**used to hold forecast id*/
	private Integer forecastId;

	/**used to hold mode 0:OFF, 1:ON*/
	private Integer mode;
	
	/**used to hold type 0:siteId, 1:deviceId*/
	private Integer type;

	/**used to hold type id*/
	private Integer typeId;

	/**used to hold forecast name*/
	private String forecastName;

	/**used to hold from date*/
	private String fromDate;
	
	/**used to hold todate*/
	private String toDate;
	
	/**used to hold created by username*/
	private int createdBy;
	
	/**used to hold created on date*/
	private String createdOn;

	/**used to hold updated by user id*/
	private int updatedBy;
	
	/**used to hold updated on date*/
	private String updatedOn;

	private List<ForecastTempResponse> forecastTempList;

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
	 * @return the updatedBy
	 */
	public int getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updatedOn
	 */
	public String getUpdatedOn() {
		return updatedOn;
	}

	/**
	 * @param updatedOn the updatedOn to set
	 */
	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	/**
	 * @return the forecastTempList
	 */
	public List<ForecastTempResponse> getForecastTempList() {
		return forecastTempList;
	}

	/**
	 * @param forecastTempList the forecastTempList to set
	 */
	public void setForecastTempList(List<ForecastTempResponse> forecastTempList) {
		this.forecastTempList = forecastTempList;
	}

	
}
