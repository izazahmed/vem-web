/**
 * 
 */
package com.enerallies.vem.beans.iot;

/**
 * File Name : TStatClock 
 * 
 * TStatClock: is for transfer thing t clock state related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        01-09-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	01-09-2016		Rajashekharaiah Muniswamy		File Created
 */public class TStatClock {
	 
	 private String current_time;
	 private String current_day;
	/**
	 * @return the current_time
	 */
	public String getCurrent_time() {
		return current_time;
	}
	/**
	 * @param current_time the current_time to set
	 */
	public void setCurrent_time(String current_time) {
		this.current_time = current_time;
	}
	/**
	 * @return the current_day
	 */
	public String getCurrent_day() {
		return current_day;
	}
	/**
	 * @param current_day the current_day to set
	 */
	public void setCurrent_day(String current_day) {
		this.current_day = current_day;
	}
	 

}
