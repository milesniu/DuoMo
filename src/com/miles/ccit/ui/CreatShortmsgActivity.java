package com.miles.ccit.ui;

import java.io.File;
import java.util.UUID;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.miles.ccit.duomo.R;
import com.miles.ccit.util.BaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.UnixTime;

public class CreatShortmsgActivity extends BaseActivity
{
	private EditText edit_inputContact;
	private EditText edit_inputMsg;
	private Button Btn_addContact;
	private Button Btn_switchVoice;
	private Button Btn_Send;
	private Button Btn_Talk;
	private MediaRecorder mediaRecorder;
	public File voiceFile;
	public long startTime;

	public long endTime;

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
			break;
		case R.id.bt_swicthvoice:
			if (edit_inputMsg.getVisibility() == View.VISIBLE)
			{
				edit_inputMsg.setVisibility(View.GONE);
				Btn_Talk.setVisibility(View.VISIBLE);
				Btn_switchVoice.setText("文字");
			}
			else
			{
				edit_inputMsg.setVisibility(View.VISIBLE);
				Btn_Talk.setVisibility(View.GONE);
				Btn_switchVoice.setText("语音");
			}
			break;
		case R.id.bt_send:
			if (edit_inputContact.getText().toString().equals(""))
			{
				Toast.makeText(mContext, "联系人不能为空...", 0).show();
				return;
			}
			else if (edit_inputMsg.getText().toString().equals(""))
			{
				Toast.makeText(mContext, "发送内容不能为空...", 0).show();
				return;
			}
			else
			{
				BaseMapObject shortmsg = new BaseMapObject();
				shortmsg.put("id", null);
				shortmsg.put("number", edit_inputContact.getText().toString());
				shortmsg.put("sendtype", "1");
				shortmsg.put("status", "1");
				shortmsg.put("msgtype", "0");
				shortmsg.put("msgcontent", edit_inputMsg.getText().toString());
				shortmsg.put("creattime", UnixTime.getStrCurrentUnixTime());
				shortmsg.put("priority", "1");
				shortmsg.put("acknowledgemen", "1");
				shortmsg.InsertObj2DB(mContext, "shortmsg");
			}
			this.finish();
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("新建消息");
		Btn_Left.setText("返回");
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
					if (edit_inputContact.getText().toString().equals(""))
					{
						MyLog.showToast(mContext, "请输入联系人号码...");
						return false;
					}
					findViewById(R.id.voice_hint_layout).setVisibility(
							View.VISIBLE);
					((AnimationDrawable) ((ImageView) findViewById(R.id.voice_hint_flash))
							.getDrawable()).start();
					((TextView) findViewById(R.id.voiceHintText))
							.setText("上滑手指,取消发送");
					Btn_Talk.setText("松开发送");
					try
					{
						mediaRecorder = new MediaRecorder();
						voiceFile = new File(OverAllData.SDCardRoot
								+ UUID.randomUUID().toString() + ".amr");
						voiceFile.createNewFile();
						mediaRecorder
								.setAudioSource(MediaRecorder.AudioSource.MIC);
						mediaRecorder
								.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
						mediaRecorder
								.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
						mediaRecorder.setOutputFile(voiceFile.getAbsolutePath());
						mediaRecorder.prepare();
						startTime = System.currentTimeMillis();
						mediaRecorder.start();
					}
					catch (Exception e)
					{
						// TODO: handle exception
						e.printStackTrace();
					}
					break;
				case MotionEvent.ACTION_UP:
					findViewById(R.id.voice_hint_layout).setVisibility(View.GONE);
					((TextView) findViewById(R.id.voiceHintText)).setText("松开手指发送");
					Btn_Talk.setText("按住录音");
					if(mediaRecorder!=null)
					{
						endTime = System.currentTimeMillis();
						mediaRecorder.stop();
						mediaRecorder.release();
					}
					if (event.getY() < 0)
					{
						Toast.makeText(mContext, "取消发送...", 0).show();
						return false;
					}
					BaseMapObject shortmsg = new BaseMapObject();
					shortmsg.put("id", null);
					shortmsg.put("number", edit_inputContact.getText().toString());
					shortmsg.put("sendtype", "1");
					shortmsg.put("status", "1");
					shortmsg.put("msgtype", "1");
					shortmsg.put("msgcontent", voiceFile.getAbsolutePath());
					shortmsg.put("creattime", UnixTime.getStrCurrentUnixTime());
					shortmsg.put("priority", "1");
					shortmsg.put("acknowledgemen", "1");
					shortmsg.InsertObj2DB(mContext, "shortmsg");

					break;
				}
				return false;
			}
		});
	}

}
