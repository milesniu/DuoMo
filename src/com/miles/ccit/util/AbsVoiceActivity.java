package com.miles.ccit.util;

import java.util.Vector;

import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.wz.codecs.Codecs;
import com.wz.media.JAudioLauncher;
import com.wz.media.RtpStreamReceiver;


public abstract class AbsVoiceActivity extends AbsBaseActivity
{
	public static JAudioLauncher audio_app = null;
	private final static String TAG = "MainActivity";

	public void startRTPSpeak()
	{
		Vector<Integer> numbs = new Vector<Integer>()
				{
					{
						new Integer(0);
						new Integer(1);
						new Integer(2);
						new Integer(3);
					}
				};

				Codecs.Map c = new Codecs.Map(8, Codecs.codecs.get(1), numbs, Codecs.codecs);

				audio_app = new JAudioLauncher(getApplicationContext(), OverAllData.LocalRTPPortPort, OverAllData.RTPIpaddress, OverAllData.RomoteRTPPort, 0, null, null, 8000, 1,
						160, c, 0);

//				Log.i(TAG, "IP:" + ip.getText() + "* receive port: " + rport.getText() + "* send port:" + sport.getText());

//				if (bt.isChecked())
//				{
//					audio_app.bluetoothMedia();
//				} else if (sp.isChecked())
//				{
					audio_app.speakerMedia(AudioManager.MODE_NORMAL);
//				} else
//				{
//					audio_app.speakerMedia(AudioManager.MODE_IN_CALL);
//				}

				audio_app.startMedia();
	}

	
	public void stopRTPSpeak()
	{
		if (audio_app != null)
		{
			audio_app.stopMedia();
		}
	}

	public void speakinSpeaker()
	{
		audio_app.speakerMedia(AudioManager.MODE_NORMAL);
	}
	
	public void speakinCall()
	{
		audio_app.speakerMedia(AudioManager.MODE_IN_CALL);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event)
	{
		// TODO Auto-generated method stub
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			RtpStreamReceiver.adjust(keyCode, true);
//			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	


	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
          if(audio_app!=null) {
            audio_app.speakerMedia(AudioManager.MODE_NORMAL);
            stopRTPSpeak();
          }
		
		super.onDestroy();
	}
	


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			RtpStreamReceiver.adjust(keyCode, false);
//			return true;
		}

		return super.onKeyUp(keyCode, event);
	}
	
}
