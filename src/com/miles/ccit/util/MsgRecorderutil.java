package com.miles.ccit.util;

import java.io.File;
import java.util.UUID;

import com.miles.ccit.net.SocketConnection;

import android.content.Context;
import android.media.MediaRecorder;

public class MsgRecorderutil {
    private MediaRecorder mediaRecorder;
    public File voiceFile;
    public long startTime;
    public long endTime;

    public void startRecorder() {
        try {
            mediaRecorder = new MediaRecorder();
            voiceFile = new File(O.SDCardRoot + UUID.randomUUID().toString() + ".amr");
            voiceFile.createNewFile();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(voiceFile.getAbsolutePath());
            mediaRecorder.prepare();
            startTime = System.currentTimeMillis();

            mediaRecorder.start();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void stopRecorder() {
        if (mediaRecorder != null) {
            endTime = System.currentTimeMillis();
            mediaRecorder.stop();
            mediaRecorder.release();

            mediaRecorder = null;
        }
    }

    public String getRecorderpath() {
        return voiceFile.getAbsolutePath();
    }

    public static long insertTextmsg(Context contex, String contact, String msgcontent, int type) {
        return insertTextmsg(contex, contact, null, msgcontent, type);
    }

    public static long insertTextmsg(Context contex, String contact, String group, String msgcontent, int type) {
        BaseMapObject shortmsg = new BaseMapObject();
        shortmsg.put("id", null);
        if (type == O.NET && group != null) {
            shortmsg.put("number", group + "@" + contact + "@" + O.LOCALIP);  //群组名称@联系人列表@本地地址
            shortmsg.put("sendtype", AbsMsgRecorderActivity.SENDSUCCESS + ""); // 群组不检测状态
        } else {
            shortmsg.put("number", contact);
            shortmsg.put("sendtype", AbsMsgRecorderActivity.SENDNOW + ""); // 正在发送
        }

        shortmsg.put("status", "1"); // 已读
        shortmsg.put("msgtype", "0"); // 语音
        shortmsg.put("msgcontent", msgcontent);
        shortmsg.put("creattime", UnixTime.getStrCurrentUnixTime());
        shortmsg.put("priority", O.Priority);
        shortmsg.put("acknowledgemen", O.Acknowledgemen);
        shortmsg.put("exp2", type + "");
        long id = shortmsg.InsertObj2DB(contex, "shortmsg");

//        SocketConnection.sendDataCallback.put("APICode.SEND_ShortTextMsg#" + id, shortmsg);

        return id;
        // insertRecvTextmsg(contex, contact, msgcontent);
    }

    public static long insertVoicemsg(Context contex, String contact, String group, String msgcontent, int type) {
        BaseMapObject shortmsg = new BaseMapObject();
        shortmsg.put("id", null);
        if (type == O.NET && group != null) {
            shortmsg.put("number", group + "@" + contact + "@" + O.LOCALIP);  //群组名称@联系人列表@本地地址
            shortmsg.put("sendtype", AbsMsgRecorderActivity.SENDSUCCESS + ""); // 群组不检测状态
        } else {
            shortmsg.put("number", contact);
            shortmsg.put("sendtype", AbsMsgRecorderActivity.SENDNOW + ""); // 正在发送
        }
        shortmsg.put("status", "1");
        shortmsg.put("msgtype", "1");
        shortmsg.put("msgcontent", msgcontent);
        shortmsg.put("creattime", UnixTime.getStrCurrentUnixTime());
        shortmsg.put("priority", O.Priority);
        shortmsg.put("acknowledgemen", O.Acknowledgemen);
        shortmsg.put("exp2", type + "");
        long id = shortmsg.InsertObj2DB(contex, "shortmsg");
        SocketConnection.sendDataCallback.put("APICode.SEND_ShortTextMsg#" + id, shortmsg);
        return id;
    }

    public long getTimeline() {
        return endTime - startTime;
    }

}
