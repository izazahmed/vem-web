package com.enerallies.vem.daoimpl.weather;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.enerallies.vem.beans.iot.ScheduleData;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.iot.UpdateForecastRequest;
import com.enerallies.vem.beans.iot.UpdateForecastTemp;
import com.enerallies.vem.beans.weather.AddForecastRequest;
import com.enerallies.vem.beans.weather.ForecastData;
import com.enerallies.vem.beans.weather.ForecastResponse;
import com.enerallies.vem.beans.weather.ForecastTemp;
import com.enerallies.vem.beans.weather.ForecastTempResponse;
import com.enerallies.vem.dao.weather.WeatherDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.DatesUtil;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : WeatherDaoImpl 
 * 
 * WeatherDaoImpl: is implementation of WeatherDao
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        22-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	22-11-2016		Rajashekharaiah Muniswamy		File Created
 * 03	
 */

public class WeatherDaoImpl implements WeatherDao{


	// Getting logger instance
	private static final Logger logger = Logger.getLogger(WeatherDaoImpl.class);

	private static final String PROC_INSERT_FORECAST_DATA = "sp_insert_forecast_data";
	private static final String PROC_INSERT_FORECAST_CONFIG = "sp_insert_forecast_config";
	private static final String PROC_UPDATE_FORECAST_CONFIG = "sp_update_forecast_config";
	private static final String PROC_INSERT_FORECAST_CONFIG_TEMP = "sp_insert_forecast_config_temp";
	private static final String PROC_UPDATE_FORECAST_CONFIG_TEMP = "sp_update_forecast_config_temp";
	private static final  String INSERT_INTO_HOURLY_FORECAST= " INSERT INTO wg_hourly_temperature(wht_device_id, wht_zipcode, wht_hour, wht_temp, wht_created_on) VALUES(?,?,?,?,?)";
	private static final  String PROC_UPDATE_FORECAST_MODE= "sp_update_forecast_mode";
	
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
	public void insertForecastData(ForecastData forecastData) throws VEMAppException {
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		Map<String,Object> inputParams=null;
		logger.info("Controller reached WeatherDaoImpl.insertForecastData");
		try{
			
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(PROC_INSERT_FORECAST_DATA);
		    inputParams=new HashMap<>();
		    
		    inputParams.put("in_device_id", forecastData.getDeviceId());
		    inputParams.put("in_zipcode", forecastData.getZipcode());
		    inputParams.put("in_td_min_temp", forecastData.getTodayMinTemp());
		    inputParams.put("in_td_max_temp", forecastData.getTodayMaxTemp());
		    inputParams.put("in_tm_min_temp", forecastData.getTomoMinTemp());
		    inputParams.put("in_tm_max_temp", forecastData.getTomoMaxTemp());
		    
		    outParameters=simpleJdbcCall.execute(inputParams);
			logger.info("Inserted forecast data successfully Affected rows"+outParameters);
		}catch(Exception e){
			logger.error("Error found While inserting forecast data",e);
		}
	}

	@Override
	public int addForecast(AddForecastRequest forecastReq) throws VEMAppException {

		
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		Map<String,Object> inputParams=null;
		int forecastConfigId;
		try{
		logger.info("Controller reached WeatherDaoImpl.addForecast");
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(PROC_INSERT_FORECAST_CONFIG);
		    inputParams=new HashMap<>();
		    
		    inputParams.put("in_mode", forecastReq.getMode());
		    inputParams.put("in_type", forecastReq.getType());
		    inputParams.put("in_type_id", forecastReq.getTypeId());
		    inputParams.put("in_forecast_name", forecastReq.getForecastName());
		    inputParams.put("in_from_date", Timestamp.valueOf(forecastReq.getFromDate()));
		    inputParams.put("in_to_date", Timestamp.valueOf(forecastReq.getToDate()));
		    inputParams.put("in_created_by", forecastReq.getCreatedBy());
		    inputParams.put("in_updated_by", forecastReq.getCreatedBy());
		    
		    outParameters=simpleJdbcCall.execute(inputParams);
			logger.info("Inserted forecast config successfully Affected rows"+outParameters.get("out_flag"));

			// get the added forecast id and then insert temperature details into child table forecast config 
			
			forecastConfigId = (Integer)outParameters.get("last_inserted_id");
			List<ForecastTemp> tempList = forecastReq.getForecastTempList();
			for (int i = 0; i < tempList.size(); i++) {
				SimpleJdbcCall simpleJdbcCall1;
				Map<String,Object> outParameters1 = null;
				Map<String,Object> inputParams1=null;
				simpleJdbcCall1 = new SimpleJdbcCall(dataSource);
			    simpleJdbcCall1.withProcedureName(PROC_INSERT_FORECAST_CONFIG_TEMP);
			    inputParams1=new HashMap<>();
			    
			    inputParams1.put("in_forecast_id", forecastConfigId);
			    inputParams1.put("in_min_temp", tempList.get(i).getMinTemp());
			    inputParams1.put("in_max_temp", tempList.get(i).getMaxTemp());
			    inputParams1.put("in_schedule_id", tempList.get(i).getScheduleId());
			    			    
			    outParameters1=simpleJdbcCall1.execute(inputParams1);
			    logger.info("Inserted forecast config temperature successfully Affected rows"+outParameters1.get("out_flag"));
			}
		}catch (Exception e) {
			logger.error("Error found while adding forecast config "+e);
			forecastConfigId =0;
		}
		return forecastConfigId;
	}


	@Override
	public List<ScheduleData> getScheduleList(int value, int sortByType) throws VEMAppException {
		logger.info("[BEGIN] [WeatherDaoImpl] [getScheduleList]  =====>");		
		String sql = "call sp_list_schedule_device ("+value+", "+sortByType+")";
		List<ScheduleData> scheduleDataList;
		try {
			scheduleDataList = jdbcTemplateRead.query(sql,  new Object [] {},  new ScheduleDataMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [WeatherDaoImpl] [getScheduleList]"+e);
			scheduleDataList = null;
		}
		logger.info("[END] [WeatherDaoImpl] [getScheduleList] =====>");
		return scheduleDataList;
	}
	
	private static final class ScheduleDataMapper implements RowMapper<ScheduleData>{

		@Override
		public ScheduleData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			ScheduleData scheduleData = new ScheduleData();
			
			scheduleData.setId(resultSet.getInt("schedule_id"));
			scheduleData.setLabel(resultSet.getString("schedule_name"));

			return scheduleData;
		}
		
	}

	@Override
	public List<ForecastResponse> getForecastList(int type, int typeId, int flag) throws VEMAppException {
		logger.info("[BEGIN] [WeatherDaoImpl] [getForecastList]  =====>");
		String sql;
		if(flag==0){
			sql = "call sp_list_forecast ("+type+", "+typeId+", 0)";
		}else if (flag==3) {
			sql = "call sp_list_forecast (0, "+typeId+", 3)";
		}
		else{
			java.util.Date date = new Date();
			
			Calendar c = Calendar.getInstance(); 
			c.setTime(date); 
			c.add(Calendar.DATE, 1);
			date = c.getTime();
			
			Timestamp timestamp = new Timestamp(date.getTime());

			logger.info("Fetching forecost record for deviceId "+typeId+" and for the date "+timestamp);
			sql = "call sp_list_forecast ("+type+", "+typeId+", 2)";
		}
		List<ForecastResponse> forecastList;
		try {
			forecastList = jdbcTemplateRead.query(sql,  new Object [] {},  new ForecastMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [WeatherDaoImpl] [getForecastList]"+e);
			forecastList = null;
		}
		logger.info("[END] [WeatherDaoImpl] [getForecastList] =====>");
		return forecastList;
	}
	
	private static final class ForecastMapper implements RowMapper<ForecastResponse>{

		@Override
		public ForecastResponse mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			ForecastResponse forecast = new ForecastResponse();
			
			forecast.setForecastId(resultSet.getInt("fc_id"));
			forecast.setMode(resultSet.getInt("fc_mode"));
			forecast.setType(resultSet.getInt("fc_type"));
			forecast.setTypeId(resultSet.getInt("fc_type_id"));
			forecast.setForecastName(resultSet.getString("fc_forecast_name"));
			forecast.setFromDate(resultSet.getTimestamp("fc_from_date").toString());
			forecast.setToDate(resultSet.getTimestamp("fc_to_date").toString());
			forecast.setCreatedOn(resultSet.getTimestamp("fc_created_on").toString());
			forecast.setCreatedBy(resultSet.getInt("fc_created_by"));
			forecast.setUpdatedOn(resultSet.getTimestamp("fc_updated_on").toString());
			forecast.setUpdatedBy(resultSet.getInt("fc_updated_by"));
			
			return forecast;
		}
		
	}

	@Override
	public List<ForecastTempResponse> getForecastTempList(int forecastId) throws VEMAppException {
		logger.info("[BEGIN] [WeatherDaoImpl] [getForecastTempList]  =====>");		
		String sql = "call sp_list_forecast (0,"+forecastId+",1)";
		List<ForecastTempResponse> forecastTempList;
		try {
			forecastTempList = jdbcTemplateRead.query(sql,  new Object [] {},  new ForecastTempMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [WeatherDaoImpl] [getForecastTempList]"+e);
			forecastTempList = null;
		}
		logger.info("[END] [WeatherDaoImpl] [getForecastTempList] =====>");
		return forecastTempList;
	}
	
	private static final class ForecastTempMapper implements RowMapper<ForecastTempResponse>{

		@Override
		public ForecastTempResponse mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			ForecastTempResponse forecastTemp = new ForecastTempResponse();
			
			forecastTemp.setForecastTempId(resultSet.getInt("fct_id"));
			forecastTemp.setForecastId(resultSet.getInt("fct_frn_id"));
			forecastTemp.setMinTemp(resultSet.getInt("fct_min_temp"));
			forecastTemp.setMaxTemp(resultSet.getInt("fct_max_temp"));
			forecastTemp.setScheduleId(resultSet.getInt("fct_frn_schedule_id"));

			return forecastTemp;
		}
		
	}

	@Override
	public void insertHourlyForecastData(JSONArray hourlyForecastArr, ThingResponse thing) throws VEMAppException {

		logger.info("[BEGIN] [insertHourlyForecastData] [WeatherDaoImpl]");
		
		jdbcTemplate.batchUpdate(INSERT_INTO_HOURLY_FORECAST, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				JSONObject hourlyForecastObj = (JSONObject)hourlyForecastArr.get(i);
				
				JSONObject fctObj = (JSONObject)hourlyForecastObj.get("FCTTIME");
				String hourS = (String)fctObj.get("hour");
				int hour = Integer.parseInt(hourS);
				
				JSONObject temp = (JSONObject)hourlyForecastObj.get("temp");
				String tempValueS = (String)temp.get("english");
				int tempValue = Integer.parseInt(tempValueS);
				
				java.util.Date date = new Date();
				Timestamp timestamp = new Timestamp(date.getTime());
				
				ps.setInt(1, thing.getDeviceId());
				ps.setInt(2, Integer.parseInt(thing.getSiteZipcode()));
				ps.setInt(3, hour);
				ps.setInt(4, tempValue);
				ps.setTimestamp(5, timestamp);
			}
			
			@Override
			public int getBatchSize() {
				return hourlyForecastArr.size()-12;
			}
		});
		logger.info("[END] [insertHourlyForecastData] [WeatherDaoImpl]");
	}

	
	public void insertHourlyHistoryData(JSONArray hourlyForecastArr, ThingResponse thing) throws VEMAppException {

		logger.info("[BEGIN] [insertHourlyHistoryData] [WeatherDaoImpl]");
		
		jdbcTemplate.batchUpdate(INSERT_INTO_HOURLY_FORECAST, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				JSONObject hourlyForecastObj = (JSONObject)hourlyForecastArr.get(i);
				
				JSONObject fctObj = (JSONObject)hourlyForecastObj.get("utcdate");
				String yearS = (String)fctObj.get("year");
				int year = Integer.parseInt(yearS);
				
				String monS = (String)fctObj.get("mon");
				int mon = Integer.parseInt(monS);
				
				String mdayS = (String)fctObj.get("mday");
				int mday = Integer.parseInt(mdayS);
				
				String hourS = (String)fctObj.get("hour");
				int hour = Integer.parseInt(hourS);
				
				String minS = (String)fctObj.get("min");
				int min = Integer.parseInt(minS);
				
				String tempValueS = (String)hourlyForecastObj.get("tempi");
				
				logger.info("Found the temperature value as:"+tempValueS);
				logger.info("For the date and hour :"+year+" "+ mon+" "+ mday+" "+hour+" "+min);
				
				/*
				tempValueS = tempValueS.substring(0, 2);
				int tempValue = Integer.parseInt(tempValueS);
				*/
				
				/* changed this logic for rounding */
				int tempValue = Math.round(Float.parseFloat(tempValueS));
				
				java.util.Date date = null;
				String dateString = year + "/" + mon + "/" + mday + " "+ hour+":"+min+":00";
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			      try {
					date = formatter.parse(dateString);
				} catch (ParseException e) {
					logger.error("Error found converting dtae ", e);
				}
				Timestamp timestamp = new Timestamp(date.getTime());
				
				ps.setInt(1, thing.getDeviceId());
				ps.setInt(2, Integer.parseInt(thing.getSiteZipcode()));
				ps.setInt(3, hour);
				ps.setInt(4, tempValue);
				ps.setTimestamp(5, timestamp);
			}
			
			@Override
			public int getBatchSize() {
				return hourlyForecastArr.size();
			}
		});
		logger.info("[END] [insertHourlyHistoryData] [WeatherDaoImpl]");
	}
	
	@Override
	public JSONObject readSchedule(int deviceId) throws VEMAppException {
		String sql = "call sp_get_schedule("+deviceId+")";
		
		JSONObject schedule = new JSONObject();
		Map<String, JSONArray> scheduleMap = new HashMap<>();
		
		jdbcTemplateRead.query(sql, new RowCallbackHandler() {
			
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				JSONObject obj = new JSONObject();
				obj.put("start_time", rs.getString("start_time"));
				obj.put("heat_temp_set", rs.getInt("htng_pt"));
				obj.put("cool_temp_set", rs.getInt("clng_pt"));
				

				if(scheduleMap.containsKey(rs.getString("day"))){
					JSONArray resArr =scheduleMap.get(rs.getString("day"));
				
					resArr.add(obj);
					
					scheduleMap.put(rs.getString("day"), resArr);
				}else{
					 JSONArray arr = new JSONArray();
					 
					arr.add(obj);
					scheduleMap.put(rs.getString("day"),arr);
				}
				
			}
		});
		
    	for (Map.Entry<String, JSONArray> entry : scheduleMap.entrySet()) {
    		schedule.put(entry.getKey(), (JSONArray)entry.getValue());
    	}
    	
		
		return schedule;
	}

	@Override
	public int updateForecast(UpdateForecastRequest forecastReq) throws VEMAppException {
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		Map<String,Object> inputParams=null;
		try{
		logger.info("Controller reached WeatherDaoImpl.updateForecast");
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(PROC_UPDATE_FORECAST_CONFIG);
		    inputParams=new HashMap<>();
		    
		    inputParams.put("in_forecast_id", forecastReq.getForecastId());
		    inputParams.put("in_mode", forecastReq.getMode());
		    inputParams.put("in_type", forecastReq.getType());
		    inputParams.put("in_type_id", forecastReq.getTypeId());
		    inputParams.put("in_forecast_name", forecastReq.getForecastName());
		    inputParams.put("in_from_date", Timestamp.valueOf(forecastReq.getFromDate()));
		    inputParams.put("in_to_date", Timestamp.valueOf(forecastReq.getToDate()));
		    inputParams.put("in_created_by", forecastReq.getCreatedBy());
		    inputParams.put("in_updated_by", forecastReq.getCreatedBy());
		    
		    outParameters=simpleJdbcCall.execute(inputParams);
			logger.info("Updated forecast config successfully Affected rows"+outParameters.get("out_flag"));

			// get the added forecast id and then insert temperature details into child table forecast config 
			
			List<UpdateForecastTemp> tempList = forecastReq.getForecastTempList();
			for (int i = 0; i < tempList.size(); i++) {
				SimpleJdbcCall simpleJdbcCall1;
				Map<String,Object> outParameters1 = null;
				Map<String,Object> inputParams1=null;
				simpleJdbcCall1 = new SimpleJdbcCall(dataSource);
			    simpleJdbcCall1.withProcedureName(PROC_UPDATE_FORECAST_CONFIG_TEMP);
			    inputParams1=new HashMap<>();
			    
			    inputParams1.put("in_forecast_temp_id", tempList.get(i).getForecastTempId());
			    inputParams1.put("in_forecast_id", tempList.get(i).getForecastId());
			    inputParams1.put("in_min_temp", tempList.get(i).getMinTemp());
			    inputParams1.put("in_max_temp", tempList.get(i).getMaxTemp());
			    inputParams1.put("in_schedule_id", tempList.get(i).getScheduleId());
			    			    
			    outParameters1=simpleJdbcCall1.execute(inputParams1);
			    logger.info("Updated forecast config temperature successfully Affected rows"+outParameters1.get("out_flag"));
			}
		}catch (Exception e) {
			logger.error("Error found while updating forecast config ", e);
			
		}
		return (Integer)outParameters.get("out_flag");

	}

	@Override
	public int deleteForecast(int forecastId, int userId) throws VEMAppException{
		logger.info("[BEGIN] [deleteForecast] [DAO LAYER]");
		
		int deleteRoleFlag = 0;
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
			simpleJdbcCall.withProcedureName("sp_delete_forecast");
			
			//Adding all the input parameter values to a hashmap.
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_forecast_id", forecastId);
			inputParams.put("in_user_id", userId);
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_delete_forecast");
			logger.debug("[DEBUG] input parameters - "+inputParams);
			
			//Adding all the input parameter map to simpleJdbcCall.execute method.
	        Map<String,Object> outParameters = simpleJdbcCall.execute(inputParams);
	        
	        logger.debug("[DUBUG] sp_delete_forecast out parameters - "+outParameters);
	        
	        //Getting the device deleted count.
	        deleteRoleFlag = (int) outParameters.get("out_flag");
	        
	        //if deleteRoleFlag is 0 means there is an exception occurred at database level.
	        if(deleteRoleFlag==0){
	        	//In procedure there is an exception occured.
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_DELETE_FORECAST_FAILED, logger, new Exception((String) outParameters.get("out_error_msg")));
	        }
	        
		  } catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_DELETE_FORECAST_FAILED, logger, e);
		}
		
		logger.info("[END] [deleteForecast] [DAO LAYER]");
		
		return deleteRoleFlag;
	}
	
	@Override
	public int updateForecastMode(String deviceId, String forecastMode,String forcastType, int userId)	throws VEMAppException {
		
		logger.info("[BEGIN] [updateForecastMode] [DAO LAYER]");
		
		int updateFlag = 0;
		SimpleJdbcCall simpleJdbcCall;
		
		try {
			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
			simpleJdbcCall.withProcedureName(PROC_UPDATE_FORECAST_MODE);
			
			//Adding all the input parameter values to a hashmap.
			Map<String,Object> inputParams=new HashMap<>();
			inputParams.put("in_device_id", deviceId);
			inputParams.put("in_mode", forecastMode);
			inputParams.put("in_forecast_type", forcastType);
			inputParams.put("in_user_id", userId);
			
			
			logger.debug("[DEBUG] Executing the stored  procedure - sp_update_forecast_mode");
			logger.debug("[DEBUG] input parameters - "+inputParams);
			
			//Adding all the input parameter map to simpleJdbcCall.execute method.
	        Map<String,Object> outParameters = simpleJdbcCall.execute(inputParams);
	        
	        logger.debug("[DUBUG] sp_update_forecast_mode out parameters - "+outParameters);
	        
	        //Getting the device deleted count.
	        updateFlag = (int) outParameters.get("out_flag");
	        
	        //if deleteRoleFlag is 0 means there is an exception occurred at database level.
	        if(updateFlag==0){
	        	//In procedure there is an exception occured.
	        	throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_FORECAST_CONFIG, logger, new Exception((String) outParameters.get("out_error_msg")));
	        }
	        
		  }catch (Exception e) {
			//Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_UPDATE_FORECAST_CONFIG, logger, e);
		}
		logger.info("[END] [updateForecastMode] [DAO LAYER]");
		return updateFlag;
	}
}
