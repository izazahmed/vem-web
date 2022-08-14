package com.enerallies.vem.service.alert;

import org.springframework.stereotype.Service;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : AlertService 
 * 
 * AlertService service is used to serve the all group related operations.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        16-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 16-09-2016		Raja		    File Created.
 *
 */

@Service
public interface AlertService {


	public Response alertList(AlertRequest alertRequest ) throws VEMAppException;
	
	public Response getConfig(AlertRequest alertRequest) throws VEMAppException;
	
	public Response saveConfig(AlertRequest alertRequest) throws VEMAppException;
	
	public Response getCustomerAlerts(AlertRequest alertRequest) throws VEMAppException;
	
	public Response getDashboardAlerts(AlertRequest alertRequest) throws VEMAppException;
	
	//public Response storeDeviceStatus(AlertRequest alertRequest) throws VEMAppException;
	
	public Response getNewAlertCount(AlertRequest alertRequest) throws VEMAppException;
	
	public Response updateAlertStatus(AlertRequest alertRequest) throws VEMAppException;
	

	/**
	 * getAlertsByActionItems: It fetches the alerts by action items based on alert type 
	 * 
	 * @param alertRequest
	 * @return
	 * @throws VEMAppException
	 */
	public Response getAlertsByActionItems(AlertRequest alertRequest) throws VEMAppException;
	
	/**
	 * updateActionItems: It will updates action items
	 * 
	 * @param updateActionItem
	 * @return
	 * @throws VEMAppException
	 */
	public Response updateActionItems(AlertRequest alertRequest) throws VEMAppException;
	
	
	/**
	 * getDeviceAlertsService: It will updates action items
	 * 
	 * @param updateActionItem
	 * @return
	 * @throws VEMAppException
	 */
	public Response getDeviceAlertsService(AlertRequest alertRequest) throws VEMAppException;
	

	/**
	 * deleteCustomerConfigService: It will delete customer configuration
	 * @param updateActionItem
	 * @return
	 * @throws VEMAppException
	 */
	public Response deleteCustomerConfigService(AlertRequest alertRequest) throws VEMAppException;
	
}
