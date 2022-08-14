package com.enerallies.vem.beans.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.enerallies.vem.beans.admin.User;
import com.enerallies.vem.dao.admin.AdminDao;
import com.enerallies.vem.exceptions.VEMAppException;
/*
 * File Name : UploadUserData 
 * Response: This is the common method for upload users to get the list of customers,sites, and groups based on roles.
 * @author (Madhu Bantu – CTE).
 * Contact (Umang)
 * @version     VEM2-1.0
 * 17-10-2016	Madhu Bantu		File Created
 *
 */
public class UploadUserData {
	public String getUploadUserData(String role, String assignedCustomer, String assginedGroup, String assignedSite, AdminDao adminDao) throws VEMAppException{
		// Creating response instance
		Response response = new Response();
	    String userData = adminDao.uploadUserData(role,assignedCustomer, assginedGroup, assignedSite);
		return userData;
	}
}
