/* This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.report;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

/**
 * File Name : HVACUsage 
 * 
 * HVACUsage: is a bean for HAVAC Usage report
 *
 * @author Naagrjuna Eerla
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        07-12-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	07-12-2016			Nagarjuna Eerla		File Created
 */
public class HVACUsage {
	
	private List<String> categories = new LinkedList<>();
	private List<DrillDown> data = new LinkedList<>();
	
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	public List<DrillDown> getData() {
		return data;
	}
	public void setData(List<DrillDown> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
