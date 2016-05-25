package com.miles.ccit.duomo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.miles.ccit.net.APICode;
import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.IAcceptServerData;
import com.miles.ccit.net.UDPTools;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.O;
import com.miles.ccit.util.SendDataTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LoginActivity extends AbsBaseActivity implements IAcceptServerData {
    private EditText edit_Account;
    private EditText edit_Password;
    private EditText edit_ip;
    private EditText edit_rtpip;
    private MyBroadcastReciver broad = null;
    public static boolean isLogin = false;

    public final String spuname = "uname";
    public final String sppwd = "pwd";
    public final String spip = "ipaddr";
    public final String sprtpip = "rtpipaddr";


    DatagramSocket ds = null; // 连接对象
    DatagramPacket sendDp; // 发送数据包对象
    DatagramPacket receiveDp; // 接收数据包对象
    final int PORT = 6004; // 接收端口


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public boolean isDestroyed() {
        // TODO Auto-generated method stub
        if (broad != null) {
            this.unregisterReceiver(broad);
        }
        return super.isDestroyed();
    }


    private void initServerUDP() {
        // 建立连接，监听端口
        try {
            ds = new DatagramSocket(PORT);
            System.out.println("服务器端已启动：");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        O.LOCALIP = ip;

    }


    @Override
    public void initView() {
        // TODO Auto-generated method stub
        initBaseView("登录");
        Btn_Left.setOnClickListener(this);
        Btn_Right.setVisibility(View.INVISIBLE);
        // showprogressdialog();
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        edit_Account = (EditText) findViewById(R.id.edit_account);
        edit_Password = (EditText) findViewById(R.id.edit_pwd);
        edit_ip = (EditText) findViewById(R.id.edit_ip);
        edit_rtpip = (EditText) findViewById(R.id.edit_rtpip);
        edit_rtpip.setVisibility(View.GONE);
        edit_ip.setVisibility(View.VISIBLE);
        edit_Account.setText(sp.getString(spuname, ""));
        edit_Password.setText(sp.getString(sppwd, ""));
        edit_ip.setText(sp.getString(spip, ""));
        edit_rtpip.setText(sp.getString(sprtpip, ""));

        findViewById(R.id.bt_login).setOnClickListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(broad_login_Action);
        broad = new MyBroadcastReciver();
        this.registerReceiver(broad, intentFilter);

        initServerUDP();
        new Thread(new AcceptThread("findip", IAcceptServerData.FindIP)).start();

//        new Thread(new ReceiverUDPServer()).start();

    }


    private String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    private Handler MyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // hideProgressDlg();
            if (msg.obj != null) {
                O.Ipaddress = msg.obj.toString();
                // MainActivity.this.startActivity(new Intent(MainActivity.this,
                // IndexActivity.class));
                // MainActivity.this.finish();
                edit_ip.setText(O.Ipaddress);
                // MyLog.showToast(mContext,msg.toString());
            } else {
                MyLog.showToast(mContext, "未获取到网络地址，请检查连接");
            }
        }
    };

    public class AcceptThread implements Runnable {
        private String str;
        private int id;

        public AcceptThread(String str, int id) {
            this.str = str;
            this.id = id;
        }

        @Override
        public void run() {

            UDPTools.getServerData(new ComposeData().sendFindIp());
            try {
                // 初始化接收数据
                byte[] b = new byte[1024];
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

                acceptUdpData(clientIP.getHostAddress(), id);

                // 发送反馈
                String response = "OK";
                byte[] bData = response.getBytes();
                sendDp = new DatagramPacket(bData, bData.length, clientIP, clientPort);
                // 发送
                ds.send(sendDp);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                try {
                    // 关闭连接
                    ds.close();
                } catch (Exception e) {
                }
            }

//			acceptUdpData(temp, id);
            // }
        }
    }

    @Override
    public void acceptUdpData(String data, int id) {
        Message msg = new Message();
        switch (id) {
            case IAcceptServerData.FindIP:
                msg.what = IAcceptServerData.FindIP;
                break;
            default:
                break;
        }
        msg.obj = data;
        MyHandler.sendMessage(msg);

    }

    public class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            hideProgressDlg();
            String action = intent.getAction();

            if (action.equals(broad_login_Action)) {
                byte[] con = intent.getByteArrayExtra("data");

                if (con == null || con.length < 4) {
                    Intent it = new Intent();
                    it.putExtra("result", "false");
                    it.putExtra("data", con);
                    LoginActivity.this.setResult(Activity.RESULT_OK, it);
                    MyLog.showToast(mContext, "登陆超时");
                    isLogin = false;
                    LoginActivity.this.finish();
                    return;
                }
                if (con.length > 4 && con[5] == (byte) 0x01) {
                    checkContact(con);

                    Intent it = new Intent();
                    it.putExtra("result", "true");
                    it.putExtra("data", con);
                    LoginActivity.this.setResult(Activity.RESULT_OK, it);
                    MyLog.showToast(mContext, "登陆成功");
                    isLogin = true;
                    LoginActivity.this.finish();
                } else {
                    MyLog.showToast(mContext, "登陆失败，请检查用户名及密码");
                    return;
                }
            } else if (action.equals("")) {

            }
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        mContext.unregisterReceiver(broad);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.bt_login:
                String name = edit_Account.getText().toString();
                String pwd = edit_Password.getText().toString();
                String ip = edit_ip.getText().toString();
                O.Ipaddress = ip;
                O.RTPIpaddress = ip;//edit_rtpip.getText().toString();
                if (name.equals("")) {
                    MyLog.showToast(mContext, "用户账号不能为空。");
                    return;
                }
                if (pwd.equals("")) {
                    MyLog.showToast(mContext, "密码不能为空。");
                    return;
                }
                showprogressdialog();
                O.Account = name;
                O.Pwd = pwd;

                SharedPreferences.Editor editor = sp.edit();
                // 修改数据
                editor.putString(spuname, String.valueOf(name));
                editor.putString(sppwd, String.valueOf(pwd));
                editor.putString(spip, String.valueOf(ip));
                editor.putString(sprtpip, String.valueOf(O.RTPIpaddress));
                editor.commit();

                new SendDataTask().execute(APICode.SEND_Login + "", name, pwd);
                // timer = new Timer();
                // timer.schedule(ttask, 100, 5000);
                break;
        }
    }
}
