package com.miles.ccit.net;

public class APICode {
    /**
     * 手机端发送心跳
     */
    public static final byte SEND_HearBeat = (byte) 0x01;
    /**
     * 手机端收到心跳响应
     */
    public static final byte BACK_Hear_Beat = (byte) 0x02;

    /**
     * 手机端发送寻找IP的广播
     */
    public static final byte SEND_FindIP = (byte) 0x32;
    /**
     * 手机端收到广播返回
     */
    public static final byte BACK_FindIP = (byte) 0x32;
    /**
     * 登陆
     */
    public static final byte SEND_Login = (byte) 0x03;
    /**
     * 登陆响应
     */
    public static final byte BACK_Login = (byte) 0x04;
    /**
     * 注销
     */
    public static final byte SEND_Logout = (byte) 0x05;
    /**
     * 修改密码
     */
    public static final byte SEND_ChangePwd = (byte) 0x06;
    /**
     * 修改密码响应
     */
    public static final byte BACK_ChangePwd = (byte) 0x07;

    /**
     * 发送短消息
     */
    public static final byte SEND_ShortTextMsg = (byte) 0x08;
    /**
     * 发短消息响应
     */
    public static final byte BACK_ShortTextMsg = (byte) 0x09;
    /**
     * 接收到短消息
     */
    public static final byte RECV_ShortTextMsg = (byte) 0x0A;

    /**
     * 发送短语音
     */
    public static final byte SEND_ShortVoiceMsg = (byte) 0x0B;
    /**
     * 发短语音响应
     */
    public static final byte BACK_ShortVoiceMsg = (byte) 0x0C;
    /**
     * 接收到短语音
     */
    public static final byte RECV_ShortVoiceMsg = (byte) 0x0D;


    /**
     * 网络模式发送短消息
     */
    public static final byte SEND_NET_ShortTextMsg = (byte) 0xA0;
    /**
     * 网络模式发短消息响应
     */
    public static final byte BACK_NET_ShortTextMsg = (byte) 0xA1;
    /**
     * 网络模式接收到短消息
     */
    public static final byte RECV_NET_ShortTextMsg = (byte) 0xA2;

    /**
     * 网络模式发送短语音
     */
    public static final byte SEND_NET_ShortVoiceMsg = (byte) 0xA3;
    /**
     * 网络模式发短语音响应
     */
    public static final byte BACK_NET_ShortVoiceMsg = (byte) 0xA3;
    /**
     * 网络模式接收到短语音
     */
    public static final byte RECV_NET_ShortVoiceMsg = (byte) 0xA5;


    /**
     * 发起声码话
     */
    public static final byte SEND_VoiceCode = (byte) 0x0E;
    /**
     * 发起声码响应
     */
    public static final byte BACK_VoiceCode = (byte) 0x0F;
    /**
     * 接收到声码话请求
     */
    public static final byte RECV_VoiceCode = (byte) 0x10;
    /**
     * 发送接收声码话请求响应
     */
    public static final byte BACK_RECV_VoiceCode = (byte) 0x2C;
    /**
     * 声码话讲话或停止讲话
     */
    public static final byte SEND_TalkVoiceCode = (byte) 0x11;
    /**
     * 发送邮件
     */
    public static final byte SEND_Email = (byte) 0x12;
    /**
     * 发邮件响应
     */
    public static final byte BACK_Email = (byte) 0x13;
    /**
     * 接收到邮件
     */
    public static final byte RECV_Email = (byte) 0x14;
    /**
     * 发送代码指挥
     */
    public static final byte SEND_CodeDirec = (byte) 0x15;
    /**
     * 代码指挥响应
     */
    public static final byte BACK_CodeDirec = (byte) 0x16;
    /**
     * 收到代码指挥
     */
    public static final byte RECV_CodeDirec = (byte) 0x17;
    /**
     * 发起专向语音
     */
    public static final byte SEND_SpecialVoice = (byte) 0x18;
    /**
     * 专向语音响应
     */
    public static final byte BACK_SpecialVoice = (byte) 0x19;
    /**
     * 专向语音讲话或停止讲话
     */
    public static final byte SEND_TalkSpecialVoice = (byte) 0x1A;
    /**
     * 发送有线语音
     */
    public static final byte SEND_WiredVoice = (byte) 0x1B;
    /**
     * 响应有线语音
     */
    public static final byte BACK_WiredVoice = (byte) 0x1C;
    /**
     * 收到有线语音
     */
    public static final byte RECV_WiredVoice = (byte) 0x1D;
    /**
     * 响应接收的有线语音
     */
    public static final byte BACK_RECV_WiredVoice = (byte) 0x2D;
    /**
     * 发起有线文件
     */
    public static final byte SEND_WiredFile = (byte) 0x1E;
    /**
     * 响应发起有线文件
     */
    public static final byte BACK_WiredFile = (byte) 0x1F;
    /**
     * 正式发送文件
     */
    public static final byte SNED_FILE = (byte) 0x20;
    /**
     * 响应文件发送结果
     */
    public static final byte BACK_FILE = (byte) 0x2E;
    /**
     * 接收有线文件
     */
    public static final byte RECV_WiredFile = (byte) 0x21;
    /**
     * 响应接收到的有线文件
     */
    public static final byte BACK_RECV_WiredFile = (byte) 0x22;
    /**
     * 发送文件接收结果
     */
    public static final byte SEND_FileResult = (byte) 0x2F;
    /**
     * 发送文件发送进度
     */
    public static final byte SEND_FileProgress = (byte) 0x30;
    /**
     * 接收有线文件的发送进度
     */
    public static final byte RECV_FileProgress = (byte) 0x30;

    /**
     * 设置发起广播
     */
    public static final byte SEND_Broadcast = (byte) 0x23;
    /**
     * 广播设置响应结果
     */
    public static final byte BACK_Broadcast = (byte) 0x33;
    /**
     * 接收到广播
     */
    public static final byte RECV_BroadcastFile = (byte) 0x31;
    /**
     * 发送配置信息
     */
    public static final byte SEND_Config = (byte) 0x25;
    /**
     * 配置信息响应
     */
    public static final byte BACK_Config = (byte) 0x26;
    /**
     * 获取位置信息
     */
    public static final byte SEND_Location = (byte) 0x27;
    /**
     * 位置信息响应
     */
    public static final byte BACK_Location = (byte) 0x28;
    /**
     * 专网模式中断
     */
    public static final byte SEND_SpecialNetInteraput = (byte) 0x29;
    /**
     * 其他模式中断
     */
    public static final byte SEND_NormalInteraput = (byte) 0x2A;

    public static final byte RECV_NormalInteraput = (byte) 0x2A;
    /**
     * 模式返回
     */
    public static final byte SEND_BackModel = (byte) 0x2B;

    /**
     * UIM卡拔出
     */
    public static final byte RECV_UIMOUT = (byte) 0x34;

    /**
     * 调试信息
     */
    public static final byte RECV_DEBUGINFO = (byte) 0x35;

    /**
     * 上传无线联系人
     */
    public static final byte SEND_WirelessContact = (byte) 0x36;

    /**
     * 上传有线联系人
     */
    public static final byte SEND_WiredContact = (byte) 0x37;

    /**
     * 查询主机路由
     */
    public static final byte SEND_QueryHost = (byte) 0x38;

    /**
     * 返回或配置主机路由
     */
    public static final byte SEND_RECV_HostCfg = (byte) 0x39;
    /**
     * 查询信道优先级
     */
    public static final byte SEND_QueryChannel = (byte) 0x42;

    /**
     * 返回或配置信道优先级
     */
    public static final byte SEND_RECV_ChannelCfg = (byte) 0x43;


}
