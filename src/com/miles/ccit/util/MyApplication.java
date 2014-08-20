package com.miles.ccit.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.SDKInitializer;
import com.miles.ccit.duomo.LoginActivity;

public class MyApplication extends Application
{

	public static Handler handle = null;
	 public static MyApplication mcontext;
	@Override
	public void onCreate() {
		super.onCreate();
		mcontext=this;
		SDKInitializer.initialize(getApplicationContext()); 
		handle = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mcontext, LoginActivity.class);
				 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				 mcontext.startActivity(intent);
//				mcontext.startActivity(new Intent(mcontext, LoginActivity.class));
				MyLog.showToast(getApplicationContext(), "连接断开，请重新登陆");
				super.handleMessage(msg);
			}
			
		};
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
	}

	
	 public static Context getAppContext(){
	        return mcontext;
	    }
	    public static Resources getAppResources(){
	        return getAppResources();
	    }
		
}
