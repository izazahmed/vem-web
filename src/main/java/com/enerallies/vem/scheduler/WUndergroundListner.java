/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.scheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * File Name : WUndergroundListner 
 * WUndergroundListner Class is used implement ServletContextListener 
 * to start the quartz job when server starts
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
public class WUndergroundListner implements ServletContextListener {
	
	// Getting logger	
	final static Logger logger = Logger.getLogger(WUndergroundListner.class);
	
	/**
	 * Method Name: contextDestroyed
	 */	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("ServletContextListener destroyed");
	}
	
	/**
	 * Method Name: contextInitialized
	 * Run this before web application is started
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("----- ServletContextListener started -----");
		try {
			// Instantiate WUndergroundScheduler and calling the runScheduler method 
			new WUndergroundScheduler().runScheduler();
		} catch (Exception exception) {
			logger.error(exception);
			exception.printStackTrace();
		}
	}
}
