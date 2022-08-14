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
import com.amazonaws.services.iot.client.AWSIotTopic;
/**
 * File Name : TopicListener 
 * TopicListener will handles all the subscribed topics
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

public class TopicListener extends AWSIotTopic{

	/**
	 * Constructor to create topic listener instance
	 * 
	 * @param topic
	 * @param qos
	 */
	public TopicListener(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AWSIotMessage message) {
       // System.out.println(System.currentTimeMillis() + ": <<< " + message.getStringPayload());
        //Please dont use system.out.println 
    }
	
}
