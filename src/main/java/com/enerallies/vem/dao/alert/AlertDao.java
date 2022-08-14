package com.enerallies.vem.dao.alert;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : AlertDAO 
 * AlertDAO dao is used to serve all the database level operations related to Alert.
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
@Repository
public interface AlertDao {

	 /**
	  * This is the method to be used to initialize database resources i.e. connection.
	  * 
	  * @param dataSource
	  */
	 public void setDataSource(DataSource dataSource);
	/**
	 * alertList dao layer,Interacts with Database to get List of Alerts.
	 * @param alertRequest object of GroupRequest bean, accepts customer id
	 * @return Response
	 * @throws VEMAppException
	 */
	 public Response alertList (AlertRequest alertRequest) throws VEMAppException;
	 public Response getConfig(AlertRequest alertRequest) throws VEMAppException;
	 public Response saveConfig(AlertRequest alertRequest) throws VEMAppException;
	 public Response getCustomerAlerts(AlertRequest alertRequest) throws VEMAppException;
	 public Response getDashboardAlerts(AlertRequest alertRequest) throws VEMAppException;
	 public Response storeDeviceStatus(AlertRequest alertRequest) throws VEMAppException;
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
	 * @param alertRequest
	 * @return
	 * @throws VEMAppException
	 */
	public Response updateActionItems(AlertRequest alertRequest) throws VEMAppException;
	
	
	/**
	 * getDeviceAlertsDao: It will updates action items
	 * @param alertRequest
	 * @return
	 * @throws VEMAppException
	 */
	public Response getDeviceAlertsDao(AlertRequest alertRequest) throws VEMAppException;
	
	/**
	 * deleteCustomerConfigDao: deletes customer configuration
	 * @param alertRequest
	 * @return
	 * @throws VEMAppException
	 */
	public Response deleteCustomerConfigDao(AlertRequest alertRequest) throws VEMAppException;
	
}
