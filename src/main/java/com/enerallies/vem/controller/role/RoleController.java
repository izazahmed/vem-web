/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.controller.role;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.enerallies.vem.beans.role.AddRoleRequest;
import com.enerallies.vem.beans.role.UpdatedRoleRequest;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.service.role.RoleService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : RoleController 
 * 
 * RoleController: is used to handle all the Role related Requests
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
 * 30-09-2016       Goush Basha	        Implemented the session management.
 * 13-10-2016		Goush Basha			Added the method loadPermissions.
 * 
 */

@RestController
@RequestMapping("/api/role")
public class RoleController {
	
	/* Get the logger object*/
	private static final Logger logger=Logger.getLogger(RoleController.class);
	
	/**Instantiated the role service for accessing the service layer.*/
	@Autowired RoleService roleService;
	
	/** Constant for user details object **/
	private static final String USER_DETAILS_OBJECT="eaiUserDetails";
	
	/**
	 * addRole controller is used handle the request & response for creating an new role.
	 *  
	 * @param roleRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/add",method=RequestMethod.POST)
	private ResponseEntity<Response> addRole(@RequestBody AddRoleRequest roleRequest,HttpSession session){
		
		logger.info("[BEGIN] [addRole] [role Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid.
				 */
				if(roleRequest!=null){
					response = roleService.addRole(roleRequest,userDetails.getUserId());
					status = HttpStatus.OK;
				}else{
					throw new NullPointerException();
				}
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch(NullPointerException ne){
			status=HttpStatus.BAD_REQUEST;
			logger.error("",ne);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [addRole] [role Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * getRoleDetails controller is used to handle the request & response 
	 * to serve the purpose of retrieving the role data.
	 * 
	 * @param roleId
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/get/{roleId}",method=RequestMethod.GET)
	private ResponseEntity<Response> getRoleDetails(@PathVariable("roleId") int roleId,
			HttpSession session){

		logger.info("[BEGIN] [getRoleDetails] [role Controller Layer]");

		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid.
				 */
				if(roleId!=0){
					response = roleService.getRoleDetails(roleId);
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.WARN_ROLE_ID_NOT_NULL);
					response.setData(CommonConstants.ERROR_OCCURRED+":Role id should not be empty!");
				}
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}

		}catch(NullPointerException ne){
			status=HttpStatus.BAD_REQUEST;
			logger.error("",ne);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [getRoleDetails] [role Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * updateRole controller is used to handle the request & response 
	 * to update an existing role.
	 * 
	 * @param updatedRoleRequest
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	private ResponseEntity<Response> updateRole(@RequestBody UpdatedRoleRequest updatedRoleRequest, HttpSession session){
		
		logger.info("[BEGIN] [updateRole] [role Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid.
				 */
				if(updatedRoleRequest!=null){
					response = roleService.updateRole(updatedRoleRequest,userDetails.getUserId());
					status = HttpStatus.OK;
				}else{
					throw new NullPointerException();
				}
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}

		}catch(NullPointerException ne){
			status=HttpStatus.BAD_REQUEST;
			logger.error("",ne);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [updateRole] [role Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * listRoles controller handles the Request & Response to 
	 * get the list of roles.
	 * 
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	private ResponseEntity<Response> listRoles(HttpSession session){
		
		logger.info("[BEGIN] [listRoles] [role Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				
				/*
				 * Catches when the session is valid
				 * And if the customerId is '0' means customerId is invalid
				 * if the customerId is not '0' means customerId is valid.
				 */
				response = roleService.listRoles();
				status = HttpStatus.OK;
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		} catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [listRoles] [role Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * deleteRole controller handles the request & response to 
	 * delete specified role details.
	 * 
	 * @param roleId
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/delete/{roleId}",method=RequestMethod.GET)
	private ResponseEntity<Response> deleteRole(@PathVariable("roleId") int roleId, HttpSession session){
		
		logger.info("[BEGIN] [deleteRole] [role Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid
				 * And if the customerId is '0' means customerId is invalid
				 * if the customerId is not '0' means customerId is valid.
				 */
				if(roleId!=0){
					response = roleService.deleteRole(roleId,userDetails.getUserId());
					status = HttpStatus.OK;
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.WARN_ROLE_ID_NOT_NULL);
					response.setData(CommonConstants.ERROR_OCCURRED+":Role id should not be empty!");
				}
			}else{

				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				 
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			
			}
			
		}catch(NullPointerException ne){
			status=HttpStatus.BAD_REQUEST;
			logger.error("",ne);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.debug("[END] [deleteRole] [role Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * loadPermissions controller is used handle the request & response for loading the permissions
	 * from database.
	 *  
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/loadPermissions",method=RequestMethod.GET)
	private ResponseEntity<Response> loadPermissions(HttpSession session){
		
		logger.info("[BEGIN] [loadPermissions] [role Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid.
				 */
				 
				response = roleService.loadPermissions();
				status = HttpStatus.OK;
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch(NullPointerException ne){
			status=HttpStatus.BAD_REQUEST;
			logger.error("",ne);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [loadPermissions] [role Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}

}
