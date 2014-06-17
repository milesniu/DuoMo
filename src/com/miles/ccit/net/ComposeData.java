package com.miles.ccit.net;

import com.miles.ccit.util.HexSwapString;


/**
 * 数据编码类,改动数据封装信息，实现发送内容的结构完成
 */
public class ComposeData
{
	public String TAG = "DataFrameCompose";

	DataFrame data = new DataFrame();

	public int switchchoice = 0;

	/**
	 * 发送心跳包数据
	 * */
	public byte[] sendHeartbeat()
	{
		byte[] mData = new byte[]{};
		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short)(mData.length+1));//new byte[]{(byte)(mData.length+1)}; // 数据区长度
		byte[] frame = new byte[]{(byte)0x01}; // 命令码
	
		byte[] SendData = new byte[mData.length+5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}
	
	
}
