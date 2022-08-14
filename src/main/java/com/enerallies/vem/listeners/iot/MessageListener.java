/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.listeners.iot;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;

/**
 * File Name : MessageListener 
 * MessageListener: will controls the publishing message states
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2.1.0
 * @date        25-07-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	25-07-2016			Nagarjuna Eerla		File Created
 *
 */


public class MessageListener extends AWSIotMessage {

	/**
	 * Constructor for MessageListener
	 * 
	 * @param topic
	 * @param qos
	 * @param payload
	 */
	
	public MessageListener(String topic, AWSIotQos qos, String payload) {
		super(topic, qos, payload);
	}

	@Override
    public void onSuccess() {
        // called when message publishing succeeded
    }

    @Override
    public void onFailure() {
        // called when message publishing failed
    }

    @Override
    public void onTimeout() {
        // called when message publishing timed out
    }
}
