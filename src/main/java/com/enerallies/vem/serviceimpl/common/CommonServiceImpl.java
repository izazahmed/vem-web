package com.enerallies.vem.serviceimpl.common;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ZipCode;
import com.enerallies.vem.dao.LookUpDao;
import com.enerallies.vem.dao.common.CommonDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.common.CommonService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : CommonServiceImpl 
 * 
 * CommonServiceImpl: is implementation of CommonService method 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	08-11-2016		Rajashekharaiah Muniswamy		File Created
 * */
@Service(value="commonService")
public class CommonServiceImpl implements CommonService{
	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(CommonServiceImpl.class);

	/**Autowiring CommonDao*/
	@Autowired
	private CommonDao commonDao;
	
	@Autowired
	private LookUpDao lookUpDao;
	
	@Override
	public Response validateZipCode(int zipCode, int stateId, String city) throws VEMAppException {
		
		/*Instantiate Response object*/
		Response response = new Response();
		
		try{
		
		int res  = commonDao.validateZipCode(zipCode, stateId, city);
		logger.info("Zip code object found :"+res);
		if (res!=0) {
			
			JSONObject resp = new JSONObject();
			resp.put("success", true);
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.SUCCESS_COMMON_ZIPCODE_VALIDATE);
			response.setData(resp.toString());
		}else{
			JSONObject resp = new JSONObject();
			resp.put("success", false);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_COMMON_ZIPCODE_NOTVALIDATE);
			response.setData(resp.toString());
		}
		}catch (Exception e) {
			JSONObject resp = new JSONObject();
			resp.put("success", false);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_COMMON_ZIPCODE_NOTVALIDATE);
			response.setData(resp.toString());
		}
		return response;
	}
	
	@Override
	public Response getTimeZone(String zipCode) throws VEMAppException {
		
		logger.info("[BEGIN] [getTimeZone] [CommonService SERVICE LAYER]");
		
		Response response = new Response();
		//Used to store list of sites.
		org.json.simple.JSONObject timeZone;
		
		try {
			
			//Calling the DAO layer getTimeZone() method.
			timeZone = lookUpDao.getTimeZone(zipCode);
			
			/* if timeZone is not null means the get timeZone request is
			 *  success
			 *  else fail.
			 */
			if(timeZone!=null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.SUCCESS_COMMON_TIMEZONE);
				response.setData(timeZone);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_COMMON_TIMEZONE);
				response.setData(CommonConstants.ERROR_OCCURRED+":No TimeZone Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_COMMON_TIMEZONE);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_COMMON_TIMEZONE, logger, e);
		}
		
		logger.info("[BEGIN] [getTimeZone] [CommonService SERVICE LAYER]");
		
		return response;
	}

}
