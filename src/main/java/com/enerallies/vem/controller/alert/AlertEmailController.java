package com.enerallies.vem.controller.alert;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.alert.AlertEmailService;
import com.itextpdf.text.DocumentException;

@Controller
public class AlertEmailController {
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(AlertEmailController.class);
	
	/** Constant for user details object **/
	private static final String USER_DETAILS_OBJECT="eaiUserDetails";
	
	@Autowired AlertEmailService alertEmailService;
	
	@RequestMapping(value = "/alertMail/mailGenerator", method = RequestMethod.GET)
	public void mailGenerator(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws VEMAppException, DocumentException, IOException {
		alertEmailService.mailGenerator(true);
	}

}
