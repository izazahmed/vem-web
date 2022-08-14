package com.enerallies.vem.daoimpl.schedule;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.enerallies.vem.beans.Schedule;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.iot.ThingResponse;
import com.enerallies.vem.beans.schedule.AddScheduleDetails;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.dao.schedule.ScheduleDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.listeners.xcspec.RestClient;
import com.enerallies.vem.serviceimpl.iot.ThingServiceDataUpdater;
import com.enerallies.vem.serviceimpl.iot.ThingServiceIoTDataHelper;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.iot.DeviceDataPublisher;

@Component
public class ScheduleDaoImpl implements ScheduleDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSourceRead;
	private JdbcTemplate jdbcTemplateRead;	
	private static final Logger logger=Logger.getLogger(ScheduleDaoImpl.class);
	private static final String FETCH_SCHEDULE_DETIALS="call sp_select_schedule_details (?,?)"; // pass customer id to get customer details
	private static final String FETCH_SCHEDULE_FILTERDTA="call sp_schedule_filterdata (?,?,?)";
	private static final String FETCH_SCHEDULE_FILTERSEARCH="call sp_schedule_filterSearch (?,?,?,?,?)";
	private static final String FETCH_SCHEDULE_APPLY_VIEW="call sp_schedule_apply_view (?,?,?,?)";
	private static final String APPLY_SCHEDULE="sp_schedule_apply";
	private static final String APPLY_SCHEDULE_VALIDATE="sp_schedule_apply_validate";
	private static final String RESULT_SET_1 = "#result-set-1";
	private static final  String INSERT_INTO_SCHEDULE="INSERT INTO schedule (schedule_name,modified_by,created_by) VALUES (?,?,?)";
	private static final  String INSERT_INTO_TIME_POINTS= " INSERT INTO schedule_time_points(schedule_id,dow_id,time,am_pm,htng_pt,htng_pt_unit,clng_pt,clng_pt_unit,created_by,modified_by)"
			+ " VALUES(?,?,?,?,?,?,?,?,?,?)";
	private static final  String VALIDATE_DUPLICATE_SCHEDULE= "select count(schedule_id) as COUNT from schedule where upper(schedule_name) = ? and is_deleted=0 ";
	private static final String GET_SCHEDULE_INFO ="call sp_schedule_info(?,?)";
	private static final String UPDATE_SCHEDULE="update schedule set schedule_name=?, modified_by=?,`modified_date`=CURRENT_TIMESTAMP  where schedule_id = ?";
	private static final String DELETE_SCHEDULE_TIME_PTS="update schedule_time_points set is_deleted=1, modified_by=?,`modified_date`=CURRENT_TIMESTAMP where schedule_id = ?";
	private static final String DELETE_SCHEDULE_DETAILS="sp_delete_schedule_details"; 
	private static final String FETCH_SELECT_SCHEDULE_APPLY_DEVICE="call sp_select_schedule_apply_device (?,?)";
	private static final String UPDATE_SCHEDULE_DEVICE_DETAILS="sp_schedule_device_timepoints_update"; 
	private static final String UPDATE_SCHEDULE_STATUS="update schedule set status_id=? where schedule_id=?";
	private static final String UPDATE_SCHEDULE_DEVICE_STATUS="update schedule_to_device set status_id=? where schedule_id=? and device_id=?";
	private static final String UPDATE_SCHEDULE_DATE="update schedule set download_time=sysdate() where schedule_id=?";
	private static final String INSERT_ALERT_DEVICE ="sp_update_schedule_device_alerts";
	private static final String INSERT_INTO_DEVICE_SCHEDULE="INSERT INTO schedule_to_device(device_id,created_by,updated_by,status_id,schedule_id) VALUES(?,?,?,1,?)";
	private static final  String INSERT_INTO_DEVICE_TIME_POINTS= " INSERT INTO schedule_dt_points(device_time_points_id,dow_id,time,am_pm,htng_pt,htng_pt_unit,clng_pt,clng_pt_unit,created_by,updated_by)"
			+ " VALUES(?,?,?,?,?,?,?,?,?,?)";	
	private static final String ADD_SCHEDULE_TO_MODULE="sp_schedule_addto_module"; 
	private static final String APPLY_DEVICEC_SCHEDULE="sp_schedule_device_apply";
	private static final String GET_SCHEDULE_MODULE_DETAILS ="call sp_schedule_module_details(?,?,?)";
	private static final String GET_SCHEDULE_DEVICE_DETAILS="sp_schedule_device_details"; 
	private static final String UPDATE_DEVICE_SCHEDULE="update schedule_to_device set is_deleted=1 where device_id = ? and is_deleted=0";
	private static final String FETCH_SELECT_SCHEDULE_APPLY_IOTDEVICE="call sp_select_schedule_apply_IOTdevice (?,?)";
	
	//private static final String VALIDATE_IS_SCHEDULE_MAPPED_TO_FORECAST="select count(fct_frn_id) as COUNT from forecast_config_temp where fct_frn_schedule_id=? AND fct_is_deleted=0";
	private static final String VALIDATE_IS_SCHEDULE_MAPPED_TO_FORECAST = "select fn_schedule_forecast_map_count(?)";
	private static final String VALIDATE_IS_SCHEDULE_MAPPED_TO_DEVICE = "select fn_schedule_device_map_count(?)";
	private static final String RESULT_SET_2 = "#result-set-2";
	private static final String PROC_INSERT_DEVICE_CONFIG = "sp_insert_device_last_update";
	private static final String GET_DEVICE_LAST_UPDATE_TIME = "sp_get_device_last_update_time";
	@Autowired private AuditDAO auditDAO;
	
	@Autowired
	DeviceDataPublisher dataPublisher;
	
	@Autowired
	ThingServiceIoTDataHelper iotDataHelper;
	
	@Autowired
	ThingServiceDataUpdater dataUpdater;
	
	@Override  
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	@Override  
	public void setDataSourceRead(DataSource dataSource) {
		this.dataSourceRead = dataSource;
	    this.jdbcTemplateRead = new JdbcTemplate(this.dataSourceRead);
	}
	@Override
	public Response getScheduleList(GetUserResponse userDetails) throws VEMAppException{
		logger.info("[BEGIN] [getScheduleList] [Schedule DAO Impl]");
		Response response=new Response();
		try{
			
			logger.debug("calling procedure "+FETCH_SCHEDULE_DETIALS+"with params ");
			//Executing the procedure.
			response= jdbcTemplateRead.query(FETCH_SCHEDULE_DETIALS, new Object[]{userDetails.getUserId(),userDetails.getTimeZone()}, new ResultSetExtractor<Response>() {
				@Override
				public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
					Response response=new Response();
					JSONArray resultAry=new JSONArray();
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					while(rs.next()){
						JSONObject tempData=new JSONObject();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
						}
						
					   if(tempData.get("isActive")!=null ){
						tempData.put("isActive", Integer.parseInt(tempData.get("isActive").toString()));   
					   }	
					   resultAry.add(tempData);
					}
					System.out.println(resultAry);
					response.setData(resultAry);
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_STATUS_GET_SCHEDULE);
					return response;
				}
				
			});

			
		}
		catch(Exception e){
			logger.error("",e);
			response.setCode(ErrorCodes.ERROR_STATUS_GET_SCHEDULE);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_STATUS_GET_SCHEDULE, logger, e);
		}
		
		logger.info("[END] [getCustomerProfile] [Customers DAO Impl]");
		return response;
	}
	
	@Override
	public Response getFilterData(Schedule schedule,GetUserResponse userDetailss) throws VEMAppException{
		logger.info("[BEGIN] [getFilterData] [Schedule DAO Impl]");
		Response response=new Response();
		try{
			
			logger.info("calling procedure "+FETCH_SCHEDULE_FILTERDTA+"with params "+schedule.getFlag());
			//Executing the procedure.
			response= jdbcTemplate.query(FETCH_SCHEDULE_FILTERDTA, new Object[]{schedule.getFlag(),userDetailss.getUserId(),schedule.getSearchId()}, new ResultSetExtractor<Response>() {
				@Override
				public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
					Response response=new Response();
					JSONArray resultAry=new JSONArray();
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					while(rs.next()){
						JSONObject tempData=new JSONObject();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
						}
					  
					   resultAry.add(tempData);
					   
					}
					response.setData(resultAry);
					
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_STATUS_GET_SCHEDULE);
					
					return response;
				}
				
			});

			
		}
		catch(Exception e){
			logger.error("",e);
			response.setCode(ErrorCodes.ERROR_STATUS_GET_SCHEDULE);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_STATUS_GET_SCHEDULE, logger, e);
		}
		
		logger.info("[END] [getCustomerProfile] [Customers DAO Impl]");
		return response;
	}
	@Override
	public Response getFilterSearch(Schedule schedule,GetUserResponse userDetailss) throws VEMAppException{
		logger.info("[BEGIN] [getFilterSearch] [Schedule DAO Impl]");
		Response response=new Response();
		try{
			
			logger.info("calling procedure "+FETCH_SCHEDULE_FILTERSEARCH+"with params "+schedule.getFlag());
			logger.info("calling procedure "+FETCH_SCHEDULE_FILTERSEARCH+"with params "+schedule.getSearchId());
			logger.info("calling procedure "+FETCH_SCHEDULE_FILTERSEARCH+"with params "+userDetailss.getUserId());
			//Executing the procedure.
			response= jdbcTemplate.query(FETCH_SCHEDULE_FILTERSEARCH, new Object[]{schedule.getFlag(),schedule.getSearchId(),userDetailss.getUserId(),schedule.getCustomerId(),userDetailss.getTimeZone()}, new ResultSetExtractor<Response>() {
				@Override
				public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
					Response response=new Response();
					JSONArray resultAry=new JSONArray();
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					while(rs.next()){
						JSONObject tempData=new JSONObject();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
						}
						if(tempData.get("isActive")!=null ){
							tempData.put("isActive", Integer.parseInt(tempData.get("isActive").toString()));   
						}
					   resultAry.add(tempData);
			   
					}
					response.setData(resultAry);
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_STATUS_GET_SCHEDULE);
					return response;
				}
				
			});

			
		}
		catch(Exception e){
			logger.error("",e);
			response.setCode(ErrorCodes.ERROR_STATUS_GET_SCHEDULE);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_STATUS_GET_SCHEDULE, logger, e);
		}
		
		logger.info("[END] [getFilterSearch] [SCHEDULE DAO Impl]");
		return response;
	}
	@Override
	public Response getCustomerList(GetUserResponse userDetails,Schedule schedule) throws VEMAppException{
		logger.info("[BEGIN] [getCustomerList] [Schedule DAO Impl]");
		Response response=new Response();
		try{
			
			logger.debug("calling procedure "+FETCH_SCHEDULE_APPLY_VIEW+"with params ");
			//Executing the procedure.
			response= jdbcTemplate.query(FETCH_SCHEDULE_APPLY_VIEW, new Object[]{userDetails.getUserId(),schedule.getFlag(),schedule.getSearchId(),schedule.getScheduleId()}, new ResultSetExtractor<Response>() {
				@Override
				public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
					Response response=new Response();
					JSONArray resultAry=new JSONArray();
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					logger.debug("schedule.getSearchId()************ "+schedule.getSearchId()); 
					while(rs.next()){
						JSONObject tempData=new JSONObject();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
						}
						
					    resultAry.add(tempData);
					  
					}
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_STATUS_GET_SCHEDULE);
					response.setData(resultAry);
					return response;
				}
				
			});

			
		}
		catch(Exception e){
			logger.error("",e);
			response.setCode(ErrorCodes.ERROR_STATUS_GET_SCHEDULE);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_STATUS_GET_SCHEDULE, logger, e);
		}
		
		logger.info("[END] [getCustomerList] [Schedule DAO Impl]");
		return response;
	}
	
	@Override
	public Response applySchedule(GetUserResponse userDetails,Schedule schedule) throws VEMAppException{
		logger.info("[BEGIN] [applySchedule] [Schedule DAO Impl]");
		Response response=new Response();
		SimpleJdbcCall simpleJdbcCall;
		try{
			
			//Executing the procedure.
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
			Map<String, Object> simpleJdbcCallResult=null;
			Iterator<Entry<String, Object>> itr =null;
			
			JSONArray resultAry=new JSONArray();
			JSONObject responseObj=new JSONObject();
			JSONParser parser=new JSONParser();
			int countvalidate=0;
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<String, Object>();
			logger.info("s*"+userDetails.getUserId());
			
			logger.info("schedule.getScheduleId()**test**"+schedule.getScheduleId());
			logger.info("schedule.getCustomerId()**"+schedule.getCustomerId());
			logger.info("schedule.getGroupId()**test**"+schedule.getGroupId());
			logger.info("schedule.in_siteId()**test**"+schedule.getSiteId());
			logger.info("schedule.getDeviceId()*"+schedule.getDeviceId());
			
		    inputParams.put("in_userId", userDetails.getUserId());
			inputParams.put("in_scheduleId", schedule.getScheduleId());
			inputParams.put("in_customerId", schedule.getCustomerId());
			inputParams.put("in_groupId", schedule.getGroupId());
			inputParams.put("in_siteId", schedule.getSiteId());
			inputParams.put("in_deviceId", schedule.getDeviceId());
			
			simpleJdbcCall.withProcedureName(APPLY_SCHEDULE_VALIDATE);
			
			simpleJdbcCallResult = simpleJdbcCall.execute(inputParams);
			

			itr = simpleJdbcCallResult.entrySet().iterator();
	
			
			
			while (itr.hasNext()) {
				
				 Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
			     String key = entry.getKey();
			        
				if(key.equals(RESULT_SET_1))
		        {
		        	Object value = (Object) entry.getValue();
		        	logger.info("value-----------------*"+value);
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	resultAry=(org.json.simple.JSONArray)parser.parse(tempAry.toString());
		        	JSONObject validobj=(JSONObject)resultAry.get(0);
		        	logger.info("validobj-----------------*"+validobj);
		        	countvalidate=((Long)validobj.get("device_id")).intValue();
		        	 logger.info("value-----------------*"+countvalidate);
		        	//responseObj.put("groupSitesList", obj.get(key));
		        	
				}
			       
			}
			logger.info("simpleJdbcCallResult.size()*"+resultAry.size());
			if(countvalidate>0){
					simpleJdbcCall = new SimpleJdbcCall(dataSource);
					simpleJdbcCall.withProcedureName(APPLY_DEVICEC_SCHEDULE);
					simpleJdbcCallResult =simpleJdbcCall.execute(inputParams);
					itr = simpleJdbcCallResult.entrySet().iterator();
					ArrayList deviceArr=null;
					ArrayList awsDeviceArr=null;
				while (itr.hasNext()) {
					
					 Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
				     String key = entry.getKey();
				        
					if(key.equals(RESULT_SET_1))
			        {
			        	Object value = (Object) entry.getValue();
			        	 deviceArr=(ArrayList)value;
			        	
					}
					if(key.equals(RESULT_SET_2))
			        {
			        	Object value = (Object) entry.getValue();
			        	awsDeviceArr=(ArrayList)value;
			        	
					}
				       
				}
				final ArrayList deviceArrfinal=deviceArr;
				final ArrayList awsDeviceArrfinal=awsDeviceArr;
				
				logger.info("deviceArr.size()*"+deviceArr);
				logger.info("awsDeviceArrfinal.size()*"+awsDeviceArrfinal);
				
		        response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.INFO_STATUS_APPLY_SCHEDULE);
				responseObj.put("Flag", "insert");
				response.setData(responseObj);
				if(deviceArrfinal!=null && deviceArrfinal.size()>0){
					logger.info("deviceArrfinal.size()*"+deviceArrfinal.size());
					Executor ex= Executors.newCachedThreadPool();
					CompletionService<Long> cs = new ExecutorCompletionService<Long>(ex);
					cs.submit(new Callable(){
						public Response call() throws Exception{
							logger.info("applyDeviceJSON Before*");
							return applyDeviceJSON(schedule,deviceArrfinal,0,userDetails.getUserId());
							
						}
					});
			    }
				if(awsDeviceArrfinal!=null && awsDeviceArrfinal.size()>0){
					logger.info("awsDeviceArrfinal.size()*"+awsDeviceArrfinal.size());
					Executor ex= Executors.newCachedThreadPool();
					CompletionService<Long> cs = new ExecutorCompletionService<Long>(ex);
					cs.submit(new Callable(){
						public Response call() throws Exception{
							logger.info("applyDeviceJSON Before*");
							return applyOnAWSIOTDevice(schedule,awsDeviceArrfinal,0,userDetails.getUserId());
							
						}
					});
			    }
				
				logger.info("after loop*");
		        
			}else{
				responseObj.put("Flag", "validateFail");
				response.setData(responseObj);
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.INFO_STATUS_APPLY_SCHEDULE);
			}
			
		} 
		catch(Exception e){
			logger.error("",e);
			response.setCode(ErrorCodes.ERROR_STATUS_APPLY_SCHEDULE);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_STATUS_APPLY_SCHEDULE, logger, e);
		}
		
		logger.info("[END] [getCustomerList] [Schedule DAO Impl]");
		return response;
	}
	
	@Override
	public int addSchedule(AddScheduleDetails addScheduleDetails, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [addSchedule] [Schedule DAO LAYER]");
		int scheduleId ;
		SimpleJdbcCall simpleJdbcCall;
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_INTO_SCHEDULE, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, addScheduleDetails.getScheduleName());
				ps.setInt(2, userId);
				ps.setInt(3, userId);
				return ps;
			}
		}, keyHolder);

		scheduleId= keyHolder.getKey().intValue();
	    List<AddScheduleDetails> dtoList= addScheduleDetails.getSchdlObjList();
		 
		 logger.info("[BEGIN] list size "+dtoList.size());

	  	  jdbcTemplate.batchUpdate(INSERT_INTO_TIME_POINTS, new BatchPreparedStatementSetter() {
	  			@Override
				public int getBatchSize() {
					return dtoList.size();
				}

				@Override
				public void setValues(java.sql.PreparedStatement ps, int i)
						throws SQLException {

					AddScheduleDetails schdlDto = dtoList.get(i);					
					ps.setLong(1, scheduleId);
					ps.setString(2, schdlDto.getDayId());
					ps.setString(3, schdlDto.getTime());
					ps.setString(4, schdlDto.getAm());
					ps.setString(5, schdlDto.getHtpoint() );
					ps.setString(6, schdlDto.getHtunit());
					ps.setString(7, schdlDto.getClpoint());
					ps.setString(8, schdlDto.getClunit());
					ps.setInt(9, userId );
					ps.setInt(10, userId );			
			   }
			  });
	  	  
	  	//Executing the procedure.
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(ADD_SCHEDULE_TO_MODULE);
		    
		  
			Map<String,Object> inputParams=new HashMap<String, Object>();
			Map<String,Object> outParameters ;
			String type=new String();
			String typeId= new String();
			
			logger.debug("Deleting schedule details "+addScheduleDetails.getScheduleId());
			logger.debug("Deleting getSiteId details "+addScheduleDetails.getSiteId());
			logger.debug("Deleting getGroupId details "+addScheduleDetails.getGroupId());
			logger.debug("Deleting getCustomerId details "+addScheduleDetails.getCustomerId());
			 
			if(addScheduleDetails.getSiteId()!=null && addScheduleDetails.getSiteId().length()>0) {
				type="SITE";
				typeId = addScheduleDetails.getSiteId();
				
			}else if(addScheduleDetails.getGroupId()!=null && addScheduleDetails.getGroupId().length()>0){
				type="GROUP";
				typeId = addScheduleDetails.getGroupId();
				
			}else if(addScheduleDetails.getCustomerId()!=null && addScheduleDetails.getCustomerId().length()>0){
				type="CUSTOMER";
				typeId = addScheduleDetails.getCustomerId();				
				
			}
			
			if (type!=null){
				
				logger.debug("in_type "+type);
				logger.debug("in_typeId "+ typeId);
				logger.debug("scheduleId "+ scheduleId);
				
			inputParams.put("in_scheduleId", scheduleId);
			inputParams.put("in_typeId", typeId);
			inputParams.put("in_type", type);
			inputParams.put("in_userId", userId);
			
	        outParameters = simpleJdbcCall.execute(inputParams);
	        }

	   	   
			  	AuditRequest auditRequest= new AuditRequest();
			  	
			  	auditRequest.setUserId(userId);
			  	auditRequest.setUserAction("Added");
				auditRequest.setLocation("");
				auditRequest.setServiceId("7");
				auditRequest.setDescription("Schedule Added");
				auditRequest.setServiceSpecificId(scheduleId);
			  	auditDAO.insertAuditLog(auditRequest);
		
	  	  logger.info("[END] [addSchedule] [Schedule DAO LAYER]"+scheduleId);

		return scheduleId;
	}

	@Override
	public int validateDuplicateSchedule(AddScheduleDetails addScheduleDetails) throws VEMAppException {		
		logger.info("[BEGIN] [validateDuplicateSchedule] [Schedule DAO LAYER]");
	
		return  jdbcTemplate.queryForObject(VALIDATE_DUPLICATE_SCHEDULE, new Object[] { addScheduleDetails.getScheduleName() }, Integer.class);
		
	}

	@Override
	public Response getScheduleDetails(AddScheduleDetails addScheduleDetails, GetUserResponse userDetails)
			throws VEMAppException {
		
		
		logger.info("[BEGIN] [getScheduleDetails] [Schedule DAO LAYER]");
		Response response =new Response();
		HashMap<Integer, JSONArray> daysMap=new HashMap<>();
		
		 
		try {//Executing the procedure.
			logger.info("calling the proc "+GET_SCHEDULE_INFO+" with params "+addScheduleDetails.getScheduleId());
			
			//Executing the procedure.
			daysMap= jdbcTemplate.query(GET_SCHEDULE_INFO, new Object[]{addScheduleDetails.getScheduleId(), userDetails.getUserId()}, new ResultSetExtractor<HashMap<Integer, JSONArray>>() {
				@Override
				public HashMap extractData(ResultSet rs) throws SQLException,DataAccessException {
					
					HashMap<Integer, JSONArray> daysMap=new HashMap<>();				
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
						
					
					while(rs.next()){
						JSONObject tempData=new JSONObject();
						JSONArray resultAry=new JSONArray();
						for(int i=1;i<=columnCount;i++){
							tempData.put(rsmd.getColumnLabel(i), rs.getString(rsmd.getColumnLabel(i)));
						}
				
						if(!daysMap.containsKey(rs.getInt("dow_id")) && rs.getInt("dow_id")>0){
							 resultAry.add(tempData);
							 daysMap.put(rs.getInt("dow_id"), resultAry);
							 							
						}else if(rs.getInt("dow_id")>0){
							
							JSONArray pts = daysMap.get(rs.getInt("dow_id"));
							pts.add(tempData);
							daysMap.put(rs.getInt("dow_id"), pts);
							
						}  
							
					  
					} 
							return daysMap;
				}
				
			});
			
			
			logger.info("[END] [getScheduleDetails] [Schedule DAO LAYER] daysMap"+ daysMap);
			
			JSONObject scheduleInfo = new JSONObject();
			
			scheduleInfo = jdbcTemplate.query(GET_SCHEDULE_MODULE_DETAILS, new Object[]{addScheduleDetails.getScheduleId(), userDetails.getUserId(), userDetails.getTimeZone()}, new ResultSetExtractor<JSONObject>() {
				@Override
				public JSONObject extractData(ResultSet rs) throws SQLException,DataAccessException {
					
					
					ResultSetMetaData rsmd=rs.getMetaData();
					int columnCount=rsmd.getColumnCount();
					
					JSONObject scheduleInfo = new JSONObject();
					
					
					while(rs.next()){
							 
						scheduleInfo.put("scheduleName",  rs.getString("scheduleName"));
						scheduleInfo.put("downloadeddate", rs.getString("downloaded_date"));
						scheduleInfo.put("grpCnt", rs.getString("grpCnt"));
						scheduleInfo.put("siteCnt", rs.getString("siteCnt"));
						scheduleInfo.put("deviceCnt", rs.getString("deviceCnt"));
						scheduleInfo.put("customers", rs.getString("customers"));
						scheduleInfo.put("groups", rs.getString("groups"));
						scheduleInfo.put("sites", rs.getString("sites"));
						scheduleInfo.put("customerIds", rs.getString("customerIds"));
						scheduleInfo.put("groupIds", rs.getString("groupIds"));
						scheduleInfo.put("siteIds", rs.getString("siteIds"));
						scheduleInfo.put("parentCustId", rs.getString("parentCustId"));
						scheduleInfo.put("schdlstatus", rs.getString("status"));
						scheduleInfo.put("scheduleId", addScheduleDetails.getScheduleId());	
					 	scheduleInfo.put("forecastinfo", rs.getString("forecast_info"));
					}
					
					return scheduleInfo;
				}
				
			});
			
			logger.info("[END] [getScheduleDetails] [Schedule DAO LAYER] scheduleInfo"+ scheduleInfo);
			
		scheduleInfo.put("timepointsmap", daysMap);
		if(scheduleInfo.isEmpty() || scheduleInfo.size()<=0){
		response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
		response.setCode(ErrorCodes.GET_SCHEDULE_DETAILS_FAILED_ERROR);
		}else{
		response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
		response.setCode(ErrorCodes.GET_SCHEDULE_DETAILS_SUCCESS);
		}
		response.setData(scheduleInfo);
		
			
		} catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			response.setData("error");
			throw new VEMAppException("Internal Error occured at DAO layer");
			
		}
		logger.info("[END] [getScheduleDetails] [Schedule DAO LAYER]"+ response);
		
			 
		return response;
	
	}
	
		
	@Override
	public Response updateSchedule(AddScheduleDetails addScheduleDetails, int userId) throws VEMAppException {
		
		logger.info("[BEGIN] [updateSchedule] [Schedule DAO LAYER]");
		Response response =new Response();
		String scheduleId = addScheduleDetails.getScheduleId();
		SimpleJdbcCall simpleJdbcCall;
		
	    List<AddScheduleDetails> dtoList= addScheduleDetails.getSchdlObjList();
		 
		 logger.info("[BEGIN] list size "+dtoList.size());
		 
			int updatecnt= jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(UPDATE_SCHEDULE, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, addScheduleDetails.getScheduleName());
					ps.setInt(2, userId);
					ps.setString(3, scheduleId);
					return ps;
				}
			});
			
			logger.info("updatecnt:::"+ updatecnt);
			
			updatecnt= jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(
						Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(DELETE_SCHEDULE_TIME_PTS, Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, userId);
					ps.setString(2, scheduleId);
					return ps;
				}
			});
			
			logger.info("updatecnt:::"+ updatecnt);
			

	  	 int updateCount[]= jdbcTemplate.batchUpdate(INSERT_INTO_TIME_POINTS, new BatchPreparedStatementSetter() {
	  			@Override
				public int getBatchSize() {
					return dtoList.size();
				}

				@Override
				public void setValues(java.sql.PreparedStatement ps, int i)
						throws SQLException {

					AddScheduleDetails schdlDto = dtoList.get(i);					
					ps.setString(1, scheduleId);
					ps.setString(2, schdlDto.getDayId());
					ps.setString(3, schdlDto.getTime());
					ps.setString(4, schdlDto.getAm());
					ps.setString(5, schdlDto.getHtpoint() );
					ps.setString(6, schdlDto.getHtunit());
					ps.setString(7, schdlDto.getClpoint());
					ps.setString(8, schdlDto.getClunit());
					ps.setInt(9, userId );
					ps.setInt(10, userId );			
			   }
			  });
	  	 
	  	 
	  	AuditRequest auditRequest= new AuditRequest();
	  	
	  	auditRequest.setUserId(userId);
	  	auditRequest.setUserAction("Updated");
		auditRequest.setLocation("");
		auditRequest.setServiceId("7");
		auditRequest.setDescription("Schedule updated");
		auditRequest.setServiceSpecificId(Integer.parseInt(scheduleId));
	  	auditDAO.insertAuditLog(auditRequest);
	  	
	  	 
			try{
			  	 
			    simpleJdbcCall = new SimpleJdbcCall(dataSource);
			    simpleJdbcCall.withProcedureName(UPDATE_SCHEDULE_DEVICE_DETAILS);
			    
			  
				Map<String,Object> inputParams=new HashMap<String, Object>();
				Map<String,Object> outParameters ;
				
				logger.debug("Update device time points "+addScheduleDetails.getScheduleId());
				
				inputParams.put("in_schedule_id", addScheduleDetails.getScheduleId());
				inputParams.put("in_userId", userId);
				
		        outParameters = simpleJdbcCall.execute(inputParams);
		        
		        
		        Iterator<Entry<String, Object>> itr =null;
		        JSONObject responseObj=new JSONObject();
		        Schedule schedule =new Schedule();
		        schedule.setScheduleId(addScheduleDetails.getScheduleId());
		      
				outParameters =simpleJdbcCall.execute(inputParams);
				itr = outParameters.entrySet().iterator();
				ArrayList deviceArr=null;
				ArrayList awsDeviceArr=null;
				while (itr.hasNext()) {
					
					 Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
				     String key = entry.getKey();
				        
					if(key.equals(RESULT_SET_1))
			        {
			        	Object value = (Object) entry.getValue();
			        	 deviceArr=(ArrayList)value;
					}
					
					if(key.equals(RESULT_SET_2))
			        {
			        	Object value = (Object) entry.getValue();
			        	awsDeviceArr=(ArrayList)value;
			        	
					}
				       
				}
				
				logger.debug("Update device time points deviceArr "+deviceArr);
				
				
				if(deviceArr !=null && deviceArr.size()>0){
					
					final ArrayList deviceArrfinal=deviceArr;
					logger.info("deviceArrfinal.size()*"+deviceArrfinal.size());
			        response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_STATUS_APPLY_SCHEDULE);
					responseObj.put("Flag", "insert");
					response.setData(responseObj);
					Executor ex= Executors.newCachedThreadPool();
					CompletionService<Long> cs = new ExecutorCompletionService<Long>(ex);
					cs.submit(new Callable(){
						public Response call() throws Exception{
							return applyDeviceJSON(schedule,deviceArrfinal,0,userId);
						}
					});
					 
				}
				
				
					if(awsDeviceArr !=null && awsDeviceArr.size()>0){
					
						final ArrayList awsDeviceArrfinal=awsDeviceArr;
					logger.info("deviceArrfinal.size()*"+awsDeviceArrfinal.size());
			        response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_STATUS_APPLY_SCHEDULE);
					responseObj.put("Flag", "insert");
					response.setData(responseObj);
					Executor ex= Executors.newCachedThreadPool();
					CompletionService<Long> cs = new ExecutorCompletionService<Long>(ex);
					cs.submit(new Callable(){
						public Response call() throws Exception{
							return applyOnAWSIOTDevice(schedule,awsDeviceArrfinal,0,userId);
						}
					});
					
					}
				
			} catch(Exception e){
				e.printStackTrace();
			}
        
	  		if(updatecnt<0  ){
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.UPDATE_SCHEDULE_FAILED);
			}else{
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.UPDATE_SCHEDULE_SUCCESS);
			}

	  	  logger.info("[END] [updateSchedule] [Schedule DAO LAYER]"+scheduleId);

		return response;
	}
	
	@Override
	public Response deleteSchedule(AddScheduleDetails addScheduleDetails, int userId) throws VEMAppException {

		Response response = new Response();
		SimpleJdbcCall simpleJdbcCall;
		try {

			logger.info("[BEGIN] [deleteSchedule] [Schedule DAO Impl]");
			if (validateScheduleIsMappedToDevice(Integer.parseInt(addScheduleDetails.getScheduleId())) == 0) {

				if (validateScheduleIsMappedToForecast(Integer.parseInt(addScheduleDetails.getScheduleId())) == 0) {
					// Executing the procedure.
					simpleJdbcCall = new SimpleJdbcCall(dataSource);
					simpleJdbcCall.withProcedureName(DELETE_SCHEDULE_DETAILS);

					/*
					 * Adding all the input parameter values to a hashmap.
					 */
					Map<String, Object> inputParams = new HashMap<String, Object>();
					Map<String, Object> outParameters;

					logger.debug("Deleting schedule details " + addScheduleDetails.getScheduleId());

					inputParams.put("in_schedule_id", addScheduleDetails.getScheduleId());
					inputParams.put("in_userId", userId);

					outParameters = simpleJdbcCall.execute(inputParams);

					logger.debug(
							"[DEBUG] Executing sp_update_customer_status procedure. with input param " + inputParams);

					int updateFlag = (int) outParameters.get("update_status");

					if (updateFlag > 0) {

						response.setCode(ErrorCodes.DELETE_SCHEDULE_SUCCESS);
						response.setData("");
						response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());

					} else {
						response.setCode(ErrorCodes.DELETE_SCHEDULE_FAILED);
						response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
						response.setData("");

					}
					logger.info("procedure update status " + updateFlag);
				} else {
					response.setCode(ErrorCodes.DELETE_SCHEDULE_FAILED_FORECAST_MAPPED);
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setData("");
				}
			} else {
				response.setCode(ErrorCodes.DELETE_SCHEDULE_FAILED_DEVICE_MAPPED);
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setData("");
			}
		} catch (Exception e) {
			logger.error("", e);
			response.setCode(ErrorCodes.ERROR_CUSTOMER_FATAL);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_CUSTOMER_FATAL, logger, e);
		}

		logger.info("[END] [deleteSchedule] [Schedule DAO Impl]");
		return response;

	}
	
	
	public Response applyDeviceJSON(Schedule schedule,ArrayList deviceList,int countval,int userId)
			throws VEMAppException {
		logger.info("[BEGIN] [applyDeviceJSON] [Schedule DAO LAYER]");
		logger.info("In method ---------------------loop*");
		Response response =new Response();
		ArrayList devicefailList=new ArrayList();
		String status="";
		try {
			
						
			//Executing the procedure.
		//logger.info("calling the proc "+FETCH_SELECT_SCHEDULE_APPLY_DEVICE+" with params "+schedule.getScheduleId());
			HashMap <String, HashMap>devicejson=  new  <String, HashMap> HashMap();
			
			//Executing the procedure.
		for (int i=0;i<deviceList.size();i++){
			HashMap map=(HashMap)deviceList.get(i);
			String deviceId=map.get("xcspec_device_id")==null ? "":map.get("xcspec_device_id").toString();
    		logger.info("value*************"+map.get("device_id"));
    		if(schedule==null){
    			if(map.get("schedule_id")==null){
    				schedule = new Schedule();
    				schedule.setScheduleId("0");
    			}else{
    				logger.info("scheduleId*************"+map.get("schedule_id").toString());
    				schedule = new Schedule();
    				schedule.setScheduleId(map.get("schedule_id").toString());
    			} 
    					
    					
    		 }
				HashMap<String, JSONArray> daysMap=new HashMap<>();
				HashMap<String, JSONArray> cdaysMap=new HashMap<>();
				response= jdbcTemplate.query(FETCH_SELECT_SCHEDULE_APPLY_DEVICE, new Object[]{schedule.getScheduleId(),map.get("device_id")}, new ResultSetExtractor<Response>() {
					@Override
					public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
						
						Response response=new Response();
						ResultSetMetaData rsmd=rs.getMetaData();
						int columnCount=rsmd.getColumnCount();
					
						JSONObject hprogramJSON = new JSONObject();
						JSONObject cprogramJSON = new JSONObject();
						
						String scheduleConfig="7";
						String program="H";
						String cprogram="C";
						String units="F";
						String perDay="4";
						String schedule="schedule";
						int period=0;
						int cperiod=0;
						
						
						while(rs.next()){
							JSONObject tempData=new JSONObject();
							JSONObject ctempData=new JSONObject();
							JSONArray resultAry=new JSONArray();
							JSONArray cresultAry=new JSONArray();
							tempData.put("start_time",rs.getString("start_time"));
							tempData.put("temp_set",rs.getString("htng_pt"));
							
							ctempData.put("start_time",rs.getString("start_time"));
							ctempData.put("temp_set",rs.getString("clng_pt"));
							
							if(!daysMap.containsKey(rs.getString("day"))){
								 period=0;
								 cperiod=0;
								 tempData.put("period_xy",""+period++);
								 ctempData.put("period_xy",""+cperiod++);
								 resultAry.add(tempData);
								 cresultAry.add(ctempData);
								 daysMap.put(rs.getString("day"), resultAry);
								 cdaysMap.put(rs.getString("day"), cresultAry); 
								 							
							}else{
							    tempData.put("period_xy",""+period++);
							    ctempData.put("period_xy",""+cperiod++);
								JSONArray pts = daysMap.get(rs.getString("day"));
								JSONArray cpts = cdaysMap.get(rs.getString("day"));
								pts.add(tempData);
								cpts.add(ctempData);
								daysMap.put(rs.getString("day"), pts);
								cdaysMap.put(rs.getString("day"), cpts);
								
							}
						 
						}
						
						hprogramJSON.put("schedule_config", scheduleConfig);
						hprogramJSON.put("program", program);
						hprogramJSON.put("t_units", units);
						hprogramJSON.put("periods_per_day", perDay);
						hprogramJSON.put("schedule", daysMap);
						  
											
						cprogramJSON.put("schedule_config", scheduleConfig);
						cprogramJSON.put("program", cprogram);
						cprogramJSON.put("t_units", units);
						cprogramJSON.put("periods_per_day", perDay);
						cprogramJSON.put("schedule", cdaysMap);
					
						 RestClient restClient= new RestClient();
						 HashMap mapJson= new HashMap();
						 logger.info("cprogramJSON.toJSONString()::::::::::::"+cprogramJSON.toJSONString());
						 logger.info("hprogramJSON.toJSONString()::::::::::::"+hprogramJSON.toJSONString());
						 String responseString=restClient.setSchedule(deviceId, cprogramJSON.toJSONString(),hprogramJSON.toJSONString());
						 logger.info("responseString::::::::::::"+responseString);
						 
						 mapJson.put("cooling", cprogramJSON);
						 mapJson.put("heat", hprogramJSON);
						 
						 devicejson.put(deviceId+"&&"+map.get("device_id"), mapJson);
						
						 ObjectMapper mapper1 = new ObjectMapper(); try {		
							  logger.info("JSON Object::::::::::::"
						  +mapper1.writeValueAsString(cprogramJSON)); }
						 catch (IOException e1)
						  { // TODO Auto-generated catch block e1.printStackTrace(); 
							 
						  }
						
						return response;
					}
					
				});
			}
			if(devicejson.size() <= 5){
	          logger.info("Thread Starts");
	          Thread.sleep(360000);
	          logger.info("Thread Ends");
	         }
			JSONObject heatJson=null;
			JSONObject coolJson=null;
			HashMap jsonMap=null;
			String deviceData="";
			String deviceHeatData="";
			Iterator deviceIt = devicejson.entrySet().iterator();
			RestClient restClient= new RestClient();
		    while (deviceIt.hasNext()) {
		         Map.Entry pair = (Map.Entry)deviceIt.next();
		     	 String splitdeviceId=pair.getKey().toString();
		    	 jsonMap=(HashMap)devicejson.get(splitdeviceId);
		     	 heatJson=(JSONObject)jsonMap.get("heat");
		       	 coolJson=(JSONObject)jsonMap.get("cooling");
		       	 String xpecdeviceId=splitdeviceId.split("&&")[0];
		       	 String deviceId=splitdeviceId.split("&&")[1];
		     	 JSONObject vhprogramJSON = new JSONObject();
				 JSONObject vcprogramJSON = new JSONObject();
				 
				 heatJson.remove("schedule_config");
				 coolJson.remove("schedule_config");
				 
				 vhprogramJSON.put("1", heatJson);
				 vcprogramJSON.put("1",coolJson);
				 logger.info("xpecdeviceId------------------"+xpecdeviceId);
				 logger.info("deviceId------------------"+deviceId);
				 deviceData=restClient.getSchedule(xpecdeviceId, "C");
		     	 deviceHeatData=restClient.getSchedule(xpecdeviceId, "H");
		     	
		     	 logger.info(" responce deviceData------------------"+deviceData);
		     	 logger.info(" resonse deviceHeatData------------------"+deviceHeatData);
		     	 
		     	ObjectMapper om = new ObjectMapper();
		     	if ((deviceData !=null && deviceData.length() >0 ) && (deviceHeatData!=null && deviceHeatData.length() >0)){
					try {
			            Map<String, Object> coolDeviceData = (Map<String, Object>)(om.readValue(deviceData, Map.class));
			            Map<String, Object> coolApplyData = (Map<String, Object>)(om.readValue(vcprogramJSON.toString(), Map.class));
			            Map<String, Object> heatDeviceData = (Map<String, Object>)(om.readValue(deviceHeatData, Map.class));
			            Map<String, Object> heatApplyData = (Map<String, Object>)(om.readValue(vhprogramJSON.toString(), Map.class));
			            
			           
			            logger.info("coolDeviceData------------------"+coolDeviceData);
			            logger.info("coolApplyData------------------"+coolApplyData);
			            logger.info("heatDeviceData------------------"+heatDeviceData);
			            logger.info("heatApplyData------------------"+heatApplyData);
			           
			            if(coolDeviceData.equals(coolApplyData)&& heatDeviceData.equals(heatApplyData)){
			            	logger.info("Appy Success------------------"+coolDeviceData);
			            	 jdbcTemplate.update(UPDATE_SCHEDULE_DEVICE_STATUS, 2,schedule.getScheduleId(),deviceId);
			            	 //jdbcTemplate.update(UPDATE_SCHEDULE_DATE, schedule.getScheduleId());
			            	 
			            	 insertActivityLog(userId, "Updated", "Schedule downloaded successfully",schedule.getScheduleId());
			            	 
			            	 
			            }else{
			            	
			            	 if(countval==2){
			            		 jdbcTemplate.update(UPDATE_SCHEDULE_DEVICE_STATUS, 3,schedule.getScheduleId(),deviceId);
				            	 SimpleJdbcCall 	simpleJdbcCall = new SimpleJdbcCall(dataSource);
				         		 simpleJdbcCall.withProcedureName(INSERT_ALERT_DEVICE);
				            	 Map<String,Object> inputParams=new HashMap<String, Object>();
				            	 inputParams.put("p_device_id", deviceId);
				            	 inputParams.put("p_schedule_id", schedule.getScheduleId());
				         		 simpleJdbcCall.execute(inputParams);
				         		 status="Fail";
			            	 }
			            	 HashMap failmap = new HashMap();
			            	 failmap.put("xcspec_device_id", xpecdeviceId);
			            	 failmap.put("device_id", deviceId);
			            	 devicefailList.add(failmap);
			            }
			          
			        } catch (Exception e) {
			            e.printStackTrace();
			             HashMap failmap = new HashMap();
		            	 failmap.put("xcspec_device_id", xpecdeviceId);
		            	 failmap.put("device_id", deviceId);
		            	 devicefailList.add(failmap);
			            jdbcTemplate.update(UPDATE_SCHEDULE_DEVICE_STATUS, 3,schedule.getScheduleId(),deviceId);
			        }
		     	}else{
		     		 HashMap failmap = new HashMap();
	            	 failmap.put("xcspec_device_id", xpecdeviceId);
	            	 failmap.put("device_id", deviceId);
	            	 devicefailList.add(failmap);
		     	}
			
		    }
		    if(devicefailList.size()>0 && countval <2){
		    	logger.info("count-----------------"+countval);
		    	countval++;
		    	applyDeviceJSON(schedule,devicefailList,countval,userId); 
		    	
		    }
		    if(status.equalsIgnoreCase("Fail")){
		    	jdbcTemplate.update(UPDATE_SCHEDULE_STATUS,4, schedule.getScheduleId());
		    }else{
		    	jdbcTemplate.update(UPDATE_SCHEDULE_STATUS, 3,schedule.getScheduleId());
		    }
			
		} catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			response.setData("error");
			throw new VEMAppException("Internal Error occured at DAO layer");
			
		}
		logger.info("[END] [getScheduleDetails] [Schedule DAO LAYER]"+ response);
		
			 
		return response;
	
	}
	
	
	public void insertActivityLog(int userId, String action, String descr, String scheduleId){

		// Activity log 
		AuditRequest auditRequest= new AuditRequest();
	  	
	  	auditRequest.setUserId(userId);
	  	auditRequest.setUserAction(action);
		auditRequest.setLocation("");
		auditRequest.setServiceId("7");
		auditRequest.setDescription(descr);
		auditRequest.setServiceSpecificId(Integer.parseInt(scheduleId));
	  	auditDAO.insertAuditLog(auditRequest);
	}
	
	@Override
	public Response applyScheduleValidate(GetUserResponse userDetails,Schedule schedule) throws VEMAppException{
		logger.info("[BEGIN] [applySchedule] [Schedule DAO Impl]");
		Response response=new Response();
		SimpleJdbcCall simpleJdbcCall;
		try{
			
			//Executing the procedure.
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<String, Object>();
			logger.info("s*"+userDetails.getUserId());
			
			logger.info("schedule.getScheduleId()** validate -----test**"+schedule.getScheduleId());
			logger.info("schedule.getCustomerId()**"+schedule.getCustomerId());
			logger.info("schedule.getGroupId()**test**"+schedule.getGroupId());
			logger.info("schedule.in_siteId()**test**"+schedule.getSiteId());
			logger.info("schedule.getDeviceId()*"+schedule.getDeviceId());
			
		    inputParams.put("in_userId", userDetails.getUserId());
			inputParams.put("in_scheduleId", schedule.getScheduleId());
			inputParams.put("in_customerId", schedule.getCustomerId());
			inputParams.put("in_groupId", schedule.getGroupId());
			inputParams.put("in_siteId", schedule.getSiteId());
			inputParams.put("in_deviceId", schedule.getDeviceId());
			
			simpleJdbcCall.withProcedureName(APPLY_SCHEDULE_VALIDATE);
			
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(inputParams);
			

			Iterator<Entry<String, Object>> itr = simpleJdbcCallResult.entrySet().iterator();
	
			JSONArray resultAry=new JSONArray();
			JSONObject responseObj=new JSONObject();
			JSONParser parser=new JSONParser();
			
			while (itr.hasNext()) {
				
				 Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
			     String key = entry.getKey();
			        
				if(key.equals(RESULT_SET_1))
		        {
		        	Object value = (Object) entry.getValue();
		        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
		        	resultAry=(org.json.simple.JSONArray)parser.parse(tempAry.toString());
		        	responseObj.put("groupSitesList", resultAry);
		        	
				}
			       
			}
			logger.info("simpleJdbcCallResult.size()*"+resultAry.size());
			if(resultAry.size()==0){
		        response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.INFO_STATUS_APPLY_SCHEDULE);
				responseObj.put("Flag", "insert");
				response.setData(responseObj);
		        
			}else{
				responseObj.put("Flag", "validateFail");
				response.setData(responseObj);
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.INFO_STATUS_APPLY_SCHEDULE);
			}
			
		} 
		catch(Exception e){
			logger.error("",e);
			response.setCode(ErrorCodes.ERROR_STATUS_APPLY_SCHEDULE);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_STATUS_APPLY_SCHEDULE, logger, e);
		}
		
		logger.info("[END] [getCustomerList] [Schedule DAO Impl]");
		return response;
	}
	

	@Override
	public int addDeviceSchedule(AddScheduleDetails addScheduleDetails, int userId, boolean isCustom) throws VEMAppException {
		
		logger.info("[BEGIN] [addDeviceSchedule] [Schedule DAO LAYER]");
		int dvcscheduleId ;
		int scheduleId ;
		SimpleJdbcCall simpleJdbcCall;
		Response response =new Response();
		
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_INTO_SCHEDULE, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, addScheduleDetails.getScheduleName());
				ps.setInt(2, userId);
				ps.setInt(3, userId);
				return ps;
			}
		}, keyHolder);

		scheduleId= keyHolder.getKey().intValue();
		
		List<AddScheduleDetails> dtoList= addScheduleDetails.getSchdlObjList();
		 
		 logger.info("[BEGIN] list size "+dtoList.size());

	  	  jdbcTemplate.batchUpdate(INSERT_INTO_TIME_POINTS, new BatchPreparedStatementSetter() {
	  			@Override
				public int getBatchSize() {
					return dtoList.size();
				}

				@Override
				public void setValues(java.sql.PreparedStatement ps, int i)
						throws SQLException {

					AddScheduleDetails schdlDto = dtoList.get(i);					
					ps.setLong(1, scheduleId);
					ps.setString(2, schdlDto.getDayId());
					ps.setString(3, schdlDto.getTime());
					ps.setString(4, schdlDto.getAm());
					ps.setString(5, schdlDto.getHtpoint() );
					ps.setString(6, schdlDto.getHtunit());
					ps.setString(7, schdlDto.getClpoint());
					ps.setString(8, schdlDto.getClunit());
					ps.setInt(9, userId );
					ps.setInt(10, userId );			
			   }
	  	  });
	  	  
	  	  if (isCustom) {
	  		  return scheduleId;
	  	  }
	  	  
	  	int updatecnt= jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(UPDATE_DEVICE_SCHEDULE, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, addScheduleDetails.getDeviceId());
				return ps;
			}
		});
		
		logger.info("updatecnt:::"+ updatecnt);
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(INSERT_INTO_DEVICE_SCHEDULE, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, addScheduleDetails.getDeviceId());
				ps.setInt(2, userId);
				ps.setInt(3, userId);
				ps.setInt(4, scheduleId);
				return ps;
			}
		}, keyHolder);

		dvcscheduleId= keyHolder.getKey().intValue();
	   
		 
		 logger.info("[BEGIN] list size "+dtoList.size());

	  	  jdbcTemplate.batchUpdate(INSERT_INTO_DEVICE_TIME_POINTS, new BatchPreparedStatementSetter() {
	  			@Override
				public int getBatchSize() {
					return dtoList.size();
				}

				@Override
				public void setValues(java.sql.PreparedStatement ps, int i)
						throws SQLException {

					AddScheduleDetails schdlDto = dtoList.get(i);					
					ps.setLong(1, dvcscheduleId);
					ps.setString(2, schdlDto.getDayId());
					ps.setString(3, schdlDto.getTime());
					ps.setString(4, schdlDto.getAm());
					ps.setString(5, schdlDto.getHtpoint() );
					ps.setString(6, schdlDto.getHtunit());
					ps.setString(7, schdlDto.getClpoint());
					ps.setString(8, schdlDto.getClunit());
					ps.setInt(9, userId );
					ps.setInt(10, userId );			
			   }
			  });


			try{
			  	 
				addScheduleDetails.setScheduleId(scheduleId+"");
			    simpleJdbcCall = new SimpleJdbcCall(dataSource);
			    simpleJdbcCall.withProcedureName(GET_SCHEDULE_DEVICE_DETAILS);
			    
			  
				Map<String,Object> inputParams=new HashMap<String, Object>();
				Map<String,Object> outParameters ;
				
				logger.debug("Update device time points "+addScheduleDetails.getScheduleId());
				
				inputParams.put("in_deviceId", addScheduleDetails.getDeviceId());
			
				
		        outParameters = simpleJdbcCall.execute(inputParams);
		        
		        
		        Iterator<Entry<String, Object>> itr =null;
		        JSONObject responseObj=new JSONObject();
		        Schedule schedule =new Schedule();
		        schedule.setScheduleId(addScheduleDetails.getScheduleId());  
		      
				outParameters =simpleJdbcCall.execute(inputParams);
				itr = outParameters.entrySet().iterator();
				ArrayList deviceArr=null;
				ArrayList awsDeviceArr=null;
				while (itr.hasNext()) {
					
					 Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
				     String key = entry.getKey();
				        
					if(key.equals(RESULT_SET_1))
			        {
			        	Object value = (Object) entry.getValue();
			        	 deviceArr=(ArrayList)value;
					}
					
					if(key.equals(RESULT_SET_2))
			        {
			        	Object value = (Object) entry.getValue();
			        	awsDeviceArr=(ArrayList)value;
			        	
					}
				       
				}
				
				logger.debug("Update device time points deviceArr "+deviceArr);
				
				
				if(deviceArr !=null && deviceArr.size()>0){
					
					final ArrayList deviceArrfinal=deviceArr;
					logger.info("deviceArrfinal.size()*"+deviceArrfinal.size());
			        response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_STATUS_APPLY_SCHEDULE);
					responseObj.put("Flag", "insert");
					response.setData(responseObj);
					Executor ex= Executors.newCachedThreadPool();
					CompletionService<Long> cs = new ExecutorCompletionService<Long>(ex);
					cs.submit(new Callable(){
						public Response call() throws Exception{
							return applyDeviceJSON(schedule,deviceArrfinal,0,userId);
						}
					});
					 
					AuditRequest auditRequest= new AuditRequest();
				  	
				  	auditRequest.setUserId(userId);
				  	auditRequest.setUserAction("Updated");
				  	auditRequest.setLocation("");
					auditRequest.setServiceId("4");
					auditRequest.setDescription("Custom Schedule has been applied");
					
					LinkedCaseInsensitiveMap map = (LinkedCaseInsensitiveMap) deviceArrfinal.get(0);
					int serviceSpecificId = (Integer)map.get("device_id");
					//System.out.println("===================="+serviceSpecificId);
					auditRequest.setServiceSpecificId(serviceSpecificId);
					auditDAO.insertAuditLog(auditRequest);
				}
				
				
					if(awsDeviceArr !=null && awsDeviceArr.size()>0){
					
						final ArrayList awsDeviceArrfinal=awsDeviceArr;
					logger.info("deviceArrfinal.size()*"+awsDeviceArrfinal.size());
			        response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.INFO_STATUS_APPLY_SCHEDULE);
					responseObj.put("Flag", "insert");
					response.setData(responseObj);
					Executor ex= Executors.newCachedThreadPool();
					CompletionService<Long> cs = new ExecutorCompletionService<Long>(ex);
					cs.submit(new Callable(){
						public Response call() throws Exception{
							return applyOnAWSIOTDevice(schedule,awsDeviceArrfinal,0,userId);
						}
					});
					
					AuditRequest auditRequest= new AuditRequest();
				  	
				  	auditRequest.setUserId(userId);
				  	auditRequest.setUserAction("Updated");
				  	auditRequest.setLocation("");
					auditRequest.setServiceId("4");
					auditRequest.setDescription("Custom Schedule has been applied");
					
					LinkedCaseInsensitiveMap map = (LinkedCaseInsensitiveMap) awsDeviceArrfinal.get(0);
					int serviceSpecificId = (Integer)map.get("device_id");
					//System.out.println("===================="+serviceSpecificId);
					auditRequest.setServiceSpecificId(serviceSpecificId);
					auditDAO.insertAuditLog(auditRequest);
				}
				
			} catch(Exception e){
				logger.error(e);
			}
		
	  	  logger.info("[END] [addDeviceSchedule] [Schedule DAO LAYER]"+dvcscheduleId);

		return dvcscheduleId;
	}
	public    String  getDeviceJsonResponse(String iotRespose, String macId){
		//iotRespose="{\"desired\":{\"cs\":\"64\",\"prU\":[\"[1,0,4,6,0,60,90]\",\"[1,0,4,6,1,120,90]\",\"[1,0,4,6,2,300,90]\",\"[1,0,4,6,3,480,90]\"],\"r\":\"1\",\"ome\":{\"a\":\"on\",\"c\":\"on\",\"eh\":\"off\",\"h\":\"on\"},\"mt\":\"Hi Chenna\",\"fm\":\"on\",\"me\":\"on\",\"cl\":{\"d\":2,\"h\":\"00:51\"},\"sm\":\"7d\",\"hs\":\"60.0\",\"om\":\"a\",\"tc\":\"3\"},\"reported\":{\"rs\":[1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0],\"tu\":\"F\",\"pr\":[\"0,0,4,0,0,600,150\",\"0,0,4,0,1,620,160\",\"0,0,4,0,2,640,170\",\"0,0,4,0,3,680,180\",\"0,0,4,1,0,360,156\",\"0,0,4,1,1,480,170\",\"0,0,4,1,2,1080,156\",\"0,0,4,1,3,1320,156\",\"0,0,4,2,0,360,156\",\"0,0,4,2,1,480,170\",\"0,0,4,2,2,1080,156\",\"0,0,4,2,3,1320,156\",\"0,0,4,3,0,360,156\",\"0,0,4,3,1,480,170\",\"0,0,4,3,2,1080,156\",\"0,0,4,3,3,1320,156\",\"0,0,4,4,0,360,156\",\"0,0,4,4,1,480,170\",\"0,0,4,4,2,1080,156\",\"0,0,4,4,3,1320,156\",\"0,0,4,5,0,360,156\",\"0,0,4,5,1,480,170\",\"0,0,4,5,2,1080,156\",\"0,0,4,5,3,1320,156\",\"0,0,4,6,0,360,156\",\"0,0,4,6,1,480,170\",\"0,0,4,6,2,1080,156\",\"0,0,4,6,3,1320,156\",\"1,0,4,0,0,360,140\",\"1,0,4,0,1,480,124\",\"1,0,4,0,2,1080,140\",\"1,0,4,0,3,1320,124\",\"1,0,4,1,0,360,140\",\"1,0,4,1,1,480,124\",\"1,0,4,1,2,1080,140\",\"1,0,4,1,3,1320,124\",\"1,0,4,2,0,360,140\",\"1,0,4,2,1,480,124\",\"1,0,4,2,2,1080,140\",\"1,0,4,2,3,1320,124\",\"1,0,4,3,0,360,140\",\"1,0,4,3,1,480,124\",\"1,0,4,3,2,1080,140\",\"1,0,4,3,3,1320,124\",\"1,0,4,4,0,360,140\",\"1,0,4,4,1,480,124\",\"1,0,4,4,2,1080,140\",\"1,0,4,4,3,1320,124\",\"1,0,4,5,0,360,140\",\"1,0,4,5,1,480,124\",\"1,0,4,5,2,1080,140\",\"1,0,4,5,3,1320,124\",\"1,0,4,6,0,360,140\",\"1,0,4,6,1,480,124\",\"1,0,4,6,2,1080,140\",\"1,0,4,6,3,1320,124\"],\"tv\":[\"1.8\",\"5.3\"],\"rt\":\"0.0\",\"ome\":{\"a\":\"on\",\"c\":\"on\",\"eh\":\"off\",\"h\":\"on\"},\"fm\":\"on\",\"dl\":\"on\",\"hs\":\"62.0\",\"fs\":\"on\",\"prU\":[\"[1,0,4,6,0,60,90]\",\"[1,0,4,6,1,120,90]\",\"[1,0,4,6,2,300,90]\",\"[1,0,4,6,3,480,90]\"],\"me\":\"on\",\"sm\":\"7d\",\"om\":\"c\",\"ss\":-43,\"os\":\"c\",\"mt\":\"Hi Chenna\",\"h\":\"d\",\"cl\":{\"d\":2,\"h\":\"05:04\"},\"tc\":\"3\",\"cs\":\"78.0\",\"rc\":[\"B\",\"O\",\"G\",\"W\",\"W2\",\"Y\",\"Y2\"],\"r\":\"1\",\"re\":\"off\",\"zt\":\"80.9\",\"lk\":\"off\"},\"delta\":{\"cs\":\"64\",\"cl\":{\"h\":\"00:51\"},\"hs\":\"60.0\",\"om\":\"a\"}}";
		//String iotRespose="{ \"os\": \"off\", \"hs\": \"62.0\", \"cs\": \"78.0\", \"zt\": \"76.2\", \"fs\": \"i\", \"fm\": \"a\", \"h\": \"e\", \"rs\": [ 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], \"rc\": [ \"B\", \"O\", \"G\", \"W\", \"W2\", \"Y\", \"Y2\" ], \"cl\": { \"h\": \"20:19\", \"d\": 3 }, \"om\": \"h\", \"mt\": \"suscess\", \"me\": \"on\", \"lk\": \"off\", \"pr\": [ [ 0, 0, 4, 0, 0, 425, 140 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 1, 0, 4, 0 ], [ 0, 0, 4, 40, 5, 636, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 1, 8, 4101, 0 ], [ 1, 0, 4, 0, 0, 360, 140 ], [ 1, 0, 4, 0, 0, 0, 0 ], [ 1, 0, 4, 0, 0, 0, 0 ], [ 1, 0, 4, 0, 0, 0, 0 ], [ 1, 0, 4, 0, 0, 0, 0 ], [ 1, 0, 4, 63, 0, 63, 0 ], [ 1, 0, 4, 0, 0, 0, 0 ], [ 1, 0, 4, 0, 0, 0, 0 ], [ 1, 0, 4, 123, 34, 25970, 112 ], [ 1, 0, 4, 48, 44, 11312, 52 ], [ 1, 0, 4, 48, 44, 11312, 48 ], [ 1, 0, 4, 48, 44, 11312, 52 ] ], \"sm\": \"5d\", \"tc\": \"0\", \"tv\": [ 1, 8, 5, 16 ], \"rt\": \"0.0\", \"re\": \"off\", \"dl\": \"on\", \"ss\": -53, \"tu\": \"C\" }";
		JSONObject  responseData= new JSONObject();
		JSONObject  deltaData= new JSONObject();
		JSONObject  deviceResponseData= new JSONObject();
		JSONObject  relayState= new JSONObject();
		JSONObject  tstatClock= new JSONObject();
		JSONArray 	configDataAry=new JSONArray();
		
		JSONArray 	responseDataAry=new JSONArray();
		String fanState="";
		String tempHold="";
		String tstatMode="";
		
		try{
			JSONParser parser=new JSONParser();
			
			logger.info("iotRespose ---------------------*"+iotRespose);
			JSONObject tempJson2=(JSONObject)parser.parse(iotRespose);
			JSONObject tempJson=(JSONObject)tempJson2.get("reported");
			
			   String opstate;
			   if(tempJson.get("os").equals("h")){
			    opstate="HEAT";
			   }else if(tempJson.get("os").equals("c")){
			    opstate="COOL";
			   }else if(tempJson.get("os").equals("off")){
			    opstate="OFF";
			   }else{
			    opstate="AUTO";
			   }
			   
			
			responseData.put("op_state", opstate);
			responseData.put("heat_set", tempJson.get("hs"));
			responseData.put("cool_set", tempJson.get("cs"));
			responseData.put("zone_temp", tempJson.get("zt"));
			responseData.put("calibration", tempJson.get("tc"));
			responseData.put("ome", tempJson.get("ome"));
			
			
			logger.debug("===$$$ Fan state got from device value :"+tempJson.get("fm"));
			if(tempJson.get("fm").equals("on")){
				fanState="ON";
			}else{
				fanState="IDLE";
			} 
			
			logger.debug("===$$$ Fan state converted value :"+fanState);
			responseData.put("fan_state", fanState);
			
			if(tempJson.get("h").equals("e")){
				tempHold="ENABLE";
			}else{
				tempHold="DISABLE";
			} 
			
			responseData.put("temp_hold", tempHold);
			
			
			//responseDataAry.add(responseData);
			
			String[] rsonoff ={"ON", "OFF"};
			JSONArray realState= (JSONArray)tempJson.get("rs");
				relayState.put("relay1", rsonoff[Integer.parseInt(realState.get(0).toString())]);
				relayState.put("relay2",  rsonoff[Integer.parseInt(realState.get(1).toString())]);
				relayState.put("relay3",  rsonoff[Integer.parseInt(realState.get(2).toString())]);
				relayState.put("relay4",  rsonoff[Integer.parseInt(realState.get(3).toString())]);
				relayState.put("relay5",  rsonoff[Integer.parseInt(realState.get(4).toString())]);
				relayState.put("relay6",  rsonoff[Integer.parseInt(realState.get(5).toString())]);
				relayState.put("relay7",  rsonoff[Integer.parseInt(realState.get(6).toString())]);
		
			responseData.put("relay_state", relayState);
			String[] days ={"mo", "tu", "we", "th", "fr", "sa", "su"};
			
			//System.out.println("----------"+tempJson.get("ome"));
			
			JSONObject clockJson=(JSONObject)tempJson.get("cl");
			
			tstatClock.put("current_time", clockJson.get("h"));
			tstatClock.put("current_day", days[Integer.parseInt(clockJson.get("d").toString())]);
			
			responseData.put("tstat_clock", tstatClock);
			
			//JSONObject omeJson=(JSONObject)tempJson.get("ome");
			if(tempJson.get("om").equals("off")){
				tstatMode="OFF";
			}else if(tempJson.get("om").equals("a")){
				tstatMode="AUTO";
			}else if(tempJson.get("om").equals("h")){
				tstatMode="HEAT";
			}else if(tempJson.get("om").equals("c")){
				tstatMode="COOL";
			}
			responseData.put("tstat_mode",  tstatMode);
			
			if(tempJson.get("me").equals("on")){
				responseData.put("tstat_msg",  tempJson.get("mt"));
			}else{
				responseData.put("tstat_msg",  "");
			}
			responseData.put("tempUnits",  tempJson.get("tu"));
			
			
			String keyBLockout;
			
			if(tempJson.get("lk").equals("off")){
				keyBLockout = "DISABLE";
			}else if(tempJson.get("lk").equals("p1")){
				keyBLockout = "PARTIAL1";
			}else if(tempJson.get("lk").equals("p2")){
				keyBLockout = "FULL";
			}else{
				keyBLockout = "DISABLE";
			}
			
			responseData.put("keyBLockout",  keyBLockout);
			logger.info("delta ---------------------*"+tempJson2.get("delta"));
			if(tempJson2.containsKey("delta")){
				JSONObject deltaJson=(JSONObject)tempJson2.get("delta");
				
				if(deltaJson.containsKey("h")){
					if(deltaJson.get("h").equals("e")){
						tempHold="ENABLE";
					}else{
						tempHold="DISABLE";
					} 
					
					deltaData.put("configName", "temp_hold");
					deltaData.put("configValue", tempHold);
					configDataAry.add(deltaData);
				}
				if(deltaJson.containsKey("fm")){
					if(deltaJson.get("fm").equals("a")){
						fanState="IDLE";
					}else{
						fanState="ON";
					} 
					deltaData= new JSONObject();
					deltaData.put("configName", "fan_state");
					deltaData.put("configValue", fanState);
					configDataAry.add(deltaData);
				}
				
				/*if(deltaJson.containsKey("ome")){
					omeJson=(JSONObject)deltaJson.get("ome");
					
					if(omeJson.containsKey("a")){
						if(omeJson.get("a").equals("on")){
							tstatMode="AUTO";
						}else if(deltaJson.get("om").equals("h")){
							tstatMode="HEAT";
						}else if(deltaJson.get("om").equals("c")){
							tstatMode="COOL";
						}else if(deltaJson.get("om").equals("off")){
							tstatMode="OFF";
						}
					}else{
						if(deltaJson.get("om").equals("h")){
							tstatMode="HEAT";
						}else if(deltaJson.get("om").equals("c")){
							tstatMode="COOL";
						}else if(deltaJson.get("om").equals("off")){
							tstatMode="OFF";
						}
					}
					deltaData= new JSONObject();
					deltaData.put("configName", "tstat_mode");
					deltaData.put("configValue", tstatMode);
					configDataAry.add(deltaData);
				}else{*/
					
					if(deltaJson.containsKey("om")){
						if(deltaJson.get("om").equals("h")){
							tstatMode="HEAT";
						}else if(deltaJson.get("om").equals("c")){
							tstatMode="COOL";
						}else if(deltaJson.get("om").equals("a")){
							tstatMode="AUTO";
						}else if(deltaJson.get("om").equals("off")){
							tstatMode="OFF";
						}
						
						
						deltaData= new JSONObject();
						deltaData.put("configName", "tstat_mode");
						deltaData.put("configValue", tstatMode);
						configDataAry.add(deltaData);
					}

				//}
				
				if(deltaJson.containsKey("hs")){
					deltaData= new JSONObject();
					deltaData.put("configName", "heat_set");
					deltaData.put("configValue", deltaJson.get("hs"));
					configDataAry.add(deltaData);
				}
				
				if(deltaJson.containsKey("mt") ){
					deltaData= new JSONObject();
					deltaData.put("configName", "tstat_msg");
					deltaData.put("configValue", deltaJson.get("mt"));
					configDataAry.add(deltaData);
				}
				if(deltaJson.containsKey("cs")){
					deltaData= new JSONObject();
					deltaData.put("configName", "cool_set");
					deltaData.put("configValue", deltaJson.get("cs"));
					configDataAry.add(deltaData);
				}
				
				
				
				if(deltaJson.containsKey("lk")){
					if(deltaJson.get("lk").equals("off")){
						keyBLockout = "DISABLE";
					}else if(deltaJson.get("lk").equals("p1")){
						keyBLockout = "PARTIAL1";
					}else if(deltaJson.get("lk").equals("p2")){
						keyBLockout = "FULL";
					}else{
						keyBLockout = "DISABLE";
					}
					
					deltaData= new JSONObject();
					deltaData.put("configName", "keyBLockout");
					deltaData.put("configValue",keyBLockout);
					configDataAry.add(deltaData);
				}
				
				if(deltaJson.containsKey("tc")){
					deltaData= new JSONObject();
					deltaData.put("configName", "calibration");
					deltaData.put("configValue", deltaJson.get("tc"));
					configDataAry.add(deltaData);
				}
				
				responseData.put("configChanges", configDataAry);
			}
			
			
			
			
			
			
			//responseData.put("button_pressed",  tempJson.get("mt"));
			ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
			
			//responseData.put("datetime", DatesUtil.getCurrentUTCDateTime());
			responseData.put("datetime", getDeviceLastUpdateTime(macId));
			//responseData.put("relay_state", value)
			
			deviceResponseData.put("data", responseData);
			
			
			
			System.out.println(deviceResponseData.toJSONString());
			
			logger.info("delta ---------out------------*"+deviceResponseData.toJSONString());
			//JSONObject deviceData=new JSONObject(); 
			//tempData.put("period_xy",""+period++);
			//return "test";
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
	}
		return deviceResponseData.toJSONString();
	}
	public Response applyOnAWSIOTDevice(Schedule schedule,ArrayList deviceList,int countval,int userId)
			throws VEMAppException {
		logger.info("[BEGIN] [applyDeviceJSON] [Schedule DAO LAYER]");
		logger.info("In method ---------------------loop*");
		Response response =new Response();
		ArrayList devicefailList=new ArrayList();
		String status="";
		
		try {
			
						
		//Executing the procedure.
		  //logger.info("calling the proc "+FETCH_SELECT_SCHEDULE_APPLY_DEVICE+" with params "+schedule.getScheduleId());
			
		HashMap <String, HashMap>devicejson=  new  <String, HashMap> HashMap();	
			//Executing the procedure.
		for (int i=0;i<deviceList.size();i++){
			
			HashMap map=(HashMap)deviceList.get(i);
			String macId=map.get("mac_id")==null ? "":map.get("mac_id").toString();
			
			if(schedule==null){
    			if(map.get("schedule_id")==null){
    				schedule = new Schedule();
    				schedule.setScheduleId("0");
    			}else{
    				logger.info("scheduleId*************"+map.get("schedule_id").toString());
    				schedule = new Schedule();
    				schedule.setScheduleId(map.get("schedule_id").toString());
    			} 
    					
    					
    		 }
			
    		logger.info("value*************"+map.get("device_id"));
    		logger.info("mac_id*****---------------********"+map.get("mac_id"));
    		
    		
				HashMap<String, JSONArray> heatMap=new HashMap<>();
				HashMap<String, JSONArray> coolsMap=new HashMap<>();
				response= jdbcTemplate.query(FETCH_SELECT_SCHEDULE_APPLY_IOTDEVICE, new Object[]{schedule.getScheduleId(),map.get("device_id")}, new ResultSetExtractor<Response>() {
					@Override
					public Response extractData(ResultSet rs) throws SQLException,DataAccessException {
						
						Response response=new Response();
						ResultSetMetaData rsmd=rs.getMetaData();
						 
					
						JSONObject hprogramJSON = new JSONObject();
						JSONObject cprogramJSON = new JSONObject();
						JSONObject sathprogramJSON = new JSONObject();
						JSONObject satcprogramJSON = new JSONObject();
						JSONObject sunhprogramJSON = new JSONObject();
						JSONObject suncprogramJSON = new JSONObject();
						
						
						int period=0;
						int cperiod=0;
						String compareDay="";
						JSONArray heatdoubleAry=new JSONArray();
						JSONArray cooldoubleAry=new JSONArray();
						JSONArray satheatdoubleAry=new JSONArray();
						JSONArray satcooldoubleAry=new JSONArray();
						JSONArray sunheatdoubleAry=new JSONArray();
						JSONArray suncooldoubleAry=new JSONArray();
						
						JSONArray ouputAry=new JSONArray();
						
						while(rs.next()){
							//JSONArray heatAry=new JSONArray();
							//JSONArray coolAry=new JSONArray();
							StringBuilder coolAry = new StringBuilder();
							StringBuilder heatAry = new StringBuilder();
								if(!compareDay.equals(rs.getString("day"))){
									 period=0;
									 cperiod=0;
								}else{
									 ++period;
									 ++cperiod;
								}
								 /*heatAry.add(1);
							     heatAry.add(rs.getInt("t_units"));
							     heatAry.add(4);
								 heatAry.add(rs.getInt("day"));
								 heatAry.add(period);	
								 heatAry.add(rs.getInt("start_time"));	
								 heatAry.add(rs.getInt("htng_pt"));*/
								 
								 heatAry.append(1);
								 heatAry.append(",");
								 heatAry.append(rs.getInt("t_units"));
								 heatAry.append(",");
								 heatAry.append(4);
								 heatAry.append(",");
								 heatAry.append(rs.getInt("day"));
								 heatAry.append(",");
								 heatAry.append(period);
								 heatAry.append(",");
								 heatAry.append(rs.getInt("start_time"));	
								 heatAry.append(",");
								 heatAry.append(rs.getInt("htng_pt"));
								
								 
								/* coolAry.add(0);
								 coolAry.add(rs.getInt("t_units_2"));
								 coolAry.add(4);
								 coolAry.add(rs.getInt("day"));
								 coolAry.add(period);	
								 coolAry.add(rs.getInt("start_time"));	
								 coolAry.add(rs.getInt("clng_pt"));
								 */
								 
								 coolAry.append(0);
								 coolAry.append(",");
								 coolAry.append(rs.getInt("t_units_2"));
								 coolAry.append(",");
								 coolAry.append(4);
								 coolAry.append(",");
								 coolAry.append(rs.getInt("day"));
								 coolAry.append(",");
								 coolAry.append(period);
								 coolAry.append(",");
								 coolAry.append(rs.getInt("start_time"));	
								 coolAry.append(",");
								 coolAry.append(rs.getInt("clng_pt"));
								 
								 if(!(rs.getString("day").equals("5") || rs.getString("day").equals("6"))){
									 //heatdoubleAry.add(heatAry.toJSONString());
									 //cooldoubleAry.add(coolAry.toJSONString());
									 
									 heatdoubleAry.add(heatAry.toString());
									 cooldoubleAry.add(coolAry.toString());
									 //ouputAry.add(coolAry.toString());
									 //ouputAry.add(heatAry.toString());
								 }
								 if(rs.getString("day").equals("5")){
									 satheatdoubleAry.add(heatAry.toString());
									 satcooldoubleAry.add(coolAry.toString());
									 cooldoubleAry.add(coolAry.toString());
									 heatdoubleAry.add(heatAry.toString());
									// ouputAry.add(heatAry.toString());
									// ouputAry.add(coolAry.toString());
								 }
								 if(rs.getString("day").equals("6")){
									 sunheatdoubleAry.add(heatAry.toString());
									 suncooldoubleAry.add(coolAry.toString());
									 cooldoubleAry.add(coolAry.toString());
									 heatdoubleAry.add(heatAry.toString());
									// ouputAry.add(heatAry.toString());
									// ouputAry.add(coolAry.toString());
								 }
								
								 compareDay=rs.getString("day");
								
								
							
						}
						ouputAry.addAll(cooldoubleAry);
						ouputAry.addAll(heatdoubleAry);
						
						hprogramJSON.put("prU", heatdoubleAry);
						cprogramJSON.put("prU",cooldoubleAry);
						/*sathprogramJSON.put("prU", satheatdoubleAry);
						satcprogramJSON.put("prU",satcooldoubleAry);
						sunhprogramJSON.put("prU", sunheatdoubleAry);
						suncprogramJSON.put("prU",suncooldoubleAry);*/
						//logger.info("**************"+hprogramJSON.toJSONString());
						logger.info("*****test*********"+cprogramJSON.toJSONString());
						logger.info("*****test*********"+hprogramJSON.toJSONString());
						logger.info("*****test*********"+ouputAry.toJSONString());
						//logger.info("*****test*********"+satcprogramJSON.toJSONString());
						//logger.info("**************"+sunhprogramJSON.toJSONString());
						//logger.info("**************"+suncprogramJSON.toJSONString());
						//logger.info("*******ouputAry*******"+ouputAry.toJSONString());
						//dataPublisher.publish(macId, cprogramJSON.toJSONString());
						//dataPublisher.publish(macId, hprogramJSON.toJSONString());
						//dataPublisher.publish(macId, cprogramJSON.toJSONString());
						///dataPublisher.publish(macId, satcprogramJSON.toJSONString());
					//	//dataPublisher.publish(macId, sathprogramJSON.toJSONString());
					//	dataPublisher.publish(macId, suncprogramJSON.toJSONString());
						//dataPublisher.publish(macId, sunhprogramJSON.toJSONString());
						
						
						
						 HashMap mapJson= new HashMap();
						 				 
						 mapJson.put("pr", ouputAry);
						 mapJson.put("heat", hprogramJSON);
						 mapJson.put("cool", cprogramJSON);
						 
						 //devicejson.put(map.get("device_id").toString(), mapJson);
						 devicejson.put(macId+"&&"+map.get("device_id"), mapJson);
						 logger.info("*****====Before return stmt====*********");
						return response;
					}
					
				});
			}
		

		 logger.info("*****====After return====*********");
		
		    /*if(devicejson.size() <= 5){
	          logger.info("Thread Starts");
	         Thread.sleep(120000);
	          logger.info("Thread Ends");
	        }*/
		    JSONArray publishData=null;
			JSONObject coolJson=null;
			JSONObject hprogramJSON = new JSONObject();
			JSONObject cprogramJSON = new JSONObject();
			HashMap jsonMap=null;
			String deviceData="";
			String deviceHeatData="";
			logger.info("================= Before iterator ==================");
			Iterator deviceIt = devicejson.entrySet().iterator();
			RestClient restClient= new RestClient();
		    while (deviceIt.hasNext()) {
		    	 Map.Entry pair = (Map.Entry)deviceIt.next();
		     	 String splitdeviceId=pair.getKey().toString();
		    	 jsonMap=(HashMap)devicejson.get(splitdeviceId);
		    	 publishData=(JSONArray)jsonMap.get("pr");
		       	// coolJson=(JSONObject)jsonMap.get("cooling");
		       	 String macId=splitdeviceId.split("&&")[0];
		       	 String deviceId=splitdeviceId.split("&&")[1];
		     	 
				 
				 hprogramJSON =(JSONObject) jsonMap.get("heat");
				 cprogramJSON =(JSONObject) jsonMap.get("cool");
				 
				 logger.info("dataPublisher------------------dataPublisher");
				 
				 ThingResponse thingResponse = new ThingResponse();
				 thingResponse.setDeviceId(Integer.parseInt(deviceId));
				 thingResponse.setMacId(macId);
				 dataUpdater.checkDeltaAndClearDesired(thingResponse);
				 
				 dataPublisher.publish(macId, hprogramJSON.toJSONString());
				 logger.info("Thread Starts");
				 Thread.sleep(150000);
				 
				 dataUpdater.checkDeltaAndClearDesired(thingResponse);
				 
				 dataPublisher.publish(macId, cprogramJSON.toJSONString());
				 logger.info("Thread Starts");
				 Thread.sleep(150000);
					
				 logger.info("macId------------------"+macId);
				 String iotresponse = iotDataHelper.getDeviceShadowState(macId);
		       	logger.info("iotresponse------------------"+iotresponse);
		       	JSONParser parser=new JSONParser();
				
				JSONObject tempJson=(JSONObject)parser.parse(iotresponse);
				
				//logger.info("tempJson------------------"+tempJson);
				//String scheduleJson= tempJson.get("pr").toString();
				JSONObject stateJson=(JSONObject)tempJson.get("state");
				logger.info("stateJson------------------"+stateJson);
				JSONObject reportedJson=(JSONObject)stateJson.get("reported");
				logger.info("reportedJson------------------"+reportedJson);
				JSONArray prArray =(JSONArray)reportedJson.get("pr");
				logger.info("prArray------------------"+prArray);
				logger.info("olddata------------------"+publishData);
				//logger.info("scheduleJson------------------"+scheduleJson);
				logger.info("heatJson------------------"+publishData.toJSONString());
				JSONObject publishDataArry= new JSONObject();
				JSONObject updatedData= new JSONObject();
			
				publishDataArry.put("pr", publishData);
				updatedData.put("pr",prArray);
				
				
		     	ObjectMapper om = new ObjectMapper();
		     	if (publishData !=null && prArray !=null){
					try {
			            Map<String, Object> publishOlddata = (Map<String, Object>)(om.readValue(publishDataArry.toJSONString(), Map.class));
			            Map<String, Object> updatedIOTData = (Map<String, Object>)(om.readValue(updatedData.toJSONString(), Map.class));
			            
			            logger.info("publishOlddata------------------"+publishOlddata);
			            logger.info("updatedIOTData------------------"+updatedIOTData);
			           
			           
			            if(publishOlddata.equals(updatedIOTData)){
			            	logger.info("EUQAL------------------");
			            	 jdbcTemplate.update(UPDATE_SCHEDULE_DEVICE_STATUS, 2,schedule.getScheduleId(),deviceId);
			            	// jdbcTemplate.update(UPDATE_SCHEDULE_DATE, schedule.getScheduleId());
			            	 insertActivityLog(userId, "Updated", "Schedule downloaded successfully",schedule.getScheduleId());
			            }else{
			            	if(countval==1){

				            	 jdbcTemplate.update(UPDATE_SCHEDULE_DEVICE_STATUS, 3,schedule.getScheduleId(),deviceId);
				            	 SimpleJdbcCall 	simpleJdbcCall = new SimpleJdbcCall(dataSource);
				         		 simpleJdbcCall.withProcedureName(INSERT_ALERT_DEVICE);
				            	 Map<String,Object> inputParams=new HashMap<String, Object>();
				            	 logger.info("deviceId----------alert-------"+deviceId);
				            	 logger.info("p_schedule_id---------alert--------"+schedule.getScheduleId());
				            	 inputParams.put("p_device_id", deviceId);
				            	 inputParams.put("p_schedule_id", schedule.getScheduleId());
				         		 simpleJdbcCall.execute(inputParams);
				         		 status="Fail";
			            	 }
			            	 HashMap failmap = new HashMap();
			            	 failmap.put("mac_id", macId);
			            	 failmap.put("device_id", deviceId);
			            	 devicefailList.add(failmap);
			            	 
			            }
			            	
			          
			        } catch (Exception e) {
			        	jdbcTemplate.update(UPDATE_SCHEDULE_DEVICE_STATUS, 3,schedule.getScheduleId(),deviceId);
			        	logger.error("",e);
			        	 HashMap failmap = new HashMap();
		            	 failmap.put("mac_id", macId);
		            	 failmap.put("device_id", deviceId);
		            	 devicefailList.add(failmap);
			            
			        }
		     	}else{
		     		 HashMap failmap = new HashMap();
	            	 failmap.put("mac_id", macId);
	            	 failmap.put("device_id", deviceId);
	            	 devicefailList.add(failmap);
		     		jdbcTemplate.update(UPDATE_SCHEDULE_DEVICE_STATUS, 3,schedule.getScheduleId(),deviceId);
		     	}
			
		    }
		    if(devicefailList.size()>0 && countval <1){
		    	logger.info("count-----------------"+countval);
		    	countval++;
		    	applyOnAWSIOTDevice(schedule,devicefailList,countval,userId); 
		    	
		    }
		    if(status.equalsIgnoreCase("Fail")){
		    	jdbcTemplate.update(UPDATE_SCHEDULE_STATUS,4, schedule.getScheduleId());
		    }else{
		    	jdbcTemplate.update(UPDATE_SCHEDULE_STATUS, 3,schedule.getScheduleId());
		    }
		    
			
		} catch (Exception e) {
			logger.error("",e);
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.GENERAL_APP_ERROR);
			response.setData("error");
			throw new VEMAppException("Internal Error occured at DAO layer");
			
		}
		logger.info("[END] [getScheduleDetails] [Schedule DAO LAYER]"+ response);
		
			 
		return response;
	
	}
	
	/**
	 * 
	 */
	@Override
	public int validateScheduleIsMappedToForecast(int scheduleId) throws VEMAppException {		
		logger.info("[BEGIN] [validateScheduleIsMappedToForecast] [Schedule DAO LAYER]");
		return jdbcTemplate.queryForObject(VALIDATE_IS_SCHEDULE_MAPPED_TO_FORECAST, Integer.class, scheduleId);
		
	}
	
	
	/**
	 * 
	 */
	@Override
	public int validateScheduleIsMappedToDevice(int scheduleId) throws VEMAppException {		
		logger.info("[BEGIN] [validateScheduleIsMappedToDevice] [Schedule DAO LAYER]");
		return jdbcTemplate.queryForObject(VALIDATE_IS_SCHEDULE_MAPPED_TO_DEVICE, Integer.class, scheduleId);
		
	}
	
	public  Map generateXpacJson(String iotRespose){
		//iotRespose="{ \"os\": \"off\", \"hs\": \"62.0\", \"cs\": \"78.0\", \"zt\": \"76.2\", \"fs\": \"i\", \"fm\": \"a\", \"h\": \"e\", \"rs\": [ 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], \"rc\": [ \"B\", \"O\", \"G\", \"W\", \"W2\", \"Y\", \"Y2\" ], \"cl\": { \"h\": \"20:19\", \"d\": 3 }, \"om\": \"h\", \"mt\": \"suscess\", \"me\": \"on\", \"lk\": \"off\", \"pr\": [ [ 0, 0, 4, 0, 0, 425, 140 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 1, 0, 0, 0 ], [ 0, 0, 4, 1, 0, 4, 0 ], [ 0, 0, 4, 1, 5, 636, 0 ], [ 0, 0, 4, 1, 0, 0, 0 ], [ 0, 0, 4, 2, 0, 0, 0 ], [ 0, 0, 4,2, 0, 0, 0 ], [ 0, 0, 4, 2, 0, 0, 0 ], [ 0, 0, 4, 2, 8, 4101, 0 ], [ 1, 0, 4, 3, 0, 360, 140 ], [ 1, 0, 4, 3, 0, 0, 0 ], [ 1, 0, 4, 3, 0, 0, 0 ], [ 1, 0, 4, 3, 0, 0, 0 ], [ 1, 0, 4, 4, 0, 0, 0 ], [ 1, 0, 4, 4, 0, 63, 0 ], [ 1, 0, 4, 4, 0, 0, 0 ], [ 1, 0, 4, 4, 0, 0, 0 ], [ 1, 0, 4, 5, 34, 25970, 112 ], [ 1, 0, 4, 5, 44, 11312, 52 ], [ 1, 0, 4, 5, 44, 11312, 48 ], [ 1, 0, 4, 5, 44, 11312, 52 ] ], \"sm\": \"5d\", \"tc\": \"0\", \"tv\": [ 1, 8, 5, 16 ], \"rt\": \"0.0\", \"re\": \"off\", \"dl\": \"on\", \"ss\": -53, \"tu\": \"C\" }";
		//iotRespose="{ \"reported\": { \"om\": \"h\", \"cl\": { \"h\": \"22:09\", \"d\": 2 }, \"ss\": -67, \"zt\": \"84.0\", \"hs\": \"55.0\", \"fm\": \"a\", \"ome\": { \"h\": \"on\", \"c\": \"on\", \"a\": \"on\", \"eh\": \"off\" }, \"os\": \"off\", \"cs\": \"85.0\", \"fs\": \"i\", \"h\": \"d\", \"rs\": [ 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], \"rc\": [ \"B\", \"O\", \"G\", \"W\", \"W2\", \"Y\", \"Y2\" ], \"me\": \"off\", \"mt\": \"\", \"lk\": \"off\", \"pr\": [ \"0,0,4,0,0,540,144\", \"0,0,4,0,1,600,146\", \"0,0,4,0,2,660,148\", \"0,0,4,0,3,1320,170\", \"0,0,4,1,0,540,144\", \"0,0,4,1,1,600,146\", \"0,0,4,1,2,660,148\", \"0,0,4,1,3,1320,170\", \"0,0,4,2,0,540,144\", \"0,0,4,2,1,600,146\", \"0,0,4,2,2,660,148\", \"0,0,4,2,3,1320,170\", \"0,0,4,3,0,540,144\", \"0,0,4,3,1,600,146\", \"0,0,4,3,2,660,148\", \"0,0,4,3,3,1320,170\", \"0,0,4,4,0,540,144\", \"0,0,4,4,1,600,146\", \"0,0,4,4,2,660,148\", \"0,0,4,4,3,1320,170\", \"0,0,4,5,0,540,144\", \"0,0,4,5,1,600,146\", \"0,0,4,5,2,660,148\", \"0,0,4,5,3,1320,170\", \"0,0,4,6,0,540,144\", \"0,0,4,6,1,600,146\", \"0,0,4,6,2,660,148\", \"0,0,4,6,3,1320,170\", \"1,0,4,0,0,540,136\", \"1,0,4,0,1,600,138\", \"1,0,4,0,2,660,140\", \"1,0,4,0,3,1320,110\", \"1,0,4,1,0,540,136\", \"1,0,4,1,1,600,138\", \"1,0,4,1,2,660,140\", \"1,0,4,1,3,1320,110\", \"1,0,4,2,0,540,136\", \"1,0,4,2,1,540,138\", \"1,0,4,2,2,660,140\", \"1,0,4,2,3,1320,110\", \"1,0,4,3,0,540,136\", \"1,0,4,3,1,600,138\", \"1,0,4,3,2,660,140\", \"1,0,4,3,3,1320,110\", \"1,0,4,4,0,540,136\", \"1,0,4,4,1,600,138\", \"1,0,4,4,2,660,140\", \"1,0,4,4,3,1320,110\", \"1,0,4,5,0,540,136\", \"1,0,4,5,1,600,136\", \"1,0,4,5,2,660,140\", \"1,0,4,5,3,1320,110\", \"1,0,4,6,0,540,136\", \"1,0,4,6,1,600,138\", \"1,0,4,6,2,660,140\", \"1,0,4,6,3,1320,110\" ], \"tc\": \"0\", \"tv\": [ \"1.8\", \"5.3\" ], \"re\": \"off\", \"rt\": \"0.0\", \"dl\": \"on\", \"tu\": \"F\" } }";
		//String iotRespose="{ \"reported\": { \"om\": \"h\", \"cl\": { \"h\": \"06:25\", \"d\": 2 }, \"ss\": -57, \"zt\": \"72.1\", \"hs\": \"55.0\", \"fm\": \"a\", \"ome\": { \"h\": \"on\", \"c\": \"on\", \"a\": \"on\", \"eh\": \"off\" }, \"os\": \"off\", \"cs\": \"85.0\", \"fs\": \"i\", \"h\": \"d\", \"rs\": [ 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], \"rc\": [ \"B\", \"O\", \"G\", \"W\", \"W2\", \"Y\", \"Y2\" ], \"me\": \"off\", \"mt\": \"\", \"lk\": \"off\", \"pr\": [ \"0,0,4,0,0,570,144\", \"0,0,4,0,1,600,146\", \"0,0,4,0,2,660,148\", \"0,0,4,0,3,1320,170\", \"0,0,4,1,0,570,144\", \"0,0,4,1,1,600,146\", \"0,0,4,1,2,660,148\", \"0,0,4,1,3,1320,170\", \"0,0,4,2,0,570,144\", \"0,0,4,2,1,600,146\", \"0,0,4,2,2,660,148\", \"0,0,4,2,3,1320,170\", \"0,0,4,3,0,570,144\", \"0,0,4,3,1,600,146\", \"0,0,4,3,2,660,148\", \"0,0,4,3,3,1320,170\", \"0,0,4,4,0,540,144\", \"0,0,4,4,1,600,146\", \"0,0,4,4,2,660,148\", \"0,0,4,4,3,1320,170\", \"0,0,4,5,0,540,144\", \"0,0,4,5,1,600,146\", \"0,0,4,5,2,660,148\", \"0,0,4,5,3,1320,170\", \"0,0,4,6,0,540,144\", \"0,0,4,6,1,600,146\", \"0,0,4,6,2,660,148\", \"0,0,4,6,3,1320,170\", \"1,0,4,0,0,540,136\", \"1,0,4,0,1,600,138\", \"1,0,4,0,2,660,140\", \"1,0,4,0,3,1320,110\", \"1,0,4,1,0,540,136\", \"1,0,4,1,1,600,138\", \"1,0,4,1,2,660,140\", \"1,0,4,1,3,1320,110\", \"1,0,4,2,0,540,136\", \"1,0,4,2,1,540,138\", \"1,0,4,2,2,660,140\", \"1,0,4,2,3,1320,110\", \"1,0,4,3,0,540,136\", \"1,0,4,3,1,600,138\", \"1,0,4,3,2,660,140\", \"1,0,4,3,3,1320,110\", \"1,0,4,4,0,540,136\", \"1,0,4,4,1,600,138\", \"1,0,4,4,2,660,140\", \"1,0,4,4,3,1320,110\", \"1,0,4,5,0,540,136\", \"1,0,4,5,1,600,136\", \"1,0,4,5,2,660,140\", \"1,0,4,5,3,1320,110\", \"1,0,4,6,0,570,136\", \"1,0,4,6,1,600,138\", \"1,0,4,6,2,660,140\", \"1,0,4,6,3,1320,110\" ], \"tc\": \"0\", \"tv\": [ \"1.8\", \"5.3\" ], \"re\": \"off\", \"rt\": \"0.0\", \"dl\": \"on\", \"tu\": \"F\" } }";
		JSONArray 	responseDataAry=new JSONArray();
		JSONArray heatAry=new JSONArray();
		HashMap responseMap = new HashMap();
		
		try{
			JSONParser parser=new JSONParser();
			
			JSONObject tempJson2=(JSONObject)parser.parse(iotRespose);
			JSONObject tempJson=(JSONObject)tempJson2.get("reported");
			System.out.println("-----------"+tempJson);
			heatAry =(JSONArray)tempJson.get("pr");
			System.out.println("-----------"+heatAry);
			HashMap<String, JSONArray> daysMap=new HashMap<>();
			HashMap<String, JSONArray> cdaysMap=new HashMap<>();
			
			JSONObject hprogramJSON = new JSONObject();
			JSONObject cprogramJSON = new JSONObject();
			JSONObject heatJSON = new JSONObject();
			JSONObject coolJSON = new JSONObject();
			JSONObject scheduleHeatJSON = new JSONObject();
			JSONObject scheduleCoolJSON = new JSONObject();
			
			String scheduleConfig="7";
			String program="H";
			String cprogram="C";
			String units="F";
			String perDay="4";
			String schedule="schedule";
			int period=0;
			int cperiod=0;
			String[] days ={"mo", "tu", "we", "th", "fr", "sa", "su"};
			
			System.out.println("-----size------"+heatAry.size());
			for (int i=0;i<heatAry.size();i++){
				
				JSONArray resultAry=new JSONArray();
				JSONArray cresultAry=new JSONArray();
				String  dataArryString=(String)heatAry.get(i);
				String  dataArry[]=dataArryString.split(",");
				JSONObject tempData=new JSONObject();
				JSONObject ctempData=new JSONObject();
				String setpoint= (String)dataArry[0].toString();
	
		        int hours=Integer.parseInt(dataArry[5])/60;
	        	String minuts=(Integer.parseInt(dataArry[5])%60)+"";
	        	if(minuts.equalsIgnoreCase("0")){
	        		 minuts="00"; 
	        	}     
				
				 
				if(setpoint.equals("0")){
					 ctempData.put("start_time",hours+":"+minuts);
					 ctempData.put("temp_set",Integer.parseInt(dataArry[6])/2);
					 ctempData.put("period_xy",dataArry[4]);
					
					 System.out.println("-----ff------"+ctempData);
					 if(!cdaysMap.containsKey(days[Integer.parseInt(dataArry[3].toString())])){
						 cresultAry.add(ctempData);
						
					 }else{
						 cresultAry=(JSONArray)cdaysMap.get(days[Integer.parseInt(dataArry[3].toString())]);
						 cresultAry.add(ctempData);
						
					 }
					 cdaysMap.put(days[Integer.parseInt(dataArry[3].toString())], cresultAry); 
				}else{
					tempData.put("start_time",hours+":"+minuts);
					tempData.put("temp_set",Integer.parseInt(dataArry[6])/2);
					tempData.put("period_xy",dataArry[4]);
					if(!daysMap.containsKey(days[Integer.parseInt(dataArry[3].toString())])){
						resultAry.add(tempData);
					}else{
						resultAry=(JSONArray)daysMap.get(days[Integer.parseInt(dataArry[3].toString())]);
						resultAry.add(tempData);
					}
					daysMap.put(days[Integer.parseInt(dataArry[3].toString())], resultAry);
				}
			}
			
			 System.out.println("-----map------"+cdaysMap.size());
			hprogramJSON.put("program", program);
			hprogramJSON.put("t_units", "F");
			hprogramJSON.put("periods_per_day", perDay);
			hprogramJSON.put("schedule", daysMap);
			heatJSON.put("1", hprogramJSON);  
			//scheduleHeatJSON.put("scheduleCool", heatJSON.toJSONString());					
			
			cprogramJSON.put("program", cprogram);
			cprogramJSON.put("t_units", units);
			cprogramJSON.put("periods_per_day", perDay);
			cprogramJSON.put("schedule", cdaysMap);
			coolJSON.put("1", cprogramJSON);
			//scheduleCoolJSON.put("scheduleHeat", coolJSON.toJSONString());
			responseMap.put("heat",heatJSON.toJSONString());
			responseMap.put("cool",coolJSON.toJSONString());
			
			
			System.out.println("heat*************"+responseMap.get("heat"));
			System.out.println("cool*************"+responseMap.get("cool"));
			
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
	}
		return responseMap;
		
	}
	
	public Map<String,JSONArray> generateXpacJsonDefault(String iotRespose){
		//iotRespose="{ \"os\": \"off\", \"hs\": \"62.0\", \"cs\": \"78.0\", \"zt\": \"76.2\", \"fs\": \"i\", \"fm\": \"a\", \"h\": \"e\", \"rs\": [ 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], \"rc\": [ \"B\", \"O\", \"G\", \"W\", \"W2\", \"Y\", \"Y2\" ], \"cl\": { \"h\": \"20:19\", \"d\": 3 }, \"om\": \"h\", \"mt\": \"suscess\", \"me\": \"on\", \"lk\": \"off\", \"pr\": [ [ 0, 0, 4, 0, 0, 425, 140 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 0, 0, 0, 0 ], [ 0, 0, 4, 1, 0, 0, 0 ], [ 0, 0, 4, 1, 0, 4, 0 ], [ 0, 0, 4, 1, 5, 636, 0 ], [ 0, 0, 4, 1, 0, 0, 0 ], [ 0, 0, 4, 2, 0, 0, 0 ], [ 0, 0, 4,2, 0, 0, 0 ], [ 0, 0, 4, 2, 0, 0, 0 ], [ 0, 0, 4, 2, 8, 4101, 0 ], [ 1, 0, 4, 3, 0, 360, 140 ], [ 1, 0, 4, 3, 0, 0, 0 ], [ 1, 0, 4, 3, 0, 0, 0 ], [ 1, 0, 4, 3, 0, 0, 0 ], [ 1, 0, 4, 4, 0, 0, 0 ], [ 1, 0, 4, 4, 0, 63, 0 ], [ 1, 0, 4, 4, 0, 0, 0 ], [ 1, 0, 4, 4, 0, 0, 0 ], [ 1, 0, 4, 5, 34, 25970, 112 ], [ 1, 0, 4, 5, 44, 11312, 52 ], [ 1, 0, 4, 5, 44, 11312, 48 ], [ 1, 0, 4, 5, 44, 11312, 52 ] ], \"sm\": \"5d\", \"tc\": \"0\", \"tv\": [ 1, 8, 5, 16 ], \"rt\": \"0.0\", \"re\": \"off\", \"dl\": \"on\", \"ss\": -53, \"tu\": \"C\" }";
		//iotRespose="{ \"reported\": { \"om\": \"h\", \"cl\": { \"h\": \"22:09\", \"d\": 2 }, \"ss\": -67, \"zt\": \"84.0\", \"hs\": \"55.0\", \"fm\": \"a\", \"ome\": { \"h\": \"on\", \"c\": \"on\", \"a\": \"on\", \"eh\": \"off\" }, \"os\": \"off\", \"cs\": \"85.0\", \"fs\": \"i\", \"h\": \"d\", \"rs\": [ 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], \"rc\": [ \"B\", \"O\", \"G\", \"W\", \"W2\", \"Y\", \"Y2\" ], \"me\": \"off\", \"mt\": \"\", \"lk\": \"off\", \"pr\": [ \"0,0,4,0,0,540,144\", \"0,0,4,0,1,600,146\", \"0,0,4,0,2,660,148\", \"0,0,4,0,3,1320,170\", \"0,0,4,1,0,540,144\", \"0,0,4,1,1,600,146\", \"0,0,4,1,2,660,148\", \"0,0,4,1,3,1320,170\", \"0,0,4,2,0,540,144\", \"0,0,4,2,1,600,146\", \"0,0,4,2,2,660,148\", \"0,0,4,2,3,1320,170\", \"0,0,4,3,0,540,144\", \"0,0,4,3,1,600,146\", \"0,0,4,3,2,660,148\", \"0,0,4,3,3,1320,170\", \"0,0,4,4,0,540,144\", \"0,0,4,4,1,600,146\", \"0,0,4,4,2,660,148\", \"0,0,4,4,3,1320,170\", \"0,0,4,5,0,540,144\", \"0,0,4,5,1,600,146\", \"0,0,4,5,2,660,148\", \"0,0,4,5,3,1320,170\", \"0,0,4,6,0,540,144\", \"0,0,4,6,1,600,146\", \"0,0,4,6,2,660,148\", \"0,0,4,6,3,1320,170\", \"1,0,4,0,0,540,136\", \"1,0,4,0,1,600,138\", \"1,0,4,0,2,660,140\", \"1,0,4,0,3,1320,110\", \"1,0,4,1,0,540,136\", \"1,0,4,1,1,600,138\", \"1,0,4,1,2,660,140\", \"1,0,4,1,3,1320,110\", \"1,0,4,2,0,540,136\", \"1,0,4,2,1,540,138\", \"1,0,4,2,2,660,140\", \"1,0,4,2,3,1320,110\", \"1,0,4,3,0,540,136\", \"1,0,4,3,1,600,138\", \"1,0,4,3,2,660,140\", \"1,0,4,3,3,1320,110\", \"1,0,4,4,0,540,136\", \"1,0,4,4,1,600,138\", \"1,0,4,4,2,660,140\", \"1,0,4,4,3,1320,110\", \"1,0,4,5,0,540,136\", \"1,0,4,5,1,600,136\", \"1,0,4,5,2,660,140\", \"1,0,4,5,3,1320,110\", \"1,0,4,6,0,540,136\", \"1,0,4,6,1,600,138\", \"1,0,4,6,2,660,140\", \"1,0,4,6,3,1320,110\" ], \"tc\": \"0\", \"tv\": [ \"1.8\", \"5.3\" ], \"re\": \"off\", \"rt\": \"0.0\", \"dl\": \"on\", \"tu\": \"F\" } }";
		//String iotRespose="{ \"reported\": { \"om\": \"h\", \"cl\": { \"h\": \"06:25\", \"d\": 2 }, \"ss\": -57, \"zt\": \"72.1\", \"hs\": \"55.0\", \"fm\": \"a\", \"ome\": { \"h\": \"on\", \"c\": \"on\", \"a\": \"on\", \"eh\": \"off\" }, \"os\": \"off\", \"cs\": \"85.0\", \"fs\": \"i\", \"h\": \"d\", \"rs\": [ 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], \"rc\": [ \"B\", \"O\", \"G\", \"W\", \"W2\", \"Y\", \"Y2\" ], \"me\": \"off\", \"mt\": \"\", \"lk\": \"off\", \"pr\": [ \"0,0,4,0,0,570,144\", \"0,0,4,0,1,600,146\", \"0,0,4,0,2,660,148\", \"0,0,4,0,3,1320,170\", \"0,0,4,1,0,570,144\", \"0,0,4,1,1,600,146\", \"0,0,4,1,2,660,148\", \"0,0,4,1,3,1320,170\", \"0,0,4,2,0,570,144\", \"0,0,4,2,1,600,146\", \"0,0,4,2,2,660,148\", \"0,0,4,2,3,1320,170\", \"0,0,4,3,0,570,144\", \"0,0,4,3,1,600,146\", \"0,0,4,3,2,660,148\", \"0,0,4,3,3,1320,170\", \"0,0,4,4,0,540,144\", \"0,0,4,4,1,600,146\", \"0,0,4,4,2,660,148\", \"0,0,4,4,3,1320,170\", \"0,0,4,5,0,540,144\", \"0,0,4,5,1,600,146\", \"0,0,4,5,2,660,148\", \"0,0,4,5,3,1320,170\", \"0,0,4,6,0,540,144\", \"0,0,4,6,1,600,146\", \"0,0,4,6,2,660,148\", \"0,0,4,6,3,1320,170\", \"1,0,4,0,0,540,136\", \"1,0,4,0,1,600,138\", \"1,0,4,0,2,660,140\", \"1,0,4,0,3,1320,110\", \"1,0,4,1,0,540,136\", \"1,0,4,1,1,600,138\", \"1,0,4,1,2,660,140\", \"1,0,4,1,3,1320,110\", \"1,0,4,2,0,540,136\", \"1,0,4,2,1,540,138\", \"1,0,4,2,2,660,140\", \"1,0,4,2,3,1320,110\", \"1,0,4,3,0,540,136\", \"1,0,4,3,1,600,138\", \"1,0,4,3,2,660,140\", \"1,0,4,3,3,1320,110\", \"1,0,4,4,0,540,136\", \"1,0,4,4,1,600,138\", \"1,0,4,4,2,660,140\", \"1,0,4,4,3,1320,110\", \"1,0,4,5,0,540,136\", \"1,0,4,5,1,600,136\", \"1,0,4,5,2,660,140\", \"1,0,4,5,3,1320,110\", \"1,0,4,6,0,570,136\", \"1,0,4,6,1,600,138\", \"1,0,4,6,2,660,140\", \"1,0,4,6,3,1320,110\" ], \"tc\": \"0\", \"tv\": [ \"1.8\", \"5.3\" ], \"re\": \"off\", \"rt\": \"0.0\", \"dl\": \"on\", \"tu\": \"F\" } }";
		JSONArray heatAry=new JSONArray();
		Map<String,JSONArray> response = new LinkedHashMap<>();
		
		try{
			JSONParser parser=new JSONParser();
			
			JSONObject tempJson2=(JSONObject)parser.parse(iotRespose);
			JSONObject tempJson=(JSONObject)tempJson2.get("reported");
			//System.out.println("-----------"+tempJson);
			heatAry =(JSONArray)tempJson.get("pr");
			System.out.println("-----------heatAry-------- "+heatAry);
			
			String[] days ={"mo", "tu", "we", "th", "fr", "sa", "su"};
			
			System.out.println("-----size------"+heatAry.size());
			
			int count = 0;
			for (int i=0;i<heatAry.size();i++) {
				
				String  dataArryString=(String)heatAry.get(i);
				//System.out.println("dataArryString "+dataArryString);
				
				String  dataArry[]=dataArryString.split(",");
				String setpoint= (String)dataArry[0].toString();
	
		        int hours=Integer.parseInt(dataArry[5])/60;
	        	String minuts=(Integer.parseInt(dataArry[5])%60)+"";
	        	if(minuts.equalsIgnoreCase("0")){
	        		 minuts="00"; 
	        	}     
	        	JSONObject data=new JSONObject();
	        	JSONArray array=new JSONArray();
				if(setpoint.equals("0")){
					data.put("c_start_time",hours+":"+minuts);
					data.put("c_temp_set",Integer.parseInt(dataArry[6])/2);
					data.put("period_xy",dataArry[4]);
					data.put("cool_point_unit",1);
					data.put("day",Integer.parseInt(dataArry[3])+1);
					if (hours > 11) {
						data.put("am_pm",2);
					} else {
						data.put("am_pm",1);
					}
					if(!response.containsKey(days[Integer.parseInt(dataArry[3])])){
						count = 0;
						data.put("index",++count);
						array.add(data);
					}else{
						array = response.get(days[Integer.parseInt(dataArry[3])]);
						data.put("index",++count);
						array.add(data);
					}
				}else{
					data.put("h_start_time",hours+":"+minuts);
					data.put("h_temp_set",Integer.parseInt(dataArry[6])/2);
					data.put("period_xy",dataArry[4]);
					data.put("heat_point_unit",1);
					data.put("day",Integer.parseInt(dataArry[3])+1);
					if (hours > 11) {
						data.put("am_pm",2);
					} else {
						data.put("am_pm",1);
					}
					if(!response.containsKey(days[Integer.parseInt(dataArry[3])])){
						count = 0;
						data.put("index",++count);
						array.add(data);
					}else{
						array = response.get(days[Integer.parseInt(dataArry[3])]);
						data.put("index",++count);
						array.add(data);
					}
				}
				response.put(days[Integer.parseInt(dataArry[3])], array);
			}
			
			System.out.println("-----response map size ------"+response.size());
			
			//System.out.println("response "+response);
			
			
		}catch (Exception e) {
			logger.error("Error found in generateXpacJsonDefault ",e);
			System.out.println(e.getMessage());
	}
		return response;
		
	}
	
	@Override
	public void addCustomSchedule(String iotRespose,int deviceId) {
		logger.info("[BEGIN] [addCustomSchedule] [Schedule DAO Impl]");
		try {
			int userId = 1;
			Map<String,JSONArray> response = generateXpacJsonDefault(iotRespose);
			
			AddScheduleDetails addScheduleDetails = new AddScheduleDetails();
			addScheduleDetails.setScheduleName("Custom Schedule");
			
			ArrayList<AddScheduleDetails> schdlObjList = new ArrayList<>();
			
			for (Entry<String, JSONArray> entry : response.entrySet()) {
				JSONArray value = entry.getValue();
				for (int i=0; i<value.size(); i++) {
					if (i<=3) {
						AddScheduleDetails details = new AddScheduleDetails();
						JSONObject jsonObject = (JSONObject) value.get(i);
						details.setAm(jsonObject.get("am_pm").toString());
						details.setTime(jsonObject.get("c_start_time").toString());
						details.setClpoint(jsonObject.get("c_temp_set").toString());
						details.setClunit("1");
						details.setDayId(jsonObject.get("day").toString());
						JSONObject heatJsonObject = (JSONObject) value.get(i+4);
						details.setHtpoint(heatJsonObject.get("h_temp_set").toString());
						details.setHtunit("1");
						schdlObjList.add(details);
					}
				}
			}
			
			addScheduleDetails.setSchdlObjList(schdlObjList);
			
			int scheduleId = addDeviceSchedule(addScheduleDetails,userId,true);
			
			addCustomDeviceSchedule(deviceId, scheduleId, userId);
			//System.out.println(schdlObjList.size());
			
			/*for (AddScheduleDetails addScheduleDetails3 : schdlObjList) {
				System.out.println(addScheduleDetails3.getDayId()+" "+addScheduleDetails3.getTime()+" "+addScheduleDetails3.getAm()+" "+addScheduleDetails3.getHtpoint()
				+" "+addScheduleDetails3.getHtunit()+" "+addScheduleDetails3.getClpoint()+" "+addScheduleDetails3.getClunit());
			}*/
			logger.info("[END] [addCustomSchedule] [Schedule DAO Impl]");
		} catch (VEMAppException e) {
			logger.error(e);
		}
	}
	
	//@Override
	public Response addCustomDeviceSchedule(int deviceId, int scheduleId, int userId) throws VEMAppException{
		logger.info("[BEGIN] [addCustomDeviceSchedule] [Schedule DAO Impl]");
		Response response=new Response();
		SimpleJdbcCall simpleJdbcCall;
		try{
			
			//Executing the procedure.
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
			
		    /*
		     * Adding all the input parameter values to a hashmap.
		     */
			Map<String,Object> inputParams=new HashMap<String, Object>();
			
		    inputParams.put("in_userId", userId);
			inputParams.put("in_scheduleId", scheduleId);
			inputParams.put("in_deviceId", deviceId);
			
			simpleJdbcCall.withProcedureName("sp_store_device_default_schedule");			
			Map<String, Object> outParameters = simpleJdbcCall.execute(inputParams);
			logger.info("[END] [addCustomDeviceSchedule] [Schedule DAO Impl] outParameters "+outParameters);
		} 
		catch(Exception e){
			logger.error("",e);
			response.setCode(ErrorCodes.ERROR_STATUS_APPLY_SCHEDULE);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ERROR_STATUS_APPLY_SCHEDULE, logger, e);
		}
		
		logger.info("[END] [getCustomerList] [Schedule DAO Impl]");
		return response;
	}
	
	
	/**
	 * @param macId
	 * @throws VEMAppException
	 * 
	 * Method used to insert device last updated time
	 * 
	 * @author Rajashekharaiah M
	 * Created on 14/03/2017
	 */
	public void insertDeviceLastUpdate(String macId) throws VEMAppException {
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		Map<String,Object> inputParams=null;
		logger.info("Controller reached ScheduleDaoImpl.insertDeviceLastUpdate");
		try{
			
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(PROC_INSERT_DEVICE_CONFIG);
		    inputParams=new HashMap<>();
		    
		    inputParams.put("in_mac_id", macId);
		    
		    outParameters=simpleJdbcCall.execute(inputParams);
			//logger.info("Affected rows"+outParameters);
		}catch(Exception e){
			logger.error("Error found While inserting device last updated time",e);
		}
	}
	
	
	public String getDeviceLastUpdateTime(String macId){
		logger.info("[BEGIN] [getDeviceLastUpdateTime] [Schedule DAO LAYER]");
		String lastUpdateTime = null;
		try {
			
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(GET_DEVICE_LAST_UPDATE_TIME);
			Map<String, Object> inParamMap = new HashMap<String, Object>();
			inParamMap.put("in_mac_id", macId);
			
			logger.info("executing proc "+GET_DEVICE_LAST_UPDATE_TIME+" params "+inParamMap);
			
			SqlParameterSource in = new MapSqlParameterSource(inParamMap);
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			Iterator<Entry<String, Object>> itr = simpleJdbcCallResult.entrySet().iterator();
			JSONObject resultObj = new JSONObject();
			
			
			while (itr.hasNext()) {
			        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
			        String key = entry.getKey();
			        if(key.equals(RESULT_SET_1)){
			        	Object result = entry.getValue();
			        	logger.info("===$$$ Last update time got as :"+result);
			        	
			        	String ss1;
			        	
			        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)result);
			        	
			        	if(tempAry.length() !=0){
			        	org.json.JSONObject resultJSON = (org.json.JSONObject)tempAry.get(0);
			        	
			        	logger.info("===$$$ Last update time got as from db:"+resultJSON.get("dlu_time"));
			        	
			        	ss1 = (String)resultJSON.get("dlu_time");
			        	
			        	ss1 = ss1.substring(0, 19);
			        	}else{
			        		Date d  = new Date();
			        		//ss1 = d.toString();
			        		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				        	ss1 = formater.format(d);
			        	}
			        	logger.info("===$$$ Before Format date :"+ss1);
			        	
			        	Date ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ss1);
			        	
			        	
			        	logger.info("===$$$ Formated date :"+ss);
			        	
			        	
			        	DateFormat formater = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			        	String ss2 = formater.format(ss);
			        	
			        	logger.info("===$$$ Required formaat :"+ss2);
			        	lastUpdateTime = ss2;
			        }
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		logger.info("[END] [getDeviceLastUpdateTime] [Schedule DAO LAYER]");
		return lastUpdateTime;
	}
	
}
