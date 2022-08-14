/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.dao.role;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;
import com.enerallies.vem.beans.role.AddRoleRequest;
import com.enerallies.vem.beans.role.UpdatedRoleRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : RoleDAO 
 * RoleDAO dao is used to serve all the database level operations related to role.
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
@Repository
public interface RoleDAO {
	
	 /**
	  * This is the method to be used to initialize database resources i.e. connection.
	  * 
	  * @param dataSource
	  */
	 public void setDataSource(DataSource dataSource);
	 
	 /**
	  * addRole dao is used to Create a new Role in databbase.
	  * 
	  * @param role
	  * @param userId
	  * @return int
	  * @throws VEMAppException
	  */
	 public int addRole(AddRoleRequest role,int userId) throws VEMAppException;
	 
	 /**
	  * getRoleDetails dao is used to get the details of Role from database.
	  * 
	  * @param roleId
	  * @return JSONObject
	  * @throws VEMAppException
	  */
	 public JSONObject getRoleDetails(int roleId) throws VEMAppException;
	 
	 /**
	  * updateRole dao is used to update an existing a Role in database.
	  * 
	  * @param updatedRoleRequest
	  * @param userId
	  * @return int
	  * @throws VEMAppException
	  */
	 public int updateRole(UpdatedRoleRequest updatedRoleRequest,int userId) throws VEMAppException;
	 
	 /**
	  * listRoles dao is used to list all the roles from Database.
	  * 
	  * @return JSONObject
	  * @throws VEMAppException
	  */
	 public JSONObject listRoles() throws VEMAppException;
	 
	 /**
	  * deleteRole dao is used to delete role from database.
	  * 
	  * @param roleId
	  * @param userId
	  * @return int
	  * @throws VEMAppException
	  */
	 public int deleteRole(int roleId,int userId) throws VEMAppException;
	 
	 /**
	  * loadPermissions dao is used to get the permissions from database.
	  * 
	  * @return JSONObject
	  * @throws VEMAppException
	  */
	 public JSONObject loadPermissions() throws VEMAppException;

}
