/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.report;

import java.util.Comparator;

/**
 * File Name : AnalyticParamComparator 
 * 
 * AnalyticParamComparator: is a Comparator to compare analytic params in sorted order
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
 * 01	11-01-2017			Nagarjuna Eerla		File Created
 */
public class AnalyticParamComparator implements Comparator<TemperatureSetpoint> {

	@Override
	public int compare(TemperatureSetpoint t1, TemperatureSetpoint t2) {
		 if(t1.getParamId() < t2.getParamId()){
	            return -1;
		 } else {
            return 1;
		 }
	}

}
