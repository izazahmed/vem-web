/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.enerallies.vem.beans.common.ConvertJsonBeanToDAOBean;
/**
 * File Name : DatesUtil 
 * DatesUtil: Utility class for Dates
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        04-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 04-08-2016		Nagarjuna Eerla		File Created
 * 21-10-2016		Rajashekharaiah M   Added method getCurrentUTCDateTime
 */
public class DatesUtil{

	// Getting logger
	private static final Logger logger = Logger.getLogger(ConvertJsonBeanToDAOBean.class);
	

	/**
	 * Return date into given date format
	 * 
	 * @param format
	 * @param date
	 * @return
	 */
	public static String getDateFormat(String format, Date date) {
		
		if(date == null)
			return null;
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}
	
	
	/**
	 * Method to format Sql Date with the provided date format
	 * @param date
	 * @return formatted date
	 */
	public static String getSqlDateFormat(java.sql.Date date) {
		if(date == null)
			return null;
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CommonConstants.DATE_DEFAULT_FORMAT);
		return simpleDateFormat.format(date);
	}
	

	/**
	 * Get current Date and time
	 * 
	 * @return date
	 */
	public static Date getCurrentDateAndTime() {
		return new Date();
	}
     
     /**
      * Method to parse date string to date object
      * @param dateString
      * @param dateFormat
      * @return date
      * @throws Exception
      */
     public static Date convertStringToDate(String date,String dateFormat)
     						throws Exception{
         DateFormat df = new SimpleDateFormat(dateFormat);
         return df.parse(date);         
     }
    
    
     /**
      * convertStringToString: Converting one form of String date to another form of String date
      * 
      * @param date
      * @param dateFormat
      * @param returnFormat
      * @return
      */
    public static String convertStringToString(String date,String dateFormat,String returnFormat){
        String dt1 = null;
        try {
            DateFormat df = new SimpleDateFormat(dateFormat);
            Date dt = df.parse(date);
            DateFormat simpleDateFormat = new SimpleDateFormat(returnFormat);
            dt1 = simpleDateFormat.format(dt);            
        }
        catch (ParseException e) {
            logger.error("Error occurred - Ignore",e);
        }       
        return dt1;
    }
    
    /**
     * 
     * convertTimestampToString : It converts the time stamp to required date format
     * 
     * @param timeStamp
     * @param dateFormat
     * @return
     */
    public static String convertTimestampToString(String timeStamp,String dateFormat){

    	/*
    	 * Converting date if it is not a blank value
    	 */
    	if(StringUtils.isNotBlank(timeStamp)){
    		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt = null;
			try {
				dt = df.parse(timeStamp);
			} catch (ParseException e) {
				logger.error("[DatesUtil][convertTimestampToString][ERROR] Error occured while parsing "+e);
			}
            DateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            return simpleDateFormat.format(dt);
    		
    	}else{
    		return timeStamp;
    	}
    	
    }
    /*
	    public static void main(String[] args){
	    	DatesUtil date = new DatesUtil();
	    	System.out.println(date.convertTimestampToString(null, CommonConstants.DATE_DEFAULT_FORMAT));
	    }
    */
    
    /**
     * To get the current UTC time in date time format and it converts to format MM/dd/yyyy HH:mm:ss
     * @return
     */
    public static String getCurrentUTCDateTime(){
    	
    	/**Current UTC time*/
		DateTime dt = new DateTime(DateTimeZone.UTC);
		
		/**formatter to convert to MM/dd/yyyy HH:mm:ss*/
		DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
		
		String dateTime = dtf.print(dt);
		logger.info("Current UTC date time : " +dateTime);
    	return dateTime;
    }
    
    /**
     * Used to convert the given time string into desired time string by using 
     * The given timezone.
     * @param timestamp
     * @param timezone
     * @return String
     */
    public static String convertTimeWithTimezone(String timestamp, String timezone){
    	DateFormat parseFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); 
    	Date dt = null;
    	String newDateString = null;
    	try {
    		dt = parseFormat.parse(timestamp);
    		parseFormat.setTimeZone(TimeZone.getTimeZone(timezone)); 
	    	newDateString = parseFormat.format(dt);
	    	logger.info("convertTimeWithTimezone : newDateString:"+newDateString);
    	}catch (ParseException e) {
    		logger.error("convertTimeWithTimezone : ERROR" ,e);
    	}
    	return newDateString;
    }
}
