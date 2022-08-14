package com.enerallies.vem.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enerallies.vem.beans.admin.MailBroadCast;
import com.enerallies.vem.beans.admin.User;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.publish.MailPublisher;
import com.enerallies.vem.util.template.ITemplateUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;


@Scope("prototype")
@Component
@Transactional
public class UploadUserTemplateThread implements Runnable{
	
	// Getting logger instance
	private static final Logger logger = Logger.getLogger(UploadUserTemplateThread.class);
	
	@Autowired
	private ITemplateUtil itemplateUtil = new ITemplateUtil() {
		
		@Override
		public String prepareContentFromTemplateString(String templateStr,
				Object model) throws VEMAppException {
			Template template = null ;
			String content = null;
			try {
				//Creating random template name
				String templateName = RandomStringUtils.randomAlphabetic(5);
				//Creating configuration object
				Configuration configuration = new Configuration();
				// Creating String template loader
				StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
				// adding template name with template string
				stringTemplateLoader.putTemplate(templateName, templateStr);
				// adding stringTemplateLoader to configuration
				configuration.setTemplateLoader(stringTemplateLoader);
				// getting final template
				template = configuration.getTemplate(templateName);
				// preparing final content by replacing replacable values
				content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			} catch (Exception e) {
				throw new VEMAppException("ERR-TODO", "Error occurred while prepare content from String", logger, e);
			}
			return content;
		}
	};
	
	UploadUserTemplateThread(){
		
	}
	
	List<User> usersList;
	public UploadUserTemplateThread(List<User> usersList){
		this.usersList = usersList;
	 }

	@Override
	public void run() {
		
		try {
			System.out.println("inside try run() usersList  ::: "+usersList);
			boolean sentEmail = sendEmail(usersList);
		} catch (VEMAppException e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean sendEmail(List<User> usersList) throws VEMAppException {
		// Checking for the user object is opted to send mail creating user or not
		// Initializing flag to send mail or not
		boolean mailFlag = false;
		for(User user: usersList){
		if (user.getIsOptedToSendMail() == 1) {

			// setting the template variable as map key
			Map<String,String> templateMap = new HashMap<>();
			String userName = user.getEmailId();
			String userFname = user.getFirstName();
			String userLname = user.getLastName();
			
			templateMap.put(CommonConstants.USER_FNAME_C, userFname);
			templateMap.put(CommonConstants.USER_LNAME_C, userLname);
			templateMap.put(CommonConstants.USER_NAME, userName);

			// Creating broadcast object to send mail related details to publisher
			MailBroadCast broadCast = new MailBroadCast();
			// Generating random key / auth token
			String randomKey = RandomStringUtils.randomAlphanumeric(30);
			System.out.println("####mail sent randomKey###" + randomKey);

			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			
			URL url = null;
			try {
				url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Preparing URL to create new password
			String resetLink = url+"/firstTimePassword/?token="+randomKey;
			
			// Adding link to forgot password template
			templateMap.put("resetLink", resetLink);
			// Getting subject from properties and adding to broadcast object
			broadCast.setSubject(ConfigurationUtils.getConfig(CommonConstants.NEW_MAIL_SUBJECT));
			// Preparing mail content from free marker template 
			broadCast.setMailText(itemplateUtil.prepareContentFromTemplateString(ConfigurationUtils.getConfig(CommonConstants.NEW_MAIL_CONTENT),templateMap));
			broadCast.setToEmail(user.getEmailId());
			
			// Instantiating mail publisher
			MailPublisher publisher = new MailPublisher();
			
			 long startTime = System.currentTimeMillis();
			// Instantiating mail publisher
			 mailFlag = publisher.publishEmail(broadCast);
			  long stopTime = System.currentTimeMillis();
			  long elapsedTime = stopTime - startTime;
			  System.out.println("####mail sent elapsedTime###" + elapsedTime);
			
		}
		}
		return mailFlag;
}

}
