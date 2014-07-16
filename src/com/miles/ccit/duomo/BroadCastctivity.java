package com.miles.ccit.duomo;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.miles.ccit.adapter.MsgorMailSetAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.net.APICode;
import com.miles.ccit.ui.ShortmsgListActivity;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsEmailCodeActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.SendDataTask;

public class BroadCastctivity extends AbsBaseActivity
{

	private EditText edit_Boundry;
	public static String number = null;
	private MyBroadcastReciver broad = null;
	private List<BaseMapObject>fileList = new Vector<BaseMapObject>();
	private ListView list_Content;
	private MsgorMailSetAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broad_castctivity);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(broad_broadcast_Action);
		broad = new MyBroadcastReciver();
		this.registerReceiver(broad, intentFilter);
		
	}

	private void refreshList()
	{
		fileList = GetData4DB.getObjectListData(mContext, "broadcastrecode");
		
		if (fileList == null || fileList.size()<1)
		{
			showEmpty();
//			Toast.makeText(getActivity(), "暂无消息记录...", 0).show();
			return;
		}
		hideEmpty();
		adapter = new MsgorMailSetAdapter(mContext, fileList,"broadcast");
		list_Content.setAdapter(adapter);
		list_Content.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
//				getActivity().startActivity(new Intent(getActivity(), ShortmsgListActivity.class).putExtra("item", msgList.get(arg2)));
				CreatEMailActivity.showFile(mContext, "a.txt", fileList.get(arg2).get("filepath").toString());
			}
		});
		list_Content.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
			{
				// TODO Auto-generated method stub
				menu.setHeaderTitle("广播文件");
				menu.add(0, 0, 0, "删除该文件");
				menu.add(0, 1, 1, "批量删除");
				menu.add(0, 3, 3, "取消");
			}
		});
	}
	

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int ListItem = (int) info.position;
		switch (item.getItemId())
		{
		case 0:
			confirmDlg("删除记录", "broadcastrecode", fileList.get(ListItem), fileList, adapter);
//			
//			BaseMapObject selectItem = shortList.get(ListItem);
//			long ret = BaseMapObject.DelObj4DB(mContext, "shortmsg", "id", selectItem.get("id").toString());
//			if (ret != -1)
//			{
//				shortList.remove(ListItem);
//				adapter.notifyDataSetChanged();
//			}
			break;
		case 1:
			for (BaseMapObject tmp : fileList)
			{
				tmp.put("exp1", "0");
			}
			adapter.notifyDataSetChanged();
			linear_Del.setVisibility(View.VISIBLE);
			break;
		case 3:
			break;
		}
		return super.onContextItemSelected(item);
	}
		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.broad_castctivity, menu);
		return true;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.bt_commit:
			
			number= edit_Boundry.getText().toString();
			if(number.equals(""))
			{
				MyLog.showToast(mContext, "请输入有效频率");
				return;
			}
			else
			{
				double num = Double.parseDouble(number);
				if(num<3||num>30)
				{
					MyLog.showToast(mContext, "请输入有效频率");
					return;
				}
				else
				{
					new SendDataTask().execute(APICode.SEND_Broadcast+"",OverAllData.Account,number);
				}
			}
			break;
		case R.id.bt_sure:
			confirmDlg("广播文件", "broadcastrecode", null, fileList, adapter);
			break;
		case R.id.bt_canle:
			for(BaseMapObject tmp:fileList)
			{
				tmp.put("exp1", null);
				tmp.put("exp2", null);
			}
			linear_Del.setVisibility(View.GONE);
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("广播模式");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		edit_Boundry = (EditText)findViewById(R.id.edit_boundry);
		findViewById(R.id.bt_commit).setOnClickListener(this);
		Btn_Delete = (Button)findViewById(R.id.bt_sure);
		Btn_Canle = (Button)findViewById(R.id.bt_canle);
		Btn_Delete.setOnClickListener(this);
		Btn_Canle.setOnClickListener(this);
		list_Content = (ListView)findViewById(R.id.list_Content);
		linear_Del = (LinearLayout)findViewById(R.id.linear_del);
		refreshList();
	}
	
	public class MyBroadcastReciver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			String action = intent.getAction();

			if (action.equals(broad_broadcast_Action))
			{
				if (intent.getSerializableExtra("data") == null)
				{
					return;
				}
				else
				{
					BaseMapObject broadmap = BaseMapObject.HashtoMyself((HashMap<String, Object>)intent.getSerializableExtra("data"));
					if(broadmap!=null)
					{
//						MyLog.showToast(mContext, "收到文件"+broadmap.get("filepath"));
						refreshList();
					}
				}
			}
		}

	}
	

}
