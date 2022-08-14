package com.enerallies.vem.daoimpl.alert;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
 
import javax.sql.DataSource;
 
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
 
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.dao.alert.AlertDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ErrorCodes;


/**
 * File Name : AlertDaoImpl 
 * AlertDaoImpl: Its an implementation class for AlertDAO interface.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-08-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 16-09-2016		Raja		    File Created.
 * 20-09-2016		Raja		    alertList() method has added.
 *
 */

@Component
public class AlertDaoImpl implements AlertDao {
       
       /* Get the logger object*/
       private static final Logger logger = Logger.getLogger(AlertDaoImpl.class);
       private DataSource dataSource;
       private JdbcTemplate jdbcTemplate;      
       private static final String FETCH_ALERT_LIST="call sp_list_alert(?,?,?,?)"; 
       private static final String FETCH_NEW_ALERT_COUNT="sp_fetch_new_alert_count";
       private static final String FETCH_CUSTOMER_ALERTS = "sp_fetch_customer_alerts";
       private static final String FETCH_CUSOTMER_CONFIG = "sp_customer_config"; 
       private static final String SP_SAVE_ALERT_CONFIG = "sp_save_alert_config";
       private static final String SP_STORE_DEVICE_ALERTS = "sp_store_device_alerts";
       private static final String UPDATE_ALERT_STATUS="sp_update_alertstatus";
       private static final String FETCH_ALERTS_ACTION_ITEMS="sp_fetch_customer_alerts_action_list";
       private static final String UPDATE_ALERT_ACTION_ITEMS="sp_update_alert_actionitems";
       private static final String FETCH_DEVICE_ALERTS="sp_fetch_device_alerts";
       private static final String DELETE_ALERT_CUSTOMER_CONFIG="sp_delete_customer_config";
       private static final String FETCH_DASHBOARD_ALERTS="sp_fetch_dashboard_alerts";
       
       @Override
       public void setDataSource(DataSource dataSource) {
              this.dataSource = dataSource;
           this.jdbcTemplate = new JdbcTemplate(this.dataSource);
       }
       
       /**
       * alertList dao layer,Interacts with Database to get List of alerts.
       * @param alertRequest object of AlertRequest bean, accepts customer id
       * @return Response
       * @throws VEMAppException
       */
       @Override
       public Response alertList(AlertRequest alertRequest) throws VEMAppException {
              
              logger.info("[BEGIN] [alertList] [Alert DAO LAYER]");
              Response response =new Response();
              
              try {
                     logger.debug("[DEBUG] Executing "+FETCH_ALERT_LIST);
                     
                     //Executing the procedure.
                     response= jdbcTemplate.query(FETCH_ALERT_LIST,
                                  new Object[]{alertRequest.getCustomerId(),
                                                       alertRequest.getUserId(),
                                                       alertRequest.getIsSuper(),
                                                       alertRequest.getIsEai()
                                                       
                         },  
                           new ResultSetExtractor<Response>() {
                           @Override
                           public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
                                  JSONArray resultAry=new JSONArray();
                                  Response response=new Response();
                                  ResultSetMetaData rsmd=rs.getMetaData();
                                  int columnCount=rsmd.getColumnCount();
                                  while(rs.next()){
                                         JSONObject tempData=new JSONObject();
                                         for(int i=1;i<=columnCount;i++){
                                                tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
                                         }
                                         
                                     tempData.put("alertStatus",Integer.parseInt((tempData.get("alertStatus")==null?"0":tempData.get("alertStatus").toString())));
                                     resultAry.add(tempData);
                                  }
                                  if(resultAry.isEmpty() || resultAry.size()<=0){
                                         response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                                         response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
                                  }else{
                                         response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                                         response.setCode(ErrorCodes.SUCCESS_ALERT_LIST_FETCH);
                                  }
                                  response.setData(resultAry);
                                  return response;
                           }
                     });
                     
              } catch (Exception e) {
                     logger.error("",e);
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
                     throw new VEMAppException("Internal Error occured at DAO layer");
              }
              logger.info("[END] [alertList] [Alert DAO LAYER]");
              return response;
       }
 
       @Override
       public Response getConfig(AlertRequest alertRequest) throws VEMAppException {
              
              logger.info("[BEGIN] [getConfig] [Alert DAO LAYER]");
              Response response =new Response();
              
              try {
                     
                     SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(FETCH_CUSOTMER_CONFIG);
                     Map<String, Object> inParamMap = new HashMap<String, Object>();
                     inParamMap.put("customerId", alertRequest.getCustomerId());
                     inParamMap.put("alertId", alertRequest.getAlertId());
                     inParamMap.put("in_user_id", alertRequest.getUserId());
                     inParamMap.put("in_eai", alertRequest.getIsEai());
                     inParamMap.put("in_super", alertRequest.getIsSuper());
                     
                     logger.debug("[DEBUG] Executing procedure "+FETCH_CUSOTMER_CONFIG+" params "+inParamMap);
                     
                     SqlParameterSource in = new MapSqlParameterSource(inParamMap);
                     Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
                     Iterator<Entry<String, Object>> itr = simpleJdbcCallResult.entrySet().iterator();
                     JSONObject responseObj=new JSONObject();
                     JSONParser parser=new JSONParser();
                     
                     while (itr.hasNext()) {
                             Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
                             String key = entry.getKey();
                             
                             if(key.equals(CommonConstants.RESULT_SET_1))
                             {
                                  Object value = (Object) entry.getValue();
                                  org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
                                  JSONArray resultAry=(JSONArray)parser.parse(tempAry.toString());
                                         responseObj.put("resultAry", resultAry);
                                  }
                             if(key.equals(CommonConstants.RESULT_SET_2))
                             {
                                  Object value = (Object) entry.getValue();
                                  org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
                                  JSONArray tempList=(JSONArray)parser.parse(tempAry.toString());
                                  responseObj.put("actionList", tempList);
                                  }
                             if(key.equals(CommonConstants.RESULT_SET_3))
                             {
                                  Object value = (Object) entry.getValue();
                                  org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
                                  JSONArray tempList=(JSONArray)parser.parse(tempAry.toString());
                                  responseObj.put("priorityList", tempList);
                                  }
                       }
                        
                        response.setData(responseObj);
                        response.setCode(ErrorCodes.SUCCESS_ALERT_LIST_FETCH);
                        response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                     
              } catch (Exception e) {
                     logger.error("",e);
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_ALERT_GETCONFIG);
                     throw new VEMAppException("Internal Error occured at DAO layer");
              }
              logger.info("[END] [customerList] [Alert DAO LAYER]");
              return response;
       }
       
       @Override
       public Response saveConfig(AlertRequest alertRequest) throws VEMAppException {
              
              logger.info("[BEGIN] [saveConfig] [Alert DAO LAYER]");
              
              int statusFlag = 0;
              String errorMsg = "";
              SimpleJdbcCall simpleJdbcCall;
              Map<String,Object> outParameters = null;
              List<String> newSitesList = null;
              StringBuilder newSitesListStr = new StringBuilder();
              Response response = new Response();
              
              try {
                     simpleJdbcCall = new SimpleJdbcCall(dataSource);
                  simpleJdbcCall.withProcedureName(SP_SAVE_ALERT_CONFIG);
                  
                  Map<String,Object> inputParams=new HashMap<>();
                     inputParams.put("customer_config_id", alertRequest.getAlertConfigId() ); 
                     inputParams.put("in_alert_name", alertRequest.getAlert() );
                     inputParams.put("in_alert_type",alertRequest.getAlertType());
                     inputParams.put("in_customer_id", alertRequest.getCustomerId());
                     inputParams.put("in_alert_priority", alertRequest.getPriority() );
                     inputParams.put("in_alert_parameter", alertRequest.getParameterId() );
                     inputParams.put("in_alert_parameter_unit", alertRequest.getParameterUnit());
                     inputParams.put("in_alert_status", alertRequest.getAlertStatus());
                     inputParams.put("in_user_id", alertRequest.getUserId());
                     inputParams.put("in_eai_admin", alertRequest.getIsEai());
                     inputParams.put("in_super_admin", alertRequest.getIsSuper());
                     
                     String listString = "";
 
                     for (String s : alertRequest.getUserAction())
                     {
                         listString += s + ",";
                     }
                     
                     inputParams.put("action_list", listString );
                     inputParams.put("alertId", alertRequest.getAlertId());
                     logger.info("executing "+SP_SAVE_ALERT_CONFIG+" with params "+inputParams);
                     outParameters = simpleJdbcCall.execute(inputParams);
                     
               statusFlag = (int) outParameters.get("out_flag");
               if(statusFlag == 0){
                           response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                                  response.setCode(ErrorCodes.SUCCESS_ALERT_CONFIG_SAVE);
                           }else{
                                  response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                                  response.setCode(ErrorCodes.ERROR_ALERT_CONFIG_SAVE);
                           }
               
              }catch (Exception e) {
                     logger.error("",e);
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_ALERT_CONFIG_SAVE);
                     throw new VEMAppException("Internal Error occured at DAO layer");
              }
              logger.info("[END] [saveConfig] [Alert DAO LAYER]");
              return response;
       
       }
 
       @Override
       public Response getCustomerAlerts(AlertRequest alertRequest) throws VEMAppException {
              logger.info("[BEGIN] [getCustomerAlerts] [Alert DAO LAYER]");
              Response response = new Response();
              SimpleJdbcCall simpleJdbcCall;
              Map<String,Object> outParameters = null;
              JSONObject responseObj=new JSONObject();
              JSONParser jsonParser=new JSONParser();
              try {
                     
                           simpleJdbcCall = new SimpleJdbcCall(dataSource);
                         simpleJdbcCall.withProcedureName(FETCH_CUSTOMER_ALERTS);
                         Map<String,Object> inputParams=new HashMap<>();
                         
                           inputParams.put("in_alert_status", alertRequest.getAlertStatus());
                         inputParams.put("in_user_id", alertRequest.getUserId());
                           inputParams.put("in_customers", alertRequest.getCustomers());
                           inputParams.put("in_super_admin", alertRequest.getIsSuper());
                           inputParams.put("in_user_time_zone", alertRequest.getTimeZone());
                           inputParams.put("in_from_page", alertRequest.getFromCurrentPage());
                           inputParams.put("in_specific_id", alertRequest.getSpecificId());
                           logger.debug("[DEBUG] Executing "+FETCH_CUSTOMER_ALERTS+"procedure with input params "+inputParams);
                           
                           outParameters=simpleJdbcCall.execute(inputParams);
                           Iterator<Entry<String, Object>> itr = outParameters.entrySet().iterator();
                           
                           JSONObject alertsData=new JSONObject();
                           
                           while (itr.hasNext()) {
                             Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
                             String key = entry.getKey();
                             
                             if(key.equals(CommonConstants.RESULT_SET_1))
                             {
                                  Object value = (Object) entry.getValue();
                                  org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
                                  //logger.info("temp ary "+tempAry);
                                  org.json.JSONObject innerJSON;
                                  String innerTemp;
                                  String outerArry[];
                                  String innerArry[];
                                  for(int i=0;i<tempAry.length();i++){
                                         innerJSON=(org.json.JSONObject)tempAry.getJSONObject(i);
                                         if(!innerJSON.isNull("actionItems"))
                                         {
                                         
                                         innerTemp=innerJSON.getString("actionItems");
                                         //logger.info("innerTemp "+innerTemp);
                                         outerArry=innerTemp.split("---");
                                         org.json.JSONArray userActionList=new org.json.JSONArray();
                                         if(outerArry.length>0){
                                               
                                               for(int arryCount=0;arryCount<outerArry.length;arryCount++){
                                                              try{
                                                                     innerArry=outerArry[arryCount].split("~:~");
                                                                     if(innerArry.length>0){
                                                                     JSONObject userActionInfoData=new JSONObject();
                                                                     userActionInfoData.put("itemId", Integer.parseInt(innerArry[0]));
                                                                     userActionInfoData.put("itemName", innerArry[1]);
                                                                     userActionInfoData.put("itemStatus", Integer.parseInt(innerArry[2]));
                                                                     userActionList.put(userActionInfoData);
                                                              }
                                                              }catch(ArrayIndexOutOfBoundsException ae){
                                                                     logger.error(""+ae);
                                                                     userActionList.put("");
                                                              }
                                               catch(NumberFormatException ae){
                                                              logger.error(""+ae);
                                                              userActionList.put("");
                                                       }
                                                              }
                                               innerJSON.put("actionItems", userActionList);
                                         }else{
                                                innerJSON.put("actionItems", userActionList);
                                         }
                                         
                                         
                                         
                                  }else{
                                         org.json.JSONArray userActionList=new org.json.JSONArray();
                                         /*JSONObject userActionInfoData=new JSONObject();
                                                userActionInfoData.put("itemId", 0);
                                                userActionInfoData.put("itemName", "");
                                                userActionInfoData.put("itemStatus", 0);
                                                userActionList.put(userActionInfoData);*/
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
              logger.info("[END] [getCustomerAlerts] [Alert DAO LAYER]");
              return response;
       }
 
       
       @Override
       public Response getDashboardAlerts(AlertRequest alertRequest) throws VEMAppException {
              logger.info("[BEGIN] [getDashboardAlerts] [Alert DAO LAYER]");
              Response response = new Response();
              SimpleJdbcCall simpleJdbcCall;
              Map<String,Object> outParameters = null;
              JSONObject responseObj=new JSONObject();
              JSONParser jsonParser=new JSONParser();
              try {
                     
                           simpleJdbcCall = new SimpleJdbcCall(dataSource);
                         simpleJdbcCall.withProcedureName(FETCH_DASHBOARD_ALERTS);
                         Map<String,Object> inputParams=new HashMap<>();
                         
                           inputParams.put("in_alert_status", alertRequest.getAlertStatus());
                         inputParams.put("in_user_id", alertRequest.getUserId());
                         if(StringUtils.isEmpty(alertRequest.getPdfReportalertIds()))
                            inputParams.put("in_alert_id", alertRequest.getAlertId());
                         else
                            inputParams.put("in_alert_id", alertRequest.getPdfReportalertIds());
                           inputParams.put("in_super_admin", alertRequest.getIsSuper());
                           inputParams.put("in_user_time_zone", alertRequest.getTimeZone());
                           inputParams.put("in_from_page", alertRequest.getFromCurrentPage());
                           inputParams.put("in_specific_id", alertRequest.getSpecificId());
                           inputParams.put("in_time_period", alertRequest.getTimePeriodInDays());
                           inputParams.put("in_alert_priority", CommonUtility.isNull(alertRequest.getPriority()));
                         
                         
                           logger.debug("[DEBUG] Executing "+FETCH_DASHBOARD_ALERTS+" procedure with input params "+inputParams);
                           
                           outParameters=simpleJdbcCall.execute(inputParams);
                           Iterator<Entry<String, Object>> itr = outParameters.entrySet().iterator();
                           
                           JSONObject alertsData=new JSONObject();
                           
                           while (itr.hasNext()) {
                             Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
                             String key = entry.getKey();
                             
                             if(key.equals(CommonConstants.RESULT_SET_1))
                             {
                                  Object value = (Object) entry.getValue();
                                  org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
                                  //logger.info("temp ary "+tempAry);
                                  org.json.JSONObject innerJSON;
                                  String innerTemp;
                                  String outerArry[];
                                  String innerArry[];
                                  for(int i=0;i<tempAry.length();i++){
                                         innerJSON=(org.json.JSONObject)tempAry.getJSONObject(i);
                                         if(!innerJSON.isNull("actionItems"))
                                         {
                                         
                                         innerTemp=innerJSON.getString("actionItems");
                                         //logger.info("innerTemp "+innerTemp);
                                         outerArry=innerTemp.split("---");
                                         org.json.JSONArray userActionList=new org.json.JSONArray();
                                         if(outerArry.length>0){
                                               
                                               for(int arryCount=0;arryCount<outerArry.length;arryCount++){
                                                              try{
                                                                     innerArry=outerArry[arryCount].split("~:~");
                                                                     if(innerArry.length>0){
                                                                     JSONObject userActionInfoData=new JSONObject();
                                                                     userActionInfoData.put("itemId", Integer.parseInt(innerArry[0]));
                                                                     userActionInfoData.put("itemName", innerArry[1]);
                                                                     userActionInfoData.put("itemStatus", Integer.parseInt(innerArry[2]));
                                                                     userActionList.put(userActionInfoData);
                                                              }
                                                              }catch(ArrayIndexOutOfBoundsException ae){
                                                                     logger.error(""+ae);
                                                                     userActionList.put("");
                                                              }
                                               catch(NumberFormatException ae){
                                                              logger.error(""+ae);
                                                              userActionList.put("");
                                                       }
                                                              }
                                               innerJSON.put("actionItems", userActionList);
                                         }else{
                                                innerJSON.put("actionItems", userActionList);
                                         }
                                         
                                         
                                         
                                  }else{
                                         org.json.JSONArray userActionList=new org.json.JSONArray();
                                         /*JSONObject userActionInfoData=new JSONObject();
                                                userActionInfoData.put("itemId", 0);
                                                userActionInfoData.put("itemName", "");
                                                userActionInfoData.put("itemStatus", 0);
                                                userActionList.put(userActionInfoData);*/
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
              logger.info("[END] [getDashboardAlerts] [Alert DAO LAYER]");
              return response;
       }
 
       @Override
       public Response storeDeviceStatus(AlertRequest alertRequest)
                     throws VEMAppException {
              logger.info("[BEGIN] [storeDeviceStatus] [Alert DAO LAYER]");
              Response response = new Response();
              SimpleJdbcCall simpleJdbcCall;
              Map<String,Object> outParameters = null;
              try{
                     
                     simpleJdbcCall = new SimpleJdbcCall(dataSource);
                  simpleJdbcCall.withProcedureName(SP_STORE_DEVICE_ALERTS);
                     JSONObject deviceJSON=parseStringToJson(alertRequest.getDeviceJSON());
                     
                     String inMacId=(deviceJSON.get(CommonConstants.DEVICE_KEY_MACID)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_KEY_MACID));
                     //String inMessageValue=(deviceJSON.get(CommonConstants.DEVICE_MESSAGE_VALUE)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_MESSAGE_VALUE));
                     String inDeviceId=(deviceJSON.get(CommonConstants.DEVICE_KEY_DEVICE)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_KEY_DEVICE));
                     
                     deviceJSON=(JSONObject)deviceJSON.get("data");
                     
                     String inTempHold=(deviceJSON.get(CommonConstants.DEVICE_KEY_TEMP_HOLD)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_KEY_TEMP_HOLD));
                     String inOpstate=(deviceJSON.get(CommonConstants.DEVICE_OP_STATE)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_OP_STATE));
                     String inFanState=(deviceJSON.get(CommonConstants.DEVICE_FAN_STATE)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_FAN_STATE));
                     String inTstatMode=(deviceJSON.get(CommonConstants.DEVICE_KEY_TSTAT_MODE)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_KEY_TSTAT_MODE));
                     String inZoneTemp=(deviceJSON.get(CommonConstants.DEVICE_KEY_ZONE_TEMP)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_KEY_ZONE_TEMP));
                     //String inStoreTime=(deviceJSON.get(CommonConstants.DEVICE_KEY_STORE_TIME)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_KEY_STORE_TIME));
                     String inCoolSet=(deviceJSON.get(CommonConstants.DEVICE_KEY_COOL_SET)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_KEY_COOL_SET));
                     String inMessageValue=(deviceJSON.get(CommonConstants.DEVICE_MESSAGE_VALUE)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_MESSAGE_VALUE));
                     String inHeatSet=(deviceJSON.get(CommonConstants.DEVICE_KEY_HEAT_SET)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_KEY_HEAT_SET));
                     String inDeviceDateTime=(deviceJSON.get(CommonConstants.DEVICE_DATE_TIME)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_DATE_TIME));
                     String inDeviceButtonPressed=(deviceJSON.get(CommonConstants.DEVICE_BUTTON_PRESSED)==null?"":(String) deviceJSON.get(CommonConstants.DEVICE_BUTTON_PRESSED));
                     
                     JSONObject inRelayState=(deviceJSON.get(CommonConstants.DEVICE_RELAY_STATE)==null?new JSONObject():(JSONObject) deviceJSON.get(CommonConstants.DEVICE_RELAY_STATE));
                     
                     JSONObject inCurrentClock=(deviceJSON.get(CommonConstants.DEVICE_CURRENT_CLOCK)==null?new JSONObject():(JSONObject) deviceJSON.get(CommonConstants.DEVICE_CURRENT_CLOCK));
                     String inCurrentDay=(inCurrentClock.get(CommonConstants.DEVICE_CURRENT_DAY)==null?"":(String) inCurrentClock.get(CommonConstants.DEVICE_CURRENT_DAY));
                     String inCurrentTime=(inCurrentClock.get(CommonConstants.DEVICE_CURRENT_TIME)==null?"":(String) inCurrentClock.get(CommonConstants.DEVICE_CURRENT_TIME));
                     
                     String inRelay1=(inRelayState.get(CommonConstants.DEVICE_RELAY_1)==null?"":(String) inRelayState.get(CommonConstants.DEVICE_RELAY_1));
                     String inRelay2=(inRelayState.get(CommonConstants.DEVICE_RELAY_2)==null?"":(String) inRelayState.get(CommonConstants.DEVICE_RELAY_2));
                     String inRelay3=(inRelayState.get(CommonConstants.DEVICE_RELAY_3)==null?"":(String) inRelayState.get(CommonConstants.DEVICE_RELAY_3));
                     String inRelay4=(inRelayState.get(CommonConstants.DEVICE_RELAY_4)==null?"":(String) inRelayState.get(CommonConstants.DEVICE_RELAY_4));
                     String inRelay5=(inRelayState.get(CommonConstants.DEVICE_RELAY_5)==null?"":(String) inRelayState.get(CommonConstants.DEVICE_RELAY_5));
                     String inRelay6=(inRelayState.get(CommonConstants.DEVICE_RELAY_6)==null?"":(String) inRelayState.get(CommonConstants.DEVICE_RELAY_6));
                     String inRelay7=(inRelayState.get(CommonConstants.DEVICE_RELAY_7)==null?"":(String) inRelayState.get(CommonConstants.DEVICE_RELAY_7));
                     
                     //********** Added by Chenna on 23/04/2017 ************Start**************
			            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			            String datetimeString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(formatter.parse(inDeviceDateTime));
			            // Default time zone.
			            DateTime dateTime = new DateTime( datetimeString );//2017-04-13 12:12:47
			            // In UTC/GMT (no time zone offset).
			            DateTime dateTimeUtc = dateTime.toDateTime( DateTimeZone.UTC );
			            
			            DateTime currentDateTime = new DateTime();
			            // In UTC/GMT (no time zone offset).
			            DateTime currenDateTimeUtc = currentDateTime.toDateTime( DateTimeZone.UTC );
			            
			            long durationInMinutes = new Duration(dateTimeUtc, currenDateTimeUtc).getStandardMinutes();             
	                                
			          //********** Added by Chenna on 23/04/2017 ************End**************
                     
                     
                     Map<String,Object> inputParams=new HashMap<>();
                     
                     inputParams.put("in_temp_hold", inTempHold);
                     inputParams.put("in_tstat_mode", inTstatMode);
                     inputParams.put("in_zone_temp", inZoneTemp);
                     inputParams.put("in_device_id", inDeviceId);
                     inputParams.put("in_store_time", inDeviceDateTime);
                     inputParams.put("in_cool_set", inCoolSet);
                     inputParams.put("in_heat_set", inHeatSet);
                     inputParams.put("in_mac_id", inMacId);
                     inputParams.put("in_relay_1", inRelay1);
                     inputParams.put("in_relay_2", inRelay2);
                     inputParams.put("in_relay_3", inRelay3);
                     inputParams.put("in_relay_4", inRelay4);
                     inputParams.put("in_relay_5", inRelay5);
                     inputParams.put("in_relay_6", inRelay6);
                     inputParams.put("in_relay_7", inRelay7);
                     inputParams.put("in_fan_state", inFanState);
                     inputParams.put("in_op_state", inOpstate); 
                      inputParams.put("in_current_time", inCurrentTime);
                     inputParams.put("in_current_day", inCurrentDay); 
                      inputParams.put("in_message_value", inMessageValue);
                     inputParams.put("in_button_pressed", inDeviceButtonPressed);
                     inputParams.put("in_durationIn_Minutes", durationInMinutes);
                     
                      
                     logger.info("Execting proc "+SP_STORE_DEVICE_ALERTS+" with inputs "+inputParams);
                     outParameters = simpleJdbcCall.execute(inputParams);
                     
                     //logger.info("out params "+outParameters);
                     
                     int statusFlag = (int) outParameters.get("out_flag");
               if(statusFlag == 0){
                        response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                     response.setCode(ErrorCodes.SUCCESS_STORE_ALERTS);
                  }else{
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_STORE_ALERTS);
                  }
                  
              } catch (Exception e) {
                     logger.error("", e);
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_STORE_ALERTS);
                     throw new VEMAppException("Internal Error occured at DAO layer");
              }
              logger.info("[END] [storeDeviceStatus] [Alert DAO LAYER]");
              return response;
       }
       
       
       @Override
       public Response getNewAlertCount(AlertRequest alertRequest) throws VEMAppException {
              logger.info("[BEGIN] [getNewAlertCount] [Alert DAO LAYER]");
              Response response = new Response();
              SimpleJdbcCall simpleJdbcCall;
              Map<String,Object> outParameters = null;
              JSONObject responseObj=new JSONObject();
              JSONParser jsonParser=new JSONParser();
              try {
                     
                           simpleJdbcCall = new SimpleJdbcCall(dataSource);
                         simpleJdbcCall.withProcedureName(FETCH_NEW_ALERT_COUNT);
                         
                         Map<String,Object> inputParams=new HashMap<>();
                         inputParams.put("in_user_id", alertRequest.getUserId());
                           inputParams.put("in_customers", alertRequest.getCustomers());
                           inputParams.put("in_super_admin", alertRequest.getIsSuper());
                           
                           logger.debug("[DEBUG] Executing "+FETCH_NEW_ALERT_COUNT+"procedure with input params "+inputParams);
                           
                           outParameters=simpleJdbcCall.execute(inputParams);
                           Iterator<Entry<String, Object>> itr = outParameters.entrySet().iterator();
                           
                           while (itr.hasNext()) {
                             Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
                             String key = entry.getKey();
                             if(key.equalsIgnoreCase("out_count")){
                                  logger.info("entry.getValue()"+entry.getValue());
                                  response.setData(entry.getValue());
                             }
                           }
                           response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                    response.setCode(ErrorCodes.SUCCESS_ALERT_LIST_FETCH);
                           
              
              } catch (Exception e) {
                     logger.error("", e);
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_ALERT_LIST_FETCH);
                     throw new VEMAppException("Internal Error occured at DAO layer");
              }
              logger.info("[END] [getNewAlertCount] [Alert DAO LAYER]");
              return response;
       }
 
       
       @Override
       public Response updateAlertStatus(AlertRequest alertRequest) throws VEMAppException {
              logger.info("[BEGIN] [updateAlertStatus] [Alert DAO LAYER]");
              Response response = new Response();
              SimpleJdbcCall simpleJdbcCall;
              Map<String,Object> outParameters = null;
              JSONObject responseObj=new JSONObject();
              JSONParser jsonParser=new JSONParser();
              try {
                     
                           simpleJdbcCall = new SimpleJdbcCall(dataSource);
                         simpleJdbcCall.withProcedureName(UPDATE_ALERT_STATUS);
                         
                         Map<String,Object> inputParams=new HashMap<>();
                         inputParams.put("in_user_id", alertRequest.getUserId());
                           inputParams.put("in_alert_ids", alertRequest.getAlertIds());
                           inputParams.put("in_alert_status", alertRequest.getAlertStatus());
                           logger.debug("[DEBUG] Executing "+UPDATE_ALERT_STATUS+" procedure with input params "+inputParams);
                           outParameters = simpleJdbcCall.execute(inputParams);
                           
                            int statusFlag = (int) outParameters.get("out_flag");
                             if(statusFlag == 0){
                                         response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                                   response.setCode(ErrorCodes.SUCCESS_ALERT_CONFIG_SAVE);
                            }else{
                                          response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                                         response.setCode(ErrorCodes.ERROR_ALERT_CONFIG_SAVE);
                             }
              
              } catch (Exception e) {
                     logger.error("", e);
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_ALERT_CONFIG_SAVE);
                     throw new VEMAppException("Internal Error occured at DAO layer");
              }
              logger.info("[END] [updateAlertStatus] [Alert DAO LAYER]");
              return response;
       }
 
       
       private JSONObject parseStringToJson(String deviceState){
              JSONObject parsedJSON=new JSONObject();
              JSONParser jsonParser=new JSONParser();
              try{
                     if(deviceState !=null && !deviceState.isEmpty()){
                           parsedJSON=(JSONObject)jsonParser.parse(deviceState);
                     }
              }catch(Exception e){
                     logger.error("",e);
              }
              return parsedJSON;
       }
 
       @Override
       public Response getAlertsByActionItems(AlertRequest alertRequest) throws VEMAppException {
              
              logger.debug("[BEGIN] [getAlertsByActionItems] [DAO LAYER]");
              Response response=new Response();
              Map<String,Object> outParameters = null;
              try {
                     
                     SimpleJdbcCall simpleJdbcCall=new SimpleJdbcCall(dataSource);
                     simpleJdbcCall.withProcedureName(FETCH_ALERTS_ACTION_ITEMS);
                     Map<String,Object> inputParams=new HashMap<>();
                     inputParams.put("in_user_id", alertRequest.getUserId() );
                     inputParams.put("in_super_admin", alertRequest.getIsSuper() );
                     inputParams.put("in_customers", alertRequest.getCustomers());
                     inputParams.put("in_eai_admin", alertRequest.getIsEai());
                     inputParams.put("in_from_page", alertRequest.getFromCurrentPage());
                     inputParams.put("in_alert_status", (alertRequest.getAlertStatus()==null || alertRequest.getAlertStatus().isEmpty())?"all":alertRequest.getAlertStatus());
                     inputParams.put("in_user_time_zone", alertRequest.getTimeZone());
                     
                     logger.debug(CommonConstants.SP_PREFIX_CONSTANT+" "+FETCH_ALERTS_ACTION_ITEMS+ " with input params "+inputParams);
                     outParameters =simpleJdbcCall.execute(inputParams);
                     
                     Iterator<Entry<String, Object>> itr = outParameters.entrySet().iterator();
                     JSONObject responseObj=new JSONObject();
                     JSONParser parser=new JSONParser();
                     JSONArray resultAry=new JSONArray();
                     
                     while (itr.hasNext()) {
                             Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
                             String key = entry.getKey();
                             if(key.equals(CommonConstants.RESULT_SET_1))
                             {
                                  Object value = (Object) entry.getValue();
                                  org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
                                  org.json.JSONObject innerJSON;
                                  String innerTemp;
                                  String outerArry[];
                                  String innerArry[];
                                  for(int i=0;i<tempAry.length();i++){
                                         innerJSON=(org.json.JSONObject)tempAry.getJSONObject(i);
                                         innerTemp=innerJSON.getString("actionItems");
                                         outerArry=innerTemp.split("---");
                                         org.json.JSONArray userActionList=new org.json.JSONArray();
                                         for(int arryCount=0;arryCount<outerArry.length;arryCount++){
                                                       try{
                                                              innerArry=outerArry[arryCount].split("~:~");
                                                              if(innerArry.length>0){
                                                              JSONObject userActionInfoData=new JSONObject();
                                                              userActionInfoData.put("itemId", Integer.parseInt(innerArry[0]));
                                                              userActionInfoData.put("itemName", innerArry[1]);
                                                              userActionInfoData.put("itemStatus", innerArry[2]);
                                                              userActionList.put(userActionInfoData);
                                                       }
                                                       }catch(ArrayIndexOutOfBoundsException ae){
                                                              logger.error(""+ae);
                                                              userActionList.put("");
                                                       }
                                                }
                                         innerJSON.put("actionItems", userActionList);
                                  }
                                  
                                  resultAry=(JSONArray)parser.parse(tempAry.toString());
                                         //responseObj.put("resultAry", resultAry);
                                   }
                     }
                     response.setData(resultAry);
                  if(resultAry.isEmpty() || resultAry.size()<=0){
                           response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                           response.setCode(ErrorCodes.ERROR_ALERT_ACTIONLIST_FETCH);
                     }else{
                           response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                           response.setCode(ErrorCodes.SUCCESS_ALERT_ACTIONLIST_FETCH);
                     }
              } catch (Exception e) {
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_ALERT_ACTIONLIST_FETCH);
                     logger.error("",e);
                     throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ALERT_ACTION_ITEMS_ERROR_FAILED, logger, e);
              }
              logger.debug("[END] [getAlertsByActionItems] [DAO LAYER]");
              return response;
       }
 
       @Override
       public Response updateActionItems(AlertRequest alertRequest) throws VEMAppException {
              logger.info("[BEGIN] [updateActionItems] [DAO LAYER]");
              Response response = new Response();
              SimpleJdbcCall simpleJdbcCall;
              Map<String,Object> outParameters = null;
              JSONObject responseObj=new JSONObject();
              JSONParser jsonParser=new JSONParser();
              try {
                           ArrayList<HashMap> actionList=new ArrayList<HashMap>();
                           StringBuilder actionItemId=new StringBuilder();
                         StringBuilder actionStatusId=new StringBuilder();
                         actionList=alertRequest.getActionItems();
                           if(actionList.size()>0){
                                  Iterator itr= actionList.iterator();
                                  HashMap actionMap=new HashMap();
                                  while(itr.hasNext()){
                                         actionMap=(HashMap)itr.next();
                                         
                                         actionItemId.append(actionMap.get("itemId")).append(",");
                                         actionStatusId.append(actionMap.get("itemStatus")).append(",");
                                  }
                                  if(actionStatusId.length()>0){
                                         actionItemId=actionItemId.delete(actionItemId.length()-1, actionItemId.length());
                                         actionStatusId=actionStatusId.delete(actionStatusId.length()-1, actionStatusId.length());
                                         //actionItemId=actionItemId.replace(actionItemId.lastIndexOf(","), actionItemId.lastIndexOf(","), "");
                                         //actionStatusId=actionStatusId.replace(actionStatusId.lastIndexOf(","), actionStatusId.lastIndexOf(","), "");
                                  }
                                  
                           }
                           
                           simpleJdbcCall = new SimpleJdbcCall(dataSource);
                         simpleJdbcCall.withProcedureName(UPDATE_ALERT_ACTION_ITEMS);
                         Map<String,Object> inputParams=new HashMap<>();
                         inputParams.put("in_user_id", alertRequest.getUserId());
                         inputParams.put("in_super_admin", alertRequest.getIsSuper());
                         inputParams.put("in_eai_admin", alertRequest.getIsEai());
                           inputParams.put("in_alert_action_item_ids", actionItemId.toString());
                           inputParams.put("in_alert_action_status", actionStatusId.toString());
                           inputParams.put("in_action_status", alertRequest.getActionStatus());
                           inputParams.put("in_alert_id", alertRequest.getAlertId());
                           
                           logger.debug("[DEBUG] Executing "+UPDATE_ALERT_ACTION_ITEMS+" procedure with input params "+inputParams);
                           outParameters = simpleJdbcCall.execute(inputParams);
                           int statusFlag = (int) outParameters.get("out_flag");
                         if(statusFlag == 0){
                                     response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                            response.setCode(ErrorCodes.SUCCESS_ALERT_ACTIONLIST_UPDATE);
                         }else{
                            response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                             response.setCode(ErrorCodes.ERROR_ALERT_ACTIONLIST_UPDATE);
                         }
              } catch (Exception e) {
                     logger.error("", e);
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_ALERT_ACTIONLIST_UPDATE);
                     throw new VEMAppException("Internal Error occured at DAO layer");
              }
              logger.info("[END] [updateActionItems] [Alert DAO LAYER]");
              return response;
       }
       
       @Override
       public Response getDeviceAlertsDao(AlertRequest alertRequest) throws VEMAppException {
              logger.info("[BEGIN] [getDeviceAlertsDao] [DAO LAYER]");
              Response response = new Response();
              SimpleJdbcCall simpleJdbcCall;
              Map<String,Object> outParameters = null;
              try {
                           
                           simpleJdbcCall = new SimpleJdbcCall(dataSource);
                         simpleJdbcCall.withProcedureName(FETCH_DEVICE_ALERTS);
                         
                         Map<String,Object> inputParams=new HashMap<>();
                         inputParams.put("in_user_id", alertRequest.getUserId());
                         inputParams.put("in_super_admin", alertRequest.getIsSuper());
                         inputParams.put("in_eai_admin", alertRequest.getIsEai());
                         inputParams.put("in_xcpec_id", alertRequest.getDeviceXpecId());
                         inputParams.put("in_frompage", alertRequest.getFromCurrentPage());
                         
                           logger.debug("[DEBUG] Executing "+FETCH_DEVICE_ALERTS+" procedure with input params "+inputParams);
                           outParameters = simpleJdbcCall.execute(inputParams);
                           
                           Iterator<Entry<String, Object>> itr = outParameters.entrySet().iterator();
                           JSONParser parser=new JSONParser();
                           
                           while (itr.hasNext()) {
                             Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
                             String key = entry.getKey();
                             
                             if(key.equals(CommonConstants.RESULT_SET_1))
                             {
                                  Object value = (Object) entry.getValue();
                                  org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
                                  JSONArray resultAry=(JSONArray)parser.parse(tempAry.toString());
                                  response.setData(resultAry);
                                    response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                                         response.setCode(ErrorCodes.SUCCESS_ALERT_CUSTOMER_CONFIG_DELETE);
                             }
                         }
 
                         
              } catch (Exception e) {
                     logger.error("", e);
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_CONFIG_DELETE);
                     throw new VEMAppException("Internal Error occured at DAO layer");
              }
              logger.info("[END] [getDeviceAlertsDao] [Alert DAO LAYER]");
              return response;
       }
 
       
       @Override
       public Response deleteCustomerConfigDao(AlertRequest alertRequest) throws VEMAppException {
              logger.info("[BEGIN] [deleteCustomerConfigDao] [DAO LAYER]");
              Response response = new Response();
              SimpleJdbcCall simpleJdbcCall;
              Map<String,Object> outParameters = null;
              try {
                           
                           simpleJdbcCall = new SimpleJdbcCall(dataSource);
                         simpleJdbcCall.withProcedureName(DELETE_ALERT_CUSTOMER_CONFIG);
                         
                         Map<String,Object> inputParams=new HashMap<>();
                         inputParams.put("in_customer_id", alertRequest.getCustomerId());
                         inputParams.put("in_alert_id", alertRequest.getAlertId());
                         
                           logger.debug("[DEBUG] Executing "+DELETE_ALERT_CUSTOMER_CONFIG+" procedure with input params "+inputParams);
                           outParameters = simpleJdbcCall.execute(inputParams);
                           
                           logger.debug("[DEBUG] output status "+outParameters);
                           
                           int statusFlag = (int) outParameters.get("out_flag");
                           
                      if(statusFlag == 0){
                                   response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
                            response.setCode(ErrorCodes.SUCCESS_ALERT_CUSTOMER_CONFIG_DELETE);
                     }
                      else if(statusFlag == -2){
                                   response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                            response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_CONFIG_ALREADY);
                     }else{
                                   response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                                   response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_CONFIG_DELETE);
                      }
 
                         
              } catch (Exception e) {
                     logger.error("", e);
                     response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
                     response.setCode(ErrorCodes.ERROR_ALERT_CUSTOMER_CONFIG_DELETE);
                     throw new VEMAppException("Internal Error occured at DAO layer");
              }
              logger.info("[END] [deleteCustomerConfigDao] [Alert DAO LAYER]");
              return response;
       }
       
}
