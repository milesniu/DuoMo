package com.miles.ccit.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPTools
{

	public static String getServerData(byte[] buf)
	{
		String acceptStr = null;
		try
		{
			InetAddress serverAddr = InetAddress.getByName(IAcceptServerData.SERVERIP);
			DatagramSocket socket = new DatagramSocket();

//			byte[] buf = str.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, IAcceptServerData.SERVERPORT);
			socket.send(packet);

			byte[] buffer = new byte[packet.getLength()];
			DatagramPacket recvPacket = new DatagramPacket(buffer, buffer.length);
			socket.receive(recvPacket);
			InetAddress ad = recvPacket.getAddress();
			String s = ad.getHostAddress();
//			recvPacket.getData();
			acceptStr = new String(s);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return acceptStr;

	}

}
