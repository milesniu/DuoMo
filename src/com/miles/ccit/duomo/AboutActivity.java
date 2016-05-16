package com.miles.ccit.duomo;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.miles.ccit.util.AbsBaseActivity;

public class AboutActivity extends AbsBaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		}
	}

	@Override
	public void initView()
	{
		initBaseView("关于我们");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
	}

}
