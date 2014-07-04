package com.miles.ccit.util;

public class ByteUtil
{
	/**
     * 整型转换为2位字节数组
     * @param intValue
     * @return
     */
    public static byte[] int2Byte(int leng,int intValue) {
        byte[] b = new byte[leng];
        for (int i = 0; i < leng; i++) {
            b[i] = (byte) (intValue >> 8 * ((leng-1) - i) & 0xFF);
            //System.out.print(Integer.toBinaryString(b[i])+" ");
            //System.out.print((b[i] & 0xFF) + " ");
        }
        return b;
    }

    /**
     * 4位字节数组转换为整型
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }
}
