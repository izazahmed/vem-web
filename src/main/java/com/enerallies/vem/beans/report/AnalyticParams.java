/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.report;

import java.util.ArrayList;
import java.util.List;

/**
 * File Name : AnalyticParams 
 * 
 * AnalyticParams: is a bean for Analytic params
 *
 * @author Naagrjuna Eerla
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        22-11-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	05-12-2016			Nagarjuna Eerla		File Created
 */
public class AnalyticParams {
	
	private String label;
	private int id;
	private List<KeyValue> analyticParams =  new ArrayList<>();
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<KeyValue> getAnalyticParams() {
		return analyticParams;
	}
	public void setAnalyticParams(List<KeyValue> analyticParams) {
		this.analyticParams = analyticParams;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
