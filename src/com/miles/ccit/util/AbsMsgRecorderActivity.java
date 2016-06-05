package com.miles.ccit.util;

import java.util.Timer;
import java.util.TimerTask;

import com.miles.ccit.duomo.R;
import com.miles.ccit.net.APICode;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class AbsMsgRecorderActivity extends AbsBaseActivity {
    public EditText edit_inputMsg;
    public Button Btn_switchVoice;
    public Button Btn_Send;
    public Button Btn_Talk;
    public MsgRecorderutil mediaRecorder;
    public Timer timer;
    private long currentlong = 0;
    public String strContatc = "";
    public boolean isUp = false;


    Handler handle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (currentlong >= 14) {
                mediaRecorder.stopRecorder();
//                talkTouchUp(null, 1);
                isUp = true;
                MyLog.showToast(mContext, "超过最大时间");
                currentlong = 0;
                timer.cancel();
            } else {
                ((TextView) findViewById(R.id.voidHinttime)).setText("还剩" + (14 - currentlong) + "秒");
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public String getStrContatc() {
        return strContatc;
    }

    public void setStrContatc(String strContatc) {
        this.strContatc = strContatc;
    }

    public void switchVoice() {
        if (edit_inputMsg.getVisibility() == View.VISIBLE) {
            edit_inputMsg.setVisibility(View.GONE);
            Btn_Talk.setVisibility(View.VISIBLE);
            Btn_switchVoice.setBackgroundResource(R.drawable.btntext);
            Btn_Send.setEnabled(false);
        } else {
            edit_inputMsg.setVisibility(View.VISIBLE);
            Btn_Talk.setVisibility(View.GONE);
            Btn_switchVoice.setBackgroundResource(R.drawable.btnvoice);

            Btn_Send.setEnabled(true);
        }
    }


    public long sendTextmsg(String contact, int type, boolean trans) {

        return sendTextmsg(contact, null, type, trans);

    }

    public long sendTextmsg(String contact, String group, int type, boolean trans) {
        long ret = -1;
        if (contact.equals("")) {
            Toast.makeText(mContext, "联系人不能为空。", Toast.LENGTH_SHORT).show();
            return ret;
        } else if (edit_inputMsg.getText().toString().equals("")) {
            Toast.makeText(mContext, "发送内容不能为空。", Toast.LENGTH_SHORT).show();
            return ret;
        } else {
            //单人发送
            if (contact.indexOf(",") == -1) {
                ret = MsgRecorderutil.insertTextmsg(mContext, contact, edit_inputMsg.getText().toString(), type);
                sendTextMsgtoNet(new long[]{ret}, new String[]{contact}, edit_inputMsg.getText().toString(), type, trans);
            } else {
                //多人发送
                if (type == O.NET) {
                    //网络模式下多人代表群发
                    ret = MsgRecorderutil.insertTextmsg(mContext, contact, group, edit_inputMsg.getText().toString(), type);
                    sendTextMsgtoNet(new long[]{ret}, new String[]{group + "@" + contact + "@" + O.LOCALIP}, edit_inputMsg.getText().toString(), type, trans);
                } else {
                    //专网模式下多人，代表单个发送
                    String[] tmparray = contact.split(",");
                    long[] arrayid = new long[tmparray.length];
                    for (int i = 0; i < tmparray.length; i++) {
                        ret = MsgRecorderutil.insertTextmsg(mContext, tmparray[i], edit_inputMsg.getText().toString(), type);
                        arrayid[i] = ret;
                    }
                    sendTextMsgtoNet(arrayid, tmparray, edit_inputMsg.getText().toString(), type, trans);
                }
            }

        }
        return ret;
    }


    public void sendTextMsgtoNet(long[] id, String[] contact, String msgcontent, int type, boolean trans) {
        if (type == 2) {
            //网络模式
            String desCon = "";
            for (int i = 0; i < id.length; i++) {
                desCon += contact[i] + ",";
            }
            desCon = desCon.substring(0, desCon.length() - 1);
            if (trans)   //是否转发
            {
                desCon = "";
                for (int i = 0; i < id.length; i++) {
                    desCon += (contact[i] + "," + id[i] + ",");
                }
                desCon = desCon.substring(0, desCon.length() - 1);

                new SendDataTask().execute(APICode.SEND_Trans_data + "", O.LOCALIP + "," + id[0], desCon, msgcontent);
                return;
            }
            if (O.isEncrypt) {
                new SendDataTask().execute(APICode.SEND_Encrypt + "", msgcontent);
            } else {
                new SendNetData().execute(APICode.SEND_NET_ShortTextMsg + "", O.LOCALIP + "," + id[0], desCon, msgcontent);
            }

        } else {
            //专网模式
            String desCon = "";
            for (int i = 0; i < id.length; i++) {
                desCon += (contact[i] + "," + id[i] + ",");
            }
            desCon = desCon.substring(0, desCon.length() - 1);
            new SendDataTask().execute(APICode.SEND_ShortTextMsg + "", O.Account, desCon, msgcontent);
        }

    }

    public static void sendVoiceMsgtoNet(long[] id, String[] contact, String voicepath, int type) {
        if (type == 2) {
            //网络模式
            new SendNetData().execute(APICode.SEND_NET_ShortVoiceMsg + "", O.LOCALIP + "," + id[0], contact[0], voicepath);

//            if (contact[0].indexOf("@") == -1) {
//                //单人
//                  } else {
//                String[] ipaddr = contact[0].split("@")[1].split(",");
//                String desCon = "";
//                for (int i = 0; i < ipaddr.length; i++) {
//                    desCon += ipaddr[i] + ",";
//                }
//                desCon = desCon.substring(0, desCon.length() - 1);
//
//                new SendNetData().execute(APICode.SEND_NET_ShortVoiceMsg + "", O.LOCALIP + "," + id[0], desCon, voicepath);
//
//            }
        } else {
            //专网模式

            String desCon = "";
            for (int i = 0; i < id.length; i++) {
                desCon += (contact[i] + "," + id[i] + ",");
            }
            desCon = desCon.substring(0, desCon.length() - 1);
            new SendDataTask().execute(APICode.SEND_ShortVoiceMsg + "", O.Account, desCon, voicepath);
        }
    }


    public boolean talkTouchDown(String contact) {
        if (contact.equals("")) {
            MyLog.showToast(mContext, "请输入联系人号码。");
            return false;
        }
        setStrContatc(contact.indexOf("@") == -1 ? contact : contact.split("@")[1]);
        findViewById(R.id.voice_hint_layout).setVisibility(View.VISIBLE);
        ((AnimationDrawable) ((ImageView) findViewById(R.id.voice_hint_flash)).getDrawable()).start();
        ((TextView) findViewById(R.id.voiceHintText)).setText("上滑手指,取消发送");
        Btn_Talk.setText("松开发送");

        mediaRecorder = new MsgRecorderutil();
        mediaRecorder.startRecorder();
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                currentlong++;
                handle.sendMessage(new Message());

            }
        }, 10, 1000);
        return false;
    }

    public boolean talkTouchUp(MotionEvent event, int type, String contact, String group) {
        if (timer != null) {
            timer.cancel();
            currentlong = 0;
        }
        if (isUp) {
            isUp = false;
        } else {
            findViewById(R.id.voice_hint_layout).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.voiceHintText)).setText("松开手指发送");
            Btn_Talk.setText("按住说话");
            if (mediaRecorder != null) {
                mediaRecorder.stopRecorder();

                if (event != null) {
                    if (event.getY() < 0) {
                        Toast.makeText(mContext, "取消发送...", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                if (getStrContatc().equals("null")) {
                    return false;
                }
                //单人发送
                if (getStrContatc().indexOf(",") == -1) {
                    long ret = MsgRecorderutil.insertVoicemsg(mContext, getStrContatc(), null, mediaRecorder.getRecorderpath(), type);
                    sendVoiceMsgtoNet(new long[]{ret}, new String[]{getStrContatc()}, mediaRecorder.getRecorderpath(), type);
                } else {
                    if (type == O.NET) //网络模式下多人代表群发
                    {
                        long ret = MsgRecorderutil.insertVoicemsg(mContext, getStrContatc(), group, mediaRecorder.getRecorderpath(), type);
                        sendVoiceMsgtoNet(new long[]{ret}, new String[]{group + "@" + contact + "@" + O.LOCALIP}, mediaRecorder.getRecorderpath(), type);

                    } else {
                        String[] tmparray = getStrContatc().split(",");
                        long[] arrayid = new long[tmparray.length];

                        for (int i = 0; i < tmparray.length; i++) {
                            long ret = MsgRecorderutil.insertVoicemsg(mContext, tmparray[i], null, mediaRecorder.getRecorderpath(), type);
                            arrayid[i] = ret;
                        }
                        sendVoiceMsgtoNet(arrayid, tmparray, mediaRecorder.getRecorderpath(), type);
                    }


                }
            }
        }
        return false;
    }

}
