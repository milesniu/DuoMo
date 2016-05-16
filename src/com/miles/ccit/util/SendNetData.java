package com.miles.ccit.util;

import android.os.AsyncTask;

import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.UDPNetModelTools;

/**
 * Created by niujason on 16/5/16.
 */
public class SendNetData extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {
        UDPNetModelTools.SendNteData(new ComposeData().sendShortTextmsg(2,strings[1]), strings[1].split("#")[1]);
        return null;
    }
}