/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.serviceimpl.group;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.beans.group.GroupRequest;
import com.enerallies.vem.dao.group.GroupDAO;
import com.enerallies.vem.dao.role.RoleDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.group.GroupService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.validators.role.RolesValidator;

/**
 * File Name : GroupServiceImpl 
 * 
 * GroupServiceImpl: Its an implementation class for GroupService service interface.
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
 * 16-09-2016		Raja		    File Created.
 
 *
 */

@Service("groupService")
@Transactional
public class GroupServiceImpl implements GroupService{
	
	private static final Logger logger = Logger.getLogger(GroupServiceImpl.class);
	@Autowired GroupDAO groupDAO;
	
	/**
	 * addGroup service layer, used to get List of Groups.
	 * @param groupRequest object of GroupRequest bean, accepts customer id and userId
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response addGroup( GroupRequest groupRequest )  throws VEMAppException {
		
		logger.info("[BEGIN] [addGroup] [GroupService SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = groupDAO.addGroup(groupRequest);
			
		} catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		
		logger.info("[BEGIN] [addRole] [GroupService SERVICE LAYER]");
		return response;
	}
	
	
	
	/**
	 * listGroup service impl layer,Interacts with DAO layer to get List of Groups.
	 * @param groupRequest object of GroupRequest bean, accepts customer id
	 * @return Response
	 * @throws VEMAppException
	 */
	
	@Override
	public Response listGroup(GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [listGroup] [GroupService SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = groupDAO.listGroup(groupRequest);
		}catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException("Internal occured at service layer");
		}
		
		logger.info("[BEGIN] [listGroup] [GroupService SERVICE LAYER]");
		
		return response;
	}
	
	/**
	 * getSites service is used to list all existing Groups for the associated customer.
	 * @param customerId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getSites(GroupRequest groupRequest) throws VEMAppException  {
		
		logger.info("[BEGIN] [getSites] [GroupService SERVICE LAYER]");
		Response sitesResponse = null;
		
		try {
			sitesResponse = groupDAO.getSites(groupRequest);
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			logger.error("",e);
			sitesResponse.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			sitesResponse.setCode(ErrorCodes.LIST_SITES_FOR_GROUP_FAILED);
			sitesResponse.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, "ErrorCodes.LIST_GROUP_FAILED", logger, e);
		}
		
		logger.info("[BEGIN] [getSites] [GroupService SERVICE LAYER]");
		return sitesResponse;
	}
	
	
	/**
	 * updateGroup service is used to update the Group Details.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response updateGroup (GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [updateGroup] [GroupService SERVICE LAYER]");
		Response response = new Response();
		try {
			response = groupDAO.updateGroup(groupRequest);
			
		} catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		logger.info("[BEGIN] [updateGroup] [GroupService SERVICE LAYER]");
		return response;
	}
	
	/**
	 * activateOrDeActivateGroup service is used to activate / inactivate  Group for the associated customer.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response activateOrDeActivateGroup(GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [activateOrDeActivateGroup] [GroupService SERVICE LAYER]");
		Response response = new Response();
		try {
			response = groupDAO.activateOrDeActivateGroup(groupRequest);
			
		} catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		logger.info("[BEGIN] [activateOrDeActivateGroup] [GroupService SERVICE LAYER]");
		return response;
	}
	
	/**
	 * deleteGroup service is used to delete the Group.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response deleteGroup(GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [deleteGroup] [GroupService SERVICE LAYER]");
		Response response = new Response();
		try {
			response = groupDAO.deleteGroup(groupRequest);
			
		} catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		logger.info("[BEGIN] [deleteGroup] [GroupService SERVICE LAYER]");
		return response;
	}
	
	/**
	 * checkDuplicate service is used check where same group name exists for the customer.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response checkDuplicate(GroupRequest groupRequest) throws VEMAppException{
		
		logger.info("[BEGIN] [checkDuplicate] [GroupService SERVICE LAYER]");
		Response response = new Response();
		try {
			response = groupDAO.checkDuplicate(groupRequest);
			
		} catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		logger.info("[BEGIN] [checkDuplicate] [GroupService SERVICE LAYER]");
		return response;
	}
	
	/**
	 * getGroupInfo Service layer is used to get group.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response getGroupInfoService(GroupRequest groupRequest, GetUserResponse userDetails) throws VEMAppException{
		
		logger.info("[BEGIN] [getGroupInfo] [GroupService SERVICE LAYER]");
		Response response = new Response();
		try {
			response = groupDAO.getGroupInfo(groupRequest,userDetails);
			
		} catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		logger.info("[BEGIN] [getGroupInfo] [GroupService SERVICE LAYER]");
		return response;
	}

	
	/**
	 * getGroupGroupsService Service layer is used to get group info and Groups list.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response getGroupSitesService(GroupRequest groupRequest) throws VEMAppException{
		
		logger.info("[BEGIN] [getGroupSitesService] [GroupService SERVICE LAYER]");
		Response response = new Response();
		try {
			response = groupDAO.getGroupSitesDao(groupRequest);
			
		} catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		logger.info("[BEGIN] [getGroupSitesService] [GroupService SERVICE LAYER]");
		return response;
	}
	/**
	 ** getCustomerGroups Service is used to get group information along with groups based on customers list.
	 * @param groupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response getCustomerGroups(GroupRequest groupRequest) throws VEMAppException{
		
		logger.info("[BEGIN] [getCustomerGroups] [GroupService SERVICE LAYER]");
		Response response = new Response();
		try {
			response = groupDAO.getCustomerGroupsDao(groupRequest);
			
		} catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		logger.info("[BEGIN] [getCustomerGroups] [GroupService SERVICE LAYER]");
		return response;
	}
	
}
