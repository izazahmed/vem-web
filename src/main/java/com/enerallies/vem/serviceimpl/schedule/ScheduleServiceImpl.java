package com.enerallies.vem.serviceimpl.schedule;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.beans.schedule.AddScheduleDetails;
import com.enerallies.vem.dao.schedule.ScheduleDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.schedule.ScheduleService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;

@Component
public class ScheduleServiceImpl implements ScheduleService{
	private static final Logger logger=Logger.getLogger(ScheduleServiceImpl.class);
	@Autowired private ScheduleDAO scheduleDao;
	
	
	@Override
	public Response getScheduleList(GetUserResponse userDetails) throws VEMAppException{
		try {
			logger.info("[BEGIN] [Schedule List] [Schedule Service Impl]");
			return scheduleDao.getScheduleList(userDetails); 
		} 
		catch(VEMAppException ve){
			throw ve;
		}
		catch (Exception e) {
			
			throw new VEMAppException(e.getMessage());
		}
	}
	@Override
	public Response getFilterData(Schedule schedule,GetUserResponse userDetails) throws VEMAppException{
		try {
			logger.info("[BEGIN] [Schedule getFilterData] [Schedule Service Impl]");
			return scheduleDao.getFilterData(schedule,userDetails); 
		} 
		catch(VEMAppException ve){
			throw ve;
		}
		catch (Exception e) {
			
			throw new VEMAppException(e.getMessage());
		}
	}
	@Override
	public Response getFilterSearch(Schedule schedule,GetUserResponse userDetailss) throws VEMAppException{
		try {
			logger.info("[BEGIN] [Schedule getFilterSearch] [Schedule Service Impl]");
			return scheduleDao.getFilterSearch(schedule,userDetailss); 
		} 
		catch(VEMAppException ve){
			throw ve;
		}
		catch (Exception e) {
			
			throw new VEMAppException(e.getMessage());
		}
	}
	@Override
	public Response getCustomerList(GetUserResponse userDetails,Schedule schedule) throws VEMAppException{
		try {
			logger.info("[BEGIN] [getCustomerList List] [Schedule Service Impl]");
			return scheduleDao.getCustomerList(userDetails,schedule); 
		} 
		catch(VEMAppException ve){
			throw ve;
		}
		catch (Exception e) {
			
			throw new VEMAppException(e.getMessage());
		}
	}
	
	@Override
	public Response applySchedule(GetUserResponse userDetails,Schedule schedule) throws VEMAppException{
		try {
			logger.info("[BEGIN] [applySchedule] [Schedule Service Impl]");
			return scheduleDao.applySchedule(userDetails,schedule); 
		} 
		catch(VEMAppException ve){
			throw ve;
		}
		catch (Exception e) {
			
			throw new VEMAppException(e.getMessage());
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public Response addSchedule(AddScheduleDetails addScheduleDetails, int userId)
			throws VEMAppException {
		
		logger.info("[BEGIN] [addSchedule] [Schedule SERVICE LAYER]");
		
		Response response = new Response();
		
		JSONObject siteObject = new JSONObject();
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(addScheduleDetails);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				//Catches when all the server or bean level validations are true.
				
				if(scheduleDao.validateDuplicateSchedule(addScheduleDetails) ==0 ){
				
					int scheduleId = scheduleDao.addSchedule(addScheduleDetails, userId);
					
					/* if status is 1 or greater means the add site request is
					 *  success
					 *  else fail.
					 */
					if(scheduleId > 0){
						siteObject.put("scheduleId", scheduleId);
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.ADD_SCHEDULE_SUCCESS);
						response.setData(siteObject);
					}else{
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.ADD_SITE_FAILED);
						response.setData(CommonConstants.ERROR_OCCURRED+":Site has not created at DB Side.");
					}
				}else{
					logger.info("[BEGIN] [addSchedule] [Duplicate Schedule name]");
					
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_DUPLICATE_SCHEDULE);
					response.setData(CommonConstants.ERROR_OCCURRED+":Duplicate Schedule");
					
				}
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ADD_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [addSchedule] [Schedule service SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getScheduleDetails(AddScheduleDetails addScheduleDetails, GetUserResponse userDetails) throws VEMAppException {
		
		logger.info("[BEGIN] [addSchedule] [Schedule SERVICE LAYER]");
		
		Response response = new Response();
		
		try {
			
			
				
			response = scheduleDao.getScheduleDetails(addScheduleDetails,userDetails);
					
			
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ADD_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [addSchedule] [Schedule service SERVICE LAYER]"+response);
		
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Response updateSchedule(AddScheduleDetails addScheduleDetails, int userId)
			throws VEMAppException {
		
		logger.info("[BEGIN] [updateSchedule] [Schedule SERVICE LAYER]");
		
		Response response = new Response();
		
		JSONObject siteObject = new JSONObject();
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(addScheduleDetails);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				//Catches when all the server or bean level validations are true.
				
				if( addScheduleDetails.getScheduleName().equalsIgnoreCase(addScheduleDetails.getOldName()) || scheduleDao.validateDuplicateSchedule(addScheduleDetails) ==0 ){
				
					response = scheduleDao.updateSchedule(addScheduleDetails, userId);
					
					/* if status is 1 or greater means the add site request is
					 *  success
					 *  else fail.
					 */
					
				}else{
					logger.info("[BEGIN] [updateSchedule] [Duplicate Schedule name]");
					
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.ERROR_DUPLICATE_SCHEDULE);
					response.setData(CommonConstants.ERROR_OCCURRED+":Duplicate Schedule");
					
				}
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ADD_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [addSchedule] [Schedule service SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response deleteSchedule(AddScheduleDetails addScheduleDetails, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [deleteSchedule] [Schedule SERVICE LAYER]");
		
		Response response = new Response();
		
		try {
			
			
				
			response = scheduleDao.deleteSchedule(addScheduleDetails,userId);
					
			
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ADD_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [deleteSchedule] [Schedule service SERVICE LAYER]"+response);
		
		
		return response;
	}
	
	@Override
	public Response applyScheduleValidate(GetUserResponse userDetails,Schedule schedule) throws VEMAppException{
		try {
			logger.info("[BEGIN] [applySchedule] [Schedule Service Impl]");
			return scheduleDao.applyScheduleValidate(userDetails,schedule); 
		} 
		catch(VEMAppException ve){
			throw ve;
		}
		catch (Exception e) {
			
			throw new VEMAppException(e.getMessage());
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Response addDeviceSchedule(AddScheduleDetails addScheduleDetails, int userId)
			throws VEMAppException {
		
		logger.info("[BEGIN] [addDeviceSchedule] [Schedule SERVICE LAYER]");
		
		Response response = new Response();
		
		JSONObject siteObject = new JSONObject();
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(addScheduleDetails);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
					int scheduleId = scheduleDao.addDeviceSchedule(addScheduleDetails, userId, false);
					
					/* if status is 1 or greater means the add site request is
					 *  success
					 *  else fail.
					 */
					if(scheduleId > 0){
						siteObject.put("scheduleId", scheduleId);
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					      logger.info("Schedule Name ----"+addScheduleDetails.getScheduleName());
						 if (addScheduleDetails.getScheduleName()=="Custom Schedule" || ("Custom Schedule" ).equalsIgnoreCase(addScheduleDetails.getScheduleName())){
						      response.setCode(ErrorCodes.ADD_SCHEDULE_CUSTOM_SUCCESS);
						      }else{
						       response.setCode(ErrorCodes.ADD_SCHEDULE_SUCCESS);
						      }
						response.setData(siteObject);
					}else{
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.ADD_SITE_FAILED);
						response.setData(CommonConstants.ERROR_OCCURRED+":Site has not created at DB Side.");
					}
			
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ADD_SITE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ADD_SITE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [addDeviceSchedule] [Schedule service SERVICE LAYER]");
		
		return response;
	}
	
}
