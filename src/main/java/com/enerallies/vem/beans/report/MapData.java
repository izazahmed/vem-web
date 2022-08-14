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
 * File Name : MapData 
 * 
 * MapData: is the bean for final map data
 *
 * @author Naagrjuna Eerla
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        01-12-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	01-12-2016			Nagarjuna Eerla		File Created
 */
public class MapData {
	
	private String label;
	private List<ReportMap> value = new LinkedList<>();
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<ReportMap> getValue() {
		return value;
	}
	public void setValue(List<ReportMap> value) {
		this.value = value;
	}
}
