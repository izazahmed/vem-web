/**
 * 
 */
package com.enerallies.vem.beans.iot;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : DisconnectDeviceRequest 
 * 
 * DisconnectDeviceRequest: is for transfer disconnect device info data between different modules 
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
 * 01	21-09-2016		Rajashekharaiah Muniswamy		File Created
  */
public class DisconnectDeviceRequest {

	/**Used to hold the device id*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_DEVICEID_NULL)
	private int 	deviceId;
	
	/**used to hold the mac id*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_MACID_NULL)
	@NotEmpty(message = ErrorCodes.ERROR_DEVICE_MACID_EMPTY)
	private String 	macId;
	
	/**used to hold the XCSPEC device id*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_XCSPECID_NULL)
	@NotEmpty(message = ErrorCodes.ERROR_DEVICE_XCSPECID_EMPTY)
	private String 	xcspecDeviceId;

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
