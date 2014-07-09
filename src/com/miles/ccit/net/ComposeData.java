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
			mLen+=i.getBytes().length;
		}
		
		byte[] mData = new byte[mLen+info.length];
		int currentpos = 0;
		for(String item:info)
		{
			byte[] len = ByteUtil.int2Byte(1,item.getBytes().length);
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
	
	/**
	 * 发送短消息
	 * @param info 源地址、目的地址、内容
	 * */
	public byte[] sendShortTextmsg(String... info)
	{
		
		int mLen = 0;
		for(String i:info)
		{
			mLen+=i.getBytes().length;
		}
		mLen +=2;//加2是加上优先级与是否回执两个字段
		
		byte[] mData = new byte[mLen+info.length];
		
		int currentpos = 0;
		for(String item:info)
		{
			byte[] len = ByteUtil.int2Byte(1,item.getBytes().length);
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(item.getBytes(), 0, mData, currentpos, item.getBytes().length);
			currentpos += item.length();
		}
		//拷贝优先级与是否回执，后期从数据库配置表中读取
		System.arraycopy(new byte[]{(byte)0x00,(byte)0x01}, 0, mData, currentpos, 2);
		
		
		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short)(mData.length+1));//new byte[]{(byte)(mData.length+1)}; // 数据区长度
		byte[] frame = new byte[]{(byte)0x08}; // 命令码
	
		byte[] SendData = new byte[mData.length+5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}
	
	
	/**
	 * 发送短语音
	 * @param info 源地址、目的地址、语音地址
	 * */
	public byte[] sendShortVoicemsg(String... info)
	{
		
		int mLen = 0;
		for(int i=0;i<2;i++)
		{
			mLen+=info[i].getBytes().length;
		}
		mLen +=2;//加2是加上优先级与是否回执两个字段
		
		byte voicebyte[] = ByteUtil.getBytes(info[2]);
		mLen+=voicebyte.length;	//添加语音长度
		
		
		byte[] mData = new byte[mLen+4];//4代表长度位置的长度，语音两个字节，其他一个字节
		
		int currentpos = 0;
		for(int i=0;i<2;i++)
		{
			byte[] len = ByteUtil.int2Byte(1,info[i].getBytes().length);
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(info[i].getBytes(), 0, mData, currentpos, info[i].getBytes().length);
			currentpos += info[i].length();
		}
		
		byte[] vlen = ByteUtil.int2Byte(2, voicebyte.length);
		System.arraycopy(voicebyte, 0, mData, currentpos, voicebyte.length);
		currentpos += voicebyte.length;
		
		
		
		
		
		//拷贝优先级与是否回执，后期从数据库配置表中读取
		System.arraycopy(new byte[]{(byte)0x00,(byte)0x01}, 0, mData, currentpos, 2);
		
		
		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short)(mData.length+1));//new byte[]{(byte)(mData.length+1)}; // 数据区长度
		byte[] frame = new byte[]{(byte)0x0B}; // 命令码
	
		byte[] SendData = new byte[mData.length+5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}
	
	
	
}
