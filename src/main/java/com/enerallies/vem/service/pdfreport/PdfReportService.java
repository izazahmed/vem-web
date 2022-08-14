package com.enerallies.vem.service.pdfreport;

import java.net.MalformedURLException;

import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.pdfreport.AddPDFReportDataRequest;
import com.enerallies.vem.beans.pdfreport.PDFThreadRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : PdfReportService 
 * 
 * PdfReportService service is used to serve the all pdf related operations.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-02-2017
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 08-02-2017		Bhoomika Rabadiya   File Created.
 * 08-02-2017		Bhoomika Rabadiya	createPDFHtml() method has added.
 *
 */

@Component
public interface PdfReportService {
	
	/**
	 * createPDFHtml service is used to create the pdf file.
	 * @param b 
	 * 
	 * @param jsonObj
	 * @return StringBuilder
	 * @throws VEMAppException
	 */
	public Response weeklyPDFReportGenerator(PDFThreadRequest pDFThreadRequest) throws VEMAppException, MalformedURLException;
	
	public Response resendPDFReport(int reportId, String email, GetUserResponse userDetails) throws VEMAppException, MalformedURLException;
	
	public Response deletePDFReport(String reportId) throws VEMAppException, MalformedURLException;
	
	public Response getReportList(int userId) throws VEMAppException;
	
	public Response addPDFReportData(AddPDFReportDataRequest addPDFReportDataRequest, int userId) throws VEMAppException;

	public Response weeklyPDFReportGeneratorCustomer(PDFThreadRequest pDFThreadRequest) throws VEMAppException, MalformedURLException;
	
	public Response weeklyPDFReportGeneratorGroup(PDFThreadRequest pDFThreadRequest) throws VEMAppException, MalformedURLException;
}
