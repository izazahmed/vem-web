package com.enerallies.vem.jobschedule;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.enerallies.vem.beans.iot.Thing;
import com.enerallies.vem.dao.iot.IoTDao;
import com.enerallies.vem.listeners.iot.awsiot.InitApp;

@Component(value="liveDataJob")
public class LiveDataJob{

	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(LiveDataJob.class);

	//@Autowired
	//private JobDaoImpl jobDaoImpl;
	
	@Autowired
	private IoTDao iotDao;

	protected void executeInternal(){
	logger.info("Live data Job has been started"+iotDao);
	try {
		/** Get thing list from DB */
		List<Thing> thingList = iotDao.getThingList();
		if(thingList!=null){
			if(InitApp.deviceIdList!=null){
				InitApp.deviceIdList.clear();
			}

			InitApp.deviceIdList = thingList;

		}

	} catch (SQLException e) {
		logger.error("Error while fetching device list :"+e);
	}catch (Exception e) {
		logger.error("Error while starting the timer of fetching device list :"+e);
	}

	/**scheduler for subscribe and Start querying data from xcspec and publish to aws iot */
	InitApp.initialize();
		
	}

}
