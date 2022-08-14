/**
 * 
 */
package com.enerallies.vem.beans.iot;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : ThingUpdateRequest 
 * 
 * ThingUpdateRequest: is for transfer thing(device) update related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        03-10-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	03-10-2016		Rajashekharaiah Muniswamy		File Created
 * 02 	17-10-2016		Rajashekharaiah Muniswamy		modified fields version, wifikey and unit removed mandatory
 */
@JsonIgnoreProperties(ignoreUnknown=true) 
public class ThingUpdateRequest {
	/**This property is used to hold device id*/
	@NotNull(message=ErrorCodes.ERROR_DEVICE_DEVICEID_NULL)
	private Integer 	deviceId;
	
	/**This property is used to hold device name*/
	@NotEmpty(message=ErrorCodes.ERROR_DEVICE_NAME_EMPTY)
	private String 	name;
	
	/**This property is used to hold device model*/
	@NotEmpty(message=ErrorCodes.ERROR_DEVICE_MODEL_EMPTY)
	private String 	model;
	
	/**This property is used to hold device mac id*/
	@NotEmpty(message=ErrorCodes.ERROR_DEVICE_MACID_EMPTY)
	private String  macId;
	
	/**This property is used to hold device version*/
	private String  version;
	
	/**This property is used to hold site id*/
	@NotNull(message=ErrorCodes.ERROR_DEVICE_SITEID_NULL)
	private Integer 	siteId;
	
	/**This property is used to hold area name or location type*/
	private Integer 	location;
	
	/**This property is used to hold registration type */
	@NotNull(message=ErrorCodes.ERROR_DEVICE_REGTYPE_NULL)
	private Integer 	registerType;
	
	/**This property is used to hold customer id*/
	@NotNull(message=ErrorCodes.ERROR_DEVICE_CUSTID_NULL)
	private Integer 	customerId;
	
	/**This property is used to hold device type*/
	@NotNull(message=ErrorCodes.ERROR_DEVICE_DEVTYPE_NULL)
	private Integer 	deviceType;
	
	/**This property is used to hold device tag*/
	private String 	deviceTag;
	
	/**This property is used to hold sf22 hw version*/
	private String 	sf22HwVersion;
	
	/**This property is used to hold device wifi key*/
	private String 	wifiKey;
	
	/**This property is used to hold device unit*/
	private String 	unit;
	
	/**This property is used to hold device fan prefernce*/
	private Integer		fanPref;
	
	/**This property is used to hold device hvac to auto*/
	private Integer 	hvacToAuto;
	
	/**This property is used to hold device hold to auto*/
	private Integer		holdToAuto;
	
	/**This property is used to hold device minimum set point*/
	private Integer 	minSP;
	
	/**This property is used to hold device maximum set point*/
	private Integer 	maxSP;
	
	/**This property is used to hold device lock prefernces*/
	private Integer 	lockPref;
	
	/**This property is used to hold device night schedule*/
	private Integer 	nightSchedule;
	
	/**This property is used to hold updated by*/
	private Integer  	updatedBy;
	
	/**This property is used to hold updated on*/
	private String 	updatedOn;
	
	/**This property is used to hold eai device id*/
	private String  eaiDeviceId;
	
	/**This property is used to hold thing arn*/
	private String 	thingARN;
	
	/**This property is used to hold xcspec device id*/
	private String 	xcspecDeviceId;
	
	/**This property is used to hold is device active*/
	private Integer 	isActive;
	
	/**This property is used to hold aws compatibiliy*/
	@NotNull(message=ErrorCodes.ERROR_DEVICE_AWS_COMPATIBLE_NULL)
	private Integer awsCompatible;
	
	/**This property is used to hold other location*/
	private String otherLocation;
	
	
	
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
	 * @return the isActive
	 */
	public Integer getIsActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the deviceId
	 */
	public Integer getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
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
	 * @return the siteId
	 */
	public Integer getSiteId() {
		return siteId;
	}

	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	/**
	 * @return the location
	 */
	public Integer getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Integer location) {
		this.location = location;
	}

	/**
	 * @return the registerType
	 */
	public Integer getRegisterType() {
		return registerType;
	}

	/**
	 * @param registerType the registerType to set
	 */
	public void setRegisterType(Integer registerType) {
		this.registerType = registerType;
	}

	/**
	 * @return the customerId
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the deviceType
	 */
	public Integer getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
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
	 * @return the fanPref
	 */
	public Integer getFanPref() {
		return fanPref;
	}

	/**
	 * @param fanPref the fanPref to set
	 */
	public void setFanPref(Integer fanPref) {
		this.fanPref = fanPref;
	}

	/**
	 * @return the hvacToAuto
	 */
	public Integer getHvacToAuto() {
		return hvacToAuto;
	}

	/**
	 * @param hvacToAuto the hvacToAuto to set
	 */
	public void setHvacToAuto(Integer hvacToAuto) {
		this.hvacToAuto = hvacToAuto;
	}

	/**
	 * @return the holdToAuto
	 */
	public Integer getHoldToAuto() {
		return holdToAuto;
	}

	/**
	 * @param holdToAuto the holdToAuto to set
	 */
	public void setHoldToAuto(Integer holdToAuto) {
		this.holdToAuto = holdToAuto;
	}

	/**
	 * @return the minSP
	 */
	public Integer getMinSP() {
		return minSP;
	}

	/**
	 * @param minSP the minSP to set
	 */
	public void setMinSP(Integer minSP) {
		this.minSP = minSP;
	}

	/**
	 * @return the maxSP
	 */
	public Integer getMaxSP() {
		return maxSP;
	}

	/**
	 * @param maxSP the maxSP to set
	 */
	public void setMaxSP(Integer maxSP) {
		this.maxSP = maxSP;
	}

	/**
	 * @return the lockPref
	 */
	public Integer getLockPref() {
		return lockPref;
	}

	/**
	 * @param lockPref the lockPref to set
	 */
	public void setLockPref(Integer lockPref) {
		this.lockPref = lockPref;
	}

	/**
	 * @return the nightSchedule
	 */
	public Integer getNightSchedule() {
		return nightSchedule;
	}

	/**
	 * @param nightSchedule the nightSchedule to set
	 */
	public void setNightSchedule(Integer nightSchedule) {
		this.nightSchedule = nightSchedule;
	}

	/**
	 * @return the updatedBy
	 */
	public Integer getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(Integer updatedBy) {
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
	
	
}
