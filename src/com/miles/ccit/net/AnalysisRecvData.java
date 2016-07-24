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
import com.miles.ccit.duomo.CodeDirectFragment;
import com.miles.ccit.duomo.CodeDirectInfoActivity;
import com.miles.ccit.duomo.EmailFragment;
import com.miles.ccit.duomo.EmailInfoActivity;
import com.miles.ccit.duomo.FileStatusActivity;
import com.miles.ccit.duomo.HaveCallActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.duomo.SettingActivity;
import com.miles.ccit.duomo.ShortMsgFragment;
import com.miles.ccit.duomo.ShortmsgListActivity;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsMsgRecorderActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.ByteUtil;
import com.miles.ccit.util.MyApplication;
import com.miles.ccit.util.O;
import com.miles.ccit.util.SendDataTask;
import com.miles.ccit.util.SendNetBackData;
import com.miles.ccit.util.SendNetData;
import com.miles.ccit.util.UnixTime;

public class AnalysisRecvData {
    private Intent messageIntent = null;
    private PendingIntent messagePendingIntent = null;
    // 通知栏消息
    private int messageNotificationID = 10001;
    private Notification messageNotification = null;
    private NotificationManager messageNotificatioManager = null;

    private Context AppContext = MyApplication.getAppContext();

    public void analyLogin(byte[] data) {
        Intent intent = new Intent();
        intent.setAction(AbsBaseActivity.broad_login_Action);
        intent.putExtra("data", data);
        AppContext.sendBroadcast(intent);
    }

    public void analyChangePwd(byte[] data) {
        Intent intent = new Intent();
        intent.setAction(AbsBaseActivity.broad_backchangepwd_Action);
        intent.putExtra("data", data);
        AppContext.sendBroadcast(intent);
    }

    public void analyEncrypt(byte code, byte[] data) throws UnsupportedEncodingException {

        int len = ByteUtil.byte2Int(new byte[]{data[5], data[6]});
        byte[] content = new byte[len];

        System.arraycopy(data, 7, content, 0, len);

        String strcon = new String(content, "UTF-8");
        Intent intent = new Intent();
        if (code == APICode.SEND_Encrypt) {
            intent.setAction(AbsBaseActivity.broad_encrypt_Action);
        } else if (code == APICode.SEND_Decryption) {
            intent.setAction(AbsBaseActivity.broad_decryption_Action);
        }
        intent.putExtra("data", strcon);
        AppContext.sendBroadcast(intent);
    }

    public void analyTextMsg(byte[] data) throws UnsupportedEncodingException {
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
        recvmsg.put("priority", O.Priority);
        recvmsg.put("exp2", "1");
        recvmsg.put("acknowledgemen", O.Acknowledgemen);
        recvmsg.InsertObj2DB(AppContext, "shortmsg");

        if (ShortMsgFragment.isTop || (ShortmsgListActivity.isTop && ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(strsrcname))) {
            intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
            intent.putExtra("data", recvmsg);
            AppContext.sendBroadcast(intent);
        }

        if (ShortMsgFragment.isTop || (ShortmsgListActivity.isTop && ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(strsrcname))) {
            intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
            intent.putExtra("data", recvmsg);
            AppContext.sendBroadcast(intent);
        }

//                else
//		{
        BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", strsrcname);
        if (contact != null) {
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


    //接收到转发
    public void analyTransData(byte[] data) throws UnsupportedEncodingException {
        Intent intent = new Intent();
        int cursor = 5;
        int namelen = ByteUtil.oneByte2oneInt(data[cursor++]);
        byte[] srcname = new byte[namelen];
        System.arraycopy(data, cursor, srcname, 0, namelen);
        String strsrcname = new String(srcname, "UTF-8");

        cursor += namelen;

        int deslen = ByteUtil.byte2Int(new byte[]{data[cursor + 1], data[cursor + 2]});
        cursor += 2;

        byte[] desname = new byte[deslen];
        System.arraycopy(data, cursor, desname, 0, deslen);
        String strdesname = new String(desname, "UTF-8");

        cursor += deslen;

        int clen = ByteUtil.byte2Int(new byte[]{data[cursor + 1], data[cursor + 2]});
        cursor += 2;
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
        recvmsg.put("priority", O.Priority);
        recvmsg.put("exp2", "2");
        recvmsg.put("acknowledgemen", O.Acknowledgemen);
        recvmsg.InsertObj2DB(AppContext, "shortmsg");

        if (ShortMsgFragment.isTop || (ShortmsgListActivity.isTop && ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(strsrcname))) {
            intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
            intent.putExtra("data", recvmsg);
            AppContext.sendBroadcast(intent);
        }

        BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", strsrcname);
        if (contact != null) {
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

//		}
    }


    public void analyNetTextMsg(byte[] data) throws UnsupportedEncodingException {
        Intent intent = new Intent();
        int namelen = ByteUtil.byte2Int(new byte[]
                {data[5], data[6]});

        byte[] srcname = new byte[namelen];
        System.arraycopy(data, 7, srcname, 0, namelen);
        String strsrcname = new String(srcname, "UTF-8");

        String[] recvdata = strsrcname.split("#");

        BaseMapObject recvmsg = new BaseMapObject();
        recvmsg.put("id", null);
        if (recvdata[1].indexOf("@") != -1) {
            recvmsg.put("number", recvdata[1]);
        } else {
            recvmsg.put("number", recvdata[0].split(",")[0]);
        }
        recvmsg.put("sendtype", AbsMsgRecorderActivity.RECVFROM + "");
        recvmsg.put("status", "0");
        recvmsg.put("msgtype", "0");
        recvmsg.put("msgcontent", recvdata[2]);
        recvmsg.put("creattime", UnixTime.getStrCurrentUnixTime());
        recvmsg.put("priority", O.Priority);
        recvmsg.put("acknowledgemen", O.Acknowledgemen);
        recvmsg.put("exp2", "2");
        recvmsg.InsertObj2DB(AppContext, "shortmsg");

        if (ShortMsgFragment.isTop || (ShortmsgListActivity.isTop && ShortmsgListActivity.number != null)) {
            intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
            intent.putExtra("data", recvmsg);
            AppContext.sendBroadcast(intent);
        }

        new SendNetBackData().execute(recvdata[0].split(","));


        BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", strsrcname);
        if (contact != null) {
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
        messageNotificationID = Integer.parseInt(recvdata[0].split(",")[1]);
        // 更新通知栏
        messageNotification.setLatestEventInfo(AppContext, contact == null ? strsrcname : (contact.get("name").toString() + "的新消息"), recvdata[2], messagePendingIntent);
        messageNotificatioManager.notify(messageNotificationID, messageNotification);

//		}
    }

    public void analyBackNetEncryptTextMsg(byte[] data) throws UnsupportedEncodingException {
        Intent intent = new Intent();
        int namelen = ByteUtil.byte2Int(new byte[]
                {data[5], data[6]});

        byte[] srcname = new byte[namelen];
        System.arraycopy(data, 7, srcname, 0, namelen);
        String strsrcname = new String(srcname, "UTF-8");

        String[] recvdata = strsrcname.split("#");

        BaseMapObject recvmsg = new BaseMapObject();
        recvmsg.put("id", null);
        recvmsg.put("number", recvdata[0].split(",")[0]);
        recvmsg.put("sendtype", AbsMsgRecorderActivity.RECVFROM + "");
        recvmsg.put("status", "0");
        recvmsg.put("msgtype", "0");
        recvmsg.put("msgcontent", "解密中...&" + recvdata[2]);
        recvmsg.put("creattime", UnixTime.getStrCurrentUnixTime());
        recvmsg.put("priority", O.Priority);
        recvmsg.put("acknowledgemen", O.Acknowledgemen);
        recvmsg.put("exp2", "2");
        long ret = recvmsg.InsertObj2DB(AppContext, "shortmsg");

        if (ShortMsgFragment.isTop || (ShortmsgListActivity.isTop && ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(recvdata[0].split(",")[0]))) {
            intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
            intent.putExtra("data", recvmsg);
            AppContext.sendBroadcast(intent);
        }

        new SendNetBackData().execute(recvdata[0].split(","));
        new SendDataTask().execute(APICode.SEND_Decryption + "", recvdata[2]);

        BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", strsrcname);
        if (contact != null) {
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
        messageNotificationID = Integer.parseInt(recvdata[0].split(",")[1]);
        // 更新通知栏
        messageNotification.setLatestEventInfo(AppContext, contact == null ? strsrcname : (contact.get("name").toString() + "的新消息"), recvdata[2], messagePendingIntent);
        messageNotificatioManager.notify(messageNotificationID, messageNotification);

//		}
    }

    public void analyBackEmail(byte[] data) throws UnsupportedEncodingException {
        Intent intent = new Intent();
        int alllen = ByteUtil.byte2Int(new byte[]
                {data[2], data[3]});
        int idlen = alllen - 2;// 长度本身1字节，命令码1字节，成败1字节

        byte[] srcname = new byte[idlen];
        System.arraycopy(data, 6, srcname, 0, idlen);
        String id = new String(srcname);
        SocketConnection.sendDataCallback.remove("APICode.SEND_Email#" + id);
        BaseMapObject senditem = GetData4DB.getObjectByid(AppContext, "emailmsg", id);
        senditem.put("sendtype", data[5] == 0 ? AbsBaseActivity.SENDERROR + "" : AbsBaseActivity.SENDSUCCESS + "");
        senditem.UpdateMyself(AppContext, "emailmsg");

        if (EmailFragment.isTop) {
            intent.setAction(AbsBaseActivity.broad_backemailresult_Action);
            // intent.putExtra("data", senditem);
            AppContext.sendBroadcast(intent);
        }

        // Intent intent = new Intent();
    }

    //解析短消息的回复
    public void analyBackTextMsg(byte[] data) throws UnsupportedEncodingException {
        Intent intent = new Intent();
        int alllen = ByteUtil.byte2Int(new byte[]
                {data[2], data[3]});
        int idlen = alllen - 2;// 长度本身1字节，命令码1字节，成败1字节
        if (idlen == 0) {
            return;
        }
        byte[] srcname = new byte[idlen];
        System.arraycopy(data, 6, srcname, 0, idlen);
        String id = new String(srcname);
        BaseMapObject senditem = GetData4DB.getObjectByid(AppContext, "shortmsg", id);
        SocketConnection.sendDataCallback.remove("APICode.SEND_ShortTextMsg#" + id); // 收到短消息返回，则删除缓存
        senditem.put("sendtype", data[5] == 0 ? AbsBaseActivity.SENDERROR + "" : AbsBaseActivity.SENDSUCCESS + "");
        senditem.UpdateMyself(AppContext, "shortmsg");

        if (senditem != null && ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(senditem.get("number").toString())) {
            intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
            intent.putExtra("data", senditem);
            AppContext.sendBroadcast(intent);
        }

        // Intent intent = new Intent();
    }


    public void analyVoiceMsg(byte[] data) throws UnsupportedEncodingException {

        Intent intent = new Intent();

        int voicecursor = 5;
        int vnlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
        byte[] srcvname = new byte[vnlen];
        System.arraycopy(data, voicecursor, srcvname, 0, vnlen);
        String vname = new String(srcvname, "UTF-8");
        voicecursor += vnlen;

        int myvlen = ByteUtil.byte2Int(new byte[]
                {data[voicecursor], data[voicecursor + 1]});

        voicecursor += 2;

        byte[] myvname = new byte[myvlen];
        System.arraycopy(data, voicecursor, myvname, 0, myvlen);
        String mstryvname = new String(myvname, "UTF-8");
        voicecursor += myvlen;

        int vclen = ByteUtil.byte2Int(new byte[]
                {data[voicecursor], data[voicecursor + 1]});
        voicecursor += 2;
        byte[] vconten = new byte[vclen];
        System.arraycopy(data, voicecursor, vconten, 0, vclen);
        // String co = new String(conten, "UTF-8");
        String vpath = ByteUtil.getFile(vconten, O.SDCardRoot, vname + "_" + UnixTime.getStrCurrentUnixTime() + ".amr");
        if (vpath == null) {
            return;
        } else {
            BaseMapObject recvvoicemsg = new BaseMapObject();
            recvvoicemsg.put("id", null);
            recvvoicemsg.put("number", vname.split(",")[0]);
            recvvoicemsg.put("sendtype", AbsMsgRecorderActivity.RECVFROM + "");
            recvvoicemsg.put("status", "0");
            recvvoicemsg.put("msgtype", "1");
            recvvoicemsg.put("msgcontent", vpath);
            recvvoicemsg.put("creattime", UnixTime.getStrCurrentUnixTime());
            recvvoicemsg.put("priority", O.Priority);
            recvvoicemsg.put("acknowledgemen", O.Acknowledgemen);
            recvvoicemsg.put("exp2", "2");
            recvvoicemsg.InsertObj2DB(AppContext, "shortmsg");

            // MyLog.showToast(AppContext, ""+(ShortmsgListActivity.isTop &&
            // ShortmsgListActivity.number != null &&
            // ShortmsgListActivity.number.equals(vname)));

            // MyLog.LogV("istop", (""+ShortmsgListActivity.isTop) +
            // (ShortmsgListActivity.number != null) +
            // (ShortmsgListActivity.number.equals(vname)));

            if (ShortMsgFragment.isTop || (ShortmsgListActivity.isTop && ShortmsgListActivity.number != null)) {
                intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
                intent.putExtra("data", recvvoicemsg);
                AppContext.sendBroadcast(intent);
            }
            new SendNetBackData().execute(vname.split(","));

//                        else
//			{
            BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", vname);
            if (contact != null) {
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
            try {
                messageNotificationID = Integer.parseInt(vname);
            } catch (Exception e) {
                messageNotificationID = Integer.parseInt(vname.split(",")[1]);
            }

            // 更新通知栏
            messageNotification.setLatestEventInfo(AppContext, contact == null ? vname : (contact.get("name").toString() + "的新消息"), "[语音]", messagePendingIntent);
            messageNotificatioManager.notify(messageNotificationID, messageNotification);

//			}
        }

    }

    public void analyrecvWiredFile(byte[] data) throws UnsupportedEncodingException {

        Intent intent = new Intent();

        int voicecursor = 5;
        int vnlen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
        byte[] srcaname = new byte[vnlen];
        System.arraycopy(data, voicecursor, srcaname, 0, vnlen);
        String cname = new String(srcaname, "UTF-8");
        voicecursor += vnlen;

        int flen = ByteUtil.oneByte2oneInt(data[voicecursor++]);
        byte[] srcfname = new byte[flen];
        System.arraycopy(data, voicecursor, srcfname, 0, vnlen);
        String fname = new String(srcfname, "UTF-8");
        voicecursor += flen;

        int vclen = ByteUtil.byte2Int(new byte[]
                {data[voicecursor], data[voicecursor + 1]});// .oneByte2oneInt(data[voicecursor++]);
        voicecursor += 2;
        byte[] vconten = new byte[vclen];
        System.arraycopy(data, voicecursor, vconten, 0, vclen);
        String co = new String(vconten, "UTF-8");
        String vpath = ByteUtil.getFile(vconten, O.SDCardRoot, fname);

        if (vpath == null) {
            return;
        } else {
            FileStatusActivity.recvpath = vpath;
            BaseMapObject record = new BaseMapObject();
            record.put("id", null);
            record.put("number", cname);
            record.put("sendtype", "1");// 语音0.文件1
            record.put("status", "1");// 呼入成功/呼出成功/呼入失败/呼出失败(1,2,3,4)
            record.put("filepath", vpath);
            record.put("creattime", UnixTime.getStrCurrentUnixTime());
            record.InsertObj2DB(AppContext, "wiredrecord");
        }

    }

    public void analyEmail(byte[] data) throws UnsupportedEncodingException {

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
                {data[voicecursor], data[voicecursor + 1]});
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
                {data[voicecursor], data[voicecursor + 1]});
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
        recvvoicemsg.put("priority", O.Priority);
        recvvoicemsg.put("acknowledgemen", O.Acknowledgemen);

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
                    {data[voicecursor], data[voicecursor + 1]});
            voicecursor += 2;
            byte[] fjcon = new byte[fjlen];
            System.arraycopy(data, voicecursor, fjcon, 0, fjlen);
            String vpath = ByteUtil.getFile(fjcon, O.SDCardRoot, co);

            recvvoicemsg.put("haveattachments", "1");
            recvvoicemsg.put("attachmentsname", co);
            recvvoicemsg.put("attachmentspath", vpath);

        } else {
            recvvoicemsg.put("haveattachments", "0");
            recvvoicemsg.put("attachmentsname", "");
            recvvoicemsg.put("attachmentspath", "");

        }
        recvvoicemsg.InsertObj2DB(AppContext, "emailmsg");

        if (EmailFragment.isTop) {
            intent.setAction(AbsBaseActivity.broad_Email_Action);
            intent.putExtra("data", recvvoicemsg);
            AppContext.sendBroadcast(intent);
        }
//                else
//		{
        BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", vname);
        if (contact != null) {
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

//		}

    }

    public void analyBackVoiceCode(byte[] data) throws UnsupportedEncodingException {

        Intent intent = new Intent();
        if (CallWaitActivity.iswait) {
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

    public void analyBackBroadcast(byte[] data) throws UnsupportedEncodingException {

        Intent intent = new Intent();
        intent.setAction(AbsBaseActivity.broad_broadcastresult_Action);
        if (data[5] == 1)// 成功响应
        {
            intent.putExtra("data", "true");
        } else if (data[5] == 0)// 失败响应
        {
            intent.putExtra("data", "false");
        }
        AppContext.sendBroadcast(intent);

    }

    public void analyBackWiredVoice(byte[] data) throws UnsupportedEncodingException {

        Intent intent = new Intent();
        if (CallWaitActivity.iswait) {
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

    public void analyBackSpecialVoice(byte[] data) throws UnsupportedEncodingException {

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

    public void analyProgress(byte[] data) throws UnsupportedEncodingException {

        Intent intent = new Intent();
        intent.setAction(AbsBaseActivity.broad_fileprogress_Action);
        int p = ByteUtil.oneByte2oneInt(data[5]);
        intent.putExtra("progress", p);
        AppContext.sendBroadcast(intent);

    }

    public void analyRecvVoicecode(byte[] data) throws UnsupportedEncodingException {

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

    public void analyRecvWiredVoice(byte[] data) throws UnsupportedEncodingException {

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

    public void analyInteraput(byte[] data) throws UnsupportedEncodingException {

        Intent intent = new Intent();
        intent.setAction(AbsBaseActivity.broad_interaput_Action);
        AppContext.sendBroadcast(intent);

    }

    public void analyFileBackresult(byte[] data) throws UnsupportedEncodingException {

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

    public void analyBroadcast(byte[] data) throws UnsupportedEncodingException {

        Intent intent = new Intent();
        // 源地址
        int voicecursor = 5;
        int flen = ByteUtil.byte2Int(new byte[]
                {data[5], data[6]});

        byte[] fconten = new byte[flen];

        System.arraycopy(data, voicecursor, fconten, 0, flen);
        String filename = UnixTime.getStrCurrentUnixTime() + "_broad.txt";
        String vpath = ByteUtil.getFile(fconten, O.SDCardRoot, filename);
        if (BroadCastctivity.number != null) {
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

    public void analyBackCodedirc(byte[] data) throws UnsupportedEncodingException {
        Intent intent = new Intent();
        int alllen = ByteUtil.byte2Int(new byte[]
                {data[2], data[3]});
        int idlen = alllen - 2;// 长度本身1字节，命令码1字节，成败1字节

        byte[] srcname = new byte[idlen];
        System.arraycopy(data, 6, srcname, 0, idlen);
        String id = new String(srcname);
        SocketConnection.sendDataCallback.remove("APICode.SEND_CodeDirec#" + id);

        BaseMapObject senditem = GetData4DB.getObjectByid(AppContext, "codedirect", id);
        senditem.put("sendtype", data[5] == 0 ? AbsBaseActivity.SENDERROR + "" : AbsBaseActivity.SENDSUCCESS + "");
        senditem.UpdateMyself(AppContext, "codedirect");

        if (CodeDirectFragment.isTop) {
            intent.setAction(AbsBaseActivity.broad_recvcodedirc_Action);
            AppContext.sendBroadcast(intent);
        }

    }

    public void analyRecvCodedirc(byte[] data) throws UnsupportedEncodingException {
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

        int clen = ByteUtil.byte2Int(new byte[]
                {data[cursor], data[cursor + 1]});
        cursor += 2;
        byte[] conten = new byte[clen];
        System.arraycopy(data, cursor, conten, 0, clen);
        String co = new String(conten, "UTF-8");

        BaseMapObject email = new BaseMapObject();
        email.put("id", null);
        email.put("number", strsrcname);
        email.put("sendtype", "1"); // 1,收，2,发，3,草稿
        email.put("codetype", "0"); // 不记录军标类型
        email.put("codecontent", co);
        email.put("priority", O.Priority);
        email.put("acknowledgemen", O.Acknowledgemen);
        email.put("creattime", UnixTime.getStrCurrentUnixTime());
        email.InsertObj2DB(AppContext, "codedirect");

        if (CodeDirectFragment.isTop) {
            intent.setAction(AbsBaseActivity.broad_recvcodedirc_Action);
            intent.putExtra("data", email);
            AppContext.sendBroadcast(intent);
        }
//                else
//		{
        BaseMapObject contact = GetData4DB.getObjectByRowName(AppContext, "contact", "number", strsrcname);
        if (contact != null) {
            email.put("name", contact.get("name").toString());
        }
        messageNotification = new Notification();
        messageNotification.icon = R.drawable.ic_launcher;
        messageNotification.tickerText = "你有一条新的代码指挥";
        messageNotification.flags = messageNotification.FLAG_AUTO_CANCEL;
        messageNotification.defaults = Notification.DEFAULT_SOUND;
        messageNotificatioManager = (NotificationManager) AppContext.getSystemService(AppContext.NOTIFICATION_SERVICE);
        messageIntent = new Intent(AppContext, CodeDirectInfoActivity.class);
        messageIntent.putExtra("item", email);
        messageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        messagePendingIntent = PendingIntent.getActivity(AppContext, 0, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        messageNotificationID = Integer.parseInt(strsrcname);
        // 更新通知栏
        messageNotification.setLatestEventInfo(AppContext, contact == null ? strsrcname : (contact.get("name").toString() + "的新消息"), showDetail(email), messagePendingIntent);
        messageNotificatioManager.notify(messageNotificationID, messageNotification);

//		}
    }

    public void analyRecvLocation(byte[] data) {
        Intent intent = new Intent();
        try {

            int locationcursor = 5;

            // 经度
            int lnglen = ByteUtil.oneByte2oneInt(data[locationcursor++]);
            byte[] lngdata = new byte[lnglen];
            System.arraycopy(data, locationcursor, lngdata, 0, lnglen);
            String lng = new String(lngdata, "UTF-8");
            SettingActivity.LoactionInfo.put("lng", lng);
            locationcursor += (lnglen);

            // 纬度
            int latlen = ByteUtil.oneByte2oneInt(data[locationcursor++]);
            byte[] latdata = new byte[latlen];
            System.arraycopy(data, locationcursor, latdata, 0, latlen);
            String lat = new String(latdata, "UTF-8");
            SettingActivity.LoactionInfo.put("lat", lat);

            locationcursor += latlen;

            // 高度
            int higlen = ByteUtil.oneByte2oneInt(data[locationcursor++]);
            byte[] higdata = new byte[higlen];
            System.arraycopy(data, locationcursor, higdata, 0, higlen);
            String high = new String(higdata, "UTF-8");
            SettingActivity.LoactionInfo.put("high", high);

            locationcursor += higlen;

            // 设备连接状态
            int statuslen = ByteUtil.oneByte2oneInt(data[locationcursor++]);
            byte[] statusdata = new byte[statuslen];
            System.arraycopy(data, locationcursor, statusdata, 0, statuslen);
            String status = new String(statusdata, "UTF-8");
            SettingActivity.LoactionInfo.put("status", status);

            locationcursor += statuslen;

            // 软件版本
            int versionlen = ByteUtil.oneByte2oneInt(data[locationcursor++]);
            byte[] versiondata = new byte[versionlen];
            System.arraycopy(data, locationcursor, versiondata, 0, versionlen);
            String version = new String(versiondata, "UTF-8");
            SettingActivity.LoactionInfo.put("version", version);

            locationcursor += statuslen;

            intent.setAction(AbsBaseActivity.broad_backlocation_Action);
            AppContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String showDetail(BaseMapObject detail) {

        String[] param = detail.get("codecontent").toString().split("&");
        for (String s : param) {
            String[] sp = s.split("=");
            if (sp.length == 2)
                detail.put(sp[0], sp[1]);
        }

        String analydata = "";
        // Map<String,Object> data =
        // JSONUtil.getMapFromJson(AbsCreatCodeActivity.getassetsCode(mContext,"actioncode.txt"));
        switch (Integer.parseInt(detail.get("P1").toString())) {
            case 0:
                analydata += "军标" + "-";
                switch (Integer.parseInt(detail.get("P2").toString())) {
                    case 0:
                        analydata += "空中目标" + "-";
                        analydata += CodeDirectInfoActivity.getcodenumName(AppContext, "skycode", detail.get("P3").toString()) + "-";
                        analydata += (detail.get("P4") == null ? "" : "军标名称：" + detail.get("P4").toString() + "-");
                        analydata += CodeDirectInfoActivity.getcodenumName(AppContext, "jbcolor", detail.get("P5").toString()) + "-";
                        analydata += detail.get("P6").toString() + "-";

                        break;
                    case 1:
                        analydata += "地面目标" + "-";
                        analydata += CodeDirectInfoActivity.getcodenumName(AppContext, "earthcode", detail.get("P3").toString()) + "-";
                        analydata += (detail.get("P4") == null ? "" : "军标名称：" + detail.get("P4").toString() + "-");
                        analydata += CodeDirectInfoActivity.getcodenumName(AppContext, "jbcolor", detail.get("P5").toString()) + "-";
                        analydata += detail.get("P6").toString() + "-";

                        break;
                    case 10:
                        analydata += "水面目标" + "-";
                        analydata += CodeDirectInfoActivity.getcodenumName(AppContext, "watercode", detail.get("P3").toString()) + "-";
                        analydata += (detail.get("P4") == null ? "" : "军标名称：" + detail.get("P4").toString() + "-");
                        analydata += CodeDirectInfoActivity.getcodenumName(AppContext, "jbcolor", detail.get("P5").toString()) + "-";
                        analydata += detail.get("P6").toString() + "-";

                        break;
                    case 11:
                        analydata += "其他目标" + "-";
                        analydata += CodeDirectInfoActivity.getcodenumName(AppContext, "skycode", detail.get("P3").toString()) + "-";
                        analydata += (detail.get("P4") == null ? "" : "军标名称：" + detail.get("P4").toString() + "-");
                        analydata += CodeDirectInfoActivity.getcodenumName(AppContext, "jbcolor", detail.get("P5").toString()) + "-";
                        analydata += detail.get("P6").toString() + "-";

                        break;
                }
                break;
            case 1:
                analydata += "-";
                switch (Integer.parseInt(detail.get("P2").toString())) {
                    case 0:
                        analydata += "集结" + "-";
                        break;
                    case 1:
                        analydata += "疏散" + "-";
                        break;
                    case 10:
                        analydata += "进攻" + "-";
                        break;
                    case 11:
                        analydata += "撤退" + "-";
                        break;
                }
                analydata += (detail.get("P3") == null ? "" : "命令名称：" + detail.get("P4").toString() + "-");
                analydata += (detail.get("P4").toString()) + "-";
                analydata += (detail.get("P5") == null ? "" : "目的地名称：" + detail.get("P5").toString() + "-");
                analydata += (detail.get("P6") == null ? "" : "目的地介绍：" + detail.get("P6").toString() + "-");
                analydata += (detail.get("P8") == null ? "" : "开始执行时间：" + detail.get("P8").toString() + "-");
                analydata += (detail.get("P9") == null ? "" : "间隔时间：" + detail.get("P9").toString() + "-");

                break;
            case 10:
                analydata += "行动命令反馈" + "-";
                switch (Integer.parseInt(detail.get("P3").toString())) {
                    case 0:
                        analydata += "反馈类型:" + "待执行" + "-";
                        break;
                    case 1:
                        analydata += "反馈类型:" + "已执行" + "-";
                        break;
                    case 10:
                        analydata += "反馈类型:" + "不能执行" + "-";
                        break;
                    case 11:
                        analydata += "反馈类型:" + "执行完毕" + "-";
                        break;
                }
                analydata += (detail.get("P2") == null ? "" : "命令名称：" + detail.get("P2").toString() + "-");
                analydata += (detail.get("P4") == null ? "" : "反馈描述信息：" + detail.get("P4").toString() + "-");
                analydata += (detail.get("P5") == null ? "" : "反馈时间：" + detail.get("P5").toString() + "-");

                break;
            case 11:
                analydata += "资源申请" + "-";

                analydata += (detail.get("P2") == null ? "" : detail.get("P2").toString() + "-");
                analydata += (detail.get("P3") == null ? "" : "物资名称：" + detail.get("P3").toString() + "-");
                analydata += (detail.get("P4") == null ? "" : "物资数量：" + detail.get("P4").toString() + "-");
                analydata += (detail.get("P5") == null ? "" : "物资单位：" + detail.get("P5").toString() + "-");
                analydata += (detail.get("P6") == null ? "" : "受领物资地：" + detail.get("P6").toString() + "-");

                break;
            case 100:
                analydata += "威胁报警通报" + "-";
                switch (Integer.parseInt(detail.get("P2").toString())) {
                    case 0:
                        analydata += "威胁报警类型:" + "规避区" + "-";
                        break;
                    case 1:
                        analydata += "威胁报警类型:" + "化学污染区" + "-";
                        break;
                    case 10:
                        analydata += "威胁报警类型:" + "核污染区" + "-";
                        break;
                }

                switch (Integer.parseInt(detail.get("P3").toString())) {
                    case 0:
                        analydata += "威胁区形状:" + "图形" + "-";
                        break;
                    case 1:
                        analydata += "威胁区形状:" + "椭圆" + "-";
                        break;
                    case 10:
                        analydata += "威胁区形状:" + "正方形" + "-";
                        break;
                    case 11:
                        analydata += "威胁区形状:" + "长方形" + "-";
                        break;
                    case 100:
                        analydata += "威胁区形状:" + "多边形" + "-";
                        break;
                }
                analydata += (detail.get("P4") == null ? "" : "威胁区说明：" + detail.get("P4").toString() + "-");

                break;
            case 101:
                analydata += "警报控制" + "-";
                switch (Integer.parseInt(detail.get("P2").toString())) {
                    case 0:
                        analydata += "威胁报警类型:" + "预警" + "-";
                        break;
                    case 1:
                        analydata += "威胁报警类型:" + "空袭" + "-";
                        break;
                    case 10:
                        analydata += "威胁报警类型:" + "解警" + "-";
                        break;
                    case 11:
                        analydata += "威胁报警类型:" + "消防" + "-";
                        break;
                }

                switch (Integer.parseInt(detail.get("P3").toString())) {
                    case 0:
                        analydata += "警报方式:" + "文字报警" + "-";
                        break;
                    case 1:
                        analydata += "警报方式:" + "声音报警" + "-";
                        break;
                }
                analydata += (detail.get("P4") == null ? "" : "警报控制说明：" + detail.get("P4").toString() + "-");

                break;
            case 110:
                analydata += "北斗报文" + "-";

                analydata += (detail.get("P2") == null ? "" : "北斗入网卡号：" + detail.get("P2").toString() + "-");
                analydata += (detail.get("P3") == null ? "" : detail.get("P3").toString() + "-");

                break;
        }
        return analydata;
    }


    public void analyDebugInfo(byte[] data) {
        Intent intent = new Intent();
        intent.setAction(AbsBaseActivity.broad_debug_info);
        intent.putExtra("data", data);
        AppContext.sendBroadcast(intent);
    }

    public void analyHostconfig(byte[] data) {
        Intent intent = new Intent();
        int flen = ByteUtil.byte2Int(new byte[]
                {data[2], data[3]}) - 1;

        byte[] fconten = new byte[flen];

        System.arraycopy(data, 5, fconten, 0, flen);
        try {
            String content = new String(fconten, "UTF-8");
            intent.setAction(AbsBaseActivity.broad_config_host);
            intent.putExtra("data", content);
            AppContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void analychannelCfg(byte[] data) {
        Intent intent = new Intent();
        intent.setAction(AbsBaseActivity.broad_query_channel);
        intent.putExtra("data", data);
        AppContext.sendBroadcast(intent);

    }


}
