package com.miles.ccit.duomo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.miles.ccit.net.APICode;
import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.IAcceptServerData;
import com.miles.ccit.net.UDPTools;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.FileUtils;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.SendDataTask;

public class LoginActivity extends AbsBaseActivity implements IAcceptServerData
{
	private EditText edit_Account;
	private EditText edit_Password;
	private EditText edit_ip;
	private EditText edit_rtpip;
	private MyBroadcastReciver broad = null;
	public static boolean isLogin = false;
	public static SharedPreferences sp;
	public final String spuname = "uname";
	public final String sppwd = "pwd";
	public final String spip = "ipaddr";
	public final String sprtpip = "rtpipaddr";

	// private Timer timer = null;
	// private TimerTask ttask = null;
	// private int deltime = 5;
	// Handler hander = new Handler(){
	//
	// @Override
	// public void handleMessage(Message msg)
	// {
	// // TODO Auto-generated method stub
	// Intent it = new Intent();
	// it.putExtra("result", "false");
	// it.putExtra("data", "");
	// LoginActivity.this.setResult(Activity.RESULT_OK, it);
	// MyLog.showToast(mContext, "登陆超时...");
	// LoginActivity.this.finish();
	// super.handleMessage(msg);
	// }
	//
	// };

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// ttask = new TimerTask()
		// {
		//
		// @Override
		// public void run()
		// {
		// // TODO Auto-generated method stub
		// while((deltime--)<=0)
		// {
		// hander.sendMessage(new Message())
		// }
		// }
		// };
	}

	@Override
	public boolean isDestroyed()
	{
		// TODO Auto-generated method stub
		if (broad != null)
		{
			this.unregisterReceiver(broad);
		}
		return super.isDestroyed();
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("登录");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		// showprogressdialog();
		new Thread(new AcceptThread("findip", IAcceptServerData.FindIP)).start();
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		sp = getPreferences(MODE_PRIVATE);

		edit_Account = (EditText) findViewById(R.id.edit_account);
		edit_Password = (EditText) findViewById(R.id.edit_pwd);
		edit_ip = (EditText) findViewById(R.id.edit_ip);
		edit_rtpip = (EditText) findViewById(R.id.edit_rtpip);
		edit_Account.setText(sp.getString(spuname, ""));
		edit_Password.setText(sp.getString(sppwd, ""));
		edit_ip.setText(sp.getString(spip, ""));
		edit_rtpip.setText(sp.getString(sprtpip, ""));

		findViewById(R.id.bt_login).setOnClickListener(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(broad_login_Action);
		broad = new MyBroadcastReciver();
		this.registerReceiver(broad, intentFilter);
	}

	private Handler MyHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// hideProgressDlg();
			if (msg.obj != null)
			{
				OverAllData.Ipaddress = msg.obj.toString();
				// MainActivity.this.startActivity(new Intent(MainActivity.this,
				// IndexActivity.class));
				// MainActivity.this.finish();
				edit_ip.setText(OverAllData.Ipaddress);
				// MyLog.showToast(mContext,msg.toString());
			} else
			{
				Toast.makeText(mContext, "未获取到网络地址，请检查连接", 0).show();
			}
		}
	};

	public class AcceptThread implements Runnable
	{
		private String str;
		private int id;

		public AcceptThread(String str, int id)
		{
			this.str = str;
			this.id = id;
		}

		@Override
		public void run()
		{
			// while (true)
			// {
			// try
			// {
			// Thread.sleep(500);
			// } catch (InterruptedException e)
			// {
			// e.printStackTrace();
			// }
			String temp = UDPTools.getServerData(new ComposeData().sendFindIp());
			acceptUdpData(temp, id);
			// }
		}
	}

	@Override
	public void acceptUdpData(String data, int id)
	{
		Message msg = new Message();
		switch (id)
		{
		case IAcceptServerData.FindIP:
			msg.what = IAcceptServerData.FindIP;
			break;
		default:
			break;
		}
		msg.obj = data;
		MyHandler.sendMessage(msg);

	}

	public class MyBroadcastReciver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			hideProgressDlg();
			String action = intent.getAction();

			if (action.equals(broad_login_Action))
			{
				byte[] con = intent.getByteArrayExtra("data");

				if (con == null || con.length < 4)
				{
					Intent it = new Intent();
					it.putExtra("result", "false");
					it.putExtra("data", con);
					LoginActivity.this.setResult(Activity.RESULT_OK, it);
					MyLog.showToast(mContext, "登陆超时");
					isLogin = false;
					LoginActivity.this.finish();
					return;
				}
				if (con.length > 4 && con[5] == (byte) 0x01)
				{
					checkContact(con);

					Intent it = new Intent();
					it.putExtra("result", "true");
					it.putExtra("data", con);
					LoginActivity.this.setResult(Activity.RESULT_OK, it);
					MyLog.showToast(mContext, "登陆成功");
					isLogin = true;
					LoginActivity.this.finish();
				} else
				{
					MyLog.showToast(mContext, "登陆失败，请检查用户名及密码");
					return;
				}
			} else if (action.equals(""))
			{

			}
		}

	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(broad);
		super.onDestroy();
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.bt_login:
			String name = edit_Account.getText().toString();
			String pwd = edit_Password.getText().toString();
			String ip = edit_ip.getText().toString();
			OverAllData.Ipaddress = ip;
			OverAllData.RTPIpaddress = edit_rtpip.getText().toString();
			if (name.equals(""))
			{
				MyLog.showToast(mContext, "用户账号不能为空...");
				return;
			}
			if (pwd.equals(""))
			{
				MyLog.showToast(mContext, "密码不能为空...");
				return;
			}
			showprogressdialog();
			OverAllData.Account = name;
			OverAllData.Pwd = pwd;

			SharedPreferences.Editor editor = sp.edit();
			// 修改数据
			editor.putString(spuname, String.valueOf(name));
			editor.putString(sppwd, String.valueOf(pwd));
			editor.putString(spip, String.valueOf(ip));
			editor.putString(sprtpip, String.valueOf(OverAllData.RTPIpaddress));
			editor.commit();

			new SendDataTask().execute(APICode.SEND_Login + "", name, pwd);
			// timer = new Timer();
			// timer.schedule(ttask, 100, 5000);
			break;
		}
	}
}
