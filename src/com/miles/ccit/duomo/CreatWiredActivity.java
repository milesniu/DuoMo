package com.miles.ccit.duomo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsEmailCodeActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.FileUtils;

public class CreatWiredActivity extends AbsToCallActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_wired);
		all = GetData4DB.getObjectListData(mContext, "contact", "type", "1");
		CurrentType = TOCALLWIREDVOICE;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case 0:
			if (resultCode == RESULT_OK)
			{
				// Get the Uri of the selected file
				Uri uri = data.getData();
				String path = FileUtils.getPath(this, uri);
				String name = AbsEmailCodeActivity.getFileName(path);
				insertWiredRecord(mContext,strNumber, path);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
		findViewById(R.id.buttoncallfile).setOnClickListener(this);
		
	}
	

}
