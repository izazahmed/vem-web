package com.enerallies.vem.jobschedule;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.weather.ForecastResponse;
import com.enerallies.vem.beans.weather.ForecastTempResponse;
import com.enerallies.vem.business.WundergroundBusiness;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.dao.weather.WeatherDao;
import com.enerallies.vem.daoimpl.schedule.ScheduleDaoImpl;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.weather.WeatherService;

@Component(value="weatherJob")
public class WeatherJob {

	/** Getting logger*/	
	private static final Logger logger = Logger.getLogger(WeatherJob.class);
	
	@Autowired
	IoTDao iotDao;
	
	@Autowired
	WeatherDao weatherDao;
	
	@Autowired
	WundergroundBusiness wunder;
	@Autowired
	ScheduleDaoImpl scheduleDaoImpl;
	
	@Autowired
	WeatherService weatherService;
	private void forecastJob(){
		try {
			
			logger.info("3 day Forecast job has been started");
			List<ThingResponse> thingResponse = iotDao.getThingListWithSiteName();
			
			logger.info("Found the device list in 3 day forecast job :"+thingResponse.size());
			ArrayList deviceListForecast = new ArrayList();
			for (int i = 0; i < thingResponse.size(); i++) {
				
				if(thingResponse.get(i).getRegisterType()==1){
					
					if(thingResponse.get(i).getDeviceStatus()==1){
					
					String forecastResp = wunder.getForeCastData(thingResponse.get(i).getSiteZipcode());
					logger.info("Forecast :"+forecastResp);
					weatherService.insertForecastData(forecastResp, thingResponse.get(i));
					
					
						List<ForecastResponse> forecastList;

						//Calling the DAO layer getForeList() method.
						forecastList = weatherDao.getForecastList(1, thingResponse.get(i).getDeviceId(), 2);
						/* if forecastList is not null means the get forecastList request is
						 *  success
						 *  else fail.
						 */
						if(forecastList!=null){
						if(forecastList.size()!=0){
							
							//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							//String fromDateStr = forecastList.get(0).getFromDate();
							//String toDateStr = forecastList.get(0).getToDate();
							
							//Date currentDate = sdf.parse(sdf.format(new Date()));
							//Date fromDate = sdf.parse(fromDateStr);
							//Date toDate = sdf.parse(toDateStr);
							
							///if(currentDate.compareTo(fromDate)>=0 && currentDate.compareTo(toDate)<=0){
							if(forecastList.get(0).getMode()==1){
							
								logger.info("Forecast  found to schedule for particular device forecastId "+forecastList.get(0).getForecastId()+" and device id "+thingResponse.get(i).getDeviceId());
								int maxTempVal=0;
								List<ForecastTempResponse> forecastTempList = weatherDao.getForecastTempList(forecastList.get(0).getForecastId());
								try{
								JSONObject forecastObj = new JSONObject(forecastResp);
								
								JSONObject nForecastObj = (JSONObject)forecastObj.get("forecast");
								
								JSONObject simpleForecastObj = (JSONObject)nForecastObj.get("simpleforecast");
								
								JSONArray forecastdayArray = (JSONArray)simpleForecastObj.get("forecastday");
								
								JSONObject tomorrow = (JSONObject)forecastdayArray.get(1);
								
								JSONObject tomorrowMax = (JSONObject)tomorrow.get("high");
								
								maxTempVal = tomorrowMax.getInt("fahrenheit");
								}catch(Exception e){
									logger.error("error found while parsing 3 day forecast data to get tomo max temp"+e);
								}
								
								for(int j=0;j<forecastTempList.size();j++){
									
									int tempMin = forecastTempList.get(j).getMinTemp();
									int tempMax = forecastTempList.get(j).getMaxTemp();
									
									logger.info("Check for the schedule id for tomo max temp"+maxTempVal);
									int scheduleId = forecastTempList.get(j).getScheduleId();
									logger.info("Found scheddule id for tomo max temp "+maxTempVal+" and forecastmin and max are"+tempMin+"&"+tempMax+ " and schedule id is"+scheduleId);

									if((tempMin<=maxTempVal) && (maxTempVal<=tempMax)){
/*										HashMap devicemapForecast= new HashMap();
										devicemapForecast.put("xcspec_device_id",thingResponse.get(i).getXcspecDeviceId());
										devicemapForecast.put("device_id",thingResponse.get(i).getDeviceId());
										devicemapForecast.put("schedule_id",scheduleId);
										deviceListForecast.add(devicemapForecast);*/
										
										logger.info("deviceListForecast for 3 day forecast************************ :");
										GetUserResponse userDetails = new GetUserResponse();
										userDetails.setUserId(1);
										Schedule schedule = new Schedule();
										logger.info("scheduleId"+scheduleId);
										logger.info("dthingResponse.get(i).getDeviceId() :"+thingResponse.get(i).getDeviceId());
										schedule.setDeviceId(""+thingResponse.get(i).getDeviceId());
										schedule.setScheduleId(""+scheduleId);
										schedule.setCustomerId("");
										schedule.setGroupId("");
										schedule.setSiteId("");
										applySchedule(userDetails, schedule);
									}
								}
							//}
						}// end check forecast mode
						}// end check forecast list size
						}
					}//end if device status
				}// end if register type
			}
/*			logger.info("deviceListForecast for 3 day forecast************************ :"+deviceListForecast.size());
			if(deviceListForecast.size()>0){
				scheduleDaoImpl.applyDeviceJSON(null,deviceListForecast,0);
			}*/
			
		} catch (SQLException e) {
			logger.error("Error found while fetching device list to fetch 3 day forecast data"+e);
		} catch (Exception e) {
			logger.error("Error found while fetching 3 day forecast data"+e);
		}
	}
	
	public void applySchedule(GetUserResponse userDetails,Schedule schedule){
		new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Apply schedule thread started for device"+schedule.getDeviceId());
				if(userDetails!=null && schedule!=null){
					try {
						//scheduleDaoImpl.applyDeviceJSON(null,deviceListForecast,0);
						scheduleDaoImpl.applySchedule(userDetails, schedule);
					} catch (VEMAppException e) {
						logger.error("Error found while applying schedule");
					}
				}
			}
		}).start();
	}
}
