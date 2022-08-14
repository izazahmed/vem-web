/* This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.report;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;

/**
 * File Name : TemperatureSetpoint 
 * 
 * TemperatureSetpoint: is a bean for TimeSetpoint report
 *
 * @author Naagrjuna Eerla
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        07-12-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	07-12-2016			Nagarjuna Eerla		File Created
 */
public class TemperatureSetpoint {
	
	private String name;
	private List<JSONArray> data = new LinkedList<>();
	private String paramUnit;
	private int deviceId;
	private String deviceName;
	private int paramId;
	private String paramShortName;
	private String paramFullName;
	
	public String getParamShortName() {
		return paramShortName;
	}
	public void setParamShortName(String paramShortName) {
		this.paramShortName = paramShortName;
	}
	public String getParamFullName() {
		return paramFullName;
	}
	public void setParamFullName(String paramFullName) {
		this.paramFullName = paramFullName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<JSONArray> getData() {
		return data;
	}
	public void setData(List<JSONArray> data) {
		this.data = data;
	}
	public String getParamUnit() {
		return paramUnit;
	}
	public void setParamUnit(String paramUnit) {
		this.paramUnit = paramUnit;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public int getParamId() {
		return paramId;
	}
	public void setParamId(int paramId) {
		this.paramId = paramId;
	}
}
