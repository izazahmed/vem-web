/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.beans.role;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.json.simple.JSONArray;

import com.enerallies.vem.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : AddRoleRequest 
 * 
 * AddRoleRequest: is used to hold the new role request data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 08-08-2016		Goush Basha		    File Created.
 * 30-08-2016       Goush Basha			Sprint-2 Suggestions/Changes.
 * 
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class AddRoleRequest {
	
	/** This property is used to hold the roleName. */
	@NotEmpty(message = ErrorCodes.WARN_ROLE_NAME_NOT_NULL)
	@Size(max=50, message=ErrorCodes.WARN_FIRST_NAME_SIZE)
	private String roleName;
	
	/** This property is used to hold the roleType. */
	@NotNull(message = ErrorCodes.WARN_ROLE_ISEAI_INVALID)
	private Integer roleType;
	
	/** This property is used to hold the permissions. */
	@NotNull(message = ErrorCodes.WARN_ROLE_PERMISSIONS_EMPTY)
	private JSONArray permissions;
	
	/** This property is used to hold the createActivityLog. */
	private Integer createActivityLog;
	
	/** This property is used to hold the isActive. */
	private Integer isActive;

	/*********************************** Getters ***********************/
	/**
	 * Gets the roleName value for this Role.
	 * @return roleName
	 */
	public String getRoleName() {
		return roleName;
	}
	
	/**
	 * Gets the roleType value for this Role.
	 * @return roleType
	 */
	public Integer getRoleType() {
		return roleType;
	}
	
	/**
	 * Gets the permissions value for this Role.
	 * @return permissions
	 */
	public JSONArray getPermissions() {
		return permissions;
	}
	
	/**
	 * Gets the createActivityLog value for this Role.
	 * @return createActivityLog
	 */
	public Integer getCreateActivityLog() {
		return createActivityLog;
	}
	
	/**
	 * Gets the isActive value for this Role.
	 * @return isActive
	 */
	public Integer getIsActive() {
		return isActive;
	}
	
	/*********************************** Setters ***********************/
	/**
	 * Sets the roleName value for this Role.
	 * @param roleName
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	/**
	 * Sets the roleType value for this Role.
	 * @param roleType
	 */
	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}
	
	/**
	 * Sets the permissions value for this Role.
	 * @param permissions
	 */
	public void setPermissions(JSONArray permissions) {
		this.permissions = permissions;
	}
	
	/**
	 * Sets the createActivityLog value for this Role.
	 * @param createActivityLog
	 */
	public void setCreateActivityLog(Integer createActivityLog) {
		this.createActivityLog = createActivityLog;
	}
	
	/**
	 * Sets the isActive value for this Role.
	 * @param isActive
	 */
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
		
}
