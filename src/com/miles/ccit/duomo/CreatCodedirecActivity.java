package com.miles.ccit.duomo;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.MutiChoiseDlg;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreatCodedirecActivity extends AbsBaseActivity
{
	public EditText edit_inputContact;
	public Button Btn_addContact;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_codedirec);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creat_codedirec, menu);
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
		case R.id.bt_addcontact:
			new MutiChoiseDlg(mContext, GetData4DB.getObjectListData(mContext, "contact", "type", "0")).getDlg(edit_inputContact);
			break;
		}
		
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("新建代码");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		Btn_addContact = (Button) findViewById(R.id.bt_addcontact);
		edit_inputContact = (EditText) findViewById(R.id.edit_concotact);
		Btn_addContact.setOnClickListener(this);
	}

}
