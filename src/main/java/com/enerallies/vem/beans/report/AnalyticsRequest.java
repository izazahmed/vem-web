/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.report;

/**
 * File Name : AnalyticsRequest 
 * 
 * AnalyticsRequest: is a bean for Analytics
 *
 * @author Naagrjuna Eerla
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        22-11-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	05-12-2016			Nagarjuna Eerla		File Created
 */
public class AnalyticsRequest {
	
	private String customerId;
	private String groupIds;
	private String siteIds;
	private String deviceIds;
	private String fromDate;
	private String toDate;
	private String dataType;
	private String analyticParams;
	private String farenHeatParams;
	private String onOffParams;
	private String modeParams;
	private String daysParams;
	private int degreeDaysGraph;
	private String type;

	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}
	public String getSiteIds() {
		return siteIds;
	}
	public void setSiteIds(String siteIds) {
		this.siteIds = siteIds;
	}
	public String getDeviceIds() {
		return deviceIds;
	}
	public void setDeviceIds(String deviceIds) {
		this.deviceIds = deviceIds;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getAnalyticParams() {
		return analyticParams;
	}
	public void setAnalyticParams(String analyticParams) {
		this.analyticParams = analyticParams;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFarenHeatParams() {
		return farenHeatParams;
	}
	public void setFarenHeatParams(String farenHeatParams) {
		this.farenHeatParams = farenHeatParams;
	}
	public String getOnOffParams() {
		return onOffParams;
	}
	public void setOnOffParams(String onOffParams) {
		this.onOffParams = onOffParams;
	}
	public String getModeParams() {
		return modeParams;
	}
	public void setModeParams(String modeParams) {
		this.modeParams = modeParams;
	}
	public String getDaysParams() {
		return daysParams;
	}
	public void setDaysParams(String daysParams) {
		this.daysParams = daysParams;
	}
	public int getDegreeDaysGraph() {
		return degreeDaysGraph;
	}
	public void setDegreeDaysGraph(int degreeDaysGraph) {
		this.degreeDaysGraph = degreeDaysGraph;
	}
}
