package com.miles.ccit.net;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsCreatActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.ByteUtil;
import com.miles.ccit.util.HexSwapString;
import com.miles.ccit.util.MyApplication;

/**
 * 数据编码类,改动数据封装信息，实现发送内容的结构完成
 */
public class ComposeData
{
	public String TAG = "DataFrameCompose";

	DataFrame data = new DataFrame();

	public int switchchoice = 0;
	private byte[] sendcfg = new byte[]
	{ (byte) 0x00, (byte) 0x00 };

	public ComposeData()
	{
		// TODO Auto-generated constructor stub
		BaseMapObject cfg = GetData4DB.getObjectByRowName(MyApplication.getAppContext(), "systeminto", "key", "sendcfg");
		if (cfg != null)
			sendcfg = HexSwapString.HexString2Bytes(cfg.get("value").toString());
	}

	/**
	 * 发送心跳包数据
	 * */
	public byte[] sendHeartbeat()
	{
		byte[] mData = new byte[]
		{};
		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_HearBeat }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
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
		byte[] mData = new byte[]
		{}; // 数据区
		byte[] head = data.head; // 数据头
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_FindIP }; // 命令码
		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送登陆数据
	 * 
	 * @param info
	 *            用户名，密码
	 * */
	public byte[] sendLogin(String... info)
	{
		int mLen = 0;
		for (String i : info)
		{
			mLen += i.getBytes().length;
		}

		byte[] mData = new byte[mLen + info.length];
		int currentpos = 0;
		for (String item : info)
		{
			byte[] len = ByteUtil.int2Byte(1, item.getBytes().length);
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(item.getBytes(), 0, mData, currentpos, item.getBytes().length);
			currentpos += item.length();
		}
		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_Login }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送修改密码
	 * 
	 * @param info
	 *            旧密码，新密码
	 * */
	public byte[] sendChangePwd(String... info)
	{
		int mLen = 0;
		for (String i : info)
		{
			mLen += i.getBytes().length;
		}

		byte[] mData = new byte[mLen + info.length];
		int currentpos = 0;
		for (String item : info)
		{
			byte[] len = ByteUtil.int2Byte(1, item.getBytes().length);
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(item.getBytes(), 0, mData, currentpos, item.getBytes().length);
			currentpos += item.length();
		}
		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_ChangePwd }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送短消息
	 * 
	 * @param info
	 *            源地址、目的地址、内容
	 * */
	public byte[] sendShortTextmsg(String... info)
	{

		int mLen = 0;
		for (String i : info)
		{
			mLen += i.getBytes().length;
		}
		mLen += 2;// 加2是加上优先级与是否回执两个字段

		byte[] mData = new byte[mLen + info.length + 1]; // 加1，因为目的终端地址变为两个字节

		int currentpos = 0;
		for (int i = 0; i < info.length; i++)
		{
			byte[] len = ByteUtil.int2Byte(i == 1 ? 2 : 1, info[i].getBytes().length);
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(info[i].getBytes(), 0, mData, currentpos, info[i].getBytes().length);
			currentpos += info[i].length();
		}
		// 拷贝优先级与是否回执，后期从数据库配置表中读取
		System.arraycopy(sendcfg, 0, mData, currentpos, 2);

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_ShortTextMsg }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送短语音
	 * 
	 * @param info
	 *            源地址、目的地址、语音地址
	 * */
	public byte[] sendShortVoicemsg(String... info)
	{

		int mLen = 0;
		for (int i = 0; i < 2; i++)
		{
			mLen += info[i].getBytes().length;
		}
		mLen += 2;// 加2是加上优先级与是否回执两个字段

		byte voicebyte[] = ByteUtil.getBytes(info[2]);
		mLen += voicebyte.length; // 添加语音长度

		byte[] mData = new byte[mLen + 5];// 源地址长度1字节，目的地址长度2字节，语音内容长度2字节

		int currentpos = 0;
		for (int i = 0; i < 2; i++)
		{
			byte[] len = ByteUtil.int2Byte(i == 1 ? 2 : 1, info[i].getBytes().length);
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(info[i].getBytes(), 0, mData, currentpos, info[i].getBytes().length);
			currentpos += info[i].length();
		}

		System.arraycopy(voicebyte, 0, mData, currentpos, voicebyte.length);
		currentpos += voicebyte.length;

		// 拷贝优先级与是否回执，后期从数据库配置表中读取
		System.arraycopy(sendcfg, 0, mData, currentpos, 2);

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_ShortVoiceMsg }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 声码话发起
	 * 
	 * @param info
	 *            源地址、目的地址
	 * */
	public byte[] sendStartVoicecode(String... info)
	{

		int mLen = 0;
		for (String i : info)
		{
			mLen += i.getBytes().length;
		}

		byte[] mData = new byte[mLen + 3];// 源地址1字节，目的地址长度2字节

		int currentpos = 0;
		for (int i = 0; i < 2; i++)
		{
			byte[] len = ByteUtil.int2Byte(1, info[i].getBytes().length);
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(info[i].getBytes(), 0, mData, currentpos, info[i].getBytes().length);
			currentpos += info[i].length();
		}

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_VoiceCode }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送邮件
	 * 
	 * @param info
	 *            源地址、抄送地址、目的地址、标题、内容、附件
	 * */
	public byte[] sendEMail(String... info)
	{

		int mLen = 0;
		for (int i = 0; i < 5; i++)
		{
			mLen += info[i].getBytes().length;
		}
		mLen += 2;// 加2是加上优先级与是否回执两个字段

		int fjnamelen = info[5] == null ? 0 : AbsCreatActivity.getFileName(info[5]).toString().getBytes().length;
		byte fjnamebyte[] = info[5] == null ? new byte[0] : ByteUtil.int2Byte(1, fjnamelen);
		mLen += fjnamebyte.length; // 添加附件名长度的位置长度
		mLen += fjnamelen;// 添加附件名的长度

		byte fjbyte[] = info[5] == null ? new byte[0] : ByteUtil.getBytes(info[5]);
		mLen += fjbyte.length; // 添加附件长度

		byte[] mData = new byte[mLen + (info[5] == null ? 8 : 11)];// 源地址长度1字节，目的地址长度1字节，抄送地址2字节，标题长度1，内容长度2，附件标示1，附件名长度1，附件长度2,

		int currentpos = 0;
		for (int i = 0; i < 5; i++)
		{
			int itemlength = 0;
			switch (i)
			{
			case 0:
			case 3:
				itemlength = 1;
				break;
			case 1:
			case 2:
			case 4:
				itemlength = 2;
				break;
			}
			byte[] len = ByteUtil.int2Byte(itemlength, info[i].getBytes().length);
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(info[i].getBytes(), 0, mData, currentpos, info[i].getBytes().length);
			currentpos += info[i].length();
		}

		// 单独处理附件组装
		if (info[5] == null)// 无附件
		{
			mData[currentpos++] = (byte) 0x00; // 附件标示无，以下与附件有关参数均无
		} else
		// 有附件
		{
			mData[currentpos++] = (byte) 0x01; // 附件标示有，继续组装名字与内容
			String fName = AbsCreatActivity.getFileName(info[5]).toString();
			// 组装文件名
			byte[] fnamelen = ByteUtil.int2Byte(1, fName.getBytes().length);
			System.arraycopy(fnamelen, 0, mData, currentpos, fnamelen.length);
			currentpos += fnamelen.length;
			System.arraycopy(fName.getBytes(), 0, mData, currentpos, fName.getBytes().length);
			currentpos += fName.getBytes().length;

			// 组装文件内容
			byte[] flen = ByteUtil.int2Byte(2, fjbyte.length);
			System.arraycopy(flen, 0, mData, currentpos, flen.length);
			currentpos += flen.length;
			System.arraycopy(fjbyte, 0, mData, currentpos, fjbyte.length);
			currentpos += fjbyte.length;
		}

		// 拷贝优先级与是否回执，后期从数据库配置表中读取
		System.arraycopy(sendcfg, 0, mData, currentpos, 2);

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_Email }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送有线文件
	 * 
	 * @param info
	 *            文件路径
	 * */
	public byte[] sendWiredFile(String... info)
	{

		int mLen = 0;

		int fjnamelen = AbsCreatActivity.getFileName(info[0]).toString().getBytes().length; // 文件名长度
		byte fjnamebyte[] = ByteUtil.int2Byte(1, fjnamelen); // 文件名长度内容
		mLen += fjnamebyte.length; // 添加附件名长度的位置长度
		mLen += fjnamelen; // 添加文件名长度

		byte fjbyte[] = ByteUtil.getBytes(info[0]); // 文件内容
		mLen += fjbyte.length; // 添加文件长度

		byte[] mData = new byte[mLen+3];// 文件名长度1字节，文件长度2字节

		int currentpos = 0;

		String fName = AbsCreatActivity.getFileName(info[0]).toString();
		// 组装文件名
		byte[] fnamelen = ByteUtil.int2Byte(1, fName.getBytes().length);
		System.arraycopy(fnamelen, 0, mData, currentpos, fnamelen.length);
		currentpos += fnamelen.length;
		System.arraycopy(fName.getBytes(), 0, mData, currentpos, fName.getBytes().length);
		currentpos += fName.getBytes().length;

		// 组装文件内容
		byte[] flen = ByteUtil.int2Byte(2, fjbyte.length);
		System.arraycopy(flen, 0, mData, currentpos, flen.length);
		currentpos += flen.length;
		System.arraycopy(fjbyte, 0, mData, currentpos, fjbyte.length);
		currentpos += fjbyte.length;

		// 拷贝优先级与是否回执，后期从数据库配置表中读取
//		System.arraycopy(sendcfg, 0, mData, currentpos, 2);

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_WiredFile }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送接听声码话响应
	 * 
	 * @param info
	 *            是否接听
	 * */
	public byte[] sendRecvVoicecode(String... info)
	{

		byte[] mData = new byte[]
		{ info[0].equals("1") ? ((byte) 0x01) : ((byte) 0x00) };// 是否同意1字节

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.BACK_RECV_VoiceCode }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送接听有线语音响应
	 * 
	 * @param info
	 *            是否接听
	 * */
	public byte[] sendRecvWiredVoice(String... info)
	{

		byte[] mData = new byte[]
		{ info[0].equals("1") ? ((byte) 0x01) : ((byte) 0x00) };// 是否同意1字节

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.BACK_RECV_WiredVoice }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 开始讲话或结束讲话
	 * 
	 * @param info
	 *            讲话、结束
	 * */
	public byte[] sendVoiceTalk(String... info)
	{

		byte[] mData = new byte[]
		{ info[0].equals("1") ? ((byte) 0x01) : ((byte) 0x00) };// 是否说话

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_TalkVoiceCode }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 专向语音开始讲话或结束讲话
	 * 
	 * @param info
	 *            讲话、结束
	 * */
	public byte[] sendSpecialVoiceTalk(String... info)
	{

		byte[] mData = new byte[]
		{ info[0].equals("1") ? ((byte) 0x01) : ((byte) 0x00) };// 是否说话

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_TalkSpecialVoice }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 普通模式的挂断
	 * 
	 * @param info
	 *            null
	 * */
	public byte[] sendNormalInteraput(String... info)
	{

		return new byte[]
		{ (byte) 0x55, (byte) 0xAA, (byte) 0x00, (byte) 0x01, (byte) 0x2A };
	}

	/**
	 * 发送广播设置
	 * 
	 * @param info
	 *            频率值
	 * */
	public byte[] sendBroadcast(String... info)
	{

		int mLen = info[0].getBytes().length;

		byte[] mData = new byte[mLen + 1];// 频率长度1字节

		byte[] len = ByteUtil.int2Byte(1, info[0].getBytes().length);
		System.arraycopy(len, 0, mData, 0, len.length);
		System.arraycopy(info[0].getBytes(), 0, mData, 1, info[0].getBytes().length);

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_Broadcast }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送广播设置
	 * 
	 * @param info
	 *            频率值
	 * */
	public byte[] sendSpecialVoice(String... info)
	{

		int mLen = info[0].getBytes().length;

		byte[] mData = new byte[mLen + 1];// 频率长度1字节

		byte[] len = ByteUtil.int2Byte(1, info[0].getBytes().length);
		System.arraycopy(len, 0, mData, 0, len.length);
		System.arraycopy(info[0].getBytes(), 0, mData, 1, info[0].getBytes().length);

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_SpecialVoice }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 发送有线模式语音呼叫
	 * 
	 * @param info
	 *            号码
	 * */
	public byte[] sendWiredVoice(String... info)
	{

		int mLen = info[0].getBytes().length;

		byte[] mData = new byte[mLen + 1];// 电话号码长度1字节

		byte[] len = ByteUtil.int2Byte(1, info[0].getBytes().length);
		System.arraycopy(len, 0, mData, 0, len.length);
		System.arraycopy(info[0].getBytes(), 0, mData, 1, info[0].getBytes().length);

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_WiredVoice }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}

	/**
	 * 模式返回
	 * 
	 * @param info
	 *            null
	 * */
	public byte[] sendBackmodel(String... info)
	{

		return new byte[]
		{ (byte) 0x55, (byte) 0xAA, (byte) 0x00, (byte) 0x01, (byte) 0x2B };

	}
	/**
		 * 请求接收文件
		 * 
		 * @param info
		 *            null
		 * */
		public byte[] sendRecvWiredFile(String... info)
		{
	
			return new byte[]
			{ (byte) 0x55, (byte) 0xAA, (byte) 0x00, (byte) 0x01, (byte) 0x22 };
	
		}
		
		/**
		 * 返回文件接受结果
		 * 
		 * @param info
		 *            null
		 * */
		public byte[] sendRecvresultWiredFile(String... info)
		{
	
			return new byte[]
			{ (byte) 0x55, (byte) 0xAA, (byte) 0x00, (byte) 0x01, (byte) 0x2F,(byte)0x01 };
	
		}
	/**
	 * 系统设置
	 * 
	 * @param info
	 *            发送延时，接受延时，电台类型，电台功率
	 * */
	public byte[] sendConfig(String... info)
	{

		return new byte[]
		{ (byte) 0x55, (byte) 0xAA, (byte) 0x00, (byte) 0x01, (byte) 0x25, Byte.parseByte(info[0]), Byte.parseByte(info[1]), Byte.parseByte(info[2]), Byte.parseByte(info[3]) };

	}
	
	/**
	 * 发送代码指挥
	 * 
	 * @param info
	 *            源地址、目的地址、代码内容
	 * */
	public byte[] sendCodeDirc(String... info)
	{

		int mLen = 0;
		for (String i : info)
		{
			mLen += i.getBytes().length;
		}
		mLen += 2;// 加2是加上优先级与是否回执两个字段

		byte[] mData = new byte[mLen + 5]; // 源地址1字节，目的地址2字节，内容两字节

		int currentpos = 0;
		for (int i = 0; i < info.length; i++)
		{
			byte[] len = ByteUtil.int2Byte(i == 0 ? 1 : 2, info[i].getBytes().length);
			System.arraycopy(len, 0, mData, currentpos, len.length);
			currentpos += len.length;
			System.arraycopy(info[i].getBytes(), 0, mData, currentpos, info[i].getBytes().length);
			currentpos += info[i].length();
		}
		// 拷贝优先级与是否回执，后期从数据库配置表中读取
		System.arraycopy(sendcfg, 0, mData, currentpos, 2);

		byte[] head = data.head;
		byte[] DataLenth = HexSwapString.short2Byte((short) (mData.length + 1));// new
																				// byte[]{(byte)(mData.length+1)};
																				// //
																				// 数据区长度
		byte[] frame = new byte[]
		{ APICode.SEND_CodeDirec }; // 命令码

		byte[] SendData = new byte[mData.length + 5]; // 最终发送的数组(4:包头两字节，长度两字节,命令码一个字节)
		int lenth = 0; // 记录当前拷贝到目的数组的下标
		System.arraycopy(head, 0, SendData, lenth, head.length);
		System.arraycopy(DataLenth, 0, SendData, lenth += head.length, DataLenth.length);
		System.arraycopy(frame, 0, SendData, lenth += DataLenth.length, frame.length);
		System.arraycopy(mData, 0, SendData, lenth += frame.length, mData.length);
		return SendData;
	}


}
