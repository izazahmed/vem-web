package com.enerallies.vem.beans.iot;

/**
 * File Name : DeviceMoreDetails 
 * 
 * DeviceMoreDetails: is for transfer device related additional data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        18-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	18-11-2016		Rajashekharaiah Muniswamy		File Created
 */
public class DeviceMoreDetails {

	/**This property is used to hold device schedule name*/
	private String 		devScheduleName;

	/**This property is used to hold device alertCount*/
	private int 		devAlertCount;

	/**This property is used to hold device activityCount*/
	private int 		devActivityCount;

	/**This property is used to hold device schedule status*/
	private int 		devScheduleStatus;
	
	private int 		devScheduleId;
	
	private int 		devCommFailConfigTime;

	
	
	/**
	 * @return the devCommFailConfigTime
	 */
	public int getDevCommFailConfigTime() {
		return devCommFailConfigTime;
	}

	/**
	 * @param devCommFailConfigTime the devCommFailConfigTime to set
	 */
	public void setDevCommFailConfigTime(int devCommFailConfigTime) {
		this.devCommFailConfigTime = devCommFailConfigTime;
	}

	/**
	 * @return the devScheduleId
	 */
	public int getDevScheduleId() {
		return devScheduleId;
	}

	/**
	 * @param devScheduleId the devScheduleId to set
	 */
	public void setDevScheduleId(int devScheduleId) {
		this.devScheduleId = devScheduleId;
	}

	/**
	 * @return the devScheduleName
	 */
	public String getDevScheduleName() {
		return devScheduleName;
	}

	/**
	 * @param devScheduleName the devScheduleName to set
	 */
	public void setDevScheduleName(String devScheduleName) {
		this.devScheduleName = devScheduleName;
	}

	/**
	 * @return the devAlertCount
	 */
	public int getDevAlertCount() {
		return devAlertCount;
	}

	/**
	 * @param devAlertCount the devAlertCount to set
	 */
	public void setDevAlertCount(int devAlertCount) {
		this.devAlertCount = devAlertCount;
	}

	/**
	 * @return the devActivityCount
	 */
	public int getDevActivityCount() {
		return devActivityCount;
	}

	/**
	 * @param devActivityCount the devActivityCount to set
	 */
	public void setDevActivityCount(int devActivityCount) {
		this.devActivityCount = devActivityCount;
	}

	/**
	 * @return the devScheduleStatus
	 */
	public int getDevScheduleStatus() {
		return devScheduleStatus;
	}

	/**
	 * @param devScheduleStatus the devScheduleStatus to set
	 */
	public void setDevScheduleStatus(int devScheduleStatus) {
		this.devScheduleStatus = devScheduleStatus;
	}
	
}
