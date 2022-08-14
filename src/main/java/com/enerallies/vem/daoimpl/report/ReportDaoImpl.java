/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.daoimpl.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.report.AnalyticParamComparator;
import com.enerallies.vem.beans.report.AnalyticParams;
import com.enerallies.vem.beans.report.AnalyticsData;
import com.enerallies.vem.beans.report.AnalyticsDataComparator;
import com.enerallies.vem.beans.report.AnalyticsGroupSiteComparator;
import com.enerallies.vem.beans.report.AnalyticsRequest;
import com.enerallies.vem.beans.report.Category;
import com.enerallies.vem.beans.report.CategoryAnalytics;
import com.enerallies.vem.beans.report.DataBean;
import com.enerallies.vem.beans.report.DataName;
import com.enerallies.vem.beans.report.DataNameAnalytics;
import com.enerallies.vem.beans.report.DrillDown;
import com.enerallies.vem.beans.report.HVACUsage;
import com.enerallies.vem.beans.report.KeyValue;
import com.enerallies.vem.beans.report.MapData;
import com.enerallies.vem.beans.report.Report;
import com.enerallies.vem.beans.report.ReportMap;
import com.enerallies.vem.beans.report.ReportRequest;
import com.enerallies.vem.beans.report.TempSetpointReport;
import com.enerallies.vem.beans.report.TemperatureSetpoint;
import com.enerallies.vem.dao.report.ReportDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.ErrorCodes;

/**
 * File Name : ReportDaoImpl 
 * 
 * ReportDaoImpl: is used to implement all the dashboard dao methods
 *
 * @author Nagarjuna Eerla
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        22-11-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	22-11-2016			Nagarjuna Eerla		File Created
 */

@Component
public class ReportDaoImpl implements ReportDao{

		// Getting logger instance
		private static final Logger logger = Logger.getLogger(ReportDaoImpl.class);
		
		/*Constant variable declaration*/
		private static final String RESULT_SET_1 = "#result-set-1";
		private static final String RESULT_SET_2 = "#result-set-2";
		private static final String RESULT_SET_3 = "#result-set-3";
		
		/** Data source instance */
		private DataSource dataSource;
		
		/** JDBC Template instance */
		private JdbcTemplate jdbcTemplate;
		
		@Override  
		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
		    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
		}

		@Override
		public List<Report> getReportData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
			
			logger.info("[BEGIN] [getReportData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_reports_data]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Reports instance initialization
			List<Report> reportsData = new LinkedList<>();
			
			try {
				if(StringUtils.equals(reportRequest.getReportType(), "1")
						|| StringUtils.equals(reportRequest.getReportType(), "6")){
					reportsData = getReportDataByReportType("call sp_get_rpt_degradedhvac_data ('", reportRequest, "", userId, timeZone);
				}else if(StringUtils.equals(reportRequest.getReportType(), "2")){
					reportsData = getReportDataByReportType("call sp_get_rpt_withinsetpoints_data ('", reportRequest, "", userId, timeZone);
				}else if(StringUtils.equals(reportRequest.getReportType(), "4")){
					reportsData = getReportDataByReportType("call sp_get_rpt_manualchanges_data ('", reportRequest, "", userId, timeZone);
				}else if(StringUtils.equals(reportRequest.getReportType(), "5")){
					reportsData = getReportDataByReportType("call sp_get_rpt_communication_failure ('", reportRequest, "", userId, timeZone);
				}
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.REPORT_ERROR_FETCH_FAILED, logger, e);
			}
			
			logger.info("[END] [getReportData] [DAO LAYER]");
			
			return reportsData;
		}

		private List<Report> getReportDataByReportType(String procedure, ReportRequest reportRequest, String heat, int userId, String timeZone) {
			
			List<Report> reportsData;
			
			// Passing store procedure as input
			reportsData = jdbcTemplate.query(procedure+
						reportRequest.getType()+"','"+
						(StringUtils.isBlank(reportRequest.getCustomerIds())?"0":reportRequest.getCustomerIds())+"','"+
						(StringUtils.isBlank(reportRequest.getGroupIds())?"0":reportRequest.getGroupIds())+"','"+
						(StringUtils.isBlank(reportRequest.getSiteIds())?"0":reportRequest.getSiteIds())+"','"+
						reportRequest.getInDays()+"','"+
						reportRequest.getFromDate()+"','"+
						reportRequest.getToDate()+"','"+
						timeZone+"','"+
						userId+"')", new RowMapper<Report>() {
				public Report mapRow(ResultSet rs, int rowNum)
						throws SQLException {
						Report report = new Report();
						report.setId(rs.getInt("id"));
						report.setName(rs.getString("name"));
						
						if(StringUtils.equals(reportRequest.getReportType(), "3")){
							int cool = rs.getInt("count");
							int temp = rs.getInt(heat);
							report.setY(cool+temp);
							report.setHeat(temp);
							report.setCool(cool);
							report.setExtraValue(temp);
						}else{
							report.setY(rs.getInt("count"));
							if((StringUtils.equals(reportRequest.getReportType(), "1") || StringUtils.equals(reportRequest.getReportType(), "4") || StringUtils.equals(reportRequest.getReportType(), "5")) && rs.getInt("count") == 1){
								report.setName(rs.getString("name"));
	                    	}else if((StringUtils.equals(reportRequest.getReportType(), "2") && rs.getInt("count") == 1)){
	                    		report.setName(rs.getString("name")+" ("+rs.getInt("count")+" min)");
	                    	}else if((StringUtils.equals(reportRequest.getReportType(), "2") && rs.getInt("count") > 1)){
	                    		report.setName(rs.getString("name")+" ("+rs.getInt("count")+" mins)");
	                    	}else{
	                    		report.setName(rs.getString("name")+" ("+rs.getInt("count")+")");
	                    	}
						}
						report.setReportId(Integer.parseInt(StringUtils.isNotBlank(reportRequest.getReportType())?reportRequest.getReportType():"0"));
						return report;
					}
				});
			return reportsData;
		}

		@Override
		public List<DataName> getCustomerAnalyticsData(AnalyticsRequest analyticsRequest, int userId) throws VEMAppException {
			
			logger.info("[BEGIN] [getCustomerAnalyticsData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_customer_analytics]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Reports instance initialization
			List<DataName> analyticsData = new LinkedList<>();
			List<String> selectedList = new LinkedList<>();
			
			try {
				
				// Passing store procedure as input
				jdbcTemplate.queryForObject("call sp_get_rpt_customer_analytics ('"+
							analyticsRequest.getCustomerId()+"','"+
							analyticsRequest.getFromDate()+"','"+
							analyticsRequest.getToDate()+"','"+
							analyticsRequest.getDataType()+"','"+
							analyticsRequest.getAnalyticParams()+"','"+
							userId+"')", new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
							
							if("3".equals(analyticsRequest.getDataType())){
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.GROUP_DATA))){
									selectedList.add(rs.getString(CommonConstants.GROUP_DATA));
								}
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.SITE_DATA))){
									selectedList.add(rs.getString(CommonConstants.SITE_DATA));
								}
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.DEVICE_DATA))){
									selectedList.add(rs.getString(CommonConstants.DEVICE_DATA));
								}
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.USER_DATA))){
									selectedList.add(rs.getString(CommonConstants.USER_DATA));
								}
							}else if("2".equals(analyticsRequest.getDataType())){
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.TOTAL_RTU_DATA))){
									selectedList.add(rs.getString(CommonConstants.TOTAL_RTU_DATA));
								}
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.SCHEDULE_DATA))){
									selectedList.add(rs.getString(CommonConstants.SCHEDULE_DATA));
								}
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.POWER_OF_RTU))){
									selectedList.add(rs.getString(CommonConstants.POWER_OF_RTU));
								}
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.AC_FAILURE))){
									selectedList.add(rs.getString(CommonConstants.AC_FAILURE));
								}
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.HEATER_FAILURE))){
									selectedList.add(rs.getString(CommonConstants.HEATER_FAILURE));
								}
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.AC_RUN_DEGREDED))){
									selectedList.add(rs.getString(CommonConstants.AC_RUN_DEGREDED));
								}
								if(StringUtils.isNotBlank(rs.getString(CommonConstants.HEATER_RUN_DEGREDED))){
									selectedList.add(rs.getString(CommonConstants.HEATER_RUN_DEGREDED));
								}
							}
						
							return null;
						}
					});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.CUSTOMER_ANALYTICS_ERROR_FETCH_FAILED, logger, e);
			}
			
			for (String str : selectedList) {
				DataName dataName = getAnalyticData(str);
				analyticsData.add(dataName);
			}
			
			logger.info("[END] [getCustomerAnalyticsData] [DAO LAYER]");
			
			/*
			 * Sorting result sets to get desired order in front end side
			 */
			Collections.sort(analyticsData, new AnalyticsDataComparator());
			
			return analyticsData;
		}

		private DataName getAnalyticData(String str) {
			DataName dataName = new DataName();
			List<Report> data = new LinkedList<>();
			
			String[] splittedArr = str.split("~");
			
			// creating report response instance
			Report report = new Report();
			report.setId(Integer.parseInt(StringUtils.isNotBlank(splittedArr[2])?splittedArr[2]:"0"));
			report.setName(splittedArr[1]);
			report.setY(Integer.parseInt(StringUtils.isNotBlank(splittedArr[0])?splittedArr[0]:"0"));
			report.setColor("null".equals(splittedArr[3])?null:splittedArr[3]);
			data.add(report);
			dataName.setName(splittedArr[1]);
			dataName.setData(data);
			dataName.setDataId(Integer.parseInt(StringUtils.isNotBlank(splittedArr[2])?splittedArr[2]:"0"));
			dataName.setColor("null".equals(splittedArr[3])?null:splittedArr[3]);
			return dataName;
		}

		@Override
		public List<DataName> getCriticalIssues(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
			
			logger.info("[BEGIN] [getCriticalIssues] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_criticalissues]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Reports instance initialization
			List<DataName> alerts = new LinkedList<>();
			
			try {
				
				// Passing store procedure as input
				jdbcTemplate.query("call sp_get_rpt_criticalissues ('"+
							reportRequest.getType()+"','"+
							(StringUtils.isBlank(reportRequest.getCustomerIds())?"0":reportRequest.getCustomerIds())+"','"+
							(StringUtils.isBlank(reportRequest.getGroupIds())?"0":reportRequest.getGroupIds())+"','"+
							(StringUtils.isBlank(reportRequest.getSiteIds())?"0":reportRequest.getSiteIds())+"','"+
							reportRequest.getInDays()+"','"+
							timeZone+"','"+
							reportRequest.getFromDate()+"','"+
							reportRequest.getToDate()+"','"+
							userId+"')", new RowMapper<Report>() {
					public Report mapRow(ResultSet rs, int rowNum)
							throws SQLException {
							DataName dataName = new DataName();
							List<Report> data = new LinkedList<>();
							
							// creating report response instance
							Report report = new Report();
							report.setId(Integer.parseInt(StringUtils.isNotBlank(rs.getString("issue_id"))?rs.getString("issue_id"):"0"));
							report.setName(rs.getString("alert_name"));
							report.setY(Integer.parseInt(StringUtils.isNotBlank(rs.getString(CommonConstants.ISSUE_CNT))?rs.getString(CommonConstants.ISSUE_CNT):"0"));
							report.setReportId(Integer.parseInt(StringUtils.isNotBlank(reportRequest.getReportType())?reportRequest.getReportType():"0"));
							report.setColor(CommonConstants.RED_COLOR_CODE);
							data.add(report);
							dataName.setName(rs.getString("alert_name"));
							dataName.setData(data);
							dataName.setDataId(Integer.parseInt(StringUtils.isNotBlank(rs.getString("issue_id"))?rs.getString("issue_id"):"0"));
							alerts.add(dataName);
							return report;
						}
					});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASH_ERROR_FETCH_FAILED, logger, e);
			}
			
			/*List<String> criticalTypes = new LinkedList<>();
			Map<String, DataName> criticalTypeMap = new HashMap<>();
			
			for (DataName dataName : alerts) {
				criticalTypes.add(dataName.getName());
				criticalTypeMap.put(dataName.getName(), dataName);
			}
			
			if(!criticalTypes.contains(CommonConstants.HVAC_MODE_OFF)){
				DataName dataName = checkCriticalIssueType(CommonConstants.HVAC_MODE_OFF);
				criticalTypeMap.put(CommonConstants.HVAC_MODE_OFF, dataName);
			}
			if(!criticalTypes.contains(CommonConstants.THERMOSTAT_OFFLINE)){
				DataName dataName = checkCriticalIssueType(CommonConstants.THERMOSTAT_OFFLINE);
				criticalTypeMap.put(CommonConstants.THERMOSTAT_OFFLINE, dataName);
			}
			if(!criticalTypes.contains(CommonConstants.SETPOINT_NOT_REACHED)){
				DataName dataName = checkCriticalIssueType(CommonConstants.SETPOINT_NOT_REACHED);
				criticalTypeMap.put(CommonConstants.SETPOINT_NOT_REACHED, dataName);
			}
			
			// Adding predefined size to arraylist
			for (int i = 0; i < 3; i++) {
				alertsList.add(new DataName());
			}
			
			// looping the map
			for (Map.Entry<String, DataName> entry : criticalTypeMap.entrySet())
			{
				if(entry.getKey().equals(CommonConstants.HVAC_MODE_OFF)){
					alertsList.set(0,entry.getValue());
				}
				if(entry.getKey().equals(CommonConstants.THERMOSTAT_OFFLINE)){
					alertsList.set(1,entry.getValue());
				}
				if(entry.getKey().equals(CommonConstants.SETPOINT_NOT_REACHED)){
					alertsList.set(2,entry.getValue());
				}
			}*/
			
			/*
			 * Sorting result sets to get desired order for critical issues
			 */
			Collections.sort(alerts, new AnalyticsDataComparator());
			
			
			logger.info("[END] [getCriticalIssues] [DAO LAYER]");
			
			return alerts;
		}
/*
		private DataName checkCriticalIssueType(String criticalIssue) {
			DataName dataName = new DataName();
			List<Report> dataReport = new LinkedList<>();
			Report report = new Report();
			report.setName(criticalIssue);
			report.setColor(CommonConstants.RED_COLOR_CODE);
			dataReport.add(report);
			dataName.setName(criticalIssue);
			dataName.setData(dataReport);
			return dataName;
		}*/

		@Override
		public List<MapData> getMapData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
			
			logger.info("[BEGIN] [getMapData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_map]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Reports instance initialization
			List<MapData> mapData = new LinkedList<>();
			List<ReportMap> nonOperationOn = new LinkedList<>();
			List<ReportMap> operationOn = new LinkedList<>();
			List<ReportMap> noDevicesOn = new LinkedList<>();
			List<ReportMap> nonOperationOff = new LinkedList<>();
			List<ReportMap> operationOff = new LinkedList<>();
			List<ReportMap> noDevicesOff = new LinkedList<>();
			
			try {
				
				// Passing store procedure as input
				jdbcTemplate.query("call sp_get_rpt_map ('"+
							reportRequest.getType()+"','"+
							(StringUtils.isBlank(reportRequest.getCustomerIds())?"0":reportRequest.getCustomerIds())+"','"+
							(StringUtils.isBlank(reportRequest.getGroupIds())?"0":reportRequest.getGroupIds())+"','"+
							(StringUtils.isBlank(reportRequest.getSiteIds())?"0":reportRequest.getSiteIds())+"','"+
							reportRequest.getInDays()+"','"+
							timeZone+"','"+
							reportRequest.getFromDate()+"','"+
							reportRequest.getToDate()+"','"+
							userId+"')", new RowMapper<ReportMap>() {
					public ReportMap mapRow(ResultSet rs, int rowNum)
							throws SQLException {
							
							// creating report response instance
							ReportMap mapReport = new ReportMap();
							
							String mapType = rs.getString("map_type");
							mapReport.setSiteId(rs.getInt("site_id"));
							mapReport.setName(rs.getString("site_name"));
							mapReport.setLat(rs.getDouble("address_latitude"));
							mapReport.setLon(rs.getDouble("address_longitude"));
							mapReport.setAddress(rs.getString("address"));
							mapReport.setCustomerId(rs.getInt("customer_id"));
							
							if(!(ConfigurationUtils.isZero(rs.getDouble("address_latitude"),0.0) || ConfigurationUtils.isZero(rs.getDouble("address_longitude"),0.0))){
								
								if(StringUtils.equalsIgnoreCase(mapType, CommonConstants.COMFORT_OPTIMIZATION_ON_NO_DEVICES)){
									mapReport.setType(CommonConstants.COMFORT_OPTIMIZATION_ON_NO_DEVICES_STR);
									noDevicesOn.add(mapReport);
								}
								if(StringUtils.equalsIgnoreCase(mapType, CommonConstants.COMFORT_OPTIMIZATION_ON_NON_OPERATIONAL_DEGRADED)){
									mapReport.setType(CommonConstants.COMFORT_OPTIMIZATION_ON_NON_OPERATIONAL_DEGRADED_STR);
									nonOperationOn.add(mapReport);
								}
								if(StringUtils.equalsIgnoreCase(mapType, CommonConstants.COMFORT_OPTIMIZATION_ON_OPERATIONAL)){
									mapReport.setType(CommonConstants.COMFORT_OPTIMIZATION_ON_OPERATIONAL_STR);
									operationOn.add(mapReport);
								}
								if(StringUtils.equalsIgnoreCase(mapType, CommonConstants.COMFORT_OPTIMIZATION_OFF_NO_DEVICES)){
									mapReport.setType(CommonConstants.COMFORT_OPTIMIZATION_OFF_NO_DEVICES_STR);
									noDevicesOff.add(mapReport);
								}
								if(StringUtils.equalsIgnoreCase(mapType, CommonConstants.COMFORT_OPTIMIZATION_OFF_NON_OPERATIONAL_DEGRADED)){
									mapReport.setType(CommonConstants.COMFORT_OPTIMIZATION_OFF_NON_OPERATIONAL_DEGRADED_STR);
									nonOperationOff.add(mapReport);
								}
								if(StringUtils.equalsIgnoreCase(mapType, CommonConstants.COMFORT_OPTIMIZATION_OFF_OPERATIONAL)){
									mapReport.setType(CommonConstants.COMFORT_OPTIMIZATION_OFF_OPERATIONAL_STR);
									operationOff.add(mapReport);
								}
							}
							
							return mapReport;
						}
					});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASH_ERROR_FETCH_FAILED, logger, e);
			}
			
			// Adding non operational data
			MapData noDevicesDataOn = new MapData();
			noDevicesDataOn.setLabel(CommonConstants.COMFORT_OPTIMIZATION_ON_NO_DEVICES_STR);
			noDevicesDataOn.setValue(noDevicesOn);
			
			// Adding non operational data
			MapData nonOperationDataOn = new MapData();
			nonOperationDataOn.setLabel(CommonConstants.COMFORT_OPTIMIZATION_ON_NON_OPERATIONAL_DEGRADED_STR);
			nonOperationDataOn.setValue(nonOperationOn);
			
			// Adding non operational data
			MapData operationDataOn = new MapData();
			operationDataOn.setLabel(CommonConstants.COMFORT_OPTIMIZATION_ON_OPERATIONAL_STR);
			operationDataOn.setValue(operationOn);
			
			// Adding non operational data
			MapData noDevicesDataOff = new MapData();
			noDevicesDataOff.setLabel(CommonConstants.COMFORT_OPTIMIZATION_OFF_NO_DEVICES_STR);
			noDevicesDataOff.setValue(noDevicesOff);
			
			// Adding non operational data
			MapData nonOperationDataOff = new MapData();
			nonOperationDataOff.setLabel(CommonConstants.COMFORT_OPTIMIZATION_OFF_NON_OPERATIONAL_DEGRADED_STR);
			nonOperationDataOff.setValue(nonOperationOff);
			
			// Adding non operational data
			MapData operationDataOff = new MapData();
			operationDataOff.setLabel(CommonConstants.COMFORT_OPTIMIZATION_OFF_OPERATIONAL_STR);
			operationDataOff.setValue(operationOff);
			
			// Adding all to the final map
			mapData.add(noDevicesDataOn);
			mapData.add(nonOperationDataOn);
			mapData.add(operationDataOn);
			mapData.add(noDevicesDataOff);
			mapData.add(nonOperationDataOff);
			mapData.add(operationDataOff);
			
			logger.info("[END] [getMapData] [DAO LAYER]");
			
			return mapData;
		}
		
		@Override
		public List<Report> getPerformanceData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
			
			logger.info("[BEGIN] [getPerformanceData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[getPerformanceData]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Reports instance initialization
			List<Report> performance = new LinkedList<>();
			try {

				performance.add(getDegradedPerformData(reportRequest, userId, timeZone));
				performance.add(getWithinSetpointPerformData(reportRequest, userId, timeZone));
				performance.add(getHVACUsagePerformData(reportRequest, userId, timeZone));
				performance.add(getManualChangesPerformData(reportRequest, userId, timeZone));
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASH_ERROR_FETCH_FAILED, logger, e);
			}
			
			logger.info("[END] [getPerformanceData] [DAO LAYER]");

			return performance;
		}

		@Override
		public List<AnalyticParams> getAnalyticParams() throws VEMAppException {
			
			logger.info("[BEGIN] [getAnalyticParams] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_reports_data]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Reports instance initialization
			List<AnalyticParams> paramsList = new LinkedList<>();
			List<KeyValue> reports = new LinkedList<>();
			List<KeyValue> siteServey = new LinkedList<>();
			List<KeyValue> overallCustomer = new LinkedList<>();
			List<KeyValue> overallGroup = new LinkedList<>();
			List<KeyValue> overallSite = new LinkedList<>();
			List<KeyValue> trendingDevice = new LinkedList<>();
			List<KeyValue> hvacStages = new LinkedList<>();
			List<KeyValue> hvacStatus = new LinkedList<>();
			
			try {
				
				// Passing store procedure as input
				jdbcTemplate.query("call sp_get_analytics_params ()", new RowMapper<KeyValue>() {
					public KeyValue mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						KeyValue param = new KeyValue();
						String dataType = rs.getString("analyticDataTypeName");
						param.setLabel(rs.getString("analyticName"));
						param.setValue(rs.getInt("analyticId"));
						param.setSuperId(rs.getInt("analyticDataTypeId"));
						param.setLabelUnit(rs.getString("analyticUnit"));
						if(StringUtils.equalsIgnoreCase(dataType, CommonConstants.REPORTS)){
							reports.add(param);
						}else if(StringUtils.equalsIgnoreCase(dataType, CommonConstants.SITE_SURVEY)){
							siteServey.add(param);
						}else if(StringUtils.equalsIgnoreCase(dataType, CommonConstants.OVERALL_CUSTOMER)){
							overallCustomer.add(param);
						}else if(StringUtils.equalsIgnoreCase(dataType, CommonConstants.OVERALL_GROUP)){
							overallGroup.add(param);
						}else if(StringUtils.equalsIgnoreCase(dataType, CommonConstants.OVERALL_SITE)){
							overallSite.add(param);
						}else if(StringUtils.equalsIgnoreCase(dataType, CommonConstants.TRENDING_DEVICE)){
							trendingDevice.add(param);
						}else if(StringUtils.equalsIgnoreCase(dataType, CommonConstants.HVAC_STAGES)){
							hvacStages.add(param);
						}else if(StringUtils.equalsIgnoreCase(dataType, CommonConstants.HVAC_SYSTEM_STATUS)){
							hvacStatus.add(param);
						}
						return param;
						}
					});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.REPORT_ERROR_FETCH_FAILED, logger, e);
			}
			
			AnalyticParams reportsParams = new AnalyticParams();
			reportsParams.setAnalyticParams(reports);
			reportsParams.setLabel(CommonConstants.REPORTS);
			reportsParams.setId(reports.get(0).getSuperId());
			
			AnalyticParams siteServeyParams = new AnalyticParams();
			siteServeyParams.setAnalyticParams(siteServey);
			siteServeyParams.setLabel(CommonConstants.SITE_SURVEY);
			siteServeyParams.setId(siteServey.get(0).getSuperId());
			
			AnalyticParams overallCustomerParams = new AnalyticParams();
			overallCustomerParams.setAnalyticParams(overallCustomer);
			overallCustomerParams.setLabel(CommonConstants.OVERALL_CUSTOMER);
			overallCustomerParams.setId(overallCustomer.get(0).getSuperId());
			
			AnalyticParams overallGroupParams = new AnalyticParams();
			overallGroupParams.setAnalyticParams(overallGroup);
			overallGroupParams.setLabel(CommonConstants.OVERALL_GROUP);
			overallGroupParams.setId(overallGroup.get(0).getSuperId());
			
			AnalyticParams overallSiteParams = new AnalyticParams();
			overallSiteParams.setAnalyticParams(overallSite);
			overallSiteParams.setLabel(CommonConstants.OVERALL_SITE);
			overallSiteParams.setId(overallSite.get(0).getSuperId());
			
			AnalyticParams trendingDeviceParams = new AnalyticParams();
			trendingDeviceParams.setAnalyticParams(trendingDevice);
			trendingDeviceParams.setLabel(CommonConstants.TRENDING_DEVICE);
			trendingDeviceParams.setId(trendingDevice.get(0).getSuperId());
			
			AnalyticParams hvacStagesParams = new AnalyticParams();
			hvacStagesParams.setAnalyticParams(hvacStages);
			hvacStagesParams.setLabel(CommonConstants.HVAC_STAGES);
			hvacStagesParams.setId(hvacStages.get(0).getSuperId());
			
			AnalyticParams hvacStatusParams = new AnalyticParams();
			hvacStatusParams.setAnalyticParams(hvacStatus);
			hvacStatusParams.setLabel(CommonConstants.HVAC_SYSTEM_STATUS);
			hvacStatusParams.setId(hvacStatus.get(0).getSuperId());
			
			paramsList.add(reportsParams);
			paramsList.add(siteServeyParams);
			paramsList.add(overallCustomerParams);
			paramsList.add(overallGroupParams);
			paramsList.add(overallSiteParams);
			paramsList.add(trendingDeviceParams);
			paramsList.add(hvacStagesParams);
			paramsList.add(hvacStatusParams);
			
			logger.info("[END] [getAnalyticParams] [DAO LAYER]");
			return paramsList;
		}

		@SuppressWarnings("unchecked")
		@Override
		public JSONObject getTempSetpointReport(ReportRequest reportRequest, int userId, String timeZone)
				throws VEMAppException {
			
			logger.info("[BEGIN] [getTempSetpointReport] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_temp_setpoints_data]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Reports instance initialization
			List<TempSetpointReport> tempSetPointData = new LinkedList<>();
			
			// Creating map instances based on units
			Map<String, TemperatureSetpoint> farenHeatParams = new LinkedHashMap<>();
			Map<String, TemperatureSetpoint> onOffParams = new LinkedHashMap<>();
			Map<String, Boolean> graphValidation = new LinkedHashMap<>();
			
			boolean isFarenHeatParamsValid = false;
			boolean isOnOffParamsValid = false;
			
			//re configuring analytic params
			String analyticParams = ""; 
			
			try {
				
				//Splitting analytic params and device id's by comma (,)
				String[] temperatureParamsArr = reportRequest.getFarenHeatParams().split(",");
				String[] onOffParamsArr = reportRequest.getOnOffParams().split(",");
				
				// Getting device id's
				String[] devicesArr = reportRequest.getDeviceIds().split(",");
				
				/*
				 * Checking whether the devices are there for current selection
				 * then only allowing user to see the report
				 * 
				 * If controller will comes here means devices length is always be more than zero
				 * we are checking this in service level only
				 */
				if(devicesArr.length != 0){
					/*
					 * Currently we decided to see the max 8 parameters w.r.t devices 
					 */
					if(temperatureParamsArr.length * devicesArr.length <= 8){
						isFarenHeatParamsValid = true;
						analyticParams = reportRequest.getFarenHeatParams();
					}
					if(onOffParamsArr.length * devicesArr.length <= 8){
						isOnOffParamsValid = true;
						analyticParams = analyticParams +","+ reportRequest.getOnOffParams();
					}
				}
				
				// Passing store procedure as input
				tempSetPointData = jdbcTemplate.query("call sp_get_rpt_temp_setpoints_data ('"+
							reportRequest.getCustomerIds()+"','"+
							reportRequest.getGroupIds()+"','"+
							reportRequest.getSiteIds()+"','"+
							reportRequest.getDeviceIds()+"','"+
							reportRequest.getInDays()+"','"+
							reportRequest.getFromDate()+"','"+
							reportRequest.getToDate()+"','"+
							userId+"','"+
							analyticParams+"','"+
							timeZone+"')", new RowMapper<TempSetpointReport>() {
					public TempSetpointReport mapRow(ResultSet rs, int rowNum)
							throws SQLException {
								
								TempSetpointReport tempSetpoint = new TempSetpointReport();
								tempSetpoint.setUtcTime(rs.getLong("UTC_TIME"));
								tempSetpoint.setAnalyticParamName(rs.getString("ANALYT_NAME"));
								tempSetpoint.setAnalyticParamUnit(rs.getString("ANALYT_UNIT"));
								tempSetpoint.setAnalyticParamId(rs.getString("ANALYT_ID"));
								tempSetpoint.setParamValue(rs.getDouble("ANALYT_VALUE"));
								tempSetpoint.setDeviceId(rs.getInt("DEVICE_ID"));
								tempSetpoint.setDeviceName(rs.getString("name"));
								tempSetpoint.setParamFullName(rs.getString("FULL_NAME"));
								tempSetpoint.setCreatedTime(rs.getString("created_time"));
								return tempSetpoint;
						}
					});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.REPORT_ERROR_FETCH_FAILED, logger, e);
			}

			// list instances by units
			List<TemperatureSetpoint> temperatureList = new LinkedList<>();
			List<TemperatureSetpoint> onOffList = new LinkedList<>();
			
			// looping and preparing the required result format
			for (TempSetpointReport tempSetpointReport : tempSetPointData) {
				if(("F").equals(tempSetpointReport.getAnalyticParamUnit())){
					TemperatureSetpoint tempSetPoint;
					// Separated the logic to reuse -  this function is written to get desired output format to display graphs
					tempSetPoint = getAnalyticFilteredMap(tempSetpointReport, farenHeatParams);
					farenHeatParams.put(tempSetpointReport.getAnalyticParamName(), tempSetPoint);
				}else if(("B").equals(tempSetpointReport.getAnalyticParamUnit())){
					TemperatureSetpoint tempSetPoint = getAnalyticFilteredMap(tempSetpointReport, onOffParams);
					// Separated the logic to reuse -  this function is written to get desired output format to display graphs
					onOffParams.put(tempSetpointReport.getAnalyticParamName(), tempSetPoint);
				}
			}
			
			// adding farenheat unit type data to faren heat list
			for (Map.Entry<String, TemperatureSetpoint> entry : farenHeatParams.entrySet())
			{
				temperatureList.add(entry.getValue());
			}
			
			int i = 1;

			// Boolean(on/off) type loop
			for (Map.Entry<String, TemperatureSetpoint> entry : onOffParams.entrySet())
			{
				
				TemperatureSetpoint tp = entry.getValue();
				List<JSONArray> list = tp.getData();
				List<JSONArray> newList = new LinkedList<>();
				boolean isListContainsAllNull = false;
				for (JSONArray jsonArray : list) {
					// getting second value by index and comparing the values
					if(jsonArray.get(1) != null)
					if(ConfigurationUtils.isZero((Double)jsonArray.get(1), 0.0)){
						jsonArray.set(1, null); // Setting 1 index value as null if 0
					}else{
						isListContainsAllNull = true;
						jsonArray.set(1, i); // Setting 1 index value
					}
					// adding filtered JSON array to new list
					newList.add(jsonArray);
				}
				tp.setData(newList);
				onOffList.add(tp);
				
				// incrementing the i value
				if(isListContainsAllNull)
				i++;
			}
			
			JSONObject tempSetpointObj = new JSONObject();
			
			/*
			 * Sorting result sets to get desired order in front end side
			 */
			Collections.sort(temperatureList,new AnalyticParamComparator());
			Collections.sort(onOffList,new AnalyticParamComparator());
			
			// Graph validation map
			graphValidation.put("isFarenHeatParamsValid", isFarenHeatParamsValid);
			graphValidation.put("isOnOffParamsValid", isOnOffParamsValid);
			
			// Final desired format			
			tempSetpointObj.put("tempurateList", temperatureList);
			tempSetpointObj.put("onOffList", onOffList);
			tempSetpointObj.put("graphsValidation", graphValidation);
			
			logger.info("[END] [getTempSetpointReport] [DAO LAYER]");
			
			return tempSetpointObj;
		}

		@SuppressWarnings("unchecked")
		private TemperatureSetpoint getAnalyticFilteredMap(TempSetpointReport tempSetpointReport, Map<String, TemperatureSetpoint> map) {
			TemperatureSetpoint tempSetPoint = new TemperatureSetpoint();
			List<JSONArray> timeValueList = new LinkedList<>();
			tempSetPoint.setName(tempSetpointReport.getAnalyticParamName());
			tempSetPoint.setParamShortName(tempSetpointReport.getAnalyticParamName());
			tempSetPoint.setParamFullName(tempSetpointReport.getParamFullName());
			tempSetPoint.setDeviceId(tempSetpointReport.getDeviceId());
			tempSetPoint.setDeviceName(tempSetpointReport.getDeviceName());
			tempSetPoint.setParamId(Integer.parseInt(tempSetpointReport.getAnalyticParamId()));
			JSONArray timeValue = new JSONArray();
			timeValue.add(tempSetpointReport.getUtcTime());
			timeValue.add(tempSetpointReport.getParamValue());
			timeValue.add(tempSetpointReport.getCreatedTime());
			if(map.containsKey(tempSetpointReport.getAnalyticParamName())){
				tempSetPoint = map.get(tempSetpointReport.getAnalyticParamName());
				timeValueList = tempSetPoint.getData();
				timeValueList.add(timeValue);
			}else{
				timeValueList.add(timeValue);
				tempSetPoint.setData(timeValueList);
			}
			return tempSetPoint;
		}

		@Override
		public HVACUsage getHVACUsageReport(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
			
			logger.info("[BEGIN] [getHVACUsageReport] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_hvacusage_data]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Reports instance initialization
			List<Report> reportsData = new LinkedList<>();
			HVACUsage hvacUsage = new HVACUsage();
					
			try {
				
				reportsData = getReportDataByReportType("call sp_get_rpt_hvacusage_data ('", reportRequest, "HEAT", userId, timeZone);
				
				List<String> mainCategories = new LinkedList<>();
				List<DrillDown> mainData = new LinkedList<>();
				for (Report report : reportsData) {
					Category category = new Category();
					DrillDown drillDown = new DrillDown();
					List<String> subCategories = new LinkedList<>();
					List<Integer> subData = new LinkedList<>();
					
					if((report.getCool() / 60) >= 1 || (report.getHeat() / 60) >= 1){
						mainCategories.add(report.getName());
					}
					
					if((report.getCool() / 60) >= 1 || (report.getHeat() / 60) >= 1){
						subCategories.add(report.getName()+" - Cool" + " ("+(report.getCool() / 60)+" hr)");
						subData.add((report.getCool()) / 60);
					}
					
					if((report.getCool() / 60) >= 1 || (report.getHeat() / 60) >= 1){
						subCategories.add(report.getName()+" - Heat" + " ("+(report.getHeat() / 60)+" hr)");
						subData.add((report.getHeat()) / 60);
					}
					
					category.setCategories(subCategories);
					category.setData(subData);
					if((report.getCool() / 60) >= 1 || (report.getHeat() / 60) >= 1){
						drillDown.setName(report.getName());
						drillDown.setY((report.getCool()+report.getHeat()) / 60);
						drillDown.setId(report.getId());
						drillDown.setDrilldown(category);
						mainData.add(drillDown);
					}
				}
				
				hvacUsage.setCategories(mainCategories);
				hvacUsage.setData(mainData);
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.REPORT_ERROR_FETCH_FAILED, logger, e);
			}
			
			logger.info("[END] [getHVACUsageReport] [DAO LAYER]");
			
			return hvacUsage;
		}

		@Override
		public CategoryAnalytics getGroupAnalyticsData(AnalyticsRequest analyticsRequest, int userId)
				throws VEMAppException {
			
			logger.info("[BEGIN] [getGroupAnalyticsData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_group_analytics]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// AnalyticsData instance initialization
			List<AnalyticsData> list = new LinkedList<>();
			CategoryAnalytics analyticData = new CategoryAnalytics();
			
			// This is the map to filter by groups
			Map<Integer, List<AnalyticsData>> groupsMap = new HashMap<>();
			
			try {
				
				// Passing store procedure as input
				list = jdbcTemplate.query("call sp_get_rpt_group_analytics ('"+
							analyticsRequest.getGroupIds()+"','"+
							analyticsRequest.getFromDate()+"','"+
							analyticsRequest.getToDate()+"','"+
							analyticsRequest.getDataType()+"','"+
							analyticsRequest.getAnalyticParams()+"','"+
							userId+"')", new RowMapper<AnalyticsData>() {
					public AnalyticsData mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						
						AnalyticsData data = new AnalyticsData();
						
						data.setCount(rs.getInt("COUNT"));
						data.setName(rs.getString("NAME"));
						data.setId(rs.getInt("ID"));
						data.setType(rs.getString("V_TYPE"));
						data.setParamId(rs.getInt("PARAM_ID"));
						data.setParamName(rs.getString("PARAM_NAME"));
						data.setColorCode(rs.getString("COLOR_CODE"));
						
						return data;
						}
					});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.GROUP_ANALYTICS_FETCH_SUCCESS, logger, e);
			}
			
			// Filtering the groups by its id
			for (AnalyticsData str : list) {
				List<AnalyticsData> dataList = new LinkedList<>();
				if(groupsMap.containsKey(str.getId())){
					dataList = groupsMap.get(str.getId());
					dataList.add(str);
					groupsMap.put(str.getId(), dataList);			
				}else{
					dataList.add(str);
					groupsMap.put(str.getId(), dataList);
				}				
			}
			
			// List with unique group ids
			List<DataNameAnalytics> dataNameAnalyticsList = new LinkedList<>();
			
			// Looping groups maps
			for (Map.Entry<Integer, List<AnalyticsData>> entry : groupsMap.entrySet())
			{
				List<AnalyticsData> dataList = entry.getValue();
				DataNameAnalytics dataNameAnalytics = new DataNameAnalytics();
				List<DataBean> dataBeanList = new LinkedList<>();
				List<String> categoryList = new LinkedList<>();
				/*
				 * Sorting result sets to get desired order in front end side
				 */
				Collections.sort(dataList, new AnalyticsGroupSiteComparator());
				
				for (AnalyticsData analyticsData : dataList) {
					DataBean dataBean = new DataBean();
					dataBean.setColor(analyticsData.getColorCode());
					dataBean.setY(analyticsData.getCount());
					dataBean.setParamName(analyticsData.getParamName());
					
					dataNameAnalytics.setId(analyticsData.getId());
					dataNameAnalytics.setName(analyticsData.getName());
					categoryList.add(analyticsData.getParamName());
					dataBeanList.add(dataBean);
				}

				analyticData.setCategories(categoryList);
				dataNameAnalytics.setData(dataBeanList);
				dataNameAnalyticsList.add(dataNameAnalytics);
			}
			
			analyticData.setData(dataNameAnalyticsList);
			
			logger.info("[END] [getGroupAnalyticsData] [DAO LAYER]");

			return analyticData;
		}

		@Override
		public CategoryAnalytics getSiteAnalyticsData(AnalyticsRequest analyticsRequest, int userId)
				throws VEMAppException {
			
			logger.info("[BEGIN] [getSiteAnalyticsData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_site_analytics]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// AnalyticsData instance initialization
			List<AnalyticsData> list = new LinkedList<>();
			CategoryAnalytics analyticData = new CategoryAnalytics();
			
			// This is the map to filter by sites
			Map<Integer, List<AnalyticsData>> sitesMap = new HashMap<>();
			
			try {
				
				// Passing store procedure as input
				list = jdbcTemplate.query("call sp_get_rpt_site_analytics ('"+
							analyticsRequest.getSiteIds()+"','"+
							analyticsRequest.getFromDate()+"','"+
							analyticsRequest.getToDate()+"','"+
							analyticsRequest.getDataType()+"','"+
							analyticsRequest.getAnalyticParams()+"','"+
							userId+"')", new RowMapper<AnalyticsData>() {
					public AnalyticsData mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						
						AnalyticsData data = new AnalyticsData();
						
						data.setCount(rs.getInt("COUNT"));
						data.setName(rs.getString("NAME"));
						data.setId(rs.getInt("ID"));
						data.setType(rs.getString("V_TYPE"));
						data.setParamId(rs.getInt("PARAM_ID"));
						data.setParamName(rs.getString("PARAM_NAME"));
						data.setColorCode(rs.getString("COLOR_CODE"));
						
						return data;
						}
					});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.SITE_ANALYTICS_FETCH_SUCCESS, logger, e);
			}
			
			// Filtering the sites by its id
			for (AnalyticsData str : list) {
				List<AnalyticsData> dataList = new LinkedList<>();
				if(sitesMap.containsKey(str.getId())){
					dataList = sitesMap.get(str.getId());
					dataList.add(str);
					sitesMap.put(str.getId(), dataList);			
				}else{
					dataList.add(str);
					sitesMap.put(str.getId(), dataList);
				}				
			}
			
			// List with unique site ids
			List<DataNameAnalytics> dataNameAnalyticsList = new LinkedList<>();
			
			// Looping sites maps
			for (Map.Entry<Integer, List<AnalyticsData>> entry : sitesMap.entrySet())
			{
				List<AnalyticsData> dataList = entry.getValue();
				DataNameAnalytics dataNameAnalytics = new DataNameAnalytics();
				List<DataBean> dataBeanList = new LinkedList<>();
				List<String> categoryList = new LinkedList<>();
				/*
				 * Sorting result sets to get desired order in front end side
				 */
				Collections.sort(dataList, new AnalyticsGroupSiteComparator());
				
				for (AnalyticsData analyticsData : dataList) {
					DataBean dataBean = new DataBean();
					dataBean.setColor(analyticsData.getColorCode());
					dataBean.setY(analyticsData.getCount());
					dataBean.setParamName(analyticsData.getParamName());
					
					dataNameAnalytics.setId(analyticsData.getId());
					dataNameAnalytics.setName(analyticsData.getName());
					categoryList.add(analyticsData.getParamName());
					dataBeanList.add(dataBean);
				}

				analyticData.setCategories(categoryList);
				dataNameAnalytics.setData(dataBeanList);
				dataNameAnalyticsList.add(dataNameAnalytics);
			}
			
			analyticData.setData(dataNameAnalyticsList);

			logger.info("[END] [getSiteAnalyticsData] [DAO LAYER]");
			
			return analyticData;
		}

		@SuppressWarnings("unchecked")
		@Override
		public JSONObject getTrendingAnalytics(AnalyticsRequest analyticsRequest, int userId, String timeZone) throws VEMAppException {
			
			logger.info("[BEGIN] [getTempSetpointReport] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_temp_setpoints_data]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Reports instance initialization
			List<TempSetpointReport> tempSetPointData = new LinkedList<>();
			
			// Creating map instances based on units
			Map<String, TemperatureSetpoint> farenHeatParams = new LinkedHashMap<>();
			Map<String, TemperatureSetpoint> onOffParams = new LinkedHashMap<>();
			Map<String, TemperatureSetpoint> modeParams = new LinkedHashMap<>();
			Map<String, TemperatureSetpoint> daysParams = new LinkedHashMap<>();
			Map<String, Boolean> graphValidation = new LinkedHashMap<>();
			
			boolean isFarenHeatParamsValid = false;
			boolean isOnOffParamsValid = false;
			boolean isModeParamsValid = false;
			boolean isDaysParamsValid = false;
			
			Set<Integer> devicesCount = new HashSet<>();

			JSONObject tempSetpointObj = new JSONObject();
			
			//re configuring analytic params
			String analyticParams = ""; 
			
			try {
				
				
				//Splitting analytic params and device id's by comma (,)
				String[] temperatureParamsArr = analyticsRequest.getFarenHeatParams().split(",");
				String[] onOffParamsArr = analyticsRequest.getOnOffParams().split(",");
				String[] modeParamsArr = analyticsRequest.getModeParams().split(",");
				String[] daysParamsArr = analyticsRequest.getDaysParams().split(",");
				
				// Getting device id's
				String[] devicesArr = analyticsRequest.getDeviceIds().split(",");
				
				/*
				 * Checking whether the devices are there for current selection
				 * then only allowing user to see the report
				 * 
				 * If controller will comes here means devices length is always be more than zero
				 * we are checking this in service level only
				 */
				if(devicesArr.length != 0){
					/*
					 * Currently we decided to see the max 8 parameters w.r.t devices 
					 */
					if(temperatureParamsArr.length * devicesArr.length <= 8){
						isFarenHeatParamsValid = true;
						analyticParams = analyticsRequest.getFarenHeatParams();
					}
					if(onOffParamsArr.length * devicesArr.length <= 8){
						isOnOffParamsValid = true;
						analyticParams = analyticParams +","+ analyticsRequest.getOnOffParams();
					}
					if(modeParamsArr.length * devicesArr.length <= 8){
						isModeParamsValid = true;
						analyticParams = analyticParams +","+ analyticsRequest.getModeParams();
					}
					if(daysParamsArr.length * devicesArr.length <= 8){
						isDaysParamsValid = true;
						analyticParams = analyticParams +","+ analyticsRequest.getDaysParams();
					}
				}
								
				// Passing store procedure as input
				tempSetPointData = jdbcTemplate.query("call sp_get_rpt_temp_setpoints_data ('0','0','0','"+
						analyticsRequest.getDeviceIds()+"','"+
						"0','"+
						analyticsRequest.getFromDate()+"','"+
						analyticsRequest.getToDate()+"','"+
						userId+"','"+
						analyticParams+"','"+
						timeZone+"')", new RowMapper<TempSetpointReport>() {
					public TempSetpointReport mapRow(ResultSet rs, int rowNum)
							throws SQLException {
								
								TempSetpointReport tempSetpoint = new TempSetpointReport();
								tempSetpoint.setUtcTime(rs.getLong("UTC_TIME"));
								tempSetpoint.setAnalyticParamName(rs.getString("ANALYT_NAME"));
								tempSetpoint.setAnalyticParamUnit(rs.getString("ANALYT_UNIT"));
								tempSetpoint.setAnalyticParamId(rs.getString("ANALYT_ID"));
								tempSetpoint.setParamValue(rs.getDouble("ANALYT_VALUE"));
								tempSetpoint.setDeviceId(rs.getInt("DEVICE_ID"));
								tempSetpoint.setCreatedTime(rs.getString("created_time"));
								tempSetpoint.setDeviceName(rs.getString("name"));
								tempSetpoint.setParamFullName(rs.getString("FULL_NAME"));
								devicesCount.add(tempSetpoint.getDeviceId());
								return tempSetpoint;
						}
					});

				// list instances by units
				List<TemperatureSetpoint> temperatureList = new LinkedList<>();
				List<TemperatureSetpoint> onOffList = new LinkedList<>();
				List<TemperatureSetpoint> modeList = new LinkedList<>();
				List<TemperatureSetpoint> daysList = new LinkedList<>();
				
				// looping and preparing the required result format
				for (TempSetpointReport tempSetpointReport : tempSetPointData) {
					if(("F").equals(tempSetpointReport.getAnalyticParamUnit())){
						TemperatureSetpoint tempSetPoint;
						// Separated the logic to reuse -  this function is written to get desired output format to display graphs
						tempSetPoint = getTrendingAnalyticFilteredMap(tempSetpointReport, farenHeatParams, devicesCount.size());
						farenHeatParams.put(tempSetpointReport.getAnalyticParamName()+tempSetpointReport.getDeviceId(), tempSetPoint);
					}else if(("B").equals(tempSetpointReport.getAnalyticParamUnit())){
						TemperatureSetpoint tempSetPoint = getTrendingAnalyticFilteredMap(tempSetpointReport, onOffParams, devicesCount.size());
						// Separated the logic to reuse -  this function is written to get desired output format to display graphs
						onOffParams.put(tempSetpointReport.getAnalyticParamName()+tempSetpointReport.getDeviceId(), tempSetPoint);
					}else if(("M").equals(tempSetpointReport.getAnalyticParamUnit())){
						TemperatureSetpoint tempSetPoint = getTrendingAnalyticFilteredMap(tempSetpointReport, modeParams, devicesCount.size());
						// Separated the logic to reuse -  this function is written to get desired output format to display graphs
						modeParams.put(tempSetpointReport.getAnalyticParamName()+tempSetpointReport.getDeviceId(), tempSetPoint);
					}else if(("D").equals(tempSetpointReport.getAnalyticParamUnit())){
						TemperatureSetpoint tempSetPoint = getTrendingAnalyticFilteredMap(tempSetpointReport, daysParams, devicesCount.size());
						// Separated the logic to reuse -  this function is written to get desired output format to display graphs
						daysParams.put(tempSetpointReport.getAnalyticParamName()+tempSetpointReport.getDeviceId(), tempSetPoint);
					}
				}
				
				// adding farenheat unit type data to faren heat list
				for (Map.Entry<String, TemperatureSetpoint> entry : farenHeatParams.entrySet())
				{
					temperatureList.add(entry.getValue());
				}
				
				int i = 1;

				// Boolean(on/off) type loop
				for (Map.Entry<String, TemperatureSetpoint> entry : onOffParams.entrySet())
				{
					
					TemperatureSetpoint tp = entry.getValue();
					List<JSONArray> list = tp.getData();
					List<JSONArray> newList = new LinkedList<>();
					boolean isListNotContainsAllNull = false;
					for (JSONArray jsonArray : list) {
						// getting second value by index and comparing the values
						if(ConfigurationUtils.isZero((Double)jsonArray.get(1), 0.0)){
							jsonArray.set(1, null); // Setting 1 index value as null if 0
						}else{
							isListNotContainsAllNull = true;
							jsonArray.set(1, i); // Setting 1 index value
						}
						// adding filtered JSON array to new list
						newList.add(jsonArray);
					}
					tp.setData(newList);
					
					onOffList.add(tp);
					
					// incrementing the i value
					if(isListNotContainsAllNull)
					i++;
				}
				
				// adding farenheat unit type data to faren heat list
				for (Map.Entry<String, TemperatureSetpoint> entry : modeParams.entrySet())
				{
					modeList.add(entry.getValue());
				}
				
				// adding farenheat unit type data to faren heat list
				for (Map.Entry<String, TemperatureSetpoint> entry : daysParams.entrySet())
				{
					daysList.add(entry.getValue());
				}
				
				/*
				 * Sorting result sets to get desired order in front end side
				 */
				Collections.sort(temperatureList,new AnalyticParamComparator());
				Collections.sort(onOffList,new AnalyticParamComparator());
				Collections.sort(modeList,new AnalyticParamComparator());
				Collections.sort(daysList,new AnalyticParamComparator());
				
				// Graph validation map
				graphValidation.put("isFarenHeatParamsValid", isFarenHeatParamsValid);
				graphValidation.put("isOnOffParamsValid", isOnOffParamsValid);
				graphValidation.put("isModeParamsValid", isModeParamsValid);
				graphValidation.put("isDaysParamsValid", isDaysParamsValid);
								
				// Final desired format			
				tempSetpointObj.put("tempurateList", temperatureList);
				tempSetpointObj.put("onOffList", onOffList);
				tempSetpointObj.put("modeTypeList", modeList);
				tempSetpointObj.put("daysTypeList", daysList);
				if(analyticsRequest.getDegreeDaysGraph() == 1){
					List<TemperatureSetpoint> degreeDaysList = new LinkedList<>();
					degreeDaysList.addAll(onOffList);
					degreeDaysList.addAll(daysList);
					tempSetpointObj.put("degreeDaysList", degreeDaysList);
				}
				tempSetpointObj.put("graphsValidation", graphValidation);
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.REPORT_ERROR_FETCH_FAILED, logger, e);
			}
			
			return tempSetpointObj;
		}
		
		@SuppressWarnings("unchecked")
		private TemperatureSetpoint getTrendingAnalyticFilteredMap(TempSetpointReport tempSetpointReport, Map<String, TemperatureSetpoint> map, int deviceCount) {
			TemperatureSetpoint tempSetPoint = new TemperatureSetpoint();
			List<JSONArray> timeValueList = new LinkedList<>();
			/*
			 * Putting device name only if we have more than one devices
			 */
			if(deviceCount > 1){
				tempSetPoint.setName(tempSetpointReport.getAnalyticParamName()+" ("+tempSetpointReport.getDeviceName()+")");
			}else{
				tempSetPoint.setName(tempSetpointReport.getAnalyticParamName());
			}
			tempSetPoint.setParamShortName(tempSetpointReport.getAnalyticParamName());
			tempSetPoint.setParamFullName(tempSetpointReport.getParamFullName());
			tempSetPoint.setDeviceId(tempSetpointReport.getDeviceId());
			tempSetPoint.setDeviceName(tempSetpointReport.getDeviceName());
			tempSetPoint.setParamId(Integer.parseInt(tempSetpointReport.getAnalyticParamId()));
			JSONArray timeValue = new JSONArray();
			timeValue.add(tempSetpointReport.getUtcTime());
			timeValue.add(tempSetpointReport.getParamValue());
			timeValue.add(tempSetpointReport.getCreatedTime());
			if(map.containsKey(tempSetpointReport.getAnalyticParamName()+tempSetpointReport.getDeviceId())){
				tempSetPoint = map.get(tempSetpointReport.getAnalyticParamName()+tempSetpointReport.getDeviceId());
				timeValueList = tempSetPoint.getData();
				timeValueList.add(timeValue);
			}else{
				timeValueList.add(timeValue);
				tempSetPoint.setData(timeValueList);
			}
			return tempSetPoint;
		}

		@Override
		public String getDeviceIds(String type, String typeIds, int userId) throws VEMAppException {
			
			logger.info("[BEGIN] [getDeviceIds] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_deviceids]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			// Initialization of device ids
			String deviceIds = "";
			
			try {
				
				// Passing store procedure as input
				deviceIds = jdbcTemplate.queryForObject("call sp_get_rpt_deviceids ('"+
						type+"','"+
						typeIds+"','"+
						userId+"')", new RowMapper<String>() {
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
							return rs.getString("device_ids");
						}
					});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DEVICE_IDS_ERROR_FETCH_FAILED, logger, e);
			}
			
			logger.info("[END] [getDeviceIds] [DAO LAYER]");
			
			return deviceIds;
		}

		@Override
		public Report getDegradedPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
			logger.info("[BEGIN] [getDegradedPerformData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_degradedhvac]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			final Report report = new Report();
			
			try {
				
				// Passing store procedure as input
				jdbcTemplate.queryForObject("call sp_get_rpt_degradedhvac ('"+
						reportRequest.getType()+"','"+
						(StringUtils.isBlank(reportRequest.getCustomerIds())?"0":reportRequest.getCustomerIds())+"','"+
						(StringUtils.isBlank(reportRequest.getGroupIds())?"0":reportRequest.getGroupIds())+"','"+
						(StringUtils.isBlank(reportRequest.getSiteIds())?"0":reportRequest.getSiteIds())+"','"+
						reportRequest.getInDays()+"','"+
						timeZone+"','"+
						reportRequest.getFromDate()+"','"+
						reportRequest.getToDate()+"','"+
						userId+"')",  new RowMapper<Report>() {
							public Report mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								
									report.setId(1);
									report.setName(CommonConstants.DEGRADED_HVAC_UNITS);
									report.setY(rs.getInt("degraded_issue_cnt"));
									report.setMax(rs.getInt("degraded_totalcount"));
									report.setReportId(1);

									logger.info("["+report.getName()+"]"+"["+report.getY()+"]"+"["+report.getMax()+"]"+"["+report.getExtraValue()+"]");
									
									return report;
								}
							});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASHBOARD_DEGRADED_PERFORMANCE_FETCH_FAIL, logger, e);
			}

			logger.info("[END] [getDegradedPerformData] [DAO LAYER]");
			
			return report;
		}

		@Override
		public Report getWithinSetpointPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {
			
			logger.info("[BEGIN] [getWithinSetpointPerformData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_withinsetpoints]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			final Report report = new Report();
			
			try {
				
				// Passing store procedure as input
				jdbcTemplate.queryForObject("call sp_get_rpt_withinsetpoints ('"+
						reportRequest.getType()+"','"+
						(StringUtils.isBlank(reportRequest.getCustomerIds())?"0":reportRequest.getCustomerIds())+"','"+
						(StringUtils.isBlank(reportRequest.getGroupIds())?"0":reportRequest.getGroupIds())+"','"+
						(StringUtils.isBlank(reportRequest.getSiteIds())?"0":reportRequest.getSiteIds())+"','"+
						reportRequest.getInDays()+"','"+
						timeZone+"','"+
						reportRequest.getFromDate()+"','"+
						reportRequest.getToDate()+"','"+
						userId+"')",  new RowMapper<Report>() {
							public Report mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								
									report.setId(2);
									report.setName(CommonConstants.WITHIN_SETPOINTS);
									report.setY(rs.getInt("within_issue_cnt"));
									report.setMax(rs.getInt("within_totalcount"));
									report.setReportId(2);

									logger.info("["+report.getName()+"]"+"["+report.getY()+"]"+"["+report.getMax()+"]"+"["+report.getExtraValue()+"]");
									
									return report;
								}
							});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASHBOARD_WITHIN_SETPOINT_PERFORMANCE_FETCH_FAIL, logger, e);
			}

			logger.info("[END] [getWithinSetpointPerformData] [DAO LAYER]");
			
			return report;
		}

		@Override
		public Report getHVACUsagePerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {

			logger.info("[BEGIN] [getHVACUsagePerformData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_hvacusage]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			final Report report = new Report();
			
			try {
				
				// Passing store procedure as input
				jdbcTemplate.queryForObject("call sp_get_rpt_hvacusage ('"+
						reportRequest.getType()+"','"+
						(StringUtils.isBlank(reportRequest.getCustomerIds())?"0":reportRequest.getCustomerIds())+"','"+
						(StringUtils.isBlank(reportRequest.getGroupIds())?"0":reportRequest.getGroupIds())+"','"+
						(StringUtils.isBlank(reportRequest.getSiteIds())?"0":reportRequest.getSiteIds())+"','"+
						reportRequest.getInDays()+"','"+
						timeZone+"','"+
						reportRequest.getFromDate()+"','"+
						reportRequest.getToDate()+"','"+
						userId+"')",  new RowMapper<Report>() {
							public Report mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								
									report.setId(3);
									report.setName(CommonConstants.HVAC_USAGE);
									report.setReportId(3);
									report.setExtraValue(rs.getInt("COOL_STG") / 60);
									report.setY(rs.getInt("HEAT_STG") / 60);
									report.setMax(Integer.parseInt(StringUtils.isNotBlank(rs.getString("SUM(occupy_hrs)"))?rs.getString("SUM(occupy_hrs)"):"0") / 60);
								
									logger.info("["+report.getName()+"]"+"["+report.getY()+"]"+"["+report.getMax()+"]"+"["+report.getExtraValue()+"]");
									
									return report;
								}
							});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASHBOARD_HVACUSAGE_PERFORMANCE_FETCH_FAIL, logger, e);
			}

			logger.info("[END] [getHVACUsagePerformData] [DAO LAYER]");
			
			return report;
		}

		@Override
		public Report getManualChangesPerformData(ReportRequest reportRequest, int userId, String timeZone) throws VEMAppException {

			logger.info("[BEGIN] [getManualChangesPerformData] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug(CommonConstants.SP_PREFIX_CONSTANT+"[sp_get_rpt_manualchanges]");
				logger.debug(CommonConstants.PARAMETERS);
			}
			
			final Report report = new Report();
			
			try {
				
				// Passing store procedure as input
				jdbcTemplate.queryForObject("call sp_get_rpt_manualchanges ('"+
						reportRequest.getType()+"','"+
						(StringUtils.isBlank(reportRequest.getCustomerIds())?"0":reportRequest.getCustomerIds())+"','"+
						(StringUtils.isBlank(reportRequest.getGroupIds())?"0":reportRequest.getGroupIds())+"','"+
						(StringUtils.isBlank(reportRequest.getSiteIds())?"0":reportRequest.getSiteIds())+"','"+
						reportRequest.getInDays()+"','"+
						timeZone+"','"+
						reportRequest.getFromDate()+"','"+
						reportRequest.getToDate()+"','"+
						userId+"')",  new RowMapper<Report>() {
							public Report mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								
									report.setId(4);
									report.setName(CommonConstants.MANUAL_CHANGES);
									report.setY(rs.getInt("manual_issue_cnt"));
									report.setMax(rs.getInt("manual_totalcount"));
									report.setReportId(4);
									
									logger.info("["+report.getName()+"]"+"["+report.getY()+"]"+"["+report.getMax()+"]"+"["+report.getExtraValue()+"]");
									
									return report;
								}
							});
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.DASHBOARD_MANUAL_PERFORMANCE_FETCH_FAIL, logger, e);
			}

			logger.info("[END] [getManualChangesPerformData] [DAO LAYER]");
			
			return report;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public JSONObject getSitesForGroups(String customerId, String groupIds, GetUserResponse userDetails) throws VEMAppException {
			
			logger.info("[BEGIN] [getSitesForGroups] [Reports DAO LAYER]");
			Response response =new Response();
			
			JSONParser parser=new JSONParser();
			JSONArray siteArray=new JSONArray();
			JSONArray deviceArray = new JSONArray();
			JSONObject resultObj = new JSONObject();
			
			try {
				
				SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_get_rpt_sites_by_groups");
				Map<String, Object> inParamMap = new HashMap<String, Object>();
				inParamMap.put("in_group_ids", groupIds);
				inParamMap.put("in_super_admin", userDetails.getIsSuper());
				inParamMap.put("in_eai_admin", userDetails.getIsEai());
				inParamMap.put("in_user_id", userDetails.getUserId());
				inParamMap.put("customerId", customerId);
				
				logger.info("executing proc sp_get_rpt_sites_by_groups params "+inParamMap);
				
				SqlParameterSource in = new MapSqlParameterSource(inParamMap);
				Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
				Iterator<Entry<String, Object>> itr = simpleJdbcCallResult.entrySet().iterator();

				while (itr.hasNext()) {
				        Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
				        String key = entry.getKey();
				        if(key.equals(RESULT_SET_1)){
				        	Object value = entry.getValue();
				        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
				        	siteArray=(JSONArray)parser.parse(tempAry.toString());	
				        }else if(key.equals(RESULT_SET_2)){
				        	Object value = entry.getValue();
				        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
				        	deviceArray=(JSONArray)parser.parse(tempAry.toString());	
				        }
				        if(key.equals(RESULT_SET_3)){
				        	Object value = entry.getValue();
				        	org.json.JSONArray tempAry=new org.json.JSONArray((ArrayList)value);
				        	siteArray=(JSONArray)parser.parse(tempAry.toString());	
				        }
				    }
				
				resultObj.put("siteList", siteArray);
				resultObj.put("deviceList", deviceArray);
				   
				   
			} catch (Exception e) {
				logger.error("",e);
				resultObj = null;
				throw new VEMAppException("Internal Error occured at DAO layer");
				
			}
				    	
			logger.info("[END] [getSitesForGroups] [Reports DAO LAYER]");
			
			return resultObj;
		}
		@SuppressWarnings("unchecked")
		@Override
		public Map<Integer, ArrayList<HashMap<String, String>>> getPDFReportUsersData() throws VEMAppException {
			
			logger.info("[BEGIN] [getPDFReportUsersData] [Report DAO LAYER]");
			
			HashMap<Integer, ArrayList<HashMap<String, String>>> userMap=new LinkedHashMap<>();

			try {
				
				jdbcTemplate.query("call sp_get_reports_users ()", new RowCallbackHandler() {
					
					ArrayList<HashMap<String, String>> sitesList = null;
					HashMap<String,String> siteData = null;
					
					@SuppressWarnings("unchecked")
					@Override
				    public void processRow(ResultSet rs) throws SQLException {

				    	logger.debug("[DEbug] the user_id - "+rs.getString("user_id"));
				    	
				    	if(userMap.containsKey(rs.getInt("user_id"))){
				    		sitesList = userMap.get(rs.getInt("user_id"));
				    	}else{
				    		sitesList = new ArrayList<>();
				    	}
				    	
				    	siteData = new LinkedHashMap<>();
			    		
				    	siteData.put("userEmail", CommonUtility.isNull(rs.getString("user_email")));
				    	siteData.put("userFname", CommonUtility.isNull(rs.getString("user_fname")));
				    	siteData.put("userLname", CommonUtility.isNull(rs.getString("user_lname")));
				    	siteData.put("userReportPreference", CommonUtility.isNull(rs.getInt("user_report_preference"))+"");
				    	siteData.put("userReportLevel", CommonUtility.isNull(rs.getInt("user_report_level"))+"");
				    	siteData.put("userReportPreferenceText", CommonUtility.isNull(rs.getString("user_report_preference_text"))+"");
				    	siteData.put("userReportLevelText", CommonUtility.isNull(rs.getString("user_report_level_text"))+"");
				    	siteData.put("loggedUser", CommonUtility.isNull(rs.getInt("logged_user"))+"");
				    	
			    		siteData.put("siteId", CommonUtility.isNull(rs.getInt("site_id"))+"");
			    		siteData.put("siteName", CommonUtility.isNull(rs.getString("site_name")));
			    		siteData.put("siteCode", CommonUtility.isNull(rs.getString("site_internal_id")));
			    		siteData.put("deviceCount", CommonUtility.isNull(rs.getInt("device_count"))+"");
			    		
			    		siteData.put("groupIds", CommonUtility.isNull(rs.getString("group_ids")));
			    		
			    		siteData.put("customerId", CommonUtility.isNull(rs.getInt("customer_id"))+"");
			    		siteData.put("companyName", CommonUtility.isNull(rs.getString("company_name")));
			    		siteData.put("companyLogo", CommonUtility.isNull(rs.getString("company_logo")));
			    		siteData.put("comfortOpt", CommonUtility.isNull(rs.getInt("comfort_opt")) > 0 ? "1" : "0");
			    		siteData.put("siteAddress", CommonUtility.isNull(rs.getString("site_address")));
			    		
			    		sitesList.add(siteData);
			    		
				    	userMap.put(CommonUtility.isNull(rs.getInt("user_id")), sitesList);
				    	
					}
				});
				
				logger.debug("[DEBUG] userMap - "+userMap);
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.LIST_SITE_FAILED, logger, e);
			}
			
			logger.info("[END] [getPDFReportUsersData] [Report DAO LAYER]");
			
			return userMap;
		}

		@Override
		public boolean isDevicesInSameTimezone(String deviceIds) throws VEMAppException {
			
			logger.info("[BEGIN] [isDevicesInSameTimezone] [DAO LAYER]");
			
			if (logger.isDebugEnabled()){
				logger.debug("Executing the Query");
				logger.debug("Parameters:");
				logger.debug("deviceIds :"+deviceIds);
			}
			
			// flag initialization
			int flag = 0;
			
			// Declaring SimpleJdbcCall 
			SimpleJdbcCall simpleJdbcCall;
			
			try {
				
				/*
				 * Initialize the simpleJdbcCall to call the stored procedure
				 * and Adding the stored procedure name to simpleJdbcCall object.  
				 */
				simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_get_rpt_check_devices_timezone");
		
				// Passing parameters to the stored procedure
				Map<String, Object> parameters = new HashMap<>();
				parameters.put("deviceIds",deviceIds);
		
				// Giving parameters to SQL Parameter source
				SqlParameterSource in = new MapSqlParameterSource(parameters);
				
				// Executing stored procedure
				Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);
				
				// Fetching the updated user id
				flag = Integer.parseInt(simpleJdbcCallResult.get("dbFlag").toString());
				
			} catch (Exception e) {
				//Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.USER_ERROR_EMAIL_EXIST_FAILED, logger, e);
			}
			
			logger.info("[END] [isDevicesInSameTimezone] [DAO LAYER]");
			
			return (flag == 1) ? true : false;
		}
}
