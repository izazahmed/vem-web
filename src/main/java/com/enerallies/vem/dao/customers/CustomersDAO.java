package com.enerallies.vem.dao.customers;



/**
 * File Name : CustomerDAO 
 * 
 * CustomersDAO: Its an persistance interface class.
 * This class has the persistance logic to serve the requests of customer service.
 *
 * @author (Arun – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        24-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 24-08-2016		Arun     		    File Created
 *
 */


import java.util.List;

import javax.sql.DataSource;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Repository;

import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.customers.CreateUsersAndSiteRequest;
import com.enerallies.vem.beans.customers.Customer;
import com.enerallies.vem.beans.customers.CustomersRequestBean;
import com.enerallies.vem.exceptions.VEMAppException;

@Repository
public interface CustomersDAO  {
	/**
	 * getCustomersList: dao layer is used to get the customers details.
	 * @param customerRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getCustomersList(CustomersRequestBean customerRequest) throws VEMAppException;
	/**
	 * updateCustomerStatus: dao is a persistance service layer, used to get the customers updateCustomerStatus.
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response updateCustomerStatus(CustomersRequestBean customerBean,int userId) throws VEMAppException;
	/**
	 * getCustomerProfile: dao is a persistance service layer, used to get the customers updateCustomerStatus.
	 * @param customerBean object of CustomerRequestBean. accepts customerId as setter, getter in customerBean
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getCustomerProfile(CustomersRequestBean customerBean) throws VEMAppException;
	
	/**
	 * setDataSource: To set data source object.
	 * @param dataSource, accepts the data source object declared in spring mapping file
	 * @return void
	 * @throws VEMAppException
	 */
	public void setDataSource(DataSource dataSource); 
	
	/**
	    * saveUserDetails: Creates an customer
	    * 
	    * @param userRequest
	    * @return
	    * @throws Exception
	    */
	public String saveCustomerDetails(Customer customerRequest, int userId) throws VEMAppException;
	 /**
	  * loadStates dao is used to get all drop down values from database.
	  * 
	  * @return JSONObject
	  * @throws VEMAppException
	  */
	 public JSONObject loadStates() throws VEMAppException;
	
	 /**
		 * getUserCustomersList: dao layer is used to get the customers details.
		 * @param customerBean is the CustomerRequestBean object
		 * @return Response
		 * @throws VEMAppException
		 */
	public Response getUserCustomersList(CustomersRequestBean customerBean) throws VEMAppException;
	
	
	 /**
	 * getUserCustomersList: dao layer is used to get the customers details.
	 * @param customerBean is the CustomerRequestBean object
	 * @return Response
	 * @throws VEMAppException
	 */
   public Response getCustomerList(CustomersRequestBean customerBean) throws VEMAppException;
	
	 /**
	    * updateCustomerDetails(Customer customer): Updates VEM Customer Details
	    * @param user
	    * @return
	    * @throws VEMAppException
	    */
	public String updateCustomerDetails(Customer customer,int userId) throws VEMAppException;
	   
	   /**
	    * deleteUser: Delete the VEM customer
	    * @param deleteCustomerRequest
	    * @return
	    * @throws VEMAppException
	    */
	 public int deleteCustomerDetails(int customerId,int userId) throws VEMAppException;
	 public String generateCustomerCode(String companyName) throws VEMAppException;
	 public int createUsersAndSites(CreateUsersAndSiteRequest createUsersAndSiteRequest) throws VEMAppException;
		
}
