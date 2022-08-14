/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.weather.beans;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


/**
 * File Name : COImage 
 * COImage Class is used hold image information
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
public class COImage {

	/**
	 * Declare String url, this property is used to hold the url
	 */
	private String url;

	/**
	 * Declare String title, this property is used to hold the title
	 */
	private String title;

	/**
	 * Declare String link, this property is used to hold the link
	 */
	private String link;

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

}
