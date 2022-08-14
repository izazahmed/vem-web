/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.report;

import java.util.LinkedList;
import java.util.List;

/**
 * File Name : ReportResponse 
 * 
 * ReportResponse: is the bean for report response
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
 * 01	23-11-2016			Nagarjuna Eerla		File Created
 */
public class ReportResponse {

	private List<Report> reports = new LinkedList<>();
	private List<DataName> alerts = new LinkedList<>();
	private List<MapData> mapData = new LinkedList<>();
	
	public List<Report> getReports() {
		return reports;
	}
	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
	public List<DataName> getAlerts() {
		return alerts;
	}
	public void setAlerts(List<DataName> alerts) {
		this.alerts = alerts;
	}
	public List<MapData> getMapData() {
		return mapData;
	}
	public void setMapData(List<MapData> mapData) {
		this.mapData = mapData;
	}
}
