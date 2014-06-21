package com.miles.ccit.ui;

import java.util.HashMap;

import com.miles.ccit.duomo.R;
import com.miles.ccit.duomo.R.layout;
import com.miles.ccit.duomo.R.menu;
import com.miles.ccit.util.BaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.UnixTime;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.RadioButton;

public class NewcontactActivity extends BaseActivity
{

	private EditText edit_Num;
	private EditText edit_Company;
	private EditText edit_Remarks;
	private RadioButton radio_wireness,radio_wired;
	private BaseMapObject tmp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newcontact);
		if(getIntent().getSerializableExtra("contact")!=null)
		{
			tmp = BaseMapObject.HashtoMyself((HashMap<String,Object>)getIntent().getSerializableExtra("contact"));
		}
	}
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.newcontact, menu);
		return true;
	}



	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("联系人");
		Btn_Left.setText("返回");
		Btn_Right.setText("添加");
		
		edit_Num = (EditText)findViewById(R.id.edit_num);
		edit_Company = (EditText)findViewById(R.id.edit_company);
		edit_Remarks = (EditText)findViewById(R.id.edit_remarks);
		radio_wireness = (RadioButton)findViewById(R.id.radio_wireness);
		radio_wired = (RadioButton)findViewById(R.id.radio_wired);
		if(tmp != null)
		{
			Btn_Right.setText("修改");
			edit_Num.setText(tmp.get("number").toString());
			edit_Company.setText(tmp.get("name").toString());
			edit_Remarks.setText(tmp.get("remarks").toString());
			if(tmp.get("type").toString().equals("0"))
			{
				radio_wireness.setChecked(true);
			}
			else
			{
				radio_wired.setChecked(true);
			}
		}
		
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
		case R.id.bt_right:
			BaseMapObject contact = new BaseMapObject();
			contact.put("id",null);
			contact.put("name",edit_Company.getText().toString());
			contact.put("number",edit_Num.getText().toString());
			contact.put("type",radio_wireness.isChecked()?"0":"1");
			contact.put("remarks",edit_Remarks.getText().toString());
			contact.put("creattime",UnixTime.getStrCurrentUnixTime());
			
			contact.InsertObj2DB(mContext, "contact");
			
			
			break;
		}
	}

}
