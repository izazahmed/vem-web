/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.daoimpl.group;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.group.GroupRequest;
import com.enerallies.vem.beans.iot.GroupInfo;
import com.enerallies.vem.dao.group.GroupDAO;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : GroupDaoImpl 
 * GroupDaoImpl: Its an implementation class for GroupDAO interface.
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
 * 16-09-2016		Raja		    addGroup() method has added.
 * 20-09-2016		Raja		    listGroup() method has added.
 *
 */

@Component
public class GroupDaoImpl implements GroupDAO{
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(GroupDaoImpl.class);
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;	
	private static final String FETCH_GROUPS_LIST="call sp_list_group(?,?,?,?)"; 
	private static final String INSERT_UPDATE_GROUP_PROC ="sp_insert_or_update_grp";
	private static final String CHECK_DUP_GRP ="call sp_chk_duplicate_grp(?,?)";
	private static final String GET_GROUP_INFO ="call sp_group_info(?,?,?,?,?)";
	private static final String GET_GROUP_SITES_INFO="sp_group_sites_info";
	private static final String GET_CUSTOMER_GROUP_INFO="sp_fetch_groups_for_customers";
	private static final String GET_GROUP_SITES_LIST="call sp_fetch_site_for_grp (?,?,?,?)";
	
	/*Constant variable declaration*/
	private static final String SITE_GROUPS = "siteGroups";
	private static final String RESULT_SET_1 = "#result-set-1";
	private static final String RESULT_SET_2 = "#result-set-2";
	private static final String RESULT_SET_3 = "#result-set-3";
	private static final String RESULT_SET_4 = "#result-set-4";
	
	/** Auto wiring instance of IoTDao  */
	@Autowired
	private IoTDao ioTDao;
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	
	 /**
	 * addGroup DAO layer, used to save the Group.
	 * @param groupRequest object of GroupRequest bean, accepts customer id and userId
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response addGroup(GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [addGroup] [Group DAO LAYER]");
		
		int statusFlag = 0;
		String errorMsg = "";
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		List<String> newSitesList = null;
		StringBuilder newSitesListStr = new StringBuilder();
		Response response = new Response();
		
		try {
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(INSERT_UPDATE_GROUP_PROC);
		    
		    ArrayList<HashMap<String, String>> list=groupRequest.getSelectedLocations();
		    Iterator<HashMap<String, String>> itr=list.iterator();
		    
		    while(itr.hasNext()){
		    	HashMap<String, String> temp=new HashMap<String, String>();
		    	temp=itr.next();
		    	newSitesListStr.append(temp.get("siteId")+",");
		    }
		    
		    if(list.size()>0){
		    	newSitesListStr.replace(newSitesListStr.length()-1, newSitesListStr.length(), "");
		    }
		    
		    // 1 means for active, by default active for newly created user
		    groupRequest.setGroupStatusCode(1);
		    
		    Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_group_id", 0); // 0 as this is add method
			inputParams.put("in_group_name", CommonUtility.isNull(groupRequest.getGroupName()));
			inputParams.put("in_site_ids",newSitesListStr);
			inputParams.put("in_customer_id", groupRequest.getCustomerId());
			inputParams.put("in_user_id", groupRequest.getUserId());//add Group request.
			inputParams.put("in_mode", groupRequest.getUpdateMode());
			inputParams.put("in_is_active", groupRequest.getGroupStatusCode());
			
			logger.debug("[DEBUG] Executing the stored  procedure - "+INSERT_UPDATE_GROUP_PROC);
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - "+inputParams);
	        outParameters = simpleJdbcCall.execute(inputParams);
	        errorMsg = (String) outParameters.get("out_error_msg");
	        if(errorMsg!=null){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get("out_flag");
	        }
	        logger.info("status flag from grp dao impl " + statusFlag);
	        response.setData(statusFlag);
	        	if(statusFlag == -1){
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_SAVE_GROUP);
				}else if(statusFlag == 2){
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_GROUP_CHECK);
				}else{
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_SAVE_GROUP);
					response.setData(outParameters.get("lastInsertedId"));
				}
	        
		}catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_SAVE_GROUP);
			throw new VEMAppException("Internal Error occured at DAO layer");
		}
		logger.info("[END] [addGroup] [Group DAO LAYER]");
		return response;
	}
	
	
	/**
	 * updateGroup DAO layer, used to update the Group details.
	 * @param groupRequest object of GroupRequest bean, accepts customer id and userId
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response updateGroup(GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [updateGroup] [Group DAO LAYER]");
		
		int statusFlag = 0;
		String errorMsg = "";
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		List<String> newSitesList = null;
		StringBuilder newSitesListStr = new StringBuilder();
		Response response = new Response();
		
		try {
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(INSERT_UPDATE_GROUP_PROC);
		    
		    ArrayList<HashMap<String, String>> list=groupRequest.getSelectedLocations();
		    Iterator<HashMap<String, String>> itr=list.iterator();
		    
		    while(itr.hasNext()){
		    	HashMap<String, String> temp=new HashMap<String, String>();
		    	temp=itr.next();
		    	newSitesListStr.append(temp.get("siteId")+",");
		    }
		    
		    if(list.size()>0){
		    	newSitesListStr.replace(newSitesListStr.length()-1, newSitesListStr.length(), "");
		    }
		    
		    Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_group_id", groupRequest.getGroupId());
			inputParams.put("in_group_name", CommonUtility.isNull(groupRequest.getGroupName()));
			inputParams.put("in_site_ids",newSitesListStr);
			inputParams.put("in_customer_id", groupRequest.getCustomerId());
			inputParams.put("in_user_id", groupRequest.getUserId());//add Group request.
			inputParams.put("in_mode", groupRequest.getUpdateMode());
			inputParams.put("in_is_active", groupRequest.getGroupStatusCode());
			
			
			logger.debug("[DEBUG] Executing the stored  procedure - "+INSERT_UPDATE_GROUP_PROC);
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - "+inputParams);
	        outParameters = simpleJdbcCall.execute(inputParams);
	        errorMsg = (String) outParameters.get("out_error_msg");
	        if(errorMsg!=null){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get("out_flag");
	        }
	        logger.info("status flag from grp dao impl " + statusFlag);
	        response.setData(statusFlag);
	        	if(statusFlag == -1){
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_SAVE_GROUP);
				}else if(statusFlag == 2){
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_GROUP_CHECK);
				}else{
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_UPDATE_GROUP);
				}
	        
		}catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_SAVE_GROUP);
			throw new VEMAppException("Internal Error occured at DAO layer");
		}
		logger.info("[END] [addGroup] [Group DAO LAYER]");
		return response;
	}
	
	/**
	 * listGroup dao layer,Interacts with Database to get List of Groups.
	 * @param groupRequest object of GroupRequest bean, accepts customer id
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response listGroup (GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [listGroup] [Group DAO LAYER]");
		Response response =new Response();
		
		try {
			//Executing the procedure.
			Object inputParams[]=new Object[]{
					groupRequest.getModuleName(),
					groupRequest.getModuleId(),
					groupRequest.getIsSuperAdmin(),
					groupRequest.getUserId()
					};
			
			logger.debug("[DEBUG] Executing "+FETCH_GROUPS_LIST+" with params "
						+groupRequest.getModuleName()+" ,"
						+groupRequest.getModuleId()+", "
						+groupRequest.getIsSuperAdmin()+", "
						+groupRequest.getUserId()
					);
			
			
			response= jdbcTemplate.query(FETCH_GROUPS_LIST, inputParams, new ResultSetExtractor<Response>() {
				@Override
				public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
					JSONArray resultAry=new JSONArray();
					Response response=new Response();
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					while(rs.next()){
						JSONObject tempData=new JSONObject();
						JSONArray siteInfoAry=new JSONArray();
						JSONArray scheduleInfoAry=new JSONArray();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
								
							if(rsmd.getColumnLabel(i)!=null && rs.getString(rsmd.getColumnLabel(i)) !=null && !rs.getString(rsmd.getColumnLabel(i)).isEmpty() && "siteInfo".equalsIgnoreCase(rsmd.getColumnLabel(i))){
								
								String outerArry[]=rs.getString(rsmd.getColumnLabel(i)).split("---");
								String innerArry[];
								for(int arryCount=0;arryCount<outerArry.length;arryCount++){
									
									try{
										innerArry=outerArry[arryCount].split("~:~");
									
									if(innerArry.length>0){
										JSONObject siteInfoData=new JSONObject();
										siteInfoData.put("siteKey", innerArry[0]);
										siteInfoData.put("siteValue", innerArry[1]);
										siteInfoAry.add(siteInfoData);
									}else{
										JSONObject siteInfoData=new JSONObject();
										siteInfoData.put("siteKey", "");
										siteInfoData.put("siteValue", "");
										siteInfoAry.add(siteInfoData);
									}
									}catch(ArrayIndexOutOfBoundsException ae){
										logger.error(""+ae);
									}
								}
							}
							tempData.put("siteInfo", siteInfoAry);
							
							
							if(rsmd.getColumnLabel(i)!=null && rs.getString(rsmd.getColumnLabel(i)) !=null && !rs.getString(rsmd.getColumnLabel(i)).isEmpty() && "scheduleInfo".equalsIgnoreCase(rsmd.getColumnLabel(i))){
								
								String outerArry[]=rs.getString(rsmd.getColumnLabel(i)).split("---");
								String innerArry[];
								for(int arryCount=0;arryCount<outerArry.length;arryCount++){
									
									try{
										innerArry=outerArry[arryCount].split("~:~");
									
									if(innerArry.length>0){
										
										/*
										JSONObject scheduleInfoData=new JSONObject();
										scheduleInfoData.put("scheduleId", Integer.parseInt(innerArry[0]));
										scheduleInfoData.put("scheduleValue", innerArry[1]);
										scheduleInfoAry.add(scheduleInfoData);
										*/
										tempData.put("scheduleId", Integer.parseInt(innerArry[0]));
										tempData.put("scheduleName", innerArry[1]);
									}else{
										
										/*JSONObject scheduleInfoData=new JSONObject();
										scheduleInfoData.put("scheduleId", "");
										scheduleInfoData.put("scheduleValue", "");
										scheduleInfoAry.add(scheduleInfoData);
										*/
										tempData.put("scheduleId", "");
										tempData.put("scheduleName", "");
									}
									}catch(ArrayIndexOutOfBoundsException ae){
										logger.error(""+ae);
									}
								}
							}
							//tempData.put("scheduleInfo", scheduleInfoAry);
							tempData.remove("scheduleInfo");
							
						}
							
					   resultAry.add(tempData);
					}
					
					if(resultAry.isEmpty() || resultAry.size()<=0){
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.ERROR_GROUPS_FETCH);
					}else{
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.INFO_GROUPS_FETCH);
					}
					response.setData(resultAry);
					return response;
				}
				
			});
			
		} catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_GROUPS_FETCH);
			//Creating and throwing the customized exception.
			throw new VEMAppException("Internal Error occured at DAO layer");
		}
		
		logger.info("[END] [listgroup] [Group DAO LAYER]");
		
		return response;
	}

	/**
	 * getSites dao layer,Interacts with Database to get List of sites w.r.t group.
	 * @param groupRequest
	 * @return Response
	 * @throws VEMAppException
	 */

	public Response getSites (GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [getSites] [Group DAO LAYER]");
		Response response =new Response();
		JSONArray siteArray= new JSONArray();
		
		try {
			
			logger.debug("[DEBUG] Executing "+GET_GROUP_SITES_LIST+" procedure. with params "+groupRequest.getCustomerId()+" , "+groupRequest.getGroupId()+", "+groupRequest.getUserId()+", "+groupRequest.getIsSuperAdmin());
			
			// to fetch all the list
			groupRequest.setGroupId(0);
			
			response= jdbcTemplate.query(GET_GROUP_SITES_LIST, 
					new Object[]{
							groupRequest.getCustomerId(),groupRequest.getGroupId(),groupRequest.getUserId(),groupRequest.getIsSuperAdmin()}, new ResultSetExtractor<Response>() {
				@Override
				public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
					JSONArray resultAry=new JSONArray();
					Response response=new Response();
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					while(rs.next()){
						JSONObject tempData=new JSONObject();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
						}
					   resultAry.add(tempData);
					}
					response.setData(resultAry);
					if(resultAry.isEmpty() || resultAry.size()<=0){
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.LIST_SITES_FOR_GROUP_FAILED);
					}else{
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.LIST_SITES_FOR_GROUP_SUCCESS);
					}
					return response;
				}
				
			});
			
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.LIST_SITES_FOR_GROUP_FAILED);

			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_SITE_FAILED, logger, e);
		}
		logger.info("[END] [getSites] [Group DAO LAYER]");
		return response;
	}
	
	
	/**
	 * deleteGroup DAO is used to delete the Group.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response deleteGroup(GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [deleteGroup] [Group DAO LAYER]");
		int updateFlag = 0;
		Response response = new Response();
		try {
			
			/*
			 *  Query to update the column is_deleted=1 for making the requested
			 *  Group as logically deleted.
			 */
			String sqlUpdate = "UPDATE `group` SET `is_deleted` = 1,`modified_by` = ?,`modified_date` = sysdate()"
					+ " WHERE `group_id` = ?"; 
			
			logger.debug("[DEBUG] Executing the update query:"+sqlUpdate);
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - userId:"+groupRequest.getUserId()+" - siteId:"+groupRequest.getGroupId());
			updateFlag = jdbcTemplate.update(sqlUpdate, groupRequest.getUserId(), groupRequest.getGroupId());
			response.setData(updateFlag);
			if(updateFlag > 0){
        		response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.INFO_DELETE_GROUP);
			}else{
				
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_DELETE_GROUP);
			}
	        
		}catch (Exception e) {
			logger.error("",e);
			//Creating and throwing the customized exception.
			throw new VEMAppException("Internal Error occured at DAO layer");
		}
		logger.info("[END] [deleteGroup] [Group DAO LAYER]");
		return response;
	}
	
	/**
	 * activateOrDeActivateGroup DAO is used to activate / inactivate  Group for the associated customer.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response activateOrDeActivateGroup(GroupRequest groupRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [activateOrDeActivateSite] [Group DAO LAYER]");
		int statusFlag = 0;
		String errorMsg = "";
		SimpleJdbcCall simpleJdbcCall;
		int updateFlag = 0;
		Response response = new Response();
		
	try {
			
		String sqlUpdate = "UPDATE `group` SET `is_active` = ?,`modified_by` = ?,`modified_date` = sysdate()"
					+ " WHERE `group_id` = ?"; 
			
			logger.debug("[DEBUG] Executing the update query:"+sqlUpdate);
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - userId:"+groupRequest.getUserId()+" - siteId:"+groupRequest.getGroupId());
	    	updateFlag = jdbcTemplate.update(sqlUpdate, groupRequest.getUserId(), groupRequest.getGroupId());
			
	    	if(updateFlag > 0){
        		response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.INFO_STATUS_UPDATE_GROUP);
			}else{
				
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_STATUS_UPDATE_GROUP);
			}
	        
		}catch (Exception e) {
			logger.error("",e);
			//Creating and throwing the customized exception.
			throw new VEMAppException("Internal Error occured at DAO layer");
		}
		logger.info("[END] [activateOrDeActivateGroup] [Group DAO LAYER]");
		return response;
	}
	
	/**
	 * checkDuplicate service is used check where same group name exists for the customer.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response checkDuplicate (GroupRequest groupRequest) throws VEMAppException {
		
	logger.info("[BEGIN] [getSites] [Group DAO LAYER]");
	Response response =new Response();
	
	try {
	//Executing the procedure.
		logger.info("calling the proc "+CHECK_DUP_GRP+" with params "+groupRequest.getGroupName()+", " +groupRequest.getCustomerId());
	response= jdbcTemplate.query(CHECK_DUP_GRP, new Object[]{groupRequest.getGroupName(),groupRequest.getCustomerId()}, new ResultSetExtractor<Response>() {
		@Override
		public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
			Response response=new Response();
			int checkCount = 0;
			if(rs.next()){
				checkCount =rs.getInt("cnt");
			}
					
			if(checkCount> 0){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode("DUPLICATE_GRP_NAME_FOR_CUSTOMER");
				response.setData("true");
			}else{
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode("NAME_NOT_EXISTS_IN_DB");
				response.setData("false");
			}
			
			return response;
		}
		});
	} catch (Exception e) {
		logger.error("",e);
		response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
		response.setCode(ErrorCodes.GENERAL_APP_ERROR);
		response.setData("error");
		throw new VEMAppException("Internal Error occured at DAO layer");
		
	}
	logger.info("[END] [listgroup] [Group DAO LAYER]");
	return response;
	}
	
	/**
	 * getGroupInfo is used to get group information, It interacts with DAO impl layer.
	 * @param GroupRequest,userDetails
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response getGroupInfo (GroupRequest groupRequest,GetUserResponse userDetails) throws VEMAppException {
		
	logger.info("[BEGIN] [getGroupInfo] [Group DAO LAYER]");
	Response response =new Response();
	
	try {
	
		//Executing the procedure.
		logger.info("calling the proc "+GET_GROUP_INFO+" with params group id "+groupRequest.getGroupId()+", customer Id " +groupRequest.getCustomerId()+", customers " +userDetails.getCustomers()+", userid "+userDetails.getUserId()+", superadmin "+groupRequest.getIsSuperAdmin());
		
		//Executing the procedure.
		response= jdbcTemplate.query(GET_GROUP_INFO, 
						new Object[]{groupRequest.getGroupId(),
									 groupRequest.getCustomerId(),
									 userDetails.getCustomers(),
									 groupRequest.getUserId(),
									 groupRequest.getIsSuperAdmin()
									}, 
		new ResultSetExtractor<Response>() 
									 
			{
			@Override
			public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
				JSONArray resultAry=new JSONArray();
				Response response=new Response();
				ResultSetMetaData rsmd=rs.getMetaData();
				int columnCount=rsmd.getColumnCount();
				while(rs.next()){
					JSONObject tempData=new JSONObject();
					for(int i=1;i<=columnCount;i++){
						tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
					}
					resultAry.add(tempData);
				}
				if(resultAry.isEmpty() || resultAry.size()<=0){
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_GROUPS_FETCH);
				}else{
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_GROUPS_FETCH);
				}
				response.setData((resultAry.isEmpty() ||  resultAry.isEmpty() || resultAry.get(0)==null )?"":resultAry.get(0));
				return response;
			}
			
		});
		
	} catch (Exception e) {
		logger.error("",e);
		response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
		response.setCode(ErrorCodes.GENERAL_APP_ERROR);
		response.setData("error");
		throw new VEMAppException("Internal Error occured at DAO layer");
		
	}
	logger.info("[END] [getGroupInfo] [Group DAO LAYER]");
	return response;
	}
	
	/**
	 * getGroupSitesDao DAO is used to get group information and sites list, It interacts with DAO impl layer.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response getGroupSitesDao (GroupRequest groupRequest) throws VEMAppException {
		
	logger.info("[BEGIN] [getGroupSitesDao] [Group DAO LAYER]");
	Response response =new Response();
	
	try {
	
		//Executing the procedure.
		
		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(GET_GROUP_SITES_INFO);
		Map<String, Object> inParamMap = new HashMap<String, Object>();
		inParamMap.put("customerId", groupRequest.getCustomerId());
		inParamMap.put("groupId", groupRequest.getGroupId());
		inParamMap.put("in_user_id", groupRequest.getUserId());
		inParamMap.put("in_super_admin", groupRequest.getIsSuperAdmin());
		
		logger.info("calling the proc "+GET_GROUP_SITES_INFO+" with params "+inParamMap);
		
		SqlParameterSource in = new MapSqlParameterSource(inParamMap);
		Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
		Iterator<Entry<String, Object>> itr = simpleJdbcCallResult.entrySet().iterator();
		JSONObject responseObj=new JSONObject();
		JSONParser parser=new JSONParser();
		
		while (itr.hasNext()) {
		        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
		        String key = entry.getKey();
		        
		        if(key.equals(RESULT_SET_1))
		        {
		        	Object value = (Object) entry.getValue();
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	JSONArray resultAry=(JSONArray)parser.parse(tempAry.toString());
					responseObj.put("groupSitesList", resultAry);
				}
		        if(key.equals(RESULT_SET_2))
		        {
		        	Object value = (Object) entry.getValue();
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	JSONArray resultAry=(JSONArray)parser.parse(tempAry.toString());
		        	responseObj.put("groupInfo", (resultAry.isEmpty() ||  resultAry.isEmpty() || resultAry.get(0)==null )?"":resultAry.get(0));
				}
		        if(key.equals(RESULT_SET_3))
		        {
		        	Object value = (Object) entry.getValue();
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	JSONArray resultAry=(JSONArray)parser.parse(tempAry.toString());
		        	responseObj.put("selectedGroupSites", resultAry);
				}
		        if(key.equals(RESULT_SET_4))
		        {
		        	Object value = (Object) entry.getValue();
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	JSONArray resultAry=(JSONArray)parser.parse(tempAry.toString());
		        	responseObj.put("groupStatusList", resultAry);
				}
		        
		    }
					   
		   response.setData(responseObj);
		   response.setCode(ErrorCodes.INFO_GROUPS_FETCH);
		   response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
		   
	} catch (Exception e) {
		logger.error("",e);
		response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
		response.setCode(ErrorCodes.GENERAL_APP_ERROR);
		response.setData("error");
		throw new VEMAppException("Internal Error occured at DAO layer");
		
	}
	logger.info("[END] [getGroupSitesDao] [Group DAO LAYER]");
	return response;
	}
	
	/**
	 * getCustomerGroupDao is used to get groups list based on customer list.
	 * @param GroupRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response getCustomerGroupsDao (GroupRequest groupRequest) throws VEMAppException {
		
	logger.info("[BEGIN] [getCustomerGroupsDao] [Group DAO LAYER]");
	Response response =new Response();
	
	try {
		
		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(GET_CUSTOMER_GROUP_INFO);
		Map<String, Object> inParamMap = new HashMap<String, Object>();
		inParamMap.put("in_customer_ids", groupRequest.getCustomerIds());
		inParamMap.put("in_super_admin", groupRequest.getIsSuperAdmin());
		inParamMap.put("in_eai_admin", groupRequest.getIsEAI());
		inParamMap.put("in_user_id", groupRequest.getUserId());
		
		logger.info("executing proc "+GET_CUSTOMER_GROUP_INFO+" params "+inParamMap);
		
		SqlParameterSource in = new MapSqlParameterSource(inParamMap);
		Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
		Iterator<Entry<String, Object>> itr = simpleJdbcCallResult.entrySet().iterator();
		JSONParser parser=new JSONParser();
		JSONArray groupArray=new JSONArray();
		JSONArray siteArray=new JSONArray();
		JSONArray deviceArray = new JSONArray();
		JSONObject resultObj = new JSONObject();
		
		
		while (itr.hasNext()) {
		        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
		        String key = entry.getKey();
		        if(key.equals(RESULT_SET_1))
		        {
		        	Object value = entry.getValue();
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	groupArray=(JSONArray)parser.parse(tempAry.toString());
		        }else if(key.equals(RESULT_SET_2)){
		        	
		        	Object value = entry.getValue();
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	siteArray=(JSONArray)parser.parse(tempAry.toString());	
		        }else if(key.equals(RESULT_SET_3)){
		        	Object value = entry.getValue();
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	deviceArray=(JSONArray)parser.parse(tempAry.toString());	
		        }
		    }
		
	
		for (int j = 0; j < deviceArray.size(); j++) {
			
			logger.info("Dev List "+deviceArray);
			JSONObject dev = (JSONObject)deviceArray.get(j);
			long siteId = (long)dev.get("siteId");
			
			
			List<GroupInfo> group = ioTDao.getGroupInfo((int)siteId, groupRequest.getUserId(), groupRequest.getIsSuperAdmin());
			
			if (group!=null && group.size()>0) {
				StringBuilder groupName = new StringBuilder();
				StringBuilder groupIds = new StringBuilder();
				for (int i = 0; i < group.size(); i++) {
					
					if(i==group.size()-1){
						groupName.append(group.get(i).getGroupName());
						groupIds.append(group.get(i).getGroupId());
					}else{
						groupName.append(group.get(i).getGroupName());
						groupName.append(", ");
						groupIds.append(group.get(i).getGroupId());
						groupIds.append(", ");
						
					}
				}
				dev.put("groupId", groupIds);
				dev.put("groupName", groupName);
			}else{
				dev.put("groupId", "");
				dev.put("groupName", "");
			}
			
		}
		
		resultObj.put("groupList", groupArray);
		resultObj.put("siteList", siteArray);
		resultObj.put("deviceList", deviceArray);
		   
		if(!resultObj.isEmpty()){
    		response.setData(resultObj);
    		response.setCode(ErrorCodes.LIST_CUSTOMER_FOR_GROUP_SUCCESS);
 		    response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
    	}else{
    		response.setData(resultObj);
    		response.setCode(ErrorCodes.LIST_CUSTOMER_FOR_GROUP_EMPTY);
 		    response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
    	}
		   
	} catch (Exception e) {
		logger.error("",e);
		response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
		response.setCode(ErrorCodes.LIST_CUSTOMER_FOR_GROUP_ERROR);
		response.setData("[]");
		throw new VEMAppException("Internal Error occured at DAO layer");
		
	}
	logger.info("[END] [getCustomerGroupsDao] [Group DAO LAYER]");
	return response;
	}
	
	
}
