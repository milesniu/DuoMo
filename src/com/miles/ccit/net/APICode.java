package com.miles.ccit.net;

public class APICode
{
	public static final byte SEND_HearBeat = (byte)0x01;
	public static final byte BACK_Hear_Beat = (byte)0x02;
	
	public static final byte SEND_FindIP = (byte)0x32;
	public static final byte BACK_FindIP = (byte)0x32;
	
	public static final byte SEND_Login = (byte)0x03;
	public static final byte BACK_Login = (byte)0x04;
	
	public static final byte SEND_Logout = (byte)0x05;
	
	public static final byte SEND_ChangePwd = (byte)0x06;
	public static final byte BACK_ChangePwd = (byte)0x07;
	
	public static final byte SEND_ShortTextMsg = (byte)0x08;
	public static final byte BACK_ShortTextMsg = (byte)0x09;
	
	public static final byte RECV_ShortTextMsg = (byte)0x0A;
	
	public static final byte SEND_ShortVoiceMsg = (byte)0x0B;
	public static final byte BACK_ShortVoiceMsg = (byte)0x0C;
	
	public static final byte RECV_ShortVoiceMsg = (byte)0x0D;
	
	public static final byte SEND_VoiceCode = (byte)0x0E;
	public static final byte BACK_VoiceCode = (byte)0x0F;
	
	public static final byte RECV_VoiceCode = (byte)0x10;
	public static final byte BACK_RECV_VoiceCode = (byte)0x2C;
	
	public static final byte SEND_TalkVoiceCode = (byte)0x11;
	
	public static final byte SEND_Email = (byte)0x12;
	public static final byte BACK_Email = (byte)0x13;
	
	public static final byte RECV_Email = (byte)0x14;
	
	public static final byte SEND_CodeDirec = (byte)0x15;
	public static final byte BACK_CodeDirec = (byte)0x16;
	
	public static final byte RECV_CodeDirec = (byte)0x17;
	
	public static final byte SEND_SpecialVoice = (byte)0x18;
	public static final byte BACK_SpecialVoice = (byte)0x19;
	
	public static final byte RECV_TalkSpecialVoice = (byte)0x1A;
	
	public static final byte SEND_WiredVoice = (byte)0x1B;
	public static final byte BACK_WiredVoice = (byte)0x1C;
	
	public static final byte RECV_WiredVoice = (byte)0x1D;
	public static final byte BACK_RECV_WiredVoice = (byte)0x2D;
	
	public static final byte SEND_WiredFile = (byte)0x1E;
	public static final byte BACK_WiredFile = (byte)0x1F;
	
	public static final byte SNED_FILE = (byte)0x20;
	public static final byte BACK_FILE = (byte)0x2E;
	
	public static final byte RECV_WiredFile = (byte)0x21;
	public static final byte BACK_RECV_WiredFile = (byte)0x22;
	
	public static final byte SEND_FileResult = (byte)0x2F;
	
	public static final byte SEND_FileProgress = (byte)0x30;
	public static final byte RECV_FileProgress = (byte)0x30;
	

	public static final byte SEND_Broadcast = (byte)0x23;

	public static final byte RECV_BroadcastFile = (byte)0x31;
	
	public static final byte SEND_Config = (byte)0x25;
	public static final byte BACK_Config = (byte)0x26;
	
	public static final byte SEND_Location = (byte)0x27;
	public static final byte BACK_Location = (byte)0x28;
	
	public static final byte SEND_SpecialNetInteraput = (byte)0x29;
	public static final byte SEND_NormalInteraput = (byte)0x2A;
	
	public static final byte SEND_BackModel = (byte)0x2B;
	
}
