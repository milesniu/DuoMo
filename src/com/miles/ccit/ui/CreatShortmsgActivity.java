package com.miles.ccit.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsMsgRecorderActivity;
import com.miles.ccit.util.MutiChoiseDlg;

public class CreatShortmsgActivity extends AbsMsgRecorderActivity
{
	private EditText edit_inputContact;
	private Button Btn_addContact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_shortmsg);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.creat_shortmsg, menu);
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
		case R.id.bt_addcontact:
			new MutiChoiseDlg(mContext, GetData4DB.getObjectListData(mContext, "contact", "type", "0")).getDlg(edit_inputContact);
			break;
		case R.id.bt_swicthvoice:
			switchVoice();
			break;
		case R.id.bt_send:
			sendTextmsg(edit_inputContact.getText().toString());
			this.finish();
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("新建消息");
//		Btn_Left.setText("返回");
		Btn_Right.setVisibility(View.INVISIBLE);

		edit_inputContact = (EditText) findViewById(R.id.edit_concotact);
		edit_inputMsg = (EditText) findViewById(R.id.edit_inputmsg);
		Btn_switchVoice = (Button) findViewById(R.id.bt_swicthvoice);
		Btn_addContact = (Button) findViewById(R.id.bt_addcontact);
		Btn_Send = (Button) findViewById(R.id.bt_send);
		Btn_Talk = (Button) findViewById(R.id.bt_talk);
		Btn_addContact.setOnClickListener(this);
		Btn_switchVoice.setOnClickListener(this);
		Btn_Send.setOnClickListener(this);
		Btn_Talk.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					talkTouchDown(edit_inputContact.getText().toString());
					
					break;
				case MotionEvent.ACTION_UP:
					talkTouchUp(event);
					CreatShortmsgActivity.this.finish();
					break;
				}
				return false;
			}
		});
	}

}
