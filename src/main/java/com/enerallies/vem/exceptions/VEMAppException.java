/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.exceptions;

import org.apache.log4j.Logger;
/**
 * File Name : VemAppException 
 * 
 * VemAppException: will handles application level exceptions
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 03-08-2016		Nagarjuna Eerla		File Created
 * 24-08-2016		Nagarjuna Eerla			Resolved the Major sonar Qube Issues
 *
 */
@SuppressWarnings("serial")
public class VEMAppException extends Exception{
	String msg;
	String code;	
	
	/**
	 * Constructor with error code, message, logger and exception as parameters
	 * @param code
	 * @param msg
	 * @param logger
	 * @param th
	 */
	public VEMAppException(String code, String msg, Logger logger, Throwable th) { 
		super(code+"_"+msg);
		this.code = code;
		this.msg = msg;
		logger.error("Code : "+code + "  Msg Code : "+msg, th);
	}
	
	/**
	 * Constructor with message as parameter
	 * 
	 * @param message
	 */
	public VEMAppException(String message) {
		super(message);
		this.msg = message;
	}
	
	/**
	 * Constructor with message and exception as parameters
	 * @param message
	 * @param e
	 */
	public VEMAppException(String message, Throwable e) {
		super(message, e);
		this.msg = message;
	}
	
	/**
	 * Constructor with message as parameter
	 * @param message
	 */
	public VEMAppException(VEMAppException message) {
		super(message);
	}
	
	/**
	 * Constructor with error code and message as parameters
	 * @param code
	 * @param msg
	 */
	public VEMAppException(String code, String msg) {
		super(code+"_"+msg);
		this.code = code;
		this.msg = msg;
	}

	@Override
	public String toString() {
		return this.code;
	}
	
	@Override
	public String getMessage() {
		return code+"_"+msg;
	}

	/**
	 * Gets the supplied error message
	 * 
	 * @return
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Gets the supplied error code
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}
}
