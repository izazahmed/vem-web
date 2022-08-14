/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : GroupRequest 
 * 
 * GroupRequest: is used to hold the Group request data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        16-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 16-09-2016		Raja		    File Created.
 * 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AlertRequest extends GetUserResponse {

	private String actionItemIds;
	private String actionItemStatus;
	private String alert;
	private int alertConfigId;
	private int alertId;
	private ArrayList<Integer> alertIds;
	private String alertStatus;
	private String alertType;
	private String customer;
	private int customerId;
	private String deviceJSON;
	private String fromCurrentPage;
	private String parameterId;
	private String parameterUnit;
	private String priority;
	private int statusId;
    private List<String> userAction;
    private ArrayList<HashMap> actionItems;
    private String actionStatus;
    private String deviceXpecId;
    private String specificId;
    private int timePeriodInDays;
    private String pdfReportalertIds;
	

	public String getPdfReportalertIds() {
		return pdfReportalertIds;
	}

	public void setPdfReportalertIds(String pdfReportalertIds) {
		this.pdfReportalertIds = pdfReportalertIds;
	}

	public String getActionItemIds() {
		return actionItemIds;
	}

	public ArrayList<HashMap> getActionItems() {
		return actionItems;
	}

	public String getActionItemStatus() {
		return actionItemStatus;
	}

	public String getActionStatus() {
		return actionStatus;
	}

	public String getAlert() {
		return alert;
	}

	public int getAlertConfigId() {
		return alertConfigId;
	}

	public int getAlertId() {
		return alertId;
	}

	public ArrayList<Integer> getAlertIds() {
		return alertIds;
	}
	
	public String getAlertStatus() {
		return alertStatus;
	}

	public String getAlertType() {
		return alertType;
	}

	public String getCustomer() {
		return customer;
	}

	public int getCustomerId() {
		return customerId;
	}

	public String getDeviceJSON() {
		return deviceJSON;
	}

	public String getDeviceXpecId() {
		return deviceXpecId;
	}

	public String getFromCurrentPage() {
		return fromCurrentPage;
	}
	
	public String getParameterId() {
		return parameterId;
	}

	public String getParameterUnit() {
		return parameterUnit;
	}

	public String getPriority() {
		return priority;
	}

	public String getSpecificId() {
		return specificId;
	}

	public int getStatusId() {
		return statusId;
	}

	public List<String> getUserAction() {
		return userAction;
	}

	public void setActionItemIds(String actionItemIds) {
		this.actionItemIds = actionItemIds;
	}

	public void setActionItems(ArrayList<HashMap> actionItems) {
		this.actionItems = actionItems;
	}

	public void setActionItemStatus(String actionItemStatus) {
		this.actionItemStatus = actionItemStatus;
	}

	public void setActionStatus(String actionStatus) {
		this.actionStatus = actionStatus;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public void setAlertConfigId(int alertConfigId) {
		this.alertConfigId = alertConfigId;
	}

	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}

	public void setAlertIds(ArrayList<Integer> alertIds) {
		this.alertIds = alertIds;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public void setDeviceJSON(String deviceJSON) {
		this.deviceJSON = deviceJSON;
	}

	public void setDeviceXpecId(String deviceXpecId) {
		this.deviceXpecId = deviceXpecId;
	}

	public void setFromCurrentPage(String fromCurrentPage) {
		this.fromCurrentPage = fromCurrentPage;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public void setParameterUnit(String parameterUnit) {
		this.parameterUnit = parameterUnit;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setSpecificId(String specificId) {
		this.specificId = specificId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setUserAction(List<String> userAction) {
		this.userAction = userAction;
	}

	public int getTimePeriodInDays() {
		return timePeriodInDays;
	}

	public void setTimePeriodInDays(int timePeriodInDays) {
		this.timePeriodInDays = timePeriodInDays;
	}

}
