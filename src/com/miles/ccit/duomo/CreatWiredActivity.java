package com.miles.ccit.duomo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsCreatActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.FileUtils;
import com.miles.ccit.util.MyLog;

public class CreatWiredActivity extends AbsToCallActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_wired);
		all = GetData4DB.getObjectListData(mContext, "contact", "type", "1");
//		CurrentType = TOCALLWIREDVOICE;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creat_wired, menu);
		return true;
	}
	
	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		super.initView();
		findViewById(R.id.buttoncallvoice).setOnClickListener(this);

	}
	

}
