/**
 * 
 */
package com.enerallies.vem.service.iot;

import com.amazonaws.services.iot.model.ResourceAlreadyExistsException;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.iot.DeviceStatusRequest;
import com.enerallies.vem.beans.iot.DisconnectDeviceRequest;
import com.enerallies.vem.beans.iot.SetClockRequest;
import com.enerallies.vem.beans.iot.SetTStatDataRequest;
import com.enerallies.vem.beans.iot.SetTemperatureRequest;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.beans.iot.ThingUpdateRequest;
import com.enerallies.vem.beans.iot.Things;
import com.enerallies.vem.beans.iot.UpdateHeatPumpFieldReq;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : ThingService 
 * 
 * ThingService: is used to handle all the thing related operations and it contains only definition
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        31-08-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	31-08-2016		Rajashekharaiah Muniswamy		File Created
 * 02	31-08-2016		Rajashekharaiah Muniswamy		Added registerThing Method
 * 03	
 */
public interface ThingService {


	/**
	 * @param thingRequest
	 * @return
	 * @throws Exception
	 */
	public Response registerThing(Thing thing) throws ResourceAlreadyExistsException;


	/**
	 * @param value
	 * @param sortBy
	 * @param isSuper
	 * @return
	 * @throws VEMAppException
	 */
	public Things getThingList(int value, String sortBy, GetUserResponse userInfo) throws VEMAppException;


	/**
	 * @param thing
	 * @return
	 */
	public Response updateDevice(ThingUpdateRequest thing) throws VEMAppException;

	/**
	 * @param customerId
	 * @return
	 */
	public Response listSite(String sortBy, int value, int userId, int isSuper) throws VEMAppException;

	/**
	 * @param deviceId
	 * @return
	 * @throws VEMAppException
	 */
	public Response getDevice(int deviceId,GetUserResponse userInfo) throws VEMAppException;

	/**
	 * @param deviceId
	 * @param integer 
	 * @return
	 * @throws VEMAppException
	 */
	public Response deleteDevice(int deviceId, int integer) throws VEMAppException;

	/**
	 * @param siteId
	 * @return
	 */
	public Response listThermostatUnit(int siteId,GetUserResponse userInfo) throws VEMAppException;


	/**
	 * @param siteId
	 * @return
	 * @throws VEMAppException
	 */
	public Response getTstatPref(int siteId) throws VEMAppException;

	/**
	 * @param xcspecDevId
	 * @param setTemperatureRequest
	 * @param userId 
	 * @return
	 */
	public Response setTemp(int devieId, SetTemperatureRequest setTemperatureRequest, int userId) throws VEMAppException;

	/**
	 * @param xcspecDevId
	 * @param setHoldRequest
	 * @param userId 
	 * @return
	 * @throws VEMAppException
	 */
	public Response setTStatData(int deviceId, SetTStatDataRequest setHoldRequest, int userId) throws VEMAppException;

	/**
	 * @param xcspecDevId
	 * @param setClockRequest
	 * @param userId 
	 * @return
	 * @throws VEMAppException
	 */
	public Response setClock(int deviceId, SetClockRequest setClockRequest, int userId) throws VEMAppException;

	/**
	 * @param xcspecId
	 * @param userId
	 * @param action
	 * @param descr
	 */
	public void insertActivityLog(String xcspecId, int userId, String action, String descr);
	
	/**
	 * @param xcspecId
	 * @param userId
	 * @param action
	 * @param descr
	 * @param specificId
	 */
	public void insertIntoActivityLog(String xcspecId, int userId, String action, String descr,String serviceId);

	/**
	 * @param siteId
	 * @return
	 * @throws VEMAppException
	 */
	public Response listDevForecast(int siteId, GetUserResponse userDetails) throws VEMAppException;


	Response disconnectDevice(DisconnectDeviceRequest disconnectDevice, Integer userId) throws VEMAppException;


	Response updateDeviceStatus(DeviceStatusRequest deviceStatus, Integer userId) throws VEMAppException;
	
	Response updateHeatPumpFields(UpdateHeatPumpFieldReq heatPump, Integer userId) throws VEMAppException;


	Things getThingList() throws RuntimeException;
}
