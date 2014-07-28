package com.miles.ccit.duomo;

import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.SendDataTask;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class SystemCfgActivity extends AbsBaseActivity
{

	private EditText edit_senddelay = null;
	private EditText edit_recvdelay = null;
	private Spinner sp_type = null;
	private Spinner sp_power = null;
	
	private String[] Type = new String[]{"民用科顿电台","十五20W电台"};
	private String[] Power = new String[]{"高","中","低"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_cfg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.system_cfg, menu);
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
		case R.id.bt_right:
			if(edit_senddelay.getText().toString().equals(""))
			{
				MyLog.showToast(mContext, "请输入发送延时");
				return;
			}
			if(edit_recvdelay.getText().toString().equals(""))
			{
				MyLog.showToast(mContext, "请输入接收延时");
				return;
			}
			
			
			int send = Integer.parseInt(edit_senddelay.getText().toString());
			int recv = Integer.parseInt(edit_recvdelay.getText().toString());
			
			if(send<0||send>99)
			{
				MyLog.showToast(mContext, "请输入有效发送延时...");
				return;
			}
			if(recv<0||recv>99)
			{
				MyLog.showToast(mContext, "请输入有效接收延时...");
				return;
			}
			int type = sp_type.getSelectedItemPosition();
			int power = sp_power.getSelectedItemPosition();
			
			new SendDataTask().execute(APICode.SEND_Config+"",send+"",recv+"",type+"",power+"");
			break;
		}
		
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("系统设置");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.btsure);
		Btn_Right.setOnClickListener(this);
		
		edit_senddelay = (EditText)findViewById(R.id.edit_senddelay);
		edit_recvdelay = (EditText)findViewById(R.id.edit_recedelay);
		sp_type = (Spinner)findViewById(R.id.sp_casttype);
		sp_power = (Spinner)findViewById(R.id.sp_casrpower);
		
		ArrayAdapter<String> typeadapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, Type);
		sp_type.setAdapter(typeadapter);
		
		ArrayAdapter<String> poweradapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, Power);
		sp_power.setAdapter(poweradapter);
		
		
	}

}
