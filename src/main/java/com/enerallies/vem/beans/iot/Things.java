/**
 * 
 */
package com.enerallies.vem.beans.iot;

import java.util.List;

/**
 * File Name : Things 
 * 
 * Things: is for transfer things related data between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        31-08-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	01-09-2016		Rajashekharaiah Muniswamy		File Created
 */
public class Things {

	List<ThingResponse> thingList;
	
	int permission;

	


	/**
	 * @return the permission
	 */
	public int getPermission() {
		return permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(int permission) {
		this.permission = permission;
	}

	/**
	 * @return the things
	 */
	public List<ThingResponse> getThings() {
		return thingList;
	}

	/**
	 * @param things the things to set
	 */
	public void setThings(List<ThingResponse> things) {
		this.thingList = things;
	}
}
