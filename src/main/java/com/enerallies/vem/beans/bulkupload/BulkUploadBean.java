package com.enerallies.vem.beans.bulkupload;

import com.enerallies.vem.beans.admin.GetUserResponse;

public class BulkUploadBean extends GetUserResponse{

	private int currentRecord;
	private String fileName;
	private int totalRecords;
	private String uploadStatus;
	private int bulkUploadProgressId;
	
	public int getBulkUploadProgressId() {
		return bulkUploadProgressId;
	}
	public void setBulkUploadProgressId(int bulkUploadProgressId) {
		this.bulkUploadProgressId = bulkUploadProgressId;
	}
	public int getCurrentRecord() {
		return currentRecord;
	}
	public String getFileName() {
		return fileName;
	}
	public int getTotalRecords() {
		return totalRecords;
	}
	public String getUploadStatus() {
		return uploadStatus;
	}
	public void setCurrentRecord(int currentRecord) {
		this.currentRecord = currentRecord;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
		
}
