package com.miles.ccit.util;

import android.content.Intent;
import android.os.AsyncTask;

import com.miles.ccit.duomo.FileStatusActivity;
import com.miles.ccit.duomo.LoginActivity;
import com.miles.ccit.net.APICode;
import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.SocketConnection;

public class SendDataTask extends AsyncTask<String, Void, byte[]> {

    @Override
    protected byte[] doInBackground(String... parm) {
        // TODO Auto-generated method stub
        try {
            switch (Byte.parseByte(parm[0])) {
                case APICode.SEND_Login:
                    UserLog.i("登录", parm);
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendLogin(parm[1], parm[2]));

                    break;
                case APICode.SEND_Logout:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendLogout());
                    UserLog.i("注销", parm);
                    break;
                case APICode.SEND_ChangePwd:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendChangePwd(parm[1], parm[2]));
                    UserLog.i("修改密码", parm);

                    break;
                case APICode.SEND_ShortTextMsg:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendShortTextmsg(0, parm[1], parm[2], parm[3]));
                    UserLog.i("发送短消息", parm);
                    break;
                case APICode.SEND_ShortVoiceMsg:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendShortVoicemsg(1, parm[1], parm[2], parm[3]));
                    UserLog.i("发送短语音", parm);
                    break;
                case APICode.SEND_VoiceCode:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendStartVoicecode(parm[1], parm[2]));
                    UserLog.i("发起声码话", parm);
                    break;
                case APICode.SEND_TalkVoiceCode:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendVoiceTalk(parm[2]));
                    UserLog.i("声码话讲话", parm);

                    break;
                case APICode.SEND_Email:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendEMail(parm[1], parm[2], parm[3], parm[4], parm[5], parm[6]));
                    UserLog.i("发送电子邮件", parm);
                    break;
                case APICode.BACK_RECV_VoiceCode:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendRecvVoicecode(parm[2]));
                    UserLog.i("发送声码话接收响应", parm);
                    break;
                case APICode.SEND_Broadcast:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendBroadcast(parm[2]));
                    UserLog.i("设置发起广播", parm);
                    break;

                case APICode.SEND_SpecialVoice:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendSpecialVoice(parm[2]));
                    UserLog.i("发送转向语音", parm);

                    break;
                case APICode.SEND_TalkSpecialVoice:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendSpecialVoiceTalk(parm[2]));
                    UserLog.i("专项语音讲话", parm);

                    break;
                case APICode.SEND_Config:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendConfig(parm[1], parm[2], parm[3], parm[4]));
                    UserLog.i("发送配置信息", parm);

                    break;
                case APICode.SEND_WiredVoice:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendWiredVoice(parm[2]));
                    UserLog.i("发送有线语音", parm);
                    break;
                case APICode.SEND_SpecialNetInteraput:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendSpNetInteraput());
                    UserLog.i("专网模式中断", parm);
                    break;

                case APICode.SEND_NormalInteraput:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendNormalInteraput());
                    UserLog.i("其他模式中断", parm);

                    break;
                case APICode.SEND_BackModel:
                    if (LoginActivity.isLogin) {
                        SocketConnection.getInstance().readReqMsg(new ComposeData().sendBackmodel());
                        UserLog.i("返回模式", parm);
                    }
                    break;
                case APICode.BACK_RECV_WiredVoice:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendRecvWiredVoice(parm[2]));
                    UserLog.i("响应接收有线语音", parm);
                    break;
                case APICode.SEND_WiredFile:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendWiredFile(parm[2], FileStatusActivity.code));
                    UserLog.i("发送有线文件", parm);
                    break;
                case APICode.BACK_RECV_WiredFile:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendRecvWiredFile());
                    UserLog.i("响应接收有线文件", parm);
                    break;
                case APICode.SEND_FileResult:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendRecvresultWiredFile());
                    UserLog.i("发送文件接收结果", parm);
                    break;
                case APICode.SEND_CodeDirec:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendCodeDirc(parm[1], parm[2], parm[3]));
                    UserLog.i("发送代码指挥", parm);
                    break;
                case APICode.SEND_Location:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendLocation());
                    UserLog.i("获取位置信息", parm);
                    break;
                case APICode.SEND_WirelessContact:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendWirelessContact(parm[1]));
                    UserLog.i("上传无线联系人", parm);
                    break;
                case APICode.SEND_WiredContact:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendWiredContact(parm[1]));
                    UserLog.i("上传有线联系人", parm);
                    break;
                case APICode.SEND_QueryHost:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendQueryHost());
                    UserLog.i("查询主机路由", parm);
                    break;
                case APICode.SEND_RECV_HostCfg:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendHostCfg(parm[1]));
                    UserLog.i("返回主机配置", parm);
                    break;
                case APICode.SEND_QueryChannel:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendQueryChannel());
                    UserLog.i("查询信道优先级", parm);
                    break;
                case APICode.SEND_RECV_ChannelCfg:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendSetChannel(parm[1], parm[2], parm[3], parm[4], parm[5], parm[6]));
                    UserLog.i("返回信道优先级", parm);
                    break;
                case APICode.SEND_Encrypt:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendEncrypt(APICode.SEND_Encrypt, parm[1]));
                    UserLog.i("加密信息", parm);
                    break;
                case APICode.SEND_Decryption:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendEncrypt(APICode.SEND_Decryption, parm[1]));
                    UserLog.i("解密信息", parm);
                    break;
                case APICode.SEND_Trans_data:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendTransData(parm[1], parm[2], parm[3]));
                    UserLog.i("转发数据", parm);
                    break;

                case APICode.SEND_3G:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().send3G(APICode.SEND_3G, parm[1]));
                    UserLog.i("3G上网", parm);
                    break;

                case APICode.SEND_ZKJIAMI:
                    SocketConnection.getInstance().readReqMsg(new ComposeData().sendZKJiami(APICode.SEND_3G, parm[1]));
                    UserLog.i("指控系统加密", parm);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
            SocketConnection.getInstance().canleSocket();
            Intent intent = new Intent();
            intent.setAction(AbsBaseActivity.broad_login_Action);
            MyApplication.getAppContext().sendBroadcast(intent);
        }
        return null;
    }

    @Override
    protected void onPostExecute(byte[] result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
    }

}
