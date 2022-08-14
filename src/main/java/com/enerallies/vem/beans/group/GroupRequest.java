/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.beans.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : GroupRequest
 * 
 * GroupRequest: is used to hold the Group request data from client.
 *
 * @author Cambridge Technologies. contact Cambridge Technologies – Umang Gupta
 *         (ugupta@ctepl.com) EnerAllies - Loanne Cheung
 *         (lcheung@enerallies.com)
 * 
 * @version VEM2-1.0
 * @date 16-09-2016
 *
 *       MODIFICATION HISTORY
 * 
 *       DATE USER COMMENTS 16-09-2016 Raja File Created.
 * 
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupRequest {

	private int customerId;
	private String customerIds;
	private String moduleName;
	private String groupDesc;
	private int groupId;
	private String groupName;
	private int groupStatusCode;
	private int moduleId;
	private int isEAI;
	private int isSuperAdmin;
	private ArrayList<HashMap<String, String>> selectedLocations;
	private List<String> sites;
	private int statusId;
	private int updateMode = 0;
	private int userId;
	
	
	
	public int getCustomerId() {
		return customerId;
	}

	public String getCustomerIds() {
		return customerIds;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public int getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public int getGroupStatusCode() {
		return groupStatusCode;
	}

	public int getIsEAI() {
		return isEAI;
	}

	public int getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public ArrayList<HashMap<String, String>> getSelectedLocations() {
		return selectedLocations;
	}

	public List<String> getSites() {
		return sites;
	}

	public int getStatusId() {
		return statusId;
	}

	public int getUpdateMode() {
		return updateMode;
	}

	public int getUserId() {
		return userId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public void setCustomerIds(String customerIds) {
		this.customerIds = customerIds;
	}


	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setGroupStatusCode(int groupStatusCode) {
		this.groupStatusCode = groupStatusCode;
	}


	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public void setIsEAI(int isEAI) {
		this.isEAI = isEAI;
	}

	public void setIsSuperAdmin(int isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public void setSelectedLocations(
			ArrayList<HashMap<String, String>> selectedLocations) {
		this.selectedLocations = selectedLocations;
	}

	public void setSites(List<String> sites) {
		this.sites = sites;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public void setUpdateMode(int updateMode) {
		this.updateMode = updateMode;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
