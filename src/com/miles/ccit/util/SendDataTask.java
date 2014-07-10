package com.miles.ccit.util;

import android.os.AsyncTask;

import com.miles.ccit.net.APICode;
import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.SocketConnection;

public class SendDataTask extends AsyncTask<String, Void, byte[]>
{

	@Override
	protected byte[] doInBackground(String... parm)
	{
		// TODO Auto-generated method stub
		try
		{
			switch (Byte.parseByte(parm[0]))
			{
			case APICode.SEND_Login:
				SocketConnection.getInstance().readReqMsg(new ComposeData().sendLogin(parm[1], parm[2]));
				break;
			case APICode.SEND_Logout:
				break;
			case APICode.SEND_ChangePwd:
				break;
			case APICode.SEND_ShortTextMsg:
				SocketConnection.getInstance().readReqMsg(new ComposeData().sendShortTextmsg(parm[1], parm[2], parm[3]));
				break;
			case APICode.SEND_ShortVoiceMsg:
				SocketConnection.getInstance().readReqMsg(new ComposeData().sendShortVoicemsg(parm[1], parm[2], parm[3]));
				break;
			case APICode.SEND_VoiceCode:
				SocketConnection.getInstance().readReqMsg(new ComposeData().sendStartVoicecode(parm[1], parm[2]));
				break;
			case APICode.SEND_TalkVoiceCode:
				break;
			case APICode.SEND_Email:
				
				break;
			case APICode.SEND_CodeDirec:
				
				break;
			case APICode.SEND_SpecialVoice:
				break;
			case APICode.SEND_WiredVoice:
				break;
			case APICode.SEND_WiredFile:
				break;

			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(byte[] result)
	{
		// TODO Auto-generated method stub
		System.out.print("aa");
		super.onPostExecute(result);
	}

}
