package com.enerallies.vem.beans.customers;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.enerallies.vem.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * File Name : CreateCustomerRequest 
 * CreateCustomerRequest: is used to transfer CustomerRequest Data from client side
 *
 * @author (Madhu Bantu � CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        01-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 01-09-2016		Madhu Bantu 		File Created
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Customer {
	
	
	/** This property is used to hold the user id */
	@NotNull(message = ErrorCodes.WARN_USER_ID_NOT_NULL)
	private Integer customerId;
	
	/** This property is used to hold the company name. */
	//@NotEmpty(message = ErrorCodes.WARN_COMPANY_NAME_NOT_EMPTY)
	//@Size(min = 6, max = 100, message = ErrorCodes.WARN_COMPANY_NAME_SIZE)
	private String companyName;

	/** This property is used to hold the customer id */
	//@NotEmpty(message = ErrorCodes.WARN_CUSTOMER_ID_NOT_EMPTY)
	//@Size(max = 100, message = ErrorCodes.WARN_CUSTOMER_ID_SIZE)
	private String customerCode;
    
	/** This property is used to hold the customer addressLine1. */
	//@NotEmpty(message = ErrorCodes.ERROR_SITE_ADDRESS_EMPTY)
	private String addressLine1;
	
	/** This property is used to hold the address line2 */
	private String addressLine2;
	
	/** This property is used to hold the customer active status details*/
	private Integer isActive;
	
	/** This property is used to hold the state name*/
	//@NotNull(message = ErrorCodes.ERROR_SITE_STATE_INVALID)
	private Integer state;

	/** This property is used to hold the city name*/
	//@NotNull(message = ErrorCodes.ERROR_SITE_CITY_INVALID)
	private Integer city;

	/** This property is used to hold the postal code*/
	//@NotEmpty(message = ErrorCodes.ERROR_SITE_ZIP_EMPTY)
	private String postalCode;
	
	/** This property is used to hold the customer status*/
	//@NotEmpty(message = ErrorCodes.WARN_CUSTOMER_STATUS_NOT_EMPTY)
	private String customerStatus;
	
	/** This property is used to hold the customer company log*/
	private String companyLogo;
	
	/** This property is used to store the fileLocation in database. */
	private String fileLocation;
	
	/** This property is used to hold the degreePreference. */
//	@NotNull(message = ErrorCodes.ERROR_SITE_DEGREE_PREFERENCES_EMPTY)
	private String degreePrefereces;

	/** This property is used to hold the thermostatePreference. */
	//@NotNull(message = ErrorCodes.ERROR_SITE_THERMOSTATE_PREFERENCES_EMPTY)
	private Integer thermostatPreferences;
	
	/** This property is used to hold the thermostateMinSetPoint. */
	private Integer thermostateMinSetPoint;
	
	/** This property is used to hold the thermostatePreferenceFanOn. */
    private Integer thermostatePreferenceFanOn;	
	
    /** This property is used to hold the thermostatePreferenceFanAuto. */
	private Integer thermostatePreferenceFanAuto;
	
	/** This property is used to hold the thermostatePreferenceResetHold. */
	private Integer thermostatePreferenceResetHold;
	
	/** This property is used to hold the thermostatePreferenceHvacAuto. */
	private Integer thermostatePreferenceHvacAuto;
	
	/** This property is used to hold the thermostateMaxSetPoint. */
	private Integer thermostateMaxSetPoint;
	
	/**This property is used to hold device lock prefernces*/
	private Integer lockPref;
	
	/** This property is used to hold the company name images*/
	private String  fileName;
	
	/** This property is used to hold the nightlyScheduleDownload. */
	//@NotNull(message = ErrorCodes.ERROR_SITE_NIGHTLY_SCHEDULE_EMPTY)
	private boolean nightlyScheduleDownload;
	
	/** This property is used to hold the locationsList. */
    private String locationsList;
	
    /** This property is used to hold the usersList. */
    private String usersList;
	
	/** This property is used to hold the user who is created the user. */
	private Integer createBy;
	
	/** This property is used to hold the created date on which the user got created. */
	private Date createDate;
	
	/** This property is used to hold the user who is updated the user. */
	private Integer updateBy;
	
	/** This property is used to hold the updated date on which the user got updated. */
	private Date updateDate;
	
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public Integer getCity() {
		return city;
	}

	public void setCity(Integer city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}


	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}

	public String getDegreePrefereces() {
		return degreePrefereces;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setDegreePrefereces(String degreePrefereces) {
		this.degreePrefereces = degreePrefereces;
	}

	public Integer getThermostatePreferenceFanOn() {
		return thermostatePreferenceFanOn;
	}

	public void setThermostatePreferenceFanOn(Integer thermostatePreferenceFanOn) {
		this.thermostatePreferenceFanOn = thermostatePreferenceFanOn;
	}

	public Integer getThermostatePreferenceFanAuto() {
		return thermostatePreferenceFanAuto;
	}

	public void setThermostatePreferenceFanAuto(Integer thermostatePreferenceFanAuto) {
		this.thermostatePreferenceFanAuto = thermostatePreferenceFanAuto;
	}

	public Integer getThermostatePreferenceResetHold() {
		return thermostatePreferenceResetHold;
	}

	public void setThermostatePreferenceResetHold(
			Integer thermostatePreferenceResetHold) {
		this.thermostatePreferenceResetHold = thermostatePreferenceResetHold;
	}

	public Integer getThermostatePreferenceHvacAuto() {
		return thermostatePreferenceHvacAuto;
	}

	public void setThermostatePreferenceHvacAuto(
			Integer thermostatePreferenceHvacAuto) {
		this.thermostatePreferenceHvacAuto = thermostatePreferenceHvacAuto;
	}

	public void setThermostatPreferences(Integer thermostatPreferences) {
		this.thermostatPreferences = thermostatPreferences;
	}

	public Integer getThermostatPreferences() {
		return thermostatPreferences;
	}

	public boolean getNightlyScheduleDownload() {
		return nightlyScheduleDownload;
	}

	public void setNightlyScheduleDownload(boolean nightlyScheduleDownload) {
		this.nightlyScheduleDownload = nightlyScheduleDownload;
	}

	public Integer getThermostateMinSetPoint() {
		return thermostateMinSetPoint;
	}

	public void setThermostateMinSetPoint(Integer thermostateMinSetPoint) {
		this.thermostateMinSetPoint = thermostateMinSetPoint;
	}

	public Integer getThermostateMaxSetPoint() {
		return thermostateMaxSetPoint;
	}

	public void setThermostateMaxSetPoint(Integer thermostateMaxSetPoint) {
		this.thermostateMaxSetPoint = thermostateMaxSetPoint;
	}

	public Integer getLockPref() {
		return lockPref;
	}

	public void setLockPref(Integer lockPref) {
		this.lockPref = lockPref;
	}

	public String getLocationsList() {
		return locationsList;
	}

	public void setLocationsList(String locationsList) {
		this.locationsList = locationsList;
	}

	public String getUsersList() {
		return usersList;
	}

	public void setUsersList(String usersList) {
		this.usersList = usersList;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Integer updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}