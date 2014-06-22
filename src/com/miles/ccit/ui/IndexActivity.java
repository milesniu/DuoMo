package com.miles.ccit.ui;

import java.io.DataOutputStream;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.miles.ccit.duomo.R;
import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.SocketClient;
import com.miles.ccit.util.BaseActivity;

public class IndexActivity extends BaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.index, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_specialnet:
			startActivity(new Intent(mContext, SpecialNetFragmentActivity.class));
			break;
		case R.id.bt_contact:
			startActivity(new Intent(mContext, ContactActivity.class));
			break;
		case R.id.bt_haveline:
			startActivity(new Intent(mContext, WiredModelActivity.class));
			break;
		case R.id.bt_broadcast:
			break;
		case R.id.bt_specialway:
			break;
		case R.id.bt_setting:
			break;
		case R.id.bt_about:
			new test().execute();
			break;
		}
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
					byte[] buf = df.sendTest();
					out.write(buf);
					out.flush();
					
					
					//等待服务器返回，并阻塞线程
//					red = new byte[256];
//					DataInputStream dis = new DataInputStream(SocketClient.getInstance().getInputStream());//服务器通过输入管道接收数据流  
//					int a = dis.read(red);
//					System.out.println(a);
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
//			Toast.makeText(mContext, red.length+"", Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

	}
	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		findViewById(R.id.bt_specialnet).setOnClickListener(this);
		findViewById(R.id.bt_contact).setOnClickListener(this);
		findViewById(R.id.bt_haveline).setOnClickListener(this);
		findViewById(R.id.bt_broadcast).setOnClickListener(this);
		findViewById(R.id.bt_specialway).setOnClickListener(this);
		findViewById(R.id.bt_setting).setOnClickListener(this);
		findViewById(R.id.bt_about).setOnClickListener(this);
	}

}
