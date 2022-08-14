package com.enerallies.vem.serviceimpl.site;

import java.util.Date;

import org.apache.log4j.Logger;

import com.enerallies.vem.beans.upload.FileUploadResponse;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.listeners.iot.awsiot.ApplicationContextProvider;
import com.enerallies.vem.service.site.SiteService;

public class BulkUploaderThread extends Thread{

	private static final Logger logger = Logger.getLogger(BulkUploaderThread.class);
	private FileUploadResponse fileUploadResponse;
	private String uploadRouterFlag;
	
	
	public void siteBulkUploadMethod(FileUploadResponse fileUploadResponse){
		
		logger.info("Intializing the site upload parameters for thread "+this.getName());
		
		uploadRouterFlag="sites";
		this.fileUploadResponse=fileUploadResponse;
		this.setName("Site_Bulk_Upload_Thread_"+new Date().toString().replace(" ", "-"));
		
		// run the thread
		this.start();
	}
	
	
	@Override
	public void run(){
		if ("sites".equalsIgnoreCase(uploadRouterFlag)) {
			logger.info("Running the thread for " + this.getName());
			try {
				SiteService siteService = (SiteService) ApplicationContextProvider.getApplicationContext().getBean("siteService");
				siteService.uploadSiteTemplate(fileUploadResponse);
			} catch (VEMAppException e) {
				logger.error(e);
			}
		}
	}
	
}
