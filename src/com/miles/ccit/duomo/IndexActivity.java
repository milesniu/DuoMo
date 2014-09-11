package com.miles.ccit.duomo;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.UnixTime;

public class IndexActivity extends AbsBaseActivity
{

//	public static boolean result = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
//		new SendDataTask().execute(APICode.SEND_Login+"","123456","abdc123");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.index, menu);
		return true;
	}

	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK)		//Back键实现Home键返回，activity后台压栈
		 { 
		        Intent intent = new Intent(Intent.ACTION_MAIN); 
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意  
		        intent.addCategory(Intent.CATEGORY_HOME); 
		        this.startActivity(intent); 
		        return true; 
		 }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(UnixTime.getCurrentUnixTime()>UnixTime.simpleTime2Unix("2014-09-30 00:00:00"))
		{
			MyLog.showToast(mContext, "软件授权期限已过，请联系开发商...");
			return;
		}
		
		
		switch (v.getId())
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
			// new MutiChoiseDlg(mContext,
			// GetData4DB.getObjectListData(mContext, "contact", "type",
			// "0")).getDlg();
			startActivity(new Intent(mContext, BroadCastctivity.class));

			break;
		case R.id.bt_specialway:
			startActivity(new Intent(mContext, SpecialVoiceActivity.class));

			break;
		case R.id.bt_setting:
			startActivity(new Intent(mContext, SettingActivity.class));

			break;
		case R.id.bt_about:
			startActivity(new Intent(mContext, AboutActivity.class));

			break;
		case R.id.linear_title:
			if(!LoginActivity.isLogin)
			{
				startActivityForResult(new Intent(mContext, LoginActivity.class),3);
			}
//			startActivityForResult(new Intent(mContext, LoginActivity.class),3);
//			findViewById(R.id.linear_title).setBackgroundResource(R.drawable.loginok8);
			break;
		}
	}

	

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		if(LoginActivity.isLogin)
		{
			findViewById(R.id.linear_title).setBackgroundResource(R.drawable.loginok8);
			
		}
		else
		{
			findViewById(R.id.linear_title).setBackgroundResource(R.drawable.indextitlenologin);
			
		}
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
//		if(requestCode==3)
//		{
//			if(data!=null&&data.getStringExtra("result").toString().equals("true"))
//			{
//				result = true;
//				findViewById(R.id.linear_title).setBackgroundResource(R.drawable.loginok8);
//				return;
//			}
//		}
//		result = false;
		
		super.onActivityResult(requestCode, resultCode, data);
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
		findViewById(R.id.linear_title).setOnClickListener(this);
	}
	
//	
//	private static String checkResult = "-1";
//	//程序可用性检测地址(阿里云)
//	public static String checkUrl = "http://ossmiles.oss-cn-hangzhou.aliyuncs.com/AppCtrl/com.miles.ccit.duomo.txt";
//	
//	public static  boolean isCanuse()
//	{
//		if(checkResult.equals("-1")||checkResult.equals("0"))
//		{
//			checkResult = GetCheckapp();
//		}
//		return checkResult.equals("0")?false:true;
//	}
//	
//	
//	
//	public static String GetCheckapp()
//	{
//
//		String result = "-1";
//		InputStream is = null;
//		HttpGet httpRequest = new HttpGet(checkUrl);
//		try
//		{
//			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
//			if (httpResponse.getStatusLine().getStatusCode() == 200)
//			{ // 正确
//
//				is = httpResponse.getEntity().getContent();
//				byte[] data = new byte[1024];
//				int n = -1;
//				ByteArrayBuffer buf = new ByteArrayBuffer(10 * 1024);
//				while ((n = is.read(data)) != -1)
//					buf.append(data, 0, n);
//				result = new String(buf.toByteArray(), HTTP.UTF_8);
//				is.close();
//					
//				return result;
//			}
//			else
//			{
//				Log.v("tip==", "error response code");
//				return "";
//			}
//		}
//		catch (Exception e)
//		{
//			Log.e("error==", "" + e.getMessage());
//			return "";
//		}
//	}

}
