package com.miles.ccit.net;

import com.miles.ccit.util.ByteUtil;
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
	
	/**
	 * 发送主机IP查询命令
	 * */
	public byte[] sendFindIp()
	{
		byte[] mData = new byte[]{};	//数据区
		byte[] head = data.head;		//数据头
		byte[] DataLenth = HexSwapString.short2Byte((short)(mData.length+1));// 数据区长度
		byte[] frame = new byte[]{(byte)0x32}; // 命令码
		byte[] SendData = new byte[mData.length+5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}
	
	/**
	 * 发送登陆数据
	 * @param info 用户名，密码
	 * */
	public byte[] sendLogin(String... info)
	{
		int mLen = 0;
		for(String i:info)
		{
			mLen+=i.length();
		}
		
		byte[] mData = new byte[mLen+info.length];
		int currentpos = 0;
		for(String item:info)
		{
			byte[] len = ByteUtil.int2Byte(1,item.length());
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(item.getBytes(), 0, mData, currentpos, item.getBytes().length);
			currentpos += item.length();
		}
		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short)(mData.length+1));//new byte[]{(byte)(mData.length+1)}; // 数据区长度
		byte[] frame = new byte[]{(byte)0x03}; // 命令码
	
		byte[] SendData = new byte[mData.length+5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}
	
	public byte[] sendTest()
	{
		byte[] mData = new byte[]{};
		byte[] head =  new byte[]{(byte)0xAB,(byte)0xaa};
		byte[] DataLenth = HexSwapString.short2Byte((short)(mData.length+1));//new byte[]{(byte)(mData.length+1)}; // 数据区长度
		byte[] frame = new byte[]{(byte)0xa1}; // 命令码
	
		byte[] SendData = new byte[mData.length+5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}
	
	
}
