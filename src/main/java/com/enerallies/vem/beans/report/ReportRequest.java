/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.report;

/**
 * File Name : ReportRequest 
 * 
 * ReportRequest: is a bean for reports
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
 * 01	23-11-2016			Nagarjuna Eerla		File Created
 */
public class ReportRequest {

	private String customerIds;
	private String groupIds;
	private String siteIds;
	private String deviceIds;
	private String type;
	private int inDays;
	private String fromDate;
	private String toDate;
	private String reportType;
	private String params;
	private String farenHeatParams;
	private String onOffParams;

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
	public String getCustomerIds() {
		return customerIds;
	}
	public void setCustomerIds(String customerIds) {
		this.customerIds = customerIds;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getInDays() {
		return inDays;
	}
	public void setInDays(int inDays) {
		this.inDays = inDays;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	
}
