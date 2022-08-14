package com.enerallies.vem.configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.PropertyConfigurator;

@WebListener
public class AppContextListener  implements ServletContextListener {
	private String LOG_LOCATION = "vem.log4j.qa";
    public void contextInitialized(ServletContextEvent servletContextEvent) {  
    	if("/vem".equals(servletContextEvent.getServletContext().getContextPath()))
    		LOG_LOCATION="vem.log4j";
        
        if(System.getProperty(LOG_LOCATION)!=null && !System.getProperty(LOG_LOCATION).equalsIgnoreCase("")){
        	PropertyConfigurator.configure(System.getProperty(LOG_LOCATION));
        }     
    }
           
    public void contextDestroyed(ServletContextEvent servletContextEvent) {        
         
    }
     
}
