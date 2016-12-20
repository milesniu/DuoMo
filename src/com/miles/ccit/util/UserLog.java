package com.miles.ccit.util;

/**
 * Created by niujason on 16/9/21.
 */

public class UserLog {

    public static final int MAX_LINE = 4;

    public static void i(String type, String... log) {
        String l = "";
        int MaxLine = 4;
        for (int i = 1; i < log.length; i++) {
            l += log[i] + ",";
        }


        String file = ByteUtil.readTxtFile(O.SDCardRoot + "/user_log.txt");


        if (l.length() > 1) {
            String[] orgLog = file.split("\r\n");
            int sIndex = 0;
            if (orgLog.length > MAX_LINE) {
                sIndex = orgLog.length - MAX_LINE;
            }
            String wStr = UnixTime.getSimpleTime() + "," + type + "," + l.substring(0, l.length() - 1);
            String newFile = "";
            for (int i = sIndex; i < orgLog.length; i++) {
                newFile += orgLog[i] + "\r\n";
            }
            newFile += wStr;

            ByteUtil.writeLog(O.SDCardRoot + "/user_log.txt", newFile);
        }

    }

}
