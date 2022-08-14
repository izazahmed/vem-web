/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util.publish;

import java.io.File;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.admin.MailBroadCast;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.ConfigurationUtils;

/**
 * File Name : MailPublisher 
 * 
 * MailPublisher: is used to publish mail
 *
 * @author (Nagarjuna Eerla � CTE).
 * 
 * Contact (Umang)
 * 
 * @version     1.0
 * @date        04-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 04-08-2016		Nagarjuna Eerla		File Created
 *
 */
@Component
public class MailPublisher{
	// Getting logger
	private static final Logger logger = Logger.getLogger(MailPublisher.class);
	
	// Mail sender implementation class to send mails
	private JavaMailSenderImpl mailSender;
	
	// holds mail host value
	private final String mailHost = ConfigurationUtils.getConfig("mail.host");
	// holds mail port value
	private final String mailPort = ConfigurationUtils.getConfig("mail.port");
	// holds mail from address value
	private final String fromAddress = ConfigurationUtils.getConfig("mail.username");
	// holds mail password
	private final String mailPassword = ConfigurationUtils.getConfig("mail.password");

	/**
	 * 
	 * publishEmail : This is a final method  to send an Email, and it is common for  for application
	 * 
	 * @param broadCast
	 * @return
	 * @throws VEMAppException
	 */
	public boolean publishEmail(MailBroadCast broadCast) throws VEMAppException {
		if(logger.isDebugEnabled()) {
			logger.debug("[BEGIN][publishEmail][Publish Layer]");
		}
		boolean flag = false;
		try {
		
			// Preparing java mail sender object
			mailSender = new JavaMailSenderImpl();
			mailSender.setHost(mailHost);
			mailSender.setPort(Integer.parseInt(mailPort));
			mailSender.setUsername(fromAddress);
			mailSender.setPassword(mailPassword);
			
			// Adding mail authentication properties
			Properties javaMailProperties = new Properties();
			javaMailProperties.put("mail.smtp.starttls.enable", "true");
			javaMailProperties.put("mail.smtp.auth", "true");
			javaMailProperties.put("mail.transport.protocol", "smtp");
			
			// Attaching authentication properties to mail sender
			mailSender.setJavaMailProperties(javaMailProperties);
			
			// Adding default from address to broadcast object
			broadCast.setFromEmail(fromAddress);
			
			if(StringUtils.isNotEmpty(broadCast.getToEmail())) {
				// Instantiating MiME message to send HTML content
				MimeMessage message = mailSender.createMimeMessage();
				
				// Instantiating helper class to message object
				MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
				
				// Adding from mail to helper
				helper.setFrom(broadCast.getFromEmail());
				
				// Splitting to address and adding those into to helper
				if(StringUtils.isNotEmpty(broadCast.getToEmail())) {
					String[] toAddress = broadCast.getToEmail().split(",");
					helper.setTo(toAddress);
				}
				
				// Adding subject to helper instance
				helper.setSubject(broadCast.getSubject());
				
				// Adding mail body to helper instance
				helper.setText(broadCast.getMailText(), true);
				
				
				// final step to send a mail
				mailSender.send(message);
				
				flag = true;
			}
			else {
				throw new VEMAppException("No to email addresses available !!");
			}
		}
		catch(Exception e) {
			logger.debug("[ERROR][publishEmail][Publish Layer] "+e);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("[END][publishEmail][Publish Layer]");
		}
		return flag;
	}
	
	public boolean publishEmailWithAttachment(MailBroadCast broadCast, File file, String fileName, boolean multiple) throws VEMAppException {
		if(logger.isDebugEnabled()) {
			logger.debug("[BEGIN][publishEmailWithAttachment][Publish Layer]");
		}
		boolean flag = false;
		try {
		
			// Preparing java mail sender object
			mailSender = new JavaMailSenderImpl();
			mailSender.setHost(mailHost);
			mailSender.setPort(Integer.parseInt(mailPort));
			mailSender.setUsername(fromAddress);
			mailSender.setPassword(mailPassword);
			
			// Adding mail authentication properties
			Properties javaMailProperties = new Properties();
			javaMailProperties.put("mail.smtp.starttls.enable", "true");
			javaMailProperties.put("mail.smtp.auth", "true");
			javaMailProperties.put("mail.transport.protocol", "smtp");
			
			// Attaching authentication properties to mail sender
			mailSender.setJavaMailProperties(javaMailProperties);
			
			// Adding default from address to broadcast object
			broadCast.setFromEmail(fromAddress);
			
			if(StringUtils.isNotEmpty(broadCast.getToEmail())) {
				// Instantiating MiME message to send HTML content
				MimeMessage message = mailSender.createMimeMessage();
				
				// Instantiating helper class to message object
				MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
				
				// Adding from mail to helper
				helper.setFrom(broadCast.getFromEmail());
				
				// Splitting to address and adding those into to helper
				if(StringUtils.isNotEmpty(broadCast.getToEmail())) {
					String[] toAddress = broadCast.getToEmail().split(",");
					helper.setTo(toAddress);
				}
				
				// Adding subject to helper instance
				helper.setSubject(broadCast.getSubject());
				
				// Adding mail body to helper instance
				helper.setText(broadCast.getMailText(), true);
				
				if (multiple) {
					helper.addAttachment(fileName, file);
				}
				
				// final step to send a mail
				mailSender.send(message);
				
				flag = true;
			}
			else {
				throw new VEMAppException("No to email addresses available !!");
			}
		}
		catch(Exception e) {
			logger.error("[ERROR][publishEmailWithAttachment][Publish Layer] ",e);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("[END][publishEmailWithAttachment][Publish Layer]");
		}
		return flag;
	}
}
