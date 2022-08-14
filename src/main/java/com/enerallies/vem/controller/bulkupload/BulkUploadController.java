package com.enerallies.vem.controller.bulkupload;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.codedeploy.model.ErrorCode;
import com.enerallies.vem.beans.admin.BulkUploadResponse;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.bulkupload.BulkUploadBean;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.bulkupload.BulkUploadService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

@RestController
@RequestMapping("/api/uploadprogress")
public class BulkUploadController {

	@Autowired BulkUploadService bulkUploadService;
	private Logger logger=Logger.getLogger(BulkUploadController.class);
	private GetUserResponse userDetails;
	
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
	
	@RequestMapping(value="/getUploadStatus", method=RequestMethod.GET)
	public ResponseEntity<Response> bulkUploadStatus(HttpSession session){
		
		Response response =new Response();
		HttpStatus status=HttpStatus.EXPECTATION_FAILED;
		try{
			
			logger.info("Controller reached at [BulkUploadController][bulkUploadStatus]");
			
			//validating session
			if(!validateSession(session)) {
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			} else {
				BulkUploadBean bulkUploadBean=new BulkUploadBean();
				bulkUploadBean.setUserId(userDetails.getUserId());
				bulkUploadBean.setTimeZone(userDetails.getTimeZone());
				bulkUploadBean.setIsSuper(userDetails.getIsSuper());
				bulkUploadBean.setFileName("");
				response=bulkUploadService.getBulkImportStatus(bulkUploadBean);
			}
		
			
			status=HttpStatus.OK;
			response.setCode(ErrorCodes.BULK_UPLOAD_SUCCESS);
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						
		}catch(Exception e){
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
			if(response.getCode()==null || response.getCode().isEmpty())
				response.setCode(ErrorCodes.BULK_UPLOAD_ERROR);
			if(response.getStatus()==null || response.getStatus().isEmpty())
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
		}
		return new ResponseEntity<>(response,status);		
	}
	
	@RequestMapping(value="/deleteSiteUpload", method=RequestMethod.GET)
	public ResponseEntity<Response> deleteSiteUpload(@RequestParam int bulkUploadProgressId, @RequestParam String fileName, HttpSession session){
		
		Response response =new Response();
		HttpStatus status=HttpStatus.EXPECTATION_FAILED;
		try{
			
			logger.info("Controller reached at [BulkUploadController][deleteSiteUpload]");
			
			//validating session
			if(!validateSession(session)) {
				status = HttpStatus.UNAUTHORIZED;
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				throw new VEMAppException(CommonConstants.ERROR_SESSION_INVALID);
			} else {
				BulkUploadBean bulkUploadBean=new BulkUploadBean();
				bulkUploadBean.setUserId(userDetails.getUserId());
				bulkUploadBean.setTimeZone(userDetails.getTimeZone());
				bulkUploadBean.setIsSuper(userDetails.getIsSuper());
				bulkUploadBean.setBulkUploadProgressId(bulkUploadProgressId);
				bulkUploadBean.setFileName(fileName);
				response=bulkUploadService.deleteSiteUpload(bulkUploadBean);
			}
		
			
			status=HttpStatus.OK;
			response.setCode(ErrorCodes.BULK_UPLOAD_SUCCESS);
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						
		}catch(Exception e){
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
			if(response.getCode()==null || response.getCode().isEmpty())
				response.setCode(ErrorCodes.BULK_UPLOAD_ERROR);
			if(response.getStatus()==null || response.getStatus().isEmpty())
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
		}
		return new ResponseEntity<>(response,status);
		
	}
	
}
