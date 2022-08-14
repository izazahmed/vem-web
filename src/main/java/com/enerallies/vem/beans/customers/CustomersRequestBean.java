package com.enerallies.vem.beans.customers;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * File Name : CustomersRequestBean 
 * 
 * CustomersRequestBean: is bean class used to sent/validate 
 * the request params data passing from view to controller layer.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        07-09-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 07-09-2016		Arun Singh		    File Created.
 * 
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class CustomersRequestBean {
	
	
	private String customerId;
	private JSONObject customerObj;
	private String customers;
	private int isSuperAdmin;
	private String status;
	private String userId;
	private String roleName;
	
	public String getCustomerId() {
		return customerId;
	}
	public JSONObject getCustomerObj() {
		return customerObj;
	}
	public String getCustomers() {
		return customers;
	}
	public int getIsSuperAdmin() {
		return isSuperAdmin;
	}
	public String getStatus() {
		return status;
	}
	public String getUserId() {
		return userId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public void setCustomerObj(JSONObject customerObj) {
		this.customerObj = customerObj;
	}
	public void setCustomers(String customers) {
		this.customers = customers;
	}
	public void setIsSuperAdmin(int isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
		
}
