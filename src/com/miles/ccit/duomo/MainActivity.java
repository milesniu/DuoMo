package com.miles.ccit.duomo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
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

		// BaseMapObject ob = new BaseMapObject();
		// ob.put("id", null);
		// ob.put("frequency", "112.3");
		// ob.put("creattime", "2222");
		// ob.InsertObj2DB(mContext, "specialway");
		// ob.InsertObj2DB(mContext, "specialway");
		// ob.InsertObj2DB(mContext, "specialway");
		// ob.InsertObj2DB(mContext, "specialway");
		// ob.InsertObj2DB(mContext, "specialway");
		// List<Map<String, String>> list=
		// GetData4DB.getObjectListData(mContext, "specialway");
		//
		// Map<String, String> data = GetData4DB.getObjectByid(mContext,
		// "specialway", "1");
		//
		// ob.InsertObj2DB(mContext, "specialway");
		// new test().execute();
		startService(new Intent(mContext, HeartbeatService.class));
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		findViewById(R.id.text).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				new test().execute();
			}
		});

	}


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
					byte[] red = new byte[256];
					DataInputStream dis = new DataInputStream(SocketClient.getInstance().getInputStream());//服务器通过输入管道接收数据流  
					dis.read(buf);

					
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			// TODO Auto-generated method stub
			String a = result;
			Toast.makeText(mContext, a, Toast.LENGTH_LONG).show();
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
