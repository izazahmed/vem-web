package com.enerallies.vem.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeFormatValidator {
	
	  private static final String TIME12HOURS_PATTERN = "(?i)(su(,)?|mo(,)?|tu(,)?|we(,)?|th(,)?|fr(,)?|sa(,)?){1,7}(\\s)*:(\\s)*(1[012]|[1-9])(:[0-5][0-9])?(\\s)?(?i)(am|pm)(\\s)*-(\\s)*(1[012]|[1-9])(:[0-5][0-9])?(\\s)?(?i)(am|pm)";
	  private static final String TIME24HOURS_PATTERN = "(?i)(su(,)?|mo(,)?|tu(,)?|we(,)?|th(,)?|fr(,)?|sa(,)?){1,7}(\\s)*:(\\s)*(1[0-9]|2[0-3]|[0-9])(:[0-5][0-9])?(\\s)*-(\\s)*(1[0-9]|2[0-3]|[0-9])(:[0-5][0-9])?";
	  //private static final String TIME_PATTERN = "((?i)(su(,)?|mo(,)?|tu(,)?|we(,)?|th(,)?|fr(,)?|sa(,)?){1,7}(\\s)*:(\\s)*(1[0-9]|2[0-3]|[0-9])(:[0-5][0-9])?(\\s)*-(\\s)*(1[0-9]|2[0-3]|[0-9])(:[0-5][0-9])?|(?i)(su(,)?|mo(,)?|tu(,)?|we(,)?|th(,)?|fr(,)?|sa(,)?){1,7}(\\s)*:(\\s)*(1[012]|[1-9])(:[0-5][0-9])?(\\s)?(?i)(am|pm)(\\s)*-(\\s)*(1[012]|[1-9])(:[0-5][0-9])?(\\s)?(?i)(am|pm))";
	  private static final String TIME_PATTERN = "((?i)(sun(,)?(\\s)*|mon(,)?(\\s)*|tue(,)?(\\s)*|wed(,)?(\\s)*|thu(,)?(\\s)*|fri(,)?(\\s)*|sat(,)?(\\s)*){1,7}(\\s)*:(\\s)*(1[0-9]|2[0-3]|[0-9])(:[0-5][0-9])?(\\s)*-(\\s)*(1[0-9]|2[0-3]|[0-9])(:[0-5][0-9])?|(?i)(sun(,)?(\\s)*|mon(,)?(\\s)*|tue(,)?(\\s)*|wed(,)?(\\s)*|thu(,)?(\\s)*|fri(,)?(\\s)*|sat(,)?(\\s)*){1,7}(\\s)*:(\\s)*(1[012]|[1-9])(:[0-5][0-9])?(\\s)?(?i)(am|pm)(\\s)*-(\\s)*(1[012]|[1-9])(:[0-5][0-9])?(\\s)?(?i)(am|pm))";
	  
	  private static Pattern PATTERN_12HOURS=Pattern.compile(TIME12HOURS_PATTERN);
	  private static Pattern PATTERN_24HOURS = Pattern.compile(TIME24HOURS_PATTERN);
	  private static Pattern PATTERN_HOURS= Pattern.compile(TIME_PATTERN);
	  
	  private static Matcher MATCHER_HOURS;
	  private static Matcher MATCHER_12HOURS;
	  private static Matcher MATCHER_24HOURS;
	  

	  public static boolean validate12HoursFormat(final String time){
		  MATCHER_12HOURS = PATTERN_12HOURS.matcher(time.trim());
		  return MATCHER_12HOURS.matches();
	  }
	  
	  public static boolean validate24HoursFormat(final String time){
		  MATCHER_24HOURS = PATTERN_24HOURS.matcher(time.trim());
		  return MATCHER_24HOURS.matches();
	  }
	  
	  public static boolean validateTimeFormat(final String time){
		  MATCHER_HOURS = PATTERN_HOURS.matcher(time.trim());
		  return MATCHER_HOURS.matches();
	  }
	  

}
