/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.daoimpl.admin;
  
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.admin.ChangePassword;
import com.enerallies.vem.beans.admin.Customer;
import com.enerallies.vem.beans.admin.Group;
import com.enerallies.vem.beans.admin.Site;
import com.enerallies.vem.beans.admin.UpdateEmailIdRequest;
import com.enerallies.vem.beans.admin.User;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.dao.admin.AdminDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.PasswordEncryptionProviderUsingSHA;
import com.enerallies.vem.util.TableFieldConstants;

/**
 * File Name : AdminDaoImpl 
 * 
 * AdminDaoImpl is the implementation file for Admin Prototype Methods
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        02-08-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	02-08-2016		Nagarjuna Eerla		File Created
 * 02	03-08-2016		Nagarjuna Eerla		Added saveUser Method
 * 03	04-08-2016		Nagarjuna Eerla		Added createNewPassword Method
 * 04	04-08-2016		Nagarjuna Eerla		Added forgotPassword Method
 * 05	05-08-2016		Nagarjuna Eerla		Added changePassword Method
 * 06	08-08-2016		Nagarjuna Eerla		Added getUserDetails Method
 * 07	09-08-2016		Nagarjuna Eerla		Added updateUserDetails Method
 * 08	10-08-2016		Nagarjuna Eerla		Added deleteUser Method
 * 09	11-08-2016		Nagarjuna Eerla		Added activateUser Method
 * 10	13-08-2016		Nagarjuna Eerla		Added inActivateUser Method
 * 11	18-08-2016		Nagarjuna Eerla		Added filterByStatus Method
 * 12	18-08-2016		Nagarjuna Eerla		Added filterByRole Method
 * 13	19-08-2016		Nagarjuna Eerla		Removing sonar qube issues
 */

@Component
public class AdminDaoImpl implements AdminDao{

	// Getting logger instance
	private static final Logger logger = Logger.getLogger(AdminDaoImpl.class);
	
	/** Data source instance */
	private DataSource dataSource;
	
	/** JDBC Template instance */
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}

	@Override
	public int saveUserDetails(User user, int loggedInUser) throws VEMAppException {

		logger.info("[BEGIN] [saveUserDetails] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_insert_user]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("userEmail :"+user.getEmailId());
			logger.debug("uname :"+user.getUname());
			logger.debug("roleId :"+user.getRoleId());
			logger.debug(CommonConstants.USER_FNAME_C + " : "+user.getFirstName());
			logger.debug("isActive :"+user.getIsActive());
			logger.debug("isFirstTimeUser :"+0);
			logger.debug("userLname :"+user.getLastName());
			logger.debug(CommonConstants.USER_PHONE_C + " : "+user.getPhoneNumber());
			logger.debug("createdBy :"+user.getCreateBy());
			logger.debug("createdDate :"+user.getCreateDate());
			logger.debug("customerIds :" +user.getCustomers());
			logger.debug("groupIds :"+user.getGroupId());
			logger.debug("siteIds :"+user.getLocationId());
			logger.debug(CommonConstants.ALERT_PREFERENCE_C + " : "+user.getAlertPreference());
			logger.debug("title :"+user.getTitle());
			logger.debug("reportPreference :"+user.getReportPreference());
			logger.debug("isOptedToSendMail :"+user.getIsOptedToSendMail());
			logger.debug("isTokenExpired :"+user.getIsTokenExpired());
			logger.debug("isEmailSent :"+user.getIsEmailSent());
			logger.debug("reportLevel :"+user.getReportLevel());
		}
		
		// Initializing the flag variable
		int flag = 0;
		
		// Declaring SimpleJdbcCall 
		SimpleJdbcCall simpleJdbcCall;
		
		try {

			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_insert_user");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("userEmail",user.getEmailId());
			parameters.put("uname",user.getUname());
			parameters.put(CommonConstants.PASSWORD_L,user.getPassword());
			parameters.put("roleId",user.getRoleId());
			parameters.put(CommonConstants.USER_FNAME_C,user.getFirstName());
			parameters.put("isActive",user.getIsActive());
			parameters.put("isFirstTimeUser",1);
			parameters.put(CommonConstants.USER_LNAME_C,user.getLastName());
			parameters.put(CommonConstants.USER_PHONE_C,user.getPhoneNumber());
			parameters.put("createdBy",user.getCreateBy());
			parameters.put("customerIds",user.getCustomers());
			parameters.put("groupIds",user.getGroupId());
			parameters.put("siteIds",user.getLocationId());
			parameters.put(CommonConstants.ALERT_PREFERENCE_C,user.getAlertPreference());
			parameters.put("title", user.getTitle());
			parameters.put("reportPreference", user.getReportPreference());
			parameters.put("isOptedToSendMail", user.getIsOptedToSendMail());
			parameters.put("randomKey", user.getAuthToken());
			parameters.put("isTokenExpired", user.getIsTokenExpired());
			parameters.put("isEmailSent", user.getIsEmailSent());
			parameters.put("rest_username", 
					PasswordEncryptionProviderUsingSHA.
					get_SHA_1_HashedString(
							ConfigurationUtils.generateRandomString(), user.getEmailId()));
			parameters.put("rest_password", PasswordEncryptionProviderUsingSHA.
					get_SHA_1_HashedString(
							ConfigurationUtils.generateRandomString(), user.getEmailId()));
			parameters.put("loggedInUser", loggedInUser);
			parameters.put("reportLevel",	user.getReportLevel());
			parameters.put("allGroupsSites", user.getIsUserOptedForAllGroups());
			
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created user id
			flag = Integer.parseInt(simpleJdbcCallResult.get(CommonConstants.USERID_L).toString());
	
			// Fetching the error log if any
			String errorFlag = simpleJdbcCallResult.get(CommonConstants.ERROR_MSG_L).toString();
			
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+errorMessage);
			}else if(StringUtils.isNotBlank(errorFlag)){
				logger.debug(CommonConstants.simpleJdbcCallResult+errorFlag);
			}
	
			/*
			 * if the flag is grater than 0 means 
			 * Successfully created the user
			 */
			if (flag > 0){
				logger.debug(CommonConstants.spOutputUserId+flag);
				logger.debug("Saved user Details sucesfully");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_SAVE_FAILED, logger, e);
			}
			
			logger.info("[END] [saveUserDetails] [DAO LAYER]");
			
			return flag;
	}

	@Override
	public int isEmailExist(String emailId) throws VEMAppException {
		
		logger.info("[BEGIN] [isEmailExist] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug("Executing the Query");
			logger.debug("Parameters:");
			logger.debug("emailId :"+emailId);
		}
		
		// flag initialization
		int flag = 0;
		
		// Declaring SimpleJdbcCall 
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			
			if(CommonUtility.isNull(emailId).isEmpty())
				return 0;
			
			/*// Query to find existence of user with email
			String sql = "SELECT count(`user_id`) as COUNT FROM `user` WHERE `user_email` = ? AND `is_active` <> 3"; //3 means deleted users
			
			// Executing the query
			String count = this.jdbcTemplate.queryForObject(sql,
					new Object[] { emailId }, new RowMapper<String>() {
						public String mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return rs.getString("COUNT");
						}
					});*/
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_is_email_exist");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("email",emailId);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the updated user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("dbFlag").toString());
			
			/*
			 * Checking the flag is valid or not
			 * if the flag is grater than 0 means success
			 * else fail
			 */
			if(flag > 0){
				flag = 1; // Record found
			}else{
				flag = 0; // Record Doesn't found
			}
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_EMAIL_EXIST_FAILED, logger, e);
		}
		
		logger.info("[END] [isEmailExist] [DAO LAYER]");
		
		return flag;
	}

	@Override
	public int changePassword(ChangePassword changePassword) throws VEMAppException {
		
		logger.info("[BEGIN] [changePassword] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_change_password]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug(CommonConstants.USERID_C + " : "+changePassword.getUserId());
			logger.debug(CommonConstants.UPDATEDBY_C +" : "+changePassword.getUpdateBy());
		}
		
		// Flag initialization
		int flag = 0;
		
		// Declaring SimpleJdbcCall 
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_change_password");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(CommonConstants.USERID_L,changePassword.getUserId());
			parameters.put(CommonConstants.PASSWORD_L,changePassword.getPassword());
			parameters.put(CommonConstants.UPDATEDBY_C,changePassword.getUpdateBy());
			parameters.put(CommonConstants.UPDATEDDATE_C,changePassword.getUpdateDate());
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the updated user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("updatedUserId").toString());
	
			// Fetching error message if any
			String errorFlag = simpleJdbcCallResult.get(CommonConstants.ERROR_MSG_L).toString();
			
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+errorMessage);
			}else if(StringUtils.isNotBlank(errorFlag)){
				logger.debug(CommonConstants.simpleJdbcCallResult+errorFlag);
			}
			
			/*
			 * if the flag is grater than 0 means 
			 * Successfully resets the password of the user
			 */
			if (flag > 0){
				logger.debug(CommonConstants.spOutputUserId+flag);
				logger.debug("Resets the password sucesfully");
			}
		
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED, logger, e);
		}
		
		logger.info("[END] [changePassword] [DAO LAYER]");
		
		return flag;
	}

	@Override
	public String getCurrentPassword(int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [getCurrentPassword] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug("Executing the Query");
			logger.debug("Parameters:");
			logger.debug("userId :"+userId);
		}
		
		// Declaration of currentPassword 
		String currentPassword;
		
		// Declaring SimpleJdbcCall 
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			
			/*// Query to get current password
			String sql = "SELECT `user_password` FROM `user` WHERE `user_id` = ?";
			
			// Executing query 
			currentPassword = this.jdbcTemplate.queryForObject(sql,
					new Object[] { userId }, new RowMapper<String>() {
						public String mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return rs.getString("PASSWORD");
						}
					});*/
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_get_user_password");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("userId",userId);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the updated user id
			currentPassword = simpleJdbcCallResult.get("dbFlag").toString();
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CURRENT_PWD_FAILED, logger, e);
		}
		
		logger.info("[END] [getCurrentPassword] [DAO LAYER]");
		
		return currentPassword;
	}

	@Override
	public int resetPasswordByMail(String emailId, String password, String randomKey) throws VEMAppException {

		logger.info("[BEGIN] [resetPasswordByMail] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_reset_password]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("emailId :"+emailId);
			logger.debug(CommonConstants.UPDATEDBY_C +" : "+1);
		}
		
		// Initialization of flag variable 
		int flag = 0;
		
		// Declaring SimpleJdbcCall 
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_reset_password");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("email",emailId);
			parameters.put("password",password);
			parameters.put("randomKey",randomKey);
			parameters.put(CommonConstants.UPDATEDBY_C,1);
			parameters.put(CommonConstants.UPDATEDDATE_C,new Date());
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the updated user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("successFlag").toString());
	
			// Fetching error message if any
			String errorFlag = simpleJdbcCallResult.get(CommonConstants.ERROR_MSG_L).toString();
			
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+errorMessage);
			}else if(StringUtils.isNotBlank(errorFlag)){
				logger.debug(CommonConstants.simpleJdbcCallResult+errorFlag);
			}
			
			/*
			 * If the flag is grater than 0 means 
			 * Successfully resets the password of the user
			 */
			if (flag > 0){
				logger.debug(CommonConstants.spOutputUserId+flag);
				logger.debug("Resets the password sucesfully");
			}
		
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHANGE_PWD_FAILED, logger, e);
		}
		
		logger.info("[END] [resetPasswordByMail] [DAO LAYER]");
		
		return flag;
	}

	@Override
	public User getUserDetails(String type, String value, int isSuper, int loggedInUserId, String timezone) throws VEMAppException {
		
		logger.info("[BEGIN] [getUserDetails] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_select_user_by_id]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("type :"+type);
			logger.debug("value :"+value);
		}
		
		// Users instance initialization
		List<User> users = null;
		
		try {
			
			// Passing store procedure as input
			users = jdbcTemplate.query("call sp_select_user_by_id ('"+type+"','"+value+"','"+isSuper+"','"+loggedInUserId+"','"+timezone+"', @error)", new RowMapper<User>() {
				public User mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					
					// creating user instance
					User user = new User();
					
					// assigning user details to user instance 
					user.setUserId(rs.getInt(TableFieldConstants.USER_ID));
					user.setEmailId(rs.getString(TableFieldConstants.USER_EMAIL));
					user.setUname(rs.getString(TableFieldConstants.USER_EMAIL));
					user.setFirstName(rs.getString(TableFieldConstants.USER_FNAME));
					user.setLastName(rs.getString(TableFieldConstants.USER_LNAME));
					user.setAlertPreference(rs.getInt(TableFieldConstants.ALERT_PREFERENCE));
					user.setIsActive(rs.getInt(TableFieldConstants.IS_ACTIVE));
					user.setGroupId(rs.getString(TableFieldConstants.GROUP_IDS));
					user.setLocationId(rs.getString(TableFieldConstants.SITE_IDS));
					user.setRoleId(rs.getInt(TableFieldConstants.ROLE_ID));
					user.setRoleName(rs.getString(TableFieldConstants.ROLE_NAME));
					user.setTitle(rs.getString(TableFieldConstants.TITLE));
					user.setCustomers(rs.getString(TableFieldConstants.CUSTOMER_IDS));
					user.setPhoneNumber(rs.getString(TableFieldConstants.USER_PHONE));
					user.setCreateBy(rs.getInt(TableFieldConstants.CREATED_BY));
					user.setCreateDate(rs.getDate(TableFieldConstants.CREATE_DATE));
					user.setUpdateBy(rs.getInt(TableFieldConstants.UPDATE_BY));
					user.setUpdateDate(rs.getDate(TableFieldConstants.UPDATE_DATE));
					user.setIsOptedToSendMail(rs.getInt("IS_OPTED_SEND_MAIL"));
					user.setReportPreference(rs.getInt("REPORT_PREFERENCE"));
					user.setIsEai(rs.getInt("IS_EAI"));
					user.setIsCSO(rs.getInt("IS_CSO"));
					user.setIsSuper(rs.getInt("IS_SUPER"));
					user.setIsFirstTimeUser(rs.getInt("IS_FIRST_TIME_USER"));
					user.setLastLoginInfo(rs.getTimestamp("LAST_LOGGED_IN"));
					user.setFailureCount(rs.getInt("FAILURE_COUNT"));
					user.setPassword(rs.getString("PASSWORD"));
					user.setIsTokenExpired(rs.getInt("IS_TOKEN_EXPIRED"));
					user.setSecurityFailCount(rs.getInt("SECURITY_FAIL_COUNT"));
					user.setIsEmailSent(rs.getInt("IS_EMAIL_SENT"));
					user.setRolePermissions(rs.getString("ROLE_PERMISSIONS"));
					user.setRestUserName(rs.getString("USER_REST_NAME"));
					user.setRestPassword(rs.getString("USER_REST_PWD"));
					user.setActivityLogCount(rs.getString("ACTIVITY_LOG_COUNT"));
					user.setCreateActivityLogFlag(rs.getString("CREATE_ACTIVITY_LOG_FLAG")); 
					user.setSiteCount(rs.getInt("SITE_COUNT"));
					user.setGroupCount(rs.getInt("GROUP_COUNT"));
					user.setCompanyLogo(rs.getString("CUSTOMER_LOGO"));
					user.setReportLevel(rs.getInt("REPORT_LEVEL"));
					user.setIsUserOptedForAllGroups(rs.getInt("ALL_GROUP_SITES_FLAG"));
					return user;
					
					}
				});
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_GET_FAILED, logger, e);
		}
		
		logger.info("[END] [getUserDetails] [DAO LAYER]");
		
		return users.get(0);
	}

	@Override
	public int updateUserDetails(User user, int loggedInUser) throws VEMAppException {
		
		logger.info("[BEGIN] [updateUserDetails] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_update_user]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug(CommonConstants.USERID_C+" : "+user.getUserId());
			logger.debug("roleId :"+user.getRoleId());
			logger.debug(CommonConstants.USER_FNAME_C+" :"+user.getFirstName());
			logger.debug("userLname :"+user.getLastName());
			logger.debug("isActive :"+user.getIsActive());
			logger.debug("userPhone :"+user.getPhoneNumber());
			logger.debug("updatedBy :"+user.getUpdateBy());
			logger.debug("customerIds :" +user.getCustomers());
			logger.debug("groupIds :"+user.getGroupId());
			logger.debug("siteIds :"+user.getLocationId());
			logger.debug("alertPreference :"+user.getAlertPreference());
			logger.debug("title :"+user.getTitle());
			logger.debug("reportPreference :"+user.getReportPreference());
			logger.debug("isOptedToSendMail :"+user.getIsOptedToSendMail());
			logger.debug("randomKey : " +user.getAuthToken());
			logger.debug("isTokenExpired :" +user.getIsTokenExpired());
			logger.debug("isEmailSent :" +user.getIsEmailSent());
			logger.debug("reportLevel :" +user.getReportLevel());
			logger.debug("allGroupsSites :" +user.getIsUserOptedForAllGroups());
		}
		
		// Initialization of flag variable 
		int flag = 0;
		
		// Declaring SimpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_update_user");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(CommonConstants.USERID_L,user.getUserId());
			parameters.put("roleId",user.getRoleId());
			parameters.put("isActive",user.getIsActive());
			parameters.put("userLname",user.getLastName());
			parameters.put("userFname",user.getFirstName());
			parameters.put("userPhone",user.getPhoneNumber());
			parameters.put("updatedBy",user.getUpdateBy());
			parameters.put("customerIds",user.getCustomers());
			parameters.put("groupIds",user.getGroupId());
			parameters.put("siteIds",user.getLocationId());
			parameters.put("alertPreference",user.getAlertPreference());
			parameters.put("title", user.getTitle());
			parameters.put("reportPreference", user.getReportPreference());
			parameters.put("isOptedToSendMail", user.getIsOptedToSendMail());
			parameters.put("randomKey", user.getAuthToken());
			parameters.put("isTokenExpired", user.getIsTokenExpired());
			parameters.put("isEmailSent", user.getIsEmailSent());
			parameters.put("loggedInUser", loggedInUser);
			parameters.put("reportLevel", user.getReportLevel());
			parameters.put("allGroupsSites", user.getIsUserOptedForAllGroups());
			
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// fetching the updated user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("updatedUserId").toString());
	
			// Fetching error message if any
			String errorFlag = simpleJdbcCallResult.get("error_msg").toString();
			
			// checking the flag is valid or not
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+" : "+errorMessage);
			}else if(StringUtils.isNotBlank(errorFlag)){
				logger.debug(CommonConstants.simpleJdbcCallResult+" : "+errorFlag);
			}
			
			/*
			 * If the flag is grater than 0 means 
			 * Successfully updated the user details
			 */
			if (flag > 0){
				logger.debug(CommonConstants.spOutputUserId+" : "+flag);
				logger.debug("Updated user Details sucesfully");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_UPDATE_FAILED, logger, e);
			}
			
		logger.info("[END] [updateUserDetails] [DAO LAYER]");
		
		return flag;
	}

	@Override
	public List<User> getAllUsersDetails(String type, String value, int isSuper, int loggedInUserId, String timezone) throws VEMAppException {
		
		logger.debug("[BEGIN] [getAllUsersDetails] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_select_users]");
		}
		
		// UserList instance initialization
		List<User> usersList = null;
		
		try {
			// Passing store procedure as input. And here user id '0' is to get all users.
			usersList = jdbcTemplate.query("call sp_select_users ('ALL','"+type+"~"+value+"','"+isSuper+"','"+loggedInUserId+"', '"+timezone+"', @error)", new RowMapper<User>() {
				public User mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					
					// creating user instance
					User user = new User();
					
					// assigning user details to user instance   
					user.setUserId(rs.getInt(TableFieldConstants.USER_ID));
					user.setEmailId(rs.getString(TableFieldConstants.USER_EMAIL));
					user.setUname(rs.getString(TableFieldConstants.USER_EMAIL));
					user.setFirstName(rs.getString(TableFieldConstants.USER_FNAME));
					user.setLastName(rs.getString(TableFieldConstants.USER_LNAME));
					user.setAlertPreference(rs.getInt(TableFieldConstants.ALERT_PREFERENCE));
					user.setIsActive(rs.getInt(TableFieldConstants.IS_ACTIVE));
					user.setGroupId(rs.getString(TableFieldConstants.GROUP_IDS));
					user.setLocationId(rs.getString(TableFieldConstants.SITE_IDS));
					user.setRoleId(rs.getInt(TableFieldConstants.ROLE_ID));
					user.setRoleName(rs.getString(TableFieldConstants.ROLE_NAME));
					user.setTitle(rs.getString(TableFieldConstants.TITLE));
					user.setCustomers(rs.getString(TableFieldConstants.CUSTOMER_IDS));
					user.setPhoneNumber(rs.getString(TableFieldConstants.USER_PHONE));
					user.setCreateBy(rs.getInt(TableFieldConstants.CREATED_BY));
					user.setCreateDate(rs.getDate(TableFieldConstants.CREATE_DATE));
					user.setUpdateBy(rs.getInt(TableFieldConstants.UPDATE_BY));
					user.setUpdateDate(rs.getDate(TableFieldConstants.UPDATE_DATE));
					user.setIsOptedToSendMail(rs.getInt("IS_OPTED_SEND_MAIL"));
					user.setReportPreference(rs.getInt("REPORT_PREFERENCE"));
					user.setIsEai(rs.getInt("IS_EAI"));
					user.setIsCSO(rs.getInt("IS_CSO"));
					user.setIsSuper(rs.getInt("IS_SUPER"));
					user.setIsFirstTimeUser(rs.getInt("IS_FIRST_TIME_USER"));
					user.setLastLoginInfo(rs.getTimestamp("LAST_LOGGED_IN"));
					user.setIsTokenExpired(rs.getInt("IS_TOKEN_EXPIRED"));
					user.setIsEmailSent(rs.getInt("IS_EMAIL_SENT"));
					user.setCreateActivityLogFlag(rs.getString("CREATE_ACTIVITY_LOG_FLAG"));
					user.setSiteCount(rs.getInt("SITE_COUNT"));
					user.setGroupCount(rs.getInt("GROUP_COUNT"));
					user.setReportLevel(rs.getInt("REPORT_LEVEL"));
					return user;
					}
				});
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_GET_FAILED, logger, e);
		}
		if (logger.isDebugEnabled()){
			logger.debug("[END] [getAllUsersDetails] [DAO LAYER]");
		}
		return usersList;
	}

	@Override
	public int deleteUserDetails(int userId, int loggedInUser) throws VEMAppException {
		
		logger.info("[BEGIN] [deleteUserDetails] [DAO LAYER]");
		
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_update_user_status]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug(CommonConstants.USERID_L+" : "+userId);
		}
		
		// Initialization of flag variable
		int flag = 0;
		
		// Declaring SimpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_update_user_status");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("userid",userId);
			parameters.put("status",3); // 3 means logical deletion
			parameters.put("updatedBy", userId);
			parameters.put("loggedInUser", loggedInUser);
			
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("userActivity").toString());
	
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+errorMessage);
			}
	
			/*
			 * If the flag is grater than 0 means 
			 * Successfully activates / in-activates / Deletes the user
			 */
			if (flag > 0){
				logger.debug("SP OUTPUT UserId : "+flag);
				logger.debug("Deleted user sucesfully");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_DELETE_FAILED, logger, e);
			}
			
			logger.info("[END] [deleteUserDetails] [DAO LAYER]");
			
			return flag;
	}

	@Override
	public int userAcitivity(int userId, int status, int loggedInUser) throws VEMAppException {
		
		logger.info("[BEGIN] [userAcitivity] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_update_user_status]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("userid :"+userId);
			logger.debug("status :"+status);
			logger.debug("updatedBy :"+userId);
		}
		
		// Initialization of flag variable
		int flag = 0;
		
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_update_user_status");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("userid",userId);
			parameters.put("status",status);
			parameters.put("updatedBy", userId);
			parameters.put("loggedInUser", loggedInUser);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("userActivity").toString());
	
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+errorMessage);
			}
	
			/*
			 * If the flag is grater than 0 means 
			 * Successfully activates / in-activates the user
			 */
			if (flag > 0){
				logger.debug("SP OUTPUT UserId : "+flag);
				logger.debug("Activates / inactivates user sucesfully");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_ACTIVITY_FAILED, logger, e);
			}
			
			logger.info("[END] [userAcitivity] [DAO LAYER]");

			return flag;
	}
	
	@Override
	public int updateMyProfile(User user) throws VEMAppException {
		
		logger.info("[BEGIN] [updateMyProfile] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_update_profile]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("userid :"+user.getUserId());
			logger.debug("userFname :"+user.getFirstName());
			logger.debug("userLname :"+user.getLastName());
			logger.debug("userPhone :"+user.getPhoneNumber());
			logger.debug("alertPreference :"+user.getAlertPreference());
		}
		
		// Initialization of flag variable
		int flag = 0;
		
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_update_profile");
			
			// parameters instance
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("userid",user.getUserId());
			parameters.put("userLname",user.getLastName());
			parameters.put("userFname",user.getFirstName());
			parameters.put("userPhone",user.getPhoneNumber());
			parameters.put("alertPreference",user.getAlertPreference());
			parameters.put("reportPreference",user.getReportPreference());
			parameters.put("reportLevel",user.getReportLevel());
			
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// fetching the created user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("out_flag").toString());
	
			String errorFlag = simpleJdbcCallResult.get("error_msg").toString();
			
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug("simpleJdbcCallResult :: "+errorMessage);
			}else if(StringUtils.isNotBlank(errorFlag)){
				logger.debug("simpleJdbcCallResult :: "+errorFlag);
			}
			
			/*
			 * If the flag is grater than 0 means 
			 * Successfully Updated user profile 
			 */
			if (flag > 0){
				logger.debug("SP OUTPUT update flag : "+flag);
				logger.debug("Updated user profile sucesfully");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_UPDATE_PROFILE_FAILED, logger, e);
			}
			
			logger.info("[END] [updateMyProfile] [DAO LAYER]");
			
			return flag;
	}

	@Override
	public List<User> filterByCustomer(int customerId) throws VEMAppException {
		/**
		 * Implementation of filterByCustomer
		 */
		return new ArrayList<>();
	}

	@Override
	public String validateUserDetails(String userName, String password) throws VEMAppException {

		logger.info("[BEGIN] [validateUserDetails] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_validate_user]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("userEmail :"+userName);
			logger.debug("updatedDate :"+new Date());
		}
		
		// Initialization of flag variable
		int flag = 0;
		
		// Initialization for key
		String key = "";
		
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_validate_user");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("userEmail",userName);
			parameters.put("password",password);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created db flag
			key = simpleJdbcCallResult.get("dbFlag").toString();
			
			// Flag to decide success or failure
			flag = Integer.parseInt(simpleJdbcCallResult.get("dbFlag").toString().split("~")[0]);
	
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+errorMessage);
			}
	
			logger.info("[INFO] [validateUserDetails][ DB FLAG IS ]:: "+key);
			
			/*
			 * If the flag is grater than 0 means 
			 * Successfully validated the user
			 */
			if (flag > 0){
				logger.debug("SP OUTPUT UserId : "+flag);
				logger.debug("User validated successfully");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_USER_VALIDATE_FAILED, logger, e);
			}
			
			logger.info("[END] [validateUserDetails] [DAO LAYER]");

			return key;
	}

	@Override
	public int isUserLocked(String email) throws VEMAppException {
		
		logger.info("[BEGIN] [isUserLocked] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_fetch_user_status]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("userEmail :"+email);
		}
		
		// Initialization of flag variable
		int flag = 0;
		
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_fetch_user_status");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("userEmail",email);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created dbFlag
			flag = Integer.parseInt(simpleJdbcCallResult.get("status").toString());
	
			/*
			 * if the flag is 2 
			 * means user is locked else
			 * unlocked
			 */
			if(flag == 2){
				flag = 1;
			}else{
				flag = 0;
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_CHECK_IS_LOCKED_FAILED, logger, e);
			}
			
			logger.info("[END] [isUserLocked] [DAO LAYER]");

			return flag;
	}

	@Override
	public int isUserEligibleToReleaseLock(String email) throws VEMAppException {

		logger.info("[BEGIN] [isUserEligibleToReleaseLock] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_release_lock]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("userEmail :"+email);
		}
		
		// Initialization of flag variable
		int flag = 0;
		
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_release_lock");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("userEmail",email);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created dbFlag
			flag = Integer.parseInt(simpleJdbcCallResult.get("dbFlag").toString());
	
			/*
			 * If the flag is less than 0 means
			 * User is not eligible to release lock
			 */
			if(flag < 1){
				logger.debug("User is not eligible to release lock");
			}
	
			/*
			 * If the flag is grater than 0 means
			 * User is eligible to release lock and released the lock
			 */
			if (flag > 0){
				logger.debug("User is eligible to release lock and released the lock");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_RELEASE_LOCK_FAILED, logger, e);
			}
			
			logger.info("[END] [isUserEligibleToReleaseLock] [DAO LAYER]");

			return flag;
	}

	@Override
	public String validateSecurityQuestions(String phoneNumber, String token) throws VEMAppException {
		
		logger.info("[BEGIN] [validateSecurityQuestions] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_validate_security_questions]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("token :"+token);
			logger.debug("phoneNumber :"+phoneNumber);
		}
		
		// Initialization of flag variable
		int flag = 0;
		
		// Initialization for key
		String key = "";
		
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_validate_security_questions");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("phoneNumber",phoneNumber);
			parameters.put("token",token);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created db flag
			key = simpleJdbcCallResult.get("dbFlag").toString();
			
			// Flag to decide success or failure
			flag = Integer.parseInt(simpleJdbcCallResult.get("dbFlag").toString().split("~")[0]);
	
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+errorMessage);
			}
	
			logger.info("[INFO] [validateSecurityQuestions][ DB FLAG IS ]:: "+key);
			
			/*
			 * If the flag is grater than 0 means 
			 * Successfully validated the user
			 */
			if (flag > 0){
				logger.debug("SP OUTPUT UserId : "+flag);
				logger.debug("User validated security questions successfully");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_USER_VALIDATE_FAILED, logger, e);
			}
			
			logger.info("[END] [validateSecurityQuestions] [DAO LAYER]");

			return key;
	}

	@Override
	public int isTokenExpired(String token, String tokenType) throws VEMAppException {
		
		logger.info("[BEGIN] [isTokenExpired] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug("Executing the Query");
			logger.debug("Parameters:");
			logger.debug("token :"+token);
			logger.debug("tokenType : "+tokenType);
		}
		
		// flag initialization
		int flag = 0;
		
		// Declaring SimpleJdbcCall 
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_is_token_expired");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("token",token);
			parameters.put("tokenType", tokenType);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the updated user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("dbFlag").toString());
			
			if(StringUtils.contains(simpleJdbcCallResult.get("error_msg").toString(), "No data - zero rows fetched, selected, or processed")){
				flag = 2; // No records found with that record
			}
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.TOKEN_EXPIRY_CHECK_FAILED, logger, e);
		}
		
		logger.info("[END] [isTokenExpired] [DAO LAYER]");
		
		return flag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray getSitesGroupsByCustomers(int selectedUserId, int loggedInUserId, int isSuper, String customerIds) throws VEMAppException {
		logger.info("[BEGIN] [getSitesGroupsByCustomers] [User DAO LAYER]");
		
		/*
		 * Temporary variables 
		 */
		HashMap<Integer, JSONObject> customersMap=new HashMap<>();
		HashMap<Integer, JSONObject> groupsMap=new HashMap<>();
		JSONArray customersArray = new JSONArray();
		
		try {
			
			logger.debug("[DEBUG] Executing sp_get_groups_sites_by_customers procedure.");
			logger.debug("[DEBUG] Input param select userId-"+selectedUserId);
			logger.debug("[DEBUG] Input param customerIds-"+customerIds);
			logger.debug("[DEBUG] Input param logged in user-"+loggedInUserId);
			logger.debug("[DEBUG] Input param isSuper-"+isSuper);
			
			jdbcTemplate.query("call sp_get_groups_sites_by_customers ('"+selectedUserId+"', '"+customerIds+"', '"+loggedInUserId+"', '"+isSuper+"')", new RowCallbackHandler() {
				/*
				 * Temporary variables.
				 */
				JSONObject customersObj = new JSONObject();
				JSONArray groupArray= new JSONArray();
				JSONObject groupObj = new JSONObject();
				JSONArray siteArray= new JSONArray();
				JSONObject siteObj = new JSONObject();
				
				@SuppressWarnings("unchecked")
				@Override
			    public void processRow(ResultSet rs) throws SQLException {

			    	logger.debug("[DEbug] the group_id - "+rs.getString(TableFieldConstants.GROUP_ID));
			    	
			    	/*
			    	 * If the GroupsMap is empty the insert new group object
			    	 * else update the group object.
			    	 */
			    	if(customersMap.containsKey(rs.getInt("customer_id"))){
			    		
			    		customersObj = customersMap.get(rs.getInt("customer_id"));
						
			    		if(groupsMap.containsKey(rs.getInt("group_id"))){
			    			groupObj = groupsMap.get(rs.getInt("group_id"));
			    			siteArray = (JSONArray) groupObj.get("sitesList");
			    			groupArray.remove(groupObj);
				    		siteObj = new JSONObject();
				    		if(StringUtils.isNotBlank(rs.getString("site_name"))){
				    			siteObj.put("siteId", CommonUtility.isNull(rs.getInt("site_id")));
					    		siteObj.put("siteName", CommonUtility.isNull(rs.getString("site_name")));
					    		siteArray.add(siteObj);
				    		}
				    		groupObj.put("sitesList", siteArray);
				    		groupArray.add(groupObj);
			    		}else{
			    			siteArray = new JSONArray();
				    		siteObj = new JSONObject();
				    		if(StringUtils.isNotBlank(rs.getString("site_name"))){
				    			siteObj.put("siteId", CommonUtility.isNull(rs.getInt("site_id")));
					    		siteObj.put("siteName", CommonUtility.isNull(rs.getString("site_name")));
					    		siteArray.add(siteObj);
				    		}
				    		
				    		groupObj = new JSONObject();
				    		groupObj.put("groupId", CommonUtility.isNull(rs.getInt("group_id")));
				    		groupObj.put("groupName", CommonUtility.isNull(rs.getString("group_name")));
				    		groupObj.put("sitesList", siteArray);
				    		groupsMap.put(CommonUtility.isNull(rs.getInt("group_id")), groupObj);
				    		groupArray.add(groupObj);
			    		}
			    		
			    		customersObj.put("groupsList",groupArray);
				    	
				    	customersMap.put(CommonUtility.isNull(rs.getInt("customer_id")), customersObj);
			    		
			    		
			    	}else{
			    		
			    		customersObj = new JSONObject();
			    		customersObj.put("customerId", CommonUtility.isNull(rs.getInt("customer_id")));
			    		customersObj.put("customerName", CommonUtility.isNull(rs.getString("company_name")));
			    		
			    		siteArray = new JSONArray();
			    		siteObj = new JSONObject();
			    		if(StringUtils.isNotBlank(rs.getString("site_name"))){
			    			siteObj.put("siteId", CommonUtility.isNull(rs.getInt("site_id")));
				    		siteObj.put("siteName", CommonUtility.isNull(rs.getString("site_name")));
				    		siteArray.add(siteObj);
			    		}
			    		
			    		groupArray = new JSONArray();
			    		groupObj = new JSONObject();
			    		groupObj.put("groupId", CommonUtility.isNull(rs.getInt("group_id")));
			    		groupObj.put("groupName", CommonUtility.isNull(rs.getString("group_name")));
			    		groupObj.put("sitesList", siteArray);
			    		groupsMap.put(CommonUtility.isNull(rs.getInt("group_id")), groupObj);
			    		groupArray.add(groupObj);
			    		
			    		customersObj.put("groupsList",groupArray);
				    	
				    	customersMap.put(CommonUtility.isNull(rs.getInt("customer_id")), customersObj);
				    	
			    	}
			    	
				}
			});
			
	    	for (Map.Entry<Integer, JSONObject> entry : customersMap.entrySet()) {
	    		customersArray.add((JSONObject)entry.getValue());
	    	}
	    	
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GET_SITES_BY_GROUP_IDS_FAILED, logger, e);
		}
		
		logger.info("[END] [getSitesGroupsByCustomers] [USER DAO LAYER]");
		
		return customersArray;
	}
	@Override
	public String uploadUserData(String role, String assignedCustomer, String assginedGroup, String assignedSite) throws VEMAppException {
		
		logger.info("[BEGIN] [uploadUserData] [DAO LAYER]");
		
		// Creating response instance
		 Response response = new Response();
		// flag initialization
		int flag = 0;
		// Initialization for key
		String key = "";
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_upload_user_data");
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("roleName",role.toUpperCase());
			parameters.put("assginedGroup",assginedGroup.toUpperCase());
			parameters.put("assginedSite",assignedSite.toUpperCase());
			parameters.put("assginedCustomer",assignedCustomer.toUpperCase());
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			// Fetching the created db flag
			key = simpleJdbcCallResult.get("dbFlag").toString();
			/*
			 * Checking the flag is valid or not
			 * if the flag is grater than 0 means success
			 * else fail
			 */
			if(flag > 0){
				flag = 1; // Record found
			}else{
				flag = 0; // Record Doesn't found
			}
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_EMAIL_EXIST_FAILED, logger, e);
		}
		logger.info("[END] [uploadUserData] [DAO LAYER]");
		return key;
	}

	@Override
	public User getUserDetailsByEmail(String emailId) throws VEMAppException {
		
		logger.info("[BEGIN] [getUserDetailsByEmail] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_select_users_by_mail]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("emailId :"+emailId);
		}
		
		// Users instance initialization
		List<User> users = null;
		
		try {
			
			// Passing store procedure as input
			users = jdbcTemplate.query("call sp_select_users_by_mail ('"+emailId+"', @error)", new RowMapper<User>() {
				public User mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					
					// creating user instance
					User user = new User();
					
					// assigning user details to user instance 
					user.setUserId(rs.getInt(TableFieldConstants.USER_ID));
					user.setEmailId(rs.getString(TableFieldConstants.USER_EMAIL));
					user.setUname(rs.getString(TableFieldConstants.USER_EMAIL));
					user.setFirstName(rs.getString(TableFieldConstants.USER_FNAME));
					user.setLastName(rs.getString(TableFieldConstants.USER_LNAME));
					user.setAlertPreference(rs.getInt(TableFieldConstants.ALERT_PREFERENCE));
					user.setIsActive(rs.getInt(TableFieldConstants.IS_ACTIVE));
					user.setRoleId(rs.getInt(TableFieldConstants.ROLE_ID));
					user.setRoleName(rs.getString(TableFieldConstants.ROLE_NAME));
					user.setIsOptedToSendMail(rs.getInt("IS_OPTED_SEND_MAIL"));
					user.setReportPreference(rs.getInt("REPORT_PREFERENCE"));
					user.setIsEai(rs.getInt("IS_EAI"));
					user.setIsCSO(rs.getInt("IS_CSO"));
					user.setIsSuper(rs.getInt("IS_SUPER"));
					user.setIsFirstTimeUser(rs.getInt("IS_FIRST_TIME_USER"));
					user.setLastLoginInfo(rs.getTimestamp("LAST_LOGGED_IN"));
					user.setFailureCount(rs.getInt("FAILURE_COUNT"));
					user.setIsTokenExpired(rs.getInt("IS_TOKEN_EXPIRED"));
					user.setSecurityFailCount(rs.getInt("SECURITY_FAIL_COUNT"));
					user.setIsEmailSent(rs.getInt("IS_EMAIL_SENT"));
					user.setRestUserName(rs.getString("USER_REST_NAME"));
					user.setRestPassword(rs.getString("USER_REST_PWD"));
					return user;
					
					}
				});
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_GET_FAILED, logger, e);
		}
		
		logger.info("[END] [getUserDetailsByEmail] [DAO LAYER]");
		
		return users.get(0);
	}

	@Override
	public String getCustomerSiteIds(int userId, int isSuper, int loggedInUserId) throws VEMAppException {
		
		logger.info("[BEGIN] [getCustomerSiteIds] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_cust_site_ids]");
			logger.debug(CommonConstants.PARAMETERS);
		}
		
		// Initialization of custSiteIds
		String custSiteIds = "";
		
		try {
			
			// Passing store procedure as input
			custSiteIds = jdbcTemplate.queryForObject("call sp_get_rpt_cust_site_ids ('"+
					userId+"','"+
					isSuper+"','"+
					loggedInUserId+"')", new RowMapper<String>() {
				public String mapRow(ResultSet rs, int rowNum)
						throws SQLException {
						return rs.getString("cust_site_ids");
					}
				});
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.FETCH_CUSTOMER_SITE_IDS, logger, e);
		}
		
		logger.info("[END] [getCustomerSiteIds] [DAO LAYER]");
		
		return custSiteIds;
	}

	@Override
	public List<Customer> getCustomers(String customerIds) throws VEMAppException {
		logger.info("[BEGIN] [getCustomers] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_cust_site_ids]");
			logger.debug(CommonConstants.PARAMETERS);
		}
		
		// Initialization of customers
		List<Customer> customers = null;
		
		try {
			
			// Passing store procedure as input
			customers = jdbcTemplate.query("call sp_get_rpt_cust_site_ids ('"+
					customerIds+"')", new RowMapper<Customer>() {
				public Customer mapRow(ResultSet rs, int rowNum)
						throws SQLException {
						Customer customer = new Customer();
						customer.setCustomerId(rs.getString(""));
						customer.setCustomerName(rs.getString(""));
						return customer;
					}
				});
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.FETCH_CUSTOMER_SITE_IDS, logger, e);
		}
		
		logger.info("[END] [getCustomers] [DAO LAYER]");
		
		return customers;
	}

	@Override
	public List<Group> getGroups(String groupIds) throws VEMAppException {
		logger.info("[BEGIN] [getGroups] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_cust_site_ids]");
			logger.debug(CommonConstants.PARAMETERS);
		}
		
		// Initialization of customers
		List<Group> groups = null;
		
		try {
			
			// Passing store procedure as input
			groups = jdbcTemplate.query("call sp_get_rpt_cust_site_ids ('"+
					groupIds+"')", new RowMapper<Group>() {
				public Group mapRow(ResultSet rs, int rowNum)
						throws SQLException {
						Group group = new Group();
						group.setGroupId(rs.getString(""));
						group.setGroupName(rs.getString(""));
						return group;
					}
				});
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.FETCH_CUSTOMER_SITE_IDS, logger, e);
		}
		
		logger.info("[END] [getGroups] [DAO LAYER]");
		
		return groups;
	}

	@Override
	public List<Site> getSites(String siteIds) throws VEMAppException {
		logger.info("[BEGIN] [getSites] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_cust_site_ids]");
			logger.debug(CommonConstants.PARAMETERS);
		}
		
		// Initialization of customers
		List<Site> sites = null;
		
		try {
			
			// Passing store procedure as input
			sites = jdbcTemplate.query("call sp_get_rpt_cust_site_ids ('"+
					siteIds+"')", new RowMapper<Site>() {
				public Site mapRow(ResultSet rs, int rowNum)
						throws SQLException {
						Site site = new Site();
						site.setSiteId(rs.getString(""));
						site.setSiteName(rs.getString(""));
						return site;
					}
				});
			
		} catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.FETCH_CUSTOMER_SITE_IDS, logger, e);
		}
		
		logger.info("[END] [getSites] [DAO LAYER]");
		
		return sites;
	}
	
	@Override
	public String getCompanyLogo(int userId) throws VEMAppException{
		String companyLogo = "";
		try {
			companyLogo = jdbcTemplate.queryForObject("select fn_get_company_logo(?)", String.class, userId);
		} catch (Exception e) {
			throw new VEMAppException("Error in getCompanyLogo",e);
		}
		return companyLogo;
	}

	@Override
	public int updateEmailId(UpdateEmailIdRequest updateEmailIdRequest) throws VEMAppException {
		logger.info("[BEGIN] [updateEmailId] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_update_user_email]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug(CommonConstants.USERID_C+" : "+updateEmailIdRequest.getUserId());
			logger.debug("isOptedToSendMail :"+updateEmailIdRequest.getIsOptedToSendMail());
			logger.debug("emailToChange : "+updateEmailIdRequest.getToChangeEmailId());
			logger.debug("fromEmail : "+updateEmailIdRequest.getFromEmailId());
			logger.debug("loggedInUser : "+updateEmailIdRequest.getLoggedInUserId());
		}
		
		// Initialization of flag variable 
		int flag = 0;
		
		// Declaring SimpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_update_user_email");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(CommonConstants.USERID_L,updateEmailIdRequest.getUserId());
			parameters.put("isOptedToSendMail",updateEmailIdRequest.getIsOptedToSendMail());
			parameters.put("randomKey",updateEmailIdRequest.getAuthToken());
			parameters.put("emailToChange",updateEmailIdRequest.getToChangeEmailId());
			parameters.put("loggedInUser",updateEmailIdRequest.getLoggedInUserId());
			
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// fetching the updated user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("updatedUserId").toString());
	
			// Fetching error message if any
			String errorFlag = simpleJdbcCallResult.get("error_msg").toString();
			
			// checking the flag is valid or not
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+" : "+errorMessage);
			}else if(StringUtils.isNotBlank(errorFlag)){
				logger.debug(CommonConstants.simpleJdbcCallResult+" : "+errorFlag);
			}
			
			/*
			 * If the flag is grater than 0 means 
			 * Successfully updated the user details
			 */
			if (flag > 0){
				logger.debug(CommonConstants.spOutputUserId+" : "+flag);
				logger.debug("Updated user email id sucesfully");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_UPDATE_EMAIL_FAILED, logger, e);
			}
			
		logger.info("[END] [updateEmailId] [DAO LAYER]");
		
		return flag;
	}

}
