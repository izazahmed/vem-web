/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.validators.JsonBeanValidator;

/**
 * File Name : ConfigurationUtils 
 * ConfigurationUtils is helper class to implement util methods
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2.1.0
 * @date        21-07-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	21-07-2016			Nagarjuna Eerla		File Created
 * 02	21-07-2016			Nagarjuna Eerla		Added getConfig method
 * 03	03-08-2016			Nagarjuna Eerla		Added validateBeans method
 * 04	18-08-2016			Nagarjuna Eerla		Added comparePasswords method
 * 05	18-08-2016			Nagarjuna Eerla		Added generateRandomPassword
 * 06	18-08-2016			Nagarjuna Eerla		Added generateEncryptedPassword
 * 07	24-08-2016			Nagarjuna Eerla			Resolved the Major sonar Qube Issues
 */
public class ConfigurationUtils {
	
	// Adding private constructor to hide implicit public one
	private ConfigurationUtils(){
		
	}
	// Getting logger
	private static final Logger logger = Logger.getLogger(ConfigurationUtils.class);
	
	private static SecureRandom random = new SecureRandom();
	
	/** propertyFile will holds the config.properties */
    static String propertyFile = "config.properties";

    /**
     * getConfig method returns the value of the specified string 
     * 
     * @param name
     * @return
     */
    public static String getConfig(String name) {
    	
    	/** Properties instance */
        Properties prop = new Properties();
        
        /** reading the property file and assigning to input stream */
        InputStream stream = ConfigurationUtils.class.getClassLoader().getResourceAsStream(propertyFile);
        
        /** checking stream */
        if (stream == null) {
            return null;
        }
        try {
            prop.load(stream);
        } catch (IOException e) {
        	logger.error("[ERROR][getConfig][Utils Layer] : "+e);
            return null;
        }
        /** storing the value of the property */
        String value = prop.getProperty(name);
        
        /** checking property value */
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return value;
        }
    }

    /**
     * generateRandomPassword: Will generates alpha numeric random password with 8 characters
     * @return
     */
    public static String generateRandomPassword(){
    	return RandomStringUtils.randomAlphanumeric(8);
    }
    

    /**
     * generateRandomPassword : Will generates Encrypted alpha numeric random password with 8 characters
     * @param saltKey
     * @return
     * @throws VEMAppException 
     */
    public static String generateRandomPassword(String saltKey) throws VEMAppException{
    	String encryptedPassword;
			try {
				encryptedPassword=PasswordEncryptionProviderUsingPBKDF2
						.generateStorngPasswordHash(saltKey.split("@")[0], RandomStringUtils.randomAlphanumeric(8));
			} catch (VEMAppException e) {
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_ENCRYPT_PASSWORD_FAILED, logger, e);
			}
    	
    	return encryptedPassword;
    }
    
    /**
     * generateEncryptedPassword: will generates encrypted password with supplied password
     * 
     * @param saltKey
     * @param password
     * @return
     * @throws VEMAppException
     */
    public static String generateEncryptedPassword(String saltKey, String password) throws VEMAppException{
    	String encryptedPassword;
			try {
				encryptedPassword=PasswordEncryptionProviderUsingPBKDF2
						.generateStorngPasswordHash(saltKey.split("@")[0], password);
			} catch (VEMAppException e) {
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_ENCRYPT_PASSWORD_FAILED, logger, e);
			}
    	
    	return encryptedPassword;
    }
    
    /**
     * Checking bean properties
     * @param bean
     * @return
     */
    public static <T> ValidatorBean validateBeans(T bean){
    	JsonBeanValidator jsonBeanValidator = new JsonBeanValidator();
    	return jsonBeanValidator.getViolatedMessages(bean);
    }
    
    /**
     * comparePasswords : It compares new password and repeated password
     * 
     * @param newPasssword
     * @param repeatedPassword
     * @return
     */
    public static boolean comparePasswords(String newPasssword, String repeatedPassword){
    	boolean flag = false;
    	if(StringUtils.equals(newPasssword, repeatedPassword)){
    		flag = true;
    	}
    	return flag;
    }
    

	/**
	 * validateAlertPreference : is to validate phone number over alert preference "Text" 
	 * 
	 * @param alertPreference
	 * @param phoneNumber
	 * @return
	 */
	public static boolean validateAlertPreference(String alertPreference, String phoneNumber){
		
		boolean flag = false;
		
		// Checking whether the alert preference is empty or not 
		if(StringUtils.isNotEmpty(alertPreference)){
			
			// Checking whether the alert preference contains "Text" alert preference or not 
			boolean textFlag = alertPreference.contains("Text");
			if(textFlag){
				if(StringUtils.isNotEmpty(phoneNumber)){
					flag = false;
				}else{
					flag = true;
				}
			}
		}

		return flag;
		
	}
	
	/**
	 * getHostAddress : It returns host IP address
	 * @return
	 */
	public static String getHostAddress(){
		String hostAddress = "";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			logger.error("[ERROR][getHostAddress][Util Layer]"+e);
		}
		return hostAddress;
	}
	
	/**
	 * validateSession method is for validating session
	 * @param session; HttpSession
	 * @return boolean
	 */
	public static boolean validateSession(HttpSession session){
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		boolean sessionFlag = false;
		
		try {
			
			// checking for if session is valid or not
			if(userDetails != null){
				sessionFlag = true;
			}
					
		} catch (Exception e) {
			logger.error("[ERROR][validateSession]",e);
			sessionFlag=false;
		}
		return sessionFlag;

	}

	public static String generateRandomString() {
		return new BigInteger(130, random).toString(32);
	}

	/**
	 * isZero: is used to check if the value is zero or not 
	 * 
	 * @param value
	 * @param threshold
	 * @return
	 */
	public static boolean isZero(double value, double threshold){
	    return value >= -threshold && value <= threshold;
	}
	
	/**
	 * storeHoursContainsCheck: Compare week days for store hours
	 * @param test
	 * @return
	 */
	public static boolean storeHoursContainsCheck(String test) {

	    for (CommonConstants.SITE_IMPORT_WEEK_DAYS s : CommonConstants.SITE_IMPORT_WEEK_DAYS.values()) {
	        if (s.name().equalsIgnoreCase(test)) {
	            return true;
	        }
	    }

	    return false;
	}
	
	/*public static void main(String[] args){
		try {
			System.out.println(StringUtils.equals(generateEncryptedPassword("neerla@ctepl.com", "admin"),"88bfac2146c483a3118d85b8683605b6efd6e3b41d6a5e234bde2d70e6de64d6bce90fb94ec0eb5a289d4518b26f5a541d623481d0587c61b66e8a209d3ab58410006e6565726c61"));
		} catch (VEMAppException e) {
			logger.error(e);
		}
	}*/
	
}
