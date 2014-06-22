package com.miles.ccit.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UnixTime
{
	
	@SuppressLint("SimpleDateFormat")
	public static String unixTime2Simplese(String unixtime,String format)
	{
		Long timestamp = Long.parseLong(unixtime + "") * 1000;
		return new SimpleDateFormat(format).format(new Date(timestamp));
	}
	
	@SuppressLint("SimpleDateFormat")
	public static long simpleTime2Unix(String simpletime) 
	{
		Date date =	null;
		try
		{
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(simpletime);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			return -1;
		}
		return date.getTime() / 1000;
	}
	
	public static String getStrCurrentUnixTime()
	{
		return  System.currentTimeMillis()/1000+"";

	}
}
