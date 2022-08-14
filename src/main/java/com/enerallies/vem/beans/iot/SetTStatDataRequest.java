/**
 * 
 */
package com.enerallies.vem.beans.iot;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : SetTStatDaataRequest 
 * 
 * SetTStatDaataRequest: is for transfer Set Thermostat related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        18-10-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	18-10-2016		Rajashekharaiah Muniswamy		File Created
 */
public class SetTStatDataRequest {
	
	/**This property is used to hold the set hold data*/
	private String data;
	
	/**This property is used to hold the type to set thermostat data*/
	@NotNull(message = ErrorCodes.ERROR_DEVICE_SET_TSTAT_TYPE_NULL)
	@NotEmpty(message = ErrorCodes.ERROR_DEVICE_SET_TSTAT_TYPE_EMPTY)
	private String type;
	
	

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	
}
