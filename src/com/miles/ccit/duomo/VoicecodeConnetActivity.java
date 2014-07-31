package com.miles.ccit.duomo;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.SendDataTask;

public class VoicecodeConnetActivity extends AbsBaseActivity
{

	
	private Button Btn_DisConnet;
	private Button Btn_Talk;
	private TextView text_Num;
	String code = "";
	private Button Btn_KeyBord;
	private LinearLayout linear_Keybord;
	private Button Btn_SendFile;
	private int type;
	private String filepath = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voicecode_connet);
		code = getIntent().getStringExtra("code");
		type = getIntent().getIntExtra("type", 0);
		filepath = getIntent().getStringExtra("filepath");
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
		case R.id.bt_keybrod:
			if(linear_Keybord.getVisibility() == View.GONE)
			{
				linear_Keybord.setVisibility(View.VISIBLE);
			}
			else
			{
				linear_Keybord.setVisibility(View.GONE);
			}
			break;
		case R.id.bt_sendfile:
			Toast.makeText(mContext, filepath, 0).show();
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		Btn_DisConnet = (Button)findViewById(R.id.bt_disconnet);
		Btn_KeyBord = (Button)findViewById(R.id.bt_keybrod);
		Btn_Talk = (Button)findViewById(R.id.bt_talk);
		linear_Keybord = (LinearLayout)findViewById(R.id.linear_inputpanle);
		Btn_SendFile = (Button)findViewById(R.id.bt_sendfile);
		Btn_SendFile.setOnClickListener(this);
		Btn_DisConnet.setOnClickListener(this);
		Btn_KeyBord.setOnClickListener(this);
		text_Num = (TextView) findViewById(R.id.text_number);
		
		if(type==AbsToCallActivity.TOCALLWIREDFILE)
		{
			Btn_SendFile.setVisibility(View.VISIBLE);
		}
		else
		{
			Btn_SendFile.setVisibility(View.GONE);
		}
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
