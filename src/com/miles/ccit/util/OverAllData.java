package com.miles.ccit.util;

import java.io.File;

import android.os.Environment;

public class OverAllData
{
	public static String Ipaddress = "192.168.1.44";
	public static int Port = 6000;
	public static int networktimeout = 1000 * 10; // 超时时间，初始值10秒
	public static int HeartbeatTime = 1000 * 5; // 心跳间隔时间5秒
	public static String TitleName = "DuoMo";
	public static String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.miles.ccit.duomo" + File.separator;
	public static String Priority = "1";//优先级
	public static String Acknowledgemen = "1";	//是否回执
}
