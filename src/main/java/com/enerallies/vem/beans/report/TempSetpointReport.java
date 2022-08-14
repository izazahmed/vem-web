/* This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.report;

/**
 * File Name : TempSetpointReport 
 * 
 * TempSetpointReport: is a bean store all the dao values to do manupulations
 *
 * @author Naagrjuna Eerla
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        12-12-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	12-12-2016			Nagarjuna Eerla		File Created
 */
public class TempSetpointReport {
	private long utcTime;
	private String analyticParamName;
	private String analyticParamUnit;
	private String analyticParamId;
	private double paramValue;
	private int deviceId;
	private String deviceName;
	private String createdTime;
	private String paramFullName;
	
	public long getUtcTime() {
		return utcTime;
	}
	public void setUtcTime(long time) {
		this.utcTime = time;
	}
	public String getAnalyticParamName() {
		return analyticParamName;
	}
	public void setAnalyticParamName(String analyticParamName) {
		this.analyticParamName = analyticParamName;
	}
	public String getAnalyticParamUnit() {
		return analyticParamUnit;
	}
	public void setAnalyticParamUnit(String analyticParamUnit) {
		this.analyticParamUnit = analyticParamUnit;
	}
	public String getAnalyticParamId() {
		return analyticParamId;
	}
	public void setAnalyticParamId(String analyticParamId) {
		this.analyticParamId = analyticParamId;
	}
	public double getParamValue() {
		return paramValue;
	}
	public void setParamValue(double paramValue) {
		this.paramValue = paramValue;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getParamFullName() {
		return paramFullName;
	}
	public void setParamFullName(String paramFullName) {
		this.paramFullName = paramFullName;
	}
}
