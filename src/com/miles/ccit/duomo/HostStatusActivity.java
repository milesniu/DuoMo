package com.miles.ccit.duomo;

import com.miles.ccit.util.AbsBaseActivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class HostStatusActivity extends AbsBaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_status);
		((TextView)findViewById(R.id.text_status)).setText(SettingActivity.LoactionInfo.get("status")+"");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_status, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		this.finish();
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("主机状态");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
	}

}
