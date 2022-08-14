package com.enerallies.vem.serviceimpl.customers;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.common.ConvertJsonBeanToDAOBean;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.beans.customers.CreateCustomerRequest;
import com.enerallies.vem.beans.customers.CreateCustomerResponse;
import com.enerallies.vem.beans.customers.CreateUsersAndSiteRequest;
import com.enerallies.vem.beans.customers.Customer;
import com.enerallies.vem.beans.customers.CustomerCodeResponse;
import com.enerallies.vem.beans.customers.CustomersRequestBean;
import com.enerallies.vem.beans.customers.DeleteCustomerRequest;
import com.enerallies.vem.beans.customers.DeleteCustomerResponse;
import com.enerallies.vem.beans.customers.UpdateCustomerRequest;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.dao.customers.CustomersDAO;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.customers.CustomersService;
import com.enerallies.vem.service.iot.ThingService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : CustomerServiceImpl 
 * 
 * CustomerServiceImpl: Its an implementation class for CustomerService interface.
 * This class has the business logic to serve the requests of customer service.
 *
 * @author (Arun – CTE).
 * 
 * Contact (Chenna Reddy)
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
@Component
public class CustomersServiceImpl implements CustomersService {

	private static final Logger logger=Logger.getLogger(CustomersServiceImpl.class);
	@Autowired private CustomersDAO customersDao;
	
	@Autowired private ThingService thingService;
	
	@Autowired private IoTDao ioTDao;
	
	/**
	 * getCustomersList: dao imp layer implementation is used to get the customers details. This layer is interacts with database.
	 * @param customerRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response getCustomersListService(CustomersRequestBean customerRequest) throws VEMAppException{
		
		try {
			return customersDao.getCustomersList(customerRequest); 
		} 
		catch(VEMAppException ve){
			throw ve;
		}
		catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		
	}
	/**
	 * updateCustomerStatus: dao impl layer is used to make the active/deactive customer. This layer is interacts with database
	 * @param It accepts string active/inactive status, customerId property binded in status property of CustomerRequestBean bean class
	 * @return Response
	 * @throws VEMAppException
	 */
	
	@Override
	public Response updateCustomerStatus(CustomersRequestBean customerBean,int userId)
			throws VEMAppException {
		
		try {
			return customersDao.updateCustomerStatus(customerBean,userId); 
		} 
		catch(VEMAppException ve){
			throw ve;
		}
		catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		
	}

	/**
	 * getCustomerProfile: dao impl layer is used to get the customer details. This layer is interacts with database
	 * @param It accepts string customerId binded in status property of CustomerRequestBean bean class
	 * @return Response
	 * @throws VEMAppException
	 */
	
	@Override
	public Response getCustomerProfile(CustomersRequestBean customerBean)
			throws VEMAppException {
		
		try {
			return customersDao.getCustomerProfile(customerBean); 
		} 
		catch(VEMAppException ve){
			throw ve;
		}
		catch (Exception e) {
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		
	}

	@Override
	public Response createCustomer(CreateCustomerRequest createCustomerRequest,int userId) throws VEMAppException {
        logger.info("[BEGIN] [saveCustomer] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		// Initialization of customerId
				String key = "";
		
		try {
			
			/* Instantiating the ValidatorBean and validating the CreateUserRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(createCustomerRequest);
			
			// if any of the property of createUserRequest is not a valid property then sending corresponding error message 
			if(validatorBean.isNotValid()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
			
				// converting client request to DAO bean
				Customer customer = ConvertJsonBeanToDAOBean.saveCustomerJsonToDAOBean(createCustomerRequest);
				
					// calling to saveUserDetails to save user details
					key = customersDao.saveCustomerDetails(customer,userId);
					
					// Splitting the key with separator '~'
					int customerId = Integer.parseInt(key.split("~")[0]);
					
					/*
					 * if customerId is >=1 means success 
					 * else request is fail
					 */
					if(customerId >= 1){
						
						CreateCustomerResponse createCustomerResponse = new CreateCustomerResponse();
						createCustomerResponse.setCustomerID(customerId);
						createCustomerResponse.setCompanyName(customer.getCompanyName());
		
						// Adding status code and response data to the response object
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.CUSTOMER_INFO_SAVE_SUCCESS);
						response.setData(createCustomerResponse);
						
					}
					// validating the key value and assigning corresponding error codes to it
					if(customerId ==0){
						
						// Preparing success response
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.CUSTOMER_CODE_ALREADY_EXISTS);
						
					}
			}
		}
		 catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ERROR_SAVE_FAILED, logger, e);
		}
		
		logger.info("[END] [saveCustomer] [SERVICE LAYER]");
		
		return response;
	}

	
	@Override
	public Response loadStates() throws VEMAppException {
          logger.info("[BEGIN] [loadStates] [CustomersService SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store list of all the drop down values.
		JSONObject loadObject;
		
		try {
			
			//Calling the DAO layer loadAddSite() method.
			loadObject = customersDao.loadStates();
			
			/* if loadObject is not null means the get all the drop down values
			 * request is success else fail.
			 */
			if(loadObject!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.LOAD_STATE_SUCCESS);
				response.setData(loadObject);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.LOAD_STATE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.LOAD_STATE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LOAD_STATE_FAILED, logger, e);
		}
		
		logger.info("[END] [loadStates] [CustomersService SERVICE LAYER]");
		
		return response;
	}
	
	
	/**
	 * getUserCustomersList service impl layer, It communicates persistence server layer to fetch customer data based on user Id.
	 * @param customerBean CustomerRequestBean
	 * @return Response
	 * @throws VEMAppException
	*/
	
	@Override
	public Response getUserCustomersList(CustomersRequestBean customerBean)
			throws VEMAppException {
		logger.info("[BEGIN] [getUserCustomersList] [CustomersService SERVICE LAYER]");
		Response response=new Response();
		try{
			
			// validating user id
			
			if(customerBean.getUserId()==null || customerBean.getUserId().isEmpty()){
				throw new VEMAppException("User ID parameter is required ");
			}else{
				response=customersDao.getUserCustomersList(customerBean);
			}
			
		}catch(Exception e){
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		logger.info("[END] [getUserCustomersList] [CustomersService SERVICE LAYER]");
		return response;
	}
	
	@Override
	public Response generateCustomerCode(String companyName) throws VEMAppException {
			  logger.info("[BEGIN] [generateCustomerCode] [SERVICE LAYER]");
				
				// Creating response instance
				Response response = new Response();
				
					
						String customerCode = customersDao.generateCustomerCode(companyName); 
							 // if customerId is >=1 means success 
							 //else request is fail
							 
							if(customerCode != null){
								
								CustomerCodeResponse customerCodeResponse = new CustomerCodeResponse();
								customerCodeResponse.setCustomerCode(customerCode.toString().toUpperCase());
				
								// Adding status code and response data to the response object
								response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
								response.setCode(ErrorCodes.CUSTOMER_CODE_INFO_GENERATE_SUCCESS);
								response.setData(customerCodeResponse);
								
							}
			
				
				logger.info("[END] [generateCustomerCode] [SERVICE LAYER]");
				
				return response;
	}
	@Override
	public Response updateCustomerDetails(UpdateCustomerRequest updateCustomerRequest,int userId) throws VEMAppException {
        logger.info("[BEGIN] [updateCustomerDetails] [SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		// Initialization of customerId
		String key = "";

		try {
		
			/* Instantiating the ValidatorBean and validating the UpdateUserRequest bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(updateCustomerRequest);
			
			// if any property is not a valid property in GetUserRequest then sending corresponding error message
			if(validatorBean.isNotValid()){ 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			} else {
				
					// converting client request to DAO bean
					Customer customer = ConvertJsonBeanToDAOBean.updateCustomerJsonToDAOBean(updateCustomerRequest);
						
						// calling to updateUserDetails to update user details
						key = customersDao.updateCustomerDetails(customer,userId);
						
						// Splitting the key with separator '~'
						int customerId = Integer.parseInt(key.split("~")[0]);
						
						/*
						 * if customerId is >=1 means success 
						 * else request is fail
						 */
						if(customerId >= 1){
							
							CreateCustomerResponse createCustomerResponse = new CreateCustomerResponse();
							createCustomerResponse.setCustomerID(customerId);
							createCustomerResponse.setCompanyName(customer.getCompanyName());
			
							// Adding status code and response data to the response object
							response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
							response.setCode(ErrorCodes.CUSTOMER_INFO_UPDATE_SUCCESS);
							response.setData(createCustomerResponse);
							
						}
						// validating the key value and assigning corresponding error codes to it
						if(customerId ==0){
							
							// Preparing success response
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.CUSTOMER_CODE_ALREADY_EXISTS);
							
						}
				}
			}
			 catch (Exception e) {
				// Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ERROR_UPDATE_FAILED, logger, e);
			}
			
			logger.info("[END] [updateCustomerDetails] [SERVICE LAYER]");
			
			return response;
		}
	@Override
	public Response deleteCustomer(DeleteCustomerRequest deleteCustomerRequest,int userId)
			throws VEMAppException {

		logger.info("[BEGIN] [deleteCustomer] [SERVICE LAYER]");
			
			// Creating response instance
			Response response = new Response();
			
			try {
			
				/* Instantiating the ValidatorBean and validating the DeleteUserRequest bean.*/
				ValidatorBean validatorBean = ConfigurationUtils.validateBeans(deleteCustomerRequest);
				
				// if any property is not a valid property in DeleteUserRequest then sending corresponding error message  
				if(validatorBean.isNotValid()){
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.CUSTOMER_ERROR_DELETE_FAILED);
					response.setData(validatorBean.getMessage());
				}else{
					
					// calling to deleteUserDetails to delete user details
					int customerId = customersDao.deleteCustomerDetails(deleteCustomerRequest.getCustomerId(),userId);
					
					/*
					 *  if status is >=1 means success 
					 *  else request is fail
					 */
					if(customerId >= 1){
						
						// Calling deleteDevice 
						List<ThingResponse> thingList = ioTDao.getThingListByCustomer(deleteCustomerRequest.getCustomerId());
						for(ThingResponse thingResponse : thingList) {
							Response deleteDeviceResponse = thingService.deleteDevice(thingResponse.getDeviceId(),userId);
							logger.info("[deleteCustomer] [SERVICE LAYER] [DeleteDeviceResponse] " +deleteDeviceResponse);
						}
						
						// Delete response instance
						DeleteCustomerResponse deleteCustomerResponse = new DeleteCustomerResponse();
						deleteCustomerResponse.setCustomerId(customerId);
		
						// Preparing success response object
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.CUSTOMER_INFO_DELETE_SUCCESS);
						response.setData(deleteCustomerResponse);
						
					}else{
						// Preparing failure response object
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.CUSTOMER_ERROR_DELETE_FAILED);
					}
				}
			} catch (Exception e) {
				// Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ERROR_DELETE_FAILED, logger, e);
			}
			
			logger.info("[END] [deleteCustomer] [SERVICE LAYER]");
			
			return response;
		}
	@Override
	public Response addUsersAndSites(CreateUsersAndSiteRequest createUsersAndSiteRequest) throws VEMAppException {

		logger.info("[BEGIN] [addUsersAndSites] [SERVICE LAYER]");
			
			// Creating response instance
			Response response = new Response();
			
			try {
			
				/* Instantiating the ValidatorBean and validating the DeleteUserRequest bean.*/
				ValidatorBean validatorBean = ConfigurationUtils.validateBeans(createUsersAndSiteRequest);
				
				// if any property is not a valid property in DeleteUserRequest then sending corresponding error message  
				if(validatorBean.isNotValid()){
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.CUSTOMER_ERROR_INSERT_FAILED);
					response.setData(validatorBean.getMessage());
				}else{
					
					// calling to deleteUserDetails to delete user details
					int customerId = customersDao.createUsersAndSites(createUsersAndSiteRequest);
					
					/*
					 *  if status is >=1 means success 
					 *  else request is fail
					 */
					if(customerId >= 1){
						
						// Delete response instance
						DeleteCustomerResponse deleteCustomerResponse = new DeleteCustomerResponse();
						deleteCustomerResponse.setCustomerId(customerId);
		
						// Preparing success response object
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.CUSTOMER_INFO_INSERT_SUCCESS);
						response.setData(deleteCustomerResponse);
						
					}else{
						// Preparing failure response object
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.CUSTOMER_ERROR_INSERT_FAILED);
					}
				}
			} catch (Exception e) {
				// Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ERROR_INSERT_FAILED, logger, e);
			}
			
			logger.info("[END] [addUsersAndSites] [SERVICE LAYER]");
			

			return response;
	}
	
	@Override
	public Response getCustomerList(CustomersRequestBean customerBean)
			throws VEMAppException {
		logger.info("[BEGIN] [getCustomerList] [CustomersService SERVICE LAYER]");
		Response response=new Response();
		try{
			
			// validating user id
			
			if(customerBean.getUserId()==null || customerBean.getUserId().isEmpty()){
				throw new VEMAppException("User ID parameter is required ");
			}else{
				response=customersDao.getCustomerList(customerBean);
			}
			
		}catch(Exception e){
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		logger.info("[END] [getCustomerList] [CustomersService SERVICE LAYER]");
		return response;
	}
	
}
