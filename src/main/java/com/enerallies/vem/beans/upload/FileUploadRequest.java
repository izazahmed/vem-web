package com.enerallies.vem.beans.upload;

import org.springframework.web.multipart.MultipartFile;

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

public class FileUploadRequest {
	
	/** This property is used to hold the company images*/
	private MultipartFile  image;
	
	/** This property is used to store the fileLocation in database. */
	private String fileLocation;
	
	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

}