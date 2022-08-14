/**
 * 
 */
package com.enerallies.vem.beans.iot;

import javax.validation.constraints.NotNull;

import com.enerallies.vem.util.ErrorCodes;

/**
 * @author rmuniswamy
 *
 */
public class UpdateHeatPumpFieldReq {
	/**Used to hold the device id*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_DEVICEID_NULL)
	private Integer 	deviceId;
	
	/**Used to hold the heat pump update type
	 * 
	 * 1- heat pump
	 * 2- stages of heat
	 * 3- stages of cool
	 * 4- gas auxilary 
	 * 
	 * */
	@NotNull(message = ErrorCodes.ERROR_DEVICE_HEATPUMP_TYPE_NULL)
	private Integer 	heatPumpUpdateType;

	/**Used to hold the heat pump update value*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_HEATPUMP_TYPE_VALUE_NULL)
	private Integer 	heatPumpUpdateTypeValue;

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
	 * @return the heatPumpUpdateType
	 */
	public Integer getHeatPumpUpdateType() {
		return heatPumpUpdateType;
	}

	/**
	 * @param heatPumpUpdateType the heatPumpUpdateType to set
	 */
	public void setHeatPumpUpdateType(Integer heatPumpUpdateType) {
		this.heatPumpUpdateType = heatPumpUpdateType;
	}

	/**
	 * @return the heatPumpUpdateTypeValue
	 */
	public Integer getHeatPumpUpdateTypeValue() {
		return heatPumpUpdateTypeValue;
	}

	/**
	 * @param heatPumpUpdateTypeValue the heatPumpUpdateTypeValue to set
	 */
	public void setHeatPumpUpdateTypeValue(Integer heatPumpUpdateTypeValue) {
		this.heatPumpUpdateTypeValue = heatPumpUpdateTypeValue;
	}


	
}
