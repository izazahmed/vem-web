package com.enerallies.vem.util.iot;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

/**
 * File Name : XCSPECDataParser 
 * 
 * XCSPECDataParser: is a JSON data parser to VEM complaint data  
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        16-02-2017
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	16-02-2017		Rajashekharaiah Muniswamy		File Created
 */

@Component(value="xcspecDataParser")
public class XCSPECDataParser {

	// Getting logger
	private static final Logger logger 	= Logger.getLogger(XCSPECDataParser.class);
	
	public JSONObject parse(JSONObject inObj){
		
		JSONObject outObj = new JSONObject();
		
		try {
			if(inObj.containsKey("os")){
				String inOSValue = (String)inObj.get("os");
				String outOSValue;
				if(inOSValue.equals("h")){
					outOSValue = "HEAT";
				}else if (inOSValue.equals("c")) {
					outOSValue = "COOL";
				}else{
					outOSValue = "OFF";
				}
				outObj.put("op_state", outOSValue);
			}else{
				outObj.put("op_state", null);
			}

			if(inObj.containsKey("hs")){
				String inHSValue = (String)inObj.get("hs");
				String outHSValue = inHSValue;
				outObj.put("heat_set", outHSValue);
			}else{
				outObj.put("heat_set", null);
			}

			if(inObj.containsKey("cs")){
				String inCSValue = (String)inObj.get("cs");
				String outCSValue = inCSValue;
				outObj.put("cool_set", outCSValue);
			}else{
				outObj.put("cool_set", null);
			}

			if(inObj.containsKey("zt")){
				String inZTValue = (String)inObj.get("zt");
				String outZTValue = inZTValue;
				outObj.put("zone_temp", outZTValue);
			}else{
				outObj.put("zone_temp", null);
			}

			if(inObj.containsKey("fs")){
				String inFSValue = (String)inObj.get("fs");
				String outFSValue;
				if(inFSValue.equals("on")){
					outFSValue = "ON";
				}else{
					outFSValue = "IDLE";
				}
				outObj.put("fan_state", outFSValue);
			}else{
				outObj.put("fan_state", null);
			}

			if(inObj.containsKey("h")){
				String inHValue = (String)inObj.get("h");
				String outHValue;
				if(inHValue.equals("e")){
					outHValue = "ENABLE";
				}else{
					outHValue = "DISABLE";
				}
				outObj.put("temp_hold", outHValue);
			}else{
				outObj.put("temp_hold", null);
			}

			if(inObj.containsKey("om")){
				String inOMValue = (String)inObj.get("om");
				String outOMValue;
				if(inOMValue.equals("h")){
					outOMValue = "HEAT";
				}else if (inOMValue.equals("c")) {
					outOMValue = "COOL";
				}else if (inOMValue.equals("off")) {
					outOMValue = "OFF";
				}else{
					outOMValue = "AUTO";
				}
				outObj.put("tstat_mode", outOMValue);
			}else{
				outObj.put("tstat_mode", null);
			}
			
			if(inObj.containsKey("mt")){
				String inMTValue = (String)inObj.get("mt");
				String outMTValue = inMTValue;
				outObj.put("tstat_msg", outMTValue);
			}else{
				outObj.put("tstat_msg", null);
			}
			
			if(inObj.containsKey("cl")){
				JSONObject clObj = (JSONObject)inObj.get("cl");

				String inHValue = (String)clObj.get("h");
				Long intDValue = (Long)clObj.get("d");
				String inDValue = intDValue.toString();

				String outHValue = inHValue;
				String outDValue = inDValue;
				
				JSONObject clockOut = new JSONObject();
				clockOut.put("current_time", outHValue);
				clockOut.put("current_day", outDValue);
				
				outObj.put("tstat_clock", clockOut);
			}else{
				outObj.put("tstat_clock", null);
			}
			
		} catch (Exception e) {
			logger.error("Error found while parsing input data", e);
		}
		
		JSONObject finalObj  = new JSONObject();
		finalObj.put("data", outObj);
		
		return finalObj;
	}

	public String setTempConverter(String s){
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj =  (JSONObject)parser.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		return s;
	}

	public static void main(String[] args) {
		String inputData = "{\"os\":\"off\",\"hs\":\"67.5\",\"cs\":\"82.3\",\"zt\":\"76.6\",\"fs\":\"on\",\"fm\":\"on\",\"h\":\"e\",\"rs\":[0,1,0,1,1,1,1,0,0,0,0,0,0,0,0,0],\"rc\":[\"B\",\"O\",\"G\",\"W\",\"W2\",\"Y\",\"Y2\"],\"cl\":{\"h\":\"15:08\",\"d\":\"4\"},\"om\":\"h\",\"mt\":\"iot test\",\"me\":\"off\",\"lk\":\"p1\",\"pr\":[[0,0,4,0,0,360,156],[0,0,4,0,104,39937,3],[0,0,4,0,104,39937,5],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,1,0,4,0],[0,0,4,40,5,636,0],[0,0,4,40,5,1148,0],[0,0,4,40,5,1660,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,1],[0,0,4,0,0,0,0],[0,0,4,0,0,512,0],[0,0,4,0,0,0,0],[0,0,4,0,5,0,48],[0,0,4,112,100,29793,101],[0,0,4,58,48,8754,44],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[0,0,4,0,0,0,0],[1,0,4,0,0,360,140],[1,0,4,0,104,35841,3],[1,0,4,0,104,35841,5],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,143,0,143,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,122,0,1,0],[1,0,4,10,0,0,0],[1,0,4,101,99,29524,116],[1,0,4,34,58,8827,115],[1,0,4,125,125,32125,102],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0],[1,0,4,0,0,0,0]],\"sm\":\"7d\",\"tc\":\"0\",\"tv\":[\"1.8\",\"5.16\"],\"rt\":\"0.0\",\"re\":\"off\",\"dl\":\"off\",\"ss\":-59,\"tu\":\"c\",\"m\":{\"h\":\"on\",\"c\":\"on\",\"a\":\"off\",\"eh\":\"off\"},\"r\":\"1\"}";
		
		JSONParser parser = new JSONParser();
		try {
			JSONObject inObj = (JSONObject)parser.parse(inputData);
			
			JSONObject obj =  new XCSPECDataParser().parse(inObj);
			
			System.out.println(obj);
		} catch (ParseException e) {
			logger.error("Error found while parsing input data", e);
		}
	}
}
