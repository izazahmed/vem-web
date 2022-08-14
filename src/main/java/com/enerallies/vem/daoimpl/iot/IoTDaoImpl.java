/**
 * 
 */
package com.enerallies.vem.daoimpl.iot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.admin.GetUserResponse;
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
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : IoTDaoImpl 
 * 
 * IoTDaoImpl: is implementation of IoTDao methods 
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
 * 03   08-09-2016		Rajashekharaiah Muniswamy		Addded getThingListWithSiteName
 * 04   19-09-2016		Rajashekharaiah Muniswamy 		Modified ThingResponseMapper with new fields
 * 05   19-09-2016		Rajashekharaiah Muniswamy 		Added getThingInfo method
 * 06	19-09-2016		Rajashekharaiah Muniswamy		Added updateDeviceStatus method
 * 07  	20-09-2016		Rajashekharaiah Muniswamy		Added disconnectDevice method
 * 08   26-09-2016		Rajashekharaiah Muniswamy       Added updateDevice method
 * 09   27-09-2016		Rajashekharaiah Muniswamy		Added  listThermostatUnit method
 * 10   28-09-2016      Rajashekharaiah Muniswamy		Added getTstatPref method
 * 11	30-09-2016		Rajashekharaiah Muniswamy		updated location name to location type (Integer)
 * 12   04-10-2016		Rajashekharaiah Muniswamy       Modified updateDevice method
 * 13   13-10-2016		Rajashekharaiah Muniswamy       Modified getThingInfo method added zip code
 */

public class IoTDaoImpl implements IoTDao{

	// Getting logger instance
	private static final Logger logger = Logger.getLogger(IoTDaoImpl.class);

	private static final String PROC_INSERT_DEVICE_CONFIG = "sp_insert_device_config";
	private static final String RESULT_SET_1 = "#result-set-1";
	private static final String RESULT_SET_2 = "#result-set-2";
	
	/** Data source instance */
	private DataSource dataSource;
	

	/** JDBC Template instance */
	private JdbcTemplate jdbcTemplate;
	
	/** Data source instance to read only*/
	private DataSource dataSourceRead;
	
	/** JDBC Template instance read only*/
	private JdbcTemplate jdbcTemplateRead;	
	
	public void setDataSourceRead(DataSource dataSource) {
		this.dataSourceRead = dataSource;
	    this.jdbcTemplateRead = new JdbcTemplate(this.dataSourceRead);
	}
	
	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate =  new JdbcTemplate(this.dataSource);
	}

	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int saveThingInfo(Thing thing) throws VEMAppException {
		
		logger.info("[BEGIN] [IoTDaoImpl] [saveThingInfo] Inside the IoTDaoImpl: saveThingInfo()  =====>");
		
		/** Java util date*/
		java.util.Date date = new Date();
		
		/** Get the sql timestamp*/
		Timestamp timestamp = new Timestamp(date.getTime());
		
		String sqlCreateThing = "sp_insert_device";
		
		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource);
		
		simpleJdbcCall.withProcedureName(sqlCreateThing);
		
		
		Map<String,Object> inputParams=new HashMap<>();
		inputParams.put("in_xcspec_device_id", thing.getXcspecDeviceId());
		inputParams.put("in_name", thing.getName());
		inputParams.put("in_model", thing.getModel());
		inputParams.put("in_mac_id", thing.getMacId());
		inputParams.put("in_version", thing.getVersion());
		inputParams.put("in_site_id", thing.getSiteId());
		inputParams.put("in_location", thing.getLocation());
		inputParams.put("in_reg_type", thing.getRegisterType());
		inputParams.put("in_thing_arn", thing.getThingARN());
		inputParams.put("in_created_by", thing.getCreatedBy());
		inputParams.put("in_created_on", timestamp);
		inputParams.put("in_is_active", thing.getIsActive());
		inputParams.put("in_customer_id", thing.getCustomerId());
		inputParams.put("in_device_type", thing.getDeviceType());
		inputParams.put("in_updated_by", thing.getUpdatedBy());
		inputParams.put("in_updated_on", timestamp);
		inputParams.put("in_eai_device_id", thing.getEaiDeviceId());
		inputParams.put("in_device_tag", thing.getDeviceTag());
		inputParams.put("in_sf22_hw_version", thing.getSf22HwVersion());
		inputParams.put("in_wifi_key", thing.getWifiKey());
		inputParams.put("in_unit", thing.getUnit());
		inputParams.put("in_fan_preference", thing.getFanPref());
		inputParams.put("in_hvac_to_auto", thing.getHvacToAuto());
		inputParams.put("in_hold_to_auto", thing.getHoldToAuto());
		inputParams.put("in_min_sp", thing.getMinSP());
		inputParams.put("in_max_sp", thing.getMaxSP());
		inputParams.put("in_lock_preference", thing.getLockPref());
		inputParams.put("in_night_schedule", thing.getNightSchedule());
		inputParams.put("in_aws_compatible", thing.getAwsCompatible());
		inputParams.put("in_other_location", thing.getOtherLocation());
		inputParams.put("in_heat_pump", thing.getHeatPump());
		inputParams.put("in_stages_of_heat", thing.getStagesOfHeat());
		inputParams.put("in_stages_of_cool", thing.getStagesOfCool());
		inputParams.put("in_gas_auxilary", thing.getGasAuxilary());
		
		int rowInserted;
		String errorMsg;
		try {
			Map<String, Object> outputParams= simpleJdbcCall.execute(inputParams);
			
			
	        errorMsg = (String) outputParams.get("out_error_msg");
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	rowInserted = (int)outputParams.get("out_flag");
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_SQL_ADD_DEVICE_FAILED, logger, new Exception(errorMsg));
	        }
			
		} catch (Exception e) {
			logger.error("[ERROR] [IoTDaoImpl] [saveThingInfo] Exception occured while storing "+e.getMessage());
			rowInserted = 0;
		}
		
	    logger.info("[END] [IoTDaoImpl] [saveThingInfo] Completed thing info store in to DB: saveThingInfo()  =====>");
		return rowInserted;
	}

	private static final class ThingInfoMapper implements RowMapper<Thing>{

		@Override
		public Thing mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			Thing thing = new Thing();
			
			thing.setDeviceId(resultSet.getInt("device_id"));
			thing.setXcspecDeviceId(resultSet.getString("xcspec_device_id"));
			thing.setName(resultSet.getString("name"));
			thing.setModel(resultSet.getString("model"));
			thing.setMacId(resultSet.getString("mac_id"));
			thing.setVersion(resultSet.getString("version"));
			thing.setSiteId(resultSet.getInt("site_id"));
			thing.setLocation(resultSet.getInt("location"));
			thing.setRegisterType(resultSet.getInt("reg_type"));
			thing.setThingARN(resultSet.getString("thing_arn"));
			thing.setCreatedBy(resultSet.getInt("created_by"));
			thing.setCreatedOn(resultSet.getTimestamp("created_on").toString());
			thing.setIsActive(resultSet.getInt("is_active"));
			thing.setCustomerId(resultSet.getInt("customer_id"));
			thing.setDeviceType(resultSet.getInt("device_type"));
			thing.setAwsCompatible(resultSet.getInt("aws_compatible"));
			
			return thing;
		}
		
	}

	@Override
	public List<Thing> getThingList() throws SQLException {
		logger.info("[BEGIN] [IoTDaoImpl] [getThingList] Inside the IoTDaoImpl : getThingList() =====>");		
		String sql = "call sp_list_device ()";
		List<Thing> thingList;
		try {
			thingList = jdbcTemplateRead.query(sql,  new Object [] {},  new ThingInfoMapper());
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getThingList]Exception occured while fetching thing list "+e);
			thingList = null;
		}
		logger.info("[END] [IoTDaoImpl] [getThingList] Completed fetching thing list IoTDaoImpl : getThingList() =====>");
		return thingList;
	}

	
	private static final class ThingResponseInfoMapper implements RowMapper<ThingResponse>{

		@Override
		public ThingResponse mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			ThingResponse thing = new ThingResponse();
			
			thing.setDeviceId(resultSet.getInt("device_id"));
			thing.setXcspecDeviceId(resultSet.getString("xcspec_device_id"));
			thing.setName(resultSet.getString("name"));
			thing.setModel(resultSet.getString("model"));
			thing.setMacId(resultSet.getString("mac_id"));
			thing.setVersion(resultSet.getString("version"));
			thing.setSiteId(resultSet.getInt("site_id"));
			thing.setSiteName(resultSet.getString("site_name"));
			thing.setSiteCode(resultSet.getString("site_code"));
			thing.setSiteInternalId(resultSet.getString("site_internal_id"));
			thing.setLocation(resultSet.getInt("location"));
			thing.setRegisterType(resultSet.getInt("reg_type"));
			thing.setThingARN(resultSet.getString("thing_arn"));
			thing.setCreatedBy(resultSet.getInt("created_by"));
			thing.setCreatedByUserName(resultSet.getString("user_fname"));
			thing.setCreatedOn(resultSet.getString("created_on"));
			thing.setIsActive(resultSet.getInt("is_active"));
			thing.setCustomerId(resultSet.getInt("customer_id"));
			thing.setDeviceType(resultSet.getInt("device_type"));
			thing.setUpdatedBy(resultSet.getInt("updated_by"));
			thing.setUpdatedOn(resultSet.getString("updated_on"));
			thing.setEaiDeviceId(resultSet.getString("eai_device_id"));
			thing.setDeviceTag(resultSet.getString("device_tag"));
			thing.setSf22HwVersion(resultSet.getString("sf22_hw_version"));
			thing.setWifiKey(resultSet.getString("wifi_key"));
			thing.setUnit(resultSet.getString("unit"));
			thing.setFanPref(resultSet.getInt("fan_preference"));
			thing.setHvacToAuto(resultSet.getInt("hvac_to_auto"));
			thing.setHoldToAuto(resultSet.getInt("hold_to_auto"));
			thing.setMinSP(resultSet.getInt("min_sp"));
			thing.setMaxSP(resultSet.getInt("max_sp"));
			thing.setLockPref(resultSet.getInt("lock_preference"));
			thing.setNightSchedule(resultSet.getInt("night_schedule"));
			thing.setSiteZipcode(resultSet.getString("zip_code"));
			thing.setCustomerName(resultSet.getString("company_name"));
			thing.setAwsCompatible(resultSet.getInt("aws_compatible"));
			thing.setCustomerCode(resultSet.getString("customer_code"));
			thing.setSiteTimezone(resultSet.getString("site_frn_time_zone_id"));
			thing.setDeviceStatus(resultSet.getInt("device_status"));
			thing.setOtherLocation(resultSet.getString("other_location"));
			thing.setHeatPump(resultSet.getInt("heat_pump"));
			thing.setStagesOfHeat(resultSet.getInt("stages_of_heat"));
			thing.setStagesOfCool(resultSet.getInt("stages_of_cool"));
			thing.setGasAuxilary(resultSet.getInt("gas_auxilary"));
		
			return thing;
		}
		
	}

	@Override
	public List<ThingResponse> getThingListWithSiteName() throws SQLException {
		logger.info("[BEGIN] [IoTDaoImpl] [getThingListWithSiteName] Inside the IoTDaoImpl : getThingListWithSiteName() =====>");		
		String sql = "call sp_list_device_withsite ()";
		List<ThingResponse> thingList;
		try {
			thingList = jdbcTemplateRead.query(sql,  new Object [] {},  new ThingResponseInfoMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getThingListWithSiteName]Exception occured while fetching thing list "+e);
			thingList = null;
		}
		logger.info("[END] [IoTDaoImpl] [getThingListWithSiteName] Completed fetching thing list IoTDaoImpl : getThingListWithSiteName() =====>");
		return thingList;
	}

	@Override
	public ThingResponse getThingInfo(int in_type, int in_deviceId, String in_macId) throws SQLException {
		logger.info("[BEGIN] [IoTDaoImpl] [getThingInfoByMacId] Inside the IoTDaoImpl : getThingInfoByMacId() =====>");		
		String sql = "call sp_select_device ("+in_type+", "+in_deviceId+", '"+in_macId+"')";
		ThingResponse thingResponse;
		try {
			thingResponse = jdbcTemplateRead.queryForObject(sql,  new Object [] {},  new ThingResponseInfoMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getThingInfoByMacId]Exception occured while fetching thing by mac id "+e);
			thingResponse = null;
		}
		logger.info("[END] [IoTDaoImpl] [getThingInfoByMacId] Completed fetching thing info IoTDaoImpl : getThingInfoByMacId() =====>");
		return thingResponse;
	}

	@Override
	public List<ThingResponse> getThingList(int value, int sortBy, GetUserResponse userInfo) {
		logger.info("[BEGIN] [IoTDaoImpl] [getThingList] Inside the IoTDaoImpl : getThingList() =====>");		
		String sql = "call sp_list_device_by_customer ("+value+", "+sortBy+", "+userInfo.getIsSuper()+", "+userInfo.getUserId()+")";
		List<ThingResponse> thingList;
		try {
			thingList = jdbcTemplateRead.query(sql,  new Object [] {},  new ThingResponseInfoMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getThingList]Exception occured while fetching thing list "+e);
			thingList = null;
		}
		logger.info("[END] [IoTDaoImpl] [getThingListBy] Completed fetching thing list IoTDaoImpl : getThingList() =====>");
		return thingList;
	}

	@Override
	public int updateDeviceStatus(DeviceStatusRequest deviceStatus, Integer userId) throws VEMAppException{
		logger.info("[BEGIN] [updateDeviceStatus] [IoTDaoImpl DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
		
	    /*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_update_device_status");
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_device_id", deviceStatus.getDeviceId());
			inputParams.put("in_is_active", deviceStatus.getIsActive());
			inputParams.put("in_updated_by", userId);
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_update_status_device");
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the update device request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get("out_error_msg");
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get("out_flag");
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED, logger, e);
		}
		
		logger.info("[END] [updateDeviceStatus] [IoTDaoImpl DAO LAYER]");

		return statusFlag;
	}

	@Override
	public int disconnectDevice(DisconnectDeviceRequest disconnectDevice, Integer userId) throws VEMAppException {
		logger.info("[BEGIN] [disconnectDevice] [IoTDaoImpl DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
		
	    /*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_update_device_connectivity");
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_device_id", disconnectDevice.getDeviceId());
			inputParams.put("in_xcspec_device_id", null);
			inputParams.put("in_thing_arn", null);
			inputParams.put("in_reg_type", 0);
			inputParams.put("in_updated_by", userId);
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_update_device_connectivity");
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the update device request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get("out_error_msg");
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get("out_flag");
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_DISCONNECT, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_DISCONNECT, logger, e);
		}
		
		logger.info("[END] [disconnectDevice] [IoTDaoImpl DAO LAYER]");

		return statusFlag;
	}

	@Override
	public int updateDevice(ThingUpdateRequest thing) {
		logger.info("[BEGIN] [IoTDaoImpl] [updateDevice] Inside the IoTDaoImpl: updateDevice()  =====>");
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */	
		int rowInserted;
		
		String errorMsg;
		
		/*Loading stored procedure**/
		String sqlUpdateDevice = "sp_update_device";
		
		/*
		 * Initialize the simpleJdbcCall to call the stored procedure
		 * and Adding the stored procedure name to simpleJdbcCall object.  
		 */

		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(dataSource);
		
		simpleJdbcCall.withProcedureName(sqlUpdateDevice);
		
		/*
	     * Adding all the input parameter values to a hashmap.
	     */
		Map<String,Object> inputParams=new HashMap<>();
		inputParams.put("in_device_id", thing.getDeviceId());
		inputParams.put("in_xcspec_device_id", thing.getXcspecDeviceId());
		inputParams.put("in_name", thing.getName());
		inputParams.put("in_model", thing.getModel());
		inputParams.put("in_mac_id", thing.getMacId());
		inputParams.put("in_version", thing.getVersion());
		inputParams.put("in_site_id", thing.getSiteId());
		inputParams.put("in_location", thing.getLocation());
		inputParams.put("in_reg_type", thing.getRegisterType());
		inputParams.put("in_thing_arn", thing.getThingARN());
		inputParams.put("in_is_active", thing.getIsActive());
		inputParams.put("in_customer_id", thing.getCustomerId());
		inputParams.put("in_device_type", thing.getDeviceType());
		inputParams.put("in_updated_by", thing.getUpdatedBy());
		inputParams.put("in_eai_device_id",thing.getEaiDeviceId());
		inputParams.put("in_device_tag", thing.getDeviceTag());
		inputParams.put("in_sf22_hw_version", thing.getSf22HwVersion());
		inputParams.put("in_wifi_key", thing.getWifiKey());
		inputParams.put("in_unit", thing.getUnit());
		inputParams.put("in_fan_preference", thing.getFanPref());
		inputParams.put("in_hvac_to_auto", thing.getHvacToAuto());
		inputParams.put("in_hold_to_auto", thing.getHoldToAuto());
		inputParams.put("in_min_sp", thing.getMinSP());
		inputParams.put("in_max_sp", thing.getMaxSP());
		inputParams.put("in_lock_preference", thing.getLockPref());
		inputParams.put("in_night_schedule", thing.getNightSchedule());
		inputParams.put("in_aws_compatible", thing.getAwsCompatible());
		inputParams.put("in_other_location", thing.getOtherLocation());
	
		try {
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
			Map<String, Object> outputParams= simpleJdbcCall.execute(inputParams);
			
			
	        errorMsg = (String) outputParams.get("out_error_msg");
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	rowInserted = (int)outputParams.get("out_flag");
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_SQL_UPDATE_DEVICE, logger, new Exception(errorMsg));
	        }
			
		} catch (Exception e) {
			logger.error("[ERROR] [IoTDaoImpl] [updateDevice] Exception occured while updating "+e.getMessage());
			rowInserted = 0;
		}
		
	    logger.info("[END] [IoTDaoImpl] [updateDevice] Completed update device info in to DB: updateDevice()  =====>");
		return rowInserted;
	}

	@Override
	public List<SiteDevice> listSite(int type, int value, int userId, int isSuper) throws VEMAppException {
		logger.info("[BEGIN] [IoTDaoImpl] [listSite] Inside the IoTDaoImpl : listSite() =====>");		
		String sql = "call sp_list_sites_device ("+type+", "+value+", "+userId+", "+isSuper+")";
		List<SiteDevice> thingList;
		try {
			thingList = jdbcTemplateRead.query(sql,  new Object [] {},  new SitesInfoMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [listSite]Exception occured while fetching sites list "+e);
			thingList = null;
		}
		logger.info("[END] [IoTDaoImpl] [listSite] Completed fetching sites list IoTDaoImpl : listSite() =====>");
		return thingList;
	}
	
	private static final class SitesInfoMapper implements RowMapper<SiteDevice>{

		@Override
		public SiteDevice mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			SiteDevice site = new SiteDevice();
			
			site.setSiteId(resultSet.getInt("site_id"));
			site.setSiteName(resultSet.getString("site_name"));

			return site;
		}
		
	}

	@Override
	public int deleteDevice(int deviceId, int userId) throws VEMAppException {
		logger.info("[BEGIN] [deleteDevice] [DAO LAYER]");
		
		int deleteRoleFlag = 0;
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
			simpleJdbcCall.withProcedureName("sp_delete_device");
			
			//Adding all the input parameter values to a hashmap.
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_device_id", deviceId);
			inputParams.put("in_user_id", userId);
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_delete_device");
			logger.debug("[DEBUG] input parameters - "+inputParams);
			
			//Adding all the input parameter map to simpleJdbcCall.execute method.
	        Map<String,Object> outParameters = simpleJdbcCall.execute(inputParams);
	        
	        logger.debug("[DUBUG] sp_delete_device out parameters - "+outParameters);
	        
	        //Getting the device deleted count.
	        deleteRoleFlag = (int) outParameters.get("out_flag");
	        
	        //if deleteRoleFlag is 0 means there is an exception occurred at database level.
	        if(deleteRoleFlag==0){
	        	//In procedure there is an exception occured.
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_DELETE_DEVICE_FAILED, logger, new Exception((String) outParameters.get("out_error_msg")));
	        }
	        
		  } catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_DELETE_DEVICE_FAILED, logger, e);
		}
		
		logger.info("[END] [deleteDevice] [DAO LAYER]");
		
		return deleteRoleFlag;
	}

	@Override
	public int updateEaiDeviceIdDevice(Thing thing) throws VEMAppException {
		logger.info("[BEGIN] [updateEaiDeviceIdDevice] [IoTDaoImpl DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
		
	    /*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_update_eai_device_id");
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_device_id", thing.getDeviceId());
			inputParams.put("in_eai_device_id", thing.getEaiDeviceId());
			inputParams.put("in_updated_by", thing.getUpdatedBy());
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_update_eai_device_id");
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the update device request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get("out_error_msg");
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get("out_flag");
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED, logger, e);
		}
		
		logger.info("[END] [updateEaiDeviceIdDevice] [IoTDaoImpl DAO LAYER]");

		return statusFlag;
	}

	@Override
	public List<ThermostatUnit> listThermostatUnit(int siteId) throws VEMAppException {
		logger.info("[BEGIN] [IoTDaoImpl] [listThermostatUnit] Inside the IoTDaoImpl : listThermostatUnit() =====>");		
		String sql = "call sp_list_tstatunit_device ("+siteId+")";
		List<ThermostatUnit> tstatUnitList;
		try {
			tstatUnitList = jdbcTemplateRead.query(sql,  new Object [] {},  new TStatUnitInfoMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [listThermostatUnit]Exception occured while fetching tstat unit list "+e);
			tstatUnitList = null;
		}
		logger.info("[END] [IoTDaoImpl] [listThermostatUnit] Completed fetching tstat unit list IoTDaoImpl : listThermostatUnit() =====>");
		return tstatUnitList;
	}
	
	private static final class TStatUnitInfoMapper implements RowMapper<ThermostatUnit>{

		@Override
		public ThermostatUnit mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			ThermostatUnit tstatUnit = new ThermostatUnit();
			
			tstatUnit.setTstatUnit(resultSet.getString("st_unit"));
			tstatUnit.setLocationType(resultSet.getInt("st_location_type"));
			tstatUnit.setOtherLocation(resultSet.getString("st_other_location"));

			return tstatUnit;
		}
		
	}

	@Override
	public TSTATPreference getTstatPref(int siteId) throws VEMAppException {
		logger.info("[BEGIN] [IoTDaoImpl] [getTstatPref] Inside the IoTDaoImpl : getTstatPref() =====>");		
		String sql = "call sp_select_tstat_pref_device ("+siteId+")";
		TSTATPreference tstatPref;
		try {
			tstatPref = jdbcTemplateRead.queryForObject(sql,  new Object [] {},  new TSTATPreferenceInfoMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getTstatPref]Exception occured while fetching thermostat preference by site id "+e);
			tstatPref = null;
		}
		logger.info("[END] [IoTDaoImpl] [getTstatPref] Completed fetching thermostat preference IoTDaoImpl : getTstatPref() =====>");
		return tstatPref;
	}
	
	private static final class TSTATPreferenceInfoMapper implements RowMapper<TSTATPreference>{

		@Override
		public TSTATPreference mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			TSTATPreference tstatPref = new TSTATPreference();
			
			tstatPref.setFanPref(resultSet.getInt("stt_fan_status"));
			tstatPref.setHoldToAuto(resultSet.getInt("stt_reset_hold_mode"));
			tstatPref.setHvacToAuto(resultSet.getInt("stt_HVAC_to_auto"));
			tstatPref.setMaxSP(resultSet.getInt("stt_max_sp"));
			tstatPref.setMinSP(resultSet.getInt("stt_min_sp"));
			tstatPref.setNightSchedule(resultSet.getInt("stt_night_schedule"));
			tstatPref.setLock(resultSet.getInt("stt_lock_status"));

			return tstatPref;
		}
		
	}

	@Override
	public void insertDeviceConfig(DeviceConfigInsert deviceConfig) throws VEMAppException {
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		Map<String,Object> inputParams=null;
		logger.info("Controller reached IoTDaoImpl.insertDeviceConfig");
		try{
			
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(PROC_INSERT_DEVICE_CONFIG);
		    inputParams=new HashMap<>();
		    
		    inputParams.put("in_action", deviceConfig.getAction());
		    inputParams.put("in_value", deviceConfig.getValue());
		    inputParams.put("in_created_by", deviceConfig.getCreatedBy());
		    inputParams.put("in_updated_flag", deviceConfig.getUpdatedFlag());
		    inputParams.put("in_device_id", deviceConfig.getDeviceId());
		    
		    outParameters=simpleJdbcCall.execute(inputParams);
			logger.info("Affected rows"+outParameters);
		}catch(Exception e){
			logger.error("Error found While inserting device configuration",e);
		}
	}
	
	
	
	@Override
	public DeviceMoreDetails getDeviceMoreDetails(int deviceId, int userId, int isSuper) throws VEMAppException {
/*		logger.info("[BEGIN] [IoTDaoImpl] [getDeviceMoreDetails] Inside the IoTDaoImpl : getDeviceMoreDetails() =====>");		
		String sql = "call sp_select_device_more_details ("+deviceId+", "+userId+", "+isSuper+")";
		DeviceMoreDetails devMoreDetails;
		try {
			devMoreDetails = jdbcTemplate.queryForObject(sql,  new Object [] {},  new DeviceMoreDetailsMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getDeviceMoreDetails]Exception occured while fetching device more details "+e);
			devMoreDetails = null;
		}
		logger.info("[END] [IoTDaoImpl] [getDeviceMoreDetails] Completed fetching device more details IoTDaoImpl : getDeviceMoreDetails() =====>");
		return devMoreDetails;*/
		
		/*
		 * Declaring the simpleJdbcCall and  simpleJdbcCallResult
		 */
		SimpleJdbcCall simpleJdbcCall;
		Map<String, Object> simpleJdbcCallResult;
		Entry<String, Object> entry;
		List<HashMap<String, Object>> tempList;
		
		/*
		 * This iterator is used to loop the results. 
		 */
		Iterator<Entry<String, Object>> it;
		DeviceMoreDetails devMoreDetails = new DeviceMoreDetails();
		logger.debug("[DEBUG] Executing sp_select_device_more_details procedure.");
		/*
		 * Instantiating simpleJdbcCall, Appending the procedure,
		 * And executing the procedure.
		 */
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplateRead).withProcedureName("sp_select_device_more_details");
		
		/*
	     * Adding all the input parameter values to a hash map.
	     */
		Map<String,Object> inputParams=new HashMap<>();
		inputParams.put("in_device_id", deviceId);
		inputParams.put("in_user_id", userId);
		inputParams.put("in_is_super", isSuper);
		
		/*
		 * Executing the procedure.
		 */
		simpleJdbcCallResult = simpleJdbcCall.execute(inputParams);
		
		/*
		 * looping the result sets.
		 */
		it = simpleJdbcCallResult.entrySet().iterator();
		
		    while (it.hasNext()) {
		        /*
		         * Getting the each result set as entry 
		         * and Getting the key from entry. 
		         */
		    	entry = (Map.Entry<String, Object>) it.next();
		        String key = entry.getKey().toString();
		        
		        logger.debug("[DEBUG] Key - "+key);
		        
		        /*
		         * Forming the device more  data from the device and schedule table. 
		         */
		        if(key.equals(RESULT_SET_1)){
		        	
		        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
		        	
			        if(!tempList.isEmpty()) {
			        	devMoreDetails.setDevScheduleName((String)CommonUtility.isNullForString(tempList.get(0).get("schedule_name")));
			        	devMoreDetails.setDevScheduleStatus((Integer)CommonUtility.isNullForInteger(tempList.get(0).get("schedule_status")));
			        	devMoreDetails.setDevScheduleId((Integer)CommonUtility.isNullForInteger(tempList.get(0).get("schedule_id")));
			        }
		        }else if (key.equals(RESULT_SET_2)) {
		        		tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
		        	
			        if(!tempList.isEmpty()) {
		        	devMoreDetails.setDevActivityCount((Integer)CommonUtility.isNullForInteger(tempList.get(0).get("activity_count")));
		        	devMoreDetails.setDevAlertCount((Integer)CommonUtility.isNullForInteger(tempList.get(0).get("alert_count")));
		        	devMoreDetails.setDevCommFailConfigTime((Integer)CommonUtility.isNullForInteger(tempList.get(0).get("dev_comm_fail_config_time")));
			        }
				}

		    }
		    logger.info("Device more details found as follows : "+devMoreDetails.getDevScheduleName() +" "+devMoreDetails.getDevScheduleStatus()+" "+devMoreDetails.getDevActivityCount()+" "+devMoreDetails.getDevAlertCount());
		return devMoreDetails;

	}
	
/*	private static final class DeviceMoreDetailsMapper implements RowMapper<DeviceMoreDetails>{

		@Override
		public DeviceMoreDetails mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			DeviceMoreDetails devMoreDetails = new DeviceMoreDetails();
			
			devMoreDetails.setDevScheduleName(resultSet.getString("schedule_name"));
			devMoreDetails.setDevScheduleStatus(resultSet.getInt("status_id"));
			devMoreDetails.setDevActivityCount(resultSet.getInt("activity_count"));
			devMoreDetails.setDevAlertCount(resultSet.getInt("alert_count"));
			return devMoreDetails;
		}
		
	}*/

	@Override
	public List<DeviceConfigInsert> getConfigChanges(int deviceId) throws VEMAppException {
		logger.info("[BEGIN] [IoTDaoImpl] [getConfigChanges] =====>");		
		String sql = "call sp_list_device_config_change ("+deviceId+")";
		List<DeviceConfigInsert> devConfig;
		try {
			devConfig = jdbcTemplateRead.query(sql,  new Object [] {},  new DeviceConfigMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getConfigChanges] "+e);
			devConfig = null;
		}
		logger.info("[END] [IoTDaoImpl] [getConfigChanges]=====>");
		return devConfig;
	}
	
	private static final class DeviceConfigMapper implements RowMapper<DeviceConfigInsert>{

		@Override
		public DeviceConfigInsert mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			DeviceConfigInsert devConfig = new DeviceConfigInsert();
			
			devConfig.setDeviceConfigId(resultSet.getInt("dc_id"));
			devConfig.setAction(resultSet.getInt("dc_action"));
			devConfig.setValue(resultSet.getString("dc_value"));
			devConfig.setCreatedOn(resultSet.getTimestamp("dc_created_on").toString());
			devConfig.setCreatedBy(resultSet.getInt("dc_created_by"));
			devConfig.setUpdatedFlag(resultSet.getInt("dc_updated_flag"));
			devConfig.setDeviceId(resultSet.getInt("dc_device_id"));
			return devConfig;
		}
		
	}

	@Override
	public List<OccupyHours> getSiteOccupyHours(int siteId) throws VEMAppException {
		logger.info("[BEGIN] [IoTDaoImpl] [getSiteOccupyHours] Inside the IoTDaoImpl : getSiteOccupyHours() =====>");		
		String sql = "call sp_list_occupancyhours_device ("+siteId+")";
		List<OccupyHours> ohs;
		try {
			ohs = jdbcTemplateRead.query(sql,  new Object [] {},  new OccupyHoursMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getSiteOccupyHours]Exception occured while fetching occupyhours "+e);
			ohs = null;
		}
		logger.info("[END] [IoTDaoImpl] [getSiteOccupyHours] Completed fetching occupyhours IoTDaoImpl : getSiteOccupyHours() =====>");
		return ohs;
	}
	
	private static final class OccupyHoursMapper implements RowMapper<OccupyHours>{

		@Override
		public OccupyHours mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			OccupyHours oh = new OccupyHours();			
			oh.setDayOfWeek(resultSet.getInt("sto_frn_dow_id"));
			oh.setOpenTime(resultSet.getString("sto_open_time"));
			oh.setCloseTime(resultSet.getString("sto_close_time"));
			oh.setOccupyHours(resultSet.getInt("stt_occupy_hrs"));
			
			return oh;
		}
		
	}

	@Override
	public ProcessSetAt processSetAt(int deviceId, String coolSet, String heatSet, int currentDay, String currentTime) throws VEMAppException{
	SimpleJdbcCall simpleJdbcCall;
	Map<String, Object> simpleJdbcCallResult;
	Entry<String, Object> entry;
	List<HashMap<String, Object>> tempList;
	
	/*
	 * This iterator is used to loop the results. 
	 */
	Iterator<Entry<String, Object>> it;
	ProcessSetAt processSetAt = new ProcessSetAt();
	logger.debug("[DEBUG] Executing sp_select_device_setat procedure.");
	/*
	 * Instantiating simpleJdbcCall, Appending the procedure,
	 * And executing the procedure.
	 */
	simpleJdbcCall = new SimpleJdbcCall(jdbcTemplateRead).withProcedureName("sp_select_device_setat");
	
	/*
     * Adding all the input parameter values to a hash map.
     */
	Map<String,Object> inputParams=new HashMap<>();
	inputParams.put("in_device_id", deviceId);
	inputParams.put("in_cool_set", coolSet);
	inputParams.put("in_heat_set", heatSet);
	inputParams.put("in_current_day", currentDay);
	inputParams.put("in_current_time", currentTime);
	/*
	 * Executing the procedure.
	 */
	simpleJdbcCallResult = simpleJdbcCall.execute(inputParams);
	
	/*
	 * looping the result sets.
	 */
	it = simpleJdbcCallResult.entrySet().iterator();
	
	    while (it.hasNext()) {
	        /*
	         * Getting the each result set as entry 
	         * and Getting the key from entry. 
	         */
	    	entry = (Map.Entry<String, Object>) it.next();
	        String key = entry.getKey().toString();
	        
	        logger.debug("[DEBUG] Key - "+key);
	        
	        /*
	         * Forming the device more  data from the device and schedule table. 
	         */
	        if(key.equals(RESULT_SET_1)){
	        	
	        	tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
	        	
		        if(!tempList.isEmpty()) {
		        	int clngInt =(Integer)CommonUtility.isNullForInteger(tempList.get(0).get("clng_pt"));
		        	processSetAt.setCoolPoint(Integer.toString(clngInt));
		        }
	        }else if (key.equals(RESULT_SET_2)) {
	        		tempList = (List<HashMap<String, Object>>) (Object) entry.getValue();
	        	
		        if(!tempList.isEmpty()) {
		        	int htngFloat = (Integer)CommonUtility.isNullForInteger(tempList.get(0).get("htng_pt"));
		        	processSetAt.setHeatPoint(Integer.toString(htngFloat));
		        }
			}

	    }
	    logger.info("Device set at details found as follows : "+processSetAt.getCoolPoint() +" "+processSetAt.getHeatPoint() +" and existing live data as follows"+coolSet+" "+heatSet);
	return processSetAt;
	}

	@Override
	public List<GroupInfo> getGroupInfo(int siteId, int userId, int isSuper) throws VEMAppException {
		logger.info("[BEGIN] [IoTDaoImpl] [getGroupInfo] Inside the IoTDaoImpl : getGroupInfo() =====>");		
		String sql = "call sp_select_group_device ("+siteId+", "+userId+", "+isSuper+")";
		List<GroupInfo> group;
		try {
			group = jdbcTemplateRead.query(sql,  new Object [] {},  new GroupInfoMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getGroupInfo]Exception occured while fetching groupInfo by site id "+e);
			group = null;
		}
		logger.info("[END] [IoTDaoImpl] [getGroupInfo] Completed fetching group IoTDaoImpl : getGroupInfo() =====>");
		return group;
	}
	
	private static final class GroupInfoMapper implements RowMapper<GroupInfo>{

		@Override
		public GroupInfo mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			GroupInfo group = new GroupInfo();
			
			group.setGroupId(resultSet.getInt("group_id"));
			group.setGroupName(resultSet.getString("group_name"));
			return group;
		}
		
	}
	
	@Override
	public List<ThingResponse> getThingListByCustomer(int customerId) {
		logger.info("[BEGIN] [IoTDaoImpl] [getThingListByCustomer] Inside the IoTDaoImpl : getThingList() =====>");		
		String sql = "select device_id from device where  is_deleted = 0 and customer_id = ?";
		List<ThingResponse> thingList;
		try {
			thingList = jdbcTemplateRead.query(sql,  new Object [] { customerId },  new ThingResponseInfoByCousterMapper());			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getThingListByCustomer]Exception occured while fetching thing list "+e);
			thingList = null;
		}
		logger.info("[END] [IoTDaoImpl] [getThingListByCustomer] Completed fetching thing list IoTDaoImpl : getThingList() =====>");
		return thingList;
	}
	
	@Override
	public List<ThingResponse> getThingListBySite(int siteId) {
		logger.info("[BEGIN] [IoTDaoImpl] [getThingListBySite] Inside the IoTDaoImpl : getThingList() =====>");		
		String sql = "select device_id from device where  is_deleted = 0 and site_id = ?";
		List<ThingResponse> thingList;
		try {
			thingList = jdbcTemplateRead.query(sql,  new Object [] { siteId },  new ThingResponseInfoByCousterMapper());			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [IoTDaoImpl] [getThingListBySite]Exception occured while fetching thing list "+e);
			thingList = null;
		}
		logger.info("[END] [IoTDaoImpl] [getThingListBySite] Completed fetching thing list IoTDaoImpl : getThingList() =====>");
		return thingList;
	}
	
	private static final class ThingResponseInfoByCousterMapper implements RowMapper<ThingResponse>{
		
		@Override
		public ThingResponse mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			ThingResponse thing = new ThingResponse();
			
			thing.setDeviceId(resultSet.getInt("device_id"));
			return thing;
		}
	}

	@Override
	public int updateHeatPumpFields(UpdateHeatPumpFieldReq heatPump, Integer userId) throws VEMAppException {
		logger.info("[BEGIN] [updateHeatPumpFields] [IoTDaoImpl DAO LAYER]");
		
		/*
		 * These two variable are used to hold out params of stored procedure.
		 */
		int statusFlag = 0;
		String errorMsg = "";
		
	    /*
	     * Declaring the SimpleJdbcCall reference.
	     */
		SimpleJdbcCall simpleJdbcCall;
		
		/*
		 * This variable holds the out parameters. 
		 */
		Map<String,Object> outParameters = null;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName("sp_update_heat_pump_fields");
		    
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_device_id", heatPump.getDeviceId());
			inputParams.put("in_heatpump_type", heatPump.getHeatPumpUpdateType());
			inputParams.put("in_heatpump_type_value", heatPump.getHeatPumpUpdateTypeValue());
			inputParams.put("in_updated_by", userId);
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_update_heat_pump_fields");
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - "+inputParams);
		    
			/*
			 * Adding all the input parameter map to simpleJdbcCall.execute method.
			 */
	        outParameters = simpleJdbcCall.execute(inputParams);
	        
	        /* 
	         * if the errorMsg is empty means the update device request 
	         * got success in database
	         * else there is an exception occurred at database side and request got failed.
	         */
	        errorMsg = (String) outParameters.get("out_error_msg");
	        if(errorMsg.isEmpty()){
	        	//getting the inserted flag value
	        	statusFlag = (int) outParameters.get("out_flag");
	        }else{
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED, logger, new Exception(errorMsg));
	        }
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_DEVICE_STATUS_FAILED, logger, e);
		}
		
		logger.info("[END] [updateHeatPumpFields] [IoTDaoImpl DAO LAYER]");

		return statusFlag;
	}
	
	
}
