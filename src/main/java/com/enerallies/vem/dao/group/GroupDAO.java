/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.dao.group;

import javax.sql.DataSource;

import org.json.simple.JSONArray;
import org.springframework.stereotype.Repository;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.group.GroupRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : GroupDAO 
 * GroupDAO dao is used to serve all the database level operations related to group.
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
@Repository
public interface GroupDAO {
	
	 /**
	  * This is the method to be used to initialize database resources i.e. connection.
	  * 
	  * @param dataSource
	  */
	 public void setDataSource(DataSource dataSource);
	 
	 /**
	 * addGroup DAO layer, used to save the Group.
	 * @param groupRequest object of GroupRequest bean, accepts customer id and userId
	 * @return Response
	 * @throws VEMAppException
	 */
	 public Response addGroup(GroupRequest groupRequest) throws VEMAppException;
	 
	 
	 /**
	 * listGroup dao layer,Interacts with Database to get List of Groups.
	 * @param groupRequest object of GroupRequest bean, accepts customer id
	 * @return Response
	 * @throws VEMAppException
	 */
	 public Response listGroup (GroupRequest groupRequest) throws VEMAppException;
	 
	 /**
	 * getSites dao layer,Interacts with Database to get List of sites w.r.t group.
	 * @param groupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	 public Response getSites (GroupRequest groupReqeust) throws VEMAppException;
	 
	 /**
	 * updateGroup in DAO is used to modify the Group.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
 	public Response updateGroup (GroupRequest groupRequest) throws VEMAppException;
 	
 	/**
	 * activateOrDeActivateGroup DAO is used to activate / inactivate  Group for the associated customer.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response activateOrDeActivateGroup(GroupRequest groupRequest) throws VEMAppException;
	
	/**
	 * deleteGroup DAO is used to delete the Group.
	 * @param groupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response deleteGroup(GroupRequest groupRequest) throws VEMAppException;

	/**
	 * checkDuplicate service is used check where same group name exists for the customer.
	 * @param groupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response checkDuplicate (GroupRequest groupRequest) throws VEMAppException;
	
	/**
	 * getGroupInfo service is a particular group info.
	 * @param groupRequest
	 * @param userDetails
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getGroupInfo (GroupRequest groupRequest, GetUserResponse userDetails) throws VEMAppException;
	
	/**
	 * getGroupSitesDao  is a particular group info and sites list.
	 * @param groupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getGroupSitesDao (GroupRequest groupRequest) throws VEMAppException;
	
	/**
	 * getCustomerGroupsDao  is a particular group info and sites list.
	 * @param groupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getCustomerGroupsDao (GroupRequest groupRequest) throws VEMAppException;
	
}
