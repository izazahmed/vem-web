/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util;

/**
 * File Name : ErrorCodes 
 * 
 * ErrorCodes: It contains all application error codes information
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 03-08-2016		Nagarjuna Eerla		File Created
 * 05-08-2016		Nagarjuna Eerla		Added Error codes for save user
 * 08-08-2016		Nagarjuna Eerla		Added Error codes for update user
 * 09-08-2016		Nagarjuna Eerla		Added Error codes for delete user
 * 10-08-2016		Goush Basha		    Added Add Role related constants.
 * 11-08-2016       Goush Basha	        updateRole() method  related constants.
 * 12-08-2016		Nagarjuna Eerla		Added WARN bean Validation messages
 * 18-08-2016		Nagarjuna Eerla		Added Error codes for filter by status.
 * 30-08-2016       Goush Basha			Sprint-2 Suggestions/Changes.
 * 02-09-2016       Goush Basha			Added the Site module related error codes(Sprint-3).
 * 07-09-2016       Rajashekharaiah M	Added device related error codes.
 * 09-09-2016       Goush Basha     	Added the look up related error codes(Sprint-3).
 * 14-09-2016       Goush Basha     	Added get site related error codes(Sprint-3).
 * 19-09-2016		Goush Basha		    Added checkSiteInternalId() related error codes (Sprint-3).
 */
public class ErrorCodes {

	
	// Adding private constructor to hide implicit public one
	private ErrorCodes(){
		
	}
	
	/** General Error Codes */
	public static final String INVALID_JSON = "ERR_INVALID_JSON_1001";
	public static final String GENERAL_APP_ERROR = "ERR_GEN_1002";
	public static final String INVALID_SESSION = "ERR_SESSION_1003";
	
	/** User related Error Codes */
	public static final String USER_ERROR_SAVE_FAILED = "ERR_USER_2001";
	public static final String USER_INFO_SAVE_SUCCESS = "INFO_USER_2002";
	public static final String USER_INFO_FORGOT_PWD_SUCCESS = "INFO_USER_2003";
	public static final String USER_ERROR_FORGOT_PWD_FAILED = "ERR_USER_2004";
	public static final String USER_INFO_CHANGE_PWD_SUCCESS = "INFO_USER_2005";
	public static final String USER_ERROR_CHANGE_PWD_FAILED = "ERR_USER_2006";
	public static final String USER_INFO_CURRENT_PWD_SUCCESS = "INFO_USER_2007";
	public static final String USER_ERROR_CURRENT_PWD_FAILED = "ERR_USER_2008";
	public static final String USER_ERROR_INVALID_FAILED = "ERR_USER_2009";
	public static final String USER_INFO_GET_USER_SUCCESS = "INFO_USER_2010";
	public static final String USER_ERROR_GET_FAILED = "ERR_USER_2011";
	public static final String USER_ERROR_UPDATE_FAILED = "ERR_USER_2012";
	public static final String USER_INFO_UPDATE_SUCCESS = "INFO_USER_2013";
	public static final String USER_ERROR_DELETE_FAILED = "ERR_USER_2014";
	public static final String USER_INFO_DELETE_SUCCESS = "INFO_USER_2015";
	public static final String USER_ERROR_ACTIVATE_FAILED = "ERR_USER_2016";
	public static final String USER_INFO_ACTIVATE_SUCCESS = "INFO_USER_2017";
	public static final String USER_ERROR_ACTIVITY_FAILED = "ERR_USER_2018";
	public static final String USER_INFO_ACTIVITY_SUCCESS = "INFO_USER_2019";
	public static final String USER_ERROR_FILTER_BY_STATUS_FAILED = "ERR_USER_2020";
	public static final String USER_INFO_FILTER_BY_STATUS_SUCCESS = "INFO_USER_2021";
	public static final String USER_ERROR_FILTER_BY_ROLE_FAILED = "ERR_USER_2022";
	public static final String USER_INFO_FILTER_BY_ROLE_SUCCESS = "INFO_USER_2023";
	public static final String USER_ERROR_ENCRYPT_PASSWORD_FAILED = "ERR_USER_2022";
	public static final String USER_ERROR_PASSWORD_NOT_MATCH_FAILED = "ERR_USER_2023";
	public static final String USER_ERROR_INVALID_EMAIL_ADDRESS_FAILED = "ERR_USER_2100";
	public static final String USER_ERROR_RELEASE_LOCK_FAILED = "ERR_USER_2101";
	public static final String USER_ERROR_CURRENT_PWD_DOES_NOT_MATCH_FAILED = "ERR_USER_2102";
	public static final String USER_ERROR_USER_VALIDATE_FAILED = "ERR_USER_2039";
	public static final String USER_ERROR_CHECK_IS_LOCKED_FAILED = "ERR_USER_2040";
	public static final String USER_INFO_USER_VALIDATE_SUCCESS = "ERR_USER_2041";
	public static final String USER_ERROR_IN_VALID_FAILURE = "ERR_USER_2042";
	public static final String USER_ERROR_EMAIL_EXIST_FAILED = "ERR_USER_2043";
	public static final String USER_ERROR_INVALID_UNAME_PHONE_FAILED = "ERR_USER_2044";
	public static final String USER_ERROR_UPDATE_PROFILE_FAILED = "ERR_USER_2045";
	public static final String USER_INFO_UPDATE_PROFILE_SUCCESS = "INFO_USER_2046";
	public static final String USER_ERROR_UPLOAD_IMAGE_EMPTY = "ERR_FILE_EMPTY_2047";
	public static final String TOKEN_EXPIRY_CHECK_FAILED = "ERR_FILE_EMPTY_2048";
	public static final String USER_UPLOAD_SUCCESS = "INFO_USER_UPLOAD_2000"; 
	public static final String USER_UPLOAD_RECORDS_FAILED = "ERR_USER_UPLOAD_RECORDS_2000";
	public static final String USER_UPLOAD_RECORD_FAILED = "ERR_USER_UPLOAD_RECORD_2000";
	public static final String USER_UPLOAD_FAILED = "ERR_USER_UPLOAD_2000";
	public static final String USER_UPLOAD_WRONG_FORMAT_FAILED = "ERR_USER_UPLOAD_FORMAT_2000";
	public static final String UPLOAD_INFO_APP_IMAGE_SAVE_SUCCESS = "INFO_USER_2501";
	public static final String USER_ERROR_UPDATE_EMAIL_FAILED = "ERR_USER_2502";		
	public static final String USER_INFO_UPDATE_EMAIL_SUCCESS = "INFO_USER_2503";	
	
	/** Role related Error Codes */
	public static final String ADD_ROLE_FAILD_ERROR = "ERR_ROLE_2001";
	public static final String ADD_ROLE_SUCCESS = "INFO_ROLE_2002";
	public static final String GET_ROLE_DETAILS_FAILED_ERROR = "ERR_ROLE_2011";
	public static final String GET_ROLE_DETAILS_SUCCESS = "INFO_ROLE_2010";
	public static final String UPDATE_ROLE_DETAILS_SUCCESS = "INFO_ROLE_2005";
	public static final String UPDATE_ROLE_DETAILS_FAILED_ERROR = "ERR_ROLE_2006";
	public static final String LIST_ROLE_DETAILS_FAILED_ERROR = "ERR_ROLE_2007";
	public static final String LIST_ROLE_DETAILS_SUCCESS = "INFO_ROLE_2008";
	public static final String CHECK_USER_TO_ROLE_FAILED_ERROR = "ERR_ROLE_2009";
	public static final String CHECK_USER_TO_ROLE_SUCCESS = "INFO_ROLE_20012";
	public static final String DELETE_ROLE_FAILED_ERROR = "ERR_ROLE_20013";
	public static final String DELETE_ROLE_SUCCESS = "INFO_ROLE_20014";
	public static final String LOAD_ROLE_PERMISSION_FAILED_ERROR = "INFO_ROLE_20015";
	public static final String LOAD_ROLE_PERMISSION_SUCCESS = "INFO_ROLE_20016";
	public static final String SITE_UPLOAD_SUCCESS = "INFO_SITE_UPLOAD_2000"; 
	public static final String SITE_UPLOAD_FILE_NOT_EXISTS = "ERR_SITE_UPLOAD_FILE_NOT_EXISTS_2000";
	public static final String SITE_UPLOAD_RECORDS_FAILED = "ERR_SITE_UPLOAD_RECORDS_2000";
	public static final String SITE_UPLOAD_RECORD_FAILED = "ERR_SITE_UPLOAD_RECORD_2000";
	public static final String SITE_UPLOAD_FAILED = "ERR_SITE_UPLOAD_2000";
	public static final String SITE_UPLOAD_WRONG_FORMAT_FAILED = "ERR_SITE_UPLOAD_FORMAT_2000";
	public static final String INFO_DELETE_SITE_UPLOAD = "INFO_DELETE_SITE_UPLOAD_SUCCESS_2001";
	public static final String ERROR_DELETE_SITE_UPLOAD="ERR_DELETE_SITE_UPLOAD_2002";
	
	public static final String SITE_GETGEOCODEDATA_SUCCESS = "INFO_SITE_GETGEOCODEDATA_2000";
			
	/**Role warning messages**/
	public static final String WARN_ROLE_ID_NOT_NULL = "ERR_ROLE_2000";
	public static final String WARN_ROLE_NAME_NOT_NULL = "ERR_ROLE_2001";
	public static final String WARN_ROLE_NAME_SIZE = "ERR_ROLE_2026";
	public static final String WARN_ROLE_NAME_NOT_VALID = "ERR_ROLE_NAME_2001";
	public static final String WARN_ROLE_ISEAI_INVALID = "ERR_ROLE_2002";
	public static final String WARN_ROLE_PERMISSIONS_EMPTY = "ERR_ROLE_2003";
	public static final String WARN_ROLE_PERMISSIONS_LEVEL_EMPTY = "ERR_ROLE_2004";
	public static final String WARN_ROLE_NULL = "ERR_ROLE_2005";
	public static final String WARN_ROLE_ISSUPURADMIN_INVALID = "ERR_ROLE_2031";
	public static final String WARN_ROLE_ISCSO_INVALID = "ERR_ROLE_2032";
	
	
	/** password encrypt related codes*/
	public static final String ENCRYPTPD_ERROR = "ERR_ENCRYPTPD_500";
	
	// invalid error messages
	public static final String WARN_USER_ID_NOT_NULL = "ERR_USER_2024";
	public static final String WARN_FIRST_NAME_NOT_EMPTY = "ERR_USER_2025";
	public static final String WARN_FIRST_NAME_SIZE = "ERR_USER_2026";
	public static final String WARN_LAST_NAME_SIZE = "ERR_USER_2027";
	public static final String WARN_TITLE_SIZE = "ERR_USER_2028";
	public static final String WARN_EMAIL_NOT_EMPTY = "ERR_USER_2030";
	public static final String WARN_EMAIL_INVALID = "ERR_USER_2031";
	public static final String WARN_PHONE_INVALID = "ERR_USER_2032";
	public static final String WARN_PHONE_NOT_EMPTY = "ERR_USER_2033";
	public static final String WARN_IVALID_CURRENT_PWD = "ERR_USER_2034";
	public static final String WARN_STATUS_NOT_NULL = "ERR_USER_2035";
	public static final String WARN_ROLE_ID_NOT_EMPTY = "ERR_USER_2036";
	public static final String WARN_EMAIL_ALREADY_EXIST = "ERR_USER_2037";
	public static final String WARN_LAST_NAME_NOT_EMPTY = "ERR_USER_2038";
	public static final String WARN_INCREMENT_FAIL_COUNT_FAILURE = "ERR_USER_2045";
	public static final String WARN_USER_GOT_LOCKED_FAILURE = "ERR_USER_2046";
	public static final String WARN_AUTH_TOKEN_NOT_EMPTY = "ERR_USER_2103"; 
	public static final String WARN_CHOOSE_PWD_NOT_EMPTY = "ERR_USER_2104"; 
	public static final String WARN_REPEAT_PWD_NOT_EMPTY = "ERR_USER_2105"; 
	public static final String WARN_CURRENT_PWD_NOT_EMPTY = "ERR_USER_2106";
	public static final String WARN_PASSWORD_SHOULD_BE_ALPHA_NUMERIC = "ERR_USER_2107";
	public static final String WARN_INVALID_TOKEN = "ERR_USER_2108"; 
	public static final String WARN_USER_MAX_ATTEMPTS_OVER_FOR_SECURITY_QUESTIONS_FAILURE = "ERR_USER_2109";
	public static final String WARN_USER_ID_NOT_EMPTY = "ERR_USER_2110";
	
	/**Site related error codes **/
	//Server side Validation error codes.
	public static final String ERROR_SITE_NAME_EMPTY = "ERR_SITE_300";
	public static final String ERROR_SITE_NAME_MAX_SIZE = "ERR_SITE_301";
	public static final String ERROR_SITE_TYPE_INVALID = "ERR_SITE_302";
	public static final String ERROR_ADD_SITE_TYPE_INVALID = "ERR_SITE_303";
	public static final String ERROR_SITE_ADDLINE1_EMPTY = "ERR_SITE_304";
	public static final String ERROR_SITE_STATE_INVALID = "ERR_SITE_305";
	public static final String ERROR_SITE_CITY_INVALID = "ERR_SITE_306";
	public static final String ERROR_SITE_ZIP_EMPTY = "ERR_SITE_307";
	public static final String ERROR_SITE_HOURS_NULL = "ERR_SITE_308";
	public static final String ERROR_SITE_GROUP_EMPTY = "ERR_SITE_309";
	public static final String ERROR_SITE_DEGREE_PREFERENCES_EMPTY = "ERR_SITE_310";
	public static final String ERROR_SITE_THERMOSTATE_PREFERENCES_EMPTY = "ERR_SITE_311";
	public static final String ERROR_SITE_NIGHTLY_SCHEDULE_EMPTY = "ERR_SITE_312";
	public static final String ERROR_SITE_ID_EMPTY = "ERR_SITE_313";
	public static final String ERROR_SITE_CUSTOMER_ID_EMPTY = "ERR_SITE_314";
	public static final String ERROR_SITE_ADDLINE1_MAX_SIZE = "ERR_SITE_315";
	public static final String ERROR_SITE_ADDLINE2_MAX_SIZE = "ERR_SITE_316";
	public static final String ERROR_SITE_STATUS_EMPTY = "ERR_SITE_317";
	public static final String ERROR_STATE_ID_EMPTY = "ERR_SITE_318";
	public static final String ERROR_DUPLICATE_SITE_INTERNAL_ID = "ERR_SITE_319";
	public static final String ERROR_ZIPCODE_MAX_LIMIT = "ERR_SITE_320";
	public static final String ERROR_OCCUPY_HOURS_NULL = "ERR_SITE_321";
	public static final String ERROR_SITE_HVAC_LIST_NULL = "ERR_SITE_322";
	public static final String ERROR_SITE_THERMOSTAT_LIST_NULL = "ERR_SITE_323";
	public static final String ERROR_SITE_HRS_FORMATE_NULL = "ERR_SITE_324";
	public static final String ERROR_SITE_OCCUPY_HRS_FORMATE_NULL = "ERR_SITE_325";
	public static final String ERROR_SITE_GROUP_IDS_EMPTY = "ERR_SITE_326";
		
	//Server Side Exception codes
	public static final String ADD_SITE_FAILED = "FAIL_SITE_400";
	public static final String ADD_SITE_SUCCESS = "SUCCESS_SITE_200";
	public static final String UPDATE_SITE_FAILED = "FAIL_SITE_401";
	public static final String UPDATE_SITE_SUCCESS = "SUCCESS_SITE_201";
	public static final String LIST_SITE_FAILED = "FAIL_SITE_402";
	public static final String LIST_SITE_SUCCESS = "SUCCESS_SITE_202";
	public static final String LOAD_ADD_SITE_FAILED = "FAIL_SITE_403";
	public static final String LOAD_ADD_SITE_SUCCESS = "SUCCESS_SITE_203";
	public static final String UPDATE_SITE_STATUS_FAILED = "FAIL_SITE_404";
	public static final String UPDATE_SITE_STATUS_SUCCESS = "SUCCESS_SITE_204";
	public static final String GET_SITE_FAILED = "FAIL_SITE_405";
	public static final String GET_SITE_SUCCESS = "SUCCESS_SITE_205";
	public static final String CHECK_SITE_INTERNAL_ID_FAILED = "FAIL_SITE_406";
	public static final String CHECK_SITE_INTERNAL_ID_SUCCESS = "SUCCESS_SITE_206";
	public static final String GET_CITIES_FAILED = "FAIL_SITE_407";
	public static final String GET_CITIES_SUCCESS = "SUCCESS_SITE_207";
	public static final String DELETE_SITE_FAILED = "FAIL_SITE_408";
	public static final String DELETE_SITE_SUCCESS = "SUCCESS_SITE_208";
	public static final String GET_CUSTOMER_ID_SUCCESS = "SUCCESS_SITE_209";
	public static final String GET_CUSTOMER_ID_FAILED = "FAIL_SITE_409";
	public static final String GET_SITES_BY_GROUP_IDS_SUCCESS = "SUCCESS_SITE_210";
	public static final String GET_SITES_BY_GROUP_IDS_FAILED = "FAIL_SITE_410";
	public static final String SITE_VALIDATE_SITE_NAME_FAILED = "FAIL_SITE_411";
	
	public static final String GET_TIMEZONE_DETAILS_SUCCESS = "SUCCESS_TIMEZONE_DETAILS_200";
	public static final String GET_TIMEZONE_DETAILS_FAILED = "FAIL_TIMEZONE_DETAILS_400";
	public static final String ERROR_TIMEZONE_EMPTY = "ERR_TIMEZONE_300";
	public static final String GET_GEOCODEDATA_DETAILS_FAILED = "FAIL_GEOCODEDATA_DETAILS_400";
	public static final String ADD_GEOCODESDATA_FAILED = "FAIL_GEOCODEDATA_DETAILS_401";
	public static final String ADD_SITE_LAT_LON_FAILED = "FAIL_SITE_LAT_LON_400";
	
	//The Activity Log validation error codes.
	public static final String ERROR_ACTIVITY_LOG_SERVICE_ID_INVALID = "ERR_ACTIVITY_LOG_300";
	public static final String ERROR_SUBJECT_EMPTY = "ERR_ACTIVITY_LOG_301";
	public static final String ERROR_TYPE_INVALID = "ERR_ACTIVITY_LOG_302";
	public static final String ERROR_TIMESTAMP_EMPTY = "ERR_ACTIVITY_LOG_303";
	public static final String ERROR_CUSTOMER_ID_INVALID = "ERR_ACTIVITY_LOG_304";
	
	//The Activity Log server error codes.
	public static final String GET_ACTIVITY_LOG_DATA_FAILED = "FAIL_ACTIVITY_LOG_400";
	public static final String GET_ACTIVITY_LOG_DATA_SUCCESS = "SUCCESS_ACTIVITY_LOG_200";
	public static final String CREATE_ACTIVITY_LOG_FAILED = "FAIL_ACTIVITY_LOG_401";
	public static final String CREATE_ACTIVITY_LOG_SUCCESS = "SUCCESS_ACTIVITY_LOG_201";
	
	// Customers specific error status
	public static final String INFO_CUSTOMER_DETAILS_FETCH="SUCCESS_CUSTOMER_DETAILS_4203";
	public static final String ERROR_CUSTOMERS_FETCH="ERR_CUSTOMERS_LIST_4404";
	public static final String INFO_CUSTOMERS_FETCH="SUCCESS_CUSTOMERS_LIST_4200";
	public static final String ERROR_CUSTOMERS_FATAL="ERR_CUSTOMERS_LIST_4500";
	public static final String ERROR_CUSTOMER_STATUS_FATAL="ERR_CUSTOMER_STATUS_4500";
	public static final String WARN_CUSTOMER_ID="ERR_CUSTOMERID_EMPTY_4404";	
	public static final String ERROR_CUSTOMER_FETCH="ERR_CUSTOMERS_LIST_4405";
	public static final String INFO_CUSTOMER_STATUS_UPDATE="SUCCESS_CUSTOMER_STATUS_UPADTE_4201";
	public static final String ERROR_CUSTOMER_FATAL="ERR_CUSTOMER_FATAL_4500";
	public static final String ERROR_COMPANY_NAME_EMPTY="ERR_COMPANY_NAME_300";
	public static final String ERROR_COMPANY_NAME_MAX_SIZE = "ERR_COMPANY_SIZE_301";
	public static final String ERROR_COMPANY_CODE_EMPTY="ERR_CUSTOMER_302";
	public static final String ERROR_COMPANY_CODE_MAX_SIZE = "ERR_CUSTOMER_303";
	public static final String ERROR_CUSTOMER_ADDLINE1_EMPTY = "ERR_CUSTOMER_304";
	public static final String ERROR_CUSTOMER_STATE_INVALID = "ERR_CUSTOMER_305";
	public static final String ERROR_CUSTOMER_CITY_INVALID = "ERR_CUSTOMER_306";
	public static final String ERROR_CUSTOMER_ZIP_EMPTY = "ERR_CUSTOMER_307";
	public static final String ERROR_CUSTOMER_ZIP_CODE_MAX_SIZE = "ERR_CUSTOMER_308";
	public static final String ERROR_CUSTOMER_DEGREE_PREFERENCES_EMPTY = "ERR_CUSTOMER_310";
	public static final String ERROR_CUSTOMER_THERMOSTATE_PREFERENCES_EMPTY = "ERR_CUSTOMER_311";
	public static final String ERROR_CUSTOMER_NIGHTLY_SCHEDULE_EMPTY = "ERR_CUSTOMER_312";
	public static final String ERROR_CUSTOMER_ID_EMPTY = "ERR_CUSTOMER_313";
	public static final String ERROR_CUSTOMER_CUSTOMER_ID_EMPTY = "ERR_CUSTOMER_314";
	public static final String ERROR_CUSTOMER_ADDLINE1_MAX_SIZE = "ERR_CUSTOMER_315";
	public static final String ERROR_CUSTOMER_ADDLINE2_MAX_SIZE = "ERR_CUSTOMER_316";
	public static final String ERROR_CUSTOMER_STATUS_EMPTY = "ERR_CUSTOMER_317";
	public static final String CUSTOMER_INFO_SAVE_SUCCESS = "INFO_CUSTOMER_2000";
	public static final String CUSTOMER_INFO_UPDATE_SUCCESS = "INFO_CUSTOMER_201";
	public static final String CUSTOMER_ERROR_SAVE_FAILED = "ERR_CUSTOMER_2001";
	public static final String CUSTOMER_ERROR_UPDATE_FAILED = "ERR_CUSTOMER_2012";
	public static final String CUSTOMER_ERROR_DELETE_FAILED = "ERR_CUSTOMER_2014";
	public static final String CUSTOMER_ERROR_INSERT_FAILED  = "ERR_CUSTOMER_2015";
	public static final String CUSTOMER_INFO_INSERT_SUCCESS = "ERR_CUSTOMER_20150";
	public static final String CUSTOMER_INFO_DELETE_SUCCESS = "INFO_CUSTOMER_2016";
	public static final String  CUSTOMER_CODE_ERROR_GENERATE_FAILED ="ERR_CUSTOMER_CODE_2016";
	public static final String  CUSTOMER_CODE_INFO_GENERATE_SUCCESS ="ERR_CUSTOMER_CODE_2017";
	public static final String CUSTOMER_CODE_ALREADY_EXISTS = "ERR_CUSTOMER_CODE_EXISTS_2017";
	public static final String UPLOAD_ERROR_SAVE_FAILED = "ERR_FILE_UPLOAD_200";
	
	/**Device related error codes **/
	public static final String ERROR_XCSPEC_UNAUTHORISED 					= "ERR_XCSPEC_UNAUTHORISED_5001";
	public static final String ERROR_XCSPEC_ALREADY_REG 					= "ERROR_XCSPEC_ALREADY_REG_5002";
	public static final String ERROR_SQL_SAVE_DEVICE						= "ERROR_SQL_SAVE_DEVICE_5003";
	public static final String ERROR_AWS_IOT_REGISTER_DEVICE 				= "ERROR_AWS_IOT_REGISTER_DEVICE_5004";
	public static final String ERROR_SQL_ADD_DEVICE_FAILED					= "ERROR_SQL_ADD_DEVICE_5005";
	public static final String ERROR_UPDATE_DEVICE_STATUS_FAILED			= "ERROR_UPDATE_DEVICE_STATUS_5006";
	public static final String ERROR_UPDATE_DISCONNECT_DEVICE_FAILED		= "ERROR_UPDATE_DISCONNECT_DEVICE_FAILED_5007";
	public static final String ERROR_XCSPEC_DELETE_DEVICE_NOT_FOUND 		= "ERROR_XCSPEC_DELETE_DEVICE_NOT_FOUND_5008";
	public static final String ERROR_AWS_IOT_DELETE_DEVICE 					= "ERROR_AWS_IOT_DELETE_DEVICE_5009";
	public static final String ERROR_UPDATE_DEVICE_DISCONNECT 				= "ERROR_UPDATE_DEVICE_DISCONNECT_5010";
	public static final String ERROR_SQL_UPDATE_DEVICE 						= "ERROR_SQL_UPDATE_DEVICE_5011";
	public static final String ERROR_GET_DEVICE_FAILED 						= "ERROR_GET_DEVICE_FAILED_5012";
	public static final String ERROR_DELETE_DEVICE_FAILED 					= "ERROR_DELETE_DEVICE_FAILED_5013";
	public static final String ERROR_UPDATING_EAI_DEV_ID_DEVICE 			= "ERROR_UPDATING_EAI_DEV_ID_DEVICE_5014";
	public static final String ERROR_DEVICE_SITEID_EMPTY_TSTATUNITS 		= "ERROR_DEVICE_SITEID_EMPTY_TSTATUNITS_5015";
	public static final String ERROR_LIST_THERMOSTAT_UNITS_FAILED 			= "ERROR_LIST_THERMOSTAT_UNITS_FAILED_5016";
	public static final String ERROR_DEVICE_GET_TSTATPREF_FAILED 			= "ERROR_DEVICE_GET_TSTATPREF_FAILED_5017";
	public static final String ERROR_SQL_FETCH_DEVICE_MACID 				= "ERROR_SQL_FETCH_DEVICE_MACID_5018";
	public static final String ERROR_SQL_UPDATE_DEVICE_ALREADY_INVEM 		= "ERROR_SQL_UPDATE_DEVICE_ALREADY_INVEM_5019";
	public static final String ERROR_INVALID_SORT_DETAILS 					= "ERROR_INVALID_SORT_DETAILS_5020";
	public static final String ERROR_DEVICE_NOT_FOUND 						= "ERROR_DEVICE_NOT_FOUND_50021";
	public static final String DEVICE_REG_VALIDATE_AWS_IOT_REG_FAILED 		= "DEVICE_REG_VALIDATE_AWS_IOT_REG_FAILED_5022";
	
	/**Device related success codes **/
	public static final String SUCCESS_REGISTER_DEVICE 							= "SUCCESS_REGISTER_DEVICE_200";
	public static final String SUCCESS_LISTING_DEVICES 							= "SUCCESS_LISTING_DEVICES_201";
	public static final String SUCCESS_UPDATING_DEVICE_STATUS_DEVICES 			= "SUCCESS_UPDATING_DEVICE_STATUS_202";
	public static final String SUCCESS_UPDATING_DEVICE_DISCONNECT 				= "SUCCESS_UPDATING_DEVICE_DISCONNECT_203";
	public static final String SUCCESS_UPDATE_DEVICE 							= "SUCCESS_UPDATE_DEVICE_204";
	public static final String SUCCES_GET_DEVICE 								= "SUCCESS_GET_DEVICE_205";
	public static final String SUCCESS_DELETE_DEVICE 							= "SUCCESS_DELETE_DEVICE_206";
	public static final String SUCCESS_REGISTER_DEVICE_BUT_NOT_UPDATED_EAI_ID 	= "SUCCESS_REGISTER_DEVICE_BUT_NOT_UPDATED_EAI_ID_207";
	public static final String SUCCESS_LIST_TSTATUNIT 							= "SUCCESS_LIST_TSTATUNIT_208";
	public static final String SUCCESS_DEVICE_TSTATPREF 						= "SUCCESS_DEVICE_TSTATPREF_208";
	public static final String SUCCESS_DEVICE_SET_TEMP 							= "SUCCESS_DEVICE_SET_TEMP_209";
	public static final String SUCCESS_DEVICE_SET_HOLD 							= "SUCCESS_DEVICE_SET_HOLD_210";
	public static final String SUCCESS_DEVICE_SET_TSTAT_DATA 					= "SUCCESS_DEVICE_SET_TSTAT_DATA_211";
	public static final String SUCCESS_DEVICE_SET_CLOCK 						= "SUCCESS_DEVICE_SET_CLOCK_212";
	
	/**Device related validation error codes **/
	public static final String ERROR_DEVICE_DEVICEID_EMPTY 		= "ERR_DEVICE_DEVICEID_EMPTY_500";
	public static final String ERROR_DEVICE_DEVICEID_NULL 		= "ERR_DEVICE_DEVICEID_NULL_501";
	public static final String ERROR_DEVICE_MACID_EMPTY 		= "ERR_DEVICE_MACID_EMPTY_502";
	public static final String ERROR_DEVICE_MACID_NULL 			= "ERR_DEVICE_MACID_NULL_503";
	public static final String ERROR_DEVICE_ISACTIVE_EMPTY 		= "ERR_DEVICE_ISACTIVE_EMPTY_504";
	public static final String ERROR_DEVICE_ISACTIVE_NULL 		= "ERR_DEVICE_ISACTIVE_NULL_505";
	public static final String ERROR_DEVICE_XCSPECID_EMPTY 		= "ERR_DEVICE_XCSPECID_EMPTY_506";
	public static final String ERROR_DEVICE_XCSPECID_NULL 		= "ERR_DEVICE_XCSPECID_NULL_507";
	public static final String ERROR_DEVICE_NAME_EMPTY 			= "ERROR_DEVICE_NAME_EMPTY_508";
	public static final String ERROR_DEVICE_MODEL_EMPTY 		= "ERROR_DEVICE_MODEL_EMPTY_509";
	public static final String ERROR_DEVICE_SITEID_NULL 		= "ERROR_DEVICE_SITEID_NULL_510";
	public static final String ERROR_DEVICE_AREA_EMPTY 			= "ERROR_DEVICE_AREA_EMPTY_511";
	public static final String ERROR_DEVICE_REGTYPE_NULL 		= "ERROR_DEVICE_REGTYPE_NULL_511";
	public static final String ERROR_DEVICE_CUSTID_NULL 		= "ERROR_DEVICE_CUSTID_NULL_512";
	public static final String ERROR_DEVICE_DEVTYPE_NULL 		= "ERROR_DEVICE_DEVTYPE_NULL_513";
	public static final String ERROR_DEVICE_WIFIKEY_EMPTY 		= "ERROR_DEVICE_WIFIKEY_EMPTY_514";
	public static final String ERROR_DEVICE_UNIT_EMPTY 			= "ERROR_DEVICE_UNIT_EMPTY_515";
	public static final String ERROR_DEVICE_LOCATION_NULL 		= "ERROR_DEVICE_LOCATION_NULL_516";
	public static final String ERROR_DEVICE_SETTEMP_MODE_NULL 	= "ERROR_DEVICE_SETTEMP_MODE_NULL_517";
	public static final String ERROR_DEVICE_SETTEMP_MODE_EMPTY 	= "ERROR_DEVICE_SETTEMP_MODE_EMPTY_518";
	public static final String ERROR_DEVICE_SETTEMP_TEMP_NULL 	= "ERROR_DEVICE_SETTEMP_TEMP_NULL_519";
	public static final String ERROR_DEVICE_SETTEMP_TEMP_EMPTY 	= "ERROR_DEVICE_SETTEMP_TEMP_EMPTY_520";
	public static final String ERROR_DEVICE_SETHOLD_NULL 		= "ERROR_DEVICE_SETHOLD_NULL_521";
	public static final String ERROR_DEVICE_SETHOLD_EMPTY 		= "ERROR_DEVICE_SETHOLD_EMPTY_522";
	public static final String ERROR_DEVICE_SET_TSTAT_DATA_NULL = "ERROR_DEVICE_SET_TSTAT_DATA_NULL_523";
	public static final String ERROR_DEVICE_SET_TSTAT_DATA_EMPTY = "ERROR_DEVICE_SET_TSTAT_DATA_EMPTY_524";
	public static final String ERROR_DEVICE_SET_TSTAT_TYPE_NULL = "ERROR_DEVICE_SET_TSTAT_TYPE_NULL_525";
	public static final String ERROR_DEVICE_SET_TSTAT_TYPE_EMPTY = "ERROR_DEVICE_SET_TSTAT_TYPE_EMPTY_526";
	public static final String ERROR_DEVICE_SETCLOCK_TIME_NULL 	= "ERROR_DEVICE_SETCLOCK_TIME_NULL_527";
	public static final String ERROR_DEVICE_SETCLOCK_TIME_EMPTY = "ERROR_DEVICE_SETCLOCK_TIME_EMPTY_528";
	public static final String ERROR_DEVICE_SETCLOCK_DAY_NULL 	= "ERROR_DEVICE_SETCLOCK_DAY_NULL_529";
	public static final String ERROR_DEVICE_SETCLOCK_DAY_EMPTY 	= "ERROR_DEVICE_SETCLOCK_DAY_EMPTY_530";
	public static final String ERROR_DEVICE_AWS_COMPATIBLE_NULL = "ERROR_DEVICE_AWS_COMPATIBLE_NULL_531";
	public static final String DEVICE_MODEL_NOT_SUPPORTED 		= "DEVICE_MODEL_NOT_SUPPORTED_532";
	public static final String DEVICE_FORECAST_NULL 		    = "DEVICE_FORECAST_NULL_533";
	public static final String ERROR_DEVICE_HEATPUMP_TYPE_NULL 	= "ERROR_DEVICE_HEATPUMP_TYPE_NULL_534";
	public static final String ERROR_DEVICE_HEATPUMP_TYPE_VALUE_NULL = "ERROR_DEVICE_HEATPUMP_TYPE_VALUE_NULL_535";



	//common error code to validate zipcode
	public static final String ERROR_COMMON_VALIDATE_ZIPCODE 	= "ERROR_COMMON_VALIDATE_ZIPCODE_EMPTY_500";
	public static final String ERROR_COMMON_ZIPCODE_NOTVALIDATE = "ERROR_COMMON_ZIPCODE_NOTVALIDATE_501";
	public static final String ERROR_COMMON_TIMEZONE = "ERROR_COMMON_TIMEZONE_501";
	public static final String ERROR_COMMON_STORE_TIMEZONE = "ERROR_COMMON_STORE_TIMEZONE_502";
	public static final String ERROR_COMMON_CITY_VALIDATION_FAILED = "ERROR_COMMON_CITY_VALIDATION_FAILED_503";


	//common success code to validate zipcode
	public static final String SUCCESS_COMMON_ZIPCODE_VALIDATE 	= "SUCCESS_COMMON_ZIPCODE_VALIDATE_200";
	public static final String SUCCESS_COMMON_TIMEZONE 	= "SUCCESS_COMMON_TIMEZONE_200";
	public static final String SUCCESS_COMMON_STORE_TIMEZONE = "ERROR_COMMON_STORE_TIMEZONE_202";
	public static final String SUCCESS_RUN_SCHEDULE = "SUCCESS_RUN_SCHEDULE_203";

	//common error codes to vaidate forecast data
	public static final String ERROR_FORECAST_MODE_NULL 		= "ERROR_FORECAST_MODE_NULL_500";
	public static final String ERROR_FORECAST_TYPE_NULL 		= "ERROR_FORECAST_TYPE_NULL_501";
	public static final String ERROR_FORECAST_TYPE_ID_NULL 		= "ERROR_FORECAST_TYPE_ID_NULL_502";
	public static final String ERROR_FORECAST_FORECASTNAME_EMPTY = "ERROR_FORECAST_FORECASTNAME_EMPTY_503";
	public static final String ERROR_FORECAST_FROMDATE_EMPTY 	= "ERROR_FORECAST_FROMDATE_EMPTY_504";
	public static final String ERROR_FORECAST_TODATE_EMPTY 		= "ERROR_FORECAST_TODATE_EMPTY_505";
	public static final String ERROR_FORECAST_ID_NULL 			= "ERROR_FORECAST_ID_NULL_506";
	public static final String ERROR_FORECAST_MINTEMP_NULL 		= "ERROR_FORECAST_MINTEMP_NULL_507";
	public static final String ERROR_FORECAST_MAXTEMP_NULL 		= "ERROR_FORECAST_MAXTEMP_NULL_508";
	public static final String ERROR_FORECAST_SCHEDULEID_NULL 	= "ERROR_FORECAST_SCHEDULEID_NULL_509";
	public static final String ERROR_FORECAST_TEMP_ID_NULL 		= "ERROR_FORECAST_TEMP_ID_NULL_510";


	//success codes forecast
	public static final String SUCCESS_ADD_FORECAST_CONFIG 	= "SUCCESS_ADD_FORECAST_CONFIG_201";
	public static final String SUCCESS_APPLY_FORECAST_CONFIG = "SUCCESS_APPLY_FORECAST_CONFIG_201";
	public static final String SUCCESS_LISTING_FORECAST 	= "SUCCESS_LISTING_FORECAST_202";
	public static final String SUCCESS_UPDATE_FORECAST_CONFIG = "SUCCESS_UPDATE_FORECAST_CONFIG_203";
	public static final String SUCCESS_DELETE_FORECAST 		= "SUCCESS_DELETE_FORECAST_204";
	public static final String SUCCESS_DEVICE_LIST_FORECAST_DATA = "SUCCESS_DEVICE_LIST_FORECAST_DATA_205";


	//error codes forecast
	public static final String ERROR_ADD_FORECAST_CONFIG 	= "ERROR_ADD_FORECAST_CONFIG_510";
	public static final String ERROR_LIST_FORECAST_FAILED 	= "ERROR_LIST_FORECAST_FAILED_511";
	public static final String ERROR_UPDATE_FORECAST_CONFIG = "ERROR_UPDATE_FORECAST_CONFIG_512";
	public static final String ERROR_DELETE_FORECAST_FAILED = "ERROR_DELETE_FORECAST_FAILED_513";

	//schedule success and error codes
	//success codes schedule
	public static final String SUCCESS_LISTING_SCHEDULE 		= "SUCCESS_LISTING_SCHEDULE_200";
	public static final String SUCCESS_READ_SCHEDULE 			= "SUCCESS_READ_SCHEDULE_201";

	//error code schedule 
	public static final String ERROR_LIST_SCHEDULE_FAILED 		= "ERROR_LIST_SCHEDULE_FAILED_500";
	public static final String ERROR_READ_SCHEDULE_FAILED 		= "ERROR_READ_SCHEDULE_FAILED_501";
	public static final String ERROR_INVALID_RUN_SCHEDULE_INFO 	= "ERROR_INVALID_RUN_SCHEDULE_INFO_502";

	
	//LookUp error codes.
	public static final String LOOKUP_CITIES_FAILED = "FAIL_L_CITY_400";
	public static final String LOOKUP_STATES_FAILED = "FAIL_L_STATE_400";
	public static final String LOOKUP_REST_USER_FAILED = "FAIL_L_REST_USER_400";
	public static final String LOOKUP_GET_TIMEZONE_FAILED = "FAIL_L_GET_TIMEZONE_400";
	
	public static final String LOAD_STATE_SUCCESS = "INFO_STATE_500";
	public static final String  LOAD_STATE_FAILED= "FAIL_STATE_501";


	// Group related codes
	public static final String INFO_GROUPS_FETCH = "INFO_GROUPSLIST_SUCCESS_6200";
	public static final String ERROR_GROUPS_FETCH="ERR_GROUPSLIST_6404";
	
	/* Save Group codes */
	public static final String INFO_SAVE_GROUP = "INFO_SAVE_GROUP_SUCCESS_6201";
	public static final String ERROR_SAVE_GROUP="ERR_SAVEGROUP_6405";
	public static final String ERROR_GROUP_CHECK="ERR_GROUPCHECK_6406";
	
	/* list sites for Group codes */
	public static final String LIST_SITES_FOR_GROUP_SUCCESS = "INFO_SITES_FOR_GROUP_SUCCESS_6202";
	public static final String LIST_SITES_FOR_GROUP_FAILED = "ERR_SITES_FOR_GROUP_6406";
	

	/* groups list for multiple customers */
	public static final String LIST_CUSTOMER_FOR_GROUP_SUCCESS = "LIST_CUSTOMER_FOR_GROUP_SUCCESS_6203";
	public static final String LIST_CUSTOMER_FOR_GROUP_ERROR = "LIST_CUSTOMER_FOR_GROUP_SUCCESS_6403";
	public static final String LIST_CUSTOMER_FOR_GROUP_EMPTY = "LIST_CUSTOMER_FOR_GROUP_SUCCESS_6404";
	
	/* Update Group codes */
	public static final String INFO_UPDATE_GROUP = "INFO_SAVE_GROUP_SUCCESS_6203";
	public static final String ERROR_UPDATE_GROUP="ERR_SAVEGROUP_6407";
	
	/* Delete Group codes */
	public static final String INFO_DELETE_GROUP = "INFO_DELETE_GROUP_SUCCESS_6204";
	public static final String ERROR_DELETE_GROUP="ERR_DELETEGROUP_6408";
	
	/* STATUS Group codes */
	public static final String INFO_STATUS_UPDATE_GROUP = "INFO_STATUS_UPDATE_GROUP_SUCCESS_6205";
	public static final String ERROR_STATUS_UPDATE_GROUP="ERR_STATUS_UPDATE_GROUP_6409";
	
	/* Schedule Codes*/
	public static final String ERROR_STATUS_GET_SCHEDULE="ERROR_STATUS_GET_SCHEDULE_7500";
	public static final String INFO_STATUS_GET_SCHEDULE="INFO_STATUS_GET_SCHEDULE_7501";
	public static final String INFO_STATUS_APPLY_SCHEDULE="INFO_STATUS_APPLY_SCHEDULE_7502";
	public static final String ERROR_STATUS_APPLY_SCHEDULE="ERROR_STATUS_APPLY_SCHEDULE_7503";
	
	public static final String ERROR_DUPLICATE_SCHEDULE = "ERR_DUP_SCHEDULE_8000";
	public static final String ADD_SCHEDULE_SUCCESS = "SUCCESS_SCHEDULE_200";
	public static final String ADD_SCHEDULE_FAILED = "FAIL_SCHEDULE_400";
	public static final String GET_SCHEDULE_DETAILS_FAILED_ERROR = "ERR_SCHEDULE_DETAILS_2011";
	public static final String GET_SCHEDULE_DETAILS_SUCCESS = "INFO_SCHEDULE_DETAILS_2010";	
	public static final String UPDATE_SCHEDULE_SUCCESS = "SUCCESS_UPDATE_SCHEDULE_200";
	public static final String UPDATE_SCHEDULE_FAILED = "FAIL_UPDATE_SCHEDULE_400";
	public static final String DELETE_SCHEDULE_SUCCESS = "SUCCESS_DELETE_SCHEDULE_200";
	public static final String DELETE_SCHEDULE_FAILED = "FAIL_DELETE_SCHEDULE_400";
	public static final String ADD_SCHEDULE_CUSTOM_SUCCESS = "SUCCESS_CUSTOM_SCHEDULE_200";
	public static final String DELETE_SCHEDULE_FAILED_FORECAST_MAPPED = "FAIL_DELETE_SCHEDULE_401";
	public static final String DELETE_SCHEDULE_FAILED_DEVICE_MAPPED = "FAIL_DELETE_SCHEDULE_402";
	
	
	// Alert error codes
	public static final String ALERT_ACTION_ITEMS_ERROR_FAILED = "ERROR_ALERT_7401";
	public static final String ALERT_ACTION_ITEMS_INFO_SUCCESS = "INFO_ALERT_7200";
	public static final String ALERT_ACTION_ITEMS_UPDATE_ERROR_FAILED = "ERROR_ALERT_7402";
	public static final String ALERT_ACTION_ITEMS_UPDATE_INFO_SUCCESS = "SUCCESS_ALERT_7201";
	public static final String ERROR_ALERT_LIST_FETCH = "ERROR_ALERT_LIST_7404";
	public static final String SUCCESS_ALERT_LIST_FETCH = "SUCCESS_ALERT_LIST_7201";
	public static final String SUCCESS_ALERT_GETCONFIG = "SUCCESS_GETCONFIG_7202";
	public static final String ERROR_ALERT_GETCONFIG = "ERROR_GETCONFIG_7403";
	public static final String SUCCESS_ALERT_CONFIG_SAVE = "SUCCESS_SAVECONFIG_7203";
	public static final String ERROR_ALERT_CONFIG_SAVE = "ERROR_SAVECONFIG_7404";
	public static final String SUCCESS_ALERT_CUSTOMER_FETCH="SUCCESS_CUSTOMERALERTS_7204";
	public static final String ERROR_ALERT_CUSTOMER_FETCH="ERROR_CUSTOMERALERTS_7204";
	public static final String SUCCESS_STORE_ALERTS="SUCCESS_STORE_ALERTS_7405";
	public static final String ERROR_STORE_ALERTS="ERROR_STORE_ALERTS_7205";
	public static final String ERROR_ALERT_ACTIONLIST_FETCH = "ERROR_ALERT_ACTIONLIST_7406";
	public static final String SUCCESS_ALERT_ACTIONLIST_FETCH = "SUCCESS_ALERT_ACTIONLIST_7206";
	public static final String ERROR_ALERT_ACTIONLIST_UPDATE = "ERROR_ALERT_ACTIONLIST_7407";
	public static final String SUCCESS_ALERT_ACTIONLIST_UPDATE = "SUCCESS_ALERT_ACTIONLIST_7207";
	public static final String ERROR_ALERT_CUSTOMER_CONFIG_DELETE = "ERROR_ALERT_CUSTOMER_CONFIG_DELETE_7501";
	public static final String SUCCESS_ALERT_CUSTOMER_CONFIG_DELETE = "SUCCESS_ALERT_CUSTOMER_CONFIG_DELETE_7208";
	public static final String ERROR_ALERT_CUSTOMER_CONFIG_ALREADY = "ALREADY_ALERT_CUSTOMER_CONFIG_7208";
	
	// Reports Error Codes
	public static final String DASH_ERROR_FETCH_FAILED = "ERROR_REPORT_1001";
	public static final String DASH_INFO_FETCH_SUCCESS = "INFO_REPORT_1002";
	public static final String REPORT_ERROR_FETCH_FAILED = "ERROR_REPORT_1003";
	public static final String REPORT_INFO_FETCH_SUCCESS = "INFO_REPORT_1004";
	public static final String ANALYTICS_ERROR_FETCH_FAILED = "ERROR_REPORT_1005";
	public static final String ANALYTICS_INFO_FETCH_SUCCESS = "INFO_REPORT_1006";
	public static final String SESSION_INFO_TIME_ZONE_SUCCESS = "INFO_SESSION_1007";
	public static final String SESSION_ERROR_TIME_ZONE_FAILED = "ERROR_SESSION_1008";
	public static final String ANALYTICS_ERR_DIFF_TIME_ZONE_FAILURE = "ERROR_ANALYTICS_1009";
	
	// Analytics
	public static final String CUSTOMER_ANALYTICS_ERROR_FETCH_FAILED = "ERROR_CUSTOMER_ANALYTICS_1001";
	public static final String GROUP_ANALYTICS_ERROR_FETCH_FAILED = "ERROR_CUSTOMER_ANALYTICS_1002";
	public static final String SITE_ANALYTICS_ERROR_FETCH_FAILED = "ERROR_CUSTOMER_ANALYTICS_1003";
	public static final String DEVICE_ANALYTICS_ERROR_FETCH_FAILED = "ERROR_CUSTOMER_ANALYTICS_1004";
	public static final String TRENDING_REPORT_ERROR_FETCH_FAILED = "ERROR_REPORT_1005";
	
	public static final String CUSTOMER_ANALYTICS_FETCH_SUCCESS = "INFO_CUSTOMER_ANALYTICS_1001";
	public static final String GROUP_ANALYTICS_FETCH_SUCCESS = "INFO_CUSTOMER_ANALYTICS_1002";
	public static final String SITE_ANALYTICS_FETCH_SUCCESS = "INFO_CUSTOMER_ANALYTICS_1003";
	public static final String DEVICE_ANALYTICS_FETCH_SUCCESS = "INFO_CUSTOMER_ANALYTICS_1004";
	public static final String EXCCEDED_MAX_PARAMS_FETCH_SUCCESS = "ERROR_ANALYTICS_1005";
	public static final String DEVICE_IDS_ERROR_FETCH_FAILED = "ERROR_DEVICE_IDS_1006";
	public static final String WARN_EMAIL_MAX_EXCEDED = "ERROR_EMAIL_1007";
	public static final String NO_DEVICES_FAILURE = "ERROR_REPORT_1017";
	public static final String FETCH_CUSTOMER_SITE_IDS = "ERROR_ADMIN_1111";
	public static final String DASHBOARD_DEGRADED_PERFORMANCE_FETCH_FAIL = "ERROR_DASHBOARD_1001";
	public static final String DASHBOARD_WITHIN_SETPOINT_PERFORMANCE_FETCH_FAIL = "ERROR_DASHBOARD_1002";
	public static final String DASHBOARD_HVACUSAGE_PERFORMANCE_FETCH_FAIL = "ERROR_DASHBOARD_1003";
	public static final String DASHBOARD_MANUAL_PERFORMANCE_FETCH_FAIL = "ERROR_DASHBOARD_1004";
	public static final String DASHBOARD_DEGRADED_PERFORMANCE_FETCH_SUCCESS = "INFO_DASHBOARD_1005";
	public static final String DASHBOARD_WITHIN_SETPOINT_PERFORMANCE_FETCH_SUCCESS = "INFO_DASHBOARD_1006";
	public static final String DASHBOARD_HVACUSAGE_PERFORMANCE_FETCH_SUCCESS = "INFO_DASHBOARD_1007";
	public static final String DASHBOARD_MANUAL_PERFORMANCE_FETCH_SUCCESS = "INFO_DASHBOARD_1008";
	
	/*Bulk upload error codes and messages*/
	public static final String BULK_UPLOAD_SUCCESS="SUCCESS_BULK_UPLOAD_8000";
	public static final String BULK_UPLOAD_ERROR="SUCCESS_BULK_UPLOAD_8400";
	
	/* PDF Report Errors code*/
	public static final String PDF_REPORT_FAILED = "ERROR_PDF_REPORT_500";
	public static final String PDF_REPORT_SUCCESS = "SUCCESS_PDF_REPORT_200";
	
	public static final String PDF_REPORT_GET_LIST_FAILED = "ERROR_PDF_REPORT_GET_LIST_500";
	public static final String PDF_REPORT_GET_LIST_SUCCESS = "SUCCESS_PDF_REPORT_GET_LIST_200";
	
	public static final String PDF_REPORT_INSERT_FAILED = "ERROR_PDF_REPORT_INSERT_500";
	public static final String PDF_REPORT_INSERT_SUCCESS = "SUCCESS_PDF_REPORT_INSERT_200";
	
	public static final String PDF_REPORT_DELETE_FAILED = "ERROR_PDF_REPORT_DELETE_500";
	public static final String PDF_REPORT_DELETE_SUCCESS = "SUCCESS_PDF_REPORT_DELETE_200";
	
	public static final String PDF_REPORT_RESEND_FAILED = "ERROR_PDF_REPORT_RESEND_500";
	public static final String PDF_REPORT_RESEND_SUCCESS = "SUCCESS_PDF_REPORT_RESEND_200";
	public static final String INCORRECT_PASSWORD_DETAILS = "ERROR_USER_EMAIL_UPDATE";
	
}
