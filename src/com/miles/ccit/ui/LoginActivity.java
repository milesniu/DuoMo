package com.miles.ccit.ui;

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
	public static final String broadAction = "cn.broadcast.login";
	
	
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

		findViewById(R.id.bt_login).setOnClickListener(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(broadAction);
		this.registerReceiver(new MyBroadcastReciver(), intentFilter);
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
			break;
		}
	}

	
}
