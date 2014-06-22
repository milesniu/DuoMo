package com.miles.ccit.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.adapter.ShortMsgSetAdapter;
import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.BaseFragment;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.UnixTime;

public class ShortMsgFragment extends BaseFragment
{

	private ListView list_Content;
	private ShortMsgSetAdapter adapter;
	private List<BaseMapObject> msgList = new Vector<BaseMapObject>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_shortmsg, null);
		initView(view);
		
//		BaseMapObject contact = new BaseMapObject();
//		contact.put("id",null);
//		contact.put("number","123");
//		contact.put("sendtype","0");
//		contact.put("status","1");
//		contact.put("msgtype","1");
//		contact.put("msgcontent","fdsafsafa");
//
//		contact.put("creattime",UnixTime.getStrCurrentUnixTime());
//		contact.put("priority","1");
//		contact.put("acknowledgemen","2");
//		contact.InsertObj2DB(getActivity(), "shortmsg");
//		contact.InsertObj2DB(getActivity(), "shortmsg");
//		contact.InsertObj2DB(getActivity(), "shortmsg");
//		contact.put("number","1234");
//		contact.InsertObj2DB(getActivity(), "shortmsg");
//		contact.put("number","1234");
//		contact.InsertObj2DB(getActivity(), "shortmsg");
//		contact.put("number","1235");
//		contact.InsertObj2DB(getActivity(), "shortmsg");
		
		
		return view;
	}

	
	
	private void refreshList()
	{
		msgList = GetData4DB.getObjecSet(getActivity(), "shortmsg", "contact", "number", "number");
		
		if (msgList == null)
		{
			Toast.makeText(getActivity(), "暂无消息记录...", 0).show();
			return;
		}

		adapter = new ShortMsgSetAdapter(getActivity(), msgList);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				

			}
		});
	}



	@Override
	public void initView(View view)
	{
		// TODO Auto-generated method stub
		initBaseView(view, "短消息");
		Btn_Left.setText("返回");
		Btn_Right.setText("新建");
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

		default:
			break;
		}
	}

	
}
