/**
 * 
 */
package com.enerallies.vem.beans.iot;

/**
 * File Name : TSTATPreference 
 * 
 * TSTATPreference: is for transfer thermostat preference related data  between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
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
public class TSTATPreference {

	/**This property is used to hold device fan prefernce*/
	private Integer		fanPref;
	
	/**This property is used to hold device hvac to auto*/
	private Integer 	hvacToAuto;
	
	/**This property is used to hold device hold to auto*/
	private Integer		holdToAuto;
	
	/**This property is used to hold device minimum set point*/
	private Integer 	minSP;
	
	/**This property is used to hold device maximum set point*/
	private Integer 	maxSP;
	
	/**This property is used to hold device night schedule*/
	private Integer 	nightSchedule;

	/**This property is used to hold device lock status*/
	private Integer lock;
	
	
	
	/**
	 * @return the lock
	 */
	public Integer getLock() {
		return lock;
	}

	/**
	 * @param lock the lock to set
	 */
	public void setLock(Integer lock) {
		this.lock = lock;
	}

	/**
	 * @return the fanPref
	 */
	public Integer getFanPref() {
		return fanPref;
	}

	/**
	 * @param fanPref the fanPref to set
	 */
	public void setFanPref(Integer fanPref) {
		this.fanPref = fanPref;
	}

	/**
	 * @return the hvacToAuto
	 */
	public Integer getHvacToAuto() {
		return hvacToAuto;
	}

	/**
	 * @param hvacToAuto the hvacToAuto to set
	 */
	public void setHvacToAuto(Integer hvacToAuto) {
		this.hvacToAuto = hvacToAuto;
	}

	/**
	 * @return the holdToAuto
	 */
	public Integer getHoldToAuto() {
		return holdToAuto;
	}

	/**
	 * @param holdToAuto the holdToAuto to set
	 */
	public void setHoldToAuto(Integer holdToAuto) {
		this.holdToAuto = holdToAuto;
	}

	/**
	 * @return the minSP
	 */
	public Integer getMinSP() {
		return minSP;
	}

	/**
	 * @param minSP the minSP to set
	 */
	public void setMinSP(Integer minSP) {
		this.minSP = minSP;
	}

	/**
	 * @return the maxSP
	 */
	public Integer getMaxSP() {
		return maxSP;
	}

	/**
	 * @param maxSP the maxSP to set
	 */
	public void setMaxSP(Integer maxSP) {
		this.maxSP = maxSP;
	}

	/**
	 * @return the nightSchedule
	 */
	public Integer getNightSchedule() {
		return nightSchedule;
	}

	/**
	 * @param nightSchedule the nightSchedule to set
	 */
	public void setNightSchedule(Integer nightSchedule) {
		this.nightSchedule = nightSchedule;
	}
	
	
}
