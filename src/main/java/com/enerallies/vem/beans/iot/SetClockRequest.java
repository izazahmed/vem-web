/**
 * 
 */
package com.enerallies.vem.beans.iot;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : SetClockRequest 
 * 
 * SetClockRequest: is for transfer set clock related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        19-10-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	19-10-2016		Rajashekharaiah Muniswamy		File Created
 */

public class SetClockRequest {

	/**used to hold current time in thermostat*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_SETCLOCK_TIME_NULL)
	@NotEmpty(message = ErrorCodes.ERROR_DEVICE_SETCLOCK_TIME_EMPTY)
	private String currentTime;
	
	/**used to hold current day in thermostat*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_SETCLOCK_DAY_NULL)
	@NotEmpty(message = ErrorCodes.ERROR_DEVICE_SETCLOCK_DAY_EMPTY)
	private String currentDay;

	/**
	 * @return the currentTime
	 */
	public String getCurrentTime() {
		return currentTime;
	}

	/**
	 * @param currentTime the currentTime to set
	 */
	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * @return the currentDay
	 */
	public String getCurrentDay() {
		return currentDay;
	}

	/**
	 * @param currentDay the currentDay to set
	 */
	public void setCurrentDay(String currentDay) {
		this.currentDay = currentDay;
	}
}
