package com.enerallies.vem.jobschedule;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.enerallies.vem.listeners.xcspec.RestClient;

@Component(value="loginApiJob")
public class LoginApiJob {

	RestClient restClient = new RestClient();
	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(LoginApiJob.class);

	public void printMessage(){
		logger.info("Login API Job has been started");
		RestClient.authStringEnc = restClient.loginToGetauthStringEnc();
	}
}
