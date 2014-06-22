package com.miles.ccit.util;

import android.util.Log;
import android.widget.Toast;

public class MyLog
{
	public static final boolean isDebug = true;
	
	public static void SystemOut(String msg)
	{
		if(isDebug)
		{
			System.out.println(msg);
		}
	}
	
	public static void LogV(String tag,String msg)
	{
		if(isDebug)
		{
			Log.v(tag, msg);
		}
	}
	
}
