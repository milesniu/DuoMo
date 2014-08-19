package com.miles.ccit.util;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.miles.ccit.duomo.R;

public abstract class AbsCreatCodeActivity extends AbsBaseActivity
{
	public Button Btn_addOption;
	public ListView list_Content;
	
	protected abstract void goAddOption();
	
	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		if(arg0 == Btn_addOption)
		{
			goAddOption();
		}
	}
	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		list_Content = (ListView)findViewById(R.id.list_content);
		Btn_addOption = (Button)findViewById(R.id.bt_addoption);
	}
	
	
	
	
}
