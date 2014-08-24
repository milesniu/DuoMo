package com.miles.ccit.duomo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.adapter.MsgorMailSetAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsBaseFragment;
import com.miles.ccit.util.BaseMapObject;

public class CodeDirectFragment extends AbsBaseFragment
{

	private ListView listview;
	private MsgorMailSetAdapter adapter;
	List<BaseMapObject> emailList = new Vector<BaseMapObject>();
	List<BaseMapObject> sendemail = new Vector<BaseMapObject>();
	List<BaseMapObject> recvemail = new Vector<BaseMapObject>();
	List<BaseMapObject> currentlist = null;
	public Button Btn_Delete;
	public Button Btn_Canle;
	private boolean issend = false;
	private boolean isrefresh = true;

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
		View view = inflater.inflate(R.layout.fragment_codedirect, null);

		listview = (ListView) view.findViewById(R.id.listView_content);
		initView(view);
		return view;
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		if(!isrefresh)
			return;
		issend = false;
		isrefresh = true;
		emailList.clear();
		recvemail.clear();
		sendemail.clear();
		emailList = GetData4DB.getObjList4LeftJoin(getActivity(), "codedirect", "contact", "number");

		if (emailList == null)
		{
			Toast.makeText(getActivity(), "网络连接异常，请检查后重试...", 0).show();
			return;
		} else
		{
			for (BaseMapObject item : emailList)
			{
				if (item.get("sendtype").toString().equals(AbsBaseActivity.RECVFROM + ""))// 收件
				{
					recvemail.add(item);
				} else
				{
					sendemail.add(item);
				}
			}
		}
		if (issend)
		{
			currentlist = sendemail;
		} else
		{
			currentlist = recvemail;
		}

		refreshList(currentlist);
		
		
	}

	private void refreshList(final List<BaseMapObject> list)
	{
		Collections.reverse(list);

		adapter = new MsgorMailSetAdapter(getActivity(), list, "codedir");

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
//				issend = true;
				isrefresh = false;
				getActivity().startActivity(new Intent(getActivity(), CodeDirectInfoActivity.class).putExtra("item", list.get(arg2)));
			}
		});
		if (list == null || list.size() < 1)
		{
			showEmpty();
			return;
		}
		hideEmpty();
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
			{
				// TODO Auto-generated method stub
				menu.setHeaderTitle("代码指挥");
				menu.add(0, 0, 0, "删除代码");
				menu.add(0, 1, 1, "批量删除代码");
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
			confirmDlg("删除代码指挥", "codedirect", "id", currentlist.get(ListItem), currentlist, adapter);
			break;
		case 1:
			for (BaseMapObject tmp : currentlist)
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
		initSwitchBaseView(view, "收件箱", "发件箱");
		// Btn_Left.setText("返回");
		// Btn_Right.setText("新建");
		Btn_Right.setBackgroundResource(R.drawable.creatmail);
		linear_Del = (LinearLayout) view.findViewById(R.id.linear_del);
		Btn_Delete = (Button) view.findViewById(R.id.bt_sure);
		Btn_Canle = (Button) view.findViewById(R.id.bt_canle);
		Btn_Delete.setOnClickListener(this);
		Btn_Canle.setOnClickListener(this);

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
		case R.id.text_left:
			changeSiwtchLeft();
			issend = false;
			currentlist = recvemail;
			refreshList(currentlist);
			break;
		case R.id.text_right:
			changeSiwtchRight();
			issend = true;
			currentlist = sendemail;
			refreshList(currentlist);
			break;
		case R.id.bt_right:
			startActivity(new Intent(getActivity(), CreatCodedirecActivity.class));
			break;
		case R.id.bt_sure:
			confirmDlg("删除代码", "codedirect", "id", null, currentlist, adapter);

			break;
		case R.id.bt_canle:
			for (BaseMapObject tmp : currentlist)
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
