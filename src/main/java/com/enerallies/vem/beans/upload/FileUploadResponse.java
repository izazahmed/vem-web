package com.enerallies.vem.beans.upload;

import com.enerallies.vem.beans.admin.GetUserResponse;

/**
 * File Name : FileUploadRequest 
 * FileUploadRequest: is used to upload the images and store it in folder.
 *
 * @author (Madhu Bantu – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        30-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 30-09-2016		Madhu Bantu 		File Created
 *
 */

public class FileUploadResponse extends GetUserResponse{
	
	/** This property is used to hold the company images*/
	private String  fileName;
	
	/** This property is used to store the fileLocation in database. */
	private String fileLocation;
	private int customerId;
	

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

}