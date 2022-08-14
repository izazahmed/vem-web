package com.enerallies.vem.daoimpl.alert;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.dao.alert.AlertEmailDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : AlertEmailDaoImpl 
 * AlertEmailDaoImpl: Its an implementation class for AlertEmailDao interface.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        27-03-2017
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 27-03-2017		Bhoomika Rabadiya   File Created.
 *
 */

@Component
public class AlertEmailDaoImpl implements AlertEmailDao {
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(AlertEmailDaoImpl.class);
	
	/** Data source instance */
	private DataSource dataSource;
	
	/** JDBC Template instance */
	private JdbcTemplate jdbcTemplate;
	
	private static final String FETCH_USER_ALERTS="sp_fetch_user_mail_alerts"; 
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}

	@Override
	public Response alertList(AlertRequest alertRequest) throws VEMAppException {
		logger.info("[BEGIN] [alertList] [Alert Email DAO LAYER]");
		
		Response response = new Response();
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		JSONParser jsonParser=new JSONParser();
		
		try {
			
				simpleJdbcCall = new SimpleJdbcCall(dataSource);
			    simpleJdbcCall.withProcedureName(FETCH_USER_ALERTS);
				
				outParameters = simpleJdbcCall.execute();
				Iterator<Entry<String, Object>> itr = outParameters.entrySet().iterator();
				
				org.json.JSONObject alertsData = new org.json.JSONObject();
				
				while (itr.hasNext()) {
			        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
			        String key = entry.getKey();
			        
			        if (key.equals(CommonConstants.RESULT_SET_1)) {
			        	Object value = (Object) entry.getValue();
			        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
			        	org.json.JSONObject innerJSON;
			        	String innerTemp;
			        	String outerArry[];
			        	String innerArry[];
			        	for (int i = 0; i<tempAry.length(); i++) {
			        		innerJSON=(org.json.JSONObject)tempAry.getJSONObject(i);
			        		if (!innerJSON.isNull("actionItems")) {
				        		innerTemp = innerJSON.getString("actionItems");
				        		outerArry = innerTemp.split("---");
				        		org.json.JSONArray userActionList=new org.json.JSONArray();
				        		if (outerArry.length > 0) {
				        			for (int arryCount = 0; arryCount<outerArry.length; arryCount++) { 
										try {
											innerArry = outerArry[arryCount].split("~:~");
											if(innerArry.length>0){
												JSONObject userActionInfoData=new JSONObject();
												userActionInfoData.put("itemId", Integer.parseInt(innerArry[0]));
												userActionInfoData.put("itemName", innerArry[1]);
												userActionInfoData.put("itemStatus", Integer.parseInt(innerArry[2]));
												userActionList.put(userActionInfoData);
											}
										} catch(ArrayIndexOutOfBoundsException ae) {
											logger.error(""+ae);
											userActionList.put("");
										} catch(NumberFormatException ae){
											logger.error(""+ae);
											userActionList.put("");
										}
									}
					        	innerJSON.put("actionItems", userActionList);
				        		} else {
				        			innerJSON.put("actionItems", userActionList);
				        		}
				        	} else {
				        		org.json.JSONArray userActionList=new org.json.JSONArray();
				        		JSONObject userActionInfoData=new JSONObject();
								userActionInfoData.put("itemId", 0);
								userActionInfoData.put("itemName", "");
								userActionInfoData.put("itemStatus", 0);
								userActionList.put(userActionInfoData);
				        		innerJSON.put("actionItems", userActionList);
				        	}
			        	}
			        	JSONArray resultAry=(JSONArray)jsonParser.parse(tempAry.toString());			        	
			        	alertsData.put("alertsInfo", resultAry);
					}
				}
				response.setCode(ErrorCodes.SUCCESS_ALERT_CUSTOMER_FETCH);
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setData(alertsData);
		
		} catch (Exception e) {
			logger.error("", e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_FETCH);
			throw new VEMAppException("Internal Error occured at DAO layer");
		}
		logger.info("[END] [alertList] [Alert Email DAO LAYER]");
		return response;
	}
}
