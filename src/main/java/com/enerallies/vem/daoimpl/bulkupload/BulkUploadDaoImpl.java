package com.enerallies.vem.daoimpl.bulkupload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.bulkupload.BulkUploadBean;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.dao.bulkupload.BulkUploadDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ErrorCodes;


@Component
public class BulkUploadDaoImpl implements BulkUploadDao{

	// Getting logger instance
	private static final Logger logger = Logger.getLogger(BulkUploadDao.class);
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private static final String GET_BULK_UPLOAD_STATUS="sp_select_bulk_upload_status";
	
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}
	
	
	@Override
	public Response getBulkUploadProgress(BulkUploadBean bulkUploadBean)  throws VEMAppException{
		Response response=new Response();
		logger.info("Controller in [BulkUploadDaoImpl][getBulkUploadProgress] method");
		
		try{
			
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(GET_BULK_UPLOAD_STATUS);
			Map<String, Object> inParamMap = new HashMap<>();
			
			inParamMap.put("user_id", bulkUploadBean.getUserId());
			inParamMap.put("specific_sheet", bulkUploadBean.getFileName());
			inParamMap.put("user_time_zone", bulkUploadBean.getTimeZone());
			inParamMap.put("in_super_admin", bulkUploadBean.getIsSuper());
		
			
			logger.debug("[DEBUG] Executing procedure "+GET_BULK_UPLOAD_STATUS+" params "+inParamMap);
			
			SqlParameterSource in = new MapSqlParameterSource(inParamMap);
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			
			Iterator<Entry<String, Object>> itr = simpleJdbcCallResult.entrySet().iterator();
			JSONParser parser=new JSONParser();
			
			while (itr.hasNext()) {
			        Map.Entry<String, Object> entry = itr.next();
			        String key = entry.getKey();
			        
			        if(key.equals(CommonConstants.RESULT_SET_1))
			        {
			        	Object value = entry.getValue();
			        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
			        	JSONArray resultAry=(JSONArray)parser.parse(tempAry.toString());
			        	response.setData(resultAry);
					}
			
			}
		}catch(Exception e){
			logger.error("",e);
			throw new VEMAppException(e.getMessage());
		}
		return response;
		
	}
	
	@Override
	public Response deleteSiteUpload(BulkUploadBean bulkUploadBean)  throws VEMAppException {
		Response response=new Response();
		logger.info("Controller in [BulkUploadDaoImpl][deleteSiteUpload] method");
		
		int updateFlag = 0;
		try {
			
			/*
			 *  Query to update the column is_deleted=1 for making the requested
			 *  Group as logically deleted.
			 */
			String sqlUpdate = "UPDATE bulk_upload_progress SET is_deleted = 1, bulk_upload_user_id = ? WHERE `bulk_upload_id` = ?"; 
			
			logger.debug("[DEBUG] Executing the update query:"+sqlUpdate);
			logger.debug("[DEBUG] input "+CommonConstants.PARAMETERS+" - userId:"+bulkUploadBean.getUserId()+" - fileName:"+bulkUploadBean.getBulkUploadProgressId());
			updateFlag = jdbcTemplate.update(sqlUpdate, bulkUploadBean.getUserId(), bulkUploadBean.getBulkUploadProgressId());
			response.setData(updateFlag);
			if(updateFlag > 0) {
        		response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.INFO_DELETE_SITE_UPLOAD);
			} else {
				
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.ERROR_DELETE_SITE_UPLOAD);
			}
	        
		}catch (Exception e) {
			logger.error("",e);
			//Creating and throwing the customized exception.
			throw new VEMAppException("Internal Error occured at deleteSiteUpload DAO layer");
		}
		logger.info("[END] [deleteSiteUpload] [Group DAO LAYER]");
		return response;
		
	}

}
