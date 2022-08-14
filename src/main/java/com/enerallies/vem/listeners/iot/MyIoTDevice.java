/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.listeners.iot;

import java.util.Random;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotDeviceProperty;
/**
 * File Name : MyIoTDevice 
 * MyIoTDevice: create ashynchronously device
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2.1.0
 * @date        28-07-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	28-07-2016			Nagarjuna Eerla		File Created
 *
 */
public class MyIoTDevice extends AWSIotDevice{

	public MyIoTDevice(String thingName) {
		super(thingName);
	}
	
	@AWSIotDeviceProperty
	private float windSpeed;
	
    @AWSIotDeviceProperty
    private float roomTemperature;

    
    public float getWindSpeed() {
        // 1. Read the actual wind speed from the thermostat
        Random rand = new Random();
        float minWindSpeed = 10.0f;
        float maxWindSpeed = 55.0f;
        float reportedWindSpeed = rand.nextFloat() * (maxWindSpeed - minWindSpeed) + minWindSpeed;

        // 2. (optionally) update the local copy
        this.windSpeed = reportedWindSpeed;
        return this.windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        // no-op as wind speed is a read-only property. It's not required
        // to have this setter.
    }
    
    public float getRoomTemperature() {
        // 1. Read the actual room temperature from the thermostat
        Random rand = new Random();
        float minTemperature = 20.0f;
        float maxTemperature = 85.0f;
        float reportedTemperature = rand.nextFloat() * (maxTemperature - minTemperature) + minTemperature;

        // 2. (optionally) update the local copy
        this.roomTemperature = reportedTemperature;
        return this.roomTemperature;
    }

    public void setRoomTemperature(float desiredTemperature) {
        // no-op as room temperature is a read-only property. It's not required
        // to have this setter.
    }
}
