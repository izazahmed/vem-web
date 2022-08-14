/**
 * 
 */
package com.enerallies.vem.beans.iot;

/**
 * File Name : ThermostatUnit 
 * 
 * ThermostatUnit: is for transfer thermostat unit related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        28-09-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	28-09-2016		Rajashekharaiah Muniswamy		File Created
 */
public class ThermostatUnit {

	/**used to hold thermostat unit*/
	private String tstatUnit;

	/**used to hold thermostat unit*/
	private Integer locationType;
	private String otherLocation;

	
	public String getOtherLocation() {
		return otherLocation;
	}

	public void setOtherLocation(String otherLocation) {
		this.otherLocation = otherLocation;
	}

	/**
	 * @return the locationType
	 */
	public Integer getLocationType() {
		return locationType;
	}

	/**
	 * @param locationType the locationType to set
	 */
	public void setLocationType(Integer locationType) {
		this.locationType = locationType;
	}

	/**
	 * @return the tstatUnit
	 */
	public String getTstatUnit() {
		return tstatUnit;
	}

	/**
	 * @param tstatUnit the tstatUnit to set
	 */
	public void setTstatUnit(String tstatUnit) {
		this.tstatUnit = tstatUnit;
	}
	
	
}
