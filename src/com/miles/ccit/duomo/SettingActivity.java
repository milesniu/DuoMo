package com.miles.ccit.duomo;

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
		
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("设置");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		
		
		
	}

}
