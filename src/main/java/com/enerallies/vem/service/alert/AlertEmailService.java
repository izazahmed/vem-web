package com.enerallies.vem.service.alert;

import java.net.MalformedURLException;

import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : AlertEmailService 
 * 
 * AlertEmailService service is used to serve the all alert mail related operations.
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

@Component
public interface AlertEmailService {
	/**
	 * mailGenerator service is used to send the alert mail file.
	 * @return Response
	 * @throws VEMAppException
	 */
	public Response mailGenerator(boolean b) throws VEMAppException, MalformedURLException;
}
