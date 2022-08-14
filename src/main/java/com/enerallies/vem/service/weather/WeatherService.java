package com.enerallies.vem.service.weather;

import org.springframework.web.bind.annotation.PathVariable;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.iot.UpdateForecastRequestList;
import com.enerallies.vem.beans.weather.AddForecastRequestList;
import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : WeatherService 
 * 
 * WeatherService: is used to handle all the weather related operations and it contains only definition
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
public interface WeatherService {
	
	/**
	 * @param forecastReq
	 * @return
	 * @throws VEMAppException
	 */
	public Response addForecast(AddForecastRequestList forecastReq, int userId) throws VEMAppException;

	/**
	 * @param sortBy
	 * @param value
	 * @return
	 * @throws VEMAppException
	 */
	public Response getScheduleList(String sortBy, int value) throws VEMAppException;

	/**
	 * @param type
	 * @param typeId
	 * @return
	 */
	public Response getForecastList(int type, int typeId) throws VEMAppException;
	
	/**
	 * @param deviceId
	 * @return
	 * @throws VEMAppException
	 */
	public Response readSchedule(int deviceId) throws VEMAppException;
	
	public void parseAndInsertHourlyForecastData(String hourlyForecastResp, ThingResponse thingResponse) throws VEMAppException;
	
	public void insertForecastData(String forecast, ThingResponse thingResponse) throws VEMAppException;
	
	/**
	 * @param updateForecastReq
	 * @return
	 * @throws VEMAppException
	 */
	public Response updateForecast(UpdateForecastRequestList updateForecastReq, int userId) throws VEMAppException;
	
	/**
	 * @param forecastId
	 * @param userId
	 * @return
	 */
	public Response deleteForecast(int forecastId, int userId) throws VEMAppException;

	/**
	 * @param deviceId
	 * @param scheduleId
	 * @param xcspecId
	 * @return
	 */
	public Response runSchedule(int deviceId, int scheduleId, String xcspecId, GetUserResponse userInfo) throws VEMAppException;
	
	/**
	 * @param deviceId
	 * @param forecastMode
	 * @return
	 * @throws VEMAppException
	 */
	public Response updateForecastMode(String deviceId, String forecastMode,String forcastType, int userId) throws VEMAppException;
	
	public void fetchAndStoreHistoryData(int deviceId, String zipcode);
}
