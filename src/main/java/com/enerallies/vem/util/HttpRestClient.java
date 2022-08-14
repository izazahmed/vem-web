/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * File Name : HttpRestClient 
 * HttpRestClient Class is used call RESTful Calls using HttpURLConnection
 *
 * @author (Y Chenna Reddy – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     1.0
 * @date        27-07-2016
 *
 * MOD HISTORY
 * 
 * DATE				USER             		COMMENTS
 * 
 * 27-07-2016		Y Chenna Reddy			File Created
 *
 */
public class HttpRestClient {

	/**
	 * Method Name : sendGet
	 * This method is used to call RESTful calls using Request method GET
	 * 
	 * @param urlString
	 * @return String
	 * @throws Exception
	 */
	public String sendGet(String urlString) throws Exception {
		
		// initialize HttpURLConnection
		HttpURLConnection httpUrlConnection = null;
		// initialize BufferedReader
		BufferedReader bufferedReader = null;
		try {
			// Instantiate URL
			URL url = new URL(urlString);
			// Instantiate HttpURLConnection
			httpUrlConnection = (HttpURLConnection) url.openConnection();

			// add Request Method (optional default is GET)
			httpUrlConnection.setRequestMethod("GET");
			// add Do Output (optional default is false)
			httpUrlConnection.setDoOutput(true);
			// add Do Do Input (optional default is true)
			httpUrlConnection.setDoInput(true);
			// add Do Use Caches (optional)
			httpUrlConnection.setUseCaches(false);
			// add request header
			httpUrlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			// getting the response code
			int responseCode = httpUrlConnection.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			
			//Instantiate StringBuilder to hold the response
			StringBuilder response = new StringBuilder();
			
			// if response code is 200, means the request has succeeded. The information returned with the response
			if (responseCode == 200) {
				// instantiate BufferedReader to read the response
				bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
				String inputLine;

				while ((inputLine = bufferedReader.readLine()) != null) {
					response.append(inputLine);
				}
			}
			// log result
			System.out.println(response.toString());
			// return response string
			return response.toString();
		} catch (SocketTimeoutException se) {
			System.out.println("SocketTimeoutException in hitHttp of HTTPRequest" + se);
			throw new Exception(se.toString());
		} catch (IOException ioe) {
			System.out.println("IOException in hitHttp of HTTPRequest" + ioe);
			throw new Exception(ioe.toString());
		} catch (Exception exception) {
			System.out.println("Exception in hitHttp of HTTPRequest" + exception);
			throw new Exception(exception.toString());
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (httpUrlConnection != null) {
				httpUrlConnection.disconnect();
			}
		}

	}

	/**
	 * Method Name: sendPost
	 * This method is used to call RESTful calls using Request method POST
	 * 
	 * @param urlString
	 * @param urlParameters
	 * @return String
	 * @throws Exception
	 */
	public String sendPost(String urlString, String urlParameters) throws Exception {
		
		// initialize HttpURLConnection
		HttpURLConnection httpUrlConnection = null;
		// initialize BufferedReader
		BufferedReader bufferedReader = null;
		
		try {
			// Instantiate URL
			URL url = new URL(urlString);
			// Instantiate HttpURLConnection
			httpUrlConnection = (HttpURLConnection) url.openConnection();

			// add Request Method (optional default is GET)
			httpUrlConnection.setRequestMethod("POST");
			// add Do Output (optional default is false)
			httpUrlConnection.setDoOutput(true);
			// add Do Do Input (optional default is true)
			httpUrlConnection.setDoInput(true);
			// add Do Use Caches (optional)
			httpUrlConnection.setUseCaches(false);
			// add request header
			httpUrlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			//Instantiate DataOutputStream
			DataOutputStream dataOutputStream = new DataOutputStream(httpUrlConnection.getOutputStream());
			// write request data 
			dataOutputStream.writeBytes(urlParameters);
			dataOutputStream.flush();
			dataOutputStream.close();

			// getting the response code
			int responseCode = httpUrlConnection.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			
			//Instantiate StringBuilder to hold the response
			StringBuilder response = new StringBuilder();
			
			// if response code is 200, means the request has succeeded. The information returned with the response
			if (responseCode == 200) {
				// instantiate BufferedReader to read the response
				bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
				String inputLine;

				while ((inputLine = bufferedReader.readLine()) != null) {
					response.append(inputLine);
				}
			}
			// log result
			System.out.println(response.toString());
			// return response string
			return response.toString();
		} catch (SocketTimeoutException se) {
			System.out.println("SocketTimeoutException in hitHttp of HTTPRequest" + se);
			throw new Exception(se.toString());
		} catch (IOException ioe) {
			System.out.println("IOException in hitHttp of HTTPRequest" + ioe);
			throw new Exception(ioe.toString());
		} catch (Exception exception) {
			System.out.println("Exception in hitHttp of HTTPRequest" + exception);
			throw new Exception(exception.toString());
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (httpUrlConnection != null) {
				httpUrlConnection.disconnect();
			}
		}
	}	
}
