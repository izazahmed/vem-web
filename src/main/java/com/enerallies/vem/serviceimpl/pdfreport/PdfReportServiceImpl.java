package com.enerallies.vem.serviceimpl.pdfreport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.beans.admin.MailBroadCast;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.audit.AddManualLogRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.beans.common.ValidatorBean;
import com.enerallies.vem.beans.pdfreport.AddPDFReportDataRequest;
import com.enerallies.vem.beans.pdfreport.PDFThreadRequest;
import com.enerallies.vem.beans.report.DataName;
import com.enerallies.vem.beans.report.HVACUsage;
import com.enerallies.vem.beans.report.Report;
import com.enerallies.vem.beans.report.ReportRequest;
import com.enerallies.vem.beans.report.ReportResponse;
import com.enerallies.vem.dao.activity.ActivityLogDao;
import com.enerallies.vem.dao.alert.AlertDao;
import com.enerallies.vem.dao.pdfreport.PdfReportDAO;
import com.enerallies.vem.dao.report.ReportDao;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.pdfreport.PdfReportService;
import com.enerallies.vem.serviceimpl.role.RoleServiceImpl;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.CommonUtility;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.DatesUtil;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.publish.MailPublisher;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

/**
 * File Name : PdfReportServiceImpl 
 * 
 * PdfReportServiceImpl: Its an implementation class for PdfReportService service interface.
 *
 * @author Cambridge Technologies.
 * contact Cambridge Technologies � Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        08-02-2017
 *
 * MODIFICATION HISTORY
 * 
 * DATE				USER             	COMMENTS
 * 08-02-2017		Bhoomika Rabadiya   File Created.
 *
 */

@Service("pdfReportService")
@Transactional
public class PdfReportServiceImpl implements PdfReportService {

	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(RoleServiceImpl.class);
	
	/**instantiating the PdfReport dao for accessing the dao layer.*/
	@Autowired PdfReportDAO pdfReportDAO;
	
	@Autowired ReportDao reportDao;
	
	@Autowired ActivityLogDao activityLogDao;
	
	@Autowired AlertDao alertDao;
	
	//Images location
	private static final String IMAGES_LOCATION = "vem.images";
		
	private String getColumnChart(JSONArray jsonArray) throws JSONException {
		 int flag = 0;
		 int max = 0;
		 int tickInterval = 0;
		 for (int i = 0; i < jsonArray.length(); i++) {
			 JSONObject barChart = jsonArray.getJSONObject(i);
			 JSONArray barChartArray = barChart.getJSONArray("data");
			  if (barChartArray.getJSONObject(0).getInt("y") > 10) {
				  flag++; 
			  }
		 }
		 if (flag == 0) {
			 max = 10;
			 tickInterval = 2;
		 }
		 
    	JSONObject subObj = new JSONObject();
    	subObj.put("chart", new JSONObject().put("type", "column"));
    	subObj.put("title", new JSONObject().put("text", JSONObject.NULL));
    	subObj.put("yAxis", new JSONObject().put("allowDecimals", false).put("gridLineWidth", 3)
    		.put("labels", new JSONObject().put("style", new JSONObject().put("fontWeight", "bold")))
    		 .put("tickWidth", 1).put("stackLabels", new JSONObject().put("enabled", true))
    		 .put("min", 0).put("max", max == 0 ? JSONObject.NULL : max).put("tickInterval", tickInterval == 0 ? JSONObject.NULL : tickInterval).put("endOnTick", false).put("tickLength", 3).put("title",  new JSONObject().put("text", JSONObject.NULL))
    		 .put("lineWidth", 1));
    	subObj.put("xAxis", new JSONObject().put("type", "category").put("labels", new JSONObject().put("style", new JSONObject().put("fontWeight", "bold"))));
    	subObj.put("plotOptions", new JSONObject().put("column", new JSONObject().put("stacking", "normal").put("pointWidth", 60).put("groupPadding", 0)));
    	subObj.put("credits", new JSONObject().put("enabled", false));
    	subObj.put("legend", new JSONObject().put("enabled", false));
    	subObj.put("series", jsonArray.length() > 0 ? jsonArray : null);
          		
		return subObj.toString();
	}
    @SuppressWarnings("unchecked")
	private String getPieChart(JSONArray jsonArray, String name) throws JSONException {	    
    	JSONObject subObj = new JSONObject(); 
    	subObj.put("chart", new JSONObject().put("type", "pie").put("marginBottom", "70").put("marginLeft", "20").put("marginRight", "20"));
    	subObj.put("title", new JSONObject().put("text", JSONObject.NULL));
    	subObj.put("credits", new JSONObject().put("enabled", false));    	
    	subObj.put("legend", new JSONObject().put("enabled", false));
    	subObj.put("plotOptions", new JSONObject().put("pie", new JSONObject().put("size", "80%")
    			.put("dataLabels", new JSONObject().put("enabled", true).put("connectorPadding", 0).put("connectorWidth", 5)
    			.put("format", "{point.name}").put("style", new JSONObject().put("fontWeight", "bold").put("fontSize", "18px").put("width", "120")))));
        org.json.simple.JSONArray jsonArrayData = new org.json.simple.JSONArray();
    	jsonArrayData.add(new JSONObject().put("data", jsonArray));
    	subObj.put("series", jsonArrayData.size() > 0 ? jsonArrayData : null);
          		
		return subObj.toString();
	}
    
    @SuppressWarnings("unchecked")
   	private static String getDonutPieChart(JSONObject hvacReportJson, String name) throws JSONException {	    
       	
    	org.json.simple.JSONArray browserData = new org.json.simple.JSONArray();
    	org.json.simple.JSONArray versionsData = new org.json.simple.JSONArray();
    	
    	String[] colors = {"#7cb5ec", "#434348", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1"};
    	JSONArray categories =  (JSONArray) hvacReportJson.get("categories");
    	JSONArray data = hvacReportJson.getJSONArray("data");
    	int k = 0;
    	int dataLen = data.length();
    	int drillDataLen;
    	String brightnessTemp;
    	    for (int i = 0; i < dataLen; i += 1) {
    	    	if (k == 0 || k==1 || k==3) {
    	    		k = 4;
   				}
    	    	JSONObject hvacObj = data.getJSONObject(i);
    	    	browserData.add(new JSONObject().put("id", hvacObj.get("id")).put("name", categories.get(i)).put("y", hvacObj.get("y"))
    	    			.put("color", colors[k]));
    	        k += 1;
       			if (k > 10) {
       				k = 0;
       			}
       			JSONArray tempData =  (JSONArray) hvacObj.getJSONObject("drilldown").get("data");
       			JSONArray categoriesDataData =  (JSONArray) hvacObj.getJSONObject("drilldown").get("categories");
       			drillDataLen = tempData.length();
    	        for (int j = 0; j < drillDataLen; j += 1) {
    	        	if (j==0) {
    	        		brightnessTemp = "#7cb5ec";
    	        	} else {
    	        		brightnessTemp = "#f7a35c";
    	        	}
    	        	JSONObject hvacTempObj = data.getJSONObject(i);
    	        	
    	        	if (Integer.parseInt(tempData.get(j).toString()) > 0) {
    	        		versionsData.add(new JSONObject().put("id", hvacTempObj.get("id")).put("name", categoriesDataData.get(j)).put("y", tempData.get(j))
        	    			.put("color", brightnessTemp));
    	        	}
    	        }
    	    }
    	    
    	
       	JSONObject subObj = new JSONObject();	
       	subObj.put("chart", new JSONObject().put("type", "pie").put("marginBottom", "70").put("marginLeft", "20").put("marginRight", "20"));
       	subObj.put("title", new JSONObject().put("text", JSONObject.NULL));
       	subObj.put("credits", new JSONObject().put("enabled", false));    	
       	subObj.put("legend", new JSONObject().put("enabled", false));
       	JSONArray center = new JSONArray();
       	center.put("50%");
       	center.put("50%");
       	subObj.put("plotOptions", new JSONObject().put("pie", new JSONObject().put("shadow", false).put("center", center)));
        org.json.simple.JSONArray jsonArrayData = new org.json.simple.JSONArray();
       	jsonArrayData.add(new JSONObject().put("data", browserData).put("size", "60%").put("dataLabels", new JSONObject().put("enabled", false)));
       	jsonArrayData.add(new JSONObject().put("data", versionsData).put("size", "80%").put("innerSize", "60%")
       			.put("dataLabels", new JSONObject().put("style", new JSONObject().put("fontWeight", "bold").put("fontSize", "18px").put("width", "120"))
       			.put("connectorPadding", 0).put("connectorWidth", 5)/*.put("distance", 5)*/));
       	subObj.put("series", jsonArrayData.size() > 0 ? jsonArrayData : null);
       	
   		return subObj.toString();
    }

	@Override
	public Response weeklyPDFReportGenerator(PDFThreadRequest pDFThreadRequest) throws VEMAppException, MalformedURLException {
		logger.info("[BEGIN] [weeklyPDFReportGenerator] [PDF REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		Map<Integer, ArrayList<HashMap<String, String>>> userData = null;
		URL url = pDFThreadRequest.getUrl();
		
		//Getting the dates
    	Date date = pDFThreadRequest.getDate();
    	String folderName = pDFThreadRequest.getFolderName();
    	Calendar cal = null;
		
		JSONObject pdfReportJson;
		org.json.simple.JSONArray siteListArray;
		org.json.simple.JSONObject siteJsonObject;
		
		List<Report> degradedReportJsonArray;
		List<Report> withinReportJsonArray;
		List<Report> manualChangesJsonArray;
		HVACUsage hvacReportJson;
		List<DataName> reportsBarJsonArray;
		List<Report> criticalIssuesJsonArray;
		List<Report> resolvedIssuesJsonArray;
		List<Report> correspondencesJsonArray;
		
		int reportLevel = 0;
		int reportPreference = 0;
		List<String> reportLevelIds;
		String userFirstName = "";
		String userLastName = "";
		String userEmail = "";
		String reportPreferenceText = "";
		String reportLevelText = "";
		int reportPreferencedays = 0;
		int daysRedirect = 0;
		String toDate = "";
		String fromDate = "";
		String toDateActivity = "";
		String fromDateActivity = "";
		String toDateMMDD = "";
		String fromDateMMDD = "";
		String toDateMMDDDot = "";
		String fromDateMMDDDot = "";
		String toDateText = "";
		String fromDateText = "";
		int systemUserId = 0;
		int userId = 0;
		List<String> siteNames;
		String timeZone="";
		org.json.simple.JSONObject timeZoneJson;
		
		try {
				
			ReportResponse reportResponse = new ReportResponse();
			
			//Getting the users data
			userData = pdfReportDAO.getPDFReportUsersData();
			
			ArrayList<HashMap<String, String>> reportSiteList;
			
			for (Map.Entry<Integer, ArrayList<HashMap<String, String>>> entry : userData.entrySet()) {
				
				siteNames = new ArrayList<String>();
				
				pdfReportJson = new JSONObject();
			    
				pdfReportJson.put("logo", "logoEA.png");
				pdfReportJson.put("footerEmail", "support@enerallies.com");
				pdfReportJson.put("footerPhone", "1-888-770-3009 x300");
				
				userId = entry.getKey();
			    reportSiteList = entry.getValue();
			    HashMap<String, String> reportSiteData;
			    
			    siteListArray = new org.json.simple.JSONArray();
			    reportLevelIds = new ArrayList<String>();
			    
			    String rootPath = System.getProperty(IMAGES_LOCATION);
			    String imageFolder = rootPath + File.separator + "PDFImages";
        		
		        File file = new File(imageFolder);
		        if (!file.exists()) {
		           file.mkdir();
		        }
		        HashMap<String, String> reportSiteDataTemp;
		        int mainDeviceCount = 0;
		        for (int i = 0; i < reportSiteList.size(); i++) {
		        	reportSiteDataTemp = reportSiteList.get(i);
			        if (Integer.parseInt(reportSiteDataTemp.get("deviceCount").toString()) > 0) {
			        	mainDeviceCount++;
			        }
			    }
		        if (reportSiteList.size() > 0) {
		        	reportSiteDataTemp = reportSiteList.get(0);
			        if (Integer.parseInt(reportSiteDataTemp.get("userReportPreference")) == 1) {
			    		reportPreferencedays = 7;
			    		daysRedirect = 7;
			    	} else if (Integer.parseInt(reportSiteDataTemp.get("userReportPreference")) == 2) {
			    		reportPreferencedays = 31;
			    		daysRedirect = 28;
			    	} else if (Integer.parseInt(reportSiteDataTemp.get("userReportPreference")) == 3) {
			    		reportPreferencedays = 90;
			    	} else {
			    		reportPreferencedays = 365;
			    	}
			        cal = Calendar.getInstance();
					cal.setTime(date);
					cal.add(Calendar.DATE, -(reportPreferencedays));
					fromDate = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", cal.getTime());
					toDate = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", date);
						
					cal.add(Calendar.DATE, +1);
					
					fromDateActivity = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", cal.getTime());
					toDateActivity = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", date);
					
					fromDateMMDD = DatesUtil.getDateFormat("MM/dd", cal.getTime());
					toDateMMDD = DatesUtil.getDateFormat("MM/dd", date);

					fromDateMMDDDot = DatesUtil.getDateFormat("MM.dd", cal.getTime());
					toDateMMDDDot = DatesUtil.getDateFormat("MM.dd", date);
					
					fromDateText = DatesUtil.getDateFormat("MMM dd, yyyy", cal.getTime());
					toDateText = DatesUtil.getDateFormat("MMM dd, yyyy", date);
			    }
				
		        if (mainDeviceCount > 0) {
			    String fileName = "PDF_"+new Date().getTime()+".pdf";
				String imageFilePath = imageFolder + File.separator + fileName;
		        Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
		        OutputStream fos = new FileOutputStream(new File(imageFilePath));
		        PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);
		        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
		        doc.open();
		        ByteArrayInputStream is = null;
		        
		        StringBuilder mainHtmlPage;
				StringBuilder mainHtmlEmailPage = null;
				
			    for (int i = 0; i < reportSiteList.size(); i++) {
			    	
			    	siteJsonObject = new org.json.simple.JSONObject();
			    	reportSiteData = reportSiteList.get(i);
			    	systemUserId  = Integer.parseInt(reportSiteData.get("loggedUser"));
			    	
			    	/*
			    	 * Getting the timezone value based on Site Address.
			    	 */
			    	timeZoneJson = CommonUtility.getTimeZone(CommonUtility.isNull(reportSiteData.get("siteAddress")));
			    	timeZone = (timeZoneJson!=null && timeZoneJson.get("timeZone") != null && !timeZoneJson.get("timeZone").toString().isEmpty())
			    			? timeZoneJson.get("timeZone").toString() : "America/New_York";
			    			
			    	if (Integer.parseInt(reportSiteData.get("deviceCount").toString()) > 0) {
				    	//Getting Manual log data
						correspondencesJsonArray = activityLogDao.getManualActivityLogData(Integer.parseInt(reportSiteData.get("userReportLevel")), Integer.parseInt(reportSiteData.get("siteId")), reportPreferencedays, "");
						logger.debug("Manual log data jsonArray @@@@@@@@@@ ::: "+ correspondencesJsonArray);
						
						//Getting Degraded HVAC Units
						ReportRequest reportRequest =  new ReportRequest();
						reportRequest.setType("SITE");
						reportRequest.setReportType("1");
						reportRequest.setCustomerIds(reportSiteData.get("customerId"));
						reportRequest.setGroupIds(reportSiteData.get("groupIds"));
						reportRequest.setSiteIds(reportSiteData.get("siteId"));
						reportRequest.setInDays(reportPreferencedays);
						reportRequest.setFromDate(fromDate);
						reportRequest.setToDate(toDate);
						degradedReportJsonArray = reportDao.getReportData(reportRequest, systemUserId, timeZone);
						logger.debug("Degraded HVAC Units reportsData @@@@@@@@@@ ::: "+degradedReportJsonArray);
						
						//Getting within set points
						reportRequest.setReportType("2");
						withinReportJsonArray = reportDao.getReportData(reportRequest, systemUserId, timeZone);
						logger.debug("within set points reportsData @@@@@@@@@@ ::: "+withinReportJsonArray);
						
						//Getting HVAC Usage
						reportRequest.setReportType("3");
						hvacReportJson = reportDao.getHVACUsageReport(reportRequest, systemUserId, timeZone);
						logger.debug("HVAC Usage reportsData @@@@@@@@@@ ::: "+hvacReportJson);
						
						//Getting Manual Changes
						reportRequest.setReportType("4");
						manualChangesJsonArray = reportDao.getReportData(reportRequest, systemUserId, timeZone);
						logger.debug("Manual Changes reportsData @@@@@@@@@@ :::  "+manualChangesJsonArray);
						
						//Getting Critical issues
						reportRequest.setReportType("4");
						reportsBarJsonArray = reportDao.getCriticalIssues(reportRequest, systemUserId, timeZone);
						logger.debug("Critical issues reportsData  @@@@@@@@@@ :::  "+reportsBarJsonArray);
						
						//Critical issues alerts
						AlertRequest alertRequest = new AlertRequest();
						alertRequest.setIsEai(1);
						alertRequest.setUserId(systemUserId);
						alertRequest.setIsSuper(1);
						alertRequest.setTimeZone(timeZone);
						alertRequest.setAlertStatus("open");
						alertRequest.setFromCurrentPage("sites");
						alertRequest.setSpecificId(reportSiteData.get("siteId"));
						alertRequest.setTimePeriodInDays(reportPreferencedays);
						alertRequest.setPriority("P1");
						alertRequest.setPdfReportalertIds("");
						Response response1 = alertDao.getDashboardAlerts(alertRequest);
						logger.debug("Critical issues alerts data  @@@@@@@@@@ :::  "+(org.json.simple.JSONObject) response1.getData());
						org.json.simple.JSONObject alertsJson = (org.json.simple.JSONObject) response1.getData();
						criticalIssuesJsonArray = (org.json.simple.JSONArray) alertsJson.get("alertsInfo");
						
						//Resolved issues alerts
						alertRequest = new AlertRequest();
						alertRequest.setIsEai(1);
						alertRequest.setUserId(systemUserId);
						alertRequest.setIsSuper(1);
						alertRequest.setTimeZone(timeZone);
						alertRequest.setAlertStatus("resolve");
						alertRequest.setFromCurrentPage("sites");
						alertRequest.setSpecificId(reportSiteData.get("siteId"));
						alertRequest.setTimePeriodInDays(reportPreferencedays);
						alertRequest.setPriority("");
						alertRequest.setPdfReportalertIds("1,2,5");
						response1 = alertDao.getDashboardAlerts(alertRequest);
						logger.debug("Resolved issues alerts data  @@@@@@@@@@ :::  "+ (org.json.simple.JSONObject) response1.getData());
						org.json.simple.JSONObject resolvedJson = (org.json.simple.JSONObject) response1.getData();
						resolvedIssuesJsonArray = (org.json.simple.JSONArray) resolvedJson.get("alertsInfo");
				    	
						/*
						 * preparing site json object for pdf report
						 * */
						
						siteJsonObject.put("logo", reportSiteData.get("companyLogo"));
						siteJsonObject.put("companyName", reportSiteData.get("companyName"));
						siteJsonObject.put("siteId", reportSiteData.get("siteId"));
						siteJsonObject.put("siteName", reportSiteData.get("siteName"));
						siteJsonObject.put("siteCode", reportSiteData.get("siteCode"));
						siteJsonObject.put("customerId", reportSiteData.get("customerId"));
				    	siteJsonObject.put("reportType", reportSiteData.get("userReportPreferenceText") + " Report");
				    	siteJsonObject.put("comfortOpt", reportSiteData.get("comfortOpt"));
				    	siteJsonObject.put("deviceCount", reportSiteData.get("deviceCount"));
				    	siteJsonObject.put("toDate", DatesUtil.getDateFormat("MMM dd, yyyy", date));
				    	siteJsonObject.put("fromDate", DatesUtil.getDateFormat("MMM dd, yyyy", cal.getTime()));
				    	
				    	siteJsonObject.put("degradedReport", degradedReportJsonArray);
				    	siteJsonObject.put("withinReport", withinReportJsonArray);
				    	siteJsonObject.put("hvacReport", hvacReportJson);
				    	siteJsonObject.put("manualChanges", manualChangesJsonArray);
				    	siteJsonObject.put("reports", reportsBarJsonArray);
				    	siteJsonObject.put("criticalIssues", criticalIssuesJsonArray);
				    	siteJsonObject.put("resolvedIssues", resolvedIssuesJsonArray);
				    	siteJsonObject.put("correspondences", correspondencesJsonArray);
			    	
			    		siteNames.add(reportSiteData.get("siteName") + (reportSiteData.get("siteCode").isEmpty() ? "" : " #" + reportSiteData.get("siteCode")));
			    		reportLevelIds.add(reportSiteData.get("siteId"));
			    	
			    		siteListArray.add(siteJsonObject);
			    	
			    		reportLevel = Integer.parseInt(reportSiteData.get("userReportLevel"));
			    		reportPreference = Integer.parseInt(reportSiteData.get("userReportPreference"));
			    		userEmail = reportSiteData.get("userEmail");
			    		userFirstName = reportSiteData.get("userFname");
			    		userLastName = reportSiteData.get("userLname");
			    		reportLevelText = reportSiteData.get("userReportLevelText");
			    		reportPreferenceText = reportSiteData.get("userReportPreferenceText");
			    	}
			    }
			    pdfReportJson.put("siteList", siteListArray);
			    JSONArray jsonArray = pdfReportJson.getJSONArray("siteList");
			    int optCount = 0;
			    int siteDeviceCount = 0;
			    Set<String> customerNames = new HashSet<String>();
			    Set<String> customerLogos = new HashSet<String>();
			    for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject siteOptObject = jsonArray.getJSONObject(i);
					if (siteOptObject.getString("comfortOpt").equalsIgnoreCase("1") && Integer.parseInt(siteOptObject.getString("deviceCount")) > 0) {
						optCount++;
					}
					if (Integer.parseInt(siteOptObject.getString("deviceCount")) > 0) {
						siteDeviceCount++;
						customerNames.add(siteOptObject.getString("companyName"));
						customerLogos.add(siteOptObject.getString("logo"));
					}
			    }
			    String logo = "";
			    String customerName = "";
			    if (customerNames.size() == 1) {
			    	for (Iterator<String> it = customerNames.iterator(); it.hasNext();) {
			    		customerName = it.next();
			    	    break;
			    	}
			    }
			    if (customerNames.size() == 1) {
			    	for (Iterator<String> it = customerLogos.iterator(); it.hasNext();) {
			    		logo = it.next();
			    	    break;
			    	}
			    }
			    String chartTitleFirst = "";
			    String chartTitleSecond = "";
			    String chartTitleThird = "";
			    
			    for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject siteObject = jsonArray.getJSONObject(i);
					JSONArray reportsData = siteObject.getJSONArray("reports");
					for(int g = 0; g < reportsData.length(); g++) {
						JSONObject criticalObject = (JSONObject) reportsData.get(g);
						if (g == 0) {
							chartTitleFirst = criticalObject.getString("name");
						}
						if (g == 1) {
							chartTitleSecond = criticalObject.getString("name");
						}
						if (g == 2) {
							chartTitleThird = criticalObject.getString("name");
						}
					}
					
					if (i==0) {
						break;
					}
				}
			    
			    if (reportLevelIds.size() > 1) {
			    	
					mainHtmlPage = new StringBuilder();
			
					mainHtmlPage.append("<!DOCTYPE html><html lang='en'><head>");
					mainHtmlPage.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
					mainHtmlPage.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
							+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
					
					mainHtmlPage.append("<style>"
							+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:14; margin:0;} .bodyText{font-size:11; color:#000000;} .bodyTextHeader{font-size:11; color:#000000;}"
							+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
							+ ".loginBtn{padding-left:15; float:left; padding-right:15; padding-top:15; padding-bottom:15; background:#1a6394; color:#fff; font-size:14; text-decoration:none; border-radius:5;}"
							+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15; padding-top:15;}"
							+ ".chartTitle{font-weight:bold; font-size:10; color:black; text-align:center; text-transform:uppercase;}"
							+ ".spacer{height:10px;}.spacertr{margin-left:12px;margin-right:12px;display:table;}"
							+ ".onlyBlueBg{background:#939393; color:#000000;} .whiteBg{background:#FFFFFF;color:#000000;} p{margin:0;}"
							+ "</style></head><body>");
					
					mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0' class='mainTable'>");
						mainHtmlPage.append("<thead>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>&nbsp;&nbsp;&nbsp;<img src='"+url+"/fileUpload/loadPDFReportImages?imageName="+pdfReportJson.getString("logo")+"' width='200'></img></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("</thead>");
					mainHtmlPage.append("<tbody>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='blue-bg'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
					mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td>");
							mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								mainHtmlPage.append("<tr class='spacertr'>");
								if (StringUtils.isNotEmpty(logo)) {
									mainHtmlPage.append("<td>");
										mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+logo+"'></img>");
									mainHtmlPage.append("</td>");
								}
								if (StringUtils.isNotEmpty(logo)) {
									mainHtmlPage.append("<td>");
										mainHtmlPage.append("<h3>"+customerName + " " + reportLevelText + "s " + reportPreferenceText + " Report</h3>");
										mainHtmlPage.append("<span class='bodyText'>"+fromDateText +" to " + toDateText+"</span>");
									mainHtmlPage.append("</td>");									
								} else {
									mainHtmlPage.append("<td>");
										mainHtmlPage.append("<h3>&nbsp;&nbsp;&nbsp;&nbsp;"+customerName + " " + reportLevelText + "s " + reportPreferenceText + " Report</h3>");
										mainHtmlPage.append("<span class='bodyText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+fromDateText +" to " + toDateText+"</span>");
									mainHtmlPage.append("</td>");
								}
								if (optCount > 0) {
									mainHtmlPage.append("<td align='right'>");
										mainHtmlPage.append("<h3>Comfort Optimization On</h3>");
									mainHtmlPage.append("</td>");
									mainHtmlPage.append("<td width='30'>");
										mainHtmlPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
									mainHtmlPage.append("</td>");
								}
								mainHtmlPage.append("</tr>");
							mainHtmlPage.append("</table>");
						mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='grey-bg'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr class='spacertr'>");
						mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sites Performance Summary</h3></td>");
					mainHtmlPage.append("</tr>");				
					mainHtmlPage.append("<tr class='spacertr'>");
						mainHtmlPage.append("<td style='padding:20;'>");
							mainHtmlPage.append("<table width='100%' border='0' class='onlyBlueBg' cellpadding='5' cellspacing='2'>");
								mainHtmlPage.append("<tr>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'>&nbsp;</td>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Degraded HVAC Units</b></p></td>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Within Setpoints</b></p></td>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>HVAC Usage</b></p></td>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Manual Changes</b></p></td>");
								mainHtmlPage.append("</tr>");
								
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject siteObject = jsonArray.getJSONObject(i);
									
									if (siteObject.getInt("deviceCount") > 0) {
										JSONArray degradedReportJson = siteObject.getJSONArray("degradedReport");
										JSONArray withinReportJson = siteObject.getJSONArray("withinReport");
										JSONObject hvacReportJsons = siteObject.getJSONObject("hvacReport");
										JSONArray manualReportJson = siteObject.getJSONArray("manualChanges");
										List<String> degradedReport = new ArrayList<String>();
										List<String> withinReport = new ArrayList<String>();
										List<String> hvacReport = new ArrayList<String>();
										List<String> manualReport = new ArrayList<String>();
										
										for (int degraded = 0; degraded < degradedReportJson.length(); degraded++) {
											JSONObject degradedObj = degradedReportJson.getJSONObject(degraded);
											degradedReport.add(degradedObj.getString("name").trim());
										}
										for (int within = 0; within < withinReportJson.length(); within++) {
											JSONObject withinObj = withinReportJson.getJSONObject(within);
											withinReport.add(withinObj.getString("name").trim());
										}
										for (int manual = 0; manual < manualReportJson.length(); manual++) {
											JSONObject manualObj = manualReportJson.getJSONObject(manual);
											manualReport.add(manualObj.getString("name").trim());
										}
										for (int hvac = 0; hvac < hvacReportJsons.getJSONArray("categories").length(); hvac++) {
											JSONArray hvacReportJsonArray = hvacReportJsons.getJSONArray("data").getJSONObject(hvac).getJSONObject("drilldown").getJSONArray("categories");
											JSONArray hvacReportJsonDataArray = hvacReportJsons.getJSONArray("data").getJSONObject(hvac).getJSONObject("drilldown").getJSONArray("data");
											for (int k = 0; k < hvacReportJsonArray.length(); k++) {
												if (Integer.parseInt(hvacReportJsonDataArray.get(k).toString()) > 0) {
													hvacReport.add(hvacReportJsonArray.get(k).toString());
												}
											}
										}
										if (degradedReportJson.length() > 0 || withinReportJson.length() > 0 || manualReportJson.length() > 0 || hvacReportJsons.getJSONArray("categories").length() > 0) {
											mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td style='color:#1a6394' class='whiteBg'><p class='bodyText'><a href='"+url+"/dashboard#?pdfFlag=dashboard&pdfValue="+siteObject.getString("customerId")+"-0-"+siteObject.getString("siteId")+"-"+daysRedirect+"' style='color:#1a6394;'>"+siteObject.get("siteName") + (StringUtils.isEmpty(siteObject.get("siteCode").toString()) ? "" : " #" + siteObject.get("siteCode"))+"</a></p></td>");
												mainHtmlPage.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(degradedReport, ", ")+"</p></td>");
												mainHtmlPage.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(withinReport, ", ")+"</p></td>");
												mainHtmlPage.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(hvacReport, ", ")+"</p></td>");
												mainHtmlPage.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(manualReport, ", ")+"</p></td>");
											mainHtmlPage.append("</tr>");
										}
									}
								}	
							mainHtmlPage.append("</table>");
						mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					int allCritical = 0;
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject siteObject = jsonArray.getJSONObject(i);
						JSONArray reportsData = siteObject.getJSONArray("reports");
						for(int g = 0; g < reportsData.length(); g++) {
							JSONObject criticalObject = (JSONObject) reportsData.get(g);
							JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
							if (criticalY.getInt("y") != 0) {
								allCritical++;
							}
						}
					}
					if (allCritical > 0) {
						mainHtmlPage.append("<tr class='spacertr'>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sites with Current Critical Issues Summary</h3></td>");
						mainHtmlPage.append("</tr>");				
						mainHtmlPage.append("<tr class='spacertr'>");
							mainHtmlPage.append("<td style='padding:20'>");
								mainHtmlPage.append("<table width='100%' border='0' class='onlyBlueBg' cellpadding='5' cellspacing='2'>");
									mainHtmlPage.append("<tr>");
										mainHtmlPage.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>&nbsp;</b></p></td>");
										mainHtmlPage.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleFirst +"</b></p></td>");
										mainHtmlPage.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleSecond +"</b></p></td>");
										mainHtmlPage.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleThird +"</b></p></td>");
									mainHtmlPage.append("</tr>");
									
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject siteObject = jsonArray.getJSONObject(i);
										JSONArray reportsData = siteObject.getJSONArray("reports");
										int countCritical = 0;
										StringBuilder tempHtml = new StringBuilder();
										
										tempHtml.append("<tr>");
										tempHtml.append("<td style='color:#1a6394' class='whiteBg'><p class='bodyText'><a href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+siteObject.getString("customerId")+"-"+siteObject.getString("siteId")+"-"+reportLevelText+"' style='color:#1a6394;'>"+siteObject.get("siteName") + (StringUtils.isEmpty(siteObject.get("siteCode").toString()) ? "" : " #" + siteObject.get("siteCode"))+"</a></p></td>");
											for(int g = 0; g < reportsData.length(); g++) {
												JSONObject criticalObject = (JSONObject) reportsData.get(g);
												JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
												tempHtml.append("<td class='whiteBg'><p class='bodyText'>"+criticalY.getInt("y")+"</p></td>");
												if (criticalY.getInt("y") != 0) {
													countCritical++;
												}
											}
										tempHtml.append("</tr>");
										
										if (countCritical > 0) {
											mainHtmlPage.append(tempHtml);
										}
									}	
								mainHtmlPage.append("</table>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");
					}

					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("</tbody>");
					mainHtmlPage.append("</table>");
					mainHtmlPage.append("</body></html>");
					/*Email Design*/
		        	is = new ByteArrayInputStream(mainHtmlPage.toString().getBytes());
				    worker.parseXHtml(pdfWriter, doc, is);
				    if (reportLevelIds.size() > 1 && reportLevelIds.size() <= 10) {
			        	doc.newPage();
			        }
				}
			

			    for (int i = 0; i < jsonArray.length(); i++) {
				
			    	JSONObject objectInArray = jsonArray.getJSONObject(i);
				
					mainHtmlPage = new StringBuilder();
					mainHtmlEmailPage = new StringBuilder();
					
					if (reportLevelIds.size() <= 10) {
				
				/*PDF Design*/
				if (objectInArray.getInt("deviceCount") > 0) {

					mainHtmlPage.append("<!DOCTYPE html><html lang='en'><head>");
					mainHtmlPage.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
					mainHtmlPage.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
							+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
					
					mainHtmlPage.append("<style>"
							+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:14; margin:0;} .bodyText{font-size:11;}"
							+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
							+ ".loginBtn{padding-left:15; float:left; padding-right:15; padding-top:15; padding-bottom:15; background:#1a6394; color:#fff; font-size:14; text-decoration:none; border-radius:5;}"
							+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15; padding-top:15;}"
							+ ".chartTitle{font-weight:bold; font-size:10; color:black; text-align:center; text-transform:uppercase;}"
							+ ".spacer{height:10px;} "
							+ "</style></head><body>");
					
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
					mainHtmlEmailPage.append("<!DOCTYPE html><html lang='en'><head>");
					mainHtmlEmailPage.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
					mainHtmlEmailPage.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
							+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
					
					mainHtmlEmailPage.append("<style>"
							+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:18px; margin:0;} .bodyText{font-size:14px;}"
							+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
							+ ".loginBtn{padding:15px; float:left; background:#1a6394; color:#fff; font-size:18px; text-decoration:none; border-radius:5px;}"
							+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15px; padding-top:15px;}"
							+ ".chartTitle{font-weight:bold; font-size:10px; color:black; text-align:center; text-transform:uppercase;}"
							+ ".spacer{height:10px}.spacertr{margin-left:12px;margin-right:12px;display:table;}"
							+ ".grayborder{background:#EEEEEE;display:block;position:relative;width:100%;margin-top:20px;margin-bottom:20px;}"
							+ "</style></head><body class='grayborder'>");
					}
					
					/*PDF Design*/
					mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0' class='mainTable'>");
						mainHtmlPage.append("<thead>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>&nbsp;&nbsp;<img src='"+url+"/fileUpload/loadPDFReportImages?imageName="+pdfReportJson.getString("logo")+"' width='200'></img></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("</thead>");
					mainHtmlPage.append("<tbody>");
					
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
						mainHtmlEmailPage.append("<div style='background: #DDDDDD;padding: 30px 0;'><table width='920' align='center' border='0' cellpadding='0' bgcolor='#FFFFFF' cellspacing='0' class='mainTable'>");
						mainHtmlEmailPage.append("<thead>");
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td><img src='"+url+"/fileUpload/loadPDFReportImages?imageName=logoEA.png' width='200'></img></td>");
							mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("</thead>");
						mainHtmlEmailPage.append("<tbody>");
					}
					
					/*PDF Design*/
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='blue-bg'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td>");
							mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								mainHtmlPage.append("<tr>");
									if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
										mainHtmlPage.append("<td>");
											mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+objectInArray.getString("logo")+"'></img>");
										mainHtmlPage.append("</td>");
									}
									if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
										mainHtmlPage.append("<td>");
											mainHtmlPage.append("<h3>"+objectInArray.getString("siteName") + " " + objectInArray.getString("reportType")+"</h3>");
											mainHtmlPage.append("<span class='bodyText'>"+fromDateText + " to " + toDateText+"</span>");
										mainHtmlPage.append("</td>");
									} else {
										mainHtmlPage.append("<td>");
											mainHtmlPage.append("<h3>&nbsp;&nbsp;&nbsp;"+objectInArray.getString("siteName") + " " + objectInArray.getString("reportType")+"</h3>");
											mainHtmlPage.append("<span class='bodyText'>&nbsp;&nbsp;&nbsp;&nbsp;"+fromDateText + " to " + toDateText+"</span>");
										mainHtmlPage.append("</td>");
									}
									if (objectInArray.getString("comfortOpt").equalsIgnoreCase("1")) {
										mainHtmlPage.append("<td align='right'>");
											mainHtmlPage.append("<h3>Comfort Optimization On</h3>");
										mainHtmlPage.append("</td>");
										mainHtmlPage.append("<td width='30'>");
											mainHtmlPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
										mainHtmlPage.append("</td>");
									}
								mainHtmlPage.append("</tr>");
							mainHtmlPage.append("</table>");
						mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='grey-bg'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a href='"+url+"/dashboard#?pdfFlag=analytics&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-0-"+daysRedirect+"' style='color:#1a6394;text-decoration: underline;'>Site Performance</a></h3></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td>");
						
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='blue-bg'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td>");
							mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								mainHtmlEmailPage.append("<tr>");
									if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
										mainHtmlEmailPage.append("<td>");
											mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+objectInArray.getString("logo")+"'></img>");
										mainHtmlEmailPage.append("</td>");
									}
									if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
										mainHtmlEmailPage.append("<td>");
										mainHtmlEmailPage.append("<h3>"+objectInArray.getString("siteName") + " " + objectInArray.getString("reportType")+"</h3>");
										mainHtmlEmailPage.append("<span class='bodyText'>"+fromDateText + " to " + toDateText+"</span>");
										mainHtmlEmailPage.append("</td>");
									} else {
										mainHtmlEmailPage.append("<td>");
										mainHtmlEmailPage.append("<h3>&nbsp;&nbsp;&nbsp;"+objectInArray.getString("siteName") + " " + objectInArray.getString("reportType")+"</h3>");
										mainHtmlEmailPage.append("<span class='bodyText'>&nbsp;&nbsp;&nbsp;&nbsp;"+fromDateText + " to " + toDateText+"</span>");
										mainHtmlEmailPage.append("</td>");
									}
									if (objectInArray.getString("comfortOpt").equalsIgnoreCase("1")) {
										mainHtmlEmailPage.append("<td align='right'>");
										mainHtmlEmailPage.append("<h3>Comfort Optimization On</h3>");
										mainHtmlEmailPage.append("</td>");
										mainHtmlEmailPage.append("<td width='30'>");
										mainHtmlEmailPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
										mainHtmlEmailPage.append("</td>");
									}
									mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("</table>");
						mainHtmlEmailPage.append("</td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='grey-bg'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr class='spacertr'>");
						mainHtmlEmailPage.append("<td><h3><a href='"+url+"/dashboard#?pdfFlag=analytics&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-0-"+daysRedirect+"' style='color:#1a6394;text-decoration: underline;'>Site Performance</a></h3></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr class='spacertr'>");
						mainHtmlEmailPage.append("<td>");
					}
					JSONArray reportsData = objectInArray.getJSONArray("reports");
					JSONArray degradedReportJson = objectInArray.getJSONArray("degradedReport");
					JSONArray withinReportJson = objectInArray.getJSONArray("withinReport");
					JSONArray manualReportJson = objectInArray.getJSONArray("manualChanges");
					JSONObject hvacReportJsons = objectInArray.getJSONObject("hvacReport");
					
						/*PDF Design*/
						mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
							mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td width='25%'>");
									if (degradedReportJson.length() > 0) {
										String chart1 = getHighChartData(getPieChart(degradedReportJson, "DEGRADED HVAC UNITS"), "DEGRADED", folderName);
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName+"&imageName=").append(chart1+"'></img>");
									} else {
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportImages?imageName=percent.png'></img>");
									}
								mainHtmlPage.append("</td>");
								mainHtmlPage.append("<td width='25%'>");
									if (withinReportJson.length() > 0) {
										String chart2 = getHighChartData(getPieChart(withinReportJson, "WITHIN SETPOINTS"), "WITHIN", folderName);
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName+"&imageName=").append(chart2+"'></img>");
									} else {
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportImages?imageName=percent.png'></img>");
									}
								mainHtmlPage.append("</td>");
								mainHtmlPage.append("<td width='25%'>");
									if (hvacReportJsons.getJSONArray("categories").length() > 0) {
										String chart3 = getHighChartData(getDonutPieChart(hvacReportJsons, "HVAC USAGE"), "HVAC", folderName);
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName+"&imageName=").append(chart3+"'></img>");
									} else {
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportImages?imageName=hour.png'></img>");
									}
								mainHtmlPage.append("</td>");
								mainHtmlPage.append("<td width='25%'>");
								if (manualReportJson.length() > 0) {
									String chart4 = getHighChartData(getPieChart(manualReportJson, "MANUAL CHANGES"), "MANUAL", folderName);
									mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName+"&imageName=").append(chart4+"'></img>");
								} else {
									mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportImages?imageName=percent.png'></img>");
								}
								mainHtmlPage.append("</td>");
							mainHtmlPage.append("</tr>");
							mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td colspan='4' class='spacer'></td>");
							mainHtmlPage.append("</tr>");
							mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td valign='middle' align='center'>");
								mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DEGRADED HVAC UNITS</p>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("<td valign='middle' align='center'>");
								mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;WITHIN SETPOINTS</p>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("<td valign='middle' align='center'>");
								mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;HVAC USAGE</p>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("<td valign='middle' align='center'>");
								mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MANUAL CHANGES</p>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("</table>");
					mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='grey-bg2'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#000000;cursor: auto;text-decoration: underline;'>Current Critical Issues</a></h3></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
					mainHtmlPage.append("<td>");
					
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
					mainHtmlEmailPage.append("<table width='1024' border='0' cellpadding='0' cellspacing='0'>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td>");
								if (degradedReportJson.length() > 0) {
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getPieChart(degradedReportJson, "DEGRADED HVAC UNITS"))+"' width='256'></img>");
								} else {
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/loadPDFReportImages?imageName=percent.png' width='256'></img>");
								}
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td>");
								if (withinReportJson.length() > 0) {
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getPieChart(withinReportJson, "WITHIN SETPOINTS"))+"' width='256'></img>");
								} else {
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/loadPDFReportImages?imageName=percent.png' width='256'></img>");
								}
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td>");
								if (hvacReportJsons.getJSONArray("categories").length() > 0) {
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getDonutPieChart(hvacReportJsons, "HVAC USAGE"))+"' width='256'></img>");
								} else {
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/loadPDFReportImages?imageName=hour.png' width='256'></img>");
								}
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td>");
								if (manualReportJson.length() > 0) {
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getPieChart(manualReportJson, "MANUAL CHANGES"))+"' width='256'></img>");
								} else {
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/loadPDFReportImages?imageName=percent.png' width='256'></img>");
								}
							mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td colspan='4' class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td width='25%'>");
								mainHtmlEmailPage.append("<p class='chartTitle'>DEGRADED HVAC UNITS</p>");
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td width='25%'>");
								mainHtmlEmailPage.append("<p class='chartTitle'>WITHIN SETPOINTS</p>");
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td width='25%'>");
								mainHtmlEmailPage.append("<p class='chartTitle'>HVAC USAGE</p> ");
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td width='25%'>");
								mainHtmlEmailPage.append("<p class='chartTitle'>MANUAL CHANGES</p>");
							mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("</table>");
					mainHtmlEmailPage.append("</td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='grey-bg2'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr class='spacertr'>");
						mainHtmlEmailPage.append("<td><h3 style='color:#000000;text-decoration: underline;'>Current Critical Issues</h3><br/></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td>");
					}
					int count = 0;
					for(int g = 0; g < reportsData.length(); g++) {
						JSONObject criticalObject = (JSONObject) reportsData.get(g);
						JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
						if (criticalY.getInt("y") > 0) {
							count++;	
						}
					}
					if (count > 0) {
						String column = getHighChartData(getColumnChart(reportsData), "COLUMN", folderName);
						mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName+"&imageName=").append(column+"' width='300'></img>");
						if (reportLevelIds.size() == 1) {
							mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getColumnChart(reportsData))+"' width='400'/>");
						}
					} else {
						mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=noCriticalIssues.png' width='300'></img>");
						if (reportLevelIds.size() == 1) {
							mainHtmlEmailPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=noCriticalIssues.png' width='400'/>");
						}
					}
					
					/*PDF Design*/
					mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='grey-bg2'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					
					JSONArray criticalData = objectInArray.getJSONArray("criticalIssues");
					
					if (criticalData.length() > 0) {
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-"+reportLevelText+"'>Current Critical Issues</a></h3></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
					}
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
					mainHtmlEmailPage.append("</td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='grey-bg2'></td>");
					mainHtmlEmailPage.append("</tr>");
						if (criticalData.length() > 0) {
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td><br/><h3><a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-"+reportLevelText+"'>Current Critical Issues</a></h3><br/></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
						}
					}
					
					if (criticalData.length() > 0) {
						/*PDF Design*/
						/*mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>");
								mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								
								*/
						/*Email Design*/
						if (reportLevelIds.size() == 1) {
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td>");
								mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
						}
								boolean temp = false;
								for (int j = 0; j < criticalData.length(); j++) {
									
									JSONObject objectInCritical = criticalData.getJSONObject(j);
									String alertMsg = "";
									try{
										alertMsg = objectInCritical.getString("alertMessage");
									}catch(JSONException e){
										alertMsg = "";
									}
									/*PDF Design*/
									mainHtmlPage.append("<tr>");
									JSONArray actionItems = objectInCritical.getJSONArray("actionItems");
									
										mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
											"&nbsp;&nbsp;&nbsp;<b>"+ (j + 1) +". "+ objectInCritical.getString("alertName") + " " + objectInCritical.getString("alertProrityName") + "</b> - "+alertMsg+" <br></br>"+
											"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("customerName") + (StringUtils.isNotEmpty(objectInCritical.getString("groupName")) ? ", " : "") + objectInCritical.getString("groupName") + "<br></br>"+
											"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("siteName") + ", "+ objectInCritical.getString("deviceName") + "<br></br>");
											if (actionItems.length() > 0) {
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Action List<br></br>");
											}
										
											/*Email Design*/	
											if (reportLevelIds.size() == 1) {
												mainHtmlEmailPage.append("<tr>");
													mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
															"<b>"+ (j + 1) +". "+ objectInCritical.getString("alertName") + " " + objectInCritical.getString("alertProrityName") + "</b> - "+alertMsg+"<br/>"+
															"&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("customerName") + ", "+ objectInCritical.getString("groupName") + "<br/>"+
															"&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("siteName") + ", "+ objectInCritical.getString("deviceName") + "<br/>");
													if (actionItems.length() > 0) {			
														mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;&nbsp;- Action List<br/>");
													}
											}
											
										for (int k = 0; k < actionItems.length(); k++) {
											JSONObject actionItemsObj = actionItems.getJSONObject(k);
											if (actionItemsObj.getInt("itemStatus") == 1) {
												/*PDF Design*/
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strike style='text-decoration: line-through'>"+actionItemsObj.getString("itemName")+"</strike><br></br>");
												/*Email Design*/	
												mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strike style='text-decoration: line-through'>"+actionItemsObj.getString("itemName")+"</strike><br/>");
											} else {
												/*PDF Design*/
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+actionItemsObj.getString("itemName")+"<br></br>");
												/*Email Design*/	
												mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+actionItemsObj.getString("itemName")+"<br/>");
											}
											
									    }
									/*PDF Design*/
										mainHtmlPage.append("</p></td>");
									mainHtmlPage.append("</tr>");
									
									/*Email Design*/
									if (reportLevelIds.size() == 1) {
										mainHtmlEmailPage.append("</p></td>");
										mainHtmlEmailPage.append("</tr>");
										mainHtmlEmailPage.append("<tr>");
											mainHtmlEmailPage.append("<td class='spacer'></td>");
										mainHtmlEmailPage.append("</tr>");
									}
									if (j == 4) {
										temp = true;
										break;
									}
								}
								if (temp) {
									/*PDF Design*/			
									mainHtmlPage.append("<tr>");
										mainHtmlPage.append("<td><p class='bodyText text-indent'>" +
															"&nbsp;&nbsp;&nbsp;Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-"+reportLevelText+"'>here</a> for more issues");
										mainHtmlPage.append("</p></td>");
									mainHtmlPage.append("</tr>");
									if (reportLevelIds.size() == 1) {
									/*Email Design*/			
									mainHtmlEmailPage.append("<tr>");
										mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>" +
														"Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-"+reportLevelText+"'>here</a> for more issues");
										mainHtmlEmailPage.append("</p></td>");
									mainHtmlEmailPage.append("</tr>");
									}
								}
								/*PDF Design*/			
								/*mainHtmlPage.append("</table>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");*/
						
						/*Email Design*/	
						if (reportLevelIds.size() == 1) {
								mainHtmlEmailPage.append("</table>");
							mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						}
					}
					/*PDF Design*/
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					JSONArray resolvedData = objectInArray.getJSONArray("resolvedIssues");
					if (resolvedData.length() > 0) {
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-"+reportLevelText+"'>Resolved Issues</a></h3></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
					}
					/*Email Design*/
					if (reportLevelIds.size() == 1 && resolvedData.length() > 0) {
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><h3><a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-"+reportLevelText+"'>Resolved Issues</a></h3><br/></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
					}
				
					if (resolvedData.length() > 0) {
						/*PDF Design*/
						/*mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>");
								mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");*/
						/*Email Design*/	
					if (reportLevelIds.size() == 1) {
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td>");
								mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
					}				
									boolean rTemp = false;
									for (int m = 0; m < resolvedData.length(); m++) {
										JSONObject objectResolved = resolvedData.getJSONObject(m);
										String alertMsg = "";
										try{
											alertMsg = objectResolved.getString("alertMessage");
										}catch(JSONException e){
											alertMsg = "";
										}
										/*PDF Design*/
										mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
													"&nbsp;&nbsp;&nbsp;<b>"+ (m + 1) +". "+ objectResolved.getString("alertName") + " " + objectResolved.getString("alertProrityName") + "</b> - " + alertMsg);
											mainHtmlPage.append("</p></td>");
										mainHtmlPage.append("</tr>");
										/*Email Design*/
										if (reportLevelIds.size() == 1) {
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
														"<b>"+ (m + 1) +". "+ objectResolved.getString("alertName") + " " + objectResolved.getString("alertProrityName") + "</b> - " + alertMsg);
												mainHtmlEmailPage.append("</p></td>");
											mainHtmlEmailPage.append("</tr>");
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td class='spacer'></td>");
											mainHtmlEmailPage.append("</tr>");
										}
										if (m == 4) {
											rTemp = true;
											break;
										}
								    }
									if (rTemp) {
										/*PDF Design*/
										mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
														"&nbsp;&nbsp;&nbsp;Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-"+reportLevelText+"'>here</a> for more issues");
											mainHtmlPage.append("</p></td>");
										mainHtmlPage.append("</tr>");
										/*Email Design*/
										if (reportLevelIds.size() == 1) {
										mainHtmlEmailPage.append("<tr>");
											mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
														"Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-0-"+objectInArray.getString("siteId")+"-"+reportLevelText+"'>here</a> for more issues");
											mainHtmlEmailPage.append("</p></td>");
										mainHtmlEmailPage.append("</tr>");
										}
									}
								/*PDF Design*/
								/*mainHtmlPage.append("</table>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");*/
						/*Email Design*/
						if (reportLevelIds.size() == 1) {
							mainHtmlEmailPage.append("</table>");
						mainHtmlEmailPage.append("</td>");
					mainHtmlEmailPage.append("</tr>");
						}
					}
					JSONArray correspondences = objectInArray.getJSONArray("correspondences");
					
					/*PDF Design*/
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					if (correspondences.length() > 0) {
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#000000;cursor: auto;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=activity'>Correspondences</a></h3></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
					}
					
					/*Email Design*/
					if (reportLevelIds.size() == 1 && correspondences.length() > 0) {
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><h3><a style='color:#000000;cursor: auto;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=activity'>Correspondences</a></h3><br/></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
					}
				
					if (correspondences.length() > 0) {
						/*PDF Design*/
						/*mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>");
								mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");*/
							/*Email Design*/
							if (reportLevelIds.size() == 1) {
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td>");
									mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
							}
								boolean cTemp = false;
									for (int c = 0; c < correspondences.length(); c++) {	
										JSONObject correspondencesObject = correspondences.getJSONObject(c);
										/*PDF Design*/
										mainHtmlPage.append("<tr>");
											String temp = "";
											if (correspondencesObject.getString("alAction").equalsIgnoreCase("called")) {
												temp = "Telephone Call";
											} else if (correspondencesObject.getString("alAction").equalsIgnoreCase("emailed")) {
												temp = "Email";
											} else if (correspondencesObject.getString("alAction").equalsIgnoreCase("texted")) {
												temp = "Text";
											}
											mainHtmlPage.append("<td><p class='bodyText text-indent'>" 
												+ "&nbsp;&nbsp;&nbsp;<b>"+ (c + 1) +". " + correspondencesObject.getString("strTimestamp") + " * " + temp);
												if (StringUtils.isNotEmpty(correspondencesObject.getString("strContact"))) {
													mainHtmlPage.append(" * " + correspondencesObject.getString("strContact"));
												}
												if (StringUtils.isNotEmpty(correspondencesObject.getString("strContactNumber"))) {
													mainHtmlPage.append(" * " + correspondencesObject.getString("strContactNumber"));
												}
												if (StringUtils.isNotEmpty(correspondencesObject.getString("alSpecificName"))) {
													mainHtmlPage.append(" * " + correspondencesObject.getString("alSpecificName"));
												}
												mainHtmlPage.append("</b><br></br>");
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<b>&nbsp;&nbsp;&nbsp;&nbsp;Subject: </b>"+ correspondencesObject.getString("strSubject"));
												if (StringUtils.isNotEmpty(correspondencesObject.getString("strDescription"))) {
													mainHtmlPage.append("<br></br>&nbsp;&nbsp;&nbsp;<b>&nbsp;&nbsp;&nbsp;&nbsp;Description: </b>"+ correspondencesObject.getString("strDescription"));
												}
											mainHtmlPage.append("</p><br></br></td>");
										mainHtmlPage.append("</tr>");	
										
										/*Email Design*/
										if (reportLevelIds.size() == 1) {
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>" 
													+ "<b>"+ (c + 1) +". " + correspondencesObject.getString("strTimestamp") + " * " + temp);
													if (StringUtils.isNotEmpty(correspondencesObject.getString("strContact"))) {
														mainHtmlEmailPage.append(" * " + correspondencesObject.getString("strContact"));
													}
													if (StringUtils.isNotEmpty(correspondencesObject.getString("strContactNumber"))) {
														mainHtmlEmailPage.append(" * " + correspondencesObject.getString("strContactNumber"));
													}													
													if (StringUtils.isNotEmpty(correspondencesObject.getString("alSpecificName"))) {
														mainHtmlEmailPage.append(" * " + correspondencesObject.getString("alSpecificName"));
													}
													mainHtmlEmailPage.append("</b><br/>");
													mainHtmlEmailPage.append("<b>&nbsp;&nbsp;&nbsp;&nbsp;Subject: </b>"+ correspondencesObject.getString("strSubject"));
													if (StringUtils.isNotEmpty(correspondencesObject.getString("strDescription"))) {
														mainHtmlEmailPage.append("<br/><b>&nbsp;&nbsp;&nbsp;&nbsp;Description: </b>"+ correspondencesObject.getString("strDescription"));
													}
													mainHtmlEmailPage.append("</p><br/></td>");
												mainHtmlEmailPage.append("</tr>");
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td class='spacer'></td>");
											mainHtmlEmailPage.append("</tr>");
										}
										if (c == 4) {
											cTemp = true;
											break;
										}
									}
									if (cTemp) {
										/*PDF Design*/
										mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
														"&nbsp;&nbsp;&nbsp;Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=activity'>here</a> for more correspondences");
											mainHtmlPage.append("</p></td>");
										mainHtmlPage.append("</tr>");
										/*Email Design*/
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
															"Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=activity'>here</a> for more correspondences");
												mainHtmlEmailPage.append("</p></td>");
											mainHtmlEmailPage.append("</tr>");
									}
								/*PDF Design*//*
								mainHtmlPage.append("</table>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");*/
						
						/*Email Design*/
						if (reportLevelIds.size() == 1) {
							mainHtmlEmailPage.append("</table>");
						mainHtmlEmailPage.append("</td>");
					mainHtmlEmailPage.append("</tr>");
						}
					}
					/*PDF Design*/
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("</tbody>");
					mainHtmlPage.append("</table>");
					mainHtmlPage.append("</body></html>");
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
				        mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><a href='"+ url +"' style='text-decoration:none'><img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=ealogin.png' width='217'></img></a></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("</tbody>");
						mainHtmlEmailPage.append("<tfoot>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><p class='bodyText'>"+
									"Please contact EnerAllies support at "+pdfReportJson.getString("footerPhone")+" or email <a href='#' style='color:#1a6394;text-decoration:none'>"+pdfReportJson.getString("footerEmail")+"</a> if you have any questions.<br/><br/>"+
									"If you wish to stop receiving notifications, please click the link below to unsubscribe:<br/>"+
									"<a style='color:#1a6394;' href='"+url+"/dashboard#?pdfFlag=user&pdfValue="+userId+"'>Alert Notification Settings</a>");
							mainHtmlEmailPage.append("</p></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td align='center'><p class='bodyText'>Copyright &copy; 2017 EnerAllies. All rights reserved.</p></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("</tfoot>");
						mainHtmlEmailPage.append("</table></div>");
			        	mainHtmlEmailPage.append("</body></html>");
						}
			        	is = new ByteArrayInputStream(mainHtmlPage.toString().getBytes());
					    worker.parseXHtml(pdfWriter, doc, is);
					    if ((reportLevelIds.size()-(i+1)) > 0) {
				        	doc.newPage();
				        }
					}
				}
			}
			if (siteDeviceCount > 0) {
				doc.close();
			}
		    fos.close();
		    if (reportLevelIds.size() > 0) {
		        
		    	AddManualLogRequest addManualLogRequest = new AddManualLogRequest();
		        addManualLogRequest.setIsPdfReport(1);
		        addManualLogRequest.setReportPreference(reportPreference);
		        addManualLogRequest.setReportLevel(reportLevel);
		        addManualLogRequest.setReportLevelIds(StringUtils.join(reportLevelIds, ", "));
		        addManualLogRequest.setDescription(customerName + " " +  reportPreferenceText + " " +
		        		reportLevelText + " Report " + "("+fromDateMMDD + "-" + toDateMMDD + ") sent to " +
		        		userFirstName + " " + userLastName + " at " + userEmail + ".");
		        addManualLogRequest.setSpecificId(reportLevelIds.isEmpty() ? 0 : Integer.parseInt(reportLevelIds.get(0)));
		        
		        addManualLogRequest.setReportComponent(reportPreferenceText + " " + reportLevelText + " Report");
		        activityLogDao.createManualActivityLog(addManualLogRequest, userId);
		        
		        AddPDFReportDataRequest addPDFReportDataRequest = new AddPDFReportDataRequest();
		        addPDFReportDataRequest.setToDate(toDateActivity);
		        addPDFReportDataRequest.setFromDate(fromDateActivity);
		        addPDFReportDataRequest.setReportLevelText(reportLevelText + "s " + reportPreferenceText + " Report");
		        addPDFReportDataRequest.setUserId(userId);
		        addPDFReportDataRequest.setReportStatus(1);
		        addPDFReportDataRequest.setSpecificIds(reportLevelIds);
		        addPDFReportDataRequest.setActualFilePath(fileName);
		        addPDFReportDataRequest.setReportLevel(reportLevel);
		        addPDFReportDataRequest.setReportPreference(reportPreference);
		        addPDFReportDataRequest.setSiteIds(StringUtils.join(reportLevelIds, ","));
		        pdfReportDAO.addPDFReportData(addPDFReportDataRequest, userId);
		        
		        /* Email Sending*/
		        MailBroadCast mailBroadCast = new MailBroadCast();
				mailBroadCast.setFromEmail(ConfigurationUtils.getConfig("from.mail.pdf"));
				mailBroadCast.setToEmail(userEmail);
				StringBuilder emailTemplate = new StringBuilder();
				
				String pdfFileName = "";
				if (reportLevelIds.size() > 1) {
					mailBroadCast.setSubject(customerName + " " + reportPreferenceText + " " + reportLevelText + "s" + " Report : "+fromDateMMDD + " - " + toDateMMDD);
					pdfFileName = customerName + " " + reportPreferenceText + " " + reportLevelText + "s" + " Report - "+ fromDateMMDDDot +" - "+ toDateMMDDDot.toString().trim() + ".pdf";
				} else {
					mailBroadCast.setSubject(siteNames.isEmpty() ? "" : siteNames.get(0) + " " + reportPreferenceText + " Report : " + fromDateMMDD + " - " + toDateMMDD);
					pdfFileName = siteNames.isEmpty() ? "" : siteNames.get(0).split("#")[0].toString().trim() + " " + reportPreferenceText + " " + reportLevelText + " Report - "+ fromDateMMDDDot +" - "+ toDateMMDDDot.toString().trim() + ".pdf";
				}
				if (reportLevelIds.size() > 1) {
				
					emailTemplate.append("<!DOCTYPE html><html lang='en'><head>");
					emailTemplate.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
					emailTemplate.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
							+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
					
					emailTemplate.append("<style>"
							+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:18px; margin:0;} .bodyText{font-size:14px;color:#000000;}"
							+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
							+ ".loginBtn{padding:15px; float:left; background:#1a6394; color:#fff; font-size:18px; text-decoration:none; border-radius:5px;}"
							+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15px; padding-top:15px;}"
							+ ".chartTitle{font-weight:bold; font-size:10px; color:black; text-align:center; text-transform:uppercase;}"
							+ ".spacer{height:10px}.spacertr{margin-left:12px;margin-right:12px;display:table;}"
							+ ".bodyTextHeader{font-size:18px; color:#000000;}"
							+ ".onlyBlueBg{background:#939393; color:#000000;} .whiteBg{background:#ffffff;color:#000000;} p{margin:0;}"
							+ ".grayborder{background:#EEEEEE;display:block;position:relative;width:100%;margin-top:20px;margin-bottom:20px;}"
							+ "</style></head><body class='grayborder'>");
					
					emailTemplate.append("<div style='background: #DDDDDD;padding: 30px 0;'><table width='920' align='center' border='0' cellpadding='0' bgcolor='#FFFFFF' cellspacing='0' class='mainTable'>");
					emailTemplate.append("<thead>");
						emailTemplate.append("<tr class='spacertr'>");
							emailTemplate.append("<td><img src='"+url+"/fileUpload/loadPDFReportImages?imageName=logoEA.png' width='200'></img></td>");
						emailTemplate.append("</tr>");
					emailTemplate.append("</thead>");
					emailTemplate.append("<tbody>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='blue-bg'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td>");
							emailTemplate.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								emailTemplate.append("<tr class='spacertr'>");
								if (StringUtils.isNotEmpty(logo)) {
									emailTemplate.append("<td>");
										emailTemplate.append("<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+logo+"'></img>");
									emailTemplate.append("</td>");
								}
								emailTemplate.append("<td>");
									emailTemplate.append("<h3>"+customerName + " " + reportLevelText + "s " + reportPreferenceText + " Report</h3>");
									emailTemplate.append("<span class='bodyText'>"+fromDateText +" to " + toDateText+"</span>");
								emailTemplate.append("</td>");
								if (optCount > 0) {
									emailTemplate.append("<td align='right'>");
										emailTemplate.append("<h3>Comfort Optimization On</h3>");
									emailTemplate.append("</td>");
									emailTemplate.append("<td width='30'>");
										emailTemplate.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
									emailTemplate.append("</td>");
								}
								emailTemplate.append("</tr>");
							emailTemplate.append("</table>");
						emailTemplate.append("</td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='grey-bg'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr class='spacertr'>");
						emailTemplate.append("<td><h3>Sites Performance Summary</h3></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr class='spacertr'>");
						emailTemplate.append("<td>");
							emailTemplate.append("<table width='100%' border='0' class='onlyBlueBg' cellpadding='5' cellspacing='2'>");
								emailTemplate.append("<tr>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'>&nbsp;</td>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Degraded HVAC Units</b></p></td>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Within Setpoints</b></p></td>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>HVAC Usage</b></p></td>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Manual Changes</b></p></td>");
								emailTemplate.append("</tr>");
								
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject siteObject = jsonArray.getJSONObject(i);
									
									if (siteObject.getInt("deviceCount") > 0) {
										JSONArray degradedReportJson = siteObject.getJSONArray("degradedReport");
										JSONArray withinReportJson = siteObject.getJSONArray("withinReport");
										JSONObject hvacReportJsons = siteObject.getJSONObject("hvacReport");
										JSONArray manualReportJson = siteObject.getJSONArray("manualChanges");
										List<String> degradedReport = new ArrayList<String>();
										List<String> withinReport = new ArrayList<String>();
										List<String> hvacReport = new ArrayList<String>();
										List<String> manualReport = new ArrayList<String>();
										
										for (int degraded = 0; degraded < degradedReportJson.length(); degraded++) {
											JSONObject degradedObj = degradedReportJson.getJSONObject(degraded);
											degradedReport.add(degradedObj.getString("name").trim());
										}
										for (int within = 0; within < withinReportJson.length(); within++) {
											JSONObject withinObj = withinReportJson.getJSONObject(within);
											withinReport.add(withinObj.getString("name").trim());
										}
										for (int manual = 0; manual < manualReportJson.length(); manual++) {
											JSONObject manualObj = manualReportJson.getJSONObject(manual);
											manualReport.add(manualObj.getString("name").trim());
										}
										for (int hvac = 0; hvac < hvacReportJsons.getJSONArray("categories").length(); hvac++) {
											JSONArray hvacReportJsonArray = hvacReportJsons.getJSONArray("data").getJSONObject(hvac).getJSONObject("drilldown").getJSONArray("categories");
											JSONArray hvacReportJsonDataArray = hvacReportJsons.getJSONArray("data").getJSONObject(hvac).getJSONObject("drilldown").getJSONArray("data");
											for (int k = 0; k < hvacReportJsonArray.length(); k++) {
												if (Integer.parseInt(hvacReportJsonDataArray.get(k).toString()) > 0) {
													hvacReport.add(hvacReportJsonArray.get(k).toString());
												}
											}
										}
										if (degradedReportJson.length() > 0 || withinReportJson.length() > 0 || manualReportJson.length() > 0 || hvacReportJsons.getJSONArray("categories").length() > 0) {
											emailTemplate.append("<tr>");
											emailTemplate.append("<td class='whiteBg' style='color:#1a6394'><p class='bodyText'><a href='"+url+"/dashboard#?pdfFlag=dashboard&pdfValue="+siteObject.getString("customerId")+"-0-"+siteObject.getString("siteId")+"-"+daysRedirect+"' style='color:#1a6394;'>"+siteObject.get("siteName") + (StringUtils.isEmpty(siteObject.get("siteCode").toString()) ? "" : " #" + siteObject.get("siteCode"))+"</a></p></td>");
												emailTemplate.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(degradedReport, ", ")+"</p></td>");
												emailTemplate.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(withinReport, ", ")+"</p></td>");
												emailTemplate.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(hvacReport, ", ")+"</p></td>");
												emailTemplate.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(manualReport, ", ")+"</p></td>");
											emailTemplate.append("</tr>");
										}
									}
								}	
							emailTemplate.append("</table>");
						emailTemplate.append("</td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					int allCritical = 0;
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject siteObject = jsonArray.getJSONObject(i);
						JSONArray reportsData = siteObject.getJSONArray("reports");
						for(int g = 0; g < reportsData.length(); g++) {
							JSONObject criticalObject = (JSONObject) reportsData.get(g);
							JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
							if (criticalY.getInt("y") != 0) {
								allCritical++;
							}
						}
					}
					if (allCritical > 0) {
						emailTemplate.append("<tr>");
							emailTemplate.append("<td class='spacer'></td>");
						emailTemplate.append("</tr>");
						emailTemplate.append("<tr class='spacertr'>");
							emailTemplate.append("<td><h3>Sites with Current Critical Issues Summary</h3></td>");
						emailTemplate.append("</tr>");
						emailTemplate.append("<tr>");
							emailTemplate.append("<td class='spacer'></td>");
						emailTemplate.append("</tr>");
						emailTemplate.append("<tr class='spacertr'>");
							emailTemplate.append("<td>");
								emailTemplate.append("<table width='100%' border='0' class='onlyBlueBg' cellpadding='5' cellspacing='2'>");
									emailTemplate.append("<tr>");
										emailTemplate.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>&nbsp;</b></p></td>");
										emailTemplate.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleFirst +"</b></p></td>");
										emailTemplate.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleSecond +"</b></p></td>");
										emailTemplate.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleThird +"</b></p></td>");
									emailTemplate.append("</tr>");
									
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject siteObject = jsonArray.getJSONObject(i);
										JSONArray reportsData = siteObject.getJSONArray("reports");
										int countCritical = 0;
										StringBuilder tempHtml = new StringBuilder();
										tempHtml.append("<tr>");
											tempHtml.append("<td class='whiteBg' style='color:#1a6394'><p class='bodyText'><a href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+siteObject.getString("customerId")+"-0-"+siteObject.getString("siteId")+"-"+reportLevelText+"' style='color:#1a6394;'>"+siteObject.get("siteName") + (StringUtils.isEmpty(siteObject.get("siteCode").toString()) ? "" : " #" + siteObject.get("siteCode"))+"</a></p></td>");
											for(int g = 0; g < reportsData.length(); g++) {
												JSONObject criticalObject = (JSONObject) reportsData.get(g);
												JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
												tempHtml.append("<td class='whiteBg'><p class='bodyText'>"+criticalY.getInt("y")+"</p></td>");
												if (criticalY.getInt("y") != 0) {
													countCritical++;
												}
											}
										tempHtml.append("</tr>");
										
										if (countCritical > 0) {
											emailTemplate.append(tempHtml);
										}
									}	
								emailTemplate.append("</table>");
							emailTemplate.append("</td>");
						emailTemplate.append("</tr>");
					}
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr class='spacertr'>");
						emailTemplate.append("<td><a href='"+ url +"' style='text-decoration:none'><img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=ealogin.png' width='217'></img></a></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("</tbody>");
					emailTemplate.append("<tfoot>");
						emailTemplate.append("<tr class='spacertr'>");
							emailTemplate.append("<td><p class='bodyText'>"+
									"Please contact EnerAllies support at 1-888-770-3009 x300 or email <a href='mailto:support@enerallies.com' style='color:#1a6394;text-decoration:none'>support@enerallies.com</a> if you have any questions.<br/><br/>"+
									"If you wish to stop receiving notifications, please click the link below to unsubscribe:<br/>"+
									"<a style='color:#1a6394;' href='"+url+"/dashboard#?pdfFlag=user&pdfValue="+userId+"'>Alert Notification Settings</a>");
							emailTemplate.append("</p></td>");
							emailTemplate.append("</tr>");
							emailTemplate.append("<tr>");
								emailTemplate.append("<td class='spacer'></td>");
							emailTemplate.append("</tr>");
							emailTemplate.append("<tr>");
								emailTemplate.append("<td align='center'><p class='bodyText'>Copyright &copy; 2017 EnerAllies. All rights reserved.</p></td>");
							emailTemplate.append("</tr>");
							emailTemplate.append("</tfoot>");
						emailTemplate.append("</table></div>");
			        	emailTemplate.append("</body></html>");
					} else {
						emailTemplate.append(mainHtmlEmailPage.toString());
					}
					mailBroadCast.setMailText(emailTemplate.toString());
					File pdfFile = new File(rootPath + File.separator + "PDFImages" + File.separator + fileName);
					
					// Instantiating mail publisher
					MailPublisher publisher = new MailPublisher();
					
					// Broad casting mail
					boolean mailFlag = publisher.publishEmailWithAttachment(mailBroadCast, pdfFile, pdfFileName, true);
					if (mailFlag) {
				        String fullPathDelete = rootPath + File.separator + folderName;
				        File deleteFolder = new File(fullPathDelete);
				        if (deleteFolder.exists()) {
				        	String[]entries = deleteFolder.list();
				        	for(String s: entries){
				        	    File currentFile = new File(deleteFolder.getPath(),s);
				        	    currentFile.delete();
				        	}
				        	deleteFolder.delete();
				        }
					}
				}
			}
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.PDF_REPORT_SUCCESS);
			response.setData(reportResponse);
			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_FAILED, logger, e);
		}
		
		logger.info("[END] [weeklyPDFReportGenerator] [PDF REPORT SERVICE LAYER]");
		return response;
		
	}

	@Override
	public Response getReportList(int userId) throws VEMAppException {
		logger.info("[BEGIN] [getReportList] [PdfReportServiceImpl SERVICE LAYER]");
		
		Response response = new Response();
		
		//Used to store list of Activity logs.
		org.json.simple.JSONObject reportData;
		
		try {
			
			//Calling the DAO layer getReportList() method.
			reportData = pdfReportDAO.getReportList(userId,0);
			
			/* if activityLogData is not null means the getActivityLogData request is
			 *  success
			 *  else fail.
			 */
			if(reportData != null){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.PDF_REPORT_GET_LIST_SUCCESS);
				response.setData(reportData);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.PDF_REPORT_GET_LIST_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.PDF_REPORT_GET_LIST_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_GET_LIST_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [getReportList] [PdfReportServiceImpl SERVICE LAYER]");
		
		return response;
	}

	@Override
	public Response addPDFReportData(AddPDFReportDataRequest addPDFReportDataRequest, int userId)
			throws VEMAppException {

		
		logger.info("[BEGIN] [addPDFReportData] [PdfReportService SERVICE LAYER]");
		
		Response response = new Response();
		
		JSONObject reportObject = new JSONObject();
		
		try {
			
			/* Instantiating the bean validator and validating the request bean.*/
			ValidatorBean validatorBean = ConfigurationUtils.validateBeans(addPDFReportDataRequest);
			if(validatorBean.isNotValid()){
				//Catches when bean validations failed.
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(validatorBean.getMessage());
			}else{
				
				//Catches when all the server or bean level validations are true.
				int status = pdfReportDAO.addPDFReportData(addPDFReportDataRequest, userId);
				
				/* if status is 1 or greater means the add site request is
				 *  success
				 *  else fail.
				 */
				if(status >= 1){
					reportObject.put("reportId", status);
					response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
					response.setCode(ErrorCodes.PDF_REPORT_INSERT_SUCCESS);
					response.setData(reportObject);
				}else{
					response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
					response.setCode(ErrorCodes.PDF_REPORT_INSERT_FAILED);
					response.setData(CommonConstants.ERROR_OCCURRED+":PDF Report has not created at DB Side.");
				}
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.PDF_REPORT_INSERT_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_INSERT_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [addPDFReportData] [PdfReportService SERVICE LAYER]");
		
		return response;
	}
	@Override
	public Response resendPDFReport(int reportId, String email, GetUserResponse userDetails) throws VEMAppException, MalformedURLException {
		logger.info("[BEGIN] [deletePDFReport] [PdfReportService SERVICE LAYER]");
		
		Response response = new Response();
		int resendFlag = 0;
		//Used to store list of Activity logs.
		org.json.simple.JSONObject reportMainData;
		org.json.simple.JSONObject reportData;
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
		
		try {
			
			//Calling the DAO layer getReportList() method.
			reportMainData = pdfReportDAO.getReportList(0, reportId);
			org.json.simple.JSONArray reportDataArray = (org.json.simple.JSONArray) reportMainData.get("reportList");
			reportData = (org.json.simple.JSONObject) reportDataArray.get(0);
			if (reportData != null) {
				
		        MailBroadCast mailBroadCast = new MailBroadCast();
				mailBroadCast.setFromEmail(ConfigurationUtils.getConfig("from.mail.pdf"));
				mailBroadCast.setToEmail(email);
				StringBuilder emailTemplate = new StringBuilder();
				
		    	//List<Object> tempArray = Arrays.asList(reportData.get("specificNamesMail").toString().split("~#~"));
		    	String fileName = reportData.get("reportPDFName").toString()+ ".pdf";
		    	mailBroadCast.setSubject(reportData.get("reportName").toString());		
				
		    	emailTemplate.append("<!DOCTYPE html><html lang='en'><head>");
				emailTemplate.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
				emailTemplate.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
						+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
				
				emailTemplate.append("<style>"
						+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:18px; margin:0;} .bodyText{font-size:14px;}"
						+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
						+ ".loginBtn{padding:15px; float:left; background:#1a6394; color:#fff; font-size:18px; text-decoration:none; border-radius:5px;}"
						+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15px; padding-top:15px;}"
						+ ".chartTitle{font-weight:bold; font-size:10px; color:black; text-align:center; text-transform:uppercase;}"
						+ ".spacer{height:10px;}.spacertr{margin-left:12px;margin-right:12px;}"
						+ "</style></head><body>");
				
				emailTemplate.append("<table width='920' border='0' cellpadding='0' cellspacing='0' class='mainTable'>");
				emailTemplate.append("<thead>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacertr'><img src='"+url+"/fileUpload/loadPDFReportImages?imageName=logoEA.png' width='200'></img></td>");
					emailTemplate.append("</tr>");
				emailTemplate.append("</thead>");
				emailTemplate.append("<tbody>");
				emailTemplate.append("<tr>");
					emailTemplate.append("<td class='blue-bg'></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr>");
					emailTemplate.append("<td class='spacer'></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr class='spacertr'>");
					emailTemplate.append("<td><p class='bodyText' style='margin:0;padding:0;'>Hello,</td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr>");
					emailTemplate.append("<td class='spacer'></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr class='spacertr'>");
					emailTemplate.append("<td><p class='bodyText' style='margin:0;padding:0;'>"
							+ userDetails.getFirstName() + " " + userDetails.getLastName() + " "
							+ "has sent you a copy of \"" + reportData.get("reportName").toString().replaceAll(":", "for").trim() + "\"."
							+ "</td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr>");
					emailTemplate.append("<td class='spacer'></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("</tbody>");
				emailTemplate.append("<tfoot>");
				emailTemplate.append("<tr class='spacertr'>");
					emailTemplate.append("<td><p class='bodyText'>Please contact EnerAllies support at 1-888-770-3009 x300 or email <a href='mailto:support@enerallies.com' style='color:#1a6394;text-decoration:none'>support@enerallies.com</a> if you have any questions.</p></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr>");
					emailTemplate.append("<td class='spacer'></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr class='spacertr'>");
					emailTemplate.append("<td><p class='bodyText' style='margin:0;padding:0;'>Thanks,</p></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr class='spacertr'>");
					emailTemplate.append("<td><p class='bodyText' style='margin:0;padding:0;'>EnerAllies Support</p></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr class='spacertr'>");
					emailTemplate.append("<td><p class='bodyText' style='margin:0;padding:0;'>1-888-770-3009 x300</p></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr class='spacertr'>");
					emailTemplate.append("<td><p class='bodyText' style='margin:0;padding:0;'><a href='mailto:support@enerallies.com' style='color:#1a6394;text-decoration:none'>support@enerallies.com</a></p></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr>");
					emailTemplate.append("<td class='spacer'></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("<tr>");
					emailTemplate.append("<td align='center'><p class='bodyText'>Copyright &copy; 2017 EnerAllies. All rights reserved.</p></td>");
				emailTemplate.append("</tr>");
				emailTemplate.append("</tfoot>");
				emailTemplate.append("</table>");
	        	emailTemplate.append("</body></html>");
				
				mailBroadCast.setMailText(emailTemplate.toString());
				
				String rootPath = System.getProperty(IMAGES_LOCATION);
				File pdfFile = new File(rootPath + File.separator + "PDFImages" + File.separator +reportData.get("fileName").toString());
				
				// Instantiating mail publisher
				MailPublisher publisher = new MailPublisher();
				
				// Broad casting mail
				boolean mailFlag = publisher.publishEmailWithAttachment(mailBroadCast, pdfFile, fileName, true);
				if (mailFlag) {
					resendFlag = 1;
				}
			}
			/* if activityLogData is not null means the getActivityLogData request is
			 *  success
			 *  else fail.
			 */
			if(reportData != null && resendFlag > 0){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.PDF_REPORT_RESEND_SUCCESS);
				response.setData(resendFlag);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.PDF_REPORT_RESEND_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":No Records Found or Email Not Sent");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.PDF_REPORT_RESEND_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_RESEND_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [getReportList] [PdfReportServiceImpl SERVICE LAYER]");
		
		return response;
		
	}
	@Override
	public Response deletePDFReport(String reportId) throws VEMAppException, MalformedURLException {
		
		logger.info("[BEGIN] [deletePDFReport] [PdfReportService SERVICE LAYER]");
		
		Response response = new Response();
		
		int updatedFlag = 0;
		
		try {
			// Calling the delete dao method.
			updatedFlag = pdfReportDAO.deletePDFReport(reportId);
			
			/* if status is 1 or greater means the Delete PDF Report request is
			 *  success
			 *  else fail.
			 */
			if(updatedFlag >= 1){
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.PDF_REPORT_DELETE_SUCCESS);
				response.setData(updatedFlag);
			}else{
				response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
				response.setCode(ErrorCodes.PDF_REPORT_DELETE_FAILED);
				response.setData(CommonConstants.ERROR_OCCURRED+":PDF Report not deleted at DB Side.");
			}
			
		}catch (Exception e) {
			//Creating and throwing the customized exception.
			response.setStatus(CommonConstants.AppStatus.FAILURE.toString());
			response.setCode(ErrorCodes.PDF_REPORT_DELETE_FAILED);
			response.setData(CommonConstants.ERROR_OCCURRED);
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_DELETE_FAILED, logger, e);
		}
		
		logger.info("[BEGIN] [deletePDFReport] [PdfReportService SERVICE LAYER]");
		
		return response;
		
	}
	 /*public String getHighChartData(String data, String fileName, String folderName) {
		  String dateFileName = "";
		  try {
		   HttpPost post = new HttpPost("http://export.highcharts.com");
		   HttpClient client = new DefaultHttpClient();
		          
		   String dataString = "{\"type\":\"image/png\",\"options\":" + data+",\"constr\":\"Chart\"}";
		   JSONObject jsonObj1 = new JSONObject(dataString);
		    
		         StringEntity entity = new StringEntity(jsonObj1.toString(), HTTP.UTF_8);
		         entity.setContentType("application/json");
		         post.setEntity(entity);
		         post.addHeader("Content-Type", "application/json");
		 
		         HttpResponse apacheResponse = client.execute(post);
		         logger.info("getHighChartData apacheResponse ::: "+ apacheResponse.getStatusLine().getStatusCode());
		         if(apacheResponse.getStatusLine().getStatusCode() != 200) {
		        	 logger.info("before : getHighChartData @@@@@@@@@@@::"+System.currentTimeMillis());
		        	 Thread.sleep(120000);
		        	 logger.info("aftter : getHighChartData @@@@@@@@@@@@@::"+System.currentTimeMillis());
		        	 dateFileName = getHighChartData( data,  fileName,  folderName);
		         } else if (apacheResponse.getStatusLine().getStatusCode() == 200) {
		  
		          String rootPath = System.getProperty(IMAGES_LOCATION);
		          String imageFolder = rootPath + File.separator + folderName;
		            
		          File file = new File(imageFolder);
		          if (!file.exists()) {
		             file.mkdir();
		          }
		    
		          InputStream instream = null;
		          FileOutputStream fosObj = null;
		          
		          HttpEntity entity1 = apacheResponse.getEntity(); 
		          if (entity1 != null) {
		               instream = entity1.getContent();
		               try {
		               dateFileName = fileName + "_" + new Date().getTime() + ".png";
		               String imageFilePath = imageFolder + File.separator + dateFileName;
		               fosObj = new FileOutputStream(new File(imageFilePath));
		              int inByte;
		              while((inByte = instream.read()) != -1) fosObj.write(inByte);
		               } finally {
		                   instream.close();
		                   fosObj.close();
		               } 
		          }
		          return dateFileName;
		         }
		         
		  } catch (Exception e) {
		   logger.error("", e);
		  }
		  return dateFileName;
		 }*/
	public String getHighChartData(String data, String fileName, String folderName) {
		  String dateFileName = "";
		  try {
			  HttpPost post = new HttpPost("http://export.highcharts.com");
			  HttpClient httpClient = HttpClientBuilder.create().build();
			  
			  ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			  postParameters.add(new BasicNameValuePair("content", "options"));
			  postParameters.add(new BasicNameValuePair("options", data));
			  postParameters.add(new BasicNameValuePair("constr", "Chart"));
			  postParameters.add(new BasicNameValuePair("type", "png"));
		      post.setEntity(new UrlEncodedFormEntity(postParameters));
		         
		         HttpResponse apacheResponse = httpClient.execute(post);
		         logger.info("getHighChartData apacheResponse ::: "+ apacheResponse.getStatusLine().getStatusCode());
		         if(apacheResponse.getStatusLine().getStatusCode() != 200) {
		        	 logger.info("before : getHighChartData @@@@@@@@@@@::"+System.currentTimeMillis());
		        	 Thread.sleep(120000);
		        	 logger.info("aftter : getHighChartData @@@@@@@@@@@@@::"+System.currentTimeMillis());
		        	 dateFileName = getHighChartData( data,  fileName,  folderName);
		         } else if (apacheResponse.getStatusLine().getStatusCode() == 200) {
		  
		          String rootPath = System.getProperty(IMAGES_LOCATION);
		          String imageFolder = rootPath + File.separator + folderName;
		            
		          File file = new File(imageFolder);
		          if (!file.exists()) {
		             file.mkdir();
		          }
		    
		          InputStream instream = null;
		          FileOutputStream fosObj = null;
		          
		          HttpEntity entity1 = apacheResponse.getEntity(); 
		          if (entity1 != null) {
		               instream = entity1.getContent();
		               try {
		            	   dateFileName = fileName + "_" + new Date().getTime() + ".png";
		            	   String imageFilePath = imageFolder + File.separator + dateFileName;
		            	   fosObj = new FileOutputStream(new File(imageFilePath));
		            	   int inByte;
		            	   while((inByte = instream.read()) != -1) fosObj.write(inByte);
		               	} finally {
		               		instream.close();
		               		fosObj.close();
		               	} 
		          	}
		          	return dateFileName;
		         }
		  } catch (Exception e) {
			  logger.error("", e);
		  }
		  return dateFileName;
	}
	 @Override
		public Response weeklyPDFReportGeneratorCustomer(PDFThreadRequest pDFThreadRequest) throws VEMAppException,
				MalformedURLException {

			logger.info("[BEGIN] [weeklyPDFReportGeneratorCustomer] [PDF REPORT SERVICE LAYER]");
			
			// Creating response instance
			Response response = new Response();
			
			Map<Integer, ArrayList<HashMap<String, String>>> userData = null;
			URL url = pDFThreadRequest.getUrl();
		
			//Getting the dates
	    	Date date = pDFThreadRequest.getDate();
	    	String folderName = pDFThreadRequest.getFolderName();
	    	Calendar cal = null;
	    	
			JSONObject pdfReportJson;
			org.json.simple.JSONArray customerListArray;
			org.json.simple.JSONObject customerJsonObject;
			
			Report degradedReportJsonArray;
			Report withinReportJsonArray;
			Report manualChangesJsonArray;
			Report hvacReportJson;
			List<DataName> reportsBarJsonArray;
			List<Report> criticalIssuesJsonArray;
			List<Report> resolvedIssuesJsonArray;
			List<Report> correspondencesJsonArray;
			
			int reportLevel = 0;
			int reportPreference = 0;
			List<String> reportLevelIds;
			String userFirstName = "";
			String userLastName = "";
			String userEmail = "";
			String reportPreferenceText = "";
			String reportLevelText = "";
			int reportPreferencedays = 0;
			int daysRedirect = 0;
			String toDate = "";
			String fromDate = "";
			String toDateActivity = "";
			String fromDateActivity = "";
			String toDateMMDD = "";
			String fromDateMMDD = "";
			String toDateMMDDDot = "";
			String fromDateMMDDDot = "";
			String toDateText = "";
			String fromDateText = "";
			int systemUserId = 0;
			int userId = 0;
			List<String> customerNames;
			String timeZone="";
			
			try {
					
				ReportResponse reportResponse = new ReportResponse();
				
				//Getting the users data
				userData = pdfReportDAO.getPDFReportUsersCustomerData();
				
				ArrayList<HashMap<String, String>> reportCustomerList;
				
				for (Map.Entry<Integer, ArrayList<HashMap<String, String>>> entry : userData.entrySet()) {
					
					customerNames = new ArrayList<String>();
					
					pdfReportJson = new JSONObject();
				    
					pdfReportJson.put("logo", "logoEA.png");
					pdfReportJson.put("footerEmail", "support@enerallies.com");
					pdfReportJson.put("footerPhone", "1-888-770-3009 x300");
					
					userId = entry.getKey();
					reportCustomerList = entry.getValue();
				    HashMap<String, String> reportSiteData;
				    
				    customerListArray = new org.json.simple.JSONArray();
				    reportLevelIds = new ArrayList<String>();
				    
				    String rootPath = System.getProperty(IMAGES_LOCATION);
				    String imageFolder = rootPath + File.separator + "PDFImages";
	        		
			        File file = new File(imageFolder);
			        if (!file.exists()) {
			           file.mkdir();
			        }
			        HashMap<String, String> reportSiteDataTemp;
			        int mainDeviceCount = 0;
			        for (int i = 0; i < reportCustomerList.size(); i++) {
			        	reportSiteDataTemp = reportCustomerList.get(i);
				        if (Integer.parseInt(reportSiteDataTemp.get("deviceCount").toString()) > 0) {
				        	mainDeviceCount++;
				        }
				    }
			        if (reportCustomerList.size() > 0) {
			        	reportSiteDataTemp = reportCustomerList.get(0);
				        if (Integer.parseInt(reportSiteDataTemp.get("userReportPreference")) == 1) {
				    		reportPreferencedays = 7;
				    		daysRedirect = 7;
				    	} else if (Integer.parseInt(reportSiteDataTemp.get("userReportPreference")) == 2) {
				    		reportPreferencedays = 31;
				    		daysRedirect = 28;
				    	} else if (Integer.parseInt(reportSiteDataTemp.get("userReportPreference")) == 3) {
				    		reportPreferencedays = 90;
				    	} else {
				    		reportPreferencedays = 365;
				    	}
				        cal = Calendar.getInstance();
						cal.setTime(date);
						cal.add(Calendar.DATE, -(reportPreferencedays));
						fromDate = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", cal.getTime());
						toDate = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", date);
							
						cal.add(Calendar.DATE, +1);
						
						fromDateActivity = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", cal.getTime());
						toDateActivity = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", date);
						
						fromDateMMDD = DatesUtil.getDateFormat("MM/dd", cal.getTime());
						toDateMMDD = DatesUtil.getDateFormat("MM/dd", date);

						fromDateMMDDDot = DatesUtil.getDateFormat("MM.dd", cal.getTime());
						toDateMMDDDot = DatesUtil.getDateFormat("MM.dd", date);
						
						fromDateText = DatesUtil.getDateFormat("MMM dd, yyyy", cal.getTime());
						toDateText = DatesUtil.getDateFormat("MMM dd, yyyy", date);
				    }
					
			        if (mainDeviceCount > 0) {
			        
			        StringBuilder mainHtmlPage;
					StringBuilder mainHtmlEmailPage = null;
					
				    for (int i = 0; i < reportCustomerList.size(); i++) {
				    	
				    	customerJsonObject = new org.json.simple.JSONObject();
				    	reportSiteData = reportCustomerList.get(i);
				    	systemUserId  = Integer.parseInt(reportSiteData.get("loggedUser"));
				    	
				    	/*
				    	 * Getting the timezone value based on Server Address.
				    	 */
				    	Calendar now = Calendar.getInstance();
					    timeZone = now.getTimeZone().getID();
				    			
				    	if (Integer.parseInt(reportSiteData.get("deviceCount").toString()) > 0) {
					    	//Getting Manual log data
							correspondencesJsonArray = activityLogDao.getManualActivityLogData(Integer.parseInt(reportSiteData.get("userReportLevel")), Integer.parseInt(reportSiteData.get("customerId")), reportPreferencedays, "");
							logger.debug("Manual log data jsonArray @@@@@@@@@@ ::: "+ correspondencesJsonArray);
							
							ReportRequest reportRequest =  new ReportRequest();
							reportRequest.setType("CUSTOMER");
							reportRequest.setReportType("1");
							reportRequest.setCustomerIds(reportSiteData.get("customerId"));
							reportRequest.setGroupIds("");
							reportRequest.setSiteIds("");
							reportRequest.setInDays(reportPreferencedays);
							reportRequest.setFromDate(fromDate);
							reportRequest.setToDate(toDate);
							
							degradedReportJsonArray = reportDao.getDegradedPerformData(reportRequest, systemUserId, timeZone);
							logger.debug("Degraded HVAC Units reportsData @@@@@@@@@@ ::: "+degradedReportJsonArray);
							
							
							withinReportJsonArray = reportDao.getWithinSetpointPerformData(reportRequest, systemUserId, timeZone);
							logger.debug("within set points reportsData @@@@@@@@@@ ::: "+withinReportJsonArray);
							
							//Getting HVAC Usage
							hvacReportJson = reportDao.getHVACUsagePerformData(reportRequest, systemUserId, timeZone);
							logger.debug("HVAC Usage reportsData @@@@@@@@@@ ::: "+hvacReportJson);
							
							//Getting Manual Changes
							manualChangesJsonArray = reportDao.getManualChangesPerformData(reportRequest, systemUserId, timeZone);
							logger.debug("Manual Changes reportsData @@@@@@@@@@ :::  "+manualChangesJsonArray);
							
							//Getting Critical issues
							reportsBarJsonArray = reportDao.getCriticalIssues(reportRequest, systemUserId, timeZone);
							logger.debug("Critical issues reportsData  @@@@@@@@@@ :::  "+reportsBarJsonArray);
							
							//Critical issues alerts
							AlertRequest alertRequest = new AlertRequest();
							alertRequest.setIsEai(1);
							alertRequest.setUserId(systemUserId);
							alertRequest.setIsSuper(1);
							alertRequest.setTimeZone(timeZone);
							alertRequest.setAlertStatus("open");
							alertRequest.setFromCurrentPage("customers");
							alertRequest.setSpecificId(reportSiteData.get("customerId"));
							alertRequest.setTimePeriodInDays(reportPreferencedays);
							alertRequest.setPriority("P1");
							alertRequest.setPdfReportalertIds("");
							Response response1 = alertDao.getDashboardAlerts(alertRequest);
							logger.debug("Critical issues alerts data  @@@@@@@@@@ :::  "+(org.json.simple.JSONObject) response1.getData());
							org.json.simple.JSONObject alertsJson = (org.json.simple.JSONObject) response1.getData();
							criticalIssuesJsonArray = (org.json.simple.JSONArray) alertsJson.get("alertsInfo");
							
							//Resolved issues alerts
							alertRequest = new AlertRequest();
							alertRequest.setIsEai(1);
							alertRequest.setUserId(systemUserId);
							alertRequest.setIsSuper(1);
							alertRequest.setTimeZone(timeZone);
							alertRequest.setAlertStatus("resolve");
							alertRequest.setFromCurrentPage("customers");
							alertRequest.setSpecificId(reportSiteData.get("customerId"));
							alertRequest.setTimePeriodInDays(reportPreferencedays);
							alertRequest.setPriority("");
							alertRequest.setPdfReportalertIds("1,2,5");
							response1 = alertDao.getDashboardAlerts(alertRequest);
							logger.debug("Resolved issues alerts data  @@@@@@@@@@ :::  "+ (org.json.simple.JSONObject) response1.getData());
							org.json.simple.JSONObject resolvedJson = (org.json.simple.JSONObject) response1.getData();
							resolvedIssuesJsonArray = (org.json.simple.JSONArray) resolvedJson.get("alertsInfo");
					    	
							/*
							 * preparing site json object for pdf report
							 * */
							
							customerJsonObject.put("logo", reportSiteData.get("companyLogo"));
							customerJsonObject.put("customerId", reportSiteData.get("customerId"));
							customerJsonObject.put("companyName", reportSiteData.get("companyName"));
					    	customerJsonObject.put("reportType", reportSiteData.get("userReportPreferenceText") + " Report");
					    	customerJsonObject.put("comfortOpt", reportSiteData.get("comfortOpt"));
					    	customerJsonObject.put("deviceCount", reportSiteData.get("deviceCount"));
					    	customerJsonObject.put("toDate", DatesUtil.getDateFormat("MMM dd, yyyy", date));
					    	customerJsonObject.put("fromDate", DatesUtil.getDateFormat("MMM dd, yyyy", cal.getTime()));
					    	customerJsonObject.put("siteIds", reportSiteData.get("siteIds"));
					    	
					    	customerJsonObject.put("degradedReport", degradedReportJsonArray);
					    	customerJsonObject.put("withinReport", withinReportJsonArray);
					    	customerJsonObject.put("hvacReport", hvacReportJson);
					    	customerJsonObject.put("manualChanges", manualChangesJsonArray);
					    	customerJsonObject.put("reports", reportsBarJsonArray);
					    	customerJsonObject.put("criticalIssues", criticalIssuesJsonArray);
					    	customerJsonObject.put("resolvedIssues", resolvedIssuesJsonArray);
					    	customerJsonObject.put("correspondences", correspondencesJsonArray);
				    	
				    		customerListArray.add(customerJsonObject);
				    	
				    		reportLevel = Integer.parseInt(reportSiteData.get("userReportLevel"));
				    		reportPreference = Integer.parseInt(reportSiteData.get("userReportPreference"));
				    		userEmail = reportSiteData.get("userEmail");
				    		userFirstName = reportSiteData.get("userFname");
				    		userLastName = reportSiteData.get("userLname");
				    		reportLevelText = reportSiteData.get("userReportLevelText");
				    		reportPreferenceText = reportSiteData.get("userReportPreferenceText");
				    	}
				    }
				    pdfReportJson.put("customerList", customerListArray);
				    JSONArray jsonArray = pdfReportJson.getJSONArray("customerList");

				    for (int i = 0; i < jsonArray.length(); i++) {
					
				    	JSONObject objectInArray = jsonArray.getJSONObject(i);
					
						mainHtmlPage = new StringBuilder();
						mainHtmlEmailPage = new StringBuilder();
						customerNames.clear();
						reportLevelIds.clear();
						customerNames.add(objectInArray.getString("companyName"));
			    		reportLevelIds.add(objectInArray.getString("customerId"));
					
						/*PDF Design*/
						if (objectInArray.getInt("deviceCount") > 0) {
							
							String fileName = "PDF_"+new Date().getTime()+".pdf";
							String imageFilePath = imageFolder + File.separator + fileName;
					        Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
					        OutputStream fos = new FileOutputStream(new File(imageFilePath));
					        PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);
					        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
					        doc.open();
					        ByteArrayInputStream is = null;

						mainHtmlPage.append("<!DOCTYPE html><html lang='en'><head>");
						mainHtmlPage.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
						mainHtmlPage.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
								+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
						
						mainHtmlPage.append("<style>"
								+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:14; margin:0;} .bodyText{font-size:11;}"
								+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
								+ ".loginBtn{padding-left:15; float:left; padding-right:15; padding-top:15; padding-bottom:15; background:#1a6394; color:#fff; font-size:14; text-decoration:none; border-radius:5;}"
								+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15; padding-top:15;}"
								+ ".chartTitle{font-weight:bold; font-size:10; color:black; text-align:center; text-transform:uppercase;}"
								+ ".spacer{height:10px;} "
								+ "</style></head><body>");
						
						/*Email Design*/
						mainHtmlEmailPage.append("<!DOCTYPE html><html lang='en'><head>");
						mainHtmlEmailPage.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
						mainHtmlEmailPage.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
								+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
						
						mainHtmlEmailPage.append("<style>"
								+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:18px; margin:0;} .bodyText{font-size:14px;}"
								+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
								+ ".loginBtn{padding:15px; float:left; background:#1a6394; color:#fff; font-size:18px; text-decoration:none; border-radius:5px;}"
								+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15px; padding-top:15px;}"
								+ ".chartTitle{font-weight:bold; font-size:10px; color:black; text-align:center; text-transform:uppercase;}"
								+ ".spacer{height:10px}.spacertr{margin-left:12px;margin-right:12px;display:table;}"
								+ ".grayborder{background:#EEEEEE;display:block;position:relative;width:100%;margin-top:20px;margin-bottom:20px;}"
								+ "</style></head><body class='grayborder'>");
						
						/*PDF Design*/
						mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0' class='mainTable'>");
							mainHtmlPage.append("<thead>");
							mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td>&nbsp;&nbsp;<img src='"+url+"/fileUpload/loadPDFReportImages?imageName="+pdfReportJson.getString("logo")+"' width='200'></img></td>");
							mainHtmlPage.append("</tr>");
							mainHtmlPage.append("</thead>");
						mainHtmlPage.append("<tbody>");
						
						/*Email Design*/
							mainHtmlEmailPage.append("<div style='background: #DDDDDD;padding: 30px 0;'><table width='920' align='center' border='0' cellpadding='0' bgcolor='#FFFFFF' cellspacing='0' class='mainTable'>");
							mainHtmlEmailPage.append("<thead>");
								mainHtmlEmailPage.append("<tr class='spacertr'>");
									mainHtmlEmailPage.append("<td><img src='"+url+"/fileUpload/loadPDFReportImages?imageName=logoEA.png' width='200'></img></td>");
								mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("</thead>");
							mainHtmlEmailPage.append("<tbody>");
						
						/*PDF Design*/
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='blue-bg'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>");
								mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
									mainHtmlPage.append("<tr>");
										if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
											mainHtmlPage.append("<td>");
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+objectInArray.getString("logo")+"'></img>");
											mainHtmlPage.append("</td>");
										}
										if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
											mainHtmlPage.append("<td>");
												mainHtmlPage.append("<h3>"+objectInArray.getString("companyName") + " " + objectInArray.getString("reportType")+"</h3>");
												mainHtmlPage.append("<span class='bodyText'>"+fromDateText + " to " + toDateText+"</span>");
											mainHtmlPage.append("</td>");
										} else {
											mainHtmlPage.append("<td>");
												mainHtmlPage.append("<h3>&nbsp;&nbsp;&nbsp;"+objectInArray.getString("companyName") + " " + objectInArray.getString("reportType")+"</h3>");
												mainHtmlPage.append("<span class='bodyText'>&nbsp;&nbsp;&nbsp;&nbsp;"+fromDateText + " to " + toDateText+"</span>");
											mainHtmlPage.append("</td>");
										}
										if (objectInArray.getString("comfortOpt").equalsIgnoreCase("1")) {
											mainHtmlPage.append("<td align='right'>");
												mainHtmlPage.append("<h3>Comfort Optimization On</h3>");
											mainHtmlPage.append("</td>");
											mainHtmlPage.append("<td width='30'>");
												mainHtmlPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
											mainHtmlPage.append("</td>");
										}
									mainHtmlPage.append("</tr>");
								mainHtmlPage.append("</table>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='grey-bg'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a href='"+url+"/dashboard#?pdfFlag=dashboard&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+daysRedirect+"' style='color:#1a6394;text-decoration: underline;'>Customer Performance</a></h3></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>");
							
						/*Email Design*/
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='blue-bg'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td>");
								mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
									mainHtmlEmailPage.append("<tr>");
										if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
											mainHtmlEmailPage.append("<td>");
												mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+objectInArray.getString("logo")+"'></img>");
											mainHtmlEmailPage.append("</td>");
										}
										if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
											mainHtmlEmailPage.append("<td>");
											mainHtmlEmailPage.append("<h3>"+objectInArray.getString("companyName") + " " + objectInArray.getString("reportType")+"</h3>");
											mainHtmlEmailPage.append("<span class='bodyText'>"+fromDateText + " to " + toDateText+"</span>");
											mainHtmlEmailPage.append("</td>");
										} else {
											mainHtmlEmailPage.append("<td>");
											mainHtmlEmailPage.append("<h3>&nbsp;&nbsp;&nbsp;"+objectInArray.getString("companyName") + " " + objectInArray.getString("reportType")+"</h3>");
											mainHtmlEmailPage.append("<span class='bodyText'>&nbsp;&nbsp;&nbsp;&nbsp;"+fromDateText + " to " + toDateText+"</span>");
											mainHtmlEmailPage.append("</td>");
										}
										if (objectInArray.getString("comfortOpt").equalsIgnoreCase("1")) {
											mainHtmlEmailPage.append("<td align='right'>");
											mainHtmlEmailPage.append("<h3>Comfort Optimization On</h3>");
											mainHtmlEmailPage.append("</td>");
											mainHtmlEmailPage.append("<td width='30'>");
											mainHtmlEmailPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
											mainHtmlEmailPage.append("</td>");
										}
										mainHtmlEmailPage.append("</tr>");
								mainHtmlEmailPage.append("</table>");
							mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='grey-bg'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><h3><a href='"+url+"/dashboard#?pdfFlag=dashboard&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+daysRedirect+"' style='color:#1a6394;text-decoration: underline;'>Customer Performance</a></h3></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td>");
						JSONArray reportsData = objectInArray.getJSONArray("reports");
						JSONObject degradedReportJson = objectInArray.getJSONObject("degradedReport");
						JSONObject withinReportJson = objectInArray.getJSONObject("withinReport");
						JSONObject manualReportJson = objectInArray.getJSONObject("manualChanges");
						JSONObject hvacReportJsons = objectInArray.getJSONObject("hvacReport");
						
							/*PDF Design*/
							mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								mainHtmlPage.append("<tr>");
									mainHtmlPage.append("<td width='25%'>");
										String chart1 = getHighChartData(getSolidGuageChart(degradedReportJson, "DEGRADED HVAC UNITS"), "DEGRADED", folderName);
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName + "&imageName=").append(chart1+"'></img>");
									mainHtmlPage.append("</td>");
									mainHtmlPage.append("<td width='25%'>");
										String chart2 = getHighChartData(getSolidGuageChart(withinReportJson, "WITHIN SETPOINTS"), "WITHIN", folderName);
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName + "&imageName=").append(chart2+"'></img>");
									mainHtmlPage.append("</td>");
									mainHtmlPage.append("<td width='25%'>");
										String chart3 = getHighChartData(getSolidGuageChart(hvacReportJsons, "HVAC USAGE"), "HVAC", folderName);
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+ folderName + "&imageName=").append(chart3+"'></img>");
									mainHtmlPage.append("</td>");
									mainHtmlPage.append("<td width='25%'>");
										String chart4 = getHighChartData(getSolidGuageChart(manualReportJson, "MANUAL CHANGES"), "MANUAL", folderName);
										mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+ folderName + "&imageName=").append(chart4+"'></img>");
									mainHtmlPage.append("</td>");
								mainHtmlPage.append("</tr>");
								mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td colspan='4' class='spacer'></td>");
								mainHtmlPage.append("</tr>");
								mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td valign='middle' align='center'>");
									mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DEGRADED HVAC UNITS</p>");
								mainHtmlPage.append("</td>");
								mainHtmlPage.append("<td valign='middle' align='center'>");
									mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;WITHIN SETPOINTS</p>");
								mainHtmlPage.append("</td>");
								mainHtmlPage.append("<td valign='middle' align='center'>");
									mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;HVAC USAGE</p>");
								mainHtmlPage.append("</td>");
								mainHtmlPage.append("<td valign='middle' align='center'>");
									mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MANUAL CHANGES</p>");
								mainHtmlPage.append("</td>");
							mainHtmlPage.append("</tr>");
							mainHtmlPage.append("</table>");
						mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='grey-bg2'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#000000;cursor: auto;text-decoration: underline;'>Current Critical Issues</a></h3></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td>");
						
						/*Email Design*/
						mainHtmlEmailPage.append("<table width='1024' border='0' cellpadding='0' cellspacing='0'>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td>");
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getSolidGuageChart(degradedReportJson, "DEGRADED HVAC UNITS"))+"' width='256'></img>");
								mainHtmlEmailPage.append("</td>");
								mainHtmlEmailPage.append("<td>");
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getSolidGuageChart(withinReportJson, "WITHIN SETPOINTS"))+"' width='256'></img>");
								mainHtmlEmailPage.append("</td>");
								mainHtmlEmailPage.append("<td>");
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getSolidGuageChart(hvacReportJsons, "HVAC USAGE"))+"' width='256'></img>");
								mainHtmlEmailPage.append("</td>");
								mainHtmlEmailPage.append("<td>");
									mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getSolidGuageChart(manualReportJson, "MANUAL CHANGES"))+"' width='256'></img>");
								mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td colspan='4' class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td width='25%'>");
									mainHtmlEmailPage.append("<p class='chartTitle'>DEGRADED HVAC UNITS</p>");
								mainHtmlEmailPage.append("</td>");
								mainHtmlEmailPage.append("<td width='25%'>");
									mainHtmlEmailPage.append("<p class='chartTitle'>WITHIN SETPOINTS</p>");
								mainHtmlEmailPage.append("</td>");
								mainHtmlEmailPage.append("<td width='25%'>");
									mainHtmlEmailPage.append("<p class='chartTitle'>HVAC USAGE</p> ");
								mainHtmlEmailPage.append("</td>");
								mainHtmlEmailPage.append("<td width='25%'>");
									mainHtmlEmailPage.append("<p class='chartTitle'>MANUAL CHANGES</p>");
								mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("</table>");
						mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='grey-bg2'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><h3 style='color:#000000;text-decoration: underline;'>Current Critical Issues</h3><br/></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
						mainHtmlEmailPage.append("<td>");
						int count = 0;
						for(int g = 0; g < reportsData.length(); g++) {
							JSONObject criticalObject = (JSONObject) reportsData.get(g);
							JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
							if (criticalY.getInt("y") > 0) {
								count++;	
							}
						}
						if (count > 0) {
							String column = getHighChartData(getColumnChart(reportsData), "COLUMN", folderName);
							mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+ folderName + "&imageName=").append(column+"' width='300'></img>");
							mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getColumnChart(reportsData))+"' width='400'/>");
						} else {
							mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=noCriticalIssues.png' width='300'></img>");
							mainHtmlEmailPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=noCriticalIssues.png' width='400'/>");
						}
						
						/*PDF Design*/
						mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='grey-bg2'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
						
						JSONArray criticalData = objectInArray.getJSONArray("criticalIssues");
						
						if (criticalData.length() > 0) {
							mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+reportLevelText+"'>Current Critical Issues</a></h3></td>");
							mainHtmlPage.append("</tr>");
							mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td class='spacer'></td>");
							mainHtmlPage.append("</tr>");
						}
						/*Email Design*/
						mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='grey-bg2'></td>");
						mainHtmlEmailPage.append("</tr>");
						if (criticalData.length() > 0) {
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td><br/><h3><a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+reportLevelText+"'>Current Critical Issues</a></h3><br/></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
						}
						
						if (criticalData.length() > 0) {
							/*Email Design*/
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td>");
									mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
									boolean temp = false;
									for (int j = 0; j < criticalData.length(); j++) {
										
										JSONObject objectInCritical = criticalData.getJSONObject(j);
										String alertMsg = "";
										try{
											alertMsg = objectInCritical.getString("alertMessage");
										}catch(JSONException e){
											alertMsg = "";
										}
										JSONArray actionItems = objectInCritical.getJSONArray("actionItems");
										
										/*PDF Design*/
										mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
												"&nbsp;&nbsp;&nbsp;<b>"+ (j + 1) +". "+ objectInCritical.getString("alertName") + " " + objectInCritical.getString("alertProrityName") + "</b> - "+alertMsg+" <br></br>"+
												"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("customerName") + (StringUtils.isNotEmpty(objectInCritical.getString("groupName")) ? ", " : "") + objectInCritical.getString("groupName") + "<br></br>"+
												"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("siteName") + ", "+ objectInCritical.getString("deviceName") + "<br></br>");
											if (actionItems.length() > 0) {		
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Action List<br></br>");
											}
											
										/*Email Design*/	
										mainHtmlEmailPage.append("<tr>");
											mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
													"<b>"+ (j + 1) +". "+ objectInCritical.getString("alertName") + " " + objectInCritical.getString("alertProrityName") + "</b> - "+alertMsg+"<br/>"+
													"&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("customerName") + ", "+ objectInCritical.getString("groupName") + "<br/>"+
													"&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("siteName") + ", "+ objectInCritical.getString("deviceName") + "<br/>");
												if (actionItems.length() > 0) {		
													mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;&nbsp;- Action List<br/>");
												}
												
											for (int k = 0; k < actionItems.length(); k++) {
												JSONObject actionItemsObj = actionItems.getJSONObject(k);
												if (actionItemsObj.getInt("itemStatus") == 1) {
													/*PDF Design*/
													mainHtmlPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strike style='text-decoration: line-through'>"+actionItemsObj.getString("itemName")+"</strike><br></br>");
													/*Email Design*/	
													mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strike style='text-decoration: line-through'>"+actionItemsObj.getString("itemName")+"</strike><br/>");
												} else {
													/*PDF Design*/
													mainHtmlPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+actionItemsObj.getString("itemName")+"<br></br>");
													/*Email Design*/	
													mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+actionItemsObj.getString("itemName")+"<br/>");
												}
												
										    }
										/*PDF Design*/
											mainHtmlPage.append("</p></td>");
										mainHtmlPage.append("</tr>");
										
										/*Email Design*/
											mainHtmlEmailPage.append("</p></td>");
											mainHtmlEmailPage.append("</tr>");
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td class='spacer'></td>");
											mainHtmlEmailPage.append("</tr>");
										if (j == 4) {
											temp = true;
											break;
										}
									}
									if (temp) {
										/*PDF Design*/			
										mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td><p class='bodyText text-indent'>" +
																"&nbsp;&nbsp;&nbsp;Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+reportLevelText+"'>here</a> for more issues");
											mainHtmlPage.append("</p></td>");
										mainHtmlPage.append("</tr>");
										/*Email Design*/			
										mainHtmlEmailPage.append("<tr>");
											mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>" +
															"Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+reportLevelText+"'>here</a> for more issues");
											mainHtmlEmailPage.append("</p></td>");
										mainHtmlEmailPage.append("</tr>");
									}
							/*Email Design*/	
									mainHtmlEmailPage.append("</table>");
								mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("</tr>");
						}
						/*PDF Design*/
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
						JSONArray resolvedData = objectInArray.getJSONArray("resolvedIssues");
						if (resolvedData.length() > 0) {
							mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+reportLevelText+"'>Resolved Issues</a></h3></td>");
							mainHtmlPage.append("</tr>");
							mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td class='spacer'></td>");
							mainHtmlPage.append("</tr>");
						}
						/*Email Design*/
						if (reportLevelIds.size() == 1 && resolvedData.length() > 0) {
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td><h3><a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+reportLevelText+"'>Resolved Issues</a></h3><br/></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
						}
					
						if (resolvedData.length() > 0) {
							/*Email Design*/	
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td>");
									mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
										boolean rTemp = false;
										for (int m = 0; m < resolvedData.length(); m++) {
											JSONObject objectResolved = resolvedData.getJSONObject(m);
											String alertMsg = "";
											try{
												alertMsg = objectResolved.getString("alertMessage");
											}catch(JSONException e){
												alertMsg = "";
											}
											/*PDF Design*/
											mainHtmlPage.append("<tr>");
												mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
														"&nbsp;&nbsp;&nbsp;<b>"+ (m + 1) +". "+ objectResolved.getString("alertName") + " " + objectResolved.getString("alertProrityName") + "</b> - " + alertMsg);
												mainHtmlPage.append("</p></td>");
											mainHtmlPage.append("</tr>");
											/*Email Design*/
												mainHtmlEmailPage.append("<tr>");
													mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
															"<b>"+ (m + 1) +". "+ objectResolved.getString("alertName") + " " + objectResolved.getString("alertProrityName") + "</b> - " + alertMsg);
													mainHtmlEmailPage.append("</p></td>");
												mainHtmlEmailPage.append("</tr>");
												mainHtmlEmailPage.append("<tr>");
													mainHtmlEmailPage.append("<td class='spacer'></td>");
												mainHtmlEmailPage.append("</tr>");
											if (m == 4) {
												rTemp = true;
												break;
											}
									    }
										if (rTemp) {
											/*PDF Design*/
											mainHtmlPage.append("<tr>");
												mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
															"&nbsp;&nbsp;&nbsp;Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+reportLevelText+"'>here</a> for more issues");
												mainHtmlPage.append("</p></td>");
											mainHtmlPage.append("</tr>");
											/*Email Design*/
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
															"Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-0-0-"+reportLevelText+"'>here</a> for more issues");
												mainHtmlEmailPage.append("</p></td>");
											mainHtmlEmailPage.append("</tr>");
										}
							/*Email Design*/
								mainHtmlEmailPage.append("</table>");
							mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						}
						JSONArray correspondences = objectInArray.getJSONArray("correspondences");
						
						/*PDF Design*/
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
						if (correspondences.length() > 0) {
							mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#000000;cursor: auto;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=activity'>Correspondences</a></h3></td>");
							mainHtmlPage.append("</tr>");
							mainHtmlPage.append("<tr>");
								mainHtmlPage.append("<td class='spacer'></td>");
							mainHtmlPage.append("</tr>");
						}
						
						/*Email Design*/
						if (reportLevelIds.size() == 1 && correspondences.length() > 0) {
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td><h3><a style='color:#000000;cursor: auto;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=activity'>Correspondences</a></h3><br/></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
						}
					
						if (correspondences.length() > 0) {
							/*Email Design*/
								mainHtmlEmailPage.append("<tr class='spacertr'>");
									mainHtmlEmailPage.append("<td>");
										mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
										boolean cTemp = false;
										for (int c = 0; c < correspondences.length(); c++) {	
											JSONObject correspondencesObject = correspondences.getJSONObject(c);
											/*PDF Design*/
											mainHtmlPage.append("<tr>");
												String temp = "";
												if (correspondencesObject.getString("alAction").equalsIgnoreCase("called")) {
													temp = "Telephone Call";
												} else if (correspondencesObject.getString("alAction").equalsIgnoreCase("emailed")) {
													temp = "Email";
												} else if (correspondencesObject.getString("alAction").equalsIgnoreCase("texted")) {
													temp = "Text";
												}
												mainHtmlPage.append("<td><p class='bodyText text-indent'>" 
													+ "&nbsp;&nbsp;&nbsp;<b>"+ (c + 1) +". " + correspondencesObject.getString("strTimestamp") + " * " + temp);
													if (StringUtils.isNotEmpty(correspondencesObject.getString("strContact"))) {
														mainHtmlPage.append(" * " + correspondencesObject.getString("strContact"));
													}
													if (StringUtils.isNotEmpty(correspondencesObject.getString("strContactNumber"))) {
														mainHtmlPage.append(" * " + correspondencesObject.getString("strContactNumber"));
													}
													if (StringUtils.isNotEmpty(correspondencesObject.getString("alWhere"))) {
														mainHtmlPage.append(" * " + correspondencesObject.getString("alWhere"));
													}
													if (StringUtils.isNotEmpty(correspondencesObject.getString("alSpecificName"))) {
														mainHtmlPage.append(" * " + correspondencesObject.getString("alSpecificName"));
													}
													mainHtmlPage.append("</b><br></br>");
													
													mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<b>&nbsp;&nbsp;&nbsp;&nbsp;Subject: </b>"+ correspondencesObject.getString("strSubject"));
													if (StringUtils.isNotEmpty(correspondencesObject.getString("strDescription"))) {
														mainHtmlPage.append("<br></br>&nbsp;&nbsp;&nbsp;<b>&nbsp;&nbsp;&nbsp;&nbsp;Description: </b>"+ correspondencesObject.getString("strDescription"));
													}
												mainHtmlPage.append("</p><br></br></td>");
											mainHtmlPage.append("</tr>");	
											
											/*Email Design*/
												mainHtmlEmailPage.append("<tr>");
													mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>" 
														+ "<b>"+ (c + 1) +". " + correspondencesObject.getString("strTimestamp") + " * " + temp);
														if (StringUtils.isNotEmpty(correspondencesObject.getString("strContact"))) {
															mainHtmlEmailPage.append(" * " + correspondencesObject.getString("strContact"));
														}
														if (StringUtils.isNotEmpty(correspondencesObject.getString("strContactNumber"))) {
															mainHtmlEmailPage.append(" * " + correspondencesObject.getString("strContactNumber"));
														}
														if (StringUtils.isNotEmpty(correspondencesObject.getString("alWhere"))) {
															mainHtmlEmailPage.append(" * " + correspondencesObject.getString("alWhere"));
														}
														if (StringUtils.isNotEmpty(correspondencesObject.getString("alSpecificName"))) {
															mainHtmlEmailPage.append(" * " + correspondencesObject.getString("alSpecificName"));
														}
														mainHtmlEmailPage.append("</b><br/>");
														mainHtmlEmailPage.append("<b>&nbsp;&nbsp;&nbsp;&nbsp;Subject: </b>"+ correspondencesObject.getString("strSubject"));
														if (StringUtils.isNotEmpty(correspondencesObject.getString("strDescription"))) {
															mainHtmlEmailPage.append("<br/><b>&nbsp;&nbsp;&nbsp;&nbsp;Description: </b>"+ correspondencesObject.getString("strDescription"));
														}
														mainHtmlEmailPage.append("</p><br/></td>");
													mainHtmlEmailPage.append("</tr>");
												mainHtmlEmailPage.append("<tr>");
													mainHtmlEmailPage.append("<td class='spacer'></td>");
												mainHtmlEmailPage.append("</tr>");
											if (c == 4) {
												cTemp = true;
												break;
											}
										}
										if (cTemp) {
											/*PDF Design*/
											mainHtmlPage.append("<tr>");
												mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
															"&nbsp;&nbsp;&nbsp;Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=activity'>here</a> for more correspondences");
												mainHtmlPage.append("</p></td>");
											mainHtmlPage.append("</tr>");
											/*Email Design*/
												mainHtmlEmailPage.append("<tr>");
													mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
																"Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=activity'>here</a> for more correspondences");
													mainHtmlEmailPage.append("</p></td>");
												mainHtmlEmailPage.append("</tr>");
										}
							/*Email Design*/
								mainHtmlEmailPage.append("</table>");
							mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						}
						/*PDF Design*/
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("</tbody>");
						mainHtmlPage.append("</table>");
						mainHtmlPage.append("</body></html>");
						/*Email Design*/
					        mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td><a href='"+ url +"' style='text-decoration:none'><img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=ealogin.png' width='217'></img></a></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("</tbody>");
							mainHtmlEmailPage.append("<tfoot>");
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td><p class='bodyText'>"+
										"Please contact EnerAllies support at "+pdfReportJson.getString("footerPhone")+" or email <a href='#' style='color:#1a6394;text-decoration:none'>"+pdfReportJson.getString("footerEmail")+"</a> if you have any questions.<br/><br/>"+
										"If you wish to stop receiving notifications, please click the link below to unsubscribe:<br/>"+
										"<a style='color:#1a6394;' href='"+url+"/dashboard#?pdfFlag=user&pdfValue="+userId+"'>Alert Notification Settings</a>");
								mainHtmlEmailPage.append("</p></td>");
								mainHtmlEmailPage.append("</tr>");
								mainHtmlEmailPage.append("<tr>");
									mainHtmlEmailPage.append("<td class='spacer'></td>");
								mainHtmlEmailPage.append("</tr>");
								mainHtmlEmailPage.append("<tr>");
									mainHtmlEmailPage.append("<td align='center'><p class='bodyText'>Copyright &copy; 2017 EnerAllies. All rights reserved.</p></td>");
								mainHtmlEmailPage.append("</tr>");
								mainHtmlEmailPage.append("</tfoot>");
							mainHtmlEmailPage.append("</table></div>");
				        	mainHtmlEmailPage.append("</body></html>");
				        	is = new ByteArrayInputStream(mainHtmlPage.toString().getBytes());
						    worker.parseXHtml(pdfWriter, doc, is);		
						    doc.close();
						
						    fos.close();
			        
			    	AddManualLogRequest addManualLogRequest = new AddManualLogRequest();
			        addManualLogRequest.setIsPdfReport(1);
			        addManualLogRequest.setReportPreference(reportPreference);
			        addManualLogRequest.setReportLevel(reportLevel);
			        addManualLogRequest.setReportLevelIds(StringUtils.join(reportLevelIds, ", "));
			        addManualLogRequest.setDescription(objectInArray.getString("companyName") + " " +  reportPreferenceText + " " +
			        		reportLevelText + " Report " + "("+fromDateMMDD + "-" + toDateMMDD + ") sent to " +
			        		userFirstName + " " + userLastName + " at " + userEmail + ".");
			        addManualLogRequest.setSpecificId(reportLevelIds.isEmpty() ? 0 : Integer.parseInt(reportLevelIds.get(0)));
			        
			        addManualLogRequest.setReportComponent(reportPreferenceText + " " + reportLevelText + " Report");
			        activityLogDao.createManualActivityLog(addManualLogRequest, userId);
			        
			        AddPDFReportDataRequest addPDFReportDataRequest = new AddPDFReportDataRequest();
			        addPDFReportDataRequest.setToDate(toDateActivity);
			        addPDFReportDataRequest.setFromDate(fromDateActivity);
			        addPDFReportDataRequest.setReportLevelText(reportLevelText + " " + reportPreferenceText + " Report");
			        addPDFReportDataRequest.setUserId(userId);
			        addPDFReportDataRequest.setReportStatus(1);
			        addPDFReportDataRequest.setSpecificIds(reportLevelIds);
			        addPDFReportDataRequest.setActualFilePath(fileName);
			        addPDFReportDataRequest.setReportLevel(reportLevel);
			        addPDFReportDataRequest.setReportPreference(reportPreference);
			        addPDFReportDataRequest.setSiteIds(objectInArray.getString("siteIds"));
			        pdfReportDAO.addPDFReportData(addPDFReportDataRequest, userId);
			        
			        /* Email Sending*/
			        MailBroadCast mailBroadCast = new MailBroadCast();
					mailBroadCast.setFromEmail(ConfigurationUtils.getConfig("from.mail.pdf"));
					mailBroadCast.setToEmail(userEmail);
					StringBuilder emailTemplate = new StringBuilder();
					
					mailBroadCast.setSubject(objectInArray.getString("companyName") + " " + reportPreferenceText + " Report : "+fromDateMMDD + " - " + toDateMMDD);
					String pdfFileName = objectInArray.getString("companyName") + " " + reportPreferenceText + " " + reportLevelText + " Report - "+ fromDateMMDDDot +" - "+ toDateMMDDDot.toString().trim() + ".pdf";
					
						emailTemplate.append(mainHtmlEmailPage.toString());
						mailBroadCast.setMailText(emailTemplate.toString());
						File pdfFile = new File(rootPath + File.separator + "PDFImages" + File.separator +fileName);
						
						// Instantiating mail publisher
						MailPublisher publisher = new MailPublisher();
						
						// Broad casting mail
						boolean mailFlag = publisher.publishEmailWithAttachment(mailBroadCast, pdfFile, pdfFileName, true);
						if (mailFlag) {
							for (int k = 0; k < jsonArray.length(); k++) {
								JSONObject siteObj = jsonArray.getJSONObject(k);
						        String fullPathDelete = rootPath + File.separator + folderName;
						        File deleteFolder = new File(fullPathDelete);
						        if (deleteFolder.exists()) {
						        	String[]entries = deleteFolder.list();
						        	for(String s: entries){
						        	    File currentFile = new File(deleteFolder.getPath(),s);
						        	    currentFile.delete();
						        	}
						        	deleteFolder.delete();
						        }
							}
						}
					}
				  }
				}
				// Preparing success response object
				response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
				response.setCode(ErrorCodes.PDF_REPORT_SUCCESS);
				response.setData(reportResponse);
				}
			} catch (Exception e) {
				// Creating and throwing the customized exception.
				throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_FAILED, logger, e);
			}
			
			logger.info("[END] [weeklyPDFReportGeneratorCustomer] [PDF REPORT SERVICE LAYER]");
			return response;
		}
		private String getSolidGuageChart(JSONObject jsonObj, String name) throws JSONException {
			
			JSONObject jsonObjTemp = new JSONObject(jsonObj, JSONObject.getNames(jsonObj));
			
			JSONObject subObj = new JSONObject();
			
			org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
			org.json.simple.JSONArray jsonArray = new org.json.simple.JSONArray();
			
			int max = 100;
			String suffix = "%";
			
			int y = 0;
			if (jsonObjTemp.getString("name") != "HVAC USAGE") {
				if (jsonObjTemp.getInt("y") <= jsonObjTemp.getInt("max")) {
					y = Math.round((float)jsonObjTemp.getInt("y") / jsonObjTemp.getInt("max")*100);
				} else {
					y = 0;
				}
				
				if(jsonObjTemp.getInt("y") < 0) {
					y = 0;
				}
				jsonObjTemp.put("y", y);
			}
			
			if (jsonObjTemp.getString("name").equalsIgnoreCase("DEGRADED HVAC UNITS")) {
			 	if (jsonObjTemp.getInt("y") < 50) {
			 		jsonObjTemp.put("color", "#8DC643");
				} else if (jsonObjTemp.getInt("y") <= 75) {
					jsonObjTemp.put("color", "#EDEA63");
				} else if (jsonObjTemp.getInt("y") > 75) {
					jsonObjTemp.put("color", "#DD2133");
				}
			}
			if (jsonObjTemp.getString("name").equalsIgnoreCase("WITHIN SETPOINTS")) {
				if (jsonObjTemp.getInt("y") < 50) {
					jsonObjTemp.put("color", "#DD2133");
				} else if (jsonObjTemp.getInt("y") <= 75) {
					jsonObjTemp.put("color", "#EDEA63");
				} else if (jsonObjTemp.getInt("y") > 75) {
					jsonObjTemp.put("color", "#8DC643");
				}
			}
			if (jsonObjTemp.getString("name").equalsIgnoreCase("MANUAL CHANGES")) {
				if (jsonObjTemp.getInt("y") < 25) {
					jsonObjTemp.put("color", "#8DC643");
				} else if (jsonObjTemp.getInt("y") <= 50) {
					jsonObjTemp.put("color", "#EDEA63");
				} else if (jsonObjTemp.getInt("y") > 50) {
					jsonObjTemp.put("color", "#DD2133");
				}
			}
			if (jsonObjTemp.getString("name").equalsIgnoreCase("HVAC USAGE")) {
				int oldYData = 0;
				int max_val = jsonObjTemp.getInt("max");
				if (max_val < jsonObjTemp.getInt("y") || max_val < jsonObjTemp.getInt("extraValue")) {
					max_val = 0;
				}
				float heat = 0;
				float cool = 0;
				int heatHours = jsonObjTemp.getInt("y");
				int coolHours = jsonObjTemp.getInt("extraValue");
				if (max_val > 0) {
					oldYData = jsonObjTemp.getInt("max");
					heat =  (( (float)jsonObjTemp.getInt("y") / max_val )*100);
					cool = (( (float)jsonObjTemp.getInt("extraValue") / max_val)*100);
				}				
				float heatTemp;
				float coolTemp;
				if (Math.round(heat) >= Math.round(cool)) {
					heatTemp = heat+cool;
					coolTemp = cool;
				} else {
					heatTemp = heat;
					coolTemp = cool+heat;
				}
				// we should maintain the order by pushing larger one first
				if (heatTemp > coolTemp) {
					jsonObject = new org.json.simple.JSONObject();
					jsonObject.put("id", jsonObjTemp.getInt("id"));
					jsonObject.put("max", jsonObjTemp.getInt("max"));
					jsonObject.put("name", jsonObjTemp.getString("name"));
					jsonObject.put("reportId", jsonObjTemp.getInt("reportId"));
					jsonObject.put("y", heatTemp);
					jsonObject.put("msg", "Heat :" + heatHours + "hr");
					jsonObject.put("color", "#f7a35c");
					jsonArray.add(jsonObject);
					 
					jsonObject = new org.json.simple.JSONObject();
					jsonObject.put("id", jsonObjTemp.getInt("id"));
					jsonObject.put("max", jsonObjTemp.getInt("max"));
					jsonObject.put("name", jsonObjTemp.getString("name"));
					jsonObject.put("reportId", jsonObjTemp.getInt("reportId"));
					jsonObject.put("y", coolTemp);
					jsonObject.put("msg", "Cool :"+ coolHours + "hr");
					jsonObject.put("color", "#7cb5ec");
					jsonArray.add(jsonObject);
				} else {
					jsonObject = new org.json.simple.JSONObject();
					jsonObject.put("id", jsonObjTemp.getInt("id"));
					jsonObject.put("max", jsonObjTemp.getInt("max"));
					jsonObject.put("name", jsonObjTemp.getString("name"));
					jsonObject.put("reportId", jsonObjTemp.getInt("reportId"));
					jsonObject.put("y", coolTemp);
					jsonObject.put("msg", "Cool :"+ coolHours + "hr");
					jsonObject.put("color", "#7cb5ec");
					jsonArray.add(jsonObject);
					
					jsonObject = new org.json.simple.JSONObject();
					jsonObject.put("id", jsonObjTemp.getInt("id"));
					jsonObject.put("max", jsonObjTemp.getInt("max"));
					jsonObject.put("name", jsonObjTemp.getString("name"));
					jsonObject.put("reportId", jsonObjTemp.getInt("reportId"));
					jsonObject.put("y", heatTemp);
					jsonObject.put("msg", "Heat :" + heatHours + "hr");
					jsonObject.put("color", "#f7a35c");
					jsonArray.add(jsonObject);
				}
				oldYData = heatHours + coolHours;
	            suffix = oldYData + "hr";
	           
	        } else {
	        	suffix = jsonObjTemp.getInt("y") + "%";
	        	jsonArray.add(jsonObjTemp);
	        }
			
	    	subObj.put("chart", new JSONObject().put("type", "solidgauge").put("backgroundColor", "white"));
	    	subObj.put("title", new JSONObject().put("text", JSONObject.NULL));
	    	subObj.put("credits", new JSONObject().put("enabled", false));    	
	    	subObj.put("pane", new JSONObject().put("startAngle", 0).put("endAngle", 360)
	    		.put("background", new JSONObject().put("backgroundColor", "#EEE")
	    		.put("innerRadius", "75%").put("outerRadius", "100%").put("shape", "arc").put("borderColor", "white")));
	    	subObj.put("yAxis", new JSONObject().put("min", 0).put("max", max)
	    			.put("tickInterval", JSONObject.NULL).put("minorTickInterval", JSONObject.NULL)
	    			.put("tickPixelInterval", 400).put("tickAmount", 0).put("tickWidth", 0).put("lineColor", "white")
	    			.put("gridLineWidth", 0).put("gridLineColor", "white").put("labels", new JSONObject().put("enabled", false))
	    			.put("title", new JSONObject().put("enabled", false)));
	    	subObj.put("plotOptions", new JSONObject().put("solidgauge", new JSONObject().put("innerRadius", "75%")
	    			.put("dataLabels", new JSONObject().put("y", -25).put("borderWidth", 0).put("useHTML", true))));
	        org.json.simple.JSONArray jsonArrayData = new org.json.simple.JSONArray();
	    	
	        jsonArrayData.add(new JSONObject().put("data", jsonArray).put("name", name).put("dataLabels", new JSONObject().put("format","<p style='font-size:20px;text-align:center;'>"+suffix+"</p>").put("fontWeight", "bold").put("fontSize", "20px")));
	        
	    	subObj.put("series", jsonArrayData.size() > 0 ? jsonArrayData : null);
	    	return subObj.toString();
		}
		
		@Override
		public Response weeklyPDFReportGeneratorGroup(PDFThreadRequest pDFThreadRequest) throws VEMAppException, MalformedURLException {
		logger.info("[BEGIN] [weeklyPDFReportGeneratorGroup] [PDF REPORT SERVICE LAYER]");
		
		// Creating response instance
		Response response = new Response();
		
		Map<Integer, ArrayList<HashMap<String, String>>> userData = null;
		URL url = pDFThreadRequest.getUrl();
		
		//Getting the dates
    	Date date = pDFThreadRequest.getDate();
    	String folderName = pDFThreadRequest.getFolderName();
    	Calendar cal = null;
		
		JSONObject pdfReportJson;
		org.json.simple.JSONArray groupListArray;
		org.json.simple.JSONObject groupJsonObject;
		
		List<Report> degradedReportJsonArrayLable;
		List<Report> withinReportJsonArrayLable;
		List<Report> manualChangesJsonArrayLable;
		HVACUsage hvacReportJsonLable;
		
		Report degradedReportJsonArray;
		Report withinReportJsonArray;
		Report manualChangesJsonArray;
		Report hvacReportJson;
		List<DataName> reportsBarJsonArray;
		List<Report> criticalIssuesJsonArray;
		List<Report> resolvedIssuesJsonArray;
		List<Report> correspondencesJsonArray;
		
		int reportLevel = 0;
		int reportPreference = 0;
		List<String> reportLevelIds;
		String userFirstName = "";
		String userLastName = "";
		String userEmail = "";
		String reportPreferenceText = "";
		String reportLevelText = "";
		int reportPreferencedays = 0;
		int daysRedirect = 0;
		String toDate = "";
		String fromDate = "";
		String toDateActivity = "";
		String fromDateActivity = "";
		String toDateMMDD = "";
		String fromDateMMDD = "";
		String toDateMMDDDot = "";
		String fromDateMMDDDot = "";
		String toDateText = "";
		String fromDateText = "";
		int systemUserId = 0;
		int userId = 0;
		List<String> groupNames;
		String timeZone="";
		org.json.simple.JSONObject timeZoneJson;
		
		try {
				
			ReportResponse reportResponse = new ReportResponse();
			
			//Getting the users data
			userData = pdfReportDAO.getPDFReportUsersGroupData();
			
			ArrayList<HashMap<String, String>> reportGroupList;
			
			for (Map.Entry<Integer, ArrayList<HashMap<String, String>>> entry : userData.entrySet()) {
				
				groupNames = new ArrayList<String>();
				
				pdfReportJson = new JSONObject();
			    
				pdfReportJson.put("logo", "logoEA.png");
				pdfReportJson.put("footerEmail", "support@enerallies.com");
				pdfReportJson.put("footerPhone", "1-888-770-3009 x300");
				
				userId = entry.getKey();
			    reportGroupList = entry.getValue();
			    HashMap<String, String> reportGroupData;
			    
			    groupListArray = new org.json.simple.JSONArray();
			    reportLevelIds = new ArrayList<String>();
			    
			    String rootPath = System.getProperty(IMAGES_LOCATION);
			    String imageFolder = rootPath + File.separator + "PDFImages";
        		
		        File file = new File(imageFolder);
		        if (!file.exists()) {
		           file.mkdir();
		        }
		        HashMap<String, String> reportGroupDataTemp;
		        int mainDeviceCount = 0;
		        for (int i = 0; i < reportGroupList.size(); i++) {
		        	reportGroupDataTemp = reportGroupList.get(i);
			        if (Integer.parseInt(reportGroupDataTemp.get("deviceCount").toString()) > 0) {
			        	mainDeviceCount++;
			        }
			    }
		        if (reportGroupList.size() > 0) {
		        	reportGroupDataTemp = reportGroupList.get(0);
			        if (Integer.parseInt(reportGroupDataTemp.get("userReportPreference")) == 1) {
			    		reportPreferencedays = 7;
			    		daysRedirect = 7;
			    	} else if (Integer.parseInt(reportGroupDataTemp.get("userReportPreference")) == 2) {
			    		reportPreferencedays = 31;
			    		daysRedirect = 28;
			    	} else if (Integer.parseInt(reportGroupDataTemp.get("userReportPreference")) == 3) {
			    		reportPreferencedays = 90;
			    	} else {
			    		reportPreferencedays = 365;
			    	}
			        cal = Calendar.getInstance();
					cal.setTime(date);
					cal.add(Calendar.DATE, -(reportPreferencedays));
					fromDate = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", cal.getTime());
					toDate = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", date);
						
					cal.add(Calendar.DATE, +1);
					
					fromDateActivity = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", cal.getTime());
					toDateActivity = DatesUtil.getDateFormat("yyyy-MM-dd HH:mm", date);
					
					fromDateMMDD = DatesUtil.getDateFormat("MM/dd", cal.getTime());
					toDateMMDD = DatesUtil.getDateFormat("MM/dd", date);

					fromDateMMDDDot = DatesUtil.getDateFormat("MM.dd", cal.getTime());
					toDateMMDDDot = DatesUtil.getDateFormat("MM.dd", date);
					
					fromDateText = DatesUtil.getDateFormat("MMM dd, yyyy", cal.getTime());
					toDateText = DatesUtil.getDateFormat("MMM dd, yyyy", date);
			    }
				
		        if (mainDeviceCount > 0) {
			    String fileName = "PDF_"+new Date().getTime()+".pdf";
				String imageFilePath = imageFolder + File.separator + fileName;
		        Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
		        OutputStream fos = new FileOutputStream(new File(imageFilePath));
		        PdfWriter pdfWriter = PdfWriter.getInstance(doc, fos);
		        XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
		        doc.open();
		        ByteArrayInputStream is = null;
		        
		        StringBuilder mainHtmlPage;
				StringBuilder mainHtmlEmailPage = null;
				String siteIDs = "";
				
			    for (int i = 0; i < reportGroupList.size(); i++) {
			    	
			    	groupJsonObject = new org.json.simple.JSONObject();
			    	reportGroupData = reportGroupList.get(i);
			    	systemUserId  = Integer.parseInt(reportGroupData.get("loggedUser"));
			    	
			    	/*
			    	 * Getting the timezone value based on Server Address.
			    	 */
			    	Calendar now = Calendar.getInstance();
				    timeZone = now.getTimeZone().getID();
			    			
			    	if (Integer.parseInt(reportGroupData.get("deviceCount").toString()) > 0) {
				    	//Getting Manual log data
						correspondencesJsonArray = activityLogDao.getManualActivityLogData(Integer.parseInt(reportGroupData.get("userReportLevel")), Integer.parseInt(reportGroupData.get("groupId")), reportPreferencedays, "");
						logger.debug("Manual log data jsonArray @@@@@@@@@@ ::: "+ correspondencesJsonArray);
						
						ReportRequest reportRequest =  new ReportRequest();
						reportRequest.setType("GROUP");
						reportRequest.setCustomerIds(reportGroupData.get("customerId"));
						reportRequest.setGroupIds(reportGroupData.get("groupId"));
						reportRequest.setSiteIds("");
						reportRequest.setInDays(reportPreferencedays);
						reportRequest.setFromDate(fromDate);
						reportRequest.setToDate(toDate);
						
						degradedReportJsonArray = reportDao.getDegradedPerformData(reportRequest, systemUserId, timeZone);
						logger.debug("Degraded HVAC Units reportsData @@@@@@@@@@ ::: "+degradedReportJsonArray);
						
						
						withinReportJsonArray = reportDao.getWithinSetpointPerformData(reportRequest, systemUserId, timeZone);
						logger.debug("within set points reportsData @@@@@@@@@@ ::: "+withinReportJsonArray);
						
						//Getting HVAC Usage
						hvacReportJson = reportDao.getHVACUsagePerformData(reportRequest, systemUserId, timeZone);
						logger.debug("HVAC Usage reportsData @@@@@@@@@@ ::: "+hvacReportJson);
						
						//Getting Manual Changes
						manualChangesJsonArray = reportDao.getManualChangesPerformData(reportRequest, systemUserId, timeZone);
						logger.debug("Manual Changes reportsData @@@@@@@@@@ :::  "+manualChangesJsonArray);
						
						//Getting Critical issues
						reportsBarJsonArray = reportDao.getCriticalIssues(reportRequest, systemUserId, timeZone);
						logger.debug("Critical issues reportsData  @@@@@@@@@@ :::  "+reportsBarJsonArray);
						

						//Getting sites labels
						ReportRequest reportRequestLable =  new ReportRequest();
						reportRequestLable.setType("GROUP");
						reportRequestLable.setReportType("1");
						reportRequestLable.setCustomerIds(reportGroupData.get("customerId"));
						reportRequestLable.setGroupIds(reportGroupData.get("groupId"));
						reportRequestLable.setSiteIds("");
						reportRequestLable.setInDays(reportPreferencedays);
						reportRequestLable.setFromDate(fromDate);
						reportRequestLable.setToDate(toDate);
						degradedReportJsonArrayLable = reportDao.getReportData(reportRequestLable, systemUserId, timeZone);
						logger.debug("Degraded HVAC Units reportsData @@@@@@@@@@ ::: "+degradedReportJsonArrayLable);
						
						reportRequestLable.setReportType("2");
						withinReportJsonArrayLable = reportDao.getReportData(reportRequestLable, systemUserId, timeZone);
						logger.debug("within set points reportsData @@@@@@@@@@ ::: "+withinReportJsonArrayLable);
						
						reportRequestLable.setReportType("3");
						hvacReportJsonLable = reportDao.getHVACUsageReport(reportRequestLable, systemUserId, timeZone);
						logger.debug("HVAC Usage reportsData @@@@@@@@@@ ::: "+hvacReportJsonLable);
						
						reportRequestLable.setReportType("4");
						manualChangesJsonArrayLable = reportDao.getReportData(reportRequestLable, systemUserId, timeZone);
						logger.debug("Manual Changes reportsData @@@@@@@@@@ :::  "+manualChangesJsonArrayLable);
						
						
						//Critical issues alerts
						AlertRequest alertRequest = new AlertRequest();
						alertRequest.setIsEai(1);
						alertRequest.setUserId(systemUserId);
						alertRequest.setIsSuper(1);
						alertRequest.setTimeZone(timeZone);
						alertRequest.setAlertStatus("open");
						alertRequest.setFromCurrentPage("groups");
						alertRequest.setSpecificId(reportGroupData.get("groupId"));
						alertRequest.setTimePeriodInDays(reportPreferencedays);
						alertRequest.setPriority("P1");
						alertRequest.setPdfReportalertIds("");
						Response response1 = alertDao.getDashboardAlerts(alertRequest);
						logger.debug("Critical issues alerts data  @@@@@@@@@@ :::  "+(org.json.simple.JSONObject) response1.getData());
						org.json.simple.JSONObject alertsJson = (org.json.simple.JSONObject) response1.getData();
						criticalIssuesJsonArray = (org.json.simple.JSONArray) alertsJson.get("alertsInfo");
						
						//Resolved issues alerts
						alertRequest = new AlertRequest();
						alertRequest.setIsEai(1);
						alertRequest.setUserId(systemUserId);
						alertRequest.setIsSuper(1);
						alertRequest.setTimeZone(timeZone);
						alertRequest.setAlertStatus("resolve");
						alertRequest.setFromCurrentPage("groups");
						alertRequest.setSpecificId(reportGroupData.get("groupId"));
						alertRequest.setTimePeriodInDays(reportPreferencedays);
						alertRequest.setPriority("");
						alertRequest.setPdfReportalertIds("1,2,5");
						response1 = alertDao.getDashboardAlerts(alertRequest);
						logger.debug("Resolved issues alerts data  @@@@@@@@@@ :::  "+ (org.json.simple.JSONObject) response1.getData());
						org.json.simple.JSONObject resolvedJson = (org.json.simple.JSONObject) response1.getData();
						resolvedIssuesJsonArray = (org.json.simple.JSONArray) resolvedJson.get("alertsInfo");
				    	
						/*
						 * preparing group json object for pdf report
						 * */
						
						groupJsonObject.put("logo", reportGroupData.get("companyLogo"));
						groupJsonObject.put("companyName", reportGroupData.get("companyName"));
						groupJsonObject.put("groupId", reportGroupData.get("groupId"));
						groupJsonObject.put("groupName", reportGroupData.get("groupName"));
						groupJsonObject.put("customerId", reportGroupData.get("customerId"));
				    	groupJsonObject.put("reportType", reportGroupData.get("userReportPreferenceText") + " Report");
				    	groupJsonObject.put("comfortOpt", reportGroupData.get("comfortOpt"));
				    	groupJsonObject.put("deviceCount", reportGroupData.get("deviceCount"));
				    	groupJsonObject.put("toDate", DatesUtil.getDateFormat("MMM dd, yyyy", date));
				    	groupJsonObject.put("fromDate", DatesUtil.getDateFormat("MMM dd, yyyy", cal.getTime()));
				    	groupJsonObject.put("siteIds", reportGroupData.get("siteIds"));
				    	
				    	if(siteIDs.isEmpty()) {
				    		siteIDs = reportGroupData.get("siteIds");
		    			} else {
		    				siteIDs = siteIDs +","+ reportGroupData.get("siteIds");
		    			}
				    	
				    	groupJsonObject.put("degradedReportLable", degradedReportJsonArrayLable);
				    	groupJsonObject.put("withinReportLable", withinReportJsonArrayLable);
				    	groupJsonObject.put("hvacReportLable", hvacReportJsonLable);
				    	groupJsonObject.put("manualChangesLable", manualChangesJsonArrayLable);
				    	
				    	groupJsonObject.put("degradedReport", degradedReportJsonArray);
				    	groupJsonObject.put("withinReport", withinReportJsonArray);
				    	groupJsonObject.put("hvacReport", hvacReportJson);
				    	groupJsonObject.put("manualChanges", manualChangesJsonArray);
				    	groupJsonObject.put("reports", reportsBarJsonArray);
				    	groupJsonObject.put("criticalIssues", criticalIssuesJsonArray);
				    	groupJsonObject.put("resolvedIssues", resolvedIssuesJsonArray);
				    	groupJsonObject.put("correspondences", correspondencesJsonArray);
			    	
			    		groupNames.add(reportGroupData.get("groupName"));
			    		reportLevelIds.add(reportGroupData.get("groupId"));
			    	
			    		groupListArray.add(groupJsonObject);
			    	
			    		reportLevel = Integer.parseInt(reportGroupData.get("userReportLevel"));
			    		reportPreference = Integer.parseInt(reportGroupData.get("userReportPreference"));
			    		userEmail = reportGroupData.get("userEmail");
			    		userFirstName = reportGroupData.get("userFname");
			    		userLastName = reportGroupData.get("userLname");
			    		reportLevelText = reportGroupData.get("userReportLevelText");
			    		reportPreferenceText = reportGroupData.get("userReportPreferenceText");
			    	}
			    }
			    List<String> siteIdsArray = Arrays.asList(siteIDs.split(","));
			    int[] myInts = new int[siteIdsArray.size()];
		    	for (int i = 0; i < siteIdsArray.size(); i++) {	
		    		myInts[i] = Integer.parseInt(siteIdsArray.get(i).trim());
		    	}
		    	
		    	List<Integer> numberList = new ArrayList<Integer>();
		    	for (int i : myInts) {
		    		if (!numberList.contains(i)) {
		    			numberList.add(i);
		    		}
		    	}
		    	String uniqueSiteIDs = StringUtils.join(numberList, ',');
			    pdfReportJson.put("groupList", groupListArray);
			    JSONArray jsonArray = pdfReportJson.getJSONArray("groupList");
			    int optCount = 0;
			    int groupDeviceCount = 0;
			    Set<String> customerNames = new HashSet<String>();
			    Set<String> customerLogos = new HashSet<String>();
			    for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject groupOptObject = jsonArray.getJSONObject(i);
					if (groupOptObject.getString("comfortOpt").equalsIgnoreCase("1") && Integer.parseInt(groupOptObject.getString("deviceCount")) > 0) {
						optCount++;
					}
					if (Integer.parseInt(groupOptObject.getString("deviceCount")) > 0) {
						groupDeviceCount++;
						customerNames.add(groupOptObject.getString("companyName"));
						customerLogos.add(groupOptObject.getString("logo"));
					}
			    }
			    String logo = "";
			    String customerName = "";
			    if (customerNames.size() == 1) {
			    	for (Iterator<String> it = customerNames.iterator(); it.hasNext();) {
			    		customerName = it.next();
			    	    break;
			    	}
			    }
			    if (customerNames.size() == 1) {
			    	for (Iterator<String> it = customerLogos.iterator(); it.hasNext();) {
			    		logo = it.next();
			    	    break;
			    	}
			    }
			    String chartTitleFirst = "";
			    String chartTitleSecond = "";
			    String chartTitleThird = "";
			    
			    for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject groupObject = jsonArray.getJSONObject(i);
					JSONArray reportsData = groupObject.getJSONArray("reports");
					for(int g = 0; g < reportsData.length(); g++) {
						JSONObject criticalObject = (JSONObject) reportsData.get(g);
						if (g == 0) {
							chartTitleFirst = criticalObject.getString("name");
						}
						if (g == 1) {
							chartTitleSecond = criticalObject.getString("name");
						}
						if (g == 2) {
							chartTitleThird = criticalObject.getString("name");
						}
					}
					
					if (i==0) {
						break;
					}
				}
			    
			    if (reportLevelIds.size() > 1) {
			    	
					mainHtmlPage = new StringBuilder();
			
					mainHtmlPage.append("<!DOCTYPE html><html lang='en'><head>");
					mainHtmlPage.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
					mainHtmlPage.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
							+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
					
					mainHtmlPage.append("<style>"
							+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:14; margin:0;} .bodyText{font-size:11; color:#000000;} .bodyTextHeader{font-size:11; color:#000000;}"
							+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
							+ ".loginBtn{padding-left:15; float:left; padding-right:15; padding-top:15; padding-bottom:15; background:#1a6394; color:#fff; font-size:14; text-decoration:none; border-radius:5;}"
							+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15; padding-top:15;}"
							+ ".chartTitle{font-weight:bold; font-size:10; color:black; text-align:center; text-transform:uppercase;}"
							+ ".spacer{height:10px;}.spacertr{margin-left:12px;margin-right:12px;display:table;}"
							+ ".onlyBlueBg{background:#939393; color:#000000;} .whiteBg{background:#FFFFFF;color:#000000;} p{margin:0;}"
							+ "</style></head><body>");
					
					mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0' class='mainTable'>");
						mainHtmlPage.append("<thead>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>&nbsp;&nbsp;&nbsp;<img src='"+url+"/fileUpload/loadPDFReportImages?imageName="+pdfReportJson.getString("logo")+"' width='200'></img></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("</thead>");
					mainHtmlPage.append("<tbody>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='blue-bg'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
					mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td>");
							mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								mainHtmlPage.append("<tr class='spacertr'>");
								if (StringUtils.isNotEmpty(logo)) {
									mainHtmlPage.append("<td>");
										mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+logo+"'></img>");
									mainHtmlPage.append("</td>");
								}
								if (StringUtils.isNotEmpty(logo)) {
									mainHtmlPage.append("<td>");
										mainHtmlPage.append("<h3>"+customerName + " " + reportLevelText + "s " + reportPreferenceText + " Report</h3>");
										mainHtmlPage.append("<span class='bodyText'>"+fromDateText +" to " + toDateText+"</span>");
									mainHtmlPage.append("</td>");									
								} else {
									mainHtmlPage.append("<td>");
										mainHtmlPage.append("<h3>&nbsp;&nbsp;&nbsp;&nbsp;"+customerName + " " + reportLevelText + "s " + reportPreferenceText + " Report</h3>");
										mainHtmlPage.append("<span class='bodyText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+fromDateText +" to " + toDateText+"</span>");
									mainHtmlPage.append("</td>");
								}
								if (optCount > 0) {
									mainHtmlPage.append("<td align='right'>");
										mainHtmlPage.append("<h3>Comfort Optimization On</h3>");
									mainHtmlPage.append("</td>");
									mainHtmlPage.append("<td width='30'>");
										mainHtmlPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
									mainHtmlPage.append("</td>");
								}
								mainHtmlPage.append("</tr>");
							mainHtmlPage.append("</table>");
						mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='grey-bg'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr class='spacertr'>");
						mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Groups Performance Summary</h3></td>");
					mainHtmlPage.append("</tr>");				
					mainHtmlPage.append("<tr class='spacertr'>");
						mainHtmlPage.append("<td style='padding:20;'>");
							mainHtmlPage.append("<table width='100%' border='0' class='onlyBlueBg' cellpadding='5' cellspacing='2'>");
								mainHtmlPage.append("<tr>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'>&nbsp;</td>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Degraded HVAC Units</b></p></td>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Within Setpoints</b></p></td>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>HVAC Usage</b></p></td>");
									mainHtmlPage.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Manual Changes</b></p></td>");
								mainHtmlPage.append("</tr>");
								
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject groupObject = jsonArray.getJSONObject(i);
									
									if (groupObject.getInt("deviceCount") > 0) {
										JSONArray degradedReportJson = groupObject.getJSONArray("degradedReportLable");
										JSONArray withinReportJson = groupObject.getJSONArray("withinReportLable");
										JSONObject hvacReportJsons = groupObject.getJSONObject("hvacReportLable");
										JSONArray manualReportJson = groupObject.getJSONArray("manualChangesLable");
										List<String> degradedReport = new ArrayList<String>();
										List<String> withinReport = new ArrayList<String>();
										List<String> hvacReport = new ArrayList<String>();
										List<String> manualReport = new ArrayList<String>();
										
										for (int degraded = 0; degraded < degradedReportJson.length(); degraded++) {
											JSONObject degradedObj = degradedReportJson.getJSONObject(degraded);
											degradedReport.add(degradedObj.getString("name").trim());
										}
										for (int within = 0; within < withinReportJson.length(); within++) {
											JSONObject withinObj = withinReportJson.getJSONObject(within);
											withinReport.add(withinObj.getString("name").trim());
										}
										for (int manual = 0; manual < manualReportJson.length(); manual++) {
											JSONObject manualObj = manualReportJson.getJSONObject(manual);
											manualReport.add(manualObj.getString("name").trim());
										}
										for (int hvac = 0; hvac < hvacReportJsons.getJSONArray("categories").length(); hvac++) {
											JSONArray hvacReportJsonArray = hvacReportJsons.getJSONArray("data").getJSONObject(hvac).getJSONObject("drilldown").getJSONArray("categories");
											JSONArray hvacReportJsonDataArray = hvacReportJsons.getJSONArray("data").getJSONObject(hvac).getJSONObject("drilldown").getJSONArray("data");
											for (int k = 0; k < hvacReportJsonArray.length(); k++) {
												if (Integer.parseInt(hvacReportJsonDataArray.get(k).toString()) > 0) {
													hvacReport.add(hvacReportJsonArray.get(k).toString());
												}
											}
										}
										
										if (degradedReportJson.length() > 0 || withinReportJson.length() > 0 || manualReportJson.length() > 0 || hvacReportJsons.getJSONArray("categories").length() > 0) {
											mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td style='color:#1a6394' class='whiteBg'><p class='bodyText'><a href='"+url+"/dashboard#?pdfFlag=dashboard&pdfValue="+groupObject.getString("customerId")+"-"+groupObject.getString("groupId")+"-0-"+daysRedirect+"' style='color:#1a6394;'>"+groupObject.get("groupName")+"</a></p></td>");
												mainHtmlPage.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(degradedReport, ", ")+"</p></td>");
												mainHtmlPage.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(withinReport, ", ")+"</p></td>");
												mainHtmlPage.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(hvacReport, ", ")+"</p></td>");
												mainHtmlPage.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(manualReport, ", ")+"</p></td>");
											mainHtmlPage.append("</tr>");
										}
									}
								}	
							mainHtmlPage.append("</table>");
						mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					int allCritical = 0;
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject groupObject = jsonArray.getJSONObject(i);
						JSONArray reportsData = groupObject.getJSONArray("reports");
						for(int g = 0; g < reportsData.length(); g++) {
							JSONObject criticalObject = (JSONObject) reportsData.get(g);
							JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
							if (criticalY.getInt("y") != 0) {
								allCritical++;
							}
						}
					}
					if (allCritical > 0) {
						mainHtmlPage.append("<tr class='spacertr'>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Groups with Current Critical Issues Summary</h3></td>");
						mainHtmlPage.append("</tr>");				
						mainHtmlPage.append("<tr class='spacertr'>");
							mainHtmlPage.append("<td style='padding:20'>");
								mainHtmlPage.append("<table width='100%' border='0' class='onlyBlueBg' cellpadding='5' cellspacing='2'>");
									mainHtmlPage.append("<tr>");
										mainHtmlPage.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>&nbsp;</b></p></td>");
										mainHtmlPage.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleFirst +"</b></p></td>");
										mainHtmlPage.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleSecond +"</b></p></td>");
										mainHtmlPage.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleThird +"</b></p></td>");
									mainHtmlPage.append("</tr>");
									
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject groupObject = jsonArray.getJSONObject(i);
										JSONArray reportsData = groupObject.getJSONArray("reports");
										int countCritical = 0;
										StringBuilder tempHtml = new StringBuilder();
										
										tempHtml.append("<tr>");
										tempHtml.append("<td style='color:#1a6394' class='whiteBg'><p class='bodyText'><a href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+groupObject.getString("customerId")+"-"+groupObject.getString("groupId")+"-"+reportLevelText+"' style='color:#1a6394;'>"+groupObject.get("groupName")+"</a></p></td>");
											for(int g = 0; g < reportsData.length(); g++) {
												JSONObject criticalObject = (JSONObject) reportsData.get(g);
												JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
												tempHtml.append("<td class='whiteBg'><p class='bodyText'>"+criticalY.getInt("y")+"</p></td>");
												if (criticalY.getInt("y") != 0) {
													countCritical++;
												}
											}
										tempHtml.append("</tr>");
										
										if (countCritical > 0) {
											mainHtmlPage.append(tempHtml);
										}
									}	
								mainHtmlPage.append("</table>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");
					}

					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("</tbody>");
					mainHtmlPage.append("</table>");
					mainHtmlPage.append("</body></html>");
					/*Email Design*/
		        	is = new ByteArrayInputStream(mainHtmlPage.toString().getBytes());
				    worker.parseXHtml(pdfWriter, doc, is);
				    if (reportLevelIds.size() > 1 && reportLevelIds.size() <= 10) {
			        	doc.newPage();
			        }
				}
			

			    for (int i = 0; i < jsonArray.length(); i++) {
				
			    	JSONObject objectInArray = jsonArray.getJSONObject(i);
				
					mainHtmlPage = new StringBuilder();
					mainHtmlEmailPage = new StringBuilder();
					
					if (reportLevelIds.size() <= 10) {
				
				/*PDF Design*/
				if (objectInArray.getInt("deviceCount") > 0) {

					mainHtmlPage.append("<!DOCTYPE html><html lang='en'><head>");
					mainHtmlPage.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
					mainHtmlPage.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
							+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
					
					mainHtmlPage.append("<style>"
							+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:14; margin:0;} .bodyText{font-size:11;}"
							+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
							+ ".loginBtn{padding-left:15; float:left; padding-right:15; padding-top:15; padding-bottom:15; background:#1a6394; color:#fff; font-size:14; text-decoration:none; border-radius:5;}"
							+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15; padding-top:15;}"
							+ ".chartTitle{font-weight:bold; font-size:10; color:black; text-align:center; text-transform:uppercase;}"
							+ ".spacer{height:10px;} "
							+ "</style></head><body>");
					
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
					mainHtmlEmailPage.append("<!DOCTYPE html><html lang='en'><head>");
					mainHtmlEmailPage.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
					mainHtmlEmailPage.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
							+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
					
					mainHtmlEmailPage.append("<style>"
							+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:18px; margin:0;} .bodyText{font-size:14px;}"
							+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
							+ ".loginBtn{padding:15px; float:left; background:#1a6394; color:#fff; font-size:18px; text-decoration:none; border-radius:5px;}"
							+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15px; padding-top:15px;}"
							+ ".chartTitle{font-weight:bold; font-size:10px; color:black; text-align:center; text-transform:uppercase;}"
							+ ".spacer{height:10px}.spacertr{margin-left:12px;margin-right:12px;display:table;}"
							+ ".grayborder{background:#EEEEEE;display:block;position:relative;width:100%;margin-top:20px;margin-bottom:20px;}"
							+ "</style></head><body class='grayborder'>");
					}
					
					/*PDF Design*/
					mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0' class='mainTable'>");
						mainHtmlPage.append("<thead>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>&nbsp;&nbsp;<img src='"+url+"/fileUpload/loadPDFReportImages?imageName="+pdfReportJson.getString("logo")+"' width='200'></img></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("</thead>");
					mainHtmlPage.append("<tbody>");
					
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
						mainHtmlEmailPage.append("<div style='background: #DDDDDD;padding: 30px 0;'><table width='920' align='center' border='0' cellpadding='0' bgcolor='#FFFFFF' cellspacing='0' class='mainTable'>");
						mainHtmlEmailPage.append("<thead>");
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td><img src='"+url+"/fileUpload/loadPDFReportImages?imageName=logoEA.png' width='200'></img></td>");
							mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("</thead>");
						mainHtmlEmailPage.append("<tbody>");
					}
					
					/*PDF Design*/
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='blue-bg'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td>");
							mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								mainHtmlPage.append("<tr>");
									if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
										mainHtmlPage.append("<td>");
											mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+objectInArray.getString("logo")+"'></img>");
										mainHtmlPage.append("</td>");
									}
									if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
										mainHtmlPage.append("<td>");
											mainHtmlPage.append("<h3>"+objectInArray.getString("groupName") + " " + objectInArray.getString("reportType")+"</h3>");
											mainHtmlPage.append("<span class='bodyText'>"+fromDateText + " to " + toDateText+"</span>");
										mainHtmlPage.append("</td>");
									} else {
										mainHtmlPage.append("<td>");
											mainHtmlPage.append("<h3>&nbsp;&nbsp;&nbsp;"+objectInArray.getString("groupName") + " " + objectInArray.getString("reportType")+"</h3>");
											mainHtmlPage.append("<span class='bodyText'>&nbsp;&nbsp;&nbsp;&nbsp;"+fromDateText + " to " + toDateText+"</span>");
										mainHtmlPage.append("</td>");
									}
									if (objectInArray.getString("comfortOpt").equalsIgnoreCase("1")) {
										mainHtmlPage.append("<td align='right'>");
											mainHtmlPage.append("<h3>Comfort Optimization On</h3>");
										mainHtmlPage.append("</td>");
										mainHtmlPage.append("<td width='30'>");
											mainHtmlPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
										mainHtmlPage.append("</td>");
									}
								mainHtmlPage.append("</tr>");
							mainHtmlPage.append("</table>");
						mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='grey-bg'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a href='"+url+"/dashboard#?pdfFlag=dashboard&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+daysRedirect+"' style='color:#1a6394;text-decoration: underline;'>Group Performance</a></h3></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td>");
						
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='blue-bg'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td>");
							mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								mainHtmlEmailPage.append("<tr>");
									if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
										mainHtmlEmailPage.append("<td>");
											mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+objectInArray.getString("logo")+"'></img>");
										mainHtmlEmailPage.append("</td>");
									}
									if (StringUtils.isNotEmpty(objectInArray.getString("logo"))) {
										mainHtmlEmailPage.append("<td>");
										mainHtmlEmailPage.append("<h3>"+objectInArray.getString("groupName") + " " + objectInArray.getString("reportType")+"</h3>");
										mainHtmlEmailPage.append("<span class='bodyText'>"+fromDateText + " to " + toDateText+"</span>");
										mainHtmlEmailPage.append("</td>");
									} else {
										mainHtmlEmailPage.append("<td>");
										mainHtmlEmailPage.append("<h3>&nbsp;&nbsp;&nbsp;"+objectInArray.getString("groupName") + " " + objectInArray.getString("reportType")+"</h3>");
										mainHtmlEmailPage.append("<span class='bodyText'>&nbsp;&nbsp;&nbsp;&nbsp;"+fromDateText + " to " + toDateText+"</span>");
										mainHtmlEmailPage.append("</td>");
									}
									if (objectInArray.getString("comfortOpt").equalsIgnoreCase("1")) {
										mainHtmlEmailPage.append("<td align='right'>");
										mainHtmlEmailPage.append("<h3>Comfort Optimization On</h3>");
										mainHtmlEmailPage.append("</td>");
										mainHtmlEmailPage.append("<td width='30'>");
										mainHtmlEmailPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
										mainHtmlEmailPage.append("</td>");
									}
									mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("</table>");
						mainHtmlEmailPage.append("</td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='grey-bg'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr class='spacertr'>");
						mainHtmlEmailPage.append("<td><h3><a href='"+url+"/dashboard#?pdfFlag=dashboard&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+daysRedirect+"' style='color:#1a6394;text-decoration: underline;'>Group Performance</a></h3></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr class='spacertr'>");
						mainHtmlEmailPage.append("<td>");
					}
					JSONArray reportsData = objectInArray.getJSONArray("reports");
					JSONObject degradedReportJson = objectInArray.getJSONObject("degradedReport");
					JSONObject withinReportJson = objectInArray.getJSONObject("withinReport");
					JSONObject manualReportJson = objectInArray.getJSONObject("manualChanges");
					JSONObject hvacReportJsons = objectInArray.getJSONObject("hvacReport");
					
						/*PDF Design*/
						mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
							mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td width='25%'>");
								String chart1 = getHighChartData(getSolidGuageChart(degradedReportJson, "DEGRADED HVAC UNITS"), "DEGRADED", folderName);
								mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName + "&imageName=").append(chart1+"'></img>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("<td width='25%'>");
								String chart2 = getHighChartData(getSolidGuageChart(withinReportJson, "WITHIN SETPOINTS"), "WITHIN", folderName);
								mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName + "&imageName=").append(chart2+"'></img>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("<td width='25%'>");
								String chart3 = getHighChartData(getSolidGuageChart(hvacReportJsons, "HVAC USAGE"), "HVAC", folderName);
								mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+ folderName + "&imageName=").append(chart3+"'></img>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("<td width='25%'>");
								String chart4 = getHighChartData(getSolidGuageChart(manualReportJson, "MANUAL CHANGES"), "MANUAL", folderName);
								mainHtmlPage.append("<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+ folderName + "&imageName=").append(chart4+"'></img>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("</tr>");
							mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td colspan='4' class='spacer'></td>");
							mainHtmlPage.append("</tr>");
							mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td valign='middle' align='center'>");
								mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DEGRADED HVAC UNITS</p>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("<td valign='middle' align='center'>");
								mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;WITHIN SETPOINTS</p>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("<td valign='middle' align='center'>");
								mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;HVAC USAGE</p>");
							mainHtmlPage.append("</td>");
							mainHtmlPage.append("<td valign='middle' align='center'>");
								mainHtmlPage.append("<p class='chartTitle'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MANUAL CHANGES</p>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("</table>");
					mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='grey-bg2'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#000000;cursor: auto;text-decoration: underline;'>Current Critical Issues</a></h3></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
					mainHtmlPage.append("<td>");
					
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
					mainHtmlEmailPage.append("<table width='1024' border='0' cellpadding='0' cellspacing='0'>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td>");
								mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getSolidGuageChart(degradedReportJson, "DEGRADED HVAC UNITS"))+"' width='256'></img>");
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td>");
								mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getSolidGuageChart(withinReportJson, "WITHIN SETPOINTS"))+"' width='256'></img>");
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td>");
								mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getSolidGuageChart(hvacReportJsons, "HVAC USAGE"))+"' width='256'></img>");
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td>");
								mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getSolidGuageChart(manualReportJson, "MANUAL CHANGES"))+"' width='256'></img>");
							mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td colspan='4' class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td width='25%'>");
								mainHtmlEmailPage.append("<p class='chartTitle'>DEGRADED HVAC UNITS</p>");
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td width='25%'>");
								mainHtmlEmailPage.append("<p class='chartTitle'>WITHIN SETPOINTS</p>");
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td width='25%'>");
								mainHtmlEmailPage.append("<p class='chartTitle'>HVAC USAGE</p> ");
							mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("<td width='25%'>");
								mainHtmlEmailPage.append("<p class='chartTitle'>MANUAL CHANGES</p>");
							mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("</table>");
					mainHtmlEmailPage.append("</td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='grey-bg2'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr class='spacertr'>");
						mainHtmlEmailPage.append("<td><h3 style='color:#000000;text-decoration: underline;'>Current Critical Issues</h3><br/></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td>");
					}
					int count = 0;
					for(int g = 0; g < reportsData.length(); g++) {
						JSONObject criticalObject = (JSONObject) reportsData.get(g);
						JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
						if (criticalY.getInt("y") > 0) {
							count++;	
						}
					}
					if (count > 0) {
						String column = getHighChartData(getColumnChart(reportsData), "COLUMN", folderName);
						mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img src='"+url+"/fileUpload/loadPDFReportChart?folderName="+folderName+"&imageName=").append(column+"' width='300'></img>");
						if (reportLevelIds.size() == 1) {
							mainHtmlEmailPage.append("<img src='"+url+"/fileUpload/getHighChartData?data=").append(URLEncoder.encode(getColumnChart(reportsData))+"' width='400'/>");
						}
					} else {
						mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=noCriticalIssues.png' width='300'></img>");
						if (reportLevelIds.size() == 1) {
							mainHtmlEmailPage.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=noCriticalIssues.png' width='400'/>");
						}
					}
					
					/*PDF Design*/
					mainHtmlPage.append("</td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='grey-bg2'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					
					JSONArray criticalData = objectInArray.getJSONArray("criticalIssues");
					
					if (criticalData.length() > 0) {
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+reportLevelText+"'>Current Critical Issues</a></h3></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
					}
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
					mainHtmlEmailPage.append("</td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='spacer'></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr>");
						mainHtmlEmailPage.append("<td class='grey-bg2'></td>");
					mainHtmlEmailPage.append("</tr>");
						if (criticalData.length() > 0) {
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td><br/><h3><a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+reportLevelText+"'>Current Critical Issues</a></h3><br/></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
						}
					}
					
					if (criticalData.length() > 0) {
						/*PDF Design*/
						/*mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>");
								mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								
								*/
						/*Email Design*/
						if (reportLevelIds.size() == 1) {
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td>");
								mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
						}
								boolean temp = false;
								for (int j = 0; j < criticalData.length(); j++) {
									
									JSONObject objectInCritical = criticalData.getJSONObject(j);
									String alertMsg = "";
									try{
										alertMsg = objectInCritical.getString("alertMessage");
									}catch(JSONException e){
										alertMsg = "";
									}
									JSONArray actionItems = objectInCritical.getJSONArray("actionItems");
									/*PDF Design*/
									mainHtmlPage.append("<tr>");
										mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
											"&nbsp;&nbsp;&nbsp;<b>"+ (j + 1) +". "+ objectInCritical.getString("alertName") + " " + objectInCritical.getString("alertProrityName") + "</b> - "+alertMsg+" <br></br>"+
											"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("customerName") + (StringUtils.isNotEmpty(objectInCritical.getString("groupName")) ? ", " : "") + objectInCritical.getString("groupName") + "<br></br>"+
											"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("siteName") + ", "+ objectInCritical.getString("deviceName") + "<br></br>");
											if (actionItems.length() > 0) {
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Action List<br></br>");
											}
										
										/*Email Design*/	
										if (reportLevelIds.size() == 1) {
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
														"<b>"+ (j + 1) +". "+ objectInCritical.getString("alertName") + " " + objectInCritical.getString("alertProrityName") + "</b> - "+alertMsg+"<br/>"+
													"&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("customerName") + ", "+ objectInCritical.getString("groupName") + "<br/>"+
													"&nbsp;&nbsp;&nbsp;&nbsp;"+ objectInCritical.getString("siteName") + ", "+ objectInCritical.getString("deviceName") + "<br/>");
											if (actionItems.length() > 0) {
												mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;&nbsp;- Action List<br/>");
											}
										}
											
										for (int k = 0; k < actionItems.length(); k++) {
											JSONObject actionItemsObj = actionItems.getJSONObject(k);
											if (actionItemsObj.getInt("itemStatus") == 1) {
												/*PDF Design*/
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strike style='text-decoration: line-through'>"+actionItemsObj.getString("itemName")+"</strike><br></br>");
												/*Email Design*/	
												mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strike style='text-decoration: line-through'>"+actionItemsObj.getString("itemName")+"</strike><br/>");
											} else {
												/*PDF Design*/
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+actionItemsObj.getString("itemName")+"<br></br>");
												/*Email Design*/	
												mainHtmlEmailPage.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+actionItemsObj.getString("itemName")+"<br/>");
											}
											
									    }
									/*PDF Design*/
										mainHtmlPage.append("</p></td>");
									mainHtmlPage.append("</tr>");
									
									/*Email Design*/
									if (reportLevelIds.size() == 1) {
										mainHtmlEmailPage.append("</p></td>");
										mainHtmlEmailPage.append("</tr>");
										mainHtmlEmailPage.append("<tr>");
											mainHtmlEmailPage.append("<td class='spacer'></td>");
										mainHtmlEmailPage.append("</tr>");
									}
									if (j == 4) {
										temp = true;
										break;
									}
								}
								if (temp) {
									/*PDF Design*/			
									mainHtmlPage.append("<tr>");
										mainHtmlPage.append("<td><p class='bodyText text-indent'>" +
															"&nbsp;&nbsp;&nbsp;Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+reportLevelText+"'>here</a> for more issues");
										mainHtmlPage.append("</p></td>");
									mainHtmlPage.append("</tr>");
									if (reportLevelIds.size() == 1) {
									/*Email Design*/			
									mainHtmlEmailPage.append("<tr>");
										mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>" +
														"Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+reportLevelText+"'>here</a> for more issues");
										mainHtmlEmailPage.append("</p></td>");
									mainHtmlEmailPage.append("</tr>");
									}
								}
								/*PDF Design*/			
								/*mainHtmlPage.append("</table>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");*/
						
						/*Email Design*/	
						if (reportLevelIds.size() == 1) {
								mainHtmlEmailPage.append("</table>");
							mainHtmlEmailPage.append("</td>");
						mainHtmlEmailPage.append("</tr>");
						}
					}
					/*PDF Design*/
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					JSONArray resolvedData = objectInArray.getJSONArray("resolvedIssues");
					if (resolvedData.length() > 0) {
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+reportLevelText+"'>Resolved Issues</a></h3></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
					}
					/*Email Design*/
					if (reportLevelIds.size() == 1 && resolvedData.length() > 0) {
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><h3><a style='color:#1a6394;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+reportLevelText+"'>Resolved Issues</a></h3><br/></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
					}
				
					if (resolvedData.length() > 0) {
						/*PDF Design*/
						/*mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>");
								mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");*/
						/*Email Design*/	
							if (reportLevelIds.size() == 1) {
								mainHtmlEmailPage.append("<tr class='spacertr'>");
									mainHtmlEmailPage.append("<td>");
										mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
							}				
									boolean rTemp = false;
									for (int m = 0; m < resolvedData.length(); m++) {
										JSONObject objectResolved = resolvedData.getJSONObject(m);
										String alertMsg = "";
										try{
											alertMsg = objectResolved.getString("alertMessage");
										}catch(JSONException e){
											alertMsg = "";
										}
										/*PDF Design*/
										mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
													"&nbsp;&nbsp;&nbsp;<b>"+ (m + 1) +". "+ objectResolved.getString("alertName") + " " + objectResolved.getString("alertProrityName") + "</b> - " + alertMsg);
											mainHtmlPage.append("</p></td>");
										mainHtmlPage.append("</tr>");
										/*Email Design*/
										if (reportLevelIds.size() == 1) {
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
														"<b>"+ (m + 1) +". "+ objectResolved.getString("alertName") + " " + objectResolved.getString("alertProrityName") + "</b> - " + alertMsg);
												mainHtmlEmailPage.append("</p></td>");
											mainHtmlEmailPage.append("</tr>");
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td class='spacer'></td>");
											mainHtmlEmailPage.append("</tr>");
										}
										if (m == 4) {
											rTemp = true;
											break;
										}
								    }
									if (rTemp) {
										/*PDF Design*/
										mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
														"&nbsp;&nbsp;&nbsp;Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+reportLevelText+"'>here</a> for more issues");
											mainHtmlPage.append("</p></td>");
										mainHtmlPage.append("</tr>");
										/*Email Design*/
										if (reportLevelIds.size() == 1) {
										mainHtmlEmailPage.append("<tr>");
											mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
														"Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=alert-resolved&pdfValue="+objectInArray.getString("customerId")+"-"+objectInArray.getString("groupId")+"-0-"+reportLevelText+"'>here</a> for more issues");
											mainHtmlEmailPage.append("</p></td>");
										mainHtmlEmailPage.append("</tr>");
										}
									}
								/*PDF Design*/
								/*mainHtmlPage.append("</table>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");*/
						/*Email Design*/
						if (reportLevelIds.size() == 1) {
							mainHtmlEmailPage.append("</table>");
						mainHtmlEmailPage.append("</td>");
					mainHtmlEmailPage.append("</tr>");
						}
					}
					JSONArray correspondences = objectInArray.getJSONArray("correspondences");
					
					/*PDF Design*/
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					if (correspondences.length() > 0) {
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td><h3>&nbsp;&nbsp;&nbsp;<a style='color:#000000;cursor: auto;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=activity'>Correspondences</a></h3></td>");
						mainHtmlPage.append("</tr>");
						mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td class='spacer'></td>");
						mainHtmlPage.append("</tr>");
					}
					
					/*Email Design*/
					if (reportLevelIds.size() == 1 && correspondences.length() > 0) {
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><h3><a style='color:#000000;cursor: auto;text-decoration: underline;' href='"+url+"/dashboard#?pdfFlag=activity'>Correspondences</a></h3><br/></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
					}
				
					if (correspondences.length() > 0) {
						/*PDF Design*/
						/*mainHtmlPage.append("<tr>");
							mainHtmlPage.append("<td>");
								mainHtmlPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");*/
							/*Email Design*/
							if (reportLevelIds.size() == 1) {
							mainHtmlEmailPage.append("<tr class='spacertr'>");
								mainHtmlEmailPage.append("<td>");
									mainHtmlEmailPage.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
							}
									boolean cTemp = false;
									for (int c = 0; c < correspondences.length(); c++) {	
										JSONObject correspondencesObject = correspondences.getJSONObject(c);
										/*PDF Design*/
										mainHtmlPage.append("<tr>");
											String temp = "";
											if (correspondencesObject.getString("alAction").equalsIgnoreCase("called")) {
												temp = "Telephone Call";
											} else if (correspondencesObject.getString("alAction").equalsIgnoreCase("emailed")) {
												temp = "Email";
											} else if (correspondencesObject.getString("alAction").equalsIgnoreCase("texted")) {
												temp = "Text";
											}
											mainHtmlPage.append("<td><p class='bodyText text-indent'>" 
												+ "&nbsp;&nbsp;&nbsp;<b>"+ (c + 1) +". " + correspondencesObject.getString("strTimestamp") + " * " + temp);
												if (StringUtils.isNotEmpty(correspondencesObject.getString("strContact"))) {
													mainHtmlPage.append(" * " + correspondencesObject.getString("strContact"));
												}
												if (StringUtils.isNotEmpty(correspondencesObject.getString("strContactNumber"))) {
													mainHtmlPage.append(" * " + correspondencesObject.getString("strContactNumber"));
												}
												if (StringUtils.isNotEmpty(correspondencesObject.getString("alWhere"))) {
													mainHtmlPage.append(" * " + correspondencesObject.getString("alWhere"));
												}
												if (StringUtils.isNotEmpty(correspondencesObject.getString("alSpecificName"))) {
													mainHtmlPage.append(" * " + correspondencesObject.getString("alSpecificName"));
												}
												mainHtmlPage.append("</b><br></br>");
												mainHtmlPage.append("&nbsp;&nbsp;&nbsp;<b>&nbsp;&nbsp;&nbsp;&nbsp;Subject: </b>"+ correspondencesObject.getString("strSubject"));
												if (StringUtils.isNotEmpty(correspondencesObject.getString("strDescription"))) {
													mainHtmlPage.append("<br></br>&nbsp;&nbsp;&nbsp;<b>&nbsp;&nbsp;&nbsp;&nbsp;Description: </b>"+ correspondencesObject.getString("strDescription"));
												}
											mainHtmlPage.append("</p><br></br></td>");
										mainHtmlPage.append("</tr>");	
										
										/*Email Design*/
										if (reportLevelIds.size() == 1) {
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>" 
													+ "<b>"+ (c + 1) +". " + correspondencesObject.getString("strTimestamp") + " * " + temp);
													if (StringUtils.isNotEmpty(correspondencesObject.getString("strContact"))) {
														mainHtmlEmailPage.append(" * " + correspondencesObject.getString("strContact"));
													}
													if (StringUtils.isNotEmpty(correspondencesObject.getString("strContactNumber"))) {
														mainHtmlEmailPage.append(" * " + correspondencesObject.getString("strContactNumber"));
													}													
													if (StringUtils.isNotEmpty(correspondencesObject.getString("alWhere"))) {
														mainHtmlEmailPage.append(" * " + correspondencesObject.getString("alWhere"));
													}
													if (StringUtils.isNotEmpty(correspondencesObject.getString("alSpecificName"))) {
														mainHtmlEmailPage.append(" * " + correspondencesObject.getString("alSpecificName"));
													}
													mainHtmlEmailPage.append("</b><br/>");
													mainHtmlEmailPage.append("<b>&nbsp;&nbsp;&nbsp;&nbsp;Subject: </b>"+ correspondencesObject.getString("strSubject"));
													if (StringUtils.isNotEmpty(correspondencesObject.getString("strDescription"))) {
														mainHtmlEmailPage.append("<br/><b>&nbsp;&nbsp;&nbsp;&nbsp;Description: </b>"+ correspondencesObject.getString("strDescription"));
													}
													mainHtmlEmailPage.append("</p><br/></td>");
												mainHtmlEmailPage.append("</tr>");
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td class='spacer'></td>");
											mainHtmlEmailPage.append("</tr>");
										}
										if (c == 4) {
											cTemp = true;
											break;
										}
									}
									if (cTemp) {
										/*PDF Design*/
										mainHtmlPage.append("<tr>");
											mainHtmlPage.append("<td><p class='bodyText text-indent'>"+
														"&nbsp;&nbsp;&nbsp;Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=activity'>here</a> for more correspondences");
											mainHtmlPage.append("</p></td>");
										mainHtmlPage.append("</tr>");
										/*Email Design*/
											mainHtmlEmailPage.append("<tr>");
												mainHtmlEmailPage.append("<td><p class='bodyText text-indent' style='margin:0;padding:0;'>"+
															"Click <a style='color:#1a6394' href='"+url+"/dashboard#?pdfFlag=activity'>here</a> for more correspondences");
												mainHtmlEmailPage.append("</p></td>");
											mainHtmlEmailPage.append("</tr>");
									}
								/*PDF Design*//*
								mainHtmlPage.append("</table>");
							mainHtmlPage.append("</td>");
						mainHtmlPage.append("</tr>");*/
						
						/*Email Design*/
						if (reportLevelIds.size() == 1) {
									mainHtmlEmailPage.append("</table>");
								mainHtmlEmailPage.append("</td>");
							mainHtmlEmailPage.append("</tr>");
						}
					}
					/*PDF Design*/
					mainHtmlPage.append("<tr>");
						mainHtmlPage.append("<td class='spacer'></td>");
					mainHtmlPage.append("</tr>");
					mainHtmlPage.append("</tbody>");
					mainHtmlPage.append("</table>");
					mainHtmlPage.append("</body></html>");
					/*Email Design*/
					if (reportLevelIds.size() == 1) {
				        mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><a href='"+ url +"' style='text-decoration:none'><img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=ealogin.png' width='217'></img></a></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("<tr>");
							mainHtmlEmailPage.append("<td class='spacer'></td>");
						mainHtmlEmailPage.append("</tr>");
						mainHtmlEmailPage.append("</tbody>");
						mainHtmlEmailPage.append("<tfoot>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><p class='bodyText'>"+
									"Please contact EnerAllies support at "+pdfReportJson.getString("footerPhone")+" or email <a href='#' style='color:#1a6394;text-decoration:none'>"+pdfReportJson.getString("footerEmail")+"</a> if you have any questions.<br/><br/>"+
									"If you wish to stop receiving notifications, please click the link below to unsubscribe:<br/>"+
									"<a style='color:#1a6394;' href='"+url+"/dashboard#?pdfFlag=user&pdfValue="+userId+"'>Alert Notification Settings</a>");
							mainHtmlEmailPage.append("</p></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td class='spacer'></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("<tr>");
								mainHtmlEmailPage.append("<td align='center'><p class='bodyText'>Copyright &copy; 2017 EnerAllies. All rights reserved.</p></td>");
							mainHtmlEmailPage.append("</tr>");
							mainHtmlEmailPage.append("</tfoot>");
						mainHtmlEmailPage.append("</table></div>");
			        	mainHtmlEmailPage.append("</body></html>");
						}
			        	is = new ByteArrayInputStream(mainHtmlPage.toString().getBytes());
					    worker.parseXHtml(pdfWriter, doc, is);
					    if ((reportLevelIds.size()-(i+1)) > 0) {
				        	doc.newPage();
				        }
					}
				}
			}
			if (groupDeviceCount > 0) {
				doc.close();
			}
		    fos.close();
		    if (reportLevelIds.size() > 0) {
		        
		    	AddManualLogRequest addManualLogRequest = new AddManualLogRequest();
		        addManualLogRequest.setIsPdfReport(1);
		        addManualLogRequest.setReportPreference(reportPreference);
		        addManualLogRequest.setReportLevel(reportLevel);
		        addManualLogRequest.setReportLevelIds(StringUtils.join(reportLevelIds, ", "));
		        addManualLogRequest.setDescription(customerName + " " +  reportPreferenceText + " " +
		        		reportLevelText + " Report " + "("+fromDateMMDD + "-" + toDateMMDD + ") sent to " +
		        		userFirstName + " " + userLastName + " at " + userEmail + ".");
		        addManualLogRequest.setSpecificId(reportLevelIds.isEmpty() ? 0 : Integer.parseInt(reportLevelIds.get(0)));
		        
		        addManualLogRequest.setReportComponent(reportPreferenceText + " " + reportLevelText + " Report");
		        activityLogDao.createManualActivityLog(addManualLogRequest, userId);
		        
		        AddPDFReportDataRequest addPDFReportDataRequest = new AddPDFReportDataRequest();
		        addPDFReportDataRequest.setToDate(toDateActivity);
		        addPDFReportDataRequest.setFromDate(fromDateActivity);
		        addPDFReportDataRequest.setReportLevelText(reportLevelText + "s " + reportPreferenceText + " Report");
		        addPDFReportDataRequest.setUserId(userId);
		        addPDFReportDataRequest.setReportStatus(1);
		        addPDFReportDataRequest.setSpecificIds(reportLevelIds);
		        addPDFReportDataRequest.setActualFilePath(fileName);
		        addPDFReportDataRequest.setReportLevel(reportLevel);
		        addPDFReportDataRequest.setReportPreference(reportPreference);
		        addPDFReportDataRequest.setSiteIds(uniqueSiteIDs);
		        pdfReportDAO.addPDFReportData(addPDFReportDataRequest, userId);
		        
		        /* Email Sending*/
		        MailBroadCast mailBroadCast = new MailBroadCast();
				mailBroadCast.setFromEmail(ConfigurationUtils.getConfig("from.mail.pdf"));
				mailBroadCast.setToEmail(userEmail);
				StringBuilder emailTemplate = new StringBuilder();
				
				String pdfFileName = "";
				if (reportLevelIds.size() > 1) {
					mailBroadCast.setSubject(customerName + " " + reportPreferenceText + " " + reportLevelText + "s" + " Report : "+fromDateMMDD + " - " + toDateMMDD);
					pdfFileName = customerName + " " + reportPreferenceText + " " + reportLevelText + "s" + " Report - "+ fromDateMMDDDot +" - "+ toDateMMDDDot.toString().trim() + ".pdf";
				} else {
					mailBroadCast.setSubject(groupNames.isEmpty() ? "" : groupNames.get(0) + " " + reportPreferenceText + " Report : " + fromDateMMDD + " - " + toDateMMDD);
					pdfFileName = groupNames.isEmpty() ? "" : groupNames.get(0).split("#")[0].toString().trim() + " " + reportPreferenceText + " " + reportLevelText + " Report - "+ fromDateMMDDDot +" - "+ toDateMMDDDot.toString().trim() + ".pdf";
				}
				if (reportLevelIds.size() > 1) {
				
					emailTemplate.append("<!DOCTYPE html><html lang='en'><head>");
					emailTemplate.append("<meta charset='utf-8'/><meta http-equiv='X-UA-Compatible' content='IE=edge'/><meta name='viewport' content='width=device-width, initial-scale=1'/>");
					emailTemplate.append("<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet'/>"
							+ "<meta name='description' content='Source code generated using layoutit.com'/><meta name='author' content='LayoutIt!'/>");
					
					emailTemplate.append("<style>"
							+ ".blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:18px; margin:0;} .bodyText{font-size:14px;color:#000000;}"
							+ ".grey-bg{background:#eeeeee; height:5px; margin:10px 0 20px 0} .grey-bg2{background:#eeeeee; height:1px;} "
							+ ".loginBtn{padding:15px; float:left; background:#1a6394; color:#fff; font-size:18px; text-decoration:none; border-radius:5px;}"
							+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15px; padding-top:15px;}"
							+ ".chartTitle{font-weight:bold; font-size:10px; color:black; text-align:center; text-transform:uppercase;}"
							+ ".spacer{height:10px}.spacertr{margin-left:12px;margin-right:12px;display:table;}"
							+ ".bodyTextHeader{font-size:18px; color:#000000;}"
							+ ".onlyBlueBg{background:#939393; color:#000000;} .whiteBg{background:#ffffff;color:#000000;} p{margin:0;}"
							+ ".grayborder{background:#EEEEEE;display:block;position:relative;width:100%;margin-top:20px;margin-bottom:20px;}"
							+ "</style></head><body class='grayborder'>");
					
					emailTemplate.append("<div style='background: #DDDDDD;padding: 30px 0;'><table width='920' align='center' border='0' cellpadding='0' bgcolor='#FFFFFF' cellspacing='0' class='mainTable'>");
					emailTemplate.append("<thead>");
						emailTemplate.append("<tr class='spacertr'>");
							emailTemplate.append("<td><img src='"+url+"/fileUpload/loadPDFReportImages?imageName=logoEA.png' width='200'></img></td>");
						emailTemplate.append("</tr>");
					emailTemplate.append("</thead>");
					emailTemplate.append("<tbody>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='blue-bg'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td>");
							emailTemplate.append("<table width='100%' border='0' cellpadding='0' cellspacing='0'>");
								emailTemplate.append("<tr class='spacertr'>");
								if (StringUtils.isNotEmpty(logo)) {
									emailTemplate.append("<td>");
										emailTemplate.append("<img height='40' src='"+url+"/fileUpload/loadPDFReportImages?imageName="+logo+"'></img>");
									emailTemplate.append("</td>");
								}
								emailTemplate.append("<td>");
									emailTemplate.append("<h3>"+customerName + " " + reportLevelText + "s " + reportPreferenceText + " Report</h3>");
									emailTemplate.append("<span class='bodyText'>"+fromDateText +" to " + toDateText+"</span>");
								emailTemplate.append("</td>");
								if (optCount > 0) {
									emailTemplate.append("<td align='right'>");
										emailTemplate.append("<h3>Comfort Optimization On</h3>");
									emailTemplate.append("</td>");
									emailTemplate.append("<td width='30'>");
										emailTemplate.append("<img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=reportIcon.png' width='24'></img>");
									emailTemplate.append("</td>");
								}
								emailTemplate.append("</tr>");
							emailTemplate.append("</table>");
						emailTemplate.append("</td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='grey-bg'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr class='spacertr'>");
						emailTemplate.append("<td><h3>Groups Performance Summary</h3></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr class='spacertr'>");
						emailTemplate.append("<td>");
							emailTemplate.append("<table width='100%' border='0' class='onlyBlueBg' cellpadding='5' cellspacing='2'>");
								emailTemplate.append("<tr>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'>&nbsp;</td>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Degraded HVAC Units</b></p></td>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Within Setpoints</b></p></td>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>HVAC Usage</b></p></td>");
									emailTemplate.append("<td width='20%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>Manual Changes</b></p></td>");
								emailTemplate.append("</tr>");
								
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject groupObject = jsonArray.getJSONObject(i);
									
									if (groupObject.getInt("deviceCount") > 0) {
										JSONArray degradedReportJson = groupObject.getJSONArray("degradedReportLable");
										JSONArray withinReportJson = groupObject.getJSONArray("withinReportLable");
										JSONObject hvacReportJsons = groupObject.getJSONObject("hvacReportLable");
										JSONArray manualReportJson = groupObject.getJSONArray("manualChangesLable");
										List<String> degradedReport = new ArrayList<String>();
										List<String> withinReport = new ArrayList<String>();
										List<String> hvacReport = new ArrayList<String>();
										List<String> manualReport = new ArrayList<String>();
										
										for (int degraded = 0; degraded < degradedReportJson.length(); degraded++) {
											JSONObject degradedObj = degradedReportJson.getJSONObject(degraded);
											degradedReport.add(degradedObj.getString("name").trim());
										}
										for (int within = 0; within < withinReportJson.length(); within++) {
											JSONObject withinObj = withinReportJson.getJSONObject(within);
											withinReport.add(withinObj.getString("name").trim());
										}
										for (int manual = 0; manual < manualReportJson.length(); manual++) {
											JSONObject manualObj = manualReportJson.getJSONObject(manual);
											manualReport.add(manualObj.getString("name").trim());
										}
										for (int hvac = 0; hvac < hvacReportJsons.getJSONArray("categories").length(); hvac++) {
											JSONArray hvacReportJsonArray = hvacReportJsons.getJSONArray("data").getJSONObject(hvac).getJSONObject("drilldown").getJSONArray("categories");
											JSONArray hvacReportJsonDataArray = hvacReportJsons.getJSONArray("data").getJSONObject(hvac).getJSONObject("drilldown").getJSONArray("data");
											for (int k = 0; k < hvacReportJsonArray.length(); k++) {
												if (Integer.parseInt(hvacReportJsonDataArray.get(k).toString()) > 0) {
													hvacReport.add(hvacReportJsonArray.get(k).toString());
												}
											}
										}
										if (degradedReportJson.length() > 0 || withinReportJson.length() > 0 || manualReportJson.length() > 0 || hvacReportJsons.getJSONArray("categories").length() > 0) {
											emailTemplate.append("<tr>");
											emailTemplate.append("<td class='whiteBg' style='color:#1a6394'><p class='bodyText'><a href='"+url+"/dashboard#?pdfFlag=dashboard&pdfValue="+groupObject.getString("customerId")+"-"+groupObject.getString("groupId")+"-0-"+daysRedirect+"' style='color:#1a6394;'>"+groupObject.get("groupName")+"</a></p></td>");
												emailTemplate.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(degradedReport, ", ")+"</p></td>");
												emailTemplate.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(withinReport, ", ")+"</p></td>");
												emailTemplate.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(hvacReport, ", ")+"</p></td>");
												emailTemplate.append("<td class='whiteBg'><p class='bodyText'>"+ StringUtils.join(manualReport, ", ")+"</p></td>");
											emailTemplate.append("</tr>");
										}
									}
								}	
							emailTemplate.append("</table>");
						emailTemplate.append("</td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					int allCritical = 0;
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject groupObject = jsonArray.getJSONObject(i);
						JSONArray reportsData = groupObject.getJSONArray("reports");
						for(int g = 0; g < reportsData.length(); g++) {
							JSONObject criticalObject = (JSONObject) reportsData.get(g);
							JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
							if (criticalY.getInt("y") != 0) {
								allCritical++;
							}
						}
					}
					if (allCritical > 0) {
						emailTemplate.append("<tr>");
							emailTemplate.append("<td class='spacer'></td>");
						emailTemplate.append("</tr>");
						emailTemplate.append("<tr class='spacertr'>");
							emailTemplate.append("<td><h3>Groups with Current Critical Issues Summary</h3></td>");
						emailTemplate.append("</tr>");
						emailTemplate.append("<tr>");
							emailTemplate.append("<td class='spacer'></td>");
						emailTemplate.append("</tr>");
						emailTemplate.append("<tr class='spacertr'>");
							emailTemplate.append("<td>");
								emailTemplate.append("<table width='100%' border='0' class='onlyBlueBg' cellpadding='5' cellspacing='2'>");
									emailTemplate.append("<tr>");
										emailTemplate.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>&nbsp;</b></p></td>");
										emailTemplate.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleFirst +"</b></p></td>");
										emailTemplate.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleSecond +"</b></p></td>");
										emailTemplate.append("<td width='25%' class='whiteBg' align='center'><p class='bodyTextHeader'><b>"+ chartTitleThird +"</b></p></td>");
									emailTemplate.append("</tr>");
									
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject groupObject = jsonArray.getJSONObject(i);
										JSONArray reportsData = groupObject.getJSONArray("reports");
										int countCritical = 0;
										StringBuilder tempHtml = new StringBuilder();
										tempHtml.append("<tr>");
											tempHtml.append("<td class='whiteBg' style='color:#1a6394'><p class='bodyText'><a href='"+url+"/dashboard#?pdfFlag=alert-critical&pdfValue="+groupObject.getString("customerId")+"-"+groupObject.getString("groupId")+"-0-"+reportLevelText+"' style='color:#1a6394;'>"+groupObject.get("groupName") +"</a></p></td>");
											for(int g = 0; g < reportsData.length(); g++) {
												JSONObject criticalObject = (JSONObject) reportsData.get(g);
												JSONObject criticalY  = (JSONObject) criticalObject.getJSONArray("data").get(0);
												tempHtml.append("<td class='whiteBg'><p class='bodyText'>"+criticalY.getInt("y")+"</p></td>");
												if (criticalY.getInt("y") != 0) {
													countCritical++;
												}
											}
										tempHtml.append("</tr>");
										
										if (countCritical > 0) {
											emailTemplate.append(tempHtml);
										}
									}	
								emailTemplate.append("</table>");
							emailTemplate.append("</td>");
						emailTemplate.append("</tr>");
					}
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr class='spacertr'>");
						emailTemplate.append("<td><a href='"+ url +"' style='text-decoration:none'><img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=ealogin.png' width='217'></img></a></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("<tr>");
						emailTemplate.append("<td class='spacer'></td>");
					emailTemplate.append("</tr>");
					emailTemplate.append("</tbody>");
					emailTemplate.append("<tfoot>");
						emailTemplate.append("<tr class='spacertr'>");
							emailTemplate.append("<td><p class='bodyText'>"+
									"Please contact EnerAllies support at 1-888-770-3009 x300 or email <a href='mailto:support@enerallies.com' style='color:#1a6394;text-decoration:none'>support@enerallies.com</a> if you have any questions.<br/><br/>"+
									"If you wish to stop receiving notifications, please click the link below to unsubscribe:<br/>"+
									"<a style='color:#1a6394;' href='"+url+"/dashboard#?pdfFlag=user&pdfValue="+userId+"'>Alert Notification Settings</a>");
							emailTemplate.append("</p></td>");
							emailTemplate.append("</tr>");
							emailTemplate.append("<tr>");
								emailTemplate.append("<td class='spacer'></td>");
							emailTemplate.append("</tr>");
							emailTemplate.append("<tr>");
								emailTemplate.append("<td align='center'><p class='bodyText'>Copyright &copy; 2017 EnerAllies. All rights reserved.</p></td>");
							emailTemplate.append("</tr>");
							emailTemplate.append("</tfoot>");
						emailTemplate.append("</table></div>");
			        	emailTemplate.append("</body></html>");
					} else {
						emailTemplate.append(mainHtmlEmailPage.toString());
					}
					mailBroadCast.setMailText(emailTemplate.toString());
					File pdfFile = new File(rootPath + File.separator + "PDFImages" + File.separator + fileName);
					
					// Instantiating mail publisher
					MailPublisher publisher = new MailPublisher();
					
					// Broad casting mail
					boolean mailFlag = publisher.publishEmailWithAttachment(mailBroadCast, pdfFile, pdfFileName, true);
					if (mailFlag) {
				        String fullPathDelete = rootPath + File.separator + folderName;
				        File deleteFolder = new File(fullPathDelete);
				        if (deleteFolder.exists()) {
				        	String[]entries = deleteFolder.list();
				        	for(String s: entries){
				        	    File currentFile = new File(deleteFolder.getPath(),s);
				        	    currentFile.delete();
				        	}
				        	deleteFolder.delete();
				        }
					}
				}
			}
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.PDF_REPORT_SUCCESS);
			response.setData(reportResponse);
			}
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_FAILED, logger, e);
		}
		
		logger.info("[END] [weeklyPDFReportGeneratorGroup] [PDF REPORT SERVICE LAYER]");
		return response;
	}
}
