package com.miles.ccit.service;

import java.io.OutputStream;
import java.io.PrintWriter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.miles.ccit.net.SocketClient;


public class HeartbeatService extends Service implements Runnable  {

	private Thread mThread;
	private static int delayTime = 5000;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			try
			{
			PrintWriter output = null;
	
			OutputStream out = SocketClient.getInstance().getOutputStream();
	
			// 注意第二个参数据为true将会自动flush，否则需要需要手动操作out.flush()
			output = new PrintWriter(out, true);
	
			output.println("1@aaaaaaaaaa");
			Thread.sleep(delayTime);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		mThread = new Thread(this);  
        mThread.start();  
		super.onStart(intent, startId);
	}

	
	
	

}
