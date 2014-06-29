package com.miles.ccit.ui;

import java.util.HashMap;

import com.miles.ccit.duomo.R;
import com.miles.ccit.duomo.R.layout;
import com.miles.ccit.duomo.R.menu;
import com.miles.ccit.util.AbsBaseActivity;
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
import android.widget.Toast;

public class CreatContactActivity extends AbsBaseActivity
{

	private EditText edit_Num;
	private EditText edit_Company;
	private EditText edit_Remarks;
	private RadioButton radio_wireness,radio_wired;
	private BaseMapObject tmp;
	private String havecode="";
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newcontact);
		if(getIntent().getSerializableExtra("contact")!=null)
		{
			tmp = BaseMapObject.HashtoMyself((HashMap<String,Object>)getIntent().getSerializableExtra("contact"));
		}
		else if(getIntent().getStringExtra("number")!=null)
		{
			havecode = getIntent().getStringExtra("number");
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
//		Btn_Left.setText("返回");
//		Btn_Right.setText("添加");
		Btn_Right.setBackgroundResource(R.drawable.btsure);
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
		else
		{
			edit_Num.setText(havecode);
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
			String name = edit_Company.getText().toString();
			String number = edit_Num.getText().toString();
			String type = radio_wireness.isChecked()?"0":"1";
			long ret = 0;
			if(name.equals("")||number.equals(""))
			{
				Toast.makeText(mContext, "必要信息不能为空...", 0).show();
				return;
			}
			if(tmp == null)
			{
				BaseMapObject contact = new BaseMapObject();
				contact.put("id",null);
				contact.put("name",name);
				contact.put("number",number);
				contact.put("type",type);
				contact.put("remarks",edit_Remarks.getText().toString());
				contact.put("creattime",UnixTime.getStrCurrentUnixTime());
				ret = contact.InsertObj2DB(mContext, "contact");
				if(ret==-1)
				{
					Toast.makeText(mContext, "号码已经存在，请勿重复添加...", 0).show();
					return;
				}
			}
			else
			{
				tmp.put("name",edit_Company.getText().toString());
				tmp.put("number",edit_Num.getText().toString());
				tmp.put("type",radio_wireness.isChecked()?"0":"1");
				tmp.put("remarks",edit_Remarks.getText().toString());
				tmp.UpdateMyself(mContext, "contact");
			}
			this.finish();
			break;
		}
	}

}
