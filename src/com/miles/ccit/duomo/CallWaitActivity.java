package com.miles.ccit.duomo;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;

public class CallWaitActivity extends AbsBaseActivity
{

	private TextView text_Num;
	String code = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_wait);
		code = getIntent().getStringExtra("code");
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.call_wait, menu);
		return true;
	}


	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_cut:
			this.finish();
			break;
		}
	}


	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		findViewById(R.id.bt_cut).setOnClickListener(this);
		text_Num = (TextView)findViewById(R.id.text_number);
		if(code==null||code.equals(""))
		{
			this.finish();
		}
		else
		{
			BaseMapObject map = GetData4DB.getObjectByRowName(mContext, "contact", "number", code);
			if(map!=null&&map.get("name")!=null)
			{
				text_Num.setText(map.get("name").toString());
			}
			else
			{
				text_Num.setText(code);
			}
		}
		
	}


}
