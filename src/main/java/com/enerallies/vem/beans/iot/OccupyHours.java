package com.enerallies.vem.beans.iot;

public class OccupyHours {

	private int 	dayOfWeek;
	private String 	openTime;
	private String 	closeTime;
	private int 	occupyHours;
	/**
	 * @return the dayOfWeek
	 */
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	/**
	 * @param dayOfWeek the dayOfWeek to set
	 */
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	/**
	 * @return the openTime
	 */
	public String getOpenTime() {
		return openTime;
	}
	/**
	 * @param openTime the openTime to set
	 */
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	/**
	 * @return the closeTime
	 */
	public String getCloseTime() {
		return closeTime;
	}
	/**
	 * @param closeTime the closeTime to set
	 */
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}
	/**
	 * @return the occupyHours
	 */
	public int getOccupyHours() {
		return occupyHours;
	}
	/**
	 * @param occupyHours the occupyHours to set
	 */
	public void setOccupyHours(int occupyHours) {
		this.occupyHours = occupyHours;
	}
	
	
	
}
