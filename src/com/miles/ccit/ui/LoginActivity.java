package com.miles.ccit.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.miles.ccit.duomo.R;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.SendDataTask;

public class LoginActivity extends AbsBaseActivity
{
	private EditText edit_Account;
	private EditText edit_Password;
	private EditText edit_ip;
	private MyBroadcastReciver broad = null;
	public static boolean isLogin = false;
//	private Timer timer = null;
//	private TimerTask ttask = null;
//	private int deltime = 5;
//	Handler hander = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg)
//		{
//			// TODO Auto-generated method stub
//			Intent it = new Intent();
//			it.putExtra("result", "false");
//			it.putExtra("data", "");
//			LoginActivity.this.setResult(Activity.RESULT_OK, it);
//			MyLog.showToast(mContext, "登陆超时...");
//			LoginActivity.this.finish();
//			super.handleMessage(msg);
//		}
//		
//	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
//		ttask = new TimerTask()
//		{
//			
//			@Override
//			public void run()
//			{
//				// TODO Auto-generated method stub
//				while((deltime--)<=0)
//				{
//					hander.sendMessage(new Message())
//				}
//			}
//		};
	}


	@Override
	public boolean isDestroyed()
	{
		// TODO Auto-generated method stub
		if(broad!=null)
		{
			this.unregisterReceiver(broad);
		}
		return super.isDestroyed();
	}



	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("登录");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);

		edit_Account = (EditText) findViewById(R.id.edit_account);
		edit_Password = (EditText) findViewById(R.id.edit_pwd);
		edit_ip = (EditText)findViewById(R.id.edit_ip);
		findViewById(R.id.bt_login).setOnClickListener(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(broad_login_Action);
		broad = new MyBroadcastReciver();
		this.registerReceiver(broad, intentFilter);
	}
	
	public class MyBroadcastReciver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			hideProgressDlg();
			String action = intent.getAction();

			if (action.equals(broad_login_Action))
			{
				byte[] con = intent.getByteArrayExtra("data");

				if (con == null || con.length < 4)
				{
					Intent it = new Intent();
					it.putExtra("result", "false");
					it.putExtra("data", con);
					LoginActivity.this.setResult(Activity.RESULT_OK, it);
					MyLog.showToast(mContext, "登陆超时");
					isLogin = false;
					LoginActivity.this.finish();
					return;
				}
				if (con.length > 4 && con[5] == (byte) 0x01)
				{
					checkContact(con);

					Intent it = new Intent();
					it.putExtra("result", "true");
					it.putExtra("data", con);
					LoginActivity.this.setResult(Activity.RESULT_OK, it);
					MyLog.showToast(mContext, "登陆成功");
					isLogin = true;
					LoginActivity.this.finish();
				} else
				{
					MyLog.showToast(mContext, "登陆失败，请检查用户名及密码");
					return;
				}
			} else if (action.equals(""))
			{

			}
		}

	}

	

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.bt_login:
			String name = edit_Account.getText().toString();
			String pwd = edit_Password.getText().toString();
			String ip = edit_ip.getText().toString();
			OverAllData.Ipaddress = ip;
			if (name.equals(""))
			{
				MyLog.showToast(mContext, "用户账号不能为空...");
				return;
			}
			if (pwd.equals(""))
			{
				MyLog.showToast(mContext, "密码不能为空...");
				return;
			}
			showprogressdialog();
			OverAllData.Account = name;
			OverAllData.Pwd = pwd;
			new SendDataTask().execute(APICode.SEND_Login + "", name, pwd);
//			timer = new Timer();
//			timer.schedule(ttask, 100, 5000);
			break;
		}
	}

	
}
