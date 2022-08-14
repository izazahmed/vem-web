/**
 * 
 */
package com.enerallies.vem.beans.iot;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : SetTemperatureRequest 
 * 
 * SetTemperatureRequest: is for transfer SetTemperature related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        17-10-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	17-10-2016		Rajashekharaiah Muniswamy		File Created
 */
public class SetTemperatureRequest {
	
	/**used to hold mode of set temperature*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_SETTEMP_MODE_NULL)
	@NotEmpty(message = ErrorCodes.ERROR_DEVICE_SETTEMP_MODE_EMPTY)
	private String mode;
	
	/**used to hold the temperature value*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_SETTEMP_TEMP_NULL)
	@NotEmpty(message = ErrorCodes.ERROR_DEVICE_SETTEMP_TEMP_EMPTY)
	private String temp;

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return the temp
	 */
	public String getTemp() {
		return temp;
	}

	/**
	 * @param temp the temp to set
	 */
	public void setTemp(String temp) {
		this.temp = temp;
	}
	
}
