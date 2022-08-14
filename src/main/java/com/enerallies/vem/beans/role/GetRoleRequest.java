/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.beans.role;

import javax.validation.constraints.NotNull;

import com.enerallies.vem.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : GetRoleRequest 
 * 
 * GetRoleRequest: is used to hold the get role request data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        09-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 09-08-2016		Goush Basha		    File Created.
 * 
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class GetRoleRequest {
	
	/** This property is used to hold the roleId. */
	@NotNull(message = ErrorCodes.WARN_ROLE_ID_NOT_NULL)
	private Integer roleId;

	/**
	 * Gets the roleId value for this Role.
	 * @return roleId
	 */
	public Integer getRoleId() {
		return roleId;
	}

	/**
	 * Sets the roleId value for this Role.
	 * @param roleId
	 */
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

}
