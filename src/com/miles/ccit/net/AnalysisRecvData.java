package com.miles.ccit.net;

import java.io.UnsupportedEncodingException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.BroadCastctivity;
import com.miles.ccit.duomo.CallWaitActivity;
import com.miles.ccit.duomo.EmailInfoActivity;
import com.miles.ccit.duomo.FileStatusActivity;
import com.miles.ccit.duomo.HaveCallActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.duomo.ShortmsgListActivity;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsMsgRecorderActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.ByteUtil;
import com.miles.ccit.util.HexSwapString;
import com.miles.ccit.util.MyApplication;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.UnixTime;

public class AnalysisRecvData
{
	private Intent messageIntent = null;
	private PendingIntent messagePendingIntent = null;
	// 通知栏消息
	private int messageNotificationID = 10001;
	private Notification messageNotification = null;
	private NotificationManager messageNotificatioManager = null;

	private Context AppContext = MyApplication.getAppContext();

	public void analyLogin(byte[] data)
	{
		Intent intent = new Intent();
		intent.setAction(AbsBaseActivity.broad_login_Action);
		intent.putExtra("data", data);
		AppContext.sendBroadcast(intent);
	}

	public void analyChangePwd(byte[] data)
	{
		Intent intent = new Intent();
		intent.setAction(AbsBaseActivity.broad_backchangepwd_Action);
		intent.putExtra("data", data);
		AppContext.sendBroadcast(intent);
	}

	public void analyTextMsg(byte[] data) throws UnsupportedEncodingException
	{
		Intent intent = new Intent();
		int cursor = 5;
		int namelen = ByteUtil.oneByte2oneInt(data[cursor++]);
		byte[] srcname = new byte[namelen];
		System.arraycopy(data, cursor, srcname, 0, namelen);
		String strsrcname = new String(srcname, "UTF-8");

		cursor += namelen;

		int deslen = ByteUtil.oneByte2oneInt(data[cursor++]);
		byte[] desname = new byte[deslen];
		System.arraycopy(data, cursor, desname, 0, deslen);
		String strdesname = new String(desname, "UTF-8");

		cursor += deslen;

		int clen = ByteUtil.oneByte2oneInt(data[cursor++]);
		byte[] conten = new byte[clen];
		System.arraycopy(data, cursor, conten, 0, clen);
		String co = new String(conten, "UTF-8");

		BaseMapObject recvmsg = new BaseMapObject();
		recvmsg.put("id", null);
		recvmsg.put("number", strsrcname);
		recvmsg.put("sendtype", AbsMsgRecorderActivity.RECVFROM + "");
		recvmsg.put("status", "0");
		recvmsg.put("msgtype", "0");
		recvmsg.put("msgcontent", co);
		recvmsg.put("creattime", UnixTime.getStrCurrentUnixTime());
		recvmsg.put("priority", OverAllData.Priority);
		recvmsg.put("acknowledgemen", OverAllData.Acknowledgemen);
		recvmsg.InsertObj2DB(AppContext, "shortmsg");

		if (ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(strsrcname))
		{
			intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
			intent.putExtra("data", recvmsg);
			AppContext.sendBroadcast(intent);
		} else
		{
			BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", strsrcname);
			if (contact != null)
			{
				recvmsg.put("name", contact.get("name").toString());
			}
			messageNotification = new Notification();
			messageNotification.icon = R.drawable.ic_launcher;
			messageNotification.tickerText = "你有一条新的短消息";
			messageNotification.flags = messageNotification.FLAG_AUTO_CANCEL;
			messageNotification.defaults = Notification.DEFAULT_SOUND;
			messageNotificatioManager = (NotificationManager) AppContext.getSystemService(AppContext.NOTIFICATION_SERVICE);
			messageIntent = new Intent(AppContext, ShortmsgListActivity.class);
			messageIntent.putExtra("item", recvmsg);
			messageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			messagePendingIntent = PendingIntent.getActivity(AppContext, 0, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			messageNotificationID = Integer.parseInt(strsrcname);
			// 更新通知栏
			messageNotification.setLatestEventInfo(AppContext, contact == null ? strsrcname : (contact.get("name").toString() + "的新消息"), co, messagePendingIntent);
			messageNotificatioManager.notify(messageNotificationID, messageNotification);

		}
	}

	public void analyBackTextMsg(byte[] data) throws UnsupportedEncodingException
	{
		Intent intent = new Intent();
		int alllen = ByteUtil.byte2Int(new byte[]
		{ data[2], data[3] });
		int idlen = alllen - 2;// 长度本身1字节，命令码1字节，成败1字节

		byte[] srcname = new byte[idlen];
		System.arraycopy(data, 6, srcname, 0, idlen);
		String id = new String(srcname);
		BaseMapObject senditem = GetData4DB.getObjectByid(AppContext, "shortmsg", id);

		if (senditem != null && ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(senditem.get("number").toString()))
		{
			senditem.put("sendtype", data[5] == 0 ? AbsBaseActivity.SENDERROR + "" : AbsBaseActivity.SENDSUCCESS + "");
			senditem.UpdateMyself(AppContext, "shortmsg");

			intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
			intent.putExtra("data", senditem);
			AppContext.sendBroadcast(intent);
		}

		// Intent intent = new Intent();
	}

	public void analyVoiceMsg(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();

		int voicecursor = 5;
		int vnlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
		byte[] srcvname = new byte[vnlen];
		System.arraycopy(data, voicecursor, srcvname, 0, vnlen);
		String vname = new String(srcvname, "UTF-8");
		voicecursor += vnlen;

		int myvlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
		byte[] myvname = new byte[myvlen];
		System.arraycopy(data, voicecursor, myvname, 0, myvlen);
		String mstryvname = new String(myvname, "UTF-8");
		voicecursor += myvlen;

		int vclen = ByteUtil.byte2Int(new byte[]
		{ data[25], data[26] });// .oneByte2oneInt(data[voicecursor++]);
		voicecursor = 27;
		byte[] vconten = new byte[vclen];
		System.arraycopy(data, voicecursor, vconten, 0, vclen);
		// String co = new String(conten, "UTF-8");
		String vpath = ByteUtil.getFile(vconten, OverAllData.SDCardRoot, vname + "_" + UnixTime.getStrCurrentUnixTime() + ".amr");
		if (vpath == null)
		{
			return;
		} else
		{
			BaseMapObject recvvoicemsg = new BaseMapObject();
			recvvoicemsg.put("id", null);
			recvvoicemsg.put("number", vname);
			recvvoicemsg.put("sendtype", AbsMsgRecorderActivity.RECVFROM + "");
			recvvoicemsg.put("status", "0");
			recvvoicemsg.put("msgtype", "1");
			recvvoicemsg.put("msgcontent", vpath);
			recvvoicemsg.put("creattime", UnixTime.getStrCurrentUnixTime());
			recvvoicemsg.put("priority", OverAllData.Priority);
			recvvoicemsg.put("acknowledgemen", OverAllData.Acknowledgemen);
			recvvoicemsg.InsertObj2DB(AppContext, "shortmsg");

			if (ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(vname))
			{
				intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
				intent.putExtra("data", recvvoicemsg);
				AppContext.sendBroadcast(intent);
			} else
			{
				BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", vname);
				if (contact != null)
				{
					recvvoicemsg.put("name", contact.get("name").toString());
				}
				messageNotification = new Notification();
				messageNotification.icon = R.drawable.ic_launcher;
				messageNotification.tickerText = "你有一条新的语音";
				messageNotification.flags = messageNotification.FLAG_AUTO_CANCEL;
				messageNotification.defaults = Notification.DEFAULT_SOUND;
				messageNotificatioManager = (NotificationManager) AppContext.getSystemService(AppContext.NOTIFICATION_SERVICE);
				messageIntent = new Intent(AppContext, ShortmsgListActivity.class);
				messageIntent.putExtra("item", recvvoicemsg);
				messageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				messagePendingIntent = PendingIntent.getActivity(AppContext, 0, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				messageNotificationID = Integer.parseInt(vname);
				// 更新通知栏
				messageNotification.setLatestEventInfo(AppContext, contact == null ? vname : (contact.get("name").toString() + "的新消息"), "[语音]", messagePendingIntent);
				messageNotificatioManager.notify(messageNotificationID, messageNotification);

			}
		}

	}

	public void analyrecvWiredFile(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();

		int voicecursor = 5;
		int vnlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
		byte[] srcvname = new byte[vnlen];
		System.arraycopy(data, voicecursor, srcvname, 0, vnlen);
		String vname = new String(srcvname, "UTF-8");
		voicecursor += vnlen;


		int vclen = ByteUtil.byte2Int(new byte[]{ data[voicecursor], data[voicecursor+1] });// .oneByte2oneInt(data[voicecursor++]);
		voicecursor +=2;
		byte[] vconten = new byte[vclen];
		System.arraycopy(data, voicecursor, vconten, 0, vclen);
		 String co = new String(vconten, "UTF-8");
		String vpath = ByteUtil.getFile(vconten, OverAllData.SDCardRoot,vname);
		if (vpath == null)
		{
			return;
		} else
		{
			FileStatusActivity.recvpath = vpath;
			BaseMapObject record = new BaseMapObject();
			record.put("id",null);
			record.put("number",FileStatusActivity.code);
			record.put("sendtype","1");//语音0.文件1
			record.put("status","1");//呼入成功/呼出成功/呼入失败/呼出失败(1,2,3,4)
			record.put("filepath",vpath);
			record.put("creattime", UnixTime.getStrCurrentUnixTime());
			
			record.InsertObj2DB(AppContext, "wiredrecord");
//			BaseMapObject recvvoicemsg = new BaseMapObject();
//			recvvoicemsg.put("id", null);
//			recvvoicemsg.put("number", vname);
//			recvvoicemsg.put("sendtype", AbsMsgRecorderActivity.RECVFROM + "");
//			recvvoicemsg.put("status", "0");
//			recvvoicemsg.put("msgtype", "1");
//			recvvoicemsg.put("msgcontent", vpath);
//			recvvoicemsg.put("creattime", UnixTime.getStrCurrentUnixTime());
//			recvvoicemsg.put("priority", OverAllData.Priority);
//			recvvoicemsg.put("acknowledgemen", OverAllData.Acknowledgemen);
//			recvvoicemsg.InsertObj2DB(AppContext, "shortmsg");

		}

	}

	public void analyEmail(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		// 源地址
		int voicecursor = 5;
		int vnlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
		byte[] srcvname = new byte[vnlen];
		System.arraycopy(data, voicecursor, srcvname, 0, vnlen);
		String vname = new String(srcvname, "UTF-8");
		voicecursor += vnlen;
		// 抄送地址
		int cslen = ByteUtil.byte2Int(new byte[]
		{ data[voicecursor], data[voicecursor + 1] });
		voicecursor += 2;
		byte[] csname = new byte[cslen];
		System.arraycopy(data, voicecursor, csname, 0, cslen);
		String cstryvname = new String(csname, "UTF-8");
		voicecursor += cslen;
		// 目的地址
		int myvlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
		// voicecursor+=2;
		byte[] myvname = new byte[myvlen];
		System.arraycopy(data, voicecursor, myvname, 0, myvlen);
		String mstryvname = new String(myvname, "UTF-8");
		voicecursor += myvlen;
		// 标题
		int btlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
		byte[] srcbtname = new byte[btlen];
		System.arraycopy(data, voicecursor, srcbtname, 0, btlen);
		String btname = new String(srcbtname, "UTF-8");
		voicecursor += btlen;
		// 内容
		int nrlen = ByteUtil.byte2Int(new byte[]
		{ data[voicecursor], data[voicecursor + 1] });
		voicecursor += 2;
		byte[] nrname = new byte[nrlen];
		System.arraycopy(data, voicecursor, nrname, 0, nrlen);
		String nr = new String(nrname, "UTF-8");
		voicecursor += nrlen;

		BaseMapObject recvvoicemsg = new BaseMapObject();
		recvvoicemsg.put("id", null);
		recvvoicemsg.put("number", vname);
		recvvoicemsg.put("csnumber", cstryvname);
		recvvoicemsg.put("sendtype", AbsMsgRecorderActivity.RECVFROM + "");
		recvvoicemsg.put("subject", btname);
		recvvoicemsg.put("mailcontent", nr);
		recvvoicemsg.put("creattime", UnixTime.getStrCurrentUnixTime());
		recvvoicemsg.put("priority", OverAllData.Priority);
		recvvoicemsg.put("acknowledgemen", OverAllData.Acknowledgemen);

		if (data[voicecursor++] == 1)// 附件标示
		{
			// 附件名称
			int fjnlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
			byte[] vconten = new byte[fjnlen];
			System.arraycopy(data, voicecursor, vconten, 0, fjnlen);
			String co = UnixTime.getStrCurrentUnixTime() + "_" + new String(vconten, "UTF-8");
			voicecursor += fjnlen;

			// 附件内容
			int fjlen = ByteUtil.byte2Int(new byte[]
			{ data[voicecursor], data[voicecursor + 1] });
			voicecursor += 2;
			byte[] fjcon = new byte[fjlen];
			System.arraycopy(data, voicecursor, fjcon, 0, fjlen);
			String vpath = ByteUtil.getFile(fjcon, OverAllData.SDCardRoot, co);

			recvvoicemsg.put("haveattachments", "1");
			recvvoicemsg.put("attachmentsname", co);
			recvvoicemsg.put("attachmentspath", vpath);

		} else
		{
			recvvoicemsg.put("haveattachments", "0");
			recvvoicemsg.put("attachmentsname", "");
			recvvoicemsg.put("attachmentspath", "");

		}
		recvvoicemsg.InsertObj2DB(AppContext, "emailmsg");

		if (ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(vname))
		{
			intent.setAction(AbsBaseActivity.broad_Email_Action);
			intent.putExtra("data", recvvoicemsg);
			AppContext.sendBroadcast(intent);
		} else
		{
			BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", vname);
			if (contact != null)
			{
				recvvoicemsg.put("name", contact.get("name").toString());
			}
			messageNotification = new Notification();
			messageNotification.icon = R.drawable.ic_launcher;
			messageNotification.tickerText = "你有一封新邮件";
			messageNotification.flags = messageNotification.FLAG_AUTO_CANCEL;
			messageNotification.defaults = Notification.DEFAULT_SOUND;
			messageNotificatioManager = (NotificationManager) AppContext.getSystemService(AppContext.NOTIFICATION_SERVICE);
			messageIntent = new Intent(AppContext, EmailInfoActivity.class);
			messageIntent.putExtra("item", recvvoicemsg);
			messageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			messagePendingIntent = PendingIntent.getActivity(AppContext, 0, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			messageNotificationID = Integer.parseInt(vname);
			// 更新通知栏
			messageNotification.setLatestEventInfo(AppContext, contact == null ? vname : (contact.get("name").toString() + "的新邮件"), btname, messagePendingIntent);
			messageNotificatioManager.notify(messageNotificationID, messageNotification);

		}

	}

	public void analyBackVoiceCode(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		if (CallWaitActivity.iswait)
		{
			intent.setAction(AbsBaseActivity.broad_recvvoicecode_Action);
			if (data[5] == 1)// 成功响应
			{
				intent.putExtra("data", "true");
			} else if (data[5] == 0)// 失败响应
			{
				intent.putExtra("data", "false");
			}
			AppContext.sendBroadcast(intent);
		}

	}

	public void analyBackWiredVoice(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		if (CallWaitActivity.iswait)
		{
			intent.setAction(AbsBaseActivity.broad_wiredvoice_Action);
			if (data[5] == 1)// 成功响应
			{
				intent.putExtra("data", "true");
			} else if (data[5] == 0)// 失败响应
			{
				intent.putExtra("data", "false");
			}
			AppContext.sendBroadcast(intent);
		}

	}

	public void analyBackSpecialVoice(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		intent.setAction(AbsBaseActivity.broad_specialvoice_Action);
		if (data[5] == 1)// 成功响应
		{
			intent.putExtra("data", "true");
		} else if (data[5] == 0)// 失败响应
		{
			intent.putExtra("data", "false");
		}
		AppContext.sendBroadcast(intent);

	}

	public void analyProgress(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		intent.setAction(AbsBaseActivity.broad_fileprogress_Action);
		int p = ByteUtil.oneByte2oneInt(data[5]);
		intent.putExtra("progress", p);
		AppContext.sendBroadcast(intent);

	}

	public void analyRecvVoicecode(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		// 源地址
		int voicecursor = 5;
		int vnlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
		byte[] srcvname = new byte[vnlen];
		System.arraycopy(data, voicecursor, srcvname, 0, vnlen);
		String vname = new String(srcvname, "UTF-8");
		voicecursor += vnlen;

		// 目的地址
		int myvlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
		// voicecursor+=2;
		byte[] myvname = new byte[myvlen];
		System.arraycopy(data, voicecursor, myvname, 0, myvlen);
		String mstryvname = new String(myvname, "UTF-8");
		voicecursor += myvlen;

		intent.setClass(AppContext, HaveCallActivity.class);
		intent.putExtra("code", vname);
		intent.putExtra("type", AbsToCallActivity.TOCALLVOICE);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		AppContext.startActivity(intent);

	}

	public void analyRecvWiredVoice(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		// 源地址
		int voicecursor = 5;
		int vnlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
		byte[] srcvname = new byte[vnlen];
		System.arraycopy(data, voicecursor, srcvname, 0, vnlen);
		String vname = new String(srcvname, "UTF-8");

		intent.setClass(AppContext, HaveCallActivity.class);
		intent.putExtra("code", vname);
		intent.putExtra("type", AbsToCallActivity.TOCALLWIREDVOICE);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		AppContext.startActivity(intent);

	}

	public void analyInteraput(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		intent.setAction(AbsBaseActivity.broad_interaput_Action);
		AppContext.sendBroadcast(intent);

	}

	public void analyFileBackresult(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		intent.setAction(AbsBaseActivity.broad_fileresult_Action);
		if (data[5] == 1)// 成功响应
		{
			intent.putExtra("data", "true");
		} else if (data[5] == 0)// 失败响应
		{
			intent.putExtra("data", "false");
		}
		AppContext.sendBroadcast(intent);

	}

	public void analyBroadcast(byte[] data) throws UnsupportedEncodingException
	{

		Intent intent = new Intent();
		// 源地址
		int voicecursor = 5;
		int flen = ByteUtil.byte2Int(new byte[]
		{ data[5], data[6] });

		byte[] fconten = new byte[flen];

		System.arraycopy(data, voicecursor, fconten, 0, flen);
		String filename = UnixTime.getStrCurrentUnixTime() + "_broad.txt";
		String vpath = ByteUtil.getFile(fconten, OverAllData.SDCardRoot, filename);
		if (BroadCastctivity.number != null)
		{
			BaseMapObject broad = new BaseMapObject();
			broad.put("id", null);
			broad.put("frequency", BroadCastctivity.number);
			broad.put("filepath", vpath);
			broad.put("creattime", UnixTime.getStrCurrentUnixTime());
			broad.InsertObj2DB(AppContext, "broadcastrecode");

			intent.setAction(AbsBaseActivity.broad_broadcast_Action);
			intent.putExtra("data", broad);
			AppContext.sendBroadcast(intent);
		}

	}

}
