package com.enerallies.vem.dao.bulkupload;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.enerallies.vem.beans.bulkupload.BulkUploadBean;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;

@Repository
public interface BulkUploadDao {

	public Response getBulkUploadProgress(BulkUploadBean bulkUploadBean)  throws VEMAppException;;
	public void setDataSource(DataSource dataSource);
	public Response deleteSiteUpload(BulkUploadBean bulkUploadBean) throws VEMAppException;
}
