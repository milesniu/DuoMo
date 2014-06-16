package com.miles.ccit.duomo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.miles.ccit.net.SocketClient;
import com.miles.ccit.service.HeartbeatService;
import com.miles.ccit.util.BaseActivity;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
//		BaseMapObject ob = new BaseMapObject();
//		ob.put("id", null);
//		ob.put("frequency", "112.3");
//		ob.put("creattime", "2222");
//		ob.InsertObj2DB(mContext, "specialway");
//		ob.InsertObj2DB(mContext, "specialway");
//		ob.InsertObj2DB(mContext, "specialway");
//		ob.InsertObj2DB(mContext, "specialway");
//		ob.InsertObj2DB(mContext, "specialway");
//		 List<Map<String, String>> list= GetData4DB.getObjectListData(mContext, "specialway");
//		
//		 Map<String, String> data = GetData4DB.getObjectByid(mContext, "specialway", "1");
//		 
//		 ob.InsertObj2DB(mContext, "specialway");
//		new test().execute();
		startService(new Intent(mContext, HeartbeatService.class));
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		findViewById(R.id.text).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new test().execute();
			}
		});
		
	}
	
	class test extends AsyncTask<Void, Void, String>
	{

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try
			{
			PrintWriter output = null;
			Socket tso = SocketClient.getInstance();
			
			OutputStream out = tso.getOutputStream();

			// 注意第二个参数据为true将会自动flush，否则需要需要手动操作out.flush()
			output = new PrintWriter(out, true);

			output.println("2@#####");
//			BufferedReader input = new BufferedReader(new InputStreamReader(tso.getInputStream()));
//			return  input.readLine();
			
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Toast.makeText(mContext, result, 0).show();
			super.onPostExecute(result);
		}
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
