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

import com.miles.ccit.adapter.MsgorMailSetAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.net.APICode;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.SendDataTask;

public class SpecialVoiceActivity extends AbsBaseActivity
{

	public Button Btn_Delete;
	public Button Btn_Canle;
	private List<BaseMapObject> voiceList = new Vector<BaseMapObject>();
	private MsgorMailSetAdapter adapter;
	private ListView listview;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special_voice);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.special_voice, menu);
		return true;
	}


	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		new SendDataTask().execute(APICode.SEND_BackModel+"");
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
		case R.id.bt_right:
			if(!LoginActivity.isLogin)
			{
				MyLog.showToast(mContext, "请登录后再执行该操作...");
				return;
			}
			startActivity(new Intent(mContext, CreatSpecialvoiceActivity.class));
			break;
		case R.id.bt_sure:
			confirmDlg("删除记录", "specialway", null, voiceList, adapter);
//			Iterator<BaseMapObject> iter = voiceList.iterator();  
//			List<String> Idlist = new Vector<String>();
//			while(iter.hasNext())
//			{  
//			    BaseMapObject s = iter.next();  
//			    if(s.get("exp2")!=null &&s.get("exp2").toString().equals("1"))
//			    {  
//			    	Idlist.add(s.get("id").toString());
//			        iter.remove();
//			    }  
//			}  
//		
//			UserDatabase.DelListObj(mContext,"specialway", "id", Idlist);
//			
//			for(BaseMapObject tmp:voiceList)
//			{
//				tmp.put("exp1", null);
//				tmp.put("exp2", null);
//			}
//			adapter.notifyDataSetChanged();
//			linear_Del.setVisibility(View.GONE);
			break;
		case R.id.bt_canle:
			for(BaseMapObject tmp:voiceList)
			{
				tmp.put("exp1", null);
				tmp.put("exp2", null);
			}
			linear_Del.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	private void refreshList()
	{
		voiceList = GetData4DB.getObjectListData(mContext, "specialway");
		
		if (voiceList == null || voiceList.size()<1)
		{
			showEmpty();
//			Toast.makeText(getActivity(), "暂无消息记录...", 0).show();
			return;
		}
		hideEmpty();
		Collections.reverse(voiceList);
		adapter = new MsgorMailSetAdapter(mContext, voiceList, "svoice");
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub

			}
		});

		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo)
			{
				// TODO Auto-generated method stub
				menu.setHeaderTitle("专向语音");
				menu.add(0, 0, 0, "删除该记录");
				menu.add(0, 1, 1, "批量删除记录");
				menu.add(0, 3, 3, "取消");
			}
		});
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int ListItem = (int) info.position;
		switch (item.getItemId())
		{
		case 0:
			confirmDlg("删除记录", "specialway", voiceList.get(ListItem), voiceList, adapter);
//			BaseMapObject selectItem = voiceList.get(ListItem);
//			long ret = BaseMapObject.DelObj4DB(mContext, "specialway", "id",
//					selectItem.get("id").toString());
//			if (ret != -1)
//			{
//				voiceList.remove(ListItem);
//				adapter.notifyDataSetChanged();
//			}
			break;
		case 1:
			for (BaseMapObject tmp : voiceList)
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
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("专向语音");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setBackgroundResource(R.drawable.creatcall);
		Btn_Right.setOnClickListener(this);
		linear_Del = (LinearLayout)findViewById(R.id.linear_del);
		Btn_Delete = (Button)findViewById(R.id.bt_sure);
		Btn_Canle = (Button)findViewById(R.id.bt_canle);
		listview = (ListView)findViewById(R.id.list_Content);
		Btn_Delete.setOnClickListener(this);
		Btn_Canle.setOnClickListener(this);
		refreshList();
	}

}
