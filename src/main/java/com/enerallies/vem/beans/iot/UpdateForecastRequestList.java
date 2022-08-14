package com.enerallies.vem.beans.iot;

import java.util.List;

public class UpdateForecastRequestList {

	private List<UpdateForecastRequest> forecastConfig;

	/**
	 * @return the forecastConfig
	 */
	public List<UpdateForecastRequest> getForecastConfig() {
		return forecastConfig;
	}

	/**
	 * @param forecastConfig the forecastConfig to set
	 */
	public void setForecastConfig(List<UpdateForecastRequest> forecastConfig) {
		this.forecastConfig = forecastConfig;
	}

	
}
