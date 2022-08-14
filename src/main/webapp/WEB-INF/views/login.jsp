<%@ page isELIgnored ="false" language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.springframework.ui.Model" %>
    <%@ page import="com.enerallies.vem.util.ConfigurationUtils" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
            <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
            <html>
            <%
		// Getting status flag attribute to check wether application has any errors
		String status = (String)request.getAttribute("status");
		String userName = (String)request.getAttribute("userName");
		String password = (String)request.getAttribute("password");
		String sessionCheck = (String)request.getAttribute("sessionCheck");
		String buildVersion = ConfigurationUtils.getConfig("build.version");

		// checking for null condition
		userName = (userName == null)?"":userName;
		password = (password == null)?"":password;
		sessionCheck = (sessionCheck == null)?"false":sessionCheck;
		
		// Adding below caching attribute to control back button
		response.setHeader("Cache-Control","no-store");
		response.setHeader("Pragma","no-cache");
		response.setDateHeader ("Expires", 0);
	%>

                <head>
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
                    <meta http-equiv="Pragma" content="no-cache">
                    <meta http-equiv="Cache-Control" content="no-store">
                    <meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
                    <title>EnerAllies :: Login</title>
                    <link rel="stylesheet" type="text/css" href="assets/utils/css/bootstrap.min.css" />
                    <link rel="stylesheet" type="text/css" href="assets/styles/common.css" />
                    <link rel="stylesheet" type="text/css" href="assets/styles/login.css" />
                    <link rel="stylesheet" type="text/css" href="assets/utils/css/icon-moon.css" />
                    <link rel="icon" type="image/png" href="assets/images/favicon-32x32.png" sizes="32x32" />
                    <link rel="icon" type="image/png" href="assets/images/favicon-16x16.png" sizes="16x16" />
                    <script src="assets/utils/jquery-2.2.4.min.js"></script>
                </head>

                <body>

                    <div class="container container-table Asap">
                        <div class="row vertical-center-row">
                            <div class="text-center col-sm-8 col-sm-offset-2">
                                <!-- printing error message if any errors -->
                                <% if (status == "error") { %>
                                    <div class="alert alert-danger">
                                        <strong>${eaiMessage}</strong>
                                    </div>
                                    <% } %>

                                        <div class="loginmodal-container">
                                            <img src="fileUpload/loadApplicationImage?imageName=uploaded_logo.png" class="logo img-responsive" />
                                            <!--
                        <span class="icon-eai-logo" aria-hidden="true">
                        <span class="path1"></span><span class="path2"></span><span class="path3"></span><span class="path4"></span><span class="path5"></span><span class="path6"></span><span class="path7"></span><span class="path8"></span><span class="path9"></span><span class="path10"></span><span class="path11"></span>
                        </span>
                        -->
                                            <br/>
                                            <gap></gap>

                                            <div class="Asap size-20 b-margin-10 signin-blue sign-in-label">Sign in with your Email Account</div>
                                            <form action="signin" method="POST" autocomplete="off" id="sigin-form">
                                                <input type="email" name="userName" placeholder="Email" id="username" class="input-rounded" required>
                                                <input type="password" name="password" placeholder="Password" id="password" class="input-rounded" required>
                                                <input type="hidden" name="timezone" id="timezone" />
                                                <input type="hidden" name="isLoginPage" id="isLoginPage" value="true"/>
                                                <input type="hidden" name="pdfFlag" id="pdfFlag" value=""/>
                                                <input type="hidden" name="pdfValue" id="pdfValue" value=""/>
                                                <input type="submit" class="btn btn-lg btn-custom input-rounded t-margin-15" value="Sign In">
                                            </form>

                                            <%-- <form:form method="POST" modelAttribute="loginRequest" action="signin">
							<form:input path="userName" />
							<form:input path="password" />
							<input type="submit" class="btn btn-lg btn-custom no-rounded" value="Sign In" />
						</form:form> --%>
                                                <!--
                        <div class="checkbox pull-left">
                            <label>
                                <input type="checkbox" value="">Stay Signed In</label>
                        </div>-->
                                                <div class="radio pull-right forgot">
                                                    <a href="forgot" class="blue">Forgot Password?</a>
                                                </div>

                                                <div class="clear"></div>
                                        </div>
                            </div>
                            <div class="text-center  col-sm-10 col-sm-offset-1 signin-info">By signing in, you agree to the EnerAllies license agreement, terms of use, and privacy policy</div>
                        <p class="text-center  col-sm-10 col-sm-offset-1 signin-info">Version:<%=buildVersion %> - Copyright &copy; 2017 EnerAllies. All rights reserved. | <a href="http://enerallies.com/" target="_blank" class="link">EnerAllies</a></p>
	                
                         
                        </div>
                            
                    </div>
                        
                       
                    
					<input type="hidden" id="session-check" name="session-check" value="<%=sessionCheck%>"/>
					<script src="assets/utils/jquery-2.2.4.min.js"></script>
                    <script src="assets/utils/jstz.min.js"></script>
                    <script language="javascript">
                        // handling browser back button
                        window.history.forward();
                        $(document).ready(function() {
                            // Resetting form details when loading 
                            $("#sigin-form")[0].reset();

                            $("#password").on("keypress", function(e) {
                                if (e.keyCode == 32) return false;
                            });

                            $('#username').attr("autocomplete", "off");
                            setTimeout('$("#username").val("<%=userName %>");', 10);

                            $('#password').attr("autocomplete", "off");
                            setTimeout('$("#password").val("<%=password %>");', 2);

                            var timezone = jstz.determine();
                            var timezoneName = timezone.name();
                            $("#timezone").val(timezoneName);
                            
                            $("#pdfFlag").val(getParameterByName('pdfFlag'));
                            $("#pdfValue").val(getParameterByName('pdfValue'));
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
    						
                        });
                    </script>
                </body>

            </html>