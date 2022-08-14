package com.enerallies.vem.beans.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : GroupRequest 
 * 
 * AuditRequest: is used to hold the Audit request data from client.
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
 * 1-11-2016		Arun		    File Created.
 * 
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditRequest {

	private int userId;
	private String userAction;
	private String serviceId;
	private String description;
	private String location;
	private int serviceSpecificId;
	private String outFlag;
	private String outErrMsg;
	
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getServiceSpecificId() {
		return serviceSpecificId;
	}
	public void setServiceSpecificId(int serviceSpecificId) {
		this.serviceSpecificId = serviceSpecificId;
	}
	public String getOutFlag() {
		return outFlag;
	}
	public void setOutFlag(String outFlag) {
		this.outFlag = outFlag;
	}
	public String getOutErrMsg() {
		return outErrMsg;
	}
	public void setOutErrMsg(String outErrMsg) {
		this.outErrMsg = outErrMsg;
	}
	
	
}
