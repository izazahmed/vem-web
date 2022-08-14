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
 * File Name : CategoryAnalytics 
 * 
 * CategoryAnalytics: is the bean for Analytics
 *
 * @author Naagrjuna Eerla
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        13-02-2017
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	13-02-2017			Nagarjuna Eerla		File Created
 */
public class CategoryAnalytics {
	
	private List<String> categories = new LinkedList<>();
	private List<DataNameAnalytics> data = new LinkedList<>();
	
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	public List<DataNameAnalytics> getData() {
		return data;
	}
	public void setData(List<DataNameAnalytics> data) {
		this.data = data;
	}
}
