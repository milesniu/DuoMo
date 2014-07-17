package com.miles.ccit.duomo;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.SendDataTask;

public class VoicecodeConnetActivity extends AbsBaseActivity
{

	
	private Button Btn_DisConnet;
	private Button Btn_Talk;
	private TextView text_Num;
	String code = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voicecode_connet);
		code = getIntent().getStringExtra("code");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voicecode_connet, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_disconnet:
			new SendDataTask().execute(APICode.SEND_NormalInteraput+"",OverAllData.Account);
			
			this.finish();
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		Btn_DisConnet = (Button)findViewById(R.id.bt_disconnet);
		Btn_Talk = (Button)findViewById(R.id.bt_talk);
		Btn_DisConnet.setOnClickListener(this);
		text_Num = (TextView) findViewById(R.id.text_number);
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
		Btn_Talk.setOnTouchListener(new OnTouchListener()
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					findViewById(R.id.voice_hint_layout).setVisibility(View.VISIBLE);
					((AnimationDrawable) ((ImageView) findViewById(R.id.voice_hint_flash)).getDrawable()).start();
					((TextView) findViewById(R.id.voiceHintText)).setText("松开手指,停止讲话");
					
					SendTalktoNet(true);
					break;
				case MotionEvent.ACTION_UP:
					findViewById(R.id.voice_hint_layout).setVisibility(View.GONE);
					((TextView) findViewById(R.id.voiceHintText)).setText("松开手指发送");
					
					SendTalktoNet(false);
					break;
				}
				return false;
			}
		});
	}
	public void SendTalktoNet(boolean connet)
	{

		new SendDataTask().execute(APICode.SEND_TalkVoiceCode+"",OverAllData.Account,connet?"1":"0");
		
	}
	

}
