package com.enerallies.vem.beans.admin;

import java.io.File;

/**
 * File Name : BulkUploadResponse 
 * BulkUploadResponse: is used to provide users upload response to client side
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        01-11-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 01-11-2016		Madhu Bantu	        File Created
 *
 */
public class BulkUploadResponse {
	
	/** this property holds the failed records */
	private int failedCount;
	/** This property is used to hold the succes records*/
	private int sucessCount;
	/** This property is used to hold the total records*/
	private int totalCount;
	/** This property is used to hold the file names*/
	private String  fileName;
	private String insertRecord;
	private String recordResponse;
	private int minSP;
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public String getInsertRecord() {
		return insertRecord;
	}

	public void setInsertRecord(String insertRecord) {
		this.insertRecord = insertRecord;
	}

	public String getRecordResponse() {
		return recordResponse;
	}

	public void setRecordResponse(String recordResponse) {
		this.recordResponse = recordResponse;
	}

	public int getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}

	public int getSucessCount() {
		return sucessCount;
	}

	public void setSucessCount(int sucessCount) {
		this.sucessCount = sucessCount;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getMinSP() {
		return minSP;
	}

	public void setMinSP(int minSP) {
		this.minSP = minSP;
	}

}
