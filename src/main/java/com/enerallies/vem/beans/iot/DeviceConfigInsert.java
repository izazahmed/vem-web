package com.enerallies.vem.beans.iot;

/**
 * File Name : DeviceConfigInsert 
 * 
 * DeviceConfigInsert: is for transfer device configuration data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        17-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	17-11-2016		Rajashekharaiah Muniswamy		File Created
  */
public class DeviceConfigInsert {
	/**used to hold device configuration insert record id*/
	private int deviceConfigId;
	
	/**used to hold device action
	 * //'1 - Target setpoint cool\n2-   Target setpoint Heat\n3 -Set point \n4- Havc mode \n5-Fan mode \n6 - calibration\n7- lock \n8- message\n9-clock day\n10- clock time\n11-clock min\n12-ET\n13- Reset\n'
	 * */
	private int action;
	
	/**used to hold value*/
	private String value;
	
	/**used to hold created on*/
	private String  createdOn;
	
	/**used to hold created by*/
	private int createdBy;

	/**used to hold updated flag*/
	private int updatedFlag;
	
	/**used to hold device  id*/
	private int deviceId;

	/**
	 * @return the deviceConfigId
	 */
	public int getDeviceConfigId() {
		return deviceConfigId;
	}

	/**
	 * @param deviceConfigId the deviceConfigId to set
	 */
	public void setDeviceConfigId(int deviceConfigId) {
		this.deviceConfigId = deviceConfigId;
	}

	/**
	 * @return the action
	 */
	public int getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the createdOn
	 */
	public String getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the createdBy
	 */
	public int getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the updatedFlag
	 */
	public int getUpdatedFlag() {
		return updatedFlag;
	}

	/**
	 * @param updatedFlag the updatedFlag to set
	 */
	public void setUpdatedFlag(int updatedFlag) {
		this.updatedFlag = updatedFlag;
	}

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
}
