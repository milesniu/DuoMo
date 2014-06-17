package com.miles.ccit.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient
{

	private static SocketClient instance = null;
	private Socket tSocket;
	private static String IpAddr = "192.168.1.113";
	private static int Port = 6000;

	private SocketClient(String ip, int port)
	{
		try
		{
//			InetAddress addr = InetAddress.getByName(ip);
//			tSocket = new Socket(addr, port);
			
			InetAddress addr = InetAddress.getByName(ip);
			tSocket = new Socket();
			InetSocketAddress socketAddress = new InetSocketAddress(addr, Port);
			//(addr, Integer.parseInt(equip.getEquipPort()));
			tSocket.connect(socketAddress, 10000);	//10s超时连接
			
		} catch (Exception e)
		{
			tSocket = new Socket();
		}
	}

	private static class SingletonHolder
	{
		/** 单例变量 */
		private static SocketClient instance = new SocketClient(IpAddr, Port);
	}

	private Socket getSocket()
	{
		return tSocket;
	}

	public static Socket getInstance()
	{
		return SingletonHolder.instance.getSocket();
	}

}
