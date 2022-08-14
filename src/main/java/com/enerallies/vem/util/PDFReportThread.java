package com.enerallies.vem.util;

import java.net.MalformedURLException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.enerallies.vem.beans.pdfreport.PDFThreadRequest;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.listeners.iot.awsiot.ApplicationContextProvider;
import com.enerallies.vem.service.pdfreport.PdfReportService;

public class PDFReportThread extends Thread {
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(PDFReportThread.class);
	
	PDFThreadRequest pDFThreadRequest;
	public PDFReportThread(PDFThreadRequest pDFThreadRequest) {
		this.pDFThreadRequest = pDFThreadRequest;
	}
	public void run(){  
		logger.info("thread is running...");  
		PdfReportService pdfReportService = (PdfReportService) ApplicationContextProvider.getApplicationContext().getBean("pdfReportService");
		
		try {
			pDFThreadRequest.setFolderName(""+new Date().getTime());
			pdfReportService.weeklyPDFReportGenerator(pDFThreadRequest);
			pDFThreadRequest.setFolderName(""+new Date().getTime());
			pdfReportService.weeklyPDFReportGeneratorCustomer(pDFThreadRequest);
			pDFThreadRequest.setFolderName(""+new Date().getTime());
			pdfReportService.weeklyPDFReportGeneratorGroup(pDFThreadRequest);
		} catch (MalformedURLException | VEMAppException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
		logger.info("thread is completed...");  
	}  
}
