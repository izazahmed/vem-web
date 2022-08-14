package com.enerallies.vem.controller.customers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enerallies.vem.beans.admin.GetUserRequest;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.customers.CreateCustomerRequest;
import com.enerallies.vem.beans.customers.CreateCustomerResponse;
import com.enerallies.vem.beans.customers.CreateUsersAndSiteRequest;
import com.enerallies.vem.beans.customers.CustomersRequestBean;
import com.enerallies.vem.beans.customers.DeleteCustomerRequest;
import com.enerallies.vem.beans.customers.DeleteCustomerResponse;
import com.enerallies.vem.beans.customers.UpdateCustomerRequest;
import com.enerallies.vem.service.admin.AdminService;
import com.enerallies.vem.service.customers.CustomersService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : CustomersController 
 * 
 * CustomersController Class: This class file is responsible to handle all the all the
 *  customers related functionalities like list of customers, create customer, edit customer.. etc
 *
 * @author (Arun Singh – CTE).
 * 
 * Contact (Chenna Reddy)
 * 
 * @version     VEM2-1.0
 * @date        24-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 24-08-2016		Arun Singh		    File Created
 * 
 *
 */

@Controller
@RequestMapping("/api/customers")
public class CustomersController {

	private static final Logger logger=Logger.getLogger(CustomersController.class);
	@Autowired private CustomersService customerService;
	@Autowired private AdminService adminservice;
	
	private GetUserResponse userDetails;
	
	/**
	 * validateSession method is for validating session
	 * @param session; HttpSession
	 * @return boolean
	 */

	private boolean validateSession(HttpSession session){
		boolean sessionFlag=false;
		try {
			userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
			// checking for if session is valid or not
			if(userDetails != null){
				sessionFlag=true;
			}
					
		} catch (Exception e) {
			logger.error("" ,e);
			sessionFlag=false;
		}
		return sessionFlag;

	}
	
	
	/**
	 * customersList controller is used to fetch the customers list for both active and inactive users.
	 * @return ResponseEntity<Response
	 */
	
	@RequestMapping(value="/customerslist",method=RequestMethod.POST)
	@ResponseBody
	private ResponseEntity<Response> customersList(@RequestParam String access_token, HttpServletRequest request, HttpSession session){
		
		logger.info("[BEGIN] [Customers List] [Customers Controller Layer]");
		userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		Response response = new Response();
		HttpStatus status = HttpStatus.BAD_REQUEST;

		try {
			//validating session
			if(!validateSession(session)){
				logger.error("[ERROR][CustomersController][customerList]");
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}else{
				GetUserRequest userRequest = new GetUserRequest();
				userRequest.setUserId(userDetails.getUserId());
				String timeZone=userDetails.getTimeZone();
				userDetails = (GetUserResponse)adminservice.getUserDetails(userRequest, userDetails.getIsSuper(), userDetails.getUserId(), userDetails.getTimeZone()).getData();
				userDetails.setTimeZone(timeZone);
				session.setAttribute("eaiUserDetails", userDetails);
				CustomersRequestBean customerRequest=new CustomersRequestBean();
				customerRequest.setCustomers(userDetails.getCustomers());
				customerRequest.setIsSuperAdmin(userDetails.getIsSuper());
				customerRequest.setUserId(userDetails.getUserId().toString());
				response=customerService.getCustomersListService(customerRequest);
				status = HttpStatus.OK;
			}
			
		} 
		catch(Exception ve ){
			logger.error("",ve);
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
				
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMERS_FETCH);
			}
			
			response.setData(CommonConstants.ERROR_OCCURRED);
			
		}
		
		logger.info("[END] [Customers List] [Customers Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	
	/**
	 * User based customersList controller is used to fetch the customers list for both active and inactive users.
	 * @return ResponseEntity<Response>
	 */
	
	@RequestMapping(value="/getCustomersByUser",method=RequestMethod.GET)
	@ResponseBody
	private ResponseEntity<Response> getCustomersByUser(@RequestParam String access_token, HttpServletRequest request, HttpSession session){
		
		logger.info("[BEGIN] [getCustomersByUser] [Customers Controller Layer]");
		userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		Response response = new Response();
		HttpStatus status = HttpStatus.BAD_REQUEST;

		try {
			//validating session
			if(!validateSession(session)){
				logger.error("[ERROR][CustomersController][getCustomersByUser]");
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}else{
				GetUserRequest userRequest = new GetUserRequest();
				CustomersRequestBean customerRequest=new CustomersRequestBean();
				customerRequest.setIsSuperAdmin(userDetails.getIsSuper());
				customerRequest.setCustomers(userDetails.getCustomers());
				customerRequest.setUserId(userDetails.getUserId().toString());
				response=customerService.getCustomerList(customerRequest);
				status = HttpStatus.OK;
			}
			
		} 
		catch(Exception ve ){
			logger.error("",ve);
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
				
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMERS_FETCH);
			}
			
			response.setData(CommonConstants.ERROR_OCCURRED);
			
		}
		
		logger.info("[END] [getCustomersByUser] [Customers Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	/**
	 * changeCustomerStatus controller is used to activate / deactivate the customer status
	 * @Param customer id
	 * @return ResponseEntity<Response
	 */
	
	@RequestMapping(value="/changeCustomerStatus",method=RequestMethod.POST)
	@ResponseBody
	private ResponseEntity<Response> changeCustomerStatus(@RequestBody CustomersRequestBean customerBean, HttpSession session,HttpServletRequest request  ){
		
		logger.info("[BEGIN] [changeCustomerStatus] [Customers Controller Layer]");
		Response response = new Response();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		try{
			
			//validating session
			if(!validateSession(session)){
				logger.error("[ERROR][CustomersController][changeCustomerStatus]");
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
	
			}else{
				response=customerService.updateCustomerStatus(customerBean,userDetails.getUserId());
				status=HttpStatus.OK;
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.INFO_CUSTOMER_STATUS_UPDATE);
			}
			
		}catch(Exception  ve){
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
			status=HttpStatus.INTERNAL_SERVER_ERROR;	
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			}
			logger.error("",ve);
		}

		
		logger.info("[END] [changeCustomerStatus] [Customers Controller Layer]");
		return new ResponseEntity<>(response, status);
	}

	/**
	 * getCustomerProfile controller is used to customer details 
	 * @Param customer id
	 * @return ResponseEntity<Response
	 */
	
	@RequestMapping(value="/getCustomerProfile",method=RequestMethod.POST)
	@ResponseBody
	private ResponseEntity<Response> getCustomerProfile(@RequestBody CustomersRequestBean customerBean, HttpSession session ){
		
		logger.info("[BEGIN] [getCustomerProfile] [Customers Controller Layer]");
		
		Response response = new Response();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		try{
			
			//validating session
			if(!validateSession(session)){
				logger.error("[ERROR][CustomersController][getCustomerProfile]");
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
				
			}else{
				customerBean.setUserId(userDetails.getUserId().toString());
				customerBean.setIsSuperAdmin(userDetails.getIsSuper());
				response=customerService.getCustomerProfile(customerBean);
				status=HttpStatus.OK;
			}
			
		}catch(Exception  ve){
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
				
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			}
			logger.error("",ve);
		}

		
		logger.info("[END] [getCustomerProfile] [Customers Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * createCustomer: Creates new customer
	 * @param createCustomerRequest
	 * @author (Madhu Bantu – CTE).
	 * @return ResponseEntity<Response>
	 * @throws IOException 
	 */
	 @ResponseBody
	@RequestMapping(value = "/createCustomer", method = RequestMethod.POST)
	public ResponseEntity<Response> createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest, HttpSession session) throws IOException {
		
		logger.info("[BEGIN] [createCustomer] [Controller Layer]");
	
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		// Getting the session details
				GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		try {
			// checking for if session is valid or not
						if(userDetails != null){
							
							// create customer details
							response = customerService.createCustomer(createCustomerRequest,userDetails.getUserId());
							String companyLogo = adminservice.getCompanyLogo(userDetails.getUserId() != null ? userDetails.getUserId() : 0);
							userDetails.setCompanyLogo(companyLogo);
							CreateCustomerResponse createCustomerResponse = (CreateCustomerResponse)response.getData();
							createCustomerResponse.setGetUserResponse(userDetails);
							response.setData(createCustomerResponse);
							
						}else{
							
							// this request is unauthorized
							status = HttpStatus.UNAUTHORIZED;
							
							// Preparing failure response
							response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
							response.setCode(ErrorCodes.INVALID_SESSION);
						}
			
			logger.error("[controllerResponse] [createCustomer] [Controller Layer]"+response);
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.CUSTOMER_ERROR_SAVE_FAILED);
			logger.error("[ERROR] [createCustomer] [Controller Layer]"+e);
		}
		
		logger.info("[END] [createCustomer] [Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * loadState controller is used to load the states while loading customer form.
	 * @author (Madhu Bantu – CTE).
	 * @param session
	 * @return ResponseEntity<Response>
	 */
	@RequestMapping(value="/loadStates", method=RequestMethod.GET)
	private ResponseEntity<Response> loadStates(HttpSession session){
		
		logger.info("[BEGIN] [loadStates] [Customers Controller Layer]");
		
		/* 
		 * Instantiating Response object and HttpStatus object
		 */
		Response response = new Response();
		HttpStatus status = HttpStatus.OK;
		
		try {
			response = customerService.loadStates();
			status = HttpStatus.OK;

		}catch (Exception e) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			logger.error("",e);
		}
		
		logger.info("[END] [loadStates] [Customers Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}

	
	/**
	 * getUserCustomersList controller is used to customer details 
	 * @Param userId
	 * @return ResponseEntity<Response
	 */
	
	@RequestMapping(value="/getusercustomerslist",method=RequestMethod.POST)
	@ResponseBody
	private ResponseEntity<Response> getUserCustomersList(@RequestBody CustomersRequestBean customerBean, HttpSession session ){
		
		logger.info("[BEGIN] [getUserCustomersList] [Customers Controller Layer]");
		
		Response response = new Response();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		try{
			
			//validating session
			if(!validateSession(session)){
				logger.error("[ERROR][CustomersController][getUserCustomersList]");
				status = HttpStatus.UNAUTHORIZED; 
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}else {
				
				response=customerService.getUserCustomersList(customerBean);
				status=HttpStatus.OK;
			}
			
		}catch(Exception  ve){
			status=HttpStatus.INTERNAL_SERVER_ERROR;
			response.setStatus(ve.getMessage());
			if(response.getStatus()==null || response.getStatus().isEmpty()){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			}
				
			if(response.getCode()==null || response.getCode().isEmpty()){
				response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			}
			logger.error("",ve);
		}
		
		logger.info("[END] [getUserCustomersList] [Customers Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	 /**
	 * CustomerCode: Generate Customer Code
	 * @param createCustomerRequest
	 * @author (Madhu Bantu – CTE).
	 * @return ResponseEntity<Response>
	 */
	
	@RequestMapping(value = "/generateCustomerCode/{companyName}",method = RequestMethod.GET)
	public ResponseEntity<Response> generateCustomerCode(@PathVariable("companyName") String companyName,HttpSession session) {
		
		logger.info("[BEGIN] [generateCustomerCode] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		// Getting the session details
				GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		try {

			// generate customer code details
			response = customerService.generateCustomerCode(companyName);
			logger.error("[controllerResponse] [generateCustomerCode] [Controller Layer]"+response);
			
		} catch (Exception e) {
			
			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.CUSTOMER_CODE_ERROR_GENERATE_FAILED);
			logger.error("[ERROR] [generateCustomerCode] [Controller Layer]"+e);
		}
		
		logger.info("[END] [generateCustomerCode] [Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * updateUserDetails: updates customer details
	 * @param updateCustomerRequest
	 * @param session, multipart request
	 * @author (Madhu Bantu – CTE).
	 * @return ResponseEntity<Response> 
	 */
	@ResponseBody
	@RequestMapping(value = "/updateCustomer", method = RequestMethod.POST)
	public ResponseEntity<Response> updateCustomer(	@RequestBody UpdateCustomerRequest updateCustomerRequest, HttpSession session) throws IOException {
		logger.info("[BEGIN] [updateCustomer] [Controller Layer]");


		// Instantiating Response object
		Response response = new Response();

		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session
				.getAttribute("eaiUserDetails");
		try {
			// checking for if session is valid or not
			if (userDetails != null) {

				// create customer details
				response = customerService.updateCustomerDetails(updateCustomerRequest, userDetails.getUserId());
				String companyLogo = adminservice.getCompanyLogo(userDetails.getUserId() != null ? userDetails.getUserId() : 0);
				userDetails.setCompanyLogo(companyLogo);
				
				CreateCustomerResponse createCustomerResponse = (CreateCustomerResponse)response.getData();
				createCustomerResponse.setGetUserResponse(userDetails);
				response.setData(createCustomerResponse);

			} else {

				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;

				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}

			logger.error("[controllerResponse] [updateCustomerDetails] [Controller Layer]"
					+ response);

		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.CUSTOMER_ERROR_SAVE_FAILED);
			logger.error("[ERROR] [updateCustomerDetails] [Controller Layer]"
					+ e);
		}

		logger.info("[END] [updateCustomer] [Controller Layer]");
		return new ResponseEntity<>(response, status);
	}
			
	/**
	 * deleteCustomer : Deletes customer details
	 * @param deleteCustomerRequest
	 * @author (Madhu Bantu – CTE).
	 * @param session,deleteCustomerRequest
	 * @return ResponseEntity<Response> 
	 */
	@RequestMapping(value = "/deleteCustomer", method = RequestMethod.POST)
	public ResponseEntity<Response> deleteCustomer(@RequestBody DeleteCustomerRequest deleteCustomerRequest, HttpSession session) {
		
		logger.info("[BEGIN] [deleteCustomerDetails] [Controller Layer]");
		
		// Instantiating Response object
		Response response = new Response();
		
		// status code instantiation
		HttpStatus status = HttpStatus.OK;
		
		// Getting the session details
		GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
		
		try {
		
			//checking for if session is valid or not
			if(userDetails != null){

				// deleting user details
				response = customerService.deleteCustomer(deleteCustomerRequest,userDetails.getUserId());
				
				String companyLogo = adminservice.getCompanyLogo(userDetails.getUserId() != null ? userDetails.getUserId() : 0);
				userDetails.setCompanyLogo(companyLogo);
				
				DeleteCustomerResponse deleteCustomerResponse = (DeleteCustomerResponse)response.getData();
				deleteCustomerResponse.setGetUserResponse(userDetails);
				response.setData(deleteCustomerResponse);
				
			}else{
				
				// this request is unauthorized
				status = HttpStatus.UNAUTHORIZED;
				
				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.INVALID_SESSION);
			}
			
			
		} catch (Exception e) {

			// Preparing failure response
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.CUSTOMER_ERROR_DELETE_FAILED);
			logger.error("[ERROR] [deleteUserDetails] [Controller Layer] : "+e);
			
		}
		
		logger.info("[END] [deleteUserDetails] [Controller Layer]");
		
		return new ResponseEntity<>(response, status);
	}
	
	/**
	 * createCustomer: Create users and sites for customer
	 * @author (Madhu Bantu – CTE).
	 * @param CreateUsersAndSiteRequest
	 * @return ResponseEntity<Response>
	 * @throws IOException 
	 */
	 @ResponseBody
	@RequestMapping(value = "/createUsersAndSites", method = RequestMethod.POST)
	public ResponseEntity<Response> createUsersAndSites(@RequestBody CreateUsersAndSiteRequest createUsersAndSiteRequest, HttpSession session) throws IOException {
		// Instantiating Response object
			Response response = new Response();
			
			// status code instantiation
			HttpStatus status = HttpStatus.OK;
			

			// Getting the session details
			GetUserResponse userDetails = (GetUserResponse) session.getAttribute("eaiUserDetails");
			
			try {
			
				//checking for if session is valid or not
				if(userDetails != null){

					// deleting user details
					response = customerService.addUsersAndSites(createUsersAndSiteRequest);
					
				}else{
					
					// this request is unauthorized
					status = HttpStatus.UNAUTHORIZED;
					
					// Preparing failure response
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.INVALID_SESSION);
				}
				
				
			} catch (Exception e) {

				// Preparing failure response
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.CUSTOMER_ERROR_INSERT_FAILED);
				logger.error("[ERROR] [insertUserDetails] [Controller Layer] : "+e);
				
			}
		 return new ResponseEntity<>(response, status);
	 }
	
}