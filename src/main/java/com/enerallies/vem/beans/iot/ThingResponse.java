package com.enerallies.vem.beans.iot;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : ThingResponse 
 * 
 * ThingResponse: is for transfer thing response related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        01-09-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	01-09-2016		Rajashekharaiah Muniswamy		File Created
 * 02	13-10-2016 		Rajashekharaiah Muniswamy       Added fields - weather, siteTimezone, siteZipcode
 * 03	14-10-2016		Rajashekharaiah Muniswamy       Added fields - calibration, tempUnits, eTInterval, keyBLockout
 */
@JsonIgnoreProperties(ignoreUnknown=true) 
public class ThingResponse {

	/**This property is used to hold device id*/
	private int 		deviceId;
	
	/**This property is used to hold xcspec device id*/
	private String 		xcspecDeviceId;
	
	/**This property is used to hold device name*/
	private String 		name;
	
	/**This property is used to hold device model*/
	private String 		model;
	
	/**This property is used to hold device mac id*/
	private String  	macId;
	
	/**This property is used to hold device version*/
	private String 		version;
	
	/**This property is used to hold site id*/
	private int     	siteId;
	
	/**This property is used to hold site name*/
	private String  	siteName;
	
	/**This property is used to hold site code*/
	private String  	siteCode;
	
	/**This property is used to hold site internal id*/
	private String 		siteInternalId;
	
	/**This property is used to hold area name or location type*/
	private int  	location;
	
	/**This property is used to hold device registration type*/
	private int 		registerType;
	
	/**This property is used to hold device thing arn*/
	private String 		thingARN;
	
	/**This property is used to hold created by*/
	private int 		createdBy;
	
	/**This property is used to hold created user name*/
	private String 		createdByUserName;
	
	/**This property is used to hold created on*/
	private String 		createdOn;
	
	/**This property is used to hold is active*/
	private int			isActive;
	
	/**This property is used to hold customer id*/
	private int 		customerId;
	
	/**This property is used to hold device type*/
	private int 		deviceType;
	
	/**This property is used to hold device latest status or device state*/
	private Object  thingState;
	
	/**This property is used to hold updated by*/
	private int  		updatedBy;
	
	/**This property is used to hold updated on*/
	private String 		updatedOn;
	
	/**This property is used to hold eai device id*/
	private String  	eaiDeviceId;
	
	/**This property is used to hold device tag*/
	private String 		deviceTag;
	
	/**This property is used to hold sf22hwversion*/
	private String 		sf22HwVersion;
	
	/**This property is used to hold device wifi key*/
	private String 		wifiKey;
	
	/**This property is used to hold device unit*/
	private String 		unit;
	
	/**This property is used to hold device fan preference*/
	private int			fanPref;
	
	/**This property is used to hold hvac to auto*/
	private int 		hvacToAuto;
	
	/**This property is used to hold device hold to auto*/
	private int			holdToAuto;
	
	/**This property is used to hold device minimum set point*/
	private int 		minSP;
	
	/**This property is used to hold device maximum set point*/
	private int 		maxSP;
	
	/**This property is used to hold lock preference*/
	private int 		lockPref;
	
	/**This property is used to hold device night schedule*/
	private int 		nightSchedule;
	
	/**This property is used to hold site zipcode*/
	private String 		siteZipcode;
	
	/**This property is used to hold site timezone*/
	private String 		siteTimezone;
	
	/**This property is used to hold outside weather object of site*/
	private Weather 	weather;
	
	/**This property is used to hold calibration of thermostat*/
	private String 		calibration;
	
	/**This property is used to hold temperature units*/
	private String 		tempUnits;

	/**This property is used to hold keyboard lockout*/
	private String 		keyBLockout;

	/**This property is used to hold engaged transactional interval*/
	private String 		eTInterval;
	
	/**This property is used to hold current UTC date time*/
	private String 		currentUTCDateTime;
	
	/**This property is used to hold relay status*/
	private Object 	relayStatus;
	
	/**This property is used to hold schedule cool*/
	private Object 	scheduleCool;
	
	/**This property is used to hold schedule heat*/
	private Object 	scheduleHeat;

	/**This property is used to hold schedule name*/
	private String 	scheduleName;
	
	/**This property is used to hold alertCount*/
	private int alertCount;

	/**This property is used to hold activityCount*/
	private int activityCount;
	
	/**This property is used to hold scheduleStatus*/
	private int scheduleStatus;
	
	/**This property is used to hold configuration changes which are not affected*/
	private List<ConfigChange> configChanges;
	
	private int scheduleId;
	
	private List<OccupyHours> occHours;
	
	private int coolSetAt;
	
	private int heatSetAt;
	
	private String customerName;
	
	private String groupId;

	private String groupName;
	
	private Object dbSchedule;
	
	private Integer awsCompatible;
	
	private String customerCode;
	
	private int devCommFailConfigTime;
	
	private int deviceStatus;
	
	/**This property is used to hold other location*/
	private String otherLocation;
	
	/**This property is used to hold heat pump*/
	private Integer heatPump;
	
	/**This property is used to hold stage of heat*/
	private Integer stagesOfHeat;

	/**This property is used to hold stage of cool*/
	private Integer stagesOfCool;

	/**This property is used to hold gas auxilary*/
	private Integer gasAuxilary;
	
	
	
	/**
	 * @return the otherLocation
	 */
	public String getOtherLocation() {
		return otherLocation;
	}
	/**
	 * @param otherLocation the otherLocation to set
	 */
	public void setOtherLocation(String otherLocation) {
		this.otherLocation = otherLocation;
	}
	/**
	 * @return the heatPump
	 */
	public Integer getHeatPump() {
		return heatPump;
	}
	/**
	 * @param heatPump the heatPump to set
	 */
	public void setHeatPump(Integer heatPump) {
		this.heatPump = heatPump;
	}
	
	/**
	 * @return the stagesOfHeat
	 */
	public Integer getStagesOfHeat() {
		return stagesOfHeat;
	}
	/**
	 * @param stagesOfHeat the stagesOfHeat to set
	 */
	public void setStagesOfHeat(Integer stagesOfHeat) {
		this.stagesOfHeat = stagesOfHeat;
	}
	/**
	 * @return the stagesOfCool
	 */
	public Integer getStagesOfCool() {
		return stagesOfCool;
	}
	/**
	 * @param stagesOfCool the stagesOfCool to set
	 */
	public void setStagesOfCool(Integer stagesOfCool) {
		this.stagesOfCool = stagesOfCool;
	}
	/**
	 * @return the gasAuxilary
	 */
	public Integer getGasAuxilary() {
		return gasAuxilary;
	}
	/**
	 * @param gasAuxilary the gasAuxilary to set
	 */
	public void setGasAuxilary(Integer gasAuxilary) {
		this.gasAuxilary = gasAuxilary;
	}
	/**
	 * @return the deviceStatus
	 */
	public int getDeviceStatus() {
		return deviceStatus;
	}
	/**
	 * @param deviceStatus the deviceStatus to set
	 */
	public void setDeviceStatus(int deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	/**
	 * @return the devCommFailConfigTime
	 */
	public int getDevCommFailConfigTime() {
		return devCommFailConfigTime;
	}
	/**
	 * @param devCommFailConfigTime the devCommFailConfigTime to set
	 */
	public void setDevCommFailConfigTime(int devCommFailConfigTime) {
		this.devCommFailConfigTime = devCommFailConfigTime;
	}
	/**
	 * @return the customerCode
	 */
	public String getCustomerCode() {
		return customerCode;
	}
	/**
	 * @param customerCode the customerCode to set
	 */
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	/**
	 * @return the awsCompatible
	 */
	public Integer getAwsCompatible() {
		return awsCompatible;
	}
	/**
	 * @param awsCompatible the awsCompatible to set
	 */
	public void setAwsCompatible(Integer awsCompatible) {
		this.awsCompatible = awsCompatible;
	}
	
	/**
	 * @return the dbSchedule
	 */
	public Object getDbSchedule() {
		return dbSchedule;
	}
	/**
	 * @param dbSchedule the dbSchedule to set
	 */
	public void setDbSchedule(Object dbSchedule) {
		this.dbSchedule = dbSchedule;
	}
	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}
	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return the coolSetAt
	 */
	public int getCoolSetAt() {
		return coolSetAt;
	}
	/**
	 * @param coolSetAt the coolSetAt to set
	 */
	public void setCoolSetAt(int coolSetAt) {
		this.coolSetAt = coolSetAt;
	}
	/**
	 * @return the heatSetAt
	 */
	public int getHeatSetAt() {
		return heatSetAt;
	}
	/**
	 * @param heatSetAt the heatSetAt to set
	 */
	public void setHeatSetAt(int heatSetAt) {
		this.heatSetAt = heatSetAt;
	}
	/**
	 * @return the occHours
	 */
	public List<OccupyHours> getOccHours() {
		return occHours;
	}
	/**
	 * @param occHours the occHours to set
	 */
	public void setOccHours(List<OccupyHours> occHours) {
		this.occHours = occHours;
	}
	/**
	 * @return the scheduleId
	 */
	public int getScheduleId() {
		return scheduleId;
	}
	/**
	 * @param scheduleId the scheduleId to set
	 */
	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}
	/**
	 * @return the configChanges
	 */
	public List<ConfigChange> getConfigChanges() {
		return configChanges;
	}
	/**
	 * @param configChanges the configChanges to set
	 */
	public void setConfigChanges(List<ConfigChange> configChanges) {
		this.configChanges = configChanges;
	}
	/**
	 * @return the scheduleStatus
	 */
	public int getScheduleStatus() {
		return scheduleStatus;
	}
	/**
	 * @param scheduleStatus the scheduleStatus to set
	 */
	public void setScheduleStatus(int scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}
	/**
	 * @return the alertCount
	 */
	public int getAlertCount() {
		return alertCount;
	}
	/**
	 * @param alertCount the alertCount to set
	 */
	public void setAlertCount(int alertCount) {
		this.alertCount = alertCount;
	}
	/**
	 * @return the activityCount
	 */
	public int getActivityCount() {
		return activityCount;
	}
	/**
	 * @param activityCount the activityCount to set
	 */
	public void setActivityCount(int activityCount) {
		this.activityCount = activityCount;
	}
	/**
	 * @return the scheduleName
	 */
	public String getScheduleName() {
		return scheduleName;
	}
	/**
	 * @param scheduleName the scheduleName to set
	 */
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	/**
	 * @return the scheduleCool
	 */
	public Object getScheduleCool() {
		return scheduleCool;
	}
	/**
	 * @param scheduleCool the scheduleCool to set
	 */
	public void setScheduleCool(Object scheduleCool) {
		this.scheduleCool = scheduleCool;
	}
	/**
	 * @return the scheduleHeat
	 */
	public Object getScheduleHeat() {
		return scheduleHeat;
	}
	/**
	 * @param scheduleHeat the scheduleHeat to set
	 */
	public void setScheduleHeat(Object scheduleHeat) {
		this.scheduleHeat = scheduleHeat;
	}
	/**
	 * @return the relayStatus
	 */
	public Object getRelayStatus() {
		return relayStatus;
	}
	/**
	 * @param relayStatus the relayStatus to set
	 */
	public void setRelayStatus(Object relayStatus) {
		this.relayStatus = relayStatus;
	}
	/**
	 * @return the currentUTCDateTime
	 */
	public String getCurrentUTCDateTime() {
		return currentUTCDateTime;
	}
	/**
	 * @param currentUTCDateTime the currentUTCDateTime to set
	 */
	public void setCurrentUTCDateTime(String currentUTCDateTime) {
		this.currentUTCDateTime = currentUTCDateTime;
	}
	/**
	 * @return the calibration
	 */
	public String getCalibration() {
		return calibration;
	}
	/**
	 * @param calibration the calibration to set
	 */
	public void setCalibration(String calibration) {
		this.calibration = calibration;
	}
	/**
	 * @return the tempUnits
	 */
	public String getTempUnits() {
		return tempUnits;
	}
	/**
	 * @param tempUnits the tempUnits to set
	 */
	public void setTempUnits(String tempUnits) {
		this.tempUnits = tempUnits;
	}
	/**
	 * @return the keyBLockout
	 */
	public String getKeyBLockout() {
		return keyBLockout;
	}
	/**
	 * @param keyBLockout the keyBLockout to set
	 */
	public void setKeyBLockout(String keyBLockout) {
		this.keyBLockout = keyBLockout;
	}
	/**
	 * @return the eTInterval
	 */
	public String geteTInterval() {
		return eTInterval;
	}
	/**
	 * @param eTInterval the eTInterval to set
	 */
	public void seteTInterval(String eTInterval) {
		this.eTInterval = eTInterval;
	}
	/**
	 * @return the site_zipcode
	 */
	public String getSiteZipcode() {
		return siteZipcode;
	}
	/**
	 * @param site_zipcode the site_zipcode to set
	 */
	public void setSiteZipcode(String siteZipcode) {
		this.siteZipcode = siteZipcode;
	}
	/**
	 * @return the site_timezone
	 */
	public String getSiteTimezone() {
		return siteTimezone;
	}
	/**
	 * @param site_timezone the site_timezone to set
	 */
	public void setSiteTimezone(String siteTimezone) {
		this.siteTimezone = siteTimezone;
	}
	/**
	 * @return the weather
	 */
	public Weather getWeather() {
		return weather;
	}
	/**
	 * @param weather the weather to set
	 */
	public void setWeather(Weather weather) {
		this.weather = weather;
	}
	/**
	 * @return the fanPref
	 */
	public int getFanPref() {
		return fanPref;
	}
	/**
	 * @param fanPref the fanPref to set
	 */
	public void setFanPref(int fanPref) {
		this.fanPref = fanPref;
	}
	/**
	 * @return the hvacToAuto
	 */
	public int getHvacToAuto() {
		return hvacToAuto;
	}
	/**
	 * @param hvacToAuto the hvacToAuto to set
	 */
	public void setHvacToAuto(int hvacToAuto) {
		this.hvacToAuto = hvacToAuto;
	}
	/**
	 * @return the holdToAuto
	 */
	public int getHoldToAuto() {
		return holdToAuto;
	}
	/**
	 * @param holdToAuto the holdToAuto to set
	 */
	public void setHoldToAuto(int holdToAuto) {
		this.holdToAuto = holdToAuto;
	}
	/**
	 * @return the minSP
	 */
	public int getMinSP() {
		return minSP;
	}
	/**
	 * @param minSP the minSP to set
	 */
	public void setMinSP(int minSP) {
		this.minSP = minSP;
	}
	/**
	 * @return the maxSP
	 */
	public int getMaxSP() {
		return maxSP;
	}
	/**
	 * @param maxSP the maxSP to set
	 */
	public void setMaxSP(int maxSP) {
		this.maxSP = maxSP;
	}
	/**
	 * @return the lockPref
	 */
	public int getLockPref() {
		return lockPref;
	}
	/**
	 * @param lockPref the lockPref to set
	 */
	public void setLockPref(int lockPref) {
		this.lockPref = lockPref;
	}
	/**
	 * @return the nightSchedule
	 */
	public int getNightSchedule() {
		return nightSchedule;
	}
	/**
	 * @param nightSchedule the nightSchedule to set
	 */
	public void setNightSchedule(int nightSchedule) {
		this.nightSchedule = nightSchedule;
	}
	/**
	 * @return the siteInternalId
	 */
	public String getSiteInternalId() {
		return siteInternalId;
	}
	/**
	 * @param siteInternalId the siteInternalId to set
	 */
	public void setSiteInternalId(String siteInternalId) {
		this.siteInternalId = siteInternalId;
	}
	/**
	 * @return the deviceType
	 */
	public int getDeviceType() {
		return deviceType;
	}
	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	/**
	 * @return the updatedBy
	 */
	public int getUpdatedBy() {
		return updatedBy;
	}
	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}
	/**
	 * @return the updatedOn
	 */
	public String getUpdatedOn() {
		return updatedOn;
	}
	/**
	 * @param updatedOn the updatedOn to set
	 */
	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}
	/**
	 * @return the eaiDeviceId
	 */
	public String getEaiDeviceId() {
		return eaiDeviceId;
	}
	/**
	 * @param eaiDeviceId the eaiDeviceId to set
	 */
	public void setEaiDeviceId(String eaiDeviceId) {
		this.eaiDeviceId = eaiDeviceId;
	}
	/**
	 * @return the deviceTag
	 */
	public String getDeviceTag() {
		return deviceTag;
	}
	/**
	 * @param deviceTag the deviceTag to set
	 */
	public void setDeviceTag(String deviceTag) {
		this.deviceTag = deviceTag;
	}
	/**
	 * @return the sf22HwVersion
	 */
	public String getSf22HwVersion() {
		return sf22HwVersion;
	}
	/**
	 * @param sf22HwVersion the sf22HwVersion to set
	 */
	public void setSf22HwVersion(String sf22HwVersion) {
		this.sf22HwVersion = sf22HwVersion;
	}
	/**
	 * @return the wifiKey
	 */
	public String getWifiKey() {
		return wifiKey;
	}
	/**
	 * @param wifiKey the wifiKey to set
	 */
	public void setWifiKey(String wifiKey) {
		this.wifiKey = wifiKey;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/**
	 * @return the customerId
	 */
	public int getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the isActive
	 */
	public int getIsActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the location
	 */
	public int getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(int location) {
		this.location = location;
	}
	/**
	 * @return the siteCode
	 */
	public String getSiteCode() {
		return siteCode;
	}
	/**
	 * @param siteCode the siteCode to set
	 */
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 * @return the siteId
	 */
	public int getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}
	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the macId
	 */
	public String getMacId() {
		return macId;
	}
	/**
	 * @param macId the macId to set
	 */
	public void setMacId(String macId) {
		this.macId = macId;
	}

	/**
	 * @return the thingState
	 */
	public Object getThingState() {
		return thingState;
	}
	/**
	 * @param thingState the thingState to set
	 */
	public void setThingState(Object thingState) {
		this.thingState = thingState;
	}
	/**
	 * @return the deviceId
	 */
	public int getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the xcspecDeviceId
	 */
	public String getXcspecDeviceId() {
		return xcspecDeviceId;
	}
	/**
	 * @param xcspecDeviceId the xcspecDeviceId to set
	 */
	public void setXcspecDeviceId(String xcspecDeviceId) {
		this.xcspecDeviceId = xcspecDeviceId;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the regType
	 */
	public int getRegisterType() {
		return registerType;
	}
	/**
	 * @param regType the regType to set
	 */
	public void setRegisterType(int registerType) {
		this.registerType = registerType;
	}
	/**
	 * @return the thingARN
	 */
	public String getThingARN() {
		return thingARN;
	}
	/**
	 * @param thingARN the thingARN to set
	 */
	public void setThingARN(String thingARN) {
		this.thingARN = thingARN;
	}
	/**
	 * @return the createdBy
	 */
	public int getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the createdByUserName
	 */
	public String getCreatedByUserName() {
		return createdByUserName;
	}
	/**
	 * @param createdByUserName the createdByUserName to set
	 */
	public void setCreatedByUserName(String createdByUserName) {
		this.createdByUserName = createdByUserName;
	}
	/**
	 * @return the createdOn
	 */
	public String getCreatedOn() {
		return createdOn;
	}
	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	
	
}
