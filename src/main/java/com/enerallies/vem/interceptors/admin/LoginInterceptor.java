/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.interceptors.admin;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.enerallies.vem.beans.admin.GetUserRequest;
import com.enerallies.vem.beans.admin.GetUserResponse;
import com.enerallies.vem.controller.customers.CustomersController;
import com.enerallies.vem.service.admin.AdminService;

/**
 * File Name : LoginInterceptor 
 * 
 * LoginInterceptor: This is the Interceptor to check every request whether the session is valid or not
 *
 * @author Nagarjuna Eerla.
 * contact Cambridge Technologies – Umang Gupta (ugupta@ctepl.com)
 * EnerAllies  -  Loanne Cheung  (lcheung@enerallies.com)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MODIFICATION HISTORY
 * =========================================================
 * SNO	DATE				USER             	COMMENTS
 * =========================================================
 * 01	02-08-2016		Nagarjuna Eerla		File Created
 */
public class LoginInterceptor implements HandlerInterceptor{
	
	@Autowired
	AdminService adminService;
	
	private static final Logger logger=Logger.getLogger(LoginInterceptor.class);
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		/**
		 * If we want to do any operation after the request completion we can do it here.
		 */
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		/**
		 * If we want to do any operation after the request we can that here.
		 */
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
		HttpSession session = request.getSession();
		String uri = request.getRequestURI();
		boolean flag=false;
		logger.info("$$$$$$$$$$$$$$$$$Test login $$$$$$$$$$$$$$$$$$$$$$111::::"+uri);
		   if((uri.contains("api") || uri.contains("dashboard")) && session.getAttribute("eaiUserDetails")==null){
			   logger.info("$$$$$$$$$$$$$$$$$Test login $$$$$$$$$$$$$$$$$$$$$$::"+session.getId());
			   session.invalidate();
			   response.sendRedirect(request.getContextPath()+"/invalidSession");
		   }else{
			   flag=true;
		   }
	   return flag;
	}
}

