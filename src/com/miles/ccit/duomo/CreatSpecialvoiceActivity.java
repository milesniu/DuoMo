package com.miles.ccit.duomo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsVoiceActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.SendDataTask;
import com.miles.ccit.util.UnixTime;

public class CreatSpecialvoiceActivity extends AbsVoiceActivity
{

//	private Button Btn_Talk;
	private Button Btn_Commit;
	private EditText  edit_frequency;
	private Button Btn_Talk;
	private String number;
	private MyBroadcastReciver broad = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_specialvoice);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(broad_specialvoice_Action);
		broad = new MyBroadcastReciver();
		this.registerReceiver(broad, intentFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creat_specialvoice, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		
		case R.id.bt_commit:
			number= edit_frequency.getText().toString();
			if(number.equals(""))
			{
				MyLog.showToast(mContext, "请输入有效频率(3~30MHz)");
				return;
			}
			else
			{
				double num = Double.parseDouble(number);
				if(num<3||num>30)
				{
					MyLog.showToast(mContext, "请输入有效频率(3~30MHz)");
					return;
				}
				else
				{
					insertSpecical(number);
					new SendDataTask().execute(APICode.SEND_SpecialVoice+"",OverAllData.Account,number);
				}
			}
			break;
		}
	}
	
	
	
	
	private void insertSpecical(String frequen)
	{
		BaseMapObject email = new BaseMapObject();
		email.put("id", null);
		email.put("frequency", frequen);
		email.put("creattime", UnixTime.getStrCurrentUnixTime());
		email.InsertObj2DB(mContext, "specialway");		
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("专向语音");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		Btn_Commit = (Button)findViewById(R.id.bt_commit);
		Btn_Talk = (Button)findViewById(R.id.bt_talk);
		Btn_Commit.setOnClickListener(this);
		edit_frequency = (EditText)findViewById(R.id.edit_boundry);
		Btn_Talk.setVisibility(View.GONE);
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
					
					SendSpecialVoicetoNet(true);
					break;
				case MotionEvent.ACTION_UP:
					findViewById(R.id.voice_hint_layout).setVisibility(View.GONE);
					((TextView) findViewById(R.id.voiceHintText)).setText("松开手指发送");
					
					SendSpecialVoicetoNet(false);
					break;
				}
				return false;
			}
		});
	}
	
	
	public void SendSpecialVoicetoNet(boolean connet)
	{

		new SendDataTask().execute(APICode.SEND_TalkSpecialVoice+"",OverAllData.Account,connet?"1":"0");
		
	}
	
	
	public class MyBroadcastReciver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			String action = intent.getAction();


			if (action.equals(broad_specialvoice_Action))
			{
				if (intent.getSerializableExtra("data").equals("true"))
				{
					Btn_Talk.setVisibility(View.VISIBLE);
					startRTPSpeak();
				}
				else
				{
					MyLog.showToast(mContext, "专项语音请求失败...");
					Btn_Talk.setVisibility(View.GONE);
				}
			}
		}

	}

}
