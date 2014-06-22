package com.miles.ccit.ui;

import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.duomo.R;
import com.miles.ccit.util.BaseFragment;

public class ShortMsgFragment extends BaseFragment
{

	private HashMap<String, Bitmap> imagesCache = new HashMap<String, Bitmap>(); // 图片缓存
	private ListView listview;
	private BaseAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_shortmsg, null);
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
		initBaseView(view, "短消息");
		Btn_Left.setText("返回");
		Btn_Right.setText("新建");
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
