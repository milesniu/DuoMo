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
                UDPNetModelTools.SendNteData(new ComposeData().sendShortNetTextmsg(APICode.SEND_NET_ShortTextMsg, strings[1], strings[2], strings[3]), strings[2].split(",")[0]);
                break;
            case APICode.SEND_NET_ShortVoiceMsg:
                UDPNetModelTools.SendNteData(new ComposeData().sendShortVoicemsg(2, strings[1], strings[2], strings[3]), strings[2].split(",")[0]);
                break;
            case APICode.SEND_NET_Encrypt_ShortTextMsg:
                UDPNetModelTools.SendNteData(new ComposeData().sendShortNetTextmsg(APICode.SEND_NET_Encrypt_ShortTextMsg, strings[1], strings[2], strings[3]), strings[2].split(",")[0]);
                break;
        }
        return null;
    }
}