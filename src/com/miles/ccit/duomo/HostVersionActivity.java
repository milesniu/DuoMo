package com.miles.ccit.duomo;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.miles.ccit.util.AbsBaseActivity;

public class HostVersionActivity extends AbsBaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_version);
		((TextView)findViewById(R.id.text_version)).setText(SettingActivity.LoactionInfo.get("version")+"");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_version, menu);
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
		initBaseView("软件版本");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
	}
}
