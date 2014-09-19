package com.miles.ccit.duomo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.miles.ccit.net.ComposeData;
import com.miles.ccit.net.IAcceptServerData;
import com.miles.ccit.net.SocketClient;
import com.miles.ccit.net.UDPTools;
import com.miles.ccit.service.HeartbeatService;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.FileUtils;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.OverAllData;

public class MainActivity extends AbsBaseActivity implements IAcceptServerData
{

	
//	Handler rhandler = new Handler()
//	{
//		public void handleMessage(Message message)
//		{
//			super.handleMessage(message);
////			MainActivity.this.startActivity(new Intent(MainActivity.this, IndexActivity.class));
////			MainActivity.this.finish();
////			new Thread(new AcceptThread("ql", IAcceptServerData.FindIP)).start();
//		};
//	};
	
	/** 文件目录的准备 */
	private void PrePareFile()
	{
		FileUtils fileutil = new FileUtils();
		//  主目录
		if (!fileutil.isFileExist(OverAllData.SDCardRoot))
		{
			fileutil.creatSDDir(OverAllData.SDCardRoot);
		}
	}
	
	
	 private Handler MyHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) 
	        {
	        	OverAllData.Ipaddress = msg.obj.toString();
	        	MainActivity.this.startActivity(new Intent(MainActivity.this, IndexActivity.class));
	        	MainActivity.this.finish();
//	            MyLog.showToast(mContext,msg.toString());
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
	            while (true) {
	                try {
	                    Thread.sleep(500);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	                String temp = UDPTools.getServerData(new ComposeData().sendFindIp());
	                acceptUdpData(temp, id);
	            }
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PrePareFile();
		startService(new Intent(mContext, HeartbeatService.class));
//		startActivity(new Intent(this, IndexActivity.class));
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				new Thread(new AcceptThread("findip", IAcceptServerData.FindIP)).start();
//				rhandler.sendEmptyMessageDelayed(1, 100);
			}
		}, 2000);

	}

	byte[] red = null;
	class test extends AsyncTask<Void, Void, String>
	{
		
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
				try
				{
					DataOutputStream out = new DataOutputStream(SocketClient.getInstance().getOutputStream());
					ComposeData df = new ComposeData();
					byte[] buf = df.sendHeartbeat();
					out.write(buf);
					out.flush();
					
					
					//等待服务器返回，并阻塞线程
					red = new byte[256];
					DataInputStream dis = new DataInputStream(SocketClient.getInstance().getInputStream());//服务器通过输入管道接收数据流  
					int a = dis.read(red);
					System.out.println(a);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return e.toString();
				}
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			// TODO Auto-generated method stub
			//连接断开，管道破裂
			Toast.makeText(mContext, red.length+"", Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		
	}

}
