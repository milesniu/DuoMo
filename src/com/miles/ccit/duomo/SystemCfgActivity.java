package com.miles.ccit.duomo;

import com.miles.ccit.util.AbsBaseActivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class SystemCfgActivity extends AbsBaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_cfg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.system_cfg, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_left:
			break;
		case R.id.bt_right:
			break;
		}
		this.finish();
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("系统设置");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.btsure);
		Btn_Right.setOnClickListener(this);
	}

}
