package com.miles.ccit.duomo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.SocketClient;
import com.miles.ccit.service.HeartbeatService;
import com.miles.ccit.util.BaseActivity;
import com.miles.ccit.util.HexSwapString;

public class MainActivity extends BaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startService(new Intent(mContext, HeartbeatService.class));

		findViewById(R.id.text).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				if(!SocketClient.getInstance().isConnected())
				{
					//发送UDP广播，进行连接
				}
				else
				{
					new test().execute();
			
				}
			}
		});

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

}
