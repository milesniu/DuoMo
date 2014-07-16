package com.miles.ccit.duomo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.SendDataTask;
import com.miles.ccit.util.UnixTime;

public class HaveCallActivity extends AbsBaseActivity
{

	private TextView text_Num;
	String code = "";
	private Button Btn_Cut;
	private Button Btn_Connet;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_have_call);
		code = getIntent().getStringExtra("code");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.have_call, menu);
		return true;
	}

	public void insetnum(boolean connet)
	{
		BaseMapObject record = new BaseMapObject();
		record.put("id",null);
		record.put("number",code);
		record.put("status",connet?AbsToCallActivity.Recv_Call:AbsToCallActivity.Recv_Error);
		record.put("creattime", UnixTime.getStrCurrentUnixTime());
		record.put("priority", OverAllData.Priority);
		record.put("acknowledgemen", OverAllData.Acknowledgemen);
		record.InsertObj2DB(mContext, "voicecoderecord");
		SendRecvVoicecodetoNet(connet);
	}
	
	public void SendRecvVoicecodetoNet(boolean connet)
	{

		new SendDataTask().execute(APICode.BACK_RECV_VoiceCode+"",OverAllData.Account,connet?"1":"0");
		
	}
	
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_cut:
			
			insetnum(false);
			this.finish();
			break;
		case R.id.bt_connet:
			insetnum(true);
			startActivity(new Intent(mContext, VoicecodeConnetActivity.class).putExtra("code", code));
			this.finish();
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		text_Num = (TextView) findViewById(R.id.text_number);
		Btn_Cut = (Button)findViewById(R.id.bt_cut);
		Btn_Connet = (Button)findViewById(R.id.bt_connet);
		Btn_Cut.setOnClickListener(this);
		Btn_Connet.setOnClickListener(this);
		if (code == null || code.equals(""))
		{
			this.finish();
		} else
		{
			BaseMapObject map = GetData4DB.getObjectByRowName(mContext, "contact", "number", code);
			if (map != null && map.get("name") != null)
			{
				text_Num.setText(map.get("name").toString() + "\r\n" + code);
			} else
			{
				text_Num.setText(code);
			}
		}
	}

}
