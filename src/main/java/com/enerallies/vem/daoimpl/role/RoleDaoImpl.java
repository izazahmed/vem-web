/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.daoimpl.role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.role.AddRoleRequest;
import com.enerallies.vem.beans.role.UpdatedRoleRequest;
import com.enerallies.vem.dao.role.RoleDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.TableFieldConstants;

/**
 * File Name : RoleDaoImpl 
 * RoleDaoImpl: Its an implementation class for RoleDAO interface.
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
 * 30-08-2016       Goush Basha			Sprint-2 Suggestions/Changes.
 * 07-10-2016       Goush Basha			Fixed the Issue-#193: Roles are not displaying in alphabetical order for the roles field in new user page.
 * 13-10-2016		Goush Basha			Added the method loadPermissions.
 *
 */

@Component
public class RoleDaoImpl implements RoleDAO{
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(RoleDaoImpl.class);
	
	/** Data source instance */
	private DataSource dataSource;
	
	/** JDBC Template instance */
	private JdbcTemplate jdbcTemplate;	
	
	private static final String RESULT_SET_1 = "#result-set-1";
	private static final String RESULT_SET_2 = "#result-set-2";
	
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public int addRole(AddRoleRequest role,int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [addRole] [DAO LAYER]");
		
		int roleId = 0;
		String errorFlag="";
		StringBuilder permissionListIds=new StringBuilder();
		StringBuilder permissionLevelListIds=new StringBuilder();
		
		LinkedHashMap<String, Integer> permissionObj = null;
		JSONArray permissionList = new JSONArray();
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_insert_role");
		    
		    //Getting the permission list from requested role.
		    permissionList = role.getPermissions();
		    
		    logger.debug("[DEBUG] permissionList - "+permissionList);
		    
			//checking the permission list and Permission level list
			if(permissionList!=null){
				
				//Making all the list values in the form of string bccause the procedure 
				//does not accept the array/arraylist as input param
				for(@SuppressWarnings({ "unchecked", "rawtypes" })
				Iterator<LinkedHashMap> iterator = permissionList.iterator(); iterator.hasNext();) {
					permissionObj =  iterator.next();
					logger.debug("[DEBUG] permissionObj - "+permissionObj);
					permissionListIds.append(permissionObj.get("permission") + ",");
					permissionLevelListIds.append(permissionObj.get("permissionLevel") + ",");
			    }
				
				/*
				 * If the isCso value is 0 then only we will have the permissions in permission list
				 * otherwise the Permission List is empty.
				 */
				//Removing the last charector.
				permissionListIds.replace(permissionListIds.length()-1, permissionListIds.length(), "");
				permissionLevelListIds.replace(permissionLevelListIds.length()-1,permissionLevelListIds.length(),"");
				
				logger.debug("[DEBUG] final permissionListIds - "+permissionListIds);
				logger.debug("[DEBUG] final permissionLevelListIds - "+permissionLevelListIds);
				
				//Adding all the input parameter values to a hashmap.
				Map<String,Object> inputParams=new HashMap<>();
				inputParams.put(TableFieldConstants.ROLE_NAME, role.getRoleName());
				inputParams.put("role_desc", "");
				inputParams.put("permission_ids", permissionListIds);
				inputParams.put("permission_level_ids", permissionLevelListIds);
				inputParams.put(TableFieldConstants.ROLE_IS_EAI, role.getRoleType());
				inputParams.put("user_id", userId);
				inputParams.put("in_create_activity_log", CommonUtility.isNull(role.getCreateActivityLog()));
				inputParams.put("in_is_active", CommonUtility.isNull(role.getIsActive()));
				
				logger.debug("[DEBUG] Executing the stored  procedure - sp_insert_role");
				logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - "+inputParams);
				
				//Adding all the input parameter map to simpleJdbcCall.execute method.
		        Map<String,Object> outParameters = simpleJdbcCall.execute(inputParams);
		        
		       logger.debug("[DEBUG] sp_insert_role out parameters - "+outParameters);
		        
		        /* 
		         * if the errorFlag is empty means the add role request 
		         * got success in database
		         * else there is an exception occured at database side and request got failed.
		         */
		        errorFlag = (String) outParameters.get(CommonConstants.ERROR_MSG);
		        if(errorFlag.isEmpty()){
		        	//getting the inserted role id value
		        	roleId = (int) outParameters.get(CommonConstants.ROLE_ID);
		        }else{
		        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_ROLE_FAILD_ERROR, logger, new Exception((String) outParameters.get("error_msg")));
		        }
				
			}else{
				/* 
				 * Catches when the permissionList and permission level list are null/sizes not matched and throwing
				 * an customized exception.
				 */
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_ROLE_FAILD_ERROR, 
						logger, new Exception("the Permission list and Permission level list are null/sizes not matched"));
			}
	        
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_ROLE_FAILD_ERROR, logger, e);
		}
		
		logger.info("[END] [addRole] [DAO LAYER]");

		return roleId;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getRoleDetails(int roleId) throws VEMAppException {
		
		logger.info("[BEGIN] [getRoleDetails] [Role DAO LAYER]");
		
		//Storing the role data in the below map
		HashMap<Integer, JSONObject> roleMap=new HashMap<>();
		JSONObject resultRole = new JSONObject();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_select_role procedure.");
			logger.debug("[DEBUG] role id - "+roleId);
			
			//Executing the procedure.
			jdbcTemplate.query("call sp_select_role ("+roleId+")", new RowCallbackHandler() {
				
				JSONArray permissionWithLevelList = null;
				JSONObject permissions = null;
				JSONObject roleObj = null;
				
				/*
				 * processRow call back method is used to process/loop the result set
				 * in order to form the Role details json. 
				 */
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {

			    	logger.debug("[DEbug] the role_id - "+rs.getInt(TableFieldConstants.ROLE_ID));
			  		  
			  		if(roleMap.containsKey(rs.getInt(TableFieldConstants.ROLE_ID))){
						/*
						 * Catches when the map contains the role.
						 * and getting the existing role object based on role id.
						 */
			  			roleObj = roleMap.get(rs.getInt(TableFieldConstants.ROLE_ID));
						
			  			/*
			  			 * Getting the existing permissions list object 
			  			 * and adding the new permission data to PermissionList.
			  			 */
			  			permissionWithLevelList = (JSONArray) roleObj.get(CommonConstants.PERMISSIONS);
						/*
						 * Checking whether the isCSO flag is 0 and permission_name, permission_level_name
						 * is not null then the role have the permissions to permissionList object.
						 * else we are not adding any permissions to permissionList object. 
						 */
						if(rs.getInt(TableFieldConstants.ROLE_IS_CSO)==0 && 
								rs.getString(TableFieldConstants.PERMISSION_ID)!=null && 
								rs.getString(TableFieldConstants.PERMISSION_LEVEL_ID)!=null){
							permissions = new JSONObject();
							permissions.put("permission", rs.getInt(TableFieldConstants.PERMISSION_ID));
							permissions.put("permissionName", rs.getString("permission_name"));
							permissions.put("permissionCode", rs.getString("permission_code"));
							permissions.put("permissionLevel", rs.getInt(TableFieldConstants.PERMISSION_LEVEL_ID));
							permissions.put("permissionLevelName", rs.getString("permission_level_name"));
							permissionWithLevelList.add(permissions);
						}
							
						
						//adding PermissionList to role object.
						roleObj.put(CommonConstants.PERMISSIONS, permissionWithLevelList);
						
						//Updating the Existing role object with new role object  
						roleMap.put(rs.getInt(TableFieldConstants.ROLE_ID), roleObj);
					}else{
						/*
						 * Catches when the map does not contains the specified role id 
						 * and Creating, setting the data to properties of Role Object.
						 * */
						roleObj = new JSONObject();
						roleObj.put("roleId", rs.getInt(TableFieldConstants.ROLE_ID));
						roleObj.put("roleType", rs.getInt(TableFieldConstants.ROLE_IS_EAI));
						roleObj.put("roleName", rs.getString(TableFieldConstants.ROLE_NAME));
						roleObj.put("superAdmin", rs.getInt(TableFieldConstants.ROLE_IS_SUPER));
						roleObj.put("customerSupport", rs.getInt(TableFieldConstants.ROLE_IS_CSO));
						roleObj.put("createActivityLog", rs.getInt("create_activity_log"));
						roleObj.put("isActive", rs.getInt("is_active"));
						roleObj.put("usersCount", rs.getInt("users_count"));
						
						//Creating the new object for permissions and adding the permissions.
						permissionWithLevelList=new JSONArray();
						/*
						 * Checking whether the isCSO flag is 0 and permission_name, permission_level_name
						 * is not null then the role have the permissions to permissionList object.
						 * else we are not adding any permissions to permissionList object. 
						 */
						if(rs.getInt(TableFieldConstants.ROLE_IS_CSO)==0 && 
								rs.getString(TableFieldConstants.PERMISSION_ID)!=null && 
								rs.getString(TableFieldConstants.PERMISSION_LEVEL_ID)!=null){
							permissions = new JSONObject();
							permissions.put("permission", rs.getInt(TableFieldConstants.PERMISSION_ID));
							permissions.put("permissionName", rs.getString("permission_name"));
							permissions.put("permissionCode", rs.getString("permission_code"));
							permissions.put("permissionLevel", rs.getInt(TableFieldConstants.PERMISSION_LEVEL_ID));
							permissions.put("permissionLevelName", rs.getString("permission_level_name"));
							permissionWithLevelList.add(permissions);
						}
						
						//Attaching the permissions list to role. 
						roleObj.put(CommonConstants.PERMISSIONS, permissionWithLevelList);
							
						//Adding the role object to hashMap
						roleMap.put(rs.getInt(TableFieldConstants.ROLE_ID), roleObj);
					}
				
				}
			});
			
			logger.debug("[DEBUG] final map - "+roleMap);
			
			//Setting the first role object to return object
			if(roleMap.size()>=1){
				resultRole.put("roleData", roleMap.get(roleId));
			}
			//Adding the load permission data to result object
			resultRole.put("permissionsData", loadPermissions());
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ROLE_DETAILS_FAILED_ERROR, logger, e);
		}
		
		logger.info("[END] [getRoleDetails] [Role DAO LAYER]");
		
		return resultRole;
	}
	
	@Override
	public int updateRole(UpdatedRoleRequest updatedRoleRequest,int userId) throws VEMAppException {
		
		logger.debug("[BEGIN] [updateRole] [DAO LAYER]");
		
		int flag = 0;
		String errorFlag="";
		StringBuilder permissionListIds=new StringBuilder();
		StringBuilder permissionLevelListIds=new StringBuilder();
		
		SimpleJdbcCall simpleJdbcCall;
		LinkedHashMap<String, Integer> permissionObj = null;
		JSONArray permissionList = new JSONArray();
		
		try {
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
			simpleJdbcCall.withProcedureName("sp_update_role");
			
			//Getting the permission list from requested role.
			permissionList = updatedRoleRequest.getPermissions();
		    
		    logger.debug("[DEBUG] permissionList - "+permissionList);
		    
			//checking the permission list and Permission level list
			if(permissionList!=null){
				
				//Making all the list values in the form of string bccause the procedure 
				//does not accept the array/arraylist as input param
				for(@SuppressWarnings({ "unchecked", "rawtypes" })
				Iterator<LinkedHashMap> iterator = permissionList.iterator(); iterator.hasNext();) {
					permissionObj =  iterator.next();
					logger.debug("[DEBUG] permissionObj - "+permissionObj);
					permissionListIds.append(permissionObj.get("permission") + ",");
					permissionLevelListIds.append(permissionObj.get("permissionLevel") + ",");
			    }
				
				/*
				 * If the isCso value is 0 then only we will have the permissions in permission list
				 * otherwise the Permission List is empty.
				 */
				//Removing the last charector.
				permissionListIds.replace(permissionListIds.length()-1, permissionListIds.length(), "");
				permissionLevelListIds.replace(permissionLevelListIds.length()-1,permissionLevelListIds.length(),"");
				
				logger.debug("[DEBUG] final permissionListIds - "+permissionListIds);
				logger.debug("[DEBUG] final permissionLevelListIds - "+permissionLevelListIds);
				
				//Adding all the input parameter values to a hashmap.
				Map<String,Object> inputParams=new HashMap<>();
				inputParams.put("roleid", updatedRoleRequest.getRoleId());
				inputParams.put("in_role_name", updatedRoleRequest.getRoleName());
				inputParams.put("in_is_eai", updatedRoleRequest.getRoleType());
				inputParams.put("permission_ids", permissionListIds);
				inputParams.put("permission_level_ids", permissionLevelListIds);
				inputParams.put("in_user_id", userId);
				inputParams.put("in_create_activity_log", CommonUtility.isNull(updatedRoleRequest.getCreateActivityLog()));
				inputParams.put("in_is_active", CommonUtility.isNull(updatedRoleRequest.getIsActive()));
				
				logger.debug("[DEBUG] Executing the stored  procedure - sp_update_role");
				logger.debug("[DEBUG] input parameters - "+inputParams);
				
				//Adding all the input parameter map to simpleJdbcCall.execute method.
		        Map<String,Object> outParameters = simpleJdbcCall.execute(inputParams);
		        
		        logger.debug("[DEBUG] sp_update_role out parameters - "+outParameters);
		        
		        /* 
		         * if the errorFlag is empty means the add role request 
		         * got success in database
		         * else there is an exception occured at database side and request got failed.
		         */
		        errorFlag = (String) outParameters.get("error_msg");
		        if(errorFlag.isEmpty()){
		        	flag = (int) outParameters.get("flag");
		        }else{
		        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_ROLE_DETAILS_FAILED_ERROR, logger, new Exception(errorFlag));
		        }
				
			}else{
				/* 
				 * Catches when the permissionList and permission level list are null/sizes not matched and throwing
				 * an customized exception.
				 */
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_ROLE_DETAILS_FAILED_ERROR, 
						logger, new Exception("the Permission list and Permission level list are null/sizes not matched"));
			}
	        
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_ROLE_DETAILS_FAILED_ERROR, logger, e);
		}
		
		logger.info("[END] [updateRole] [DAO LAYER]");
		
		return flag;
	}
	
	@Override
	public JSONObject listRoles() throws VEMAppException {
		
		logger.info("[BEGIN] [listRoles] [Role DAO LAYER]");
		
		//Storing the role data in the below map
		LinkedHashMap<Integer, JSONObject> roleMap=new LinkedHashMap<>();
		JSONArray roleArray= new JSONArray();
		JSONObject resultObj = new JSONObject();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_list_role procedure.");
			
			jdbcTemplate.query("call sp_list_role ()", new RowCallbackHandler() {
				
				JSONArray permissionWithLevelList = null;
				JSONObject permissions = null;
				JSONObject roleObj = null;
				
				/*
				 * processRow call back method is used to process/loop the result set
				 * in order to form the Role list json. 
				 */
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {


			    	logger.debug("[DEbug] the role_id - "+rs.getInt(TableFieldConstants.ROLE_ID));
			  		  
			  		if(roleMap.containsKey(rs.getInt(TableFieldConstants.ROLE_ID))){
						/*
						 * Catches when the map contains the role.
						 * and getting the existing role object based on role id.
						 */
			  			roleObj = roleMap.get(rs.getInt(TableFieldConstants.ROLE_ID));
						
			  			/*
			  			 * Getting the existing permissions list object 
			  			 * and adding the new permission data to PermissionList.
			  			 */
			  			permissionWithLevelList = (JSONArray) roleObj.get(CommonConstants.PERMISSIONS);
						/*
						 * Checking whether the isCSO flag is 0 and permission_name, permission_level_name
						 * is not null then the role have the permissions to permissionList object.
						 * else we are not adding any permissions to permissionList object. 
						 */
						if(rs.getInt(TableFieldConstants.ROLE_IS_CSO)==0 && 
								rs.getString(TableFieldConstants.PERMISSION_ID)!=null && 
								rs.getString(TableFieldConstants.PERMISSION_LEVEL_ID)!=null){
							permissions = new JSONObject();
							permissions.put("permission", rs.getInt(TableFieldConstants.PERMISSION_ID));
							permissions.put("permissionName", rs.getString("permission_name"));
							permissions.put("permissionCode", rs.getString("permission_code"));
							permissions.put("permissionLevel", rs.getInt(TableFieldConstants.PERMISSION_LEVEL_ID));
							permissions.put("permissionLevelName", rs.getString("permission_level_name"));
							permissionWithLevelList.add(permissions);
						}
							
						
						//adding PermissionList to role object.
						roleObj.put(CommonConstants.PERMISSIONS, permissionWithLevelList);
						
						//Updating the Existing role object with new role object  
						roleMap.put(rs.getInt(TableFieldConstants.ROLE_ID), roleObj);
					}else{
						/*
						 * Catches when the map does not contains the specified role id 
						 * and Creating, setting the data to properties of Role Object.
						 * */
						roleObj = new JSONObject();
						roleObj.put("roleId", rs.getInt(TableFieldConstants.ROLE_ID));
						roleObj.put("roleType", rs.getInt(TableFieldConstants.ROLE_IS_EAI));
						roleObj.put("roleName", rs.getString(TableFieldConstants.ROLE_NAME));
						roleObj.put("roleTypeName", rs.getInt(TableFieldConstants.ROLE_IS_EAI) == 0 ? "User" : "Admin");
						roleObj.put("superAdmin", rs.getInt(TableFieldConstants.ROLE_IS_SUPER));
						roleObj.put("customerSupport", rs.getInt(TableFieldConstants.ROLE_IS_CSO));
						roleObj.put("createActivityLog", rs.getInt("create_activity_log"));
						roleObj.put("isActive", rs.getInt("is_active"));
						
						//Creating the new object for permissions and adding the permissions.
						permissionWithLevelList=new JSONArray();
						/*
						 * Checking whether the isCSO flag is 0 and permission_name, permission_level_name
						 * is not null then the role have the permissions to permissionList object.
						 * else we are not adding any permissions to permissionList object. 
						 */
						if(rs.getInt(TableFieldConstants.ROLE_IS_CSO)==0 && 
								rs.getString(TableFieldConstants.PERMISSION_ID)!=null && 
								rs.getString(TableFieldConstants.PERMISSION_LEVEL_ID)!=null){
							permissions = new JSONObject();
							permissions.put("permission", rs.getInt(TableFieldConstants.PERMISSION_ID));
							permissions.put("permissionName", rs.getString("permission_name"));
							permissions.put("permissionCode", rs.getString("permission_code"));
							permissions.put("permissionLevel", rs.getInt(TableFieldConstants.PERMISSION_LEVEL_ID));
							permissions.put("permissionLevelName", rs.getString("permission_level_name"));
							permissionWithLevelList.add(permissions);
						}
						
						//Attaching the permissions list to role. 
						roleObj.put(CommonConstants.PERMISSIONS, permissionWithLevelList);
							
						//Adding the role object to hashMap
						roleMap.put(rs.getInt(TableFieldConstants.ROLE_ID), roleObj);
					}
				
				}
			});
			
			//Looping and adding the all roles data to JSONArray.
			for (Map.Entry<Integer, JSONObject> entry : roleMap.entrySet()) {
				roleArray.add(entry.getValue());
			}
			
			logger.debug("[DEBUG] Role JSONArray - "+roleArray);
			resultObj.put("roleList", roleArray);
			//Adding the load permission data to result object
			resultObj.put("permissionsData", loadPermissions());
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_ROLE_DETAILS_FAILED_ERROR, logger, e);
		}
		
		logger.info("[END] [listRoles] [Role DAO LAYER]");
		
		return resultObj;
	}
	
	@Override
	public int deleteRole(int roleId,int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [deleteRole] [DAO LAYER]");
		
		int deleteRoleFlag = 0;
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
			simpleJdbcCall.withProcedureName("sp_delete_role");
			
			//Adding all the input parameter values to a hashmap.
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_role_id", roleId);
			inputParams.put("in_user_id", userId);
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_delete_role");
			logger.debug("[DEBUG] input parameters - "+inputParams);
			
			//Adding all the input parameter map to simpleJdbcCall.execute method.
	        Map<String,Object> outParameters = simpleJdbcCall.execute(inputParams);
	        
	        logger.debug("[DUBUG] sp_delete_role out parameters - "+outParameters);
	        
	        //Getting the roleToUserCount.
	        deleteRoleFlag = (int) outParameters.get("out_flag");
	        
	        //if deleteRoleFlag is 0 means there is an exception occurred at database level.
	        if(deleteRoleFlag==0){
	        	//In procedure there is an exception occured.
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DELETE_ROLE_FAILED_ERROR, logger, new Exception((String) outParameters.get("out_error_msg")));
	        }
	        
		  } catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DELETE_ROLE_FAILED_ERROR, logger, e);
		}
		
		logger.info("[END] [checkRoleDelete] [DAO LAYER]");
		
		return deleteRoleFlag;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject loadPermissions() throws VEMAppException {
		
		logger.info("[BEGIN] [loadPermissions] [Role DAO LAYER]");
		
		/*
		 * Used to hold all the all the drop down values.
		 */
		JSONObject loadObject = new JSONObject();
		JSONArray permissionsArray = new JSONArray();
		JSONArray permissionLevelArray = new JSONArray();
		
		/*
		 * Temperary variables.
		 */
		JSONObject obj ;
		JSONObject levelObj ;
		Map.Entry<String, Object> entry;
		String key;
		List<HashMap<String, Object>> tempList;
		String permissionLevel="";
		String [] levelsArray = null;
		String [] eachLevelArray = null;
		
		/*
		 * Declaring the simpleJdbcCall and  simpleJdbcCallResult
		 */
		SimpleJdbcCall simpleJdbcCall;
		Map<String, Object> simpleJdbcCallResult;
		
		/*
		 * This iterator is used to loop the results. 
		 */
		Iterator<Entry<String, Object>> it;
		
		try {

			logger.debug("[DEBUG] Executing sp_load_role_permissions procedure.");
			/*
			 * Instantiating simpleJdbcCall, Appending the procedure
			 * And executing the procedure.
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_load_role_permissions");
			
			/*
			 * Executing the procedure
			 */
			simpleJdbcCallResult = simpleJdbcCall.execute();
			
			/*
			 * looping the resulsets.
			 */
			it = simpleJdbcCallResult.entrySet().iterator();
			    while (it.hasNext()) {
			        /*
			         * Getting the each result set as entry 
			         * and Getting the key from entry. 
			         */
			    	entry = (Map.Entry<String, Object>) it.next();
			        key = entry.getKey().toString();
			        
			        logger.debug("[DEBUG] Key - "+key);
			        
			        /*
			         * Forming the Role Permissions data. 
			         */
			        if(key.equals(RESULT_SET_1)){
			        	
			        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
				        for (int i = 0; i < tempList.size(); i++) {
				        	obj = new JSONObject();
				        	obj.put("permissionId", CommonUtility.isNullForInteger(tempList.get(i).get("permission_id")));
				        	obj.put("permissionName", CommonUtility.isNullForString(tempList.get(i).get("permission_name")));
				        	
				        	permissionLevelArray = new JSONArray();
				        	permissionLevel = (String) CommonUtility.isNullForString(tempList.get(i).get("permission_levels"));
				        	
				        	if(!permissionLevel.isEmpty() && permissionLevel.contains("###")){
				        		levelsArray = permissionLevel.split("###");
				        		if(levelsArray.length>1){
				        			for(int k = 0 ;k < levelsArray.length ; k++){
				        				if(!levelsArray[k].isEmpty() && levelsArray[k].contains("~~~")){
				        					eachLevelArray = levelsArray[k].split("~~~");
				        					if(eachLevelArray.length > 1){
				        						levelObj = new JSONObject();
				        						levelObj.put("permissionLevelId", CommonUtility.isNullForInteger(Integer.parseInt(eachLevelArray[0])));
				        						levelObj.put("permissionLevelName", CommonUtility.isNullForString(eachLevelArray[1]));
				    				        	permissionLevelArray.add(levelObj);
				        					}
				        				}
				        			}
				        		}
				        	}
				        	obj.put("permissionLevels", permissionLevelArray);
				        	permissionsArray.add(obj);
						}
				        logger.debug("[DEBUG] permissions---ARRAYLIST-- "+permissionsArray);
				        
			        }
			    }
			    
			    /*
			     *	Attaching Permissions
			     * 	to loadObject.
			     */
			    loadObject.put("permissions", permissionsArray);
			    
			    logger.debug("[DEBUG] loadObject---OBJECT-- "+loadObject);
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOAD_ROLE_PERMISSION_FAILED_ERROR, logger, e);
		}
		
		logger.info("[END] [loadPermissions] [Role DAO LAYER]");
		
		return loadObject;
	}

}
