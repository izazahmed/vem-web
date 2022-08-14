/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */

package com.enerallies.vem.validators.role;

import org.json.simple.JSONObject;

/**
 * File Name : RolesValidator 
 * 
 * RolesValidator: is used for server side validations of roles.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 08-08-2016		Goush Basha		    File Created.
 * 30-08-2016       Goush Basha			Sprint-2 Suggestions/Changes.
 * 
 */

public class RolesValidator {
	
	//Used for not to create the object for this class.
	private RolesValidator(){}
	
	/**
	 * validatePermissions validator Validate whether the 
	 * PermissionList contains any permissions or not.
	 *  
	 * @param permissions
	 * @param isCSO
	 * @return boolean
	 */
	public static boolean validatePermissions(JSONObject permissions,Integer isCSO){
		
		if(isCSO == 0)
			return permissions.keySet().iterator().hasNext();
		else
			return true;
		
	}
	
	/**
	 * validateRoleFlags validator Validate whether the isEAI,
	 * isSuperEai and isCso flag
	 * contains other than 1 and 0 digits.
	 *  
	 * @param isEAI
	 * @return boolean
	 */
	public static boolean validateRoleFlags(int isEAI){
		
		return isEAI==0 || isEAI==1;
		
	}

}
