package com.enerallies.vem.serviceimpl.alert;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.dao.alert.AlertDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.alert.AlertService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

@Service("alertService")
@Transactional
public class AlertServiceImpl implements AlertService {

	private static final Logger logger = Logger.getLogger(AlertServiceImpl.class);
	@Autowired AlertDao alertDao;
	
	
	/**
	 * listGroup service impl layer,Interacts with DAO layer to get List of Groups.
	 * @param groupRequest object of GroupRequest bean, accepts customer id
	 * @return Response
	 * @throws VEMAppException
	 */
	
	@Override
	public Response alertList(AlertRequest alertRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [alertList] [Alert SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = alertDao.alertList(alertRequest);
		}catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
			throw new VEMAppException("Internal occured at service layer");
		}
		
		logger.info("[BEGIN] [alertList] [Alert SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response getConfig(AlertRequest alertRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [getConfig] [Alert SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = alertDao.getConfig(alertRequest);
		}catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_GETCONFIG);
			throw new VEMAppException("Internal occured at service layer");
		}
		
		logger.info("[BEGIN] [getConfig] [Alert SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response saveConfig(AlertRequest alertRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [saveConfig] [Alert SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = alertDao.saveConfig(alertRequest);
		}catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_CONFIG_SAVE);
			throw new VEMAppException("Internal occured at service layer");
		}
		
		logger.info("[BEGIN] [saveConfig] [Alert SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getDashboardAlerts(AlertRequest alertRequest) throws VEMAppException {
		logger.info("[BEGIN] [getDashboardAlerts] [Alert SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = alertDao.getDashboardAlerts(alertRequest);
		}catch (Exception e) {
			logger.error("", e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_FETCH);
			throw new VEMAppException("Internal occured at service layer");
		}
		
		logger.info("[BEGIN] [getDashboardAlerts] [Alert SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response getCustomerAlerts(AlertRequest alertRequest) throws VEMAppException {
		logger.info("[BEGIN] [getCustomer] [Alert SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = alertDao.getCustomerAlerts(alertRequest);
		}catch (Exception e) {
			logger.error("", e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_FETCH);
			throw new VEMAppException("Internal occured at service layer");
		}
		
		logger.info("[BEGIN] [getCustomer] [Alert SERVICE LAYER]");
		
		return response;
	}

	/*@Override
	public Response storeDeviceStatus(AlertRequest alertRequest)
			throws VEMAppException {
		logger.info("[BEGIN] [storeDeviceStatus] [Alert SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = alertDao.storeDeviceStatus(alertRequest);
		}catch (Exception e) {
			logger.error("", e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_STORE_ALERTS);
			throw new VEMAppException("Internal occured at service layer");
		}
		
		logger.info("[BEGIN] [storeDeviceStatus] [Alert SERVICE LAYER]");
		
		return response;
	}
	*/
	@Override
	public Response getNewAlertCount(AlertRequest alertRequest)
			throws VEMAppException {
		logger.info("[BEGIN] [getNewAlertCount] [Alert SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = alertDao.getNewAlertCount(alertRequest);
		}catch (Exception e) {
			logger.error("", e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
			throw new VEMAppException("Internal occured at service layer");
		}
		
		logger.info("[BEGIN] [getNewAlertCount] [Alert SERVICE LAYER]");
		
		return response;
	}


	
	@Override
	public Response updateAlertStatus(AlertRequest alertRequest)
			throws VEMAppException {
		logger.info("[BEGIN] [updateAlertStatus] [Alert SERVICE LAYER]");
		Response response = new Response();
		
		try {
			response = alertDao.updateAlertStatus(alertRequest);
		}catch (Exception e) {
			logger.error("", e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_CONFIG_SAVE);
			throw new VEMAppException("Internal occured at service layer");
		}
		
		logger.info("[BEGIN] [updateAlertStatus] [Alert SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response getAlertsByActionItems(AlertRequest alertRequest) throws VEMAppException {
		
		logger.info("[BEGIN] [getAlertsByActionItems] [Alert SERVICE LAYER]");
		
		Response response = new Response();
		
		try {
			
			// Getting alert details from Database
			response = alertDao.getAlertsByActionItems(alertRequest);
			
		}catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_ACTIONLIST_FETCH);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ALERT_ACTION_ITEMS_ERROR_FAILED, logger, e);
		}
		
		logger.info("[END] [getAlertsByActionItems] [ALERT SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response updateActionItems(AlertRequest alertRequest) throws VEMAppException {
		logger.info("[BEGIN] [updateActionItems] [Alert SERVICE LAYER]");
		Response response = new Response();
		try {
			response=alertDao.updateActionItems(alertRequest);
		}catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_ACTIONLIST_UPDATE);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ALERT_ACTION_ITEMS_UPDATE_ERROR_FAILED, logger, e);
		}
		logger.info("[BEGIN] [updateActionItems] [Alert SERVICE LAYER]");
		
		return response;
	}

	
	@Override
	public Response getDeviceAlertsService(AlertRequest alertRequest) throws VEMAppException {
		logger.info("[BEGIN] [getDeviceAlertsService] [Alert SERVICE LAYER]");
		Response response = new Response();
		try {
			response=alertDao.getDeviceAlertsDao(alertRequest);
		}catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ALERT_ACTION_ITEMS_UPDATE_ERROR_FAILED, logger, e);
		}
		logger.info("[BEGIN] [getDeviceAlertsService] [Alert SERVICE LAYER]");
		
		return response;
	}
	
	@Override
	public Response deleteCustomerConfigService(AlertRequest alertRequest) throws VEMAppException {
		logger.info("[BEGIN] [deleteCustomerConfigService] [Alert SERVICE LAYER]");
		Response response = new Response();
		try {
			response=alertDao.deleteCustomerConfigDao(alertRequest);
		}catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ALERT_ACTION_ITEMS_UPDATE_ERROR_FAILED, logger, e);
		}
		logger.info("[BEGIN] [deleteCustomerConfigService] [Alert SERVICE LAYER]");
		
		return response;
	}
	
}
