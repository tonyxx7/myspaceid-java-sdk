package com.myspace.myspaceid.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/*
 * Contains common utilities function which useful across myspaceId
 */
public class Utilities {
	
	
	/**
	 * This method returns timeStamp for given TimeZone
	 * @param timeZone
	 * @return
	 */
	public long getTimeStamp(TimeZone timeZone) 
	{
		long 	 timeStamp = 0;
		Calendar calObj    = new GregorianCalendar(timeZone);
		
		int year    = calObj.get(Calendar.YEAR);            
		int month   = calObj.get(Calendar.MONTH);           
		int date    = calObj.get(Calendar.DATE);           
		int hour 	= calObj.get(Calendar.HOUR_OF_DAY);            
		int minutes = calObj.get(Calendar.MINUTE);       
		int seconds = calObj.get(Calendar.SECOND);       
		
		calObj.setTime(new Date(year-1900,month,date,hour,minutes,seconds));
		
		timeStamp = calObj.getTimeInMillis()/1000;
		
		return timeStamp;
	}

}
