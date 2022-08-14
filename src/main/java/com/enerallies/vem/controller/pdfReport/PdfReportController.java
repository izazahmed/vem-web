package com.enerallies.vem.controller.pdfReport;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.pdfreport.PDFThreadRequest;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.pdfreport.PdfReportService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.PDFReportThread;
import com.itextpdf.text.DocumentException;

@Controller
public class PdfReportController {
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(PdfReportController.class);
	
	/** Constant for user details object **/
	private static final String USER_DETAILS_OBJECT="eaiUserDetails";
	
	@Autowired PdfReportService pdfReportService;
	
	@RequestMapping(value = "/pdfreport/weeklyPDFReportGenerator", method = RequestMethod.GET)
	public void weeklyPDFReportGenerator(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws VEMAppException, DocumentException, IOException {
		URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());	
		
		PDFThreadRequest pDFThreadRequest = new PDFThreadRequest();
		pDFThreadRequest.setUrl(url);
		pDFThreadRequest.setDate(new Date());
		PDFReportThread pDFReportThread = new PDFReportThread(pDFThreadRequest);
		pDFReportThread.start();
	}
	
	@RequestMapping(value = "/api/pdfreport/resendPDF", method = RequestMethod.GET)
	public ResponseEntity<Response> resendPDF(
			@RequestParam int reportId, @RequestParam String email,HttpServletRequest request, HttpSession session) throws VEMAppException, DocumentException, IOException {
		logger.info("[BEGIN] [resendPDF] [PDF Report Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid.
				 */
				response = pdfReportService.resendPDFReport(reportId, email, userDetails);
				status = HttpStatus.OK;
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch(NullPointerException ne){
			status = HttpStatus.BAD_REQUEST;
			logger.error("",ne);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [resendPDF] [PDF Report Controller Layer]");

		return new ResponseEntity<>(response, status);
	}
	
	@RequestMapping(value = "/api/pdfreport/deletePDFReport", method = RequestMethod.GET)
	public ResponseEntity<Response> deletePDFReport(
			@RequestParam String reportId, HttpServletRequest request, HttpSession session) throws VEMAppException, DocumentException, IOException {
		logger.info("[BEGIN] [deletePDFReport] [PDF Report Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid.
				 */
				response = pdfReportService.deletePDFReport(reportId);
				status = HttpStatus.OK;
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch(NullPointerException ne){
			status = HttpStatus.BAD_REQUEST;
			logger.error("",ne);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [deletePDFReport] [PDF Report Controller Layer]");

		return new ResponseEntity<>(response, status);
	}
	
	@RequestMapping(value = "/api/pdfreport/getReportList", method = RequestMethod.GET)
	public ResponseEntity<Response> getReportList(HttpServletRequest request, HttpSession session) throws VEMAppException, DocumentException, IOException {
		logger.info("[BEGIN] [getReportList] [PDF Report Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		/*
		 *  Getting the session details
		 */
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute(USER_DETAILS_OBJECT);
		
		try {
			
			/*
			 *  checking for if session is valid or not
			 */
			if(userDetails != null){
				/*
				 * Catches when the session is valid.
				 */
				response = pdfReportService.getReportList(userDetails.getUserId());
				status = HttpStatus.OK;
			}else{
				
				/*
				 *  Catches when the request is unauthorized
				 *  and Preparing failure response
				 */
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}
			
		}catch(NullPointerException ne){
			status = HttpStatus.BAD_REQUEST;
			logger.error("",ne);
		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [getReportList] [PDF Report Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
}
