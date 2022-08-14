package com.enerallies.vem.beans.weather;

import java.util.List;

/**
 * File Name : AddForecastRequestList 
 * 
 * AddForecastRequestList: is for transfer forecast related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        29-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	29-11-2016		Rajashekharaiah Muniswamy		File Created
 */
public class AddForecastRequestList {

	/**used to hold forecast configuration data*/
	private List<AddForecastRequest> forecastConfig;

	/**
	 * @return the forecastConfig
	 */
	public List<AddForecastRequest> getForecastConfig() {
		return forecastConfig;
	}

	/**
	 * @param forecastConfig the forecastConfig to set
	 */
	public void setForecastConfig(List<AddForecastRequest> forecastConfig) {
		this.forecastConfig = forecastConfig;
	}
	
	
}
