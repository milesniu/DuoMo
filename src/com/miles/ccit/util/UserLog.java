package com.miles.ccit.util;

/**
 * Created by niujason on 16/9/21.
 */

public class UserLog {

    public static void i(String type, String... log) {
        String l = "";
        for (String s : log) {
            l += s + ",";
        }
        ByteUtil.writeLog(O.SDCardRoot + "/user_log.txt", UnixTime.getSimpleTime() + "," + type + "," + l.substring(0, l.length() - 1) + "\r\n");

    }

}
