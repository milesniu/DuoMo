package com.miles.ccit.util;

import android.app.Application;
import android.os.Handler;
import android.os.Message;

public class MyApplication extends Application
{

	public static Handler handle = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		handle = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				// TODO Auto-generated method stub
				MyLog.showToast(getApplicationContext(), "重新连接中...");
				super.handleMessage(msg);
			}
			
		};
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
	}

		
}
