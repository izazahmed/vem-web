package com.enerallies.vem.daoimpl.customers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.amazonaws.util.StringUtils;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.customers.CreateUsersAndSiteRequest;
import com.enerallies.vem.beans.customers.Customer;
import com.enerallies.vem.beans.customers.CustomersRequestBean;
import com.enerallies.vem.dao.LookUpDao;
import com.enerallies.vem.dao.customers.CustomersDAO;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;



/**
 * File Name : CustomerDAOImpl 
 * 
 * CustomersDAO: Its an implementation class for CustomerDAO interface.
 * This class has the persistance logic to serve the requests of customer service.
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
public class CustomersDaoImpl implements CustomersDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;	
	private static final Logger logger=Logger.getLogger(CustomersDaoImpl.class);
	private static final String FETCH_CUSTOMER_PROC="call sp_select_companies(?,?,?)"; // pass 1 to fetch active customers, 0 for inactive
	private static final String FETCH_CUSTOMER_DETIALS="call sp_select_customer_details (?,?,?)"; // pass customer id to get customer details
	private static final String UPDATE_CUSTOMER_STATUS="sp_update_customer_status"; 
	private static final String FETCH_USER_CUSTOMER_DETAILS_PROC="call sp_select_user_companies (?)";
	private static final String FETCH_USER_CUSTOMER_LIST_PROC="call sp_select_user_customers (?,?,?)";
	private static final String FETCH_CUSTOMER_DEVICES="sp_customer_devices";
	
	@Autowired
	LookUpDao lookUpDao;
	@Autowired IoTDao iotDao;
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}

	/**
	 * getCustomersList: dao impl layer is used to get the customers details. This method interacts with data base.
	 * @param customerRequest
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response getCustomersList(CustomersRequestBean customerRequest) throws VEMAppException{
		Response response=new Response();
		try{
			
			logger.info("[BEGIN] [Customers List] [Customers DAO Impl]");
			logger.debug("[DEBUG] Executing "+FETCH_CUSTOMER_PROC+" with input param "+customerRequest.getCustomers()+" "+customerRequest.getIsSuperAdmin()+""+customerRequest.getUserId());
			
			//Executing the procedure.
			response= jdbcTemplate.query(FETCH_CUSTOMER_PROC, new Object[]{customerRequest.getCustomers(),customerRequest.getIsSuperAdmin(),customerRequest.getUserId()}, new ResultSetExtractor<Response>() {
				@Override
				public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
					Response response=new Response();
					JSONArray resultAry=new JSONArray();
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					while(rs.next()){
						JSONObject tempData=new JSONObject();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
						}
						
					   if(tempData.get("isActive")!=null ){
						tempData.put("isActive", Integer.parseInt(tempData.get("isActive").toString()));   
					   }	
					   resultAry.add(tempData);
					   
					}
					
					if(resultAry.isEmpty() || resultAry.size()<=0){
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.ERROR_CUSTOMER_FETCH);
					}else{
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.INFO_CUSTOMERS_FETCH);
					}
					response.setData(resultAry);
					return response;
				}
				
			});

		}catch(Exception e){
			logger.error("",e);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_CUSTOMERS_FATAL, logger, e);
		}
		
		logger.info("[END] [Customers List] [Customers DAO Impl]");
		return response;
	}

	/**
	 * updateCustomerStatus: dao is a persistance service impl layer, used to get the customers updateCustomerStatus. This method interacts with data base
	 * @param customerBean object of CustomerRequestBean. accepts customerId as setter, getter in customerBean
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response updateCustomerStatus(CustomersRequestBean customerBean,int userId)
			throws VEMAppException {
		Response response=new Response();
		SimpleJdbcCall simpleJdbcCall;
		try{
			
			logger.info("[BEGIN] [updateCustomerStatus] [Customers DAO Impl]");
			
			//Executing the procedure.
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(UPDATE_CUSTOMER_STATUS);
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<String, Object>();
			Map<String,Object> outParameters ;
			
			logger.debug("updating the status for customer "+customerBean.getCustomerId()+" value "+customerBean.getStatus());
			
			
			inputParams.put("customerStatus", customerBean.getStatus());
			inputParams.put("in_customer_id", customerBean.getCustomerId());
			inputParams.put("user_id",userId);
			
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        logger.debug("[DEBUG] Executing sp_update_customer_status procedure. with input param "+inputParams);
			
		    int updateFlag=(int)outParameters.get("update_status");
			String updateStatus=(String)outParameters.get("error_msg");
			
			if(updateFlag>0){
				
				response.setCode(ErrorCodes.INFO_CUSTOMER_STATUS_UPDATE);
				response.setData(updateStatus);
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				
			}else{
				response.setCode(ErrorCodes.ERROR_CUSTOMER_STATUS_FATAL);
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setData(updateStatus);
				
			}
			logger.info("procedure update status "+updateFlag+"error "+updateStatus);
		    
		}catch(Exception e){
			logger.error("",e);
			response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_CUSTOMER_FATAL, logger, e);
		}
		
		logger.info("[END] [updateCustomerStatus] [Customers DAO Impl]");
		return response;
	}


	/**
	 * getCustomerProfile: dao is a persistance service impl layer, used to get the customers updateCustomerStatus.
	 * @param customerBean object of CustomerRequestBean. accepts customerId as setter, getter in customerBean
	 * @return Response
	 * @throws VEMAppException
	 */
	
	@Override
	public Response getCustomerProfile(CustomersRequestBean customerBean)
			throws VEMAppException {
		logger.info("[BEGIN] [getCustomerProfile] [Customers DAO Impl]");
		Response response=new Response();
		try{
			
			logger.debug("calling procedure "+FETCH_CUSTOMER_DETIALS+"with params "+customerBean.getCustomerId()+"userID"+customerBean.getUserId()+"superAdmin"+customerBean.getIsSuperAdmin());
			//Executing the procedure.
			response= jdbcTemplate.query(FETCH_CUSTOMER_DETIALS, new Object[]{customerBean.getCustomerId(),customerBean.getUserId(),customerBean.getIsSuperAdmin()}, new ResultSetExtractor<Response>() {
				@Override
				public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
					Response response=new Response();
					JSONArray resultAry=new JSONArray();
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					if(rs.next()){
						JSONObject tempData=new JSONObject();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
						}
						
					   resultAry.add(tempData);
					}

					if(resultAry.isEmpty() || resultAry.size()<=0){
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
					}else{
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.INFO_CUSTOMER_DETAILS_FETCH);
					}
					response.setData(resultAry);
					return response;
				}
				
			});

			
		}
		catch(Exception e){
			logger.error("",e);
			response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			throw new VEMAppException(e.getMessage());
		}
		
		logger.info("[END] [getCustomerProfile] [Customers DAO Impl]");
		return response;
	}
	
	/**
	 * saveCustomerDetails: dao is a persistance service impl layer, used to save the customers.
	 * @param customer object of createCustomerRequest fields as setter, getter
	 * @author (Madhu Bantu – CTE).
	 * @return Response
	 * @throws VEMAppException
	 */
	
	@Override
	public String saveCustomerDetails(Customer customer, int userId) throws VEMAppException {

		logger.debug("[DaoImpl log Response] [saveCustomer] [DaoImpl Layer]");
			
		// Initializing the flag variable
		int flag = 0;
		
		// Initialization for key
				String key = "";
				
		// Declaring SimpleJdbcCall 
		SimpleJdbcCall simpleJdbcCall;
		
		try {

			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_insert_customer");
			logger.debug("[DaoImpl Response] [saveCustomer] [DaoImpl Layer]" +simpleJdbcCall);
			
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("customerCode",customer.getCustomerCode());
			parameters.put("companyName",customer.getCompanyName());
			parameters.put("addressLine1",customer.getAddressLine1());
			parameters.put("addressLine2",customer.getAddressLine2());
			parameters.put("city",customer.getCity());
			parameters.put("state",customer.getState());
			parameters.put("postalCode",customer.getPostalCode());
			parameters.put("customerStatus",customer.getCustomerStatus());
			parameters.put("degreePrefereces", customer.getDegreePrefereces());
			parameters.put("fan", customer.getThermostatePreferenceFanOn());
			parameters.put("fanAuto", customer.getThermostatePreferenceFanAuto());
			parameters.put("hvacAuto", customer.getThermostatePreferenceHvacAuto());
			parameters.put("resetHold", customer.getThermostatePreferenceResetHold());
			parameters.put("nightly_download",customer.getNightlyScheduleDownload());
			parameters.put("thermostate_min",customer.getThermostateMinSetPoint());
			parameters.put("thermostate_max",customer.getThermostateMaxSetPoint());
			parameters.put("lock_preference", customer.getLockPref());                         
			parameters.put("user_id",userId);
			parameters.put("createdDate",customer.getCreateDate());
			parameters.put("companyLogo",customer.getCompanyLogo());
			
			logger.debug("[DAOIMPLResponse] [saveCustomer] [DAOIMPL Layer]" +parameters);
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			logger.debug("simpleJdbcCallResult" + simpleJdbcCallResult);
			
			// Fetching the created db flag
			key = simpleJdbcCallResult.get("dbFlag").toString();
			
			// Fetching the created customer id
			flag = Integer.parseInt(simpleJdbcCallResult.get(CommonConstants.CUSTOMER_L).toString());
	
			// Fetching the error log if any
			String errorFlag = simpleJdbcCallResult.get(CommonConstants.CUSTOMER_ERROR_MSG_L).toString();
			
			logger.debug("flag" + flag);
			
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult + errorMessage);
			}else if(StringUtils.isNullOrEmpty(errorFlag)){
				logger.debug(CommonConstants.simpleJdbcCallResult + errorFlag);
			}
	
			/*
			 * if the flag is grater than 0 means 
			 * Successfully created the customer
			 */
			if (flag > 0){
				logger.debug(CommonConstants.spOutputUserId+flag);
				logger.debug("Saved customer Details sucesfully");
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ERROR_SAVE_FAILED, logger, e);
			}
			
			logger.info("[END] [saveCustomerDetails] [DAO LAYER]");
			
			return key;
	}
	/**
	 * loadStates: dao is a persistance service impl layer, load the states.
	 * @param 
	 * @author (Madhu Bantu – CTE).
	 * @return JSONObject	
	 */

	@Override
	public JSONObject loadStates() throws VEMAppException {
         logger.info("[BEGIN] [loadStates] [Customers DAO LAYER]");
		
		/*
		 * Used to hold all the all the dropdown values.
		 */
		JSONObject loadObject = new JSONObject();
	
	    loadObject.put("states", lookUpDao.getStates());
	    
	    logger.debug("[DEBUG] loadObject---OBJECT-- "+loadObject);
			    
		logger.info("[END] [loadStates] [Customers DAO LAYER]");
		
		return loadObject;
}

	/**
	 * getUserCustomersList: dao is a persistance service impl layer, used to get the customers list based on user id. This method interacts with data base
	 * @param customerBean object of CustomerRequestBean. accepts userId as setter, getter in customerBean
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public Response getUserCustomersList(CustomersRequestBean customerBean)
			throws VEMAppException {
		
		// Creating response instance
		Response response=new Response();
		
		logger.info("[BEGIN][getUserCustomersList][CustomersDaoImpl]");
		
		try{
			
			logger.debug("[DEBUG] Executing "+FETCH_USER_CUSTOMER_DETAILS_PROC+". with input param "+customerBean.getUserId());
			
			//Executing the procedure.
			response= jdbcTemplate.query(FETCH_USER_CUSTOMER_DETAILS_PROC, new Object[]{customerBean.getUserId()}, new ResultSetExtractor<Response>() {
			
				@Override
				public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
					Response response=new Response();
					JSONArray resultAry=new JSONArray();
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					while(rs.next()){
						JSONObject tempData=new JSONObject();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
						}
						
					   if(tempData.get("isActive")!=null ){
						tempData.put("isActive", Integer.parseInt(tempData.get("isActive").toString()));   
					   }	
					   resultAry.add(tempData);
					}
					
					if(resultAry.isEmpty() || resultAry.size()<=0){
						response.setStatus("No companies associated with this user");
						response.setCode(ErrorCodes.ERROR_CUSTOMER_FETCH);
					}else{
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
						response.setCode(ErrorCodes.INFO_CUSTOMER_DETAILS_FETCH);
					}
					response.setData(resultAry);
					return response;
				}
				
			});

			
		}catch(Exception e){
			logger.error("",e);
			throw new VEMAppException("Persistance error");
	    }
		logger.info("[END][getUserCustomersList][CustomersDaoImpl]");
	   return response;
	}
	
	/**
	 * generateCustomerCode: dao is a persistance service impl layer, used to generate the customercodes.
	 * @param  companyName
	 * @author (Madhu Bantu – CTE).
	 * @return Response
	 * @throws VEMAppException
	 */
	 public String generateCustomerCode(String companyName) throws VEMAppException {
		  logger.debug("[DaoImpl log Response] [generateCustomerCode] [DaoImpl Layer]");
		  // Initializing the flag variable
		int flag = 0;
		// Declaring SimpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		// UserList instance initialization
		List<String> customerList = new ArrayList<String>();
		StringBuffer customerCode = new StringBuffer("");
		StringBuffer custNm = new StringBuffer("");

		try {
			String[] customerCodeArray = companyName.trim().split(" ");
			if (customerCodeArray.length == 1) {
				// custNm.append(companyName.substring(0, 2));

				for (int i = 2; i <= companyName.length(); i++) {
					if (customerList.size() > 0 || ( customerList.size() == 0 && i==2)) {
						custNm = new StringBuffer("");
						custNm = custNm.append(companyName.substring(0, i));
						customerList = jdbcTemplate.query(
								"call sp_generate_customercode ('" + custNm.toString().toUpperCase()
										+ "')", new RowMapper<String>() {
									public String mapRow(ResultSet rs,
											int rowNum) throws SQLException {
										return rs.getString("customer_code");
									}
								});
					} else {
						break;
					}
				}

			} else {
				for (String cmpObj : customerCodeArray) {
					System.out.println(" " + cmpObj);

					if (!cmpObj.trim().equals("") && cmpObj.trim().length() > 0) {
						custNm.append(cmpObj.substring(0, 1));
					}
				}

				customerList = jdbcTemplate.query(
						"call sp_generate_customercode ('" + custNm.toString().toUpperCase() + "')",
						new RowMapper<String>() {
							public String mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return rs.getString("customer_code");
							}

						});
				System.out.println("customerlist" +customerList);

				String str1 = customerCodeArray[customerCodeArray.length - 1];
				if (customerList.size() > 0 && str1.length()>1) {
					for (int i = 1; i < str1.length(); i++) {
						//custNm = new StringBuffer("");
						if (customerList.size() > 0) {
							custNm = custNm.append(str1.substring(i, i+1));
	
							customerList = jdbcTemplate.query(
								"call sp_generate_customercode ('" + custNm.toString().toUpperCase()
										+ "')", new RowMapper<String>() {
									public String mapRow(ResultSet rs,
											int rowNum) throws SQLException {
										return rs.getString("customer_code");
									}

								});
						}else{
							break;
						}
					}

				}
				System.out.println("customerlist" +customerList+" custNm ::: "+custNm);

			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR,
					ErrorCodes.CUSTOMER_ERROR_SAVE_FAILED, logger, e);
		}

		logger.info("[END] [generateCustomerCode] [DAO LAYER]");
		return custNm.toString();

	}
	/**
	 * updateCustomerDetails: dao is a persistance service impl layer, used to update the CustomerDetails.
	 * @param  updateCustomerDetails
	 * @author (Madhu Bantu – CTE).
	 * @return Response
	 * @throws VEMAppException
	 */
	@Override
	public String updateCustomerDetails(Customer customer,int userId) throws VEMAppException {

		logger.debug("[DaoImpl log Response] [updateCustomerDetails] [DaoImpl Layer]");
		
		// Initializing the flag variable
		int flag = 0;
		
		// Initialization for key
		String key = "";
		
		// Declaring SimpleJdbcCall 
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			          /*
			           * Initialize the simpleJdbcCall to call the stored procedure
			           * and Adding the stored procedure name to simpleJdbcCall object.  
			          */
			           simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_update_customer");
			           // Passing parameters to the stored procedure
						Map<String, Object> parameters = new HashMap<>();
						parameters.put("in_customerId",customer.getCustomerId());
						parameters.put("in_customer_Code",customer.getCustomerCode());
						parameters.put("companyName",customer.getCompanyName());
						parameters.put("addressLine1",customer.getAddressLine1());
						parameters.put("addressLine2",customer.getAddressLine2());
						parameters.put("city",customer.getCity());
						parameters.put("state",customer.getState());
						parameters.put("postalCode",customer.getPostalCode());
						parameters.put("customerStatus",customer.getCustomerStatus());
						parameters.put("degreePrefereces", customer.getDegreePrefereces());
						parameters.put("fan", customer.getThermostatePreferenceFanOn());
						parameters.put("fanAuto", customer.getThermostatePreferenceFanAuto());
						parameters.put("hvacAuto", customer.getThermostatePreferenceHvacAuto());
						parameters.put("resetHold", customer.getThermostatePreferenceResetHold());
						parameters.put("nightly_download",customer.getNightlyScheduleDownload());
						parameters.put("thermostate_min",customer.getThermostateMinSetPoint());
						parameters.put("thermostate_max",customer.getThermostateMaxSetPoint());
						parameters.put("lock_preference", customer.getLockPref());
						parameters.put("user_id",userId);
						parameters.put("createdDate",customer.getCreateDate());
						parameters.put("companyLogo",customer.getCompanyLogo());
						parameters.put("active",customer.getIsActive());

						logger.debug("[DAOIMPLResponse] [updateCustomerDetails] [DAOIMPL Layer]" +parameters);
						// Giving parameters to SQL Parameter source
						SqlParameterSource in = new MapSqlParameterSource(parameters);
						
						// Executing stored procedure
						Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
						
						logger.debug("simpleJdbcCallResult" + simpleJdbcCallResult);
						
						// Fetching the created db flag
						key = simpleJdbcCallResult.get("dbFlag").toString();
						
						// Fetching the created customer id
						flag = Integer.parseInt(simpleJdbcCallResult.get(CommonConstants.CUSTOMER_L).toString());
				
						// Fetching the error log if any
						String errorFlag = simpleJdbcCallResult.get(CommonConstants.CUSTOMER_ERROR_MSG_L).toString();
						
						logger.debug("flag" + flag);
						
						/*
						 * Checking the flag is valid or not
						 * if the flag is less than 1 means error occurred 
						 * while executing the stored procedure
						 */
						if(flag < 1){
							String errorMessage = simpleJdbcCallResult.toString();
							logger.debug(CommonConstants.simpleJdbcCallResult + errorMessage);
						}else if(StringUtils.isNullOrEmpty(errorFlag)){
							logger.debug(CommonConstants.simpleJdbcCallResult + errorFlag);
						}
				
						/*
						 * if the flag is grater than 0 means 
						 * Successfully created the customer
						 */
						if (flag > 0){
							logger.debug(CommonConstants.spOutputUserId+flag);
							logger.debug("Updated customer Details sucesfully");
						}
						
						} catch (Exception e) {
							//Creating and throwing the customized exception.
							throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ERROR_UPDATE_FAILED, logger, e);
						}
						
						logger.info("[END] [updateCustomerDetails] [DAO LAYER]");
						
						return key;
				}
	
	/**
	 * deleteCustomerDetails: dao is a persistance service impl layer, used to delete the CustomerDetails.
	 * @param  customerId
	 * @author (Madhu Bantu – CTE).
	 * @return int
	 * @throws VEMAppException
	 */

	@Override
	public int deleteCustomerDetails(int customerId,int userId) throws VEMAppException {
        logger.info("[BEGIN] [deleteCustomerDetails] [DAO LAYER]");
		
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_delete_customer]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug(CommonConstants.CUSTOMER_L+" : "+customerId);
		}
		
		// Initialization of flag variable
		int flag = 0;
		
		// Declaring SimpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		
		try {
	
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_delete_customer");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("customerId",customerId);
			parameters.put("user_id",userId);
			parameters.put("status",3); // 3 means logical deletion
			parameters.put("updatedBy", 1);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created user id
			flag = Integer.parseInt(simpleJdbcCallResult.get("deletedCustomerId").toString());
	
			/*
			 * Checking the flag is valid or not
			 * if the flag is less than 1 means error occurred 
			 * while executing the stored procedure
			 */
			if(flag < 1){
				String errorMessage = simpleJdbcCallResult.toString();
				logger.debug(CommonConstants.simpleJdbcCallResult+errorMessage);
			}
	
			/*
			 * If the flag is grater than 0 means 
			 * Successfully activates / in-activates / Deletes the user
			 */
			if (flag > 0){
				logger.debug("SP OUTPUT CustomerId : "+flag);
				logger.debug("Customer deleted sucesfully");
				
				// deleting the devices
				deleteCustomerDevices(customerId, userId);
				
			}
			
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ERROR_DELETE_FAILED, logger, e);
			}
			
			logger.info("[END] [deleteCustomerDetails] [DAO LAYER]");
			
			return flag;
	}

	/**
	 * createUsersAndSites: dao is a persistance service impl layer, used to update the CustomerDetails.
	 * @param  CreateUsersAndSiteRequest
	 * @author (Madhu Bantu – CTE).
	 * @return int
	 * @throws VEMAppException
	 */
	@Override
	public int createUsersAndSites(CreateUsersAndSiteRequest createUsersAndSiteRequest) throws VEMAppException {
   logger.debug("[DaoImpl log Response] [createUsersAndSites] [DaoImpl Layer]");
 
		// Initializing the flag variable
		int flag = 0;
		
		// Initialization for key
		String key = "";
		
		// Declaring SimpleJdbcCall 
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			          /*
			           * Initialize the simpleJdbcCall to call the stored procedure
			           * and Adding the stored procedure name to simpleJdbcCall object.  
			          */
			           simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_insert_customer_user_sites");
			           // Passing parameters to the stored procedure
						Map<String, Object> parameters = new HashMap<>();
						parameters.put("in_customerId",createUsersAndSiteRequest.getCustomerId());
						parameters.put("in_locationId",createUsersAndSiteRequest.getLocationId());
						parameters.put("in_userId",createUsersAndSiteRequest.getUserId());
						logger.info("[END] [createUsersAndSites] [DAO LAYER]");
						
						// Giving parameters to SQL Parameter source
						SqlParameterSource in = new MapSqlParameterSource(parameters);
						
						// Executing stored procedure
						Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
						
						// Fetching the created user id
						flag = Integer.parseInt(simpleJdbcCallResult.get("var_customer_id").toString());
						
						
						if(flag < 1){
							String errorMessage = simpleJdbcCallResult.toString();
							logger.debug(CommonConstants.simpleJdbcCallResult+errorMessage);
						}
				
						/*
						 * If the flag is grater than 0 means 
						 * Successfully activates / in-activates / Deletes the user
						 */
						if (flag > 0){
							logger.debug("SP OUTPUT CustomerId : "+flag);
							logger.debug("users created sucesfully");
						}
						
						} catch (Exception e) {
							//Creating and throwing the customized exception.
							throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ERROR_INSERT_FAILED, logger, e);
						}
						
						logger.info("[END] [createUsersAndSites] [DAO LAYER]");			
						return flag;
	}

	@Override
	public Response getCustomerList(CustomersRequestBean customerBean)
			throws VEMAppException {
		// Creating response instance
				Response response=new Response();
				
				logger.info("[BEGIN][getCustomerList][CustomersDaoImpl]");
				
				try{
					
					logger.debug("[DEBUG] Executing "+FETCH_USER_CUSTOMER_LIST_PROC+". with input param "+customerBean.getUserId());
					
					//Executing the procedure.
					response= jdbcTemplate.query(FETCH_USER_CUSTOMER_LIST_PROC, new Object[]{customerBean.getCustomers(),customerBean.getUserId(),customerBean.getIsSuperAdmin()}, new ResultSetExtractor<Response>() {
					
						@Override
						public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
							Response response=new Response();
							JSONArray resultAry=new JSONArray();
							ResultSetMetaData rsmd=rs.getMetaData();
							int columnCount=rsmd.getColumnCount();
							while(rs.next()){
								JSONObject tempData=new JSONObject();
								for(int i=1;i<=columnCount;i++){
									tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
								}
								
							   if(tempData.get("isActive")!=null ){
								tempData.put("isActive", Integer.parseInt(tempData.get("isActive").toString()));   
							   }	
							   resultAry.add(tempData);
							}
							
							if(resultAry.isEmpty() || resultAry.size()<=0){
								response.setStatus("No companies associated with this user");
								response.setCode(ErrorCodes.ERROR_CUSTOMER_FETCH);
							}else{
								response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
								response.setCode(ErrorCodes.INFO_CUSTOMER_DETAILS_FETCH);
							}
							response.setData(resultAry);
							return response;
						}
						
					});

					
				}catch(Exception e){
					logger.error("",e);
					throw new VEMAppException("Persistance error");
			    }
				logger.info("[END][getCustomerList][CustomersDaoImpl]");
			   return response;
	}
	
	
	private void deleteCustomerDevices(int customerId, int userId){
		
		logger.info("[BEGIN] [deleteCustomerDevices] [Customer DAO LAYER]");
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		
		try {
			
				simpleJdbcCall = new SimpleJdbcCall(dataSource);
			    simpleJdbcCall.withProcedureName(FETCH_CUSTOMER_DEVICES);
			    
			    Map<String,Object> inputParams=new HashMap<>();
			    inputParams.put("customerId", customerId);
				logger.debug("[DEBUG] Executing "+FETCH_CUSTOMER_DEVICES+" procedure with input params "+inputParams);
				
				outParameters=simpleJdbcCall.execute(inputParams);
				Iterator<Entry<String, Object>> itr = outParameters.entrySet().iterator();
				
				while (itr.hasNext()) {
			        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
			        String key = entry.getKey();
			        
			        if(key.equals(CommonConstants.RESULT_SET_1))
			        {
			        	Object value = entry.getValue();
			        		
			        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
			        	logger.info("devices associated with customer are  "+tempAry);
			        	
			        	for(int i=0;i<tempAry.length();i++){
			        	  
			        		org.json.JSONObject tempObj=tempAry.getJSONObject(i);
			        		logger.info("deleting the device with id  "+tempObj.getInt("device_id"));
			        		iotDao.deleteDevice(tempObj.getInt("device_id"), userId);
			        	}
			        	
			        	
			        }
				}
		
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("[END] [deleteCustomerDevices] [Customer DAO LAYER]");
		

	}
	
  }
