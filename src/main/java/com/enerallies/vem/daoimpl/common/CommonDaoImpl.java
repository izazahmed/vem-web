package com.enerallies.vem.daoimpl.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.enerallies.vem.beans.common.ZipCode;
import com.enerallies.vem.dao.common.CommonDao;
import com.enerallies.vem.exceptions.VEMAppException;


/**
 * File Name : CommonDaoImpl 
 * 
 * CommonDaoImpl: is implementation of CommonService method 
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-11-2016
 *
 * MODIFICATION HISTORY
 * ===========================================================================
 * SNO	DATE			USER             				COMMENTS
 * ===========================================================================
 * 01	08-11-2016		Rajashekharaiah Muniswamy		File Created
 * */
public class CommonDaoImpl implements CommonDao{
	/** Get Logger  */
	private static final Logger logger = Logger.getLogger(CommonDaoImpl.class);
	
	/** Data source instance */
	private DataSource dataSource;
	
	/** JDBC Template instance */
	private JdbcTemplate jdbcTemplate;

	
	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate =  new JdbcTemplate(this.dataSource);
	}

	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	@Override
	public ZipCode validateZipCodeOld(int zipCode, int stateId) throws VEMAppException {
		logger.info("[BEGIN] [CommonDaoImpl] [validateZipCode]  =====>");		
		String sql = "call sp_select_zipcode ("+zipCode+", "+stateId+")";
		ZipCode zipCodeResp;
		try {
			zipCodeResp = jdbcTemplate.queryForObject(sql,  new Object [] {},  new ZipCodeMapper());
			
		} catch (DataAccessException e) {
			logger.error("[ERROR] [CommonDaoImpl] [validateZipCode]Exception occured while fetching zipcode "+e);
			zipCodeResp = null;
		}
		logger.info("[END] [CommonDaoImpl] [validateZipCode] =====>");
		return zipCodeResp;
	}
	
	private static final class ZipCodeMapper implements RowMapper<ZipCode>{

		@Override
		public ZipCode mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			ZipCode zipCode = new ZipCode();
			
			zipCode.setZipCode(resultSet.getInt("ZIP_code"));
			zipCode.setCity(resultSet.getString("City"));
			zipCode.setStateId(resultSet.getInt("state_id"));
			return zipCode;
		}
		
	}
	
	
	public int validateZipCode(int zipCOde, int stateId, String city){
		// Declaration of simpleJdbcCall
		SimpleJdbcCall simpleJdbcCall;
		int flag;

			/*
			 * Initialize the simpleJdbcCall to call the stored procedure
			 * and Adding the stored procedure name to simpleJdbcCall object.  
			 */
			simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_select_zipcode");
	
			// Passing parameters to the stored procedure
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("in_zip_code",zipCOde);
			parameters.put("in_state_id",stateId);
			parameters.put("in_city",city);
	
			// Giving parameters to SQL Parameter source
			SqlParameterSource in = new MapSqlParameterSource(parameters);
			
			// Executing stored procedure
			Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
			
			// Fetching the created dbFlag
			flag = Integer.parseInt(simpleJdbcCallResult.get("status").toString());
			
			if(flag>=1)
			return 1;
			else
			return 0;
	}

}
