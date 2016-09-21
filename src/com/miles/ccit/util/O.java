package com.miles.ccit.util;

import java.io.File;

import android.os.Environment;

public class O {
    public static String Ipaddress = "192.168.1.26";
    public static String RTPIpaddress = "192.168.1.26";
    public static String LOCALIP = "192.168.1.255";
    public static int Port = 6000;
    //	public static int SendPort = 6001;
//	public static int RecvPort = 6002;
    public static int LocalRTPPortPort = 6002;
    public static int RomoteRTPPort = 6002;
    public static int networktimeout = 1000 * 5; // 超时时间，初始值10秒
    public static int HeartbeatTime = 1000 * 5; // 心跳间隔时间5秒
    public static String TitleName = "综合接入设备";
    public static String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.miles.ccit.duomo" + File.separator;
    public static String Priority = "1";//优先级
    public static String Acknowledgemen = "1";    //是否回执

    public static String Account = "";
    public static String Pwd = "";
    public static String loginPath = SDCardRoot + "android.dat";

    public static final int WIRENESS = 0;
    public static final int WIRED = 1;
    public static final int NET = 2;

    public static boolean isEncrypt = false;

}
