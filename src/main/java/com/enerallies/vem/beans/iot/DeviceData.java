/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.beans.iot;

/**
 * File Name : DeviceData 
 * 
 * DeviceData is the bean to transfer Thermostat data to IoT 
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2.1.0
 * @date        22-07-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	22-07-2016			Nagarjuna Eerla		File Created
 *
 */

public class DeviceData {

		private String name;
		private String serialNumber;
		private String clickType;
		private String batteryVoltage;
	
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the serialNumber
		 */
		public String getSerialNumber() {
			return serialNumber;
		}
		/**
		 * @param serialNumber the serialNumber to set
		 */
		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}
		/**
		 * @return the clickType
		 */
		public String getClickType() {
			return clickType;
		}
		/**
		 * @param clickType the clickType to set
		 */
		public void setClickType(String clickType) {
			this.clickType = clickType;
		}
		/**
		 * @return the batteryVoltage
		 */
		public String getBatteryVoltage() {
			return batteryVoltage;
		}
		/**
		 * @param batteryVoltage the batteryVoltage to set
		 */
		public void setBatteryVoltage(String batteryVoltage) {
			this.batteryVoltage = batteryVoltage;
		}

		
}
