/**
 * 
 */
package com.enerallies.vem.beans.iot;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * File Name : ThingState 
 * 
 * ThingState: is for transfer thing state related data between different modules 
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
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThingState {

	private String op_state;
	private String heat_set;
	private String cool_set;
	private String zone_temp;
	private String fan_state;
	private String temp_hold;
	private String tstat_mode;
	
	private String tstat_msg;
	
	@JsonProperty("CO2_1")
	private String co2_1;
	
	@JsonProperty("CO2_2")
	private String co2_2;
	
	@JsonProperty("CO2_3")
	private String co2_3;
	
	@JsonProperty("CO2_4")
	private String co2_4;
	
	@JsonProperty("CO2_1_RSSI")
	private String co2_1_RSSI;
	
	@JsonProperty("CO2_2_RSSI")
	private String co2_2_RSSI;
	
	@JsonProperty("CO2_3_RSSI")
	private String co2_3_RSSI;
	
	@JsonProperty("CO2_4_RSSI")
	private String co2_4_RSSI;
	
	private String button_pressed;
	private String datetime;
	
	private RelayState relay_state;
	
	private TStatClock tstat_clock;

	/**
	 * @return the co2_1
	 */
	public String getCo2_1() {
		return co2_1;
	}

	/**
	 * @param co2_1 the co2_1 to set
	 */
	public void setCo2_1(String co2_1) {
		this.co2_1 = co2_1;
	}

	/**
	 * @return the co2_2
	 */
	public String getCo2_2() {
		return co2_2;
	}

	/**
	 * @param co2_2 the co2_2 to set
	 */
	public void setCo2_2(String co2_2) {
		this.co2_2 = co2_2;
	}

	/**
	 * @return the co2_3
	 */
	public String getCo2_3() {
		return co2_3;
	}

	/**
	 * @param co2_3 the co2_3 to set
	 */
	public void setCo2_3(String co2_3) {
		this.co2_3 = co2_3;
	}

	/**
	 * @return the co2_4
	 */
	public String getCo2_4() {
		return co2_4;
	}

	/**
	 * @param co2_4 the co2_4 to set
	 */
	public void setCo2_4(String co2_4) {
		this.co2_4 = co2_4;
	}

	/**
	 * @return the co2_1_RSSI
	 */
	public String getCo2_1_RSSI() {
		return co2_1_RSSI;
	}

	/**
	 * @param co2_1_RSSI the co2_1_RSSI to set
	 */
	public void setCo2_1_RSSI(String co2_1_RSSI) {
		this.co2_1_RSSI = co2_1_RSSI;
	}

	/**
	 * @return the co2_2_RSSI
	 */
	public String getCo2_2_RSSI() {
		return co2_2_RSSI;
	}

	/**
	 * @param co2_2_RSSI the co2_2_RSSI to set
	 */
	public void setCo2_2_RSSI(String co2_2_RSSI) {
		this.co2_2_RSSI = co2_2_RSSI;
	}

	/**
	 * @return the co2_3_RSSI
	 */
	public String getCo2_3_RSSI() {
		return co2_3_RSSI;
	}

	/**
	 * @param co2_3_RSSI the co2_3_RSSI to set
	 */
	public void setCo2_3_RSSI(String co2_3_RSSI) {
		this.co2_3_RSSI = co2_3_RSSI;
	}

	/**
	 * @return the co2_4_RSSI
	 */
	public String getCo2_4_RSSI() {
		return co2_4_RSSI;
	}

	/**
	 * @param co2_4_RSSI the co2_4_RSSI to set
	 */
	public void setCo2_4_RSSI(String co2_4_RSSI) {
		this.co2_4_RSSI = co2_4_RSSI;
	}

	/**
	 * @return the op_state
	 */
	public String getOp_state() {
		return op_state;
	}

	/**
	 * @param op_state the op_state to set
	 */
	public void setOp_state(String op_state) {
		this.op_state = op_state;
	}

	/**
	 * @return the heat_set
	 */
	public String getHeat_set() {
		return heat_set;
	}

	/**
	 * @param heat_set the heat_set to set
	 */
	public void setHeat_set(String heat_set) {
		this.heat_set = heat_set;
	}

	/**
	 * @return the cool_set
	 */
	public String getCool_set() {
		return cool_set;
	}

	/**
	 * @param cool_set the cool_set to set
	 */
	public void setCool_set(String cool_set) {
		this.cool_set = cool_set;
	}

	/**
	 * @return the zone_temp
	 */
	public String getZone_temp() {
		return zone_temp;
	}

	/**
	 * @param zone_temp the zone_temp to set
	 */
	public void setZone_temp(String zone_temp) {
		this.zone_temp = zone_temp;
	}

	/**
	 * @return the fan_state
	 */
	public String getFan_state() {
		return fan_state;
	}

	/**
	 * @param fan_state the fan_state to set
	 */
	public void setFan_state(String fan_state) {
		this.fan_state = fan_state;
	}

	/**
	 * @return the temp_hold
	 */
	public String getTemp_hold() {
		return temp_hold;
	}

	/**
	 * @param temp_hold the temp_hold to set
	 */
	public void setTemp_hold(String temp_hold) {
		this.temp_hold = temp_hold;
	}

	/**
	 * @return the tstat_mode
	 */
	public String getTstat_mode() {
		return tstat_mode;
	}

	/**
	 * @param tstat_mode the tstat_mode to set
	 */
	public void setTstat_mode(String tstat_mode) {
		this.tstat_mode = tstat_mode;
	}

	/**
	 * @return the tstat_msg
	 */
	public String getTstat_msg() {
		return tstat_msg;
	}

	/**
	 * @param tstat_msg the tstat_msg to set
	 */
	public void setTstat_msg(String tstat_msg) {
		this.tstat_msg = tstat_msg;
	}



	/**
	 * @return the button_pressed
	 */
	public String getButton_pressed() {
		return button_pressed;
	}

	/**
	 * @param button_pressed the button_pressed to set
	 */
	public void setButton_pressed(String button_pressed) {
		this.button_pressed = button_pressed;
	}

	/**
	 * @return the datetime
	 */
	public String getDatetime() {
		return datetime;
	}

	/**
	 * @param datetime the datetime to set
	 */
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	/**
	 * @return the relay_state
	 */
	public RelayState getRelay_state() {
		return relay_state;
	}

	/**
	 * @param relay_state the relay_state to set
	 */
	public void setRelay_state(RelayState relay_state) {
		this.relay_state = relay_state;
	}

	/**
	 * @return the tstat_clock
	 */
	public TStatClock getTstat_clock() {
		return tstat_clock;
	}

	/**
	 * @param tstat_clock the tstat_clock to set
	 */
	public void setTstat_clock(TStatClock tstat_clock) {
		this.tstat_clock = tstat_clock;
	}
	
	
}
