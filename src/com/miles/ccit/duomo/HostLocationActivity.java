package com.miles.ccit.duomo;

import com.miles.ccit.util.AbsBaseActivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class HostLocationActivity extends AbsBaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_location);
		((TextView)findViewById(R.id.text_lng)).setText(SettingActivity.LoactionInfo.get("lng")+"");
		((TextView)findViewById(R.id.text_lat)).setText(SettingActivity.LoactionInfo.get("lat")+"");
		((TextView)findViewById(R.id.text_high)).setText(SettingActivity.LoactionInfo.get("high")+"");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_location, menu);
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
		initBaseView("位置信息");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
	}
}
