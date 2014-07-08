package com.miles.ccit.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.miles.ccit.net.SocketConnection;

public class HeartbeatService extends Service implements Runnable
{

	private Thread mThread;
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
		//暂时关闭心跳
//		SocketConnection.getInstance().launchHeartcheck();

	}
	
	

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId)
	{
		// TODO Auto-generated method stub
		mThread = new Thread(this);
		mThread.start();
		super.onStart(intent, startId);
	}

}
