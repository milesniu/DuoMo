package com.miles.ccit.duomo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.miles.ccit.util.AbsBaseActivity;

public class SettingActivity extends AbsBaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
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
		case R.id.rela_changepws:
			startActivity(new Intent(mContext, ChangePwdActivity.class));
			break;
		case R.id.rela_checklocation:
			startActivity(new Intent(mContext, HostLocationActivity.class));
			break;
		case R.id.rela_checkversion:
			startActivity(new Intent(mContext, HostVersionActivity.class));
			break;
		case R.id.rela_checkstatus:
			startActivity(new Intent(mContext, HostStatusActivity.class));
			break;
		case R.id.rela_syssetting:
			startActivity(new Intent(mContext, SystemCfgActivity.class));
			break;
		case R.id.rela_sendsetting:
			startActivity(new Intent(mContext, SendCfgActivity.class));
			break;
			
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("设置");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		findViewById(R.id.rela_sendsetting).setOnClickListener(this);
		findViewById(R.id.rela_syssetting).setOnClickListener(this);
		findViewById(R.id.rela_checkversion).setOnClickListener(this);
		findViewById(R.id.rela_checklocation).setOnClickListener(this);
		findViewById(R.id.rela_checkstatus).setOnClickListener(this);
		findViewById(R.id.rela_changepws).setOnClickListener(this);
		
	}

}
