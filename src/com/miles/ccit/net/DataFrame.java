package com.miles.ccit.net;

/**数据封装类
 * */
public class DataFrame 
{
	/** 包头： 2字节 */
	public byte head[] = new byte[]{(byte)0x55,(byte)0xaa};
	
	/** 命令码： 1字节 */
	public byte mDataType;
	
	/** 数据长度： 2 */
	public short mDataSize;
	
	/** 数据 */
	public byte[] mData = new byte[20480];
	
	public int getSize()
	{
		return mDataSize + 1;
	}
}