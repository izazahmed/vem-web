/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * File Name : WUndergroundScheduler 
 * WUndergroundScheduler Class is used schedule the job
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
public class WUndergroundScheduler {
	// Getting logger	
	final static Logger logger = Logger.getLogger(WUndergroundScheduler.class);
		
	/**
	 * Method Name: runScheduler()
	 * This method is used to schedule the Quartz job
	 */
	public void runScheduler() {
		try {
			// Instantiate the Scheduler Factory
			SchedulerFactory sf = new StdSchedulerFactory();
			// Getting the Scheduler
			Scheduler schdulerObj = sf.getScheduler();
			// Getting/Creating the JobDetail
			JobDetail wUndergroundJob = newJob(WUndergroundJob.class).withIdentity("WUnderground", "WUnderground_Group")
					.build();
			logger.info("------------------ Job Created " + new Date());
			// Getting/Creating the SimpleTrigger
			SimpleTrigger wUndergroundTrigger = (SimpleTrigger) newTrigger()
					.withIdentity("WUnderground", "WUnderground_Group")
					.startAt(DateBuilder.futureDate(0, IntervalUnit.SECOND))
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							// .withIntervalInSeconds(Integer.parseInt(prop.getProperty("RSD_SCHDR_INTVL")))
							.withIntervalInSeconds(5).repeatForever()
							.withMisfireHandlingInstructionNextWithExistingCount())
					.forJob(wUndergroundJob).build();
			
			// Scheduling the Job
			schdulerObj.scheduleJob(wUndergroundJob, wUndergroundTrigger);
			// Starting the Schedule
			schdulerObj.start();
			logger.info("Wunderground Scheduler Started " + new Date());
			// schdulerObj.shutdown();
		} catch (Exception exception) {
			logger.error("Exception while scheduling the job in WundergroundScheduler", exception);
			exception.printStackTrace();			
		}
	}
}
