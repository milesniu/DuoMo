package com.miles.ccit.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.miles.ccit.duomo.R;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.UnixTime;

public class MsgorMailSetAdapter extends BaseAdapter
{

	private List<BaseMapObject> contactlist;
	private Context mContext;
	private String Type;
	public MsgorMailSetAdapter(Context contex,List<BaseMapObject> list,String type)
	{
		this.mContext = contex;
		this.contactlist = list;
		this.Type = type;
	}
	
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return contactlist.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return contactlist.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		final BaseMapObject map = contactlist.get(position);
		View view = null;

		LayoutInflater mInflater = LayoutInflater.from(mContext);
		view = mInflater.inflate(R.layout.listitem_shortmsg, null);
		((TextView)  view.findViewById(R.id.text_number)).setText(map.get("name")==null?map.get("number").toString():map.get("name").toString());
		((TextView) view.findViewById(R.id.text_time)).setText(UnixTime.unixTime2Simplese(map.get("creattime").toString(),"MM-dd HH:mm"));
		if(Type.equals("shortmsg"))
		{
			((TextView) view.findViewById(R.id.text_contact)).setText(map.get("msgtype").toString().equals("0")?(String)map.get("msgcontent"):"[语音]");
		}
		else if(Type.equals("mail"))
		{
			((TextView) view.findViewById(R.id.text_contact)).setText(map.get("subject").toString());
		}
		CheckBox checkDel = (CheckBox)view.findViewById(R.id.check_del);
		if(map.get("exp1")!=null&&map.get("exp1").toString().equals("0"))
		{
			checkDel.setVisibility(View.VISIBLE);
			view.findViewById(R.id.text_time).setVisibility(View.GONE);
		}
		else
		{
			checkDel.setVisibility(View.INVISIBLE);
			view.findViewById(R.id.text_time).setVisibility(View.VISIBLE);
		}
		checkDel.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				// TODO Auto-generated method stub
				if(isChecked)
				{
					map.put("exp2", "1");
				}
				else
				{
					map.put("exp2", null);
				}
			}
		});
		return view;
	}

}
