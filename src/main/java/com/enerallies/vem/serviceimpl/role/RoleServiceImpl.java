/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.serviceimpl.role;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.enerallies.vem.beans.role.AddRoleRequest;
import com.enerallies.vem.beans.role.GetRoleRequest;
import com.enerallies.vem.beans.role.UpdatedRoleRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.dao.role.RoleDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.role.RoleService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.validators.role.RolesValidator;

/**
 * File Name : RoleServiceImpl 
 * 
 * RoleServiceImpl: Its an implementation class for RoleService service interface.
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
 * 13-10-2016		Goush Basha			Added the method loadPermissions.
 *
 */

@Service("roleService")
@Transactional
public class RoleServiceImpl implements RoleService{
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(RoleServiceImpl.class);
	
	/**instantiating the role dao for accessing the dao layer.*/
	@Autowired RoleDAO roleDAO;
	
	@Override
	public Response addRole(AddRoleRequest roleRequest,int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [addRole] [RoleService SERVICE LAYER]");
		
		Response response = new Response();
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(roleRequest);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}/*else if(!RolesValidator.validatePermissions(roleRequest.getPermissions(),roleRequest.getIsCSO())){
				//Catches when customized validation of permission list failed. 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_ROLE_PERMISSIONS_EMPTY);
				response.setData(CommonConstants.ERROR_OCCURRED+":Role Permission List should not be empty.");
				return response;
			}*/else if(!RolesValidator.validateRoleFlags(roleRequest.getRoleType())){
				//Catches when customized validation of IsEai filed failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_ROLE_ISEAI_INVALID);
				response.setData(CommonConstants.ERROR_OCCURRED+":Role Type should be valid.");
				return response;
			}else{
				
				//Catches when all the server or bean level validations are true.
				int status = roleDAO.addRole(roleRequest,userId);
				
				/* if status is 1 or greater means the add role request is
				 *  success
				 *  else fail.
				 */
				if(status >= 1){
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.ADD_ROLE_SUCCESS);
					response.setData(status);
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ADD_ROLE_FAILD_ERROR);
					response.setData(CommonConstants.ERROR_OCCURRED+":Role has not created at DB Side.");
				}
				
			}
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ADD_ROLE_FAILD_ERROR);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_ROLE_FAILD_ERROR, logger, e);
		}
		
		logger.info("[BEGIN] [addRole] [RoleService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response getRoleDetails(int roleId) throws VEMAppException {

		logger.info("[BEGIN] [getRoleDetails] [RoleService SERVICE LAYER]");
		
		Response response = new Response();
		JSONObject role;
		
		try {
			
			//Catches when all the server or bean level validations are true.
			role = roleDAO.getRoleDetails(roleId);
			
			/* if role is not null means the get role request is
			 *  success
			 *  else fail.
			 */
			if(role!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.GET_ROLE_DETAILS_SUCCESS);
				response.setData(role);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.GET_ROLE_DETAILS_FAILED_ERROR);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GET_ROLE_DETAILS_FAILED_ERROR);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_ROLE_DETAILS_FAILED_ERROR, logger, e);
		}
		
		logger.info("[BEGIN] [getRoleDetails] [RoleService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response updateRole(UpdatedRoleRequest updatedRoleRequest,int userId) throws VEMAppException {

		logger.info("[BEGIN] [updateRole] [RoleService SERVICE LAYER]");
		
		Response response = new Response();
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(updatedRoleRequest);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}/*else if(!RolesValidator.validatePermissions(updatedRoleRequest.getPermissions(),updatedRoleRequest.getCustomerSupport())){
				//Catches when customized validation of permission list failed. 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_ROLE_PERMISSIONS_EMPTY);
				response.setData(CommonConstants.ERROR_OCCURRED+":Role Permission List should not be empty.");
				return response;
			}*/else if(!RolesValidator.validateRoleFlags(updatedRoleRequest.getRoleType())){
				//Catches when customized validation of IsEai filed failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.WARN_ROLE_ISEAI_INVALID);
				response.setData(CommonConstants.ERROR_OCCURRED+":Role RoleType should be valid.");
				return response;
			}else{
				
				//Catches when all the server or bean level validations are true.
				int status = roleDAO.updateRole(updatedRoleRequest,userId);
				
				/* if status is 1 or greater means the add role request is
				 *  success
				 *  else fail.
				 */
				if(status >= 1){
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.UPDATE_ROLE_DETAILS_SUCCESS);
					response.setData(status);
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.UPDATE_ROLE_DETAILS_FAILED_ERROR);
					response.setData(CommonConstants.ERROR_OCCURRED+":Role has not updated at DB Side.");
				}
				
			}
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.UPDATE_ROLE_DETAILS_FAILED_ERROR);
			response.setData(CommonConstants.ERROR_OCCURRED+" while updating the Role");
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.UPDATE_ROLE_DETAILS_FAILED_ERROR, logger, e);
		}

		logger.info("[BEGIN] [updateRole] [RoleService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response listRoles() throws VEMAppException {
		
		logger.info("[BEGIN] [listRoles] [RoleService SERVICE LAYER]");
		
		Response response = new Response();
		JSONObject roles;
		
		try {

			//Calling the ListRiles method from dao.
			roles = roleDAO.listRoles();
			
			/* if roles is not null means the get roles list request is
			 *  success
			 *  else fail.
			 */
			if(roles!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.LIST_ROLE_DETAILS_SUCCESS);
				response.setData(roles);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.LIST_ROLE_DETAILS_FAILED_ERROR);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found");
			}
			
		} catch (Exception e) {
			//Catches when customized validation of IsEai filed failed.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.LIST_ROLE_DETAILS_FAILED_ERROR);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_ROLE_DETAILS_FAILED_ERROR, logger, e);
		}
		
		logger.info("[BEGIN] [listRoles] [RoleService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response deleteRole(int roleId,int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [deleteRole] [RoleService SERVICE LAYER]");
		
		Response response = new Response();
		int deleteFlag=0;
		
		try {

			//Catches when all the server or bean level validations are true.
			deleteFlag = roleDAO.deleteRole(roleId,userId);
			
			/* if deleteFlag is 1 means the role has removed successfully
			 * else if deleteFlag is 2 means there are some users assigned to the role
			 * so we can not remove the role
			 * else there is a exception occurred in stored procedure and remove operation failed.
			 */
			if(deleteFlag==1){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.DELETE_ROLE_SUCCESS);
				response.setData(deleteFlag);
			}else if(deleteFlag==2){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.CHECK_USER_TO_ROLE_SUCCESS);
				response.setData(deleteFlag);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.DELETE_ROLE_FAILED_ERROR);
				response.setData(deleteFlag);
			}
		
		} catch (Exception e) {
			//Catches when customized validation of IsEai filed failed.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.DELETE_ROLE_FAILED_ERROR);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DELETE_ROLE_FAILED_ERROR, logger, e);
		}
		
		logger.info("[BEGIN] [deleteRole] [RoleService SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response loadPermissions() throws VEMAppException {
		
		logger.info("[BEGIN] [loadPermissions] [RoleService SERVICE LAYER]");
		
		Response response = new Response();
		JSONObject permissions;
		
		try {

			//Calling the loadPermissions method from dao.
			permissions = roleDAO.loadPermissions();
			
			/* 
			 * if permissions is not null means the load permissions request is
			 *  success
			 *  else fail.
			 */
			if(permissions!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.LOAD_ROLE_PERMISSION_SUCCESS);
				response.setData(permissions);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.LOAD_ROLE_PERMISSION_FAILED_ERROR);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found");
			}
			
		} catch (Exception e) {
			//Catches when Exception occurred.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.LOAD_ROLE_PERMISSION_FAILED_ERROR);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOAD_ROLE_PERMISSION_FAILED_ERROR, logger, e);
		}
		
		logger.info("[BEGIN] [loadPermissions] [RoleService SERVICE LAYER]");
		
		return response;
	}
}
