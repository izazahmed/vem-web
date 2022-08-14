/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util;

/**
 * File Name : TableFieldConstants 
 * 
 * TableFieldConstants: is used to declare all table fields related constants.
 *
 * @author (Goush Basha – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        23-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 23-08-2016		Goush Basha		    File Created
 * 23-08-2016		Nagarjuna Eerla	    Added USERS table field constants
 * 30-08-2016       Goush Basha			Sprint-2 Suggestions/Changes.
 *
 */
public class TableFieldConstants {
	
	private TableFieldConstants(){}
	
	/** Role table  fields constants **/
	public static final String ROLE_ID = "role_id";
	public static final String ROLE_NAME = "role_name";
	public static final String ROLE_DESC = "role_description";
	public static final String ROLE_IS_EAI = "is_eai";
	public static final String ROLE_IS_CSO = "is_cso";
	public static final String ROLE_IS_SUPER = "is_super";
	
	/**Role bean properties constants **/
	public static final String BEAN_ROLE_IS_EAI = "isEai";
	
	/** Permission table  fields constants **/
	public static final String PERMISSION_ID = "permission_id";
	public static final String PERMISSION_INTERNAL_NAME = "permission_internal_name";
	public static final String PERMISSION_NAME = "permission_name";
	public static final String PERMISSION_DISPLAY_ONLY_FOR_EAI = "display_only_for_eai";
	
	/** Permission_level table  fields constants **/
	public static final String PERMISSION_LEVEL_ID = "permission_level_id";
	public static final String PERMISSION_LEVEL_NAME = "permission_level_name";
	
	// USERS table fields constants
	public static final String USER_ID = "USER_ID";
	public static final String USER_EMAIL = "USER_EMAIL";
	public static final String USER_FNAME = "USER_FNAME";
	public static final String USER_LNAME = "USER_LNAME";
	public static final String ALERT_PREFERENCE = "ALERT_PREFERENCE";
	public static final String UNAME = "UNAME";
	public static final String IS_ACTIVE = "IS_ACTIVE";
	public static final String GROUP_IDS = "GROUP_IDS";
	public static final String SITE_IDS = "SITE_IDS";
	public static final String ROLE_ID_U = "ROLE_ID";
	public static final String ROLE_NAME_U = "ROLE_NAME";
	public static final String TITLE = "TITLE";
	public static final String CUSTOMER_IDS = "CUSTOMER_IDS";
	public static final String USER_PHONE = "USER_PHONE";
	public static final String CREATED_BY = "CREATED_BY";
	public static final String CREATE_DATE = "CREATE_DATE";
	public static final String UPDATE_BY = "UPDATE_BY";
	public static final String UPDATE_DATE = "UPDATE_DATE";
	
	/** Site related table or procedure fields/parameters **/
	public static final String IN_SITE_INTERNAL_ID = "in_site_internal_id";
	public static final String IN_USER_ID = "in_user_id";
	public static final String IN_CUSTOMER_ID = "in_customer_id";
	public static final String OUT_ERROR_MSG = "out_error_msg";
	public static final String OUT_FLAG = "out_flag";
	public static final String IN_SITE_ID = "in_site_id";
	public static final String IN_SITE_STATENAME = "in_site_statename";
	
	public static final String SITE_ID = "site_id";
	public static final String GROUP_ID = "group_id";
	public static final String GROUP_NAME = "group_name";
	public static final String CITY_ID = "city_id";
	public static final String CITY_NAME = "city_name";
	public static final String STATE_NAME = "state_name";
	public static final String STATE_CODE = "state";
	public static final String STATE_ID = "state_id";
	public static final String ST_ID = "st_id";
	public static final String DOW_DISPLAY_NAME = "dow_display_name";
	
	public static final String OUT_STATE_ID = "out_state_id";
	public static final String OUT_CITY_ID = "out_city_id";
	
}
