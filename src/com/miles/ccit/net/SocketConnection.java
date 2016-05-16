package com.miles.ccit.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.CodeDirectFragment;
import com.miles.ccit.duomo.EmailFragment;
import com.miles.ccit.duomo.LoginActivity;
import com.miles.ccit.duomo.ShortmsgListActivity;
import com.miles.ccit.main.IndexActivity;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyApplication;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.O;
import com.miles.ccit.util.UnixTime;

public class SocketConnection {
    private volatile Socket socket;
    public static boolean isHealthCheck = true;

    private boolean isNetworkConnect = false; // 网络是否已连接
    // private static String host;
    // private static int port;
    static InputStream inStream = null;
    static InputStream ninStream = null;
    static OutputStream outStream = null;

    private static SocketConnection socketConnection = null;
    private static Timer heartTimer = null;

    private static Thread receiveThread = null;
    private static Thread receiveUDPThread = null;
    private static Thread cacheCheckThread = null;
    private final ReentrantLock lock = new ReentrantLock();
    private String result;
    public static boolean isSocketRun = false;
    public static HashMap<String, HashMap<String, Object>> sendDataCallback = new HashMap<String, HashMap<String, Object>>();


    private SocketConnection() {
        // Properties conf = new Properties();
        try {
            init(O.Ipaddress, O.Port);

        } catch (IOException e) {
            // log.fatal("socket初始化异常!",e);
            // throw new RuntimeException("socket初始化异常,请检查配置参数");
            isSocketRun = false;
            e.printStackTrace();
        }
    }

    public void canleSocket() {
        if (socket != null) {
            try {
                socketConnection = null;
                socket.close();
                socket = null;
                LoginActivity.isLogin = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public Socket getsocket() {
        return socket;
    }

    /**
     * 单态模式
     */
    public static SocketConnection getInstance() {
        if (socketConnection == null) {
            synchronized (SocketConnection.class) {
                if (socketConnection == null) {
                    socketConnection = new SocketConnection();
                    return socketConnection;
                }
            }
        }
        return socketConnection;
    }

    private void init(String host, int port) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(host, port);
        socket = new Socket();
        synchronized (this) {
            MyLog.SystemOut("【准备与" + addr + "建立连接】");
            socket.connect(addr, O.networktimeout);
            MyLog.SystemOut("【与" + addr + "连接已建立】");
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
            socket.setTcpNoDelay(true); // 数据不作缓冲，立即发送
            socket.setSoLinger(true, 0); // socket关闭时，立即释放资源
            socket.setKeepAlive(true);
            socket.setTrafficClass(0x04 | 0x10); // 高可靠性和最小延迟传输
            isNetworkConnect = true;
            receiveThread = new Thread(new ReceiveWorker());
            receiveThread.start();

            //不进行超时检查
//			cacheCheckThread = new Thread(new CheckCallBack());
//			cacheCheckThread.start();

            isSocketRun = true;
        }
        // 开启心跳，注释此句，则关闭心跳
        launchHeartcheck();

        initServerUDP();
        new Thread(new ReceiverUDPServer()).start();

    }

    /**
     * 重连与目标IP建立重连
     */
    public void reConnectToCTCC() {
        Message msg = new Message();
        msg.arg1 = 1;
        MyApplication.handle.sendMessage(msg); // 连接断开，显示登录界面
        if (heartTimer != null)
            heartTimer.cancel();
        isNetworkConnect = false;
        if (receiveThread != null && receiveThread.isAlive())
            receiveThread.interrupt();
        canleSocket();

    }

    /**
     * 发送命令并接受响应
     *
     * @param requestMsg
     * @return
     * @throws SocketTimeoutException
     * @throws IOException
     */
    public String readReqMsg(byte[] requestMsg) throws IOException {
        if (requestMsg == null) {
            return null;
        }
        if (!isNetworkConnect) {
            synchronized (this) {
                try {
                    this.wait(1000 * 5); // 等待5秒，如果网络还没有恢复，抛出IO流异常
                    if (!isNetworkConnect) {
                        canleSocket();
                        throw new IOException("网络连接中断！");
                    }
                } catch (InterruptedException e) {

                }
            }
        }
        outStream = socket.getOutputStream();
        outStream.write(requestMsg);
        outStream.flush();

        return "ok";
    }

    /**
     * 心跳包检测
     */
    public void launchHeartcheck() {
        // if (SocketConnection.getInstance().getsocket() == null)
        // throw new IllegalStateException("socket is not 	established!");
        heartTimer = new Timer();
        heartTimer.schedule(new TimerTask() {
            @SuppressLint("SimpleDateFormat")
            public void run() {
                SimpleDateFormat dateformate = new SimpleDateFormat("yyyyMMddHHmmss");
                String msgDateTime = dateformate.format(new Date());
                MyLog.SystemOut("心跳检测包 -> IVR " + msgDateTime);
                int reconnCounter = 1;
                while (true) {

                    try {
                        if (isHealthCheck) {
                            if (sendDataCallback.get("heartbeat#1") == null) {
                                HashMap<String, Object> data = new HashMap<String, Object>();
                                data.put("creattime", UnixTime.getCurrentUnixTime());
                                sendDataCallback.put("heartbeat#1", data);
                            }
                            result = readReqMsg(new ComposeData().sendHeartbeat());
                        } else {
                            sendDataCallback.remove("heartbeat#1");
                        }
                    } catch (IOException e) {
                        MyLog.SystemOut("IO流异常" + e.toString());
                        reconnCounter++;
                    }
                    if (result != null) {
                        MyLog.SystemOut("心跳响应包 <- IVR " + result);
                        reconnCounter = 1;
                        break;
                    } else {
                        reconnCounter++;
                    }
                    if (reconnCounter > 3) {
                        // 重连次数已达三次，判定网络连接中断，重新建立连接。连接未被建立时不释放锁
                        reConnectToCTCC();
                        break;
                    }
                }
            }

        }, 1000, O.HeartbeatTime);
    }

    public void finalize() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class CheckCallBack implements Runnable {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                //遍历没有回复的map
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Iterator it = sendDataCallback.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry entry = (HashMap.Entry) it.next();
                    String key = entry.getKey() + "";
                    HashMap<String, Object> value = (HashMap<String, Object>) entry.getValue();
                    if (UnixTime.getCurrentUnixTime() - (Long.parseLong(value.get("creattime") + "")) < 5)//当前对象时间间隔小于5s
                    {
                        continue;
                    }
                    if (key.equals("heartbeat#1"))    //心跳
                    {
                        Message msg = new Message();
                        msg.arg1 = 0;
                        MyApplication.handle.sendMessage(msg); // 连接断开，显示登录界面
                        sendDataCallback.clear();
                        canleSocket();
                    } else {
                        String[] arraykey = key.split("#");
                        if (arraykey[0].equals("APICode.SEND_ShortTextMsg")) {
                            BaseMapObject senditem = GetData4DB.getObjectByid(MyApplication.getAppContext(), "shortmsg", arraykey[1]);
                            sendDataCallback.remove("APICode.SEND_ShortTextMsg#" + arraykey[1]);    //收到短消息返回，则删除缓存
                            senditem.put("sendtype", AbsBaseActivity.SENDERROR + "");
                            senditem.UpdateMyself(MyApplication.getAppContext(), "shortmsg");

                            if (senditem != null && ShortmsgListActivity.number != null && ShortmsgListActivity.number.equals(senditem.get("number").toString())) {
                                Intent intent = new Intent();
                                intent.setAction(AbsBaseActivity.broad_recvtextmsg_Action);
                                intent.putExtra("data", senditem);
                                MyApplication.getAppContext().sendBroadcast(intent);
                            }
                        } else if (arraykey[0].equals("APICode.SEND_Email")) {
                            BaseMapObject senditem = GetData4DB.getObjectByid(MyApplication.getAppContext(), "emailmsg", arraykey[1]);
                            sendDataCallback.remove("APICode.SEND_Email#" + arraykey[1]);
                            senditem.put("sendtype", AbsBaseActivity.SENDERROR + "");
                            senditem.UpdateMyself(MyApplication.getAppContext(), "emailmsg");

                            if (EmailFragment.isTop) {
                                Intent intent = new Intent();
                                intent.setAction(AbsBaseActivity.broad_backemailresult_Action);
                                MyApplication.getAppContext().sendBroadcast(intent);
                            }
                        }//"APICode.SEND_CodeDirec#"+id
                        else if (arraykey[0].equals("APICode.SEND_CodeDirec")) {
                            BaseMapObject senditem = GetData4DB.getObjectByid(MyApplication.getAppContext(), "codedirect", arraykey[1]);
                            sendDataCallback.remove("APICode.SEND_CodeDirec#" + arraykey[1]);
                            senditem.put("sendtype", AbsBaseActivity.SENDERROR + "");
                            senditem.UpdateMyself(MyApplication.getAppContext(), "codedirect");

                            if (CodeDirectFragment.isTop) {
                                Intent intent = new Intent();
                                intent.setAction(AbsBaseActivity.broad_recvcodedirc_Action);
                                MyApplication.getAppContext().sendBroadcast(intent);
                            }
                        }
                    }

                }


            }
        }

    }

    DatagramSocket ds = null; // 连接对象
    DatagramPacket sendDp; // 发送数据包对象
    DatagramPacket receiveDp; // 接收数据包对象
    final int PORT = 6080; // 接收端口


    private void initServerUDP() {
        // 建立连接，监听端口
        try {
            ds = new DatagramSocket(PORT);
            System.out.println("服务器端已启动：");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ReceiverUDPServer implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    // 初始化接收数据
                    byte[] b = new byte[20480];
                    receiveDp = new DatagramPacket(b, b.length);
                    // 接收
                    ds.receive(receiveDp);
                    // 读取反馈内容，并输出
                    InetAddress clientIP = receiveDp.getAddress();
                    int clientPort = receiveDp.getPort();
                    byte[] data = receiveDp.getData();
                    int len = receiveDp.getLength();
                    System.out.println("客户端IP：" + clientIP.getHostAddress());
                    System.out.println("客户端端口：" + clientPort);
                    System.out.println("客户端发送内容：" + new String(data, 0, len));

                    // 发送反馈
                    String response = "OK";
                    byte[] bData = response.getBytes();
                    sendDp = new DatagramPacket(bData, bData.length, clientIP, clientPort);
                    // 发送
                    ds.send(sendDp);

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

        }
    }

    // 消息接收线程
    private class ReceiveWorker implements Runnable {
        public void run() {
            byte[] heart = new byte[20480];
            while (!Thread.interrupted()) {
                try {
                    byte[] b = new byte[1024]; // 缓冲区20K
                    while (inStream.read(heart) == -1) {
                        // log.warn("读到流未尾，对方已关闭流!");
                        reConnectToCTCC();// 读到流未尾，对方已关闭流
                        return;
                    }

                    MyLog.SystemOut("接收到消息：" + heart);

                    AnalysisRecvData analyUtil = new AnalysisRecvData();

                    switch (heart[4]) {
                        case APICode.BACK_Login:
                            analyUtil.analyLogin(heart);
                            break;
                        case APICode.BACK_Hear_Beat:
                            sendDataCallback.remove("heartbeat#1");//收到心跳返回，清除心跳缓存
                            break;
                        case APICode.BACK_ShortTextMsg:
                        case APICode.BACK_ShortVoiceMsg:
                            analyUtil.analyBackTextMsg(heart);
                            break;
                        case APICode.BACK_Email:
                            analyUtil.analyBackEmail(heart);
                            break;
                        case APICode.RECV_ShortTextMsg:
                            analyUtil.analyTextMsg(heart);
                            IndexActivity.underspecise++;
                            break;
                        case APICode.RECV_ShortVoiceMsg:
                            analyUtil.analyVoiceMsg(heart);
                            IndexActivity.underspecise++;
                            break;
                        case APICode.RECV_Email:
                            analyUtil.analyEmail(heart);
                            IndexActivity.underspecise++;
                            break;
                        case APICode.BACK_VoiceCode:
                            analyUtil.analyBackVoiceCode(heart);
                            break;
                        case APICode.RECV_VoiceCode:
                            analyUtil.analyRecvVoicecode(heart);
                            IndexActivity.underspecise++;
                            break;
                        case APICode.RECV_BroadcastFile:
                            analyUtil.analyBroadcast(heart);
                            break;
                        case APICode.BACK_Broadcast:
                            analyUtil.analyBackBroadcast(heart);
                            break;
                        case APICode.BACK_SpecialVoice:
                            analyUtil.analyBackSpecialVoice(heart);
                            break;
                        case APICode.BACK_ChangePwd:
                            analyUtil.analyChangePwd(heart);
                            break;
                        case APICode.BACK_WiredVoice:
                            analyUtil.analyBackWiredVoice(heart);
                            break;
                        case APICode.RECV_WiredVoice:
                            analyUtil.analyRecvWiredVoice(heart);
                            break;
                        case APICode.RECV_NormalInteraput:
                            analyUtil.analyInteraput(heart);
                            break;
                        case APICode.SEND_FileProgress:
                            analyUtil.analyProgress(heart);
                            break;
                        case APICode.BACK_FILE:
                        case APICode.SEND_FileResult:
                            analyUtil.analyFileBackresult(heart);
                            break;
                        case APICode.RECV_WiredFile:
                            analyUtil.analyrecvWiredFile(heart);
                            break;
                        case APICode.RECV_UIMOUT:
                            Message msg = new Message();
                            msg.arg1 = 0;
                            MyApplication.handle.sendMessage(msg); // 连接断开，显示登录界面
                            canleSocket();
                            break;
                        case APICode.BACK_CodeDirec:
                            analyUtil.analyBackCodedirc(heart);
                            break;
                        case APICode.RECV_CodeDirec:
                            analyUtil.analyRecvCodedirc(heart);
                            IndexActivity.underspecise++;
                            break;
                        case APICode.BACK_Location:
                            analyUtil.analyRecvLocation(heart);
                            break;
                        case APICode.RECV_DEBUGINFO:
                            analyUtil.analyDebugInfo(heart);
                            break;
                        case APICode.SEND_RECV_HostCfg:
                            analyUtil.analyHostconfig(heart);
                            break;
                        case APICode.SEND_RECV_ChannelCfg:
                            analyUtil.analychannelCfg(heart);
                            break;
                    }

                    try {
                        lock.lock();
                    } finally {
                        lock.unlock();
                    }
                } catch (SocketException e) {
                    MyLog.SystemOut("服务端关闭socket" + e.toString());
                    reConnectToCTCC();
                } catch (IOException e) {
                    MyLog.SystemOut("接收线程读取响应数据时发生IO流异常" + e.toString());
                }

            }
        }

    }

}
