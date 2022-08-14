<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.springframework.ui.Model" %>
        <%@ page import="com.enerallies.vem.util.ConfigurationUtils" %>
            <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
                <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
                <html>
                	<%
						// Getting status flag attribute to check wether application has any errors
						String status = (String)request.getAttribute("newPwdStatus");
				     	String tokenStatus = (String)request.getAttribute("tokenStatus");
			   			String buildVersion = ConfigurationUtils.getConfig("build.version");

			   			// Adding below caching attribute to control back button
						response.setHeader("Cache-Control","no-cache");
						response.setHeader("Cache-Control","no-store");
						response.setHeader("Pragma","no-cache");
						response.setDateHeader ("Expires", 0);
					%>

                    <head>
                     	<meta http-equiv="X-UA-Compatible" content="IE=edge">
                        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
                        <title>New Password</title>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
                        <link rel="stylesheet" type="text/css" href="../assets/styles/common.css" />
                        <link rel="stylesheet" type="text/css" href="../assets/utils/angular/loading-bar.css" />
                        <script src="../assets/utils/jquery-2.2.4.min.js"></script>
                        <link rel="icon" type="image/png" href="../assets/images/favicon-32x32.png" sizes="32x32" />
                        <link rel="icon" type="image/png" href="../assets/images/favicon-16x16.png" sizes="16x16" />
                        <style>
                            .error{
                                display: none;
                            }
                        </style>
                        <script type="text/javascript">
                            // handling browser back button
                            window.history.forward();
                            $(document).ready(function() {


                                var token = window.location.search.substring(1).split("=")[1];

                                $("#choosePwd").on("keypress", function(e) {
                                    if (e.keyCode == 32) return false;
                                });

                                $("#repeatPwd").on("keypress", function(e) {
                                    if (e.keyCode == 32) return false;
                                });

                                function getPattern(str) {
                                    if(str.trim().length < 8){
                                        return false;
                                    }
                                    var re = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!$%@#£€*?&]{8,}$/;
                                    
                                    return re.test(str);
                                }

                                $("#create_new_password").submit(function(eventObj) {

                                    // Prevent the form from submitting via the browser.
                                    eventObj.preventDefault();
                                    var data = {
                                        authToken: token,
                                        choosePwd: $("#choosePwd").val(),
                                        repeatPwd: $("#repeatPwd").val(),
                                        isFirstTimeChange: true
                                    }

                                    $(".error").hide()

                                  if(!getPattern($("#choosePwd").val())){
                                      
                                      $("#choosePwd").next(".error").show();
                                      
                                      return;
                                  }

                                  if($("#choosePwd").val().trim() != $("#repeatPwd").val().trim()){
                                      
                                      $("#repeatPwd").next(".error").show();
                                      
                                      return;
                                  }

                                    $("#loading-bar-spinner").show();



                                    $.ajax({
                                        type: "POST",
                                        url: "<%=request.getContextPath() %>/createNewPassword",
                                        contentType: "application/json",
                                        data: JSON.stringify(data),
                                        dataType: "JSON",
                                        success: function(data) {
                                            if (data.status == "SUCCESS") {

                                                // Display success message
                                                $(".alert").show().addClass("alert-success").text(data.code);

                                                // Resetting form details after success
                                                $("#create_new_password")[0].reset();

                                                window.location.href = "<%=request.getContextPath() %>/login";

                                            } else if (data.status == "FAILURE") {
                                                // Display error message
                                                $(".alert").show().addClass("alert-danger").text(data.code);
                                            }
                                            $("#loading-bar-spinner").hide();

                                            setTimeout(function() {
                                                $(".alert").hide();
                                            }, 5000)

                                        },
                                        error: function(e) {
                                            $("#loading-bar-spinner").hide();
                                           
                                        }
                                    });
                                });
                            });
                        </script>
                    </head>

                    <body>
                        <div class="container container-table">
                            <div class="row vertical-center-row">
                                <div class="text-center col-md-4 col-md-offset-4">
                                    <div class="alert" style="display:none">
                                    </div>
                                    <div class="loginmodal-container">
                                        <% if (status == null) {%>
                                            <img src="../fileUpload/loadApplicationImage?imageName=uploaded_logo.png" class="logo img-responsive" />
                                            <% } else {%>
                                                <img src="../fileUpload/loadApplicationImage?imageName=uploaded_logo.png" class="logo img-responsive" />
                                                <% } %>
                          							<br/>
													<gap></gap>
                                                    <form id="create_new_password" autocomplete="off">

                                                        <div class="size-20 b-margin-10 signin-blue">Create New Password</div>
                                                        <input class="input-rounded" type="password" name="choosePwd" id="choosePwd" placeholder="New Password" required>
                                                        <p class="error">Password should contain at least 8 alphanumeric characters</p>

                                                        <!-- oninvalid="this.setCustomValidity('Password should contain at least 8 alphanumeric characters')"-->
                                                        <input class="input-rounded" type="password" name="repeatPwd" id="repeatPwd" placeholder="Repeat Password" required>
                                                        <p class="error">Repeat password doesn't match the new password</p>
                                                        <input type="submit" class="btn btn-lg btn-custom input-rounded t-margin-15" value="Submit">
                                                    </form>

                                                    <div class="radio pull-right forgot">
                                                        <a href="<%=request.getContextPath() %>" class="blue">Login</a>
                                                    </div>
                                                    <div class="clear"></div>
                                                    </div>

                                </div>
                                      <div class="clear"></div>
                 <p class="text-center  col-sm-10 col-sm-offset-1 signin-info">Version:<%=buildVersion %> - Copyright &copy; 2017 EnerAllies. All rights reserved. | <a href="http://enerallies.com/" target="_blank" class="link">EnerAllies</a></p>
	               
                            </div>
                        </div>
                        
                        <% if (status == null) {%>
                            <link rel="stylesheet" type="text/css" href="../assets/utils/css/bootstrap.min.css" />
                            <link rel="stylesheet" type="text/css" href="../assets/styles/login.css" />
                            <% } else {%>
                                <link rel="stylesheet" type="text/css" href="assets/utils/css/bootstrap.min.css" />
                                <link rel="stylesheet" type="text/css" href="assets/styles/login.css" />
                                <% } %>

                                    <div id="loading-bar-spinner" style="display:none">
                                        <div class="spinner-icon"></div>
                                    </div>

                                    <script language='javascript' type='text/javascript'>
                                        function check(input) {
                                            if (input.value != document.getElementById('password').value) {
                                                input.setCustomValidity("Repeat password doesn't match the new password");
                                            } else {
                                                // input is valid -- reset the error message
                                                input.setCustomValidity('');
                                            }
                                        }
                                    </script>
                                    
                    </body>

                </html>