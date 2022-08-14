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
 * File Name : UpdateSiteRequest 
 * 
 * UpdateSiteRequest: is used to hold the updated site request data from client.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        06-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER              	COMMENTS
 * 06-09-2016		Goush Basha		    File Created(Sprint-3).
 * 30-09-2016		Goush Basha		    Added properties for Site more Info(Sprint-3).
 * 
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class UpdateSiteRequest {
	
	/** This property is used to hold the siteId. */
	@NotNull(message = ErrorCodes.ERROR_SITE_ID_EMPTY)
	private Integer siteId;
	
	/** This property is used to hold the siteName. */
	@NotEmpty(message = ErrorCodes.ERROR_SITE_NAME_EMPTY)
	@Size(max=50, message=ErrorCodes.ERROR_SITE_NAME_MAX_SIZE)
	private String siteName;
	
	/** This property is used to hold the siteInternalId. */
	private String siteInternalId;
	
	/** This property is used to hold the siteType. */
	@NotNull(message = ErrorCodes.ERROR_SITE_TYPE_INVALID)
	private Integer siteType;
	
	/** This property is used to hold the siteTypeNew. */
	private String siteTypeNew;
	
	/** This property is used to hold the siteAddLine1. */
	@NotEmpty(message = ErrorCodes.ERROR_SITE_ADDLINE1_EMPTY)
	@Size(max=50, message=ErrorCodes.ERROR_SITE_ADDLINE1_MAX_SIZE)
	private String siteAddLine1;
	
	/** This property is used to hold the siteAddLine2. */
	private String siteAddLine2;
	
	/** This property is used to hold the siteState. */
	@NotNull(message = ErrorCodes.ERROR_SITE_STATE_INVALID)
	private Integer siteState;
	
	/** This property is used to hold the siteCity. */
	@NotNull(message = ErrorCodes.ERROR_SITE_CITY_INVALID)
	private Integer siteCity;
	
	/** This property is used to hold the siteCityName. */
	private String siteCityName;
	
	/** This property is used to hold the siteStateName. */
	private String siteStateName;
	
	/** This property is used to hold the siteZipCode. */
	@NotNull(message = ErrorCodes.ERROR_SITE_ZIP_EMPTY)
	@NotEmpty(message = ErrorCodes.ERROR_SITE_ZIP_EMPTY)
	private String siteZipCode;
	
	/** This property is used to hold the sitePhNo. */
	private String sitePhNo;
	
	/** This property is used to hold the siteDistrict. */
	private String siteDistrict;
	
	/** This property is used to hold the siteRegion. */
	private String siteRegion;
	
	/** This property is used to hold the siteArea. */
	private String siteArea;
	
	/** This property is used to hold the siteHours. */
	@NotNull(message = ErrorCodes.ERROR_SITE_HOURS_NULL)
	private List<SiteHoursRequest> siteHours;
	
	/** This property is used to hold the siteGroups. */
	@NotNull(message = ErrorCodes.ERROR_SITE_GROUP_EMPTY)
	private List<String> siteGroups;
	
	/** This property is used to hold the deleteSiteGroups. */
	private List<String> deleteSiteGroups;

	/** This property is used to hold the degreePreference. */
	@NotNull(message = ErrorCodes.ERROR_SITE_DEGREE_PREFERENCES_EMPTY)
	private Integer degreePreference;
	
	/** This property is used to hold the minSP. */
	private String minSP;
	
	/** This property is used to hold the maxSP. */
	private String maxSP;
	
	/** This property is used to hold the nightlyScheduleDownload. */
	@NotNull(message = ErrorCodes.ERROR_SITE_NIGHTLY_SCHEDULE_EMPTY)
	private Integer nightlyScheduleDownload;
	
	/** This property is used to hold the customerId. */
	@NotNull(message = ErrorCodes.ERROR_SITE_CUSTOMER_ID_EMPTY)
	private Integer customerId;
	
	/** This property is used to hold the fanOn. */
	private Integer fanOn;
	
	/** This property is used to hold the fanAuto. */
	private Integer fanAuto;
	
	/** This property is used to hold the isHVACModeToAuto. */
	private Integer isHVACModeToAuto;
	
	/** This property is used to hold the resetHoldMode. */
	private Integer resetHoldMode;
	
	/** This property is used to hold the surveyDate. */
	private String surveyDate;
	
	/** This property is used to hold the squareFootage. */
	private String squareFootage;
	
	/** This property is used to hold the floorImage. */
	private String floorImage;
	
	/** This property is used to hold the buildingLayout. */
	private String buildingLayout;
	
	/** This property is used to hold the buildingLayoutImage. */
	private String buildingLayoutImage;
	
	/** This property is used to hold the floorPlan. */
	private String floorPlan;
	
	/** This property is used to hold the occupyHours. */
	@NotNull(message = ErrorCodes.ERROR_OCCUPY_HOURS_NULL)
	private List<SiteHoursRequest> occupyHours;
	
	//Site Contact
	/** This property is used to hold the localSiteContact. */
	private String localSiteContact;
	
	/** This property is used to hold the localContactPhone. */
	private String localContactPhone; 
	
	/** This property is used to hold the localContactEmail. */
	private String localContactEmail; 
	
	/** This property is used to hold the localContactMobile. */
	private String localContactMobile; 
	
	/** This property is used to hold the alternateSiteContact. */
	private String alternateSiteContact; 
	
	/** This property is used to hold the alternateContactPhone. */
	private String alternateContactPhone; 
	
	/** This property is used to hold the escortContactName. */
	private String escortContactName;
	
	/** This property is used to hold the escortContactNumber. */
	private String escortContactNumber;
	
	/** This property is used to hold the listaAccessRestrictionsFromFormalHours. */
	private String listaAccessRestrictionsFromFormalHours;
	
	/** This property is used to hold the specialRoomAccessInformation. */
	private String specialRoomAccessInformation;
	
	/** This property is used to hold the lockBoxOnThermostat. */
	private String lockBoxOnThermostat;
	
	/** This property is used to hold the accessOrManagesThermostat. */
	private String accessOrManagesThermostat;
	
	/** This property is used to hold the buildingType. */
	private Integer buildingType;
	
	/** This property is used to hold the buildingNotes. */
	private String buildingNotes;
	
	/** This property is used to hold the rTUList. */
	@NotNull(message = ErrorCodes.ERROR_SITE_HVAC_LIST_NULL)
	private List<RTURequest> rTUList;
	
	/** This property is used to hold the thermostatList. */
	@NotNull(message = ErrorCodes.ERROR_SITE_THERMOSTAT_LIST_NULL)
	private List<ThermostatRequest> thermostatList;
	
	/** This property is used to hold the technicianName. */
	private String technicianName;
	
	/** This property is used to hold the technicianPhone. */
	private String technicianPhone;
	
	/** This property is used to hold the technicianNotes. */
	private String technicianNotes;
	
	/** This property is used to hold the cellularMode. */
	private String cellularMode;
	
	/** This property is used to hold the cellularCoverage. */
	private String cellularCoverage;
	
	/** This property is used to hold the cellularProvide. */
	private String cellularProvide;
	
	/** This property is used to hold the cellularWIFI. */
	private String cellularWIFI;
	
	/** This property is used to hold the cellularWIFIPWD. */
	private String cellularWIFIPWD;
	
	/** This property is used to hold the modemType. */
	private Integer modemType;
	
	/** This property is used to hold the simCardNumber. */
	private String simCardNumber;
	
	/** This property is used to hold the modemSerialNumber. */
	private String modemSerialNumber;
	
	/** This property is used to hold the modemIPAddress. */
	private String modemIPAddress;
	
	/** This property is used to hold the modemPhoneNumber. */
	private String modemPhoneNumber;
	
	/** This property is used to hold the isHvacRunning. */
	private String isHvacRunning;
	
	/** This property is used to hold the siteHrsFormate. */
	@NotNull(message = ErrorCodes.ERROR_SITE_HRS_FORMATE_NULL)
	private Integer siteHrsFormate;
	
	/** This property is used to hold the siteOccupyHrsFormate. */
	@NotNull(message = ErrorCodes.ERROR_SITE_OCCUPY_HRS_FORMATE_NULL)
	private Integer siteOccupyHrsFormate;
	
	/** This property is used to hold the siteTimeZone. */
	private String siteTimeZone;
	private String siteTimeZoneStd;
	private String siteTimeZoneDls;
	
	/** This property is used to hold the siteStatus. */
	private int siteStatus;
	private StringBuilder siteStoreHours;
	private String hvacDetails;
	private String thermostatDetails;
	private String empBelieveSystem;
	private int userId;
	private StringBuilder siteOccupyHours;
	
	public String getSiteTimeZoneStd() {
		return siteTimeZoneStd;
	}

	public void setSiteTimeZoneStd(String siteTimeZoneStd) {
		this.siteTimeZoneStd = siteTimeZoneStd;
	}

	public String getSiteTimeZoneDls() {
		return siteTimeZoneDls;
	}

	public void setSiteTimeZoneDls(String siteTimeZoneDls) {
		this.siteTimeZoneDls = siteTimeZoneDls;
	}

	/** This property is used to lock. */
	private Integer lock;
	
	/** This property is used to sameAsStore. */
	private Integer sameAsStore;
	private List<String> newGroups;
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

	public List<String> getNewGroups() {
		return newGroups;
	}

	public void setNewGroups(List<String> newGroups) {
		this.newGroups = newGroups;
	}

	public StringBuilder getSiteOccupyHours() {
		return siteOccupyHours;
	}

	public void setSiteOccupyHours(StringBuilder siteOccupyHours) {
		this.siteOccupyHours = siteOccupyHours;
	}

	public StringBuilder getSiteStoreHours() {
		return siteStoreHours;
	}

	public void setSiteStoreHours(StringBuilder siteStoreHours) {
		this.siteStoreHours = siteStoreHours;
	}

	public String getHvacDetails() {
		return hvacDetails;
	}

	public void setHvacDetails(String hvacDetails) {
		this.hvacDetails = hvacDetails;
	}

	public String getThermostatDetails() {
		return thermostatDetails;
	}

	public void setThermostatDetails(String thermostatDetails) {
		this.thermostatDetails = thermostatDetails;
	}

	public String getEmpBelieveSystem() {
		return empBelieveSystem;
	}

	public void setEmpBelieveSystem(String empBelieveSystem) {
		this.empBelieveSystem = empBelieveSystem;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	/*********************************** Getters ************************/
	/**
	 * Gets the siteId value for this Site.
	 * @return siteId
	 */
	public Integer getSiteId() {
		return siteId;
	}
	
	/**
	 * Gets the siteName value for this Site.
	 * @return siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * Gets the siteInternalId value for this Site.
	 * @return siteInternalId
	 */
	public String getSiteInternalId() {
		return siteInternalId;
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
	 * Gets the siteState value for this Site.
	 * @return siteState
	 */
	public Integer getSiteState() {
		return siteState;
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
	 * Gets the siteStateName value for this Site.
	 * @return siteStateName
	 */
	public String getSiteStateName() {
		return siteStateName;
	}

	/**
	 * Gets the siteZipCode value for this Site.
	 * @return siteZipCode
	 */
	public String getSiteZipCode() {
		return siteZipCode;
	}

	/**
	 * Gets the sitePhNo value for this Site.
	 * @return sitePhNo
	 */
	public String getSitePhNo() {
		return sitePhNo;
	}

	/**
	 * Gets the siteDistrict value for this Site.
	 * @return siteDistrict
	 */
	public String getSiteDistrict() {
		return siteDistrict;
	}

	/**
	 * Gets the siteRegion value for this Site.
	 * @return siteRegion
	 */
	public String getSiteRegion() {
		return siteRegion;
	}

	/**
	 * Gets the siteArea value for this Site.
	 * @return siteArea
	 */
	public String getSiteArea() {
		return siteArea;
	}

	/**
	 * Gets the siteHours value for this Site.
	 * @return siteHours
	 */
	public List<SiteHoursRequest> getSiteHours() {
		return siteHours;
	}

	/**
	 * Gets the siteGroups value for this Site.
	 * @return siteGroups
	 */
	public List<String> getSiteGroups() {
		return siteGroups;
	}
	
	/**
	 * Gets the deleteSiteGroups value for this Site.
	 * @return deleteSiteGroups
	 */
	public List<String> getDeleteSiteGroups() {
		return deleteSiteGroups;
	}

	/**
	 * Gets the degreePreference value for this Site.
	 * @return degreePreference
	 */
	public Integer getDegreePreference() {
		return degreePreference;
	}
	
	/**
	 * Gets the minSP value for this Site.
	 * @return minSP
	 */
	public String getMinSP() {
		return minSP;
	}
	
	/**
	 * Gets the maxSP value for this Site.
	 * @return maxSP
	 */
	public String getMaxSP() {
		return maxSP;
	}

	/**
	 * Gets the nightlyScheduleDownload value for this Site.
	 * @return nightlyScheduleDownload
	 */
	public Integer getNightlyScheduleDownload() {
		return nightlyScheduleDownload;
	}
	
	/**
	 * Gets the customerId value for this Site.
	 * @return customerId
	 */
	public Integer getCustomerId() {
		return customerId;
	}
	
	/**
	 * Gets the isHVACModeToAuto value for this Site.
	 * @return isHVACModeToAuto
	 */
	public Integer getIsHVACModeToAuto() {
		return isHVACModeToAuto;
	}
	
	/**
	 * Gets the resetHoldMode value for this Site.
	 * @return resetHoldMode
	 */
	public Integer getResetHoldMode() {
		return resetHoldMode;
	}
	
	/**
	 * Gets the fanOn value for this Site.
	 * @return fanOn
	 */
	public Integer getFanOn() {
		return fanOn;
	}
	
	/**
	 * Gets the fanAuto value for this Site.
	 * @return fanAuto
	 */
	public Integer getFanAuto() {
		return fanAuto;
	}
	
	/**
	 * Gets the buildingLayout value for this Site.
	 * @return buildingLayout
	 */
	public String getBuildingLayout() {
		return buildingLayout;
	}
	
	/**
	 * Gets the buildingLayoutImage value for this Site.
	 * @return buildingLayoutImage
	 */
	public String getBuildingLayoutImage() {
		return buildingLayoutImage;
	}
	
	/**
	 * Gets the floorPlan value for this Site.
	 * @return floorPlan
	 */
	public String getFloorPlan() {
		return floorPlan;
	}
	
	/**
	 * Gets the rTUList value for this Site.
	 * @return rTUList
	 */
	public List<RTURequest> getrTUList() {
		return rTUList;
	}
	
	/**
	 * Gets the surveyDate value for this Site.
	 * @return surveyDate
	 */
	public String getSurveyDate() {
		return surveyDate;
	}
	
	/**
	 * Gets the squareFootage value for this Site.
	 * @return squareFootage
	 */
	public String getSquareFootage() {
		return squareFootage;
	}
	
	/**
	 * Gets the floorImage value for this Site.
	 * @return floorImage
	 */
	public String getFloorImage() {
		return floorImage;
	}
	
	/**
	 * Gets the occupyHours value for this Site.
	 * @return occupyHours
	 */
	public List<SiteHoursRequest> getOccupyHours() {
		return occupyHours;
	}
	
	/**
	 * Gets the localSiteContact value for this Site.
	 * @return localSiteContact
	 */
	public String getLocalSiteContact() {
		return localSiteContact;
	}
	
	/**
	 * Gets the localContactPhone value for this Site.
	 * @return localContactPhone
	 */
	public String getLocalContactPhone() {
		return localContactPhone;
	}
	
	/**
	 * Gets the localContactEmail value for this Site.
	 * @return localContactEmail
	 */
	public String getLocalContactEmail() {
		return localContactEmail;
	}
	
	/**
	 * Gets the localContactMobile value for this Site.
	 * @return localContactMobile
	 */
	public String getLocalContactMobile() {
		return localContactMobile;
	}
	
	/**
	 * Gets the alternateSiteContact value for this Site.
	 * @return alternateSiteContact
	 */
	public String getAlternateSiteContact() {
		return alternateSiteContact;
	}
	
	/**
	 * Gets the alternateContactPhone value for this Site.
	 * @return alternateContactPhone
	 */
	public String getAlternateContactPhone() {
		return alternateContactPhone;
	}
	
	/**
	 * Gets the escortContactName value for this Site.
	 * @return escortContactName
	 */
	public String getEscortContactName() {
		return escortContactName;
	}
	
	/**
	 * Gets the escortContactNumber value for this Site.
	 * @return escortContactNumber
	 */
	public String getEscortContactNumber() {
		return escortContactNumber;
	}
	
	/**
	 * Gets the listaAccessRestrictionsFromFormalHours value for this Site.
	 * @return listaAccessRestrictionsFromFormalHours
	 */
	public String getListaAccessRestrictionsFromFormalHours() {
		return listaAccessRestrictionsFromFormalHours;
	}
	
	/**
	 * Gets the specialRoomAccessInformation value for this Site.
	 * @return specialRoomAccessInformation
	 */
	public String getSpecialRoomAccessInformation() {
		return specialRoomAccessInformation;
	}
	
	/**
	 * Gets the lockBoxOnThermostat value for this Site.
	 * @return lockBoxOnThermostat
	 */
	public String getLockBoxOnThermostat() {
		return lockBoxOnThermostat;
	}
	
	/**
	 * Gets the accessOrManagesThermostat value for this Site.
	 * @return accessOrManagesThermostat
	 */
	public String getAccessOrManagesThermostat() {
		return accessOrManagesThermostat;
	}
	
	/**
	 * Gets the buildingType value for this Site.
	 * @return buildingType
	 */
	public Integer getBuildingType() {
		return buildingType;
	}
	
	/**
	 * Gets the buildingNotes value for this Site.
	 * @return buildingNotes
	 */
	public String getBuildingNotes() {
		return buildingNotes;
	}
	
	/**
	 * Gets the thermostatList value for this Site.
	 * @return thermostatList
	 */
	public List<ThermostatRequest> getThermostatList() {
		return thermostatList;
	}
	
	/**
	 * Gets the technicianName value for this Site.
	 * @return technicianName
	 */
	public String getTechnicianName() {
		return technicianName;
	}
	
	/**
	 * Gets the technicianPhone value for this Site.
	 * @return technicianPhone
	 */
	public String getTechnicianPhone() {
		return technicianPhone;
	}
	
	/**
	 * Gets the technicianNotes value for this Site.
	 * @return technicianNotes
	 */
	public String getTechnicianNotes() {
		return technicianNotes;
	}
	
	/**
	 * Gets the cellularMode value for this Site.
	 * @return cellularMode
	 */
	public String getCellularMode() {
		return cellularMode;
	}
	
	/**
	 * Gets the cellularCoverage value for this Site.
	 * @return cellularCoverage
	 */
	public String getCellularCoverage() {
		return cellularCoverage;
	}
	
	/**
	 * Gets the cellularProvide value for this Site.
	 * @return cellularProvide
	 */
	public String getCellularProvide() {
		return cellularProvide;
	}
	
	/**
	 * Gets the cellularWIFI value for this Site.
	 * @return cellularWIFI
	 */
	public String getCellularWIFI() {
		return cellularWIFI;
	}
	
	/**
	 * Gets the cellularWIFIPWD value for this Site.
	 * @return cellularWIFIPWD
	 */
	public String getCellularWIFIPWD() {
		return cellularWIFIPWD;
	}
	
	/**
	 * Gets the modemType value for this Site.
	 * @return modemType
	 */
	public Integer getModemType() {
		return modemType;
	}
	
	/**
	 * Gets the simCardNumber value for this Site.
	 * @return simCardNumber
	 */
	public String getSimCardNumber() {
		return simCardNumber;
	}
	
	/**
	 * Gets the modemSerialNumber value for this Site.
	 * @return modemSerialNumber
	 */
	public String getModemSerialNumber() {
		return modemSerialNumber;
	}
	
	/**
	 * Gets the modemIPAddress value for this Site.
	 * @return modemIPAddress
	 */
	public String getModemIPAddress() {
		return modemIPAddress;
	}
	
	/**
	 * Gets the modemPhoneNumber value for this Site.
	 * @return modemPhoneNumber
	 */
	public String getModemPhoneNumber() {
		return modemPhoneNumber;
	}
	
	/**
	 * Gets the isHvacRunning value for this Site.
	 * @return isHvacRunning
	 */
	public String getIsHvacRunning() {
		return isHvacRunning;
	}
	
	/**
	 * Gets the siteHrsFormate value for this Site.
	 * @return siteHrsFormate
	 */
	public Integer getSiteHrsFormate() {
		return siteHrsFormate;
	}
	
	/**
	 * Gets the siteOccupyHrsFormate value for this Site.
	 * @return siteOccupyHrsFormate
	 */
	public Integer getSiteOccupyHrsFormate() {
		return siteOccupyHrsFormate;
	}
	
	/**
	 * Gets the siteTimeZone value for this Site.
	 * @return siteTimeZone
	 */
	public String getSiteTimeZone() {
		return siteTimeZone;
	}
	
	/**
	 * Gets the siteStatus value for this Site.
	 * @return siteStatus
	 */
	public int getSiteStatus() {
		return siteStatus;
	}

	/*********************************** Setters ***********************/
	/**
	 * Sets the siteId value for this Site.
	 * @param siteId
	 */
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	
	/**
	 * Sets the siteName value for this Site.
	 * @param siteName
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	/**
	 * Sets the siteInternalId value for this Site.
	 * @param siteInternalId
	 */
	public void setSiteInternalId(String siteInternalId) {
		this.siteInternalId = siteInternalId;
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
	 * Sets the siteState value for this Site.
	 * @param siteState
	 */
	public void setSiteState(Integer siteState) {
		this.siteState = siteState;
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
	 * Sets the siteStateName value for this Site.
	 * @param siteStateName
	 */
	public void setSiteStateName(String siteStateName) {
		this.siteStateName = siteStateName;
	}
	
	/**
	 * Sets the siteZipCode value for this Site.
	 * @param siteZipCode
	 */
	public void setSiteZipCode(String siteZipCode) {
		this.siteZipCode = siteZipCode;
	}
	
	/**
	 * Sets the sitePhNo value for this Site.
	 * @param sitePhNo
	 */
	public void setSitePhNo(String sitePhNo) {
		this.sitePhNo = sitePhNo;
	}
	
	/**
	 * Sets the siteDistrict value for this Site.
	 * @param siteDistrict
	 */
	public void setSiteDistrict(String siteDistrict) {
		this.siteDistrict = siteDistrict;
	}
	
	/**
	 * Sets the siteRegion value for this Site.
	 * @param siteRegion
	 */
	public void setSiteRegion(String siteRegion) {
		this.siteRegion = siteRegion;
	}
	
	/**
	 * Sets the siteArea value for this Site.
	 * @param siteArea
	 */
	public void setSiteArea(String siteArea) {
		this.siteArea = siteArea;
	}
	
	/**
	 * Sets the siteHours value for this Site.
	 * @param siteHours
	 */
	public void setSiteHours(List<SiteHoursRequest> siteHours) {
		this.siteHours = siteHours;
	}
	
	/**
	 * Sets the siteGroups value for this Site.
	 * @param siteGroups
	 */
	public void setSiteGroups(List<String> siteGroups) {
		this.siteGroups = siteGroups;
	}
	
	/**
	 * Sets the deleteSiteGroups value for this Site.
	 * @param deleteSiteGroups
	 */
	public void setDeleteSiteGroups(List<String> deleteSiteGroups) {
		this.deleteSiteGroups = deleteSiteGroups;
	}
	
	/**
	 * Sets the degreePreference value for this Site.
	 * @param degreePreference
	 */
	public void setDegreePreference(Integer degreePreference) {
		this.degreePreference = degreePreference;
	}
	
	/**
	 * Sets the minSP value for this Site.
	 * @param minSP
	 */
	public void setMinSP(String minSP) {
		this.minSP = minSP;
	}

	/**
	 * Sets the maxSP value for this Site.
	 * @param maxSP
	 */
	public void setMaxSP(String maxSP) {
		this.maxSP = maxSP;
	}
	
	/**
	 * Sets the nightlyScheduleDownload value for this Site.
	 * @param nightlyScheduleDownload
	 */
	public void setNightlyScheduleDownload(Integer nightlyScheduleDownload) {
		this.nightlyScheduleDownload = nightlyScheduleDownload;
	}
	
	/**
	 * Sets the customerId value for this Site.
	 * @param customerId
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	
	/**
	 * Sets the isHVACModeToAuto value for this Site.
	 * @param isHVACModeToAuto
	 */
	public void setIsHVACModeToAuto(Integer isHVACModeToAuto) {
		this.isHVACModeToAuto = isHVACModeToAuto;
	}

	/**
	 * Sets the resetHoldMode value for this Site.
	 * @param resetHoldMode
	 */
	public void setResetHoldMode(Integer resetHoldMode) {
		this.resetHoldMode = resetHoldMode;
	}

	/**
	 * Sets the fanOn value for this Site.
	 * @param fanOn
	 */
	public void setFanOn(Integer fanOn) {
		this.fanOn = fanOn;
	}

	/**
	 * Sets the fanAuto value for this Site.
	 * @param fanAuto
	 */
	public void setFanAuto(Integer fanAuto) {
		this.fanAuto = fanAuto;
	}
	
	/**
	 * Sets the buildingLayout value for this Site.
	 * @param buildingLayout
	 */
	public void setBuildingLayout(String buildingLayout) {
		this.buildingLayout = buildingLayout;
	}
    
	/**
	 * Sets the buildingLayoutImage value for this Site.
	 * @param buildingLayoutImage
	 */
	public void setBuildingLayoutImage(String buildingLayoutImage) {
		this.buildingLayoutImage = buildingLayoutImage;
	}
	
	/**
	 * Sets the floorPlan value for this Site.
	 * @param floorPlan
	 */
	public void setFloorPlan(String floorPlan) {
		this.floorPlan = floorPlan;
	}
	
	/**
	 * Sets the rTUList value for this Site.
	 * @param rTUList
	 */
	public void setrTUList(List<RTURequest> rTUList) {
		this.rTUList = rTUList;
	}
	
	/**
	 * Sets the surveyDate value for this Site.
	 * @param surveyDate
	 */
	public void setSurveyDate(String surveyDate) {
		this.surveyDate = surveyDate;
	}
	
	/**
	 * Sets the squareFootage value for this Site.
	 * @param squareFootage
	 */
	public void setSquareFootage(String squareFootage) {
		this.squareFootage = squareFootage;
	}

	/**
	 * Sets the floorImage value for this Site.
	 * @param floorImage
	 */
	public void setFloorImage(String floorImage) {
		this.floorImage = floorImage;
	}

	/**
	 * Sets the occupyHours value for this Site.
	 * @param occupyHours
	 */
	public void setOccupyHours(List<SiteHoursRequest> occupyHours) {
		this.occupyHours = occupyHours;
	}

	/**
	 * Sets the localSiteContact value for this Site.
	 * @param localSiteContact
	 */
	public void setLocalSiteContact(String localSiteContact) {
		this.localSiteContact = localSiteContact;
	}

	/**
	 * Sets the localContactPhone value for this Site.
	 * @param localContactPhone
	 */
	public void setLocalContactPhone(String localContactPhone) {
		this.localContactPhone = localContactPhone;
	}

	/**
	 * Sets the localContactEmail value for this Site.
	 * @param localContactEmail
	 */
	public void setLocalContactEmail(String localContactEmail) {
		this.localContactEmail = localContactEmail;
	}

	/**
	 * Sets the localContactMobile value for this Site.
	 * @param localContactMobile
	 */
	public void setLocalContactMobile(String localContactMobile) {
		this.localContactMobile = localContactMobile;
	}

	/**
	 * Sets the alternateSiteContact value for this Site.
	 * @param alternateSiteContact
	 */
	public void setAlternateSiteContact(String alternateSiteContact) {
		this.alternateSiteContact = alternateSiteContact;
	}

	/**
	 * Sets the alternateContactPhone value for this Site.
	 * @param alternateContactPhone
	 */
	public void setAlternateContactPhone(String alternateContactPhone) {
		this.alternateContactPhone = alternateContactPhone;
	}

	/**
	 * Sets the escortContactName value for this Site.
	 * @param escortContactName
	 */
	public void setEscortContactName(String escortContactName) {
		this.escortContactName = escortContactName;
	}

	/**
	 * Sets the escortContactNumber value for this Site.
	 * @param escortContactNumber
	 */
	public void setEscortContactNumber(String escortContactNumber) {
		this.escortContactNumber = escortContactNumber;
	}

	/**
	 * Sets the listaAccessRestrictionsFromFormalHours value for this Site.
	 * @param listaAccessRestrictionsFromFormalHours
	 */
	public void setListaAccessRestrictionsFromFormalHours(
			String listaAccessRestrictionsFromFormalHours) {
		this.listaAccessRestrictionsFromFormalHours = listaAccessRestrictionsFromFormalHours;
	}

	/**
	 * Sets the specialRoomAccessInformation value for this Site.
	 * @param specialRoomAccessInformation
	 */
	public void setSpecialRoomAccessInformation(String specialRoomAccessInformation) {
		this.specialRoomAccessInformation = specialRoomAccessInformation;
	}

	/**
	 * Sets the lockBoxOnThermostat value for this Site.
	 * @param lockBoxOnThermostat
	 */
	public void setLockBoxOnThermostat(String lockBoxOnThermostat) {
		this.lockBoxOnThermostat = lockBoxOnThermostat;
	}

	/**
	 * Sets the accessOrManagesThermostat value for this Site.
	 * @param accessOrManagesThermostat
	 */
	public void setAccessOrManagesThermostat(String accessOrManagesThermostat) {
		this.accessOrManagesThermostat = accessOrManagesThermostat;
	}

	/**
	 * Sets the buildingType value for this Site.
	 * @param buildingType
	 */
	public void setBuildingType(Integer buildingType) {
		this.buildingType = buildingType;
	}

	/**
	 * Sets the buildingNotes value for this Site.
	 * @param buildingNotes
	 */
	public void setBuildingNotes(String buildingNotes) {
		this.buildingNotes = buildingNotes;
	}

	/**
	 * Sets the thermostatList value for this Site.
	 * @param thermostatList
	 */
	public void setThermostatList(List<ThermostatRequest> thermostatList) {
		this.thermostatList = thermostatList;
	}

	/**
	 * Sets the technicianName value for this Site.
	 * @param technicianName
	 */
	public void setTechnicianName(String technicianName) {
		this.technicianName = technicianName;
	}

	/**
	 * Sets the technicianPhone value for this Site.
	 * @param technicianPhone
	 */
	public void setTechnicianPhone(String technicianPhone) {
		this.technicianPhone = technicianPhone;
	}

	/**
	 * Sets the technicianNotes value for this Site.
	 * @param technicianNotes
	 */
	public void setTechnicianNotes(String technicianNotes) {
		this.technicianNotes = technicianNotes;
	}

	/**
	 * Sets the cellularMode value for this Site.
	 * @param cellularMode
	 */
	public void setCellularMode(String cellularMode) {
		this.cellularMode = cellularMode;
	}

	/**
	 * Sets the cellularCoverage value for this Site.
	 * @param cellularCoverage
	 */
	public void setCellularCoverage(String cellularCoverage) {
		this.cellularCoverage = cellularCoverage;
	}

	/**
	 * Sets the cellularProvide value for this Site.
	 * @param cellularProvide
	 */
	public void setCellularProvide(String cellularProvide) {
		this.cellularProvide = cellularProvide;
	}

	/**
	 * Sets the cellularWIFI value for this Site.
	 * @param cellularWIFI
	 */
	public void setCellularWIFI(String cellularWIFI) {
		this.cellularWIFI = cellularWIFI;
	}

	/**
	 * Sets the cellularWIFIPWD value for this Site.
	 * @param cellularWIFIPWD
	 */
	public void setCellularWIFIPWD(String cellularWIFIPWD) {
		this.cellularWIFIPWD = cellularWIFIPWD;
	}

	/**
	 * Sets the modemType value for this Site.
	 * @param modemType
	 */
	public void setModemType(Integer modemType) {
		this.modemType = modemType;
	}

	/**
	 * Sets the simCardNumber value for this Site.
	 * @param simCardNumber
	 */
	public void setSimCardNumber(String simCardNumber) {
		this.simCardNumber = simCardNumber;
	}

	/**
	 * Sets the modemSerialNumber value for this Site.
	 * @param modemSerialNumber
	 */
	public void setModemSerialNumber(String modemSerialNumber) {
		this.modemSerialNumber = modemSerialNumber;
	}

	/**
	 * Sets the modemIPAddress value for this Site.
	 * @param modemIPAddress
	 */
	public void setModemIPAddress(String modemIPAddress) {
		this.modemIPAddress = modemIPAddress;
	}

	/**
	 * Sets the modemPhoneNumber value for this Site.
	 * @param modemPhoneNumber
	 */
	public void setModemPhoneNumber(String modemPhoneNumber) {
		this.modemPhoneNumber = modemPhoneNumber;
	}
	
	/**
	 * Sets the isHvacRunning value for this Site.
	 * @param isHvacRunning
	 */
	public void setIsHvacRunning(String isHvacRunning) {
		this.isHvacRunning = isHvacRunning;
	}
	
	/**
	 * Sets the siteHrsFormate value for this Site.
	 * @param siteHrsFormate
	 */
	public void setSiteHrsFormate(Integer siteHrsFormate) {
		this.siteHrsFormate = siteHrsFormate;
	}

	/**
	 * Sets the siteOccupyHrsFormate value for this Site.
	 * @param siteOccupyHrsFormate
	 */
	public void setSiteOccupyHrsFormate(Integer siteOccupyHrsFormate) {
		this.siteOccupyHrsFormate = siteOccupyHrsFormate;
	}
	
	/**
	 * Sets the siteTimeZone value for this Site.
	 * @param siteTimeZone
	 */
	public void setSiteTimeZone(String siteTimeZone) {
		this.siteTimeZone = siteTimeZone;
	}
	
	/**
	 * Sets the siteStatus value for this Site.
	 * @param siteStatus
	 */
	public void setSiteStatus(int siteStatus) {
		this.siteStatus = siteStatus;
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
