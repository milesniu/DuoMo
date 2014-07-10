package com.miles.ccit.util;

public class HexSwapString
{
	/**将byte数组转成16进制数的字符串*/
	public static String encode(byte[] bytes)
	{
		String hexString="0123456789ABCDEF";
		// 根据默认编码获取字节数组
		//byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		int DataLength = bytes.length;
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < DataLength; i++)
		{
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}
	
	private static byte uniteBytes(byte src0, byte src1)
	{
		byte _b0 = Byte.decode("0x" + new String(new byte[]{ src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[]{ src1 })).byteValue();
		byte ret = (byte) (_b0 | _b1);
		return ret;
	}
	/**将16进制数的字符串转成byte数组*/
	public static byte[] HexString2Bytes(String src)
	{
		int DataLenth = src.length()/2;
		byte[] ret = new byte[DataLenth];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < DataLenth; ++i)
		{
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}
	
	public static byte[] short2Byte(short data)
	{
		byte[] b = new byte[2];  
        b[0] = (byte) (data >> 8);  
        b[1] = (byte) (data);  
        return b;  
	}
	
}