/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.beans.audit;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : AddManualLogRequest 
 * 
 * AddManualLogRequest: is used to hold the new manual activity log data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        11-10-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER              	COMMENTS
 * 11-10-2016		Goush Basha		    File Created.
 * 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AddManualLogRequest {
	
	@NotEmpty(message = ErrorCodes.ERROR_SUBJECT_EMPTY)
	private String subject;
	
	@NotNull(message = ErrorCodes.ERROR_TYPE_INVALID)
	private Integer type;
	
	@NotEmpty(message = ErrorCodes.ERROR_TIMESTAMP_EMPTY)
	private String timeStamp;
	
	private String contact;
	private String contactNumber;
	
	@NotNull(message = ErrorCodes.ERROR_CUSTOMER_ID_INVALID)
	private Integer customerId;
	private Integer groupId;
	private Integer siteId;
	private Integer deviceId;
	private String description;
	private Integer specificId;
	
	private Integer isPdfReport;
	private Integer reportPreference;
	private Integer reportLevel;
	private String 	reportLevelIds;
	private String 	reportComponent;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public Integer getSiteId() {
		return siteId;
	}
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getIsPdfReport() {
		return isPdfReport;
	}
	public void setIsPdfReport(Integer isPdfReport) {
		this.isPdfReport = isPdfReport;
	}
	public Integer getReportPreference() {
		return reportPreference;
	}
	public void setReportPreference(Integer reportPreference) {
		this.reportPreference = reportPreference;
	}
	public Integer getReportLevel() {
		return reportLevel;
	}
	public void setReportLevel(Integer reportLevel) {
		this.reportLevel = reportLevel;
	}
	public String getReportLevelIds() {
		return reportLevelIds;
	}
	public void setReportLevelIds(String reportLevelIds) {
		this.reportLevelIds = reportLevelIds;
	}
	public Integer getSpecificId() {
		return specificId;
	}
	public void setSpecificId(Integer specificId) {
		this.specificId = specificId;
	}
	public String getReportComponent() {
		return reportComponent;
	}
	public void setReportComponent(String reportComponent) {
		this.reportComponent = reportComponent;
	}

}
