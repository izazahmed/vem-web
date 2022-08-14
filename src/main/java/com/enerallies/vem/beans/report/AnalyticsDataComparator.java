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
 * File Name : AnalyticsDataComparator 
 * 
 * AnalyticsDataComparator: is a Comparator to compare analytic data attributes in sorted order
 *
 * @author Nagarjuna Eerla
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
 * 01	17-01-2017			Nagarjuna Eerla		File Created
 */
public class AnalyticsDataComparator implements Comparator<DataName>{

	@Override
	public int compare(DataName d1, DataName d2) {
		if(d1.getDataId() < d2.getDataId()){
            return -1;
		} else {
	        return 1;
		}
	}

}
