package com.enerallies.vem.beans.schedule;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddScheduleDetails {

	
	private String scheduleName;
	
	private String scheduleId;
	
	private String oldName;
	
	private String customerId;
	
	private String siteId;
	
	private String groupId;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	private String deviceId;
	
	
	
	/**
	 * 
	 */
	private JSONObject timepointsmap;
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	private ArrayList<AddScheduleDetails> schdlObjList;
	
	public ArrayList<AddScheduleDetails> getSchdlObjList() {
		return schdlObjList;
	}
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	public void setSchdlObjList(ArrayList<AddScheduleDetails> schdlObjList) {
		this.schdlObjList = schdlObjList;
	}
	public String getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	private String dayId;
	public String getDayId() {
		return dayId;
	}
	public void setDayId(String dayId) {
		this.dayId = dayId;
	}
	private String am;
	private String time;
	public String getAm() {
		return am;
	}
	public void setAm(String am) {
		this.am = am;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getHtpoint() {
		return htpoint;
	}
	public void setHtpoint(String htpoint) {
		this.htpoint = htpoint;
	}
	public String getHtunit() {
		return htunit;
	}
	public void setHtunit(String htunit) {
		this.htunit = htunit;
	}
	public String getClpoint() {
		return clpoint;
	}
	public void setClpoint(String clpoint) {
		this.clpoint = clpoint;
	}
	public String getClunit() {
		return clunit;
	}
	public void setClunit(String clunit) {
		this.clunit = clunit;
	}
	private String htpoint;
	private String htunit;
	private String clpoint;
	private String clunit;
	
	
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	public JSONObject getTimepointsmap() {
		return timepointsmap;
	}
	public void setTimepointsmap(JSONObject timepointsmap) {
		this.timepointsmap = timepointsmap;
	}

	
}
