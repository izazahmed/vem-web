package com.enerallies.vem.controller.schedule;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.controller.customers.CustomersController;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.customers.CustomersService;
import com.enerallies.vem.service.schedule.ScheduleService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

@Controller
@RequestMapping("/api/schedule")
public class ScheduleController { 
	
	private static final Logger logger=Logger.getLogger(ScheduleController.class);
	@Autowired private ScheduleService scheduleService;
	
	@RequestMapping(value="/scheduleList",method=RequestMethod.POST)
	@ResponseBody
	private ResponseEntity<Response> schedulerList(HttpServletRequest request,HttpSession session,@RequestBody org.json.simple.JSONObject obj){
		
		Response response = new Response();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		try{
			logger.info("[BEGIN] [Customers List] [Customers Controller Layer]");
			
			logger.info("Scheduler getFilterData Begin **************"+obj);
			String customerId="";
			if(obj.get("customerId") !=null){
				customerId=(String)obj.get("customerId");
			}
			logger.info("customerId------------"+customerId);
			
			GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		    if (userDetails != null) {
				response=scheduleService.getScheduleList(userDetails);
				status = HttpStatus.OK;
				response.setStatus("Success");
			}else{
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
				
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
			
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
				
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMERS_FETCH);
			}
			
		}	
			
		
		
		return new ResponseEntity<>(response, status);
	}
	@RequestMapping(value="/getFilterData",method=RequestMethod.POST)
	@ResponseBody
	private ResponseEntity<Response> getFilterData(HttpServletRequest request,HttpSession session,@RequestBody org.json.simple.JSONObject obj){
		
		Response response = new Response();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String filterBy="";
		String tabFiledId="";
		
		try{
			logger.info("Scheduler getFilterData Begin **************"+obj);
			if(!validateSession(session)){
				logger.error("[ERROR][CustomersController][customerList]");
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);	
			}
			
			GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
			
			if(obj.get("filterBy") !=null){
				filterBy=(String)obj.get("filterBy");
			}
			if(obj.get("tabFiledId") !=null){
				tabFiledId=(String)obj.get("tabFiledId");
			}
			Schedule schedule=new Schedule();
			schedule.setFlag(filterBy);
			schedule.setSearchId(tabFiledId);
			response=scheduleService.getFilterData(schedule,userDetails);
			status = HttpStatus.OK;
			
			
			logger.info("Scheduler getFilterData Begin **************"+obj);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
			
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
				
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMERS_FETCH);
			}
			
		}	
			
		logger.info("status **************"+status);
		
		return new ResponseEntity<>(response, status);
	}
	@RequestMapping(value="/getFilterSearch",method=RequestMethod.POST)
	@ResponseBody
	private ResponseEntity<Response> getFilterSearch(HttpServletRequest request,HttpSession session,@RequestBody org.json.simple.JSONObject obj){
		
		Response response = new Response();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		String filterBy="";
		String searchValue="";
		String tabFiledId="";
		
		try{
			GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
			logger.info("Scheduler getFilterSearch Begin *************"+obj);
			if(!validateSession(session)){
				logger.error("[ERROR][ScheduleController][getFilterSearch]");
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);	
			}
			
			if(obj.get("filterBy") !=null){
				filterBy=(String)obj.get("filterBy");
			}
			if(obj.get("searchValue") !=null){
				searchValue=(String)obj.get("searchValue");
			}
			if(obj.get("tabFiledId") !=null){
				tabFiledId=(String)obj.get("tabFiledId");
			}
			Schedule schedule=new Schedule();
			schedule.setFlag(filterBy);
			schedule.setSearchId(searchValue);
			schedule.setCustomerId(tabFiledId);
			response=scheduleService.getFilterSearch(schedule,userDetails);
			status = HttpStatus.OK;
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
			
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
				
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMERS_FETCH);
			}
			
		}	
			
		
		
		return new ResponseEntity<>(response, status);
	}
	
	private boolean validateSession(HttpSession session){
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
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

}
