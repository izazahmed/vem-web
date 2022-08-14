<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.enerallies.vem.util.ConfigurationUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<%
			// Fetching context of the application
			String expiryType = (String)request.getAttribute("expiryType");
			
			// Adding below caching attribute to control back button
			response.setHeader("Cache-Control","no-store");
			response.setHeader("Pragma","no-cache");
			response.setDateHeader ("Expires", 0);
	%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>EAI</title>
</head>
<body>
	<div class="container container-table">
            <div class="row vertical-center-row">
                <div class="text-center col-md-4 col-md-offset-4">
                <%!
                	public String getFirstTextByType(String expiryType){
	                	if(expiryType.equals("cretePassword")) {
	                		return "create password";
	                	} else{
	                		return "password reset";
	                	}
                	}
                
                	public String getSecondTextByType(String expiryType){
	                	if(expiryType.equals("cretePassword")) {
	                		return "create";
	                	} else{
	                		return "reset";
	                	}
	            	}
                %>
	            Your <%=getFirstTextByType(expiryType) %> request has expired. Click <a href="<%=request.getContextPath() %>" class="blue">here</a> to go to login page.
                </div>
            </div>
        </div>
        <link rel="stylesheet" type="text/css" href="assets/utils/css/bootstrap.min.css" />
        <link rel="stylesheet" type="text/css" href="assets/styles/login.css" />
        
</body>
</html>