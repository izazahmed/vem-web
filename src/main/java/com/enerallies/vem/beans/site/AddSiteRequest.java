/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.beans.site;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : AddSiteRequest 
 * 
 * AddSiteRequest: is used to hold the new site request data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        31-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER              	COMMENTS
 * 31-08-2016		Goush Basha		    File Created(Sprint-3).
 * 
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AddSiteRequest {
	
	/** This property is used to hold the customerId. */
	@NotNull(message = ErrorCodes.ERROR_SITE_CUSTOMER_ID_EMPTY)
	private Integer customerId;
	
	/** This property is used to hold the degreePreference. */
	@NotNull(message = ErrorCodes.ERROR_SITE_DEGREE_PREFERENCES_EMPTY)
	private Integer degreePreference;
	
	private String empBelieveSystem;
	
	/** This property is used to hold the fanAuto. */
	private Integer fanAuto;
	
	/** This property is used to hold the fanOn. */
	private Integer fanOn;
	
	private String fileName;
	
	private String hvacDetails;
	
	/** This property is used to hold the isHVACModeToAuto. */
	private Integer isHVACModeToAuto;
	
	/** This property is used to hold the maxSP. */
	private String maxSP;
	
	/** This property is used to hold the minSP. */
	private String minSP;
	
	/** This property is used to hold the nightlyScheduleDownload. */
	@NotNull(message = ErrorCodes.ERROR_SITE_NIGHTLY_SCHEDULE_EMPTY)
	private Integer nightlyScheduleDownload;
	
	/** This property is used to hold the resetHoldMode. */
	private Integer resetHoldMode;
	
	/** This property is used to hold the siteAddLine1. */
	@NotEmpty(message = ErrorCodes.ERROR_SITE_ADDLINE1_EMPTY)
	@Size(max=50, message=ErrorCodes.ERROR_SITE_ADDLINE1_MAX_SIZE)
	private String siteAddLine1;
	
	/** This property is used to hold the siteAddLine2. */
	private String siteAddLine2;
	
	/** This property is used to hold the siteArea. */
	private String siteArea;
	
	/** This property is used to hold the siteCity. */
	@NotNull(message = ErrorCodes.ERROR_SITE_CITY_INVALID)
	private Integer siteCity;
	
	/** This property is used to hold the siteCityName. */
	private String siteCityName;
	
	/** This property is used to hold the siteDistrict. */
	private String siteDistrict;
	
	/** This property is used to hold the siteGroups. */
	@NotNull(message = ErrorCodes.ERROR_SITE_GROUP_EMPTY)
	private List<String> siteGroups;
	
	/** This property is used to hold the siteHours. */
	@NotNull(message = ErrorCodes.ERROR_SITE_HOURS_NULL)
	private List<SiteHoursRequest> siteHours;
	
	/** This property is used to hold the siteHrsFormate. */
	@NotNull(message = ErrorCodes.ERROR_SITE_HRS_FORMATE_NULL)
	private Integer siteHrsFormate;
	
	private long siteId;
	
	/** This property is used to hold the siteInternalId. */
	private String siteInternalId;
	
	/** This property is used to hold the siteName. */
	@NotEmpty(message = ErrorCodes.ERROR_SITE_NAME_EMPTY)
	@Size(max=50, message=ErrorCodes.ERROR_SITE_NAME_MAX_SIZE)
	private String siteName;
	
	/** This property is used to hold the sitePhNo. */
	private String sitePhNo;
	
	/** This property is used to hold the siteRegion. */
	private String siteRegion;
	
	/** This property is used to hold the siteState. */
	@NotNull(message = ErrorCodes.ERROR_SITE_STATE_INVALID)
	private Integer siteState;
	
	/** This property is used to hold the siteStateName. */
	private String siteStateName;
	
	private StringBuilder siteStoreHours;
	/** This property is used to hold the siteTimeZone. */
	private String siteTimeZone;
	
	/** This property is used to hold the siteType. */
	@NotNull(message = ErrorCodes.ERROR_SITE_TYPE_INVALID)
	private Integer siteType;

	/** This property is used to hold the siteTypeNew. */
	private String siteTypeNew;

	/** This property is used to hold the siteZipCode. */
	@NotNull(message = ErrorCodes.ERROR_SITE_ZIP_EMPTY)
	@NotEmpty(message = ErrorCodes.ERROR_SITE_ZIP_EMPTY)
	@Size(max=10, message=ErrorCodes.ERROR_ZIPCODE_MAX_LIMIT)
	private String siteZipCode;
	
	private String thermostatDetails;
	private int totalRecords;
	/** This property is used to hold the userId.*/
	private int userId;
	/**
	 * Gets the customerId value for this Site.
	 * @return customerId
	 */
	
	/** This property is used to lock. */
	private Integer lock;
	
	/** This property is used to sameAsStore. */
	private Integer sameAsStore;
	
	private String latitude;
	private String langitude;
	 
	 public String getLatitude() {
	  return latitude;
	 }

	 public void setLatitude(String latitude) {
	  this.latitude = latitude;
	 }

	 public String getLangitude() {
	  return langitude;
	 }

	 public void setLangitude(String langitude) {
	  this.langitude = langitude;
	 }
	 
	public Integer getCustomerId() {
		return customerId;
	}
	/**
	 * Gets the degreePreference value for this Site.
	 * @return degreePreference
	 */
	public Integer getDegreePreference() {
		return degreePreference;
	}
	
	public String getEmpBelieveSystem() {
		return empBelieveSystem;
	}

	/**
	 * Gets the fanAuto value for this Site.
	 * @return fanAuto
	 */
	public Integer getFanAuto() {
		return fanAuto;
	}

	/**
	 * Gets the fanOn value for this Site.
	 * @return fanOn
	 */
	public Integer getFanOn() {
		return fanOn;
	}

	public String getFileName() {
		return fileName;
	}

	/*********************************** Getters ***********************/

	public String getHvacDetails() {
		return hvacDetails;
	}

	/**
	 * Gets the isHVACModeToAuto value for this Site.
	 * @return isHVACModeToAuto
	 */
	public Integer getIsHVACModeToAuto() {
		return isHVACModeToAuto;
	}

	/**
	 * Gets the maxSP value for this Site.
	 * @return maxSP
	 */
	public String getMaxSP() {
		return maxSP;
	}

	/**
	 * Gets the minSP value for this Site.
	 * @return minSP
	 */
	public String getMinSP() {
		return minSP;
	}

	/**
	 * Gets the nightlyScheduleDownload value for this Site.
	 * @return nightlyScheduleDownload
	 */
	public Integer getNightlyScheduleDownload() {
		return nightlyScheduleDownload;
	}

	/**
	 * Gets the resetHoldMode value for this Site.
	 * @return resetHoldMode
	 */
	public Integer getResetHoldMode() {
		return resetHoldMode;
	}

	/**
	 * Gets the siteAddLine1 value for this Site.
	 * @return siteAddLine1
	 */
	public String getSiteAddLine1() {
		return siteAddLine1;
	}

	/**
	 * Gets the siteAddLine2 value for this Site.
	 * @return siteAddLine2
	 */
	public String getSiteAddLine2() {
		return siteAddLine2;
	}

	/**
	 * Gets the siteArea value for this Site.
	 * @return siteArea
	 */
	public String getSiteArea() {
		return siteArea;
	}
	
	/**
	 * Gets the siteCity value for this Site.
	 * @return siteCity
	 */
	public Integer getSiteCity() {
		return siteCity;
	}

	/**
	 * Gets the siteCityName value for this Site.
	 * @return siteCityName
	 */
	public String getSiteCityName() {
		return siteCityName;
	}

	/**
	 * Gets the siteDistrict value for this Site.
	 * @return siteDistrict
	 */
	public String getSiteDistrict() {
		return siteDistrict;
	}

	/**
	 * Gets the siteGroups value for this Site.
	 * @return siteGroups
	 */
	public List<String> getSiteGroups() {
		return siteGroups;
	}

	/**
	 * Gets the siteHours value for this Site.
	 * @return siteHours
	 */
	public List<SiteHoursRequest> getSiteHours() {
		return siteHours;
	}
	
	/**
	 * Gets the siteHrsFormate value for this Site.
	 * @return siteHrsFormate
	 */
	public Integer getSiteHrsFormate() {
		return siteHrsFormate;
	}
	
	public long getSiteId() {
		return siteId;
	}

	/**
	 * Gets the siteInternalId value for this Site.
	 * @return siteInternalId
	 */
	public String getSiteInternalId() {
		return siteInternalId;
	}

	/**
	 * Gets the siteName value for this Site.
	 * @return siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * Gets the sitePhNo value for this Site.
	 * @return sitePhNo
	 */
	public String getSitePhNo() {
		return sitePhNo;
	}

	/**
	 * Gets the siteRegion value for this Site.
	 * @return siteRegion
	 */
	public String getSiteRegion() {
		return siteRegion;
	}

	/**
	 * Gets the siteState value for this Site.
	 * @return siteState
	 */
	public Integer getSiteState() {
		return siteState;
	}

	/**
	 * Gets the siteStateName value for this Site.
	 * @return siteStateName
	 */
	public String getSiteStateName() {
		return siteStateName;
	}

	public StringBuilder getSiteStoreHours() {
		return siteStoreHours;
	}

	/**
	 * Gets the siteTimeZone value for this Site.
	 * @return siteTimeZone
	 */
	public String getSiteTimeZone() {
		return siteTimeZone;
	}
	
	/**
	 * Gets the siteType value for this Site.
	 * @return siteType
	 */
	public Integer getSiteType() {
		return siteType;
	}
	
	/**
	 * Gets the siteTypeNew value for this Site.
	 * @return siteTypeNew
	 */
	public String getSiteTypeNew() {
		return siteTypeNew;
	}

	/**
	 * Gets the siteZipCode value for this Site.
	 * @return siteZipCode
	 */
	public String getSiteZipCode() {
		return siteZipCode;
	}
	
	public String getThermostatDetails() {
		return thermostatDetails;
	}
	
	public int getTotalRecords() {
		return totalRecords;
	}
	
	/**
	 * Gets the userId value for this Site.
	 * @return userId
	 */
	public int getUserId() {
		return userId;
	}
	
	/**
	 * Sets the customerId value for this Site.
	 * @param customerId
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	
	/**
	 * Sets the degreePreference value for this Site.
	 * @param degreePreference
	 */
	public void setDegreePreference(Integer degreePreference) {
		this.degreePreference = degreePreference;
	}
	
	public void setEmpBelieveSystem(String empBelieveSystem) {
		this.empBelieveSystem = empBelieveSystem;
	}
	
	/**
	 * Sets the fanAuto value for this Site.
	 * @param fanAuto
	 */
	public void setFanAuto(Integer fanAuto) {
		this.fanAuto = fanAuto;
	}
	
	/**
	 * Sets the fanOn value for this Site.
	 * @param fanOn
	 */
	public void setFanOn(Integer fanOn) {
		this.fanOn = fanOn;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setHvacDetails(String hvacDetails) {
		this.hvacDetails = hvacDetails;
	}
	
	/**
	 * Sets the isHVACModeToAuto value for this Site.
	 * @param isHVACModeToAuto
	 */
	public void setIsHVACModeToAuto(Integer isHVACModeToAuto) {
		this.isHVACModeToAuto = isHVACModeToAuto;
	}
	
	/**
	 * Sets the maxSP value for this Site.
	 * @param maxSP
	 */
	public void setMaxSP(String maxSP) {
		this.maxSP = maxSP;
	}
	
	/**
	 * Sets the minSP value for this Site.
	 * @param minSP
	 */
	public void setMinSP(String minSP) {
		this.minSP = minSP;
	}
	
	/**
	 * Sets the nightlyScheduleDownload value for this Site.
	 * @param nightlyScheduleDownload
	 */
	public void setNightlyScheduleDownload(Integer nightlyScheduleDownload) {
		this.nightlyScheduleDownload = nightlyScheduleDownload;
	}
	
	/**
	 * Sets the resetHoldMode value for this Site.
	 * @param resetHoldMode
	 */
	public void setResetHoldMode(Integer resetHoldMode) {
		this.resetHoldMode = resetHoldMode;
	}
	
	/**
	 * Sets the siteAddLine1 value for this Site.
	 * @param siteAddLine1
	 */
	public void setSiteAddLine1(String siteAddLine1) {
		this.siteAddLine1 = siteAddLine1;
	}
	
	/**
	 * Sets the siteAddLine2 value for this Site.
	 * @param siteAddLine2
	 */
	public void setSiteAddLine2(String siteAddLine2) {
		this.siteAddLine2 = siteAddLine2;
	}
	
	/**
	 * Sets the siteArea value for this Site.
	 * @param siteArea
	 */
	public void setSiteArea(String siteArea) {
		this.siteArea = siteArea;
	}
	
	/**
	 * Sets the siteCity value for this Site.
	 * @param siteCity
	 */
	public void setSiteCity(Integer siteCity) {
		this.siteCity = siteCity;
	}
	
	/**
	 * Sets the siteCityName value for this Site.
	 * @param siteCityName
	 */
	public void setSiteCityName(String siteCityName) {
		this.siteCityName = siteCityName;
	}
	
	/**
	 * Sets the siteDistrict value for this Site.
	 * @param siteDistrict
	 */
	public void setSiteDistrict(String siteDistrict) {
		this.siteDistrict = siteDistrict;
	}
	
	/**
	 * Sets the siteGroups value for this Site.
	 * @param siteGroups
	 */
	public void setSiteGroups(List<String> siteGroups) {
		this.siteGroups = siteGroups;
	}
	
	/**
	 * Sets the siteHours value for this Site.
	 * @param siteHours
	 */
	public void setSiteHours(List<SiteHoursRequest> siteHours) {
		this.siteHours = siteHours;
	}
	
	/**
	 * Sets the siteHrsFormate value for this Site.
	 * @param siteHrsFormate
	 */
	public void setSiteHrsFormate(Integer siteHrsFormate) {
		this.siteHrsFormate = siteHrsFormate;
	}
	
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	/**
	 * Sets the siteInternalId value for this Site.
	 * @param siteInternalId
	 */
	public void setSiteInternalId(String siteInternalId) {
		this.siteInternalId = siteInternalId;
	}
	
	/*********************************** Setters ***********************/
	/**
	 * Sets the siteName value for this Site.
	 * @param siteName
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * Sets the sitePhNo value for this Site.
	 * @param sitePhNo
	 */
	public void setSitePhNo(String sitePhNo) {
		this.sitePhNo = sitePhNo;
	}
	
	/**
	 * Sets the siteRegion value for this Site.
	 * @param siteRegion
	 */
	public void setSiteRegion(String siteRegion) {
		this.siteRegion = siteRegion;
	}
	
	/**
	 * Sets the siteState value for this Site.
	 * @param siteState
	 */
	public void setSiteState(Integer siteState) {
		this.siteState = siteState;
	}
	
	/**
	 * Sets the siteStateName value for this Site.
	 * @param siteStateName
	 */
	public void setSiteStateName(String siteStateName) {
		this.siteStateName = siteStateName;
	}

	public void setSiteStoreHours(StringBuilder siteStoreHours) {
		this.siteStoreHours = siteStoreHours;
	}

	/**
	 * Sets the siteTimeZone value for this Site.
	 * @param siteTimeZone
	 */
	public void setSiteTimeZone(String siteTimeZone) {
		this.siteTimeZone = siteTimeZone;
	}

	/**
	 * Sets the siteType value for this Site.
	 * @param siteType
	 */
	public void setSiteType(Integer siteType) {
		this.siteType = siteType;
	}
	
	/**
	 * Sets the siteTypeNew value for this Site.
	 * @param siteTypeNew
	 */
	public void setSiteTypeNew(String siteTypeNew) {
		this.siteTypeNew = siteTypeNew;
	}
	
	/**
	 * Sets the siteZipCode value for this Site.
	 * @param siteZipCode
	 */
	public void setSiteZipCode(String siteZipCode) {
		this.siteZipCode = siteZipCode;
	}
	
	public void setThermostatDetails(String thermostatDetails) {
		this.thermostatDetails = thermostatDetails;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * Sets the userId value for this Site.
	 * @param userId
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public Integer getLock() {
		return lock;
	}
	public void setLock(Integer lock) {
		this.lock = lock;
	}
	
	public Integer getSameAsStore() {
		return sameAsStore;
	}
	public void setSameAsStore(Integer sameAsStore) {
		this.sameAsStore = sameAsStore;
	}
}
