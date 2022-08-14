/**
 * 
 */
package com.enerallies.vem.dao.iot;


import java.sql.SQLException;
import java.util.List;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.iot.ConfigChange;
import com.enerallies.vem.beans.iot.DeviceConfigInsert;
import com.enerallies.vem.beans.iot.DeviceMoreDetails;
import com.enerallies.vem.beans.iot.DeviceStatusRequest;
import com.enerallies.vem.beans.iot.DisconnectDeviceRequest;
import com.enerallies.vem.beans.iot.GroupInfo;
import com.enerallies.vem.beans.iot.OccupyHours;
import com.enerallies.vem.beans.iot.ProcessSetAt;
import com.enerallies.vem.beans.iot.ScheduleData;
import com.enerallies.vem.beans.iot.SiteDevice;
import com.enerallies.vem.beans.iot.TSTATPreference;
import com.enerallies.vem.beans.iot.ThermostatUnit;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.iot.ThingUpdateRequest;
import com.enerallies.vem.beans.iot.UpdateHeatPumpFieldReq;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : IoTDao 
 * 
 * IoTDao: is used to handle all the thing related data access operations and it contains only definition
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
 * 02	31-08-2016		Rajashekharaiah Muniswamy		Added saveThingInfo Method
 */

public interface IoTDao {
	

	/**
	 * @param thing
	 * @return
	 * @throws VEMAppException
	 */
	public int saveThingInfo(Thing thing) throws VEMAppException;

	/**
	 * @return List<Thing>
	 */
	public List<Thing> getThingList() throws SQLException;
	
	
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public List<ThingResponse> getThingListWithSiteName() throws SQLException;
	
	/**
	 * @param macId
	 * @return
	 * @throws SQLException
	 */
	public ThingResponse getThingInfo(int type, int deviceId, String macId) throws SQLException;

	
	/**
	 * @param value
	 * @param sortBy
	 * @return
	 */
	public List<ThingResponse> getThingList(int value, int sortBy, GetUserResponse userInfo);

	/**
	 * @param deviceStatus
	 * @param userId
	 * @return
	 * @throws VEMAppException
	 */
	public int updateDeviceStatus(DeviceStatusRequest deviceStatus, Integer userId) throws VEMAppException;
	
	public int updateHeatPumpFields(UpdateHeatPumpFieldReq heatPump, Integer userId) throws VEMAppException;

	/**
	 * @param disconnectDevice
	 * @param userId
	 * @return
	 * @throws VEMAppException
	 */
	public int disconnectDevice(DisconnectDeviceRequest disconnectDevice, Integer userId) throws VEMAppException;

	/**
	 * @param thing
	 * @return
	 */
	public int updateDevice(ThingUpdateRequest thing) throws VEMAppException;

	/**
	 * @param customerId
	 * @return
	 * @throws VEMAppException
	 */
	public List<SiteDevice> listSite(int type, int value, int userId, int isSuper) throws VEMAppException;

	/**
	 * @param deviceId
	 * @param userId 
	 * @return
	 * @throws VEMAppException
	 */
	public int deleteDevice(int deviceId, int userId) throws VEMAppException;

	/**
	 * @param thing
	 */
	public int updateEaiDeviceIdDevice(Thing thing) throws VEMAppException;

	/**
	 * @param siteId
	 * @return
	 */
	public List<ThermostatUnit> listThermostatUnit(int siteId) throws VEMAppException;

	/**
	 * @param siteId
	 * @return
	 * @throws VEMAppException
	 */
	
	public TSTATPreference getTstatPref(int siteId) throws VEMAppException;
	
	/**
	 * @param deviceConfig
	 * @throws VEMAppException
	 */
	public void insertDeviceConfig(DeviceConfigInsert deviceConfig) throws VEMAppException;
	
	/**
	 * @param deviceId
	 * @param userId
	 * @return
	 * @throws VEMAppException
	 */
	public DeviceMoreDetails getDeviceMoreDetails(int deviceId, int userId, int isSuper) throws VEMAppException;
	
	/**
	 * @param deviceId
	 * @return
	 * @throws VEMAppException
	 */
	public List<DeviceConfigInsert> getConfigChanges(int deviceId) throws VEMAppException;
	
	/**
	 * @param siteId
	 * @return
	 * @throws VEMAppException
	 */
	public List<OccupyHours> getSiteOccupyHours(int siteId) throws VEMAppException;

	public ProcessSetAt processSetAt(int deviceId, String coolSet, String heatSet,int currentDay,String currentTime) throws VEMAppException;
	
	/**
	 * @param siteId
	 * @return
	 * @throws VEMAppException
	 */
	public List<GroupInfo> getGroupInfo(int siteId, int userId, int isSuper) throws VEMAppException;
	
	/**
	 * 
	 * @param customerId
	 * @return
	 */
	public List<ThingResponse> getThingListByCustomer(int customerId);
	
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	public List<ThingResponse> getThingListBySite(int siteId);
} 
