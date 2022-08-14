package com.enerallies.vem.serviceimpl.bulkupload;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.bulkupload.BulkUploadBean;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.upload.FileUploadResponse;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.dao.bulkupload.BulkUploadDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.bulkupload.BulkUploadService;

@Service("bulkUploadService")
public class BulkUploadServiceImpl implements BulkUploadService {

	private Logger logger=Logger.getLogger(BulkUploadServiceImpl.class);
	@Autowired BulkUploadDao bulkUploadDao;
	@Autowired AuditDAO auditDAO;
	
	@Override
	public Response getBulkImportStatus(BulkUploadBean bulkUploadBean)  throws VEMAppException{
		Response response=new Response();
		logger.info("Controller reached at [BulkUploadServiceImpl][getBulkImportStatus]");
		try{
			response=bulkUploadDao.getBulkUploadProgress(bulkUploadBean);
		}catch(Exception e){
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}		
		return response;		
	}
	
	@Override
	public Response deleteSiteUpload(BulkUploadBean bulkUploadBean)  throws VEMAppException{
		Response response=new Response();
		logger.info("Controller reached at [BulkUploadServiceImpl][deleteSiteUpload]");
		try{
			response=bulkUploadDao.deleteSiteUpload(bulkUploadBean);
			logSuccessInformation(bulkUploadBean);
		}catch(Exception e){
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}		
		return response;		
	}
	
	private void logSuccessInformation(BulkUploadBean bulkUploadBean) {
		try {
			AuditRequest auditRequest = new AuditRequest();
			auditRequest.setUserId(bulkUploadBean.getUserId());
			auditRequest.setUserAction("Deleted");
			auditRequest.setLocation("");
			auditRequest.setServiceId("13");
			auditRequest.setDescription("Bulk import template <"+bulkUploadBean.getFileName()+"> has been successfully deleted");
			auditRequest.setServiceSpecificId(0);
			auditDAO.insertAuditLog(auditRequest);
		} catch (Exception e) {
			logger.error("BulkUploadServiceImpl logSuccessInformation Error:",e);
		}
	}
}
