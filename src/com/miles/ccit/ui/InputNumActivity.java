package com.miles.ccit.ui;

import android.os.Bundle;
import android.view.Menu;

import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsToCallActivity;

public class InputNumActivity extends AbsToCallActivity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_num);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.input_num, menu);
		return true;
	}


}
