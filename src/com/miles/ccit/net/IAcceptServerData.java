package com.miles.ccit.net;

public interface IAcceptServerData
{
	public static final String SERVERIP = "255.255.255.255"; // 广播地址
	public static final int SERVERPORT = 6005; // 端口号

	public static final int FindIP = 1;

	public void acceptUdpData(String data, int id);

}
