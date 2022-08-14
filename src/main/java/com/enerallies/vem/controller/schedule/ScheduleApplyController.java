package com.enerallies.vem.controller.schedule;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.service.schedule.ScheduleService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

@Controller
@RequestMapping("/api/scheduleApply")
public class ScheduleApplyController {
	
	private static final Logger logger=Logger.getLogger(ScheduleController.class);
		@Autowired private ScheduleService scheduleService;
		
		@RequestMapping(value="/applyViewList",method=RequestMethod.POST)
		@ResponseBody
		private ResponseEntity<Response> applyViewList(HttpServletRequest request,HttpSession session,@RequestBody org.json.simple.JSONObject obj){
			
			Response response = new Response();
			HttpStatus status = HttpStatus.BAD_REQUEST;
			
			try{
				logger.info("[BEGIN] [applyViewList] [ScheduleApplyController  Layer]");
				
				GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
				Schedule schedule=new Schedule();
				logger.info("filterBy--111----------"+obj);
				
				logger.info("filterBy--111----------"+obj.get("searchId"));
				
				 String filterBy=(String)obj.get("filterBy");
				 String scheduleId=(String)obj.get("scheduleId");
				 
				 String searchIds="";
				 if(filterBy.equalsIgnoreCase("groups")){
					 searchIds=(String)obj.get("searchId");
				 }else if(filterBy.equalsIgnoreCase("sitesdefualt")){
					 searchIds=(String)obj.get("searchId");
				 }else if(filterBy.equalsIgnoreCase("devicedefualt")){
					 searchIds=(String)obj.get("searchId");
				 }
				 else{
					 ArrayList list1 = new ArrayList();
					 if(obj.get("searchId") !=null){
						 list1=(ArrayList)obj.get("searchId"); 
						
						if(list1.size()>0){
							for(int i=0;i<list1.size();i++){
								searchIds+=list1.get(i)+"~";
							}
						}
					 }
				 }
				/*if(obj.get("searchId")!=null){
					searchId=searchId.replace(",", "~");
				}*/
				
				schedule.setFlag(filterBy);
				schedule.setSearchId(searchIds);
				schedule.setScheduleId(scheduleId);
				logger.info("userDetails------------"+userDetails.getUserId());
				logger.info("filterBy------------"+filterBy);
				logger.info("searchId----replace--------"+searchIds);
				logger.info("scheduleId----scheduleId--------"+scheduleId);
				response=scheduleService.getCustomerList(userDetails,schedule);
				
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
		@RequestMapping(value="/applySchedule",method=RequestMethod.POST)
		@ResponseBody
		private ResponseEntity<Response> applySchedule(HttpServletRequest request,HttpSession session,@RequestBody org.json.simple.JSONObject obj){
			
			Response response = new Response();
			List customerList = null;
			List groupList = null;
			List siteList=null;
			List deviceList=null;
			
			HttpStatus status = HttpStatus.BAD_REQUEST;
			String customerId="";
			String groupId="";
			String siteId="";
			String deviceId="";
			String scheduleId="";
			String validateFlag="";
			String fromSubTab="";
			
			try{
				logger.info("[BEGIN] [applySchedule] [ScheduleApplyController  Layer]");
				
				GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
				Schedule schedule=new Schedule();
				logger.info("filterBy--scheduleApplySave----------"+obj);
				if(obj.get("validateFlag") !=null){
					validateFlag=(String)obj.get("validateFlag");
				}
				if(obj.get("scheduleId") !=null){
					scheduleId=(String)obj.get("scheduleId");
				}
				if(obj.get("customerId") !=null){
					customerId=(String)obj.get("customerId");
				}
				 if(obj.get("groupIds") !=null){
					 groupList=(ArrayList)obj.get("groupIds"); 
			 		if(groupList.size()>0){
			 			groupId=getSplitIds(groupList);
					}
				 }
				 if(obj.get("siteIds") !=null){
					 siteList=(ArrayList)obj.get("siteIds"); 
			 		if(siteList.size()>0){
			 			siteId=getSplitIds(siteList);
					}
				 }
				 if(obj.get("deviceIds") !=null){
					 deviceList=(ArrayList)obj.get("deviceIds"); 
			 		if(deviceList.size()>0){
			 			deviceId=getSplitIds(deviceList);
					}
				 }
				 if(obj.get("fromSubTab") !=null){
					 fromSubTab=(String)obj.get("fromSubTab"); 
			 		
				 }
				 logger.info("**********fromSubTab********************"+fromSubTab);
				 logger.info("******************************"+deviceId);
				 logger.info("****************scheduleId**************"+scheduleId);
				schedule.setCustomerId(customerId);
				schedule.setGroupId(groupId);
				schedule.setSiteId(siteId);
				schedule.setDeviceId(deviceId);
				schedule.setScheduleId(scheduleId);
				schedule.setFlag(fromSubTab);
				if(validateFlag.equalsIgnoreCase("true")){
					response=scheduleService.applyScheduleValidate(userDetails,schedule);
				}else{
					response=scheduleService.applySchedule(userDetails,schedule);
				}
				
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
	private String getSplitIds(List list){
			String ids="";
			
			for(int i=0;i<list.size();i++){
				ids+=list.get(i)+"~";
			}
			ids=ids.substring(0,ids.length()-1);
		return ids;
	}	
}
