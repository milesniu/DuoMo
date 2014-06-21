package com.miles.ccit.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.miles.ccit.net.SocketConnection;
import com.miles.ccit.util.OverAllData;

public class HeartbeatService extends Service implements Runnable
{

	private Thread mThread;
	private Timer timer;
	private String result;
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
		SocketConnection.getInstance().launchHeartcheck();
//		while (true)
//		{	
//			timer = new Timer();
//			timer.schedule(new TimerTask()
//			{
//				
//				@Override
//				public void run()
//				{
//					// TODO Auto-generated method stub
//					try
//					{
//						DataOutputStream out = new DataOutputStream(SocketClient.getInstance().getOutputStream());
//						ComposeData df = new ComposeData();
//						byte[] buf = df.sendHeartbeat();
//						out.write(buf);
//						out.flush();

						/**需要服务器回复，请解开以下注释*/
//						byte[] red = new byte[256];
//						DataInputStream dis = new DataInputStream(SocketClient.getInstance().getInputStream());//服务器通过输入管道接收数据流  
//						dis.read(buf);
//
//						Thread.sleep(HeartdelayTime);
//					}
//					catch (Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//			}, 100, HeartdelayTime);
//			
//			
//		}
	}
	
	

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId)
	{
		// TODO Auto-generated method stub
		mThread = new Thread(this);
		mThread.start();
		
		
//		new ReadThread().start();
		super.onStart(intent, startId);
	}

}
