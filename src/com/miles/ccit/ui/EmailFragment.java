package com.miles.ccit.ui;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.adapter.MsgorMailSetAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.database.UserDatabase;
import com.miles.ccit.duomo.CreatEMailActivity;
import com.miles.ccit.duomo.EmailInfoActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsBaseFragment;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MyLog;
import com.miles.ccit.util.OverAllData;

public class EmailFragment extends AbsBaseFragment
{

	private ListView listview;
	private MsgorMailSetAdapter adapter;
	List<BaseMapObject> emailList = new Vector<BaseMapObject>();
	List<BaseMapObject> sendemail = new Vector<BaseMapObject>();
	List<BaseMapObject> recvemail = new Vector<BaseMapObject>();
	List<BaseMapObject> currentlist = null;;
	public Button Btn_Delete;
	public Button Btn_Canle;

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
		View view = inflater.inflate(R.layout.fragment_email, null);

		listview = (ListView) view.findViewById(R.id.listView_content);
		initView(view);
		return view;
	}

	private void refreshList(final List<BaseMapObject> list)
	{

		Collections.reverse(list);

		adapter = new MsgorMailSetAdapter(getActivity(), list, "mail");

		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				getActivity().startActivity(new Intent(getActivity(), EmailInfoActivity.class).putExtra("item", list.get(arg2)));
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
				menu.setHeaderTitle("电子邮件");
				menu.add(0, 0, 0, "删除邮件");
				menu.add(0, 1, 1, "批量删除邮件");
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
			confirmDlg("删除邮件", "emailmsg", "id", currentlist.get(ListItem), currentlist, adapter);
			// BaseMapObject selectItem = currentlist.get(ListItem);
			// long ret = BaseMapObject.DelObj4DB(getActivity(), "emailmsg",
			// "id", selectItem.get("id").toString());
			// if (ret != -1)
			// {
			// currentlist.remove(ListItem);
			// adapter.notifyDataSetChanged();
			// }
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
	public void onResume()
	{
		// TODO Auto-generated method stub
		emailList.clear();
		recvemail.clear();
		sendemail.clear();
		emailList = GetData4DB.getObjList4LeftJoin(getActivity(), "emailmsg", "contact", "number");

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
		currentlist = recvemail;
		refreshList(currentlist);
		super.onResume();
	}

	@Override
	public void initView(View view)
	{
		// TODO Auto-generated method stub
		initSwitchBaseView(view, "收件箱", "发件箱");
		// Btn_Left.setText("返回");
		// Btn_Right.setText("写邮件");
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

			currentlist = recvemail;
			refreshList(currentlist);
			break;
		case R.id.text_right:
			changeSiwtchRight();
			currentlist = sendemail;
			refreshList(currentlist);
			break;
		case R.id.bt_right:
			if (LoginActivity.isLogin)
			{
				startActivity(new Intent(getActivity(), CreatEMailActivity.class));
			} else
			{
				MyLog.showToast(getActivity(), "请登录后再执行该操作...");
			}
			break;
		case R.id.bt_sure:
			confirmDlg("删除邮件", "emailmsg", "id", null, currentlist, adapter);
			// Iterator<BaseMapObject> iter = currentlist.iterator();
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
			// UserDatabase.DelListObj(getActivity(),"emailmsg", "id", Idlist);
			//
			// for(BaseMapObject tmp:currentlist)
			// {
			// tmp.put("exp1", null);
			// tmp.put("exp2", null);
			// }
			// adapter.notifyDataSetChanged();
			// linear_Del.setVisibility(View.GONE);
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
