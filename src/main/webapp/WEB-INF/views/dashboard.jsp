
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
	<%@ page session="true" %>
    <%@page import="com.enerallies.vem.beans.admin.GetUserResponse" %>
        <%@page import="com.enerallies.vem.json.mapper.JacksonJsonImpl" %>
            <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
			<!DOCTYPE html>
                <html>			
                <head>    
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <title>EnerAllies</title>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
                    <link rel="icon" type="image/png" href="assets/images/favicon-32x32.png" sizes="32x32" />
                    <link rel="icon" type="image/png" href="assets/images/favicon-16x16.png" sizes="16x16" />
                    <meta http-equiv="Pragma" content="no-cache">
                    <meta http-equiv="Cache-Control" content="no-store">
                    <meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">                    
                    <link rel="stylesheet" type="text/css" href="assets/utils/css/bootstrap.min.css" />
		            <%
		            	String pdfFlag = session.getAttribute("pdfFlag") != null ? (String)session.getAttribute("pdfFlag") :"";
		            	session.removeAttribute("pdfFlag");
		            	
		            	String pdfValue = session.getAttribute("pdfValue") != null ? (String)session.getAttribute("pdfValue") :"";
		            	session.removeAttribute("pdfValue");
 
		             	GetUserResponse userDetails = (GetUserResponse)session.getAttribute("eaiUserDetails");
		            	JacksonJsonImpl jsonImpl = new JacksonJsonImpl();
		            	
		            	response.setHeader("Cache-Control","no-store");
		            	response.setHeader("Pragma","no-cache");
		            	response.setDateHeader ("Expires", 0);
		            %>
                    <script>
                    	var pdfFlag = "<%= pdfFlag %>";
                    	var pdfValue = "<%= pdfValue %>";
                    	if (!pdfFlag) {
                    		pdfFlag = getParameterByName('pdfFlag');
                    		pdfValue = getParameterByName('pdfValue');
                    	}
						function getParameterByName(name, url) {
						    if (!url) {
						      url = window.location.href;
						    }
						    name = name.replace(/[\[\]]/g, "\\$&");
						    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
						        results = regex.exec(url);
						    if (!results) return null;
						    if (!results[2]) return '';
						    return decodeURIComponent(results[2].replace(/\+/g, " "));
						}
                        var eaiDetails = (eaiDetails == undefined) ? {} : eaiDetails;
                        eaiDetails = <%=jsonImpl.toJson(userDetails)%>;
                        var appContext = '<%=request.getContextPath() %>';
                     	// handling browser back button
                        window.history.forward();
                    </script>
                </head>

                <body ng-app="EAI" >   
                    <div class="container-fluid">
                        <div ng-include="'app/views/common/header.html'" ng-controller="header"></div>
                        <div class="row">                       
                            <div class="dashboard">
                               <div class=" dark-gray-bg full-height absolute" ng-class="{'width-185': !toggle.leftMenuArea, 'width-62':toggle.leftMenuArea}">
                                   <div ng-include="'app/views/common/leftMenu.html'" ng-controller="leftMenu"></div>
                               </div>
                               <div  ng-class="{'reduce-width-56' : toggle.leftMenuArea, 'reduce-width-185' : !toggle.leftMenuArea}">
	                               <div class=" full-height">
	                                    <div class="icon-dot-position icon-dot size-26 top-6 text-light cursor" aria-hidden="true"  ng-click="toggle.leftMenuArea = !toggle.leftMenuArea" ></div>
	                                    <div ui-view="right" class="right"></div>
	                                    </div>
                                </div>
                            </div>
                            <!-- <p class="text-center footer">Version:<%-- <%=buildVersion %> --%> - Copyright &copy; 2017 EnerAllies. All rights reserved. | <a href="http://enerallies.com/" target="_blank" class="link">EnerAllies</a></p>
	                         -->        
                        </div>
                    </div>
                    <link rel="stylesheet" type="text/css" href="assets/utils/css/bootstrap.min.css" />
                    <link rel="stylesheet" type="text/css" href="assets/utils/css/jquery.dataTables.min.css" />
                    <link rel="stylesheet" type="text/css" href="assets/utils/angular/select.min.css" />
                    <link rel="stylesheet" type="text/css" href="assets/styles/common.css" />
                     <link rel="stylesheet" type="text/css" href="assets/utils/css/icon-moon.css"/>
                    <link rel="stylesheet" type="text/css" href="assets/utils/angular/loading-bar.css" />
                    <link rel="stylesheet" type="text/css" href="assets/utils/angular/angular-toastr.css" />                    
                    <link rel="stylesheet" type="text/css" href="assets/utils/jquery-ui.css" />
                    <link rel="stylesheet" type="text/css" href="assets/utils/lightbox/angular-bootstrap-lightbox.css" />
                    
               
                    <script src="app/extendes.js"></script>
                    <script src="assets/utils/jquery-2.2.4.min.js"></script>
                    <script src="assets/utils/jquery-ui.min.js"></script>
                    <script src="assets/utils/js/bootstrap.min.js"></script>
                    <script src="assets/utils/angular/angular.min.js"></script>
                    <script src="assets/utils/angular/angular-messages.min.js"></script>
                    <script src="assets/utils/angular/angular-ui-router.js"></script>
                    <script src="assets/utils/angular/loading-bar.js"></script>
                    <script src="assets/utils/angular/angular-animate.min.js"></script>
                    <script src="assets/utils/disable-all.js"></script>
                    <script src="assets/utils/bootstrap.file-input.js"></script>
                    <script src="assets/utils/angular/angular-toastr.tpls.js"></script>
                    <script src="assets/utils/angular/uiBreadcrumbs.js"></script>
                    <script src="assets/utils/angular/select.min.js"></script>
                    <script src="assets/utils/angular/angular-sanitize.min.js"></script>
                    <script src="assets/utils/angular/mask.min.js"></script>
                    <script src="assets/utils/angular/angular-ui-numeric.js"></script>
                    <script type="text/javascript" src="assets/utils/angular/aes.js"></script>
                    <script type="text/javascript" src="assets/utils/angular/mdo-angular-cryptography.js"></script>
    				<script src="assets/utils/angular/ocLazyLoad.min.js"></script>
    				<script src="assets/utils/moment.min.js"></script>
                    <script src="assets/utils/moment-timezone-with-data.js"></script>
    				<!--<script src="assets/utils/moment-timezone.js"></script>-->
					<!--<script src="assets/utils/angular/angular-momentjs.js"></script>-->
					
					<script src="assets/utils/jquery.dataTables.js" type="text/javascript"></script>
					<script src="assets/utils/angular-datatables.js" type="text/javascript"></script>
					<script type="text/javascript" src="assets/utils/angular-datatables-bootstrap.js"></script>
					<script src="assets/utils/angular/lodash.js"></script>
					<script src="assets/utils/angular/ngMask.min.js"></script>
					<script src="assets/utils/jstz.min.js"></script>
					
					<script src="assets/utils/charts/highcharts.js"></script>
					<script src="assets/utils/charts/highcharts-more.js"></script>
					<script src="assets/utils/charts/solid-gauge.js"></script>
					<script src="assets/utils/charts/exporting.js"></script>
					<script src="assets/utils/charts/proj4.js"></script>
					<script src="assets/utils/charts/map.js"></script>
					<script src="assets/utils/charts/data.js"></script>
					<script src="assets/utils/charts/us-all.js"></script>
                    <script src="assets/utils/fastclick.js"></script>

					<script src="assets/utils/angular/directive.js"></script>
 					<script src="assets/utils/lightbox/angular-bootstrap-lightbox.js"></script>
					<script src="assets/utils/angular/ui-bootstrap-tpls.js"></script>
					
					
                    <script src="app/config.js"></script>
                    <script src="app/states.js"></script>
                    <script src="app/views/reports/reportsDirectives.js"></script>
                    <script src="app/views/common/commonController.js"></script>
                    <script src="app/views/users/usersController.js"></script>
                    <script src="app/views/customers/customersController.js"></script>
                    <script src="app/views/roles/rolesController.js"></script>
                    <script src="app/views/users/usersDirectives.js"></script>
                    <script src="app/views/users/usersFactory.js"></script>
                    <script src="app/views/customers/devices/deviceController.js"></script>
                    <script src="app/views/customers/devices/deviceFactory.js"></script>
                    <script src="app/views/customers/sites/siteController.js"></script>
                    <script src="app/views/customers/groups/groupsController.js"></script>
                    <script src="app/views/dashboard/dashboardController.js"></script>
                    <script src="app/views/activitylog/activityLogController.js"></script>
                    <script src="app/views/upload/fileUpload.js"></script>
                    <script src="app/messageFactory.js"></script>
                    <script src="app/apiFactory.js"></script>
                    <script src="app/commonDirectives.js"></script>
                    <script src="app/commonFactories.js"></script>
                    <script src="app/views/scheduler/schedulerController.js"></script>
                    <script src="app/views/customers/devices/deviceProfile/deviceProfileController.js"></script>
                    <script src="app/views/alert/alertController.js"></script>
 					<script src="app/views/reports/reportController.js"></script>
					<script src="app/views/pdfreport/pdfreportController.js"></script>
                </body>

                </html>