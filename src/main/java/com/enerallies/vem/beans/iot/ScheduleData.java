package com.enerallies.vem.beans.iot;


/**
 * File Name : ScheduleData 
 * 
 * ScheduleData: is for transfer label and id between different modules 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        02-12-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	02-12-2016		Rajashekharaiah Muniswamy		File Created
  */
public class ScheduleData {

	/**This property is used to hold scedule id*/
	private int id;
	
	/**This property is used to hold scedule name*/
	private String label;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
