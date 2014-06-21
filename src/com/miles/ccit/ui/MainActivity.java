package com.miles.ccit.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.miles.ccit.duomo.R;
import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.SocketClient;
import com.miles.ccit.service.HeartbeatService;
import com.miles.ccit.util.BaseActivity;

public class MainActivity extends BaseActivity
{

	
	Handler rhandler = new Handler()
	{
		public void handleMessage(Message message)
		{
			super.handleMessage(message);
			MainActivity.this.startActivity(new Intent(MainActivity.this, IndexActivity.class));
			MainActivity.this.finish();
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startService(new Intent(mContext, HeartbeatService.class));
//		startActivity(new Intent(this, IndexActivity.class));
		
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				rhandler.sendEmptyMessageDelayed(1, 100);
			}
		}, 2000);

	}

	byte[] red = null;
	class test extends AsyncTask<Void, Void, String>
	{
		
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
				try
				{
					DataOutputStream out = new DataOutputStream(SocketClient.getInstance().getOutputStream());
					ComposeData df = new ComposeData();
					byte[] buf = df.sendHeartbeat();
					out.write(buf);
					out.flush();
					
					
					//等待服务器返回，并阻塞线程
					red = new byte[256];
					DataInputStream dis = new DataInputStream(SocketClient.getInstance().getInputStream());//服务器通过输入管道接收数据流  
					int a = dis.read(red);
					System.out.println(a);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return e.toString();
				}
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			// TODO Auto-generated method stub
			//连接断开，管道破裂
			Toast.makeText(mContext, red.length+"", Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		
	}

}
