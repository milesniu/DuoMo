package com.miles.ccit.duomo;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.adapter.WiredModelAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsCreatActivity;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.OverAllData;
import com.miles.ccit.util.SendDataTask;

public class WiredModelActivity extends AbsBaseActivity
{

	private ListView list_Content;
	private List<BaseMapObject> wiredlist = new Vector<BaseMapObject>();
	private WiredModelAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wired_model);

		// BaseMapObject contact = new BaseMapObject();
		// contact.put("id",null);
		// contact.put("number","13");
		// contact.put("sendtype","0");
		// contact.put("status","1");
		// contact.put("filepath","1111");
		// contact.put("creattime",UnixTime.getStrCurrentUnixTime());
		// contact.InsertObj2DB(mContext, "wiredrecord");
		// contact.InsertObj2DB(mContext, "wiredrecord");
		// contact.InsertObj2DB(mContext, "wiredrecord");
		//

		// int a = list.size();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		new SendDataTask().execute(APICode.SEND_BackModel + "");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wired_model, menu);
		return true;
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
		case R.id.bt_right:
			if (LoginActivity.isLogin)
			{
				startActivity(new Intent(this, CreatWiredActivity.class));
			} else
			{
				MyLog.showToast(mContext, "请登录后再执行该操作...");
			}
			break;

		case R.id.bt_sure:
			confirmDlg("删除记录", "wiredrecord", null, wiredlist, adapter);
			//
			// Iterator<BaseMapObject> iter = wiredlist.iterator();
			// List<String> Idlist = new Vector<String>();
			// while(iter.hasNext())
			// {
			// BaseMapObject s = iter.next();
			// if(s.get("exp2")!=null &&s.get("exp2").toString().equals("1"))
			// {
			// Idlist.add(s.get("id").toString());
			// iter.remove();
			// }
			// }
			//
			// UserDatabase.DelListObj(mContext,"wiredrecord", "id", Idlist);
			//
			// for(BaseMapObject tmp:wiredlist)
			// {
			// tmp.put("exp1", null);
			// tmp.put("exp2", null);
			// }
			// adapter.notifyDataSetChanged();
			// linear_Del.setVisibility(View.GONE);
			break;
		case R.id.bt_canle:
			for (BaseMapObject tmp : wiredlist)
			{
				tmp.put("exp1", null);
				tmp.put("exp2", null);
			}
			linear_Del.setVisibility(View.GONE);
			break;
		}
	}

	private void refreshList()
	{
		Collections.reverse(wiredlist);
		if (wiredlist == null || wiredlist.size() < 1)
		{
			showEmpty();
			return;
		} else
		{
			hideEmpty();
			adapter = new WiredModelAdapter(mContext, wiredlist);
			list_Content.setAdapter(adapter);
		}
		list_Content.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
			{
				// TODO Auto-generated method stub
				menu.setHeaderTitle("优先模式");
				menu.add(0, 0, 0, "删除该联系人");
				menu.add(0, 1, 1, "批量删除");
				menu.add(0, 2, 2, "取消");
			}
		});
		list_Content.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				if (wiredlist.get(arg2).get("sendtype").toString().equals("1"))
				{
					String path = wiredlist.get(arg2).get("filepath").toString();
					AbsCreatActivity.showFile(mContext, AbsCreatActivity.getFileName(path), path);
				} else
				{
					AbsToCallActivity.CurrentType = AbsToCallActivity.TOCALLWIREDVOICE;
					AbsToCallActivity.insertWiredRecord(mContext, wiredlist.get(arg2).get("number").toString(), null);
				}

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
			confirmDlg("删除记录", "wiredrecord", wiredlist.get(ListItem), wiredlist, adapter);
			// BaseMapObject selectItem = wiredlist.get(ListItem);
			// long ret = BaseMapObject.DelObj4DB(mContext, "wiredrecord",
			// "id",selectItem.get("id").toString());
			// if(ret != -1)
			// {
			// wiredlist.remove(ListItem);
			// adapter.notifyDataSetChanged();
			// }
			break;
		case 1:
			for (BaseMapObject tmp : wiredlist)
			{
				tmp.put("exp1", "0");
			}
			adapter.notifyDataSetChanged();
			linear_Del.setVisibility(View.VISIBLE);
			break;
		case 2:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("有线模式");
		Btn_Right.setBackgroundResource(R.drawable.creatcall);
		Btn_Left.setOnClickListener(this);
		Btn_Right.setOnClickListener(this);
		Btn_Delete = (Button) findViewById(R.id.bt_sure);
		Btn_Canle = (Button) findViewById(R.id.bt_canle);
		Btn_Delete.setOnClickListener(this);
		Btn_Canle.setOnClickListener(this);
		list_Content = (ListView) findViewById(R.id.listView_content);
		linear_Del = (LinearLayout) findViewById(R.id.linear_del);
		wiredlist.clear();
		wiredlist = GetData4DB.getObjList4LeftJoin(mContext, "wiredrecord", "contact", "number");
		refreshList();
	}

}
