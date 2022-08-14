package com.enerallies.vem.dao.common;


import com.enerallies.vem.beans.common.ZipCode;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : CommonDao 
 * 
 * CommonDao: is used to handle common Requests used in all modules of VEM2.0 
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
public interface CommonDao {

	
	/**
	 * @param zipCode
	 * @param stateId
	 * @return
	 * @throws VEMAppException
	 */
	public ZipCode validateZipCodeOld(int zipCode, int stateId) throws VEMAppException;
	
	
	/**
	 * @param zipCode
	 * @param stateId
	 * @return
	 * @throws VEMAppException
	 */
	public int validateZipCode(int zipCode, int stateId, String city) throws VEMAppException;

}
