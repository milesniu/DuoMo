package com.miles.ccit.util;

import java.io.File;
import java.util.UUID;

import android.content.Context;
import android.media.MediaRecorder;

public class MsgRecorderutil
{
	private MediaRecorder mediaRecorder;
	public File voiceFile;
	public long startTime;
	public long endTime;
	
	public void startRecorder()
	{
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
	}
	
	public void stopRecorder()
	{
		endTime = System.currentTimeMillis();
		mediaRecorder.stop();
		mediaRecorder.release();
	}
	
	public String getRecorderpath()
	{
		return voiceFile.getAbsolutePath();
	}
	
	public static void insertTextmsg(Context contex,String contact,String msgcontent)
	{
		BaseMapObject shortmsg = new BaseMapObject();
		shortmsg.put("id", null);
		shortmsg.put("number", contact);
		shortmsg.put("sendtype", "1");	//发送
		shortmsg.put("status", "1");	//已读
		shortmsg.put("msgtype", "0");	//语音
		shortmsg.put("msgcontent", msgcontent);
		shortmsg.put("creattime", UnixTime.getStrCurrentUnixTime());
		shortmsg.put("priority", "1");
		shortmsg.put("acknowledgemen", "1");
		shortmsg.InsertObj2DB(contex, "shortmsg");
	}
	
	public static void insertVoicemsg(Context contex,String contact,String msgcontent)
	{
		BaseMapObject shortmsg = new BaseMapObject();
		shortmsg.put("id", null);
		shortmsg.put("number",contact);
		shortmsg.put("sendtype", "1");
		shortmsg.put("status", "1");
		shortmsg.put("msgtype", "1");
		shortmsg.put("msgcontent", msgcontent);
		shortmsg.put("creattime", UnixTime.getStrCurrentUnixTime());
		shortmsg.put("priority", "1");
		shortmsg.put("acknowledgemen", "1");
		shortmsg.InsertObj2DB(contex, "shortmsg");
	}
	
}
