package com.miles.ccit.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.adapter.MsgorMailSetAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.CreatEMailActivity;
import com.miles.ccit.duomo.EmailInfoActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsBaseFragment;
import com.miles.ccit.util.BaseMapObject;

public class EmailFragment extends AbsBaseFragment
{

	private ListView listview;
	private MsgorMailSetAdapter adapter;
	List<BaseMapObject> emailList = new Vector<BaseMapObject>();
	List<BaseMapObject> sendemail = new Vector<BaseMapObject>();
	List<BaseMapObject> recvemail = new Vector<BaseMapObject>();
	
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
		if(list==null || list.size()<1)
		{
			showEmpty();
			return;
		}
		hideEmpty();
	}



	@Override
	public void initView(View view)
	{
		// TODO Auto-generated method stub
		initSwitchBaseView(view, "收件箱", "发件箱");
//		Btn_Left.setText("返回");
//		Btn_Right.setText("写邮件");
		Btn_Right.setBackgroundResource(R.drawable.creatmail);
		emailList.clear();
		recvemail.clear();
		sendemail.clear();
		emailList = GetData4DB.getObjList4LeftJoin(getActivity(), "emailmsg", "contact", "number");
		
		if (emailList == null)
		{
			Toast.makeText(getActivity(), "网络连接异常，请检查后重试...", 0).show();
			return;
		}
		else
		{
			for(BaseMapObject item:emailList)
			{
				if(item.get("sendtype").toString().equals("1"))//收件
				{
					recvemail.add(item);
				}
				else
				{
					sendemail.add(item);
				}
			}
		}
		refreshList(recvemail);
		
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
			refreshList(recvemail);
			break;
		case R.id.text_right:
			changeSiwtchRight();
			refreshList(sendemail);
			break;
		case R.id.bt_right:
			startActivity(new Intent(getActivity(), CreatEMailActivity.class));
			break;
		default:
			break;
		}
	}

	
}
