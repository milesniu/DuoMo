package com.miles.ccit.duomo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.adapter.VoicecodeAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.database.UserDatabase;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsBaseFragment;
import com.miles.ccit.util.AbsToCallActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.OverAllData;

public class VoicecodeFragment extends AbsBaseFragment
{
	private VoicecodeAdapter adapter;
	private List<BaseMapObject> voiceList = new Vector<BaseMapObject>();
	public Button Btn_Delete;
	public Button Btn_Canle;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			switch (msg.what)
			{
			case 0:
				break;
			case 1:
				adapter.notifyDataSetChanged();
				break;
			case 2:

				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_voicecode, null);
		initView(view);
		return view;
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		refreshList();
		super.onResume();
	}

	private void refreshList()
	{
		
		voiceList = GetData4DB.getObjList4LeftJoin(getActivity(), "voicecoderecord", "contact", "number");//getObjectListData(getActivity(), "voicecoderecord");

		if (voiceList == null || voiceList.size() < 1)
		{
			showEmpty();
			return;
		}
		hideEmpty();
		Collections.reverse(voiceList);
		adapter = new VoicecodeAdapter(getActivity(), voiceList);

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				AbsToCallActivity.CurrentType = AbsToCallActivity.TOCALLVOICE;
				AbsToCallActivity.insertVoiceRecord(getActivity(),voiceList.get(arg2).get("number").toString());
				
			}
		});

		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
			{
				// TODO Auto-generated method stub
				menu.setHeaderTitle("声码话");
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
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int ListItem = (int) info.position;
		switch (item.getItemId())
		{
		case 0:
			confirmDlg("删除记录", "voicecoderecord","id",voiceList.get(ListItem), voiceList, adapter);
//			
//			BaseMapObject selectItem = voiceList.get(ListItem);
//			long ret = BaseMapObject.DelObj4DB(getActivity(), "voicecoderecord", "id", selectItem.get("id").toString());
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
	public void initView(View view)
	{
		// TODO Auto-generated method stub
		initBaseView(view, "声码话");
		// Btn_Left.setText("返回");
		// Btn_Right.setText("拨打");
		Btn_Right.setBackgroundResource(R.drawable.creatcall);
		Btn_Left.setOnClickListener(this);
		Btn_Right.setOnClickListener(this);
		linear_Del = (LinearLayout) view.findViewById(R.id.linear_del);
		Btn_Delete = (Button) view.findViewById(R.id.bt_sure);
		Btn_Canle = (Button) view.findViewById(R.id.bt_canle);
		Btn_Delete.setOnClickListener(this);
		Btn_Canle.setOnClickListener(this);
		refreshList();
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.bt_left:
			getActivity().finish();
			break;
		case R.id.bt_right:
			if(LoginActivity.isLogin)
			{
				startActivity(new Intent(getActivity(), CreatVoicecodeActivity.class));
			}
			else
			{
				MyLog.showToast(getActivity(), "请登录后再执行该操作...");
			}
			break;
		case R.id.bt_sure:
			confirmDlg("删除记录", "voicecoderecord", "id",null, voiceList, adapter);
//			
//			Iterator<BaseMapObject> iter = voiceList.iterator();
//			List<String> Idlist = new Vector<String>();
//			while (iter.hasNext())
//			{
//				BaseMapObject s = iter.next();
//				if (s.get("exp2") != null && s.get("exp2").toString().equals("1"))
//				{
//					Idlist.add(s.get("id").toString());
//					iter.remove();
//				}
//			}
//
//			UserDatabase.DelListObj(getActivity(), "voicecoderecord", "id", Idlist);
//
//			for (BaseMapObject tmp : voiceList)
//			{
//				tmp.put("exp1", null);
//				tmp.put("exp2", null);
//			}
//			adapter.notifyDataSetChanged();
//			linear_Del.setVisibility(View.GONE);
//			break;
		case R.id.bt_canle:
			for (BaseMapObject tmp : voiceList)
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

}
