/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.service.role;

import org.springframework.stereotype.Component;
import com.enerallies.vem.beans.role.AddRoleRequest;
import com.enerallies.vem.beans.role.GetRoleRequest;
import com.enerallies.vem.beans.role.UpdatedRoleRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : RoleService 
 * 
 * RoleService service is used to serve the all role related operations.
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
 * 08-08-2016		Goush Basha		    addRole() method has added.
 * 09-08-2016		Goush Basha		    getRoleDetails() method has added.
 * 10-08-2016		Goush Basha		    updateRole() method has added.
 * 11-08-2016       Goush Basha	        listRoles() and deleteRole() methods has added.
 * 13-10-2016		Goush Basha			Added the method loadPermissions.
 *
 */

@Component
public interface RoleService {
	
	/**
	 * addRole service is used to create the new role.
	 * 
	 * @param roleRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response addRole(AddRoleRequest roleRequest,int userId) throws VEMAppException;
	
	/**
	 * getRoleDetails service is used to get the role details.
	 * 
	 * @param roleId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getRoleDetails(int roleId) throws VEMAppException;
	
	/**
	 * updateRole service is used to update the existing role.
	 * 
	 * @param updatedRoleRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response updateRole(UpdatedRoleRequest updatedRoleRequest,int userId) throws VEMAppException;
	
	/**
	 * listRoles service is used to list all the role details.
	 * 
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response listRoles() throws VEMAppException;
	
	/**
	 * deleteRole service is used to delete the role.
	 *  
	 * @param roleId
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response deleteRole(int roleId,int userId) throws VEMAppException;
	
	/**
	 * loadPermissions service is used to load the permissions from DB.
	 *  
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response loadPermissions() throws VEMAppException;
}
