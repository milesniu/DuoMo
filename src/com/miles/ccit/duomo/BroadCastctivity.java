package com.miles.ccit.duomo;

import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.MyLog;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class BroadCastctivity extends AbsBaseActivity
{

	private EditText edit_Boundry;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broad_castctivity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.broad_castctivity, menu);
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
		case R.id.bt_commit:
			if(edit_Boundry.getText().toString().equals(""))
			{
				MyLog.showToast(mContext, "请输入有效频率");
				return;
			}
			else
			{
				//进行设置
				edit_Boundry.setEnabled(false);
			}
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("广播模式");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		edit_Boundry = (EditText)findViewById(R.id.edit_boundry);
		findViewById(R.id.bt_commit).setOnClickListener(this);
	}

}
