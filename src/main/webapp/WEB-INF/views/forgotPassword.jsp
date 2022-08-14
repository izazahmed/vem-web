<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="org.springframework.ui.Model" %>
<%@ page import="com.enerallies.vem.util.ConfigurationUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
		// Getting status flag attribute to check wether application has any errors
		String status = (String)request.getAttribute("forgotStatus");
		String buildVersion = ConfigurationUtils.getConfig("build.version");
	
		// Adding below caching attribute to control back button
		response.setHeader("Cache-Control","no-store");
		response.setHeader("Pragma","no-cache");
		response.setDateHeader ("Expires", 0);
%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>EnerAllies :: Forgot password</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="icon" type="image/png" href="assets/images/favicon-32x32.png" sizes="32x32" />
    <link rel="icon" type="image/png" href="assets/images/favicon-16x16.png" sizes="16x16" />
	<script src="assets/utils/jquery-2.2.4.min.js"></script>
	<script language="javascript">
		// handling browser back button
	    window.history.forward();
	</script>
</head>
<body>
         <div class="container container-table">
            <div class="row vertical-center-row">
                <div class="text-center col-md-4 col-md-offset-4">
	                <% if (status == "success") { %>
						<div class="alert alert-success">
		                    <strong>${forgotMessage}</strong>
		                </div>
		                <script type="text/javascript">
                            $(document).ready(function() {
                            	$(".forgotPassword").hide();
                            });
		                </script>
					<% } else if (status == "error") {%>
						<div class="alert alert-danger">
		                    <strong>${forgotMessage}</strong>
		                </div>
					<% }%>
                    <div class="loginmodal-container">
                        <img src="fileUpload/loadApplicationImage?imageName=uploaded_logo.png" class="logo img-responsive" />
						<br/>
						<gap></gap>
                        <div class="Asap size-20 b-margin-10 signin-blue forgotPassword">Forgot Password</div>
                        <p class="text-left forgotPassword">Enter the email address for your account to reset the password</p>            
                        <form action="forgotPassword" method="POST" class="forgotPassword">
                            <input type="email" name="emailId" placeholder="Email address" class="input-rounded" required>
                            <input type="submit" class="btn btn-lg btn-custom input-rounded t-margin-15" value="Reset">
                        </form>
                        <div class="radio pull-right">
                            <a href="<%=request.getContextPath() %>" class="blue">Login</a>
                        </div>
                        <div class="clear"></div>
                       </div>
                </div>
                
                        <div class="clear"></div>
                 <p class="text-center  col-sm-10 col-sm-offset-1 signin-info">Version:<%=buildVersion %> - Copyright &copy; 2017 EnerAllies. All rights reserved. | <a href="http://enerallies.com/" target="_blank" class="link">EnerAllies</a></p>
	                
            </div>
        </div>
        <link rel="stylesheet" type="text/css" href="assets/utils/css/bootstrap.min.css" />
        <link rel="stylesheet" type="text/css" href="assets/styles/common.css" />
        <link rel="stylesheet" type="text/css" href="assets/styles/login.css" />
        
</body>
</html>