package com.enerallies.vem.daoimpl.audit;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;


/**
 * File Name : AuditDaoImpl
 * AuditDAO  is used to serve all the database level operations related to audit.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        1-11-2016
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 1-11-2016		Arun		    File Created.
 *
 */
@Component
public class AuditDaoImpl implements AuditDAO {

	private static final Logger logger = Logger.getLogger(AuditDaoImpl.class);
	private static final String PROC_INSERT_AUDIT_RECORD="sp_insert_active_log";
	private DataSource dataSource;
	
	
	/**
	 * setDataSource DAO layer, used to set the datasource object.
	 * @param dataSource object for mvc dispacter xml file
	 * @throws VEMAppException
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * insertAuditLog DAO layer, used to save audit log Information.
	 * @param auditRequest object of GroupRequest bean, accepts customer id and userId
	*/
	@Override
	public void insertAuditLog(AuditRequest auditRequest) {
		
		SimpleJdbcCall simpleJdbcCall;
		Map<String,Object> outParameters = null;
		Map<String,Object> inputParams=null;
		logger.info("Controller reached AuditDaoImpl.insertAuditLog");
		if (logger.isDebugEnabled()){
			logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_insert_active_log]");
			logger.debug(CommonConstants.PARAMETERS);
			logger.debug("al_user_id :"+auditRequest.getUserId());
			logger.debug("al_action :"+auditRequest.getUserAction());
			logger.debug("al_location :"+auditRequest.getLocation());
			logger.debug("al_service_id :"+auditRequest.getServiceId());
			logger.debug("al_discription :"+auditRequest.getDescription());
			logger.debug("al_service_specific_id :"+auditRequest.getServiceSpecificId());
		}
		try{
			
			simpleJdbcCall = new SimpleJdbcCall(dataSource);
		    simpleJdbcCall.withProcedureName(PROC_INSERT_AUDIT_RECORD);
		    inputParams=new HashMap<>();
		    
		    inputParams.put("al_user_id", auditRequest.getUserId());
		    inputParams.put("al_action", auditRequest.getUserAction());
		    inputParams.put("al_location", auditRequest.getLocation());
		    inputParams.put("al_service_id", auditRequest.getServiceId());
		    inputParams.put("al_discription", auditRequest.getDescription());
		    inputParams.put("al_service_specific_id", auditRequest.getServiceSpecificId());
		    
		    outParameters=simpleJdbcCall.execute(inputParams);
		    
		    int out_flag = (Integer) outParameters.get("out_flag");
		    String out_error_msg = (String) outParameters.get("out_error_msg");
		    
		    logger.info("[out_flag] :: "+out_flag);
		    logger.info("[out_error_msg] :: "+out_error_msg);
			
		}catch(Exception e){
			logger.error("",e);
		}
		
		
	}

	
	
	
}
