package com.enerallies.vem.service.common;

import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : CommonService 
 * 
 * CommonService: is used to handle common Requests used in all modules of VEM2.0 
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
 */
public interface CommonService {
	
	/**
	 * @param zipCode
	 * @param stateId
	 * @return
	 * @throws VEMAppException
	 */
	public Response validateZipCode(int zipCode, int stateId, String city) throws VEMAppException;
	
	public Response getTimeZone(String zipCode) throws VEMAppException;
	
	
}
