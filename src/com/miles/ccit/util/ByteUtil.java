package com.miles.ccit.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ByteUtil {
    /**
     * 整型转换为2位字节数组
     *
     * @param intValue
     * @return
     */
    public static byte[] int2Byte(int leng, int intValue) {
        byte[] b = new byte[leng];
        for (int i = 0; i < leng; i++) {
            b[i] = (byte) (intValue >> 8 * ((leng - 1) - i) & 0xFF);
        }
        return b;
    }

    public static byte[] intToByteArray2(int i) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buf);
        out.writeInt(i);
        byte[] b = buf.toByteArray();
        out.close();
        buf.close();
        return b;
    }

    /**
     * 2位字节数组转换为整型
     *
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (1 - i));
        }
        return intValue;
    }

    /**
     * 2位字节数组转换为整型
     *
     * @param b
     * @return
     */
    public static int nbyte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (b.length - i));
        }
        return intValue;
    }


    public static int oneByte2oneInt(byte b) {
        return (b & 0xFF);
    }

    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }


    public static void writeAppendLog(String filePath, String info) {
        try {
            File file = new File(filePath);
            //第二个参数意义是说是否以append方式添加内容
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(info);
            bw.flush();
            System.out.println("写入成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeLog(String filePath, String info) {
        try {
            File file = new File(filePath);
            //第二个参数意义是说是否以append方式添加内容
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
            bw.write(info);
            bw.flush();
            System.out.println("写入成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readTxtFile(String fileName) {
        String result = null;
        try {
            File file = new File(fileName);
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            result = new String(b);
            System.out.println("读取成功：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

}
