/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util.template;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.enerallies.vem.exceptions.VEMAppException;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * File Name : FreemarkerTemplateUtil 
 * FreemarkerTemplateUtil: is the free marker template implementation
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2.1.0
 * @date        04-08-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	04-08-2016			Nagarjuna Eerla		File Created
 *
 */
@Component
public class FreemarkerTemplateUtil implements ITemplateUtil {
	// Getting logger
	private static final Logger logger = Logger.getLogger(FreemarkerTemplateUtil.class);
	
	@Override
	public String prepareContentFromTemplateString(String templateStr, Object model) throws VEMAppException {
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

}
