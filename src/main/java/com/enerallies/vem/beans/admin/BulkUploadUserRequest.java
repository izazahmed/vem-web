/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.beans.admin;

import java.util.Date;


/**
 * File Name : CreateUserRequest 
 * CreateUserRequest: is used to transfer UserRequest Data from client side
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        04-11-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 04-11-2016		Madhu Bantu     	File Created
 *
 */
public class BulkUploadUserRequest {
    
	/** This property is used to hold the user id */
	private int userId;
	
	/** This property is used to hold the user name */
	private String uname;
	
	/** This property is used to hold the user id. */
	private String firstName;
	
	/** This property is used to hold the last name */
	private String lastName;
	
	/** This property is used to hold the title */
	private String title;
	
	/** This property is used to hold the first name */
	private int isActive;

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
	
	/** This property holds the new password **/
	private String newPassword;
	
	/** This property is used to hold the value of is opted to send mail while creating the user */
	private int isOptedToSendMail;
	
	/** This property is used to hold the value of report preference for the user  */
	private int reportPreference;

	/** This property is used to hold the value of isEai property for the user */
	private int isEai;
	
	/** This property is used to hold the value of isCSO property for the user */
	private int isCSO;
	
	/** This property is used to hold the value of isSuper property for the user */
	private int isSuper;
	
	/** This property is used to hold the isFirstTimeUser or not */
	private int isFirstTimeUser;
	
	/** This property is used to hold the lastLoginInfo */
	private Date lastLoginInfo;
	
	/** This property is used to hold the failureCount */
	private int failureCount;
	
	private String randomKey;
	
	/*
	 * 3- Violated the validation of security questions
	 */
	/** This property is used to hold the securityFailCount */
	private int securityFailCount;
	
	/** This property is used to hold rolePermissions **/
	private String rolePermissions;
	
	/** This property is used to hold restUserName **/
	private String restUserName;
	
	/** This property is used to hold restPassword **/
	private String restPassword;
	/** This property is used to hold the isTokenExpired */
	private int isTokenExpired;
	
	/** This property is used to hold the authToken */
	private String authToken;
	
	/** This property is used to hold whether the user received email or not **/
	private int isEmailSent;

	public int getIsTokenExpired() {
		return isTokenExpired;
	}

	public void setIsTokenExpired(int isTokenExpired) {
		this.isTokenExpired = isTokenExpired;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public int getIsEmailSent() {
		return isEmailSent;
	}

	public void setIsEmailSent(int isEmailSent) {
		this.isEmailSent = isEmailSent;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getCustomers() {
		return customers;
	}

	public void setCustomers(String customers) {
		this.customers = customers;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public int getAlertPreference() {
		return alertPreference;
	}

	public void setAlertPreference(int alertPreference) {
		this.alertPreference = alertPreference;
	}

	public int getCreateBy() {
		return createBy;
	}

	public void setCreateBy(int createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(int updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public int getIsOptedToSendMail() {
		return isOptedToSendMail;
	}

	public void setIsOptedToSendMail(int isOptedToSendMail) {
		this.isOptedToSendMail = isOptedToSendMail;
	}

	public int getReportPreference() {
		return reportPreference;
	}

	public void setReportPreference(int reportPreference) {
		this.reportPreference = reportPreference;
	}

	public int getIsEai() {
		return isEai;
	}

	public void setIsEai(int isEai) {
		this.isEai = isEai;
	}

	public int getIsCSO() {
		return isCSO;
	}

	public void setIsCSO(int isCSO) {
		this.isCSO = isCSO;
	}

	public String getRandomKey() {
		return randomKey;
	}

	public void setRandomKey(String randomKey) {
		this.randomKey = randomKey;
	}
	public int getIsSuper() {
		return isSuper;
	}

	public void setIsSuper(int isSuper) {
		this.isSuper = isSuper;
	}

	public int getIsFirstTimeUser() {
		return isFirstTimeUser;
	}

	public void setIsFirstTimeUser(int isFirstTimeUser) {
		this.isFirstTimeUser = isFirstTimeUser;
	}

	public Date getLastLoginInfo() {
		return lastLoginInfo;
	}

	public void setLastLoginInfo(Date lastLoginInfo) {
		this.lastLoginInfo = lastLoginInfo;
	}

	public int getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}

	public int getSecurityFailCount() {
		return securityFailCount;
	}

	public void setSecurityFailCount(int securityFailCount) {
		this.securityFailCount = securityFailCount;
	}

	public String getRolePermissions() {
		return rolePermissions;
	}

	public void setRolePermissions(String rolePermissions) {
		this.rolePermissions = rolePermissions;
	}

	public String getRestUserName() {
		return restUserName;
	}

	public void setRestUserName(String restUserName) {
		this.restUserName = restUserName;
	}

	public String getRestPassword() {
		return restPassword;
	}

	public void setRestPassword(String restPassword) {
		this.restPassword = restPassword;
	}
}
