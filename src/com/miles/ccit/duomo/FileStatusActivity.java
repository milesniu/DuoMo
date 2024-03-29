package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miles.ccit.duomo.CallWaitActivity.MyBroadcastReciver;
import com.miles.ccit.util.AbsCreatActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.UnixTime;

public class FileStatusActivity extends AbsCreatActivity
{

	private String filepath = null;
	private ProgressBar progressBar = null;
	private TextView text_progress;
	private MyBroadcastReciver broad = null;
	public static String code = "";
	public static String recvpath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_status);
		filepath = getIntent().getStringExtra("path");
		code = getIntent().getStringExtra("code");
		if (filepath != null)
		{
			// 发送文件
			sendFile(filepath);
			
		} else
		{
			// 接收文件
			sendrecvFile();
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(broad_fileprogress_Action);
		intentFilter.addAction(broad_fileresult_Action);
		broad = new MyBroadcastReciver();
		this.registerReceiver(broad, intentFilter);
	}

	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
		case R.id.bt_cut:
			filepath = null;
			code = null;
			recvpath = null;
			this.finish();
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		findViewById(R.id.bt_cut).setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		text_progress = (TextView) findViewById(R.id.text_progress);
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(broad);
		super.onDestroy();
	}
	
	
	public class MyBroadcastReciver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			// hideProgressDlg();
			String action = intent.getAction();

			BaseMapObject record = new BaseMapObject();
			record.put("id",null);
			record.put("number",FileStatusActivity.code);
			record.put("sendtype","1");//语音0.文件1
			
			record.put("filepath",filepath);
			record.put("creattime", UnixTime.getStrCurrentUnixTime());
			
			
			
			if (action.equals(broad_fileprogress_Action))
			{
				// 更新进度
				int p = intent.getIntExtra("progress", 0);
				progressBar.setProgress(p);
				text_progress.setText(p + "%");
				if (p >= 100)
				{
				}
			} else if (action.equals(broad_fileresult_Action))
			{
				// 文件发送或者接受结果
				if (intent.getStringExtra("data").equals("true"))
				{
					
					if (filepath == null)
					{
						sendrecvresultFile();
						record.put("status",AbsCreatActivity.RECVFROM+"");
						AbsCreatActivity.showFile(mContext, AbsCreatActivity.getFileName(recvpath), recvpath);
						MyLog.showToast(mContext, "文件接收成功！");
					}
					else
					{
						record.put("status",AbsCreatActivity.SENDSUCCESS+"");
						MyLog.showToast(mContext, "文件发送成功！");
						
					}
					
				} else
				{
					record.put("status",AbsCreatActivity.SENDERROR+"");
					MyLog.showToast(mContext, "文件发送失败！");

				}
				record.InsertObj2DB(mContext, "wiredrecord");
				FileStatusActivity.this.finish();
			}
		}

	}

}
