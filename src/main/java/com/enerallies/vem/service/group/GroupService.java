/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.service.group;

import org.springframework.stereotype.Service;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.group.GroupRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : GroupService 
 * 
 * GroupService service is used to serve the all group related operations.
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

@Service
public interface GroupService {
	
	/**
	 * addGroup service layer, used to save the Group.
	 * @param groupRequest object of GroupRequest bean, accepts customer id and userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response addGroup(GroupRequest groupRequest) throws VEMAppException;
	

	/**
	 * updateGroup service layer, used to update the Group.
	 * @param groupRequest object of GroupRequest bean, accepts customer id and userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response updateGroup(GroupRequest groupRequest) throws VEMAppException;
	
	
	/**
	 * listGroup service layer, used to get List of Groups.
	 * @param groupRequest object of GroupRequest bean, accepts customer id
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response listGroup(GroupRequest groupRequest) throws VEMAppException;
	
	/**
	 * getSites service is used to list all existing Groups for the associated customer.
	 * @param customerId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getSites(GroupRequest groupRequest) throws VEMAppException;
	
	/**
	 * activateOrDeActivateGroup service is used to activate / inactivate  Group for the associated customer.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response activateOrDeActivateGroup(GroupRequest groupRequest) throws VEMAppException;
	
	/**
	 * deleteGroup service is used to delete the Group.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response deleteGroup(GroupRequest groupRequest) throws VEMAppException;
	
	/**
	 * checkDuplicate service is used check where same group name exists for the customer.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response checkDuplicate(GroupRequest groupRequest) throws VEMAppException;
	
	/**
	 * getGroupInfo Service is used to get group information.
	 * @param groupRequest
	 * @param userDetails
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getGroupInfoService(GroupRequest groupRequest, GetUserResponse userDetails) throws VEMAppException;
	
	/**
	 * getGroupInfo Service is used to get group information along with sites.
	 * @param groupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getGroupSitesService(GroupRequest groupRequest) throws VEMAppException;

	/**
	 * getCustomerGroups Service is used to get group information along with groups based on customers list.
	 * @param groupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getCustomerGroups(GroupRequest groupRequest) throws VEMAppException;

}
