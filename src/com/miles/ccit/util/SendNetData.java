package com.miles.ccit.util;

import android.os.AsyncTask;

import com.miles.ccit.net.APICode;
import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.UDPNetModelTools;

/**
 * Created by niujason on 16/5/16.
 */
public class SendNetData extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {
        switch (Byte.parseByte(strings[0])) {
            case APICode.SEND_NET_ShortTextMsg:
                if (strings[2].indexOf(",") != -1)//多人发送
                {
                    String[] strCon = strings[2].split("@")[1].split(",");
                    for (String s : strCon) {
                        UDPNetModelTools.SendNteData(new ComposeData().sendShortNetTextmsg(APICode.SEND_NET_ShortTextMsg, strings[1],  strings[2], strings[3]), s.split(",")[0]);
                    }
                } else {
                    UDPNetModelTools.SendNteData(new ComposeData().sendShortNetTextmsg(APICode.SEND_NET_ShortTextMsg, strings[1], strings[2], strings[3]), strings[2].split(",")[0]);

                }
                break;
            case APICode.SEND_NET_ShortVoiceMsg:

                if (strings[2].indexOf(",") != -1)//多人发送
                {
                    String[] strCon = strings[2].split("@")[1].split(",");
                    for (String s : strCon) {
                        UDPNetModelTools.SendNteData(new ComposeData().sendShortVoicemsg(2, strings[1], strings[2], strings[3]),s.split(",")[0]);
                    }
                } else {
                    UDPNetModelTools.SendNteData(new ComposeData().sendShortVoicemsg(2, strings[1], strings[2], strings[3]), strings[2].split(",")[0]);

                }

//                UDPNetModelTools.SendNteData(new ComposeData().sendShortVoicemsg(2, strings[1], strings[2], strings[3]), strings[2].split(",")[0]);
                break;
            case APICode.SEND_NET_Encrypt_ShortTextMsg:
                UDPNetModelTools.SendNteData(new ComposeData().sendShortNetTextmsg(APICode.SEND_NET_Encrypt_ShortTextMsg, strings[1], strings[2], strings[3]), strings[2].split(",")[0]);
                break;
        }
        return null;
    }
}