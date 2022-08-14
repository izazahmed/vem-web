package com.enerallies.vem.dao.alert;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : AlertEmailDao 
 * AlertEmailDao dao is used to serve all the database level operations related to alert mail.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        27-03-2017
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 27-03-2017		Bhoomika Rabadiya   File Created.
 *
 */

@Repository
public interface AlertEmailDao {
	 /**
	  * This is the method to be used to initialize database resources i.e. connection.
	  * 
	  * @param dataSource
	  */
	 public void setDataSource(DataSource dataSource);
	 
	 public Response alertList (AlertRequest alertRequest) throws VEMAppException;
}
