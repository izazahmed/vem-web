/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.beans.pdfreport;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown=true)
public class AddPDFReportDataRequest {
	
	private String reportLevelText;
	
	private int userId;
	
	private String fromDate;
	
	private String toDate;
	
	private String actualFilePath;
	
	private int reportStatus;
	
	private int reportLevel;
	
	private int reportPreference;

	public int getReportPreference() {
		return reportPreference;
	}

	public void setReportPreference(int reportPreference) {
		this.reportPreference = reportPreference;
	}

	private List<String> specificIds;
	
	private String siteIds;

	public String getReportLevelText() {
		return reportLevelText;
	}

	public void setReportLevelText(String reportLevelText) {
		this.reportLevelText = reportLevelText;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	
	public String getActualFilePath() {
		return actualFilePath;
	}

	public void setActualFilePath(String actualFilePath) {
		this.actualFilePath = actualFilePath;
	}

	public int getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(int reportStatus) {
		this.reportStatus = reportStatus;
	}

	public List<String> getSpecificIds() {
		return specificIds;
	}

	public void setSpecificIds(List<String> specificIds) {
		this.specificIds = specificIds;
	}

	public int getReportLevel() {
		return reportLevel;
	}

	public void setReportLevel(int reportLevel) {
		this.reportLevel = reportLevel;
	}

	public String getSiteIds() {
		return siteIds;
	}

	public void setSiteIds(String siteIds) {
		this.siteIds = siteIds;
	}

}
