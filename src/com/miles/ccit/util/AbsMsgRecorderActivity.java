package com.miles.ccit.util;

import java.util.Timer;
import java.util.TimerTask;

import com.miles.ccit.duomo.R;
import com.miles.ccit.net.APICode;
import com.miles.ccit.ui.CreatShortmsgActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class AbsMsgRecorderActivity extends AbsBaseActivity
{
	public EditText edit_inputMsg;
	public Button Btn_switchVoice;
	public Button Btn_Send;
	public Button Btn_Talk;
	public MsgRecorderutil mediaRecorder;
	public Timer timer;
	private long currentlong = 0;
	public String strContatc = "";
	public boolean isUp = false;
	public static final int RECVFROM = 1;
	public static final int SENDTO = 2;
	public static final int SENDERROR = 3;
	
	
	
	
	Handler handle = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			if (currentlong >= 14)
			{
				mediaRecorder.stopRecorder();
				talkTouchUp(null);
				isUp = true;
				MyLog.showToast(mContext, "超过最大时间,立即发送...");
				currentlong = 0;
				timer.cancel();
			} else
			{
				((TextView) findViewById(R.id.voidHinttime)).setText("还剩" + (14 - currentlong) + "秒");
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public String getStrContatc()
	{
		return strContatc;
	}

	public void setStrContatc(String strContatc)
	{
		this.strContatc = strContatc;
	}

	public void switchVoice()
	{
		if (edit_inputMsg.getVisibility() == View.VISIBLE)
		{
			edit_inputMsg.setVisibility(View.GONE);
			Btn_Talk.setVisibility(View.VISIBLE);
			Btn_switchVoice.setBackgroundResource(R.drawable.btntext);
			Btn_Send.setEnabled(false);
		} else
		{
			edit_inputMsg.setVisibility(View.VISIBLE);
			Btn_Talk.setVisibility(View.GONE);
			Btn_switchVoice.setBackgroundResource(R.drawable.btnvoice);

			Btn_Send.setEnabled(true);
		}
	}

	public void sendTextmsg(String contact)
	{
		if (contact.equals(""))
		{
			Toast.makeText(mContext, "联系人不能为空...", 0).show();
			return;
		} else if (edit_inputMsg.getText().toString().equals(""))
		{
			Toast.makeText(mContext, "发送内容不能为空...", 0).show();
			return;
		} else
		{
			if (contact.indexOf(",") == -1)
			{
				long ret = MsgRecorderutil.insertTextmsg(mContext, contact, edit_inputMsg.getText().toString());
				sendTextMsgtoNet(new long[]{ret}, new String[]{contact}, edit_inputMsg.getText().toString());
			} else
			{
				String[] tmparray = contact.split(",");
				long[] arrayid = new long[tmparray.length];
				for (int i = 0; i < tmparray.length; i++)
				{
					long ret = MsgRecorderutil.insertTextmsg(mContext, tmparray[i], edit_inputMsg.getText().toString());
					arrayid[i] = ret;
				}
				sendTextMsgtoNet(arrayid, tmparray, edit_inputMsg.getText().toString());
			}
		}
	}
	
	public static void sendTextMsgtoNet(long[] id,String[] contact,String msgcontent)
	{
		String desCon = "";
		for(int i=0;i<id.length;i++)
		{
			desCon+=(contact[i]+","+id[i]+",");
		}
		desCon = desCon.substring(0, desCon.length()-1);
		new SendDataTask().execute(APICode.SEND_ShortTextMsg+"",OverAllData.Account,desCon,msgcontent);
		
	}
	
	public static void sendVoiceMsgtoNet(long[] id,String[] contact,String voicepath)
	{
		String desCon = "";
		for(int i=0;i<id.length;i++)
		{
			desCon+=(contact[i]+","+id[i]+",");
		}
		desCon = desCon.substring(0, desCon.length()-1);
		new SendDataTask().execute(APICode.SEND_ShortVoiceMsg+"",OverAllData.Account,desCon,voicepath);
		
	}
	
	

	public boolean talkTouchDown(String contact)
	{
		if (contact.equals(""))
		{
			MyLog.showToast(mContext, "请输入联系人号码...");
			return false;
		}
		setStrContatc(contact);
		findViewById(R.id.voice_hint_layout).setVisibility(View.VISIBLE);
		((AnimationDrawable) ((ImageView) findViewById(R.id.voice_hint_flash)).getDrawable()).start();
		((TextView) findViewById(R.id.voiceHintText)).setText("上滑手指,取消发送");
		Btn_Talk.setText("松开发送");

		mediaRecorder = new MsgRecorderutil();
		mediaRecorder.startRecorder();
		timer = new Timer();
		timer.schedule(new TimerTask()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				currentlong++;
				handle.sendMessage(new Message());

			}
		}, 10, 1000);
		return false;
	}

	public boolean talkTouchUp(MotionEvent event)
	{
		if (timer != null)
		{
			timer.cancel();
			currentlong = 0;
		}
		if (isUp)
		{
			isUp = false;
		} else
		{
			findViewById(R.id.voice_hint_layout).setVisibility(View.GONE);
			((TextView) findViewById(R.id.voiceHintText)).setText("松开手指发送");
			Btn_Talk.setText("按住录音");
			if (mediaRecorder != null)
			{
				mediaRecorder.stopRecorder();

				if (event != null)
				{
					if (event.getY() < 0)
					{
						Toast.makeText(mContext, "取消发送...", 0).show();
						return false;
					}
				}
				if (getStrContatc().equals("null"))
				{
					return false;
				}
				if (getStrContatc().indexOf(",") == -1)
				{
					long ret = MsgRecorderutil.insertVoicemsg(mContext, getStrContatc(), mediaRecorder.getRecorderpath());
					
					sendVoiceMsgtoNet(new long[]{ret}, new String[]{getStrContatc()}, mediaRecorder.getRecorderpath());
				
				} else
				{
					String[] tmparray = getStrContatc().split(",");
					long[] arrayid = new long[tmparray.length];
						
					for (int i = 0; i < tmparray.length; i++)
					{
						long ret = MsgRecorderutil.insertVoicemsg(mContext, tmparray[i], mediaRecorder.getRecorderpath());
						arrayid[i] = ret;
					}
					sendVoiceMsgtoNet(arrayid, tmparray, mediaRecorder.getRecorderpath());
					
				}
			}
		}
		return false;
	}

}
