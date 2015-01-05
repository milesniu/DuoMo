package com.miles.ccit.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.miles.ccit.duomo.AboutActivity;
import com.miles.ccit.duomo.BroadCastctivity;
import com.miles.ccit.duomo.ContactActivity;
import com.miles.ccit.duomo.LoginActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.duomo.SettingActivity;
import com.miles.ccit.duomo.SpecialNetFragmentActivity;
import com.miles.ccit.duomo.SpecialVoiceActivity;
import com.miles.ccit.duomo.WiredModelActivity;
import com.miles.ccit.duomo.R.drawable;
import com.miles.ccit.duomo.R.id;
import com.miles.ccit.duomo.R.layout;
import com.miles.ccit.duomo.R.menu;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.FileUtils;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.UnixTime;

public class IndexActivity extends AbsBaseActivity
{

//	public static boolean result = false;
	private MyBroadcastReciver broad = null;
	private BaseMapObject checkCount = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		checkCount = FileUtils.getMapData4SD();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(broad_usimout_Action);
		broad = new MyBroadcastReciver();
		this.registerReceiver(broad, intentFilter);
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
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(broad);
		super.onDestroy();
		
	}

	public class MyBroadcastReciver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			hideProgressDlg();
			String action = intent.getAction();

			if (action.equals(broad_usimout_Action))
			{
				findViewById(R.id.linear_title).setBackgroundResource(R.drawable.indextitlenologin);
				return;
			} 
		}

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
		int count = Integer.parseInt(checkCount.get("count").toString());
		if(count>=600)
		{
			MyLog.showToast(mContext, "测试版使用已到期，请联系开发商...");
			return;
		}
		else
		{
			checkCount.put("count", (count+1));
			FileUtils.setMapData2SD(checkCount);
		}
		
		if(UnixTime.getCurrentUnixTime()>UnixTime.simpleTime2Unix("2015-01-30 00:00:00"))
		{
			MyLog.showToast(mContext, "测试版使用已到期，请联系开发商...");
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
