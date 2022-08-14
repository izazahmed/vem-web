package com.enerallies.vem.service.customers;

import org.springframework.stereotype.Service;

import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.customers.CreateCustomerRequest;
import com.enerallies.vem.beans.customers.CreateUsersAndSiteRequest;
import com.enerallies.vem.beans.customers.CustomersRequestBean;
import com.enerallies.vem.beans.customers.DeleteCustomerRequest;
import com.enerallies.vem.beans.customers.UpdateCustomerRequest;
import com.enerallies.vem.exceptions.VEMAppException;



/**
 * File Name : CustomersService 
 * 
 * CustomersService: is used declare all the admin operation methods
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
 * 24-08-2016		Arun		File Created
 *
 */


@Service
public interface CustomersService {

	/**
	 * getCustomersList: service layer is used to get the customers details.
	 * @param customerRequest 
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getCustomersListService(CustomersRequestBean customerRequest) throws VEMAppException;
	
	/**
	 * updateCustomerStatus: service layer is used to get the active/deactive customer
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response updateCustomerStatus(CustomersRequestBean customerBean,int userId) throws VEMAppException;
	
	/**
	 * getCustomerProfile: service layer is used to get the customers details.
	 * @param customerBean is the object of CustomerRequestBean class, It accepts string customerId binded in status property of CustomerRequestBean bean class
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response getCustomerProfile(CustomersRequestBean customerBean) throws VEMAppException;
	
		/**
	 * createCustomer : Creates an customer in Database by using customer request object
	 * 
	 * @param customerRequest
	 * @return
	 * @throws VEMAppException
	 */
   public Response createCustomer(CreateCustomerRequest customerRequest,int userId) throws VEMAppException;

	/**
	 * loadStates service is used to fill the add customers form drop downs with data.
	 * 
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response loadStates() throws VEMAppException;
	
	/**
	 * getUserCustomersList service is used to customer data based on user Id.
	 * @param customerBean CustomerRequestBean
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
	
	public Response updateCustomerDetails(UpdateCustomerRequest updateCustomerRequest,int userId) throws VEMAppException;
	   
	   /**
	    * deleteUser: Delete the VEM user
	    * @param deleteUserRequest
	    * @return
	    * @throws VEMAppException
	    */
public Response deleteCustomer(DeleteCustomerRequest deleteCustomerRequest,int userId) throws VEMAppException;

public Response generateCustomerCode(String companyName) throws VEMAppException;

public Response addUsersAndSites(CreateUsersAndSiteRequest createUsersAndSiteRequest) throws VEMAppException;
}
