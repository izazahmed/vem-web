package com.enerallies.vem.beans.iot;

public class DevForecastResponse {

	private int deviceId;
	
	private String deviceName;
	
	private int forecastFlag;

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
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @param deviceName the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * @return the forecastFlag
	 */
	public int getForecastFlag() {
		return forecastFlag;
	}

	/**
	 * @param forecastFlag the forecastFlag to set
	 */
	public void setForecastFlag(int forecastFlag) {
		this.forecastFlag = forecastFlag;
	}
	
	
}
