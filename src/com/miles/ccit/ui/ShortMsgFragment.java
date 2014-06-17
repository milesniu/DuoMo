package com.miles.ccit.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miles.ccit.duomo.R;
import com.miles.ccit.util.BaseFragment;

public class ShortMsgFragment extends BaseFragment
{

	private HashMap<String, Bitmap> imagesCache = new HashMap<String, Bitmap>(); // 图片缓存
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
		View view = inflater.inflate(R.layout.shortmsg_fragment, null);
		
		listview = (ListView) view.findViewById(R.id.listView_shortmsg);
		
LayoutTitle = (View)view.findViewById(R.id.include_layout);
		
		
		LayoutTitle.findViewById(R.id.bt_left).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
//				startActivity(new Intent(getActivity(),SearchActivity.class));
				getActivity().finish();
			}
		});

		view.findViewById(R.id.bt_right).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + OverAllData.TelPhoneNum)));
			}
		});
		
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

	
}
