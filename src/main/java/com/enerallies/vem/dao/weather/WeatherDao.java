package com.enerallies.vem.dao.weather;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.iot.ScheduleData;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.iot.UpdateForecastRequest;
import com.enerallies.vem.beans.weather.AddForecastRequest;
import com.enerallies.vem.beans.weather.ForecastData;
import com.enerallies.vem.beans.weather.ForecastResponse;
import com.enerallies.vem.beans.weather.ForecastTempResponse;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : WeatherDao 
 * 
 * WeatherDao: is used to handle all the weather related operations and it contains only definition
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

public interface WeatherDao {


	/**
	 * @param forecastData
	 * @throws VEMAppException
	 */
	public void insertForecastData(ForecastData forecastData) throws VEMAppException;
	
	/**
	 * @param forecastReq
	 * @return
	 * @throws VEMAppException
	 */
	public int addForecast(AddForecastRequest forecastReq) throws VEMAppException;
	
	/**
	 * @param sortBy
	 * @param value
	 * @return
	 * @throws VEMAppException
	 */
	public List<ScheduleData> getScheduleList(int value, int sortByType) throws VEMAppException;
	
	
	/**
	 * @param type
	 * @param typeId
	 * @return
	 * @throws VEMAppException
	 */
	public List<ForecastResponse> getForecastList(int type, int typeId, int flag) throws VEMAppException;
	

	/**
	 * @param forecastId
	 * @return
	 * @throws VEMAppException
	 */
	public List<ForecastTempResponse> getForecastTempList(int forecastId) throws VEMAppException;
	
	/**
	 * @param hourlyForecast
	 * @param thing
	 * @throws VEMAppException
	 */
	public void insertHourlyForecastData(JSONArray hourlyForecast, ThingResponse thing) throws VEMAppException;
	
	public void insertHourlyHistoryData(JSONArray hourlyForecast, ThingResponse thing) throws VEMAppException;
	public JSONObject readSchedule(int deviceId) throws VEMAppException;
	
	/**
	 * @param forecastReq
	 * @return
	 * @throws VEMAppException
	 */
	public int updateForecast(UpdateForecastRequest forecastReq) throws VEMAppException;

	/**
	 * @param forecastId
	 * @param userId
	 * @return
	 */
	public int deleteForecast(int forecastId, int userId) throws VEMAppException;
	
	/**
	 * @param deviceId
	 * @param forecastMode
	 * @return
	 * @throws VEMAppException
	 */
	public int updateForecastMode(String deviceId, String forecastMode,String forcastType, int userId) throws VEMAppException;

}
