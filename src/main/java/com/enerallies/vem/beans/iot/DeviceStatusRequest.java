/**
 * 
 */
package com.enerallies.vem.beans.iot;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : DeviceStatusRequest 
 * 
 * DeviceStatusRequest: is for transfer status of thing data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        31-08-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	20-09-2016		Rajashekharaiah Muniswamy		File Created
  */
public class DeviceStatusRequest {

	/**Used to hold the device id*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_DEVICEID_NULL)
	private Integer 	deviceId;
	
	/**used to hold the mac id*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_MACID_NULL)
	@NotEmpty(message = ErrorCodes.ERROR_DEVICE_MACID_EMPTY)
	private String 	macId;
	
	/**used to hold the device status*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_ISACTIVE_NULL)
	private Integer 	isActive;

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
	
	
}
