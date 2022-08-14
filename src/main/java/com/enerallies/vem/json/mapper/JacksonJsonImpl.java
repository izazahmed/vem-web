/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.json.mapper;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ser.StdSerializerProvider;

import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : JacksonJsonImpl 
 * 
 * JacksonJsonImpl: Manages Json conversion
 *
 * @author (Nagarjuna Eerla – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 03-08-2016		Nagarjuna Eerla		File Created
 *
 */
public class JacksonJsonImpl implements JsonInterface {

	private Logger logger = Logger.getLogger(JacksonJsonImpl.class);
	
	private ObjectMapper objectMapper;

	/**
	 * JacksonJsonImpl() : Constructor
	 */
	public JacksonJsonImpl() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.setDateFormat(new SimpleDateFormat(CommonConstants.DATE_DEFAULT_FORMAT));
        StdSerializerProvider sp = new StdSerializerProvider();
        sp.setNullValueSerializer(new NullToEmptyStringSerializer());
        objectMapper.setSerializerProvider(sp);
	}

	@Override
	public <T> T fromJson(InputStream is, Class<T> pojoClass) throws VEMAppException {
		T response = null;

		try {
			response = objectMapper.readValue(is, pojoClass);
		} catch (Exception exc) {
			throw new VEMAppException(ErrorCodes.INVALID_JSON, ErrorCodes.INVALID_JSON, logger, exc);
		}
		return response;
	}

	@Override
	public <T> T fromJson(String string, Class<T> pojoClass)  throws VEMAppException {
		T response = null;

		try {
			response = objectMapper.readValue(string, pojoClass);
			
		} catch (Exception exc) {
			throw new VEMAppException(ErrorCodes.INVALID_JSON, ErrorCodes.INVALID_JSON, logger, exc);
		}
		
		return response;
	}

	@Override
	public <T> String toJson(T request)  throws VEMAppException{
		String json = null;

		try {
			json = objectMapper.writeValueAsString(request);
		} catch (Exception exc) {
			throw new VEMAppException(ErrorCodes.INVALID_JSON, ErrorCodes.INVALID_JSON, logger, exc);
		}

		return json;
	}

}