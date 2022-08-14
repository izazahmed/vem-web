/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util;
/**
 * File Name : CommonConstants 
 * CommonConstants: Contains all the constant values
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2.1.0
 * @date        28-07-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	28-07-2016			Nagarjuna Eerla		File Created
 * 01	23-08-2016			Nagarjuna Eerla		Added AdminDaoImpl common repeated constants
 *
 */
public class CommonConstants {
	
	/**
	 * Application statuses
	 * 
	 * @author neerla
	 *
	 */
	public enum AppStatus {
		FAILURE, SUCCESS
	}
	
	/**
	 * DbFlags: will contains the DB strings, these will use in code to validate
	 * @author neerla
	 *
	 */
	public enum DbFlags {
		CUSTOMER_CODE_ALREADY_EXISTS, ERROR, INCREMENTED_FAIL_COUNT, INVALID_TOKEN, LOGIN_SUCCESS, MAX_ATTEMPTS_OVER_FOR_SECURITY_QUESTIONS, USER_GOT_LOCKED,VALIED_SECURITY_QUESTIONS
	}
	
	public enum ReportPreferences {
		Weekly, Monthly,Quarterly,Yearly,None
	}
	
	public enum AlertPreferences {
		Email,SMS,NONE
	}
	
	/**
	 * It fetches the user details by different types
	 * @author neerla
	 *
	 */
	public enum FetchUserDetailsByType {
		ALL, EMAIL,TOKEN,USER_ID
	}
	
	/**
	 * it holds the permission levels
	 * @author neerla
	 *
	 */
	public enum PermissionLevels {
		READ_ONLY, READ_WRITE
	}
	
	public static final String ALERT_PREFERENCE_C = "alertPreference";
	
	public static final String CUSTOMER_ERROR_MSG_L = "error_msg";
	public static final String CUSTOMER_L = "var_customer_id";
	
	/** Default date format applicable across the application */
	public static final String DATE_DEFAULT_FORMAT = "MM/dd/yyyy HH:mm:ss";
	
	public static final String ERROR_MSG = "error_msg";
	public static final String ERROR_MSG_L = "error_msg";
	public static final String ERROR_OCCURRED = "Error occurred:";
	public static final String ERROR_SESSION_INVALID="Session got invalidated";
	public static final String INPUT = "input ";
	public static final String INVALID_ROLE_ID_MESSAGE = "Role id is 0.";
	
	/**
	 * Parameters constant
	 */
	public static final String PARAMETERS = "Parameters:";
	public static final String PASSWORD_L = "password";
	public static final String PERMISSIONS = "permissions";
	public static final String NEW_MAIL_CONTENT = "new.mail.content";
	public static final String NEW_MAIL_SUBJECT = "new.mail.subject";
	public static final String RESET_MAIL_CONTENT = "reset.mail.content";
	public static final String RESET_MAIL_SUBJECT = "reset.mail.subject";
	public static final String LOCK_MAIL_CONTENT = "lock.mail.content";
	public static final String LOCK_MAIL_SUBJECT = "lock.mail.subject";
	public static final String UPDATE_PROFILE_MAIL_CONTENT = "update.profile.mail.content";
	public static final String UPDATE_PROFILE_MAIL_SUBJECT = "update.profile.mail.subject";
	public static final String UPDATE_PASSWORD_MAIL_CONTENT = "update.password.mail.content";
	public static final String UPDATE_PASSWORD_MAIL_SUBJECT = "update.password.mail.subject";
	public static final String DELETE_MAIL_CONTENT = "deactivate.mail.content";
	public static final String DELETE_MAIL_SUBJECT = "deactivate.mail.subject";
	
	public static final String ROLE_ID = "role_id";
	public static final String simpleJdbcCallResult = "simpleJdbcCallResult :: ";
	/**
	 * Stored procedure prefix constant string
	 */
	public static final String SP_PREFIX_CONSTANT = "Executing Stored Procedure :: ";
	public static final String spOutputUserId = "SP OUTPUT UserId :";

	public static final String YOU_HAVE_TWO_MORE_ATTEMPTS = "You have two more attempts before your account is locked.";
	public static final String THIS_IS_YOUR_LAST_ATTEMPT = "This is your last attempt before your account is locked.";
	public static final String YOUR_ACCOUNT_IS_LOCKED_NEXT_24 = "Your account is locked for the next 24 hours. <br />Please contact EnerAllies at 1-888-770-3009 x300 or <br />support@enerallies.com for help.";
	public static final String YOUR_ACCOUNT_IS_LOCKED = "Your account has been locked. <br />Please contact EnerAllies at 1-888-770-3009 x300 or <br />support@enerallies.com for help.";
	public static final String CSO_USER_TYPE = "Unknown user account";
	public static final String CONTACT_EAI_HELP = "Please contact EnerAllies at 1-888-770-3009 x300 or support@enerallies.com for help.";
	
	public static final String UPDATE_MAIL_CONTENT = "update.mail.content";
	public static final String UPDATE_MAIL_SUBJECT = "update.mail.subject";
	public static final String UPDATEDBY_C = "updatedBy";
	public static final String UPDATEDDATE_C = "updatedDate";
	// camel case constants
	public static final String USER_FNAME_C = "userFname";
	
	public static final String USER_LNAME_C = "userLname";
	public static final String USER_NAME = "userName";
	public static final String USER_PHONE_C = "userPhone";
	public static final String USERID_C = "userId";
	// camel case constants
	public static final String USERID_L = "userid";
	

	// device level keys
	public static final String DEVICE_KEY_TEMP_HOLD="temp_hold";
	public static final String DEVICE_KEY_TSTAT_MODE="tstat_mode";
	public static final String DEVICE_KEY_ZONE_TEMP="zone_temp";
	//public static final String DEVICE_KEY_STORE_TIME="store_time";
	public static final String DEVICE_KEY_COOL_SET="cool_set";
	public static final String DEVICE_KEY_HEAT_SET="heat_set";
	public static final String DEVICE_KEY_DEVICE="xcspec_device_id";
	public static final String DEVICE_KEY_MACID="macId";
	public static final String DEVICE_MESSAGE_VALUE="tstat_msg";
	public static final String DEVICE_DATE_TIME="datetime";
	public static final String DEVICE_RELAY_1="relay1";
	public static final String DEVICE_RELAY_2="relay2";
	public static final String DEVICE_RELAY_3="relay3";
	public static final String DEVICE_RELAY_4="relay4";
	public static final String DEVICE_RELAY_5="relay5";
	public static final String DEVICE_RELAY_6="relay6";
	public static final String DEVICE_RELAY_7="relay7";
	public static final String DEVICE_RELAY_STATE="relay_state";
	public static final String DEVICE_FAN_STATE="fan_state";
	public static final String DEVICE_OP_STATE="op_state";
	public static final String DEVICE_CURRENT_CLOCK="tstat_clock";
	public static final String DEVICE_CURRENT_DAY="current_day";
	public static final String DEVICE_CURRENT_TIME="current_time";
	public static final String DEVICE_BUTTON_PRESSED="button_pressed";
	
	// Result set key
	public static final String RESULT_SET_1 = "#result-set-1";
	public static final String RESULT_SET_2 = "#result-set-2";
	public static final String RESULT_SET_3 = "#result-set-3";
	public static final String RESULT_SET_4 = "#result-set-4";
	public static final String RESULT_SET_5 = "#result-set-5";
	
	// sign in constants
	public static final String DASHBOARD_REDIRECT = "redirect:/dashboard";
	public static final String LOGIN = "login";
	public static final String INVALID_CREDENTIALS = "Invalid credentials";
	public static final String FAIL_TO_VALIDATE_USER = "Failed to validate user details";
	public static final String USER_IS_INACTIVE = "Your account has been set to inactive. <br /> Please contact EnerAllies at 1-888-770-3009 x300 or <br /> email support@enerallies.com for help";
	public static final String USER_LOCKED = "User got locked";
	public static final String CHECK_MAIL_FOR_INSTRUCTIONS = "Please check your email for instructions on how to create your first time password.";
	public static final String EXPIRED_MAIL_FOR_INSTRUCTIONS = "The password for your account hasn't been set up yet. Please contact EnerAllies at 1-888-770-3009 x300 or <br /> email support@enerallies.com for help";
	public static final String INVALID_USERNAME = "Unknown email address";
	public static final String USER_NAME_NOT_EMPTY = "User name should not be empty";
	public static final String PSWD_NOT_EMPTY = "Password should not be empty";
	public static final String ERROR_WHILE_DOING_REQUEST = "Error occured while doing your request";
	public static final String RESENT_PSWD_DETAILS_SENT_MAIL = "A reset link has been sent to you. Please check your inbox and click the reset link in the email. <br />If you don't receive an e-mail please contact support@enerallies.com or call 1-888-770-3009 x300.";
	public static final String REQUEST_COMPLETED = "Successfully completed your request please create new password";
	public static final String LINK_ALREADY_IN_USE = "A reset link has already been sent to you. Please check your inbox and click the reset link in the email. <br/>If you don't receive an e-mail please contact support@enerallies.com or call 1-888-770-3009 x300.";

	
	// Adding private constructor to hide implicit public one
	private CommonConstants(){
		
	}
	
	/**
	 * 1 - "Comfort Optimization On - No Devices"
	 * 2 - "Comfort Optimization On - Non Operational/Degraded"
	 * 3 - "Comfort Optimization On - Operational"
	 * 4 - "Comfort Optimization Off - No Devices"
	 * 5 - "Comfort Optimization Off - Non Operational/Degraded"
	 * 6 - "Comfort Optimization Off - Operational"
	 */
	public static final String COMFORT_OPTIMIZATION_ON_NO_DEVICES = "1"; 
	public static final String COMFORT_OPTIMIZATION_ON_NON_OPERATIONAL_DEGRADED = "2";
	public static final String COMFORT_OPTIMIZATION_ON_OPERATIONAL = "3"; 
	public static final String COMFORT_OPTIMIZATION_OFF_NO_DEVICES = "4"; 
	public static final String COMFORT_OPTIMIZATION_OFF_NON_OPERATIONAL_DEGRADED = "5";
	public static final String COMFORT_OPTIMIZATION_OFF_OPERATIONAL = "6";
	
	public static final String COMFORT_OPTIMIZATION_ON_NO_DEVICES_STR = "No Devices";
	public static final String COMFORT_OPTIMIZATION_ON_NON_OPERATIONAL_DEGRADED_STR = "Non Operational/Degraded"; 
	public static final String COMFORT_OPTIMIZATION_ON_OPERATIONAL_STR = "Operational"; 
	public static final String COMFORT_OPTIMIZATION_OFF_NO_DEVICES_STR = "No Devices"; 
	public static final String COMFORT_OPTIMIZATION_OFF_NON_OPERATIONAL_DEGRADED_STR = "Non Operational/Degraded";
	public static final String COMFORT_OPTIMIZATION_OFF_OPERATIONAL_STR = "Operational";
	
	public static final String DEGRADED_HVAC_UNITS = "DEGRADED HVAC UNITS";
	public static final String WITHIN_SETPOINTS = "WITHIN SETPOINTS";
	public static final String HVAC_USAGE = "HVAC USAGE";
	public static final String MANUAL_CHANGES = "MANUAL CHANGES";
	
	public static final String REPORTS = "Reports";
	public static final String SITE_SURVEY = "Site Survey";
	public static final String OVERALL_CUSTOMER = "Overall Customer Data Summary";
	public static final String OVERALL_GROUP = "Overall Group Data Summary";
	public static final String OVERALL_SITE = "Overall Site Data Summary";
	public static final String TRENDING_DEVICE = "Trending Device Data";
	public static final String HVAC_STAGES = "HVAC Stages vs. Degree Days";
	public static final String HVAC_SYSTEM_STATUS = "HVAC System Status";
	
	public static final String HVAC_MODE_OFF = "HVAC Mode Off";
	public static final String THERMOSTAT_OFFLINE = "Thermostat Offline";
	public static final String SETPOINT_NOT_REACHED = "Setpoint Not Reached";
	
	public static final String RED_COLOR_CODE = "#DD2133";
	public static final String ISSUE_CNT = "issue_cnt";
	public static final String TOTAL_COUNT = "totalcount";
	public static final String GROUP_DATA = "groupdata";
	public static final String SITE_DATA = "sitedata";
	public static final String USER_DATA = "userdata";
	public static final String DEVICE_DATA = "devicedata";
	public static final String SESSION_USER = "eaiUserDetails";
	public static final String CUSTOMERS = "CUSTOMERS";
	public static final String GROUP = "GROUP";
	public static final String SITE = "SITE";
	
	// Site servay attributes
	public static final String TOTAL_RTU_DATA = "rtudata";
	public static final String SCHEDULE_DATA = "scheduledata";
	public static final String POWER_OF_RTU = "powerofrtu";
	public static final String AC_FAILURE = "acfailure";
	public static final String HEATER_FAILURE = "heaterfailure";
	public static final String AC_RUN_DEGREDED = "acrundegreded";
	public static final String HEATER_RUN_DEGREDED = "heaterrundegreded";
	public static final String NOT_VALID_RECORD = "Not a valid record";
	public static final String RESULT = "Result";
	
	public enum SITE_IMPORT_WEEK_DAYS {
		SUN, MON, TUE, WED, THU, FRI, SAT
	}	
}
