package com.miles.ccit.duomo;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
	MediaPlayer player;
	int type = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_have_call);
		code = getIntent().getStringExtra("code");
		type = getIntent().getIntExtra("type", -1);
		try
		{
			player = MediaPlayer.create(mContext, R.raw.callbeep);

			player.stop();
			player.prepare();
			player.start();
			player.setOnCompletionListener(new OnCompletionListener()
			{

				@Override
				public void onCompletion(MediaPlayer mp)
				{
					// TODO Auto-generated method stub
					player.release();
					player = null;
					HaveCallActivity.this.finish();
				}
			});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.have_call, menu);
		return true;
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		if (player!=null && player.isPlaying())
		{
			player.stop();
			player.release();
			player = null;
		}
		super.onDestroy();
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
		new SendDataTask().execute(APICode.BACK_RECV_VoiceCode+"",OverAllData.Account,connet?"1":"0");
		
	}
	
	public void insetwiredvoicenum(boolean connet)
	{
		BaseMapObject record = new BaseMapObject();
		record.put("id",null);
		record.put("number",code);
		record.put("sendtype","0");//语音0.文件1
		record.put("status",connet?AbsToCallActivity.Recv_Call:AbsToCallActivity.Recv_Error);//呼入成功/呼出成功/呼入失败/呼出失败(1,2,3,4)
		record.put("filepath",null);
		record.put("creattime", UnixTime.getStrCurrentUnixTime());
		
		record.InsertObj2DB(mContext, "wiredrecord");
		new SendDataTask().execute(APICode.BACK_RECV_WiredVoice+"",OverAllData.Account,connet?"1":"0");
		
	}
	
	
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_cut:
			if(type == AbsToCallActivity.TOCALLVOICE)
			{
				insetnum(false);
			}
			else if(type == AbsToCallActivity.TOCALLWIREDVOICE)
			{
				insetwiredvoicenum(false);
			}
			
			this.finish();
			break;
		case R.id.bt_connet:
			if(type == AbsToCallActivity.TOCALLVOICE)
			{
				insetnum(true);
			}
			else if(type == AbsToCallActivity.TOCALLWIREDVOICE)
			{
				insetwiredvoicenum(true);
			}
			startActivity(new Intent(mContext, VoicecodeConnetActivity.class).putExtra("code", code).putExtra("type", type));
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
