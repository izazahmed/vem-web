/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.controller.group;

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

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.group.GroupRequest;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.group.GroupService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : GroupController 
 * 
 * GroupController: is used to handle all the Role related Requests
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
 * 16-09-2016		Raja		    addGroup() method has added.
 * 
 */

@RestController
@RequestMapping("/api/group")
public class GroupController {
	
	private static final Logger logger=Logger.getLogger(GroupController.class);
	@Autowired GroupService groupService;
	private GetUserResponse userDetails=null;
	
	/**
	 * validateSession method is for validating session
	 * @param session; HttpSession
	 * @return boolean
	 */

	private boolean validateSession(HttpSession session){
		// Getting the session details
		userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		boolean sessionFlag=false;
		try {
			// checking for if session is valid or not
			if(userDetails != null){
				sessionFlag=true;
			}
					
		} catch (Exception e) {
			logger.error("" ,e);
			sessionFlag=false;
		}
		return sessionFlag;

	}

	
	/**
	 * addGroup controller is used handle the request & response for creating an new group.
	 * @param groupRequest
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/addGroup", method=RequestMethod.POST)
	private ResponseEntity<Response> addGroup(@RequestBody GroupRequest groupRequest,HttpSession session){
		logger.info("[BEGIN] [addGroup] [Site Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		try{
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				groupRequest.setUpdateMode(0); // 0 means new
				
				if(groupRequest.getUserId()==0 && userDetails.getUserId() >0){
					groupRequest.setUserId(userDetails.getUserId());
				}
				
				response = groupService.addGroup(groupRequest);
				status = HttpStatus.OK;
				
			}
		}
		catch (Exception e) {
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			}
			logger.error("",e);
		}
		logger.info("[END] [addSite] [Site Controller Layer]");
		return new ResponseEntity<>(response, status);
	}

	/**
	 * updateGroup controller is used handle the request & response for updating group.
	 * @param groupRequest
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/updateGroup", method=RequestMethod.POST)
	private ResponseEntity<Response> updateGroup(@RequestBody GroupRequest groupRequest,HttpSession session){
		logger.info("[BEGIN] [updateGroup] [Site Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		try{
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				
				if(groupRequest.getUpdateMode() !=3 )
				{
					groupRequest.setUpdateMode(1); // 1 means update
				}
				groupRequest.setUserId(userDetails.getUserId());
				response = groupService.updateGroup(groupRequest);
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_UPDATE_GROUP);
			}
			logger.error("",e);
		}
		logger.info("[END] [updateGroup] [Site Controller Layer]");
		return new ResponseEntity<>(response, status);
	}

	/**
	 * deleteGroup controller is used handle the request & response for updating group.
	 * @param groupRequest
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/deleteGroup", method=RequestMethod.POST)
	private ResponseEntity<Response> deleteGroup(@RequestBody GroupRequest groupRequest,HttpSession session){
		logger.info("[BEGIN] [deleteGroup] [Site Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		try{
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				groupRequest.setUpdateMode(2); // 2 means delete
				groupRequest.setGroupStatusCode(7); // 7 for delete
				groupRequest.setUserId(userDetails.getUserId());
				response = groupService.updateGroup(groupRequest);
				response.setCode(ErrorCodes.INFO_DELETE_GROUP);
				status = HttpStatus.OK;
				
			}
		}
		catch (Exception e) {
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_DELETE_GROUP);
			}
			logger.error("",e);
		}
		logger.info("[END] [deleteGroup] [Group Controller Layer]");
		return new ResponseEntity<>(response, status);
	}

	

	
	/**
	 * listGroup controller is used handle the request & response for fetching the Groups for the customer
	 * Listing all the Groups.
	 *  
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/groupList", method=RequestMethod.POST)
	private ResponseEntity<Response> listGroup(@RequestBody GroupRequest groupRequest, HttpSession session){
		
		logger.info("[BEGIN] [listGroup] [Group Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			
		try{
			//validating session
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				groupRequest.setUserId(userDetails.getUserId());
				groupRequest.setIsSuperAdmin(userDetails.getIsSuper());
				groupRequest.setIsEAI(userDetails.getIsEai());
				response=groupService.listGroup(groupRequest);
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			}
			logger.error("",e);
		}
		logger.info("[END] [listGroup] [ Group Controller Layer]");
		return new ResponseEntity<>(response, status);
	}

	
	/**
	 * listGroup controller is used to handle the request & response for fetching the sites for the group associated to the customer
	 * Listing all the Sites for the group w.r.t customerId.
	 *  
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getSites", method=RequestMethod.POST)
	private ResponseEntity<Response> getSites(@RequestBody GroupRequest groupRequest,HttpSession session){

		logger.info("[BEGIN] [getSites] [Group Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		try {
			
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				groupRequest.setUserId(userDetails.getUserId());
				groupRequest.setIsSuperAdmin(userDetails.getIsSuper());
				response = groupService.getSites(groupRequest);
				status = HttpStatus.OK;
			}
			
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		logger.info("[END] [getSites] [Group Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	

	/**
	 * checkDuplicate controller is used to handle the request & response for fetching the sites for the group associated to the customer
	 * @param groupRequest object, which takes groupName
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/checkDuplicate", method=RequestMethod.POST)
	private ResponseEntity<Response> checkDuplicateController(@RequestBody GroupRequest groupRequest,HttpSession session){
		
		logger.info("[BEGIN] [checkDuplicate] [Group Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		try{
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				groupRequest.setUserId(userDetails.getUserId());
				response = groupService.checkDuplicate(groupRequest);
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			}
			response.setData("error");
			logger.error("",e);
		}
		logger.info("[END] [checkDuplicate] [Group Controller Layer]");
		return new ResponseEntity<>(response, status);
		}
		
	

	/**
	 * getGroupInfo controller is used to handle the request & response for fetching group related info
	 * @param groupRequest object, which takes groupName
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getGroupInfo", method=RequestMethod.POST)
	private ResponseEntity<Response> getGroupInfoController(@RequestBody GroupRequest groupRequest,HttpSession session){
		
		logger.info("[BEGIN] [getGroupInfo] [Group Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		try{
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				groupRequest.setUserId(userDetails.getUserId());
				groupRequest.setIsSuperAdmin(userDetails.getIsSuper());
				groupRequest.setIsEAI(userDetails.getIsEai());
				response = groupService.getGroupInfoService(groupRequest,userDetails);
				
				if(response.getStatus().equalsIgnoreCase("failure")){
					status = HttpStatus.INTERNAL_SERVER_ERROR;
				}else{
					status = HttpStatus.OK;
				}
				
				
			}
		}
		catch (Exception e) {
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			}
			response.setData("error");
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		logger.info("[END] [getGroupInfo] [Group Controller Layer]");
		return new ResponseEntity<>(response, status);
		}


	/**
	 * getGroupSitesController controller is used to handle the request & response for fetching group related info and sites list
	 * @param groupRequest object, which takes groupName
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/getGroupSites", method=RequestMethod.POST)
	private ResponseEntity<Response> getGroupSitesController(@RequestBody GroupRequest groupRequest,HttpSession session){
		
		logger.info("[BEGIN] [getGroupSites] [Group Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		try{
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				groupRequest.setUserId(userDetails.getUserId());
				groupRequest.setIsSuperAdmin(userDetails.getIsSuper());
				response = groupService.getGroupSitesService(groupRequest);
				status = HttpStatus.OK;
			}
		}
		catch (Exception e) {
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			}
			response.setData("error");
			logger.error("",e);
		}
		logger.info("[END] [getGroupSites] [Group Controller Layer]");
		return new ResponseEntity<>(response, status);
		}
	
	/**
	 * getCustomerGroups controller is used to handle the request & response for fetching the sites for the group associated to the customer
	 * Listing all the Groups  for the group w.r.t multiple customerIds.
	 * @return ResponseEntity<Response>
	 */
	
	@RequestMapping(value="/getCustomerGroups", method=RequestMethod.POST)
	private ResponseEntity<Response> getCustomerGroups(@RequestBody GroupRequest groupRequest,HttpSession session){

		logger.info("[BEGIN] [getCustomerGroups] [Group Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		try {
			
			if(!validateSession(session)){
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			}else{
				groupRequest.setUserId(userDetails.getUserId());
				groupRequest.setIsSuperAdmin(userDetails.getIsSuper());
				groupRequest.setIsEAI(userDetails.getIsEai());
				response = groupService.getCustomerGroups(groupRequest);
				status = HttpStatus.OK;
			}
			
		}catch (Exception e) {
			
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.LIST_CUSTOMER_FOR_GROUP_ERROR);
			}
			
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		logger.info("[END] [getCustomerGroups] [Group Controller Layer]");
		return new ResponseEntity<>(response, status);
	}


	}
