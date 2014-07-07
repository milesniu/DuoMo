package com.miles.ccit.util;

import java.io.DataOutputStream;

import com.miles.ccit.net.APICode;
import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.SocketClient;
import com.miles.ccit.net.SocketConnection;

import android.os.AsyncTask;

public class SendDataTask extends AsyncTask<String, Void, byte[]>
{

	@Override
	protected byte[] doInBackground(String... parm)
	{
		// TODO Auto-generated method stub
		try
		{
			
//			DataOutputStream out = new DataOutputStream(SocketConnection.getInstance().getsocket().getOutputStream());
//			ComposeData df = new ComposeData();
//			byte[] buf = null;

			switch (Byte.parseByte(parm[0]))
			{
			case APICode.SEND_Login:
				SocketConnection.getInstance().readReqMsg(new ComposeData().sendLogin(parm[1],parm[2]));
				break;
			case APICode.SEND_Logout:
				break;
			case APICode.SEND_ChangePwd:
				break;
			case APICode.SEND_ShortTextMsg:
				break;
			case APICode.SEND_ShortVoiceMsg:
				break;
			case APICode.SEND_VoiceCode:
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
//			out.write(buf);
//			out.flush();

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
