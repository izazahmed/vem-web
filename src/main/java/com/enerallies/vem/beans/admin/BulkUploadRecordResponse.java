package com.enerallies.vem.beans.admin;
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
public class BulkUploadRecordResponse {
	
	/** this property holds the user id */
	private String recordResponse;

	public String getRecordResponse() {
		return recordResponse;
	}

	public void setRecordResponse(String recordResponse) {
		this.recordResponse = recordResponse;
	}

	/** this property holds the user name */
	private String failedFlag;

	public String getFailedFlag() {
		return failedFlag;
	}

	public void setFailedFlag(String failedFlag) {
		this.failedFlag = failedFlag;
	}

}
