package com.enerallies.vem.dao.pdfreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import com.enerallies.vem.beans.pdfreport.AddPDFReportDataRequest;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : PdfReportDAO 
 * PdfReportDAO dao is used to serve all the database level operations related to pdf.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-02-2017
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 08-02-2017		Bhoomika Rabadiya   File Created.
 *
 */
@Repository
public interface PdfReportDAO {
	
	 /**
	  * This is the method to be used to initialize database resources i.e. connection.
	  * 
	  * @param dataSource
	  */
	 public void setDataSource(DataSource dataSource);
	 
	 public JSONObject getReportList(int userId, int reportId) throws VEMAppException;
	 
	 public int addPDFReportData(AddPDFReportDataRequest addPDFReportDataRequest, int userId) throws VEMAppException;
	 
	 public int deletePDFReport(String reportId) throws VEMAppException;
	 
	 public Map<Integer, ArrayList<HashMap<String, String>>> getPDFReportUsersData() throws VEMAppException;
	   
	 public Map<Integer, ArrayList<HashMap<String, String>>> getPDFReportUsersCustomerData() throws VEMAppException;
	 
	 public Map<Integer, ArrayList<HashMap<String, String>>> getPDFReportUsersGroupData() throws VEMAppException;
	   
}
