package com.enerallies.vem.service.schedule;

import org.springframework.stereotype.Service;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.schedule.AddScheduleDetails;
import com.enerallies.vem.exceptions.VEMAppException;

@Service
public interface ScheduleService {
	
	public Response getScheduleList(GetUserResponse userDetails) throws VEMAppException;
	public Response getFilterData(Schedule schedule,GetUserResponse userDetails) throws VEMAppException;
	public Response getFilterSearch(Schedule schedule,GetUserResponse userDetailss) throws VEMAppException;
	public Response getCustomerList(GetUserResponse userDetails,Schedule schedule) throws VEMAppException;
	public Response applySchedule(GetUserResponse userDetails,Schedule schedule) throws VEMAppException;
	public Response applyScheduleValidate(GetUserResponse userDetails,Schedule schedule) throws VEMAppException;
	
	/**
	 * addSite service is used to create the new Site.
	 * 
	 * @param addSiteRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response addSchedule(AddScheduleDetails addScheduleDetails,int userId) throws VEMAppException;
	
	/**
	 * addSite service is used to create the new Site.
	 * 
	 * @param addScheduleDetails
	 * @param userDetails
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getScheduleDetails(AddScheduleDetails addScheduleDetails, GetUserResponse userDetails) throws VEMAppException;
	
	
	/**
	 * addSite service is used to create the new Site.
	 * 
	 * @param addSiteRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response updateSchedule(AddScheduleDetails addScheduleDetails,int userId) throws VEMAppException;
	
	/**
	 * addSite service is used to create the new Site.
	 * 
	 * @param addSiteRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response deleteSchedule(AddScheduleDetails addScheduleDetails,int userId) throws VEMAppException;
	/**
	 * addSite service is used to create the new Site.
	 * 
	 * @param addSiteRequest
	 * @param userId
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response addDeviceSchedule(AddScheduleDetails addScheduleDetails,int userId) throws VEMAppException;
	

	

}
