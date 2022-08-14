package com.enerallies.vem.serviceimpl.alert;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enerallies.vem.beans.admin.MailBroadCast;
import com.enerallies.vem.beans.alert.AlertRequest;
import com.enerallies.vem.beans.audit.AddManualLogRequest;
import com.enerallies.vem.beans.audit.AuditRequest;
import com.enerallies.vem.beans.common.Response;
import com.enerallies.vem.dao.activity.ActivityLogDao;
import com.enerallies.vem.dao.alert.AlertEmailDao;
import com.enerallies.vem.dao.audit.AuditDAO;
import com.enerallies.vem.exceptions.VEMAppException;
import com.enerallies.vem.service.alert.AlertEmailService;
import com.enerallies.vem.util.CommonConstants;
import com.enerallies.vem.util.ConfigurationUtils;
import com.enerallies.vem.util.DatesUtil;
import com.enerallies.vem.util.ErrorCodes;
import com.enerallies.vem.util.publish.MailPublisher;

/**
 * File Name : AlertEmailServiceImpl 
 * 
 * AlertEmailServiceImpl: Its an implementation class for AlertEmailService service interface.
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
 * 27-03-2017		Bhoomika Rabadiya   File Created.
 *
 */

@Service("alertEmailService")
@Transactional
public class AlertEmailServiceImpl implements AlertEmailService {

	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(AlertEmailServiceImpl.class);
	
	/**instantiating the AlertEmail dao for accessing the dao layer.*/
	@Autowired AlertEmailDao alertEmailDao;

	@Autowired AuditDAO auditDAO;
	
	//Images location
	private static final String IMAGES_LOCATION = "vem.images";
		
	@Override
	public Response mailGenerator(boolean b) throws VEMAppException, MalformedURLException {
		logger.info("[BEGIN] [mailGenerator] [Alert Email SERVICE LAYER]");
		
		Response response = new Response();
		JSONObject alertsData = new JSONObject();
		
		try {
			AlertRequest alertRequest = new AlertRequest();
			response = alertEmailDao.alertList(alertRequest);
			
			alertsData = (JSONObject) response.getData();
			URL url = null;
			if (b) {
				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
				url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());	
			} else {
				url = new URL(ConfigurationUtils.getConfig("app.url."+ConfigurationUtils.getConfig("build.env")));	
			}
			JSONArray jsonArray = alertsData.getJSONArray("alertsInfo");
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject alertObj = jsonArray.getJSONObject(i);
				StringBuilder mainHtmlEmailPage = new StringBuilder();
				
				String alertMessage = "";
				try{
					alertMessage = alertObj.getString("alertMessage");
				}catch(JSONException e){
					alertMessage = "";
				}
	
				mainHtmlEmailPage.append("<!DOCTYPE html><html lang='en'><head>");
				mainHtmlEmailPage.append("<style>"
						+ "*,td,p,h1,h2,h3,h4,h5 {line-height: 100%;mso-line-height-rule:exactly;} .blue-bg{background:#1a6394; height:15px;} .mainTable td{font-size:6;} h3{color:#333; font-size:18px; margin:0;} .bodyText{font-size:14px;}"
						+ ".grey-bg{background:#eeeeee; height:2px;} .grey-bg2{background:#eeeeee; height:1px;} "
						+ ".loginBtn{padding:15px; float:left; background:#1a6394; color:#fff; font-size:18px; text-decoration:none; border-radius:5px;}"
						+ ".border-right{background:#ccc; height:25px; color:#ccc; float:right;} .text-indent{padding-left:15px; padding-top:15px;}"
						+ ".chartTitle{font-weight:bold; font-size:10px; color:black; text-align:center; text-transform:uppercase;}"
						+ ".spacertr{margin-left:12px;margin-right:12px;display:table;}"
						+ ".grayborder{background:#EEEEEE;display:block;position:relative;width:100%;margin-top:20px;margin-bottom:20px;}"
						+ "</style></head><body class='grayborder body-main'>");
					mainHtmlEmailPage.append("<div style='background: #DDDDDD;padding: 30px 0;'><table width='920' align='center' border='0' cellpadding='0' bgcolor='#FFFFFF' cellspacing='0' class='mainTable'>");
					mainHtmlEmailPage.append("<thead>");
						mainHtmlEmailPage.append("<tr class='spacertr'>");
							mainHtmlEmailPage.append("<td><img src='"+url+"/fileUpload/loadPDFReportImages?imageName=logoEA.png' width='200'></img></td>");
						mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("</thead>");
					mainHtmlEmailPage.append("<tbody>");
				mainHtmlEmailPage.append("<tr>");
					mainHtmlEmailPage.append("<td class='blue-bg'></td>");
				mainHtmlEmailPage.append("</tr>");
				
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td><p class='bodyText' style='margin:0;padding:0;line-height:35px;mso-line-height-rule:exactly;'>Hello "+alertObj.get("userFname").toString()+",</p></td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td><p class='bodyText' style='margin:0;padding:0;'>You are receiving this email because <span style='color:#ef3437'>"+alertMessage+"</span> at "+alertObj.get("siteName").toString()+(StringUtils.isEmpty(alertObj.get("siteInternalId").toString()) ? "" : " #" + alertObj.get("siteInternalId"))+"</p></td>");
				mainHtmlEmailPage.append("</tr>");
				JSONArray actionItems = alertObj.getJSONArray("actionItems");
				if (actionItems.length() > 0) {
					mainHtmlEmailPage.append("<tr class='spacertr'>");
						mainHtmlEmailPage.append("<td><p class='bodyText' style='margin:0;padding:0;'>The actions you can take are: </p><p style='margin:0;padding:0;line-height:10px;mso-line-height-rule:exactly;'>&nbsp;</p></td>");
					mainHtmlEmailPage.append("</tr>");
					mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					for (int k = 0; k < actionItems.length(); k++) {
						JSONObject actionItemsObj = actionItems.getJSONObject(k);
						mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>"+(k+1) + ". " + actionItemsObj.getString("itemName")+"</p>");
					}
					mainHtmlEmailPage.append("</td></tr>");
				}
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td><br><p class='bodyText' style='margin:0;padding:0;'>More details about this alarm are provided below:</p><br></td>");
				mainHtmlEmailPage.append("</tr>");
				
				mainHtmlEmailPage.append("<tr>");
					mainHtmlEmailPage.append("<td class='grey-bg'></td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td><p class='bodyText' style='margin:0;padding:0;line-height:35px;mso-line-height-rule:exactly;'>Click on button below to get to the alerts listing page.</p></td>");
				mainHtmlEmailPage.append("</tr>");
			
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td><a href='"+url+"/dashboard#?pdfFlag=alert-new&pdfValue="+alertObj.get("customerId").toString()+"-"+alertObj.get("siteId").toString()+"' style='text-decoration:none'><img src='"+ url +"/fileUpload/loadPDFReportImages?imageName=ealogin.png' width='217'></img></a></td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					mainHtmlEmailPage.append("<h4 style='margin:0;padding:0;line-height:35px;mso-line-height-rule:exactly;'>Alert Details:</h4>");
				mainHtmlEmailPage.append("</td></tr>");
			
				mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					if (StringUtils.isNotEmpty(alertObj.get("alertName").toString())) {
						mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Name: 			"+ alertObj.get("alertName").toString() + "</p>");
					} else {
						mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Name: 			"+ alertObj.get("alertType").toString() + "</p>");
					}
				mainHtmlEmailPage.append("</td></tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Priority: 		"+ alertObj.get("alertProrityName").toString() + "</p>");
				mainHtmlEmailPage.append("</td></tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Description: 		<span style='color:#ef3437'>"+alertMessage + "</span></p>");
				mainHtmlEmailPage.append("</td></tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Parameter: 		"+ alertObj.get("alertParameter").toString() + " " + alertObj.get("alertParameterUnit").toString()+"</p>");
				mainHtmlEmailPage.append("</td></tr>");
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				TimeZone tz = TimeZone.getTimeZone(alertObj.get("siteTimeZoneId").toString());
		        
		        String databaseString = alertObj.get("createdTimeStamp").toString();
		        
		        String datetimeString = DatesUtil.getDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", formatter.parse(databaseString) );
		        DateTime dateTime = new DateTime( datetimeString );
		        DateTime dateTimeUtc = dateTime.toDateTime( DateTimeZone.UTC );

		  	  	DateTime createdTimeStamp = new DateTime( dateTimeUtc, DateTimeZone.forID( alertObj.get("siteTimeZoneId").toString() ) );
		        
				mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Timestamp: 		"+ DateTimeFormat.forPattern("EEEE MMM dd, yyyy hh:mm a").print(createdTimeStamp) + " (" + tz.getDisplayName(Boolean.TRUE, 0) + ")</p>");
				mainHtmlEmailPage.append("</td></tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Customer: 		"+ alertObj.get("customerName").toString() + "</p>");
				mainHtmlEmailPage.append("</td></tr>");
				if (StringUtils.isNotEmpty(alertObj.get("groupName").toString())) {
					mainHtmlEmailPage.append("<tr class='spacertr'><td>");
						mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Group: 			"+ alertObj.get("groupName").toString() + "</p>");
					mainHtmlEmailPage.append("</td></tr>");
				}
				mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Site: 			"+ alertObj.get("siteName").toString() + (StringUtils.isEmpty(alertObj.get("siteInternalId").toString()) ? "" : " #" + alertObj.get("siteInternalId")) + "</p>");
				mainHtmlEmailPage.append("</td></tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'><td>");
					mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Device Name: 		"+ alertObj.get("deviceName").toString() + "</p>");
				mainHtmlEmailPage.append("</td></tr>");
				if (StringUtils.isNotEmpty(alertObj.get("deviceLocation").toString())) {
					mainHtmlEmailPage.append("<tr class='spacertr'><td>");
						mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;'>Device Location:  "+ alertObj.get("deviceLocation").toString() + "</p>");
					mainHtmlEmailPage.append("</td></tr>");
				}
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td>");
						mainHtmlEmailPage.append("<p class='bodyText' style='margin:0;padding:0;line-height:35px;mso-line-height-rule:exactly;'>Please contact EnerAllies support at 1-888-770-3009 x300 or <a href='mailto:support@enerallies.com' style='color:#1a6394;text-decoration:none'>support@enerallies.com</a> if you have any questions.</p>");
					mainHtmlEmailPage.append("</td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr>");
					mainHtmlEmailPage.append("<td class='grey-bg'></td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td>");
						mainHtmlEmailPage.append("<p style='margin:0;padding:0;line-height:10px;mso-line-height-rule:exactly;'>&nbsp;</p><p class='bodyText' style='margin:0;padding:0;'>If you wish to stop receiving notifications, please click the link below to unsubscribe:<br>"+
								"<a style='color:#1a6394;' href='"+url+"/dashboard#?pdfFlag=user&pdfValue="+alertObj.get("userId").toString()+"'>Alert Notification Settings</a></p>");
					mainHtmlEmailPage.append("</td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td><p style='margin:0;padding:0;line-height:10px;mso-line-height-rule:exactly;'>&nbsp;</p><p class='bodyText' style='margin:0;padding:0;'>Thanks,</p></td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td><p class='bodyText' style='margin:0;padding:0;'>EnerAllies Support</p></td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td><p class='bodyText' style='margin:0;padding:0;'>1-888-770-3009 x300</p></td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr class='spacertr'>");
					mainHtmlEmailPage.append("<td><p class='bodyText' style='margin:0;padding:0;'><a href='mailto:support@enerallies.com' style='margin:0;padding:0;color:#1a6394;text-decoration:none'>support@enerallies.com</a></p></td>");
				mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("<tr>");
					mainHtmlEmailPage.append("<td align='center'><p class='bodyText'>Copyright &copy; 2017 EnerAllies. All rights reserved.</p></td>");
					mainHtmlEmailPage.append("</tr>");
				mainHtmlEmailPage.append("</tbody>");
				mainHtmlEmailPage.append("</table></div>");
	        	mainHtmlEmailPage.append("</body></html>");
	        	
		        AuditRequest auditRequest = new AuditRequest();
		        auditRequest.setUserId(Integer.parseInt(alertObj.get("userId").toString()));
		        auditRequest.setUserAction("Emailed");
		        auditRequest.setLocation("");
		        auditRequest.setServiceId("14");
		        auditRequest.setDescription("Notification has been sent");
		        auditRequest.setServiceSpecificId(Integer.parseInt(alertObj.get("alertId").toString()));
		        auditDAO.insertAuditLog(auditRequest);
		        
		        MailBroadCast mailBroadCast = new MailBroadCast();
				mailBroadCast.setFromEmail(ConfigurationUtils.getConfig("from.mail.pdf"));
				mailBroadCast.setToEmail(alertObj.get("userEmail").toString());
				mailBroadCast.setSubject(alertObj.get("alertName").toString()+" on "+alertObj.get("deviceName").toString()+" at "+alertObj.get("siteName").toString()+(StringUtils.isEmpty(alertObj.get("siteInternalId").toString()) ? "" : " #" + alertObj.get("siteInternalId")));
				mailBroadCast.setMailText(mainHtmlEmailPage.toString());
				MailPublisher publisher = new MailPublisher();
				boolean mailFlag = publisher.publishEmail(mailBroadCast);
			}
			// Preparing success response object
			response.setStatus(CommonConstants.AppStatus.SUCCESS.toString());
			response.setCode(ErrorCodes.PDF_REPORT_SUCCESS);
			response.setData("");
			
		} catch (Exception e) {
			// Creating and throwing the customized exception.
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.PDF_REPORT_FAILED, logger, e);
		}
		
		logger.info("[END] [mailGenerator] [Alert Email SERVICE LAYER]");
		return response;
		
	}
}
