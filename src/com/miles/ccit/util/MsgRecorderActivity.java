package com.miles.ccit.util;

import com.miles.ccit.duomo.R;
import com.miles.ccit.ui.CreatShortmsgActivity;

import android.graphics.drawable.AnimationDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class MsgRecorderActivity extends BaseActivity
{
	public EditText edit_inputMsg;
	public Button Btn_switchVoice;
	public Button Btn_Send;
	public Button Btn_Talk;
	public MsgRecorderutil mediaRecorder;
	
	public void switchVoice()
	{
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
	}
	
	public void sendTextmsg(String contact)
	{
		if (contact.equals(""))
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
			if(contact.indexOf(",")==-1)
			{
				MsgRecorderutil.insertTextmsg(mContext, contact, edit_inputMsg.getText().toString());
			}
			else
			{
				String[] tmparray = contact.split(",");
				for(int i=0;i<tmparray.length;i++)
				{
					MsgRecorderutil.insertTextmsg(mContext, tmparray[i], edit_inputMsg.getText().toString());
				}
			}
		}
	}
	
	public boolean talkTouchDown(String contact)
	{
		if (contact.equals(""))
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
		
		mediaRecorder = new MsgRecorderutil();
		mediaRecorder.startRecorder();
		return false;
	}
	
	public boolean talkTouchUp(MotionEvent event,String contact)
	{
		findViewById(R.id.voice_hint_layout).setVisibility(
				View.GONE);
		((TextView) findViewById(R.id.voiceHintText))
				.setText("松开手指发送");
		Btn_Talk.setText("按住录音");
		
		mediaRecorder.stopRecorder();

		if (event.getY() < 0)
		{
			Toast.makeText(mContext, "取消发送...", 0).show();
			return false;
		}
		MsgRecorderutil.insertVoicemsg(mContext, contact, mediaRecorder.getRecorderpath());
	
		return false;
	}
	
}
