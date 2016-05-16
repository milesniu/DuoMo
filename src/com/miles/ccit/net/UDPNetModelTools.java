package com.miles.ccit.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPNetModelTools {

    public static String SendNteData(byte[] buf, String ip) {
        String acceptStr = null;
        try {
            InetAddress serverAddr = InetAddress.getByName(ip);
            DatagramSocket socket = new DatagramSocket();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, 6080);
            socket.send(packet);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return acceptStr;

    }

}
