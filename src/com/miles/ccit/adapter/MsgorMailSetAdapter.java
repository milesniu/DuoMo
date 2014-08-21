package com.miles.ccit.adapter;

import java.util.List;
import java.util.Map;

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
import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.JSONUtil;
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

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		final BaseMapObject map = contactlist.get(position);
		View view = null;

		LayoutInflater mInflater = LayoutInflater.from(mContext);
		if(Type.equals("broadcast"))
		{
			view = mInflater.inflate(R.layout.listitem_broadfile, null);
		}
		else
		{
			view = mInflater.inflate(R.layout.listitem_shortmsg, null);
		}
		
		if(Type.equals("svoice"))
		{
			((TextView) view.findViewById(R.id.text_time)).setText("");
			((TextView)  view.findViewById(R.id.text_number)).setText(map.get("frequency").toString());
			
		}
		else if(Type.equals("broadcast"))
		{
			((TextView) view.findViewById(R.id.text_name)).setText(map.get("frequency").toString());
			((TextView)  view.findViewById(R.id.text_number)).setText(UnixTime.unixTime2Simplese(map.get("creattime").toString(),"MM-dd HH:mm"));
			
		}
		else
		{
			((TextView) view.findViewById(R.id.text_time)).setText(UnixTime.unixTime2Simplese(map.get("creattime").toString(),"MM-dd HH:mm"));
			((TextView)  view.findViewById(R.id.text_number)).setText(map.get("name")==null?map.get("number").toString():map.get("name").toString());
			
		}
		
		if(Type.equals("shortmsg"))
		{
			((TextView) view.findViewById(R.id.text_contact)).setText(map.get("msgtype").toString().equals("0")?(String)map.get("msgcontent"):"[语音]");
		}
		else if(Type.equals("mail"))
		{
			((TextView) view.findViewById(R.id.text_contact)).setText(map.get("subject").toString());
		}
		else if(Type.equals("codedir"))
		{
			String content = map.get("codecontent").toString();
			String P1 = content.substring(content.indexOf("P1=")+3, content.indexOf("&P2="));
			String P2 = content.substring(content.indexOf("&P2=")+4, content.indexOf("&P3="));
			
			Map<String,Object> data = JSONUtil.getMapFromJson(AbsCreatCodeActivity.getassetsCode(mContext,"actioncode.txt"));
			String p1name = ((Map<String,Object>)data.get(P1)).get("name")+"";
			String p2name = ((Map<String,Object>)data.get(P1)).get(P2)+"";
			if(p2name.equals("null"))
			{
				p2name = P2;
			}
			((TextView) view.findViewById(R.id.text_contact)).setText(p1name +" "+p2name);
		}
		else if(Type.equals("svoice"))
		{
			((TextView) view.findViewById(R.id.text_contact)).setText(UnixTime.unixTime2Simplese(map.get("creattime").toString(),"MM-dd HH:mm"));
			
		}

		CheckBox checkDel = (CheckBox)view.findViewById(R.id.check_del);
		if(map.get("exp1")!=null&&map.get("exp1").toString().equals("0"))
		{
			checkDel.setVisibility(View.VISIBLE);
			if(view.findViewById(R.id.text_time)!=null)
			{
				view.findViewById(R.id.text_time).setVisibility(View.GONE);
			}
		}
		else
		{
			checkDel.setVisibility(View.INVISIBLE);
			if(view.findViewById(R.id.text_time)!=null)
			{
				view.findViewById(R.id.text_time).setVisibility(View.VISIBLE);
			}
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
