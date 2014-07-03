package com.miles.ccit.ui;

import java.util.HashMap;
import java.util.List;

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

import com.miles.ccit.duomo.CreatCodedirecActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.util.AbsBaseFragment;

public class CodeDirectFragment extends AbsBaseFragment
{

	private ListView listview;
	private BaseAdapter adapter;
	
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


	private void refreshList(List<HashMap<String, Object>> contentList)
	{
		if (contentList == null)
		{
			Toast.makeText(getActivity(), "网络连接异常，请检查后重试...", 0).show();
			return;
		}

	
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
		initSwitchBaseView(view, "收件箱", "发件箱");
//		Btn_Left.setText("返回");
//		Btn_Right.setText("新建");
		Btn_Right.setBackgroundResource(R.drawable.creatmail);
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
			break;
		case R.id.text_right:
			changeSiwtchRight();
			break;
		case R.id.bt_right:
			startActivity(new Intent(getActivity(), CreatCodedirecActivity.class));
			break;
		default:
			break;
		}
	}

	
}
