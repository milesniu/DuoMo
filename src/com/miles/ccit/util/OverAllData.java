package com.miles.ccit.util;

import java.io.File;

import android.os.Environment;

public class OverAllData
{
	public static String Ipaddress = "10.10.22.100";
	public static int Port = 6000;
	public static int networktimeout = 1000 * 5; // 超时时间，初始值10秒
	public static int HeartbeatTime = 1000 * 5; // 心跳间隔时间5秒
	public static String TitleName = "多模终端";
	public static String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.miles.ccit.duomo" + File.separator;
	public static String Priority = "1";//优先级
	public static String Acknowledgemen = "1";	//是否回执

	public static String Account = "";
	public static String Pwd = "";
	
}
