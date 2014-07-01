package com.miles.ccit.duomo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;

import com.miles.ccit.adapter.MsgorMailSetAdapter;
import com.miles.ccit.util.AbsMsgRecorderActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.UnixTime;

public class CreatSpecialvoiceActivity extends AbsMsgRecorderActivity
{

//	private Button Btn_Talk;
	private Button Btn_Commit;
	private EditText  edit_frequency;
	private MsgorMailSetAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_specialvoice);
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
			Btn_Talk.setVisibility(View.VISIBLE);
			insertSpecical(edit_frequency.getText().toString());
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
					talkTouchDown("null");
					
					break;
				case MotionEvent.ACTION_UP:
					setStrContatc("null");
					talkTouchUp(event);
					break;
				}
				return false;
			}
		});
	}

}
