/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.scheduler;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import com.enerallies.vem.business.WundergroundBusiness;



/**
 * File Name : WUndergroundJob 
 * WUndergroundJob Class is used implement the Quartz Job
 *
 * @author (Y Chenna Reddy – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     1.0
 * @date        29-07-2016
 *
 * MOD HISTORY
 * 
 * DATE				USER             		COMMENTS
 * 
 * 29-07-2016		Y Chenna Reddy			File Created
 *
 */
public class WUndergroundJob implements Job {
	
	// Getting logger	
    private static final Logger logger = Logger.getLogger(WUndergroundJob.class);
    
    /**
     * Method Name: execute
     * Implementation of execute method of Job
     * 
     * @param JobExecutionContext
     */
	public void execute(JobExecutionContext arg0) {
		try {
			 logger.info("----------Scheduler cycle started-------------"+ new Date());
			 // Instantiate WundergroundBusiness
			 WundergroundBusiness wundergroundBusiness = new WundergroundBusiness();
			 // Call processWundergroundAPI method of WundergroundBusiness
			 wundergroundBusiness.processWundergroundAPI("");
		} catch (Exception exception) {
			logger.error("Exception while calling  processWundergroundAPI method of WundergroundBusiness", exception);
			exception.printStackTrace();
		}
		logger.info("----------Scheduler cycle finished-------------");
	}

}
