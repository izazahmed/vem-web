/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.json.mapper;

import java.io.InputStream;
import com.enerallies.vem.exceptions.VEMAppException;
/**
 * File Name : JsonInterface 
 * 
 * JsonInterface: Json Mapper interface
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
public interface JsonInterface {

	/**
	 * Converts JSON stream to POJO object
	 * 
	 * @param is
	 * @param pojoClass
	 * @return
	 * @throws VEMAppException
	 */
	public <T> T fromJson(InputStream is, Class<T> pojoClass) throws VEMAppException;

	/**
	 * Converts JSON string to POJO object
	 * 
	 * @param string
	 * @param pojoClass
	 * @return
	 * @throws VEMAppException
	 */
	public <T> T fromJson(String string, Class<T> pojoClass) throws VEMAppException;

	/**
	 * Converts POJO object to JSON string
	 * 
	 * @param request
	 * @return
	 * @throws VEMAppException
	 */
	public <T> String toJson(T request) throws VEMAppException;

}
