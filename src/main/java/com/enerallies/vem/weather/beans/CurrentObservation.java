/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.weather.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * File Name : CurrentObservation 
 * CurrentObservation Class is used hold current observation information
 *
 * @author (Y Chenna Reddy – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     1.0
 * @date        28-07-2016
 *
 * MOD HISTORY
 * 
 * DATE				USER             		COMMENTS
 * 
 * 28-07-2016		Y Chenna Reddy			File Created
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentObservation {

	/**
	 * Declare COImage image, this property is used to hold the image
	 */
	private COImage image;

	/**
	 * Declare DisplayLocation display_location, this property is used to hold
	 * the display_location
	 */
	private DisplayLocation display_location;

	/**
	 * Declare ObservationLocation observation_location, this property is used
	 * to hold the observation_location
	 */
	private ObservationLocation observation_location;

	/**
	 * Declare Estimated estimated, this property is used to hold the estimated
	 */
	private Estimated estimated;

	/**
	 * Declare String station_id, this property is used to hold the station_id
	 */
	private String station_id;

	/**
	 * Declare String observation_time, this property is used to hold the
	 * observation_time
	 */
	private String observation_time;

	/**
	 * Declare String observation_time_rfc822, this property is used to hold the
	 * observation_time_rfc822
	 */
	private String observation_time_rfc822;

	/**
	 * Declare String observation_epoch, this property is used to hold the
	 * observation_epoch
	 */
	private String observation_epoch;

	/**
	 * Declare String local_time_rfc822, this property is used to hold the
	 * local_time_rfc822
	 */
	private String local_time_rfc822;

	/**
	 * Declare String local_epoch, this property is used to hold the local_epoch
	 */
	private String local_epoch;

	/**
	 * Declare String local_tz_short, this property is used to hold the
	 * local_tz_short
	 */
	private String local_tz_short;

	/**
	 * Declare String local_tz_long, this property is used to hold the
	 * local_tz_long
	 */
	private String local_tz_long;

	/**
	 * Declare String local_tz_offset, this property is used to hold the
	 * local_tz_offset
	 */
	private String local_tz_offset;

	/**
	 * Declare String weather, this property is used to hold the weather
	 */
	private String weather;

	/**
	 * Declare String temperature_string, this property is used to hold the
	 * temperature_string
	 */
	private String temperature_string;

	/**
	 * Declare String temp_f, this property is used to hold the temp_f
	 */
	private String temp_f;

	/**
	 * Declare String temp_c, this property is used to hold the temp_c
	 */
	private String temp_c;

	/**
	 * Declare String relative_humidity, this property is used to hold the
	 * relative_humidity
	 */
	private String relative_humidity;

	/**
	 * Declare String wind_string, this property is used to hold the wind_string
	 */
	private String wind_string;

	/**
	 * Declare String wind_dir, this property is used to hold the wind_dir
	 */
	private String wind_dir;

	/**
	 * Declare String wind_degrees, this property is used to hold the
	 * wind_degrees
	 */
	private String wind_degrees;

	/**
	 * Declare String wind_mph, this property is used to hold the wind_mph
	 */
	private String wind_mph;

	/**
	 * Declare String wind_gust_mph, this property is used to hold the
	 * wind_gust_mph
	 */
	private String wind_gust_mph;

	/**
	 * Declare String wind_kph, this property is used to hold the wind_kph
	 */
	private String wind_kph;

	/**
	 * Declare String wind_gust_kph, this property is used to hold the
	 * wind_gust_kph
	 */
	private String wind_gust_kph;

	/**
	 * Declare String pressure_mb, this property is used to hold the pressure_mb
	 */
	private String pressure_mb;

	/**
	 * Declare String pressure_in, this property is used to hold the pressure_in
	 */
	private String pressure_in;

	/**
	 * Declare String pressure_trend, this property is used to hold the
	 * pressure_trend
	 */
	private String pressure_trend;

	/**
	 * Declare String dewpoint_string, this property is used to hold the
	 * dewpoint_string
	 */
	private String dewpoint_string;

	/**
	 * Declare String dewpoint_f, this property is used to hold the dewpoint_f
	 */
	private String dewpoint_f;

	/**
	 * Declare String dewpoint_c, this property is used to hold the dewpoint_c
	 */
	private String dewpoint_c;

	/**
	 * Declare String heat_index_string, this property is used to hold the
	 * heat_index_string
	 */
	private String heat_index_string;

	/**
	 * Declare String heat_index_f, this property is used to hold the
	 * heat_index_f
	 */
	private String heat_index_f;

	/**
	 * Declare String heat_index_c, this property is used to hold the
	 * heat_index_c
	 */
	private String heat_index_c;

	/**
	 * Declare String windchill_string, this property is used to hold the
	 * windchill_string
	 */
	private String windchill_string;

	/**
	 * Declare String windchill_f, this property is used to hold the windchill_f
	 */
	private String windchill_f;

	/**
	 * Declare String windchill_c, this property is used to hold the windchill_c
	 */
	private String windchill_c;

	/**
	 * Declare String feelslike_string, this property is used to hold the
	 * feelslike_string
	 */
	private String feelslike_string;

	/**
	 * Declare String feelslike_f, this property is used to hold the feelslike_f
	 */
	private String feelslike_f;

	/**
	 * Declare String feelslike_c, this property is used to hold the feelslike_c
	 */
	private String feelslike_c;

	/**
	 * Declare String visibility_mi, this property is used to hold the
	 * visibility_mi
	 */
	private String visibility_mi;

	/**
	 * Declare String visibility_km, this property is used to hold the
	 * visibility_km
	 */
	private String visibility_km;

	/**
	 * Declare String solarradiation, this property is used to hold the
	 * solarradiation
	 */
	private String solarradiation;

	/**
	 * Declare String UV, this property is used to hold the UV
	 */
	@JsonProperty("UV")
	private String uV;

	/**
	 * Declare String precip_1hr_string, this property is used to hold the
	 * precip_1hr_string
	 */
	private String precip_1hr_string;

	/**
	 * Declare String precip_1hr_in, this property is used to hold the
	 * precip_1hr_in
	 */
	private String precip_1hr_in;

	/**
	 * Declare String precip_1hr_metric, this property is used to hold the
	 * precip_1hr_metric
	 */
	private String precip_1hr_metric;

	/**
	 * Declare String precip_today_string, this property is used to hold the
	 * precip_today_string
	 */
	private String precip_today_string;

	/**
	 * Declare String precip_today_in, this property is used to hold the
	 * precip_today_in
	 */
	private String precip_today_in;

	/**
	 * Declare String precip_today_metric, this property is used to hold the
	 * precip_today_metric
	 */
	private String precip_today_metric;

	/**
	 * Declare String icon, this property is used to hold the icon
	 */
	private String icon;

	/**
	 * Declare String icon_url, this property is used to hold the icon_url
	 */
	private String icon_url;

	/**
	 * Declare String forecast_url, this property is used to hold the
	 * forecast_url
	 */
	private String forecast_url;

	/**
	 * Declare String history_url, this property is used to hold the history_url
	 */
	private String history_url;

	/**
	 * Declare String ob_url, this property is used to hold the ob_url
	 */
	private String ob_url;

	/**
	 * Declare String nowcast, this property is used to hold the nowcast
	 */
	private String nowcast;

	/**
	 * @return the image
	 */
	public COImage getImage() {
		return image;
	}

	/**
	 * @return the display_location
	 */
	public DisplayLocation getDisplay_location() {
		return display_location;
	}

	/**
	 * @return the observation_location
	 */
	public ObservationLocation getObservation_location() {
		return observation_location;
	}

	/**
	 * @return the estimated
	 */
	public Estimated getEstimated() {
		return estimated;
	}

	/**
	 * @return the station_id
	 */
	public String getStation_id() {
		return station_id;
	}

	/**
	 * @return the observation_time
	 */
	public String getObservation_time() {
		return observation_time;
	}

	/**
	 * @return the observation_time_rfc822
	 */
	public String getObservation_time_rfc822() {
		return observation_time_rfc822;
	}

	/**
	 * @return the observation_epoch
	 */
	public String getObservation_epoch() {
		return observation_epoch;
	}

	/**
	 * @return the local_time_rfc822
	 */
	public String getLocal_time_rfc822() {
		return local_time_rfc822;
	}

	/**
	 * @return the local_epoch
	 */
	public String getLocal_epoch() {
		return local_epoch;
	}

	/**
	 * @return the local_tz_short
	 */
	public String getLocal_tz_short() {
		return local_tz_short;
	}

	/**
	 * @return the local_tz_long
	 */
	public String getLocal_tz_long() {
		return local_tz_long;
	}

	/**
	 * @return the local_tz_offset
	 */
	public String getLocal_tz_offset() {
		return local_tz_offset;
	}

	/**
	 * @return the weather
	 */
	public String getWeather() {
		return weather;
	}

	/**
	 * @return the temperature_string
	 */
	public String getTemperature_string() {
		return temperature_string;
	}

	/**
	 * @return the temp_f
	 */
	public String getTemp_f() {
		return temp_f;
	}

	/**
	 * @return the temp_c
	 */
	public String getTemp_c() {
		return temp_c;
	}

	/**
	 * @return the relative_humidity
	 */
	public String getRelative_humidity() {
		return relative_humidity;
	}

	/**
	 * @return the wind_string
	 */
	public String getWind_string() {
		return wind_string;
	}

	/**
	 * @return the wind_dir
	 */
	public String getWind_dir() {
		return wind_dir;
	}

	/**
	 * @return the wind_degrees
	 */
	public String getWind_degrees() {
		return wind_degrees;
	}

	/**
	 * @return the wind_mph
	 */
	public String getWind_mph() {
		return wind_mph;
	}

	/**
	 * @return the wind_gust_mph
	 */
	public String getWind_gust_mph() {
		return wind_gust_mph;
	}

	/**
	 * @return the wind_kph
	 */
	public String getWind_kph() {
		return wind_kph;
	}

	/**
	 * @return the wind_gust_kph
	 */
	public String getWind_gust_kph() {
		return wind_gust_kph;
	}

	/**
	 * @return the pressure_mb
	 */
	public String getPressure_mb() {
		return pressure_mb;
	}

	/**
	 * @return the pressure_in
	 */
	public String getPressure_in() {
		return pressure_in;
	}

	/**
	 * @return the pressure_trend
	 */
	public String getPressure_trend() {
		return pressure_trend;
	}

	/**
	 * @return the dewpoint_string
	 */
	public String getDewpoint_string() {
		return dewpoint_string;
	}

	/**
	 * @return the dewpoint_f
	 */
	public String getDewpoint_f() {
		return dewpoint_f;
	}

	/**
	 * @return the dewpoint_c
	 */
	public String getDewpoint_c() {
		return dewpoint_c;
	}

	/**
	 * @return the heat_index_string
	 */
	public String getHeat_index_string() {
		return heat_index_string;
	}

	/**
	 * @return the heat_index_f
	 */
	public String getHeat_index_f() {
		return heat_index_f;
	}

	/**
	 * @return the heat_index_c
	 */
	public String getHeat_index_c() {
		return heat_index_c;
	}

	/**
	 * @return the windchill_string
	 */
	public String getWindchill_string() {
		return windchill_string;
	}

	/**
	 * @return the windchill_f
	 */
	public String getWindchill_f() {
		return windchill_f;
	}

	/**
	 * @return the windchill_c
	 */
	public String getWindchill_c() {
		return windchill_c;
	}

	/**
	 * @return the feelslike_string
	 */
	public String getFeelslike_string() {
		return feelslike_string;
	}

	/**
	 * @return the feelslike_f
	 */
	public String getFeelslike_f() {
		return feelslike_f;
	}

	/**
	 * @return the feelslike_c
	 */
	public String getFeelslike_c() {
		return feelslike_c;
	}

	/**
	 * @return the visibility_mi
	 */
	public String getVisibility_mi() {
		return visibility_mi;
	}

	/**
	 * @return the visibility_km
	 */
	public String getVisibility_km() {
		return visibility_km;
	}

	/**
	 * @return the solarradiation
	 */
	public String getSolarradiation() {
		return solarradiation;
	}

	

	/**
	 * @return the precip_1hr_string
	 */
	public String getPrecip_1hr_string() {
		return precip_1hr_string;
	}

	/**
	 * @return the precip_1hr_in
	 */
	public String getPrecip_1hr_in() {
		return precip_1hr_in;
	}

	/**
	 * @return the precip_1hr_metric
	 */
	public String getPrecip_1hr_metric() {
		return precip_1hr_metric;
	}

	/**
	 * @return the precip_today_string
	 */
	public String getPrecip_today_string() {
		return precip_today_string;
	}

	/**
	 * @return the precip_today_in
	 */
	public String getPrecip_today_in() {
		return precip_today_in;
	}

	/**
	 * @return the precip_today_metric
	 */
	public String getPrecip_today_metric() {
		return precip_today_metric;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @return the icon_url
	 */
	public String getIcon_url() {
		return icon_url;
	}

	/**
	 * @return the forecast_url
	 */
	public String getForecast_url() {
		return forecast_url;
	}

	/**
	 * @return the history_url
	 */
	public String getHistory_url() {
		return history_url;
	}

	/**
	 * @return the ob_url
	 */
	public String getOb_url() {
		return ob_url;
	}

	/**
	 * @return the nowcast
	 */
	public String getNowcast() {
		return nowcast;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(COImage image) {
		this.image = image;
	}

	/**
	 * @param display_location
	 *            the display_location to set
	 */
	public void setDisplay_location(DisplayLocation display_location) {
		this.display_location = display_location;
	}

	/**
	 * @param observation_location
	 *            the observation_location to set
	 */
	public void setObservation_location(ObservationLocation observation_location) {
		this.observation_location = observation_location;
	}

	/**
	 * @param estimated
	 *            the estimated to set
	 */
	public void setEstimated(Estimated estimated) {
		this.estimated = estimated;
	}

	/**
	 * @param station_id
	 *            the station_id to set
	 */
	public void setStation_id(String station_id) {
		this.station_id = station_id;
	}

	/**
	 * @param observation_time
	 *            the observation_time to set
	 */
	public void setObservation_time(String observation_time) {
		this.observation_time = observation_time;
	}

	/**
	 * @param observation_time_rfc822
	 *            the observation_time_rfc822 to set
	 */
	public void setObservation_time_rfc822(String observation_time_rfc822) {
		this.observation_time_rfc822 = observation_time_rfc822;
	}

	/**
	 * @param observation_epoch
	 *            the observation_epoch to set
	 */
	public void setObservation_epoch(String observation_epoch) {
		this.observation_epoch = observation_epoch;
	}

	/**
	 * @param local_time_rfc822
	 *            the local_time_rfc822 to set
	 */
	public void setLocal_time_rfc822(String local_time_rfc822) {
		this.local_time_rfc822 = local_time_rfc822;
	}

	/**
	 * @param local_epoch
	 *            the local_epoch to set
	 */
	public void setLocal_epoch(String local_epoch) {
		this.local_epoch = local_epoch;
	}

	/**
	 * @param local_tz_short
	 *            the local_tz_short to set
	 */
	public void setLocal_tz_short(String local_tz_short) {
		this.local_tz_short = local_tz_short;
	}

	/**
	 * @param local_tz_long
	 *            the local_tz_long to set
	 */
	public void setLocal_tz_long(String local_tz_long) {
		this.local_tz_long = local_tz_long;
	}

	/**
	 * @param local_tz_offset
	 *            the local_tz_offset to set
	 */
	public void setLocal_tz_offset(String local_tz_offset) {
		this.local_tz_offset = local_tz_offset;
	}

	/**
	 * @param weather
	 *            the weather to set
	 */
	public void setWeather(String weather) {
		this.weather = weather;
	}

	/**
	 * @param temperature_string
	 *            the temperature_string to set
	 */
	public void setTemperature_string(String temperature_string) {
		this.temperature_string = temperature_string;
	}

	/**
	 * @param temp_f
	 *            the temp_f to set
	 */
	public void setTemp_f(String temp_f) {
		this.temp_f = temp_f;
	}

	/**
	 * @param temp_c
	 *            the temp_c to set
	 */
	public void setTemp_c(String temp_c) {
		this.temp_c = temp_c;
	}

	/**
	 * @param relative_humidity
	 *            the relative_humidity to set
	 */
	public void setRelative_humidity(String relative_humidity) {
		this.relative_humidity = relative_humidity;
	}

	/**
	 * @param wind_string
	 *            the wind_string to set
	 */
	public void setWind_string(String wind_string) {
		this.wind_string = wind_string;
	}

	/**
	 * @param wind_dir
	 *            the wind_dir to set
	 */
	public void setWind_dir(String wind_dir) {
		this.wind_dir = wind_dir;
	}

	/**
	 * @param wind_degrees
	 *            the wind_degrees to set
	 */
	public void setWind_degrees(String wind_degrees) {
		this.wind_degrees = wind_degrees;
	}

	/**
	 * @param wind_mph
	 *            the wind_mph to set
	 */
	public void setWind_mph(String wind_mph) {
		this.wind_mph = wind_mph;
	}

	/**
	 * @param wind_gust_mph
	 *            the wind_gust_mph to set
	 */
	public void setWind_gust_mph(String wind_gust_mph) {
		this.wind_gust_mph = wind_gust_mph;
	}

	/**
	 * @param wind_kph
	 *            the wind_kph to set
	 */
	public void setWind_kph(String wind_kph) {
		this.wind_kph = wind_kph;
	}

	/**
	 * @param wind_gust_kph
	 *            the wind_gust_kph to set
	 */
	public void setWind_gust_kph(String wind_gust_kph) {
		this.wind_gust_kph = wind_gust_kph;
	}

	/**
	 * @param pressure_mb
	 *            the pressure_mb to set
	 */
	public void setPressure_mb(String pressure_mb) {
		this.pressure_mb = pressure_mb;
	}

	/**
	 * @param pressure_in
	 *            the pressure_in to set
	 */
	public void setPressure_in(String pressure_in) {
		this.pressure_in = pressure_in;
	}

	/**
	 * @param pressure_trend
	 *            the pressure_trend to set
	 */
	public void setPressure_trend(String pressure_trend) {
		this.pressure_trend = pressure_trend;
	}

	/**
	 * @param dewpoint_string
	 *            the dewpoint_string to set
	 */
	public void setDewpoint_string(String dewpoint_string) {
		this.dewpoint_string = dewpoint_string;
	}

	/**
	 * @param dewpoint_f
	 *            the dewpoint_f to set
	 */
	public void setDewpoint_f(String dewpoint_f) {
		this.dewpoint_f = dewpoint_f;
	}

	/**
	 * @param dewpoint_c
	 *            the dewpoint_c to set
	 */
	public void setDewpoint_c(String dewpoint_c) {
		this.dewpoint_c = dewpoint_c;
	}

	/**
	 * @param heat_index_string
	 *            the heat_index_string to set
	 */
	public void setHeat_index_string(String heat_index_string) {
		this.heat_index_string = heat_index_string;
	}

	/**
	 * @param heat_index_f
	 *            the heat_index_f to set
	 */
	public void setHeat_index_f(String heat_index_f) {
		this.heat_index_f = heat_index_f;
	}

	/**
	 * @param heat_index_c
	 *            the heat_index_c to set
	 */
	public void setHeat_index_c(String heat_index_c) {
		this.heat_index_c = heat_index_c;
	}

	/**
	 * @param windchill_string
	 *            the windchill_string to set
	 */
	public void setWindchill_string(String windchill_string) {
		this.windchill_string = windchill_string;
	}

	/**
	 * @param windchill_f
	 *            the windchill_f to set
	 */
	public void setWindchill_f(String windchill_f) {
		this.windchill_f = windchill_f;
	}

	/**
	 * @param windchill_c
	 *            the windchill_c to set
	 */
	public void setWindchill_c(String windchill_c) {
		this.windchill_c = windchill_c;
	}

	/**
	 * @param feelslike_string
	 *            the feelslike_string to set
	 */
	public void setFeelslike_string(String feelslike_string) {
		this.feelslike_string = feelslike_string;
	}

	/**
	 * @param feelslike_f
	 *            the feelslike_f to set
	 */
	public void setFeelslike_f(String feelslike_f) {
		this.feelslike_f = feelslike_f;
	}

	/**
	 * @param feelslike_c
	 *            the feelslike_c to set
	 */
	public void setFeelslike_c(String feelslike_c) {
		this.feelslike_c = feelslike_c;
	}

	/**
	 * @param visibility_mi
	 *            the visibility_mi to set
	 */
	public void setVisibility_mi(String visibility_mi) {
		this.visibility_mi = visibility_mi;
	}

	/**
	 * @param visibility_km
	 *            the visibility_km to set
	 */
	public void setVisibility_km(String visibility_km) {
		this.visibility_km = visibility_km;
	}

	/**
	 * @param solarradiation
	 *            the solarradiation to set
	 */
	public void setSolarradiation(String solarradiation) {
		this.solarradiation = solarradiation;
	}

	public String getUV() {
		return uV;
	}

	public void setUV(String uV) {
		this.uV = uV;
	}

	/**
	 * @param precip_1hr_string
	 *            the precip_1hr_string to set
	 */
	public void setPrecip_1hr_string(String precip_1hr_string) {
		this.precip_1hr_string = precip_1hr_string;
	}

	/**
	 * @param precip_1hr_in
	 *            the precip_1hr_in to set
	 */
	public void setPrecip_1hr_in(String precip_1hr_in) {
		this.precip_1hr_in = precip_1hr_in;
	}

	/**
	 * @param precip_1hr_metric
	 *            the precip_1hr_metric to set
	 */
	public void setPrecip_1hr_metric(String precip_1hr_metric) {
		this.precip_1hr_metric = precip_1hr_metric;
	}

	/**
	 * @param precip_today_string
	 *            the precip_today_string to set
	 */
	public void setPrecip_today_string(String precip_today_string) {
		this.precip_today_string = precip_today_string;
	}

	/**
	 * @param precip_today_in
	 *            the precip_today_in to set
	 */
	public void setPrecip_today_in(String precip_today_in) {
		this.precip_today_in = precip_today_in;
	}

	/**
	 * @param precip_today_metric
	 *            the precip_today_metric to set
	 */
	public void setPrecip_today_metric(String precip_today_metric) {
		this.precip_today_metric = precip_today_metric;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @param icon_url
	 *            the icon_url to set
	 */
	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	/**
	 * @param forecast_url
	 *            the forecast_url to set
	 */
	public void setForecast_url(String forecast_url) {
		this.forecast_url = forecast_url;
	}

	/**
	 * @param history_url
	 *            the history_url to set
	 */
	public void setHistory_url(String history_url) {
		this.history_url = history_url;
	}

	/**
	 * @param ob_url
	 *            the ob_url to set
	 */
	public void setOb_url(String ob_url) {
		this.ob_url = ob_url;
	}

	/**
	 * @param nowcast
	 *            the nowcast to set
	 */
	public void setNowcast(String nowcast) {
		this.nowcast = nowcast;
	}

}
