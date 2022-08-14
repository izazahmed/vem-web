package com.enerallies.vem.jobschedule;

import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.pdfreport.PDFThreadRequest;
import com.enerallies.vem.service.pdfreport.PdfReportService;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.PDFReportThread;

@Component(value="pdfReportJob")
public class PDFReportJob {
	
	/** Getting logger*/	
	private static final Logger logger = Logger.getLogger(PDFReportJob.class);
	
	private void weeklyPdfReportMethod(){
		try {
			logger.info("weeklyPdfReportMethod job has been started"+new Date());
			
			PDFThreadRequest pDFThreadRequest = new PDFThreadRequest();
			pDFThreadRequest.setUrl(new URL(ConfigurationUtils.getConfig("app.url."+ConfigurationUtils.getConfig("build.env"))));
			pDFThreadRequest.setDate(new Date());
			PDFReportThread pDFReportThread = new PDFReportThread(pDFThreadRequest);
			pDFReportThread.start();
			
			logger.info("weeklyPdfReportMethod job has been ended"+new Date());
		} catch (Exception e) {
			logger.error("weeklyPdfReportMethod : ", e);
		}
	}
}
