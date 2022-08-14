package com.enerallies.vem.service.bulkupload;

import org.springframework.stereotype.Service;
import com.enerallies.vem.beans.bulkupload.BulkUploadBean;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;

@Service
public interface BulkUploadService {

	public Response getBulkImportStatus(BulkUploadBean bulkUploadBean)  throws VEMAppException;

	public Response deleteSiteUpload(BulkUploadBean bulkUploadBean) throws VEMAppException; 
	
}
