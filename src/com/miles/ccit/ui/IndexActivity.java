package com.miles.ccit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.miles.ccit.duomo.R;
import com.miles.ccit.util.BaseActivity;

public class IndexActivity extends BaseActivity implements OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		
		findViewById(R.id.bt_specialnet).setOnClickListener(this);
		findViewById(R.id.bt_contact).setOnClickListener(this);
		findViewById(R.id.bt_haveline).setOnClickListener(this);
		findViewById(R.id.bt_broadcast).setOnClickListener(this);
		findViewById(R.id.bt_specialway).setOnClickListener(this);
		findViewById(R.id.bt_setting).setOnClickListener(this);
		findViewById(R.id.bt_about).setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.index, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_specialnet:
			startActivity(new Intent(mContext, SpecialNetFragmentActivity.class));
			break;
		case R.id.bt_contact:
			startActivity(new Intent(mContext, ContactActivity.class));
			break;
		case R.id.bt_haveline:
			break;
		case R.id.bt_broadcast:
			break;
		case R.id.bt_specialway:
			break;
		case R.id.bt_setting:
			break;
		case R.id.bt_about:
			break;
		}
	}

}
