/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * File Name : GetUserResponse 
 * GetUserResponse: is used to transfer User data from server to client
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 03-08-2016		Nagarjuna Eerla		File Created
 *
 */
public class GetUserResponse {
	
	/** This property is used to hold the user id */
	private Integer userId;
	
	/** This property is used to hold the user name */
	private String uname;
	
	/** This property is used to hold the user id. */
	private String firstName;
	
	/** This property is used to hold the last name */
	private String lastName;
	
	/** This property is used to hold the title name */
	private String title;
	
	/** This property is used to hold the first name */
	private int isActive;
	
	/** This property is used to hold the timeZone of the user*/
	private String timeZone;

	/** This property is used to hold the user password. */
	private String password;
	
	/** This property is used to hold the contact number of the user. */
	private String phoneNumber;
	
	/** This property is used to hold the email id of the user. */
	private String emailId;

	/** This property is used to hold the status of user whether user is active or not. */
	private String status;
	
	/** This property is used to hold the role of the user */
	private int roleId;

	/** This property is used to hold the role name */
	private String roleName;
	
	/** This property is used to hold the customers */
	private String customers;
	
	/** This property is used to hold the group id */
	private String groupId;
	
	/** This property is used to hold the location id */
	private String locationId;
	
	/** This property is used to hold the alert preferences */
	private int alertPreference;
	
	/** This property is used to hold the user who is created the user. */
	private int createBy;
	
	/** This property is used to hold the created date on which the user got created. */
	private Date createDate;
	
	/** This property is used to hold the user who is updated the user. */
	private int updateBy;
	
	/** This property is used to hold the updated date on which the user got updated. */
	private Date updateDate;
	
	/** This property is used to hold the last login info */
	private String lastLoginInfo;
	
	/** This property is used to hold the value of is opted to send mail while creating the user */
	private int isOptedToSendMail;
	
	/** This property is used to hold the value of report preference for the user */
	private int reportPreference;
	
	/** This property is used to hold the value of isEai property for the user */
	private int isEai;
	
	/** This property is used to hold the value of isCSO property for the user */
	private int isCSO;
	
	/** This property is used to hold the value of isSuper property for the user */
	private int isSuper;
	
	/** This property is used to hold the isFirstTimeUser or not */
	private int isFirstTimeUser;
	
	/** This property is used to hold the failureCount */
	private int failureCount;
	
	/** This property is used to hold the isTokenExpired */
	private int isTokenExpired;
	
	/** It gets the customers list to this user id **/
	private Object customersList;
	
	/** This property is used to hold whether the user received email or not **/
	private int isEmailSent;
	
	/** This property is used to hold rolePermissions **/
	private Map<String, String> rolePermissions = new HashMap<>();
	
	/** This property is used to hold restUserName **/
	private String restUserName;
	
	/** This property is used to hold restPassword **/
	private String restPassword;
	
	/**  This property is used to hold the activityLogCount **/
	private String activityLogCount;
	
	/**  This property is used to hold the createActivityLogFlag **/
	private String createActivityLogFlag;

	private int groupCount;
	
	private int siteCount;
	
	private String custSiteIds;
	
	private String companyLogo;

	/** This property is used to hold the property to say the user is opted for all the groups or not  */
	private int isUserOptedForAllGroups;
	
	/** This property is used to hold the value of report level for the user  */
	private int reportLevel;
	
	//*********************************** Getters ***********************//
	
	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	/**
	 * Gets the activityLogCount property
	 * @return
	 */
	public void setActivityLogCount(String activityLogCount) {
		this.activityLogCount = activityLogCount;
	}
	
	/**
	 * Gets the restUserName property
	 * @return
	 */
	public String getRestUserName() {
		return restUserName;
	}

	/**
	 * Gets the restPassword property
	 * @return
	 */
	public String getRestPassword() {
		return restPassword;
	}

	/**
	 * Gets the isEai property
	 * @return
	 */
	public int getIsEai() {
		return isEai;
	}

	/**
	 * Gets the isSuper property
	 * @return
	 */
	public int getIsSuper() {
		return isSuper;
	}

	/**
	 * Gets the isCSO property
	 * @return
	 */
	public int getIsCSO() {
		return isCSO;
	}
	
	/**
	 * Gets the value of isOptedToSendMail property
	 * @return
	 */
	public int getIsOptedToSendMail() {
		return isOptedToSendMail;
	}

	/**
	 * Gets the value of reportPreference property
	 * @return
	 */
	public int getReportPreference() {
		return reportPreference;
	}
	
	/**
	 * Gets the user id
	 * @return
	 */
	public Integer getUserId() {
		return userId;
	}
	
	/**
	 * gets password
	 * @return String
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * gets contact number
	 * @return String
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	/**
	 * gets email id
	 * @return String
	 */
	public String getEmailId() {
		return emailId;
	}
	
	/**
	 * gets status of user
	 * @return String
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Gets the created by details
	 * @return
	 */
	public int getCreateBy() {
		return createBy;
	}

	/**
	 * Gets the user created date
	 * @return
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * Gets the Updated by 
	 * @return
	 */
	public int getUpdateBy() {
		return updateBy;
	}
	
	/**
	 * Gets the updated date
	 * @return
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	
	/**
	 * Gets the first name of the user
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Gets the last name of the user
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Gets the isActive status
	 * @return
	 */
	public Integer getIsActive() {
		return isActive;
	}

	/**
	 * Gets the timeZone
	 * @return
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * Gets the role of the user
	 * @return
	 */
	public Integer getRoleId() {
		return roleId;
	}
	
	/**
	 * Gets the group id
	 * @return
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Gets the location id
	 * @return
	 */
	public String getLocationId() {
		return locationId;
	}
	
	/**
	 * Gets the unique user name
	 * @return
	 */
	public String getUname() {
		return uname;
	}

	/**
	 * Gets the alert preference
	 * @return
	 */
	public int getAlertPreference() {
		return alertPreference;
	}

	/**
	 * Gets the last login information
	 * @return
	 */
	public String getLastLoginInfo() {
		return lastLoginInfo;
	}
	
	/**
	 * Gets the customers
	 * @return
	 */
	public String getCustomers() {
		return customers;
	}

	/**
	 * Gets the role name
	 * @return
	 */
	public String getRoleName() {
		return roleName;
	}
	
	/**
	 * Gets the title name
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the isFirstTimeUser property
	 * @return
	 */
	public int getIsFirstTimeUser() {
		return isFirstTimeUser;
	}

	/**
	 * Gets the failureCount property
	 * @param failureCount
	 */
	public int getFailureCount() {
		return failureCount;
	}
	
	/**
	 * Gets the isTokenExpired property
	 * @param isTokenExpired
	 */
	public int getIsTokenExpired() {
		return isTokenExpired;
	}

	/**
	 * Gets the customers list
	 * @return
	 */
	public Object getCustomersList() {
		return customersList;
	}
	
	/**
	 * Gets the isEmailSent flag
	 * @return
	 */
	public int getIsEmailSent() {
		return isEmailSent;
	}
	
	/**
	 * Sets the rolePermissions
	 * @return
	 */
	public Map<String, String> getRolePermissions() {
		return rolePermissions;
	}

	//*********************************** Setters ***********************//
	
	/**
	 * Sets the value of isOptedToSendMail property
	 * @param isOptedToSendMail
	 */
	public void setIsOptedToSendMail(int isOptedToSendMail) {
		this.isOptedToSendMail = isOptedToSendMail;
	}

	/**
	 * Sets the value of reportPreference property
	 * @param reportPreference
	 */
	public void setReportPreference(int reportPreference) {
		this.reportPreference = reportPreference;
	}
	
	/**
	 * sets password
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * sets contact number
	 * @param contactNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * sets email id
	 * @param emailId
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	/**
	 * sets status of user
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Sets the created by 
	 * @param createBy
	 */
	public void setCreateBy(int createBy) {
		this.createBy = createBy;
	}

	/**
	 * Sets the user creation date
	 * @param createDate
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * Sets the updated by
	 * @param updateBy
	 */
	public void setUpdateBy(int updateBy) {
		this.updateBy = updateBy;
	}

	/**
	 * Sets the first name of the user
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the first name of the user
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * Sets the isActive status 
	 * @param isActive
	 */
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	/**
	 * Sets the time Zone
	 * @param timeZone
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * Sets the User updated date
	 * @param updateDate
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * Sets the role of the user
	 * @param role
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	/**
	 * Sets the unique user name
	 * @param uname
	 */
	public void setUname(String uname) {
		this.uname = uname;
	}

	/**
	 * Sets the group id
	 * @param groupId
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * Sets the Location id
	 * @param locationId
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * Sets the alert preference
	 * @param alertPreference
	 */
	public void setAlertPreference(int alertPreference) {
		this.alertPreference = alertPreference;
	}

	/**
	 * Sets the last login user details
	 * @param lastLoginInfo
	 */
	public void setLastLoginInfo(String lastLoginInfo) {
		this.lastLoginInfo = lastLoginInfo;
	}

	/**
	 * Sets customers
	 * @param customers
	 */
	public void setCustomers(String customers) {
		this.customers = customers;
	}

	/**
	 * Gets the user id
	 * @param userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Sets the role name
	 * @param roleName
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * Sets the title name
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Sets the isCSO property
	 * @param isCSO
	 */
	public void setIsCSO(int isCSO) {
		this.isCSO = isCSO;
	}

	/**
	 * Sets the isSuper property
	 * @param isSuper
	 */
	public void setIsSuper(int isSuper) {
		this.isSuper = isSuper;
	}

	/**
	 * Sets the user id property
	 * @param userId
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * Sets the isEai property
	 * @param isEai
	 */
	public void setIsEai(int isEai) {
		this.isEai = isEai;
	}

	/**
	 * Sets the isFirstTimeUser property
	 * @param isFirstTimeUser
	 */
	public void setIsFirstTimeUser(int isFirstTimeUser) {
		this.isFirstTimeUser = isFirstTimeUser;
	}

	/**
	 * Sets the failureCount property
	 * @param failureCount
	 */
	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}
	
	/**
	 * Sets the isTokenExpired property
	 * @param isTokenExpired
	 */
	public void setIsTokenExpired(int isTokenExpired) {
		this.isTokenExpired = isTokenExpired;
	}

	/**
	 * Sets the customers list
	 * @param object
	 */
	public void setCustomersList(Object object) {
		this.customersList = object;
	}
	
	/**
	 * Sets the isEmailSent flag
	 * @param isEmailSent
	 */
	public void setIsEmailSent(int isEmailSent) {
		this.isEmailSent = isEmailSent;
	}

	/**
	 * Sets the rolePermissions
	 * @param rolePermissions
	 */
	public void setRolePermissions(Map<String, String> rolePermissions) {
		this.rolePermissions = rolePermissions;
	}
	

	/**
	 * Sets the restUserName property
	 * @param restUserName
	 */
	public void setRestUserName(String restUserName) {
		this.restUserName = restUserName;
	}

	/**
	 * Sets the restPassword property
	 * @param restUserName
	 */
	public void setRestPassword(String restPassword) {
		this.restPassword = restPassword;
	}

	public String getActivityLogCount() {
		return activityLogCount;
	}

	public String getCreateActivityLogFlag() {
		return createActivityLogFlag;
	}

	public void setCreateActivityLogFlag(String createActivityLogFlag) {
		this.createActivityLogFlag = createActivityLogFlag;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public int getSiteCount() {
		return siteCount;
	}

	public void setSiteCount(int siteCount) {
		this.siteCount = siteCount;
	}

	public String getCustSiteIds() {
		return custSiteIds;
	}

	public void setCustSiteIds(String custSiteIds) {
		this.custSiteIds = custSiteIds;
	}

	public int getReportLevel() {
		return reportLevel;
	}

	public void setReportLevel(int reportLevel) {
		this.reportLevel = reportLevel;
	}

	public int getIsUserOptedForAllGroups() {
		return isUserOptedForAllGroups;
	}

	public void setIsUserOptedForAllGroups(int isUserOptedForAllGroups) {
		this.isUserOptedForAllGroups = isUserOptedForAllGroups;
	}

}
