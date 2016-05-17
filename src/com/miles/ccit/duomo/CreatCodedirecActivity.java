package com.miles.ccit.duomo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.MutiChoiseDlg;

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
			new MutiChoiseDlg(mContext, GetData4DB.getObjectListData(mContext, "contact", "type", "0"),0).getDlg(edit_inputContact);
			break;
		case R.id.img_sky:
			goCreatCode(CreatcodeSkyActivity.class);
			break;
		case R.id.img_earth:
			goCreatCode(CreatcodeEarthActivity.class);
			break;
		case R.id.img_water:
			goCreatCode(CreatcodeWaterActivity.class);
			break;
		case R.id.img_other:
			goCreatCode(CreatcodeOtherActivity.class);
			break;
		case R.id.img_action:
			goCreatCode(CreatcodeActionActivity.class);
			break;
		case R.id.img_actionback:
			goCreatCode(CreatcodeActionbackActivity.class);
			break;
		case R.id.img_applyres:
			goCreatCode(CreatcodeApplyresActivity.class);
			break;
		case R.id.img_warning:
			goCreatCode(CreatcodeWarningActivity.class);
			break;
		case R.id.img_ctrl:
			goCreatCode(CreatcodeCtrlActivity.class);
			break;
		case R.id.img_bddata:
			goCreatCode(CreatBDdataActivity.class);
			break;
		}
		
	}

	private void goCreatCode( Class<?> cls)
	{
		if(edit_inputContact.getText().toString().equals(""))
		{
			Toast.makeText(mContext, "联系人为空，无法新建。", Toast.LENGTH_SHORT).show();
			return;
		}
		startActivity(new Intent(mContext, cls).putExtra("contact", edit_inputContact.getText().toString()));
		
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
		findViewById(R.id.img_sky).setOnClickListener(this);
		findViewById(R.id.img_earth).setOnClickListener(this);
		findViewById(R.id.img_water).setOnClickListener(this);
		findViewById(R.id.img_other).setOnClickListener(this);
		findViewById(R.id.img_action).setOnClickListener(this);
		findViewById(R.id.img_actionback).setOnClickListener(this);
		findViewById(R.id.img_applyres).setOnClickListener(this);
		findViewById(R.id.img_warning).setOnClickListener(this);
		findViewById(R.id.img_ctrl).setOnClickListener(this);
		findViewById(R.id.img_bddata).setOnClickListener(this);
	}

}
