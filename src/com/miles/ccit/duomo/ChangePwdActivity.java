package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.SendDataTask;

public class ChangePwdActivity extends AbsBaseActivity
{
	
	private EditText edit_old;
	private EditText edit_new1;
	private EditText edit_new2;
	private MyBroadcastReciver broad = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pwd);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(broad_backchangepwd_Action);
		broad = new MyBroadcastReciver();
		this.registerReceiver(broad, intentFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_pwd, menu);
		return true;
	}
	
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.bt_right:
			String edold = edit_old.getText().toString();
			String ednew1 = edit_new1.getText().toString();
			String ednew2 = edit_new2.getText().toString();
			
			if(!ednew1.equals(ednew2))
			{
				MyLog.showToast(mContext, "两次密码输入不一致...");
				return;
			}
			else
			{
				showprogressdialog();
				new SendDataTask().execute(APICode.SEND_ChangePwd+"",edold,ednew1);
			}
			
			break;
		}
		
	}
	
	public class MyBroadcastReciver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			hideProgressDlg();
			String action = intent.getAction();

			if (action.equals(broad_backchangepwd_Action))
			{
				byte[] con = intent.getByteArrayExtra("data");

				if (con.length > 4 && con[5] == (byte) 0x01)
				{
					MyLog.showToast(mContext, "修改成功...");
					ChangePwdActivity.this.finish();
				} else
				{
					MyLog.showToast(mContext, "修改失败...");
					return;
				}
			} 
		}

	}
	

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("修改密码");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.btsure);
		Btn_Right.setOnClickListener(this);
		
		edit_new1 = (EditText)findViewById(R.id.edit_newpwd);
		edit_new2 = (EditText)findViewById(R.id.edit_confirmpwd);
		edit_old = (EditText)findViewById(R.id.edit_oldpwd);
	}

}
